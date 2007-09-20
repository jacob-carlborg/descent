/**
 * 
 */
package dtool.ast.declarations;

import java.util.Iterator;

import static melnorme.miscutil.Assert.assertNotNull;

import melnorme.miscutil.IteratorUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.declarations.DeclarationImport.ImportFragment;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.INonScopedBlock;
import dtool.refmodel.IScopeNode;

public class ImportAliasing extends ImportFragment implements INonScopedBlock {
	
	public static class ImportAliasingDefUnit extends DefUnit {
		
		public ImportAliasing impAlias; // Non-structural Element

		public ImportAliasingDefUnit(IdentifierExp ident, ImportAliasing impAlias) {
			super(ident);
			convertNode(impAlias);
			setSourceRange(ident.start, impAlias.getEndPos() - ident.start);
			this.impAlias = impAlias;
			assertNotNull(impAlias);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}

		@Override
		public IScopeNode getMembersScope() {
			return impAlias.moduleRef.getTargetScope();
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, defname);
			}
			visitor.endVisit(this);
		}
	}
	
	ImportAliasingDefUnit aliasDefUnit;
	
	public ImportAliasing(Import elem) {
		super(elem);
		this.aliasDefUnit = new ImportAliasingDefUnit(elem.aliasId, this);
		// Fix Import fragment range
		//elem.startPos = elem.ident.getStartPos();
		//elem.setEndPos(elem.qName.getEndPos());
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, aliasDefUnit);
			TreeVisitor.acceptChildren(visitor, moduleRef);
		}
		visitor.endVisit(this);
	}

	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch options) {
		// Do nothing. Aliasing imports do not contribute secondary-space DefUnits
		// TODO: this is a bug in D, it's not according to spec.
	}

	public Iterator<? extends IASTNode> getMembersIterator() {
		return IteratorUtil.singletonIterator(aliasDefUnit);
	}
	
	@Override
	public String toStringAsElement() {
		return aliasDefUnit.toStringAsElement() 
		+ " = "+ moduleRef.toStringAsElement() ;
	}
}