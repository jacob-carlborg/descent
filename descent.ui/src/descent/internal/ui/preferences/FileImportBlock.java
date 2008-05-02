package descent.internal.ui.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.util.PixelConverter;
import descent.internal.ui.util.SWTUtil;
import descent.internal.ui.viewsupport.ImageDescriptorRegistry;
import descent.internal.ui.viewsupport.JavaElementImageProvider;
import descent.internal.ui.wizards.IStatusChangeListener;
import descent.ui.JavaElementImageDescriptor;

public class FileImportBlock extends OptionsConfigurationBlock
{
    public static Key[] KEYS = new Key[]
	{
		// TODO ...and then the ogre took the keys...
	};
    
    private class FileImportContentProvider implements IStructuredContentProvider
    {
        public Object[] getElements(Object inputElement)
        {
            return fList.toArray(new File[fList.size()]);
        }

        public void dispose()
        {
            // Do nothing
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
            // Do nothing
        }
    }
    
    private static class FileImportLabelProvider extends LabelProvider
    {
        private final Image ICON_FOLDER;
        private final Image ICON_FOLDER_NOT_FOUND;
        
        FileImportLabelProvider()
        {
            ImageDescriptorRegistry registry = JavaPlugin.getImageDescriptorRegistry();
            ISharedImages shared = JavaPlugin.getDefault().getWorkbench().getSharedImages();
            
            ICON_FOLDER = shared.getImage(ISharedImages.IMG_OBJ_FOLDER);
            
            ImageDescriptor base = shared.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
            JavaElementImageDescriptor descriptor = new JavaElementImageDescriptor(
                    base, JavaElementImageDescriptor.ERROR, 
                    JavaElementImageProvider.SMALL_SIZE);
            ICON_FOLDER_NOT_FOUND = registry.get(descriptor);
        }
        
        public Image getImage(Object element)
        {
            File file = (File) element;
            if(file.exists() && file.isDirectory())
                return ICON_FOLDER;
            else
                return ICON_FOLDER_NOT_FOUND;
        }

        public String getText(Object element)
        { 
            File file = (File) element;
            return file.getAbsolutePath();
        }
    }
    
    private static class FileImportSorter extends ViewerSorter
    {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2)
        {
            File f1 = (File) e1;
            File f2 = (File) e2;
            
            return collator.compare(f1.getAbsolutePath(), f2.getAbsolutePath());
        }
    }
	
    private TableViewer fViewer;
    private Button fNewButton;
    private Button fEditButton;
    private Button fRemoveButton;
    private List<File> fList = new ArrayList<File>();
    
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
	    class ListViewerListener implements ISelectionChangedListener,
	        IDoubleClickListener
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                updateEnablement();
            }

            public void doubleClick(DoubleClickEvent event)
            {
                performEdit();
            }
        }
	    
	    Table table = new Table(comp, SWT.SINGLE | SWT.BORDER | SWT.SINGLE);
	    fViewer = new TableViewer(table);
	    
	    GridData gd = new GridData(GridData.FILL_BOTH);
        PixelConverter conv = new PixelConverter(comp);
        gd.widthHint = conv.convertWidthInCharsToPixels(50);
        fViewer.getControl().setLayoutData(gd);
        
        fViewer.setContentProvider(new FileImportContentProvider());
        fViewer.setLabelProvider(new FileImportLabelProvider());
        fViewer.setSorter(new FileImportSorter());
        fViewer.setInput(this);
        
        ListViewerListener listener = new ListViewerListener();
        fViewer.addSelectionChangedListener(listener);
        fViewer.addDoubleClickListener(listener);
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
	    IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
	    if(null != selection)
	        return (File) selection.getFirstElement();
	    else
	        return null;
	}
	
	private void performNew()
    {
	    File file = selectFile(null);
	    if(null == file)
	        return;
	    if(fList.contains(file))
	        return;
	    
	    fList.add(file);
	    fViewer.refresh();
	    fViewer.setSelection(new StructuredSelection(file));
    }
	
	private void performEdit()
	{
	    File old = getSelectedElement();
	    if(null == old)
	        return;
	    
	    File file = selectFile(old);
	    if(null == file)
	        return;
	    if(fList.contains(file))
	        return;
	    
	    fList.remove(old);
	    fList.add(file);
        fViewer.refresh();
        fViewer.setSelection(new StructuredSelection(file));
	}
	
	private void performRemove()
	{
	    File file = getSelectedElement();
        if(null == file)
            return;
        
        fList.remove(file);
        fViewer.setSelection(new StructuredSelection());
        fViewer.refresh();
	}
	
	private File selectFile(File seed)
	{
	    if(null != seed && (!seed.exists() || !seed.isDirectory()))
	        seed = null;
	    
	    DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.NONE);
	    dialog.setText("Select path to search for file imports");
	    dialog.setFilterPath(null == seed ? null : seed.getAbsolutePath());
	    
	    String path = dialog.open();
	    return null == path ? null : new File(path);
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
