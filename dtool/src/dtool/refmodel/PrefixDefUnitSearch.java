package dtool.refmodel;

import static melnorme.miscutil.Assert.assertFail;
import static melnorme.miscutil.Assert.assertNotNull;

import java.util.Iterator;

import org.eclipse.dltk.core.ISourceModule;

import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.ast.TokenUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.Expression;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.descentadapter.DescentASTConverter;

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
			IScopeNode refScope, int refOffset,
			IDefUnitMatchAccepter defUnitAccepter) {
		super(refScope, refOffset);
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
		if(notOccluded(defUnit))
			defUnitAccepter.accept(defUnit, searchOptions);
	}
	
	public boolean notOccluded(DefUnit newDefUnit) {
		Iterator<DefUnit> iter = defUnitAccepter.getResultsIterator();
		while (iter.hasNext()) {
			DefUnit	defunit = iter.next();
			if(defunit.toStringAsElement().equals(newDefUnit.toStringAsElement()))
				return false;
		}
		return true;
	};

	public static interface IDefUnitMatchAccepter {
		void accept(DefUnit defUnit, PrefixSearchOptions searchOptions);

		Iterator<DefUnit> getResultsIterator();
	}

	public static String doCompletionSearch(final int offset,
			ISourceModule moduleUnit, String docstr,
			CompletionSession session,
			IDefUnitMatchAccepter defUnitAccepter) {

		if(session.invokeNode != null) {
			// Give no results if durring a code completion session the
			// cursor goes behind the invoked reference node.
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
		
		
		ASTNeoNode node = ASTNodeFinder.findElement(neoModule, offset);
		assertNotNull(node);
		session.invokeNode = node;
		PrefixSearchOptions searchOptions = new PrefixSearchOptions();
		IScopeNode refScope = NodeUtil.getScopeNode(node);
		PrefixDefUnitSearch search;
		search = new PrefixDefUnitSearch(searchOptions,
				refScope, offset, defUnitAccepter);

		
		if(node instanceof NamedReference)  {
			NamedReference namedRef = (NamedReference) node;
			
			if(node instanceof RefIdentifier) {
				RefIdentifier refIdent = (RefIdentifier) node;
				if(!parserAdapter.isQualifiedDotFixSearch) {
					setupPrefixedSearch(searchOptions, offset, 
							refIdent.getOffset(), refIdent.name);
				}
			} else if(node instanceof CommonRefQualified) {
				//CommonRefQualified refQual = (CommonRefQualified) node;

				if(lastToken.value != TOK.TOKdot)
					return "No completions available (Invalid qualified ref context)";
			} else if(node instanceof RefTemplateInstance) {
				RefTemplateInstance refTpl = (RefTemplateInstance) node;
				int idEndPos = refTpl.getStartPos() + refTpl.name.length();
				
				if(offset <= idEndPos) {
					setupPrefixedSearch(searchOptions, offset, 
							refTpl.getOffset(), refTpl.name);
				} else if(lastToken.value == TOK.TOKnot) {
					return "Invalid Context:" + lastToken;
				}
			} else if(node instanceof RefModule) {
				RefModule refMod = (RefModule) node;
				setupPrefixedSearch(searchOptions, offset, 
						refMod.getOffset(), refMod.toStringAsElement());
			} else if (node instanceof RefImportSelection) {
				RefImportSelection refImpSel = (RefImportSelection) node;
				setupPrefixedSearch(searchOptions, offset, 
						refImpSel.getOffset(), refImpSel.name);
			} else {
				assertFail();
			}
			
			namedRef.doSearch(search);
			return null;
		} else if(node instanceof Reference) {
			return "Don't know how to complete for node: "+node+"(TODO)";
		}
		
		// Since picked node was not a reference,
		// determine appropriate scope search parameters
		IScopeNode scope;
		while(true) {
			assertNotNull(node);
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

	private static void setupPrefixedSearch(PrefixSearchOptions searchOptions, 
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
