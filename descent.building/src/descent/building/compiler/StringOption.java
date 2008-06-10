package descent.building.compiler;

public abstract class StringOption extends CompilerOption implements IValidatableOption
{
    public StringOption(String attributeId, String defaultValue,
            String label, String groupLabel)
    {
        super(attributeId, defaultValue, label, groupLabel);
    }

    public String isValid(String value)
    {
        return null;
    }
}
