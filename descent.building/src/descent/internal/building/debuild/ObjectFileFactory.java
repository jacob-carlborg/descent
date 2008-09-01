package descent.internal.building.debuild;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import descent.building.compiler.BuildException;
import descent.building.compiler.IObjectFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;

/* package */ final class ObjectFileFactory
{
	private static final class ObjectFile implements IObjectFile
	{
	    private final String name;
		private final File inputFile;
		private final File outputFile;
		private final boolean isLibraryFile;
		
		public ObjectFile(String name, 
		        File inputFile, File outputFile, 
				boolean isLibraryFile)
		{
		    this.name = name;
			this.inputFile = inputFile;
			this.outputFile = outputFile;
			this.isLibraryFile = isLibraryFile;
		}
		
		public String getName()
		{
		    return name;
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

        @Override
        public boolean equals(Object other)
        {
            return ((null != other) &&
                    (other instanceof ObjectFile) &&
                    (((ObjectFile) other).name.equals(this.name)));
        }

        @Override
        public int hashCode()
        {
            return name.hashCode();
        }

        @Override
        public String toString()
        {
            StringBuilder str = new StringBuilder();
            
            str.append(getName());
            if(isLibraryFile())
                str.append(" (EXTERNAL)");
            
            str.append("\n    ");
            str.append("Input file: ");
            str.append(getInputFile());
            
            str.append("\n    ");
            str.append("Output file: ");
            str.append(getOutputFile());
            
            return str.toString();
        }
	}
	
	private static final int MAX_FILENAME_LENGTH = 240;
	
	private final String outputPrefix;
	
	public ObjectFileFactory(BuildRequest req)
	{
	    outputPrefix = req.getOutputLocation().getAbsolutePath();
	}
	
	public IObjectFile create(String moduleName, ICompilationUnit cu, boolean isLibraryFile)
	{
	    //File inputFile = new File(BuilderUtil.getAbsolutePath(cu.getResource().getLocation()));
	    IResource res = cu.getResource();
	    IPath path = res.getLocation();
	    String absPath = BuilderUtil.getAbsolutePath(path);
	    File inputFile = new File(absPath);
        File outputFile = new File(outputPrefix + File.separator + getOutputFilename(moduleName));
        return new ObjectFile(moduleName, inputFile, outputFile, isLibraryFile);
	}
	
	private static String getOutputFilename(String moduleName)
	{
	    String name = moduleName.replace('.', '-') + BuilderUtil.EXTENSION_OBJECT_FILE;
	    int len = name.length();
	    if(len > MAX_FILENAME_LENGTH)
	        name = name.substring(len - MAX_FILENAME_LENGTH);
	    name = "__" + name;
	    return name;
	}
}