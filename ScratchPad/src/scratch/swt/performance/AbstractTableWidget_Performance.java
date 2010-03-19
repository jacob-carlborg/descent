package scratch.swt.performance;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import scratch.swt.SWTApp;
import scratch.swt.UISynchronizer;
import scratch.utils.NewUtils;

import scratch.utils.PerfUtils;
import scratch.utils.PerfUtils.PerfStatistics;
import scratch.utils.PerfUtils.PerfTimer;


public abstract class AbstractTableWidget_Performance extends SWTApp {
	

	public AbstractTableWidget_Performance() {
		super();
	}
	
	protected abstract Control getControl();
	
	protected abstract void createWidget();

	protected abstract void resetWidgetStructure();
	
	protected abstract void refreshWidgetCells();
	
	protected abstract void refreshWidgetCell(int rowIndex, int columIndex);
	
	protected static final int COLUM_WIDTH1 = 100;

	
	protected static int ROW_COUNT;
	protected static int COLUMN_COUNT;

	
	@Override
	public void createShellContents() {
		shell.setLayout(new GridLayout(2, false));
		

//		Button button = new Button(shell, SWT.PUSH);
//		button.setText("Add Items");
//
//		Button button2 = new Button(shell, SWT.PUSH);
//		button2.setText("Test");

//		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		
		shell.setSize(900, 900);
		shell.setLocation(50, 50);
		
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				udpateThread.stopUpdateThread();
			}
		});

		System.out.println("== Beginnning test: "+ getClass().getSimpleName() + " ==");

		udpateThread.start();
	}
	
	protected String getCellText(final int rowIndex, int colIndex) {
		return rowIndex + "-" + colIndex + "-" + NewUtils.TIMESTAMPSHORT_FORMAT.format(new Date())
		+ " sadfsadf d fasdkfl sadf sad \n sad fasd sdaf asdf asd fasd fsad fsdf"
		+ " sadfsadf d fasdkfl sadf sad \n sad fasd sdaf asdf asd fasd fsad fsdf"
		;
	}
	
	protected String getColumnText(int i) {
		return "Col" + i;
	}

	private void recreateWidget() {
		if(getControl() != null) {
			getControl().dispose();
		}
		createWidget();
		getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		shell.layout();
		resetWidgetStructure();
	}

	
	final Runnable REFRESH_ALL_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			if(!getControl().isDisposed())
				refreshWidgetCells();
		}
	};
	
	final Runnable REFRESH_HALF_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			if(!getControl().isDisposed()) {
				for (int i = 0; i < 10; i++) {
					refreshWidgetCell(i*2, i);
				}
			}
		}
	};
	
	final Runnable REFRESH_FEW_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			if(!getControl().isDisposed()) {
				refreshWidgetCell(0, 1);
				refreshWidgetCell(2, 3);
				refreshWidgetCell(10, 5);
			}
		}
	};
	
	
	final TestUpdateThread udpateThread = new TestUpdateThread();
	
	
	private final class TestUpdateThread extends Thread {

		AtomicBoolean doUpdate = new AtomicBoolean(true);
		final UISynchronizer synchronizer = new UISynchronizer();

		
		@Override
		public void run() {
			ROW_COUNT = 50;
			COLUMN_COUNT = 10;
			runScenario("scenario 1", 10, REFRESH_ALL_RUNNABLE);
			
			ROW_COUNT = 50;
			COLUMN_COUNT = 100;
			runScenario("scenario 2", 20, REFRESH_ALL_RUNNABLE);

			ROW_COUNT = 50;
			COLUMN_COUNT = 100;
			runScenario("scenario 2-half", 40, REFRESH_HALF_RUNNABLE);

			ROW_COUNT = 10000;
			COLUMN_COUNT = 100;
			runScenario("scenario 3", 20, REFRESH_ALL_RUNNABLE);


			ROW_COUNT = 10000;
			COLUMN_COUNT = 100;
			runScenario("scenario 3-few", 40, REFRESH_FEW_RUNNABLE);
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					shell.dispose();
				}
			});
		}

		private void runScenario(final String scenarioName, int cycles, Runnable runnable) {
			System.out.println();
			System.out.println(" --- Starting scenario "+ scenarioName);
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					recreateWidget();
					shell.setText(scenarioName);
				}
			});
			
			final PerfTimer timer1 = PerfUtils.timer(scenarioName);
			updateWidgetWithEvents(timer1, cycles, runnable);
			final PerfStatistics stats = timer1.getStats();
			System.out.println("Scenario complete. Total runs: " + cycles);
			System.out.println(" >> Total time: " + stats.getTotalTime() 
					+ " Average time: " + stats.getAverage() +  " (" + stats.getAverage2() + ")");
		}
		
		private void updateWidgetWithEvents(PerfTimer perfTimer, int cycles, Runnable updateRunnable) {
			int counter = cycles + 10;
			while(counter > 0) {
				if(!doUpdate.get()) {
					throw new RuntimeException("exit");
				}
				
				if(counter < cycles ) { // Ignore first 10 updates
					perfTimer.markBegin();
				}

				Display.getDefault().asyncExec(updateRunnable);
				try {
					synchronizer.waitOnUI();
				} catch (InterruptedException e) {
				}
				
				if(counter < cycles ) { // Ignore first 10 updates
					perfTimer.markEnd();
				}
				
				counter--;
			}
		}
		
		public void stopUpdateThread() {
			try {
				doUpdate.set(false);
				synchronizer.awake();
				if(false)
					udpateThread.join();
			} catch (InterruptedException ie) {
				throw melnorme.miscutil.ExceptionAdapter.unchecked(ie);
			}
		}
	}

}