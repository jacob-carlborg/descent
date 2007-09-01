package melnorme.lang.ui;

import mmrnmhrm.ui.ActualPlugin;
import mmrnmhrm.ui.DeeUIMessages;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;

public class InitializeAfterLoadJob extends UIJob {

	private final class RealJob extends Job {
		public RealJob(String name) {
			super(name);
		}
		
		public void waitForAutoBuild() {
			boolean wasInterrupted = false;
			do {
				try {
					Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
					wasInterrupted = false;
				} catch (OperationCanceledException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					wasInterrupted = true;
				}
			} while (wasInterrupted);
		}
		
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("", 10); //$NON-NLS-1$
			
			try {
				//waitForAutoBuild(); // DLTK Ruby code
				//ActualCore.initializeAfterLoad(new SubProgressMonitor(monitor, 6));
				ActualPlugin.initializeAfterLoad(new SubProgressMonitor(monitor, 4));
			} catch (CoreException e) {
				ActualPlugin.log(e);
				ActualPlugin.initialized = true;
				return e.getStatus();
			}
			
			ActualPlugin.initialized = true;
			return new Status(IStatus.OK, ActualPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
		}
		public boolean belongsTo(Object family) {
			return ActualPlugin.PLUGIN_ID.equals(family);
		}
	}
	
	public InitializeAfterLoadJob() {
		super(DeeUIMessages.InitializeAfterLoadJob_starter_job_name);
		setSystem(true);
	}
	public IStatus runInUIThread(IProgressMonitor monitor) {
		ActualPlugin.initialized = false;
		Job job = new RealJob(DeeUIMessages.LangPlugin_initializing_ui);
		job.setPriority(Job.SHORT);
		job.schedule();
		return new Status(IStatus.OK, ActualPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
	}
	
}