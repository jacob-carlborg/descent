package descent.core.dom;

/**
 * A strong type of the language.
 */
public interface IStrongType extends IElement {
	
	int TYPEDEF = 1;
	int STRUCT = 2;
	int UNION = 3;
	int CLASS = 4;
	int ENUM = 5;
	int INTERFACE = 6;
	int FUNCTION = 7;
	int DELEGATE = 8;
	int RETURN = 9;
	int SUPER = 10;
	
	/**
	 * Returns one of the constants defined in this interface.
	 */
	int getStrongTypeType();

}
