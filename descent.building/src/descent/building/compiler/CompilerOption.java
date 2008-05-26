package descent.building.compiler;

public abstract class CompilerOption
{
    private final String attributeId;
    private final String defaultValue;
    private final String label;
    private final String groupLabel;
    
    public CompilerOption(String attributeId, String defaultValue, String label,
            String groupLabel)
    {
        this.attributeId = attributeId;
        this.defaultValue = defaultValue;
        this.label = label;
        this.groupLabel = groupLabel;
    }
    
    public final String getAttributeId()
    {
        return attributeId;
    }

    public final String getDefaultValue()
    {
        return defaultValue;
    }

    public final String getLabel()
    {
        return label;
    }

    public final String getGroupLabel()
    {
        return groupLabel;
    }
}
