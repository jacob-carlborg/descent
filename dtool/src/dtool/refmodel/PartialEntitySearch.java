package dtool.refmodel;

import descent.core.domX.ASTNode;
import descent.core.domX.TokenUtil;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNodeFinder;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.expressions.Expression;
import dtool.dom.references.CommonRefQualified;
import dtool.dom.references.RefIdentifier;
import dtool.dom.references.RefQualified;
import dtool.dom.references.Reference;
import dtool.refmodel.pluginadapters.IGenericCompilationUnit;

/** Class that does a scoped name lookup for matches that start with 
 * a given prefix name. 
 * TODO: Matches with the same name as matches in a scope with higher 
 * priority are not added.
 */
public class PartialEntitySearch extends CommonDefUnitSearch {
	
	public static class CompletionSession {
		public String errorMsg;
		public ASTNode invokeNode;
	}

	public PartialSearchOptions searchOptions;
	private IDefUnitMatchAccepter defUnitAccepter;

	public PartialEntitySearch(PartialSearchOptions searchOptions,
			IDefUnitMatchAccepter defUnitAccepter) {
		super(null);
		this.searchOptions = searchOptions;
		this.defUnitAccepter = defUnitAccepter;
	}
	
	@Override
	public boolean matches(DefUnit defUnit) {
		return defUnit.getName().startsWith(searchOptions.searchPrefix);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	@Override
	public void addMatch(DefUnit defUnit) {
		defUnitAccepter.accept(defUnit, searchOptions);
	}
	
	public static interface IDefUnitMatchAccepter {
		void accept(DefUnit defUnit, PartialSearchOptions searchOptions);
	}

	public static String doCompletionSearch(final int offset,
			IGenericCompilationUnit cunit, String docstr,
			CompletionSession session,
			IDefUnitMatchAccepter defUnitAccepter) {
		
		System.out.println("Do Code Assist");
		if(session.invokeNode != null) {
			if(offset < session.invokeNode.getOffset()) 
				return null; // return without doing matches
		}
		
		Token tokenList = ParserAdapter.tokenizeSource(docstr);
		
		// : Find last token before offset
		Token lastToken = null;
		
		Token newtoken = tokenList;
		while (newtoken.ptr < offset) {
			if(!TokenUtil.isWhiteToken(newtoken.value))
				lastToken = newtoken;
			
			newtoken = newtoken.next;
		}
		
		// : Check if completion request is *inside* a token
		if(lastToken != null && lastToken.ptr < offset && (lastToken.ptr + lastToken.len) > offset) {
			// if so then check if it's an allowed token
			if(!isValidReferenceToken(lastToken)) {
				return "Invalid Context:" + lastToken;
			}
		}
		
		ParserAdapter parserAdapter;
		parserAdapter = ParserAdapter.parseSource(docstr);

		// : Do Syntax error recovery
		parserAdapter.recoverForCompletion(docstr, offset, lastToken);

		Module neoModule = DescentASTConverter.convertModule(parserAdapter.mod);
		neoModule.setCUnit(cunit);
		
		/* ============================================== */
		// : Do actual completion search
		
		PartialSearchOptions searchOptions = new PartialSearchOptions();
		CommonDefUnitSearch search = new PartialEntitySearch(searchOptions, defUnitAccepter);
		
		ASTNode node = ASTNodeFinder.findElement(neoModule, offset);
		session.invokeNode = node;
		
		if(node instanceof Reference)  {
			Reference ref = (Reference) node;
			search.refScope = NodeUtil.getOuterScope(ref);

			if(node instanceof RefIdentifier) {

				if(parserAdapter.isQualifiedDotFixSearch) {
					searchOptions.prefixLen = 0;
					searchOptions.rplLen = 0;
					searchOptions.searchPrefix = "";
				} else {
					RefIdentifier refIdent = (RefIdentifier) node;
					searchOptions.prefixLen = offset - refIdent.getOffset();
					searchOptions.rplLen = refIdent.getLength() - searchOptions.prefixLen;
					searchOptions.searchPrefix = refIdent.name.substring(0, searchOptions.prefixLen);
				}
				
				CommonRefQualified.doSearchForPossiblyQualifiedSingleRef(search, (RefIdentifier) node);
			} else if(node instanceof CommonRefQualified) {
				if(lastToken.value != TOK.TOKdot)
					return "No completions available (Invalid qualified ref context)";
				RefQualified.doQualifiedSearch(search, (CommonRefQualified) node);
				return null;
			} else {
				return "Don't know how to complete for node: "+node+"(TODO)";
			}
		} else if(node instanceof IScopeNode) {
			search.refScope = (IScopeNode) node;
			EntityResolver.findDefUnitInExtendedScope(search.refScope, search);
		} else if(node instanceof Expression) {
			search.refScope = NodeUtil.getOuterScope(node);
			EntityResolver.findDefUnitInExtendedScope(search.refScope, search);
		} else {
			return "No completions available";
		}
		return null;
	}


	private static boolean isValidReferenceToken(Token token) {
		return token.value == TOK.TOKidentifier
		|| token.value == TOK.TOKbool
		|| token.value == TOK.TOKchar
		|| token.value == TOK.TOKdchar
		|| token.value == TOK.TOKfloat32
		|| token.value == TOK.TOKfloat64
		|| token.value == TOK.TOKfloat80
		|| token.value == TOK.TOKint8
		|| token.value == TOK.TOKint16
		|| token.value == TOK.TOKint32
		|| token.value == TOK.TOKint64
		//|| token.value == TOK.TOKnull
		//|| token.value == TOK.TOKthis
		//|| token.value == TOK.TOKsuper
		|| token.value == TOK.TOKuns8
		|| token.value == TOK.TOKuns16
		|| token.value == TOK.TOKuns32
		|| token.value == TOK.TOKuns64
		|| token.value == TOK.TOKvoid
		|| token.value == TOK.TOKwchar
		;
	}

}
