package dtool.ast.references;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;

public abstract class CommonRefNative extends Reference {

	@Override
	public boolean canMatch(DefUnit defunit) {
		if(defunit instanceof NativeDefUnit)
			return true;
		return false;
	}
}
