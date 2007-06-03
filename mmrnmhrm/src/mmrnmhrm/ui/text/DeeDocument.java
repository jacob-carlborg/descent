package mmrnmhrm.ui.text;

import mmrnmhrm.core.model.CompilationUnit;

import org.eclipse.jface.text.Document;
import org.eclipse.ui.IFileEditorInput;


public class DeeDocument extends Document {

	private CompilationUnit cunit;
	
	public DeeDocument() {
	}
	
	public CompilationUnit getCompilationUnit() {
		return cunit;
	}
	
	public void setCompilationUnit(CompilationUnit cunit) {
		this.cunit = cunit;
	}

	public void updateCompilationUnit() {
		cunit.setSource(get());
		cunit.parseAST();
	}
	
}
