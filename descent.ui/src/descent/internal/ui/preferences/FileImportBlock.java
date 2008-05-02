package descent.internal.ui.preferences;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import descent.internal.ui.util.PixelConverter;
import descent.internal.ui.util.SWTUtil;
import descent.internal.ui.wizards.IStatusChangeListener;

public class FileImportBlock extends OptionsConfigurationBlock
{
    public static Key[] KEYS = new Key[]
	{
		// TODO ...and then the ogre took the keys...
	};
    
    private static class FileImportLabelProvider extends LabelProvider
    {
        // TODO these images -- a folder and a folder with an X
        private static Image ICON_FOLDER = null;
        private static Image ICON_FOLDER_NOT_FOUND = null;
        
        public Image getImage(Object element)
        {
            if(null == element || !(element instanceof File))
                throw new IllegalArgumentException("Expected a non-null java.io.File");
            
            File file = (File) element;
            if(file.exists() && file.isDirectory())
                return ICON_FOLDER;
            else
                return ICON_FOLDER_NOT_FOUND;
        }

        public String getText(Object element)
        {
            if(null == element || !(element instanceof File))
                throw new IllegalArgumentException("Expected a non-null java.io.File");
            
            File file = (File) element;
            return file.getAbsolutePath();
        }
    }
	
    private Button fNewButton;
    private Button fEditButton;
    private Button fRemoveButton;
    
	public FileImportBlock(IStatusChangeListener context,
			IProject project,
			IWorkbenchPreferenceContainer container)
	{
		super(context, project, KEYS, container);
	}

	@Override
	protected Control createContents(Composite parent)
	{   
	    setShell(parent.getShell());
	    
	    // Create the composite
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.marginHeight= 0;
        layout.marginWidth= 0;
        layout.numColumns = 2;
        comp.setLayout(layout);
        
        createListControl(comp);
        createButtons(comp);
		
		return comp;
	}
	
	private void createListControl(Composite comp)
	{
	    // TODO temp testing layout
	    Group tmp = new Group(comp, SWT.NONE);
	    GridData gd = new GridData(GridData.FILL_BOTH);
        PixelConverter conv = new PixelConverter(comp);
        gd.widthHint = conv.convertWidthInCharsToPixels(50);
        tmp.setLayoutData(gd);
	}
	
	private void createButtons(Composite parent)
	{
	    SelectionListener listener = new SelectionListener()
	    {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e)
            {
                if(fNewButton == e.widget)
                    performNew();
                else if(fEditButton == e.widget)
                    performEdit();
                else if(fRemoveButton == e.widget)
                    performRemove();
            }
	    };
	    
	    Composite comp = new Composite(parent, SWT.NONE);
	    
	    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	    comp.setLayoutData(gd);
	    
	    GridLayout layout = new GridLayout();
	    layout.marginWidth = 0;
        layout.marginHeight = 0;
        comp.setLayout(layout);
	    
	    fNewButton = createButton(comp, listener, "New...");
	    fEditButton = createButton(comp, listener, "Edit...");
	    fRemoveButton = createButton(comp, listener, "Remove");
	    
	    updateEnablement();
	}
	
	private Button createButton(Composite comp, SelectionListener listener, String label)
	{
	    Button button = new Button(comp, SWT.PUSH);
	    button.setFont(comp.getFont());
        button.setText(label);
        button.addSelectionListener(listener);
        
        GridData gd= new GridData();
        gd.horizontalAlignment= GridData.FILL;
        gd.grabExcessHorizontalSpace= true;
        gd.verticalAlignment= GridData.BEGINNING;
        gd.widthHint = SWTUtil.getButtonWidthHint(button);
        button.setLayoutData(gd);
	    
	    return button;
	}
	
	private void updateEnablement()
	{
	    File file = getSelectedElement();
	    if(null == file)
	    {
    	    fEditButton.setEnabled(false);
    	    fRemoveButton.setEnabled(false);
	    }
	    else
	    {
	        fEditButton.setEnabled(true);
	        fRemoveButton.setEnabled(true);
	    }
	}
	
	private File getSelectedElement()
	{
	    return null;
	}
	
	private void performNew()
    {
	    // TODO
    }
	
	private void performEdit()
	{
	    File file = getSelectedElement();
	    if(null == file)
	        return;
	   
	    // TODO
	}
	
	private void performRemove()
	{
	    File file = getSelectedElement();
        if(null == file)
            return;
        
        // TODO
	}

    @Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings)
	{
		String title= PreferencesMessages.FileImportConfigurationBlock_needsbuild_title; 
		String message;
		if (fProject == null) {
			message= PreferencesMessages.FileImportConfigurationBlock_needsfullbuild_message; 
		} else {
			message= PreferencesMessages.FileImportConfigurationBlock_needsprojectbuild_message; 
		}	
		return new String[] { title, message };
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue,
			String newValue)
	{
		// TODO Auto-generated method stub
	}
	
}
