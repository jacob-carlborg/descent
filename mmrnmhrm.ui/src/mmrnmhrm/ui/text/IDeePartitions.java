package mmrnmhrm.ui.text;

public interface IDeePartitions {
	
	String DEE_PARTITIONING = "___dee_partioning";
	
	String DEE_CODE = "___dee_code";  
	String DEE_DOC = "___dee_doc_comment"; 
	String DEE_STRING = "___dee_string";
	
	
	public static final String[] legalContentTypes = {
		IDeePartitions.DEE_CODE,
		IDeePartitions.DEE_DOC,
		IDeePartitions.DEE_STRING
	};
}
