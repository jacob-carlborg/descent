package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeSArray extends TypeArray {

	public Expression dim;

	public TypeSArray(Type next, Expression dim) {
		super(TY.Tsarray, next);
		this.dim = dim;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, next);
			TreeVisitor.acceptChildren(visitor, dim);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression defaultInit(SemanticContext context) {
		return next.defaultInit(context);
	}

	@Override
	public Expression toExpression() {
		Expression e = next.toExpression();
		if (e != null) {
			Expressions arguments = new Expressions(1);
			arguments.add(dim);
			e = new ArrayExp(dim.loc, e, arguments);
			e.setSourceRange(start, length);
		}
		return e;
	}

	@Override
	public int getNodeType() {
		return TYPE_S_ARRAY;
	}

	@Override
	public void toPrettyBracket(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("[");
		buf.writestring(dim.toChars(context));
		buf.writestring("]");
	}

	@Override
	public Type syntaxCopy() {
		Type t = next.syntaxCopy();
		Expression e = dim.syntaxCopy();
		t = new TypeSArray(t, e);
		return t;
	}
	
	@Override
	public int alignsize(SemanticContext context) {
		return next.alignsize(context);
	}

}
