package descent.internal.building.debuild;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;

import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.internal.building.BuildingPlugin;

/**
 * Represents a compilable D module together with compilation options. This class is
 * initially constructed with information about a particular input module, which allows
 * it limited functionality to simply hold that information. When given compile options,
 * it is able to deduce the output filename/path of a file compiled with those options.
 * 
 * This class also holds an important utility method,
 * {@link #cleanupOutputDirectory(File, int, int)}, which should be used to clean the
 * output directory of old object files.
 * 
 * @author Robert Fraser
 */
/* package */ class ObjectFile
{
	public static final int MAX_FILENAME_LENGTH      = 160;
	public static final int PREFIX_LENGTH            = 15;
	public static final int MAX_MODULE_LENGTH        = MAX_FILENAME_LENGTH - PREFIX_LENGTH;
	
	public static final int SYMBOLIC_DEBUG           = 0x01;
	public static final int UNITTEST                 = 0x02;
	public static final int ASSERTS_AND_CONTRACTS    = 0x04;
	public static final int ADD_DEBUG_CODE           = 0x08;
	public static final int INLINE_FUNCTIONS         = 0x10;
	public static final int OPTIMIZE_CODE            = 0x20;
	public static final int INSTRUMENT_FOR_COVERAGE  = 0x40;
	public static final int INSTRUMENT_FOR_PROFILE   = 0x80;
	
	public static final String FILETYPE_PREFIX       = "$";
	public static final char   COMPONENT_SEPARATOR   = '$';
	public static final char   PACKAGE_SEPARATOR     = '-';
	
	private static final char[] BASE_64_CHARS = 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+="
		.toCharArray();
	private static final char BASE_64_PLACEHOLDER = '_';
	
	private static final Comparator<File> NEWEST_FIRST_COMPARATOR =
		new Comparator<File>()
		{
			public int compare(File f1, File f2)
			{
				return (int) (f1.lastModified() - f2.lastModified());
			}
		};
	
	private final IJavaProject project;
	private final File inputFile;
	private final String moduleName;
	private final boolean isLibraryFile;
	private CompileOptions opts;
    private File cachedOutputFile;
	
	/**
	 * Crates a new instance of an object file container
	 * 
	 * @param project    the project whose output folder the output file should be
	 *                   placed in
	 * @param inputFile  the input file for this resource
	 * @param moduleName the name of the module
	 */
	public ObjectFile(IJavaProject project, File inputFile, String moduleName,
			boolean isLibraryFile)
	{
        if(DebuildBuilder.DEBUG)
        {   
            Assert.isTrue(null != project);
            Assert.isTrue(null != moduleName);
            Assert.isTrue(null != inputFile);
            
            Assert.isTrue(project.exists());
            Assert.isTrue(project.isOpen());
            Assert.isTrue(!project.isReadOnly());
            
            Assert.isTrue(inputFile.exists());
            Assert.isTrue(inputFile.canRead());
        }
        
		this.project = project;
		this.inputFile = inputFile;
		this.moduleName = moduleName;
		this.isLibraryFile = isLibraryFile;
	}
	
	/**
	 * Checks whether this file needs to be built. Generally, will check if
	 * the module it represents has any modifications since this object file
	 * was last built.
	 * 
	 * @return        true if and only if this object file needs to be rebuilt
	 */
	public boolean shouldBuild()
	{
		// PERHAPS should getModificationStamp be used instead? Then we have to track
		// modifications somehow...
		File ouputFile = getOutputFile();
		long date = inputFile.lastModified();
		Assert.isTrue(date != IResource.NULL_STAMP);
		
		if(!ouputFile.exists())
			return true;
		
		if(ouputFile.lastModified() < date)
			return true;
		
		return false;
	}
	
	/**
	 * Gets the handle to the input file (which should exist unless the filesystem is
	 * going crazy during the build).
	 */
	public File getInputFile()
	{
		return inputFile;
	}
	
	/**
	 * Gets the handle to the file, which may or may not exist. File will be
	 * be an absolute path generally in the project's "bin" directory.
	 *
	 * @return                       the handle to the file
	 * @throws IllegalStateException if the compile options haven't been set yet
	 */
	public File getOutputFile()
	{
		if(null == opts)
			throw new IllegalStateException("Compile options not set yet!");
		
        if(null == cachedOutputFile)
            cachedOutputFile = new File(getOutputPath() + "/" + getFilename());
		return cachedOutputFile;
	}
	
	/**
	 * Gets the compile options this file should be compiled with, or null if
	 * they have not beens set yet or have been unset for whatever reason.
	 */
	public CompileOptions getOptions()
	{
		return opts;
	}
	
	/**
	 * Sets the compile options this file should be compiled with. Must be
	 * called with a non-null value before any of the followng methods are
	 * called:
	 *     <ul>
	 *         <li>{@link #getOutputFile()}</li>
	 *         <li>{@link #shouldBuild()}</li>
	 *     </ul>
	 */
	public void setOptions(CompileOptions opts)
	{
		this.opts = opts;
	}
	
	/**
	 * Gets the name of this module
	 */
	public String getModuleName()
	{
		return moduleName;
	}
	
	/**
	 * Checks whether the input file represents a library file
	 */
	public boolean isLibraryFile()
	{
		return isLibraryFile;
	}
    
    /**
     * Renames the initally-output module from the compiler to the mangled name
     * 
     * TODO figure out how to make this compiler-ambivilent
     */
    public void renameOutputFile()
    {
        int index = moduleName.lastIndexOf('.');
        String modulePart = index > 0 ? moduleName.substring(index + 1) : moduleName;
        File original = new File(getOutputPath() + "/" + modulePart + getExtension());
        File target = getOutputFile();
        
        if(!inputFile.exists())
        {
            throw new DebuildException("Could not find expected compiler output file " +
                    original.getAbsolutePath());
        }
        
        if(target.exists())
        {
            if(!target.delete())
            {
                throw new DebuildException("Could not delete file " + 
                        original.getAbsolutePath());
            }
        }
        
        if(!original.renameTo(target))
        {
            throw new DebuildException("Could not rename file " + 
                    original.getAbsolutePath() + " to " + target.getAbsolutePath());
        }
    }
	
	private String getFilename()
	{
        return getMangledName() + getExtension();
	}
	
	private String getMangledName()
	{
		/* This function is responsible for generating a semi-unique name to
		 * give to the object file based on its build options and module name.
		 * This is used for incremental compilation, since if a module's source
		 * hasn't changed, it shouldn't have to be rebuilt unless the build
		 * options are different. This allows for object files from
		 * multiple builds to sit aorund simeltaneously, so if a user is
		 * continuously switching back and forth from a unit test build and
		 * a debug build, the files don't have to be recompiled every time.
		 * 
		 * Unfortunately, since filename lengths are limited by the operating
		 * system and I'm too lazy to implement a more robust system, this uses
		 * a lot of hashing (the hashes are all base64 encoded to help with
		 * reducing filename length). This means collisions *are* possible (in
		 * other words, this method is fundamentally broken). So here's a big
		 * PERHAPS: fix the builder so it uses a separate file or something
		 * instead of filenames full of hashes.
		 */
		
		StringBuffer buf = new StringBuffer();
		
		int flags = 0;
		if(opts.addDebugInfo)           flags |= SYMBOLIC_DEBUG;
		if(opts.addUnittests)           flags |= UNITTEST;
		if(opts.addAssertsAndContracts) flags |= ASSERTS_AND_CONTRACTS;
		if(opts.insertDebugCode)        flags |= ADD_DEBUG_CODE;
		if(opts.inlineFunctions)        flags |= INLINE_FUNCTIONS;
		if(opts.optimizeCode)           flags |= OPTIMIZE_CODE;
		if(opts.instrumentForCoverage)  flags |= INSTRUMENT_FOR_COVERAGE;
		if(opts.instrumentForProfile)   flags |= INSTRUMENT_FOR_PROFILE;
		
		/* Note: To facuiltate cleanup of the output directory, the prefix
		 * must be exactly {@link PREFIX_LENGTH}.
		 */
		buf.append(FILETYPE_PREFIX);
		buf.append(base64Encode(flags, 2));
		buf.append(base64Encode(hashInfo(opts.versionIdents, opts.versionLevel), 6));
		buf.append(base64Encode(hashInfo(opts.debugIdents, opts.debugLevel), 6));
		Assert.isTrue(buf.length() == PREFIX_LENGTH);
		
		String module = moduleName.replace('.', PACKAGE_SEPARATOR);
		if(module.length() <= MAX_MODULE_LENGTH)
		{
			buf.append(module);
		}
		else
		{
			/* If the module name is too long, we hash the beginning of the
			 * filename (since these are more similar, thus reducing the chance
			 * of collisions), then append a unique character (just in case the
			 * hash code conflicts with a module name... it could happen!), then
			 * the end of the module name. This assumes that the prefix stuff
			 * is shorter than {@link #MAX_FILENAME_LENGTH}, and that the maximum
			 * length of any string returned by {@link #base64Encode} will be
			 * at maximum length 6 (thus, the 7 there, since that's the length
			 * of hash string + 1 for the separator)
			 */
			int modulePartLength = MAX_MODULE_LENGTH - 7;
			int start = module.length() - modulePartLength;
			String modulePrefix = module.substring(0, start);
			String modulePostfix = module.substring(start);
			buf.append(base64Encode(modulePrefix.hashCode(), 6));
			buf.append(COMPONENT_SEPARATOR);
			buf.append(modulePostfix);
		}
		
		Assert.isTrue(buf.length() <= MAX_FILENAME_LENGTH);
		return buf.toString();
	}
	
	private static int hashInfo(List<String> idents, Integer level)
	{
		int result = null != level ? level.intValue() * 3001 : 0;
		for(String ident : idents)
		{
			/* Note: this can't be order-dependant, since the order of version
			 * and debug identifiers doesn't matter in the compile
			 */
			result += ident.hashCode() * 31;
		}
		return result;
	}
	
	private static String base64Encode(int val, int length)
	{
		/* Note: this is not any RFC-compiant base64 encoding. Instead, this is
		 * a base64 encoding that attempts to be filesystem-neutral. MIME base64
		 * uses the / character, which is not allowed on certain filesystems. #
		 * replaces / in this encoding.
		 */
		char[] result = new char[length];
		
		int i = length - 1;
		while(val != 0)
		{
			result[i] = BASE_64_CHARS[val & 63];
			val >>>= 6;
			i--;
		}
		
		for(; i >= 0; i--)
			result[i] = BASE_64_PLACEHOLDER;
		
		return new String(result);
	}
	
	private String getExtension()
	{
        return Util.isWindows() ? ".obj" : ".o";
    }
    
    public static void main(String[] args)
    {
        for(Entry<Object, Object> prop : System.getProperties().entrySet())
            System.out.format("%1$s = %2$s\n", prop.getKey(), prop.getValue());
    }
    
    private String getOutputPath()
    {
        try
        {
            return Util.getAbsolutePath(project.getOutputLocation());
        }
        catch(JavaModelException e)
        {
            BuildingPlugin.log(e);
            return null;
        }
    }
	
	/**
	 * Cleans up the output directory, so unused object files (i.e. compiled
	 * with version/debug settings no longer in use, etc.) aren't left around
	 * in the output directory. This is done in two ways. First, for each
	 * module, only <code>versionsToLeave</code> versions will be allowed to
	 * exist at once. Versions removed are those accessed least recently.
	 * Second, if there is a signifigant difference (defined as
	 * <dode>daysToLeave</code> days) in the times between accesses of the most
	 * recently accessed object file for a given module and another object file
	 * in the set, that object file will be deleted.
	 * 
	 * Note that the directory passed to this is generally expected to be
	 * the output directory for a D project that has been untouched by anything
	 * but the debuild builder, so it assumes (for example) that all files whose
	 * names begin with {@link #FILETYPE_PREFIX} are files it can possibly
	 * delete, whose names were generated by this class. If that's not true,
	 * while this method should succeed, it'll probably be deleting random files
	 * (which isn't a good thing...). Note that this suggests that the output]
	 * directory for a project (or whatever directory this method is passed)
	 * should be managed entirely by debuild.
	 * 
	 * @param directory       the directory to clean
	 * @param versionsToLeave number of versions of the same module to leave
	 * @param daysToLeave     difference in days to consider one file
	 *                        "signifigantly older" than another
	 */
	public static void cleanupOutputDirectory(File directory,
			int versionsToLeave, int daysToLeave)
	{
		Assert.isTrue(directory.exists());
		Assert.isTrue(directory.isDirectory());
		
		long timeToLeave = daysToLeave * (24 * 60 * 60 * 1000);
		Map<String, SortedSet<File>> modules = new HashMap<String, 
				SortedSet<File>>();
		
		// Map object files to their 
		for(File file : directory.listFiles())
		{
			String name = file.getName();
			if(file.isDirectory())
				continue;
			if(!name.startsWith(FILETYPE_PREFIX))
				continue;
			if(name.length() < PREFIX_LENGTH + 1)
				continue;
			
			String module = name.substring(PREFIX_LENGTH);
			SortedSet<File> files;
			if(modules.keySet().contains(module))
			{
				files = modules.get(module);
			}
			else
			{
				files = new TreeSet<File>(NEWEST_FIRST_COMPARATOR);
				modules.put(module, files);
			}
			files.add(file);
		}
		
		for(String module : modules.keySet())
		{	
			SortedSet<File> fileSet = modules.get(module);
			File[] files = fileSet.toArray(new File[fileSet.size()]);
			Assert.isTrue(files.length > 0);
			long newestTime = files[0].lastModified();
			long oldestAllowed = newestTime - timeToLeave;
			
			// Remove files whose time difference makes them signifigantly older
			for(int i = 1; (i < files.length) && (i < versionsToLeave); i++)
			{
				if(files[i].lastModified() < oldestAllowed)
				{
					files[i].delete();
				}
			}
			
			// Remove all files in the list past versionsAllowed
			for(int i = versionsToLeave; i < files.length; i++)
			{
				files[i].delete();
			}
		}
	}
	
	@Override
	public String toString()
	{
		if(null != opts)
			return String.format("Object file: %1$s", getMangledName());
		else
			return String.format("Object file: %1$s", getModuleName());
	}
	
	@Override
	public int hashCode()
	{
		return moduleName.hashCode();
	}

	@Override
	public boolean equals(Object other)
	{
		if(null == other)
			return false;
		
		if(!(other instanceof ObjectFile))
			return false;
		
		return moduleName.equals(((ObjectFile) other).moduleName);
	}
}
