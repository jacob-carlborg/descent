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
import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;
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
		IDElement e = (IDElement) parentElement;
		switch(e.getElementType()) {
		case IDElement.COMPILATION_UNIT:
			list = new ArrayList<Object>();
			
			IModuleDeclaration module = compilationUnit.getModuleDeclaration();
			if (module != null) {
				list.add(module);
			}
			
			addDeclDefs(list, compilationUnit.getDeclarationDefinitions());
			
			return list.toArray();
		case IDElement.AGGREGATE_DECLARATION:
			list = new ArrayList<Object>();
			addDeclDefs(list, ((IAggregateDeclaration) e).getDeclarationDefinitions());
			return list.toArray();
		case IDElement.TEMPLATE_DECLARATION:
			list = new ArrayList<Object>();
			addDeclDefs(list, ((ITemplateDeclaration) e).getDeclarationDefinitions());
			return list.toArray();
		case IDElement.ENUM_DECLARATION:
			return ((IEnumDeclaration) e).getMembers();
		case IDElement.LINK_DECLARATION:
			list = new ArrayList<Object>();
			addDeclDefs(list, ((ILinkDeclaration) e).getDeclarationDefinitions());
			return list.toArray();
		case IDElement.VERSION_DECLARATION:
		case IDElement.DEBUG_DECLARATION:
		case IDElement.STATIC_IF_DECLARATION:
			IConditionalDeclaration c = (IConditionalDeclaration) e;
			list = new ArrayList<Object>();
			addDeclDefs(list, c.getIfTrueDeclarationDefinitions());
			IDElement[] ifFalse = c.getIfFalseDeclarationDefinitions();
			if (ifFalse.length > 0) {
				list.add(new Else(ifFalse));
			}
			return list.toArray();
		case IDElement.PRAGMA_DECLARATION:
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
	
	private void addDeclDefs(List<Object> list, IDElement[] declDefs) {
		List<IImportDeclaration> importDeclarations = null;
		for(IDElement elem : declDefs) {
			switch(elem.getElementType()) {
			case IDElement.PROTECTION_DECLARATION:
				addDeclDefs(list, ((IProtectionDeclaration) elem).getDeclarationDefinitions());
				break;
			case IDElement.STORAGE_CLASS_DECLARATION:
				addDeclDefs(list, ((IStorageClassDeclaration) elem).getDeclarationDefinitions());
				break;
			case IDElement.ALIGN_DECLARATION:
				addDeclDefs(list, ((IAlignDeclaration) elem).getDeclarationDefinitions());
				break;
			case IDElement.IMPORT_DECLARATION:
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
		
		IDElement e = (IDElement) element;
		switch(e.getElementType()) {
		case IDElement.COMPILATION_UNIT:
			if (compilationUnit.getModuleDeclaration() != null) {
				return true;
			}
			if (compilationUnit.getDeclarationDefinitions().length > 0) {
				return true;
			}
		case IDElement.AGGREGATE_DECLARATION:
			return ((IAggregateDeclaration) e).getDeclarationDefinitions().length > 0;
		case IDElement.ENUM_DECLARATION:
			return ((IEnumDeclaration) e).getMembers().length > 0;
		case IDElement.LINK_DECLARATION:
			return ((ILinkDeclaration) e).getDeclarationDefinitions().length > 0;
		case IDElement.VERSION_DECLARATION:
		case IDElement.DEBUG_DECLARATION:
		case IDElement.STATIC_IF_DECLARATION:
			IConditionalDeclaration v = (IConditionalDeclaration) e;
			return v.getIfTrueDeclarationDefinitions().length > 0 ||
				v.getIfFalseDeclarationDefinitions().length > 0;
		case IDElement.PRAGMA_DECLARATION:
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
	
	class Imports implements IDElement {
		
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
		
		public void accept(IDElementVisitor visitor) { }
		
	}
	
	class Else implements IDElement {
		
		private IDElement[] declDefs;
		
		public Else(IDElement[] declDefs) {
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
		
		public void accept(IDElementVisitor visitor) { }
		
	}
	
}