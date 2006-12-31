package descent.ui.text.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;

import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AlignDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ConditionalDeclaration;
import descent.core.dom.Declaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.ExternDeclaration;
import descent.core.dom.ModifierDeclaration;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.PragmaDeclaration;
import descent.core.dom.TemplateDeclaration;
import descent.ui.text.DEditor;

public class DOutlineContentProvider implements ITreeContentProvider {
	
	private IEditorInput input;
	private DEditor editor;
	
	public DOutlineContentProvider(DEditor editor) {
		super();
		this.editor = editor;
	}

	public Object[] getChildren(Object parentElement) {
		CompilationUnit compilationUnit = editor.getCompilationUnit();
		
		if (parentElement == input) {
			parentElement = editor.getCompilationUnit();
		}
		
		List<ASTNode> list;
		ASTNode e = (ASTNode) parentElement;
		switch(e.getNodeType()) {
		case ASTNode.COMPILATION_UNIT:
			list = new ArrayList<ASTNode>();
			
			ModuleDeclaration module = compilationUnit.getModuleDeclaration();
			if (module != null) {
				list.add(module);
			}
			
			addDeclDefs(list, compilationUnit.declarations());
			
			return list.toArray();
		case ASTNode.AGGREGATE_DECLARATION:
			list = new ArrayList<ASTNode>();
			addDeclDefs(list, ((AggregateDeclaration) e).declarations());
			return list.toArray();
		case ASTNode.TEMPLATE_DECLARATION:
			list = new ArrayList<ASTNode>();
			addDeclDefs(list, ((TemplateDeclaration) e).declarations());
			return list.toArray();
		case ASTNode.ENUM_DECLARATION:
			return ((EnumDeclaration) e).enumMembers().toArray();
		case ASTNode.EXTERN_DECLARATION:
			list = new ArrayList<ASTNode>();
			addDeclDefs(list, ((ExternDeclaration) e).declarations());
			return list.toArray();
		case ASTNode.VERSION_DECLARATION:
		case ASTNode.DEBUG_DECLARATION:
		case ASTNode.STATIC_IF_DECLARATION:
			ConditionalDeclaration c = (ConditionalDeclaration) e;
			list = new ArrayList<ASTNode>();
			addDeclDefs(list, c.thenDeclarations());
			return list.toArray();
		case ASTNode.PRAGMA_DECLARATION:
			list = new ArrayList<ASTNode>();
			addDeclDefs(list, ((PragmaDeclaration) e).declarations());
			return list.toArray();
		}
		return new Object[0];
	}
	
	private void addDeclDefs(List<ASTNode> list, List<Declaration> declDefs) {
		//List<ImportDeclaration> importDeclarations = null;
		for(Declaration elem : declDefs) {
			switch(elem.getNodeType()) {
			case ASTNode.MODIFIER_DECLARATION:
				addDeclDefs(list, ((ModifierDeclaration) elem).declarations());
				break;
			case ASTNode.ALIGN_DECLARATION:
				addDeclDefs(list, ((AlignDeclaration) elem).declarations());
				break;
			case ASTNode.IMPORT_DECLARATION:
				list.add(elem);
				/*
				if (importDeclarations == null) {
					importDeclarations = new ArrayList<ImportDeclaration>();
				}
				importDeclarations.add((ImportDeclaration) elem);
				*/
				break;
			default:
				list.add(elem);
				break;
			}
		}
		/*
		if (importDeclarations != null) {
			list.add(new Imports(importDeclarations.toArray(new ImportDeclaration[importDeclarations.size()])));
		}
		*/
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		CompilationUnit compilationUnit = editor.getCompilationUnit();
		if (element == input) {
			element = compilationUnit;
		}
		
		if (element == null) return false;
		
		ASTNode e = (ASTNode) element;
		switch(e.getNodeType()) {
		case ASTNode.COMPILATION_UNIT:
			if (compilationUnit.getModuleDeclaration() != null) {
				return true;
			}
			if (compilationUnit.declarations().size() > 0) {
				return true;
			}
		case ASTNode.AGGREGATE_DECLARATION:
			return ((AggregateDeclaration) e).declarations().size() > 0;
		case ASTNode.ENUM_DECLARATION:
			return ((EnumDeclaration) e).enumMembers().size() > 0;
		case ASTNode.EXTERN_DECLARATION:
			return ((ExternDeclaration) e).declarations().size() > 0;
		case ASTNode.VERSION_DECLARATION:
		case ASTNode.DEBUG_DECLARATION:
		case ASTNode.STATIC_IF_DECLARATION:
			ConditionalDeclaration v = (ConditionalDeclaration) e;
			return v.thenDeclarations().size() > 0 ||
				v.elseDeclarations().size() > 0;
		case ASTNode.PRAGMA_DECLARATION:
			return ((PragmaDeclaration) e).declarations().size() > 0;
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
	
	/*
	class Imports extends ASTNode {
		
		private ImportDeclaration[] imports;

		public Imports(AST ast, ImportDeclaration[] imports) {
			super(ast);
			this.imports = imports;
		}

		public int getNodeType0() {
			return IImaginaryElements.IMPORTS;
		}

		public int getLength() {
			return 0;
		}

		public int getStartPosition() {
			return imports[0].getStartPosition();
		}
		
		public void accept(ASTVisitor visitor) { }
		
	}
	
	class Else extends ASTNode {
		
		private ASTNode[] declDefs;
		
		public Else(AST ast, ASTNode[] declDefs) {
			super(ast);
			this.declDefs = declDefs;
		}

		public int getNodeType0() {
			return IImaginaryElements.ELSE;
		}

		public int getLength() {
			return 0;
		}

		public int getStartPosition() {
			return declDefs[0].getStartPosition();
		}
		
		public void accept(ASTVisitor visitor) { }
		
	}
	*/
	
}