package descent.internal.core.trace;

import descent.core.trace.IFan;
import descent.core.trace.ITraceNode;

public class TraceNode implements ITraceNode {
	
	private static IFan[] NO_FANS = new IFan[0];
	
	private final String signature;
	private long numberOfCalls;
	private final long ticks;
	private long treeTicks;
	private final IFan[] fanIn;
	private IFan[] fanOut;
	private long functionTime;
	private long functionTimePerCall;
	private long treeTime;

	public TraceNode(String signature, long numberOfCalls, long ticks, long treeTicks, IFan[] fanIn) {
		this.signature = signature;
		this.numberOfCalls = numberOfCalls;
		this.ticks = ticks;
		this.treeTicks = treeTicks;
		this.fanIn = fanIn;
	}

	public IFan[] getFanIn() {
		if (fanIn == null) {
			return NO_FANS;
		}
		return fanIn;
	}
	
	public void setFanOut(IFan[] fanOut) {
		this.fanOut = fanOut;
	}

	public IFan[] getFanOut() {
		if (fanOut == null) {
			return NO_FANS;
		}
		return fanOut;
	}
	
	public void setFunctionTime(long functionTime) {
		this.functionTime = functionTime;
	}

	public long getFunctionTime() {
		return functionTime;
	}
	
	public void setFunctionTimePerCall(long functionTimePerCall) {
		this.functionTimePerCall = functionTimePerCall;
	}

	public long getFunctionTimePerCall() {
		return functionTimePerCall;
	}
	
	public void setNumberOfCalls(long numberOfCalls) {
		this.numberOfCalls = numberOfCalls;
	}

	public long getNumberOfCalls() {
		return numberOfCalls;
	}

	public String getSignature() {
		return signature;
	}

	public long getTicks() {
		return ticks;
	}
	
	public void setTreeTicks(long treeTicks) {
		this.treeTicks = treeTicks;
	}

	public long getTreeTicks() {
		return treeTicks;
	}
	
	public long getTreeTime() {
		return treeTime;
	}
	
	public void setTreeTime(long treeTime) {
		this.treeTime = treeTime;
	}

}
