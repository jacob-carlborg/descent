package descent.internal.launching.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;

import descent.core.IJavaProject;
import descent.launching.compiler.ICompileCommand;

public class ObjectFile extends AbstractBinaryFile
{
	public static final int MAX_FILENAME_LENGTH      = 127;
	public static final int PREFIX_LENGTH            = 15;
	
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
	
	public String moduleName;
	
	public boolean addDebugInfo;
	public boolean addUnittests;
	public boolean addAssertsAndContracts;
	public boolean insertDebugCode;
	public boolean inlineFunctions;
	public boolean optimizeCode;
	public boolean instrumentForCoverage;
	public boolean instrumentForProfile;
	
	public final List<String> debugIdents = new ArrayList<String>();
	public final List<String> versionIdents = new ArrayList<String>();
	public Integer debugLevel;   // Or null if no debug level
	public Integer versionLevel; // Or null if no version level
	
	public ObjectFile(IJavaProject proj)
	{
		super(proj);
		setDefaults();
	}
	
	@Override
	public boolean isValid()
	{
		if(!super.isValid())
			return false;
		
		return moduleName != null;
	}
	
	@Override
	public String getFilename()
	{
		return getMangledName() + "." + getExtension();
	}
	
	/**
	 * Sets the options on the command to match the options in this file. Will
	 * not clear existing debug and version idents, if any have been added to
	 * the command, and will not change any of the command's options not
	 * covered by the scope of this class. The options it will set are:
	 *     - addDebugInfo
	 *     - addUnittests
	 *     - addAssertsAndContracts
	 *     - insertDebugCode
	 *     - inlineFunctions
	 *     - optimizeCode
	 *     - instrumentForCoverage
	 *     - instrumentForProfile
	 *     - versionLevel
	 *     - versionIdents
	 *     - debugLevel
	 *     - debugIdents
	 * 
	 * @param cmd the command to set the options for
	 */
	public void setCompileCommandOptions(ICompileCommand cmd)
	{
		cmd.setAddDebugInfo(addDebugInfo);
		cmd.setAddUnittests(addUnittests);
		cmd.setAddAssertsAndContracts(addAssertsAndContracts);
		cmd.setInsertDebugCode(insertDebugCode);
		cmd.setInlineFunctions(inlineFunctions);
		cmd.setOptimizeCode(optimizeCode);
		cmd.setInstrumentForCoverage(instrumentForCoverage);
		cmd.setInstrumentForProfile(instrumentForProfile);
		
		cmd.setVersionLevel(versionLevel);
		for(String versionIdent : versionIdents)
			cmd.addVersionIdent(versionIdent);
		
		cmd.setDebugLevel(debugLevel);
		for(String debugIdent : debugIdents)
			cmd.addVersionIdent(debugIdent);
	}
	
	private void setDefaults()
	{
		moduleName = null;
		addDebugInfo = true;
		addUnittests = false;
		addAssertsAndContracts = true;
		inlineFunctions = false;
		optimizeCode = false;
		instrumentForCoverage = false;
		instrumentForProfile = false;
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
		 * TODO: fix the builder so it uses a separate file or something
		 * instead of filenames full of hashes.
		 */
		
		StringBuffer buf = new StringBuffer();
		
		int opts = 0;
		if(addDebugInfo)           opts |= SYMBOLIC_DEBUG;
		if(addUnittests)           opts |= UNITTEST;
		if(addAssertsAndContracts) opts |= ASSERTS_AND_CONTRACTS;
		if(insertDebugCode)        opts |= ADD_DEBUG_CODE;
		if(inlineFunctions)        opts |= INLINE_FUNCTIONS;
		if(optimizeCode)           opts |= OPTIMIZE_CODE;
		if(instrumentForCoverage)  opts |= INSTRUMENT_FOR_COVERAGE;
		if(instrumentForProfile)   opts |= INSTRUMENT_FOR_PROFILE;
		
		/* Note: To facuiltate cleanup of the output directory, the prefix
		 * must be exactly {@link PREFIX_LENGTH}.
		 */
		buf.append(FILETYPE_PREFIX);
		buf.append(base64Encode(opts, 2));
		buf.append(base64Encode(hashInfo(versionIdents, versionLevel), 6));
		buf.append(base64Encode(hashInfo(debugIdents, debugLevel), 6));
		Assert.isTrue(buf.length() == PREFIX_LENGTH);
		
		int remaining = MAX_FILENAME_LENGTH - PREFIX_LENGTH;
		String module = moduleName.replace('.', PACKAGE_SEPARATOR);
		if(module.length() <= remaining)
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
			int modulePartLength = remaining - 7;
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
		
		int i = length;
		while(val != 0)
		{
			result[i] = BASE_64_CHARS[val & 63];
			val >>>= 6;
			i--;
		}
		
		for(; i >= 0; i++)
			result[i] = BASE_64_PLACEHOLDER;
		
		return new String(result);
	}
	
	private String getExtension()
	{
		// TODO
		return "obj";
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
}
