package descent.internal.ui.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.text.IDocument;

import org.eclipse.compare.HistoryItem;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.IResourceProvider;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;

import descent.core.dom.ASTNode;

import descent.internal.ui.JavaPlugin;

/**
 * Implements the IStreamContentAccessor and ITypedElement protocols
 * for a TextBuffer.
 */
// TODO JDT UI compare implement well with D's AST
class JavaTextBufferNode implements ITypedElement, IEncodedStreamContentAccessor, IResourceProvider {
	
	private IFile fFile;
	private IDocument fDocument;
	private boolean fInEditor;
	
	JavaTextBufferNode(IFile file, IDocument document, boolean inEditor) {
		fFile= file;
		fDocument= document;
		fInEditor= inEditor;
	}
	
	public String getName() {
		if (fInEditor)
			return CompareMessages.Editor_Buffer; 
		return CompareMessages.Workspace_File; 
	}
	
	public String getType() {
		return "java";	//$NON-NLS-1$
	}
	
	public Image getImage() {
		return null;
	}
	
	public InputStream getContents() {
		return new ByteArrayInputStream(JavaCompareUtilities.getBytes(fDocument.get(), "UTF-16")); //$NON-NLS-1$
	}
	
	public String getCharset() {
		return "UTF-16"; //$NON-NLS-1$
	}

	public IResource getResource() {
		return fFile;
	}
	
	static final ITypedElement[] buildEditions(ITypedElement target, IFile file) {

		// setup array of editions
		IFileState[] states= null;		
		// add available editions
		try {
			states= file.getHistory(null);
		} catch (CoreException ex) {
			JavaPlugin.log(ex);
		}
		
		int count= 1;
		if (states != null)
			count+= states.length;

		ITypedElement[] editions= new ITypedElement[count];
		editions[0]= new ResourceNode(file);
		if (states != null)
			for (int i= 0; i < states.length; i++)
				editions[i+1]= new HistoryItem(target, states[i]);
		return editions;
	}
	
	/**
	 * Returns the corresponding place holder type for the given element.
	 * @return a place holder type (see ASTRewrite) or -1 if there is no corresponding placeholder
	 */
	static final int getPlaceHolderType(ITypedElement element) {
		
		if (element instanceof DocumentRangeNode) {
			JavaNode jn= (JavaNode) element;
			switch (jn.getTypeCode()) {
				
			case JavaNode.MODULE:
			    return ASTNode.MODULE_DECLARATION;

			case JavaNode.CLASS:
			case JavaNode.INTERFACE:
			case JavaNode.STRUCT:
			case JavaNode.UNION:
				return ASTNode.AGGREGATE_DECLARATION;
				
			case JavaNode.ENUM:
				return ASTNode.ENUM_DECLARATION;
				
			case JavaNode.CONSTRUCTOR:
			case JavaNode.METHOD:
				return ASTNode.FUNCTION_DECLARATION;
				
			case JavaNode.FIELD:
				return ASTNode.VARIABLE_DECLARATION;
				
			case JavaNode.STATIC_CONSTRUCTOR:
				return ASTNode.FUNCTION_DECLARATION;

			case JavaNode.IMPORT:
			case JavaNode.IMPORT_CONTAINER:
				return ASTNode.IMPORT_DECLARATION;

			case JavaNode.CU:
			    return ASTNode.COMPILATION_UNIT;
			}
		}
		return -1;
	}
}
