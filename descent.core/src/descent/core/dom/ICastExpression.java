package descent.core.dom;

public interface ICastExpression extends IExpression {
	
	IType getType();
	
	IExpression getExpression();

}
