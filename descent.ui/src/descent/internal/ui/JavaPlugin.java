package descent.internal.ui;

import descent.internal.ui.javaeditor.CompilationUnitDocumentProvider;
import descent.internal.ui.javaeditor.ICompilationUnitDocumentProvider;
import descent.internal.ui.javaeditor.WorkingCopyManager;

public class JavaPlugin {
	
	private ICompilationUnitDocumentProvider fCompilationUnitDocumentProvider;
	private WorkingCopyManager fWorkingCopyManager;
	private static JavaPlugin fgJavaPlugin = new JavaPlugin();
	
	public static JavaPlugin getDefault() {
		return fgJavaPlugin;
	}
	
	public synchronized WorkingCopyManager getWorkingCopyManager() {
		if (fWorkingCopyManager == null) {
			ICompilationUnitDocumentProvider provider= getCompilationUnitDocumentProvider();
			fWorkingCopyManager= new WorkingCopyManager(provider);
		}
		return fWorkingCopyManager;
	}
	
	public synchronized ICompilationUnitDocumentProvider getCompilationUnitDocumentProvider() {
		if (fCompilationUnitDocumentProvider == null)
			fCompilationUnitDocumentProvider= new CompilationUnitDocumentProvider();
		return fCompilationUnitDocumentProvider;
	}

}
