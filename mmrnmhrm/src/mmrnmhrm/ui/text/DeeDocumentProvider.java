package mmrnmhrm.ui.text;


import mmrnmhrm.core.model.CompilationUnit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import util.Assert;
import util.log.Logg;

/* XXX: JDT uses TextFileDocumentProvider, WHY?
 * 
 */
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
		// AbstractDecoratedTextEditor uses FileEditorInput
		setupDeeDocument((IFileEditorInput) element, deedocument);
		return deedocument;
	}

	private void setupDeeDocument(IFileEditorInput input, DeeDocument deedocument) {
		IDocumentPartitioner partitioner = new FastPartitioner(
				new DeePartitionScanner_Fast(), EDeePartitions.legalContentTypes);
		partitioner.connect(deedocument);
		deedocument.setDocumentPartitioner(partitioner);

		CompilationUnit cunit = new CompilationUnit(input.getFile());
		cunit.setSource(deedocument.get());
		deedocument.setCompilationUnit(cunit);
		Logg.model.println("Got Editor Input: ", input);
		deedocument.updateCompilationUnit();
	}
	

	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		super.doSaveDocument(monitor, element, document, overwrite);
		DeeDocument deedocument = (DeeDocument) document;
		deedocument.updateCompilationUnit();
	}


	public CompilationUnit getCompilationUnit(IEditorInput input) {
		DeeDocument deedocument = (DeeDocument) getDocument(input);
		Assert.isTrue(deedocument != null);
		return deedocument.getCompilationUnit();
	}
	
}