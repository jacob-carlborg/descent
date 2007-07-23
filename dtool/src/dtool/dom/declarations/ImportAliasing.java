/**
 * 
 */
package dtool.dom.declarations;

import java.util.Iterator;

import melnorme.miscutil.IteratorUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Identifier;
import descent.internal.core.dom.Import;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DeclarationImport.ImportFragment;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Symbol;
import dtool.refmodel.EntitySearch;
import dtool.refmodel.INonScopedBlock;
import dtool.refmodel.IScopeNode;

public class ImportAliasing extends ImportFragment implements INonScopedBlock {
	
	public static class ImportAliasingDefUnit extends DefUnit {
		
		public ImportAliasing impAlias; // Non-structural Element

		public ImportAliasingDefUnit(Identifier ident, ImportAliasing impAlias) {
			convertNode(impAlias);
			setSourceRange(ident.startPos, impAlias.getEndPos() - ident.startPos);
			this.defname = new Symbol(ident);
			this.impAlias = impAlias;
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}

		@Override
		public IScopeNode getMembersScope() {
			return impAlias.moduleEnt.getTargetScope();
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
		this.aliasDefUnit = new ImportAliasingDefUnit(elem.alias, this);
		// Fix Import fragment range
		//elem.startPos = elem.ident.getStartPos();
		//elem.setEndPos(elem.qName.getEndPos());
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, aliasDefUnit);
			TreeVisitor.acceptChildren(visitor, moduleEnt);
		}
		visitor.endVisit(this);
	}

	@Override
	public void searchDefUnit(EntitySearch options) {
		// Do nothing. Selective imports do not contribute secondary-space DefUnits
	}

	public Iterator<? extends ASTNode> getMembersIterator() {
		return IteratorUtil.singletonIterator(aliasDefUnit);
	}
}