package descent.internal.ui.javaeditor;



import descent.core.IClassFile;
import org.eclipse.ui.IEditorInput;


/**
 * Editor input for class files.
 */
public interface IClassFileEditorInput extends IEditorInput {

	/**
	 * Returns the class file acting as input.
	 */
	public IClassFile getClassFile();
}

