package descent.internal.building.debuild;

import java.io.File;

import descent.building.compiler.IObjectFile;
import descent.core.ICompilationUnit;
import descent.internal.building.BuilderUtil;

/* package */ final class ObjectFileFactory
{
	private static final class ObjectFile implements IObjectFile
	{
		private final File inputFile;
		private final File outputFile;
		private final boolean isLibraryFile;
		
		public ObjectFile(File inputFile, File outputFile, 
				boolean isLibraryFile)
		{
			this.inputFile = inputFile;
			this.outputFile = outputFile;
			this.isLibraryFile = isLibraryFile;
		}

		public File getInputFile()
		{
			return inputFile;
		}

		public File getOutputFile()
		{
			return outputFile;
		}

		public boolean isLibraryFile()
		{
			return isLibraryFile;
		}
	}
	
	private static final int MAX_FILENAME_LENGTH = 240;
	
	private final BuildRequest req;
	
	public ObjectFileFactory(BuildRequest req)
	{
	    this.req = req;
	}
	
	public IObjectFile create(ICompilationUnit cu,
	        boolean isLibraryFile)
	{
	    File inputFile = new File(BuilderUtil.getAbsolutePath(cu.getResource().
	            getLocation()));
	    File outputFile = new File(req.getOutputLocation().getAbsolutePath() +
	            File.separator + getOutputFilename(cu));
	    return new ObjectFile(inputFile, outputFile, isLibraryFile);
	}
	
	private static String getOutputFilename(ICompilationUnit cu)
	{
	    String name = cu.getFullyQualifiedName().replace('.', '-') + "." +
	        BuilderUtil.EXTENSION_OBJECT_FILE;
	    int len = name.length();
	    if(len > MAX_FILENAME_LENGTH)
	        name = name.substring(len - MAX_FILENAME_LENGTH);
	    return name;
	}
}