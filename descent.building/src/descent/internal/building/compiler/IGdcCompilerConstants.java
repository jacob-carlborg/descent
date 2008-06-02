package descent.internal.building.compiler;

public interface IGdcCompilerConstants extends IDmdfeCompilerConstants
{
    // TODO is bounds checking on or off by default in GDC? Does release mode
    // change this?
    public static final String ATTR_BOUNDS_CHECK = "descent.building.compiler.gdc.bounds_check";
    
    public static final String ATTR_ALL_SOURCES = "descent.building.compiler.gdc.all_sources";
    public static final String ATTR_EMIT_TEMPLATES = "descent.building.compiler.gdc.emit_templates";
    
    public static final String EMIT_TEMPLATES_AUTO = "auto";
    public static final String EMIT_TEMPLATES_NORMAL = "normal";
    public static final String EMIT_TEMPLATES_PRIVATE = "private";
    public static final String EMIT_TEMPLATES_ALL = "all";
    public static final String EMIT_TEMPLATES_NONE = "none";
}
