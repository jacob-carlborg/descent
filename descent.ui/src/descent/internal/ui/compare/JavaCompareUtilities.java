package descent.internal.ui.compare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.Image;

import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.dom.AggregateDeclaration;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.ui.JavaElementLabels;
import descent.ui.text.JavaTextTools;

class JavaCompareUtilities {
	
	private static final char PACKAGEDECLARATION= '%';
	private static final char IMPORTDECLARATION= '#';
	private static final char IMPORT_CONTAINER= '<';
	private static final char FIELD= '^';
	private static final char METHOD= '~';
	private static final char INITIALIZER= '|';
	private static final char COMPILATIONUNIT= '{';
	private static final char TYPE= '[';
	private static final char CONDITIONAL= '&';
			
	static String getString(ResourceBundle bundle, String key, String dfltValue) {
		
		if (bundle != null) {
			try {
				return bundle.getString(key);
			} catch (MissingResourceException x) {
				// NeedWork
			}
		}
		return dfltValue;
	}
	
	static String getString(ResourceBundle bundle, String key) {
		return getString(bundle, key, key);
	}
	
	static int getInteger(ResourceBundle bundle, String key, int dfltValue) {
		
		if (bundle != null) {
			try {
				String s= bundle.getString(key);
				if (s != null)
					return Integer.parseInt(s);
			} catch (NumberFormatException x) {
				// NeedWork
			} catch (MissingResourceException x) {
				// NeedWork
			}
		}
		return dfltValue;
	}

	static ImageDescriptor getImageDescriptor(int type) {
		switch (type) {
		case IJavaElement.INITIALIZER:
		case IJavaElement.METHOD:
		case IJavaElement.CONDITIONAL:
			return getImageDescriptor("obj16/compare_method.gif"); //$NON-NLS-1$			
		case IJavaElement.FIELD:
			return getImageDescriptor("obj16/compare_field.gif"); //$NON-NLS-1$
		case IJavaElement.PACKAGE_DECLARATION:
			return JavaPluginImages.DESC_OBJS_PACKDECL;
		case IJavaElement.IMPORT_DECLARATION:
			return JavaPluginImages.DESC_OBJS_IMPDECL;
		case IJavaElement.IMPORT_CONTAINER:
			return JavaPluginImages.DESC_OBJS_IMPCONT;
		case IJavaElement.COMPILATION_UNIT:
			return JavaPluginImages.DESC_OBJS_CUNIT;
		}
		return ImageDescriptor.getMissingImageDescriptor();
	}
	
	static ImageDescriptor getAliasImageDescriptor() {
		return getImageDescriptor("obj16/alias_public_obj.gif"); //$NON-NLS-1$
	}
	
	static ImageDescriptor getTypedefImageDescriptor() {
		return getImageDescriptor("obj16/typedef_public_obj.gif"); //$NON-NLS-1$
	}
	
	static ImageDescriptor getTypeImageDescriptor(AggregateDeclaration.Kind kind) {
		switch(kind) {
		case CLASS: return JavaPluginImages.DESC_OBJS_CLASS;
		case INTERFACE: return JavaPluginImages.DESC_OBJS_INTERFACE;
		case STRUCT: return JavaPluginImages.DESC_OBJS_STRUCT;
		case UNION: return JavaPluginImages.DESC_OBJS_UNION;
		default: return ImageDescriptor.getMissingImageDescriptor();
		}
	}
	
	static ImageDescriptor getTemplateImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_TEMPLATE;
	}

	static ImageDescriptor getEnumImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_ENUM;
	}

	static ImageDescriptor getAnnotationImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_ANNOTATION;
	}
	
	static ImageDescriptor getAlignImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_ALIGN;
	}
	
	static ImageDescriptor getMixinImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_MIXIN;
	}
	
	static ImageDescriptor getDebugAssignmentImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_DEBUG_ASSIGNMENT;
	}
	
	static ImageDescriptor getVersionAssignmentImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_VERSION_ASSIGNMENT;
	}
	
	static ImageDescriptor getDebugDeclarationImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_DEBUG_DECLARATION;
	}
	
	static ImageDescriptor getVersionDeclarationImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_VERSION_DECLARATION;
	}
	
	static ImageDescriptor getIftypeDeclarationImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_IFTYPE;
	}
	
	static ImageDescriptor getStaticIfDeclarationImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_STATIC_IF;
	}
	
	static ImageDescriptor getTemplateMixinImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_MIXIN;
	}
	
	static ImageDescriptor getUnitTestImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_UNITTEST;
	}
	
	static ImageDescriptor getInvariantImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_INVARIANT;
	}
	
	static ImageDescriptor getPragmaImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_PRAGMA;
	}
	
	static ImageDescriptor getStaticAssertImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_STATIC_ASSERT;
	}
	
	static ImageDescriptor getThenElseImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_THEN_ELSE;
	}
	
	static ImageDescriptor getExternImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_EXTERN;
	}

	static ImageDescriptor getImageDescriptor(IMember element) {
		int t= element.getElementType();
		if (t == IJavaElement.TYPE) {
			IType type= (IType) element;
			try {
				if (type.isClass()) {
					return getTypeImageDescriptor(AggregateDeclaration.Kind.CLASS);
				} else if (type.isInterface()) {
					return getTypeImageDescriptor(AggregateDeclaration.Kind.INTERFACE);
				} else if (type.isStruct()) {
					return getTypeImageDescriptor(AggregateDeclaration.Kind.STRUCT);
				} else if (type.isUnion()) {
					return getTypeImageDescriptor(AggregateDeclaration.Kind.UNION);
				} else if (type.isEnum()) {
					return getEnumImageDescriptor();
				} else {
					return JavaPluginImages.DESC_OBJS_GHOST;
				}
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
				return JavaPluginImages.DESC_OBJS_GHOST;
			}
		}
		return getImageDescriptor(t);
	}
	
	/**
	 * Returns a name for the given Java element that uses the same conventions
	 * as the JavaNode name of a corresponding element.
	 */
	static String getJavaElementID(IJavaElement je) {
		
		if (je instanceof IMember && ((IMember)je).isBinary())
			return null;
			
		StringBuffer sb= new StringBuffer();
		
		switch (je.getElementType()) {
		case IJavaElement.COMPILATION_UNIT:
			sb.append(COMPILATIONUNIT);
			break;
		case IJavaElement.TYPE:
			sb.append(TYPE);
			sb.append(je.getElementName());
			break;
		case IJavaElement.FIELD:
			sb.append(FIELD);
			sb.append(je.getElementName());
			break;
		case IJavaElement.METHOD:
			sb.append(METHOD);
			sb.append(JavaElementLabels.getElementLabel(je, JavaElementLabels.M_PARAMETER_TYPES));
			break;
		case IJavaElement.INITIALIZER:
			String id= je.getHandleIdentifier();
			int pos= id.lastIndexOf(INITIALIZER);
			if (pos >= 0)
				sb.append(id.substring(pos));
			break;
		case IJavaElement.PACKAGE_DECLARATION:
			sb.append(PACKAGEDECLARATION);
			break;
		case IJavaElement.IMPORT_CONTAINER:
			sb.append(IMPORT_CONTAINER);
			break;
		case IJavaElement.IMPORT_DECLARATION:
			sb.append(IMPORTDECLARATION);
			sb.append(je.getElementName());			
			break;
		case IJavaElement.CONDITIONAL:
			sb.append(CONDITIONAL);
			sb.append(je.getElementName());			
			break;
		default:
			return null;
		}
		return sb.toString();
	}
	
	/**
	 * Returns a name which identifies the given typed name.
	 * The type is encoded as a single character at the beginning of the string.
	 */
	static String buildID(int type, String name) {
		StringBuffer sb= new StringBuffer();
		switch (type) {
		case JavaNode.CU:
			sb.append(COMPILATIONUNIT);
			break;
		case JavaNode.CLASS:
		case JavaNode.INTERFACE:
		case JavaNode.STRUCT:
		case JavaNode.UNION:
		case JavaNode.ENUM:
		case JavaNode.TEMPLATE:
			sb.append(TYPE);
			sb.append(name);
			break;
		case JavaNode.FIELD:
		case JavaNode.ALILAS:
		case JavaNode.TYPEDEF:
		case JavaNode.TEMPLATE_MIXIN:
			sb.append(FIELD);
			sb.append(name);
			break;
		case JavaNode.METHOD:
		case JavaNode.CONSTRUCTOR:
		case JavaNode.DESTRUCTOR:
		case JavaNode.NEW:
		case JavaNode.DELETE:
			sb.append(METHOD);
			sb.append(name);
			break;
		case JavaNode.STATIC_CONSTRUCTOR:
		case JavaNode.STATIC_DESTRUCTOR:
		case JavaNode.ALIGN:
		case JavaNode.MIXIN:
		case JavaNode.DEBUG_ASSIGNMENT:
		case JavaNode.VERSION_ASSIGNMENT:
		case JavaNode.UNITTEST:
		case JavaNode.INVARIANT:
		case JavaNode.PRAGMA:
		case JavaNode.STATIC_ASSERT:
		case JavaNode.THEN:
		case JavaNode.ELSE:
		case JavaNode.EXTERN:
			sb.append(INITIALIZER);
			sb.append(name);
			break;
		case JavaNode.MODULE:
			sb.append(PACKAGEDECLARATION);
			break;
		case JavaNode.IMPORT:
			sb.append(IMPORTDECLARATION);
			sb.append(name);
			break;
		case JavaNode.IMPORT_CONTAINER:
			sb.append(IMPORT_CONTAINER);
			break;
		case JavaNode.DEBUG_DECLARATION:
		case JavaNode.VERSION_DECLARATION:
		case JavaNode.IFTYPE_DECLARATION:
		case JavaNode.STATIC_IF_DECLARATION:
			sb.append(CONDITIONAL);
			break;
		default:
			Assert.isTrue(false);
			break;
		}
		return sb.toString();
	}

	static ImageDescriptor getImageDescriptor(String relativePath) {
		IPath path= JavaPluginImages.ICONS_PATH.append(relativePath);
		return JavaPluginImages.createImageDescriptor(JavaPlugin.getDefault().getBundle(), path, true);
	}
	
	static boolean getBoolean(CompareConfiguration cc, String key, boolean dflt) {
		if (cc != null) {
			Object value= cc.getProperty(key);
			if (value instanceof Boolean)
				return ((Boolean) value).booleanValue();
		}
		return dflt;
	}

	static Image getImage(IMember member) {
		ImageDescriptor id= getImageDescriptor(member);
		return id.createImage();
	}

	static JavaTextTools getJavaTextTools() {
		JavaPlugin plugin= JavaPlugin.getDefault();
		if (plugin != null)
			return plugin.getJavaTextTools();
		return null;
	}
	
	static IDocumentPartitioner createJavaPartitioner() {
		JavaTextTools tools= getJavaTextTools();
		if (tools != null)
			return tools.createDocumentPartitioner();
		return null;
	}
	
	static void setupDocument(IDocument document) {
		JavaTextTools tools= getJavaTextTools();
		if (tools != null)
			tools.setupJavaDocumentPartitioner(document);
	}
	
	static void setupPropertiesFileDocument(IDocument document) {
		/* TODO JDT UI properties
		IDocumentPartitioner partitioner= new FastPartitioner(new PropertiesFilePartitionScanner(), IPropertiesFilePartitions.PARTITIONS);
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);
		*/
	}

	/**
	 * Reads the contents of the given input stream into a string.
	 * The function assumes that the input stream uses the platform's default encoding
	 * (<code>ResourcesPlugin.getEncoding()</code>).
	 * Returns null if an error occurred.
	 */
	private static String readString(InputStream is, String encoding) {
		if (is == null)
			return null;
		BufferedReader reader= null;
		try {
			StringBuffer buffer= new StringBuffer();
			char[] part= new char[2048];
			int read= 0;
			reader= new BufferedReader(new InputStreamReader(is, encoding));

			while ((read= reader.read(part)) != -1)
				buffer.append(part, 0, read);
			
			return buffer.toString();
			
		} catch (IOException ex) {
			// NeedWork
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					// silently ignored
				}
			}
		}
		return null;
	}
	
	public static String readString(IStreamContentAccessor sa) throws CoreException {
		InputStream is= sa.getContents();
		if (is != null) {
			String encoding= null;
			if (sa instanceof IEncodedStreamContentAccessor) {
				try {
					encoding= ((IEncodedStreamContentAccessor) sa).getCharset();
				} catch (Exception e) {
				}
			}
			if (encoding == null)
				encoding= ResourcesPlugin.getEncoding();
			return readString(is, encoding);
		}
		return null;
	}

	/**
	 * Returns the contents of the given string as an array of bytes 
	 * in the platform's default encoding.
	 */
	static byte[] getBytes(String s, String encoding) {
		try {
			return s.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			return s.getBytes();
		}
	}
	
	/**
	 * Breaks the contents of the given input stream into an array of strings.
	 * The function assumes that the input stream uses the platform's default encoding
	 * (<code>ResourcesPlugin.getEncoding()</code>).
	 * Returns null if an error occurred.
	 */
	static String[] readLines(InputStream is2, String encoding) {
		
		BufferedReader reader= null;
		try {
			reader= new BufferedReader(new InputStreamReader(is2, encoding));
			StringBuffer sb= new StringBuffer();
			List list= new ArrayList();
			while (true) {
				int c= reader.read();
				if (c == -1)
					break;
				sb.append((char)c);
				if (c == '\r') {	// single CR or a CR followed by LF
					c= reader.read();
					if (c == -1)
						break;
					sb.append((char)c);
					if (c == '\n') {
						list.add(sb.toString());
						sb= new StringBuffer();
					}
				} else if (c == '\n') {	// a single LF
					list.add(sb.toString());
					sb= new StringBuffer();
				}
			}
			if (sb.length() > 0)
				list.add(sb.toString());
			return (String[]) list.toArray(new String[list.size()]);

		} catch (IOException ex) {
			return null;

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					// silently ignored
				}
			}
		}
	}
	
	/*
	 * Initialize the given Action from a ResourceBundle.
	 */
	static void initAction(IAction a, ResourceBundle bundle, String prefix) {
		
		String labelKey= "label"; //$NON-NLS-1$
		String tooltipKey= "tooltip"; //$NON-NLS-1$
		String imageKey= "image"; //$NON-NLS-1$
		String descriptionKey= "description"; //$NON-NLS-1$
		
		if (prefix != null && prefix.length() > 0) {
			labelKey= prefix + labelKey;
			tooltipKey= prefix + tooltipKey;
			imageKey= prefix + imageKey;
			descriptionKey= prefix + descriptionKey;
		}
		
		a.setText(getString(bundle, labelKey, labelKey));
		a.setToolTipText(getString(bundle, tooltipKey, null));
		a.setDescription(getString(bundle, descriptionKey, null));
		
		String relPath= getString(bundle, imageKey, null);
		if (relPath != null && relPath.trim().length() > 0) {
			
			String dPath;
			String ePath;
			
			if (relPath.indexOf("/") >= 0) { //$NON-NLS-1$
				String path= relPath.substring(1);
				dPath= 'd' + path;
				ePath= 'e' + path;
			} else {
				dPath= "dlcl16/" + relPath; //$NON-NLS-1$
				ePath= "elcl16/" + relPath; //$NON-NLS-1$
			}
			
			ImageDescriptor id= JavaCompareUtilities.getImageDescriptor(dPath);	// we set the disabled image first (see PR 1GDDE87)
			if (id != null)
				a.setDisabledImageDescriptor(id);
			id= JavaCompareUtilities.getImageDescriptor(ePath);
			if (id != null) {
				a.setImageDescriptor(id);
				a.setHoverImageDescriptor(id);
			}
		}
	}
	
	static void initToggleAction(IAction a, ResourceBundle bundle, String prefix, boolean checked) {

		String tooltip= null;
		if (checked)
			tooltip= getString(bundle, prefix + "tooltip.checked", null);	//$NON-NLS-1$
		else
			tooltip= getString(bundle, prefix + "tooltip.unchecked", null);	//$NON-NLS-1$
		if (tooltip == null)
			tooltip= getString(bundle, prefix + "tooltip", null);	//$NON-NLS-1$
		
		if (tooltip != null)
			a.setToolTipText(tooltip);
			
		String description= null;
		if (checked)
			description= getString(bundle, prefix + "description.checked", null);	//$NON-NLS-1$
		else
			description= getString(bundle, prefix + "description.unchecked", null);	//$NON-NLS-1$
		if (description == null)
			description= getString(bundle, prefix + "description", null);	//$NON-NLS-1$
		
		if (description != null)
			a.setDescription(description);
	}
}
