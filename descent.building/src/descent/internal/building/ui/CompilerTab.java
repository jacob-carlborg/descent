package descent.internal.building.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.widgets.ScrolledFormText;

import descent.building.IDescentBuilderConstants;
import descent.building.compiler.ICompilerInterface;
import descent.building.compiler.ui.BooleanOption;
import descent.building.compiler.ui.CompilerOption;
import descent.building.compiler.ui.EnumOption;
import descent.building.compiler.ui.IValidatableOption;
import descent.building.compiler.ui.StringOption;
import descent.internal.building.BuilderUtil;
import descent.launching.IVMInstall;
import descent.launching.IVMInstallType;
import descent.launching.JavaRuntime;

// TODO egads, the whole issue of synchronizing all this is CRAZY!!!! So
// this is in a NON-WORKING STATE right now
/* package */ final class CompilerTab extends AbstractBuilderTab
{   
    //--------------------------------------------------------------------------
    // Compiler selection
    
    private final class CompilerSetting implements ISetting
    {
        private Link fHelpText;
        private Combo fCombo;
        private IVMInstall[] fCompilers;
        
        public void addToControl(Composite comp)
        {
            // Create a sub-composite so the table viewer doesn't take over the
            // entire tab
            comp = new Composite(comp, SWT.NONE);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 2;
            comp.setLayoutData(gd);
            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            comp.setLayout(layout);
            
            fHelpText = new Link(comp, SWT.LEFT | SWT.WRAP);
            fHelpText.setText(BuilderUIMessages.CompilerTab_compilers_preference_page_link);
            gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 2;
            fHelpText.setLayoutData(gd);
            fHelpText.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    PreferencesUtil.createPreferenceDialogOn(getShell(),
                            "descent.debug.ui.preferences.VMPreferencePage", //$NON-NLS-1$
                            null, null).open();
                    
                    // Reset stuff so new changes are reflected in this dialog
                    reinitializeCompilers();
                    int selectionIndex = fCombo.getSelectionIndex();
                    resetComboItems();
                    fCombo.select(fCombo.getSelectionIndex() >= fCompilers.length ?
                            0 : selectionIndex);
                } 
            });
            
            fCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
            gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            gd.horizontalSpan = 1;
            fCombo.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent e)
                {
                    int selectedIndex = fCombo.getSelectionIndex();
                    IVMInstall selectedCompiler = selectedIndex >= 0 ?
                            fCompilers[selectedIndex] : null;
                    compilerOptions.initializeInput(selectedCompiler);
                    
                    validatePage();
                    updateLaunchConfigurationDialog();
                }
            });
            
            initializeCompilers();
            resetComboItems();
        }
        
        /**
         * Initialize compilers if they haven't been initialized (since I'm not
         * sure which order createControl and setDefaults will be set in)
         */
        private void initializeCompilers()
        {
            if(null != fCompilers)
                return;
            
            reinitializeCompilers();
        }
        
        /**
         * Reset the compiler list whether it's already been set or not
         */
        private void reinitializeCompilers()
        {
            List<IVMInstall> compilers = new ArrayList<IVMInstall>();
            for(IVMInstallType vmInstallType : JavaRuntime.getVMInstallTypes())
                for(IVMInstall vmInstall : vmInstallType.getVMInstalls())
                    compilers.add(vmInstall);
            fCompilers = compilers.toArray(new IVMInstall[compilers.size()]);
        }
        
        private void resetComboItems()
        {
            String[] items = new String[fCompilers.length];
            for(int i = 0; i < items.length; i++)
                items[i] = getCompilerLabel(fCompilers[i]);
            fCombo.setItems(items);
        }
        
        private String getCompilerLabel(IVMInstall compiler)
        {
            StringBuilder label = new StringBuilder();
            label.append(compiler.getName());
            label.append(" ("); //$NON-NLS-1$
            label.append(compiler.getVMInstallType().getName());
            label.append(")"); //$NON-NLS-1$
            return label.toString();
        }
        
        public void initializeFrom(ILaunchConfiguration config)
        {
            int i = getCompilerIndex(config);
            if(-1 != i)
                fCombo.select(i);
            else if(fCompilers.length >= 0)
                fCombo.select(0);
        }
        
        public IVMInstall getCompiler(ILaunchConfiguration config)
        {
            int i = getCompilerIndex(config);
            return -1 == i ? null : fCompilers[i];
        }
        
        private int getCompilerIndex(ILaunchConfiguration config)
        {
            initializeCompilers();
            String compilerTypeId = BuilderUtil.getAttribute(config, 
                    IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, ""); //$NON-NLS-1$
            String compilerId = BuilderUtil.getAttribute(config, 
                    IDescentBuilderConstants.ATTR_COMPILER_ID, ""); //$NON-NLS-1$
            
            for(int i = 0; i < fCompilers.length; i++)
            {
                IVMInstall compiler = fCompilers[i];
                if(compiler.getVMInstallType().getId().equals(compilerTypeId) &&
                        compiler.getId().equals(compilerId))
                {
                    return i;
                }
            }
            
            return -1;
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            int selectedIndex = fCombo.getSelectionIndex();
            if(selectedIndex >= 0)
            {
                IVMInstall compiler = fCompilers[selectedIndex];
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, compiler.getVMInstallType().getId());
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_ID, compiler.getId());
            }
            else
            {
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, ""); //$NON-NLS-1$
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_ID, ""); //$NON-NLS-1$
            }
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            IVMInstall defaultCompiler = getDefault();
            if(null != defaultCompiler)
            {
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, defaultCompiler.getVMInstallType().getId());
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_ID, defaultCompiler.getId());
            }
            else
            {
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, ""); //$NON-NLS-1$
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_ID, ""); //$NON-NLS-1$
            }
        }
        
        public IVMInstall getDefault()
        {
            initializeCompilers();
            return fCompilers.length > 0 ? fCompilers[0] : null;
        }

        public String validate()
        {
            int selectedIndex = fCombo.getSelectionIndex();
            
            if(selectedIndex < 0)
                return BuilderUIMessages.CompilerTab_error_no_compiler;
            
            return null;
        }
    }
    
    private final class CompilerOptions implements ISetting
    {
        private abstract class CompilerUIOption
        {
            public TreeEntry parent;
            public CellEditor editor;
            
            public abstract CompilerOption getOption();
            public abstract CellEditor getCellEditor();
            public abstract Object getValue();
            public abstract void setValue(Object value);
            public abstract String getText();
            public abstract void initializeTo(String value);
            public abstract String getStringValue();
        }
        
        private final class CheckboxUIOption extends CompilerUIOption
        {
            private BooleanOption option;
            public Boolean selected = Boolean.FALSE;
            
            public CheckboxUIOption(BooleanOption option)
            {
                this.option = option;
            }
            
            @Override
            public CompilerOption getOption()
            {
                return option;
            }

            @Override
            public CellEditor getCellEditor()
            {
                if(null == editor)
                {
                    editor = new CheckboxCellEditor(fViewer.getTree());
                    editor.addListener(fCellEditorListener);
                }
                
                return editor;
            }

            @Override
            public Object getValue()
            {
                return selected;
            }

            @Override
            public void setValue(Object value)
            {
                selected = (Boolean) value;
            }
            
            @Override
            public String getText()
            {
                return selected ? option.getOnText() : option.getOffText();
            }
            
            @Override
            public void initializeTo(String value)
            {
                selected = Boolean.valueOf(value);
            }

            @Override
            public String getStringValue()
            {
                return selected ? "true" : "false"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        
        private final class ComboUIOption extends CompilerUIOption
        {
            private EnumOption option;
            public Integer selected = Integer.valueOf(0);
            
            public ComboUIOption(EnumOption option)
            {
                this.option = option;
            }
            
            @Override
            public CompilerOption getOption()
            {
                return option;
            }
            
            @Override
            public CellEditor getCellEditor()
            {
                if(null == editor)
                {
                    editor = new ComboBoxCellEditor(fViewer.getTree(), 
                            option.getOptionEditLabels());
                    editor.addListener(fCellEditorListener);
                }
                
                return editor;
            }

            @Override
            public Object getValue()
            {
                return selected;
            }

            @Override
            public void setValue(Object value)
            {
                selected = (Integer) value;
            }
            
            @Override
            public String getText()
            {
                return option.getOptionViewLabels()[selected];
            }
            
            @Override
            public void initializeTo(String value)
            {
                String[] optionValues = option.getOptionValues();
                for(int i = 0; i < optionValues.length; i++)
                {
                    if(optionValues[i].equals(value))
                    {
                        selected = Integer.valueOf(i);
                        return;
                    }
                }
            }
            
            @Override
            public String getStringValue()
            {
                return option.getOptionValues()[selected.intValue()];
            }
        }
        
        private class TextUIOption extends CompilerUIOption
        {
            protected StringOption option;
            public String selected = ""; //$NON-NLS-1$
            
            public TextUIOption(StringOption option)
            {
                this.option = option;
            }
            
            @Override
            public CompilerOption getOption()
            {
                return option;
            }
            
            @Override
            public CellEditor getCellEditor()
            {
                if(null == editor)
                {
                    editor = new TextCellEditor(fViewer.getTree());
                    editor.addListener(fCellEditorListener);
                }
                
                return editor;
            }

            @Override
            public String getText()
            {
                return selected;
            }

            @Override
            public Object getValue()
            {
                return selected;
            }

            @Override
            public void setValue(Object value)
            {
                selected = (String) value;
            }
            
            @Override
            public void initializeTo(String value)
            {
                selected = value;
            }
            
            @Override
            public String getStringValue()
            {
                return selected;
            }
        }
        
        // TODO file UI options
        
        private final class TreeEntry
        {
            private final String label;
            private final CompilerUIOption[] children;
            
            public TreeEntry(String label, CompilerUIOption[] children)
            {
                this.label = label;
                this.children = children;
                
                for(CompilerUIOption child : children)
                    child.parent = this;
            }
        }
        
        private final class OptionsContentProvider implements ITreeContentProvider
        {   
            public Object[] getChildren(Object parentElement)
            {
                if(!(parentElement instanceof TreeEntry))
                    return BuilderUtil.EMPTY_ARRAY;
                return ((TreeEntry) parentElement).children;
            }

            public Object getParent(Object element)
            {
                if(!(element instanceof CompilerUIOption))
                    return null;
                return ((CompilerUIOption) element).parent;
            }

            public boolean hasChildren(Object element)
            {
                // Assume every tree entry has at least one element; if it
                // doesn't, the plus to expand it will just disappear when
                // the user clicks on it
                return element instanceof TreeEntry;
            }

            public Object[] getElements(Object inputElement)
            {
                return fEntries;
            }

            public void dispose()
            {
                // Nothing to do
            }

            public void inputChanged(Viewer viewer, Object oldInput,
                    Object newInput)
            {
                // This is handled externally
            }
        }
        
        private final ICellEditorListener fCellEditorListener =
            new ICellEditorListener()
            {
                public void applyEditorValue()
                {
                    validatePage();
                    updateLaunchConfigurationDialog();
                }

                public void cancelEditor()
                {
                    // Ignore
                }

                public void editorValueChanged(boolean oldValidState,
                        boolean newValidState)
                {
                    // Ignore; the user hasn't finished editing yet
                }
            };
        
        private TreeViewer fViewer;
        private ICompilerInterface fCurrentInterface;
        private TreeEntry[] fEntries;
        
        public void addToControl(Composite comp)
        {
            initializeInput(null);
            
            fViewer = new TreeViewer(comp, SWT.BORDER | SWT.FULL_SELECTION);
            fViewer.setContentProvider(new OptionsContentProvider());
            fViewer.getTree().setHeaderVisible(true);
            fViewer.getTree().setLinesVisible(true);
            
            TreeViewerColumn column = new TreeViewerColumn(fViewer, SWT.NONE);
            column.getColumn().setWidth(250);
            column.getColumn().setText(BuilderUIMessages.CompilerTab_column_option);
            column.setLabelProvider(new ColumnLabelProvider()
            {
                @Override
                public String getText(Object element)
                {
                    if(element instanceof TreeEntry)
                        return ((TreeEntry) element).label;
                    else
                        return ((CompilerUIOption) element).getOption().getLabel();
                }
            });
            
            column = new TreeViewerColumn(fViewer, SWT.NONE);
            column.getColumn().setWidth(175);
            column.getColumn().setText(BuilderUIMessages.CompilerTab_column_value);
            column.setLabelProvider(new ColumnLabelProvider()
            {
                @Override
                public Image getImage(Object element)
                {
                    if(element instanceof CheckboxUIOption)
                        return ((CheckboxUIOption) element).selected ?
                                fCheckedIcon : fUncheckedIcon;
                    else
                        return null;
                }
                
                @Override
                public String getText(Object element)
                {
                    if(element instanceof CompilerUIOption)
                        return ((CompilerUIOption) element).getText();
                    else
                        return ""; //$NON-NLS-1$
                }
            });
            column.setEditingSupport(new EditingSupport(fViewer)
            {
                @Override
                protected boolean canEdit(Object element)
                {
                    return element instanceof CompilerUIOption;
                }

                @Override
                protected CellEditor getCellEditor(Object element)
                {
                    if(element instanceof CompilerUIOption)
                        return ((CompilerUIOption) element).getCellEditor();
                    else
                        return null;
                }

                @Override
                protected Object getValue(Object element)
                {
                    if(element instanceof CompilerUIOption)
                        return ((CompilerUIOption) element).getValue();
                    else
                        return null;
                }

                @Override
                protected void setValue(Object element, Object value)
                {
                    if(element instanceof CompilerUIOption)
                        ((CompilerUIOption) element).setValue(value);
                    fViewer.update(element, null);
                }
            });
            
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 1;
            fViewer.getControl().setLayoutData(gd);
            
            Group helpGroup = new Group(comp, SWT.SHADOW_IN);
            gd = new GridData(GridData.FILL_VERTICAL);
            gd.horizontalSpan = 1;
            gd.widthHint = 225; // PERHAPS use PixelConverter or something...?
            helpGroup.setLayoutData(gd);
            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            helpGroup.setLayout(layout);
            
            final ScrolledFormText helpForm = new ScrolledFormText(helpGroup, true);
            final String NO_TEXT = "<form></form>"; //$NON-NLS-1$
            gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 1;
            helpForm.setLayoutData(gd);
            helpForm.setAlwaysShowScrollBars(true);
            helpForm.setExpandVertical(true);
            helpForm.setExpandHorizontal(false);
            helpForm.setText(NO_TEXT);
            
            fViewer.addSelectionChangedListener(new ISelectionChangedListener()
            {   
                public void selectionChanged(SelectionChangedEvent event)
                {   
                    TreePath[] paths = ((ITreeSelection) fViewer.getSelection()).getPaths();
                    if(0 == paths.length)
                    {
                        helpForm.setText(NO_TEXT);
                        return;
                    }
                    
                    Object selected = paths[0].getLastSegment();
                    if(!(selected instanceof CompilerUIOption))
                    {
                        helpForm.setText(NO_TEXT);
                        return;
                    }
                    
                    CompilerOption opt = ((CompilerUIOption) selected).getOption();
                    helpForm.setText(makeFormText(opt.getLabel(), opt.getHelpText()));
                }
                
                private String makeFormText(String label, String helpText)
                {
                    StringBuilder content = new StringBuilder();
                    content.append("<form><p><b>"); //$NON-NLS-1$
                    content.append(label);
                    content.append("</b></p><br></br>"); //$NON-NLS-1$
                    content.append(helpText);
                    content.append("</form>"); //$NON-NLS-1$
                    return content.toString();
                }
            });
            
            fViewer.setInput(this);
            fViewer.expandAll();
        }
        
        public void initializeInput(IVMInstall compiler)
        {
            ICompilerInterface compilerInterface = BuilderUtil.getCompilerInterface(compiler);
            if(compilerInterface == fCurrentInterface)
            {
                if(null == fEntries)
                    fEntries = new TreeEntry[] { };
                return;
            }
            else if(null == compilerInterface)
            {
                fEntries = new TreeEntry[] { };
                refreshViewer();
                return;
            }
            fCurrentInterface = compilerInterface;
            
            Map<String, List<CompilerUIOption>> groups = 
                new TreeMap<String, List<CompilerUIOption>>();
            for(CompilerOption opt : compilerInterface.getOptions())
            {
                String group = opt.getGroupLabel();
                if(!groups.containsKey(group))
                    groups.put(group, new ArrayList<CompilerUIOption>());
                groups.get(group).add(toUIOption(opt));
            }
            
            fEntries = new TreeEntry[groups.size()];
            int i = 0;
            for(String group : groups.keySet())
            {
                List<CompilerUIOption> list = groups.get(group);
                fEntries[i] = new TreeEntry(group, 
                        list.toArray(new CompilerUIOption[list.size()]));
                i++;
            }
            
            refreshViewer();
        }
        
        private void refreshViewer()
        {
            if(null != fViewer)
            {
                fViewer.refresh();
                fViewer.expandAll();
            }
        }
        
        private CompilerUIOption toUIOption(CompilerOption opt)
        {
            if(opt instanceof BooleanOption)
                return new CheckboxUIOption((BooleanOption) opt);
            else if(opt instanceof EnumOption)
                return new ComboUIOption((EnumOption) opt);
            else if(opt instanceof StringOption)
                return new TextUIOption((StringOption) opt);
            else
                throw new UnsupportedOperationException();
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            initializeInput(compilerSetting.getCompiler(config));
            for(TreeEntry group : fEntries)
            {
                for(CompilerUIOption uiOpt : group.children)
                {
                    CompilerOption opt = uiOpt.getOption();
                    String defaultValue = opt.getDefaultValue();
                    String value = BuilderUtil.getAttribute(config, opt.getAttributeId(), defaultValue);
                    uiOpt.initializeTo(value);
                    fViewer.update(uiOpt, null);
                }
            }
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            // TODO needed? initializeTree();
            for(TreeEntry group : fEntries)
            {
                for(CompilerUIOption uiOpt : group.children)
                {
                    CompilerOption opt = uiOpt.getOption();
                    config.setAttribute(opt.getAttributeId(), uiOpt.getStringValue());
                }
            }
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            initializeInput(compilerSetting.getDefault());
            for(TreeEntry group : fEntries)
            {
                for(CompilerUIOption uiOpt : group.children)
                {
                    CompilerOption opt = uiOpt.getOption();
                    config.setAttribute(opt.getAttributeId(), opt.getDefaultValue());
                }
            }
        }

        public String validate()
        {
            for(TreeEntry group : fEntries)
            {
                for(CompilerUIOption uiOpt : group.children)
                {
                    CompilerOption opt = uiOpt.getOption();
                    if(opt instanceof IValidatableOption)
                    {
                        String msg = ((IValidatableOption) opt).isValid(uiOpt.getStringValue());
                        if(null != msg)
                            return msg;
                    }
                }
            }
            return null;
        }
    }
    
    //--------------------------------------------------------------------------
    // Additional arguments
    
    private final class ArgumentsSetting implements ISetting
    {
        private Text fCompilerText;
        private Text fLinkerText;
        
        public final void addToControl(Composite comp)
        {   
            comp = new Composite(comp, SWT.NONE);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 2;
            comp.setLayoutData(gd);
            
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            comp.setLayout(layout);
            
            addLabel(comp, BuilderUIMessages.CompilerTab_label_additional_compiler_args);
            fCompilerText = addText(comp);
            
            addLabel(comp, BuilderUIMessages.CompilerTab_label_additional_linker_args);
            fLinkerText = addText(comp);
        }
        
        private Label addLabel(Composite comp, String str)
        {
            Label label = new Label(comp, SWT.NONE);
            label.setText(str);
            GridData gd = new GridData();
            gd.horizontalSpan = 1;
            label.setLayoutData(gd);
            return label;
        }
        
        private Text addText(Composite comp)
        {
            Text text = new Text(comp, SWT.SINGLE | SWT.BORDER);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 1;
            text.setLayoutData(gd);
            text.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent evt)
                {
                    validatePage();
                    updateLaunchConfigurationDialog();
                }
            });
            return text;
        }
        
        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(IDescentBuilderConstants.ATTR_ADDITIONAL_COMPILER_ARGS, ""); //$NON-NLS-1$
            config.setAttribute(IDescentBuilderConstants.ATTR_ADDITIONAL_LINKER_ARGS, ""); //$NON-NLS-1$
        }
        
        public void initializeFrom(ILaunchConfiguration config)
        {
            fCompilerText.setText(BuilderUtil.getAttribute(config, IDescentBuilderConstants.ATTR_ADDITIONAL_COMPILER_ARGS, "")); //$NON-NLS-1$
            fLinkerText.setText(BuilderUtil.getAttribute(config, IDescentBuilderConstants.ATTR_ADDITIONAL_LINKER_ARGS, "")); //$NON-NLS-1$
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(IDescentBuilderConstants.ATTR_ADDITIONAL_COMPILER_ARGS, fCompilerText.getText());
            config.setAttribute(IDescentBuilderConstants.ATTR_ADDITIONAL_LINKER_ARGS, fLinkerText.getText());
        }
        
        public String validate()
        {
            // Assume anything is valid... this setting is for advanced users who
            // should know what they're doing
            return null;
        }
    }
    
    //--------------------------------------------------------------------------
    // Resource management
    
    // This needs to be done at the tab level to allow for disposing)
    
    private Image fCheckedIcon = createImage("obj16/checked.png"); //$NON-NLS-1$
    private Image fUncheckedIcon = createImage("obj16/unchecked.png");  //$NON-NLS-1$
    
    public void dispose()
    {
        super.dispose();
        fCheckedIcon.dispose();
        fUncheckedIcon.dispose();
    }
    
    //--------------------------------------------------------------------------
    // Tab
    
    private final GeneralTab generalTab;
    
    private CompilerSetting compilerSetting;
    private CompilerOptions compilerOptions;
    
    public CompilerTab(GeneralTab generalTab)
    {
        this.generalTab = generalTab;
    }
    
    @Override
    protected String getIconPath()
    {
        return "obj16/builders.gif"; //$NON-NLS-1$
    }

    @Override
    protected ISetting[] getSettings()
    {
        compilerSetting = new CompilerSetting();
        compilerOptions = new CompilerOptions();
        return new ISetting[]
        {
            compilerSetting,
            compilerOptions,
            new ArgumentsSetting(),
        };
    }

    @Override
    protected Layout getTopLayout()
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        return layout;
    }

    public String getName()
    {
        return BuilderUIMessages.CompilerTab_tab_name;
    }
}
