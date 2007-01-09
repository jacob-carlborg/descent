package mmrnmhrm.text;

import org.eclipse.jface.text.Document;
import org.eclipse.ui.IPathEditorInput;

import dtool.project.CompilationUnit;

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

	public void setFileInput(IPathEditorInput input) {
		cunit.file = input.getPath().toFile();
	}

	public void updateCompilationUnit() {
		cunit.update(get());
		cunit.parse();
	}
	
}
