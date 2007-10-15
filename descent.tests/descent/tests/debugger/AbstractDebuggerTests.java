package descent.tests.debugger;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.jmock.Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;

import descent.debug.core.model.IDebugElementFactory;
import descent.debug.core.model.IDebugger;
import descent.debug.core.model.IDebuggerListener;
import descent.internal.debug.core.model.ddbg.DdbgDebugger;
import descent.tests.ddoc.DdocParserTests;

public abstract class AbstractDebuggerTests extends MockObjectTestCase {
	
	protected Mockery mockery = new Mockery();
	protected IDebugger debugger;
	protected IDebugElementFactory factory;
	protected IDebuggerListener listener;
	protected IStreamsProxy proxy;
	protected IRegisterGroup registerGroup;
	
	public void setUp() {
		debugger = new DdbgDebugger();		
		factory = mock(IDebugElementFactory.class);
		listener = mock(IDebuggerListener.class);
		proxy = mock(IStreamsProxy.class);
		registerGroup = mock(IRegisterGroup.class);
		
		debugger.initialize(listener, factory, proxy, 300000, true);
	}
	
	protected abstract IDebugger createDebugger();
	
	protected abstract String getEndCommunicationString();
	
	protected abstract class InThreadRunnable {
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
		sleep();
	}
	
	protected void endCommunicationTimes(int times) {
		interpretTimes(getEndCommunicationString(), times);
	}
	
	protected void endCommunication() {
		interpret(getEndCommunicationString());
	}
	
	protected void sleep() {
		try {
			Thread.sleep(40);
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
