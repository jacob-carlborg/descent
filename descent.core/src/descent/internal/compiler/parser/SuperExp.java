package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class SuperExp extends ThisExp {

	public SuperExp(Loc loc) {
		super(loc);
		op = TOK.TOKsuper;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return SUPER_EXP;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context) {
		super.scanForNestedRef(sc, context);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		FuncDeclaration fd;
		@SuppressWarnings("unused")
		FuncDeclaration fdthis;
		ClassDeclaration cd;
		Dsymbol s;

		if (type != null) {
			return this;
		}

		/*
		 * Special case for typeof(this) and typeof(super) since both should
		 * work even if they are not inside a non-static member function
		 */
		if (sc.intypeof != 0) {
			// Find enclosing class
			for (Dsymbol s2 = sc.parent; true; s2 = s2.parent) {
				ClassDeclaration cd2;

				if (s2 == null) {
					if (context.acceptsErrors()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.SuperNotInClass, this));
					}
					// goto Lerr;
					return semantic_Lerr(sc, context);
				}
				cd2 = s2.isClassDeclaration();
				if (cd2 != null) {
					cd2 = cd2.baseClass;
					if (cd2 == null) {
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.ClassHasNoSuper, this,
									new String[] { new String(s2.ident.ident) }));
						}
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
		Assert.isNotNull(fd.vthis());
		var = fd.vthis();
		Assert.isNotNull(var.parent);

		s = fd.toParent();
		while (s != null && s.isTemplateInstance() != null) {
			s = s.toParent();
		}
		Assert.isNotNull(s);
		cd = s.isClassDeclaration();
		// fd.toParent().toChars());
		if (cd == null) {
			// goto Lerr;
			return semantic_Lerr(sc, context);
		}
		if (cd.baseClass == null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.ClassHasNoSuper, this,
						new String[] { new String(cd.ident.ident) }));
			}
			type = fd.vthis().type;
		} else {
			type = cd.baseClass.type;
		}

		var.isVarDeclaration().checkNestedReference(sc, loc, context);
		/*
		 * #if 0 if (fd != fdthis) { fdthis.getLevel(loc, fd);
		 * fd.vthis.nestedref = 1; fd.nestedFrameRef = 1; } #endif
		 */

		if (0 == sc.intypeof) {
			sc.callSuper |= Scope.CSXsuper;
		}
		return this;
	}

	@Override
	public Expression semantic_Lerr(Scope sc, SemanticContext context) {
		if (context.acceptsErrors()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.SuperOnlyAllowedInNonStaticMemberFunctions, this));
		}
		type = Type.tint32;
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("super");
	}

}
