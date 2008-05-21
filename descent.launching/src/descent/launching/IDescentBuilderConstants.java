package descent.launching;

/**
 * These are the attributes in use by the default Descent builder. They are
 * exposed because other plugins (notably descent.unittest, maybe a profiling
 * plugin at some point, too) within the umbrellas of the Descent project
 * depend on these constants. While the entire API is subject to change at any
 * moment, these should generally b kept as deprecated aliases if any changes
 * are made, which should allow other plugins (not under the direct umbrella of
 * the Descent project) to use the Descent default builder. However, the exact
 * selection of which attributes are included in this interface and which are
 * internal to this plugin is rather arbitrary, and will remain so until the
 * API for the default Descent builder is finalized.
 * 
 * @author Robert Fraser
 */
public interface IDescentBuilderConstants
{
    /**
     * The ID of the default descent builder.
     */
    public static final String ID_DESCENT_BUILDER = "descent.launching.builders.debuildBuilder";
    
    /**
     * The launch configuration type associated with default descent builder.
     * These attributes should be used in launch configurations of this type
     * only.
     */
    public static final String ID_LAUNCH_CONFIGURATION_TYPE = "descent.launching.builders.debuild";
}
