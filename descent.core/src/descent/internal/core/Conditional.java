package descent.internal.core;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.Flags;
import descent.core.IConditional;
import descent.core.IJavaModelStatusConstants;
import descent.core.IMember;
import descent.core.ISourceManipulation;
import descent.core.ISourceRange;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

// TODO JDT finish implementations
class Conditional extends Member implements IConditional {
	
	private String displayString;
	
	public Conditional(JavaElement parent, int count) {
		this(parent, count, "");
	}
	
	public Conditional(JavaElement parent, int count, String displayString) {
		super(parent);
		// 0 is not valid: this first occurrence is occurrence 1.
		if (count <= 0)
			throw new IllegalArgumentException();
		this.displayString = displayString;
		this.occurrenceCount = count;
	}

	public boolean isDebugDeclaration() throws JavaModelException {
		long flags = getFlags();
		return !Flags.isIftypeDeclaration(flags)
			&& !Flags.isStaticIfDeclaration(flags)
			&& !Flags.isVersionDeclaration(flags);
	}

	public boolean isIftypeDeclaration() throws JavaModelException {
		return Flags.isIftypeDeclaration(getFlags());
	}

	public boolean isStaticIfDeclaration() throws JavaModelException {
		return Flags.isStaticIfDeclaration(getFlags());
	}

	public boolean isVersionDeclaration() throws JavaModelException {
		return Flags.isVersionDeclaration(getFlags());
	}

	public int getElementType() {
		return CONDITIONAL;
	}
	
	/**
	 * @see JavaElement#getHandleMemento(StringBuffer)
	 */
	protected void getHandleMemento(StringBuffer buff) {
		((JavaElement)getParent()).getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		buff.append(this.occurrenceCount);
	}
	/**
	 * @see JavaElement#getHandleMemento()
	 */
	protected char getHandleMementoDelimiter() {
		return JavaElement.JEM_CONDITIONAL;
	}
	public int hashCode() {
		return Util.combineHashCodes(this.parent.hashCode(), this.occurrenceCount);
	}
	/**
	 */
	public String readableName() {

		return ((JavaElement)getDeclaringType()).readableName();
	}
	/**
	 * @see ISourceManipulation
	 */
	public void rename(String newName, boolean force, IProgressMonitor monitor) throws JavaModelException {
		throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.INVALID_ELEMENT_TYPES, this));
	}
	/**
	 * @see IMember
	 */
	public ISourceRange getNameRange() {
		return null;
	}
	@Override
	public String getElementName() {
		return displayString;
	}
	@Override
	protected void appendElementSignature(StringBuilder sb) {
		
	}
}
