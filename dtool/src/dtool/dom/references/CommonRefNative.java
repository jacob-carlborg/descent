package dtool.dom.references;

import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.NativeDefUnit;

public abstract class CommonRefNative extends Reference {

	@Override
	public boolean canMatch(DefUnit defunit) {
		if(defunit instanceof NativeDefUnit)
			return true;
		return false;
	}
}
