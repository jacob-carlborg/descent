package descent.internal.compiler;

import java.util.Arrays;

import descent.core.dom.AST;
import descent.core.dom.ASTParser;
import descent.core.dom.CompilationUnit;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.problem.AbortCompilation;

// TODO JDT compile: compiler
public class Compiler {

	public ASTParser parser;
	public ICompilerRequestor requestor;
	public CompilerOptions options;
	public ICompilationUnit initialUnitsToProcess;
	public CompilationUnit[] unitsToProcess;
	public CompilationResult[] resultsToProcess;
	public int parseThreshold = -1; // number of initial units parsed at once (-1: none)
	public int totalUnits; // (totalUnits-1) gives the last unit in unitToProcess

	public Compiler(INameEnvironment nameEnvironment, 
			IErrorHandlingPolicy policy, 
			CompilerOptions options, 
			final ICompilerRequestor requestor,
			IProblemFactory problemFactory) {
	
		this.options = options;
		this.requestor = requestor;
		initializeParser();
	}

	public void compile(ICompilationUnit[] sourceUnits) {
		/* TODO JDT compiler
		CompilationUnit unit = null;
		CompilationResult result = null;
		
		int i = 0;
		try {
			// build and record parsed units
			beginToCompile(sourceUnits);

			// process all units (some more could be injected in the loop by the lookup environment)
			for (; i < this.totalUnits; i++) {
				unit = unitsToProcess[i];
				result = resultsToProcess[i];
				
				process(unit, result);
				
				result.problems = unit.getProblems();
				result.problemCount = unit.getProblems().length;
				
				requestor.acceptResult(result);
			}
		} catch (AbortCompilation e) {
			this.handleInternalException(e, unit);
		} catch (Error e) {
			this.handleInternalException(e, unit, null);
			throw e; // rethrow
		} catch (RuntimeException e) {
			this.handleInternalException(e, unit, null);
			throw e; // rethrow
		} finally {
			
		}
		*/
	}
	
	protected void beginToCompile(ICompilationUnit[] sourceUnits) {
		int maxUnits = sourceUnits.length;
		totalUnits = 0;
		unitsToProcess = new CompilationUnit[maxUnits];
		resultsToProcess = new CompilationResult[maxUnits];

		// Switch the current policy and compilation result for this unit to the requested one.
		for (int i = 0; i < maxUnits; i++) {
			CompilationUnit parsedUnit;
			CompilationResult unitResult =
				new CompilationResult(sourceUnits[i], i, maxUnits, this.options.maxProblemsPerUnit);
			try {
				parser.setSource(sourceUnits[i].getContents());
				parsedUnit = (CompilationUnit) parser.createAST(null);
				this.addCompilationUnit(parsedUnit, unitResult);
			} finally {
				sourceUnits[i] = null; // no longer hold onto the unit
			}
		}
	}
	
	protected void process(CompilationUnit unit, CompilationResult result) {
		// TODO for now, compile the file with the underlying compiler
	}
	
	protected void addCompilationUnit(
		CompilationUnit parsedUnit,
		CompilationResult unitResult) {

		// append the unit to the list of ones to process later on
		int size = unitsToProcess.length;
		if (totalUnits == size) {
			// when growing reposition units starting at position 0
			System.arraycopy(
				unitsToProcess,
				0,
				(unitsToProcess = new CompilationUnit[size * 2]),
				0,
				totalUnits);
			System.arraycopy(
					resultsToProcess,
					0,
					(resultsToProcess = new CompilationResult[size * 2]),
					0,
					totalUnits);
		}
		unitsToProcess[totalUnits] = parsedUnit;
		resultsToProcess[totalUnits] = unitResult;
		totalUnits++;
	}
	
	/*
	 * Compiler crash recovery in case of unexpected runtime exceptions
	 */
	protected void handleInternalException(
		Throwable internalException,
		CompilationUnit unit,
		CompilationResult result) {
		/* dump a stack trace to the console */
		internalException.printStackTrace();
	}

	/*
	 * Compiler recovery in case of internal AbortCompilation event
	 */
	protected void handleInternalException(
		AbortCompilation abortException,
		CompilationUnit unit) {
		/* dump a stack trace to the console */
		abortException.printStackTrace();
	}
	
	public void initializeParser() {
		this.parser = ASTParser.newParser(AST.D2);
		this.parser.setCompilerOptions(options.getMap());
		this.parser.setKind(ASTParser.K_COMPILATION_UNIT);
	}

}
