package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TypeDelegate extends Type {
	
	private String signature; // Descent signature

	public TypeDelegate(Type next) {
		super(TY.Tdelegate, next);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceNext);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean checkBoolean(SemanticContext context) {
		return true;
	}

	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
		e = new NullExp(Loc.ZERO);
		e.type = this;
		return e;
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		if (equals(ident, Id.ptr)) {
			e.type = context.Type_tvoidptr;
			return e;
		} else if (equals(ident, Id.funcptr)) {
			e = e.addressOf(sc, context);
			e.type = context.Type_tvoidptr;
			e = new AddExp(e.loc, e, new IntegerExp(PTRSIZE));
			e.type = context.Type_tvoidptr;
			e = new PtrExp(e.loc, e);
			e.type = next.pointerTo(context);
			return e;
		} else {
			e = super.dotExp(sc, e, ident, context);
		}
		return e;
	}

	@Override
	public int getNodeType() {
		return TYPE_DELEGATE;
	}

	@Override
	public TypeInfoDeclaration getTypeInfoDeclaration(SemanticContext context) {
		return new TypeInfoDelegateDeclaration(this, context);
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		return true;
	}

	@Override
	public boolean isZeroInit(SemanticContext context) {
		return true;
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		if (deco != null) { // if semantic() already run
			return this;
		}
		next = next.semantic(loc, sc, context);
		return merge(context);
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		return PTRSIZE * 2;
	}

	@Override
	public Type syntaxCopy(SemanticContext context) {
		Type t = next.syntaxCopy(context);
		if (same(t, next, context)) {
			t = this;
		} else {
			t = new TypeDelegate(t);
		}
		return t;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer args = new OutBuffer();
		TypeFunction tf = (TypeFunction) next;

		argsToCBuffer(args, hgs, tf.parameters, tf.varargs, context);
		buf.prependstring(args.toChars());
		buf.prependstring(" delegate");
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
		next.next.toCBuffer2(buf, null, hgs, context);
	}
	
	@Override
	public String getSignature() {
		if (signature == null) {
			StringBuilder sb = new StringBuilder();
			appendSignature(sb);
			signature = sb.toString();
		}
		return signature;
	}
	
	@Override
	protected void appendSignature(StringBuilder sb) {
		sb.append('D');
		next.appendSignature(sb);
	}
	

}
