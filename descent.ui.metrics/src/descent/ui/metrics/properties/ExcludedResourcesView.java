package descent.ui.metrics.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.oro.text.regex.MalformedPatternException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;

import descent.ui.metrics.MetricsPlugin;

public final class ExcludedResourcesView extends Composite {
    private static class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object input) {
            ExcludedResources resources = (ExcludedResources) input;
            ArrayList list = new ArrayList();
            list.addAll(Arrays.asList(resources.getRegexes()));
            list.addAll(Arrays.asList(resources.getFiles()));
            return list.toArray(new Object[list.size()]);
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }
    }

    private static class ExcludedResourcesViewSorter extends ViewerSorter {
        public int compare(Viewer viewer, Object e1, Object e2) {
            return getString(e1).compareTo(getString(e2));
        }

        private String getString(Object e1) {
            if (e1 instanceof String) {
                return (String) e1;
            } else {
                return ((IFile) e1).getProjectRelativePath().toString();
            }
        }
    }

    private static class ExcludedResourcesTableLabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof String) {
                return (String) element;
            } else {
                return ((IFile) element).getProjectRelativePath().toString();
            }
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void removeListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
    }

    private ExcludedResources excludedResources;
    private boolean modified;

    private Table excludedResourcesTable;
    private TableViewer excludedResourcesTableViewer;

    public ExcludedResourcesView(Composite parent, IProject project) throws IOException {
        super(parent, SWT.NULL);
        excludedResources = new ExcludedResources(project);

        setLayout(new GridLayout());
        setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        initialiseTable();
        initialiseButtons();
    }

    public void store() throws CoreException {
        excludedResources.store();
        modified = false;
    }

    public boolean isModified() {
        return modified;
    }

    private void initialiseTable() {
        excludedResourcesTable = new Table(this, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        excludedResourcesTable.setHeaderVisible(true);
        excludedResourcesTable.setLinesVisible(false);

        initialiseTableLayout();
        initialiseTableViewer();
    }

    private void initialiseTableLayout() {
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1000));
        excludedResourcesTable.setLayout(tableLayout);
        excludedResourcesTable.setLayoutData(new GridData(GridData.FILL_BOTH));

        TableColumn column = new TableColumn(excludedResourcesTable, SWT.NULL);
        column.setText("Excluded Resources");
        column.setResizable(false);
    }

    private void initialiseTableViewer() {
        excludedResourcesTableViewer = new TableViewer(excludedResourcesTable);
        excludedResourcesTableViewer.setContentProvider(new ContentProvider());
        excludedResourcesTableViewer.setInput(excludedResources);
        excludedResourcesTableViewer.setSorter(new ExcludedResourcesViewSorter());
        excludedResourcesTableViewer.setLabelProvider(new ExcludedResourcesTableLabelProvider());
    }

    private void initialiseButtons() {
        Composite buttons = new Composite(this, SWT.NULL);
        buttons.setLayout(new GridLayout(2, false));
        buttons.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Button removeButton = createRemoveButton(buttons);
        Button editButton = createEditButton(buttons);
        addSelectionChangedListenerToTable(removeButton, editButton);

        createAddResourceButton(buttons);
        createAddRegexButton(buttons);
    }

    private Button createAddRegexButton(Composite buttons) {
        return createPushButton(buttons, "Add Regex...", true, new Listener() {
            public void handleEvent(Event evt) {
                addRegex();
            }
        });
    }

    private Button createAddResourceButton(Composite buttons) {
        return createPushButton(buttons, "Add Resource...", true, new Listener() {
            public void handleEvent(Event evt) {
                addExcludedFile();
            }
        });
    }

    private void addSelectionChangedListenerToTable(final Button removeButton, final Button editButton) {
        excludedResourcesTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                boolean empty = event.getSelection().isEmpty();
                removeButton.setEnabled(!empty);
                editButton.setEnabled(!empty);
            }
        });
    }

    private Button createEditButton(Composite buttons) {
        return createPushButton(buttons, "Edit...", false, new Listener() {
            public void handleEvent(Event evt) {
                editSelectedResource();
            }
        });
    }

    private Button createRemoveButton(Composite buttons) {
        return createPushButton(buttons, "Remove", false, new Listener() {
            public void handleEvent(Event evt) {
                removeSelectedExclusions();
            }
        });
    }

    private Button createPushButton(Composite parent, String label, boolean enabled, Listener listener) {
        Button button = new Button(parent, SWT.PUSH);
        button.setEnabled(enabled);
        button.setText(label);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        button.setLayoutData(data);
        button.addListener(SWT.Selection, listener);
        return button;
    }

    private void editSelectedResource() {
        IStructuredSelection selection = (IStructuredSelection) excludedResourcesTableViewer.getSelection();
        if (selection.isEmpty()) {
            return;
        }

        editResource(selection.getFirstElement());
    }

    private void editResource(Object firstSelectedItem) {
        if (firstSelectedItem instanceof String) {
            editRegex((String) firstSelectedItem);
        } else {
            addExcludedFile();
        }
    }

    private void addExcludedFile() {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), excludedResources.getProject(), "Select resources to exclude");
        dialog.setInitialSelections(excludedResources.getFiles());
        dialog.open();
        Object[] resources = dialog.getResult();
        if (resources != null) {
            addFilesToExcludedResources(resources);
        }
    }

    private void addFilesToExcludedResources(Object[] resources) {
        ArrayList files = new ArrayList();
        for (int i = 0; i < resources.length; i++) {
            if (resources[i] instanceof IFile) {
                files.add(resources[i]);
            }
        }
        excludedResources.setExcludedFiles((IFile[]) files.toArray(new IFile[files.size()]));
        excludedResourcesTableViewer.refresh();
        modified = true;
    }

    private void editRegex(String original) {
        InputDialog dialog = openRegexDialog(original);
        if (dialog.getReturnCode() == Window.OK) {
            excludedResources.removeRegex(original);
            addRegex(dialog.getValue());
        }
    }

    private void addRegex() {
        InputDialog dialog = openRegexDialog("");
        if (dialog.getReturnCode() == Window.OK) {
            addRegex(dialog.getValue());
        }
    }

    private void addRegex(String regex) {
        String trimmedRegex = regex.trim();
        if (trimmedRegex.length() == 0) {
            return;
        }

        addTrimmedRegex(trimmedRegex);

        excludedResourcesTableViewer.refresh();
        modified = true;
    }

    private void addTrimmedRegex(String trimmedRegex) {
        try {
            excludedResources.addRegex(trimmedRegex);
        } catch (MalformedPatternException mpex) {
            ErrorDialog.openError(getShell(), "Invalid regex", null, new Status(IStatus.ERROR, MetricsPlugin.PLUGIN_ID, IStatus.OK, "The string '" + trimmedRegex + "' is not a valid regex.", null));
        }
    }

    private InputDialog openRegexDialog(String initialValue) {
        InputDialog dialog = new InputDialog(getShell(), "Regex Entry", "Enter a regex", initialValue, new IInputValidator() {
            public String isValid(String regex) {
                if (!excludedResources.isValidRegex(regex.trim())) {
                    return "Not a valid regex";
                } else {
                    return null;
                }
            }
        });
        dialog.setBlockOnOpen(true);
        dialog.open();
        return dialog;
    }

    private void removeSelectedExclusions() {
        IStructuredSelection selection = (IStructuredSelection) excludedResourcesTableViewer.getSelection();
        if (selection.isEmpty()) {
            return;
        }

        removeSelectedExclusions(selection);
        excludedResourcesTableViewer.refresh();

        modified = true;
    }

    private void removeSelectedExclusions(IStructuredSelection selection) {
        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
            Object toRemove = iter.next();
            if (toRemove instanceof String) {
                excludedResources.removeRegex((String) toRemove);
            } else {
                excludedResources.removeFile((IFile) toRemove);
            }
        }
    }
}
