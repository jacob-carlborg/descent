/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     John Kaplan, johnkaplantech@gmail.com - 108071 [code templates] template for body of newly created class
 *******************************************************************************/
package descent.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

import descent.core.IBuffer;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaConventions;
import descent.core.JavaModelException;
import descent.core.ToolFactory;
import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.compiler.InvalidInputException;
import descent.core.dom.AST;
import descent.core.dom.ASTParser;
import descent.core.dom.CompilationUnit;
import descent.internal.corext.codemanipulation.StubUtility;
import descent.internal.corext.dom.TokenScanner;
import descent.internal.corext.template.java.JavaContext;
import descent.internal.corext.util.JavaModelUtil;
import descent.internal.corext.util.Messages;
import descent.internal.corext.util.Resources;
import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.dialogs.StatusInfo;
import descent.internal.ui.dialogs.TextFieldNavigationHandler;
import descent.internal.ui.preferences.CodeTemplatePreferencePage;
import descent.internal.ui.refactoring.contentassist.ControlContentAssistHelper;
import descent.internal.ui.refactoring.contentassist.JavaPackageCompletionProcessor;
import descent.internal.ui.wizards.NewWizardMessages;
import descent.internal.ui.wizards.dialogfields.DialogField;
import descent.internal.ui.wizards.dialogfields.IDialogFieldListener;
import descent.internal.ui.wizards.dialogfields.IListAdapter;
import descent.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import descent.internal.ui.wizards.dialogfields.LayoutUtil;
import descent.internal.ui.wizards.dialogfields.ListDialogField;
import descent.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import descent.internal.ui.wizards.dialogfields.Separator;
import descent.internal.ui.wizards.dialogfields.StringButtonStatusDialogField;
import descent.internal.ui.wizards.dialogfields.StringDialogField;
import descent.ui.CodeGeneration;
import descent.ui.JavaElementLabelProvider;

/**
 * The class <code>NewModuleWizardPage</code> contains controls and validation routines 
 * for a 'New Module WizardPage'.
 */
public class NewModuleWizardPage extends NewContainerWizardPage {

	private final static String PAGE_NAME= "NewModuleWizardPage"; //$NON-NLS-1$
		
	/** Field ID of the package input field. */
	protected final static String PACKAGE= PAGE_NAME + ".package";	 //$NON-NLS-1$
	/** Field ID of the type name input field. */	
	protected final static String MODULENAME= PAGE_NAME + ".modulename"; //$NON-NLS-1$
	/** Field ID of the method stubs check boxes. */
	protected final static String METHODS= PAGE_NAME + ".methods"; //$NON-NLS-1$

	private StringButtonStatusDialogField fPackageDialogField;
		
	private boolean fCanModifyPackage;
	
	private IPackageFragment fCurrPackage;
	/**
	 * a handle to the type to be created (does usually not exist, can be null)
	 */
	private StringDialogField fTypeNameDialogField;
	
	private SelectionButtonDialogField fAddCommentButton;
	private boolean fUseAddCommentButtonValue; // used for compatibility: Wizards that don't show the comment button control
	// will use the preferences settings
	
	private ICompilationUnit fCreatedCompilationUnit;
	
	private JavaPackageCompletionProcessor fCurrPackageCompletionProcessor;
	
	protected IStatus fPackageStatus;
	protected IStatus fTypeNameStatus;
	
	/**
	 * Creates a new <code>NewTypeWizardPage</code>.
	 * 
	 * @param typeKind Signals the kind of the type to be created. Valid kinds are
	 * {@link #CLASS_TYPE}, {@link #INTERFACE_TYPE}, {@link #ENUM_TYPE} and {@link #ANNOTATION_TYPE}
	 * @param pageName the wizard page's name
	 * @since 3.1
	 */
	public NewModuleWizardPage() {
	    super(PAGE_NAME);

	    fCreatedCompilationUnit= null;
		
		TypeFieldsAdapter adapter= new TypeFieldsAdapter();
		
		fPackageDialogField= new StringButtonStatusDialogField(adapter);
		fPackageDialogField.setDialogFieldListener(adapter);
		fPackageDialogField.setLabelText(getPackageLabel()); 
		fPackageDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_package_button); 
		fPackageDialogField.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default); 
				
		fTypeNameDialogField= new StringDialogField();
		fTypeNameDialogField.setDialogFieldListener(adapter);
		fTypeNameDialogField.setLabelText(getTypeNameLabel());
		
		fAddCommentButton= new SelectionButtonDialogField(SWT.CHECK);
		fAddCommentButton.setLabelText(NewWizardMessages.NewTypeWizardPage_addcomment_label); 
		
		fUseAddCommentButtonValue= false; // only used when enabled
		
		fCurrPackageCompletionProcessor= new JavaPackageCompletionProcessor();
		
		fPackageStatus= new StatusInfo();
		
		fCanModifyPackage= true;
		updateEnableState();
					
		fTypeNameStatus= new StatusInfo();
		
		setTitle(NewWizardMessages.NewModuleWizardPage_title); 
		setDescription(NewWizardMessages.NewModuleWizardPage_description); 
	}
	
// -------- Initialization ---------
	
	/**
	 * The wizard owning this page is responsible for calling this method with the
	 * current selection. The selection is used to initialize the fields of the wizard 
	 * page.
	 * 
	 * @param selection used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		IJavaElement jelem= getInitialJavaElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();
	}
	
//	 ------ validation --------
	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status= new IStatus[] {
			fContainerStatus,
			fPackageStatus,
			fTypeNameStatus,
		};
		
		// the mode severe status will be displayed and the OK button enabled/disabled.
		updateStatus(status);
	}
	
	/**
	 * Initializes all fields provided by the page with a given selection.
	 * 
	 * @param elem the selection used to initialize this page or <code>
	 * null</code> if no selection was available
	 */
	protected void initTypePage(IJavaElement elem) {
		IJavaProject project= null;
		IPackageFragment pack= null;
				
		if (elem != null) {
			// evaluate the enclosing type
			project= elem.getJavaProject();
			pack= (IPackageFragment) elem.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
		}
		
		String typeName= ""; //$NON-NLS-1$
		
		ITextSelection selection= getCurrentTextSelection();
		if (selection != null) {
			String text= selection.getText();
			if (text != null && JavaConventions.validateJavaTypeName(text).isOK()) {
				typeName= text;
			}
		}

		setPackageFragment(pack, true);	
		setTypeName(typeName, true);		
		setAddComments(StubUtility.doAddComments(project), true); // from project or workspace
	}		
	
	// -------- UI Creation ---------
	
	/**
	 * Returns the label that is used for the package input field.
	 * 
	 * @return the label that is used for the package input field.
	 * @since 3.2
	 */
	protected String getPackageLabel() {
		return NewWizardMessages.NewTypeWizardPage_package_label;
	}
	
	/**
	 * Returns the label that is used for the type name input field.
	 * 
	 * @return the label that is used for the type name input field.
	 * @since 3.2
	 */
	protected String getTypeNameLabel() {
		return NewWizardMessages.NewTypeWizardPage_typename_label;
	}
	
	/**
	 * Creates a separator line. Expects a <code>GridLayout</code> with at least 1 column.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createSeparator(Composite composite, int nColumns) {
		(new Separator(SWT.SEPARATOR | SWT.HORIZONTAL)).doFillIntoGrid(composite, nColumns, convertHeightInCharsToPixels(1));		
	}

	/**
	 * Creates the controls for the package name field. Expects a <code>GridLayout</code> with at 
	 * least 4 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */	
	protected void createPackageControls(Composite composite, int nColumns) {
		fPackageDialogField.doFillIntoGrid(composite, nColumns);
		Text text= fPackageDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());	
		LayoutUtil.setHorizontalGrabbing(text);
		ControlContentAssistHelper.createTextContentAssistant(text, fCurrPackageCompletionProcessor);
		TextFieldNavigationHandler.install(text);
	}

	/**
	 * Creates the controls for the type name field. Expects a <code>GridLayout</code> with at 
	 * least 2 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createTypeNameControls(Composite composite, int nColumns) {
		fTypeNameDialogField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);
		
		Text text= fTypeNameDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		TextFieldNavigationHandler.install(text);
	}
	
	/**
	 * Creates the controls for the preference page links. Expects a <code>GridLayout</code> with 
	 * at least 3 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 * 
	 * @since 3.1
	 */			
	protected void createCommentControls(Composite composite, int nColumns) {
    	Link link= new Link(composite, SWT.NONE);
    	link.setText(NewWizardMessages.NewTypeWizardPage_addcomment_description);
    	link.addSelectionListener(new TypeFieldsAdapter());
    	link.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, nColumns, 1));
		DialogField.createEmptySpace(composite);
		fAddCommentButton.doFillIntoGrid(composite, nColumns - 1);
	}

	/*
	 * @see WizardPage#becomesVisible
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setFocus();
		}
	}	
	
	/**
	 * Sets the focus on the type name input field.
	 */		
	protected void setFocus() {
		fTypeNameDialogField.setFocus();
	}
				
	// -------- TypeFieldsAdapter --------

	private class TypeFieldsAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter, SelectionListener {
		
		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			typePageChangeControlPressed(field);
		}
		
		// -------- IListAdapter
		public void customButtonPressed(ListDialogField field, int index) {
			
		}
		
		public void selectionChanged(ListDialogField field) {}
		
		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			typePageDialogFieldChanged(field);
		}
		
		public void doubleClicked(ListDialogField field) {
		}


		public void widgetSelected(SelectionEvent e) {
			typePageLinkActivated(e);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			typePageLinkActivated(e);
		}
	}
	
	private void typePageLinkActivated(SelectionEvent e) {
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root != null) {
			PreferenceDialog dialog= PreferencesUtil.createPropertyDialogOn(getShell(), root.getJavaProject().getProject(), CodeTemplatePreferencePage.PROP_ID, null, null);
			dialog.open();
		} else {
			String title= NewWizardMessages.NewTypeWizardPage_configure_templates_title; 
			String message= NewWizardMessages.NewTypeWizardPage_configure_templates_message; 
			MessageDialog.openInformation(getShell(), title, message);
		}
	}
	
	private void typePageChangeControlPressed(DialogField field) {
		if (field == fPackageDialogField) {
			IPackageFragment pack= choosePackage();	
			if (pack != null) {
				fPackageDialogField.setText(pack.getElementName());
			}
		}
	}
	
	/*
	 * A field on the type has changed. The fields' status and all dependent
	 * status are updated.
	 */
	private void typePageDialogFieldChanged(DialogField field) {
		String fieldName= null;
		if (field == fPackageDialogField) {
			fPackageStatus= packageChanged();
			updatePackageStatusLabel();
			fTypeNameStatus= typeNameChanged();	
			fieldName= PACKAGE;
		} else if (field == fTypeNameDialogField) {
			fTypeNameStatus= typeNameChanged();
			fieldName= MODULENAME;
		} else {
			fieldName= METHODS;
		}
		// tell all others
		handleFieldChanged(fieldName);
	}		
	
	// -------- update message ----------------		
	
	/*
	 * @see descent.ui.wizards.NewContainerWizardPage#handleFieldChanged(String)
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		if (fieldName == CONTAINER) {
			fPackageStatus= packageChanged();
			fTypeNameStatus= typeNameChanged();
		}
		
		doStatusUpdate();
	}
	
	// ---- set / get ----------------
	
	/**
	 * Returns the text of the package input field.
	 * 
	 * @return the text of the package input field
	 */
	public String getPackageText() {
		return fPackageDialogField.getText();
	}
	
	
	/**
	 * Returns the package fragment corresponding to the current input.
	 * 
	 * @return a package fragment or <code>null</code> if the input 
	 * could not be resolved.
	 */
	public IPackageFragment getPackageFragment() {
		return fCurrPackage;
	}
	
	/**
	 * Sets the package fragment to the given value. The method updates the model 
	 * and the text of the control.
	 * 
	 * @param pack the package fragment to be set
	 * @param canBeModified if <code>true</code> the package fragment is
	 * editable; otherwise it is read-only.
	 */
	public void setPackageFragment(IPackageFragment pack, boolean canBeModified) {
		fCurrPackage= pack;
		fCanModifyPackage= canBeModified;
		String str= (pack == null) ? "" : pack.getElementName(); //$NON-NLS-1$
		fPackageDialogField.setText(str);
		updateEnableState();
	}
	
	/**
	 * Returns the type name entered into the type input field.
	 * 
	 * @return the type name
	 */
	public String getModuleName() {
		return fTypeNameDialogField.getText();
	}

	/**
	 * Sets the type name input field's text to the given value. Method doesn't update
	 * the model.
	 * 
	 * @param name the new type name
	 * @param canBeModified if <code>true</code> the type name field is
	 * editable; otherwise it is read-only.
	 */	
	public void setTypeName(String name, boolean canBeModified) {
		fTypeNameDialogField.setText(name);
		fTypeNameDialogField.setEnabled(canBeModified);
	}	
	
	/**
	 * Sets 'Add comment' checkbox. The value set will only be used when creating source when
	 * the comment control is enabled (see {@link #enableCommentControl(boolean)}
	 * 
	 * @param doAddComments if <code>true</code>, comments are added.
	 * @param canBeModified if <code>true</code> check box is
	 * editable; otherwise it is read-only.
	 * 	@since 3.1
	 */	
	public void setAddComments(boolean doAddComments, boolean canBeModified) {
		fAddCommentButton.setSelection(doAddComments);
		fAddCommentButton.setEnabled(canBeModified);
	}
	
	/**
	 * Sets to use the 'Add comment' checkbox value. Clients that use the 'Add comment' checkbox
	 * additionally have to enable the control. This has been added for backwards compatibility.
	 * 
	 * @param useAddCommentValue if <code>true</code>, 
	 * 	@since 3.1
	 */	
	public void enableCommentControl(boolean useAddCommentValue) {
		fUseAddCommentButtonValue= useAddCommentValue;
	}
	
	
	/**
	 * Returns if comments are added. This method can be overridden by clients.
	 * The selection of the comment control is taken if enabled (see {@link #enableCommentControl(boolean)}, otherwise
	 * the settings as specified in the preferences is used.
	 * 
	 * @return Returns <code>true</code> if comments can be added
	 * @since 3.1
	 */	
	public boolean isAddComments() {
		if (fUseAddCommentButtonValue) {
			return fAddCommentButton.isSelected();
		}
		IPackageFragmentRoot root= getPackageFragmentRoot();
		IJavaProject project= (root != null) ? root.getJavaProject() : null; // use project settings 
		return StubUtility.doAddComments(project); 
	}
			
	/**
	 * Returns the resource handle that corresponds to the compilation unit to was or
	 * will be created or modified.
	 * @return A resource or null if the page contains illegal values.
	 * @since 3.0
	 */
	public IResource getModifiedResource() {
		IPackageFragment pack= getPackageFragment();
		if (pack != null) {
			String cuName= getCompilationUnitName(getModuleName());
			return pack.getCompilationUnit(cuName).getResource();
		}
		return null;
	}
			
	// ----------- validation ----------
			
	/*
	 * @see descent.ui.wizards.NewContainerWizardPage#containerChanged()
	 */
	protected IStatus containerChanged() {
		IStatus status= super.containerChanged();
		fCurrPackageCompletionProcessor.setPackageFragmentRoot(getPackageFragmentRoot());
		return status;
	}
	
	/**
	 * A hook method that gets called when the package field has changed. The method 
	 * validates the package name and returns the status of the validation. The validation
	 * also updates the package fragment model.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus packageChanged() {
		StatusInfo status= new StatusInfo();
		fPackageDialogField.enableButton(getPackageFragmentRoot() != null);
		
		String packName= getPackageText();
		if (packName.length() > 0) {
			IStatus val= JavaConventions.validatePackageName(packName);
			if (val.getSeverity() == IStatus.ERROR) {
				status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidPackageName, val.getMessage())); 
				return status;
			} else if (val.getSeverity() == IStatus.WARNING) {
				status.setWarning(Messages.format(NewWizardMessages.NewTypeWizardPage_warning_DiscouragedPackageName, val.getMessage())); 
				// continue
			}
		} else {
			//status.setWarning(NewWizardMessages.NewTypeWizardPage_warning_DefaultPackageDiscouraged); 
		}
		
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root != null) {
			if (root.getJavaProject().exists() && packName.length() > 0) {
				try {
					IPath rootPath= root.getPath();
					IPath outputPath= root.getJavaProject().getOutputLocation();
					if (rootPath.isPrefixOf(outputPath) && !rootPath.equals(outputPath)) {
						// if the bin folder is inside of our root, don't allow to name a package
						// like the bin folder
						IPath packagePath= rootPath.append(packName.replace('.', '/'));
						if (outputPath.isPrefixOf(packagePath)) {
							status.setError(NewWizardMessages.NewTypeWizardPage_error_ClashOutputLocation); 
							return status;
						}
					}
				} catch (JavaModelException e) {
					JavaPlugin.log(e);
					// let pass			
				}
			}
			
			fCurrPackage= root.getPackageFragment(packName);
		} else {
			status.setError(""); //$NON-NLS-1$
		}
		return status;
	}

	/*
	 * Updates the 'default' label next to the package field.
	 */	
	private void updatePackageStatusLabel() {
		String packName= getPackageText();
		
		if (packName.length() == 0) {
			fPackageDialogField.setStatus(NewWizardMessages.NewTypeWizardPage_default); 
		} else {
			fPackageDialogField.setStatus(""); //$NON-NLS-1$
		}
	}
	
	/*
	 * Updates the enable state of buttons related to the enclosing type selection checkbox.
	 */
	private void updateEnableState() {
		fPackageDialogField.setEnabled(fCanModifyPackage);
	}
	
	/**
	 * Hook method that is called when evaluating the name of the compilation unit to create. By default, a file extension
	 * <code>java</code> is added to the given type name, but implementors can override this behavior.
	 * 
	 * @param typeName the name of the type to create the compilation unit for.
	 * @return the name of the compilation unit to be created for the given name
	 * 
	 * @since 3.2
	 */
	protected String getCompilationUnitName(String typeName) {
		return typeName + JavaModelUtil.DEFAULT_CU_SUFFIX;
	}
	
	
	/**
	 * Hook method that gets called when the type name has changed. The method validates the 
	 * type name and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus typeNameChanged() {
		StatusInfo status= new StatusInfo();
		String typeNameWithParameters= getModuleName();
		// must not be empty
		if (typeNameWithParameters.length() == 0) {
			status.setError(NewWizardMessages.NewModuleWizardPage_error_EnterModuleName); 
			return status;
		}
		
		String typeName= getModuleName();
		if (typeName.indexOf('.') != -1) {
			status.setError(NewWizardMessages.NewModuleWizardPage_error_QualifiedName); 
			return status;
		}
		IStatus val= JavaConventions.validateJavaTypeName(typeName);
		if (val.getSeverity() == IStatus.ERROR) {
			status.setError(Messages.format(NewWizardMessages.NewModuleWizardPage_error_InvalidModuleName, val.getMessage())); 
			return status;
		} else if (val.getSeverity() == IStatus.WARNING) {
			//status.setWarning(Messages.format(NewWizardMessages.NewModuleWizardPage_warning_TypeNameDiscouraged, val.getMessage())); 
			// continue checking
		}		

		// must not exist
		IPackageFragment pack= getPackageFragment();
		if (pack != null) {
			ICompilationUnit cu= pack.getCompilationUnit(getCompilationUnitName(typeName));
			IResource resource= cu.getResource();

			if (resource.exists()) {
				status.setError(NewWizardMessages.NewModuleWizardPage_error_ModuleNameExists); 
				return status;
			}
			URI location= resource.getLocationURI();
			if (location != null) {
				try {
					IFileStore store= EFS.getStore(location);
					if (store.fetchInfo().exists()) {
						status.setError(NewWizardMessages.NewModuleWizardPage_error_ModuleNameExistsDifferentCase); 
						return status;
					}
				} catch (CoreException e) {
					status.setError(Messages.format(
						NewWizardMessages.NewModuleWizardPage_error_uri_location_unkown, 
						Resources.getLocationString(resource)));
				}
			}
		}
		
		/*
		if (typeNameWithParameters != typeName) {
			IPackageFragmentRoot root= getPackageFragmentRoot();
			if (root != null) {
				if (!JavaModelUtil.is50OrHigher(root.getJavaProject())) {
					status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeParameters); 
					return status;
				}
				String typeDeclaration= "class " + typeNameWithParameters + " {}"; //$NON-NLS-1$//$NON-NLS-2$
				ASTParser parser= ASTParser.newParser(AST.D2);
				parser.setSource(typeDeclaration.toCharArray());
				parser.setProject(root.getJavaProject());
				CompilationUnit compilationUnit= (CompilationUnit) parser.createAST(null);
				IProblem[] problems= compilationUnit.getProblems();
				if (problems.length > 0) {
					status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidTypeName, problems[0].getMessage())); 
					return status;
				}
			}
		}
		*/
		return status;
	}
	
	// selection dialogs

	/**
	 * Opens a selection dialog that allows to select a package. 
	 * 
	 * @return returns the selected package or <code>null</code> if the dialog has been canceled.
	 * The caller typically sets the result to the package input field.
	 * <p>
	 * Clients can override this method if they want to offer a different dialog.
	 * </p>
	 * 
	 * @since 3.2
	 */
	protected IPackageFragment choosePackage() {
		IPackageFragmentRoot froot= getPackageFragmentRoot();
		IJavaElement[] packages= null;
		try {
			if (froot != null && froot.exists()) {
				packages= froot.getChildren();
			}
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
		if (packages == null) {
			packages= new IJavaElement[0];
		}
		
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setIgnoreCase(false);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_title); 
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_description); 
		dialog.setEmptyListMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_empty); 
		dialog.setElements(packages);
		dialog.setHelpAvailable(false);
		
		IPackageFragment pack= getPackageFragment();
		if (pack != null) {
			dialog.setInitialSelections(new Object[] { pack });
		}

		if (dialog.open() == Window.OK) {
			return (IPackageFragment) dialog.getFirstResult();
		}
		return null;
	}
	
		
	// ---- creation ----------------

	/**
	 * Creates the new compilation unit using the entered field values.
	 * 
	 * @param monitor a progress monitor to report progress.
	 * @throws CoreException Thrown when the creation failed.
	 * @throws InterruptedException Thrown when the operation was canceled.
	 */
	public void createCompilationUnit(IProgressMonitor monitor) throws CoreException, InterruptedException {		
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}

		monitor.beginTask(NewWizardMessages.NewModuleWizardPage_operationdesc, 8); 
		
		IPackageFragmentRoot root= getPackageFragmentRoot();
		IPackageFragment pack= getPackageFragment();
		if (pack == null) {
			pack= root.getPackageFragment(""); //$NON-NLS-1$
		}
		
		if (!pack.exists()) {
			String packName= pack.getElementName();
			pack= root.createPackageFragment(packName, true, new SubProgressMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}
		
		boolean needsSave;
		ICompilationUnit connectedCU= null;
		
		try {	
			String moduleName= getModuleName();
			
			String lineDelimiter= null;	
			lineDelimiter= StubUtility.getLineDelimiterUsed(pack.getJavaProject());
			
			String cuName= getCompilationUnitName(moduleName);
			connectedCU= pack.createCompilationUnit(cuName, "", false, new SubProgressMonitor(monitor, 2)); //$NON-NLS-1$
			// create a working copy with a new owner
			
			needsSave= true;
			connectedCU.becomeWorkingCopy(null, new SubProgressMonitor(monitor, 1)); // cu is now a (primary) working copy
			
			IBuffer buffer= connectedCU.getBuffer();
			
			String cuContent= constructCUContent(connectedCU, moduleName, lineDelimiter);
			buffer.setContents(cuContent);
			
			JavaModelUtil.reconcile(connectedCU);
			
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
			
			IBuffer buf= connectedCU.getBuffer();
			
			String fileComment= getFileComment(connectedCU, lineDelimiter);
			if (fileComment != null && fileComment.length() > 0) {
				buf.replace(0, 0, fileComment + lineDelimiter);
			}
			fCreatedCompilationUnit= connectedCU;

			if (needsSave) {
				connectedCU.commitWorkingCopy(true, new SubProgressMonitor(monitor, 1));
			} else {
				monitor.worked(1);
			}
			
		} finally {
			if (connectedCU != null) {
				connectedCU.discardWorkingCopy();
			}
			monitor.done();
		}
	}

	/**
	 * Uses the New Java file template from the code template page to generate a
	 * compilation unit with the given type content.
	 * @param cu The new created compilation unit
	 * @param typeContent The content of the type, including signature and type
	 * body.
	 * @param lineDelimiter The line delimiter to be used.
	 * @return String Returns the result of evaluating the new file template
	 * with the given type content.
	 * @throws CoreException
	 * @since 2.1
	 */
	protected String constructCUContent(ICompilationUnit cu, String moduleName, String lineDelimiter) throws CoreException {
		String fileComment= getFileComment(cu, lineDelimiter);
		IPackageFragment pack= (IPackageFragment) cu.getParent();
		String content= CodeGeneration.getCompilationUnitContent(cu, fileComment, lineDelimiter);
		if (content != null) {
			ASTParser parser= ASTParser.newParser(AST.D2);
			parser.setProject(cu.getJavaProject());
			parser.setSource(content.toCharArray());
			CompilationUnit unit= (CompilationUnit) parser.createAST(null);
			if ((pack.isDefaultPackage() || unit.getModuleDeclaration() != null) && !unit.declarations().isEmpty()) {
				return content;
			}
		}
		StringBuffer buf= new StringBuffer();
		buf.append("module "); //$NON-NLS-1$
		if (!pack.isDefaultPackage()) {
			buf.append(pack.getElementName());
			buf.append('.');
		}
		buf.append(moduleName);
		buf.append(';');
		buf.append(lineDelimiter).append(lineDelimiter);
		return buf.toString();
	}
	

	/**
	 * Returns the created type or <code>null</code> is the type has not been created yet. The method
	 * only returns a valid type after <code>createType</code> has been called.
	 * 
	 * @return the created type
	 * @see #createCompilationUnit(IProgressMonitor)
	 */			
	public ICompilationUnit getCreatedCompilationUnit() {
		return fCreatedCompilationUnit;
	}
	
	// ---- construct CU body----------------
	
	/**
	 * Hook method that gets called from <code>createType</code> to retrieve 
	 * a file comment. This default implementation returns the content of the 
	 * 'file comment' template or <code>null</code> if no comment should be created.
	 * 
	 * @param parentCU the parent compilation unit
	 * @param lineDelimiter the line delimiter to use
	 * @return the file comment or <code>null</code> if a file comment 
	 * is not desired
	 * @throws CoreException 
     *
     * @since 3.1
	 */		
	protected String getFileComment(ICompilationUnit parentCU, String lineDelimiter) throws CoreException {
		if (isAddComments()) {
			return CodeGeneration.getFileComment(parentCU, lineDelimiter);
		}
		return null;
		
	}
	
	private boolean isValidComment(String template) {
		IScanner scanner= ToolFactory.createScanner(true, false, false, false);
		scanner.setSource(template.toCharArray());
		try {
			int next= scanner.getNextToken();
			while (TokenScanner.isComment(next)) {
				next= scanner.getNextToken();
			}
			return next == ITerminalSymbols.TokenNameEOF;
		} catch (InvalidInputException e) {
		}
		return false;
	}
	
	/**
	 * Hook method that gets called from <code>createType</code> to retrieve 
	 * a type comment. This default implementation returns the content of the 
	 * 'type comment' template.
	 * 
	 * @param parentCU the parent compilation unit
	 * @param lineDelimiter the line delimiter to use
	 * @return the type comment or <code>null</code> if a type comment 
	 * is not desired
     *
     * @since 3.0
	 */		
	protected String getTypeComment(ICompilationUnit parentCU, String lineDelimiter) {
		if (isAddComments()) {
			try {
				StringBuffer typeName= new StringBuffer();
				typeName.append(getModuleName());
				String[] typeParamNames= new String[0];
				String comment= CodeGeneration.getTypeComment(parentCU, typeName.toString(), typeParamNames, lineDelimiter);
				if (comment != null && isValidComment(comment)) {
					return comment;
				}
			} catch (CoreException e) {
				JavaPlugin.log(e);
			}
		}
		return null;
	}

	/**
	 * @deprecated Use getTypeComment(ICompilationUnit, String)
	 */
	protected String getTypeComment(ICompilationUnit parentCU) {
		if (StubUtility.doAddComments(parentCU.getJavaProject()))
			return getTypeComment(parentCU, StubUtility.getLineDelimiterUsed(parentCU));
		return null;
	}

	/**
	 * @deprecated Use getTemplate(String,ICompilationUnit,int)
	 */
	protected String getTemplate(String name, ICompilationUnit parentCU) {
		return getTemplate(name, parentCU, 0);
	}
		
	
	/**
	 * Returns the string resulting from evaluation the given template in
	 * the context of the given compilation unit. This accesses the normal
	 * template page, not the code templates. To use code templates use
	 * <code>constructCUContent</code> to construct a compilation unit stub or
	 * getTypeComment for the comment of the type.
	 * 
	 * @param name the template to be evaluated
	 * @param parentCU the templates evaluation context
	 * @param pos a source offset into the parent compilation unit. The
	 * template is evaluated at the given source offset
	 */
	protected String getTemplate(String name, ICompilationUnit parentCU, int pos) {
		try {
			Template template= JavaPlugin.getDefault().getTemplateStore().findTemplate(name);
			if (template != null) {
				return JavaContext.evaluateTemplate(template, parentCU, pos);
			}
		} catch (CoreException e) {
			JavaPlugin.log(e);
		} catch (BadLocationException e) {
			JavaPlugin.log(e);
		} catch (TemplateException e) {
			JavaPlugin.log(e);
		}
		return null;
	}

	
	// ---- creation ----------------

	/**
	 * Returns the runnable that creates the type using the current settings.
	 * The returned runnable must be executed in the UI thread.
	 * 
	 * @return the runnable to create the new type
	 */		
	public IRunnableWithProgress getRunnable() {				
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					if (monitor == null) {
						monitor= new NullProgressMonitor();
					}
					createCompilationUnit(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} 				
			}
		};
	}
	
	//
	
	/*
	 * @see WizardPage#createControl
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		
		int nColumns= 4;
		
		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;		
		composite.setLayout(layout);
		
		// pick & choose the wanted UI components
		
		createContainerControls(composite, nColumns);	
		createPackageControls(composite, nColumns);
				
		createSeparator(composite, nColumns);
		
		createTypeNameControls(composite, nColumns);
		//createMethodStubSelectionControls(composite, nColumns);
		
		createSeparator(composite, nColumns);
		
		createCommentControls(composite, nColumns);
		enableCommentControl(true);
		
		setControl(composite);
			
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);	
	}
	
}
