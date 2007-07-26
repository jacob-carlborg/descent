package descent.internal.ui.javaeditor;


import org.eclipse.core.runtime.IAdaptable;

import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

import descent.core.IClassFile;
import descent.core.IJavaElement;
import descent.core.JavaCore;
import descent.core.JavaModelException;

/**
 * The factory which is capable of recreating class file editor
 * inputs stored in a memento.
 */
public class ClassFileEditorInputFactory implements IElementFactory {

	public final static String ID=  "descent.ui.ClassFileEditorInputFactory"; //$NON-NLS-1$
	public final static String KEY= "descent.ui.ClassFileIdentifier"; //$NON-NLS-1$

	public ClassFileEditorInputFactory() {
	}

	/**
	 * @see IElementFactory#createElement
	 */
	public IAdaptable createElement(IMemento memento) {
		String identifier= memento.getString(KEY);
		if (identifier != null) {
			IJavaElement element= JavaCore.create(identifier);
			try {
				return EditorUtility.getEditorInput(element);
			} catch (JavaModelException x) {
			}
		}
		return null;
	}

	public static void saveState(IMemento memento, InternalClassFileEditorInput input) {
		IClassFile c= input.getClassFile();
		memento.putString(KEY, c.getHandleIdentifier());
	}
}
