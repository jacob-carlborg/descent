package descent.core.dom;

/**
 * A list of modifiers. They are combined by logical "or" to form
 * combined modifiers.
 */
public interface IModifier {
	
	int NONE = 0;
	int PRIVATE = 1;
	int PACKAGE = 2;
	int PROTECTED = 4;
	int PUBLIC = 8;	
	int EXPORT = 0x10;
	int UNDEFINED = 0x20;
	int STATIC = 0x40;
	int FINAL = 0x80;
	int ABSTRACT = 0x100;
	int OVERRIDE = 0x200;
	int AUTO = 0x400;
	int SYNCHRONIZED = 0x800;
	int DEPRECATED = 0x1000;
	int EXTERN = 0x2000;
	int CONST =  0x4000;
	int SCOPE =  0x8000;

}
