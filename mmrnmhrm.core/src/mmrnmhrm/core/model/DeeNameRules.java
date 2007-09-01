package mmrnmhrm.core.model;

public class DeeNameRules {

	private static final String DEE_FILE_EXTENSION = ".d";
	private static final String DEE_HEADERFILE_EXTENSION = ".di";
	private static final String DEE_HEADERFILE_EXTENSION2 = ".dh";

	public static final String[] VALID_EXTENSIONS = new String[] {
		".d", ".di", ".dh"
	};
	
	public static boolean isValidCompilationUnitName(String name) {
		
		if(!(Character.isLetter(name.charAt(0)) || name.charAt(0) == '_'))
			return false;
	
		for(int i = 1; i < name.length(); ++i){
			if(name.charAt(i) == '.') {
				String fileext = name.substring(i);
				return fileext.equals(DEE_FILE_EXTENSION)
					|| fileext.equals(DEE_HEADERFILE_EXTENSION)
					|| fileext.equals(DEE_HEADERFILE_EXTENSION2);
			}
			if(Character.isLetterOrDigit(name.charAt(i)) || name.charAt(i) == '_')
				continue;
			else
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
