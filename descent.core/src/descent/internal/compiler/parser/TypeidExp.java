package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeidExp extends Expression {
	
	public Type typeidType;

	public TypeidExp(Loc loc, Type typeidType) {
		super(loc, TOK.TOKtypeid);
		this.typeidType = typeidType;
	}
		
	@Override
	public int getNodeType() {
		return TYPEID_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, typeidType);
		}
		visitor.endVisit(this);
	}

	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;
	    typeidType = typeidType.semantic(loc, sc, context);
	    e = typeidType.getTypeInfo(sc, context);
	    return e;
	}
	
	@Override
	public Expression syntaxCopy() {
		return new TypeidExp(loc, typeidType.syntaxCopy());
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("typeid(");
	    typeidType.toCBuffer(buf, null, hgs, context);
	    buf.writeByte(')');
	}

}
