package descent.internal.compiler.parser;

import java.util.ArrayList;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class DebugSymbol extends Dsymbol {

	public long level;
	public Version version;

	public DebugSymbol(Loc loc, IdentifierExp ident, Version version) {
		super(loc, ident);
		this.version = version;
	}

	public DebugSymbol(Loc loc, long level, Version version) {
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
			if (null == m) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.DebugDeclarationMustBeAtModuleLevel, 0, start, length));
			} else {
				if (findCondition(m.debugidsNot, ident)) {
					error("defined after use");
				}
				if (null == m.debugids) {
					m.debugids = new ArrayList<char[]>();
				}
				m.debugids.add(ident.ident);
			}
		} else {
			if (null == m) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.DebugDeclarationMustBeAtModuleLevel, 0, start, length));
			} else {
				m.debuglevel = level;
			}
		}
		return 0;
	}

	@Override
	public int getNodeType() {
		return DEBUG_SYMBOL;
	}

	@Override
	public String kind() {
		return "debug";
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		// empty
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		if (s != null) {
			throw new IllegalStateException("assert(!s)");
		}
		DebugSymbol ds = new DebugSymbol(loc, ident, version);
		ds.level = level;
		return ds;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("debug = ");
		if (ident != null) {
			buf.writestring(ident.toChars());
		} else {
			buf.writestring(level);
		}
		buf.writestring(";");
		buf.writenl();
	}

}
