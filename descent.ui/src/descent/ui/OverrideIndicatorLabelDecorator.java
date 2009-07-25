package descent.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import descent.core.Flags;
import descent.core.IJavaElement;
import descent.core.IMethod;
import descent.core.IType;
import descent.core.ITypeHierarchy;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IMethodBinding;
import descent.core.dom.SimpleName;
import descent.internal.corext.dom.Bindings;
import descent.internal.corext.dom.NodeFinder;
import descent.internal.corext.util.JavaModelUtil;
import descent.internal.corext.util.JdtFlags;
import descent.internal.corext.util.MethodOverrideTester;
import descent.internal.corext.util.SuperTypeHierarchyCache;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.javaeditor.ASTProvider;
import descent.internal.ui.viewsupport.ImageDescriptorRegistry;
import descent.internal.ui.viewsupport.ImageImageDescriptor;

/**
 * LabelDecorator that decorates an method's image with override or implements overlays.
 * The viewer using this decorator is responsible for updating the images on element changes.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class OverrideIndicatorLabelDecorator implements ILabelDecorator, ILightweightLabelDecorator {

	private ImageDescriptorRegistry fRegistry;
	private boolean fUseNewRegistry= false;

	/**
	 * Creates a decorator. The decorator creates an own image registry to cache
	 * images. 
	 */
	public OverrideIndicatorLabelDecorator() {
		this(null);
		fUseNewRegistry= true;
	}	

	/*
	 * Creates decorator with a shared image registry.
	 * 
	 * @param registry The registry to use or <code>null</code> to use the Java plugin's
	 * image registry.
	 */	
	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param registry The registry to use.
	 */
	public OverrideIndicatorLabelDecorator(ImageDescriptorRegistry registry) {
		fRegistry= registry;
	}
	
	private ImageDescriptorRegistry getRegistry() {
		if (fRegistry == null) {
			fRegistry= fUseNewRegistry ? new ImageDescriptorRegistry() : JavaPlugin.getImageDescriptorRegistry();
		}
		return fRegistry;
	}	
	
	
	/* (non-Javadoc)
	 * @see ILabelDecorator#decorateText(String, Object)
	 */
	public String decorateText(String text, Object element) {
		return text;
	}	

	/* (non-Javadoc)
	 * @see ILabelDecorator#decorateImage(Image, Object)
	 */
	public Image decorateImage(Image image, Object element) {
		int adornmentFlags= computeAdornmentFlags(element);
		if (adornmentFlags != 0) {
			ImageDescriptor baseImage= new ImageImageDescriptor(image);
			Rectangle bounds= image.getBounds();
			return getRegistry().get(new JavaElementImageDescriptor(baseImage, adornmentFlags, new Point(bounds.width, bounds.height)));
		}
		return image;
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * @param element The element to decorate
	 * @return Resulting decorations (combination of JavaElementImageDescriptor.IMPLEMENTS
	 * and JavaElementImageDescriptor.OVERRIDES)
	 */
	public int computeAdornmentFlags(Object element) {
		if (element instanceof IMethod) {
			try {
				IMethod method= (IMethod) element;
				if (!method.getJavaProject().isOnClasspath(method)) {
					return 0;
				}
				long flags= method.getFlags();
				if (!method.isConstructor() && !Flags.isPrivate(flags) && !Flags.isStatic(flags)) {
					int res= getOverrideIndicators(method);
					if (res != 0 && Flags.isSynchronized(flags)) {
						return res | JavaElementImageDescriptor.SYNCHRONIZED;
					}
					return res;
				}
			} catch (JavaModelException e) {
				if (!e.isDoesNotExist()) {
					JavaPlugin.log(e);
				}
			}
		}
		return 0;
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * @param method The element to decorate
	 * @return Resulting decorations (combination of JavaElementImageDescriptor.IMPLEMENTS
	 * and JavaElementImageDescriptor.OVERRIDES)
	 * @throws JavaModelException
	 */
	protected int getOverrideIndicators(IMethod method) throws JavaModelException {
		CompilationUnit astRoot= JavaPlugin.getDefault().getASTProvider().getAST((IJavaElement) method.getOpenable(), ASTProvider.WAIT_NO, null);
		if (astRoot != null) {
			int res= findInHierarchyWithAST(astRoot, method);
			if (res != -1) {
				return res;
			}
		}
		
		IType type= method.getDeclaringType();
		
		MethodOverrideTester methodOverrideTester= SuperTypeHierarchyCache.getMethodOverrideTester(type);
		IMethod defining= methodOverrideTester.findOverriddenMethod(method, true);
		if (defining != null) {
			if (JdtFlags.isAbstract(defining)) {
				return JavaElementImageDescriptor.IMPLEMENTS;
			} else {
				return JavaElementImageDescriptor.OVERRIDES;
			}
		}
		return 0;
	}
	
	private int findInHierarchyWithAST(CompilationUnit astRoot, IMethod method) throws JavaModelException {
		ASTNode node= NodeFinder.perform(astRoot, method.getNameRange());
		if (node instanceof SimpleName && node.getParent() instanceof FunctionDeclaration) {
			IMethodBinding binding= ((FunctionDeclaration) node.getParent()).resolveBinding();
			if (binding != null) {
				IMethodBinding defining= Bindings.findOverriddenMethod(binding, true);
				if (defining != null) {
					if (JdtFlags.isAbstract(defining)) {
						return JavaElementImageDescriptor.IMPLEMENTS;
					} else {
						return JavaElementImageDescriptor.OVERRIDES;
					}
				}
				return 0;
			}
		}		
		return -1;
	}

	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * @param type The declaring type of the method to decorate.
	 * @param hierarchy The type hierarchy of the declaring type.
	 * @param name The name of the method to find.
	 * @param paramTypes The parameter types of the method to find.
	 * @return The resulting decoration.
	 * @throws JavaModelException
	 * @deprecated Not used anymore. This method is not accurate for methods in generic types.
	 */
	protected int findInHierarchy(IType type, ITypeHierarchy hierarchy, String name, String[] paramTypes) throws JavaModelException {
		IType superClass= hierarchy.getSuperclass(type);
		if (superClass != null) {
			IMethod res= JavaModelUtil.findMethodInHierarchy(hierarchy, superClass, name, paramTypes, false);
			if (res != null && !Flags.isPrivate(res.getFlags()) && JavaModelUtil.isVisibleInHierarchy(res, type.getPackageFragment())) {
				if (JdtFlags.isAbstract(res)) {
					return JavaElementImageDescriptor.IMPLEMENTS;
				} else {
					return JavaElementImageDescriptor.OVERRIDES;
				}
			}
		}
		IType[] interfaces= hierarchy.getSuperInterfaces(type);
		for (int i= 0; i < interfaces.length; i++) {
			IMethod res= JavaModelUtil.findMethodInHierarchy(hierarchy, interfaces[i], name, paramTypes, false);
			if (res != null) {
				if (JdtFlags.isAbstract(res)) {
					return JavaElementImageDescriptor.IMPLEMENTS;
				} else {
					return JavaElementImageDescriptor.OVERRIDES;
				}
			}
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/* (non-Javadoc)
	 * @see IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		if (fRegistry != null && fUseNewRegistry) {
			fRegistry.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	/* (non-Javadoc)
	 * @see IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) { 
		int adornmentFlags= computeAdornmentFlags(element);
		if ((adornmentFlags & JavaElementImageDescriptor.IMPLEMENTS) != 0) {
			if ((adornmentFlags & JavaElementImageDescriptor.SYNCHRONIZED) != 0) {
				decoration.addOverlay(JavaPluginImages.DESC_OVR_SYNCH_AND_IMPLEMENTS);
			} else {
				decoration.addOverlay(JavaPluginImages.DESC_OVR_IMPLEMENTS);
			}
		} else if ((adornmentFlags & JavaElementImageDescriptor.OVERRIDES) != 0) {
			if ((adornmentFlags & JavaElementImageDescriptor.SYNCHRONIZED) != 0) {
				decoration.addOverlay(JavaPluginImages.DESC_OVR_SYNCH_AND_OVERRIDES);
			} else {
				decoration.addOverlay(JavaPluginImages.DESC_OVR_OVERRIDES);
			}
		}
	}

}
