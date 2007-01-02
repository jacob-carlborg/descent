package descent.internal.core.builder;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.eclipse.core.resources.IProject;

// TODO replace with real one
public class JavaBuilder {
	
	public static boolean DEBUG = false;

	public static void buildStarting() {
	}

	public static void buildFinished() {
	}

	public static void removeProblemsAndTasksFor(IProject project) {
	}

	public static Object readState(IProject project, DataInputStream in) {
		return null;
	}

	public static void writeState(Object savedState, DataOutputStream out) {
		
	}

}
