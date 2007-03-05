/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

import descent.ui.PreferenceConstants;
import descent.ui.text.IJavaPartitions;
import descent.ui.text.JavaTextTools;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.JavaSourceViewer;
import descent.internal.ui.text.SimpleJavaSourceViewerConfiguration;
import descent.internal.ui.text.template.preferences.TemplateVariableProcessor;

public class JavaTemplatePreferencePage extends TemplatePreferencePage implements IWorkbenchPreferencePage {

	private TemplateVariableProcessor fTemplateProcessor;
	
	public JavaTemplatePreferencePage() {
		setPreferenceStore(JavaPlugin.getDefault().getPreferenceStore());
		setTemplateStore(JavaPlugin.getDefault().getTemplateStore());
		setContextTypeRegistry(JavaPlugin.getDefault().getTemplateContextRegistry());
		fTemplateProcessor= new TemplateVariableProcessor();
	}
	
	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaHelpContextIds.JAVA_EDITOR_PREFERENCE_PAGE);
	}


	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean ok= super.performOk();

		JavaPlugin.getDefault().savePluginPreferences();
		
		return ok;
	}
	
	/*
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#getFormatterPreferenceKey()
	 */
	protected String getFormatterPreferenceKey() {
		return PreferenceConstants.TEMPLATES_USE_CODEFORMATTER;
	}
	
	/*
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#createTemplateEditDialog2(org.eclipse.jface.text.templates.Template, boolean, boolean)
	 */
	protected Template editTemplate(Template template, boolean edit, boolean isNameModifiable) {
		EditTemplateDialog dialog= new EditTemplateDialog(getShell(), template, edit, isNameModifiable, getContextTypeRegistry());
		if (dialog.open() == Window.OK) {
			return dialog.getTemplate();
		}
		return null;
	}
	
	/*
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#createViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected SourceViewer createViewer(Composite parent) {
		IDocument document= new Document();
		JavaTextTools tools= JavaPlugin.getDefault().getJavaTextTools();
		tools.setupJavaDocumentPartitioner(document, IJavaPartitions.JAVA_PARTITIONING);
		IPreferenceStore store= JavaPlugin.getDefault().getCombinedPreferenceStore();
		SourceViewer viewer= new JavaSourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, store);
		SimpleJavaSourceViewerConfiguration configuration= new SimpleJavaSourceViewerConfiguration(tools.getColorManager(), store, null, IJavaPartitions.JAVA_PARTITIONING, false);
		viewer.configure(configuration);
		viewer.setEditable(false);
		viewer.setDocument(document);
	
		Font font= JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
		viewer.getTextWidget().setFont(font);
		new JavaSourcePreviewerUpdater(viewer, configuration, store);
		
		Control control= viewer.getControl();
		GridData data= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_VERTICAL);
		control.setLayoutData(data);
		
		return viewer;
	}
	
	
	/*
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#updateViewerInput()
	 */
	protected void updateViewerInput() {
		IStructuredSelection selection= (IStructuredSelection) getTableViewer().getSelection();
		SourceViewer viewer= getViewer();
		
		if (selection.size() == 1 && selection.getFirstElement() instanceof TemplatePersistenceData) {
			TemplatePersistenceData data= (TemplatePersistenceData) selection.getFirstElement();
			Template template= data.getTemplate();
			String contextId= template.getContextTypeId();
			TemplateContextType type= JavaPlugin.getDefault().getTemplateContextRegistry().getContextType(contextId);
			fTemplateProcessor.setContextType(type);
			
			IDocument doc= viewer.getDocument();
			
			String start= null;
			if ("javadoc".equals(contextId)) { //$NON-NLS-1$
				start= "/**" + doc.getLegalLineDelimiters()[0]; //$NON-NLS-1$
			} else
				start= ""; //$NON-NLS-1$
			
			doc.set(start + template.getPattern());
			int startLen= start.length();
			viewer.setDocument(doc, startLen, doc.getLength() - startLen);

		} else {
			viewer.getDocument().set(""); //$NON-NLS-1$
		}		
	}
}
