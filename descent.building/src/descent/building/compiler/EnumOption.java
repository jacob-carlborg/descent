package descent.building.compiler;

public class EnumOption extends CompilerOption
{
    private final String[] optionValues;
    private final String[] optionEditLabels;
    private final String[] optionViewLabels;
    
    public EnumOption(String attributeId, String defaultValue,
            String label, String groupLabel, String[] optionValues,
            String[] optionEditLabels, String helpText)
    {
        this(attributeId, defaultValue, label, groupLabel, 
                optionValues, optionEditLabels, null, helpText);
    }
    
    public EnumOption(String attributeId, String defaultValue,
            String label, String groupLabel, String[] optionValues,
            String[] optionEditLabels, String[] optionViewLabels,
            String helpText)
    {
        super(attributeId, defaultValue, label, groupLabel, helpText);
        this.optionValues = optionValues;
        this.optionEditLabels = optionEditLabels;
        
        if(null == optionViewLabels)
        {
            optionViewLabels = new String[optionEditLabels.length];
            for(int i = 0; i < optionEditLabels.length; i++)
                optionViewLabels[i] = optionEditLabels[i];
        }
        this.optionViewLabels = optionViewLabels;
    }

    public final String[] getOptionValues()
    {
        return optionValues;
    }

    public final String[] getOptionEditLabels()
    {
        return optionEditLabels;
    }

    public final String[] getOptionViewLabels()
    {
        return optionViewLabels;
    }
}
