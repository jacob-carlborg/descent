package descent.internal.unittest.launcher;


import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;

import descent.debug.core.IDescentLaunchConfigurationConstants;

import descent.ui.JavaElementLabelProvider;
import descent.ui.StandardJavaElementContentProvider;

import descent.internal.ui.wizards.TypedElementSelectionValidator;
import descent.internal.ui.wizards.TypedViewerFilter;
import descent.internal.unittest.DescentUnittestPlugin;
import descent.internal.unittest.ui.JUnitMessages;
import descent.internal.unittest.ui.LayoutUtil;

public class UnittestLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab
{

	//--------------------------------------------------------------------------
	// General

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);

		createTestSelection(comp);
		createPortSelection(comp);

		Dialog.applyDialogFont(comp);
		validatePage();
	}

	public String getName()
	{
		return JUnitMessages.UnittestLaunchConfigurationTab_tab_label;
	}

	@Override
	public void dispose()
	{
		super.dispose();
		fTestIcon.dispose();
	}
	
	//--------------------------------------------------------------------------
    // Test selection
    
    private Group fTestSelectionGroup;
    private Label fContainerLabel;
    private Text fContainerText;
    private Button fContainerButton;
    private Button fIncludeSubpackagesCheckbox;
    private IJavaElement fContainerElement;

    private void createTestSelection(Composite comp)
    {
        fTestSelectionGroup = createGroup(comp, JUnitMessages.UnittestLaunchConfigurationTab_group_test_selection);
        
        fContainerLabel = new Label(fTestSelectionGroup, SWT.NONE);
        fContainerLabel.setText(JUnitMessages.UnittestLaunchConfigurationTab_label_test_container);
        GridData gd = new GridData();
        fContainerLabel.setLayoutData(gd);

        fContainerText = new Text(fTestSelectionGroup, SWT.SINGLE | SWT.BORDER);
        fContainerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fContainerText.setEditable(false);

        fContainerButton = new Button(fTestSelectionGroup, SWT.PUSH);
        fContainerButton.setText(JUnitMessages.UnittestLaunchConfigurationTab_label_browse);
        gd = new GridData();
        fContainerButton.setLayoutData(gd);
        LayoutUtil.setButtonDimensionHint(fContainerButton);
        fContainerButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent evt)
            {
                handleContainerButtonSelected();
                validatePage();
                updateLaunchConfigurationDialog();
            }
        });
        
        fIncludeSubpackagesCheckbox = new Button(fTestSelectionGroup, SWT.CHECK);
        fIncludeSubpackagesCheckbox.setText(JUnitMessages.UnittestLaunchConfigurationTab_label_include_subpackages);
        gd = new GridData();
        gd.horizontalSpan = 3;
        fIncludeSubpackagesCheckbox.setLayoutData(gd);
        fIncludeSubpackagesCheckbox.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent evt)
            {
                updateLaunchConfigurationDialog();
            }
        });
    }
    
    private void updateTestContainerFromConfig(ILaunchConfiguration config)
    {
        String containerHandle = ""; //$NON-NLS-1$
        String includeSubpackages = ""; //$NON-NLS-1$
        try
        {
            containerHandle = config.getAttribute(IUnittestLaunchConfigurationAttributes.LAUNCH_CONTAINER_ATTR, ""); //$NON-NLS-1$
            includeSubpackages = config.getAttribute(IUnittestLaunchConfigurationAttributes.INCLUDE_SUBPACKAGES_ATTR, "false"); //$NON-NLS-1$
        }
        catch (CoreException ce) { }
        
        fIncludeSubpackagesCheckbox.setSelection("true".equals(includeSubpackages)); //$NON-NLS-1$
        if(!("".equals(containerHandle))) //$NON-NLS-1$
        {
            IJavaElement element = JavaCore.create(containerHandle);
            if(null != element)
                setContainerElement(element);
        }
    }
    
    private void handleContainerButtonSelected()
    {
        IJavaElement container = chooseContainer();
        if(null != container)
            setContainerElement(container);
        validatePage();
        updateLaunchConfigurationDialog();
    }
    
    private void setContainerElement(IJavaElement element)
    {
        fContainerElement = element;
        fContainerText.setText(getReadableName(element));
    }
    
    private String getReadableName(IJavaElement element)
    {
        if(element instanceof IJavaProject)
        {
            return element.getElementName();
        }
        else
        {
            StringBuilder elementName = new StringBuilder();
            elementName.append(element.getJavaProject().getElementName());
            elementName.append(" - "); //$NON-NLS-1$
            
            if(element instanceof ICompilationUnit)
                elementName.append(((ICompilationUnit) element).getFullyQualifiedName());
            else if(element instanceof IPackageFragment)
                elementName.append(((IPackageFragment) element).isDefaultPackage() ?
                        JUnitMessages.UnittestLaunchConfigurationTab_default_package :
                        element.getElementName());
            else
                elementName.append(element.getElementName());
            
            return elementName.toString();
        }
    }
    
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    private IJavaElement chooseContainer()
    {
        Class[] acceptedClasses = new Class[]
        {
            IJavaProject.class,
            IPackageFragmentRoot.class,
            IPackageFragment.class,
            ICompilationUnit.class,
        };
        TypedElementSelectionValidator validator = 
            new TypedElementSelectionValidator(acceptedClasses, false)
            {
                public boolean isSelectedValid(Object element)
                {
                    return true;
                }
            };
        
        acceptedClasses = new Class[] 
        {
            IJavaProject.class,
            IJavaModel.class,
            IPackageFragmentRoot.class,
            IPackageFragment.class,
            ICompilationUnit.class,
        };
        ViewerFilter filter= new TypedViewerFilter(acceptedClasses)
        {
            public boolean select(Viewer viewer, Object parent, Object element)
            {
                if((element instanceof IPackageFragmentRoot))
                    if(((IPackageFragmentRoot) element).isArchive())
                        return false;
                
                return super.select(viewer, parent, element);
            }
        };      

        StandardJavaElementContentProvider provider = new StandardJavaElementContentProvider();
        ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT); 
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), labelProvider, provider);
        dialog.setValidator(validator);
        dialog.setTitle(JUnitMessages.UnittestLaunchConfigurationTab_dialog_test_container_selection);
        dialog.setMessage(JUnitMessages.UnittestLaunchConfigurationTab_label_test_container_selection);  
        dialog.addFilter(filter);
        dialog.setInput(JavaCore.create(getWorkspaceRoot()));
        dialog.setInitialSelection(fContainerElement);
        dialog.setAllowMultiple(false);
        
        if (dialog.open() == Window.OK)
            return (IJavaElement) dialog.getFirstResult();
        
        return null;
    }
    
    private String validateTestSelection()
    {
        if(null == fContainerElement)
            return JUnitMessages.UnittestLaunchConfigurationTab_error_no_test_container;
        
        if(!fContainerElement.exists())
            return JUnitMessages.UnittestLaunchConfigurationTab_error_test_container_does_not_exist;
        
        return null;
    }
	
	//--------------------------------------------------------------------------
	// Port selection
	
	private Group fPortGroup;
	private Button fAutoPortRadioButton;
	private Button fSpecPortRadioButton;
	private Label fSpecPortLabel;
	private Text fSpecPortText;
	
	private void createPortSelection(Composite comp)
	{
	    fPortGroup = createGroup(comp, JUnitMessages.UnittestLaunchConfigurationTab_group_port);
	    
	    createAutoPortSelection(fPortGroup);
	    createSpecPortSelection(fPortGroup);
	}
	
	private void createAutoPortSelection(Composite comp)
	{
	    fAutoPortRadioButton = new Button(comp, SWT.RADIO);
	    fAutoPortRadioButton.setText(JUnitMessages.UnittestLaunchConfigurationTab_label_automatically_choose_port);
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        fAutoPortRadioButton.setLayoutData(gd);
        fAutoPortRadioButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (fAutoPortRadioButton.getSelection())
                    portModeChanged();
            }
        });
	}
	
	private void createSpecPortSelection(Composite comp)
	{
	    fSpecPortRadioButton = new Button(comp, SWT.RADIO);
	    fSpecPortRadioButton.setText(JUnitMessages.UnittestLaunchConfigurationTab_label_use_specified_port);
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        fSpecPortRadioButton.setLayoutData(gd);
        fSpecPortRadioButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (fSpecPortRadioButton.getSelection())
                    portModeChanged();
            }
        });
        
        fSpecPortLabel = new Label(comp, SWT.NONE);
        fSpecPortLabel.setText(JUnitMessages.UnittestLaunchConfigurationTab_label_port);
        gd = new GridData();
        gd.horizontalIndent = 25;
        fSpecPortLabel.setLayoutData(gd);

        fSpecPortText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        fSpecPortText.setLayoutData(gd);
        fSpecPortText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent evt)
            {
                validatePage();
                updateLaunchConfigurationDialog();
            }
        });
	}
	
	private void portModeChanged()
    {
        boolean isAutoPortMode = fAutoPortRadioButton.getSelection();
        setEnablePortSelection(!isAutoPortMode);
        validatePage();
        updateLaunchConfigurationDialog();
    }
	
	private void setEnablePortSelection(boolean enabled)
	{
	    fSpecPortLabel.setEnabled(enabled);
	    fSpecPortText.setEnabled(enabled);
	}
	
	private void updatePortFromConfig(ILaunchConfiguration config)
	{
	    String portStr = ""; //$NON-NLS-1$
	    try
	    {
	        portStr = config.getAttribute(IUnittestLaunchConfigurationAttributes.PORT_ATTR, ""); //$NON-NLS-1$
	    }
	    catch(CoreException ce) { }
	    
	    int portNum = 0;
	    portStr = portStr.trim();
	    if(!portStr.equals("")) //$NON-NLS-1$
	    {
	        try
	        {
	            portNum = Integer.parseInt(portStr);
	        }
	        catch(NumberFormatException e) { }
	    }
	    
	    if(portNum == 0)
	    {
	        fAutoPortRadioButton.setSelection(true);
	        fSpecPortRadioButton.setSelection(false);
	    }
	    else
	    {
	        fAutoPortRadioButton.setSelection(false);
	        fSpecPortRadioButton.setSelection(true);
	        fSpecPortText.setText(String.valueOf(portNum));
	    }
	    
	    portModeChanged();
	}
	
	private String validatePort()
    {
        if(fAutoPortRadioButton.getSelection())
            return null;
        
        String portStr = fSpecPortText.getText().trim();
        if(portStr.length() == 0)
            return JUnitMessages.UnittestLaunchConfigurationTab_error_no_port;
        
        int portNum;
        try
        {
            portNum = Integer.parseInt(portStr);
        }
        catch(NumberFormatException e)
        {
            return JUnitMessages.UnittestLaunchConfigurationTab_error_invalid_port;
        }
        
        if(portNum < 1024 || portNum > 65535)
        {
            return JUnitMessages.UnittestLaunchConfigurationTab_error_port_number;
        }
        
        return null;
    }

	//--------------------------------------------------------------------------
	// Initialization

	public void initializeFrom(ILaunchConfiguration config)
	{
	    updateTestContainerFromConfig(config);
		updatePortFromConfig(config);
	}

	//--------------------------------------------------------------------------
	// Validation

	@Override
	public boolean isValid(ILaunchConfiguration config)
	{
		return getErrorMessage() == null;
	}

	private void validatePage()
	{
		setErrorMessage(null);
		setMessage(null);
		
		String errorMsg = validateTestSelection();
        if(null != errorMsg)
        {
            setErrorMessage(errorMsg);
            return;
        }
		
		errorMsg = validatePort();
		if(null != errorMsg)
        {
            setErrorMessage(errorMsg);
            return;
        }
	}

	//--------------------------------------------------------------------------
	// Application

	public void performApply(ILaunchConfigurationWorkingCopy config)
	{
	    String launchContainer = ""; //$NON-NLS-1$
        String project = ""; //$NON-NLS-1$
	    
	    if(null != fContainerElement && fContainerElement.exists())
	    {
	        launchContainer = fContainerElement.getHandleIdentifier();
	        project = fContainerElement.getJavaProject().getElementName();
	    }
	    
	    config.setAttribute(IUnittestLaunchConfigurationAttributes.LAUNCH_CONTAINER_ATTR, launchContainer);
        config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, project);
	    config.setAttribute(IUnittestLaunchConfigurationAttributes.PORT_ATTR, 
	            fAutoPortRadioButton.getSelection() ? "" : fSpecPortText.getText()); //$NON-NLS-1$
	    config.setAttribute(IUnittestLaunchConfigurationAttributes.INCLUDE_SUBPACKAGES_ATTR,
	            fIncludeSubpackagesCheckbox.getSelection() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// TODO get the fluted program executable
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME,
				"C:/Users/xycos/workspace/descent.unittest/testdata/bin/test.exe"); //$NON-NLS-1$
	}

	//--------------------------------------------------------------------------
	// Defaults
	public void setDefaults(ILaunchConfigurationWorkingCopy config)
	{
	    String launchContainer = ""; //$NON-NLS-1$
	    String project = ""; //$NON-NLS-1$
	    
		ICompilationUnit element = getContext();
		if (element != null)
		{
		    launchContainer = element.getHandleIdentifier();
	        project = element.getJavaProject().getElementName();
		}
		
		config.setAttribute(IUnittestLaunchConfigurationAttributes.LAUNCH_CONTAINER_ATTR, launchContainer);
        config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, project);
		config.setAttribute(IUnittestLaunchConfigurationAttributes.PORT_ATTR, ""); //$NON-NLS-1$
	}

	/**
	 * Returns the current compilation unit context from which to initialize
	 * default settings, or <code>null</code> if none. This is generally the
	 * module currently open in the editor.
	 * 
	 * @return Compilation unit context.
	 */
	private ICompilationUnit getContext()
	{
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (null == activeWorkbenchWindow)
			return null;
		
		IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
		if(null == page)
		    return null;
		
		IEditorPart part = page.getActiveEditor();
        if(null == part)
            return null;
        
        IEditorInput input = part.getEditorInput();
        IJavaElement element = (IJavaElement) input.getAdapter(IJavaElement.class);
        if(element instanceof ICompilationUnit)
            return (ICompilationUnit) element;
        else
            return null;
	}

	//--------------------------------------------------------------------------
	// Convenience methods

	/**
	 * Convenience method to get the workspace root.
	 */
	private IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/**
	 * Method used to create the groups for port & test selection. The created
	 * group will take up 3 columns and have 3 columns.
	 * 
	 * @param comp the composite to create the group under
	 * @param text the group label
	 * @return     the new group
	 */
	private Group createGroup(Composite comp, String text)
	{
	    Group group = new Group(comp, SWT.NONE);
	    group.setText(text);
	    
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        group.setLayoutData(gd);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        group.setLayout(layout);
        
        return group;
	}

	//--------------------------------------------------------------------------
	// Icon
	private final Image fTestIcon = createImage("obj16/test.gif"); //$NON-NLS-1$

	private static Image createImage(String path)
	{
		return DescentUnittestPlugin.getImageDescriptor(path).createImage();
	}

	@Override
	public Image getImage()
	{
		return fTestIcon;
	}
}
