package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeDelegate extends Type {
	
	public TypeDelegate(Type next) {
		super(TY.Tdelegate, next);
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		if (deco != null) {			// if semantic() already run
			return this;
	    }
	    next = next.semantic(loc, sc, context);
	    return merge(context);
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, next);
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
		return TYPE_DELEGATE;
	}
	
	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs, SemanticContext context) {
		OutBuffer args = new OutBuffer();
	    TypeFunction tf = (TypeFunction )next;

	    // TODO semantic
	    // argsToCBuffer(args, hgs, tf.parameters, tf.varargs, context);
	    buf.prependstring(args.toChars());
	    buf.prependstring(" delegate");
	    if (ident != null)
	    {
		buf.writeByte(' ');
		buf.writestring(ident.toChars(context));
	    }
	    next.next.toCBuffer2(buf, null, hgs, context);
	}

}
