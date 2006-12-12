package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Declaration;
import descent.internal.core.dom.ExternDeclaration.Linkage;

/**
 * A linkage declaration:
 * 
 * <pre>
 * extern(linkage) { }
 * </pre>
 */
public interface IExternDeclaration extends IDeclaration {
	
	/**
	 * Returns the linkage identifier. Check the constans defined
	 * in this inteface.
	 */
	Linkage getLinkage();
	
	/**
	 * Returns the declarations in this linkage.
	 */
	List<Declaration> declarations();

}
