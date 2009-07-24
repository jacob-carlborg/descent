package descent.internal.ui.typehierarchy;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.ui.PlatformUI;

import descent.core.IJavaElement;

import descent.internal.corext.util.Messages;

import descent.ui.JavaElementLabels;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.viewsupport.JavaElementImageProvider;

/**
 * Action used for the type hierarchy forward / backward buttons
 */
public class HistoryAction extends Action {

	private TypeHierarchyViewPart fViewPart;
	private IJavaElement fElement;
	
	public HistoryAction(TypeHierarchyViewPart viewPart, IJavaElement element) {
        super("", AS_RADIO_BUTTON); //$NON-NLS-1$
		fViewPart= viewPart;
		fElement= element;		
		
		String elementName= JavaElementLabels.getElementLabel(element, JavaElementLabels.ALL_POST_QUALIFIED | JavaElementLabels.ALL_DEFAULT);
		setText(elementName);
		setImageDescriptor(getImageDescriptor(element));
				
		setDescription(Messages.format(TypeHierarchyMessages.HistoryAction_description, elementName)); 
		setToolTipText(Messages.format(TypeHierarchyMessages.HistoryAction_tooltip, elementName)); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.HISTORY_ACTION);
	}
	
	private ImageDescriptor getImageDescriptor(IJavaElement elem) {
		JavaElementImageProvider imageProvider= new JavaElementImageProvider();
		ImageDescriptor desc= imageProvider.getBaseImageDescriptor(elem, 0);
		imageProvider.dispose();
		return desc;
	}
	
	/*
	 * @see Action#run()
	 */
	public void run() {
		fViewPart.gotoHistoryEntry(fElement);
	}
	
}
