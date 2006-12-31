package mmrnmhrm.text;

public interface EDeePartitions {
	
	String DEE_DEFAULT = "___dee_default";  

	String DEE_DOC = "___dee_doc_comment"; 

	String DEE_STRING = "___dee_string";
	
	
	public static final String[] legalContentTypes = {
		EDeePartitions.DEE_DEFAULT,
		EDeePartitions.DEE_DOC,
	};
}
