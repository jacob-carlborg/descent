package dtool.ast.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.MultiImport;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.RefModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.INonScopedBlock;

/**
 * An import Declaration.
 * This is considered an INonScopedBlock because it might contain aliasing
 * imports and selective imports, which are primary-space {@link DefUnit}s.
 */
public class DeclarationImport extends ASTNeoNode implements INonScopedBlock {

	public ImportFragment[] imports;
	public boolean isStatic;
	public boolean isTransitive; // aka public imports
	
	public DeclarationImport(MultiImport elem) {
		convertNode(elem);
		this.isStatic = elem.isstatic;
		//this.isTransitive is adapted in post conversion;
		Assert.isNull(elem.modifiers);

		int importsNum = elem.imports == null ? 0 : elem.imports.size(); 

		// Selective import are at the end		
		this.imports = new ImportFragment[importsNum];
		for(int i = 0; i < importsNum; i++) {
			Import imprt = elem.imports.get(i); 
			ImportFragment imprtFragment = null;
			if(elem.isstatic) {
				imprtFragment = new ImportStatic(imprt);
				//Ignore FQN aliasing for now.
				//Assert.isTrue(imprt.alias == null);
			} else if(imprt.aliasId != null) {
				imprtFragment = new ImportAliasing(imprt);
			} else if(imprt.names != null) {
				Assert.isTrue(imprt.names.size() == imprt.aliases.size());
				Assert.isTrue(imprt.names.size() > 0 );
				imprtFragment = new ImportSelective(imprt);
			} else {
				imprtFragment = new ImportContent(imprt);
			}
			imports[i] = imprtFragment;
		}
		
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, imports);
		}
		visitor.endVisit(this);
	}
	

	public static abstract class ImportFragment extends ASTNeoNode {
		public RefModule moduleRef;

		public ImportFragment(Import elem) {
			convertNode(elem);
			this.moduleRef = new RefModule(elem.packages, elem.id);
		}

		/** Performs a search in the secondary/background scope.
		 * Only imports contribute to this secondary namespace. */
		public abstract void searchInSecondaryScope(CommonDefUnitSearch options);
		
		@Override
		public String toStringAsElement() {
			return moduleRef.toStringAsElement();
		}

	}
	
	public ASTNeoNode[] getMembers() {
		return imports;
	}

	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}

	@Override
	public String toStringAsElement() {
		String str = "";
		for (int i = 0; i < imports.length; i++) {
			ImportFragment fragment = imports[i];
			if(i > 0)
				str = str + ", ";
			str = str + fragment.toStringAsElement();
		}
		return "[import "+str+"]";
	}

}
