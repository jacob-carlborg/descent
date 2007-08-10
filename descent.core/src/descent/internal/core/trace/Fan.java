package descent.internal.core.trace;

import descent.core.trace.IFan;
import descent.core.trace.ITraceNode;

public class Fan implements IFan {

	private final Trace trace;
	private final String signature;
	private final long numberOfCalls;
	private final boolean in;

	public Fan(Trace trace, String signature, long numberOfCalls, boolean in) {
		this.trace = trace;
		this.signature = signature;
		this.numberOfCalls = numberOfCalls;
		this.in = in;
	}

	public long getNumberOfCalls() {
		return numberOfCalls;
	}

	public ITraceNode getTraceNode() {
		return trace.getNode(signature);
	}
	
	public boolean isIn() {
		return in;
	}

}
