package dtool.dom.references;

import java.util.Collection;

import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;

/**
 */
public class TypeStruct extends CommonRefNative {

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		throw new UnsupportedOperationException();
	}

	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toStringAsElement() {
		throw new UnsupportedOperationException();
	}

}
