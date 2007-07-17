package descent.internal.ui.infoviews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;
import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.JavaEditor;
import descent.internal.ui.text.HTMLPrinter;
import descent.internal.ui.text.HTMLTextPresenter;
import descent.ui.JavaElementLabels;
import descent.ui.JavaUI;
import descent.ui.JavadocContentAccess;
import descent.ui.PreferenceConstants;
import descent.ui.text.IJavaColorConstants;
import descent.ui.text.IJavaPartitions;

/**
 * View which shows Javadoc for a given Java element.
 *
 * FIXME: As of 3.0 selectAll() and getSelection() is not working
 *			see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
 *
 * @since 3.0
 */
public class JavadocView extends AbstractInfoView {

	/**
	 * Preference key for the preference whether to show a dialog
	 * when the SWT Browser widget is not available.
	 * @since 3.0
	 */
	private static final String DO_NOT_WARN_PREFERENCE_KEY= "JavadocView.error.doNotWarn"; //$NON-NLS-1$
	
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=73558
	private static final boolean WARNING_DIALOG_ENABLED= false;

	/** Flags used to render a label in the text widget. */
	private static final long LABEL_FLAGS=  JavaElementLabels.ALL_FULLY_QUALIFIED
		| JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_EXCEPTIONS
		| JavaElementLabels.F_PRE_TYPE_SIGNATURE | JavaElementLabels.T_TYPE_PARAMETERS;


	/** The HTML widget. */
	private Browser fBrowser;
	/** The text widget. */
	private StyledText fText;
	/** The information presenter. */
	private DefaultInformationControl.IInformationPresenter fPresenter;
	/** The text presentation. */
	private TextPresentation fPresentation= new TextPresentation();
	/** The select all action */
	private SelectAllAction fSelectAllAction;
	/** The style sheet (css) */
	private static String fgStyleSheet;

	/** The Browser widget */
	private boolean fIsUsingBrowserWidget;

	private RGB fBackgroundColorRGB;

	
	/**
	 * The Javadoc view's select all action.
	 */
	private class SelectAllAction extends Action {

		/** The control. */
		private Control fControl;
		/** The selection provider. */
		private SelectionProvider fSelectionProvider;

		/**
		 * Creates the action.
		 *
		 * @param control the widget
		 * @param selectionProvider the selection provider
		 */
		public SelectAllAction(Control control, SelectionProvider selectionProvider) {
			super("selectAll"); //$NON-NLS-1$

			Assert.isNotNull(control);
			Assert.isNotNull(selectionProvider);
			fControl= control;
			fSelectionProvider= selectionProvider;

			// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
			setEnabled(!fIsUsingBrowserWidget);

			setText(InfoViewMessages.SelectAllAction_label);
			setToolTipText(InfoViewMessages.SelectAllAction_tooltip);
			setDescription(InfoViewMessages.SelectAllAction_description);

			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IAbstractTextEditorHelpContextIds.SELECT_ALL_ACTION);
		}

		/**
		 * Selects all in the view.
		 */
		public void run() {
			if (fControl instanceof StyledText)
		        ((StyledText)fControl).selectAll();
			else {
				// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
//				((Browser)fControl).selectAll();
				if (fSelectionProvider != null)
					fSelectionProvider.fireSelectionChanged();
			}
		}
	}

	/**
	 * The Javadoc view's selection provider.
	 */
	private static class SelectionProvider implements ISelectionProvider {

		/** The selection changed listeners. */
		private ListenerList fListeners= new ListenerList(ListenerList.IDENTITY);
		/** The widget. */
		private Control fControl;

		/**
		 * Creates a new selection provider.
		 *
		 * @param control	the widget
		 */
		public SelectionProvider(Control control) {
		    Assert.isNotNull(control);
			fControl= control;
			if (fControl instanceof StyledText) {
			    ((StyledText)fControl).addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
					    fireSelectionChanged();
					}
			    });
			} else {
				// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
//				((Browser)fControl).addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						fireSelectionChanged();
//					}
//				});
			}
		}

		/**
		 * Sends a selection changed event to all listeners.
		 */
		public void fireSelectionChanged() {
			ISelection selection= getSelection();
			SelectionChangedEvent event= new SelectionChangedEvent(this, selection);
			Object[] selectionChangedListeners= fListeners.getListeners();
			for (int i= 0; i < selectionChangedListeners.length; i++)
				((ISelectionChangedListener)selectionChangedListeners[i]).selectionChanged(event);
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.add(listener);
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
		 */
		public ISelection getSelection() {
			if (fControl instanceof StyledText) {
				IDocument document= new Document(((StyledText)fControl).getSelectionText());
				return new TextSelection(document, 0, document.getLength());
			} else {
				// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
				return StructuredSelection.EMPTY;
			}
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.remove(listener);
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
		 */
		public void setSelection(ISelection selection) {
			// not supported
		}
	}

	/*
	 * @see AbstractInfoView#internalCreatePartControl(Composite)
	 */
	protected void internalCreatePartControl(Composite parent) {
		try {
			fBrowser= new Browser(parent, SWT.NONE);
			fIsUsingBrowserWidget= true;
			
		} catch (SWTError er) {

			/* The Browser widget throws an SWTError if it fails to
			 * instantiate properly. Application code should catch
			 * this SWTError and disable any feature requiring the
			 * Browser widget.
			 * Platform requirements for the SWT Browser widget are available
			 * from the SWT FAQ web site.
			 */

			IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
			boolean doNotWarn= store.getBoolean(DO_NOT_WARN_PREFERENCE_KEY);
			if (WARNING_DIALOG_ENABLED && !doNotWarn) {
				String title= InfoViewMessages.JavadocView_error_noBrowser_title;
				String message= InfoViewMessages.JavadocView_error_noBrowser_message;
				String toggleMessage= InfoViewMessages.JavadocView_error_noBrowser_doNotWarn;
				MessageDialogWithToggle dialog= MessageDialogWithToggle.openError(parent.getShell(), title, message, toggleMessage, false, null, null); 
				if (dialog.getReturnCode() == Window.OK)
					store.setValue(DO_NOT_WARN_PREFERENCE_KEY, dialog.getToggleState());
			}

			fIsUsingBrowserWidget= false;
		}

		if (!fIsUsingBrowserWidget) {
			fText= new StyledText(parent, SWT.V_SCROLL | SWT.H_SCROLL);
			fText.setEditable(false);
			fPresenter= new HTMLTextPresenter(false);

			fText.addControlListener(new ControlAdapter() {
				/*
				 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
				 */
				public void controlResized(ControlEvent e) {
					setInput(fText.getText());
				}
			});
		}

		initStyleSheet();
		getViewSite().setSelectionProvider(new SelectionProvider(getControl()));
	}

	private static void initStyleSheet() {
		Bundle bundle= Platform.getBundle(JavaPlugin.getPluginId());
		URL styleSheetURL= bundle.getEntry("/JavadocViewStyleSheet.css"); //$NON-NLS-1$
		if (styleSheetURL == null)
			return;
		
		try {
			styleSheetURL= FileLocator.toFileURL(styleSheetURL);
			BufferedReader reader= new BufferedReader(new InputStreamReader(styleSheetURL.openStream()));
			StringBuffer buffer= new StringBuffer(200);
			String line= reader.readLine();
			while (line != null) {
				buffer.append(line);
				buffer.append('\n');
				line= reader.readLine();
			}
			
			addPreferencesFontsAndColorsToStyleSheet(buffer);
			
			fgStyleSheet= buffer.toString();
		} catch (IOException ex) {
			JavaPlugin.log(ex);
		}
		
	}

	private static void addPreferencesFontsAndColorsToStyleSheet(StringBuffer buffer) {
		addStyle(buffer, IJavaColorConstants.JAVA_KEYWORD);
		addStyle(buffer, IJavaColorConstants.JAVA_KEYWORD_RETURN);
		addStyle(buffer, IJavaColorConstants.JAVA_OPERATOR);
		addStyle(buffer, IJavaColorConstants.JAVA_DEFAULT);
		addStyle(buffer, IJavaColorConstants.JAVA_PRAGMA);
		addStyle(buffer, IJavaColorConstants.JAVA_STRING);
		addStyle(buffer, IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVA_MULTI_LINE_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVADOC_DEFAULT);
	}
	
	private static void addStyle(StringBuffer buffer, String partialPreferenceKey) {
		IJavaProject javaProject = null;
		
		buffer.append("."); //$NON-NLS-1$
		buffer.append(partialPreferenceKey);
		buffer.append("{"); //$NON-NLS-1$

		String colorString = PreferenceConstants.getPreference(partialPreferenceKey, javaProject);
		RGB color = colorString == null ? new RGB(0, 0, 0) : StringConverter.asRGB(colorString);
		buffer.append("color: "); //$NON-NLS-1$
		HTMLPrinter.appendColor(buffer, color);
		buffer.append(";"); //$NON-NLS-1$
		
		String boolString;
		boolean bool, bool2;
		
		boolString = PreferenceConstants.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_BOLD_SUFFIX, javaProject);
		bool = boolString == null ? false : StringConverter.asBoolean(boolString);
		if (bool) {
			buffer.append("font-weight: bold;"); //$NON-NLS-1$
		}
		
		boolString = PreferenceConstants.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_ITALIC_SUFFIX, javaProject);
		bool = boolString == null ? false : StringConverter.asBoolean(boolString);
		if (bool) {
			buffer.append("font-style: italic;"); //$NON-NLS-1$
		}
		
		boolString = PreferenceConstants.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_UNDERLINE_SUFFIX, javaProject);
		bool = boolString == null ? false : StringConverter.asBoolean(boolString);
		
		boolString = PreferenceConstants.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_STRIKETHROUGH_SUFFIX, javaProject);
		bool2 = boolString == null ? false : StringConverter.asBoolean(boolString);
		
		if (bool || bool2) {
			buffer.append("text-decoration:"); //$NON-NLS-1$
			if (bool) {
				buffer.append("underline"); //$NON-NLS-1$
			}
			if (bool && bool2) {
				buffer.append(", "); //$NON-NLS-1$
			}
			if (bool2) {
				buffer.append("line-through"); //$NON-NLS-1$
			}
			buffer.append(";"); //$NON-NLS-1$
		}
		
		buffer.append("}\n"); //$NON-NLS-1$
	}

	/*
	 * @see AbstractInfoView#createActions()
	 */
	protected void createActions() {
		super.createActions();
		fSelectAllAction= new SelectAllAction(getControl(), (SelectionProvider)getSelectionProvider());
	}


	/*
	 * @see descent.internal.ui.infoviews.AbstractInfoView#getSelectAllAction()
	 * @since 3.0
	 */
	protected IAction getSelectAllAction() {
		// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
		if (fIsUsingBrowserWidget)
			return null;

		return fSelectAllAction;
	}

	/*
	 * @see descent.internal.ui.infoviews.AbstractInfoView#getCopyToClipboardAction()
	 * @since 3.0
	 */
	protected IAction getCopyToClipboardAction() {
		// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
		if (fIsUsingBrowserWidget)
			return null;

		return super.getCopyToClipboardAction();
	}

	/*
 	 * @see AbstractInfoView#setForeground(Color)
 	 */
	protected void setForeground(Color color) {
		getControl().setForeground(color);
	}

	/*
	 * @see AbstractInfoView#setBackground(Color)
	 */
	protected void setBackground(Color color) {
		getControl().setBackground(color);

		// Apply style sheet
		fBackgroundColorRGB= color.getRGB();
		if (getInput() == null) {
			StringBuffer buffer= new StringBuffer(""); //$NON-NLS-1$
			HTMLPrinter.insertPageProlog(buffer, 0, fBackgroundColorRGB, fgStyleSheet);
			setInput(buffer.toString());
		} else {
			setInput(computeInput(getInput()));
		}
	}
	
	/*
	 * @see descent.internal.ui.infoviews.AbstractInfoView#getBackgroundColorKey()
	 * @since 3.2
	 */
	protected String getBackgroundColorKey() {
		return "descent.ui.JavadocView.backgroundColor";		 //$NON-NLS-1$
	}

	/*
	 * @see AbstractInfoView#internalDispose()
	 */
	protected void internalDispose() {
		fText= null;
		fBrowser= null;
	}

	/*
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		getControl().setFocus();
	}

	/*
	 * @see AbstractInfoView#computeInput(Object)
	 */
	protected Object computeInput(Object input) {
		if (getControl() == null || ! (input instanceof IJavaElement))
			return null;

		IJavaElement je= (IJavaElement)input;
		String javadocHtml;

		switch (je.getElementType()) {
			case IJavaElement.COMPILATION_UNIT:
				try {
					javadocHtml= getJavadocHtml(((ICompilationUnit)je).getTypes());
				} catch (JavaModelException ex) {
					javadocHtml= null;
				}
				break;
			case IJavaElement.CLASS_FILE:
				try {
					javadocHtml= getJavadocHtml(new IJavaElement[] {((IClassFile)je).getType()});
				} catch (JavaModelException ex) {
					javadocHtml= null;
				}
				break;
			default:
				javadocHtml= getJavadocHtml(new IJavaElement[] { je });
		}
		
		if (javadocHtml == null)
			return ""; //$NON-NLS-1$
		
		return javadocHtml;
	}

	/*
	 * @see AbstractInfoView#setInput(Object)
	 */
	protected void setInput(Object input) {
		String javadocHtml= (String)input;

		if (fIsUsingBrowserWidget) {
			if (javadocHtml != null && javadocHtml.length() > 0) {
				boolean RTL= (getSite().getShell().getStyle() & SWT.RIGHT_TO_LEFT) != 0;
				if (RTL) {
					StringBuffer buffer= new StringBuffer(javadocHtml);
					HTMLPrinter.insertStyles(buffer, new String[] { "direction:rtl" } ); //$NON-NLS-1$
					javadocHtml= buffer.toString();
				}
			}
			fBrowser.setText(javadocHtml);
		} else {
			fPresentation.clear();
			Rectangle size=  fText.getClientArea();

			try {
				javadocHtml= ((DefaultInformationControl.IInformationPresenterExtension)fPresenter).updatePresentation(getSite().getShell(), javadocHtml, fPresentation, size.width, size.height);
			} catch (IllegalArgumentException ex) {
				// the javadoc might no longer be valid
				return;
			}
			fText.setText(javadocHtml);
			TextPresentation.applyTextPresentation(fPresentation, fText);
		}
	}
	
	/**
	 * Returns the Javadoc in HTML format.
	 *
	 * @param result the Java elements for which to get the Javadoc
	 * @return a string with the Javadoc in HTML format.
	 */
	private String getJavadocHtml(IJavaElement[] result) {
		StringBuffer buffer= new StringBuffer();
		int nResults= result.length;

		if (nResults == 0)
			return null;

		if (nResults > 1) {

			for (int i= 0; i < result.length; i++) {
				HTMLPrinter.startBulletList(buffer);
				IJavaElement curr= result[i];
				if (curr instanceof IMember)
					HTMLPrinter.addBullet(buffer, getInfoText((IMember) curr));
				HTMLPrinter.endBulletList(buffer);
			}

		} else {

			IJavaElement curr= result[0];
			if (curr instanceof IMember) {
				IMember member= (IMember) curr;
//				HTMLPrinter.addSmallHeader(buffer, getInfoText(member));
				Reader reader;
				try {
					reader= JavadocContentAccess.getHTMLContentReader(member, true, true);
					
					// Provide hint why there's no Javadoc
					if (reader == null && member.isBinary()) {
						IPackageFragmentRoot root= (IPackageFragmentRoot)member.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
						/* TODO JDT UI attached javadoc
						if (root != null && root.getSourceAttachmentPath() == null && root.getAttachedJavadoc(null) == null)
							reader= new StringReader(InfoViewMessages.JavadocView_noAttachedInformation);
						*/
					}
					
				} catch (JavaModelException ex) {
					return null;
				}
				if (reader != null) {
					HTMLPrinter.addParagraph(buffer, reader);
				}
			}
		}

		boolean flushContent= true;
		if (buffer.length() > 0 || flushContent) {
			HTMLPrinter.insertPageProlog(buffer, 0, fBackgroundColorRGB, fgStyleSheet);
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}

		return null;
	}

	/**
	 * Gets the label for the given member.
	 *
	 * @param member the Java member
	 * @return a string containing the member's label
	 */
	private String getInfoText(IMember member) {
		return JavaElementLabels.getElementLabel(member, LABEL_FLAGS);
	}

	/*
	 * @see descent.internal.ui.infoviews.AbstractInfoView#isIgnoringNewInput(descent.core.IJavaElement, org.eclipse.jface.viewers.ISelection)
	 * @since 3.2
	 */
	protected boolean isIgnoringNewInput(IJavaElement je, IWorkbenchPart part, ISelection selection) {
		if (super.isIgnoringNewInput(je, part, selection)
				&& part instanceof ITextEditor
				&& selection instanceof ITextSelection) {
			
			ITextEditor editor= (ITextEditor)part;
			IDocumentProvider docProvider= editor.getDocumentProvider();
			if (docProvider == null)
				return false;
			
			IDocument document= docProvider.getDocument(editor.getEditorInput());
			if (!(document instanceof IDocumentExtension3))
				return false;
			
			try {
				int offset= ((ITextSelection)selection).getOffset();
				String partition= ((IDocumentExtension3)document).getContentType(IJavaPartitions.JAVA_PARTITIONING, offset, false);
				return  partition != IJavaPartitions.JAVA_DOC;
			} catch (BadPartitioningException ex) {
				return false;
			} catch (BadLocationException ex) {
				return false;
			}

		}
		return false;
	}

	/*
	 * @see AbstractInfoView#findSelectedJavaElement(IWorkbenchPart)
	 */
	protected IJavaElement findSelectedJavaElement(IWorkbenchPart part, ISelection selection) {
		IJavaElement element;
		try {
			element= super.findSelectedJavaElement(part, selection);

			if (element == null && part instanceof JavaEditor && selection instanceof ITextSelection) {

				JavaEditor editor= (JavaEditor)part;
				ITextSelection textSelection= (ITextSelection)selection;

				IDocumentProvider documentProvider= editor.getDocumentProvider();
				if (documentProvider == null)
					return null;

				IDocument document= documentProvider.getDocument(editor.getEditorInput());
				if (document == null)
					return null;

				ITypedRegion typedRegion= TextUtilities.getPartition(document, IJavaPartitions.JAVA_PARTITIONING, textSelection.getOffset(), false);
				if (IJavaPartitions.JAVA_DOC.equals(typedRegion.getType()) ||
						IJavaPartitions.JAVA_MULTI_LINE_PLUS_DOC_COMMENT.equals(typedRegion.getType()) ||
						IJavaPartitions.JAVA_SINGLE_LINE_DOC_COMMENT.equals(typedRegion.getType()))
					return TextSelectionConverter.getElementAtOffset((JavaEditor)part, textSelection);
				else
					return null;
			} else
				return element;
		} catch (JavaModelException e) {
			return null;
		} catch (BadLocationException e) {
			return null;
		}
	}

	/*
	 * @see AbstractInfoView#getControl()
	 */
	protected Control getControl() {
		if (fIsUsingBrowserWidget)
			return fBrowser;
		else
			return fText;
	}

	/*
	 * @see descent.internal.ui.infoviews.AbstractInfoView#getHelpContextId()
	 * @since 3.1
	 */
	protected String getHelpContextId() {
		return IJavaHelpContextIds.JAVADOC_VIEW;
	}
}
