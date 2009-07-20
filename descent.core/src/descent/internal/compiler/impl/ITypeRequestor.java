package descent.internal.compiler.impl;

import descent.internal.compiler.env.AccessRestriction;
import descent.internal.compiler.env.ICompilationUnit;

public interface ITypeRequestor {
	
	/**
	 * Accept the resolved binary form for the requested type.
	 */
	//void accept(IBinaryType binaryType, PackageBinding packageBinding, AccessRestriction accessRestriction);

	/**
	 * Accept the requested type's compilation unit.
	 */
	void accept(ICompilationUnit unit, AccessRestriction accessRestriction);

	/**
	 * Accept the unresolved source forms for the requested type.
	 * Note that the multiple source forms can be answered, in case the target compilation unit
	 * contains multiple types. The first one is then guaranteed to be the one corresponding to the
	 * requested type.
	 */
	//void accept(ISourceType[] sourceType, PackageBinding packageBinding, AccessRestriction accessRestriction);
}
