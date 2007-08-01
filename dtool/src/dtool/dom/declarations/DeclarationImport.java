package dtool.dom.declarations;

import java.io.NotSerializableException;
import java.util.Arrays;
import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IModifier;
import descent.internal.core.dom.Import;
import descent.internal.core.dom.ImportDeclaration;
import descent.internal.core.dom.STC;
import descent.internal.core.dom.SelectiveImport;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.EntModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.DefUnitSearch;
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
	
	public DeclarationImport(ImportDeclaration elem) {
		convertNode(elem);
		this.isStatic = elem.isStatic;
		this.isTransitive = (elem.modifiers & IModifier.PUBLIC) != 0;

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

		public abstract void searchDefUnit(CommonDefUnitSearch options);

	}
	
	public ASTNode[] getMembers() {
		return imports;
	}

	public Iterator<? extends ASTNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}

}
