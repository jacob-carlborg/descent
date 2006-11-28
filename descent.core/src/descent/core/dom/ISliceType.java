package descent.core.dom;

/**
 * A slice type.
 */
public interface ISliceType extends IType {
	
	IType getInnerType();
	
	IExpression getFrom();
	
	IExpression getTo();

}
