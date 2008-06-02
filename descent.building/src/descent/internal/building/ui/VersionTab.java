package descent.internal.building.ui;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;

import descent.internal.building.BuilderUtil;
import descent.internal.ui.util.SWTUtil;
import descent.internal.ui.util.TableLayoutComposite;

import static descent.building.IDescentBuilderConstants.*;

/* package */ class VersionTab extends AbstractBuilderTab
{
    //--------------------------------------------------------------------------
    // Version source setting
    
    private final class VersionSourceSetting implements ISetting
    {
        private Button fSelectedProjRadio;
        private Button fActiveProjRadio;
        private Button fNoneRadio;
        
        public void addToControl(Composite comp)
        {
            Label label = new Label(comp, SWT.LEFT);
            GridData gd = new GridData();
            gd.horizontalSpan = 1;
            label.setLayoutData(gd);
            label.setText("Include versions from:");
            
            fSelectedProjRadio = createRadioButton(comp, 1,
                    "Selected project", 25, null);
            fActiveProjRadio = createRadioButton(comp, 1,
                    "Workspace active project", 25, null); 
            fNoneRadio = createRadioButton(comp, 1,
                    "None (only use versions selected below)", 25, null);
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            String mode = getAttribute(config, ATTR_VERSION_SOURCE, 
                    SOURCE_SELECTED_PROJECT);
            
            if(mode.equals(SOURCE_ACTIVE_PROJECT))
            {
                fSelectedProjRadio.setSelection(false);
                fActiveProjRadio.setSelection(true);
                fNoneRadio.setSelection(false);
            }
            else if(mode.equals(SOURCE_LAUNCH_CONFIG))
            {
                fSelectedProjRadio.setSelection(false);
                fActiveProjRadio.setSelection(false);
                fNoneRadio.setSelection(true);
            }
            else
            {
                fSelectedProjRadio.setSelection(true);
                fActiveProjRadio.setSelection(false);
                fNoneRadio.setSelection(false);
            }
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            if(fActiveProjRadio.getSelection())
                config.setAttribute(ATTR_VERSION_SOURCE, SOURCE_ACTIVE_PROJECT);
            else if(fNoneRadio.getSelection())
                config.setAttribute(ATTR_VERSION_SOURCE, SOURCE_LAUNCH_CONFIG);
            else
                config.setAttribute(ATTR_VERSION_SOURCE, SOURCE_SELECTED_PROJECT);
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(ATTR_VERSION_SOURCE, SOURCE_SELECTED_PROJECT);
        }

        public String validate()
        {
            // Any choice is valid
            return null;
        }
    }
    
    //--------------------------------------------------------------------------
    // Debug mode setting
    
    private final class DebugModeSetting implements ISetting
    {
        private Button fCheckbox;

        public void addToControl(Composite comp)
        {
            fCheckbox = new Button(comp, SWT.CHECK);
            fCheckbox.setText("Debug mode (-debug)");
            GridData gd = new GridData();
            gd.horizontalSpan = 1;
            fCheckbox.setLayoutData(gd);
            fCheckbox.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    updateLaunchConfigurationDialog();
                }
            });
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            fCheckbox.setSelection(getAttribute(config, ATTR_DEBUG_MODE, true));
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(ATTR_DEBUG_MODE, fCheckbox.getSelection());
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(ATTR_DEBUG_MODE, true);
        }

        public String validate()
        {
            // Either way it's valid
            return null;
        }
    }
    
    //--------------------------------------------------------------------------
    // Identifiers and levels setting
    
    @SuppressWarnings("unchecked")
    private final class DVSetting implements ISetting
    {
        private final String fName;
        private final boolean fAllowPredefined;
        private final String fLevelAttr;
        private final String fIdentsAttr;
        
        private Button fLevelCheckbox;
        private Label fLevelLabel;
        private Spinner fLevelSpinner;
        
        /*
         * Note: if you're wondering why I didn't just use a ListDialogField
         * it's because I spent about an hour trying to get editing support to
         * work with it, and it won't. So here I am re-implementing it all.
         */
        private Set fIdents;
        private TableViewer fViewer;
        private Button fNewButton;
        private Button fRemoveButton;
        
        public DVSetting(String name, boolean allowPredefined, 
                String levelAttr, String identsAttr)
        {
            fName = name;
            fAllowPredefined = allowPredefined;
            fLevelAttr = levelAttr;
            fIdentsAttr = identsAttr;
        }

        public void addToControl(Composite comp)
        {
            comp = new Composite(comp, SWT.NONE);
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 1;
            comp.setLayoutData(gd);
            GridLayout layout = new GridLayout();
            layout.numColumns = 4;
            comp.setLayout(layout);
            
            createSpinner(comp);
            createList(comp);
        }
        
        private void createSpinner(Composite comp)
        {
            fLevelCheckbox = new Button(comp, SWT.CHECK);
            GridData gd = new GridData();
            gd.horizontalSpan = 1;
            fLevelCheckbox.setLayoutData(gd);
            fLevelCheckbox.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    updateSpinnerEnablement();
                    updateLaunchConfigurationDialog();
                }
            });
            
            fLevelLabel = new Label(comp, SWT.LEFT);
            fLevelLabel.setText(String.format("%1$s level:", fName));
            gd = new GridData();
            gd.horizontalSpan = 1;
            fLevelLabel.setLayoutData(gd);
            
            fLevelSpinner = new Spinner(comp, SWT.BORDER);
            gd = new GridData();
            gd.horizontalSpan = 1;
            fLevelSpinner.setLayoutData(gd);
            fLevelSpinner.setMaximum(Integer.MAX_VALUE);
            
            Label spacer = new Label(comp, SWT.NONE);
            gd = new GridData();
            gd.horizontalSpan = 1;
            spacer.setLayoutData(gd);
            
            updateSpinnerEnablement();
        }
        
        private void updateSpinnerEnablement()
        {
            boolean enabled = fLevelCheckbox.getSelection();
            fLevelLabel.setEnabled(enabled);
            fLevelSpinner.setEnabled(enabled);
        }
        
        private void createList(Composite comp)
        {
            fIdents = new TreeSet();
            
            Label listLabel = new Label(comp, SWT.NONE);
            listLabel.setText(String.format("%1$s identifiers:", fName));
            GridData gd = new GridData();
            gd.horizontalSpan = 4;
            listLabel.setLayoutData(gd);
            
            createViewer(comp);
            createButtons(comp);
        }
        
        private void createViewer(Composite comp)
        {
            // Why must Eclipse make you jump though all these hoops to use resources?
            fRedColor = new Color(getShell().getDisplay(), 255, 0, 0);
            
            TableLayoutComposite tableComp = new TableLayoutComposite(comp, SWT.NONE);
            Table table = new Table(tableComp, SWT.SINGLE | SWT.BORDER | SWT.SINGLE);
            fViewer = new TableViewer(table);
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 3;
            tableComp.setLayoutData(gd);
            
            // Makes it so that the editor only activates on double-click
            TableViewerEditor.create(fViewer,
                new ColumnViewerEditorActivationStrategy(fViewer)
                {
                    @Override
                    protected boolean isEditorActivationEvent(
                            ColumnViewerEditorActivationEvent event)
                    {
                        return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                                || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
                                || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
                    }
                },
                ColumnViewerEditor.DEFAULT);
            
            fViewer.setContentProvider(new IStructuredContentProvider()
            {
                public Object[] getElements(Object inputElement)
                {
                    return fIdents.toArray(new Object[fIdents.size()]);
                }

                public void dispose()
                {
                    // Do nothing
                }

                public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
                {
                    // Do nothing
                }
            });
            fViewer.addSelectionChangedListener(new ISelectionChangedListener()
            {
                public void selectionChanged(SelectionChangedEvent event)
                {
                    updateButtonEnablement();
                }
            });
            
            tableComp.addColumnData(new ColumnWeightData(1));
            final TextCellEditor cellEditor = new TextCellEditor(fViewer.getTable());
            TableViewerColumn column = new TableViewerColumn(fViewer, SWT.NONE);
            column.setLabelProvider(new ColumnLabelProvider()
            {
                @Override
                public Color getForeground(Object element)
                {
                    if(!isValidIdentifier((String) element))
                        return fRedColor;
                    else
                        return null;
                }

                @Override
                public String getText(Object element)
                {
                    return (String) element;
                }
            });
            column.setEditingSupport(new EditingSupport(fViewer)
            {
                @Override
                protected boolean canEdit(Object element)
                {
                    return true;
                }

                @Override
                protected CellEditor getCellEditor(Object element)
                {
                    return cellEditor;
                }

                @Override
                protected Object getValue(Object element)
                {
                    return element;
                }

                @Override
                protected void setValue(Object element, Object value)
                {
                    fIdents.remove(element);
                    if(!"".equals(value));
                        fIdents.add(value);
                    updateList();
                    fViewer.setSelection(new StructuredSelection(value));
                }
            });
            
            fViewer.setInput(this);
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
                    {
                        fViewer.add("");
                        fViewer.editElement("", 0);
                    }
                    else if(fRemoveButton == e.widget)
                    {
                        String selectedElement = getSelectedElement();
                        if(null != selectedElement)
                        {
                            fIdents.remove(getSelectedElement());
                            updateList();
                        }
                    }
                }
            };
            
            Composite comp = new Composite(parent, SWT.NONE);
            GridData gd = new GridData(GridData.FILL_VERTICAL);
            comp.setLayoutData(gd);
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            comp.setLayout(layout);
            
            fNewButton = createButton(comp, listener, "New");
            fRemoveButton = createButton(comp, listener, "Remove");
        }
        
        private Button createButton(Composite comp, SelectionListener listener, String label)
        {
            Button button = new Button(comp, SWT.PUSH);
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
        
        private String getSelectedElement()
        {
            IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
            if(null != selection && selection.size() > 0)
                return (String) selection.getFirstElement();
            else
                return null;
        }
        
        private void updateButtonEnablement()
        {
            boolean enabled = null != getSelectedElement();
            fRemoveButton.setEnabled(enabled);
        }
        
        private void updateList()
        {
            updateButtonEnablement();
            fViewer.refresh();
            validatePage();
            updateLaunchConfigurationDialog();
        }
        
        public void initializeFrom(ILaunchConfiguration config)
        {
            String level = getAttribute(config, fLevelAttr, "");
            if("".equals(level))
            {
                fLevelCheckbox.setSelection(false);
                fLevelSpinner.setSelection(0);
            }
            else
            {
                fLevelCheckbox.setSelection(true);
                fLevelSpinner.setSelection(Integer.parseInt(level));
            }
            updateSpinnerEnablement();
            
            fIdents = new TreeSet(getAttribute(config, fIdentsAttr, EMPTY_LIST));
            updateList();
        }

        @SuppressWarnings("unchecked")
        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(fLevelAttr, fLevelCheckbox.getSelection() ?
                    Integer.toString(fLevelSpinner.getSelection()) : "");
            config.setAttribute(fIdentsAttr, new ArrayList(fIdents));
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(fLevelAttr, "");
            config.setAttribute(fIdentsAttr, EMPTY_LIST);
        }
        
        private boolean isValidIdentifier(String id)
        {
            return BuilderUtil.isValidIdentifier(id) && 
                    (fAllowPredefined || !BuilderUtil.isPredefinedVersion(id));
        }
        
        public String validate()
        {
            if(!fAllowPredefined)
            {
                for(Object element : fIdents)
                {
                    String id = (String) element;
                    
                    if(!BuilderUtil.isValidIdentifier(id))
                        return String.format("%1$s is not a valid identifier", id);
                    
                    if(!fAllowPredefined && BuilderUtil.isPredefinedVersion(id))
                        return String.format("Cannot use predefined version %1$s", id);
                }
            }
            return null;
        }
    }
    
    //--------------------------------------------------------------------------
    // Resources
    
    private Color fRedColor;
    
    @Override
    public void dispose()
    {
        super.dispose();
        if(null != fRedColor)
            fRedColor.dispose();
    }
    
    //--------------------------------------------------------------------------
    // Tab

    @Override
    protected String getIconPath()
    {
        return "obj16/builders.gif";
    }
    
    @Override
    protected ISetting[] getSettings()
    {
        return new ISetting[]
        {
            new VersionSourceSetting(),
            new DebugModeSetting(),
            new GroupSetting("Additional versions", 1, 2, GridData.FILL_BOTH,
                new ISetting[]
                {
                    new DVSetting("Version", false, ATTR_VERSION_LEVEL, ATTR_VERSION_IDENTS),
                    new DVSetting("Debug", true, ATTR_DEBUG_LEVEL, ATTR_DEBUG_IDENTS),
                }),
        };
    }
    
    @Override
    protected Layout getTopLayout()
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        return layout;
    }
    
    public String getName()
    {
        return "Version";
    }

}
