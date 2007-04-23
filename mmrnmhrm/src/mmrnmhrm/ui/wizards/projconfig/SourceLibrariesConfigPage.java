package mmrnmhrm.ui.wizards.projconfig;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SourceLibrariesConfigPage extends AbstractConfigPage {

	TreeListDialogField fSrcFoldersList;
	

	
	public SourceLibrariesConfigPage() {

	}

	
	@Override
	protected void createContents(Composite content) {
		Label label = new Label(content, SWT.NONE);
		label.setText("TEST");
		label.setLayoutData(new GridData());
		content.setLayout(new GridLayout());
//		LayoutUtil.doDefaultLayout(content, new DialogField[] { fSrcFoldersList}, true, SWT.DEFAULT, SWT.DEFAULT);
/*		LayoutUtil.setHorizontalGrabbing(fSrcFoldersList.getTreeControl(null));
	
		fSrcFoldersList.setButtonsMinWidth(converter.convertWidthInCharsToPixels(24));
*/	}
	
	

}
