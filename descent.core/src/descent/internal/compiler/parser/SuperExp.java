package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public class SuperExp extends ThisExp {
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		FuncDeclaration fd;
		FuncDeclaration fdthis;
		ClassDeclaration cd;
		Dsymbol s;

		if (type != null)
			return this;

		/*
		 * Special case for typeof(this) and typeof(super) since both should
		 * work even if they are not inside a non-static member function
		 */
		if (sc.intypeof) {
			// Find enclosing class
			for (Dsymbol s2 = sc.parent; true; s2 = s2.parent) {
				ClassDeclaration cd2;

				if (s2 == null) {
					context.acceptProblem(Problem.newSemanticTypeError(
							"Not in a class", IProblem.SuperNotInClass, 0,
							start, length));
					// goto Lerr;
					return semantic_Lerr(sc, context);
				}
				cd2 = s2.isClassDeclaration();
				if (cd2 == null) {
					cd2 = cd2.baseClass;
					if (cd2 == null) {
						context.acceptProblem(Problem.newSemanticTypeError(
								"Class " + s2.ident.ident.string
										+ " has no 'super'",
								IProblem.ClassHasNoSuper, 0, start, length));
						// goto Lerr;
						return semantic_Lerr(sc, context);
					}
					type = cd2.type;
					return this;
				}
			}
		}

		fdthis = sc.parent.isFuncDeclaration();
		fd = hasThis(sc);
		if (fd == null) {
			// goto Lerr;
			return semantic_Lerr(sc, context);
		}
		Assert.isNotNull(fd.vthis);
		var = fd.vthis;
		Assert.isNotNull(var.parent);

		s = fd.toParent();
		while (s != null && s.isTemplateInstance() != null)
			s = s.toParent();
		Assert.isNotNull(s);
		cd = s.isClassDeclaration();
		// printf("parent is %s %s\n", fd.toParent().kind(),
		// fd.toParent().toChars());
		if (cd == null) {
			// goto Lerr;
			return semantic_Lerr(sc, context);
		}
		if (cd.baseClass == null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					"Class " + cd.ident.ident.string
							+ " has no base class",
					IProblem.ClassHasNoSuper, 0, start, length));
			type = fd.vthis.type;
		} else {
			type = cd.baseClass.type;
		}

		var.isVarDeclaration().checkNestedReference(sc, context);
		/*
		 * #if 0 if (fd != fdthis) { fdthis.getLevel(loc, fd);
		 * fd.vthis.nestedref = 1; fd.nestedFrameRef = 1; } #endif
		 */

		sc.callSuper |= Scope.CSXsuper;
		return this;
	}
	
	public Expression semantic_Lerr(Scope sc, SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError("'super' is only allowed in non-static member functions", IProblem.SuperOnlyAllowedInNonStaticMemberFunctions, 0, start, length));
    	type = Type.tint32;
	    return this;
	}
	
	@Override
	public int kind() {
		return SUPER_EXP;
	}

}
