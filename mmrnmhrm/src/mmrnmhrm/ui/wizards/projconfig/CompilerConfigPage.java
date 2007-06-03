package mmrnmhrm.ui.wizards.projconfig;

import melnorme.util.ui.fields.FieldUtil;
import melnorme.util.ui.fields.StringDialogField;
import melnorme.util.ui.swt.LayoutUtil;
import mmrnmhrm.core.build.DeeCEManager;
import mmrnmhrm.core.build.DeeCompilerOptions;
import mmrnmhrm.core.build.IDeeCE;
import mmrnmhrm.core.model.DeeProject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class CompilerConfigPage extends AbstractConfigPage {

	protected SelectionComboDialogField<DeeCompilerOptions.EBuildTypes> fBuildType;
	protected SelectionComboDialogField<IDeeCE> fCompiler;
	protected StringDialogField fCompilerOptions;
	protected StringDialogField fOptionsPreview;
	
	
	public CompilerConfigPage() {
		fBuildType = new SelectionComboDialogField<DeeCompilerOptions.EBuildTypes>();
		fBuildType.setLabelText("Build Type:");
		fBuildType.setObjectItems(DeeCompilerOptions.EBuildTypes.values());
		
		fCompiler = new SelectionComboDialogField<IDeeCE>();
		fCompiler.setLabelText("Compiler:");
		fCompiler.setObjectItems(DeeCEManager.getAvailableDCEs());
		
		fCompilerOptions = new StringDialogField(SWT.BORDER | SWT.SINGLE);
		fCompilerOptions.setLabelText("Extra Compiler Options (newline separated):");

		fOptionsPreview = new StringDialogField(SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		fOptionsPreview.setLabelText("Compiler Options Preview:");
	}
	
	@Override
	protected void createContents(Composite content) {
		Composite comp;

		comp = FieldUtil.createCompose(content, false, fBuildType);

		comp = FieldUtil.createCompose(content, false, fCompiler);

		comp = FieldUtil.createCompose(content, true, fCompilerOptions);
		LayoutUtil.setDiagonalExpand(comp);
		LayoutUtil.setDiagonalExpand(fCompilerOptions.getTextControl(null));
		
		comp = FieldUtil.createCompose(content, true, fOptionsPreview);
		LayoutUtil.setDiagonalExpand(comp);
		LayoutUtil.setDiagonalExpand(fOptionsPreview.getTextControl(null));
	}
	
	public void init(DeeProject project) {
		super.init(project);
		updateView();
	}
	
	private void updateView() {
		DeeCompilerOptions options = fDeeProject.compilerOptions;
		fBuildType.selectItem(options.buildType.toString());
		fCompiler.selectItem(options.compiler.toString());
		fCompilerOptions.setText(options.extraOptions);
	}

}
