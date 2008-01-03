package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TypeidExp extends Expression {

	public Type typeidType, sourceTypeidType;

	public TypeidExp(Loc loc, Type typeidType) {
		super(loc, TOK.TOKtypeid);
		this.typeidType = this.sourceTypeidType = typeidType;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceTypeidType);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return TYPEID_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;
		typeidType = typeidType.semantic(loc, sc, context);
		e = typeidType.getTypeInfo(sc, context);
		return e;
	}

	@Override
	public Expression syntaxCopy(SemanticContext context) {
		return new TypeidExp(loc, typeidType.syntaxCopy(context));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("typeid(");
		typeidType.toCBuffer(buf, null, hgs, context);
		buf.writeByte(')');
	}

}
