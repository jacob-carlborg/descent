package descent.internal.core.builder.debuild;

import java.io.File;

import org.eclipse.core.runtime.IPath;

import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

public abstract class AbstractBinaryFile implements IBinaryFile
{
	protected IJavaProject proj;
	
	protected AbstractBinaryFile(IJavaProject proj)
	{
		this.proj = proj;
	}
	
	public boolean isValid()
	{
		return null != proj && proj.exists();
	}
	
	public final File getFile()
	{
		try
		{
			IPath outputLocation = proj.getOutputLocation();
			StringBuffer path = new StringBuffer();
			path.append(outputLocation.makeAbsolute().toPortableString());
			path.append(IPath.SEPARATOR);
			path.append(getFilename());
			return new File(path.toString());
		}
		catch(JavaModelException e)
		{
			Util.log(e);
			return null;
		}
	}

	public final boolean shouldBuild(long date)
	{
		File file = getFile();
		
		if(!file.exists())
			return true;
		
		if(file.lastModified() < date)
			return true;
		
		return false;
	}
	
	public abstract String getFilename();
}
