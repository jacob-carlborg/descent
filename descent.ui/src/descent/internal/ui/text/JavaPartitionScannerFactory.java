package descent.internal.ui.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

/**
 * Factory for a java partition scanner. JDT uses a FastJavaPartitionScanner
 * and it also has a JavaPartitionScanner.
 * Thi factory currently uses the slower, but easier to implement,
 * partitioner: JavaPartitionScanner.
 */
public class JavaPartitionScannerFactory {
	
	public final static IPartitionTokenScanner newJavaPartitionScanner(IPreferenceStore store) {
		return new JavaPartitionScanner(store);
	}

}
