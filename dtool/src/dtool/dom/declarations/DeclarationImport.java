package dtool.dom.declarations;

import util.StringUtil;
import util.tree.TreeVisitor;
import descent.internal.core.dom.Import;
import descent.internal.core.dom.ImportDeclaration;
import descent.internal.core.dom.SelectiveImport;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit.Symbol;
import dtool.dom.references.EntIdentifier;

/**
 * An import Declaration 
 */
public class DeclarationImport extends ASTNeoNode {

	public ImportFragment[] imports;
	public boolean isStatic;
	
	public DeclarationImport(ImportDeclaration elem) {
		convertNode(elem);
		this.isStatic = elem.isStatic;

		int importsNum = elem.imports.size(); 

		// Selective import are at the end		
		SelectiveImport[] selectiveImports; 
		selectiveImports = elem.imports.get(importsNum-1).getSelectiveImports();
		if(selectiveImports.length > 0) {
			if(importsNum != 1)
				throw new UnsupportedOperationException();
			
			this.imports = new ImportFragment[1];
			this.imports[0] = new ImportSelective(elem.imports.get(0));
			return;
		}
		
		this.imports = new ImportFragment[importsNum];
		for(int i = 0; i < importsNum; i++) {
			Import imprt = elem.imports.get(i); 
			ImportFragment imprtFragment = null;
			if(elem.isStatic) {
				imprtFragment = new ImportStatic(imprt);
				//Ignore FQN aliasing for now.
				//Assert.isTrue(imprt.alias == null);
			} else if(imprt.alias == null) {
				imprtFragment = new ImportContent(imprt);
			} else {
				imprtFragment = new ImportAliasing(imprt);
			}
			imports[i] = imprtFragment;
		}
		
	}
	

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, imports);
		}
		visitor.endVisit(this);
	}
	
	public String toString() {
		return StringUtil.collToString(imports, ",");
	}
	
	public static abstract class ImportFragment extends ASTNeoNode {
		protected EntIdentifier moduleName;
	}
	
	public static class ImportStatic extends ImportFragment {
		
		public ImportStatic(Import elem) {
			convertNode(elem);
			this.moduleName = new EntIdentifier(elem.qName.name);
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, moduleName);
			}
			visitor.endVisit(this);
		}
	}
	
	public static class ImportContent extends ImportStatic {

		public ImportContent(Import elem) {
			super(elem);
		}
	}
	
	public static class ImportAliasing extends ImportFragment {
		
		Symbol ident;
		
		public ImportAliasing(Import elem) {
			convertNode(elem);
			this.ident = new Symbol(elem.ident);
			this.moduleName = new EntIdentifier(elem.qName.name);
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, ident);
				TreeVisitor.acceptChildren(visitor, moduleName);
			}
			visitor.endVisit(this);
		}
	}
	
	public static class ImportSelective extends ImportFragment {
		
		ImportSelectiveFragment aliases[];
		
		public ImportSelective(Import selImport) {
			convertNode(selImport);
			this.moduleName = new EntIdentifier(selImport.qName.name);
			
			int importsSize = selImport.getSelectiveImports().length; 
			this.aliases = new ImportSelectiveFragment[importsSize];
			for(int i = 0; i < importsSize; i++) {
				SelectiveImport imprt = selImport.getSelectiveImports()[i]; 
				this.aliases[i] = new ImportSelectiveFragment(imprt);
			}
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, moduleName);
				TreeVisitor.acceptChildren(visitor, aliases);
			}
			visitor.endVisit(this);
		}
	}
	
	public static class ImportSelectiveFragment extends ASTNeoNode {
		Symbol aliasname;
		EntIdentifier targetname;

		public ImportSelectiveFragment(SelectiveImport imprt) {
			convertNode(imprt);
			if(imprt.alias == null) {
				this.aliasname = new Symbol(imprt.name);
			} else {
				this.aliasname = new Symbol(imprt.alias);
				this.targetname = new EntIdentifier(imprt.name);
			}
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, aliasname);
				TreeVisitor.acceptChildren(visitor, targetname);
			}
			visitor.endVisit(this);		
		}
	}
	
	

}
