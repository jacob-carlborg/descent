package descent.ui.text.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IAlignDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IConditionalDeclaration;
import descent.core.dom.IElement;
import descent.core.dom.ElementVisitor;
import descent.core.dom.IEnumDeclaration;
import descent.core.dom.IImportDeclaration;
import descent.core.dom.ILinkDeclaration;
import descent.core.dom.IModuleDeclaration;
import descent.core.dom.IPragmaDeclaration;
import descent.core.dom.IProtectionDeclaration;
import descent.core.dom.IStorageClassDeclaration;
import descent.core.dom.ITemplateDeclaration;
import descent.ui.text.DEditor;

public class DOutlineContentProvider implements ITreeContentProvider {
	
	private IEditorInput input;
	private DEditor editor;
	
	public DOutlineContentProvider(DEditor editor) {
		super();
		this.editor = editor;
	}

	public Object[] getChildren(Object parentElement) {
		ICompilationUnit compilationUnit = editor.getCompilationUnit();
		
		if (parentElement == input) {
			parentElement = editor.getCompilationUnit();
		}
		
		List<Object> list;
		IElement e = (IElement) parentElement;
		switch(e.getElementType()) {
		case IElement.COMPILATION_UNIT:
			list = new ArrayList<Object>();
			
			IModuleDeclaration module = compilationUnit.getModuleDeclaration();
			if (module != null) {
				list.add(module);
			}
			
			addDeclDefs(list, compilationUnit.getDeclarationDefinitions());
			
			return list.toArray();
		case IElement.AGGREGATE_DECLARATION:
			list = new ArrayList<Object>();
			addDeclDefs(list, ((IAggregateDeclaration) e).getDeclarationDefinitions());
			return list.toArray();
		case IElement.TEMPLATE_DECLARATION:
			list = new ArrayList<Object>();
			addDeclDefs(list, ((ITemplateDeclaration) e).getDeclarationDefinitions());
			return list.toArray();
		case IElement.ENUM_DECLARATION:
			return ((IEnumDeclaration) e).getMembers();
		case IElement.LINK_DECLARATION:
			list = new ArrayList<Object>();
			addDeclDefs(list, ((ILinkDeclaration) e).getDeclarationDefinitions());
			return list.toArray();
		case IElement.VERSION_DECLARATION:
		case IElement.DEBUG_DECLARATION:
		case IElement.STATIC_IF_DECLARATION:
			IConditionalDeclaration c = (IConditionalDeclaration) e;
			list = new ArrayList<Object>();
			addDeclDefs(list, c.getIfTrueDeclarationDefinitions());
			IElement[] ifFalse = c.getIfFalseDeclarationDefinitions();
			if (ifFalse.length > 0) {
				list.add(new Else(ifFalse));
			}
			return list.toArray();
		case IElement.PRAGMA_DECLARATION:
			list = new ArrayList<Object>();
			addDeclDefs(list, ((IPragmaDeclaration) e).getDeclarationDefinitions());
			return list.toArray();
		case IImaginaryElements.IMPORTS:
			return ((Imports) e).imports;
		case IImaginaryElements.ELSE:
			return ((Else) e).declDefs;
		}
		return new Object[0];
	}
	
	private void addDeclDefs(List<Object> list, IElement[] declDefs) {
		List<IImportDeclaration> importDeclarations = null;
		for(IElement elem : declDefs) {
			switch(elem.getElementType()) {
			case IElement.PROTECTION_DECLARATION:
				addDeclDefs(list, ((IProtectionDeclaration) elem).getDeclarationDefinitions());
				break;
			case IElement.STORAGE_CLASS_DECLARATION:
				addDeclDefs(list, ((IStorageClassDeclaration) elem).getDeclarationDefinitions());
				break;
			case IElement.ALIGN_DECLARATION:
				addDeclDefs(list, ((IAlignDeclaration) elem).getDeclarationDefinitions());
				break;
			case IElement.IMPORT_DECLARATION:
				if (importDeclarations == null) {
					importDeclarations = new ArrayList<IImportDeclaration>();
				}
				importDeclarations.add((IImportDeclaration) elem);
				break;
			default:
				list.add(elem);
				break;
			}
		}
		if (importDeclarations != null) {
			list.add(new Imports(importDeclarations.toArray(new IImportDeclaration[importDeclarations.size()])));
		}
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		ICompilationUnit compilationUnit = editor.getCompilationUnit();
		if (element == input) {
			element = compilationUnit;
		}
		
		if (element == null) return false;
		
		IElement e = (IElement) element;
		switch(e.getElementType()) {
		case IElement.COMPILATION_UNIT:
			if (compilationUnit.getModuleDeclaration() != null) {
				return true;
			}
			if (compilationUnit.getDeclarationDefinitions().length > 0) {
				return true;
			}
		case IElement.AGGREGATE_DECLARATION:
			return ((IAggregateDeclaration) e).getDeclarationDefinitions().length > 0;
		case IElement.ENUM_DECLARATION:
			return ((IEnumDeclaration) e).getMembers().length > 0;
		case IElement.LINK_DECLARATION:
			return ((ILinkDeclaration) e).getDeclarationDefinitions().length > 0;
		case IElement.VERSION_DECLARATION:
		case IElement.DEBUG_DECLARATION:
		case IElement.STATIC_IF_DECLARATION:
			IConditionalDeclaration v = (IConditionalDeclaration) e;
			return v.getIfTrueDeclarationDefinitions().length > 0 ||
				v.getIfFalseDeclarationDefinitions().length > 0;
		case IElement.PRAGMA_DECLARATION:
			return ((IPragmaDeclaration) e).getDeclarationDefinitions().length > 0;
		case IImaginaryElements.IMPORTS:
		case IImaginaryElements.ELSE:
			return true;
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		input = (IEditorInput) newInput;
	}
	
	class Imports implements IElement {
		
		private IImportDeclaration[] imports;

		public Imports(IImportDeclaration[] imports) {
			this.imports = imports;
		}

		public int getElementType() {
			return IImaginaryElements.IMPORTS;
		}

		public int getLength() {
			return 0;
		}

		public int getStartPosition() {
			return imports[0].getStartPosition();
		}
		
		public void accept(ElementVisitor visitor) { }
		
	}
	
	class Else implements IElement {
		
		private IElement[] declDefs;
		
		public Else(IElement[] declDefs) {
			this.declDefs = declDefs;
		}

		public int getElementType() {
			return IImaginaryElements.ELSE;
		}

		public int getLength() {
			return 0;
		}

		public int getStartPosition() {
			return declDefs[0].getStartPosition();
		}
		
		public void accept(ElementVisitor visitor) { }
		
	}
	
}