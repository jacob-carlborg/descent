package mmrnmhrm.ui.text;

import org.eclipse.jface.text.IDocument;

public interface DeePartitions {
	
	String DEE_PARTITIONING = "___dee_partioning";
	
	String DEE_CODE = IDocument.DEFAULT_CONTENT_TYPE;
	String DEE_COMMENT = "___dee_comment";  
	String DEE_DOCCOMMENT = "___dee_doc_comment";  
	String DEE_STRING = "___dee_string";
	
	public static final String[] LEGAL_CONTENT_TYPES = {
		IDocument.DEFAULT_CONTENT_TYPE,
		DeePartitions.DEE_COMMENT,
		DeePartitions.DEE_DOCCOMMENT,
		DeePartitions.DEE_STRING
	};
	
	public static final String[] DEE_PARTITION_TYPES = LEGAL_CONTENT_TYPES;

}
