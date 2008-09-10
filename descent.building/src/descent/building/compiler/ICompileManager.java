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
 *     <li>After all compiler-ambivalent preparation has been completed, the
 *         {@link #compile(IObjectFile[], IProgressMonitor)} method will be
 *         called on this object. After this call, this object takes over the
 *         thread with the goal of producing a compiled resource.</li>
 *         
 *     <li>During the course of building, methods on the {@link IBuildManager}
 *         should be called to handle things like executing specific commands,
 *         reporting errors, or getting build information.</li>
 * </ol>
 * 
 * <b>This class should be implemented by clients providing a compiler interface.</b>
 * <b>Objects of this class do not need to be thread-safe, however multiple
 *    instances may exist at once (for example, if multiple builds are running
 *    concurrently), so all access to static data should be locked.</b>
 * 
 * @author Robert Fraser
 */
public interface ICompileManager
{
    /**
     * Compiles the given object files to form an executable.
     * DOCS moar
     * 
     * @param objectFiles the object files to be built
     * @param pm          a monitor to rack the progress of the operation
     * @return            the full path of the output file if and only if the
     *                    compilation was successful, null otherwise
     */
    public boolean compile(IObjectFile[] objectFiles, IProgressMonitor pm);
}
