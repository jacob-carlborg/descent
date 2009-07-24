package descent.internal.ui.typehierarchy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IType;
import descent.core.ITypeHierarchy;
import descent.core.JavaModelException;

import descent.internal.corext.util.MethodOverrideTester;

import descent.ui.JavaElementLabels;

import descent.internal.ui.viewsupport.AppearanceAwareLabelProvider;

/**
 * Label provider for the hierarchy method viewers. 
 */
public class MethodsLabelProvider extends AppearanceAwareLabelProvider {

	private Color fResolvedBackground;
	
	private boolean fShowDefiningType;
	private TypeHierarchyLifeCycle fHierarchy;
	private MethodsViewer fMethodsViewer;

	public MethodsLabelProvider(TypeHierarchyLifeCycle lifeCycle, MethodsViewer methodsViewer) {
		super(DEFAULT_TEXTFLAGS, DEFAULT_IMAGEFLAGS);
		fHierarchy= lifeCycle;
		fShowDefiningType= false;
		fMethodsViewer= methodsViewer;
		fResolvedBackground= null;
	}
	
	public void setShowDefiningType(boolean showDefiningType) {
		fShowDefiningType= showDefiningType;
	}
	
	public boolean isShowDefiningType() {
		return fShowDefiningType;
	}	
			

	private IType getDefiningType(Object element) throws JavaModelException {
		int kind= ((IJavaElement) element).getElementType();
	
		if (kind != IJavaElement.METHOD && kind != IJavaElement.FIELD && kind != IJavaElement.INITIALIZER) {
			return null;
		}
		IType declaringType= ((IMember) element).getDeclaringType();
		if (kind != IJavaElement.METHOD) {
			return declaringType;
		}
		ITypeHierarchy hierarchy= fHierarchy.getHierarchy();
		if (hierarchy == null) {
			return declaringType;
		}
		IMethod method= (IMethod) element;
		MethodOverrideTester tester= new MethodOverrideTester(declaringType, hierarchy);
		IMethod res= tester.findDeclaringMethod(method, true);
		if (res == null || method.equals(res)) {
			return declaringType;
		}
		return res.getDeclaringType();
	}

	/* (non-Javadoc)
	 * @see ILabelProvider#getText
	 */ 	
	public String getText(Object element) {
		String text= super.getText(element);
		if (fShowDefiningType) {
			try {
				IType type= getDefiningType(element);
				if (type != null) {
					StringBuffer buf= new StringBuffer(super.getText(type));
					buf.append(JavaElementLabels.CONCAT_STRING);
					buf.append(text);
					return buf.toString();			
				}
			} catch (JavaModelException e) {
			}
		}
		return text;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {
		if (fMethodsViewer.isShowInheritedMethods() && element instanceof IMethod) {
			IMethod curr= (IMethod) element;
			IMember declaringType= curr.getDeclaringType();
			
			if (declaringType.equals(fMethodsViewer.getInput())) {
				if (fResolvedBackground == null) {
					Display display= Display.getCurrent();
					fResolvedBackground= display.getSystemColor(SWT.COLOR_DARK_BLUE);
				}
				return fResolvedBackground;
			}
		}
		return null;
	}
	
}
