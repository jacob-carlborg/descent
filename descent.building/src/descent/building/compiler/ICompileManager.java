package descent.building.compiler;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The build process contains some processes that are compiler-ambivalent, and
 * others that depend on the compiler being used. The compiler-ambivalent parts
 * are managed internally by the builder, and an instance of this class is used
 * to manage compiler-specific parts. The lifecycle of a build involves
 * cooperation between this class and the builder (accessed via an
 * {@link IBuildManager}), with multiple calls made back and forth between the
 * two components. From the perspective of the compiler implementer, the
 * lifecycle works roughly like this:
 * 
 * <ol>
 *     <li>For each build, this class will be constructed via the 
 *         {@link ICompilerInterface#getCompileManager(IBuildManager)} class.</li>
 *         
 *     <li>When an object file needs to be compiled, the 
 *         {@link #compile(IObjectFile)} method will be called.</li>
 *         
 *     <li>During the course of building, methods on the {@link IBuildManager}
 *         should be called to handle things like executing specific commands,
 *         reporting errors, or getting build information.</li>
 *         
 *     <li>TODO linking</li>
 * </ol>
 * 
 * <b>This class should be implemented by clients providing a compiler interface.</b>
 * 
 * @author Robert Fraser
 */
public interface ICompileManager
{
    /**
     * Compiles the given object files. If multiple files are passed, they may
     * be built together. If 
     * {@link ICompilerInterface#supportsInternalDependancyAnalysis()} returns
     * <code>true</code> for this compiler type, this class should return a 
     * non-null array of the names of all modules imported by the given object
     * files (whether or not these files need to be built should be decided by
     * the builder). If supportsInternalDependancyAnalysis() is
     * <code>false</code>, the return of this method will be ignored.
     * 
     * @param objectFiles the object files to be built
     * @param pm          a monitor to rack the progress of the operation
     * @return            a list of files imported by the given targets
     */
    public String[] compile(IObjectFile[] objectFiles, IProgressMonitor pm);
}
