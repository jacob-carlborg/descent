package descent.internal.core.trace;

import java.util.HashMap;
import java.util.Map;

import descent.core.trace.ITrace;
import descent.core.trace.ITraceNode;

public class Trace implements ITrace {
	
	private Map<String, TraceNode> nodes;
	private long ticksPerSecond;
	
	public Trace() {
		nodes = new HashMap<String, TraceNode>();
	}
	
	public void addNode(TraceNode currentNode) {
		nodes.put(currentNode.getSignature(), currentNode);
	}
	
	public ITraceNode getNode(String signature) {
		return nodes.get(signature);
	}

	public ITraceNode[] getNodes() {
		return nodes.values().toArray(new ITraceNode[nodes.size()]);
	}
	
	public void setTicksPerSecond(long ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
	}

	public long getTicksPerSecond() {
		return ticksPerSecond;
	}

}
