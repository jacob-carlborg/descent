package descent.launching.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBuildCommand extends AbstractExecutableCommand 
	implements IBuildCommand
{
	protected List<File> files;
	
	@Override
	public void setDefaults()
	{
		super.setDefaults();
		files = null;
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
	
	public void setFiles(List<File> files)
	{
		this.files = files;
	}
}
