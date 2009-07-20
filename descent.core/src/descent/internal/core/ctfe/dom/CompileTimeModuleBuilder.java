package descent.internal.core.ctfe.dom;

import descent.core.ISourceRange;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.dom.AST;
import descent.internal.compiler.lookup.ModuleBuilder;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.AlignDeclaration;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.DeleteDeclaration;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.DtorDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Lexer;
import descent.internal.compiler.parser.LinkDeclaration;
import descent.internal.compiler.parser.NewDeclaration;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.StaticIfDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateParameters;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionSymbol;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.core.CompilerConfiguration;

public class CompileTimeModuleBuilder extends ModuleBuilder {
	
	private Lexer fLexer;

	public CompileTimeModuleBuilder(CompilerConfiguration config, ASTNodeEncoder encoder) {
		super(config, encoder);
	}
	
	@Override
	protected FuncDeclaration newFuncDeclaration(char[] filename, int lineNumber, IdentifierExp ident, int storageClass, Type type) {
		return new CompileTimeFuncDeclaration(filename, lineNumber, ident, storageClass, type, true);
	}
	
	@Override
	protected AliasDeclaration newAliasDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Type type) {
		return new CompileTimeAliasDeclaration(filename, lineNumber, ident, type);
	}
	
	@Override
	protected AlignDeclaration newAlignDeclaration(int i, Dsymbols sub) {
		return new CompileTimeAlignDeclaration(i, sub);
	}
	
	@Override
	protected AnonDeclaration newAnonDeclaration(char[] filename, int lineNumber, boolean isUnion, Dsymbols symbols) {
		return new CompileTimeAnonDeclaration(filename, lineNumber, isUnion, symbols);
	}
	
	@Override
	protected ClassDeclaration newClassDeclaration(char[] filename, int lineNumber, IdentifierExp ident, BaseClasses baseClasses) {
		return new CompileTimeClassDeclaration(filename, lineNumber, ident, baseClasses);
	}
	
	@Override
	protected CompileDeclaration newCompileDeclaration(char[] filename, int lineNumber, Expression exp) {
		return new CompileTimeCompileDeclaration(filename, lineNumber, exp);
	}
	
	@Override
	protected ConditionalDeclaration newConditionalDeclaration(Condition condition, Dsymbols thenDecls, Dsymbols elseDecls) {
		return new CompileTimeConditionalDeclaration(condition, thenDecls, elseDecls);
	}
	
	@Override
	protected CtorDeclaration newCtorDeclaration(char[] filename, int lineNumber, Arguments arguments, int varargs) {
		return new CompileTimeCtorDeclaration(filename, lineNumber, arguments, varargs);
	}
	
	@Override
	protected DebugSymbol newDebugSymbol(char[] filename, int lineNumber, IdentifierExp exp, Version version) {
		return new CompileTimeDebugSymbol(filename, lineNumber, exp, version);
	}
	
	@Override
	protected DebugSymbol newDebugSymbol(char[] filename, int lineNumber, long level, Version version) {
		return new CompileTimeDebugSymbol(filename, lineNumber, level, version);
	}
	
	@Override
	protected DeleteDeclaration newDeleteDeclaration(char[] filename, int lineNumber, Arguments arguments) {
		return new CompileTimeDeleteDeclaration(filename, lineNumber, arguments);
	}
	
	@Override
	protected DtorDeclaration newDtorDeclaration(char[] filename, int lineNumber) {
		return new CompileTimeDtorDeclaration(filename, lineNumber);
	}
	
	@Override
	protected EnumDeclaration newEnumDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Type type) {
		return new CompileTimeEnumDeclaration(filename, lineNumber, ident, type);
	}
	
	@Override
	protected EnumMember newEnumMember(char[] filename, int lineNumber, IdentifierExp ident, Expression expression) {
		return new CompileTimeEnumMember(filename, lineNumber, ident, expression);
	}
	
	@Override
	protected InterfaceDeclaration newInterfaceDeclaration(char[] filename, int lineNumber, IdentifierExp ident, BaseClasses baseClasses) {
		return new CompileTimeInterfaceDeclaration(filename, lineNumber, ident, baseClasses);
	}
	
	@Override
	protected LinkDeclaration newLinkDeclaration(LINK link, Dsymbols symbols) {
		return new CompileTimeLinkDeclaration(link, symbols);
	}
	
	@Override
	protected NewDeclaration newNewDeclaration(char[] filename, int lineNumber, Arguments arguments, int varargs) {
		return new CompileTimeNewDeclaration(filename, lineNumber, arguments, varargs);
	}
	
	@Override
	protected StaticIfDeclaration newStaticIfDeclaration(StaticIfCondition condition, Dsymbols thenDecls, Dsymbols elseDecls) {
		return new CompileTimeStaticIfDeclaration(condition, thenDecls, elseDecls);
	}
	
	@Override
	protected StructDeclaration newStructDeclaration(char[] filename, int lineNumber, IdentifierExp id) {
		return new CompileTimeStructDeclaration(filename, lineNumber, id);
	}
	
	@Override
	protected TemplateDeclaration newTemplateDeclaration(char[] filename, int lineNumber, IdentifierExp ident, TemplateParameters templateParameters, Expression constraint, Dsymbols symbols) {
		return new CompileTimeTemplateDeclaration(filename, lineNumber, ident, templateParameters, constraint,
				symbols, true);
	}
	
	@Override
	protected TypedefDeclaration newTypedefDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Type type, Initializer initializer) {
		return new CompileTimeTypedefDeclaration(filename, lineNumber, ident, type, initializer);
	}
	
	@Override
	protected UnionDeclaration newUnionDeclaration(char[] filename, int lineNumber, IdentifierExp id) {
		return new CompileTimeUnionDeclaration(filename, lineNumber, id);
	}
	
	@Override
	protected VarDeclaration newVarDeclaration(char[] filename, int lineNumber, Type type, IdentifierExp ident, Initializer initializer) {
		return new CompileTimeVarDeclaration(filename, lineNumber, type, ident, initializer);
	}
	
	@Override
	protected VersionSymbol newVersionSymbol(char[] filename, int lineNumber, IdentifierExp exp, Version version) {
		return new CompileTimeVersionSymbol(filename, lineNumber, exp, version);
	}
	
	@Override
	protected VersionSymbol newVersionSymbol(char[] filename, int lineNumber, long level, Version version) {
		return new CompileTimeVersionSymbol(filename, lineNumber, level, version);
	}
	
	@Override
	protected void copySourceRange(ASTDmdNode node, ISourceReference sourceReference) throws JavaModelException {
		// This is to assign a correct start position to the node.
		// The sourceReference contains the start and end of any documentation comment
		// preceding or following the declaration, so here we skip them.
		
		ISourceRange range = sourceReference.getSourceRange();
		int start = range.getOffset();
		int end = start + range.getLength();
		
		String source = sourceReference.getSource();
		if (fLexer == null) {
			fLexer = new Lexer(source, true, false, false, false, AST.D1);
		} else {
			fLexer.reset(source.toCharArray(), 0, source.length(), true, false, false, false);
		}
		
		TOK tok = null;
		do {
			tok = fLexer.nextToken();
		} while(tok == TOK.TOKlinecomment || tok == TOK.TOKdoclinecomment ||
				tok == TOK.TOKblockcomment || tok == TOK.TOKdocblockcomment ||
				tok == TOK.TOKpluscomment || tok == TOK.TOKdocpluscomment);
		
		start += fLexer.p;
		
		node.setSourceRange(start, end - start);
	}
	
	@Override
	protected void copySourceRangeRecursive(ASTDmdNode node, ISourceReference sourceReference) throws JavaModelException {
		ISourceRange range = sourceReference.getSourceRange();
		final int start = range.getOffset();
		final int length = range.getLength();
		
		node.accept(new AstVisitorAdapter() {
			@Override
			public void preVisit(ASTNode node) {
				node.setSourceRange(start, length);
			}
		});
	}

}
