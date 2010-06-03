package descent.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;

public interface IDescentValue extends IValue {
	
	String getDetail() throws DebugException;
	
	boolean isLazy();

}
