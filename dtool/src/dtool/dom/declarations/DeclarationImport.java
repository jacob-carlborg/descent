package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Import;
import descent.internal.core.dom.ImportDeclaration;
import descent.internal.core.dom.SelectiveImport;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.EntIdentifier;
import dtool.dom.references.EntModule;
import dtool.refmodel.EntityResolver;

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
		return "[import declaration]";
		//return StringUtil.collToString(imports, ",");
	}
	
	public static abstract class ImportFragment extends ASTNeoNode {
		public EntModule moduleEnt;

		public ImportFragment(Import elem) {
			// Use elem.qName instead of elem to fix range
			convertNode(elem.qName); 
			this.moduleEnt = new EntModule(elem.qName);
		}

		public abstract DefUnit searchDefUnit(String name);

	}
	
	public static class ImportStatic extends ImportFragment {
		
		public ImportStatic(Import elem) {
			super(elem);
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, moduleEnt);
			}
			visitor.endVisit(this);
		}

		@Override
		public DefUnit searchDefUnit(String name) {
			return EntityResolver.searchDefUnit(this, name);
		}
		
	}
	
	public static class ImportContent extends ImportStatic {

		public ImportContent(Import elem) {
			super(elem);
		}
		
		@Override
		public DefUnit searchDefUnit(String name) {
			return EntityResolver.searchDefUnit(this, name);
		}
	}
	
	public static class ImportAliasing extends ImportFragment {
		
		Symbol ident;
		
		public ImportAliasing(Import elem) {
			super(elem);
			// Fix Import fragment range
			//elem.startPos = elem.ident.getStartPos();
			//elem.setEndPos(elem.qName.getEndPos());
			this.ident = new Symbol(elem.ident);
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, ident);
				TreeVisitor.acceptChildren(visitor, moduleEnt);
			}
			visitor.endVisit(this);
		}

		@Override
		public DefUnit searchDefUnit(String name) {
			return EntityResolver.searchDefUnit(this, name);
		}
	}
	
	public static class ImportSelective extends ImportFragment {
		
		ImportSelectiveFragment aliases[];
		
		public ImportSelective(Import selImport) {
			super(selImport);
			
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
				TreeVisitor.acceptChildren(visitor, moduleEnt);
				TreeVisitor.acceptChildren(visitor, aliases);
			}
			visitor.endVisit(this);
		}

		@Override
		public DefUnit searchDefUnit(String name) {
			return EntityResolver.searchDefUnit(this, name);
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
