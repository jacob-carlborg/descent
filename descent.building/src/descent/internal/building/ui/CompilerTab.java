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
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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

import descent.building.IDescentBuilderConstants;
import descent.building.compiler.BooleanOption;
import descent.building.compiler.CompilerOption;
import descent.building.compiler.EnumOption;
import descent.building.compiler.ICompilerInterface;
import descent.building.compiler.IValidatableOption;
import descent.building.compiler.StringOption;
import descent.internal.building.compiler.DmdCompilerInterface;
import descent.launching.IVMInstall;
import descent.launching.IVMInstallType;
import descent.launching.JavaRuntime;

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
            fHelpText.setText("Use the <a>Compilers preference page</a> to set up " +
            		"compiler/standard library configurations");
            gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 2;
            fHelpText.setLayoutData(gd);
            fHelpText.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    PreferencesUtil.createPreferenceDialogOn(getShell(),
                            "descent.debug.ui.preferences.VMPreferencePage",
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
                    compilerOptions.compilerModeChanged(selectedCompiler);
                    
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
            label.append(" (");
            label.append(compiler.getVMInstallType().getName());
            label.append(")");
            return label.toString();
        }
        
        public void initializeFrom(ILaunchConfiguration config)
        {
            String compilerTypeId = getAttribute(config, 
                    IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, "");
            String compilerId = getAttribute(config, 
                    IDescentBuilderConstants.ATTR_COMPILER_ID, "");
            
            for(int i = 0; i < fCompilers.length; i++)
            {
                IVMInstall compiler = fCompilers[i];
                if(compiler.getVMInstallType().getId().equals(compilerTypeId) &&
                        compiler.getId().equals(compilerId))
                {
                    fCombo.select(i);
                    return;
                }
            }
            
            if(fCompilers.length >= 0)
                fCombo.select(0);
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
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, "");
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_ID, "");
            }
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            initializeCompilers();
            if(fCompilers.length > 0)
            {
                IVMInstall first = fCompilers[0];
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, first.getVMInstallType().getId());
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_ID, first.getId());
            }
            else
            {
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, "");
                config.setAttribute(IDescentBuilderConstants.ATTR_COMPILER_ID, "");
            }
        }

        public String validate()
        {
            int selectedIndex = fCombo.getSelectionIndex();
            if(selectedIndex < 0)
                return "You must select a compiler to use";
            
            return null;
        }
    }
    
    private final class CompilerOptions implements ISetting
    {
        private abstract class CompilerUIOption
        {
            public TreeEntry parent;
            
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
            private CheckboxCellEditor editor;
            
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
                    editor = new CheckboxCellEditor(fViewer.getTree());
                
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
                return selected ? "true" : "false";
            }
        }
        
        private final class ComboUIOption extends CompilerUIOption
        {
            private EnumOption option;
            public Integer selected = Integer.valueOf(0);
            private ComboBoxCellEditor editor;
            
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
                    if(optionValues[i].equals(i))
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
        
        private final class TextUIOption extends CompilerUIOption
        {
            private StringOption option;
            public String selected = "";
            private TextCellEditor editor;
            
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
                    editor = new TextCellEditor(fViewer.getTree());
                
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
                    return EMPTY_ARRAY;
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
        
        private IVMInstall fSelectedCompiler;
        private TreeViewer fViewer;
        private TreeEntry[] fEntries;
        private Group fHelpGroup;
        private Label fHelpHeader;
        private Label fHelpText;
        
        public void addToControl(Composite comp)
        {
            initializeTree();
            
            fViewer = new TreeViewer(comp, SWT.BORDER | SWT.FULL_SELECTION);
            fViewer.setContentProvider(new OptionsContentProvider());
            fViewer.getTree().setHeaderVisible(true);
            fViewer.getTree().setLinesVisible(true);
            TreeViewerEditor.create(fViewer,
                    // Here comes the world's most unnecessarily long class name...
                    new ColumnViewerEditorActivationStrategy(fViewer),
                    ColumnViewerEditor.DEFAULT);
            
            TreeViewerColumn column = new TreeViewerColumn(fViewer, SWT.NONE);
            column.getColumn().setWidth(250);
            column.getColumn().setText("Option");
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
            column.getColumn().setText("Value");
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
                        return "";
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
            
            fHelpGroup = new Group(comp, SWT.SHADOW_IN);
            gd = new GridData(GridData.FILL_VERTICAL);
            gd.horizontalSpan = 1;
            gd.widthHint = 225; // PERHAPS use PixelConverter or something...?
            fHelpGroup.setLayoutData(gd);
            
            GridLayout groupLayout = new GridLayout();
            groupLayout.numColumns = 1;
            fHelpGroup.setLayout(groupLayout);
            
            fHelpHeader = new Label(fHelpGroup, SWT.LEFT);
            gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 1;
            fHelpHeader.setText("");
            fHelpHeader.setFont(getBoldFont(comp));
            fHelpHeader.setLayoutData(gd);
            
            fHelpText = new Label(fHelpGroup, SWT.LEFT | SWT.WRAP);
            gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 1;
            fHelpText.setText("");
            fHelpText.setLayoutData(gd);
            
            fViewer.addSelectionChangedListener(new ISelectionChangedListener()
            {
                public void selectionChanged(SelectionChangedEvent event)
                {   
                    TreePath[] paths = ((ITreeSelection) fViewer.getSelection()).getPaths();
                    if(0 == paths.length)
                    {
                        unsetHelp();
                        return;
                    }
                    
                    Object selected = paths[0].getLastSegment();
                    if(!(selected instanceof CompilerUIOption))
                    {
                        unsetHelp();
                        return;
                    }
                    
                    
                    fHelpHeader.setText(((CompilerUIOption) selected).getOption().
                            getLabel());
                    fHelpText.setText(((CompilerUIOption) selected).getOption().
                            getHelpText());
                    updateHelp();
                }
                
                private void unsetHelp()
                {
                    fHelpHeader.setText("");
                    fHelpText.setText("");
                    updateHelp();
                }
                
                private void updateHelp()
                {
                    fHelpHeader.update();
                    fHelpText.update();
                }
            });
            
            fViewer.setInput(this);
            fViewer.expandAll();
        }
        
        private void initializeTree()
        {
            if(null != fEntries)
                return;
            
            ICompilerInterface compilerInterface = 
                getCompilerInterface(fSelectedCompiler);
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
        }
        
        private CompilerUIOption toUIOption(CompilerOption opt)
        {
            if(opt instanceof BooleanOption)
                return new CheckboxUIOption((BooleanOption) opt);
            else if(opt instanceof StringOption)
                return new TextUIOption((StringOption) opt);
            else if(opt instanceof EnumOption)
                return new ComboUIOption((EnumOption) opt);
            else
                throw new UnsupportedOperationException();
        }
        
        private ICompilerInterface getCompilerInterface(IVMInstall compiler)
        {
            // TODO
            return new DmdCompilerInterface();
        }
        
        public void compilerModeChanged(IVMInstall compiler)
        {
            if(compiler == fSelectedCompiler)
                return;
            
            fSelectedCompiler = compiler;
            if(null != fViewer && null != fSelectedCompiler)
            {
                // TODO
            }
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            initializeTree();
            for(TreeEntry group : fEntries)
            {
                for(CompilerUIOption uiOpt : group.children)
                {
                    CompilerOption opt = uiOpt.getOption();
                    String defaultValue = opt.getDefaultValue();
                    String value = getAttribute(config, opt.getAttributeId(), defaultValue);
                    uiOpt.initializeTo(value);
                    fViewer.update(uiOpt, null);
                }
            }
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            initializeTree();
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
            initializeTree();
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
            
            addLabel(comp, "Additional compiler args:");
            fCompilerText = addText(comp);
            
            addLabel(comp, "Additional linker args:");
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
            config.setAttribute(IDescentBuilderConstants.ATTR_ADDITIONAL_COMPILER_ARGS, "");
            config.setAttribute(IDescentBuilderConstants.ATTR_ADDITIONAL_LINKER_ARGS, "");
        }
        
        public void initializeFrom(ILaunchConfiguration config)
        {
            fCompilerText.setText(getAttribute(config, IDescentBuilderConstants.ATTR_ADDITIONAL_COMPILER_ARGS, ""));
            fLinkerText.setText(getAttribute(config, IDescentBuilderConstants.ATTR_ADDITIONAL_LINKER_ARGS, ""));
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
    
    private Image fCheckedIcon = createImage("obj16/checked.png");
    private Image fUncheckedIcon = createImage("obj16/unchecked.png");
    private Font fBoldFont;
    
    private Font getBoldFont(Composite comp)
    {
        if(null == fBoldFont)
        {
            FontData[] fontData = comp.getFont().getFontData();
            for(FontData dataItem : fontData)
                dataItem.setStyle(dataItem.getStyle() | SWT.BOLD);
            fBoldFont = new Font(comp.getDisplay(), fontData);
        }
        return fBoldFont;
    }
    
    public void dispose()
    {
        super.dispose();
        fCheckedIcon.dispose();
        fUncheckedIcon.dispose();
        if(null != fBoldFont)
            fBoldFont.dispose();
    }
    
    //--------------------------------------------------------------------------
    // Tab
    
    private CompilerOptions compilerOptions;
    
    @Override
    protected String getIconPath()
    {
        return "obj16/builders.gif";
    }

    @Override
    protected ISetting[] getSettings()
    {
        compilerOptions = new CompilerOptions();
        return new ISetting[]
        {
            new CompilerSetting(),
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
        return "Compiler";
    }
}
