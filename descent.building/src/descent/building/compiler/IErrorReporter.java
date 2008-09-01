package descent.building.compiler;

/**
 * A class used to report errors and warnings to the user by attaching markers to
 * files. This class should be used to report all recoverable errors. Unrecoverable
 * errors (that is, those for which the entire build process should be stopped)
 * should instead be reported by throwing a {@link BuildException}.
 * 
 * <b>This class is not intended to be implemented by clients.</b>
 * <b>This class is thread-safe.</b>
 * 
 * @author Robert Fraser
 */
public interface IErrorReporter
{

}
