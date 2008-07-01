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
package descent.internal.ui.text.java.hover;

import java.io.Reader;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import descent.core.ICompilationUnit;
import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IPackageDeclaration;
import descent.core.JavaModelException;
import descent.internal.ui.text.HTMLPrinter;
import descent.internal.ui.text.HTMLTextPresenter;
import descent.internal.ui.text.IInformationControlExtension4;
import descent.ui.JavaElementLabels;
import descent.ui.JavadocContentAccess;

/**
 * Provides Javadoc as hover info for Java elements.
 *
 * @since 2.1
 */
public class JavadocHover extends AbstractJavaEditorTextHover implements IInformationProviderExtension2, ITextHoverExtension {

	private final long LABEL_FLAGS=  JavaElementLabels.ALL_FULLY_QUALIFIED
		| JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_EXCEPTIONS
		| JavaElementLabels.F_PRE_TYPE_SIGNATURE | JavaElementLabels.M_PRE_TYPE_PARAMETERS | JavaElementLabels.T_TYPE_PARAMETERS
		| JavaElementLabels.USE_RESOLVED | JavaElementLabels.F_KIND | JavaElementLabels.T_KIND;
	private final long LOCAL_VARIABLE_FLAGS= LABEL_FLAGS & ~JavaElementLabels.F_FULLY_QUALIFIED | JavaElementLabels.F_POST_QUALIFIED | JavaElementLabels.F_KIND | JavaElementLabels.T_KIND;

	
	/**
	 * The hover control creator.
	 * 
	 * @since 3.2
	 */
	private IInformationControlCreator fHoverControlCreator;
	/**
	 * The presentation control creator.
	 * 
	 * @since 3.2
	 */
	private IInformationControlCreator fPresenterControlCreator;


	/*
	 * @see IInformationProviderExtension2#getInformationPresenterControlCreator()
	 * @since 3.1
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null) {
			fPresenterControlCreator= new AbstractReusableInformationControlCreator() {

				/*
				 * @see descent.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
				 */
				public IInformationControl doCreateInformationControl(Shell parent) {
					int shellStyle= SWT.RESIZE | SWT.TOOL;
					int style= SWT.V_SCROLL | SWT.H_SCROLL;
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent, shellStyle, style);
					else
						return new DefaultInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false));
				}
			};
		}
		return fPresenterControlCreator;
	}

	/*
	 * @see ITextHoverExtension#getHoverControlCreator()
	 * @since 3.2
	 */
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null) {
			fHoverControlCreator= new AbstractReusableInformationControlCreator() {
				
				/*
				 * @see descent.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
				 */
				public IInformationControl doCreateInformationControl(Shell parent) {
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent, SWT.TOOL | SWT.NO_TRIM, SWT.NONE, getTooltipAffordanceString());
					else
						return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true), getTooltipAffordanceString());
				}
				
				/*
				 * @see descent.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#canReuse(org.eclipse.jface.text.IInformationControl)
				 */
				public boolean canReuse(IInformationControl control) {
					boolean canReuse= super.canReuse(control);
					if (canReuse && control instanceof IInformationControlExtension4)
						((IInformationControlExtension4)control).setStatusText(getTooltipAffordanceString());
					return canReuse;
						
				}
			};
		}
		return fHoverControlCreator;
	}

	/*
	 * @see JavaElementHover
	 */
	protected String getHoverInfo(IJavaElement[] result) {

		StringBuffer buffer= new StringBuffer();
		int nResults= result.length;
		if (nResults == 0)
			return null;

		boolean hasContents= false;
		if (nResults > 1) {

			for (int i= 0; i < result.length; i++) {
				HTMLPrinter.startBulletList(buffer);
				IJavaElement curr= result[i];
				if (curr instanceof IMember || curr.getElementType() == IJavaElement.LOCAL_VARIABLE) {
					HTMLPrinter.addBullet(buffer, getInfoText(curr));
					hasContents= true;
				}
				HTMLPrinter.endBulletList(buffer);
			}

		} else {

			IJavaElement curr= result[0];
			if (curr instanceof IMember) {
				IMember member= (IMember) curr;
				HTMLPrinter.addSmallHeader(buffer, getInfoText(member));
				Reader reader;
				try {
					reader= JavadocContentAccess.getHTMLContentReader(member, true, true);
					
					// Provide hint why there's no Javadoc
					/* TODO JDT UI binary
					if (reader == null && member.isBinary()) {
						IPackageFragmentRoot root= (IPackageFragmentRoot)member.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
						if (root != null && root.getSourceAttachmentPath() == null && root.getAttachedJavadoc(null) == null)
							reader= new StringReader(JavaHoverMessages.JavadocHover_noAttachedInformation);
					}
					*/
				} catch (JavaModelException ex) {
					return null;
				}
				
				if (reader != null) {
					HTMLPrinter.addParagraph(buffer, reader);
				}
				hasContents= true;
			} else if (curr.getElementType() == IJavaElement.LOCAL_VARIABLE || curr.getElementType() == IJavaElement.TYPE_PARAMETER) {
				HTMLPrinter.addSmallHeader(buffer, getInfoText(curr));
				hasContents= true;
			} else if (curr instanceof ICompilationUnit) {
				ICompilationUnit cu = (ICompilationUnit) curr;
				try {
					IPackageDeclaration[] pds = cu.getPackageDeclarations();
					if (pds.length > 0) {
						IPackageDeclaration member= pds[0];
						HTMLPrinter.addSmallHeader(buffer, getInfoText(cu));
						Reader reader= JavadocContentAccess.getHTMLContentReader(member, true, true);
						if (reader != null) {
							HTMLPrinter.addParagraph(buffer, reader);
						}
						hasContents= true;
					}
				} catch (JavaModelException ex) {
					return null;
				}
			} else if (curr instanceof IImportDeclaration) {
				HTMLPrinter.addSmallHeader(buffer, "import " + ((IImportDeclaration) curr).getElementName());
				hasContents= true;
			}
		}
		
		if (!hasContents)
			return null;

		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}

		return null;
	}

	private String getInfoText(IJavaElement member) {
		long flags= member.getElementType() == IJavaElement.LOCAL_VARIABLE ? LOCAL_VARIABLE_FLAGS : LABEL_FLAGS;
		String label= JavaElementLabels.getElementLabel(member, flags);
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < label.length(); i++) {
			char ch= label.charAt(i);
			if (ch == '<') {
				buf.append("&lt;"); //$NON-NLS-1$
			} else if (ch == '>') {
				buf.append("&gt;"); //$NON-NLS-1$
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

}
