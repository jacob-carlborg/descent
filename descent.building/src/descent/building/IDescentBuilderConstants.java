package descent.building;

/**
 * These are the attributes in use by the default Descent builder. They are
 * exposed because other plugins (notably descent.unittest, maybe a profiling
 * plugin at some point, too) within the umbrellas of the Descent project
 * depend on these constants. While the entire API is subject to change at any
 * moment, these should generally b kept as deprecated aliases if any changes
 * are made, which should allow other plugins (not under the direct umbrella of
 * the Descent project) to use the Descent default builder. However, the exact
 * selection of which attributes are included in this interface is rather 
 * arbitrary, and will remain so until the API for the default Descent builder
 * is finalized.
 * 
 * @author Robert Fraser
 */
public interface IDescentBuilderConstants
{
    /**
     * The ID of the default descent builder.
     */
    public static final String ID_DESCENT_BUILDER = "descent.building.builders.debuildBuilder";
    
    /**
     * The launch configuration type associated with default descent builder.
     * These attributes should be used in launch configurations of this type
     * only.
     */
    public static final String ID_LAUNCH_CONFIGURATION_TYPE = "descent.building.builders.debuild";
    
    /**
     * The project being built (so version/debug/etc. settings should be taken
     * from the project if not overidden here, the output folder in whcih to
     * place stuff, etc.)
     * 
     * Type: String
     */
    public static final String ATTR_PROJECT_NAME = "descent.building.debuild.project_name";
    
    /**
     * A list containing the fully-qualified names of all modules which should
     * be built (with their dependencies). This may include modules from outside
     * the project. If any modules in the list do not exist, they should be
     * ignored by the builder, but may be treated as error conditions by the UI
     * or anything else that uses the launch configuration.
     * 
     * Type: List&lt;String&gt;
     */
    public static final String ATTR_MODULES_LIST = "descent.building.debuild.modules_list";
    
    /**
     * The ID of the {@link descent.launching.IVMInstallType} of the type of 
     * the selected compiler (this is needed since the IDs of different
     * {@link descent.launching.IVMInstall}s are not necessarily unique.
     * 
     * Type: String
     */
    public static final String ATTR_COMPILER_TYPE_ID = "descent.building.debuild.compiler_type_id";
    
    /**
     * The ID of the {@link descent.launching.IVMInstall} to use for the
     * compile. Must be of the type specified in {@link #ATTR_COMPILER_TYPE_ID}.
     * 
     * Type: String
     */
    public static final String ATTR_COMPILER_ID = "descent.building.debuild.compiler_id";
}
