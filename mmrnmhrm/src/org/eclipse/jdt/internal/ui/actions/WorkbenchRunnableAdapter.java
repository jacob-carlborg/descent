package org.eclipse.jdt.internal.ui.actions;


import java.lang.reflect.InvocationTargetException;

import mmrnmhrm.core.jdtadapt.JavaCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.jdt.internal.ui.JavaUIStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.IThreadListener;

/**
 * An <code>IRunnableWithProgress</code> that adapts an <code>IWorkspaceRunnable</code>
 * so that is can be executed inside <code>IRunnableContext</code>. <code>OperationCanceledException</code> 
 * thrown by the adapted runnable are caught and re-thrown as a <code>InterruptedException</code>.
 */
public class WorkbenchRunnableAdapter implements IRunnableWithProgress, IThreadListener {
	
	private boolean fTransfer= false;
	private IWorkspaceRunnable fWorkspaceRunnable;
	private ISchedulingRule fRule;
	
	/**
	 * Runs a workspace runnable with the workspace lock.
	 */
	public WorkbenchRunnableAdapter(IWorkspaceRunnable runnable) {
		this(runnable, ResourcesPlugin.getWorkspace().getRoot());
	}
	
	/**
	 * Runs a workspace runnable with the given lock or <code>null</code> to run with no lock at all.
	 */
	public WorkbenchRunnableAdapter(IWorkspaceRunnable runnable, ISchedulingRule rule) {
		fWorkspaceRunnable= runnable;
		fRule= rule;
	}
	
	/**
	 * Runs a workspace runnable with the given lock or <code>null</code> to run with no lock at all.
	 * @param transfer <code>true</code> if the rule is to be transfered 
	 *  to the model context thread. Otherwise <code>false</code>
	 */
	public WorkbenchRunnableAdapter(IWorkspaceRunnable runnable, ISchedulingRule rule, boolean transfer) {
		fWorkspaceRunnable= runnable;
		fRule= rule;
		fTransfer= transfer;
	}
	
	
	public ISchedulingRule getSchedulingRule() {
		return fRule;
	}

	/** {@inheritDoc} */
	public void threadChange(Thread thread) {
		if (fTransfer)
			Platform.getJobManager().transferRule(fRule, thread);
	}

	/** {@inheritDoc} */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			JavaCore.run(fWorkspaceRunnable, fRule, monitor);
		} catch (OperationCanceledException e) {
			throw new InterruptedException(e.getMessage());
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}
	
	// From JDT, todo
	public void runAsUserJob(String name, final Object jobFamiliy) {
		Job buildJob = new Job(name){ 
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			protected IStatus run(IProgressMonitor monitor) {
				try {
					WorkbenchRunnableAdapter.this.run(monitor);
				} catch (InvocationTargetException e) {
					Throwable cause= e.getCause();
					if (cause instanceof CoreException) {
						return ((CoreException) cause).getStatus();
					} else {
						return JavaUIStatus.createError(IStatus.ERROR, cause);
					}
				} catch (InterruptedException e) {
					return Status.CANCEL_STATUS;
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
			public boolean belongsTo(Object family) {
				return jobFamiliy == family;
			}
		};
		buildJob.setRule(fRule);
		buildJob.setUser(true); 
		buildJob.schedule();
		
		// TODO: should block until user pressed 'to background'
	}
}

