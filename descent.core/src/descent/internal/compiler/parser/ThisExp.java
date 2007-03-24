package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public class ThisExp extends Expression {
	
	public Declaration var;

	public ThisExp() {
		super(TOK.TOKthis);
	}
	
	/**
	 * Determine if 'this' is available.
	 * If it is, return the FuncDeclaration that has it.
	 */
	public FuncDeclaration hasThis(Scope sc) {
		FuncDeclaration fd;
		FuncDeclaration fdthis;

		fdthis = sc.parent.isFuncDeclaration();

		// Go upwards until we find the enclosing member function
		fd = fdthis;
		while (true) {
			if (fd == null) {
				// goto Lno;
				return null; // don't have 'this' available
			}
			if (!fd.isNested())
				break;

			Dsymbol parent = fd.parent;
			while (parent != null) {
				TemplateInstance ti = parent.isTemplateInstance();
				if (ti != null)
					parent = ti.parent;
				else
					break;
			}

			fd = fd.parent.isFuncDeclaration();
		}

		if (fd.isThis() == null) {
			// goto Lno;
			return null; // don't have 'this' available
		}

		Assert.isNotNull(fd.vthis);
		return fd;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		FuncDeclaration fd;
		FuncDeclaration fdthis;
		int nested = 0;

		if (type != null) { // assert(global.errors || var);
			return this;
		}

		/*
		 * Special case for typeof(this) and typeof(super) since both should
		 * work even if they are not inside a non-static member function
		 */
		if (sc.intypeof) {
			// Find enclosing struct or class
			for (Dsymbol s = sc.parent; true; s = s.parent) {
				ClassDeclaration cd;
				StructDeclaration sd;

				if (s == null) {
					context.acceptProblem(Problem.newSemanticTypeError(
							"Not in a struct or class",
							IProblem.ThisNotInClassOrStruct, 0, start, length));
					// goto Lerr;
					return semantic_Lerr(sc, context);
				}
				cd = s.isClassDeclaration();
				if (cd != null) {
					type = cd.type;
					return this;
				}
				sd = s.isStructDeclaration();
				if (sd != null) {
					type = sd.type.pointerTo(context);
					return this;
				}
			}
		}

		fdthis = sc.parent.isFuncDeclaration();
		fd = hasThis(sc); // fd is the uplevel function with the 'this'
							// variable
		if (fd == null) {
			// goto Lerr;
			return semantic_Lerr(sc, context);
		}

		Assert.isNotNull(fd.vthis);
		var = fd.vthis;
		Assert.isNotNull(var.parent);
		type = var.type;
		var.isVarDeclaration().checkNestedReference(sc, context);
		/*
		 * #if 0 if (fd != fdthis) // if nested { fdthis.getLevel(fd);
		 * fd.vthis.nestedref = 1; fd.nestedFrameRef = 1; } #endif
		 */
		sc.callSuper |= Scope.CSXthis;
		return this;
	}
	
	public Expression semantic_Lerr(Scope sc, SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError("'this' is only allowed in non-static member functions", IProblem.ThisOnlyAllowedInNonStaticMemberFunctions, 0, start, length));
    	type = Type.tint32;
	    return this;
	}
	
	@Override
	public boolean isBool(boolean result) {
		return result;
	}
		
	@Override
	public int getNodeType() {
		return THIS_EXP;
	}

}
