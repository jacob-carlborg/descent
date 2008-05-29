package descent.building.compiler;

public class StringOption extends CompilerOption implements IValidatableOption
{
    public StringOption(String attributeId, String defaultValue,
            String label, String groupLabel, String helpText)
    {
        super(attributeId, defaultValue, label, groupLabel, helpText);
    }
    
    public String isValid(String value)
    {
        return null;
    }
}
