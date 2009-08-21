package descent.internal.ui.javaeditor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import org.eclipse.ui.texteditor.ITextEditor;

import descent.core.ICompilationUnit;
import descent.core.IConditional;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.JavaModelException;

import descent.internal.corext.util.JdtFlags;

import descent.ui.actions.SelectionDispatchAction;

import descent.internal.ui.JavaPlugin;


/**
 * Java element implementation hyperlink detector for methods.
 * 
 * @since 3.5
 */
public class JavaElementHyperlinkImplementationDetector extends JavaElementHyperlinkDetector {

	/*
	 * @see descent.internal.ui.javaeditor.JavaElementHyperlinkDetector#createHyperlink(org.eclipse.jface.text.IRegion, descent.ui.actions.SelectionDispatchAction, descent.core.IJavaElement, boolean, org.eclipse.ui.texteditor.ITextEditor)
	 * @since 3.5
	 */
	protected IHyperlink createHyperlink(IRegion wordRegion, SelectionDispatchAction openAction, IJavaElement element, boolean qualify, ITextEditor editor) {
		if (element.getElementType() == IJavaElement.METHOD && canBeOverridden((IMethod)element)) {
			return new JavaElementImplementationHyperlink(wordRegion, openAction, element, qualify, editor);
		}
		return null;
	}

	/**
	 * Checks whether a method can be overridden.
	 * 
	 * @param method the method
	 * @return <code>false</code> if the method is final, static, or a constructor, or if its declaring
	 *         class is final, or in case of an exception, <code>true</code> otherwise
	 */
	private boolean canBeOverridden(IMethod method) {
		IJavaElement parent = method.getParent();
		while(parent instanceof IConditional) {
			parent = parent.getParent();
		}
		
		// Functions can't be overriden: only methods
		if (parent instanceof ICompilationUnit) {
			return false;
		}
		
		try {
			return !(JdtFlags.isPrivate(method) || JdtFlags.isFinal(method) || JdtFlags.isStatic(method) || method.isConstructor() /* || JdtFlags.isFinal((IMember)method.getParent()) */);
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
			return false;
		}
	}
}
