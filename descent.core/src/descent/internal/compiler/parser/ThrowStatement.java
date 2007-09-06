package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ThrowStatement extends Statement {

	public Expression exp;

	public ThrowStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = exp;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return THROW_STATEMENT;
	}

	@Override
	public Statement inlineScan(InlineScanState iss) {
		if (exp != null) {
			exp = exp.inlineScan(iss);
		}
		return this;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		FuncDeclaration fd = sc.parent.isFuncDeclaration();
		fd.hasReturnExp |= 2;

		if (sc.incontract != 0) {
			error("Throw statements cannot be in contracts");
		}
		exp = exp.semantic(sc, context);
		exp = resolveProperties(sc, exp, context);
		if (null == exp.type.toBasetype(context).isClassHandle()) {
			error("can only throw class objects, not type %s", exp.type
					.toChars(context));
		}
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		ThrowStatement s = new ThrowStatement(loc, exp.syntaxCopy());
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("throw ");
		exp.toCBuffer(buf, hgs, context);
		buf.writeByte(';');
		buf.writenl();
	}

}
