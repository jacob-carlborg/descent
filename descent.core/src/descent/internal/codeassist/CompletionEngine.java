package descent.internal.codeassist;

import java.util.Arrays;
import java.util.Map;

import descent.core.CompletionProposal;
import descent.core.CompletionRequestor;
import descent.core.IJavaProject;
import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.internal.codeassist.complete.CompletionOnArgumentName;
import descent.internal.codeassist.complete.CompletionOnImport;
import descent.internal.codeassist.complete.CompletionOnModuleDeclaration;
import descent.internal.codeassist.complete.CompletionParser;
import descent.internal.codeassist.impl.Engine;
import descent.internal.compiler.env.AccessRestriction;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.util.HashtableOfObject;
import descent.internal.core.SearchableEnvironment;

/**
 * This class is the entry point for source completions.
 * It contains two public APIs used to call CodeAssist on a given source with
 * a given environment, assisting position and storage (and possibly options).
 */
public class CompletionEngine extends Engine 
	implements RelevanceConstants, ISearchRequestor {
	
	public static boolean DEBUG = false;
	public static boolean PERF = false;
	
	IJavaProject javaProject;
	CompletionParser parser;
	CompletionRequestor requestor;
	
	char[] fileName = null;
	char[] sourceUnitFqn = null;
	int startPosition, actualCompletionPosition, endPosition, offset;
	
	char[] source;
	char[] completionToken;
	char[] qualifiedCompletionToken;
	
	public HashtableOfObject typeCache;
	
	HashtableOfObject knownCompilationUnits = new HashtableOfObject(10);
	
	/**
	 * The CompletionEngine is responsible for computing source completions.
	 *
	 * It requires a searchable name environment, which supports some
	 * specific search APIs, and a requestor to feed back the results to a UI.
	 *
	 *  @param nameEnvironment descent.internal.codeassist.ISearchableNameEnvironment
	 *      used to resolve type/package references and search for types/packages
	 *      based on partial names.
	 *
	 *  @param requestor descent.internal.codeassist.ICompletionRequestor
	 *      since the engine might produce answers of various forms, the engine 
	 *      is associated with a requestor able to accept all possible completions.
	 *
	 *  @param settings java.util.Map
	 *		set of options used to configure the code assist engine.
	 */
	public CompletionEngine(
			SearchableEnvironment nameEnvironment,
			CompletionRequestor requestor,
			Map settings,
			IJavaProject javaProject) {
		super(settings);
		this.javaProject = javaProject;
		this.requestor = requestor;
		this.nameEnvironment = nameEnvironment;
		this.typeCache = new HashtableOfObject(5);
	}

	/**
	 * Ask the engine to compute a completion at the specified position
	 * of the given compilation unit.
	 *
	 *  No return
	 *      completion results are answered through a requestor.
	 *
	 *  @param sourceUnit descent.internal.compiler.env.ICompilationUnit
	 *      the source of the current compilation unit.
	 *
	 *  @param completionPosition int
	 *      a position in the source where the completion is taking place. 
	 *      This position is relative to the source provided.
	 */
	public void complete(ICompilationUnit sourceUnit, int completionPosition, int pos) {
		if(DEBUG) {
			System.out.print("COMPLETION IN "); //$NON-NLS-1$
			System.out.print(sourceUnit.getFileName());
			System.out.print(" AT POSITION "); //$NON-NLS-1$
			System.out.println(completionPosition);
			System.out.println("COMPLETION - Source :"); //$NON-NLS-1$
			System.out.println(sourceUnit.getContents());
		}
		this.requestor.beginReporting();
		try {
			this.source = sourceUnit.getContents();	
			this.fileName = sourceUnit.getFileName();
			this.actualCompletionPosition = completionPosition; // - 1;
			this.offset = pos;
			
			
			// Get the sourceUnit's fqn
			sourceUnitFqn = fileName;
			int index = CharOperation.lastIndexOf('.', fileName);
			if (index != -1) {
				sourceUnitFqn = CharOperation.subarray(this.fileName, 0, index);
			} else {
				sourceUnitFqn = Arrays.copyOf(sourceUnitFqn, sourceUnitFqn.length);
			}
			CharOperation.replace(sourceUnitFqn, '/', '.');
			
			CompletionParser parser = new CompletionParser(AST.D2, source);
			parser.cursorLocation = completionPosition;
			
			parser.parseModuleObj();
			ASTDmdNode assistNode = parser.getAssistNode();
			if (assistNode == null) {
				return;
			}
			
			if (assistNode instanceof CompletionOnModuleDeclaration) {
				CompletionOnModuleDeclaration node = (CompletionOnModuleDeclaration) assistNode;
				completeModuleDeclaration(node);
			} else if (assistNode instanceof CompletionOnImport) {
				CompletionOnImport node = (CompletionOnImport) assistNode;
				completeImport(node);
			} else if (assistNode instanceof CompletionOnArgumentName) {
				CompletionOnArgumentName node = (CompletionOnArgumentName) assistNode;
				completeArgumentName(node);
			}
		} finally {
			this.requestor.endReporting();
		}
	}
	
	private void completeModuleDeclaration(CompletionOnModuleDeclaration node) {
		char[] fqn = node.getFQN();
		char[] fqnBeforeCursor = CharOperation.subarray(fqn, 0, this.actualCompletionPosition - node.getFqnStart());
		
		this.startPosition = node.getFqnStart();
		this.endPosition = node.getFqnEnd();
		
		// Also include the next dot, if any
		if (this.endPosition < this.source.length && this.source[this.endPosition] == '.') {
			this.endPosition++;
		}
		
		if (CharOperation.prefixEquals(fqnBeforeCursor, sourceUnitFqn)) {
			CompletionProposal proposal = createProposal(CompletionProposal.PACKAGE_REF, this.actualCompletionPosition);
			proposal.setCompletion(sourceUnitFqn);
			proposal.setReplaceRange(this.startPosition, this.endPosition);
			this.requestor.accept(proposal);
		}
	}
	
	private void completeImport(CompletionOnImport node) {
		char[] fqn = node.getFQN();
		char[] fqnBeforeCursor = CharOperation.subarray(fqn, 0, this.actualCompletionPosition - node.getFqnStart());
		
		if (fqnBeforeCursor.length == 0) {
			return;
		}
		
		this.startPosition = node.getFqnStart();
		this.endPosition = node.getFqnEnd();
		
		// Also include the next dot, if any
		if (this.endPosition < this.source.length && this.source[this.endPosition] == '.') {
			this.endPosition++;
		}
		
		nameEnvironment.findCompilationUnits(fqnBeforeCursor, this);
	}
	
	private void completeArgumentName(CompletionOnArgumentName node) {
		
	}

	protected CompletionProposal createProposal(int kind, int completionOffset) {
		CompletionProposal proposal = CompletionProposal.create(kind, completionOffset - this.offset);
		proposal.nameLookup = this.nameEnvironment.nameLookup;
		proposal.completionEngine = this;
		return proposal;
	}

	public void acceptCompilationUnit(char[] fullyQualifiedName) {
		if (knownCompilationUnits.containsKey(fullyQualifiedName)
				|| CharOperation.equals(fullyQualifiedName, this.sourceUnitFqn) ) {
			return;
		}
		
		knownCompilationUnits.put(fullyQualifiedName, this);
		
		CompletionProposal proposal = createProposal(CompletionProposal.PACKAGE_REF, this.actualCompletionPosition);
		proposal.setCompletion(fullyQualifiedName);
		proposal.setReplaceRange(this.startPosition, this.endPosition);
		this.requestor.accept(proposal);
	}

	public void acceptPackage(char[] packageName) {
		
	}

	public void acceptType(char[] packageName, char[] typeName, char[][] enclosingTypeNames, int modifiers, AccessRestriction accessRestriction) {
	}

}
