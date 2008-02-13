package descent.core.builder;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBuildCommand extends AbstractExecutableCommand 
	implements IBuildCommand
{
	protected final List<String> files = new ArrayList<String>();
	
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
	
	public final List<String> getFiles()
	{
		return files;
	}
	
	public void addFile(String file)
	{
		if(!files.contains(file))
			files.add(file);
	}
}
