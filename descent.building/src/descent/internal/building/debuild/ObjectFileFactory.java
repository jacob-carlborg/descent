package descent.internal.building.debuild;

import java.io.File;

import descent.building.compiler.IObjectFile;

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
}