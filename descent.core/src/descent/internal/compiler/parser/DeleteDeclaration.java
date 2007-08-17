package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class DeleteDeclaration extends FuncDeclaration {
	
	public List<Argument> arguments;
	public int varargs;
	
	public DeleteDeclaration(Loc loc, List<Argument> arguments, int varags) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.classDelete), STC.STCundefined, null);
		this.arguments = arguments;
		this.varargs = varags;
	}
	
	@Override
	public boolean isDelete() {
		return true;
	}
	
	@Override
	public boolean isVirtual() {
		return false;
	}
	
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
	public void semantic(Scope sc, SemanticContext context) {
		ClassDeclaration cd;

		parent = sc.parent;
		Dsymbol parent = toParent();
		cd = parent.isClassDeclaration();
		if (cd == null && parent.isStructDeclaration() == null) {
			// TODO semantic point out the "delete" token
			context.acceptProblem(Problem.newSemanticTypeError("Delete deallocators only are for class or struct definitions", IProblem.DeleteDeallocatorsOnlyForClassOrStruct, 0, start, "delete".length()));
		}
		type = new TypeFunction(arguments, Type.tvoid, 0, LINK.LINKd);

		type = type.semantic(loc, sc, context);
		Assert.isTrue(type.ty == TY.Tfunction);

		// Check that there is only one argument of type void*
		TypeFunction tf = (TypeFunction) type;
		if (Argument.dim(tf.parameters, context) != 1) {
			// TODO semantic point out the "delete" token
			context.acceptProblem(Problem.newSemanticTypeError("One argument of type void* expected", IProblem.IllegalParameters, 0, start, "delete".length()));
		} else {
			Argument a = Argument.getNth(tf.parameters, 0, context);
			if (!a.type.equals(Type.tvoid.pointerTo(context))) {
				context.acceptProblem(Problem.newSemanticTypeError("One argument of type void* expected", IProblem.IllegalParameters, 0, a.type.start, a.type.length));
			}
		}

		super.semantic(sc, context);
	}
	
	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return false;
	}
	
	@Override
	public boolean addPostInvariant(SemanticContext context) {
		return false;
	}
	
	@Override
	public int getNodeType() {
		return DELETE_DECLARATION;
	}

}
