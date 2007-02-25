/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Chapman, mpchapman@gmail.com - 89977 Make JDT .java agnostic
 *******************************************************************************/
package descent.internal.ui.viewsupport;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;

import descent.core.Flags;
import descent.core.IConditional;
import descent.core.IField;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IType;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.internal.corext.util.JavaModelUtil;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.JavaUIMessages;
import descent.ui.JavaElementImageDescriptor;

/**
 * Default strategy of the Java plugin for the construction of Java element icons.
 */
public class JavaElementImageProvider {
	

	/**
	 * Flags for the JavaImageLabelProvider:
	 * Generate images with overlays.
	 */
	public final static int OVERLAY_ICONS= 0x1;

	/**
	 * Generate small sized images.
	 */
	public final static int SMALL_ICONS= 0x2;
	
	/**
	 * Use the 'light' style for rendering types.
	 */	
	public final static int LIGHT_TYPE_ICONS= 0x4;	


	public static final Point SMALL_SIZE= new Point(16, 16);
	public static final Point BIG_SIZE= new Point(22, 16);

	private static ImageDescriptor DESC_OBJ_PROJECT_CLOSED;	
	private static ImageDescriptor DESC_OBJ_PROJECT;	
	{
		ISharedImages images= JavaPlugin.getDefault().getWorkbench().getSharedImages(); 
		DESC_OBJ_PROJECT_CLOSED= images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
		DESC_OBJ_PROJECT= 		 images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
	}
	
	private ImageDescriptorRegistry fRegistry;
		
	public JavaElementImageProvider() {
		fRegistry= null; // lazy initialization
	}	
		
	/**
	 * Returns the icon for a given element. The icon depends on the element type
	 * and element properties. If configured, overlay icons are constructed for
	 * <code>ISourceReference</code>s.
	 * @param flags Flags as defined by the JavaImageLabelProvider
	 */
	public Image getImageLabel(Object element, int flags) {
		return getImageLabel(computeDescriptor(element, flags));
	}
	
	private Image getImageLabel(ImageDescriptor descriptor){
		if (descriptor == null) 
			return null;	
		return getRegistry().get(descriptor);
	}
	
	private ImageDescriptorRegistry getRegistry() {
		if (fRegistry == null) {
			fRegistry= JavaPlugin.getImageDescriptorRegistry();
		}
		return fRegistry;
	}
	

	private ImageDescriptor computeDescriptor(Object element, int flags){
		if (element instanceof IJavaElement) {
			return getJavaImageDescriptor((IJavaElement) element, flags);
		} else if (element instanceof IFile) {
			IFile file= (IFile) element;
			if (JavaCore.isJavaLikeFileName(file.getName())) {
				return getCUResourceImageDescriptor(file, flags); // image for a CU not on the build path
			}
			return getWorkbenchImageDescriptor(file, flags);
		} else if (element instanceof IAdaptable) {
			return getWorkbenchImageDescriptor((IAdaptable) element, flags);
		}
		return null;
	}
	
	private static boolean showOverlayIcons(int flags) {
		return (flags & OVERLAY_ICONS) != 0;
	}
		
	private static boolean useSmallSize(int flags) {
		return (flags & SMALL_ICONS) != 0;
	}
	
	private static boolean useLightIcons(int flags) {
		return (flags & LIGHT_TYPE_ICONS) != 0;
	}	

	/**
	 * Returns an image descriptor for a compilation unit not on the class path.
	 * The descriptor includes overlays, if specified.
	 */
	public ImageDescriptor getCUResourceImageDescriptor(IFile file, int flags) {
		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new JavaElementImageDescriptor(JavaPluginImages.DESC_OBJS_CUNIT_RESOURCE, 0, size);
	}	
		
	/**
	 * Returns an image descriptor for a java element. The descriptor includes overlays, if specified.
	 */
	public ImageDescriptor getJavaImageDescriptor(IJavaElement element, int flags) {
		int adornmentFlags= computeJavaAdornmentFlags(element, flags);
		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new JavaElementImageDescriptor(getBaseImageDescriptor(element, flags), adornmentFlags, size);
	}

	/**
	 * Returns an image descriptor for a IAdaptable. The descriptor includes overlays, if specified (only error ticks apply).
	 * Returns <code>null</code> if no image could be found.
	 */	
	public ImageDescriptor getWorkbenchImageDescriptor(IAdaptable adaptable, int flags) {
		IWorkbenchAdapter wbAdapter= (IWorkbenchAdapter) adaptable.getAdapter(IWorkbenchAdapter.class);
		if (wbAdapter == null) {
			return null;
		}
		ImageDescriptor descriptor= wbAdapter.getImageDescriptor(adaptable);
		if (descriptor == null) {
			return null;
		}

		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new JavaElementImageDescriptor(descriptor, 0, size);
	}
	
	// ---- Computation of base image key -------------------------------------------------
	
	/**
	 * Returns an image descriptor for a java element. This is the base image, no overlays.
	 */
	public ImageDescriptor getBaseImageDescriptor(IJavaElement element, int renderFlags) {

		try {			
			switch (element.getElementType()) {	
				case IJavaElement.CONDITIONAL:
					IConditional cond = (IConditional) element;
					if (cond.isDebugDeclaration()) {
						return JavaPluginImages.DESC_OBJS_DEBUG_DECLARATION;
					} else if (cond.isIftypeDeclaration()) {
						return JavaPluginImages.DESC_OBJS_IFTYPE;
					} else if (cond.isStaticIfDeclaration()) {
						return JavaPluginImages.DESC_OBJS_STATIC_IF;
					} else {
						return JavaPluginImages.DESC_OBJS_VERSION_DECLARATION;
					}
				case IJavaElement.INITIALIZER:
					IInitializer init = (IInitializer) element;
					if (init.isStaticConstructor() || init.isStaticDestructor()) {
						return JavaPluginImages.DESC_MISC_PRIVATE; // 23479
					} else if (init.isInvariant()) {
						return JavaPluginImages.DESC_OBJS_INVARIANT;
					} else if (init.isUnitTest()) {
						return JavaPluginImages.DESC_OBJS_UNITTEST;
					} else if (init.isStaticAssert()) {
						return JavaPluginImages.DESC_OBJS_STATIC_ASSERT;
					} else if (init.isDebugAssignment()) {
						return JavaPluginImages.DESC_OBJS_DEBUG_ASSIGNMENT;
					} else if (init.isVersionAssignment()) {
						return JavaPluginImages.DESC_OBJS_VERSION_ASSIGNMENT;
					} else if (init.isAlign()) {
						return JavaPluginImages.DESC_OBJS_ALIGN;
					} else if (init.isExtern()) {
						return JavaPluginImages.DESC_OBJS_EXTERN;
					} else if (init.isPragma()) {
						return JavaPluginImages.DESC_OBJS_PRAGMA;
					} else if (init.isThen() || init.isElse()) {
						return JavaPluginImages.DESC_OBJS_THEN_ELSE;
					} else {
						return JavaPluginImages.DESC_OBJS_MIXIN;
					}
				case IJavaElement.METHOD: {
					IMethod method= (IMethod) element;
					IType declType= method.getDeclaringType();
					int flags= method.getFlags();
					if (declType != null && declType.isEnum() && isDefaultFlag(flags) && method.isConstructor())
						return JavaPluginImages.DESC_MISC_PRIVATE;
					return getMethodImageDescriptor(JavaModelUtil.isInterfaceOrAnnotation(declType), flags);				
				}
				case IJavaElement.FIELD: {
					IField field= (IField) element;
					if (field.isVariable() || field.isEnumConstant()) {
						IType declType= field.getDeclaringType();
						return getVariableImageDescriptor(JavaModelUtil.isInterfaceOrAnnotation(declType), field.getFlags());
					} else if (field.isAlias()) {
						return getAliasImageDescriptor(field.getFlags());
					} else if (field.isTypedef()) {
						return getTypedefImageDescriptor(field.getFlags());
					} else {
						return getTemplateMixinImageDescriptor(field.getFlags());
					}
				}
				case IJavaElement.LOCAL_VARIABLE:
					return JavaPluginImages.DESC_OBJS_LOCAL_VARIABLE;				

				case IJavaElement.PACKAGE_DECLARATION:
					return JavaPluginImages.DESC_OBJS_PACKDECL;
				
				case IJavaElement.IMPORT_DECLARATION:
					return JavaPluginImages.DESC_OBJS_IMPDECL;
					
				case IJavaElement.IMPORT_CONTAINER:
					return JavaPluginImages.DESC_OBJS_IMPCONT;
				
				case IJavaElement.TYPE: {
					IType type= (IType) element;

					IType declType= type.getDeclaringType();
					boolean isInner= declType != null;
					boolean isInInterfaceOrAnnotation= isInner && JavaModelUtil.isInterfaceOrAnnotation(declType);
					return getTypeImageDescriptor(isInner, isInInterfaceOrAnnotation, type.getFlags(), useLightIcons(renderFlags));
				}

				case IJavaElement.PACKAGE_FRAGMENT_ROOT: {
					IPackageFragmentRoot root= (IPackageFragmentRoot) element;
					if (root.isArchive()) {
						IPath attach= root.getSourceAttachmentPath();
						if (root.isExternal()) {
							if (attach == null) {
								return JavaPluginImages.DESC_OBJS_EXTJAR;
							} else {
								return JavaPluginImages.DESC_OBJS_EXTJAR_WSRC;
							}
						} else {
							if (attach == null) {
								return JavaPluginImages.DESC_OBJS_JAR;
							} else {
								return JavaPluginImages.DESC_OBJS_JAR_WSRC;
							}
						}							
					} else {
						return JavaPluginImages.DESC_OBJS_PACKFRAG_ROOT;
					}
				}
				
				case IJavaElement.PACKAGE_FRAGMENT:
					return getPackageFragmentIcon(element, renderFlags);

					
				case IJavaElement.COMPILATION_UNIT:
					return JavaPluginImages.DESC_OBJS_CUNIT;
					
				case IJavaElement.CLASS_FILE:
					/* this is too expensive for large packages
					try {
						IClassFile cfile= (IClassFile)element;
						if (cfile.isClass())
							return JavaPluginImages.IMG_OBJS_CFILECLASS;
						return JavaPluginImages.IMG_OBJS_CFILEINT;
					} catch(JavaModelException e) {
						// fall through;
					}*/
					return JavaPluginImages.DESC_OBJS_CFILE;
					
				case IJavaElement.JAVA_PROJECT: 
					IJavaProject jp= (IJavaProject)element;
					if (jp.getProject().isOpen()) {
						IProject project= jp.getProject();
						IWorkbenchAdapter adapter= (IWorkbenchAdapter)project.getAdapter(IWorkbenchAdapter.class);
						if (adapter != null) {
							ImageDescriptor result= adapter.getImageDescriptor(project);
							if (result != null)
								return result;
						}
						return DESC_OBJ_PROJECT;
					}
					return DESC_OBJ_PROJECT_CLOSED;
					
				case IJavaElement.JAVA_MODEL:
					return JavaPluginImages.DESC_OBJS_JAVA_MODEL;

				case IJavaElement.TYPE_PARAMETER:
					return JavaPluginImages.DESC_OBJS_LOCAL_VARIABLE;
			}
			
			Assert.isTrue(false, JavaUIMessages.JavaImageLabelprovider_assert_wrongImage); 
			return JavaPluginImages.DESC_OBJS_GHOST;
		
		} catch (JavaModelException e) {
			if (e.isDoesNotExist())
				return JavaPluginImages.DESC_OBJS_UNKNOWN;
			JavaPlugin.log(e);
			return JavaPluginImages.DESC_OBJS_GHOST;
		}
	}
	
	private static boolean isDefaultFlag(int flags) {
		//return !Flags.isPublic(flags) && !Flags.isProtected(flags) && !Flags.isPrivate(flags);
		return Flags.isPackage(flags);
	}
	
	protected ImageDescriptor getPackageFragmentIcon(IJavaElement element, int renderFlags) throws JavaModelException {
		IPackageFragment fragment= (IPackageFragment)element;
		boolean containsJavaElements= false;
		try {
			containsJavaElements= fragment.hasChildren();
		} catch(JavaModelException e) {
			// assuming no children;
		}
		if(!containsJavaElements && (fragment.getNonJavaResources().length > 0))
			return JavaPluginImages.DESC_OBJS_EMPTY_PACKAGE_RESOURCES;
		else if (!containsJavaElements)
			return JavaPluginImages.DESC_OBJS_EMPTY_PACKAGE;
		return JavaPluginImages.DESC_OBJS_PACKAGE;
	}
	
	public void dispose() {
	}	

	// ---- Methods to compute the adornments flags ---------------------------------
	
	private int computeJavaAdornmentFlags(IJavaElement element, int renderFlags) {
		int flags= 0;
		if (showOverlayIcons(renderFlags) && element instanceof IMember) {
			try {
				IMember member= (IMember) element;
				
				if (element.getElementType() == IJavaElement.METHOD && ((IMethod)element).isConstructor())
					flags |= JavaElementImageDescriptor.CONSTRUCTOR;
					
				int modifiers= member.getFlags();
				if (Flags.isAbstract(modifiers) && confirmAbstract(member))
					flags |= JavaElementImageDescriptor.ABSTRACT;
				if (Flags.isFinal(modifiers) /*|| isInterfaceOrAnnotationField(member) || isEnumConstant(member, modifiers) */)
					flags |= JavaElementImageDescriptor.FINAL;
				if (Flags.isSynchronized(modifiers) && confirmSynchronized(member))
					flags |= JavaElementImageDescriptor.SYNCHRONIZED;
				if (Flags.isStatic(modifiers) /*|| isInterfaceOrAnnotationFieldOrType(member) || isEnumConstant(member, modifiers)*/)
					flags |= JavaElementImageDescriptor.STATIC;
				
				if (Flags.isDeprecated(modifiers))
					flags |= JavaElementImageDescriptor.DEPRECATED;
				
				if (member.getElementType() == IJavaElement.TYPE) {
					if (JavaModelUtil.hasMainMethod((IType) member)) {
						flags |= JavaElementImageDescriptor.RUNNABLE;
					}
				}
			} catch (JavaModelException e) {
				// do nothing. Can't compute runnable adornment or get flags
			}
		}
		return flags;
	}
		
	private static boolean confirmAbstract(IMember element) throws JavaModelException {
		// never show the abstract symbol on interfaces or members in interfaces
		/*
		if (element.getElementType() == IJavaElement.TYPE) {
			return ! JavaModelUtil.isInterfaceOrAnnotation((IType) element);
		}
		return ! JavaModelUtil.isInterfaceOrAnnotation(element.getDeclaringType());
		*/
		if (element.getElementType() == IJavaElement.TYPE) {
			return ((IType) element).isClass();
		} else {
			return element.getElementType() == IJavaElement.METHOD;	
		}
	}

	private static boolean confirmSynchronized(IJavaElement member) {
		// Synchronized types are allowed but meaningless.
		return member.getElementType() != IJavaElement.TYPE;
	}
	
	
	public static ImageDescriptor getMethodImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPackage(flags) || isInInterfaceOrAnnotation)
			return JavaPluginImages.DESC_MISC_DEFAULT;
		if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_MISC_PROTECTED;
		if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_MISC_PRIVATE;
		
		return JavaPluginImages.DESC_MISC_PUBLIC;
	}
		
	public static ImageDescriptor getVariableImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isEnum(flags))
			return JavaPluginImages.DESC_FIELD_PUBLIC;
		if (Flags.isPackage(flags))
			return JavaPluginImages.DESC_FIELD_DEFAULT;
		if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_FIELD_PROTECTED;
		if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_FIELD_PRIVATE;
			
		return JavaPluginImages.DESC_FIELD_PUBLIC;
	}
	
	public static ImageDescriptor getAliasImageDescriptor(int flags) {
		if (Flags.isPackage(flags))
			return JavaPluginImages.DESC_ALIAS_DEFAULT;
		if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_ALIAS_PROTECTED;
		if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_ALIAS_PRIVATE;
			
		return JavaPluginImages.DESC_ALIAS_PUBLIC;
	}
	
	public static ImageDescriptor getTypedefImageDescriptor(int flags) {
		if (Flags.isPackage(flags))
			return JavaPluginImages.DESC_TYPEDEF_DEFAULT;
		if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_TYPEDEF_PROTECTED;
		if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_TYPEDEF_PRIVATE;
			
		return JavaPluginImages.DESC_TYPEDEF_PUBLIC;
	}
	
	public static ImageDescriptor getTemplateMixinImageDescriptor(int flags) {
		return JavaPluginImages.DESC_TEMPLATE_MIXIN_DEFAULT;
	}
	
	public static ImageDescriptor getMixinImageDescriptor(int flags) {
		return JavaPluginImages.DESC_OBJS_MIXIN;
	}
	
	public static ImageDescriptor getInvariantImageDescriptor(int flags) {
		return JavaPluginImages.DESC_OBJS_INVARIANT;
	}
	
	public static ImageDescriptor getUnitTestImageDescriptor(int flags) {
		return JavaPluginImages.DESC_OBJS_UNITTEST;
	}
	
	/**
	 * @deprecated
	 */
	/*
	public static ImageDescriptor getTypeImageDescriptor(boolean isInterface, boolean isInner, boolean isInInterface, int flags) {
		return getTypeImageDescriptor(isInner, isInInterface, isInterface ? flags | Flags.AccInterface : flags, false);
	}
	*/
	
	public static ImageDescriptor getTypeImageDescriptor(boolean isInner, boolean isInInterfaceOrAnnotation, int flags, boolean useLightIcons) {
		if (Flags.isEnum(flags)) {
			if (useLightIcons) {
				return JavaPluginImages.DESC_OBJS_ENUM_ALT;
			}
			if (isInner) {
				return getInnerEnumImageDescriptor(isInInterfaceOrAnnotation, flags);
			}
			return getEnumImageDescriptor(flags);
		/* TODO JDT UI images
		} else if (Flags.isAnnotation(flags)) {
			if (useLightIcons) {
				return JavaPluginImages.DESC_OBJS_ANNOTATION_ALT;
			}
			if (isInner) {
				return getInnerAnnotationImageDescriptor(isInInterfaceOrAnnotation, flags);
			}
			return getAnnotationImageDescriptor(flags);
		*/
		}  else if (Flags.isInterface(flags)) {
			if (useLightIcons) {
				return JavaPluginImages.DESC_OBJS_INTERFACEALT;
			}
			if (isInner) {
				return getInnerInterfaceImageDescriptor(isInInterfaceOrAnnotation, flags);
			}
			return getInterfaceImageDescriptor(flags);
		}  else if (Flags.isStruct(flags)) {
			if (useLightIcons) {
				return JavaPluginImages.DESC_OBJS_STRUCTALT;
			}
			if (isInner) {
				return getInnerStructImageDescriptor(isInInterfaceOrAnnotation, flags);
			}
			return getStructImageDescriptor(flags);
		}  else if (Flags.isUnion(flags)) {
			if (useLightIcons) {
				return JavaPluginImages.DESC_OBJS_UNIONALT;
			}
			if (isInner) {
				return getInnerUnionImageDescriptor(isInInterfaceOrAnnotation, flags);
			}
			return getUnionImageDescriptor(flags);
		}  else if (Flags.isTemplate(flags)) {
			if (useLightIcons) {
				return JavaPluginImages.DESC_OBJS_TEMPLATE_ELEMENTALT;
			}
			if (isInner) {
				return getInnerTemplateImageDescriptor(isInInterfaceOrAnnotation, flags);
			}
			return getTemplateImageDescriptor(flags);
		} else {
			if (useLightIcons) {
				return JavaPluginImages.DESC_OBJS_CLASSALT;
			}
			if (isInner) {
				return getInnerClassImageDescriptor(isInInterfaceOrAnnotation, flags);
			}
			return getClassImageDescriptor(flags);
		}
	}
	
	
	public static Image getDecoratedImage(ImageDescriptor baseImage, int adornments, Point size) {
		return JavaPlugin.getImageDescriptorRegistry().get(new JavaElementImageDescriptor(baseImage, adornments, size));
	}
	

	private static ImageDescriptor getClassImageDescriptor(int flags) {
		if (Flags.isPackage(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_CLASS_DEFAULT;
		else
			return JavaPluginImages.DESC_OBJS_CLASS;
	}
	
	private static ImageDescriptor getInnerClassImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPackage(flags))
			return JavaPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT;
		else if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_INNER_CLASS_PRIVATE;
		else if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_OBJS_INNER_CLASS_PROTECTED;
		else
			return JavaPluginImages.DESC_OBJS_INNER_CLASS_PUBLIC;
	}
	
	private static ImageDescriptor getEnumImageDescriptor(int flags) {
		if (Flags.isPackage(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_ENUM_DEFAULT;
		else
			return JavaPluginImages.DESC_OBJS_ENUM;
	}
	
	private static ImageDescriptor getInnerEnumImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPackage(flags))
			return JavaPluginImages.DESC_OBJS_ENUM_DEFAULT;
		else if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_ENUM_PRIVATE;
		else if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_OBJS_ENUM_PROTECTED;
		else
			return JavaPluginImages.DESC_OBJS_ENUM;
	}
	
	/*
	private static ImageDescriptor getAnnotationImageDescriptor(int flags) {
		if (Flags.isPublic(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_ANNOTATION;
		else
			return JavaPluginImages.DESC_OBJS_ANNOTATION_DEFAULT;
	}
	
	private static ImageDescriptor getInnerAnnotationImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
			return JavaPluginImages.DESC_OBJS_ANNOTATION;
		else if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_ANNOTATION_PRIVATE;
		else if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_OBJS_ANNOTATION_PROTECTED;
		else
			return JavaPluginImages.DESC_OBJS_ANNOTATION_DEFAULT;
	}
	*/
	
	private static ImageDescriptor getInterfaceImageDescriptor(int flags) {
		if (Flags.isPackage(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_INTERFACE_DEFAULT;
		else	
			return JavaPluginImages.DESC_OBJS_INTERFACE;
	}
	
	private static ImageDescriptor getStructImageDescriptor(int flags) {
		if (Flags.isPackage(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_STRUCT_DEFAULT;
		else
			return JavaPluginImages.DESC_OBJS_STRUCT;			
	}
	
	private static ImageDescriptor getUnionImageDescriptor(int flags) {
		if (Flags.isPackage(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_UNION_DEFAULT;
		else
			return JavaPluginImages.DESC_OBJS_UNION;
	}
	
	private static ImageDescriptor getTemplateImageDescriptor(int flags) {
		if (Flags.isPackage(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_TEMPLATE_ELEMENT_DEFAULT;
		else
			return JavaPluginImages.DESC_OBJS_TEMPLATE_ELEMENT;
	}
	
	private static ImageDescriptor getInnerInterfaceImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPackage(flags))
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT;
		else if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE;
		else if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED;
		else
			return JavaPluginImages.DESC_OBJS_INTERFACE;
	}
	
	private static ImageDescriptor getInnerStructImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		/* TODO JDT UI images
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC;
		else if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE;
		else if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED;
		else
			return JavaPluginImages.DESC_OBJS_INTERFACE_DEFAULT;
		*/
		return getStructImageDescriptor(flags);
	}
	
	private static ImageDescriptor getInnerUnionImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		/* TODO JDT UI images
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC;
		else if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE;
		else if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED;
		else
			return JavaPluginImages.DESC_OBJS_INTERFACE_DEFAULT;
		*/
		return getUnionImageDescriptor(flags);
	}
	
	private static ImageDescriptor getInnerTemplateImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		/* TODO JDT UI images
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC;
		else if (Flags.isPrivate(flags))
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE;
		else if (Flags.isProtected(flags))
			return JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED;
		else
			return JavaPluginImages.DESC_OBJS_INTERFACE_DEFAULT;
		*/
		return getTemplateImageDescriptor(flags);
	}
	
}
