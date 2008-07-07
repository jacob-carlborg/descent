package descent.building.compiler;

import java.io.File;

public interface IObjectFile
{
    public File getInputFile();
    public File getOutputFile();
    public boolean isLibraryFile();
}
