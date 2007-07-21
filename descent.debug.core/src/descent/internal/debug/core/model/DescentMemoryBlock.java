package descent.internal.debug.core.model;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;

import descent.debug.core.model.IDebugger;

public class DescentMemoryBlock extends DescentDebugElement implements IMemoryBlock {

	private final long fStartAddress;
	private final long fLength;
	private final IDebugger fDebugger;

	public DescentMemoryBlock(IDebugTarget target, IDebugger debugger, long startAddress, long length) {
		super(target);
		this.fDebugger = debugger;
		this.fStartAddress = startAddress;
		this.fLength = length;
	}

	public byte[] getBytes() throws DebugException {
		try {
			return fDebugger.getMemoryBlock(fStartAddress, fLength);
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	public long getLength() {
		return fLength;
	}

	public long getStartAddress() {
		return fStartAddress;
	}

	public void setValue(long offset, byte[] bytes) throws DebugException {
	}

	public boolean supportsValueModification() {
		return false;
	}

}
