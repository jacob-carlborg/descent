package descent.internal.debug.ui.jres;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import descent.internal.debug.ui.JDIImageDescriptor;
import descent.internal.debug.ui.jres.LibraryContentProvider.SubElement;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.viewsupport.ImageDescriptorRegistry;
import descent.ui.ISharedImages;
import descent.ui.JavaUI;

/**
 * Label provider for jre libraries.
 * 
 * @since 3.2
 */
public class LibraryLabelProvider extends LabelProvider {

	private ImageDescriptorRegistry fRegistry= JavaPlugin.getImageDescriptorRegistry();

	public Image getImage(Object element) {
		if (element instanceof LibraryStandin) {
			LibraryStandin library= (LibraryStandin) element;
			IPath sourcePath= library.getSystemLibrarySourcePath();
			String key = null;
			if (sourcePath != null && !Path.EMPTY.equals(sourcePath)) {
                key = ISharedImages.IMG_OBJS_EXTERNAL_ARCHIVE_WITH_SOURCE;
			} else {
				key = ISharedImages.IMG_OBJS_EXTERNAL_ARCHIVE;
			}
			IStatus status = library.validate();
			if (!status.isOK()) {
				ImageDescriptor base = JavaUI.getSharedImages().getImageDescriptor(key);
				JDIImageDescriptor descriptor= new JDIImageDescriptor(base, JDIImageDescriptor.IS_OUT_OF_SYNCH);
				return JavaPlugin.getImageDescriptorRegistry().get(descriptor);
			}
			return JavaUI.getSharedImages().getImage(key);
		} else if (element instanceof SubElement) {
			if (((SubElement)element).getType() == SubElement.SOURCE_PATH) {
				return fRegistry.get(JavaPluginImages.DESC_OBJS_SOURCE_ATTACH_ATTRIB); // todo: change image
			}
			return fRegistry.get(JavaPluginImages.DESC_OBJS_JAVADOC_LOCATION_ATTRIB); // todo: change image
		}
		return null;
	}

	public String getText(Object element) {
		if (element instanceof LibraryStandin) {
			return ((LibraryStandin)element).getSystemLibraryPath().toOSString();
		} else if (element instanceof SubElement) {
			SubElement subElement= (SubElement) element;
			StringBuffer text= new StringBuffer();
			if (subElement.getType() == SubElement.SOURCE_PATH) {
				text.append(JREMessages.VMLibraryBlock_0);
				IPath systemLibrarySourcePath= subElement.getParent().getSystemLibrarySourcePath();
				if (systemLibrarySourcePath != null && !Path.EMPTY.equals(systemLibrarySourcePath)) {
					text.append(systemLibrarySourcePath.toOSString());
				} else {
					text.append(JREMessages.VMLibraryBlock_1);
				}
			} else {
				text.append(JREMessages.VMLibraryBlock_2);
				URL javadocLocation= subElement.getParent().getJavadocLocation();
				if (javadocLocation != null) {
					text.append(javadocLocation.toExternalForm());
				} else {
					text.append(JREMessages.VMLibraryBlock_1);
				}
			}
			return text.toString();
		}
		return null;
	}

}