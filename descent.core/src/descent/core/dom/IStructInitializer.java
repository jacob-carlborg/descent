package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.StructInitializerFragment;

/**
 * A struct initializer:
 * 
 * <pre>
 * { id1 : init1, id2 : init2, id3 : init3, ... }
 * </pre>
 */
public interface IStructInitializer extends IInitializer {
	
	List<StructInitializerFragment> fragments();

}
