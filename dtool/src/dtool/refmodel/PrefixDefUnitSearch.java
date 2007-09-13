package dtool.refmodel;

import static melnorme.miscutil.Assert.assertFail;

import org.eclipse.dltk.core.ISourceModule;

import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.ast.TokenUtil;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNodeFinder;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.expressions.Expression;
import dtool.dom.references.CommonRefQualified;
import dtool.dom.references.CommonRefSingle;
import dtool.dom.references.NamedReference;
import dtool.dom.references.RefIdentifier;
import dtool.dom.references.RefImportSelection;
import dtool.dom.references.RefModule;
import dtool.dom.references.RefTemplateInstance;
import dtool.dom.references.Reference;

/** Class that does a scoped name lookup for matches that start with 
 * a given prefix name. 
 * TODO: Matches with the same name as matches in a scope with higher 
 * priority are not added.
 */
public class PrefixDefUnitSearch extends CommonDefUnitSearch {
	
	public static class CompletionSession {
		public String errorMsg;
		public ASTNeoNode invokeNode;
	}

	public PrefixSearchOptions searchOptions;
	private IDefUnitMatchAccepter defUnitAccepter;

	public PrefixDefUnitSearch(PrefixSearchOptions searchOptions,
			IDefUnitMatchAccepter defUnitAccepter) {
		super(null);
		this.searchOptions = searchOptions;
		this.defUnitAccepter = defUnitAccepter;
	}
	
	@Override
	public boolean matches(DefUnit defUnit) {
		return matchesName(defUnit.getName());
	}
	
	@Override
	public boolean matchesName(String defName) {
		return defName.startsWith(searchOptions.searchPrefix);
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
		void accept(DefUnit defUnit, PrefixSearchOptions searchOptions);
	}

	public static String doCompletionSearch(final int offset,
			ISourceModule moduleUnit, String docstr,
			CompletionSession session,
			IDefUnitMatchAccepter defUnitAccepter) {

		if(session.invokeNode != null) {
			if(offset < session.invokeNode.getOffset()) 
				return null; // return without doing matches
		}
		
		Token tokenList = ParserAdapter.tokenizeSource(docstr);
		
		// : Find last non-white token before offset
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
		
		// : Parse source and do syntax error recovery
		ParserAdapter parserAdapter = ParserAdapter.parseSource(docstr);
		parserAdapter.recoverForCompletion(docstr, offset, lastToken);

		Module neoModule = DescentASTConverter.convertModule(parserAdapter.mod);
		neoModule.setModuleUnit(moduleUnit);
		
		/* ============================================== */
		// : Do actual completion search
		
		PrefixSearchOptions searchOptions = new PrefixSearchOptions();
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(searchOptions, defUnitAccepter);
		
		ASTNeoNode node = ASTNodeFinder.findElement(neoModule, offset);
		session.invokeNode = node;
		search.refScope = NodeUtil.getScopeNode(node);
		
		if(node instanceof NamedReference)  {
			NamedReference namedRef = (NamedReference) node;
			
			if(node instanceof RefIdentifier) {
				RefIdentifier refIdent = (RefIdentifier) node;
				if(!parserAdapter.isQualifiedDotFixSearch) {
					setupPrefixedSearch(searchOptions, offset, refIdent);
				}
			} else if(node instanceof CommonRefQualified) {
				//CommonRefQualified refQual = (CommonRefQualified) node;

				if(lastToken.value != TOK.TOKdot)
					return "No completions available (Invalid qualified ref context)";
			} else if(node instanceof RefTemplateInstance) {
				RefTemplateInstance refTpl = (RefTemplateInstance) node;
				int idEndPos = refTpl.getStartPos() + refTpl.name.length();
				
				if(offset <= idEndPos) {
					setupPrefixedSearch(searchOptions, offset, refTpl);
				} else if(lastToken.value == TOK.TOKnot) {
					return "Invalid Context:" + lastToken;
				}
			} else if(node instanceof RefModule) {
				RefModule refMod = (RefModule) node;
				setupPrefixedSearch2(searchOptions, offset, refMod.getOffset(), refMod.toStringAsElement());
			} else if (node instanceof RefImportSelection) {
				RefImportSelection refImpSel = (RefImportSelection) node;
				setupPrefixedSearch2(searchOptions, offset, refImpSel.getOffset(), refImpSel.name);
			} else {
				assertFail();
			}
			
			namedRef.doSearch(search);
			return null;
		} else if(node instanceof Reference) {
			return "Don't know how to complete for node: "+node+"(TODO)";
		}
		
		// Determine a scope for an unprefix search
		IScopeNode scope;
		while(true) {
			scope = isValidCompletionScope(node);
			if(scope != null)
				break;
			if(offset == node.getStartPos() || offset == node.getEndPos() ) {
				node = node.getParent();
				session.invokeNode = node;
			} else
				return "No completions available";
		}
		
		ReferenceResolver.findDefUnitInExtendedScope(scope, search);
		return null;
	}

	@Deprecated
	private static void setupPrefixedSearch(PrefixSearchOptions searchOptions, 
			final int offset, CommonRefSingle refTpl) {
		int prefixLen = offset - refTpl.getOffset(); //get *Name* offset
		searchOptions.prefixLen = prefixLen;
		searchOptions.rplLen = refTpl.name.length() - prefixLen;
		searchOptions.searchPrefix = refTpl.name.substring(0, prefixLen);
	}
	
	private static void setupPrefixedSearch2(PrefixSearchOptions searchOptions, 
			final int offset, int nameOffset, String name) {
		int prefixLen = offset - nameOffset; 
		searchOptions.prefixLen = prefixLen;
		searchOptions.rplLen = name.length() - prefixLen;
		searchOptions.searchPrefix = name.substring(0, prefixLen);
	}


	private static IScopeNode isValidCompletionScope(ASTNeoNode node) {
		if(node instanceof IScopeNode) {
			return (IScopeNode) node;
		} else if(node instanceof Expression) {
			return NodeUtil.getOuterScope(node);
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
