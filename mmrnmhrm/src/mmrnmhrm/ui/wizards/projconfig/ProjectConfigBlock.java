package mmrnmhrm.ui.wizards.projconfig;

import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

//Converting from BuildPathsBlock
/**
 * 
 */
public class ProjectConfigBlock {

	private DeeProject project;

	private SourceContainerConfigPage fSourceContainerPage;
	private SourceLibrariesConfigPage fSourceLibrariesPage;
	private CompilerConfigPage fCompilerPage;
	
	public void init(DeeProject jproject) {
		project = jproject;
	}

	public Control createControl(Composite parent) {
		final Composite content = parent;
		//content.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabFolder folder= new TabFolder(content, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem item;
		// Folders config
        item= new TabItem(folder, SWT.NONE);
        item.setText("&Source"); 
        item.setImage(DeePluginImages.getImage(DeePluginImages.ELEM_SOURCEFOLDER));

		fSourceContainerPage = new SourceContainerConfigPage();
		fSourceContainerPage.init(project);
		item.setData(fSourceContainerPage);     
        item.setControl(fSourceContainerPage.getControl(folder));
        
		// Libraries config
        item = new TabItem(folder, SWT.NONE);
        item.setText("&Libraries"); 
        item.setImage(DeePluginImages.getImage(DeePluginImages.ELEM_LIBRARY));

		fSourceLibrariesPage = new SourceLibrariesConfigPage();
		fSourceLibrariesPage.init(project);
		item.setData(fSourceLibrariesPage);     
        item.setControl(fSourceLibrariesPage.getControl(folder));
        
		// Compiler config
        item = new TabItem(folder, SWT.NONE);
        item.setText("&Compiler"); 
        item.setImage(DeePluginImages.getImage(DeePluginImages.NODE_IMPORT));

		fCompilerPage = new CompilerConfigPage();
		fCompilerPage.init(project);
		item.setData(fCompilerPage);     
        item.setControl(fCompilerPage.getControl(folder));
        
        return content;
	}
	
	void saveConfig() {
		//fSourceLibrariesPage.
	}

}
