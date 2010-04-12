package testproduct;

import static melnorme.miscutil.Assert.assertTrue;
import melnorme.miscutil.MiscUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

public class Test_Jobs {

	

	public static void testJobs() {
		testJobs1();
	}
	
	private static void testJobs2() {
		
		new Thread() {
			@Override
			public void run() {
			}
		}.start();
		
		Job job = new Job("UI-Lock-Test") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						System.out.println("in sync output");
					}
				});
				return Status.OK_STATUS;
			}
		};
		
		System.out.println("UI starting job");
		job.schedule();
		System.out.println("UI joining with job");
		try {
			job.join(); 
		} catch (InterruptedException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
		System.out.println("UI joined with job");
		
	}
	
	
	private static final class PrintJob extends Job {
		private PrintJob(String name) {
			super(name);
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if(monitor.isCanceled())
				return Status.CANCEL_STATUS;
			System.out.println("running: " + getName() + " - " + monitor.isCanceled());
			MiscUtil.sleepUnchecked(2000);
			System.out.println("ending: " + getName() + " - " + monitor.isCanceled());
			return Status.OK_STATUS;
		}
	}
	

	private static void testJobs1() {

		System.out.println(">> Scheduling job X");
		PrintJob printJob = new PrintJob("print");
		printJob.schedule();
		MiscUtil.sleepUnchecked(10);
		printJob.schedule();
		printJob.cancel();
//		try {
//			printJob.join();
//		} catch (InterruptedException e1) {
//			throw melnorme.miscutil.ExceptionAdapter.uncheckedTODO(e1);
//		}
		printJob.cancel();
		
		Job job = new Job("Test1") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				System.out.println("Started test1");
				int counter = 5000;
				while(counter-- > 0) {
					if(!monitor.isCanceled()) {
						MiscUtil.sleepUnchecked(2);
					}
				}
				System.out.println("ended test1");
				return Status.OK_STATUS;
			}
		};
		
		System.out.println(">> Scheduling job");
		assertTrue(job.getState() == Job.NONE);
		job.schedule();

		try {
			job.join();
		} catch (InterruptedException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
		assertTrue(job.getState() == Job.NONE);
		MiscUtil.sleepUnchecked(10);
		System.out.println(">> Canceling job");
		job.cancel();
		MiscUtil.sleepUnchecked(100);
		System.out.println(">> Scheduling job 2");
		job.schedule();
		assertTrue(job.getState() == Job.RUNNING);
		System.out.println(">> Canceling job 2");
		job.cancel();
		MiscUtil.sleepUnchecked(100);
		System.out.println(">> Scheduling job 3");
		job.schedule();
	}
}
