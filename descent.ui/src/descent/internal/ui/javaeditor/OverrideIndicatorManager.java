package descent.internal.ui.javaeditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.ui.PartInitException;

import descent.core.IJavaElement;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.CompilationUnit;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IMethodBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.SimpleName;
import descent.internal.corext.dom.Bindings;
import descent.internal.corext.util.JdtFlags;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.actions.OpenActionUtil;
import descent.internal.ui.text.java.IJavaReconcilingListener;

/**
 * Manages the override and overwrite indicators for
 * the given Java element and annotation model.
 *
 * @since 3.0
 */
class OverrideIndicatorManager implements IJavaReconcilingListener {

	/**
	 * Overwrite and override indicator annotation.
	 *
	 * @since 3.0
	 */
	class OverrideIndicator extends Annotation {

		private boolean fIsOverwriteIndicator;
		private String fAstNodeKey;

		/**
		 * Creates a new override annotation.
		 *
		 * @param isOverwriteIndicator <code>true</code> if this annotation is
		 *            an overwrite indicator, <code>false</code> otherwise
		 * @param text the text associated with this annotation
		 * @param key the method binding key
		 * @since 3.0
		 */
		OverrideIndicator(boolean isOverwriteIndicator, String text, String key) {
			super(ANNOTATION_TYPE, false, text);
			fIsOverwriteIndicator= isOverwriteIndicator;
			fAstNodeKey= key;
		}

		/**
		 * Tells whether this is an overwrite or an override indicator.
		 *
		 * @return <code>true</code> if this is an overwrite indicator
		 */
		public boolean isOverwriteIndicator() {
			return fIsOverwriteIndicator;
		}

		/**
		 * Opens and reveals the defining method.
		 */
		public void open() {
			boolean hasLogEntry= false;
			CompilationUnit ast= JavaPlugin.getDefault().getASTProvider().getAST(fJavaElement, ASTProvider.WAIT_ACTIVE_ONLY, null);
			if (ast != null) {
				ASTNode node= ast.findDeclaringNode(fAstNodeKey);
				if (node instanceof FunctionDeclaration) {
					try {
						IMethodBinding methodBinding= ((FunctionDeclaration)node).resolveBinding();
						IMethodBinding definingMethodBinding= Bindings.findOverriddenMethod(methodBinding, true);
						if (definingMethodBinding != null) {
							IJavaElement definingMethod= definingMethodBinding.getJavaElement();
							if (definingMethod != null) {
								OpenActionUtil.open(definingMethod, true);
								return;
							}
						}
					} catch (JavaModelException ex) {
						JavaPlugin.log(ex.getStatus());
						hasLogEntry= true;
					} catch (PartInitException ex) {
						JavaPlugin.log(ex.getStatus());
						hasLogEntry= true;
					}
				}
			}
			String title= JavaEditorMessages.OverrideIndicatorManager_open_error_title;
			String message;
			if (hasLogEntry)
				message= JavaEditorMessages.OverrideIndicatorManager_open_error_messageHasLogEntry;
			else
				message= JavaEditorMessages.OverrideIndicatorManager_open_error_message;
			MessageDialog.openError(JavaPlugin.getActiveWorkbenchShell(), title, message);
		}
	}

	static final String ANNOTATION_TYPE= "descent.ui.overrideIndicator"; //$NON-NLS-1$

	private IAnnotationModel fAnnotationModel;
	private Object fAnnotationModelLockObject;
	private Annotation[] fOverrideAnnotations;
	private IJavaElement fJavaElement;


	public OverrideIndicatorManager(IAnnotationModel annotationModel, IJavaElement javaElement, CompilationUnit ast) {
		Assert.isNotNull(annotationModel);
		Assert.isNotNull(javaElement);

		fJavaElement= javaElement;
		fAnnotationModel=annotationModel;
		fAnnotationModelLockObject= getLockObject(fAnnotationModel);

		updateAnnotations(ast, new NullProgressMonitor());
	}

	/**
	 * Returns the lock object for the given annotation model.
	 *
	 * @param annotationModel the annotation model
	 * @return the annotation model's lock object
	 * @since 3.0
	 */
	private Object getLockObject(IAnnotationModel annotationModel) {
		if (annotationModel instanceof ISynchronizable) {
			Object lock= ((ISynchronizable)annotationModel).getLockObject();
			if (lock != null)
				return lock;
		}
		return annotationModel;
	}

	/**
	 * Updates the override and implements annotations based
	 * on the given AST.
	 *
	 * @param ast the compilation unit AST
	 * @param progressMonitor the progress monitor
	 * @since 3.0
	 */
	protected void updateAnnotations(CompilationUnit ast, IProgressMonitor progressMonitor) {

		if (ast == null || progressMonitor.isCanceled())
			return;

		final Map annotationMap= new HashMap(50);

		ast.accept(new ASTVisitor() {
			public boolean visit(FunctionDeclaration node) {
				IMethodBinding binding= node.resolveBinding();
				if (binding != null) {
					IMethodBinding definingMethod= Bindings.findOverriddenMethod(binding, true);
					if (definingMethod != null) {

						ITypeBinding definingType= (ITypeBinding) definingMethod.getDeclaringSymbol();
						String qualifiedMethodName= definingType.getQualifiedName() + "." + binding.getName(); //$NON-NLS-1$

						boolean isImplements= JdtFlags.isAbstract(definingMethod);
						String text = null;
						/* TODO JDT UI override indicator
						if (isImplements)
							text= Messages.format(JavaEditorMessages.OverrideIndicatorManager_implements, qualifiedMethodName);
						else
							text= Messages.format(JavaEditorMessages.OverrideIndicatorManager_overrides, qualifiedMethodName);
							*/

						SimpleName name= node.getName();
						Position position= new Position(name.getStartPosition(), name.getLength());

						annotationMap.put(
								new OverrideIndicator(isImplements, text, binding.getKey()), 
								position);

					}
				}
				return true;
			}
		});

		if (progressMonitor.isCanceled())
			return;

		synchronized (fAnnotationModelLockObject) {
			if (fAnnotationModel instanceof IAnnotationModelExtension) {
				((IAnnotationModelExtension)fAnnotationModel).replaceAnnotations(fOverrideAnnotations, annotationMap);
			} else {
				removeAnnotations();
				Iterator iter= annotationMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry mapEntry= (Map.Entry)iter.next();
					fAnnotationModel.addAnnotation((Annotation)mapEntry.getKey(), (Position)mapEntry.getValue());
				}
			}
			fOverrideAnnotations= (Annotation[])annotationMap.keySet().toArray(new Annotation[annotationMap.keySet().size()]);
		}
	}

	/**
	 * Removes all override indicators from this manager's annotation model.
	 */
	void removeAnnotations() {
		if (fOverrideAnnotations == null)
			return;

		synchronized (fAnnotationModelLockObject) {
			if (fAnnotationModel instanceof IAnnotationModelExtension) {
				((IAnnotationModelExtension)fAnnotationModel).replaceAnnotations(fOverrideAnnotations, null);
			} else {
				for (int i= 0, length= fOverrideAnnotations.length; i < length; i++)
					fAnnotationModel.removeAnnotation(fOverrideAnnotations[i]);
			}
			fOverrideAnnotations= null;
		}
	}

	/*
	 * @see descent.internal.ui.text.java.IJavaReconcilingListener#aboutToBeReconciled()
	 */
	public void aboutToBeReconciled() {
	}

	/*
	 * @see descent.internal.ui.text.java.IJavaReconcilingListener#reconciled(CompilationUnit, boolean, IProgressMonitor)
	 */
	public void reconciled(CompilationUnit ast, boolean forced, IProgressMonitor progressMonitor) {
		updateAnnotations(ast, progressMonitor);
	}
}

