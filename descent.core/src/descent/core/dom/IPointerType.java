package descent.core.dom;

/**
 * A pointer type:
 * 
 * <pre>
 * * type
 * </pre>
 */
public interface IPointerType extends IType {
	
	/**
	 * Returns the pointed type.
	 */
	IType getComponentType();

}
