package descent.tests.trace;

import java.io.StringReader;

import junit.framework.TestCase;

import descent.core.trace.IFan;
import descent.core.trace.ITrace;
import descent.core.trace.ITraceNode;
import descent.core.trace.TraceParser;

public class Trace_Test extends TestCase {
	
	String TRACE = 
			"------------------\r\n" + 
			"	    1	_D4main3fooFiZv\r\n" + 
			"	    1	_D4main4foo2FiZv\r\n" + 
			"_D4main3barFiZv	2	2	2\r\n" + 
			"------------------\r\n" + 
			"	    1	__Dmain\r\n" + 
			"_D4main3fooFiZv	1	10	9\r\n" + 
			"	    1	_D4main3barFiZv\r\n" + 
			"------------------\r\n" + 
			"	    1	__Dmain\r\n" + 
			"_D4main4foo2FiZv	1	10	9\r\n" + 
			"	    1	_D4main3barFiZv\r\n" + 
			"------------------\r\n" + 
			"__Dmain	0	38	18\r\n" + 
			"	    1	_D4main3fooFiZv\r\n" + 
			"	    1	_D4main4foo2FiZv\r\n" + 
			"\r\n" + 
			"======== Timer Is 3579545 Ticks/Sec, Times are in Microsecs ========\r\n" + 
			"\r\n" + 
			"  Num          Tree        Func        Per\r\n" + 
			"  Calls        Time        Time        Call\r\n" + 
			"\r\n" + 
			"      1          10           5           4     __Dmain\r\n" + 
			"      1           2           3           4     _D4main3fooFiZv\r\n" + 
			"      1           2           2           2     _D4main4foo2FiZv\r\n" + 
			"      2           0           0           0     _D4main3barFiZv";
	
	public void testNumberOfTraces() throws Exception {
		TraceParser parser = new TraceParser();
		ITrace trace = parser.parse(new StringReader(TRACE));
		assertEquals(4, trace.getNodes().length);
	}
	
	public void testTracesProperties() throws Exception {
		TraceParser parser = new TraceParser();
		ITrace trace = parser.parse(new StringReader(TRACE));
		ITraceNode node;
		IFan[] fans;
		
		node = trace.getNode("__Dmain");
		assertEquals("__Dmain", node.getSignature());
		assertEquals(1, node.getNumberOfCalls());
		assertEquals(10, node.getTreeTime());
		assertEquals(5, node.getFunctionTime());
		assertEquals(4, node.getFunctionTimePerCall());
		
		fans = node.getFanIn();
		assertEquals(0, fans.length);
		
		fans = node.getFanOut();
		assertEquals(2, fans.length);
		assertEquals(1, fans[0].getNumberOfCalls());
		assertEquals("_D4main3fooFiZv", fans[0].getTraceNode().getSignature());
		assertEquals(1, fans[1].getNumberOfCalls());
		assertEquals("_D4main4foo2FiZv", fans[1].getTraceNode().getSignature());
		
		node = trace.getNode("_D4main3fooFiZv");
		assertEquals("_D4main3fooFiZv", node.getSignature());
		assertEquals(1, node.getNumberOfCalls());
		assertEquals(2, node.getTreeTime());
		assertEquals(3, node.getFunctionTime());
		assertEquals(4, node.getFunctionTimePerCall());
		
		fans = node.getFanIn();
		assertEquals(1, fans.length);
		fans = node.getFanOut();
		assertEquals(1, fans.length);
		
		node = trace.getNode("_D4main4foo2FiZv");
		assertEquals("_D4main4foo2FiZv", node.getSignature());
		assertEquals(1, node.getNumberOfCalls());
		assertEquals(2, node.getTreeTime());
		assertEquals(2, node.getFunctionTime());
		assertEquals(2, node.getFunctionTimePerCall());
		
		fans = node.getFanIn();
		assertEquals(1, fans.length);
		fans = node.getFanOut();
		assertEquals(1, fans.length);
		
		node = trace.getNode("_D4main3barFiZv");
		assertEquals("_D4main3barFiZv", node.getSignature());
		assertEquals(2, node.getNumberOfCalls());
		assertEquals(0, node.getTreeTime());
		assertEquals(0, node.getFunctionTime());
		assertEquals(0, node.getFunctionTimePerCall());
		
		fans = node.getFanIn();
		assertEquals(2, fans.length);
		
		assertEquals(1, fans[0].getNumberOfCalls());
		assertEquals("_D4main3fooFiZv", fans[0].getTraceNode().getSignature());
		assertEquals(1, fans[1].getNumberOfCalls());
		assertEquals("_D4main4foo2FiZv", fans[1].getTraceNode().getSignature());
		
		fans = node.getFanOut();
		assertEquals(0, fans.length);
	}

}
