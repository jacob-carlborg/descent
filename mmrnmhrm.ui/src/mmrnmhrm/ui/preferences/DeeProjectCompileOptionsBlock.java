package mmrnmhrm.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.StringUtil;
import melnorme.util.ui.fields.FieldUtil;
import melnorme.util.ui.fields.ProjectContainerSelectionDialog;
import melnorme.util.ui.fields.SelectionComboDialogField;
import melnorme.util.ui.fields.StringDialogField;
import melnorme.util.ui.swt.LayoutUtil;
import mmrnmhrm.core.build.BudDeeModuleCompiler;
import mmrnmhrm.core.build.DeeCompilerOptions;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProjectOptions;
import mmrnmhrm.ui.actions.OperationsManager;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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

import static melnorme.miscutil.Assert.assertNotNull;

public class DeeProjectCompileOptionsBlock implements IDialogFieldListener  {
	
	DeeProjectOptions fDeeProjInfo;
	DeeCompilerOptions overlayOptions;

	
	protected SelectionComboDialogField<DeeCompilerOptions.EBuildTypes> fBuildType;
	protected StringDialogField fArtifactName;
	protected StringButtonDialogField fOutputDir;
	protected StringButtonDialogField fCompilerTool;
	//protected IPath fCompilerToolPath;
	protected StringDialogField fExtraOptions;

	protected StringDialogField fOptionsPreview;



	private Shell shell;
	
	public DeeProjectCompileOptionsBlock() {

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
				containerDialog	= new ProjectContainerSelectionDialog(getShell(), fDeeProjInfo.getProject());
				containerDialog.dialog.setTitle("Folder Selection"); 
				containerDialog.dialog.setMessage("Choose the output location folder.");

				IResource initSelection = null;
				if (fOutputDir != null) {
					initSelection = fDeeProjInfo.getProject().findMember(new Path(fOutputDir.getText()));
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
		
		fExtraOptions = new StringDialogField(SWT.BORDER | SWT.SINGLE);
		fExtraOptions.setLabelText("Extra Compiler Options (newline separated):");
		fExtraOptions.setDialogFieldListener(this);

		fOptionsPreview = new StringDialogField(SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		fOptionsPreview.setLabelText("Compiler Options Preview:");
	}
	
	protected void init(DeeProjectOptions projectInfo) {
		assertNotNull(projectInfo);
		fDeeProjInfo = projectInfo;
		overlayOptions = fDeeProjInfo.compilerOptions.clone();
		updateView();
	}
	
	public void init2(IScriptProject scriptProject) {
		init(DeeModel.getDeeProjectInfo(scriptProject));
	}

	
	public Composite createControl(Composite parent) {
		Composite content = parent;
		shell = parent.getShell();
		Composite comp;

		//Composite topcontent = new RowComposite(parent);
		//LayoutUtil.enableHorizontalGrabbing(topcontent);
/*		FieldUtil.doDefaultLayout2(topcontent, false, 
				fArtifactName, fOutputDir, fCompilerTool);
		LayoutUtil.enableHorizontalGrabbing(fOutputDir.getTextControl(null));
		LayoutUtil.setHorizontalSpan(fArtifactName.getTextControl(null), 1);
		*/

		comp = FieldUtil.createCompose(content, false, fBuildType);
		//LayoutUtil.setHorizontalSpan(fBuildType.getLabelControl(null), 1);
		LayoutUtil.setWidthHint(fBuildType.getLabelControl(null), 100);
		LayoutUtil.setWidthHint(fBuildType.getComboControl(null), 80);
		
		comp = FieldUtil.createCompose(content, false, fArtifactName);
		LayoutUtil.setWidthHint(fArtifactName.getLabelControl(null), 100);
		LayoutUtil.setWidthHint(fArtifactName.getTextControl(null), 120);
		
		comp = FieldUtil.createCompose(content, false, fOutputDir);
		LayoutUtil.setWidthHint(fOutputDir.getLabelControl(null), 100);
		LayoutUtil.enableHorizontalGrabbing(fOutputDir.getTextControl(null));

		comp = FieldUtil.createCompose(content, false, fCompilerTool);
		LayoutUtil.setWidthHint(fCompilerTool.getLabelControl(null), 100);
		LayoutUtil.enableHorizontalGrabbing(fCompilerTool.getTextControl(null));

		comp = FieldUtil.createCompose(content, true, fExtraOptions);
		LayoutUtil.enableDiagonalExpand(comp);
		LayoutUtil.enableDiagonalExpand(fExtraOptions.getTextControl(null));
		
		comp = FieldUtil.createCompose(content, true, fOptionsPreview);
		LayoutUtil.enableDiagonalExpand(comp);
		LayoutUtil.enableDiagonalExpand(fOptionsPreview.getTextControl(null));
		return content;
	}

	private Shell getShell() {
		return shell;
	}
	
	private void updateView() {
		DeeCompilerOptions options = overlayOptions;
		fBuildType.setTextWithoutUpdate(options.buildType.toString());
		fArtifactName.setTextWithoutUpdate(options.artifactName);
		fOutputDir.setTextWithoutUpdate(options.outputDir.toString());
		fCompilerTool.setTextWithoutUpdate(options.buildTool);
		fExtraOptions.setTextWithoutUpdate(options.extraOptions);

		updateBuildPreview(options);
	}
	
	//@Override
	public void dialogFieldChanged(DialogField field) {
		DeeCompilerOptions options = overlayOptions;
		options.buildType = fBuildType.getSelectedObject();
		options.artifactName = fArtifactName.getText();
		options.outputDir = new Path(fOutputDir.getText());
		options.buildTool = fCompilerTool.getText();
		options.extraOptions = fExtraOptions.getText();
		
		updateBuildPreview(options);
	}

	private void updateBuildPreview(DeeCompilerOptions options) {
		List<IFile> previewModules = new ArrayList<IFile>();
		IFolder outputFolder = fDeeProjInfo.getProject().getFolder(options.outputDir);
		IFile file = outputFolder.getFile("<files.d>");
		previewModules.add(file);
		List<String> text =	BudDeeModuleCompiler.createCommandLine(
				previewModules, fDeeProjInfo.dltkProj, options);
		fOptionsPreview.setText(StringUtil.collToString(text, "  "));
	}

	public boolean performOk() {
		return OperationsManager.executeOperation(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				fDeeProjInfo.compilerOptions = overlayOptions;
				fDeeProjInfo.saveProjectConfigFile();
			}
		}, "Saving Project Compile Option");
	}
	
}
