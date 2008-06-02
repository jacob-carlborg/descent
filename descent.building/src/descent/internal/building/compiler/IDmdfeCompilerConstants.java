package descent.internal.building.compiler;

/**
 * Constants for the DMD compiler front-end in use in both GDC and DMD (and probably
 * LLVMDC if it gets released).
 * 
 * @author Robert Fraser
 */
public interface IDmdfeCompilerConstants
{
    public static final String ATTR_ALLOW_DEPRECATED = "descent.building.compiler.dmdfe.allow_deprectaed";
    public static final String ATTR_SHOW_WARNINGS = "descent.building.compiler.dmdfe.show_warnings";
    public static final String ATTR_INLINE_CODE = "descent.building.compiler.dmdfe.inline_code";
    public static final String ATTR_OPTIMIZE_CODE = "descent.building.compiler.dmdfe.optimize_code";
}
