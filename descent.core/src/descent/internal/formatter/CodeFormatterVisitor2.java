package descent.internal.formatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.text.edits.TextEdit;

import descent.core.dom.*;
import descent.internal.compiler.parser.Lexer;
import descent.internal.compiler.parser.TOK;

/**
 * The class that visits everything in the source tree and formats it by sending
 * it to the Scribe2 class.
 * 
 * @author Robert Fraser
 */
public class CodeFormatterVisitor2 extends ASTVisitor
{
	public DefaultCodeFormatterOptions	preferences;
	
	public Scribe2						scribe;
	public Lexer						lexer;
	public CodeFormatterVisitor2(DefaultCodeFormatterOptions $preferences,
			Map settings, int offset, int length, CompilationUnit unit)
	{
		preferences = $preferences;
		scribe = new Scribe2(this, 0, offset, length, unit);
	}
	
	public TextEdit format(String src, Block block)
	{
		try
		{
			startup(src, block);
			formatStatements(block.statements(), false);
			if (hasComments())
				scribe.printNewLine();
			scribe.printComment();
		} catch (AbortFormatting e)
		{
			return failedToFormat(e);
		}
		return scribe.getRootEdit();
	}
	
	public TextEdit format(String src, CompilationUnit compilationUnit)
	{
		try
		{
			startup(src, compilationUnit);
			compilationUnit.accept(this);
		} catch (AbortFormatting e)
		{
			return failedToFormat(e);
		}
		return scribe.getRootEdit();
	}
	
	public TextEdit format(String src, Expression expression)
	{
		try
		{
			startup(src, expression);
			expression.accept(this);
			scribe.printComment();
		} catch (AbortFormatting e)
		{
			return failedToFormat(e);
		}
		return scribe.getRootEdit();
	}
	
	
	public boolean visit(AggregateDeclaration node)
	{
		formatModifiers(node.modifiers());
		
		switch(node.getKind())
		{
			case CLASS:
				scribe.printNextToken(TOK.TOKclass);
				break;
			case INTERFACE:
				scribe.printNextToken(TOK.TOKinterface);
				break;
			case STRUCT:
				scribe.printNextToken(TOK.TOKstruct);
				break;
			case UNION:
				scribe.printNextToken(TOK.TOKunion);
				break;
			default:
				throw new UnsupportedOperationException();
		}
		
		scribe.space();
		node.getName().accept(this);
		formatTemplateParams(node.templateParameters());
		
		List<BaseClass> baseClasses = node.baseClasses();
		if(!baseClasses.isEmpty())
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKcolon);
			scribe.space();
			formatCSV(baseClasses, true);
		}
		
		formatDeclarationBlock(node.declarations());
		
		return false;
	}
	
	public boolean visit(AliasDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKalias);
		scribe.space();
		node.getType().accept(this);
		scribe.space();
		formatCSV(node.fragments(), true);
		scribe.printNextToken(TOK.TOKsemicolon, this.preferences.insert_space_before_semicolon);
		scribe.printNewLine();
		return false;
	}
	
	public boolean visit(AliasDeclarationFragment node)
	{
		node.getName().accept(this);
		return false;
	}
	
	public boolean visit(AliasTemplateParameter node)
	{
		scribe.printNextToken(TOK.TOKalias);
		scribe.space();
		node.getName().accept(this);
		Type specificType = node.getSpecificType();
		if(null != specificType)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKcolon);
			scribe.space();
			specificType.accept(this);
		}
		Type defaultType = node.getDefaultType();
		if(null != defaultType)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
			defaultType.accept(this);
		}
		return false;
	}
	
	public boolean visit(AlignDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKalign);
		scribe.printNextToken(TOK.TOKlparen);
		scribe.printNextToken(literalOrIdentiferTokenList());
		scribe.printNextToken(TOK.TOKrparen);
		formatDeclarationBlock(node.declarations());
		return false;
	}
	
	public boolean visit(Argument node)
	{
		// Print the passage mode
		switch (node.getPassageMode())
		{
			case IN:
				scribe.printNextToken(TOK.TOKin);
				scribe.space();
				break;
			case OUT:
				scribe.printNextToken(TOK.TOKout);
				scribe.space();
				break;
			case INOUT:
				scribe.printNextToken(TOK.TOKinout);
				scribe.space();
				break;
			case LAZY:
				scribe.printNextToken(TOK.TOKlazy);
				scribe.space();
				break;
			case DEFAULT:
			default:
				break;
		}
		
		Type type = node.getType();
		if(null != type && !((type instanceof PrimitiveType) && 
				(((PrimitiveType) type).getPrimitiveTypeCode() ==
					PrimitiveType.Code.VOID) && 
					!isNextToken(TOK.TOKvoid)))
		{
			type.accept(this);
			scribe.space();
		}
		
		SimpleName name = node.getName();
		if(null != name)
			name.accept(this);
		
		Expression defaultValue = node.getDefaultValue();
		if (null != defaultValue)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
			defaultValue.accept(this);
		}
		
		return false;
	}
	
	public boolean visit(ArrayAccess node)
	{
		node.getArray().accept(this);
		scribe.printNextToken(TOK.TOKlbracket);
		formatCSV(node.indexes(), false);
		scribe.printNextToken(TOK.TOKrbracket);
		return false;
	}
	
	public boolean visit(ArrayInitializer node)
	{
		scribe.printNextToken(TOK.TOKlbracket);
		formatCSV(node.fragments(), true);
		scribe.printNextToken(TOK.TOKrbracket);
		return false;
	}
	
	public boolean visit(ArrayInitializerFragment node)
	{
		Expression expression = node.getExpression();
		if(null != expression)
		{
			expression.accept(this);
			scribe.space();
			scribe.printNextToken(TOK.TOKcolon);
			scribe.space();
		}
		node.getInitializer().accept(this);
		return false;
	}
	
	public boolean visit(ArrayLiteral node)
	{
		scribe.printNextToken(TOK.TOKlbracket);
		formatCSV(node.arguments(), false);
		scribe.printNextToken(TOK.TOKrbracket);
		return false;
	}
	
	public boolean visit(AsmBlock node)
	{
		scribe.printNextToken(TOK.TOKasm);
		scribe.printNewLine();
		scribe.printNextToken(TOK.TOKlcurly);
		scribe.indent();
		formatStatements(node.statements(), true);
		scribe.unIndent();
		scribe.printNextToken(TOK.TOKrcurly);
		return false;
	}
	
	public boolean visit(AsmStatement node)
	{
		// TODO asm
		throw new UnsupportedOperationException();
	}
	
	public boolean visit(AsmToken node)
	{
		// TODO asm
		throw new UnsupportedOperationException();
	}
	
	public boolean visit(AssertExpression node)
	{
		scribe.printNextToken(TOK.TOKassert);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		Expression message = node.getMessage();
		if(null != message)
		{
			scribe.printNextToken(TOK.TOKcomma);
			scribe.space();
			message.accept(this);
		}
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(Assignment node)
	{
		node.getLeftHandSide().accept(this);
		scribe.space();
		scribe.printNextToken(assignmentOperatorTokenList());
		scribe.space();
		node.getRightHandSide().accept(this);
		return false;
	}
	
	public boolean visit(AssociativeArrayType node)
	{
		node.getComponentType().accept(this);
		scribe.printNextToken(TOK.TOKlbracket);
		node.getKeyType().accept(this);
		scribe.printNextToken(TOK.TOKrbracket);
		return false;
	}
	
	public boolean visit(BaseClass node)
	{
		Modifier modifier = node.getModifier();
		if(null != modifier)
		{
			modifier.accept(this);
			scribe.space();
		}
		node.getType().accept(this);
		return false;
	}
	
	public boolean visit(Block node)
	{
		// A varriable declaration list, such as: "int i, j;" will be treated
		// as a block. All non-braced blocks (for example, case statement
		// bodies) will have formatSubStatement() called directly on them.
		// Thus, if a non-braced statement is encountered, it is a variable
		// declaration with multiple parts.
		if(!isNextToken(TOK.TOKlcurly))
		{
			try
			{
				((VariableDeclaration) 
					((DeclarationStatement) 
						node.statements().get(0)).getDeclaration())
					.getType().accept(this);
				scribe.space();
				
				List<VariableDeclarationFragment> fragments = 
					new ArrayList<VariableDeclarationFragment>();
				for(Statement stmt : node.statements())
				{
					fragments.addAll((
						(VariableDeclaration) 
							((DeclarationStatement) stmt).getDeclaration())
						.fragments());
				}
				formatCSV(fragments, true);
				scribe.printNextToken(TOK.TOKsemicolon);
			}
			catch(ClassCastException e)
			{
				// If a ClassCastException is ever thrown here, my assumption
				// above was wrong...
				formatSubStatement(node, true, 0, false, 0);
			}
		}
		else
			formatSubStatement(node, true, 0, false, 0);
		return false;
	}
	
	public boolean visit(BooleanLiteral node)
	{
		scribe.printNextToken(booleanLiteralTokenList());
		return false;
	}
	
	public boolean visit(BreakStatement node)
	{
		scribe.printNextToken(TOK.TOKbreak);
		SimpleName label = node.getLabel();
		if(null != label)
		{
			scribe.space();
			label.accept(this);
		}
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(CallExpression node)
	{
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKlparen);
		formatCSV(node.arguments(), true);
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(CastExpression node)
	{
		scribe.printNextToken(TOK.TOKcast);
		scribe.printNextToken(TOK.TOKlparen);
		node.getType().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		scribe.space();
		node.getExpression().accept(this);
		return false;
	}
	
	public boolean visit(CatchClause node)
	{
		scribe.printNextToken(TOK.TOKcatch);
		if(isNextToken(TOK.TOKlparen))
		{
			scribe.printNextToken(TOK.TOKlparen);
			node.getType().accept(this);
			scribe.space();
			node.getName().accept(this);
			scribe.printNextToken(TOK.TOKrparen);
		}
		formatSubStatement(node.getBody(), true, 0, false, 1);
		return false;
	}
	
	public boolean visit(CharacterLiteral node)
	{
		scribe.printNextToken(characterLiteralTokenList());
		return false;
	}
	
	public boolean visit(CompilationUnit node)
	{
		// fake new line to handle empty lines before package declaration or import declarations
		this.scribe.lastNumberOfNewLines = 1;
		/* 
		 * Module declaration
		 */
		final ModuleDeclaration moduleDeclaration = node.getModuleDeclaration();
		final boolean hasModule = moduleDeclaration != null;
		if(hasModule) {
			if (hasComments()) {
				this.scribe.printComment();
			}
			int blankLinesBeforePackage = this.preferences.blank_lines_before_package;
			if (blankLinesBeforePackage > 0) {
				this.scribe.printEmptyLines(blankLinesBeforePackage);
			}
			
			moduleDeclaration.accept(this);
			
			int blankLinesAfterPackage = this.preferences.blank_lines_after_package;
			if (blankLinesAfterPackage > 0) {
				this.scribe.printEmptyLines(blankLinesAfterPackage);
			} else {
				this.scribe.printNewLine();
			}
		} else {
			scribe.printComment();
		}
		formatDeclarations(node.declarations());
		scribe.printEndOfCompilationUnit();
		return false;
	}
	
	public boolean visit(ConditionalExpression node)
	{
		node.getExpression().accept(this);
		scribe.space();
		scribe.printNextToken(TOK.TOKquestion);
		scribe.space();
		node.getThenExpression().accept(this);
		scribe.space();
		scribe.printNextToken(TOK.TOKcolon);
		scribe.space();
		node.getElseExpression().accept(this);
		return false;
	}
	
	public boolean visit(ConstructorDeclaration node)
	{
		switch(node.getKind())
		{
			case CONSTRUCTOR:
				scribe.printNextToken(TOK.TOKthis);
				break;
			case DESTRUCTOR:
				scribe.printNextToken(tildeOrCatTokenList());
				scribe.printNextToken(TOK.TOKthis);
				break;
			case STATIC_CONSTRUCTOR:
				scribe.printNextToken(TOK.TOKstatic);
				scribe.space();
				scribe.printNextToken(TOK.TOKthis);
				break;
			case STATIC_DESTRUCTOR:
				scribe.printNextToken(tildeOrCatTokenList());
				scribe.printNextToken(TOK.TOKstatic);
				scribe.space();
				scribe.printNextToken(TOK.TOKthis);
				break;
			case NEW:
				scribe.printNextToken(TOK.TOKnew);
				break;
			case DELETE:
				scribe.printNextToken(TOK.TOKdelete);
				break;
			default:
				throw new AbortFormatting("Unknown constructor declaration");
		}
		formatFunction(node);
		return false;
	}
	
	public boolean visit(ContinueStatement node)
	{
		scribe.printNextToken(TOK.TOKcontinue);
		scribe.space();
		node.getLabel().accept(this);
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(DebugAssignment node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKdebug);
		scribe.space();
		scribe.printNextToken(TOK.TOKassign);
		scribe.space();
		node.getVersion().accept(this);
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(DebugDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKdebug);
		Version version = node.getVersion();
		if(null != version)
		{
			scribe.printNextToken(TOK.TOKlparen);
			version.accept(this);
			scribe.printNextToken(TOK.TOKrparen);
		}
		formatConditionalDeclaration(node);
		return false;
	}
	
	public boolean visit(DebugStatement node)
	{
		scribe.printNextToken(TOK.TOKdebug);
		Version version = node.getVersion();
		if(null != version)
		{
			scribe.printNextToken(TOK.TOKlparen);
			version.accept(this);
			scribe.printNextToken(TOK.TOKrparen);
		}
		formatConditionalStatement(node);
		return false;
	}
	
	public boolean visit(DeclarationStatement node)
	{
		node.getDeclaration().accept(this);
		return false;
	}
	
	public boolean visit(DefaultStatement node)
	{
		scribe.printNextToken(TOK.TOKdefault);
		scribe.printNextToken(TOK.TOKcolon);
		scribe.printNewLine();
		node.getBody().accept(this);
		return false;
	}
	
	public boolean visit(DelegateType node)
	{
		node.getReturnType().accept(this);
		scribe.space();
		if(node.isFunctionPointer())
			scribe.printNextToken(TOK.TOKfunction);
		else
			scribe.printNextToken(TOK.TOKdelegate);
		scribe.printNextToken(TOK.TOKlparen);
		formatCSV(node.arguments(), true);
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(DeleteExpression node)
	{
		scribe.printNextToken(TOK.TOKdelete);
		scribe.space();
		node.getExpression().accept(this);
		return false;
	}
	
	public boolean visit(DollarLiteral node)
	{
		scribe.printNextToken(TOK.TOKdollar);
		return false;
	}
	
	public boolean visit(DoStatement node)
	{
		scribe.printNextToken(TOK.TOKdo);
		formatSubStatement(node.getBody(), true, 0, false, 0);
		scribe.printNewLine();
		scribe.printNextToken(TOK.TOKwhile);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(DotIdentifierExpression node)
	{
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKdot);
		node.getName().accept(this);
		return false;
	}
	
	public boolean visit(DotTemplateTypeExpression node)
	{
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKdot);
		node.getTemplateType().accept(this);
		return false;
	}
	
	public boolean visit(DynamicArrayType node)
	{
		node.getComponentType().accept(this);
		scribe.printNextToken(TOK.TOKlbracket);
		scribe.printNextToken(TOK.TOKrbracket);
		return false;
	}
	
	public boolean visit(EmptyStatement node)
	{
		if(isNextToken(TOK.TOKsemicolon))
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(EnumDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKenum);
		
		Name name = node.getName();
		if(null != name)
		{
			scribe.space();
			node.getName().accept(this);
		}
		
		Type baseType = node.getBaseType();
		if(null != baseType)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKcolon);
			scribe.space();
			baseType.accept(this);
		}
		
		if(isNextToken(TOK.TOKsemicolon))
			scribe.printNextToken(TOK.TOKsemicolon);
		else
		{
			scribe.printNewLine();
			scribe.printNextToken(TOK.TOKlcurly);
			scribe.printNewLine();
			scribe.indent();
			for(EnumMember member : node.enumMembers())
			{
				member.accept(this);
				if(isNextToken(TOK.TOKcomma))
					scribe.printNextToken(TOK.TOKcomma);
				scribe.printNewLine();
			}
			scribe.unIndent();
			scribe.printNextToken(TOK.TOKrcurly);
		}
		return false;
	}
	
	public boolean visit(EnumMember node)
	{
		node.getName().accept(this);
		Expression value = node.getValue();
		if(null != value)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
			value.accept(this);
		}
		return false;
	}
	
	public boolean visit(ExpressionInitializer node)
	{
		node.getExpression().accept(this);
		return false;
	}
	
	public boolean visit(ExpressionStatement node)
	{
		node.getExpression().accept(this);
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(ExternDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKextern);
		if(isNextToken(TOK.TOKlparen))
		{
			scribe.printNextToken(TOK.TOKlparen);
			if(node.getLinkage() != ExternDeclaration.Linkage.DEFAULT)
				scribe.printNextToken(TOK.TOKidentifier);
			scribe.printNextToken(TOK.TOKrparen);
		}
		formatDeclarationBlock(node.declarations());
		return false;
	}
	
	public boolean visit(FileImportExpression node)
	{
		scribe.printNextToken(TOK.TOKimport);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(ForeachStatement node)
	{
		if(node.isReverse())
			scribe.printNextToken(TOK.TOKforeach_reverse);
		else
			scribe.printNextToken(TOK.TOKforeach);
		scribe.printNextToken(TOK.TOKlparen);
		formatCSV(node.arguments(), true);
		scribe.space();
		scribe.printNextToken(TOK.TOKsemicolon);
		scribe.space();
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		formatSubStatement(node.getBody(), true, 0, false, 0);
		return false;
	}
	
	public boolean visit(ForStatement node)
	{
		Statement initializer = node.getInitializer();
		Expression condition = node.getCondition();
		Expression increment = node.getIncrement();
		
		scribe.printNextToken(TOK.TOKfor);
		scribe.printNextToken(TOK.TOKlparen);
		if(null != initializer)
		{
			initializer.accept(this);
			scribe.space();
		}
		else
			scribe.printNextToken(TOK.TOKsemicolon);
		if(null != condition)
		{
			condition.accept(this);
			scribe.printNextToken(TOK.TOKsemicolon);
			scribe.space();
		}
		else
			scribe.printNextToken(TOK.TOKsemicolon);
		if(null != increment)
			increment.accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		formatSubStatement(node.getBody(), true, 0, false, 0);
		return false;
	}
	
	public boolean visit(FunctionDeclaration node)
	{
		formatModifiers(node.modifiers());
		node.getReturnType().accept(this);
		scribe.space();
		node.getName().accept(this);
		formatTemplateParams(node.templateParameters());
		formatFunction(node);
		return false;
	}
	
	public boolean visit(FunctionLiteralDeclarationExpression node)
	{
		switch(node.getSyntax())
		{
			case FUNCTION:
				scribe.printNextToken(TOK.TOKfunction);
				scribe.space();
				break;
			case DELEGATE:
				scribe.printNextToken(TOK.TOKdelegate);
				scribe.space();
				break;
			case EMPTY:
			default:
				break;
		}
		formatFunction(node);
		return false;
	}
	
	public boolean visit(GotoCaseStatement node)
	{
		scribe.printNextToken(TOK.TOKgoto);
		scribe.space();
		scribe.printNextToken(TOK.TOKcase);
		scribe.space();
		Expression label = node.getLabel();
		if(null != label)
		{
			scribe.space();
			label.accept(this);
		}
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(GotoDefaultStatement node)
	{
		scribe.printNextToken(TOK.TOKgoto);
		scribe.space();
		scribe.printNextToken(TOK.TOKdefault);
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(GotoStatement node)
	{
		scribe.printNextToken(TOK.TOKgoto);
		scribe.space();
		node.getLabel().accept(this);
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(IfStatement node)
	{
		scribe.printNextToken(TOK.TOKif);
		scribe.printNextToken(TOK.TOKlparen);
		Argument arg = node.getArgument();
		if(null != arg)
		{
			arg.accept(this);
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
		}
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		formatSubStatement(node.getThenBody(), true, 0, false, 0);
		if(isNextToken(TOK.TOKelse))
		{
			scribe.printNewLine();
			scribe.printNextToken(TOK.TOKelse);
			Statement elseBody = node.getElseBody();
			if(elseBody instanceof IfStatement) // handle "else if"
			{
				scribe.space();
				formatSubStatement(elseBody, false, 0, false, 0, false);
			}
			else
			{
				formatSubStatement(elseBody, true, 0, false, 0);
			}
		}
		return false;
	}
	
	public boolean visit(Import node)
	{
		SimpleName alias = node.getAlias();
		if (null != alias)
		{
			alias.accept(this);
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
		}
		node.getName().accept(this);
		List<SelectiveImport> imports = node.selectiveImports();
		if(null != imports && !imports.isEmpty())
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKcolon);
			scribe.space();
			formatCSV(imports, true);
		}
		return false;
	}
	
	public boolean visit(ImportDeclaration node)
	{
		formatModifiers(node.modifiers()); // Will slurp up a "static" modifer
		
		// So, if it's static and has no modifiers, print the "static" token
		// (otherwise it was already printed in formatModifiers)
		if (node.isStatic() && node.modifiers().size() == 0) {
			this.scribe.printNextToken(TOK.TOKstatic);
			this.scribe.space();
		}
		// Print the "import" keyword
		scribe.printNextToken(TOK.TOKimport);
		scribe.space();
		formatCSV(node.imports(), true);
		scribe.printNextToken(TOK.TOKsemicolon, this.preferences.insert_space_before_semicolon);
		scribe.printTrailingComment();
		scribe.printNewLine();
		return false;
	}
	
	public boolean visit(InfixExpression node)
	{
		node.getLeftOperand().accept(this);
		scribe.space();
		scribe.printNextToken(infixOperatorTokenList());
		scribe.space();
		node.getRightOperand().accept(this);
		return false;
	}
	
	public boolean visit(InvariantDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKinvariant);
		scribe.space();
		node.getBody().accept(this);
		return false;
	}
	
	public boolean visit(IsTypeExpression node)
	{
		scribe.printNextToken(TOK.TOKis);
		scribe.printNextToken(TOK.TOKlparen);
		node.getType().accept(this);
		SimpleName name = node.getName();
		if(null != name)
		{
			scribe.space();
			name.accept(this);
		}
		Type specialization = node.getSpecialization();
		if(null != specialization)
		{
			scribe.space();
			scribe.printNextToken(node.isSameComparison() ? 
					TOK.TOKequal : TOK.TOKcolon);
			scribe.space();
			specialization.accept(this);
		}
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(IsTypeSpecializationExpression node)
	{
		scribe.printNextToken(TOK.TOKis);
		scribe.printNextToken(TOK.TOKlparen);
		node.getType().accept(this);
		SimpleName name = node.getName();
		if(null != name)
		{
			scribe.space();
			name.accept(this);
		}
		scribe.space();
		scribe.printNextToken(node.isSameComparison() ? 
				TOK.TOKequal : TOK.TOKcolon);
		scribe.space();
		scribe.printNextToken(typeSpecializationTokenList());
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(LabeledStatement node)
	{
		node.getLabel().accept(this);
		scribe.printNextToken(TOK.TOKcolon);
		scribe.printNewLine();
		node.getBody().accept(this);
		return false;
	}

	public boolean visit(MixinDeclaration node)
	{
		formatModifiers(node.modifiers()); // just in case; there shouldn't be any
		scribe.printNextToken(TOK.TOKmixin);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		if(isNextToken(TOK.TOKsemicolon)) // The D spec requires it, Descent doesn't
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}

	public boolean visit(MixinExpression node)
	{
		scribe.printNextToken(TOK.TOKmixin);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(Modifier node)
	{
		scribe.printNextToken(modifierTokenList());
		return false;
	}
	
	public boolean visit(ModifierDeclaration node)
	{
		formatModifiers(node.modifiers());
		node.getModifier().accept(this);
		formatDeclarationBlock(node.declarations());
		return false;
	}
	
	public boolean visit(ModuleDeclaration node)
	{
		scribe.printNextToken(TOK.TOKmodule);
		scribe.space();
		node.getName().accept(this);
		scribe.printNextToken(TOK.TOKsemicolon, this.preferences.insert_space_before_semicolon);
		scribe.printTrailingComment();
		return false;
	}
	
	public boolean visit(NewAnonymousClassExpression node)
	{
		Expression exp = node.getExpression();
		if(null != exp)
		{
			exp.accept(this);
			scribe.printNextToken(TOK.TOKdot);
		}
		scribe.printNextToken(TOK.TOKnew);
		if(isNextToken(TOK.TOKlparen))
		{
			scribe.printNextToken(TOK.TOKlparen);
			formatCSV(node.newArguments(), true);
			scribe.printNextToken(TOK.TOKrparen);
		}
		scribe.space();
		scribe.printNextToken(TOK.TOKclass);
		if(isNextToken(TOK.TOKlparen))
		{
			scribe.printNextToken(TOK.TOKlparen);
			formatCSV(node.constructorArguments(), true);
			scribe.printNextToken(TOK.TOKrparen);
		}
		List<BaseClass> baseClasses = node.baseClasses();
		if(null != baseClasses && !baseClasses.isEmpty());
		{
			scribe.space();
			formatCSV(baseClasses, true);
		}
		formatDeclarationBlock(node.declarations());
		return false;
	}
	
	public boolean visit(NewExpression node)
	{
		Expression exp = node.getExpression();
		if(null != exp)
		{
			exp.accept(this);
			scribe.printNextToken(TOK.TOKdot);
		}
		scribe.printNextToken(TOK.TOKnew);
		if(isNextToken(TOK.TOKlparen))
		{
			scribe.printNextToken(TOK.TOKlparen);
			formatCSV(node.newArguments(), true);
			scribe.printNextToken(TOK.TOKrparen);
		}
		scribe.space();
		Type type = node.getType();
		if(type instanceof DynamicArrayType)
		{
			DynamicArrayType arr = (DynamicArrayType) type;
			arr.getComponentType().accept(this);
			scribe.printNextToken(TOK.TOKlbracket);
			formatCSV(node.constructorArguments(), true);
			scribe.printNextToken(TOK.TOKrbracket);
			return false;
		}
		type.accept(this);
		if(isNextToken(TOK.TOKlparen))
		{
			scribe.printNextToken(TOK.TOKlparen);
			formatCSV(node.constructorArguments(), true);
			scribe.printNextToken(TOK.TOKrparen);
		}
		return false;
	}
	
	public boolean visit(NullLiteral node)
	{
		scribe.printNextToken(TOK.TOKnull);
		return false;
	}
	
	public boolean visit(NumberLiteral node)
	{
		scribe.printNextToken(numericLiteralTokenList());
		return false;
	}
	
	public boolean visit(ParenthesizedExpression node)
	{
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(PointerType node)
	{
		node.getComponentType().accept(this);
		scribe.printNextToken(TOK.TOKmul);
		return false;
	}
	
	public boolean visit(PostfixExpression node)
	{
		node.getOperand().accept(this);
		scribe.printNextToken(postfixOperatorTokenList());
		return false;
	}
	
	public boolean visit(Pragma node)
	{
		scribe.printNextToken(TOK.TOKPRAGMA);
		scribe.printNewLine();
		return false;
	}
	
	public boolean visit(PragmaDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKpragma);
		scribe.printNextToken(TOK.TOKlparen);
		node.getName().accept(this);
		List<Expression> args = node.arguments();
		if(!args.isEmpty())
		{
			scribe.printNextToken(TOK.TOKcomma);
			scribe.space();
			formatCSV(args, true);
		}
		scribe.printNextToken(TOK.TOKrparen);
		formatDeclarationBlock(node.declarations());
		return false;
	}
	
	public boolean visit(PragmaStatement node)
	{
		scribe.printNextToken(TOK.TOKpragma);
		scribe.printNextToken(TOK.TOKlparen);
		node.getName().accept(this);
		List<Expression> args = node.arguments();
		if(!args.isEmpty())
		{
			scribe.printNextToken(TOK.TOKcomma);
			scribe.space();
			formatCSV(args, true);
		}
		scribe.printNextToken(TOK.TOKrparen);
		Statement body = node.getBody();
		if(null != body)
			formatSubStatement(body, true, 0, false, 0);
		if(isNextToken(TOK.TOKsemicolon))
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(PrefixExpression node)
	{
		scribe.printNextToken(prefixOperatorTokenList());
		node.getOperand().accept(this);
		return false;
	}
	
	public boolean visit(PrimitiveType node)
	{
		if(node.getPrimitiveTypeCode() == PrimitiveType.Code.VOID)
		{
			// If an argument has no type, getType() returns void. So, void
			// can mean that tehre is an explicit void token or not
			if(isNextToken(TOK.TOKvoid))
				scribe.printNextToken(TOK.TOKvoid);
		}
		else
		{
			scribe.printNextToken(primitiveTypesTokenList());
		}
		return false;
	}
	
	public boolean visit(QualifiedName node)
	{
		node.getQualifier().accept(this);
		scribe.printNextToken(TOK.TOKdot);
		node.getName().accept(this);
		return false;
	}
	
	public boolean visit(QualifiedType node)
	{
		Type qualifier = node.getQualifier();
		if(null != qualifier)
			qualifier.accept(this);
		scribe.printNextToken(TOK.TOKdot);
		node.getType().accept(this);
		return false;
	}
	
	public boolean visit(ReturnStatement node)
	{
		scribe.printNextToken(TOK.TOKreturn);
		scribe.space();
		Expression exp = node.getExpression();
		if(null != exp)
		{
			scribe.space();
			exp.accept(this);
		}
		scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(ScopeStatement node)
	{
		scribe.printNextToken(TOK.TOKscope);
		scribe.printNextToken(TOK.TOKlparen);
		scribe.printNextToken(TOK.TOKidentifier);
		scribe.printNextToken(TOK.TOKrparen);
		formatSubStatement(node.getBody(), true, 0, false, 0);
		return false;
	}
	
	public boolean visit(SelectiveImport node)
	{
		SimpleName alias = node.getAlias();
		if (null != alias)
		{
			alias.accept(this);
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
		}
		
		node.getName().accept(this);
		
		return false;
	}
	
	public boolean visit(SimpleName node)
	{
		// If the name is in parentheses, but nothing else is there, the
		// parser might make it a SimpleName, not a ParenthesizedExpression
		if(isNextToken(TOK.TOKlparen))
		{
			scribe.printNextToken(TOK.TOKlparen);
			scribe.printNextToken(TOK.TOKidentifier);
			scribe.printNextToken(TOK.TOKrparen);
		}
		else
			scribe.printNextToken(TOK.TOKidentifier);
		return false;
	}
	
	public boolean visit(SimpleType node)
	{
		node.getName().accept(this);
		return false;
	}
	
	public boolean visit(SliceExpression node)
	{
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKlbracket);
		node.getFromExpression().accept(this);
		scribe.space();
		scribe.printNextToken(TOK.TOKslice);
		scribe.space();
		node.getToExpression().accept(this);
		scribe.printNextToken(TOK.TOKrbracket);
		return false;
	}
	
	public boolean visit(SliceType node)
	{
		node.getComponentType().accept(this);
		scribe.printNextToken(TOK.TOKlbracket);
		node.getFromExpression().accept(this);
		scribe.space();
		scribe.printNextToken(TOK.TOKslice);
		scribe.space();
		node.getToExpression().accept(this);
		scribe.printNextToken(TOK.TOKrbracket);
		return false;
	}
	
	public boolean visit(StaticArrayType node)
	{
		node.getComponentType().accept(this);
		scribe.printNextToken(TOK.TOKlbracket);
		node.getSize().accept(this);
		scribe.printNextToken(TOK.TOKrbracket);
		return false;
	}
	
	public boolean visit(StaticAssert node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKstatic);
		scribe.space();
		scribe.printNextToken(TOK.TOKassert);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		Expression message = node.getMessage();
		if(null != message)
		{
			scribe.printNextToken(TOK.TOKcomma);
			scribe.space();
			message.accept(this);
		}
		scribe.printNextToken(TOK.TOKrparen);
		if(isNextToken(TOK.TOKsemicolon))
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(StaticAssertStatement node)
	{
		node.getStaticAssert().accept(this);
		return false;
	}
	
	public boolean visit(StaticIfDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKstatic);
		scribe.space();
		scribe.printNextToken(TOK.TOKif);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		formatConditionalDeclaration(node);
		return false;
	}
	
	public boolean visit(StaticIfStatement node)
	{
		scribe.printNextToken(TOK.TOKstatic);
		scribe.space();
		scribe.printNextToken(TOK.TOKif);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		formatConditionalStatement(node);
		return false;
	}
	
	public boolean visit(StringLiteral node)
	{
		scribe.printNextToken(TOK.TOKstring);
		return false;
	}
	
	public boolean visit(StringsExpression node)
	{
		List<StringLiteral> literals = node.stringLiterals();
		int len = literals.size();
		if(len == 0)
			return false;
		
		for (int i = 0; i < len - 1; i++)
		{
			literals.get(i).accept(this);
			scribe.space();
		}
		literals.get(len - 1).accept(this);
		return false;
	}
	
	public boolean visit(StructInitializer node)
	{
		scribe.printNextToken(TOK.TOKlcurly);
		formatCSV(node.fragments(), true);
		scribe.printNextToken(TOK.TOKrcurly);
		return false;
	}
	
	public boolean visit(StructInitializerFragment node)
	{
		SimpleName name = node.getName();
		if(null != name)
		{
			name.accept(this);
			scribe.space();
			scribe.printNextToken(TOK.TOKcolon);
			scribe.space();
		}
		node.getInitializer().accept(this);
		return false;
	}
	
	public boolean visit(SuperLiteral node)
	{
		scribe.printNextToken(TOK.TOKsuper);
		return false;
	}
	
	public boolean visit(SwitchCase node)
	{
		scribe.printNextToken(TOK.TOKcase);
		scribe.space();
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKcolon);
		node.getBody().accept(this);
		return false;
	}
	
	public boolean visit(SwitchStatement node)
	{
		scribe.printNextToken(TOK.TOKswitch);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		formatSubStatement(node.getBody(), true, 0, false, 0, false);
		return false;
	}
	
	public boolean visit(SynchronizedStatement node)
	{
		scribe.printNextToken(TOK.TOKsynchronized);
		Expression expression = node.getExpression();
		if(null != expression)
		{
			scribe.printNextToken(TOK.TOKlparen);
			expression.accept(this);
			scribe.printNextToken(TOK.TOKrparen);
		}
		formatSubStatement(node.getBody(), true, 0, false, 0);
		return false;
	}
	
	public boolean visit(TemplateDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKtemplate);
		scribe.space();
		node.getName().accept(this);
		scribe.space();
		scribe.printNextToken(TOK.TOKlparen);
		formatCSV(node.templateParameters(), true);
		scribe.printNextToken(TOK.TOKrparen);
		formatDeclarationBlock(node.declarations());
		return false;
	}
	
	public boolean visit(TemplateMixinDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKmixin);
		scribe.space();
		node.getType().accept(this);
		SimpleName name = node.getName();
		if(null != name)
		{
			scribe.space();
			name.accept(this);
		}
		if(isNextToken(TOK.TOKsemicolon))
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(TemplateType node)
	{
		node.getName().accept(this);
		scribe.printNextToken(negOrNotTokenList());
		scribe.printNextToken(TOK.TOKlparen);
		formatCSV(node.arguments(), true);
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(ThisLiteral node)
	{
		scribe.printNextToken(TOK.TOKthis);
		return false;
	}
	
	public boolean visit(ThrowStatement node)
	{
		scribe.printNextToken(TOK.TOKthrow);
		scribe.space();
		node.getExpression().accept(this);
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(TryStatement node)
	{
		scribe.printNextToken(TOK.TOKtry);
		formatSubStatement(node.getBody(), true, 0, true, 0);
		List<CatchClause> catchClauses = node.catchClauses();
		for(CatchClause c : catchClauses)
			c.accept(this);
		Statement $finally = node.getFinally();
		if(null != $finally)
		{
			scribe.printNextToken(TOK.TOKfinally);
			formatSubStatement($finally, true, 0, false, 0);
		}
		return false;
	}
	
	public boolean visit(TupleTemplateParameter node)
	{
		node.getName().accept(this);
		scribe.printNextToken(TOK.TOKdotdotdot);
		return false;
	}
	
	public boolean visit(TypedefDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKtypedef);
		scribe.space();
		node.getType().accept(this);
		scribe.space();
		formatCSV(node.fragments(), true);
		if(isNextToken(TOK.TOKsemicolon))
			scribe.printNextToken(TOK.TOKsemicolon, this.preferences.insert_space_before_semicolon);
		return false;
	}
	
	public boolean visit(TypedefDeclarationFragment node)
	{
		node.getName().accept(this);
		Initializer init = node.getInitializer();
		if(null != init)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
			init.accept(this);
		}
		return false;
	}
	
	public boolean visit(TypeDotIdentifierExpression node)
	{
		node.getType().accept(this);
		scribe.printNextToken(TOK.TOKdot);
		node.getName().accept(this);
		return false;
	}
	
	public boolean visit(TypeExpression node)
	{
		node.getType().accept(this);
		return false;
	}
	
	public boolean visit(TypeidExpression node)
	{
		scribe.printNextToken(TOK.TOKtypeid);
		scribe.printNextToken(TOK.TOKlparen);
		node.getType().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(TypeofType node)
	{
		scribe.printNextToken(TOK.TOKtypeof);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		return false;
	}
	
	public boolean visit(TypeTemplateParameter node)
	{
		node.getName().accept(this);
		Type specificType = node.getSpecificType();
		if(null != specificType)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKcolon);
			scribe.space();
			specificType.accept(this);
		}
		Type defaultType = node.getDefaultType();
		if(null != defaultType)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
			defaultType.accept(this);
		}
		return false;
	}
	
	public boolean visit(UnitTestDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKunittest);
		scribe.space();
		node.getBody().accept(this);
		if(isNextToken(TOK.TOKsemicolon))
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}
	
	public boolean visit(ValueTemplateParameter node)
	{
		node.getName().accept(this);
		Expression specificValue = node.getSpecificValue();
		if(null != specificValue)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKcolon);
			scribe.space();
			specificValue.accept(this);
		}
		Expression defaultValue = node.getDefaultValue();
		if(null != defaultValue)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
			defaultValue.accept(this);
		}
		return false;
	}

	public boolean visit(VariableDeclaration node)
	{
		formatModifiers(node.modifiers());
		// For some reason, formatModifiers() doesn't seem to grab this
		if(isNextToken(TOK.TOKstatic))
		{
			scribe.printNextToken(TOK.TOKstatic);
			scribe.space();
		}
		Type type = node.getType();
		if(null != type)
		{
			type.accept(this);
			scribe.space();
		}
		formatCSV(node.fragments(), true);
		if(isNextToken(TOK.TOKsemicolon))
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}

	public boolean visit(VariableDeclarationFragment node)
	{
		node.getName().accept(this);
		Initializer init = node.getInitializer();
		if(null != init)
		{
			scribe.space();
			scribe.printNextToken(TOK.TOKassign);
			scribe.space();
			init.accept(this);
		}
		return false;
	}

	public boolean visit(Version node)
	{
		scribe.printNextToken(literalOrIdentiferTokenList());
		return false;
	}
	
	public boolean visit(VersionAssignment node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKversion);
		scribe.space();
		scribe.printNextToken(TOK.TOKassign);
		scribe.space();
		node.getVersion().accept(this);
			scribe.printNextToken(TOK.TOKsemicolon);
		return false;
	}

	public boolean visit(VersionDeclaration node)
	{
		formatModifiers(node.modifiers());
		scribe.printNextToken(TOK.TOKversion);
		Version version = node.getVersion();
		if(null != version)
		{
			scribe.printNextToken(TOK.TOKlparen);
			version.accept(this);
			scribe.printNextToken(TOK.TOKrparen);
		}
		formatConditionalDeclaration(node);
		return false;
	}

	public boolean visit(VersionStatement node)
	{
		scribe.printNextToken(TOK.TOKversion);
		Version version = node.getVersion();
		if(null != version)
		{
			scribe.printNextToken(TOK.TOKlparen);
			version.accept(this);
			scribe.printNextToken(TOK.TOKrparen);
		}
		formatConditionalStatement(node);
		return false;
	}
	
	public boolean visit(VoidInitializer node)
	{
		scribe.printNextToken(TOK.TOKvoid);
		return false;
	}

	public boolean visit(VolatileStatement node)
	{
		scribe.printNextToken(TOK.TOKvolatile);
		formatSubStatement(node.getBody(), true, 0, false, 0);
		return false;
	}

	public boolean visit(WhileStatement node)
	{
		scribe.printNextToken(TOK.TOKwhile);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		formatSubStatement(node.getBody(), true, 0, false, 0);
		return false;
	}

	public boolean visit(WithStatement node)
	{
		scribe.printNextToken(TOK.TOKwith);
		scribe.printNextToken(TOK.TOKlparen);
		node.getExpression().accept(this);
		scribe.printNextToken(TOK.TOKrparen);
		formatSubStatement(node.getBody(), true, 0, false, 0);
		return false;
	}

	private void formatConditionalDeclaration(ConditionalDeclaration node)
	{
		formatDeclarationBlock(node.thenDeclarations());
		if(isNextToken(TOK.TOKelse))
		{
			scribe.printNewLine();
			scribe.printNextToken(TOK.TOKelse);
			formatDeclarationBlock(node.elseDeclarations());
		}
	}
	
	private void formatConditionalStatement(ConditionalStatement node)
	{
		formatSubStatement(node.getThenBody(), true, 0, false, 0);
		if(isNextToken(TOK.TOKelse))
		{
			scribe.printNewLine();
			scribe.printNextToken(TOK.TOKelse);
			formatSubStatement(node.getElseBody(), true, 0, false, 0);
		}
	}
	
	/**
	 * Formats a list of comma-separated values. This may not be appropriate for
	 * all CSV lists (for example, if the values need to be on separate lines),
	 * but should increase code reuse since the JDT repeats the logic here about
	 * 20 times.
	 */
	private void formatCSV(List<? extends ASTNode> values, boolean insertSpaces)
	{
		int len = values.size();
		if(len == 0)
			return;
		
		for (int i = 0; i < len - 1; i++)
		{
			values.get(i).accept(this);
			scribe.printNextToken(TOK.TOKcomma);
			if(insertSpaces)
				scribe.space();
		}
		
		values.get(len - 1).accept(this);
		
	}
	
	private void formatDeclarationBlock(List<Declaration> declarations)
	{
		if(isNextToken(TOK.TOKlcurly))
		{
			scribe.printNewLine();
			scribe.printNextToken(TOK.TOKlcurly);
			scribe.printNewLine();
			scribe.indent();
			formatDeclarations(declarations);
			scribe.printNewLine();
			scribe.unIndent();
			scribe.printNextToken(TOK.TOKrcurly);
		}
		else if(isNextToken(TOK.TOKcolon))
		{
			// These are usually rendered as empty in the AST
			scribe.printNextToken(TOK.TOKcolon);
			scribe.printNewLine();
			scribe.indent();
			formatDeclarations(declarations);
			scribe.unIndent();
		}
		else
		{
			scribe.printNewLine();
			scribe.indent();
			formatDeclarations(declarations);
			scribe.unIndent();
		}
	}
	
	private void formatDeclarations(List<Declaration> declarations)
	{
		if(null == declarations || declarations.isEmpty())
			return;
		
		Declaration prev = declarations.get(0);
		prev.accept(this);
		scribe.printNewLine();
		int len = declarations.size();
		for (int i = 1; i < len; i++)
		{
			Declaration cur = declarations.get(i);
			
			// Determine how many lines to put above this declararation
			int blankLines;
			if((prev instanceof AliasDeclaration && cur instanceof AliasDeclaration) ||
			   (prev instanceof AlignDeclaration && cur instanceof AlignDeclaration) ||
			   (prev instanceof DebugAssignment && cur instanceof DebugAssignment) ||
			   (prev instanceof ImportDeclaration && cur instanceof ImportDeclaration) ||
			   (prev instanceof InvariantDeclaration && cur instanceof InvariantDeclaration) ||
			   (prev instanceof MixinDeclaration && cur instanceof MixinDeclaration) ||
			   (prev instanceof PragmaDeclaration && cur instanceof PragmaDeclaration) ||
			   (prev instanceof StaticAssert && cur instanceof StaticAssert) ||
			   (prev instanceof TypedefDeclaration && cur instanceof TypedefDeclaration) ||
			   (prev instanceof VariableDeclaration && cur instanceof VariableDeclaration) ||
			   (prev instanceof VersionAssignment && cur instanceof VersionAssignment) ||
			   (prev instanceof DebugAssignment && cur instanceof VersionAssignment) ||
			   (prev instanceof VersionAssignment && cur instanceof DebugAssignment)
			  )
				blankLines = 0;
			else
				blankLines = 1;
			
			if(blankLines > 0)
				scribe.printEmptyLines(blankLines);
			
			cur.accept(this);
			scribe.printNewLine();
			prev = cur;
		}
	}
	
	/**
	 * Formats the given function. This is used because functions will be
	 * visited as FunctionDeclarations and ConstructorDeclarations.
	 */
	private void formatFunction(IFunctionDeclaration node)
	{
		if(isNextToken(TOK.TOKlparen))
		{
			boolean spaceBeforeParen = this.preferences.insert_space_before_opening_paren_in_method_declaration;
			if (node instanceof ConstructorDeclaration) {
				spaceBeforeParen = this.preferences.insert_space_before_opening_paren_in_constructor_declaration;
			}			
			scribe.printNextToken(TOK.TOKlparen, spaceBeforeParen);
			
			if (node.arguments().size() > 0) {
				if (this.preferences.insert_space_after_opening_paren_in_method_declaration) {
					this.scribe.space();
				}
				formatCSV(node.arguments(), true);
				if (node.isVariadic()) {
					if (node.arguments().size() > 0) {
						scribe.printNextToken(TOK.TOKcomma);
						scribe.space();
					}
					scribe.printNextToken(TOK.TOKdotdotdot);	
				}
				if (this.preferences.insert_space_before_closing_paren_in_method_declaration) {
					this.scribe.space();
				}
			} else {
				if (this.preferences.insert_space_between_empty_parens_in_method_declaration) {
					this.scribe.space();
				}
			}
			scribe.printNextToken(TOK.TOKrparen);
		}
		
		Block in   = (Block) node.getPrecondition();
		Block out  = (Block) node.getPostcondition();
		Block body = (Block) node.getBody();
		
		if (null != in || null != out)
		{
			scribe.printNewLine();
			scribe.indent();
			loop: do
				switch (nextNonCommentToken())
				{
					case TOKin:
						scribe.printNextToken(TOK.TOKin);
						formatSubStatement(in, true, 0, false, 0);
						continue loop;
					case TOKout:
						scribe.printNextToken(TOK.TOKout);
						if(isNextToken(TOK.TOKlparen))
						{
							scribe.printNextToken(TOK.TOKlparen);
							SimpleName name = node.getPostconditionVariableName();
							if(null != name)
								scribe.printNextToken(TOK.TOKidentifier);
							scribe.printNextToken(TOK.TOKrparen);
						}
						formatSubStatement(out, true, 0, false, 0);
						continue loop;
					case TOKbody:
						scribe.printNextToken(TOK.TOKbody);
						formatSubStatement(body, true, 0, false, 0);
						continue loop;
					default:
						break loop;
				}
			while (true);
			scribe.unIndent();
		}
		else if (isNextToken(TOK.TOKbody))
		{
			scribe.printNewLine();
			scribe.indent();
			scribe.printNextToken(TOK.TOKbody);
			formatSubStatement(body, true, 0, false, 0);
			scribe.unIndent();
		}
		else {
			if (body != null) {
				scribe.printNewLine();
				formatSubStatement(body, true, 0, false, 0);
			} else {
				scribe.printNextToken(TOK.TOKsemicolon, this.preferences.insert_space_before_semicolon);
			}
		}
	}
	
	/**
	 * Asks the scribe to print modifers if there are any (this is here to
	 * prevent extra spaces from being put in).
	 */
	private void formatModifiers(List<Modifier> modifiers)
	{
		if (!modifiers.isEmpty())
		{
			scribe.printModifiers(modifiers);
			scribe.space();
		}
	}
	
	/**
	 * Given a series of statements (such as in a function body), formats them
	 * such that only a single non-empty statement appears per line. Calls
	 * accept() on each node.
	 */
	private void formatStatements(final List<Statement> statements,
			boolean insertNewLineAfterLastStatement)
	{
		int statementsLength = statements.size();
		if (statementsLength == 0)
			return;
		else if (statementsLength == 1)
			statements.get(0).accept(this);
		else
		{
			Statement previousStatement = statements.get(0);
			previousStatement.accept(this);
			int previousStatementNodeType = previousStatement.getNodeType();
			for (int i = 1; i < statementsLength - 1; i++)
			{
				Statement statement = statements.get(i);
				int statementNodeType = statement.getNodeType();
				if ((previousStatementNodeType == ASTNode.EMPTY_STATEMENT && statementNodeType != ASTNode.EMPTY_STATEMENT)
						|| (previousStatementNodeType != ASTNode.EMPTY_STATEMENT && statementNodeType != ASTNode.EMPTY_STATEMENT))
					scribe.printNewLine();
				statement.accept(this);
				previousStatement = statement;
			}
			Statement statement = statements.get(statementsLength - 1);
			int statementNodeType = statement.getNodeType();
			if ((previousStatementNodeType == ASTNode.EMPTY_STATEMENT && statementNodeType != ASTNode.EMPTY_STATEMENT)
					|| (previousStatementNodeType != ASTNode.EMPTY_STATEMENT && statementNodeType != ASTNode.EMPTY_STATEMENT))
				scribe.printNewLine();
			statement.accept(this);
		}
		
		if (insertNewLineAfterLastStatement)
			scribe.printNewLine();
	}
	
	private void formatSubStatement(Statement statement, boolean newLineAtStart,
			int spacesAtStart, boolean newLineAtEnd, int spacesAtEnd)
	{
		formatSubStatement(statement, newLineAtStart, spacesAtStart,
				newLineAtEnd, spacesAtEnd, true);
	}
	
	private void formatSubStatement(Statement statement, boolean newLineAtStart,
			int spacesAtStart, boolean newLineAtEnd, int spacesAtEnd, boolean indent)
	{
		if(statement instanceof EmptyStatement)
		{
			statement.accept(this);
			return;
		}
		
		if(newLineAtStart)
			scribe.printNewLine();
		
		if(statement instanceof Block)
		{
			if(isNextToken(TOK.TOKlcurly)) // Switch statment bodies are blocks,
				                           // but don't have curly braces.
			{
				scribe.printNextToken(TOK.TOKlcurly);
				scribe.printNewLine();
				if(indent)
					scribe.indent();
				formatStatements(((Block) statement).statements(), true);
				if(indent)
					scribe.unIndent();
				scribe.printNextToken(TOK.TOKrcurly);
			}
			else
			{
				if(indent)
					scribe.indent();
				formatStatements(((Block) statement).statements(), true);
				if(indent)
					scribe.unIndent();
			}
		}
		
		else
		{
			if(indent)
				scribe.indent();
			statement.accept(this);
			if(indent)
				scribe.unIndent();
		}
		
		if(newLineAtEnd)
			scribe.printNewLine();
	}
	
	private void formatTemplateParams(List<TemplateParameter> tp)
	{
		if(tp.isEmpty())
			return;
		scribe.printNextToken(TOK.TOKlparen);
		formatCSV(tp, true);
		scribe.printNextToken(TOK.TOKrparen);
	}
	
	private TextEdit failedToFormat(Throwable e)
	{
		if (DEBUG)
			e.printStackTrace();
		return null;
	}
	
	private boolean hasComments()
	{
		lexer.reset(scribe.lexer.base, scribe.scannerEndPosition - 1);
		return isComment(lexer.nextToken());
	}
	
	/**
	 * Checks whether the token is a comment
	 * 
	 * @param token
	 * @return
	 */
	private boolean isComment(TOK token)
	{
		switch (token)
		{
			case TOKlinecomment:
			case TOKblockcomment:
			case TOKpluscomment:
			case TOKdoclinecomment:
			case TOKdocblockcomment:
			case TOKdocpluscomment:
				return true;
			default:
				return false;
		}
	}
	
	private boolean isNextToken(TOK test)
	{
		TOK token = nextNonCommentToken();
		return token == test;
	}
	
	/**
	 * Kills off comments in the lexer until a non-comment token is reached
	 */
	private TOK nextNonCommentToken()
	{
		lexer.reset(scribe.lexer.p, scribe.scannerEndPosition - 1);
		TOK token;
		do
			token = lexer.nextToken();
		while (isComment(token));
		return token;
	}
	
	/**
	 * Resets the lexer with the specified source
	 * 
	 * @param source
	 */
	private void restartLexer(String source)
	{
		if (null == lexer)
			lexer = new Lexer(source, true, true, false, false, AST.D2);
		else
			lexer.reset(source.toCharArray(), 0, source.length(), true, true,
					false, false);
	}
	
	private void startup(String src, ASTNode node) throws AbortFormatting
	{
		if ((node.getFlags() & ASTNode.MALFORMED) != 0)
			throw new AbortFormatting("Malformed AST node");
		scribe.reset();
		restartLexer(src);
		scribe.initializeScanner(src);
	}
	
	public final static boolean	        DEBUG = true;
	
	private static TOK[] PRIMITIVE_TYPES_LIST;
	private static TOK[] LITERAL_OR_IDENTIFIER;
	private static TOK[] MODIFIERS;
	private static TOK[] ASSIGNMENT_OPERATORS;
	private static TOK[] PREFIX_OPERATORS;
	private static TOK[] POSTFIX_OPERATORS;
	private static TOK[] INFIX_OPERATORS;
	private static TOK[] BOOLEAN_LITERALS;
	private static TOK[] CHARACTER_LITERALS;
	private static TOK[] NUMERIC_LITERALS;
	private static TOK[] TYPE_SPECIALIZATIONS;
	private static TOK[] NEG_OR_NOT;
	private static TOK[] TILDE_OR_CAT;
	
	private static TOK[] assignmentOperatorTokenList()
	{
		if(null == ASSIGNMENT_OPERATORS)
		{
			ASSIGNMENT_OPERATORS = new TOK[13];
			
			ASSIGNMENT_OPERATORS[0] = TOK.TOKassign;
			ASSIGNMENT_OPERATORS[1] = TOK.TOKmulass;
			ASSIGNMENT_OPERATORS[2] = TOK.TOKdivass;
			ASSIGNMENT_OPERATORS[3] = TOK.TOKmodass;
			ASSIGNMENT_OPERATORS[4] = TOK.TOKaddass;
			ASSIGNMENT_OPERATORS[5] = TOK.TOKminass;
			ASSIGNMENT_OPERATORS[6] = TOK.TOKcatass;
			ASSIGNMENT_OPERATORS[7] = TOK.TOKandass;
			ASSIGNMENT_OPERATORS[8] = TOK.TOKxorass;
			ASSIGNMENT_OPERATORS[9] = TOK.TOKorass;
			ASSIGNMENT_OPERATORS[10] = TOK.TOKshlass;
			ASSIGNMENT_OPERATORS[11] = TOK.TOKshrass;
			ASSIGNMENT_OPERATORS[12] = TOK.TOKushrass;
			Arrays.sort(ASSIGNMENT_OPERATORS);
		}
		return ASSIGNMENT_OPERATORS;
	}
	
	
	
	private static TOK[] booleanLiteralTokenList()
	{
		if(null == BOOLEAN_LITERALS)
		{
			BOOLEAN_LITERALS = new TOK[2];
			
			// I bet you can guess what comes next...
			BOOLEAN_LITERALS[0] = TOK.TOKtrue;
			BOOLEAN_LITERALS[1] = TOK.TOKfalse;
			Arrays.sort(BOOLEAN_LITERALS);
		}
		return BOOLEAN_LITERALS;
	}
	
	private static TOK[] characterLiteralTokenList()
	{
		if(null == CHARACTER_LITERALS)
		{
			CHARACTER_LITERALS = new TOK[3];
			
			CHARACTER_LITERALS[0] = TOK.TOKcharv;
			CHARACTER_LITERALS[1] = TOK.TOKwcharv;
			CHARACTER_LITERALS[2] = TOK.TOKdcharv;
			Arrays.sort(CHARACTER_LITERALS);
		}
		return CHARACTER_LITERALS;
	}
	
	private static TOK[] infixOperatorTokenList()
	{
		if(null == INFIX_OPERATORS)
		{
			INFIX_OPERATORS = new TOK[35];
			
			INFIX_OPERATORS[0] = TOK.TOKmul;
			INFIX_OPERATORS[1] = TOK.TOKdiv;
			INFIX_OPERATORS[2] = TOK.TOKmod;
			INFIX_OPERATORS[3] = TOK.TOKadd;
			INFIX_OPERATORS[4] = TOK.TOKmin;
			INFIX_OPERATORS[5] = TOK.TOKcat;
			INFIX_OPERATORS[6] = TOK.TOKshl;
			INFIX_OPERATORS[7] = TOK.TOKshr;
			INFIX_OPERATORS[8] = TOK.TOKushr;
			INFIX_OPERATORS[9] = TOK.TOKlt;
			INFIX_OPERATORS[10] = TOK.TOKgt;
			INFIX_OPERATORS[11] = TOK.TOKle;
			INFIX_OPERATORS[12] = TOK.TOKge;
			INFIX_OPERATORS[13] = TOK.TOKequal;
			INFIX_OPERATORS[14] = TOK.TOKnotequal;
			INFIX_OPERATORS[15] = TOK.TOKxor;
			INFIX_OPERATORS[16] = TOK.TOKand;
			INFIX_OPERATORS[17] = TOK.TOKor;
			INFIX_OPERATORS[18] = TOK.TOKandand;
			INFIX_OPERATORS[19] = TOK.TOKoror;
			INFIX_OPERATORS[20] = TOK.TOKcomma;
			INFIX_OPERATORS[21] = TOK.TOKin;
			INFIX_OPERATORS[22] = TOK.TOKis;
			INFIX_OPERATORS[23] = TOK.TOKnotis;
			INFIX_OPERATORS[24] = TOK.TOKidentity;
			INFIX_OPERATORS[25] = TOK.TOKnotidentity;
			INFIX_OPERATORS[26] = TOK.TOKunord;
			INFIX_OPERATORS[27] = TOK.TOKlg;
			INFIX_OPERATORS[28] = TOK.TOKleg;
			INFIX_OPERATORS[29] = TOK.TOKug;
			INFIX_OPERATORS[30] = TOK.TOKuge;
			INFIX_OPERATORS[31] = TOK.TOKul;
			INFIX_OPERATORS[32] = TOK.TOKule;
			INFIX_OPERATORS[33] = TOK.TOKue;
			INFIX_OPERATORS[34] = TOK.TOKtilde; //Redundant with TOKcat?
			Arrays.sort(INFIX_OPERATORS);
			
		}
		return INFIX_OPERATORS;
	}
	
	private static TOK[] literalOrIdentiferTokenList()
	{
		if(null == LITERAL_OR_IDENTIFIER)
		{
			LITERAL_OR_IDENTIFIER = new TOK[18];
			
			LITERAL_OR_IDENTIFIER[0] = TOK.TOKint32v;
			LITERAL_OR_IDENTIFIER[1] = TOK.TOKuns32v;
			LITERAL_OR_IDENTIFIER[2] = TOK.TOKint64v;
			LITERAL_OR_IDENTIFIER[3] = TOK.TOKuns64v;
			LITERAL_OR_IDENTIFIER[4] = TOK.TOKfloat32v;
			LITERAL_OR_IDENTIFIER[5] = TOK.TOKfloat64v;
			LITERAL_OR_IDENTIFIER[6] = TOK.TOKfloat80v;
			LITERAL_OR_IDENTIFIER[7] = TOK.TOKimaginary32v;
			LITERAL_OR_IDENTIFIER[8] = TOK.TOKimaginary64v;
			LITERAL_OR_IDENTIFIER[9] = TOK.TOKimaginary80v;
			LITERAL_OR_IDENTIFIER[10] = TOK.TOKcharv;
			LITERAL_OR_IDENTIFIER[11] = TOK.TOKwcharv;
			LITERAL_OR_IDENTIFIER[12] = TOK.TOKdcharv;
			LITERAL_OR_IDENTIFIER[13] = TOK.TOKidentifier;
			LITERAL_OR_IDENTIFIER[14] = TOK.TOKstring;
			LITERAL_OR_IDENTIFIER[15] = TOK.TOKtrue;
			LITERAL_OR_IDENTIFIER[16] = TOK.TOKfalse;
			LITERAL_OR_IDENTIFIER[17] = TOK.TOKnull;
			Arrays.sort(LITERAL_OR_IDENTIFIER);
		}
		return LITERAL_OR_IDENTIFIER;
	}
	
	private static TOK[] modifierTokenList()
	{
		if(null == MODIFIERS)
		{
			MODIFIERS = new TOK[15];
			
			MODIFIERS[0] = TOK.TOKprivate;
			MODIFIERS[1] = TOK.TOKprotected;
			MODIFIERS[2] = TOK.TOKpackage;
			MODIFIERS[3] = TOK.TOKpublic;
			MODIFIERS[4] = TOK.TOKexport;
			MODIFIERS[5] = TOK.TOKstatic;
			MODIFIERS[6] = TOK.TOKfinal;
			MODIFIERS[7] = TOK.TOKabstract;
			MODIFIERS[8] = TOK.TOKoverride;
			MODIFIERS[9] = TOK.TOKauto;
			MODIFIERS[10] = TOK.TOKsynchronized;
			MODIFIERS[11] = TOK.TOKdeprecated;
			MODIFIERS[12] = TOK.TOKextern;
			MODIFIERS[13] = TOK.TOKconst;
			MODIFIERS[14] = TOK.TOKscope;
			Arrays.sort(MODIFIERS);
		}
		return MODIFIERS;
	}
	
	private static TOK[] negOrNotTokenList()
	{
		if(null == NEG_OR_NOT)
		{
			NEG_OR_NOT = new TOK[2];
			
			NEG_OR_NOT[0] = TOK.TOKneg;
			NEG_OR_NOT[1] = TOK.TOKnot;
			Arrays.sort(NEG_OR_NOT);
		}
		return NEG_OR_NOT;
	}
	
	private static TOK[] numericLiteralTokenList()
	{
		if(null == NUMERIC_LITERALS)
		{
			NUMERIC_LITERALS = new TOK[10];
			
			NUMERIC_LITERALS[0] = TOK.TOKint32v;
			NUMERIC_LITERALS[1] = TOK.TOKuns32v;
			NUMERIC_LITERALS[2] = TOK.TOKint64v;
			NUMERIC_LITERALS[3] = TOK.TOKuns64v;
			NUMERIC_LITERALS[4] = TOK.TOKfloat32v;
			NUMERIC_LITERALS[5] = TOK.TOKfloat64v;
			NUMERIC_LITERALS[6] = TOK.TOKfloat80v;
			NUMERIC_LITERALS[7] = TOK.TOKimaginary32v;
			NUMERIC_LITERALS[8] = TOK.TOKimaginary64v;
			NUMERIC_LITERALS[9] = TOK.TOKimaginary80v;
			Arrays.sort(NUMERIC_LITERALS);
		}
		return NUMERIC_LITERALS;
	}
	
	private static TOK[] postfixOperatorTokenList()
	{
		if(null == POSTFIX_OPERATORS)
		{
			POSTFIX_OPERATORS = new TOK[2];
			
			POSTFIX_OPERATORS[0] = TOK.TOKplusplus;
			POSTFIX_OPERATORS[1] = TOK.TOKminusminus;
			Arrays.sort(POSTFIX_OPERATORS);
		}
		return POSTFIX_OPERATORS;
	}
	
	private static TOK[] prefixOperatorTokenList()
	{
		if(null == PREFIX_OPERATORS)
		{
			PREFIX_OPERATORS = new TOK[10];
			
			PREFIX_OPERATORS[0] = TOK.TOKplusplus;
			PREFIX_OPERATORS[1] = TOK.TOKminusminus;
			PREFIX_OPERATORS[2] = TOK.TOKand;
			PREFIX_OPERATORS[3] = TOK.TOKmul;
			PREFIX_OPERATORS[4] = TOK.TOKadd;
			PREFIX_OPERATORS[5] = TOK.TOKmin;
			PREFIX_OPERATORS[6] = TOK.TOKtilde;
			PREFIX_OPERATORS[7] = TOK.TOKnot;
			PREFIX_OPERATORS[8] = TOK.TOKcat; // Redundant with TOKtilde?
			PREFIX_OPERATORS[9] = TOK.TOKneg; // Redundant with TOKnot?
			Arrays.sort(PREFIX_OPERATORS);
		}
		return PREFIX_OPERATORS;
	}
	
	private static TOK[] primitiveTypesTokenList()
	{
		if (null == PRIMITIVE_TYPES_LIST)
		{
			PRIMITIVE_TYPES_LIST = new TOK[25];
			
			PRIMITIVE_TYPES_LIST[0] = TOK.TOKauto;
			PRIMITIVE_TYPES_LIST[1] = TOK.TOKint8;
			PRIMITIVE_TYPES_LIST[2] = TOK.TOKuns8;
			PRIMITIVE_TYPES_LIST[3] = TOK.TOKint16;
			PRIMITIVE_TYPES_LIST[4] = TOK.TOKuns16;
			PRIMITIVE_TYPES_LIST[5] = TOK.TOKint32;
			PRIMITIVE_TYPES_LIST[6] = TOK.TOKuns32;
			PRIMITIVE_TYPES_LIST[7] = TOK.TOKint64;
			PRIMITIVE_TYPES_LIST[8] = TOK.TOKuns64;
			PRIMITIVE_TYPES_LIST[9] = TOK.TOKfloat32;
			PRIMITIVE_TYPES_LIST[10] = TOK.TOKfloat64;
			PRIMITIVE_TYPES_LIST[11] = TOK.TOKfloat80;
			PRIMITIVE_TYPES_LIST[12] = TOK.TOKimaginary32;
			PRIMITIVE_TYPES_LIST[13] = TOK.TOKimaginary64;
			PRIMITIVE_TYPES_LIST[14] = TOK.TOKimaginary80;
			PRIMITIVE_TYPES_LIST[15] = TOK.TOKcomplex32;
			PRIMITIVE_TYPES_LIST[16] = TOK.TOKcomplex64;
			PRIMITIVE_TYPES_LIST[17] = TOK.TOKcomplex80;
			PRIMITIVE_TYPES_LIST[18] = TOK.TOKchar;
			PRIMITIVE_TYPES_LIST[19] = TOK.TOKwchar;
			PRIMITIVE_TYPES_LIST[20] = TOK.TOKdchar;
			PRIMITIVE_TYPES_LIST[21] = TOK.TOKbit;
			PRIMITIVE_TYPES_LIST[22] = TOK.TOKbool;
			PRIMITIVE_TYPES_LIST[23] = TOK.TOKcent;
			PRIMITIVE_TYPES_LIST[24] = TOK.TOKucent;
			Arrays.sort(PRIMITIVE_TYPES_LIST);
		}
		return PRIMITIVE_TYPES_LIST;
	}
	
	private static TOK[] tildeOrCatTokenList()
	{
		if(null == TILDE_OR_CAT)
		{
			TILDE_OR_CAT = new TOK[2];
			
			TILDE_OR_CAT[0] = TOK.TOKtilde;
			TILDE_OR_CAT[1] = TOK.TOKcat;
			Arrays.sort(TILDE_OR_CAT);
		}
		return TILDE_OR_CAT;
	}
	
	private static TOK[] typeSpecializationTokenList()
	{
		if(null == TYPE_SPECIALIZATIONS)
		{
			TYPE_SPECIALIZATIONS = new TOK[10];
			
			TYPE_SPECIALIZATIONS[0] = TOK.TOKtypedef;
			TYPE_SPECIALIZATIONS[1] = TOK.TOKstruct;
			TYPE_SPECIALIZATIONS[2] = TOK.TOKunion;
			TYPE_SPECIALIZATIONS[3] = TOK.TOKclass;
			TYPE_SPECIALIZATIONS[4] = TOK.TOKenum;
			TYPE_SPECIALIZATIONS[5] = TOK.TOKinterface;
			TYPE_SPECIALIZATIONS[6] = TOK.TOKfunction;
			TYPE_SPECIALIZATIONS[7] = TOK.TOKdelegate;
			TYPE_SPECIALIZATIONS[8] = TOK.TOKreturn;
			TYPE_SPECIALIZATIONS[9] = TOK.TOKsuper;
			Arrays.sort(TYPE_SPECIALIZATIONS);
		}
		return TYPE_SPECIALIZATIONS;
	}
}
