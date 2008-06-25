package descent.internal.building.compiler;

public interface IGdcCompilerConstants extends IDmdfeCompilerConstants
{
    // TODO is bounds checking on or off by default in GDC? Does release mode
    // change this?
    public static final String ATTR_BOUNDS_CHECK = "descent.building.compiler.gdc.bounds_check"; //$NON-NLS-1$
    
    public static final String ATTR_ALL_SOURCES = "descent.building.compiler.gdc.all_sources"; //$NON-NLS-1$
    public static final String ATTR_EMIT_TEMPLATES = "descent.building.compiler.gdc.emit_templates"; //$NON-NLS-1$
    
    public static final String EMIT_TEMPLATES_AUTO = "auto"; //$NON-NLS-1$
    public static final String EMIT_TEMPLATES_NORMAL = "normal"; //$NON-NLS-1$
    public static final String EMIT_TEMPLATES_PRIVATE = "private"; //$NON-NLS-1$
    public static final String EMIT_TEMPLATES_ALL = "all"; //$NON-NLS-1$
    public static final String EMIT_TEMPLATES_NONE = "none"; //$NON-NLS-1$
}
