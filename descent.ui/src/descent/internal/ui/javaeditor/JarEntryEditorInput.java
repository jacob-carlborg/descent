package descent.internal.ui.javaeditor;


import org.eclipse.core.resources.IStorage;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PlatformUI;


/**
 * An EditorInput for a JarEntryFile.
 */
public class JarEntryEditorInput implements IStorageEditorInput {

	private IStorage fJarEntryFile;

	public JarEntryEditorInput(IStorage jarEntryFile) {
		fJarEntryFile= jarEntryFile;
	}

	/*
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof JarEntryEditorInput))
			return false;
		JarEntryEditorInput other= (JarEntryEditorInput) obj;
		return fJarEntryFile.equals(other.fJarEntryFile);
	}

	/*
	 * @see IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/*
	 * @see IEditorInput#getName()
	 */
	public String getName() {
		return fJarEntryFile.getName();
	}

	/*
	 * @see IEditorInput#getFullPath()
	 */
	public String getFullPath() {
		return fJarEntryFile.getFullPath().toString();
	}

	/*
	 * @see IEditorInput#getContentType()
	 */
	public String getContentType() {
		return fJarEntryFile.getFullPath().getFileExtension();
	}

	/*
	 * @see IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return fJarEntryFile.getFullPath().toString();
	}

	/*
	 * @see IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		IEditorRegistry registry= PlatformUI.getWorkbench().getEditorRegistry();
		return registry.getImageDescriptor(fJarEntryFile.getFullPath().getFileExtension());
	}

	/*
	 * @see IEditorInput#exists()
	 */
	public boolean exists() {
		// JAR entries can't be deleted
		return true;
	}

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

	/*
	 * see IStorageEditorInput#getStorage()
	 */
	 public IStorage getStorage() {
	 	return fJarEntryFile;
	 }
}


