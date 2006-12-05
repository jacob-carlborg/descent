package descent.core.dom;

import descent.internal.core.dom.Declaration;

/**
 * A linkage declaration:
 * 
 * <pre>
 * extern(linkage) { }
 * </pre>
 */
public interface ILinkDeclaration extends IDeclaration {
	
	int LINKAGE_D = 1;
	int LINKAGE_C = 2;
	int LINKAGE_CPP = 3;
	int LINKAGE_WINDOWS = 4;
	int LINKAGE_PASCAL = 5;
	
	/**
	 * Returns the linkage identifier. Check the constans defined
	 * in this inteface.
	 */
	int getLinkage();
	
	/**
	 * Returns the declarations in this linkage.
	 */
	IDeclaration[] getDeclarationDefinitions();

}
