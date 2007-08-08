package descent.internal.core.trace;

import descent.core.trace.IFan;
import descent.core.trace.ITraceNode;

public class Fan implements IFan {

	private final Trace trace;
	private final String signature;
	private final long numberOfCalls;

	public Fan(Trace trace, String signature, long numberOfCalls) {
		this.trace = trace;
		this.signature = signature;
		this.numberOfCalls = numberOfCalls;
	}

	public long getNumberOfCalls() {
		return numberOfCalls;
	}

	public ITraceNode getTraceNode() {
		return trace.getNode(signature);
	}

}
