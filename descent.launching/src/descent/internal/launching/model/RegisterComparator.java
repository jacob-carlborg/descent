package descent.internal.launching.model;

import java.util.Comparator;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;

/**
 * Compares registers by name. Singleton.
 */
public class RegisterComparator implements Comparator<IRegister> {
	
	private static RegisterComparator instance;
	private RegisterComparator() { }
	public static RegisterComparator getInstance() {
		if (instance == null) {
			instance = new RegisterComparator();
		}
		return instance;
	}

	public int compare(IRegister o1, IRegister o2) {
		try {
			return o1.getName().compareTo(o2.getName());
		} catch (DebugException e) {
			return 0;
		}
	}

}
