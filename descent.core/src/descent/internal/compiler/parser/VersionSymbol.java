package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class VersionSymbol extends Dsymbol {

	public long level;
	public Version version;

	public VersionSymbol(Loc loc, IdentifierExp ident, Version version) {
		super(ident);
		this.loc = loc;
		this.version = version;
	}

	public VersionSymbol(Loc loc, long level, Version version) {
		this.loc = loc;
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
			VersionCondition.checkPredefined(loc, ident, context);
			if (m == null) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.VersionDeclarationMustBeAtModuleLevel, this));
				}
			} else {
				if (findCondition(m.versionidsNot, ident)) {
					if (context.acceptsErrors()) {
						context.acceptProblem(Problem.newSemanticTypeError(IProblem.VersionDefinedAfterUse, this, new String[] { ident.toString() } ));
					}
				}
				if (null == m.versionids) {
					m.versionids = new HashtableOfCharArrayAndObject();
				}
				m.versionids.put(ident.ident, this);
			}
		} else {
			if (m == null) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.VersionDeclarationMustBeAtModuleLevel, this));
				}
			} else {
				m.versionlevel = level;
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
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		if (s != null) {
			throw new IllegalStateException("assert(!s)");
		}
		VersionSymbol ds = context.newVersionSymbol(loc, ident, version);
		ds.level = level;
		ds.copySourceRange(this);
		return ds;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("version = ");
		if (ident != null) {
			buf.writestring(ident.toChars());
		} else {
			buf.writestring(level);
		}
		buf.writestring(";");
		buf.writenl();
	}
	
	@Override
	public String getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

}
