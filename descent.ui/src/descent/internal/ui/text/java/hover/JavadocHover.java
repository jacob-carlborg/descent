/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Genady Beryozkin <eclipse@genady.org> - [hovering] tooltip for constant string does not show constant value - https://bugs.eclipse.org/bugs/show_bug.cgi?id=85382
 *******************************************************************************/
package descent.internal.ui.text.java.hover;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;
import org.eclipse.jface.internal.text.html.BrowserInput;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension4;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.osgi.framework.Bundle;

import descent.core.IDocumented;
import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.ISourceRange;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.CompilationUnit;
import descent.core.dom.IBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.SimpleName;
import descent.core.formatter.DefaultCodeFormatterConstants;
import descent.internal.corext.dom.NodeFinder;
import descent.internal.corext.util.JdtFlags;
import descent.internal.corext.util.Messages;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.actions.OpenBrowserUtil;
import descent.internal.ui.actions.SimpleSelectionProvider;
import descent.internal.ui.infoviews.JavadocView;
import descent.internal.ui.infoviews.JavadocViewHelper;
import descent.internal.ui.javaeditor.ASTProvider;
import descent.internal.ui.javaeditor.EditorUtility;
import descent.internal.ui.viewsupport.BasicElementLabels;
import descent.internal.ui.viewsupport.JavaElementLinks;
import descent.ui.JavaElementLabels;
import descent.ui.JavaUI;
import descent.ui.JavadocContentAccess;
import descent.ui.PreferenceConstants;


/**
 * Provides Javadoc as hover info for Java elements.
 *
 * @since 2.1
 */
public class JavadocHover extends AbstractJavaEditorTextHover {

	/**
	 * Action to go back to the previous input in the hover control.
	 *
	 * @since 3.4
	 */
	private static final class BackAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public BackAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			setText(JavaHoverMessages.JavadocHover_back);
			ISharedImages images= PlatformUI.getWorkbench().getSharedImages();
			setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
			setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));

			update();
		}

		public void run() {
			BrowserInformationControlInput previous= (BrowserInformationControlInput) fInfoControl.getInput().getPrevious();
			if (previous != null) {
				fInfoControl.setInput(previous);
			}
		}

		public void update() {
			BrowserInformationControlInput current= fInfoControl.getInput();

			if (current != null && current.getPrevious() != null) {
				BrowserInput previous= current.getPrevious();
				setToolTipText(Messages.format(JavaHoverMessages.JavadocHover_back_toElement_toolTip, BasicElementLabels.getJavaElementName(previous.getInputName())));
				setEnabled(true);
			} else {
				setToolTipText(JavaHoverMessages.JavadocHover_back);
				setEnabled(false);
			}
		}
	}

	/**
	 * Action to go forward to the next input in the hover control.
	 *
	 * @since 3.4
	 */
	private static final class ForwardAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public ForwardAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			setText(JavaHoverMessages.JavadocHover_forward);
			ISharedImages images= PlatformUI.getWorkbench().getSharedImages();
			setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
			setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));

			update();
		}

		public void run() {
			BrowserInformationControlInput next= (BrowserInformationControlInput) fInfoControl.getInput().getNext();
			if (next != null) {
				fInfoControl.setInput(next);
			}
		}

		public void update() {
			BrowserInformationControlInput current= fInfoControl.getInput();

			if (current != null && current.getNext() != null) {
				setToolTipText(Messages.format(JavaHoverMessages.JavadocHover_forward_toElement_toolTip, BasicElementLabels.getJavaElementName(current.getNext().getInputName())));
				setEnabled(true);
			} else {
				setToolTipText(JavaHoverMessages.JavadocHover_forward_toolTip);
				setEnabled(false);
			}
		}
	}

	/**
	 * Action that shows the current hover contents in the Javadoc view.
	 *
	 * @since 3.4
	 */
	private static final class ShowInJavadocViewAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public ShowInJavadocViewAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			setText(JavaHoverMessages.JavadocHover_showInJavadoc);
			setImageDescriptor(JavaPluginImages.DESC_OBJS_JAVADOCTAG); //TODO: better image
		}

		/*
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			JavadocBrowserInformationControlInput infoInput= (JavadocBrowserInformationControlInput) fInfoControl.getInput(); //TODO: check cast
			fInfoControl.notifyDelayedInputChange(null);
			fInfoControl.dispose(); //FIXME: should have protocol to hide, rather than dispose
			try {
				JavadocView view= (JavadocView) JavaPlugin.getActivePage().showView(JavaUI.ID_JAVADOC_VIEW);
				view.setInput(infoInput);
			} catch (PartInitException e) {
				JavaPlugin.log(e);
			}
		}
	}

	/**
	 * Action that opens the current hover input element.
	 *
	 * @since 3.4
	 */
	private static final class OpenDeclarationAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public OpenDeclarationAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			setText(JavaHoverMessages.JavadocHover_openDeclaration);
			JavaPluginImages.setLocalImageDescriptors(this, "goto_input.gif"); //$NON-NLS-1$ //TODO: better images
		}

		/*
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			JavadocBrowserInformationControlInput infoInput= (JavadocBrowserInformationControlInput) fInfoControl.getInput(); //TODO: check cast
			fInfoControl.notifyDelayedInputChange(null);
			fInfoControl.dispose(); //FIXME: should have protocol to hide, rather than dispose

			try {
				//FIXME: add hover location to editor navigation history?
				IEditorPart part = JavaUI.openInEditor(infoInput.getElement());
				EditorUtility.revealInEditor(part, infoInput.getElement());
			} catch (PartInitException e) {
				JavaPlugin.log(e);
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
	}


	/**
	 * Presenter control creator.
	 *
	 * @since 3.3
	 */
	public static final class PresenterControlCreator extends AbstractReusableInformationControlCreator {

		/*
		 * @see descent.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
		 */
		public IInformationControl doCreateInformationControl(Shell parent) {
			if (BrowserInformationControl.isAvailable(parent)) {
				ToolBarManager tbm= new ToolBarManager(SWT.FLAT);
				String font= PreferenceConstants.APPEARANCE_JAVADOC_FONT;
				BrowserInformationControl iControl= new BrowserInformationControl(parent, font, tbm);

				final BackAction backAction= new BackAction(iControl);
				backAction.setEnabled(false);
				tbm.add(backAction);
				final ForwardAction forwardAction= new ForwardAction(iControl);
				tbm.add(forwardAction);
				forwardAction.setEnabled(false);

				final ShowInJavadocViewAction showInJavadocViewAction= new ShowInJavadocViewAction(iControl);
				tbm.add(showInJavadocViewAction);
				final OpenDeclarationAction openDeclarationAction= new OpenDeclarationAction(iControl);
				tbm.add(openDeclarationAction);

				final SimpleSelectionProvider selectionProvider= new SimpleSelectionProvider();
//				OpenExternalBrowserAction openExternalJavadocAction= new OpenExternalBrowserAction(parent.getDisplay(), selectionProvider);
//				selectionProvider.addSelectionChangedListener(openExternalJavadocAction);
				selectionProvider.setSelection(new StructuredSelection());
//				tbm.add(openExternalJavadocAction);

				IInputChangedListener inputChangeListener= new IInputChangedListener() {
					public void inputChanged(Object newInput) {
						backAction.update();
						forwardAction.update();
						if (newInput == null) {
							selectionProvider.setSelection(new StructuredSelection());
						} else if (newInput instanceof BrowserInformationControlInput) {
							BrowserInformationControlInput input= (BrowserInformationControlInput) newInput;
							Object inputElement= input.getInputElement();
							selectionProvider.setSelection(new StructuredSelection(inputElement));
							boolean isJavaElementInput= inputElement instanceof IJavaElement;
							showInJavadocViewAction.setEnabled(isJavaElementInput);
							openDeclarationAction.setEnabled(isJavaElementInput);
						}
					}
				};
				iControl.addInputChangeListener(inputChangeListener);

				tbm.update(true);

				addLinkListener(iControl);
				return iControl;

			} else {
				return new DefaultInformationControl(parent, true);
			}
		}
	}


	/**
	 * Hover control creator.
	 *
	 * @since 3.3
	 */
	public static final class HoverControlCreator extends AbstractReusableInformationControlCreator {
		/**
		 * The information presenter control creator.
		 * @since 3.4
		 */
		private final IInformationControlCreator fInformationPresenterControlCreator;
		/**
		 * <code>true</code> to use the additional info affordance, <code>false</code> to use the hover affordance.
		 */
		private final boolean fAdditionalInfoAffordance;

		/**
		 * @param informationPresenterControlCreator control creator for enriched hover
		 * @since 3.4
		 */
		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator) {
			this(informationPresenterControlCreator, false);
		}

		/**
		 * @param informationPresenterControlCreator control creator for enriched hover
		 * @param additionalInfoAffordance <code>true</code> to use the additional info affordance, <code>false</code> to use the hover affordance
		 * @since 3.4
		 */
		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator, boolean additionalInfoAffordance) {
			fInformationPresenterControlCreator= informationPresenterControlCreator;
			fAdditionalInfoAffordance= additionalInfoAffordance;
		}

		/*
		 * @see descent.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
		 */
		public IInformationControl doCreateInformationControl(Shell parent) {
			String tooltipAffordanceString= fAdditionalInfoAffordance ? JavaPlugin.getAdditionalInfoAffordanceString() : EditorsUI.getTooltipAffordanceString();
			if (BrowserInformationControl.isAvailable(parent)) {
				String font= PreferenceConstants.APPEARANCE_JAVADOC_FONT;
				BrowserInformationControl iControl= new BrowserInformationControl(parent, font, tooltipAffordanceString) {
					/*
					 * @see org.eclipse.jface.text.IInformationControlExtension5#getInformationPresenterControlCreator()
					 */
					public IInformationControlCreator getInformationPresenterControlCreator() {
						return fInformationPresenterControlCreator;
					}
				};
				addLinkListener(iControl);
				return iControl;
			} else {
				return new DefaultInformationControl(parent, tooltipAffordanceString);
			}
		}

		/*
		 * @see descent.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#canReuse(org.eclipse.jface.text.IInformationControl)
		 */
		public boolean canReuse(IInformationControl control) {
			if (!super.canReuse(control))
				return false;

			if (control instanceof IInformationControlExtension4) {
				String tooltipAffordanceString= fAdditionalInfoAffordance ? JavaPlugin.getAdditionalInfoAffordanceString() : EditorsUI.getTooltipAffordanceString();
				((IInformationControlExtension4)control).setStatusText(tooltipAffordanceString);
			}

			return true;
		}
	}

	private static final long LABEL_FLAGS=  JavaElementLabels.ALL_FULLY_QUALIFIED
		| JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_EXCEPTIONS
		| JavaElementLabels.F_PRE_TYPE_SIGNATURE | JavaElementLabels.M_PRE_TYPE_PARAMETERS | JavaElementLabels.T_TYPE_PARAMETERS
		| JavaElementLabels.USE_RESOLVED;
	private static final long LOCAL_VARIABLE_FLAGS= LABEL_FLAGS & ~JavaElementLabels.F_FULLY_QUALIFIED | JavaElementLabels.F_POST_QUALIFIED;
	private static final long TYPE_PARAMETER_FLAGS= LABEL_FLAGS /* | JavaElementLabels.TP_POST_QUALIFIED */;

	/**
	 * The style sheet (css).
	 * @since 3.4
	 */
	private static String fgStyleSheet;

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
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getInformationPresenterControlCreator()
	 * @since 3.1
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null)
			fPresenterControlCreator= new PresenterControlCreator();
		return fPresenterControlCreator;
	}

	/*
	 * @see ITextHoverExtension#getHoverControlCreator()
	 * @since 3.2
	 */
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null)
			fHoverControlCreator= new HoverControlCreator(getInformationPresenterControlCreator());
		return fHoverControlCreator;
	}

	private static void addLinkListener(final BrowserInformationControl control) {
		control.addLocationListener(JavaElementLinks.createLocationListener(new JavaElementLinks.ILinkHandler() {
			/* (non-Javadoc)
			 * @see descent.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleJavadocViewLink(descent.core.IJavaElement)
			 */
			public void handleJavadocViewLink(IJavaElement linkTarget) {
				control.notifyDelayedInputChange(null);
				control.setVisible(false);
				control.dispose(); //FIXME: should have protocol to hide, rather than dispose
				try {
					JavadocView view= (JavadocView) JavaPlugin.getActivePage().showView(JavaUI.ID_JAVADOC_VIEW);
					view.setInput(linkTarget);
				} catch (PartInitException e) {
					JavaPlugin.log(e);
				}
			}

			/* (non-Javadoc)
			 * @see descent.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleInlineJavadocLink(descent.core.IJavaElement)
			 */
			public void handleInlineJavadocLink(IJavaElement linkTarget) {
				JavadocBrowserInformationControlInput hoverInfo= getHoverInfo(new IJavaElement[] { linkTarget }, null, (JavadocBrowserInformationControlInput) control.getInput());
				if (control.hasDelayedInputChangeListener())
					control.notifyDelayedInputChange(hoverInfo);
				else
					control.setInput(hoverInfo);
			}

			/* (non-Javadoc)
			 * @see descent.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleDeclarationLink(descent.core.IJavaElement)
			 */
			public void handleDeclarationLink(IJavaElement linkTarget) {
				control.notifyDelayedInputChange(null);
				control.dispose(); //FIXME: should have protocol to hide, rather than dispose
				try {
					//FIXME: add hover location to editor navigation history?
					JavaUI.openInEditor(linkTarget);
				} catch (PartInitException e) {
					JavaPlugin.log(e);
				} catch (JavaModelException e) {
					JavaPlugin.log(e);
				}
			}

			/* (non-Javadoc)
			 * @see descent.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleExternalLink(java.net.URL, org.eclipse.swt.widgets.Display)
			 */
			public boolean handleExternalLink(URL url, Display display) {
				control.notifyDelayedInputChange(null);
				control.dispose(); //FIXME: should have protocol to hide, rather than dispose

				// open external links in real browser:
				OpenBrowserUtil.open(url, display, ""); //$NON-NLS-1$

				return true;
			}

			public void handleTextSet() {
			}
		}));
	}

	/**
	 * @deprecated see {@link org.eclipse.jface.text.ITextHover#getHoverInfo(ITextViewer, IRegion)}
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		JavadocBrowserInformationControlInput info= (JavadocBrowserInformationControlInput) getHoverInfo2(textViewer, hoverRegion);
		return info != null ? info.getHtml() : null;
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		return internalGetHoverInfo(textViewer, hoverRegion);
	}

	private JavadocBrowserInformationControlInput internalGetHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		IJavaElement[] elements= getJavaElementsAt(textViewer, hoverRegion);
		if (elements == null || elements.length == 0)
			return null;

		String constantValue;
		if (elements.length == 1 && elements[0].getElementType() == IJavaElement.FIELD) {
			constantValue= getConstantValue((IField) elements[0], hoverRegion);
			if (constantValue != null)
				constantValue= HTMLPrinter.convertToHTMLContent(constantValue);
		} else {
			constantValue= null;
		}

		return getHoverInfo(elements, constantValue, null);
	}

	/**
	 * Computes the hover info.
	 *
	 * @param elements the resolved elements
	 * @param constantValue a constant value iff result contains exactly 1 constant field, or <code>null</code>
	 * @param previousInput the previous input, or <code>null</code>
	 * @return the HTML hover info for the given element(s) or <code>null</code> if no information is available
	 * @since 3.4
	 */
	private static JavadocBrowserInformationControlInput getHoverInfo(IJavaElement[] elements, String constantValue, JavadocBrowserInformationControlInput previousInput) {
		int nResults= elements.length;
		StringBuffer buffer= new StringBuffer();
		boolean hasContents= false;
		String base= null;
		IJavaElement element= null;

		int leadingImageWidth= 0;

		if (nResults > 1) {

			for (int i= 0; i < elements.length; i++) {
				HTMLPrinter.startBulletList(buffer);
				IJavaElement curr= elements[i];
				if (curr instanceof IMember || curr.getElementType() == IJavaElement.LOCAL_VARIABLE) {
					//FIXME: provide links
					HTMLPrinter.addBullet(buffer, getInfoText(curr, constantValue, false));
					hasContents= true;
				}
				HTMLPrinter.endBulletList(buffer);
			}

		} else {

			element= elements[0];
			hasContents= true;
				
			HTMLPrinter.addSmallHeader(buffer, getInfoText(element, constantValue, true));
			
			if (element instanceof IDocumented) {
				IDocumented member= (IDocumented) element;	
				try {
					Reader reader= JavadocContentAccess.getHTMLContentReader(member, true, true);
					
					/* TODO JDT UI attached javadoc
					// Provide hint why there's no Javadoc
					if (reader == null && member.isBinary()) {
						IPackageFragmentRoot root= (IPackageFragmentRoot)member.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
						if (root != null && root.getSourceAttachmentPath() == null && root.getAttachedJavadoc(null) == null)
							reader= new StringReader(InfoViewMessages.JavadocView_noAttachedInformation);
					}
					*/
					
					if (reader != null) {
						HTMLPrinter.addParagraph(buffer, reader);
					}
				} catch (JavaModelException ex) {
					return null;
				}	
				
			}	
			leadingImageWidth= 20;
		}

		if (!hasContents)
			return null;

		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, JavadocHover.getStyleSheet());
			if (base != null) {
				int endHeadIdx= buffer.indexOf("</head>"); //$NON-NLS-1$
				buffer.insert(endHeadIdx, "\n<base href='" + base + "'>\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			HTMLPrinter.addPageEpilog(buffer);
			return new JavadocBrowserInformationControlInput(previousInput, element, buffer.toString(), leadingImageWidth);
		}

		return null;
	}

	private static String getInfoText(IJavaElement element, String constantValue, boolean allowImage) {
		long flags;
		switch (element.getElementType()) {
			case IJavaElement.LOCAL_VARIABLE:
				flags= LOCAL_VARIABLE_FLAGS;
				break;
			case IJavaElement.TYPE_PARAMETER:
				flags= TYPE_PARAMETER_FLAGS;
				break;
			default:
				flags= LABEL_FLAGS;
				break;
		}
		StringBuffer label= new StringBuffer(JavaElementLinks.getElementLabel(element, flags));
		if (element.getElementType() == IJavaElement.FIELD) {
			if (constantValue != null) {
				IJavaProject javaProject= element.getJavaProject();
				if ("true".equals(javaProject.getOption(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, true)))
					label.append(' ');
				label.append('=');
				if ("true".equals(javaProject.getOption(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR, true)))
					label.append(' ');
				label.append(constantValue);
			}
		}

		String imageName= null;
		if (allowImage) {
			URL imageUrl= JavaPlugin.getDefault().getImagesOnFSRegistry().getImageURL(element);
			if (imageUrl != null) {
				imageName= imageUrl.toExternalForm();
			}
		}

		StringBuffer buf= new StringBuffer();
		addImageAndLabel(buf, imageName, 16, 16, 2, 2, label.toString(), 20, 2);
		return buf.toString();
	}

	/*
	 * @since 3.4
	 */
	private static boolean isStaticFinal(IField field) {
		try {
			return JdtFlags.isFinal(field) && JdtFlags.isStatic(field);
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
			return false;
		}
	}

	/**
	 * Returns the constant value for the given field.
	 *
	 * @param field the field
	 * @param hoverRegion the hover region
	 * @return the constant value for the given field or <code>null</code> if none
	 * @since 3.4
	 */
	private String getConstantValue(IField field, IRegion hoverRegion) {
		ISourceRange selection;
		try {
			selection = field.getNameRange();
		} catch (JavaModelException e) {
			return null;
		}
		
		Object constantValue= null;

		CompilationUnit unit= ASTProvider.getASTProvider().getAST(field.getCompilationUnit(), ASTProvider.WAIT_ACTIVE_ONLY, new NullProgressMonitor());
		if (unit == null) {
			ASTParser p= ASTParser.newParser(field.getJavaProject().getApiLevel());
			p.setSource(field.getCompilationUnit());
			p.setResolveBindings(true);			
			p.setFocalPosition(selection.getOffset());
			unit = (CompilationUnit) p.createAST(null);
		}
		
		if (unit == null) {
			return null;
		}

		ASTNode node= NodeFinder.perform(unit, selection.getOffset(), selection.getLength());
		if (node != null && node.getNodeType() == ASTNode.SIMPLE_NAME) {
			IBinding binding= ((SimpleName)node).resolveBinding();
			if (binding != null && binding.getKind() == IBinding.VARIABLE) {
				IVariableBinding variableBinding= (IVariableBinding)binding;
				if (field.equals(variableBinding.getJavaElement())) {
					constantValue= variableBinding.getConstantValue();
				}
			}
		}
		
		if (constantValue != null) {
			return constantValue.toString();
		}
		
		return null;
	}

	/**
	 * Returns the Javadoc hover style sheet with the current Javadoc font from the preferences.
	 * @return the updated style sheet
	 * @since 3.4
	 */
	private static String getStyleSheet() {
		if (fgStyleSheet == null)
			fgStyleSheet= loadStyleSheet();
		String css= fgStyleSheet;
		if (css != null) {
			FontData fontData= JFaceResources.getFontRegistry().getFontData(PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
			css= HTMLPrinter.convertTopLevelFont(css, fontData);
		}

		return css;
	}

	/**
	 * Loads and returns the Javadoc hover style sheet.
	 * @return the style sheet, or <code>null</code> if unable to load
	 * @since 3.4
	 */
	private static String loadStyleSheet() {
		Bundle bundle= Platform.getBundle(JavaPlugin.getPluginId());
		URL styleSheetURL= bundle.getEntry("/JavadocHoverStyleSheet.css"); //$NON-NLS-1$
		if (styleSheetURL != null) {
			BufferedReader reader= null;
			try {
				reader= new BufferedReader(new InputStreamReader(styleSheetURL.openStream()));
				StringBuffer buffer= new StringBuffer(1500);
				String line= reader.readLine();
				while (line != null) {
					buffer.append(line);
					buffer.append('\n');
					line= reader.readLine();
				}
				
				JavadocViewHelper.addPreferencesFontsAndColorsToStyleSheet(buffer);
				
				return buffer.toString();
			} catch (IOException ex) {
				JavaPlugin.log(ex);
				return ""; //$NON-NLS-1$
			} finally {
				try {
					if (reader != null)
						reader.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static void addImageAndLabel(StringBuffer buf, String imageName, int imageWidth, int imageHeight, int imageLeft, int imageTop, String label, int labelLeft, int labelTop) {

		if (imageName != null) {
			StringBuffer imageStyle= new StringBuffer("position: absolute; "); //$NON-NLS-1$
			imageStyle.append("width: ").append(imageWidth).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("height: ").append(imageHeight).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("top: ").append(imageTop).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("left: ").append(imageLeft).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$

			buf.append("<!--[if lte IE 6]><![if gte IE 5.5]>\n"); //$NON-NLS-1$
			buf.append("<span style=\"").append(imageStyle).append("filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='").append(imageName).append("')\"></span>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buf.append("<![endif]><![endif]-->\n"); //$NON-NLS-1$

			buf.append("<!--[if !IE]>-->\n"); //$NON-NLS-1$
			buf.append("<img style='").append(imageStyle).append("' src='").append(imageName).append("'/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buf.append("<!--<![endif]-->\n"); //$NON-NLS-1$
			buf.append("<!--[if gte IE 7]>\n"); //$NON-NLS-1$
			buf.append("<img style='").append(imageStyle).append("' src='").append(imageName).append("'/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buf.append("<![endif]-->\n"); //$NON-NLS-1$
		}

		buf.append("<div style='word-wrap:break-word;"); //$NON-NLS-1$
		if (imageName != null) {
			buf.append("margin-left: ").append(labelLeft).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("margin-top: ").append(labelTop).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		buf.append("'>"); //$NON-NLS-1$
		buf.append(label);
		buf.append("</div>"); //$NON-NLS-1$
	}

}
