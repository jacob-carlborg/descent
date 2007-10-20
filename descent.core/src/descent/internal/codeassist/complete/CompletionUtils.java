package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;

class CompletionUtils {
	
	static int getFqnStart(Identifiers packages, IdentifierExp id, int completePosition) {
		if (packages != null) {
			return packages.get(0).start;
		} else if (id != null) {
			return id.start;
		} else {
			return completePosition;
		}
	}
	
	static int getFqnEnd(Identifiers packages, IdentifierExp id, int completePosition) {
		if (id != null) {
			return id.start + id.length;
		} else if (packages != null) {
			IdentifierExp last = packages.get(packages.size() - 1);
			int ret = last.start + last.length;
			if (completePosition == ret + 1) {
				return completePosition;
			} else {
				return ret;
			}
		} else {
			return completePosition;
		}
	}

}
