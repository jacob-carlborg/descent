package mmrnmhrm.ui.wizards.projconfig;

import java.util.List;

import mmrnmhrm.core.DeeCoreException;
import mmrnmhrm.core.build.DeeCompilerOptions;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.IDeeSourceRoot;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * An UI section for project configuration. Consists of a TabFolder with 
 * pages of other configuration sections.
 */
public class ProjectConfigBlock {

	protected DeeProject fDeeProject;

	protected SourceFoldersConfigPage fSourceFoldersPage;
	protected SourceLibrariesConfigPage fSourceLibrariesPage;
	protected CompilerConfigPage fCompilerPage;
	
	public ProjectConfigBlock() {
		fSourceFoldersPage = new SourceFoldersConfigPage();
		fSourceLibrariesPage = new SourceLibrariesConfigPage();
		fCompilerPage = new CompilerConfigPage();
	}
	
	public void init(DeeProject project) {
		fDeeProject = project;
		fSourceFoldersPage.init(fDeeProject);
		fSourceLibrariesPage.init(fDeeProject);
		fCompilerPage.init(fDeeProject);
	}

	public Control createControl(Composite parent) {
		
		TabFolder folder = new TabFolder(parent, SWT.NONE);

		TabItem item;
		// Folders config
        item= new TabItem(folder, SWT.NONE);
        item.setText("&Source"); 
        item.setImage(DeePluginImages.getImage(DeePluginImages.ELEM_SOURCEFOLDER));

		item.setData(fSourceFoldersPage);     
        item.setControl(fSourceFoldersPage.getControl(folder));
        
		// Libraries config
        item = new TabItem(folder, SWT.NONE);
        item.setText("&Libraries"); 
        item.setImage(DeePluginImages.getImage(DeePluginImages.ELEM_LIBRARY));

		item.setData(fSourceLibrariesPage);     
        item.setControl(fSourceLibrariesPage.getControl(folder));
        
		// Compiler config
        item = new TabItem(folder, SWT.NONE);
        item.setText("&Compiler"); 
        item.setImage(DeePluginImages.getImage(DeePluginImages.NODE_IMPORT));

		item.setData(fCompilerPage);     
        item.setControl(fCompilerPage.getControl(folder));
        
        return folder;
	}
	
	@SuppressWarnings("unchecked")
	public void applyConfig() throws CoreException {
		try {
			IProject project = fDeeProject.getProject();
			IResource outputDir = project.findMember(fSourceFoldersPage.fOutputLocationPath);
			fDeeProject.setOutputDir((IFolder) outputDir);
			
			List<IDeeSourceRoot> roots; 
			roots = fSourceFoldersPage.fSrcFoldersList.getElements();
			roots.addAll(fSourceLibrariesPage.fSrcLibrariesList.getElements());

			// Removing existing roots;
			for(IDeeSourceRoot root : fDeeProject.getSourceRoots()) {
				fDeeProject.removeSourceRoot(root);
			}
			
			// Add the new ones
			for(IDeeSourceRoot root : roots) {
				fDeeProject.addSourceRoot(root);
			}
			
			/*IDeeSourceRoot[] children = fDeeProject.newChildrenArray(roots.size());
			for(int i = 0; i < roots.size(); i++) {
				children[i] = roots.get(i);
			}
			fDeeProject.setChildren(children);*/
			
			DeeCompilerOptions options = fDeeProject.compilerOptions;

			options.buildType = fCompilerPage.fBuildType.getSelectedObject();

			options.compiler = fCompilerPage.fCompiler.getSelectedObject();

			options.extraOptions = fCompilerPage.fCompilerOptions.getText();
		} catch (RuntimeException e) {
			throw new DeeCoreException("Error on apply configuration.", e);
		}

	}

}