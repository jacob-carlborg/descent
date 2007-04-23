package mmrnmhrm.ui.wizards.projconfig;

import mmrnmhrm.ui.util.ColumnComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CompilerConfigPage extends AbstractConfigPage {

	SelectionComboDialogField fBuildType;
	
	
	enum BuildTypes {
		BINARY,
		LIB_STATIC,
		LIB_DYNAMIC
	}
	
	public CompilerConfigPage() {
		fBuildType = new SelectionComboDialogField();
		fBuildType.setObjectItems(BuildTypes.values());
		fBuildType.setLabelText("Build Type");
	}
	
	@Override
	protected void createContents(Composite content) {
		Label label = new Label(content, SWT.NONE);
		label.setText("TEST");
		Composite content2 = new ColumnComposite(content, 2);
		fBuildType.doFillIntoGrid(content2, 2);
	}
	
}
