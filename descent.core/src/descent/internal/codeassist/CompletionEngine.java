package descent.internal.codeassist;

import java.io.File;
import java.util.Map;

import descent.core.CompletionProposal;
import descent.core.CompletionRequestor;
import descent.core.IJavaProject;
import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.internal.codeassist.complete.CompletionOnModuleDeclaration;
import descent.internal.codeassist.complete.CompletionParser;
import descent.internal.codeassist.impl.Engine;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.util.HashtableOfObject;
import descent.internal.core.SearchableEnvironment;

/**
 * This class is the entry point for source completions.
 * It contains two public APIs used to call CodeAssist on a given source with
 * a given environment, assisting position and storage (and possibly options).
 */
public class CompletionEngine extends Engine implements RelevanceConstants {
	
	public static boolean DEBUG = false;
	public static boolean PERF = false;
	
	IJavaProject javaProject;
	CompletionParser parser;
	CompletionRequestor requestor;
	
	char[] fileName = null;
	int startPosition, actualCompletionPosition, endPosition, offset;
	
	char[] source;
	char[] completionToken;
	char[] qualifiedCompletionToken;
	
	public HashtableOfObject typeCache;
	
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
			CompletionParser parser = new CompletionParser(AST.D2, source);
			parser.cursorLocation = completionPosition;
			
			parser.parseModuleObj();
			ASTDmdNode assistNode = parser.getAssistNode();
			if (assistNode == null) {
				return;
			}
			
			if (assistNode instanceof CompletionOnModuleDeclaration) {
				CompletionOnModuleDeclaration node = (CompletionOnModuleDeclaration) assistNode;
				
				complete(node);
			}
		} finally {
			this.requestor.endReporting();
		}
	}
	
	private void complete(CompletionOnModuleDeclaration node) {
		char[] fqn = computeFQN(node.packages, node.id);
		
		// Remove file extension
		char[] suggestedModuleName = fileName;
		int index = CharOperation.lastIndexOf('.', fileName);
		if (index != -1) {
			suggestedModuleName = CharOperation.subarray(this.fileName, 0, index);
		}
		
		// Replace file separator with .
		CharOperation.replace(suggestedModuleName, '/', '.');
		
		if (CharOperation.prefixEquals(fqn, suggestedModuleName)) {
			int start = this.actualCompletionPosition;
			if (node.packages != null)
			{
				start = node.packages.get(0).start;
			} else if (node.id != null) {
				start = node.id.start;
			}
			CompletionProposal proposal = createProposal(CompletionProposal.PACKAGE_REF, this.actualCompletionPosition);
			proposal.setCompletion(suggestedModuleName);
			proposal.setReplaceRange(start, this.actualCompletionPosition);
			proposal.setRelevance(R_EXACT_NAME);
			this.requestor.accept(proposal);
		}
	}

	private char[] computeFQN(Identifiers packages, IdentifierExp id) {
		// TODO Descent char[] optimize, don't use StringBuilder
		StringBuilder sb = new StringBuilder();
		if (packages != null) {
			for(int i = 0; i < packages.size(); i++) {
				sb.append(packages.get(i).toCharArray());
				sb.append('.');
			}
		}
		
		if (id != null) {
			sb.append(id.toCharArray());
		}
		
		char[] ret = new char[sb.length()];
		sb.getChars(0, sb.length(), ret, 0);
		return ret;
	}

	protected CompletionProposal createProposal(int kind, int completionOffset) {
		CompletionProposal proposal = CompletionProposal.create(kind, completionOffset - this.offset);
		proposal.nameLookup = this.nameEnvironment.nameLookup;
		proposal.completionEngine = this;
		return proposal;
	}

}
