package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class DeleteDeclaration extends FuncDeclaration {

	public List<Argument> arguments;
	public int deleteStart; // where the "delete" keyword starts

	public DeleteDeclaration(Loc loc, List<Argument> arguments) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.classDelete),
				STC.STCundefined, null);
		this.arguments = arguments;
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
		return false;
	}

	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return DELETE_DECLARATION;
	}

	@Override
	public boolean isDelete() {
		return true;
	}
	
	@Override
	public DeleteDeclaration isDeleteDeclaration() {
		return this;
	}

	@Override
	public boolean isVirtual() {
		return false;
	}

	@Override
	public String kind() {
		return "deallocator";
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		ClassDeclaration cd;

		parent = sc.parent;
		Dsymbol parent = toParent();
		cd = parent.isClassDeclaration();
		if (cd == null && parent.isStructDeclaration() == null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.DeleteDeallocatorsOnlyForClassOrStruct, 0,
					deleteStart, 6));
		}
		type = new TypeFunction(arguments, Type.tvoid, 0, LINK.LINKd);

		type = type.semantic(loc, sc, context);
		Assert.isTrue(type.ty == TY.Tfunction);

		// Check that there is only one argument of type void*
		TypeFunction tf = (TypeFunction) type;
		if (Argument.dim(tf.parameters, context) != 1) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.OneArgumentOfTypeExpected, 0, deleteStart, 6,
					new String[] { "void*" }));
		} else {
			Argument a = Argument.getNth(tf.parameters, 0, context);
			if (!a.type.equals(Type.tvoid.pointerTo(context))) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.OneArgumentOfTypeExpected, 0, a.type.start,
						a.type.length, new String[] { "void*" }));
			}
		}

		super.semantic(sc, context);
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		DeleteDeclaration f = new DeleteDeclaration(loc, null);
		super.syntaxCopy(f);
		f.arguments = arraySyntaxCopyArguments(arguments);
		return f;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("delete");
		argsToCBuffer(buf, hgs, arguments, 0, context);
		bodyToCBuffer(buf, hgs, context);
	}

}
