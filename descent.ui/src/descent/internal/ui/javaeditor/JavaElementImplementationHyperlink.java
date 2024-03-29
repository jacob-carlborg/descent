package descent.internal.ui.javaeditor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.core.IJavaElement;
import descent.core.IMethod;
import descent.core.IType;
import descent.core.ITypeRoot;
import descent.core.dom.ASTNode;
import descent.core.dom.CallExpression;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Expression;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.ITypeBinding;
import descent.core.dom.SimpleName;
import descent.core.search.IJavaSearchConstants;
import descent.core.search.SearchEngine;
import descent.core.search.SearchMatch;
import descent.core.search.SearchParticipant;
import descent.core.search.SearchPattern;
import descent.core.search.SearchRequestor;
import descent.internal.corext.dom.Bindings;
import descent.internal.corext.dom.NodeFinder;
import descent.internal.corext.util.JdtFlags;
import descent.internal.corext.util.Messages;
import descent.internal.ui.JavaPlugin;
import descent.ui.JavaElementLabels;
import descent.ui.actions.SelectionDispatchAction;


/**
 * Java element implementation hyperlink.
 * 
 * @since 3.5
 */
public class JavaElementImplementationHyperlink implements IHyperlink {
	
	private final IRegion fRegion;
	private final SelectionDispatchAction fOpenAction;
	private final IJavaElement fElement;
	private final boolean fQualify;

	/**
	 * The current text editor.
	 */
	private ITextEditor fEditor;

	/**
	 * Creates a new Java element implementation hyperlink for methods.
	 * 
	 * @param region the region of the link
	 * @param openAction the action to use to open the java elements
	 * @param element the java element to open
	 * @param qualify <code>true</code> if the hyperlink text should show a qualified name for
	 *            element.
	 * @param editor the active java editor
	 */
	public JavaElementImplementationHyperlink(IRegion region, SelectionDispatchAction openAction, IJavaElement element, boolean qualify, ITextEditor editor) {
		Assert.isNotNull(openAction);
		Assert.isNotNull(region);
		Assert.isNotNull(element);

		fRegion= region;
		fOpenAction= openAction;
		fElement= element;
		fQualify= qualify;
		fEditor= editor;
	}

	/*
	 * @see descent.internal.ui.javaeditor.IHyperlink#getHyperlinkRegion()
	 */
	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	/*
	 * @see descent.internal.ui.javaeditor.IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		if (fQualify) {
			String elementLabel= JavaElementLabels.getElementLabel(fElement, JavaElementLabels.ALL_FULLY_QUALIFIED);
			return Messages.format(JavaEditorMessages.JavaElementImplementationHyperlink_hyperlinkText_qualified, new Object[] { elementLabel });
		} else {
			return JavaEditorMessages.JavaElementImplementationHyperlink_hyperlinkText;
		}
	}

	/*
	 * @see descent.internal.ui.javaeditor.IHyperlink#getTypeLabel()
	 */
	public String getTypeLabel() {
		return null;
	}

	/**
	 * Opens the given implementation hyperlink for methods.
	 * <p>
	 * If there's only one implementor that hyperlink is opened in the editor, otherwise the
	 * Quick Hierarchy is opened.
	 * </p>
	 */
	public void open() {
		ITypeRoot editorInput= EditorUtility.getEditorInputJavaElement(fEditor, false);

		CompilationUnit ast= ASTProvider.getASTProvider().getAST(editorInput, ASTProvider.WAIT_ACTIVE_ONLY, null);
		if (ast == null) {
			openQuickHierarchy();
			return;
		}

		ASTNode node= NodeFinder.perform(ast, fRegion.getOffset(), fRegion.getLength());
		ITypeBinding parentTypeBinding= null;
		if (node instanceof SimpleName) {
			ASTNode parent= node.getParent();
			if (parent instanceof CallExpression) {
				Expression expression= (CallExpression)parent;
				if (expression == null) {
					parentTypeBinding= Bindings.getBindingOfParentType(node);
				} else {
					parentTypeBinding= expression.resolveTypeBinding();
				}
//			} else if (parent instanceof SuperMethodInvocation) {
//				// Directly go to the super method definition
//				fOpenAction.run(new StructuredSelection(fElement));
//				return;
			} else if (parent instanceof FunctionDeclaration) {
				parentTypeBinding= Bindings.getBindingOfParentType(node);
			}
		}
		final IType type= parentTypeBinding != null ? (IType) parentTypeBinding.getJavaElement() : null;
		if (type == null) {
			openQuickHierarchy();
			return;
		}

		final String dummyString= new String();
		final ArrayList links= new ArrayList();
		IRunnableWithProgress runnable= new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (monitor == null) {
					monitor= new NullProgressMonitor();
				}
				try {
					String methodLabel= JavaElementLabels.getElementLabel(fElement, JavaElementLabels.DEFAULT_QUALIFIED);
					monitor.beginTask(Messages.format(JavaEditorMessages.JavaElementImplementationHyperlink_search_method_implementors, methodLabel), 100);
					SearchRequestor requestor= new SearchRequestor() {
						public void acceptSearchMatch(SearchMatch match) throws CoreException {
							if (match.getAccuracy() == SearchMatch.A_ACCURATE) {
								IJavaElement element= (IJavaElement)match.getElement();
								if (element instanceof IMethod && !JdtFlags.isAbstract((IMethod)element)) {
									links.add(element);
									if (links.size() > 1) {
										throw new OperationCanceledException(dummyString);
									}
								}
							}
						}
					};
					int limitTo= IJavaSearchConstants.DECLARATIONS | IJavaSearchConstants.IGNORE_DECLARING_TYPE | IJavaSearchConstants.IGNORE_RETURN_TYPE;
					SearchPattern pattern= SearchPattern.createPattern(fElement, limitTo);
					Assert.isNotNull(pattern);
					SearchParticipant[] participants= new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
					SearchEngine engine= new SearchEngine();
					engine.search(pattern, participants, SearchEngine.createHierarchyScope(type), requestor, new SubProgressMonitor(monitor, 100));

					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};

		try {
			IRunnableContext context= fEditor.getSite().getWorkbenchWindow();
			context.run(true, true, runnable);
		} catch (InvocationTargetException e) {
			IStatus status= new Status(IStatus.ERROR, JavaPlugin.getPluginId(), IStatus.OK,
					Messages.format(JavaEditorMessages.JavaElementImplementationHyperlink_error_status_message, fElement.getElementName()), e.getCause());
			JavaPlugin.log(status);
			ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					JavaEditorMessages.JavaElementImplementationHyperlink_hyperlinkText,
					JavaEditorMessages.JavaElementImplementationHyperlink_error_no_implementations_found_message, status);
		} catch (InterruptedException e) {
			if (e.getMessage() != dummyString) {
				return;
			}
		}

		if (links.size() == 1) {
			fOpenAction.run(new StructuredSelection(links.get(0)));
		} else {
			openQuickHierarchy();
		}
	}

	/**
	 * Opens a quick type hierarchy for the editor's current input.
	 */
	private void openQuickHierarchy() {
		ITextOperationTarget textOperationTarget= (ITextOperationTarget)fEditor.getAdapter(ITextOperationTarget.class);
		textOperationTarget.doOperation(JavaSourceViewer.SHOW_HIERARCHY);
	}
}
