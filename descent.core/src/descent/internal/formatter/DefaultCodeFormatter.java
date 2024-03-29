/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import descent.core.JavaCore;
import descent.core.dom.ASTParser;
import descent.core.dom.Block;
import descent.core.dom.CompilationUnit;
import descent.core.formatter.CodeFormatter;
import descent.core.formatter.DefaultCodeFormatterConstants;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.util.Util;

public class DefaultCodeFormatter extends CodeFormatter {

	public static final boolean DEBUG = false;
	//private static Scanner ProbingScanner;
	public static boolean USE_NEW_FORMATTER = false;

	/**
	 * Creates a comment region for a specific document partition type.
	 * 
	 * @param kind the comment snippet kind
	 * @param document the document which contains the comment region
	 * @param range range of the comment region in the document
	 * @return a new comment region for the comment region range in the
	 *         document
	 * @since 3.1
	 */
	/*public static CommentRegion createRegion(int kind, IDocument document, Position range, CodeFormatterVisitor formatter) {
		switch (kind) {
			case CodeFormatter.K_SINGLE_LINE_COMMENT:
				return new CommentRegion(document, range, formatter);
			case CodeFormatter.K_MULTI_LINE_COMMENT:
				return new MultiCommentRegion(document, range, formatter);
			case CodeFormatter.K_JAVA_DOC:
				return new JavaDocRegion(document, range, formatter);
		}
		return null;
	}*/
	
	//private CodeSnippetParsingUtil codeSnippetParsingUtil;
	private Map defaultCompilerOptions;
	
	private CodeFormatterVisitor newCodeFormatter2;
	private Map options;
	
	private DefaultCodeFormatterOptions preferences;
	private int apiLevel;
	
	public DefaultCodeFormatter() {
		this(new DefaultCodeFormatterOptions(DefaultCodeFormatterConstants.getDefaultSettings()), null);
	}
	
	public DefaultCodeFormatter(DefaultCodeFormatterOptions preferences) {
		this(preferences, null);
	}

	public DefaultCodeFormatter(DefaultCodeFormatterOptions defaultCodeFormatterOptions, Map options) {
		if (options != null) {
			this.options = options;
			this.preferences = new DefaultCodeFormatterOptions(options);
		} else {
			this.options = JavaCore.getOptions();
			this.preferences = new DefaultCodeFormatterOptions(DefaultCodeFormatterConstants.getDefaultSettings());
		}
		this.defaultCompilerOptions = getDefaultCompilerOptions();
		if (defaultCodeFormatterOptions != null) {
			this.preferences.set(defaultCodeFormatterOptions.getMap());
		}
		this.apiLevel = descent.internal.core.util.Util.getApiLevel(options);
	}

	public DefaultCodeFormatter(Map options) {
		this(null, options);
	}
	
	public String createIndentationString(final int indentationLevel) {
		if (indentationLevel < 0) {
			throw new IllegalArgumentException();
		}
		
		int tabs = 0;
		int spaces = 0;
		if(DefaultCodeFormatterConstants.SPACE.equals(preferences.tab_char))
		{
			spaces = indentationLevel * this.preferences.tab_size;
		}
		else if(DefaultCodeFormatterConstants.TAB.equals(preferences.tab_char))
		{
			tabs = indentationLevel;
		}
		else if(DefaultCodeFormatterConstants.MIXED.equals(preferences.tab_char))
		{
			int tabSize = this.preferences.tab_size;
			int spaceEquivalents = indentationLevel * this.preferences.indentation_size;
			tabs = spaceEquivalents / tabSize;
			spaces = spaceEquivalents % tabSize;
		}
		else
		{
			return Util.EMPTY_STRING;
		}
		
		if (tabs == 0 && spaces == 0) {
			return Util.EMPTY_STRING;
		}
		StringBuffer buffer = new StringBuffer(tabs + spaces);
		for(int i = 0; i < tabs; i++) {
			buffer.append('\t');
		}
		for(int i = 0; i < spaces; i++) {
			buffer.append(' ');
		}
		return buffer.toString();
	}
	
	/**
	 * @see descent.core.formatter.CodeFormatter#format(int, java.lang.String, int, int, int, java.lang.String)
	 */
	public TextEdit format(
			int kind,
			String source,
			int offset,
			int length,
			int indentationLevel,
			String lineSeparator) {

		if (offset < 0 || length < 0 || length > source.length()) {
			throw new IllegalArgumentException();
		}
		try
		{
			//this.codeSnippetParsingUtil = new CodeSnippetParsingUtil();
			switch(kind) {
				case K_COMPILATION_UNIT :
				case K_CLASS_BODY_DECLARATIONS : // Synonym for compilation unit
					return formatCompilationUnit(source, indentationLevel, lineSeparator, offset, length);
				case K_EXPRESSION :
					return formatExpression(source, indentationLevel, lineSeparator, offset, length);
				case K_STATEMENTS :
					return formatStatements(source, indentationLevel, lineSeparator, offset, length);
				case K_UNKNOWN :
					//TODO JDT formatter unknown formatting
					//return probeFormatting(source, indentationLevel, lineSeparator, offset, length);
					throw new UnsupportedOperationException("Format unknown!");
				case K_JAVA_DOC :
				case K_MULTI_LINE_COMMENT :
				case K_SINGLE_LINE_COMMENT :
					return formatComment(kind, source, indentationLevel, lineSeparator, offset, length);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	//TODO JDT formatter commets
	private TextEdit formatComment(int kind, String source, int indentationLevel, String lineSeparator, int offset, int length) {
		/* final boolean isFormattingComments = DefaultCodeFormatterConstants.TRUE.equals(this.options.get(DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT));
		if (isFormattingComments) {
			if (lineSeparator != null) {
				this.preferences.line_separator = lineSeparator;
			} else {
				this.preferences.line_separator = Util.LINE_SEPARATOR;
			}
			this.preferences.initial_indentation_level = indentationLevel;
			this.newCodeFormatter = new CodeFormatterVisitor(this.preferences, this.options, offset, length, null);
			final CommentRegion region = createRegion(kind, new Document(source), new Position(offset, length), this.newCodeFormatter);
			if (region != null) {
				return this.newCodeFormatter.format(source, region);
			}
		} */
		return new MultiTextEdit();
	}

	private TextEdit formatCompilationUnit(String source, int indentationLevel, String lineSeparator, int offset, int length) {
		ASTParser parser = ASTParser.newParser(apiLevel);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setCompilerOptions(getDefaultCompilerOptions());
		parser.setResolveBindings(false);
		parser.setUnitName(Util.EMPTY_STRING);
		descent.core.dom.ASTNode node = parser.createAST(null);
		if (lineSeparator != null) {
			this.preferences.line_separator = lineSeparator;
		} else {
			this.preferences.line_separator = Util.LINE_SEPARATOR;
		}
		this.preferences.initial_indentation_level = indentationLevel;

		this.newCodeFormatter2 = new CodeFormatterVisitor(this.preferences, this.options, offset, length, (CompilationUnit) node.getRoot(), apiLevel);
		return this.newCodeFormatter2.format(source, (CompilationUnit) node);
	}

	private TextEdit formatExpression(String source, int indentationLevel, String lineSeparator, int offset, int length) {
		ASTParser parser = ASTParser.newParser(apiLevel);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setCompilerOptions(getDefaultCompilerOptions());
		parser.setResolveBindings(false);
		parser.setUnitName(Util.EMPTY_STRING);
		descent.core.dom.ASTNode node = parser.createAST(null);
		if (node.getNodeType() == descent.core.dom.ASTNode.COMPILATION_UNIT) return null;
		if (lineSeparator != null) {
			this.preferences.line_separator = lineSeparator;
		} else {
			this.preferences.line_separator = Util.LINE_SEPARATOR;
		}
		this.preferences.initial_indentation_level = indentationLevel;

		this.newCodeFormatter2 = new CodeFormatterVisitor(this.preferences, this.options, offset, length, (CompilationUnit) node.getRoot(), apiLevel);
		
		return this.newCodeFormatter2.format(source, (descent.core.dom.Expression) node);
	}

	private TextEdit formatStatements(String source, int indentationLevel, String lineSeparator, int offset, int length) {
		ASTParser parser = ASTParser.newParser(apiLevel);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setCompilerOptions(getDefaultCompilerOptions());
		parser.setResolveBindings(false);
		parser.setUnitName(Util.EMPTY_STRING);
		descent.core.dom.ASTNode node = parser.createAST(null);
		if (lineSeparator != null) {
			this.preferences.line_separator = lineSeparator;
		} else {
			this.preferences.line_separator = Util.LINE_SEPARATOR;
		}
		this.preferences.initial_indentation_level = indentationLevel;

		this.newCodeFormatter2 = new CodeFormatterVisitor(this.preferences, this.options, offset, length, (CompilationUnit) node.getRoot(), apiLevel);
			
		return this.newCodeFormatter2.format(source, (Block) node);
	}

	public String getDebugOutput() {
		return this.newCodeFormatter2.scribe.toString();
	}

	private Map getDefaultCompilerOptions() {
		if (this.defaultCompilerOptions ==  null) {
			Map optionsMap = new HashMap(30);
			optionsMap.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.DO_NOT_GENERATE); 
			optionsMap.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.DO_NOT_GENERATE);
			optionsMap.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.DO_NOT_GENERATE);
			optionsMap.put(CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.PRESERVE);
			optionsMap.put(CompilerOptions.OPTION_DocCommentSupport, CompilerOptions.DISABLED); 
			optionsMap.put(CompilerOptions.OPTION_ReportMethodWithConstructorName, CompilerOptions.IGNORE); 
			optionsMap.put(CompilerOptions.OPTION_ReportOverridingPackageDefaultMethod, CompilerOptions.IGNORE);
			//optionsMap.put(CompilerOptions.OPTION_ReportOverridingMethodWithoutSuperInvocation, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportDeprecationInDeprecatedCode, CompilerOptions.DISABLED); 
			optionsMap.put(CompilerOptions.OPTION_ReportDeprecationWhenOverridingDeprecatedMethod, CompilerOptions.DISABLED); 
			optionsMap.put(CompilerOptions.OPTION_ReportHiddenCatchBlock, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportUnusedLocal, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportUnusedParameter, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportSyntheticAccessEmulation, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportNoEffectAssignment, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportNonExternalizedStringLiteral, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportNoImplicitStringConversion, CompilerOptions.IGNORE); 
			optionsMap.put(CompilerOptions.OPTION_ReportNonStaticAccessToStatic, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportIndirectStaticAccess, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportIncompatibleNonInheritedInterfaceMethod, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportUnusedPrivateMember, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportLocalVariableHiding, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportFieldHiding, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportPossibleAccidentalBooleanAssignment, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportEmptyStatement, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportAssertIdentifier, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportEnumIdentifier, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportUndocumentedEmptyBlock, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportUnnecessaryTypeCheck, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportInvalidJavadoc, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsVisibility, CompilerOptions.PUBLIC);
			optionsMap.put(CompilerOptions.OPTION_ReportInvalidJavadocTags, CompilerOptions.DISABLED);
			optionsMap.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsDeprecatedRef, CompilerOptions.DISABLED);
			optionsMap.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsNotVisibleRef, CompilerOptions.DISABLED);
			optionsMap.put(CompilerOptions.OPTION_ReportMissingJavadocTags, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportMissingJavadocTagsVisibility, CompilerOptions.PUBLIC);
			optionsMap.put(CompilerOptions.OPTION_ReportMissingJavadocTagsOverriding, CompilerOptions.DISABLED);
			optionsMap.put(CompilerOptions.OPTION_ReportMissingJavadocComments, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportMissingJavadocCommentsVisibility, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportMissingJavadocCommentsOverriding, CompilerOptions.DISABLED);
			optionsMap.put(CompilerOptions.OPTION_ReportFinallyBlockNotCompletingNormally, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportUnusedDeclaredThrownException, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_ReportUnusedDeclaredThrownExceptionWhenOverriding, CompilerOptions.DISABLED); 
			optionsMap.put(CompilerOptions.OPTION_ReportUnqualifiedFieldAccess, CompilerOptions.IGNORE);
			optionsMap.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_2_x);
			optionsMap.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_2_x); 
			optionsMap.put(CompilerOptions.OPTION_TaskTags, ""); //$NON-NLS-1$
			optionsMap.put(CompilerOptions.OPTION_TaskPriorities, ""); //$NON-NLS-1$
			optionsMap.put(CompilerOptions.OPTION_TaskCaseSensitive, CompilerOptions.DISABLED);
			optionsMap.put(CompilerOptions.OPTION_ReportUnusedParameterWhenImplementingAbstract, CompilerOptions.DISABLED); 
			optionsMap.put(CompilerOptions.OPTION_ReportUnusedParameterWhenOverridingConcrete, CompilerOptions.DISABLED); 
			optionsMap.put(CompilerOptions.OPTION_ReportSpecialParameterHidingField, CompilerOptions.DISABLED); 
			optionsMap.put(CompilerOptions.OPTION_MaxProblemPerUnit, String.valueOf(100));
			optionsMap.put(CompilerOptions.OPTION_InlineJsr, CompilerOptions.DISABLED); 
			this.defaultCompilerOptions = optionsMap;
		}
		Object sourceOption = this.options.get(CompilerOptions.OPTION_Source);
		if (sourceOption != null) {
			this.defaultCompilerOptions.put(CompilerOptions.OPTION_Source, sourceOption);
		} else {
			this.defaultCompilerOptions.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_2_x);
		}
		return this.defaultCompilerOptions;		
	}
	
}
