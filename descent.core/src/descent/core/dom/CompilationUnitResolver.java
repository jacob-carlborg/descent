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

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.internal.compiler.ICompilerRequestor;
import descent.internal.compiler.IErrorHandlingPolicy;
import descent.internal.compiler.IProblemFactory;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.impl.CompilerOptions;

class CompilationUnitResolver extends descent.internal.compiler.Compiler {
	
	boolean hasCompilationAborted;
	
	private IProgressMonitor monitor;
	
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
		this.monitor =monitor;
	}
	
	public static CompilationUnit parse(int apiLevel,
			descent.internal.compiler.env.ICompilationUnit sourceUnit, 
			Map options, 
			boolean statementsRecovery) {
		
		AST ast = AST.newAST(apiLevel);
		
		// Mark all nodes created by the parser as originals
		int savedDefaultNodeFlag = ast.getDefaultNodeFlag();
		ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
		ast.internalParserMode = true;
		
		char[] source = sourceUnit.getContents();
		Parser parser = new Parser(ast, source, 0, source.length);
		
		PublicScanner scanner = new PublicScanner(true, true, true, true, ast.apiLevel);
		scanner.setLexerAndSource(parser, source);
		
		List<Declaration> declDefs = parser.parseModule();
		
		CompilationUnit result = parser.compilationUnit;		
		if (declDefs != null) {
			result.declarations().addAll(declDefs);
		}
		result.setSourceRange(0, source.length);
		result.setCommentTable(parser.comments.toArray(new Comment[parser.comments.size()]));
		result.setPragmaTable(parser.pragmas.toArray(new Pragma[parser.pragmas.size()]));
		result.setLineEndTable(parser.getLineEnds());
		result.initCommentMapper(scanner);		
		result.problems = parser.problems;
		
		ast.setOriginalModificationCount(ast.modificationCount());
		ast.setDefaultNodeFlag(savedDefaultNodeFlag);
		ast.internalParserMode = false;
		
		return result;
	}
	
	public static CompilationUnit resolve(int apiLevel,
			descent.internal.compiler.env.ICompilationUnit sourceUnit,
			IJavaProject javaProject,
			Map options,
			WorkingCopyOwner owner,
			boolean statementsRecovery,
			IProgressMonitor monitor) throws JavaModelException {
		
		CompilationUnit unit = parse(apiLevel, sourceUnit, options, statementsRecovery);
		
		//CancelableNameEnvironment environment = null;
		//CancelableProblemFactory problemFactory = null;
		
		SemanticComputer computer = new SemanticComputer(owner, new DefaultBindingResolver.BindingTables());
		unit.accept(computer);
		
		return unit;
	}

}
