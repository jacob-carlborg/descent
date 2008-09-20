package descent.internal.ui.filters;

/**
 * Filters out all .map files.
 */
public class MapFileFilter extends FileExtensionFileFilter {

	public MapFileFilter() {
		super("map"); //$NON-NLS-1$
	}

}
