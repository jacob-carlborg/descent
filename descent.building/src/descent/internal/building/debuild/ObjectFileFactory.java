package descent.internal.building.debuild;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
                str.append(" (ExTERNAL)");
            
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
	
	private final BuildRequest req;
	private Map<String, IObjectFile> cached = 
	    new HashMap<String, IObjectFile>();
	
	// Since findModule() is called quite a bit, creating
    // a new hash set every time sounds like a horrible thrashing of the GC.
    // Instead, we'll just keep clearing out these set. They can't be static
    // to maintain the threadsafe nature of the builder as a whole, however
    private final Set<IJavaProject> visitedProjectAccumulator = 
        new HashSet<IJavaProject>();
	
	public ObjectFileFactory(BuildRequest req)
	{
	    this.req = req;
	}
	
	public IObjectFile create(String moduleName)
	{
	    // If there's a cached version, return that one
	    if(cached.containsKey(moduleName))
	        return cached.get(moduleName);
	    
	    // If we shouldn't be building it, return null
        for(String ignored : req.getIgnoredModules())
            if(moduleName.startsWith(ignored))
                return null;
	    
	    try
	    {
    	    // Use a one-element array to get pointer semantics
    	    boolean[] _isLibraryFile = new boolean[1];
    	    ICompilationUnit cu = findModule(moduleName, _isLibraryFile);
    	    if(null == cu)
    	        return null;
    	    boolean isLibraryFile = _isLibraryFile[0];
    	    IObjectFile obj = getInstance(moduleName, cu, isLibraryFile);
    	    cached.put(moduleName, obj);
    	    return obj;
	    }
	    catch(JavaModelException e)
	    {
	        throw new BuildException(e);
	    }
	}
	
	private IObjectFile getInstance(String moduleName,
	        ICompilationUnit cu, boolean isLibraryFile)
	{
	    File inputFile = new File(BuilderUtil.getAbsolutePath(cu.getResource().
	            getLocation()));
	    File outputFile = new File(req.getOutputLocation().getAbsolutePath() +
	            File.separator + getOutputFilename(cu));
	    return new ObjectFile(moduleName, inputFile, outputFile, isLibraryFile);
	}
	
	private ICompilationUnit findModule(String moduleName, boolean[] isLibraryFile)
        throws JavaModelException
    {
        visitedProjectAccumulator.clear();
        return findModuleInProject(req.getProject(), moduleName, isLibraryFile);
    }
    
    private ICompilationUnit findModuleInProject(
            IJavaProject project,
            String moduleName,
            boolean[] isLibraryFile)
        throws JavaModelException
    {
        if (visitedProjectAccumulator.contains(project))
            return null;
        
        int index = moduleName.lastIndexOf('.');
        String packagePart = index > 0 ? moduleName.substring(0, index) : "";
        String modulePart = (index > 0 ? moduleName.substring(index + 1) : 
            moduleName) + ".d";
        
        ICompilationUnit module = null;
        boolean libraryFile = false;
        for(IPackageFragmentRoot root : project.getPackageFragmentRoots())
        {
            IPackageFragment pkg = root.getPackageFragment(packagePart);
            if (pkg.exists())
            {
                // Check if it's in the package...
                module = pkg.getCompilationUnit(modulePart);
                
                // If not, check if it's a class file
                if (!module.exists())
                {
                    module = pkg.getClassFile(modulePart);
                    libraryFile = true;
                }
                
                if(module.exists())
                {
                    break;
                }
            }
        }
        
        if(null != module && module.exists())
        {
            // Note: existance must be tested since an ICompilationUnit may
            // be returned for non-existant modules
            isLibraryFile[0] = libraryFile;
            return module;
        }
        
        visitedProjectAccumulator.add(project);
        IJavaModel javaModel= project.getJavaModel();
        String[] requiredProjectNames= project.getRequiredProjectNames();
        for (int i= 0; i < requiredProjectNames.length; i++)
        {
            IJavaProject requiredProject= javaModel.getJavaProject(requiredProjectNames[i]);
            if (requiredProject.exists())
            {
                module = findModuleInProject(requiredProject, moduleName, isLibraryFile);
                if (module != null)
                    return module;
            }
        }
        return null;
    }
	
	private static String getOutputFilename(ICompilationUnit cu)
	{
	    String name = cu.getFullyQualifiedName().replace('.', '-') + 
	        BuilderUtil.EXTENSION_OBJECT_FILE;
	    int len = name.length();
	    if(len > MAX_FILENAME_LENGTH)
	        name = name.substring(len - MAX_FILENAME_LENGTH);
	    return name;
	}
}