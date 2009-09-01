package descent.internal.core.builder;

import org.eclipse.core.resources.IFile;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.internal.compiler.parser.ASTNodeEncoder;

public class BuildRequest {
	
	public ICompilationUnit unit;
	public IFile file;
	public ASTNodeEncoder encoder;
	public boolean isDependency;
	
	public BuildRequest(ICompilationUnit unit, IFile file, ASTNodeEncoder encoder) {
		this.unit = unit;
		this.file = file;
		this.encoder = encoder;
	}

}
