package descent.internal.building.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

import descent.building.IDescentBuilderConstants;
import descent.launching.IVMInstall;
import descent.launching.IVMInstallType;
import descent.launching.JavaRuntime;

/* package */ final class CompilerTab extends AbstractBuilderTab
{
    //--------------------------------------------------------------------------
    // Compiler selection
    
    private final class CompilerSetting implements ISetting
    {
        private Group fGroup;
        private Link fHelpText;
        private Combo fCombo;
        private IVMInstall[] fCompilers;
        
        public void addToControl(Composite comp)
        {
            fGroup = createGroup(comp, "Compiler Selection", 1, 1);
            
            fHelpText = new Link(fGroup, SWT.LEFT | SWT.WRAP);
            fHelpText.setText("Select the compiler/standard library set to use " +
            		"for this build configuration. Use the <a>Compilers " +
            		"preference page</a> to set up compiler/standard library " +
            		"configurations.");
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 1;
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
            
            createSpacer(fGroup, 1);
            
            fCombo = new Combo(fGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
            gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
            gd.horizontalSpan = 1;
            gd.grabExcessHorizontalSpace = true;
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
            String compilerTypeId = "";
            try
            {
                compilerTypeId = config.getAttribute(IDescentBuilderConstants.ATTR_COMPILER_TYPE_ID, "");
            }
            catch(CoreException e) { }
            
            String compilerId = "";
            try
            {
                compilerId = config.getAttribute(IDescentBuilderConstants.ATTR_COMPILER_ID, "");
            }
            catch(CoreException e) { }
            
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

        public void validate()
        {
            int selectedIndex = fCombo.getSelectionIndex();
            if(selectedIndex < 0)
            {
                setErrorMessage("You must select a compiler to use");
                return;
            }
        }
    }
    
    public class CompilerOptions implements ISetting
    {
        private Group fGroup;
        private IVMInstall fSelectedCompiler;
        
        public void addToControl(Composite comp)
        {
            fGroup = createGroup(comp, "Compiler Options", 1, 1);
            
            createSpacer(fGroup, 1);
            
            // TODO make the list
        }
        
        public void compilerModeChanged(IVMInstall compiler)
        {
            if(compiler == fSelectedCompiler)
                return;
            
            fSelectedCompiler = compiler;
            // TODO update the displayed options
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            // TODO Auto-generated method stub
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            // TODO Auto-generated method stub
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            // TODO Auto-generated method stub
        }

        public void validate()
        {
            // TODO Auto-generated method stub
        }
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
        return "Compiler";
    }
}
