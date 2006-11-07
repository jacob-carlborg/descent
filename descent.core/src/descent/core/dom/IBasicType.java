package descent.core.dom;


/**
 * A basic (primitive) type.
 */
public interface IBasicType extends IType {
	
	int VOID = 1;
	int INT8 = 2;
	int UNS8 = 3;
	int INT16 = 4;
	int UNS16 = 5;
	int INT32 = 6;
	int UNS32 = 7;
	int INT64 = 8;
	int UNS64 = 9;
	int FLOAT32 = 10;
	int FLOAT64 = 11;
	int FLOAT80 = 12;
	int IMAGINARY32 = 13;
	int IMAGINARY64 = 14;
	int IMAGINARY80 = 15;
	int COMPLEX32 = 16;
	int COMPLEX64 = 17;
	int COMPLEX80 = 18;
	int BIT = 19;
	int BOOL = 20;
	int CHAR = 21;
	int WCHAR = 22;
	int DCHAR = 23;
	
	/**
	 * Returns the type of this basic type. Check the constants
	 * declared in this interface.
	 */
	int getBasicTypeKind();

}
