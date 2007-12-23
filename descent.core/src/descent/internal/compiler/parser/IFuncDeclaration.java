package descent.internal.compiler.parser;

import java.util.List;

public interface IFuncDeclaration extends IDeclaration {
	
	IDeclaration overnext();
	
	boolean overrides(IFuncDeclaration fd, SemanticContext context);
	
	IFuncDeclaration overloadExactMatch(Type t, SemanticContext context);
	
	IFuncDeclaration overloadResolve(Expressions arguments, SemanticContext context, ASTDmdNode caller);
	
	boolean canInline(boolean hasthis, boolean hdrscan, SemanticContext context);
	
	boolean canInline(boolean hasthis, SemanticContext context);
	
	boolean isNested();
	
	IAggregateDeclaration isMember2();
	
	VarDeclaration vthis();
	
	Expression doInline(InlineScanState iss, Expression ethis,
			List arguments, SemanticContext context);
	
	Expression interpret(InterState istate, Expressions arguments,
			SemanticContext context);
	
	Type tintro();
	
	boolean inferRetType();
	
	void nestedFrameRef(boolean nestedFrameRef);

}
