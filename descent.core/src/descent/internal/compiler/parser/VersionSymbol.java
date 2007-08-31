package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class VersionSymbol extends Dsymbol {

	public long level;
	public Version version;

	public VersionSymbol(Loc loc, IdentifierExp ident, Version version) {
		super(loc, ident);
		this.version = version;
	}

	public VersionSymbol(Loc loc, long level, Version version) {
		super(loc);
		this.level = level;
		this.version = version;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, version);
		}
		visitor.endVisit(this);
	}

	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum,
			SemanticContext context) {
		Module m;

		// Do not add the member to the symbol table,
		// just make sure subsequent debug declarations work.
		m = sd.isModule();
		if (ident != null) {
			/* TODO semantic
			 VersionCondition.checkPredefined(loc, ident.toChars(context));
			 */
			if (m == null) {
				error("declaration must be at module level");
			} else {
				/* TODO semantic
				 if (findCondition(m.versionidsNot, ident))
				 error("defined after use");
				 if (!m.versionids)
				 m.versionids = new Array();
				 m.versionids.push(ident.toChars());
				 */
			}
		} else {
			if (m == null) {
				error("level declaration must be at module level");
			} else {
				/* TODO semantic
				 m.versionlevel = level;
				 */
			}
		}
		return 0;
	}

	@Override
	public int getNodeType() {
		return VERSION_SYMBOL;
	}

	@Override
	public String kind() {
		return "version";
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		// empty
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("version = ");
		if (ident != null) {
			buf.writestring(ident.toChars(context));
		} else {
			buf.printf(level);
		}
		buf.writestring(";");
		buf.writenl();
	}

}
