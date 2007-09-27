package mmrnmhrm.ui.preferences;

import static melnorme.miscutil.Assert.assertNotNull;

import java.util.List;

import melnorme.miscutil.StringUtil;
import melnorme.util.ui.fields.FieldUtil;
import melnorme.util.ui.fields.ProjectContainerSelectionDialog;
import melnorme.util.ui.fields.SelectionComboDialogField;
import melnorme.util.ui.fields.StringDialogField;
import melnorme.util.ui.swt.SWTLayoutUtil;
import melnorme.util.ui.swt.RowComposite;
import mmrnmhrm.core.build.DeeBuilder;
import mmrnmhrm.core.build.DeeCompilerOptions;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProjectOptions;
import mmrnmhrm.ui.actions.OperationsManager;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class DeeProjectOptionsBlock implements IDialogFieldListener  {
	
	DeeProjectOptions fDeeProjOptions;
	DeeProjectOptions fOverlayOptions;

	
	protected SelectionComboDialogField<DeeCompilerOptions.EBuildTypes> fBuildType;
	protected StringDialogField fArtifactName;
	protected StringButtonDialogField fOutputDir;
	protected StringButtonDialogField fCompilerTool;
	//protected IPath fCompilerToolPath;
	protected StringDialogField fExtraOptions;

	protected StringDialogField fOptionsPreview;



	private Shell shell;
	
	public DeeProjectOptionsBlock() {

		fBuildType = new SelectionComboDialogField<DeeCompilerOptions.EBuildTypes>();
		fBuildType.setLabelText("Build Type:");
		fBuildType.setObjectItems(DeeCompilerOptions.EBuildTypes.values());
		fBuildType.setDialogFieldListener(this);
		
		fArtifactName = new StringDialogField(SWT.BORDER | SWT.SINGLE);
		fArtifactName.setLabelText("Target name:");
		fArtifactName.setDialogFieldListener(this);
		
		fOutputDir = new StringButtonDialogField(new IStringButtonAdapter() {
			//@Override
			public void changeControlPressed(DialogField field) {
				ProjectContainerSelectionDialog containerDialog;
				containerDialog	= new ProjectContainerSelectionDialog(getShell(), fDeeProjOptions.getProject());
				containerDialog.dialog.setTitle("Folder Selection"); 
				containerDialog.dialog.setMessage("Choose the output location folder.");

				IResource initSelection = null;
				if (fOutputDir != null) {
					initSelection = fDeeProjOptions.getProject().findMember(new Path(fOutputDir.getText()));
					containerDialog.dialog.setInitialSelection(initSelection);
				}

				IContainer container = containerDialog.chooseContainer();
				if (container != null) {
					fOutputDir.setText(container.getProjectRelativePath().toString());
					//fCompilerToolPath = container.getProjectRelativePath();
				}
				
			}
		});
		fOutputDir.setLabelText("Output folder:");
		fOutputDir.setButtonLabel("Browse");
		fOutputDir.setDialogFieldListener(this);
		
		fCompilerTool = new StringButtonDialogField(new IStringButtonAdapter() {
			//@Override
			public void changeControlPressed(DialogField field) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setFilterPath(fCompilerTool.getText());
				if (Platform.getOS().equals(Platform.OS_WIN32)) {
					dialog.setFilterExtensions(new String[] { "*.exe;*.bat" });
				} else {
					dialog.setFilterExtensions(new String[] { "*" });
				}
				dialog.setFilterNames(new String[] { "Executables" });
				String newPath = dialog.open();
				if (newPath != null) {
					fCompilerTool.setText((new Path(newPath)).toString());
				}
			}
		});
		fCompilerTool.setLabelText("Build Tool:");
		fCompilerTool.setButtonLabel("Browse");
		fCompilerTool.setDialogFieldListener(this);
		
		fExtraOptions = new StringDialogField(SWT.BORDER | SWT.MULTI);
		fExtraOptions.setLabelText("Extra Compiler Options (newline separated):");
		fExtraOptions.setDialogFieldListener(this);

		fOptionsPreview = new StringDialogField(SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		fOptionsPreview.setLabelText("Compiler Options Preview:");
		
	}
	
	protected void internalInit(DeeProjectOptions projectInfo) {
		assertNotNull(projectInfo);
		fDeeProjOptions = projectInfo;
		fOverlayOptions = fDeeProjOptions.clone();
		updateView();
	}
	
	public void init2(IScriptProject scriptProject) {
		internalInit(DeeModel.getDeeProjectInfo(scriptProject));
	}

	
	public Composite createControl(Composite parent) {
		Composite content = parent;
		shell = parent.getShell();
		content = new RowComposite(parent);
		
		RowComposite rowComposite = new RowComposite(content);
		SWTLayoutUtil.setWidthHint(rowComposite, 200);
		SWTLayoutUtil.enableDiagonalExpand(rowComposite);

		
		//LayoutUtil.enableHorizontalGrabbing(content);
/*		FieldUtil.doDefaultLayout2(topcontent, false, 
				fArtifactName, fOutputDir, fCompilerTool);
		LayoutUtil.enableHorizontalGrabbing(fOutputDir.getTextControl(null));
		LayoutUtil.setHorizontalSpan(fArtifactName.getTextControl(null), 1);
		*/
		Composite comp;


		comp = FieldUtil.createCompose(rowComposite, false, fBuildType);
		//LayoutUtil.setHorizontalSpan(fBuildType.getLabelControl(null), 1);
		SWTLayoutUtil.setWidthHint(fBuildType.getLabelControl(null), 100);
		SWTLayoutUtil.setWidthHint(fBuildType.getComboControl(null), 80);
		
		comp = FieldUtil.createCompose(rowComposite, false, fArtifactName);
		SWTLayoutUtil.setWidthHint(fArtifactName.getLabelControl(null), 100);
		SWTLayoutUtil.setWidthHint(fArtifactName.getTextControl(null), 120);

		
		comp = FieldUtil.createCompose(rowComposite, false, fOutputDir);
		SWTLayoutUtil.setWidthHint(fOutputDir.getLabelControl(null), 100);
		SWTLayoutUtil.enableHorizontalGrabbing(fOutputDir.getTextControl(null));

		
		comp = FieldUtil.createCompose(rowComposite, false, fCompilerTool);
		SWTLayoutUtil.setWidthHint(fCompilerTool.getLabelControl(null), 100);
		SWTLayoutUtil.enableHorizontalGrabbing(fCompilerTool.getTextControl(null));

		
		comp = FieldUtil.createCompose(rowComposite, true, fExtraOptions);
		SWTLayoutUtil.enableDiagonalExpand(comp);
		SWTLayoutUtil.enableDiagonalExpand(fExtraOptions.getTextControl(null));

		comp = FieldUtil.createCompose(rowComposite, true, fOptionsPreview);
		SWTLayoutUtil.enableDiagonalExpand(comp);
		SWTLayoutUtil.enableDiagonalExpand(fOptionsPreview.getTextControl(null));

		return content;
	}

	private Shell getShell() {
		return shell;
	}
	
	private void updateView() {
		DeeCompilerOptions options = fOverlayOptions.compilerOptions;
		fBuildType.setTextWithoutUpdate(options.buildType.toString());
		fArtifactName.setTextWithoutUpdate(options.artifactName);
		fOutputDir.setTextWithoutUpdate(options.outputDir.toString());
		fCompilerTool.setTextWithoutUpdate(options.buildTool);
		fExtraOptions.setTextWithoutUpdate(options.extraOptions);

		updateBuildPreview(options);
	}
	
	//@Override
	public void dialogFieldChanged(DialogField field) {
		DeeCompilerOptions options = fOverlayOptions.compilerOptions;
		options.buildType = fBuildType.getSelectedObject();
		options.artifactName = fArtifactName.getText();
		options.outputDir = new Path(fOutputDir.getText());
		options.buildTool = fCompilerTool.getText();
		options.extraOptions = fExtraOptions.getText();
		
		updateBuildPreview(options);
	}

	private void updateBuildPreview(DeeCompilerOptions options) {
		List<String> cmdLine = DeeBuilder.getDemoCmdLine(fDeeProjOptions.dltkProj,
				fOverlayOptions, new NullProgressMonitor());
		fOptionsPreview.setText(StringUtil.collToString(cmdLine, "  "));
	}

	public boolean performOk() {
		return OperationsManager.executeOperation(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				fDeeProjOptions.compilerOptions = fOverlayOptions.compilerOptions;
				fDeeProjOptions.saveProjectConfigFile();
			}
		}, "Saving Project Compile Option");
	}
	
}
