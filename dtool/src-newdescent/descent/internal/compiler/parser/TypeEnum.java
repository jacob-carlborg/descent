package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.core.compiler.IProblem;
import descent.core.domX.IASTVisitor;

public class TypeEnum extends Type {
	
	public EnumDeclaration sym;

	public TypeEnum(EnumDeclaration sym) {
		super(TY.Tenum, null);
		this.sym = sym;
	}
	
	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 on fake class");
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		sym.semantic(sc, context);
	    return merge(context);
	}
	
	@Override
	public Type toBasetype(SemanticContext context) {
		if (sym.memtype == null)
	    {
			context.acceptProblem(Problem
					.newSemanticTypeError(
							"Enum is forward reference",
							IProblem.EnumValueOverflow, 0,
							sym.start, sym.length));
			return tint32;
	    }
	    return sym.memtype.toBasetype(context);
	}
	
	@Override
	public boolean isintegral() {
		return true;
	}
	
	@Override
	public boolean isunsigned() {
		return sym.memtype.isunsigned();
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		// Initialize to first member of enum
		Expression e;
	    e = new IntegerExp(Loc.ZERO, sym.defaultval, this);
	    return e;
	}

	@Override
	public int getNodeType() {
		return TYPE_ENUM;
	}

}
