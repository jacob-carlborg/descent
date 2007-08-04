package descent.internal.debug.ui.jres;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
//import org.eclipse.debug.internal.ui.SWTUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.ibm.icu.text.MessageFormat;

import descent.internal.ui.JavaPlugin;
import descent.launching.AbstractVMInstallType;
import descent.launching.IVMInstall;
import descent.launching.IVMInstallType;
import descent.launching.JavaRuntime;
import descent.launching.VMStandin;
import descent.ui.ISharedImages;
import descent.ui.JavaUI;

/**
 * A composite that displays installed JRE's in a table. JREs can be 
 * added, removed, edited, and searched for.
 * <p>
 * This block implements ISelectionProvider - it sends selection change events
 * when the checked JRE in the table changes, or when the "use default" button
 * check state changes.
 * </p>
 */
public class InstalledJREsBlock implements IAddVMDialogRequestor, ISelectionProvider {
	
	/**
	 * This block's control
	 */
	private Composite fControl;
	
	/**
	 * VMs being displayed
	 */
	private List fVMs = new ArrayList(); 
	
	/**
	 * The main list control
	 */ 
	private CheckboxTableViewer fVMList;
	
	// Action buttons
	private Button fAddButton;
	private Button fRemoveButton;
	private Button fEditButton;
	private Button fCopyButton;
	private Button fSearchButton;	
    
	// index of column used for sorting
	private int fSortColumn = 0;
	
	/**
	 * Selection listeners (checked JRE changes)
	 */
	private ListenerList fSelectionListeners = new ListenerList();
	
	/**
	 * Previous selection
	 */
	private ISelection fPrevSelection = new StructuredSelection();

    private Table fTable;
			
	// Make sure that VMStandin ids are unique if multiple calls to System.currentTimeMillis()
	// happen very quickly
	private static String fgLastUsedID;	
	
	/** 
	 * Content provider to show a list of JREs
	 */ 
	class JREsContentProvider implements IStructuredContentProvider {		
		public Object[] getElements(Object input) {
			return fVMs.toArray();
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
	}
	
	/**
	 * Label provider for installed JREs table.
	 */
	class VMLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * @see ITableLabelProvider#getColumnText(Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IVMInstall) {
				IVMInstall vm= (IVMInstall)element;
				switch(columnIndex) {
					case 0:
						if (isContributed(vm)) {
							return MessageFormat.format(JREMessages.InstalledJREsBlock_19, new String[]{vm.getName()});
						}
						return vm.getName();
					case 1:
						return vm.getInstallLocation().getAbsolutePath();
					case 2: 
						return vm.getVMInstallType().getName();						
				}
			}
			return element.toString();
		}

		/**
		 * @see ITableLabelProvider#getColumnImage(Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_LIBRARY);
			}
			return null;
		}

	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		fSelectionListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return new StructuredSelection(fVMList.getCheckedElements());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		fSelectionListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			if (!selection.equals(fPrevSelection)) {
				fPrevSelection = selection;
				Object jre = ((IStructuredSelection)selection).getFirstElement();
				if (jre == null) {
					fVMList.setCheckedElements(new Object[0]);
				} else {
					fVMList.setCheckedElements(new Object[]{jre});
					fVMList.reveal(jre);
				}
				fireSelectionChanged();
			}
		}
	}

	/**
	 * Creates this block's control in the given control.
	 * 
	 * @param ancestor containing control
	 * @param useManageButton whether to present a single 'manage...' button to
	 *  the user that opens the installed JREs pref page for JRE management,
	 *  or to provide 'add, remove, edit, and search' buttons.
	 */
	public void createControl(Composite ancestor) {
		
		Composite parent= new Composite(ancestor, SWT.NULL);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		Font font = ancestor.getFont();
		parent.setFont(font);	
		fControl = parent;	
		
		GridData data;
				
		Label tableLabel = new Label(parent, SWT.NONE);
		tableLabel.setText(JREMessages.InstalledJREsBlock_15); 
		data = new GridData();
		data.horizontalSpan = 2;
		tableLabel.setLayoutData(data);
		tableLabel.setFont(font);
				
		fTable= new Table(parent, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		
		data= new GridData(GridData.FILL_BOTH);
        data.widthHint = 450;
		fTable.setLayoutData(data);
		fTable.setFont(font);
				
		fTable.setHeaderVisible(true);
		fTable.setLinesVisible(true);		

		TableColumn column1= new TableColumn(fTable, SWT.NULL);
		column1.setText(JREMessages.InstalledJREsBlock_0); 
		column1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortByName();
			}
		});
	
		TableColumn column2= new TableColumn(fTable, SWT.NULL);
		column2.setText(JREMessages.InstalledJREsBlock_1); 
		column2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortByLocation();
			}
		});
        
		TableColumn column3= new TableColumn(fTable, SWT.NULL);
		column3.setText(JREMessages.InstalledJREsBlock_2); 
		column3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortByType();
			}
		});

		fVMList= new CheckboxTableViewer(fTable);			
		fVMList.setLabelProvider(new VMLabelProvider());
		fVMList.setContentProvider(new JREsContentProvider());
		// by default, sort by name
		sortByName();
		
		fVMList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent evt) {
				enableButtons();
			}
		});
		
		fVMList.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					setCheckedJRE((IVMInstall)event.getElement());
				} else {
					setCheckedJRE(null);
				}
			}
		});
		
		fVMList.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				if (!fVMList.getSelection().isEmpty()) {
					editVM();
				}
			}
		});
		fTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0) {
					removeVMs();
				}
			}
		});	
		
		Composite buttons= new Composite(parent, SWT.NULL);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		buttons.setLayout(layout);
		buttons.setFont(font);
		
		fAddButton = createPushButton(buttons, JREMessages.InstalledJREsBlock_3); 
		fAddButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				addVM();
			}
		});
		
		fEditButton= createPushButton(buttons, JREMessages.InstalledJREsBlock_4); 
		fEditButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				editVM();
			}
		});
		
		fCopyButton = createPushButton(buttons, JREMessages.InstalledJREsBlock_16); 
		fCopyButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				copyVM();
			}
		});
		
		fRemoveButton= createPushButton(buttons, JREMessages.InstalledJREsBlock_5); 
		fRemoveButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				removeVMs();
			}
		});
		
		// copied from ListDialogField.CreateSeparator()
		Label separator= new Label(buttons, SWT.NONE);
		separator.setVisible(false);
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.verticalAlignment= GridData.BEGINNING;
		gd.heightHint= 4;
		separator.setLayoutData(gd);
		
		fSearchButton = createPushButton(buttons, JREMessages.InstalledJREsBlock_6); 
		fSearchButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				search();
			}
		});		
		
		fillWithWorkspaceJREs();
		enableButtons();
		fAddButton.setEnabled(JavaRuntime.getVMInstallTypes().length > 0);
	}
	
	/**
	 * Adds a duplicate of the selected VM to the block 
	 * @since 3.2
	 */
	protected void copyVM() {
        IStructuredSelection selection = (IStructuredSelection) fVMList.getSelection();
        Iterator it = selection.iterator();

        ArrayList newEntries = new ArrayList();
        while (it.hasNext()) {
            IVMInstall selectedVM = (IVMInstall) it.next();

            // duplicate & add vm
            VMStandin standin = new VMStandin(selectedVM, createUniqueId(selectedVM.getVMInstallType()));
            standin.setName(generateName(selectedVM.getName()));
            AddVMDialog dialog = new AddVMDialog(this, getShell(), JavaRuntime.getVMInstallTypes(), standin);
            dialog.setTitle(JREMessages.InstalledJREsBlock_18);
            if (dialog.open() != Window.OK) {
                return;
            }
            newEntries.add(standin);
            fVMs.add(standin);
        }
        fVMList.refresh();
        fVMList.setSelection(new StructuredSelection(newEntries.toArray()));
    }

	/**
	 * Compares the given name against current names and adds the appropriate numerical 
	 * suffix to ensure that it is unique.
	 * @param name the name with which to ensure uniqueness 
	 * @return the unique version of the given name
	 * @since 3.2
	 */
	public String generateName(String name){
            if (!isDuplicateName(name)) {
                return name;
            }
            
            if (name.matches(".*\\(\\d*\\)")) { //$NON-NLS-1$
                int start = name.lastIndexOf('(');
                int end = name.lastIndexOf(')');
                String stringInt = name.substring(start+1, end);
                int numericValue = Integer.parseInt(stringInt);
                String newName = name.substring(0, start+1) + (numericValue+1) + ")"; //$NON-NLS-1$
                return generateName(newName);
            } else {
                return generateName(name + " (1)"); //$NON-NLS-1$
            }
        }
	
	/**
	 * Fire current selection
	 */
	private void fireSelectionChanged() {
		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		Object[] listeners = fSelectionListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			ISelectionChangedListener listener = (ISelectionChangedListener)listeners[i];
			listener.selectionChanged(event);
		}	
	}

	/**
	 * Sorts by VM type, and name within type.
	 */
	private void sortByType() {
		fVMList.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IVMInstall) && (e2 instanceof IVMInstall)) {
					IVMInstall left= (IVMInstall)e1;
					IVMInstall right= (IVMInstall)e2;
					String leftType= left.getVMInstallType().getName();
					String rightType= right.getVMInstallType().getName();
					int res= leftType.compareToIgnoreCase(rightType);
					if (res != 0) {
						return res;
					}
					return left.getName().compareToIgnoreCase(right.getName());
				}
				return super.compare(viewer, e1, e2);
			}
			
			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});	
		fSortColumn = 3;			
	}
	
	/**
	 * Sorts by VM name.
	 */
	private void sortByName() {
		fVMList.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IVMInstall) && (e2 instanceof IVMInstall)) {
					IVMInstall left= (IVMInstall)e1;
					IVMInstall right= (IVMInstall)e2;
					return left.getName().compareToIgnoreCase(right.getName());
				}
				return super.compare(viewer, e1, e2);
			}
			
			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});		
		fSortColumn = 1;		
	}
	
	/**
	 * Sorts by VM location.
	 */
	private void sortByLocation() {
		fVMList.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IVMInstall) && (e2 instanceof IVMInstall)) {
					IVMInstall left= (IVMInstall)e1;
					IVMInstall right= (IVMInstall)e2;
					return left.getInstallLocation().getAbsolutePath().compareToIgnoreCase(right.getInstallLocation().getAbsolutePath());
				}
				return super.compare(viewer, e1, e2);
			}
			
			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});		
		fSortColumn = 2;		
	}
		
	private void enableButtons() {
		IStructuredSelection selection = (IStructuredSelection) fVMList.getSelection();
		int selectionCount= selection.size();
		fEditButton.setEnabled(selectionCount == 1);
		fCopyButton.setEnabled(selectionCount > 0);
		if (selectionCount > 0 && selectionCount < fVMList.getTable().getItemCount()) {
			Iterator iterator = selection.iterator();
			while (iterator.hasNext()) {
				IVMInstall install = (IVMInstall)iterator.next();
				if (isContributed(install)) {
					fRemoveButton.setEnabled(false);
					return;
				}
			}
			fRemoveButton.setEnabled(true);
		} else {
			fRemoveButton.setEnabled(false);
		}
	}	
	
	private boolean isContributed(IVMInstall install) {
		return JavaRuntime.isContributedVMInstall(install.getId());
	}
	
	protected Button createPushButton(Composite parent, String label) {
		//return SWTUtil.createPushButton(parent, label, null);
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(label);
		return button;
	}	
	
	/**
	 * Returns this block's control
	 * 
	 * @return control
	 */
	public Control getControl() {
		return fControl;
	}
	
	/**
	 * Sets the JREs to be displayed in this block
	 * 
	 * @param vms JREs to be displayed
	 */
	protected void setJREs(IVMInstall[] vms) {
		fVMs.clear();
		for (int i = 0; i < vms.length; i++) {
			fVMs.add(vms[i]);
		}
		fVMList.setInput(fVMs);
		fVMList.refresh();
	}
	
	/**
	 * Returns the JREs currently being displayed in this block
	 * 
	 * @return JREs currently being displayed in this block
	 */
	public IVMInstall[] getJREs() {
		return (IVMInstall[])fVMs.toArray(new IVMInstall[fVMs.size()]);
	}
	
	/**
	 * Bring up a dialog that lets the user create a new VM definition.
	 */
	private void addVM() {
		AddVMDialog dialog= new AddVMDialog(this, getShell(), JavaRuntime.getVMInstallTypes(), null);
		dialog.setTitle(JREMessages.InstalledJREsBlock_7); 
		if (dialog.open() != Window.OK) {
			return;
		}
		fVMList.refresh();
	}
	
	/**
	 * @see IAddVMDialogRequestor#vmAdded(IVMInstall)
	 */
	public void vmAdded(IVMInstall vm) {
		fVMs.add(vm);
		fVMList.refresh();
	}
	
	/**
	 * @see IAddVMDialogRequestor#isDuplicateName(String)
	 */
	public boolean isDuplicateName(String name) {
		for (int i= 0; i < fVMs.size(); i++) {
			IVMInstall vm = (IVMInstall)fVMs.get(i);
			if (vm.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}	
	
	private void editVM() {
		IStructuredSelection selection= (IStructuredSelection)fVMList.getSelection();
		IVMInstall vm= (IVMInstall)selection.getFirstElement();
		if (vm == null) {
			return;
		}
		if (isContributed(vm)) {
			VMDetailsDialog dialog= new VMDetailsDialog(getShell(), vm);
			dialog.open();
		} else {
			AddVMDialog dialog= new AddVMDialog(this, getShell(), JavaRuntime.getVMInstallTypes(), vm);
			dialog.setTitle(JREMessages.InstalledJREsBlock_8); 
			if (dialog.open() != Window.OK) {
				return;
			}
			fVMList.refresh(vm);
		}
	}
	
	private void removeVMs() {
		IStructuredSelection selection= (IStructuredSelection)fVMList.getSelection();
		IVMInstall[] vms = new IVMInstall[selection.size()];
		Iterator iter = selection.iterator();
		int i = 0;
		while (iter.hasNext()) {
			vms[i] = (IVMInstall)iter.next();
			i++;
		}
		removeJREs(vms);
	}	
	
	/**
	 * Removes the given VMs from the table.
	 * 
	 * @param vms
	 */
	public void removeJREs(IVMInstall[] vms) {
		IStructuredSelection prev = (IStructuredSelection) getSelection();
		for (int i = 0; i < vms.length; i++) {
			fVMs.remove(vms[i]);
		}
		fVMList.refresh();
		IStructuredSelection curr = (IStructuredSelection) getSelection();
		if (!curr.equals(prev)) {
			IVMInstall[] installs = getJREs();
			if (curr.size() == 0 && installs.length == 1) {
				// pick a default VM automatically
				setSelection(new StructuredSelection(installs[0]));
			} else {
				fireSelectionChanged();
			}
		}
	}
	
	/**
	 * Search for installed VMs in the file system
	 */
	protected void search() {
		
		// choose a root directory for the search 
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage(JREMessages.InstalledJREsBlock_9); 
		dialog.setText(JREMessages.InstalledJREsBlock_10); 
		String path = dialog.open();
		if (path == null) {
			return;
		}
		
		// ignore installed locations
		final Set exstingLocations = new HashSet();
		Iterator iter = fVMs.iterator();
		while (iter.hasNext()) {
			exstingLocations.add(((IVMInstall)iter.next()).getInstallLocation());
		}
		
		// search
		final File rootDir = new File(path);
		final List locations = new ArrayList();
		final List types = new ArrayList();

		IRunnableWithProgress r = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				monitor.beginTask(JREMessages.InstalledJREsBlock_11, IProgressMonitor.UNKNOWN); 
				search(rootDir, locations, types, exstingLocations, monitor);
				monitor.done();
			}
		};
		
		try {
            ProgressMonitorDialog progress = new ProgressMonitorDialog(getShell());
            progress.run(true, true, r);
		} catch (InvocationTargetException e) {
			JavaPlugin.log(e);
		} catch (InterruptedException e) {
			// cancelled
			return;
		}
		
		if (locations.isEmpty()) {
			MessageDialog.openInformation(getShell(), JREMessages.InstalledJREsBlock_12, MessageFormat.format(JREMessages.InstalledJREsBlock_13, new String[]{path})); // 
		} else {
			iter = locations.iterator();
			Iterator iter2 = types.iterator();
			while (iter.hasNext()) {
				File location = (File)iter.next();
				IVMInstallType type = (IVMInstallType)iter2.next();
				IVMInstall vm = new VMStandin(type, createUniqueId(type));
				String name = VMInstallTypeUtil.getVMInstallName(type, location);
				String nameCopy = new String(name);
				int i = 1;
				while (isDuplicateName(nameCopy)) {
					nameCopy = name + '(' + i++ + ')'; 
				}
				vm.setName(nameCopy);
				vm.setInstallLocation(location);
				if (type instanceof AbstractVMInstallType) {
					//set default java doc location
					AbstractVMInstallType abs = (AbstractVMInstallType)type;
					vm.setJavadocLocation(abs.getDefaultJavadocLocation(location));
				}
				vmAdded(vm);
			}
		}
		
	}
	
	protected Shell getShell() {
		return getControl().getShell();
	}

	/**
	 * Find a unique VM id.  Check existing 'real' VMs, as well as the last id used for
	 * a VMStandin.
	 */
	private String createUniqueId(IVMInstallType vmType) {
		String id= null;
		do {
			id= String.valueOf(System.currentTimeMillis());
		} while (vmType.findVMInstall(id) != null || id.equals(fgLastUsedID));
		fgLastUsedID = id;
		return id;
	}	
	
	/**
	 * Searches the specified directory recursively for installed VMs, adding each
	 * detected VM to the <code>found</code> list. Any directories specified in
	 * the <code>ignore</code> are not traversed.
	 * 
	 * @param directory
	 * @param found
	 * @param types
	 * @param ignore
	 */
	protected void search(File directory, List found, List types, Set ignore, IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return;
		}

		String[] names = directory.list();
		if (names == null) {
			return;
		}
		List subDirs = new ArrayList();
		for (int i = 0; i < names.length; i++) {
			if (monitor.isCanceled()) {
				return;
			}
			File file = new File(directory, names[i]);
			try {
				monitor.subTask(MessageFormat.format(JREMessages.InstalledJREsBlock_14, new String[]{Integer.toString(found.size()), file.getCanonicalPath()})); 
			} catch (IOException e) {
			}		
			IVMInstallType[] vmTypes = JavaRuntime.getVMInstallTypes();	
			if (file.isDirectory()) {
				if (!ignore.contains(file)) {
					boolean validLocation = false;
					
					// Take the first VM install type that claims the location as a
					// valid VM install.  VM install types should be smart enough to not
					// claim another type's VM, but just in case...
					for (int j = 0; j < vmTypes.length; j++) {
						if (monitor.isCanceled()) {
							return;
						}
						IVMInstallType type = vmTypes[j];
						IStatus status = type.validateInstallLocation(file);
						if (status.isOK()) {
							found.add(file);
							types.add(type);
							validLocation = true;
							break;
						}
					}
					if (!validLocation) {
						subDirs.add(file);
					}
				}
			}
		}
		while (!subDirs.isEmpty()) {
			File subDir = (File)subDirs.remove(0);
			search(subDir, found, types, ignore, monitor);
			if (monitor.isCanceled()) {
				return;
			}
		}
		
	}	
	
	/**
	 * Sets the checked JRE, possible <code>null</code>
	 * 
	 * @param vm JRE or <code>null</code>
	 */
	public void setCheckedJRE(IVMInstall vm) {
		if (vm == null) {
			setSelection(new StructuredSelection());
		} else {
			setSelection(new StructuredSelection(vm));
		}
	}
	
	/**
	 * Returns the checked JRE or <code>null</code> if none.
	 * 
	 * @return the checked JRE or <code>null</code> if none
	 */
	public IVMInstall getCheckedJRE() {
		Object[] objects = fVMList.getCheckedElements();
		if (objects.length == 0) {
			return null;
		}
		return (IVMInstall)objects[0];
	}
	
	/**
	 * Persist table settings into the give dialog store, prefixed
	 * with the given key.
	 * 
	 * @param settings dialog store
	 * @param qualifier key qualifier
	 */
	public void saveColumnSettings(IDialogSettings settings, String qualifier) {
        int columnCount = fTable.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			settings.put(qualifier + ".columnWidth" + i, fTable.getColumn(i).getWidth());	 //$NON-NLS-1$
		}
		settings.put(qualifier + ".sortColumn", fSortColumn); //$NON-NLS-1$
	}
	
	/**
	 * Restore table settings from the given dialog store using the
	 * given key.
	 * 
	 * @param settings dialog settings store
	 * @param qualifier key to restore settings from
	 */
	public void restoreColumnSettings(IDialogSettings settings, String qualifier) {
		fVMList.getTable().layout(true);
        restoreColumnWidths(settings, qualifier);
		try {
			fSortColumn = settings.getInt(qualifier + ".sortColumn"); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			fSortColumn = 1;
		}
		switch (fSortColumn) {
			case 1:
				sortByName();
				break;
			case 2:
				sortByLocation();
				break;
			case 3:
				sortByType();
				break;
		}
	}
	
	private void restoreColumnWidths(IDialogSettings settings, String qualifier) {
        int columnCount = fTable.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            int width = -1;
            
            try {
                width = settings.getInt(qualifier + ".columnWidth" + i); //$NON-NLS-1$
            } catch (NumberFormatException e) {}
            
            if (width <= 0) {
                fTable.getColumn(i).pack();
            } else {
                fTable.getColumn(i).setWidth(width);
            }
        }
	}
	
	/**
	 * Populates the JRE table with existing JREs defined in the workspace.
	 */
	protected void fillWithWorkspaceJREs() {
		// fill with JREs
		List standins = new ArrayList();
		IVMInstallType[] types = JavaRuntime.getVMInstallTypes();
		for (int i = 0; i < types.length; i++) {
			IVMInstallType type = types[i];
			IVMInstall[] installs = type.getVMInstalls();
			for (int j = 0; j < installs.length; j++) {
				IVMInstall install = installs[j];
				standins.add(new VMStandin(install));
			}
		}
		setJREs((IVMInstall[])standins.toArray(new IVMInstall[standins.size()]));	
	}
		
}
