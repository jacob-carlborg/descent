package descent.internal.building.compiler;

public interface IDmdCompilerConstants
{
    // TODO comment
    
    // Compiler
    public static final String ATTR_ALLOW_DEPRECATED = "descent.building.compiler.dmd.allow_deprectaed";
    public static final String ATTR_SHOW_WARNINGS = "descent.building.compiler.dmd.show_warnings";
    public static final String ATTR_INLINE_CODE = "descent.building.compiler.dmd.inline_code";
    public static final String ATTR_OPTIMIZE_CODE = "descent.building.compiler.dmd.optimize_code";
    public static final String ATTR_NOFLOAT = "descent.building.compiler.dmd.nofloat";
    
    // TODO linker options (both optlink and ld). Many of these options will be
    // passed by the compiler, and about half of OPTLINK's options are ignored or
    // apply to 16-bit code anyways. It may be fine to just have a box for "additional
    // arguments" somewhere and allow the user to put them in as they want.
}
