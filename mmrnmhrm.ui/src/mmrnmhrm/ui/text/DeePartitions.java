package mmrnmhrm.ui.text;

import org.eclipse.jface.text.IDocument;

public interface DeePartitions {
	
	String DEE_PARTITIONING = "___dee_partioning";
	
	String DEE_CODE = IDocument.DEFAULT_CONTENT_TYPE;
	String DEE_STRING = "___dee_string";

	String DEE_SINGLE_COMMENT = "___dee_single_comment";  
	String DEE_SINGLE_DOCCOMMENT = "___dee_single_doccomment";  
	String DEE_MULTI_COMMENT = "___dee_multi_comment";  
	String DEE_MULTI_DOCCOMMENT = "___dee_multi_doccomment";  
	String DEE_NESTED_COMMENT = "___dee_nested_comment";  
	String DEE_NESTED_DOCCOMMENT = "___dee_nested_doccomment";  

	
	public static final String[] DEE_PARTITION_TYPES = {
		DEE_CODE,
		DEE_STRING,
		DEE_SINGLE_COMMENT,
		DEE_SINGLE_DOCCOMMENT,
		DEE_MULTI_COMMENT,
		DEE_MULTI_DOCCOMMENT,
		DEE_NESTED_COMMENT,
		DEE_NESTED_DOCCOMMENT,
	};
	
	/* Bruno: Some IDEs use a LEGAL_CONTENT_TYPES without a 
	IDocument.DEFAULT_CONTENT_TYPE , but I have no idea why. */ 
	public static final String[] LEGAL_CONTENT_TYPES = DEE_PARTITION_TYPES;

}
