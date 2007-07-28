/**
 * 
 */
package mmrnmhrm.core.model;

public interface EModelStatus {
	int OK = 0;
	int PARSER_INTERNAL_ERROR = 1;
	int PARSER_SYNTAX_ERRORS = 2;
	int PARSER_AST_UNSUPPORTED_NODE = 3;
	
}