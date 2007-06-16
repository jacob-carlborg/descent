package descent.internal.ui.text.correction;

import org.eclipse.ui.IMarkerResolution2;

/**
 *
 */
public interface IJavaMarkerResolutionExtension extends IMarkerResolution2 {

	/**
	 * Returns the relevance of this resolution.
	 * <p>
	 * The relevance is used to determine if this proposal is more
	 * relevant than another proposal.</p>
	 *
	 * @return the relevance of this completion proposal in the range of [0, 100]
	 */
	int getRelevance();
}
