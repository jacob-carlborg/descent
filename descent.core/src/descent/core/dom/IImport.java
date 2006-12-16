package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Name;
import descent.internal.core.dom.SelectiveImport;
import descent.internal.core.dom.SimpleName;

/**
 * An import present in an import declaration:
 * 
 * <pre>
 * ble = foo.bar : some = bla
 * --------------------------
 *        ^        ----------
 *        |             ^
 *     import           |
 *               selective import
 * </pre>
 */
public interface IImport extends IElement {
	
	/**
	 * Returns the qualified name to import.
	 */
	Name getName();
	
	/**
	 * Returns the alias of the import, if any, or <code>null</code>.
	 */
	SimpleName getAlias();
	
	/**
	 * Returns the selective imports to import from the qualified name. May
	 * be empty if no selective imports are defined.
	 */
	List<SelectiveImport> selectiveImports();

}
