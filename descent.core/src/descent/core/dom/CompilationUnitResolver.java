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
package descent.core.dom;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.IProblemRequestor;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.IProblem;
import descent.core.dom.DefaultBindingResolver.BindingTables;
import descent.internal.compiler.ICompilerRequestor;
import descent.internal.compiler.IErrorHandlingPolicy;
import descent.internal.compiler.IProblemFactory;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.lookup.DescentModuleFinder;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.core.CancelableNameEnvironment;
import descent.internal.core.CompilerConfiguration;
import descent.internal.core.JavaProject;
import descent.internal.core.util.Util;

public class CompilationUnitResolver extends descent.internal.compiler.Compiler {
	
	private final static boolean RESOLVE = true;
	private final static boolean STATS = true;
	
	public static class ParseResult {
		public Module module;
		public PublicScanner scanner;
		public SemanticContext context;
		public ParseResult(Module module, PublicScanner scanner) {
			this.module = module;
			this.scanner = scanner;
		}
	}
	
	boolean hasCompilationAborted;
	
	//private IProgressMonitor monitor;
	
	/**
	 * Answer a new CompilationUnitVisitor using the given name environment and compiler options.
	 * The environment and options will be in effect for the lifetime of the compiler.
	 * When the compiler is run, compilation results are sent to the given requestor.
	 *
	 *  @param environment descent.internal.compiler.api.env.INameEnvironment
	 *      Environment used by the compiler in order to resolve type and package
	 *      names. The name environment implements the actual connection of the compiler
	 *      to the outside world (for example, in batch mode the name environment is performing
	 *      pure file accesses, reuse previous build state or connection to repositories).
	 *      Note: the name environment is responsible for implementing the actual classpath
	 *            rules.
	 *
	 *  @param policy descent.internal.compiler.api.problem.IErrorHandlingPolicy
	 *      Configurable part for problem handling, allowing the compiler client to
	 *      specify the rules for handling problems (stop on first error or accumulate
	 *      them all) and at the same time perform some actions such as opening a dialog
	 *      in UI when compiling interactively.
	 *      @see descent.internal.compiler.DefaultErrorHandlingPolicies
	 * 
	 *	@param compilerOptions The compiler options to use for the resolution.
	 *      
	 *  @param requestor descent.internal.compiler.api.ICompilerRequestor
	 *      Component which will receive and persist all compilation results and is intended
	 *      to consume them as they are produced. Typically, in a batch compiler, it is 
	 *      responsible for writing out the actual .class files to the file system.
	 *      @see descent.internal.compiler.CompilationResult
	 *
	 *  @param problemFactory descent.internal.compiler.api.problem.IProblemFactory
	 *      Factory used inside the compiler to create problem descriptors. It allows the
	 *      compiler client to supply its own representation of compilation problems in
	 *      order to avoid object conversions. Note that the factory is not supposed
	 *      to accumulate the created problems, the compiler will gather them all and hand
	 *      them back as part of the compilation unit result.
	 */
	public CompilationUnitResolver(
		INameEnvironment environment,
		IErrorHandlingPolicy policy,
		CompilerOptions compilerOptions,
		ICompilerRequestor requestor,
		IProblemFactory problemFactory,
		IProgressMonitor monitor) {

		super(environment, policy, compilerOptions, requestor, problemFactory);
		this.hasCompilationAborted = false;
		//this.monitor =monitor;
	}
	
	public static ParseResult parse(int apiLevel,
			descent.internal.compiler.env.ICompilationUnit sourceUnit,
			Map options, 
			boolean statementsRecovery,
			boolean diet) {
		
		return parse(apiLevel, sourceUnit.getContents(), sourceUnit.getFileName(), options, statementsRecovery, diet);
	}
	
	public static ParseResult parse(int apiLevel,
			char[] source,
			char[] filename, 
			Map options, 
			boolean statementsRecovery,
			boolean diet) {
		
		descent.internal.compiler.parser.Parser parser;
		if (options != null) {
			String taskTags = (String) options.get(JavaCore.COMPILER_TASK_TAGS);
			if (taskTags != null) {
				parser = new Parser(apiLevel, source, 0, source.length, 
						Util.toCharArrays(taskTags.split(",")),
						Util.toCharArrays(((String) options.get(JavaCore.COMPILER_TASK_PRIORITIES)).split(",")),
						JavaCore.ENABLED.equals(options.get(JavaCore.COMPILER_TASK_CASE_SENSITIVE)),
						filename
						);
			} else {
				parser = new Parser(apiLevel, source, 0, source.length, filename);
			}
		} else {
			parser = new Parser(apiLevel, source, 0, source.length, filename);
		}
		
		parser.diet = diet;
		
		PublicScanner scanner = new PublicScanner(true, true, true, true, apiLevel);
		scanner.setLexerAndSource(parser, source);
		
		Module module = parser.parseModuleObj();
		module.setSourceRange(0, source.length);
		
		return new ParseResult(module, scanner);
	}
	
	public static ParseResult resolve(
			int apiLevel,
			descent.internal.compiler.env.ICompilationUnit sourceUnit,
			IJavaProject javaProject,
			Map options,
			WorkingCopyOwner owner,
			boolean statementsRecovery,
			IProgressMonitor monitor) throws JavaModelException {
		
		ParseResult result = parse(apiLevel, sourceUnit, options, statementsRecovery, false);
		result.module.moduleName = sourceUnit.getFullyQualifiedName();
		result.context = resolve(result.module, javaProject, owner);
		return result;
	}
	
	public static SemanticContext resolve(
			final Module module, 
			final IJavaProject project,
			final WorkingCopyOwner owner) 
		throws JavaModelException {
		
		CompilerConfiguration config = new CompilerConfiguration();
		
		Global global = prepareForSemantic(project, config);
		return resolve(module, project, global, owner, config);
	}
	
	private static SemanticContext resolve(
			final Module module, 
			final IJavaProject project,
			final Global global,
			final WorkingCopyOwner owner,
			final CompilerConfiguration config) throws JavaModelException {
		
		long time = System.currentTimeMillis();
		
		IProblemRequestor problemRequestor = new IProblemRequestor() {
			public void acceptProblem(IProblem problem) {
				if (config.semanticAnalysisLevel == 2) {
					module.problems.add(problem);
				} else if (config.semanticAnalysisLevel == 1) {
					// For now, leave out some errors that probably have bugs, but
					// leave some others which are common and useful.
					switch(problem.getID()) {
					case IProblem.SymbolConflictsWithSymbolAtLocation:
//					case IProblem.SymbolAtLocationConflictsWithSymbolAtLocation:
					case IProblem.PropertyCanNotBeRedefined:
					case IProblem.CircularDefinition:
					case IProblem.EnumValueOverflow:
					case IProblem.EnumMustHaveAtLeastOneMember:
					case IProblem.EnumBaseTypeMustBeOfIntegralType:
					case IProblem.ThisNotInClassOrStruct:
					case IProblem.ThisOnlyAllowedInNonStaticMemberFunctions:
					case IProblem.SuperOnlyAllowedInNonStaticMemberFunctions:
					case IProblem.SuperNotInClass:
					case IProblem.ClassHasNoSuper:
					case IProblem.MemberIsPrivate:
					case IProblem.ExternSymbolsCannotHaveInitializers:
					case IProblem.DuplicatedInterfaceInheritance:
					case IProblem.FieldsNotAllowedInInterfaces:
					case IProblem.UndefinedIdentifier:
					case IProblem.NewAllocatorsOnlyForClassOrStruct:
					case IProblem.DeleteDeallocatorsOnlyForClassOrStruct:
					case IProblem.ConstructorsOnlyForClass:
					case IProblem.DestructorsOnlyForClass:
					case IProblem.InvariantsOnlyForClassStructUnion:
					case IProblem.CannotOverrideFinalFunctions:
					case IProblem.OverrideOnlyForClassMemberFunctions:
					case IProblem.FunctionMustReturnAResultOfType:
					case IProblem.MoreThanOneInvariant:
					case IProblem.ParameterMultiplyDefined:
					case IProblem.SymbolNotFound:
//					case IProblem.StatementIsNotReachable:
					case IProblem.VoidFunctionsHaveNoResult:
					case IProblem.ReturnStatementsCannotBeInContracts:
					case IProblem.NotAnAggregateType:
						// case IProblem.UnrecognizedPragma:
					case IProblem.AnonCanOnlyBePartOfAnAggregate:
					case IProblem.PragmaIsMissingClosingSemicolon:
					case IProblem.UndefinedType:
					case IProblem.FunctionsCannotBeConstOrAuto:
					case IProblem.NonVirtualFunctionsCannotBeAbstract:
					case IProblem.ModifierCannotBeAppliedToVariables:
					case IProblem.StructsCannotBeAbstract:
					case IProblem.UnionsCannotBeAbstract:
					case IProblem.AliasCannotBeConst:
					case IProblem.IllegalMainParameters:
					case IProblem.MustReturnIntOrVoidFromMainFunction:
					case IProblem.AtLeastOneArgumentOfTypeExpected:
					case IProblem.FirstArgumentMustBeOfType:
					case IProblem.StringExpectedForPragmaMsg:
					case IProblem.LibPragmaMustRecieveASingleArgumentOfTypeString:
					case IProblem.StringExpectedForPragmaLib:
					case IProblem.CannotHaveOutOrInoutParameterOfTypeStaticArray:
					case IProblem.FunctionsCannotReturnStaticArrays:
					case IProblem.UnrecongnizedTrait:
//					case IProblem.UndefinedProperty:
//					case IProblem.DeprecatedProperty:
					case IProblem.FileNameMustBeString:
					case IProblem.FileImportsMustBeSpecified:
					case IProblem.VersionIdentifierReserved:
					case IProblem.CannotPutCatchStatementInsideFinallyBlock:
					case IProblem.BreakIsNotInsideALoopOrSwitch:
					case IProblem.CaseIsNotInSwitch:
					case IProblem.VersionDeclarationMustBeAtModuleLevel:
					case IProblem.DebugDeclarationMustBeAtModuleLevel:
					case IProblem.GotoCaseNotInSwitch:
					case IProblem.GotoDefaultNotInSwitch:
					case IProblem.LazyVariablesCannotBeLvalues:
					case IProblem.DivisionByZero:
					case IProblem.DefaultNotInSwitch:
					case IProblem.SwitchAlreadyHasDefault:
					case IProblem.ContinueNotInLoop:
					case IProblem.ForeachIndexCannotBeRef:
					case IProblem.IncompatibleParameterStorageClass:
					case IProblem.OutCannotBeFinal:
					case IProblem.ScopeCannotBeRefOrOut:
					case IProblem.SymbolNotDefined:
					case IProblem.SymbolNotATemplate:
					case IProblem.CannotDeleteType:
					case IProblem.CannotAssignToStaticArray:
					case IProblem.CannotChangeReferenceToStaticArray:
					case IProblem.CannotModifyParameterInContract:
					case IProblem.TooManyInitializers:
					case IProblem.CannotHaveArrayOfType:
					case IProblem.DeclarationIsAlreadyDefined:
					case IProblem.DeclarationIsAlreadyDefinedInAnotherScope:
					case IProblem.VersionDefinedAfterUse:
					case IProblem.DebugDefinedAfterUse:
//					case IProblem.MemberIsNotAccessible:
//					case IProblem.SymbolIsNotAccessible:
					case IProblem.EnclosingLabelForBreakNotFound:
					case IProblem.EnclosingLabelForContinueNotFound:
					case IProblem.DeleteAAKeyDeprecated:
					case IProblem.SymbolIsDeprecated:
					case IProblem.ShadowingDeclarationIsDeprecated:
					case IProblem.ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies:
					case IProblem.CannotReturnExpressionFromConstructor:
					case IProblem.CaseNotFound:
					case IProblem.ImportCannotBeResolved:
					case IProblem.FunctionsCannotReturnAFunction:
					case IProblem.FunctionsCannotReturnATuple:
					case IProblem.FunctionsCannotReturnAuto:
					case IProblem.VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter:
//					case IProblem.DuplicateCaseInSwitchStatement:
					case IProblem.SpecialMemberFunctionsNotAllowedForSymbol:
					case IProblem.SpecialFunctionsNotAllowedInInterface:
					case IProblem.StaticIfConditionalCannotBeAtGlobalScope:
					case IProblem.CannotBreakOutOfFinallyBlock:
					case IProblem.LabelHasNoBreak:
					case IProblem.CannotGotoInOrOutOfFinallyBlock:
					case IProblem.ForeachKeyTypeMustBeIntOrUint:
					case IProblem.ForeachKeyCannotBeOutOrRef:
					case IProblem.MissingOrCurruptObjectDotD:
					case IProblem.CannotContinueOutOfFinallyBlock:
					case IProblem.NoReturnAtEndOfFunction:
					case IProblem.NoCaseStatementFollowingGoto:
					case IProblem.SwitchStatementHasNoDefault:
					case IProblem.ThrowStatementsCannotBeInContracts:
					case IProblem.ImportNotFound:
					case IProblem.LabelIsAlreadyDefined:
						module.problems.add(problem);
						break;
					}
				}
			}
			public void beginReporting() {
			}
			public void endReporting() {
			}
			public boolean isActive() {
				return true;
			}
		};
		
		SemanticContext context = new SemanticContext(
				problemRequestor, 
				module, 
				project,
				new DescentModuleFinder(new CancelableNameEnvironment((JavaProject) project, owner, null), config),
				global);
		
		if (!RESOLVE) return context;
		
		// First adhere to DMD: if there are syntaxis errors, don't do
		// semantic analysis.
		// COMMENTED THIS, since when there are syntax errors we would
		// like to have a better recovery
//		if (module.problems != null && module.problems.size() > 0) {
//			return context;
//		}
		
		try {
			module.semantic(context);
		} catch (Throwable t) {
			t.printStackTrace();
			Util.log(t);
		}
		
		if (STATS) {
			time = System.currentTimeMillis() - time;
			if (time != 0) {
				System.out.println("Resolve of " + module.moduleName + " took " + time + " miliseconds to complete.");
			}
		}
		
		return context;
	}
	
	private static Global prepareForSemantic(IJavaProject project, CompilerConfiguration config) {
		Global global = new Global();
		try {
			for(IPackageFragmentRoot root : project.getAllPackageFragmentRoots()) {
				if (root.getResource() == null) {
					global.path.add(root.getPath().toOSString());
				} else {
					global.path.add(root.getResource().getLocation().toOSString());
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		global.params.versionlevel = config.versionLevel;
		global.params.debuglevel = config.debugLevel;
		addIdentifiers(config.versionIdentifiers, global.params.versionids);
		addIdentifiers(config.debugIdentifiers, global.params.debugids);
		global.params.useDeprecated = config.useDeprecated;
		global.params.warnings = config.warnings;
		
		return global;
	}
	
	private static void addIdentifiers(HashtableOfCharArrayAndObject hash, List<char[]> list) {
		for(char[] key : hash.keys()) {
			if (key != null) {
				list.add(key);
			}
		}
	}
	
	public static CompilationUnit convert(AST ast, ParseResult parseResult, IJavaProject project, ICompilationUnit unit, WorkingCopyOwner owner, IProgressMonitor monitor) {
		int savedDefaultNodeFlag = ast.getDefaultNodeFlag();
		ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
		
		ASTConverter converter = new ASTConverter(project != null, monitor);
		converter.setAST(ast);
		if (project != null) {
			BindingTables tables = new BindingTables();
			converter.ast.setBindingResolver(new DefaultBindingResolver(project, parseResult.context, owner, tables));
		} else {
			converter.ast.setBindingResolver(new BindingResolver());
		}
		CompilationUnit result = converter.convert(parseResult.module, unit);
		result.setLineEndTable(parseResult.module.lineEnds);
		result.problems = parseResult.module.problems;
		result.initCommentMapper(parseResult.scanner);
		
		ast.setOriginalModificationCount(ast.modificationCount());
		ast.setDefaultNodeFlag(savedDefaultNodeFlag);
		
		return result;
	}

	public static ASTNode convert(AST ast, descent.internal.compiler.parser.Initializer initializer) {
		int savedDefaultNodeFlag = ast.getDefaultNodeFlag();
		ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
		
		ASTConverter converter = new ASTConverter(false, null);
		converter.setAST(ast);
		Initializer init = converter.convert(initializer);
		
		ast.setOriginalModificationCount(ast.modificationCount());
		ast.setDefaultNodeFlag(savedDefaultNodeFlag);
		
		return init;
	}
	
	public static ASTNode convert(AST ast, descent.internal.compiler.parser.Expression expression) {
		int savedDefaultNodeFlag = ast.getDefaultNodeFlag();
		ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
		
		ASTConverter converter = new ASTConverter(false, null);
		converter.setAST(ast);
		Expression exp = converter.convert(expression);
		
		ast.setOriginalModificationCount(ast.modificationCount());
		ast.setDefaultNodeFlag(savedDefaultNodeFlag);
		
		return exp;
	}
	
	public static ASTNode convert(AST ast, descent.internal.compiler.parser.Statement statement) {
		int savedDefaultNodeFlag = ast.getDefaultNodeFlag();
		ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
		
		ASTConverter converter = new ASTConverter(false, null);
		converter.setAST(ast);
		Statement stm = converter.convert(statement);
		
		ast.setOriginalModificationCount(ast.modificationCount());
		ast.setDefaultNodeFlag(savedDefaultNodeFlag);
		
		return stm;
	}

}
