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
    
    /**
     * Specifies the target output type. Must be one of {@link #OUTPUT_TYPE_EXECUTABLE}
     * or {@link #OUTPUT_TYPE_STATIC_LIBRARY}. Support may be added in the future for
     * OS dynamic libraries (.dll/.so) and/or DDL.
     * 
     * Type: String
     */
    public static final String ATTR_OUTPUT_TYPE = "descent.building.debuild.output_type";
    
    /**
     * Specifies the target output file location.
     * 
     * Type: String
     */
    public static final String ATTR_OUTPUT_FILE = "descent.building.debuild.output_file";
    
    /**
     * Add unit tests to the generated executable?
     * 
     * Type: String of "true" or "false"
     */
    public static final String ATTR_ADD_UNITTESTS = "descent.building.debuild.add_unittests";
    
    /**
     * Disable asserts and contracts (in DMD this is called "release mode"). In
     * other compilers, if asserions, contracts, and array bounds errors can be
     * disabled separately, this option should only be for disabling asserts,
     * however clients should be aware that other features may be disabled as well
     * in generated code.
     * 
     * If the compiler does not support disabling of assertions, this option
     * should be ignored. If the compiler does not support assertions at all, but
     * this attribute was specified outside the UI to be explicitly "false", an error
     * should be marked.
     * 
     * Type: String of "true" or "false"
     */
    public static final String ATTR_DISABLE_ASSERTS = "descent.building.debuild.disable_asserts";
    
    /**
     * Include debugging symbols in the generated executable. 
     * 
     * If the compiler supports multiple kinds of debugging symbols, the system
     * default should be used. If a default cannot be determined or the compiler
     * does not support adding of debugging symbols, an error should be marked.
     * 
     * Type: String of "true" or "false"
     */
    public static final String ATTR_ADD_DEBUG_INFO = "descent.building.debuild.add_debug_info";
    
    /**
     * Include code allowing code coverage to be tracked in the generated
     * executable.
     * 
     * If the compiler does not support debug symbols, an error should be marked.
     * 
     * Type: String of "true" or "false"
     */
    public static final String ATTR_INSTRUMENT_FOR_COVERAGE = "descent.building.debuild.instrument_for_coverage";
    
    /**
     * Include code for profiling the executable.
     * 
     * If the compiler supports multiple kinds of profile information, the system
     * default should be used. If a default cannot be determined or the compiler
     * does not support adding of profile information, an error should be marked.
     * 
     * Type: String of "true" or "false"
     */
    public static final String ATTR_INSTRUMENT_FOR_PROFILE = "descent.building.debuild.instrument_for_profile";
    
    /**
     * A set of additional compiler arguments that should be added to
     * the command line when calling the compiler.
     * 
     * Type: String
     */
    public static final String ATTR_ADDITIONAL_COMPILER_ARGS = "descent.building.debuild.compiler_args";
    
    /**
     * A set of additional linker arguments that should be added to
     * the command line when calling the linker.
     * 
     * Type: String
     */
    public static final String ATTR_ADDITIONAL_LINKER_ARGS = "descent.building.debuild.linker_args";
    
    /**
     * Constant used for {@link #ATTR_OUTPUT_TYPE} to specify the output target
     * should be an executable file.
     */
    public static final String OUTPUT_TYPE_EXECUTABLE = "executable";
    
    /**
     * Constant used for {@link #ATTR_OUTPUT_TYPE} to specify the output target
     * should be a static library (.lib or .a)
     */
    public static final String OUTPUT_TYPE_STATIC_LIBRARY = "static_library";
}
