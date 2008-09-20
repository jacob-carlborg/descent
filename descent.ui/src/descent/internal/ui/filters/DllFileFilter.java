package descent.internal.ui.filters;


/**
 * Filters out all .dll files.
 */
public class DllFileFilter extends FileExtensionFileFilter {

	public DllFileFilter() {
		super("dll"); //$NON-NLS-1$
	}

}
