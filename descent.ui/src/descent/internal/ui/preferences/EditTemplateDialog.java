/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IUpdate;

import descent.ui.IContextMenuConstants;
import descent.ui.PreferenceConstants;
import descent.ui.text.IJavaPartitions;
import descent.ui.text.JavaTextTools;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.dialogs.StatusInfo;
import descent.internal.ui.javaeditor.JavaSourceViewer;
import descent.internal.ui.text.template.preferences.TemplateVariableProcessor;

/**
 * Dialog to edit a template.
 */
public class EditTemplateDialog extends StatusDialog {

	private static class TextViewerAction extends Action implements IUpdate {
	
		private int fOperationCode= -1;
		private ITextOperationTarget fOperationTarget;
	
		/** 
		 * Creates a new action.
		 * 
		 * @param viewer the viewer
		 * @param operationCode the opcode
		 */
		public TextViewerAction(ITextViewer viewer, int operationCode) {
			fOperationCode= operationCode;
			fOperationTarget= viewer.getTextOperationTarget();
			update();
		}
	
		/**
		 * Updates the enabled state of the action.
		 * Fires a property change if the enabled state changes.
		 * 
		 * @see Action#firePropertyChange(String, Object, Object)
		 */
		public void update() {
	
			boolean wasEnabled= isEnabled();
			boolean isEnabled= (fOperationTarget != null && fOperationTarget.canDoOperation(fOperationCode));
			setEnabled(isEnabled);
	
			if (wasEnabled != isEnabled) {
				firePropertyChange(ENABLED, wasEnabled ? Boolean.TRUE : Boolean.FALSE, isEnabled ? Boolean.TRUE : Boolean.FALSE);
			}
		}
		
		/**
		 * @see Action#run()
		 */
		public void run() {
			if (fOperationCode != -1 && fOperationTarget != null) {
				fOperationTarget.doOperation(fOperationCode);
			}
		}
	}	

	private Template fTemplate;
	
	private Text fNameText;
	private Text fDescriptionText;
	private Combo fContextCombo;
	private SourceViewer fPatternEditor;	
	private Button fInsertVariableButton;
	private Button fAutoInsertCheckbox;
	private boolean fIsNameModifiable;

	private StatusInfo fValidationStatus;
	private boolean fSuppressError= true; // #4354	
	private Map fGlobalActions= new HashMap(10);
	private List fSelectionActions = new ArrayList(3);	
	private String[][] fContextTypes;
	
	private ContextTypeRegistry fContextTypeRegistry; 
	
	private final TemplateVariableProcessor fTemplateProcessor= new TemplateVariableProcessor();
		
	/**
	 * Creates a new dialog.
	 * 
	 * @param parent the shell parent of the dialog
	 * @param template the template to edit
	 * @param edit whether this is a new template or an existing being edited
	 * @param isNameModifiable whether the name of the template may be modified
	 * @param registry the context type registry to use
	 */
	public EditTemplateDialog(Shell parent, Template template, boolean edit, boolean isNameModifiable, ContextTypeRegistry registry) {
		super(parent);
		
		setShellStyle(getShellStyle() | SWT.MAX | SWT.RESIZE);
		
		String title= edit
			? PreferencesMessages.EditTemplateDialog_title_edit
			: PreferencesMessages.EditTemplateDialog_title_new; 
		setTitle(title);

		fTemplate= template;
		fIsNameModifiable= isNameModifiable;
		
		String delim= new Document().getLegalLineDelimiters()[0];
		
		List contexts= new ArrayList();
		for (Iterator it= registry.contextTypes(); it.hasNext();) {
			TemplateContextType type= (TemplateContextType) it.next();
			if (type.getId().equals("ddoc")) //$NON-NLS-1$
				contexts.add(new String[] { type.getId(), type.getName(), "/**" + delim }); //$NON-NLS-1$
			else
				contexts.add(0, new String[] { type.getId(), type.getName(), "" }); //$NON-NLS-1$
		}
		fContextTypes= (String[][]) contexts.toArray(new String[contexts.size()][]);
				
		fValidationStatus= new StatusInfo();
		
		fContextTypeRegistry= registry;
		
		TemplateContextType type= fContextTypeRegistry.getContextType(template.getContextTypeId());
		fTemplateProcessor.setContextType(type);
	}
	
	/*
	 * @see descent.internal.ui.dialogs.StatusDialog#create()
	 */
	public void create() {
		super.create();
		// update initial OK button to be disabled for new templates 
		boolean valid= fNameText == null || fNameText.getText().trim().length() != 0;
		if (!valid) {
			StatusInfo status = new StatusInfo();
			status.setError(PreferencesMessages.EditTemplateDialog_error_noname); 
			updateButtonsEnableState(status);
 		}
	}
	
	/*
	 * @see Dialog#createDialogArea(Composite)
	 */
	protected Control createDialogArea(Composite ancestor) {
		Composite parent= new Composite(ancestor, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		parent.setLayout(layout);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ModifyListener listener= new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				doTextWidgetChanged(e.widget);
			}
		};
		
		if (fIsNameModifiable) {
			createLabel(parent, PreferencesMessages.EditTemplateDialog_name); 
			
			Composite composite= new Composite(parent, SWT.NONE);
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			layout= new GridLayout();		
			layout.numColumns= 4;
			layout.marginWidth= 0;
			layout.marginHeight= 0;
			composite.setLayout(layout);
			
			fNameText= createText(composite);
			fNameText.addFocusListener(new FocusListener() {
				
				public void focusGained(FocusEvent e) {
				}
				
				public void focusLost(FocusEvent e) {
					if (fSuppressError) {
						fSuppressError= false;
						updateButtons();
					}
				}
			});
			
			createLabel(composite, PreferencesMessages.EditTemplateDialog_context); 
			fContextCombo= new Combo(composite, SWT.READ_ONLY);
	
			for (int i= 0; i < fContextTypes.length; i++) {
				fContextCombo.add(fContextTypes[i][1]);
			}
	
			fContextCombo.addModifyListener(listener);
			
			fAutoInsertCheckbox= createCheckbox(composite, PreferencesMessages.EditTemplateDialog_autoinsert);
			fAutoInsertCheckbox.setSelection(fTemplate.isAutoInsertable());
		}
		
		createLabel(parent, PreferencesMessages.EditTemplateDialog_description); 
		
		int descFlags= fIsNameModifiable ? SWT.BORDER : SWT.BORDER | SWT.READ_ONLY;
		fDescriptionText= new Text(parent, descFlags );
		fDescriptionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		
		fDescriptionText.addModifyListener(listener);

		Label patternLabel= createLabel(parent, PreferencesMessages.EditTemplateDialog_pattern); 
		patternLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		fPatternEditor= createEditor(parent);
		
		Label filler= new Label(parent, SWT.NONE);		
		filler.setLayoutData(new GridData());
		
		Composite composite= new Composite(parent, SWT.NONE);
		layout= new GridLayout();		
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		composite.setLayout(layout);		
		composite.setLayoutData(new GridData());
		
		fInsertVariableButton= new Button(composite, SWT.NONE);
		fInsertVariableButton.setLayoutData(getButtonGridData(fInsertVariableButton));
		fInsertVariableButton.setText(PreferencesMessages.EditTemplateDialog_insert_variable); 
		fInsertVariableButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				fPatternEditor.getTextWidget().setFocus();
				fPatternEditor.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);			
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		fDescriptionText.setText(fTemplate.getDescription());
		if (fIsNameModifiable) {
			fNameText.setText(fTemplate.getName());
			fNameText.addModifyListener(listener);
			fContextCombo.select(getIndex(fTemplate.getContextTypeId()));
		} else {
			fPatternEditor.getControl().setFocus();
		}
		initializeActions();

		applyDialogFont(parent);
		return composite;
	}
	
	protected void doTextWidgetChanged(Widget w) {
		if (w == fNameText) {
			fSuppressError= false;
			updateButtons();			
		} else if (w == fContextCombo) {
			String contextId= getContextId();
			fTemplateProcessor.setContextType(fContextTypeRegistry.getContextType(contextId));
			IDocument document= fPatternEditor.getDocument();
			String prefix= getPrefix();
			document.set(prefix + getPattern());
			fPatternEditor.setVisibleRegion(prefix.length(), document.getLength() - prefix.length());
		} else if (w == fDescriptionText) {
			// nothing
		}	
	}
	
	private String getContextId() {
		if (fContextCombo != null && !fContextCombo.isDisposed()) {
			String name= fContextCombo.getText();
			for (int i= 0; i < fContextTypes.length; i++) {
				if (name.equals(fContextTypes[i][1])) {
					return fContextTypes[i][0];	
				}
			}
		}
		
		return fTemplate.getContextTypeId();
	}

	protected void doSourceChanged(IDocument document) {
		String text= document.get();
		fValidationStatus.setOK();
		TemplateContextType contextType= fContextTypeRegistry.getContextType(getContextId());
		if (contextType != null) {
			try {
				contextType.validate(text);
			} catch (TemplateException e) {
				fValidationStatus.setError(e.getLocalizedMessage());
			}
		}

		updateUndoAction();
		updateButtons();
	}	

	private static GridData getButtonGridData(Button button) {
		GridData data= new GridData(GridData.FILL_HORIZONTAL);	
		return data;
	}

	private static Label createLabel(Composite parent, String name) {
		Label label= new Label(parent, SWT.NULL);
		label.setText(name);
		label.setLayoutData(new GridData());

		return label;
	}

	private static Button createCheckbox(Composite parent, String name) {
		Button button= new Button(parent, SWT.CHECK);
		button.setText(name);
		button.setLayoutData(new GridData());
		
		return button;
	}
	
	private static Text createText(Composite parent) {
		Text text= new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		return text;
	}

	private SourceViewer createEditor(Composite parent) {
		String prefix= getPrefix();
		IDocument document= new Document(prefix + fTemplate.getPattern());
		JavaTextTools tools= JavaPlugin.getDefault().getJavaTextTools();
		tools.setupJavaDocumentPartitioner(document, IJavaPartitions.JAVA_PARTITIONING);
		IPreferenceStore store= JavaPlugin.getDefault().getCombinedPreferenceStore();
		SourceViewer viewer= new JavaSourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, store);
		TemplateEditorSourceViewerConfiguration configuration= new TemplateEditorSourceViewerConfiguration(tools.getColorManager(), store, null, fTemplateProcessor);
		viewer.configure(configuration);
		viewer.setEditable(true);
		viewer.setDocument(document, prefix.length(), document.getLength() - prefix.length());
		
		Font font= JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
		viewer.getTextWidget().setFont(font);
		new JavaSourcePreviewerUpdater(viewer, configuration, store);
		
		int nLines= document.getNumberOfLines();
		if (nLines < 5) {
			nLines= 5;
		} else if (nLines > 12) {
			nLines= 12;	
		}
				
		Control control= viewer.getControl();
		GridData data= new GridData(GridData.FILL_BOTH);
		data.widthHint= convertWidthInCharsToPixels(80);
		data.heightHint= convertHeightInCharsToPixels(nLines);
		control.setLayoutData(data);
		
		viewer.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				if (event .getDocumentEvent() != null)
					doSourceChanged(event.getDocumentEvent().getDocument());
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {			
			public void selectionChanged(SelectionChangedEvent event) {
				updateSelectionDependentActions();
			}
		});

		viewer.prependVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent event) {
				handleVerifyKeyPressed(event);
			}
		});
		
		return viewer;
	}
	
	private String getPrefix() {
		String id= getContextId();
		int idx= getIndex(id);
		if (idx != -1)
			return fContextTypes[idx][2];
		else
			return ""; //$NON-NLS-1$
	}

	private void handleVerifyKeyPressed(VerifyEvent event) {
		if (!event.doit)
			return;

		if (event.stateMask != SWT.MOD1)
			return;
			
		switch (event.character) {
			case ' ':
				fPatternEditor.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
				event.doit= false;
				break;

			// CTRL-Z
			case 'z' - 'a' + 1:
				fPatternEditor.doOperation(ITextOperationTarget.UNDO);
				event.doit= false;
				break;				
		}
	}

	private void initializeActions() {
		TextViewerAction action= new TextViewerAction(fPatternEditor, ITextOperationTarget.UNDO);
		action.setText(PreferencesMessages.EditTemplateDialog_undo); 
		fGlobalActions.put(ITextEditorActionConstants.UNDO, action);

		action= new TextViewerAction(fPatternEditor, ITextOperationTarget.CUT);
		action.setText(PreferencesMessages.EditTemplateDialog_cut); 
		fGlobalActions.put(ITextEditorActionConstants.CUT, action);

		action= new TextViewerAction(fPatternEditor, ITextOperationTarget.COPY);
		action.setText(PreferencesMessages.EditTemplateDialog_copy); 
		fGlobalActions.put(ITextEditorActionConstants.COPY, action);

		action= new TextViewerAction(fPatternEditor, ITextOperationTarget.PASTE);
		action.setText(PreferencesMessages.EditTemplateDialog_paste); 
		fGlobalActions.put(ITextEditorActionConstants.PASTE, action);

		action= new TextViewerAction(fPatternEditor, ITextOperationTarget.SELECT_ALL);
		action.setText(PreferencesMessages.EditTemplateDialog_select_all); 
		fGlobalActions.put(ITextEditorActionConstants.SELECT_ALL, action);

		action= new TextViewerAction(fPatternEditor, ISourceViewer.CONTENTASSIST_PROPOSALS);
		action.setText(PreferencesMessages.EditTemplateDialog_content_assist); 
		fGlobalActions.put("ContentAssistProposal", action); //$NON-NLS-1$

		fSelectionActions.add(ITextEditorActionConstants.CUT);
		fSelectionActions.add(ITextEditorActionConstants.COPY);
		fSelectionActions.add(ITextEditorActionConstants.PASTE);
		
		// create context menu
		MenuManager manager= new MenuManager(null, null);
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});

		StyledText text= fPatternEditor.getTextWidget();		
		Menu menu= manager.createContextMenu(text);
		text.setMenu(menu);
	}

	private void fillContextMenu(IMenuManager menu) {
		menu.add(new GroupMarker(ITextEditorActionConstants.GROUP_UNDO));
		menu.appendToGroup(ITextEditorActionConstants.GROUP_UNDO, (IAction) fGlobalActions.get(ITextEditorActionConstants.UNDO));
		
		menu.add(new Separator(ITextEditorActionConstants.GROUP_EDIT));		
		menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT, (IAction) fGlobalActions.get(ITextEditorActionConstants.CUT));
		menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT, (IAction) fGlobalActions.get(ITextEditorActionConstants.COPY));
		menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT, (IAction) fGlobalActions.get(ITextEditorActionConstants.PASTE));
		menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT, (IAction) fGlobalActions.get(ITextEditorActionConstants.SELECT_ALL));

		menu.add(new Separator(IContextMenuConstants.GROUP_GENERATE));
		menu.appendToGroup(IContextMenuConstants.GROUP_GENERATE, (IAction) fGlobalActions.get("ContentAssistProposal")); //$NON-NLS-1$
	}

	protected void updateSelectionDependentActions() {
		Iterator iterator= fSelectionActions.iterator();
		while (iterator.hasNext())
			updateAction((String)iterator.next());		
	}

	protected void updateUndoAction() {
		IAction action= (IAction) fGlobalActions.get(ITextEditorActionConstants.UNDO);
		if (action instanceof IUpdate)
			((IUpdate) action).update();
	}

	protected void updateAction(String actionId) {
		IAction action= (IAction) fGlobalActions.get(actionId);
		if (action instanceof IUpdate)
			((IUpdate) action).update();
	}

	private int getIndex(String contextid) {
		
		if (contextid == null)
			return -1;
		
		for (int i= 0; i < fContextTypes.length; i++) {
			if (contextid.equals(fContextTypes[i][0])) {
				return i;	
			}
		}
		return -1;
	}
	
	protected void okPressed() {
		String name= fNameText == null ? fTemplate.getName() : fNameText.getText();
		boolean isAutoInsertable= fAutoInsertCheckbox != null && fAutoInsertCheckbox.getSelection();
		fTemplate= new Template(name, fDescriptionText.getText(), getContextId(), getPattern(), isAutoInsertable);
		super.okPressed();
	}
	
	private void updateButtons() {		
		StatusInfo status;

		boolean valid= fNameText == null || fNameText.getText().trim().length() != 0;
		if (!valid) {
			status = new StatusInfo();
			if (!fSuppressError) {
				status.setError(PreferencesMessages.EditTemplateDialog_error_noname); 
			}
 		} else {
 			status= fValidationStatus; 
 		}
		updateStatus(status);
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.EDIT_TEMPLATE_DIALOG);
	}

	/**
	 * Returns the created template.
	 * 
	 * @return the created template
	 * @since 3.1
	 */
	public Template getTemplate() {
		return fTemplate;
	}
	
	private String getPattern() {
		IDocument doc= fPatternEditor.getDocument();
		IRegion visible= fPatternEditor.getVisibleRegion();
		try {
			return doc.get(visible.getOffset(), doc.getLength() - visible.getOffset());
		} catch (BadLocationException e) {
			return ""; //$NON-NLS-1$
		}
	}
	
	/*
	 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
	 * @since 3.2
	 */
	protected IDialogSettings getDialogBoundsSettings() {
		String sectionName= getClass().getName() + "_dialogBounds"; //$NON-NLS-1$
		IDialogSettings settings= JavaPlugin.getDefault().getDialogSettings();
		IDialogSettings section= settings.getSection(sectionName);
		if (section == null)
			section= settings.addNewSection(sectionName);
		return section;
	}

}
