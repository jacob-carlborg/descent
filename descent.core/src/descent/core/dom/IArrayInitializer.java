package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.ArrayInitializerFragment;

/**
 * An array initializer:
 * 
 * <pre>
 * { len1 : init1, len2 : init2, len3 : init3, ... }
 * </pre>
 */
public interface IArrayInitializer extends IInitializer {
	
	List<ArrayInitializerFragment> fragments();

}
