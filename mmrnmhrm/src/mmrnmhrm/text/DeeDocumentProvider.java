package mmrnmhrm.text;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import util.Assert;
import dtool.project.CompilationUnit;

/// JDT uses TextFileDocumentProvider
public class DeeDocumentProvider extends FileDocumentProvider {

	public DeeDocumentProvider() {
	}

	@Override
	protected IDocument createEmptyDocument() {
		return new DeeDocument();
	}
	
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);

		DeeDocument deedocument = (DeeDocument) document;
		
		setupDocument((IPathEditorInput) element, deedocument);

		return deedocument;
	}

	private void setupDocument(IPathEditorInput element, DeeDocument deedocument) {
		IDocumentPartitioner partitioner = new DebugPartitioner(
				new DeePartitionScanner_Fast(), EDeePartitions.legalContentTypes);
		partitioner.connect(deedocument);
		deedocument.setDocumentPartitioner(partitioner);

		CompilationUnit cunit = new CompilationUnit(deedocument.get());
		deedocument.setCompilationUnit(cunit);
		deedocument.setFileInput(element);
	}
	
	

	

	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		super.doSaveDocument(monitor, element, document, overwrite);
		// TODO: update model here
	}


	public CompilationUnit getCompilationUnit(IEditorInput input) {
		DeeDocument deedocument = (DeeDocument) getDocument(input);
		Assert.isTrue(deedocument != null);
		return deedocument.getCompilationUnit();
	}
	
}