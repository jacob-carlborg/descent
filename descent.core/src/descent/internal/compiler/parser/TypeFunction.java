package descent.internal.compiler.parser;

import java.util.List;

import descent.core.compiler.IProblem;

public class TypeFunction extends Type {
	
	public List<Argument> parameters;
	public int varargs;
	public LINK linkage;	// calling convention
	public int inuse;

	public TypeFunction(List<Argument> parameters, Type treturn, int varargs, LINK linkage) {
		super(TY.Tfunction, treturn);
		this.parameters = parameters;
		this.varargs = varargs;
		this.linkage = linkage;
	}
	
	@Override
	public Type semantic(Scope sc, SemanticContext context) {
		if (deco != null) { // if semantic() already run
			return this;
		}

		linkage = sc.linkage;
		if (next == null) {
			next = tvoid;
		}
		next = next.semantic(sc, context);
		if (next.toBasetype(context).ty == TY.Tsarray) {
			context.acceptProblem(Problem.newSemanticTypeError("Functions cannot return static arrays", IProblem.IllegalReturnType, 0, start, length));
			next = Type.terror;
		}
		if (next.toBasetype(context).ty == TY.Tfunction) {
			error("functions cannot return a function");
			next = Type.terror;
		}
		if (next.toBasetype(context).ty == TY.Ttuple) {
			error("functions cannot return a tuple");
			next = Type.terror;
		}
		if (next.isauto() && (sc.flags & Scope.SCOPEctor) == 0)
			error("functions cannot return auto %s", next.toChars());

		if (parameters != null) {
			int dim = Argument.dim(parameters, context);

			for (int i = 0; i < dim; i++) {
				Argument arg = Argument.getNth(parameters, i, context);
				Type t;

				inuse++;
				arg.type = arg.type.semantic(sc, context);
				if (inuse == 1) {
					inuse--;
				}
				t = arg.type.toBasetype(context);

				/*
				 * If arg turns out to be a tuple, the number of parameters may
				 * change.
				 */
				if (t.ty == TY.Ttuple) {
					dim = Argument.dim(parameters, context);
				}

				if (arg.inout != InOut.In) {
					if (t.ty == TY.Tsarray) {
						context.acceptProblem(Problem.newSemanticTypeError("Cannot have out or inout parameter of type static array", IProblem.IllegalParameters, 0, t.start, t.length));
					}
				}
				if (arg.inout != InOut.Lazy && t.ty == TY.Tvoid) {
					context.acceptProblem(Problem.newSemanticTypeError("Cannot have parameter of type void", IProblem.IllegalParameters, 0, t.start, t.length));
				}

				if (arg.defaultArg != null) {
					arg.defaultArg = arg.defaultArg.semantic(sc, context);
					arg.defaultArg = Expression.resolveProperties(sc,
							arg.defaultArg, context);
					arg.defaultArg = arg.defaultArg
							.implicitCastTo(sc, arg.type);
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
	
	@Override
	public int getNodeType() {
		return TYPE_FUNCTION;
	}

}
