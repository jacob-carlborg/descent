package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TY.Taarray;
import static descent.internal.compiler.parser.TY.Tident;
import static descent.internal.compiler.parser.TY.Tinstance;
import static descent.internal.compiler.parser.TY.Tsarray;

// DMD 1.020
public class TypeAArray extends TypeArray {

	public Type index;
	public Type key;

	public TypeAArray(Type t, Type index) {
		super(TY.Taarray, t);
		this.index = index;
		this.key = null;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, index);
			TreeVisitor.acceptChildren(visitor, key);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean checkBoolean(SemanticContext context) {
		return true;
	}

	@Override
	public MATCH deduceType(Scope sc, Type tparam,
			List<TemplateParameter> parameters, List<ASTDmdNode> dedtypes,
			SemanticContext context) {
		// Extra check that index type must match
		if (tparam != null && tparam.ty == Taarray) {
			TypeAArray tp = (TypeAArray) tparam;
			if (index.deduceType(sc, tp.index, parameters, dedtypes, context) == MATCHnomatch) {
				return MATCHnomatch;
			}
		}
		return super.deduceType(sc, tparam, parameters, dedtypes, context);
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
		if (CharOperation.equals(ident.ident, Id.length)) {
			Expression ec;
			FuncDeclaration fd;
			List<Expression> arguments;

			fd = context.genCfunc(Type.tsize_t, Id.aaLen);
			ec = new VarExp(Loc.ZERO, fd);
			arguments = new ArrayList<Expression>();
			arguments.add(e);
			e = new CallExp(e.loc, ec, arguments);
			e.type = fd.type.next;
		} else if (CharOperation.equals(ident.ident, Id.keys)) {
			Expression ec;
			FuncDeclaration fd;
			List<Expression> arguments;
			int size = key.size(e.loc, context);

			if (size == 0) {
				throw new IllegalStateException("assert(size);");
			}
			fd = context.genCfunc(Type.tindex, Id.aaKeys);
			ec = new VarExp(Loc.ZERO, fd);
			arguments = new ArrayList<Expression>();
			arguments.add(e);
			arguments.add(new IntegerExp(Loc.ZERO, size, Type.tsize_t));
			e = new CallExp(e.loc, ec, arguments);
			e.type = index.arrayOf(context);
		} else if (CharOperation.equals(ident.ident, Id.values)) {
			Expression ec;
			FuncDeclaration fd;
			List<Expression> arguments;

			fd = context.genCfunc(Type.tindex, Id.aaValues);
			ec = new VarExp(Loc.ZERO, fd);
			arguments = new ArrayList<Expression>();
			arguments.add(e);
			int keysize = key.size(e.loc, context);
			keysize = (keysize + 3) & ~3; // BUG: 64 bit pointers?
			arguments.add(new IntegerExp(Loc.ZERO, keysize, Type.tsize_t));
			arguments.add(new IntegerExp(Loc.ZERO, next.size(e.loc, context),
					Type.tsize_t));
			e = new CallExp(e.loc, ec, arguments);
			e.type = next.arrayOf(context);
		} else if (CharOperation.equals(ident.ident, Id.rehash)) {
			Expression ec;
			FuncDeclaration fd;
			List<Expression> arguments;

			fd = context.genCfunc(Type.tint64, Id.aaRehash);
			ec = new VarExp(Loc.ZERO, fd);
			arguments = new ArrayList<Expression>();
			arguments.add(e.addressOf(sc, context));
			arguments.add(key.getInternalTypeInfo(sc));
			e = new CallExp(e.loc, ec, arguments);
			e.type = this;
		} else {
			e = super.dotExp(sc, e, ident, context);
		}
		return e;
	}

	@Override
	public int getNodeType() {
		return TYPE_A_ARRAY;
	}

	@Override
	public TypeInfoDeclaration getTypeInfoDeclaration(SemanticContext context) {
		return new TypeInfoAssociativeArrayDeclaration(this, context);
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		return true;
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		// Deal with the case where we thought the index was a type, but
		// in reality it was an expression.
		if (index.ty == Tident || index.ty == Tinstance || index.ty == Tsarray) {
			Expression[] e = { null };
			Type t[] = { null };
			Dsymbol s[] = { null };

			index.resolve(loc, sc, e, t, s, context);
			if (e[0] != null) { // It was an expression -
				// Rewrite as a static array
				TypeSArray tsa;

				tsa = new TypeSArray(next, e[0]);
				return tsa.semantic(loc, sc, context);
			} else if (t[0] != null) {
				index = t[0];
			} else {
				index.error(loc, "index is not a type or an expression");
			}
		} else {
			index = index.semantic(loc, sc, context);
		}

		// Compute key type; the purpose of the key type is to
		// minimize the permutations of runtime library
		// routines as much as possible.
		key = index.toBasetype(context);
		switch (key.ty) {
		case Tsarray:
			break;
		case Tbit:
		case Tbool:
		case Tfunction:
		case Tvoid:
		case Tnone:
			error(loc, "can't have associative array key of %s", key
					.toChars(context));
			break;
		}
		next = next.semantic(loc, sc, context);
		switch (next.toBasetype(context).ty) {
		case Tfunction:
		case Tnone:
			error(loc, "can't have associative array of %s", next
					.toChars(context));
			break;
		}
		if (next.isauto()) {
			error(loc, "cannot have array of auto %s", next.toChars(context));
		}

		return merge(context);
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		return PTRSIZE; /* * 2*/
	}

	@Override
	public Type syntaxCopy() {
		Type t = next.syntaxCopy();
		Type ti = index.syntaxCopy();
		if (t == next && ti == index) {
			t = this;
		} else {
			t = new TypeAArray(t, ti);
		}
		return t;
	}

	@Override
	public void toDecoBuffer(OutBuffer buf) {
		buf.writeByte(ty.mangleChar);
		index.toDecoBuffer(buf);
		next.toDecoBuffer(buf);
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
