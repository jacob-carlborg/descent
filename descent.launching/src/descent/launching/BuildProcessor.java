package descent.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import descent.internal.launching.LaunchingPlugin;
import descent.internal.launching.rebuild.RebuildBuilder;

public class BuildProcessor
{
	//--------------------------------------------------------------------------
	@SuppressWarnings("serial")
	public static class BuildFailedException extends RuntimeException
	{
		public BuildFailedException()
		{
			
		}
		
		public BuildFailedException(String message)
		{
			super(message);
		}
	}
	
	@SuppressWarnings("serial")
	public static class BuildCancelledException extends RuntimeException
	{
		public BuildCancelledException()
		{
			
		}
		
		public BuildCancelledException(String message)
		{
			super(message);
		}
	}
	
	//--------------------------------------------------------------------------
	// Build request processor
	
	public String build(IExecutableTarget target, IProgressMonitor pm)
	{
		try
		{
			String executableFilePath = RebuildBuilder.build(target, pm);
			Assert.isTrue(null != executableFilePath);
			notifyBuildSucceeded(target, executableFilePath);
			return executableFilePath;
		}
		catch(BuildFailedException failed)
		{
			notifyBuildFailed(target);
			return null;
		}
		catch(BuildCancelledException cancelled)
		{
			notifyBuildCancelled(target);
			return null;
		}
	}
	
	//--------------------------------------------------------------------------
	// Notification framework
	private static abstract class ListenerSafeRunnable implements ISafeRunnable
	{
		public void handleException(Throwable exception)
		{
			LaunchingPlugin.log(exception);
		}
	}
	
	private void notifyBuildStarted(final IExecutableTarget build)
	{
		SafeRunner.run(new ListenerSafeRunnable()
		{
			public void run() throws Exception
			{
				for(IRebuildEventListener listener : listeners)
				{
					listener.buildStarted(build);
				}
			}
		});
	}
	
	private void notifyBuildFailed(final IExecutableTarget build)
	{
		SafeRunner.run(new ListenerSafeRunnable()
		{
			public void run() throws Exception
			{
				for(IRebuildEventListener listener : listeners)
				{
					listener.buildFailed(build);
				}
			}
		});
	}
	
	private void notifyBuildCancelled(final IExecutableTarget build)
	{
		SafeRunner.run(new ListenerSafeRunnable()
		{
			public void run() throws Exception
			{
				for(IRebuildEventListener listener : listeners)
				{
					listener.buildCancelled(build);
				}
			}
		});
	}
	
	private void notifyBuildSucceeded(final IExecutableTarget build,
			final String executableFilePath)
	{
		SafeRunner.run(new ListenerSafeRunnable()
		{
			public void run() throws Exception
			{
				for(IRebuildEventListener listener : listeners)
				{
					listener.buildSucceeded(build, executableFilePath);
				}
			}
		});
	}
	
	//--------------------------------------------------------------------------
	// Listener management
	
	private final List<IRebuildEventListener> listeners = 
		new ArrayList<IRebuildEventListener>();
	
	public void addListener(IRebuildEventListener listener)
	{
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeListener(IRebuildEventListener listener)
	{
		listeners.remove(listener);
	}
	
	//--------------------------------------------------------------------------
	// Instance management
	
	private static BuildProcessor instance = new BuildProcessor();
	
	private BuildProcessor()
	{
		
	}
	
	public BuildProcessor getInstance()
	{
		return instance;
	}
}