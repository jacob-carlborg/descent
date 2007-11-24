package descent.internal.ui.compare;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;

import descent.core.IJavaElement;
import descent.internal.ui.JavaPlugin;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;

/**
 * Comparable Java elements are represented as JavaNodes.
 * Extends the DocumentRangeNode with method signature information.
 */
class JavaNode extends DocumentRangeNode implements ITypedElement {
	
	public static final int CU= 0;
	public static final int PACKAGE= 1;
	public static final int IMPORT_CONTAINER= 2;
	public static final int IMPORT= 3;
	public static final int INTERFACE= 4;
	public static final int CLASS= 5;
	public static final int ENUM= 6;
	public static final int ANNOTATION= 7;
	public static final int FIELD= 8;
	public static final int INIT= 9;
	public static final int CONSTRUCTOR= 10;
	public static final int METHOD= 11;
	public static final int STRUCT = 12;
	public static final int UNION = 13;
	public static final int TEMPLATE = 13;

	private int fInitializerCount= 1;
	private boolean fIsEditable;
	private JavaNode fParent;


	/**
	 * Creates a JavaNode under the given parent.
	 * @param type the Java elements type. Legal values are from the range CU to METHOD of this class.
	 * @param name the name of the Java element
	 * @param start the starting position of the java element in the underlying document
	 * @param length the number of characters of the java element in the underlying document
	 */
	public JavaNode(JavaNode parent, int type, String name, int start, int length) {
		super(type, JavaCompareUtilities.buildID(type, name), parent.getDocument(), start, length);
		fParent= parent;
		if (parent != null) {
			parent.addChild(this);
			fIsEditable= parent.isEditable();
		}
	}	
	
	/**
	 * Creates a JavaNode for a CU. It represents the root of a
	 * JavaNode tree, so its parent is null.
	 * @param document the document which contains the Java element
	 * @param editable whether the document can be modified
	 */
	public JavaNode(IDocument document, boolean editable) {
		super(CU, JavaCompareUtilities.buildID(CU, "root"), document, 0, document.getLength()); //$NON-NLS-1$
		fIsEditable= editable;
	}	

	public String getInitializerCount() {
		return Integer.toString(fInitializerCount++);
	}
	
	/**
	 * Extracts the method name from the signature.
	 * Used for smart matching.
	 */
	public String extractMethodName() {
		String id= getId();
		int pos= id.indexOf('(');
		if (pos > 0)
			return id.substring(1, pos);
		return id.substring(1);
	}
	
	/**
	 * Extracts the method's arguments name the signature.
	 * Used for smart matching.
	 */
	public String extractArgumentList() {
		String id= getId();
		int pos= id.indexOf('(');
		if (pos >= 0)
			return id.substring(pos+1);
		return id.substring(1);
	}
	
	/**
	 * Returns a name which is presented in the UI.
	 * @see ITypedElement#getName()
	 */
	public String getName() {
		
		switch (getTypeCode()) {
		case INIT:
			return CompareMessages.JavaNode_initializer; 
		case IMPORT_CONTAINER:
			return CompareMessages.JavaNode_importDeclarations; 
		case CU:
			return CompareMessages.JavaNode_compilationUnit; 
		case PACKAGE:
			return CompareMessages.JavaNode_packageDeclaration; 
		}
		return getId().substring(1);	// we strip away the type character
	}
	
	/*
	 * @see ITypedElement#getType()
	 */
	public String getType() {
		return "java2"; //$NON-NLS-1$
	}
	
	/* (non Javadoc)
	 * see IEditableContent.isEditable
	 */
	public boolean isEditable() {
		return fIsEditable;
	}
		
	/**
	 * Returns a shared image for this Java element.
	 *
	 * see ITypedInput.getImage
	 */
	public Image getImage() {
						
		ImageDescriptor id= null;
					
		switch (getTypeCode()) {
		case CU:
			id= JavaCompareUtilities.getImageDescriptor(IJavaElement.COMPILATION_UNIT);
			break;
		case PACKAGE:
			id= JavaCompareUtilities.getImageDescriptor(IJavaElement.PACKAGE_DECLARATION);
			break;
		case IMPORT:
			id= JavaCompareUtilities.getImageDescriptor(IJavaElement.IMPORT_DECLARATION);
			break;
		case IMPORT_CONTAINER:
			id= JavaCompareUtilities.getImageDescriptor(IJavaElement.IMPORT_CONTAINER);
			break;
		case CLASS:
			id= JavaCompareUtilities.getTypeImageDescriptor(true);
			break;
		case INTERFACE:
			id= JavaCompareUtilities.getTypeImageDescriptor(false);
			break;
		case INIT:
			id= JavaCompareUtilities.getImageDescriptor(IJavaElement.INITIALIZER);
			break;
		case CONSTRUCTOR:
		case METHOD:
			id= JavaCompareUtilities.getImageDescriptor(IJavaElement.METHOD);
			break;
		case FIELD:
			id= JavaCompareUtilities.getImageDescriptor(IJavaElement.FIELD);
			break;					
		case ENUM:
			id= JavaCompareUtilities.getEnumImageDescriptor();
			break;
		case ANNOTATION:
			id= JavaCompareUtilities.getAnnotationImageDescriptor();
			break;
		}
		return JavaPlugin.getImageDescriptorRegistry().get(id);
	}

	public void setContent(byte[] content) {
		super.setContent(content);
		nodeChanged(this);
	}
	
	public ITypedElement replace(ITypedElement child, ITypedElement other) {
		nodeChanged(this);
		return child;
	}

	void nodeChanged(JavaNode node) {
		if (fParent != null)
			fParent.nodeChanged(node);
	}
}

