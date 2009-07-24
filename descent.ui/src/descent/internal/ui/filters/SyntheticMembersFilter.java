package descent.internal.ui.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import descent.core.Flags;
import descent.core.IMember;
import descent.core.JavaModelException;

/**
 * Filters synthetic members
 * 
 * @since 3.1
 */
public class SyntheticMembersFilter extends ViewerFilter {
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (!(element instanceof IMember))
			return true;
		IMember member= (IMember)element;
		if (!(member.isBinary()))
			return true;
//		try {
//			return !Flags.isSynthetic(member.getFlags());
			return false;
//		} catch (JavaModelException e) {
//			return true;
//		}
	}
}