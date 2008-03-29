package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class NewDeclaration extends FuncDeclaration {

	public Arguments arguments;
	public int varargs;
	public int newStart;

	public NewDeclaration(Loc loc, Arguments arguments, int varargs) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.classNew), STC.STCstatic,
				null);
		this.arguments = arguments;
		this.varargs = varargs;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			// Template args?
			TreeVisitor.acceptChildren(visitor, arguments);
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean addPostInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return NEW_DECLARATION;
	}

	@Override
	public NewDeclaration isNewDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "allocator";
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		ClassDeclaration cd;
		Type tret;

		parent = sc.parent;
		Dsymbol parent = toParent();
		cd = parent.isClassDeclaration();
		if (cd == null && parent.isStructDeclaration() == null) {
			context
					.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.NewAllocatorsOnlyForClassOrStruct, this));
		}
		tret = Type.tvoid.pointerTo(context);
		type = new TypeFunction(arguments, tret, varargs, LINK.LINKd);

		type = type.semantic(loc, sc, context);
		assert (type.ty == TY.Tfunction);

		// Check that there is at least one argument of type uint
		TypeFunction tf = (TypeFunction) type;
		if (Argument.dim(tf.parameters, context) < 1) {
			context.acceptProblem(Problem.newSemanticTypeErrorLoc(
					IProblem.AtLeastOneArgumentOfTypeExpected, this,
					new String[] { "uint" }));
		} else {
			Argument a = Argument.getNth(tf.parameters, 0, context);
			if (!a.type.equals(Type.tuns32)) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.FirstArgumentMustBeOfType, a.type, new String[] { "uint" }));
			}
		}

		super.semantic(sc, context);
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		NewDeclaration f;

		f = new NewDeclaration(loc, null, varargs);

		super.syntaxCopy(f, context);

		f.arguments = Argument.arraySyntaxCopy(arguments, context);

		return f;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("new");
		Argument.argsToCBuffer(buf, hgs, arguments, varargs, context);
		bodyToCBuffer(buf, hgs, context);
	}
	
	@Override
	public int getErrorStart() {
		return newStart;
	}
	
	@Override
	public int getErrorLength() {
		return 3;
	}

}
