package dtool;


public class DeeNamingRules {
	
	private static final String DEE_FILE_EXTENSION = ".d";
	private static final String DEE_HEADERFILE_EXTENSION = ".di";
	private static final String DEE_HEADERFILE_EXTENSION2 = ".dh"; // IDE extension
	
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
	
	
	public static boolean isValidPackagePathName(String packageName) {
		if(packageName.equals(""))
			return true;
		
		String[] parts = packageName.split(".");
		for (int i = 0; i < parts.length; i++) {
			if(!isValidPackageNamePart(parts[i]))
				return false;
		}
		return true;
	}
	
	public static boolean isValidPackageNamePart(String partname) {
		if(partname.length() == 0)
			return false;
		
		if(!(Character.isLetter(partname.charAt(0)) || partname.charAt(0) == '_'))
			return false;
		
		for(int i = 1; i < partname.length(); ++i){
			
			if(!Character.isLetterOrDigit(partname.charAt(i)))
				return false;
		}
		return true;
	}
	
}
