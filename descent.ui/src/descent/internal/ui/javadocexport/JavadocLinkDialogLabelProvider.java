package descent.internal.ui.javadocexport;

import java.net.URL;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import descent.internal.corext.util.Messages;

import descent.ui.ISharedImages;
import descent.ui.JavaElementImageDescriptor;
import descent.ui.JavaUI;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.viewsupport.JavaElementImageProvider;


public class JavadocLinkDialogLabelProvider extends LabelProvider {

	public String getText(Object element) {
		if (element instanceof JavadocLinkRef) {
			JavadocLinkRef ref= (JavadocLinkRef) element;
			URL url= ref.getURL();
			String text= ref.getFullPath().lastSegment();
			if (url != null) {
				Object[] args= new Object[] { text, url.toExternalForm() };
				return Messages.format(JavadocExportMessages.JavadocLinkDialogLabelProvider_configuredentry, args); 
			} else {
				return Messages.format(JavadocExportMessages.JavadocLinkDialogLabelProvider_notconfiguredentry, text); 
			}
		}
		return super.getText(element);
	}
	
	public Image getImage(Object element) {
		if (element instanceof JavadocLinkRef) {
			JavadocLinkRef ref= (JavadocLinkRef) element;
			ImageDescriptor desc;
			if (ref.isProjectRef()) {
				desc= PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
			} else {
				desc= JavaUI.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_JAR);
			}
			if (ref.getURL() == null) {
				return JavaPlugin.getImageDescriptorRegistry().get(new JavaElementImageDescriptor(desc, JavaElementImageDescriptor.WARNING, JavaElementImageProvider.SMALL_SIZE));
			}
			return JavaPlugin.getImageDescriptorRegistry().get(desc);
		}
		return null;
	}

}
