package descent.core.dom;

/**
 * A slice type.
 */
public interface ISliceType extends IType {
	
	IType getComponentType();
	
	IExpression getFromExpression();
	
	IExpression getToExpression();

}
