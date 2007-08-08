package mmrnmhrm.core.model;

public class DeeNameRules {

	private static final String DEE_FILE_EXTENSION = ".d";

	protected static boolean isValidCompilationUnitName(String name) {
		
		if(!(Character.isLetter(name.charAt(0)) || name.charAt(0) == '_'))
			return false;
	
		for(int i = 1; i < name.length(); ++i){
			if(name.charAt(i) == '.') {
				return name.substring(i).equals(DEE_FILE_EXTENSION);
			}
			if(!Character.isLetterOrDigit(name.charAt(i)))
				return false;
		}
		return false;
	}

	public static boolean isValidPackageName(String name) {
		if(!(Character.isLetter(name.charAt(0)) || name.charAt(0) == '_'))
			return false;
	
		for(int i = 1; i < name.length(); ++i){
			//if(name.charAt(i) == '.');
			
			if(!Character.isLetterOrDigit(name.charAt(i)))
				return false;
		}
		return true;
	}

}
