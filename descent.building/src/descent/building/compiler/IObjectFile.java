package descent.building.compiler;

import java.io.File;

/**
 * Ties together an input file (a D module) and an output file (the target .obj
 * file). Instances of this interface represent fully-qualified D modules with
 * their target outputs from a build.
 * 
 * There should be only one of this class for any given module in or referenced by
 * a build. The {@link #equals(Object)} method should return <code>true</true> for
 * two object file if they refer to the same module.
 * 
 * <b>This class is not intended to be implemented by clients.</b>
 * 
 * @author Robert Fraser
 */
public interface IObjectFile
{
    /**
     * Gets the name of the D module to which this refers.
     */
    public String getName();
    
    /**
     * Gets the D file which is the module's source code. This file should
     * exist, but be sure to check for existence before use.
     */
    public File getInputFile();
    
    /**
     * Gets the object file (which may or may not exist) which is the target
     * output for a compile execution.
     */
    public File getOutputFile();
    
    /**
     * Checks whether this module is local to the project/workspace or is
     * referenced from a source library.
     */
    public boolean isLibraryFile();
}
