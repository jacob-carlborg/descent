package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeAArray extends TypeArray {

	public Type index;
	public Type key;

	public TypeAArray(Type t, Type index) {
		super(TY.Taarray, t);
		this.index = index;
		this.key = null;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, index);
			TreeVisitor.acceptChildren(visitor, key);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
		e = new NullExp(Loc.ZERO);
		e.type = this;
		return e;
	}

	@Override
	public int getNodeType() {
		return TYPE_A_ARRAY;
	}

	@Override
	public void toPrettyBracket(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writeByte('[');
		{
			OutBuffer ibuf = new OutBuffer();
			index.toCBuffer2(ibuf, null, hgs, context);
			buf.write(ibuf);
		}
		buf.writeByte(']');
	}

}
