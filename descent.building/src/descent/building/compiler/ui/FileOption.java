package descent.building.compiler.ui;

public class FileOption extends StringOption
{
    /**
     * Option switch indicating that files can be selected.
     */
    public static final int TYPE_FILE = 1;
    
    /**
     * Option switch indicating that folders can be selected.
     */
    public static final int TYPE_FOLDER = 2;
    
    /**
     * Select existing files only ({@link #TYPE_FILE} allows wither existing or
     * new files to be selected). Will present an open dialog. Implies
     * {@link #TYPE_FILE}. Note that {@link #TYPE_FOLDER} requires existing 
     * folders. This does not validate whether the files/folder exists, it
     * just requires the user select an existing file when the UI is presented.
     * If the file has been deleted since the option was set, this will need
     * to be checked in the actual builder.
     */
    public static final int TYPE_EXISTING_FILE = 4;
    
    private final int type;
    
    public FileOption(String attributeId, String defaultValue, String label,
            String groupLabel, int type)
    {
        super(attributeId, defaultValue, label, groupLabel);
        if(0 != (type & TYPE_EXISTING_FILE))
            type |= TYPE_FILE;
        this.type = type;
    }
    
    public final int getType()
    {
        return type;
    }
    
    public String[] getExtensions()
    {
        return new String[] { "*.*" }; //$NON-NLS-1$
    }
}
