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
package descent.internal.corext.template.java;

import java.util.Iterator;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;

import descent.internal.ui.JavaPlugin;

public class CodeTemplateContext extends TemplateContext {
	
	private String fLineDelimiter;
	private IJavaProject fProject;

	public CodeTemplateContext(String contextTypeName, IJavaProject project, String lineDelim) {
		super(JavaPlugin.getDefault().getCodeTemplateContextRegistry().getContextType(contextTypeName));
		fLineDelimiter= lineDelim;
		fProject= project;
	}

	public IJavaProject getJavaProject() {
		return fProject;
	}

	/*
	 * @see descent.internal.corext.template.TemplateContext#evaluate(descent.internal.corext.template.Template)
	 */
	public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException {
		// test that all variables are defined
		Iterator iterator= getContextType().resolvers();
		while (iterator.hasNext()) {
			TemplateVariableResolver var= (TemplateVariableResolver) iterator.next();
			if (var instanceof CodeTemplateContextType.CodeTemplateVariableResolver) {
				Assert.isNotNull(getVariable(var.getType()), "Variable " + var.getType() + "not defined"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		if (!canEvaluate(template))
			return null;
			
		String pattern= changeLineDelimiter(template.getPattern(), fLineDelimiter);
		
		TemplateTranslator translator= new TemplateTranslator();
		TemplateBuffer buffer= translator.translate(pattern);
		getContextType().resolve(buffer, this);
		return buffer;
	}
	
	private static String changeLineDelimiter(String code, String lineDelim) {
		try {
			ILineTracker tracker= new DefaultLineTracker();
			tracker.set(code);
			int nLines= tracker.getNumberOfLines();
			if (nLines == 1) {
				return code;
			}
			
			StringBuffer buf= new StringBuffer();
			for (int i= 0; i < nLines; i++) {
				if (i != 0) {
					buf.append(lineDelim);
				}
				IRegion region = tracker.getLineInformation(i);
				String line= code.substring(region.getOffset(), region.getOffset() + region.getLength());
				buf.append(line);
			}
			return buf.toString();
		} catch (BadLocationException e) {
			// can not happen
			return code;
		}
	}		

	/* (non-Javadoc)
	 * @see descent.internal.corext.template.TemplateContext#canEvaluate(descent.internal.corext.template.Template)
	 */
	public boolean canEvaluate(Template template) {
		return true;
	}
	
	public void setCompilationUnitVariables(ICompilationUnit cu) {
		setVariable(CodeTemplateContextType.FILENAME, cu.getElementName());
		setVariable(CodeTemplateContextType.PACKAGENAME, cu.getParent().getElementName());
		setVariable(CodeTemplateContextType.PROJECTNAME, cu.getJavaProject().getElementName());
	}

}
