package descent.internal.core.ctfe;

import descent.core.ICompilationUnit;

public class Breakpoint {

	public ICompilationUnit unit;
	public int line;

	public Breakpoint(ICompilationUnit unit, int line) {
		this.unit = unit;
		this.line = line;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + line;
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Breakpoint other = (Breakpoint) obj;
		if (line != other.line)
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return unit.getElementName() + ":" + line;
	}

}