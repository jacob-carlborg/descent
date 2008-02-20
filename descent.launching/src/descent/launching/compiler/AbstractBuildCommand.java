package descent.launching.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBuildCommand extends AbstractExecutableCommand 
	implements IBuildCommand
{
	protected final List<File> files = new ArrayList<File>();
	
	@Override
	public void setDefaults()
	{
		super.setDefaults();
		files.clear();
	}
	
	@Override
	public boolean isValid()
	{
		if(files.isEmpty())
			return false;
		
		return super.isValid();
	}
	
	public final List<File> getFiles()
	{
		return files;
	}
	
	public void addFile(File file)
	{
		if(!files.contains(file))
			files.add(file);
	}
}
