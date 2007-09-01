package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class CtorDeclaration extends FuncDeclaration {

	public List<Argument> arguments;
	public int varargs;
	public int thisStart; // where the "this" keyword starts

	public CtorDeclaration(Loc loc, List<Argument> arguments, int varags) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.ctor), STC.STCundefined, null);
		this.arguments = arguments;
		this.varargs = varags;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
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
		return (vthis != null && context.global.params.useInvariants);
	}

	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return CTOR_DECLARATION;
	}

	@Override
	public CtorDeclaration isCtorDeclaration() {
		return this;
	}

	@Override
	public boolean isVirtual() {
		return false;
	}

	@Override
	public String kind() {
		return "constructor";
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		ClassDeclaration cd;
		Type tret;

		sc = sc.push();
		sc.stc &= ~STC.STCstatic; // not a static constructor

		parent = sc.parent;
		Dsymbol parent = toParent();
		cd = parent.isClassDeclaration();
		if (cd == null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ConstructorsOnlyForClass, 0, thisStart, 4));
			tret = Type.tvoid;
		} else {
			tret = cd.type; // .referenceTo();
		}
		type = new TypeFunction(arguments, tret, varargs, LINK.LINKd);

		sc.flags |= Scope.SCOPEctor;
		type = type.semantic(loc, sc, context);
		sc.flags &= ~Scope.SCOPEctor;

		// Append:
		// return this;
		// to the function body
		if (fbody != null) {
			Expression e;
			Statement s;

			e = new ThisExp(loc);
			e.synthetic = true;
			s = new ReturnStatement(loc, e);
			s.synthetic = true;
			fbody = new CompoundStatement(loc, fbody, s);
			fbody.synthetic = true;
		}

		super.semantic(sc, context);

		sc.pop();
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		CtorDeclaration f;

		f = new CtorDeclaration(loc, null, varargs);

		f.outId = outId;
		f.frequire = frequire != null ? frequire.syntaxCopy() : null;
		f.fensure = fensure != null ? fensure.syntaxCopy() : null;
		f.fbody = fbody != null ? fbody.syntaxCopy() : null;

		if (fthrows != null) {
			throw new IllegalStateException("assert(!fhtorws);");
		}

		f.arguments = arraySyntaxCopyArguments(arguments);
		return f;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("this");
		argsToCBuffer(buf, hgs, arguments, varargs, context);
		bodyToCBuffer(buf, hgs, context);
	}

	@Override
	public String toChars(SemanticContext context) {
		return "this";
	}

}
