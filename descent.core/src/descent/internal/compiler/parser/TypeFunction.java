package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.STC.STClazy;
import static descent.internal.compiler.parser.STC.STCout;
import static descent.internal.compiler.parser.STC.STCref;
import static descent.internal.compiler.parser.Scope.SCOPEctor;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Ttuple;
import static descent.internal.compiler.parser.TY.Tvoid;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeFunction extends Type {
	
	public List<Argument> parameters;
	public int varargs;
	public LINK linkage;	// calling convention
	public int inuse;

	public TypeFunction(List<Argument> parameters, Type treturn, int varargs, LINK linkage) {
		super(Tfunction, treturn);
		this.parameters = parameters;
		this.varargs = varargs;
		this.linkage = linkage;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, next);
			TreeVisitor.acceptChildren(visitor, parameters);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		if (deco != null) { // if semantic() already run
			return this;
		}

		linkage = sc.linkage;
		if (next == null) {
			next = tvoid;
		}
		next = next.semantic(loc, sc, context);
		if (next.toBasetype(context).ty == Tsarray) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.FunctionsCannotReturnStaticArrays, 0, start, length));
			next = Type.terror;
		}
		if (next.toBasetype(context).ty == Tfunction) {
			error("functions cannot return a function");
			next = Type.terror;
		}
		if (next.toBasetype(context).ty == Ttuple) {
			error("functions cannot return a tuple");
			next = Type.terror;
		}
		if (next.isauto() && (sc.flags & SCOPEctor) == 0)
			error("functions cannot return auto %s", next.toChars(context));

		if (parameters != null) {
			int dim = Argument.dim(parameters, context);

			for (int i = 0; i < dim; i++) {
				Argument arg = Argument.getNth(parameters, i, context);
				Type t;

				inuse++;
				arg.type = arg.type.semantic(loc, sc, context);
				if (inuse == 1) {
					inuse--;
				}
				t = arg.type.toBasetype(context);

				/*
				 * If arg turns out to be a tuple, the number of parameters may
				 * change.
				 */
				if (t.ty == Ttuple) {
					dim = Argument.dim(parameters, context);
				}

				if ((arg.storageClass & (STCout | STCref | STClazy)) != 0) {
					if (t.ty == Tsarray) {
						context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotHaveOutOrInoutParameterOfTypeStaticArray, 0, t.start, t.length));
					}
				}
				if ((arg.storageClass & STClazy) == 0 && t.ty == Tvoid) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotHaveParameterOfTypeVoid, 0, t.start, t.length));
				}

				if (arg.defaultArg != null) {
					arg.defaultArg = arg.defaultArg.semantic(sc, context);
					arg.defaultArg = Expression.resolveProperties(sc,
							arg.defaultArg, context);
					arg.defaultArg = arg.defaultArg
							.implicitCastTo(sc, arg.type, context);
				}
			}
		}
		deco = merge(context).deco;

		if (inuse != 0) {
			error("recursive type");
			inuse = 0;
			return terror;
		}

		if (varargs != 0 && linkage != LINK.LINKd
				&& Argument.dim(parameters, context) == 0) {
			error("variadic functions with non-D linkage must have at least one parameter");
		}

		/*
		 * Don't return merge(), because arg identifiers and default args can be
		 * different even though the types match
		 */
		return this;
	}
	
	public RET retStyle() {
		return RET.RETstack;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_FUNCTION;
	}
	
	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs, SemanticContext context) {
		// TODO semantic
	}

}
