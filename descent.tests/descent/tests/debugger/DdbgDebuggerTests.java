package descent.tests.debugger;

import java.io.IOException;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;

import descent.internal.launching.model.ddbg.DdbgDebugger;
import descent.launching.model.IDebugElementFactory;
import descent.launching.model.IDebugger;
import descent.launching.model.IDebuggerListener;

public class DdbgDebuggerTests extends MockObjectTestCase {
	
	private Mockery mockery = new Mockery();
	private IDebugger debugger;
	private IDebugElementFactory factory;
	private IDebuggerListener listener;
	private IStreamsProxy proxy;
	
	public void setUp() {
		debugger = new DdbgDebugger();		
		factory = mock(IDebugElementFactory.class);
		listener = mock(IDebuggerListener.class);
		proxy = mock(IStreamsProxy.class);
		
		debugger.initialize(listener, factory, proxy, 300000, true);
	}
	
	public void testGetDebuggerCommandLineArguments() {
		assertTrue(debugger.getDebuggerCommandLineArguments().isEmpty());
	}
	
	public void testDebugeeCommandLineArguments() {
		assertInIterable(debugger.getDebugeeCommandLineArguments(
				new String[] { "one", "two" }),
				"one", "two");
	}
	
	public void testEndCommunicationString() {
		assertEquals("->", debugger.getEndCommunicationString());
	}
	
	public void testStart() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("er\n");
			one(proxy).write("nc\n");
			one(proxy).write("r\n");
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.start();
			}
		});
		
		endCommunicationTimes(3);
		
		mockery.assertIsSatisfied();
	}
	
	public void testResume() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("r\n");
		}});
		
		debugger.resume();
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testTerminate() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("q\n");
		}});
		
		debugger.terminate();
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepOverEndsProgram() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_OVER);
			one(proxy).write("ov\n");
			one(listener).terminated();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepOver();
			}
		});		
		
		interpret("Process terminated");
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepOverContinues() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_OVER);
			one(proxy).write("ov\n");
			one(listener).stepEnded();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepOver();
			}
		});		
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepIntoEndsProgram() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_INTO);
			one(proxy).write("in\n");
			one(listener).terminated();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepInto();
			}
		});		
		
		interpret("Process terminated");
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepIntoContinues() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_INTO);
			one(proxy).write("in\n");
			one(listener).stepEnded();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepInto();
			}
		});		
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepReturnEndsProgram() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_RETURN);
			one(proxy).write("out\n");
			one(listener).terminated();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepReturn();
			}
		});		
		
		interpret("Process terminated");
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepReturnContinues() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_RETURN);
			one(proxy).write("out\n");
			one(listener).stepEnded();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepReturn();
			}
		});		
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testAddBreakpoing() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("bp main.d:26\n");
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.addBreakpoint("main.d", 26);
			}
		});
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testRemoveBreakpoing() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("dbp main.d:26\n");
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.removeBreakpoint("main.d", 26);
			}
		});
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	abstract class InThreadRunnable {
		abstract void run() throws Exception;
	}
	
	protected void runInThread(final InThreadRunnable runnable) {
		new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	protected void interpretTimes(String cmd, int times) {
		for(int i = 0; i < times; i++) {
			interpret(cmd);
		}
	}
	
	protected void interpret(String cmd) {
		sleep();
		try {
			debugger.interpret(cmd);
		} catch (DebugException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void endCommunicationTimes(int times) {
		interpretTimes("->", times);
	}
	
	protected void endCommunication() {
		interpret("->");
	}
	
	protected void sleep() {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected <S> void assertInIterable(Iterable<S> elements, S ... searchElements) {
		boolean[] found = new boolean[searchElements.length];
		loop: for(S elem : elements) {
			for(int i = 0; i < searchElements.length; i++) {
				if (elem.equals(searchElements[i])) {
					assertFalse(found[i]);
					found[i] = true;
					continue loop;
				}				
			}
			fail("Element was not in search elements: " + elem);
		}
		
		for(int i = 0; i < found.length; i++) {
			if (!found[i]) {
				fail("Element was not found: " + searchElements[i]);
			}
		}
	}

}
