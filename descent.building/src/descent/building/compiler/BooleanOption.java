package descent.building.compiler;

public final class BooleanOption extends CompilerOption
{
    private final String onText;
    private final String offText;
    
    public BooleanOption(String attributeId, boolean defaultValue,
            String label, String groupLabel, String onText, String offText)
    {
        super(attributeId, defaultValue ? "true" : "false", label, groupLabel);
        this.onText = onText;
        this.offText = offText;
    }

    public final String getOnText()
    {
        return onText;
    }

    public final String getOffText()
    {
        return offText;
    }
}
