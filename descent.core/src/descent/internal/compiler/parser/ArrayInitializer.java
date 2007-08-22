package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ArrayInitializer extends Initializer {
	
	public List<Expression> index;
	public List<Initializer> value;
	
	public ArrayInitializer(Loc loc) {
		super(loc);
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, index);
			TreeVisitor.acceptChildren(visitor, value);
		}
		visitor.endVisit(this);
	}
	
	public void addInit(Expression index, Initializer value) {
		if (this.index == null) {
			this.index = new ArrayList<Expression>();
			this.value = new ArrayList<Initializer>();
		}
		this.index.add(index);
		this.value.add(value);
	}
	
	@Override
	public Type inferType(Scope sc, SemanticContext context) {
		if (value != null) {
			for (int i = 0; i < value.size(); i++) {
				if (index.get(i) != null) {
					// goto Lno;
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotInferType, 0, start, length));
					return Type.terror;
				}
			}
			if (value.size() > 0) {
				Initializer iz = (Initializer) value.get(0);
				if (iz != null) {
					Type t = iz.inferType(sc, context);
					t = new TypeSArray(t, new IntegerExp(iz.loc, value.size()));
					t = t.semantic(loc, sc, context);
					return t;
				}
			}
		}
		
		context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotInferType, 0, start, length));
		return Type.terror;
	}
	
	@Override
	public int getNodeType() {
		return ARRAY_INITIALIZER;
	}

}
