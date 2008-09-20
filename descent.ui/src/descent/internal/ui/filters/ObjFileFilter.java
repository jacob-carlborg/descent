package descent.internal.ui.filters;

/**
 * Filters out all .obj files.
 */
public class ObjFileFilter extends FileExtensionFileFilter {

	public ObjFileFilter() {
		super("obj"); //$NON-NLS-1$
	}

}
