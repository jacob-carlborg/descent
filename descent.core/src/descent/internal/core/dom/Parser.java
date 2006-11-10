package descent.internal.core.dom;

import static descent.internal.core.dom.InOut.In;
import static descent.internal.core.dom.InOut.InOut;
import static descent.internal.core.dom.InOut.Lazy;
import static descent.internal.core.dom.InOut.Out;
import static descent.internal.core.dom.LINK.LINKc;
import static descent.internal.core.dom.LINK.LINKcpp;
import static descent.internal.core.dom.LINK.LINKd;
import static descent.internal.core.dom.LINK.LINKdefault;
import static descent.internal.core.dom.LINK.LINKpascal;
import static descent.internal.core.dom.LINK.LINKwindows;
import static descent.internal.core.dom.PROT.PROTpackage;
import static descent.internal.core.dom.PROT.PROTprivate;
import static descent.internal.core.dom.PROT.PROTprotected;
import static descent.internal.core.dom.PROT.PROTpublic;
import static descent.internal.core.dom.STC.STCabstract;
import static descent.internal.core.dom.STC.STCauto;
import static descent.internal.core.dom.STC.STCconst;
import static descent.internal.core.dom.STC.STCdeprecated;
import static descent.internal.core.dom.STC.STCextern;
import static descent.internal.core.dom.STC.STCfinal;
import static descent.internal.core.dom.STC.STCoverride;
import static descent.internal.core.dom.STC.STCstatic;
import static descent.internal.core.dom.STC.STCsynchronized;
import static descent.internal.core.dom.STC.STCundefined;
import static descent.internal.core.dom.TOK.TOKalias;
import static descent.internal.core.dom.TOK.TOKand;
import static descent.internal.core.dom.TOK.TOKandand;
import static descent.internal.core.dom.TOK.TOKassert;
import static descent.internal.core.dom.TOK.TOKassign;
import static descent.internal.core.dom.TOK.TOKauto;
import static descent.internal.core.dom.TOK.TOKcase;
import static descent.internal.core.dom.TOK.TOKcatch;
import static descent.internal.core.dom.TOK.TOKclass;
import static descent.internal.core.dom.TOK.TOKcolon;
import static descent.internal.core.dom.TOK.TOKcomma;
import static descent.internal.core.dom.TOK.TOKdefault;
import static descent.internal.core.dom.TOK.TOKdelegate;
import static descent.internal.core.dom.TOK.TOKdot;
import static descent.internal.core.dom.TOK.TOKdotdotdot;
import static descent.internal.core.dom.TOK.TOKelse;
import static descent.internal.core.dom.TOK.TOKenum;
import static descent.internal.core.dom.TOK.TOKeof;
import static descent.internal.core.dom.TOK.TOKequal;
import static descent.internal.core.dom.TOK.TOKfinally;
import static descent.internal.core.dom.TOK.TOKfunction;
import static descent.internal.core.dom.TOK.TOKidentifier;
import static descent.internal.core.dom.TOK.TOKidentity;
import static descent.internal.core.dom.TOK.TOKif;
import static descent.internal.core.dom.TOK.TOKimport;
import static descent.internal.core.dom.TOK.TOKinout;
import static descent.internal.core.dom.TOK.TOKint32v;
import static descent.internal.core.dom.TOK.TOKinterface;
import static descent.internal.core.dom.TOK.TOKis;
import static descent.internal.core.dom.TOK.TOKlbracket;
import static descent.internal.core.dom.TOK.TOKlcurly;
import static descent.internal.core.dom.TOK.TOKlparen;
import static descent.internal.core.dom.TOK.TOKmodule;
import static descent.internal.core.dom.TOK.TOKnew;
import static descent.internal.core.dom.TOK.TOKnot;
import static descent.internal.core.dom.TOK.TOKnotidentity;
import static descent.internal.core.dom.TOK.TOKon_scope_exit;
import static descent.internal.core.dom.TOK.TOKon_scope_failure;
import static descent.internal.core.dom.TOK.TOKon_scope_success;
import static descent.internal.core.dom.TOK.TOKor;
import static descent.internal.core.dom.TOK.TOKoror;
import static descent.internal.core.dom.TOK.TOKplusplus;
import static descent.internal.core.dom.TOK.TOKquestion;
import static descent.internal.core.dom.TOK.TOKrbracket;
import static descent.internal.core.dom.TOK.TOKrcurly;
import static descent.internal.core.dom.TOK.TOKreserved;
import static descent.internal.core.dom.TOK.TOKrparen;
import static descent.internal.core.dom.TOK.TOKsemicolon;
import static descent.internal.core.dom.TOK.TOKslice;
import static descent.internal.core.dom.TOK.TOKstring;
import static descent.internal.core.dom.TOK.TOKstruct;
import static descent.internal.core.dom.TOK.TOKthis;
import static descent.internal.core.dom.TOK.TOKtilde;
import static descent.internal.core.dom.TOK.TOKtypedef;
import static descent.internal.core.dom.TOK.TOKtypeof;
import static descent.internal.core.dom.TOK.TOKunion;
import static descent.internal.core.dom.TOK.TOKwhile;
import static descent.internal.core.dom.TOK.TOKxor;
import static descent.internal.core.dom.TY.Taarray;
import static descent.internal.core.dom.TY.Tfunction;
import static descent.internal.core.dom.TY.Tsarray;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.IBaseClass;
import descent.core.dom.IDElement;
import descent.core.dom.IEnumMember;
import descent.core.dom.IImport;
import descent.core.dom.IProblem;
import descent.core.dom.ITemplateParameter;

public class Parser extends Lexer {
	
	public final static boolean LTORARRAYDECL = true;
	
	public final static int PSsemi = 1;		// empty ';' statements are allowed
	public final static int PSscope = 2;	// start a new scope
	public final static int PScurly = 4;	// { } statement is required
	public final static int PScurlyscope = 8;	// { } starts a new scope
	
	ModuleDeclaration md;
	
	Loc endloc = new Loc();
	int inBrackets;
	
	LINK linkage = LINK.LINKd;

	public Parser(String source) {
		super(source);
		
		nextToken();
	}
	
	public Parser(String source, int base, int begoffset, 
			int endoffset, boolean doDocComment, boolean commentToken) {
		super(source, base, begoffset, endoffset, doDocComment, commentToken);
		
		nextToken();
	}
	
	@SuppressWarnings("unchecked")
	public List<IDElement> parseModule() {
	    List<IDElement> decldefs = new ArrayList<IDElement>();

		// ModuleDeclation leads off
		if (token.value == TOKmodule) {
			Token moduleToken = new Token(token);
			nextToken();
			if (token.value != TOKidentifier) {
				
				problem("Identifier expected following module", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, moduleToken.ptr, moduleToken.len);
				// goto Lerr;
				return parseModule_LErr();
			} else {
				List<Identifier> a = null;
				Identifier id;

				int qNameStart = token.ptr;

				id = new Identifier(token);
				while (nextToken() == TOKdot) {
					if (a == null)
						a = new ArrayList<Identifier>();
					a.add(id);
					nextToken();
					if (token.value != TOKidentifier) {
						problem("Identifier expected following package", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, qNameStart, token.ptr - qNameStart);
						return parseModule_LErr();
					}
					id = new Identifier(token);
				}

				md = new ModuleDeclaration(a, id);
				md.start = moduleToken.ptr;
				md.length = token.ptr + token.len - md.start;

				mod.md = md;

				if (token.value != TOKsemicolon) {
					problem("';' expected following module declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, moduleToken.ptr, moduleToken.len);
				}
				nextToken();
				addComment(md, moduleToken.blockComment, moduleToken.blockCommentPtr);
			}
		}

		decldefs = parseDeclDefs(false);
		if (token.value != TOKeof) {
			problem("Unrecognized declaration", IProblem.SEVERITY_ERROR, IProblem.UNRECOGNIZED_DECLARATION, token.ptr, token.len);
			// goto Lerr;
			return parseModule_LErr();
		}
		return decldefs;
	}
	
	private List parseModule_LErr() {
		while (token.value != TOKsemicolon && token.value != TOKeof)
	    	nextToken();
	    nextToken();
	    return new ArrayList();
	}

	@SuppressWarnings("unchecked")
	private List<IDElement> parseDeclDefs(boolean once) {
		Object[] tempObj;

		AbstractElement s;
		List<IDElement> decldefs;
		List<IDElement> a = new ArrayList<IDElement>();
		List<IDElement> aelse;
		PROT prot;
		int stc;
		Condition condition;
		String comment;
		int commentStart = -1;
		
		Token saveToken;
		boolean[] isSingle = new boolean[1];

		// printf("Parser::parseDeclDefs()\n");
		decldefs = new ArrayList<IDElement>();
		do {
			comment = token.blockComment;
			commentStart = token.blockCommentPtr;
			switch (token.value) {
			case TOKenum:
				s = parseEnum();
				break;

			case TOKstruct:
			case TOKunion:
			case TOKclass:
			case TOKinterface:
				s = parseAggregate();
				break;

			case TOKimport:
				s = parseImport(decldefs, false);
				break;

			case TOKtemplate:
				s = parseTemplateDeclaration();
				break;

			case TOKmixin:
				s = parseMixin();
				break;

			// begin CASE_BASIC_TYPES
			case TOKwchar:
			case TOKdchar:
			case TOKbit:
			case TOKbool:
			case TOKchar:
			case TOKint8:
			case TOKuns8:
			case TOKint16:
			case TOKuns16:
			case TOKint32:
			case TOKuns32:
			case TOKint64:
			case TOKuns64:
			case TOKfloat32:
			case TOKfloat64:
			case TOKfloat80:
			case TOKimaginary32:
			case TOKimaginary64:
			case TOKimaginary80:
			case TOKcomplex32:
			case TOKcomplex64:
			case TOKcomplex80:
			case TOKvoid:
				// end CASE_BASIC_TYPES
			case TOKalias:
			case TOKtypedef:
			case TOKidentifier:
			case TOKtypeof:
			case TOKdot:
				// Ldeclaration:
				a = parseDeclarations();
				decldefs.addAll(a);
				continue;

			case TOKthis:
				s = parseCtor();
				break;

			case TOKtilde:
				s = parseDtor();
				break;

			case TOKinvariant:
				saveToken = new Token(token);
				InvariantDeclaration inv = parseInvariant();
				s = inv;
				break;

			case TOKunittest:
				saveToken = new Token(token);
				UnitTestDeclaration unit = parseUnitTest();
				s = unit;
				break;

			case TOKnew:
				s = parseNew();
				break;

			case TOKdelete:
				s = parseDelete();
				break;

			case TOKeof:
			case TOKrcurly:
				return decldefs;

			case TOKstatic:
				Token staticToken = new Token(token);
				nextToken();
				if (token.value == TOKthis) {
					s = parseStaticCtor();
				}
				else if (token.value == TOKtilde) {
					s = parseStaticDtor();
				}
				else if (token.value == TOKassert)
					s = parseStaticAssert();
				else if (token.value == TOKif) {
					condition = parseStaticIfCondition();
					a = parseBlock();
					aelse = null;
					if (token.value == TOKelse) {
						nextToken();
						aelse = parseBlock();
					}
					s = new StaticIfDeclaration(condition, a, aelse);
					s.start = staticToken.ptr;
					s.length = prevToken.ptr + prevToken.len - s.start;
					break;
				} else if (token.value == TOKimport) {
					s = parseImport(decldefs, true);
					ImportDeclaration id = (ImportDeclaration) decldefs.get(decldefs.size() -1);
					id.isStatic = true;
					id.length += id.start - staticToken.ptr;
					id.start = staticToken.ptr;
				} else {
					stc = STCstatic;
					// goto Lstc2;
					tempObj = parseDeclDefs_Lstc2(stc, a, isSingle);
					a = (List<IDElement>) tempObj[0];
					stc = ((Integer) tempObj[1]);
					s = (AbstractElement) tempObj[2];
				}
				if (s != null) {
					s.start = staticToken.ptr;
					s.length = prevToken.ptr + prevToken.len - s.start;
				}
				break;

			case TOKconst:
			case TOKfinal:
			case TOKauto:
			case TOKoverride:
			case TOKabstract:
			case TOKsynchronized:
			case TOKdeprecated:
				stc = STC.fromToken(token.value);
				int mod = STC.getModifiers(stc);
				// goto Lstc;
				saveToken = new Token(token);
				nextToken(); 
				tempObj = parseDeclDefs_Lstc2(stc, a, isSingle);
				a = (List) tempObj[0];
				stc = ((Integer) tempObj[1]);
				s = (AbstractElement) tempObj[2];
				
				if (a != null && a.size() == 1) {
					if (isSingle[0]) {
						s = (AbstractElement) a.get(0);
						s.modifiers |= mod;
					} else {
						for(IDElement elem : (List<IDElement>) a) {
							((AbstractElement) elem).modifiers |= mod;
						}
					}
				}
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;

			case TOKextern:
				saveToken = new Token(token);
				
				if (peek(token).value != TOKlparen) {
					stc = STCextern;
					// goto Lstc;
					nextToken();
					tempObj = parseDeclDefs_Lstc2(stc, a, isSingle);
					a = (List) tempObj[0];
					stc = ((Integer) tempObj[1]);
					s = (AbstractElement) tempObj[2];
					break;
				}
				{
					LINK linksave = linkage;
					linkage = parseLinkage();
					a = parseBlock();
					s = new LinkDeclaration(linkage, a);
					s.start = saveToken.ptr;
					s.length = prevToken.ptr + prevToken.len - s.start;
					linkage = linksave;
					break;
				}
			case TOKprivate:
			case TOKpackage:
			case TOKprotected:
			case TOKpublic:
			case TOKexport:
				prot = PROT.fromToken(token.value);
				int protection = prot.getModifiers();
				// goto Lprot;
				saveToken = new Token(token);
				nextToken();
				a = parseBlock(isSingle);
				if (a != null && a.size() > 0) {
					if (isSingle[0]) {
						s = (AbstractElement) a.get(0);
						s.modifiers |= protection;
					} else {
						s = new ProtDeclaration(prot, a);
						for(IDElement elem : (List<IDElement>) a) {
							((AbstractElement) elem).modifiers |= protection;
						}
					}
				} else {
					s = new ProtDeclaration(prot, a);
				}
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;
				
			case TOKalign: {
				long n;

				s = null;
				saveToken = new Token(token);
				nextToken();
				if (token.value == TOKlparen) {
					nextToken();
					if (token.value == TOKint32v)
						n = token.numberValue;
					else {
						problem("Integer expected", IProblem.SEVERITY_ERROR, IProblem.INTEGER_EXPECTED, token.ptr, token.len);
						n = 1;
					}
					nextToken();
					check(TOKrparen);
				} else
					n = global.structalign; // default

				a = parseBlock();
				s = new AlignDeclaration(n, a);
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;
			}

			case TOKpragma: {
				Identifier ident;
				List<Expression> args = null;
				
				saveToken = new Token(token);

				nextToken();
				check(TOKlparen);
				if (token.value != TOKidentifier) {
					problem("Pragma identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					// goto Lerror;
					s = parseDeclDefs_Lerror();
					continue;
				}
				ident = new Identifier(token);
				nextToken();
				if (token.value == TOKcomma) {
					args = parseArguments(); // pragma(identifier, args...)
				} else {
					check(TOKrparen); // pragma(identifier)
				}

				if (token.value == TOKsemicolon) {
					a = null;
				} else {
					a = parseBlock();
				}
				s = new PragmaDeclaration(loc, ident, args, a);
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;
			}

			case TOKdebug:
				saveToken = new Token(token);
				
				nextToken();
				if (token.value == TOKassign) {
					nextToken();
					if (token.value == TOKidentifier) {
						s = new DebugSymbol(loc, new Identifier(token));
					} else if (token.value == TOKint32v) {
						Identifier id = new Identifier(String.valueOf(token.numberValue), TOK.TOKidentifier);
						id.start = token.ptr;
						id.length = token.len;
						s = new DebugSymbol(loc, id);
					} else {
						problem("Identifier or integer expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, token.ptr, token.len);
						s = null;
					}
					nextToken();
					if (token.value != TOKsemicolon) {
						problem("Semicolon expected", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
					}
					nextToken();
					
					if (s != null) {
						s.start = saveToken.ptr;
						s.length = prevToken.ptr + prevToken.len - s.start;
					}
					
					break;
				}

				condition = parseDebugCondition();
				// goto Lcondition;
				tempObj = parseDeclDefs_Lcondition(condition);
				a = (List) tempObj[0];
				aelse = (List) tempObj[1];
				s = (AbstractElement) tempObj[2];
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;

			case TOKversion:
				saveToken = new Token(token);
				
				nextToken();
				if (token.value == TOKassign) {
					nextToken();
					if (token.value == TOKidentifier) {
						s = new VersionSymbol(loc, new Identifier(token));
					} else if (token.value == TOKint32v) {
						Identifier id = new Identifier(String.valueOf(token.numberValue), TOK.TOKidentifier);
						id.start = token.ptr;
						id.length = token.len;
						s = new VersionSymbol(loc, id);
					} else {
						problem("Identifier or integer expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, token.ptr, token.len);
						s = null;
					}
					nextToken();
					if (token.value != TOKsemicolon) {
						problem("Semicolon expected", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
					}
					nextToken();
					
					if (s != null) {
						s.start = saveToken.ptr;
						s.length = prevToken.ptr + prevToken.len - s.start;
					}
					
					break;
				}
				condition = parseVersionCondition();
				// goto Lcondition;
				tempObj = parseDeclDefs_Lcondition(condition);
				a = (List) tempObj[0];
				aelse = (List) tempObj[1];
				s = (AbstractElement) tempObj[2];
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;

			case TOKiftype:
				saveToken = new Token(token);
				
				condition = parseIftypeCondition();
				// goto Lcondition;
				tempObj = parseDeclDefs_Lcondition(condition);
				a = (List<IDElement>) tempObj[0];
				aelse = (List<IDElement>) tempObj[1];
				s = (AbstractElement) tempObj[2];
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;

			case TOKsemicolon: // empty declaration
				nextToken();
				continue;

			default:
				problem("Declaration expected", IProblem.SEVERITY_ERROR, IProblem.DECLARATION_EXPECTED, token.ptr, token.len);
				s = parseDeclDefs_Lerror();
				continue;
			}
			if (s != null) {
				decldefs.add(s);
				addComment(s, comment, commentStart);
			}
		} while (!once);
		return decldefs;
	}
	
	private Dsymbol parseDeclDefs_Lerror() {
		while (token.value != TOKsemicolon && token.value != TOKeof)
		    nextToken();
		nextToken();
		return null;
	}
	
	// a, aelse, s
	private Object[] parseDeclDefs_Lcondition(Condition condition) {
		List<IDElement> a = parseBlock();
		List<IDElement> aelse = null;
		if (token.value == TOKelse)
		{   nextToken();
		    aelse = parseBlock();
		}
		Dsymbol s = new ConditionalDeclaration(condition, a, aelse);
		return new Object[] { a, aelse, s };
	}
	
	// a, stc, s
	private Object[] parseDeclDefs_Lstc2(int stc, List<IDElement> a, boolean[] isSingle) {
		boolean repeat = true;
		while(repeat) {
			switch (token.value)
			{
			    case TOKconst:	  stc |= STCconst; nextToken(); break;
			    case TOKfinal:	  stc |= STCfinal; nextToken(); break;
			    case TOKauto:	  stc |= STCauto; nextToken(); break;
			    case TOKoverride:	  stc |= STCoverride; nextToken(); break;
			    case TOKabstract:	  stc |= STCabstract; nextToken(); break;
			    case TOKsynchronized: stc |= STCsynchronized; nextToken(); break;
			    case TOKdeprecated:   stc |= STCdeprecated; nextToken(); break;
			    default:
			    	repeat = false;
				break;
			}
		}

		/* Look for auto initializers:
		 *	storage_class identifier = initializer;
		 */
		if (token.value == TOKidentifier &&
		    peek(token).value == TOKassign)
		{
		    Identifier ident = token.ident;
		    nextToken();
		    nextToken();
		    Initializer init = parseInitializer();
		    VarDeclaration v = new VarDeclaration(loc, null, ident, init);
		    v.storage_class = stc;
		    v.modifiers = STC.getModifiers(stc);
		    Dsymbol s = v;
		    if (token.value != TOKsemicolon) {
		    	problem("Semicolon expected following auto declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
		    }
		    else
			nextToken();
		    
		    return new Object[] { a, stc, s }; 
		}
		else
		{   
			a = parseBlock(isSingle);
		    Dsymbol s = new StorageClassDeclaration(stc, a);
		    
		    return new Object[] { a, stc, s }; 
		}
	}
	
	private List<IDElement> parseBlock() {
		return parseBlock(new boolean[1]);
	}
	
	private List<IDElement> parseBlock(boolean[] isSingle) {
		List<IDElement> a = null;
	    // Dsymbol s; // <-- not used

	    //printf("parseBlock()\n");
	    switch (token.value)
	    {
		case TOKsemicolon:
			problem("Declaration expected following attribute", IProblem.SEVERITY_ERROR, IProblem.DECLARATION_EXPECTED, token.ptr, token.len);
		    nextToken();
		    break;

		case TOKlcurly:
		    nextToken();
		    a = parseDeclDefs(false);
		    if (token.value != TOKrcurly)
		    {   /* { */
		    	problem("Matching '}' expected", IProblem.SEVERITY_ERROR, IProblem.MATCHING_CURLY_EXPECTED, token.ptr, token.len);
		    }
		    else
			nextToken();
		    break;

		case TOKcolon:
		    nextToken();
	//#if 1
	//	    a = null;
	//#else
		    a = parseDeclDefs(false);	// grab declarations up to closing curly bracket
	//#endif
		    break;

		default:
		    a = parseDeclDefs(true);
			isSingle[0] = true;
		    break;
	    }
	    return a;
	}
	
	private StaticAssert parseStaticAssert() {
		Loc loc = new Loc(this.loc);
	    Expression exp;
	    Expression msg = null;

	    //printf("parseStaticAssert()\n");
	    nextToken();
	    check(TOKlparen);
	    exp = parseAssignExp();
	    if (token.value == TOKcomma)
	    {	nextToken();
		msg = parseAssignExp();
	    }
	    check(TOKrparen);
	    check(TOKsemicolon);
	    return new StaticAssert(loc, exp, msg);
	}
	
	private LINK parseLinkage() {
		LINK link = LINKdefault;
		nextToken();
		assert (token.value == TOKlparen);
		nextToken();
		if (token.value == TOKidentifier) {
			Identifier id = new Identifier(token);

			nextToken();
			if (id.string.equals(Id.Windows))
				link = LINKwindows;
			else if (id.string.equals(Id.Pascal))
				link = LINKpascal;
			else if (id.string.equals(Id.D))
				link = LINKd;
			else if (id.string.equals(Id.C)) {
				link = LINKc;
				if (token.value == TOKplusplus) {
					link = LINKcpp;
					nextToken();
				}
			} else {
				problem("valid linkage identifiers are D, C, C++, Pascal, Windows", IProblem.SEVERITY_ERROR, IProblem.INVALID_LINKAGE_IDENTIFIER, id.start, id.length);
				link = LINKd;
			}
		} else {
			link = LINKd; // default
		}
		check(TOKrparen);
		return link;
	}
	
	private Condition parseDebugCondition() {
		DebugCondition c;
		Identifier id = null;
		Token idToken = null;
		long level = 1;

		if (token.value == TOKlparen) {
			nextToken();
			if (token.value == TOKidentifier) {
				id = new Identifier(token);
			} else if (token.value == TOKint32v) {
				idToken = new Token(token);
				level = token.numberValue;
			} else {
				problem("Identifier or integer expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, token.ptr, token.len);
			}
			nextToken();
			check(TOKrparen);
			c = new DebugCondition(mod, level, id);
		} else {
			c = new DebugCondition(mod, 1, null);
		}
		if (id == null && idToken != null) {
			c.id = new Identifier(String.valueOf(level), TOK.TOKint32);
			c.id.start = idToken.ptr;
			c.id.length = idToken.len;
		}
		return c;
	}
	
	private Condition parseVersionCondition() {
		Condition c;
		long level = 1;
		Identifier id = null;
		
		Token idToken = null;

		if (token.value == TOKlparen) {
			nextToken();
			if (token.value == TOKidentifier) {
				id = new Identifier(token);
			} else if (token.value == TOKint32v) {
				idToken = new Token(token);
				level = token.numberValue;
			} else {
				problem("Identifier or integer expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, token.ptr, token.len);
			}
			nextToken();
			check(TOKrparen);
		} else {
			problem("(condition) expected following version", IProblem.SEVERITY_ERROR, IProblem.CONDITION_EXPECTED_FOLLOWING_VERSION, token.ptr, token.len);
		}
		VersionCondition vc = new VersionCondition(mod, level, id);
		if (id == null && idToken != null) {
			vc.id = new Identifier(String.valueOf(level), TOK.TOKint32);
			vc.id.start = idToken.ptr;
			vc.id.length = idToken.len;
		}
		c = vc;
		return c;
	}
	
	private Condition parseStaticIfCondition() {
		Expression exp;
	    Condition condition;
	    // List aif; // <-- not used
	    // List aelse; // <-- not used
	    Loc loc = new Loc(this.loc);

	    nextToken();
	    if (token.value == TOKlparen)
	    {
		nextToken();
		exp = parseAssignExp();
		check(TOKrparen);
	    }
	    else
	    {   
	    	problem("(expression) expected following version", IProblem.SEVERITY_ERROR, IProblem.EXPRESSION_EXPECTED, token.ptr, token.len);
	    	exp = null;
	    }
	    condition = new StaticIfCondition(loc, exp);
	    return condition;
	}
	
	private Condition parseIftypeCondition() {
		Type targ;
		Identifier[] ident = new Identifier[1];
		Type tspec = null;
		TOK tok = TOKreserved;
		Loc loc = new Loc(this.loc);
		
		Token firstToken = new Token(token);

		nextToken();
		if (token.value == TOKlparen) {
			nextToken();
			targ = parseBasicType();
			targ = parseDeclarator(targ, ident);
			if (token.value == TOKcolon || token.value == TOKequal) {
				tok = token.value;
				nextToken();
				tspec = parseBasicType();
				tspec = parseDeclarator(tspec, null);
			}
			check(TOKrparen);
		} else {
			problem("(type identifier : specialization) expected following iftype", IProblem.SEVERITY_ERROR, IProblem.INVALID_IFTYPE_SYNTAX, token.ptr, token.len);
			return null;
		}
		Condition condition = new IftypeCondition(loc, targ, ident[0], tok, tspec);

		problem("iftype(condition) is deprecated, use static if (is(condition))", IProblem.SEVERITY_WARNING, IProblem.IFTYPE_DEPRECATED, firstToken.ptr, firstToken.len);

		return condition;
	}
	
	private CtorDeclaration parseCtor() {
		Token firstToken = new Token(token);
		
		CtorDeclaration f;
	    List<Argument> arguments;
	    int[] varargs = new int[1];
	    Loc loc = new Loc(this.loc);

	    nextToken();
	    arguments = parseParameters(varargs);
	    f = new CtorDeclaration(loc, 0, arguments, varargs[0]);
	    f.start = firstToken.ptr;
	    f.ident.start = firstToken.ptr;
	    f.ident.length = firstToken.len;
	    parseContracts(f);
	    return f;
	}
	
	private DtorDeclaration parseDtor() {
		Token firstToken = new Token(token);
		
		DtorDeclaration f;
	    Loc loc = new Loc(this.loc);

	    nextToken();
	    Token secondToken = new Token(token);
	    check(TOKthis);
	    check(TOKlparen);
	    check(TOKrparen);

	    f = new DtorDeclaration(loc, 0);
	    f.start = firstToken.ptr;
	    f.ident.start = firstToken.ptr;
	    f.ident.length = secondToken.ptr + secondToken.len - firstToken.ptr;
	    parseContracts(f);
	    return f;
	}
	
	private StaticCtorDeclaration parseStaticCtor() {
		Token firstToken = new Token(token);
		
		StaticCtorDeclaration f;
	    Loc loc = new Loc(this.loc);

	    nextToken();
	    check(TOKlparen);
	    check(TOKrparen);

	    f = new StaticCtorDeclaration(loc, 0);
	    f.start = firstToken.ptr;
	    f.ident.start = firstToken.ptr;
	    f.ident.length = firstToken.len;
	    parseContracts(f);
	    return f;
	}
	
	private StaticDtorDeclaration parseStaticDtor() {
		Token firstToken = new Token(token);
		
		StaticDtorDeclaration f;
	    Loc loc = new Loc(this.loc);

	    nextToken();
	    Token secondToken = new Token(token);
	    check(TOKthis);
	    check(TOKlparen);
	    check(TOKrparen);

	    f = new StaticDtorDeclaration(loc, 0);
	    f.start = firstToken.ptr;
	    f.ident.start = firstToken.ptr;
	    f.ident.length = secondToken.ptr + secondToken.len - firstToken.ptr;
	    parseContracts(f);
	    return f;
	}
	
	private InvariantDeclaration parseInvariant() {
		InvariantDeclaration f;
	    Loc loc = new Loc(this.loc);

	    nextToken();
	    //check(TOKlparen);		// don't require ()
	    //check(TOKrparen);

	    f = new InvariantDeclaration(loc, 0);
	    f.start = prevToken.ptr;
	    f.fbody = parseStatement(PScurly);
	    f.length = prevToken.ptr + prevToken.len - f.start;
	    return f;
	}
	
	private UnitTestDeclaration parseUnitTest() {
		UnitTestDeclaration f;
	    Loc loc = new Loc(this.loc);

	    nextToken();

	    f = new UnitTestDeclaration(loc, this.loc);
	    f.start = prevToken.ptr;
	    f.fbody = parseStatement(PScurly);
	    f.length = prevToken.ptr + prevToken.len - f.start;
	    return f;
	}
	
	private NewDeclaration parseNew() {
		Token firstToken = new Token(token);
		
		NewDeclaration f;
	    List<Argument> arguments;
	    int[] varargs = new int[1];
	    Loc loc = new Loc(this.loc);

	    nextToken();
	    arguments = parseParameters(varargs);
	    f = new NewDeclaration(loc, 0, arguments, varargs[0]);
	    f.start = firstToken.ptr;
	    f.ident.start = firstToken.ptr;
	    f.ident.length = firstToken.len;
	    parseContracts(f);
	    return f;
	}
	
	private DeleteDeclaration parseDelete() {
		Token firstToken = new Token(token);
		
		DeleteDeclaration f;
	    List<Argument> arguments;
	    int[] varargs = new int[1];
	    Loc loc = new Loc(this.loc);

	    nextToken();
	    arguments = parseParameters(varargs);
	    f = new DeleteDeclaration(loc, 0, arguments);
	    f.start = firstToken.ptr;
	    f.ident.start = firstToken.ptr;
	    f.ident.length = firstToken.len;
	    
	    if (varargs[0] != 0) {
	    	problem("... not allowed in delete function parameter list", IProblem.SEVERITY_ERROR, IProblem.VARIADIC_NOT_ALLOWED_IN_DELETE, f.ident.start, f.ident.length);
	    }
	    
	    parseContracts(f);
	    return f;
	}
	
	@SuppressWarnings("unchecked")
	private List<Argument> parseParameters(int[] pvarargs) {
		List<Argument> arguments = new ArrayList<Argument>();
		int varargs = 0;
		boolean hasdefault = false;

		check(TOKlparen);
		while (true) {
			Type tb;
			Identifier ai;
			Type at;
			Argument a;
			InOut inout;
			Expression ae;
			
			Token firstToken = new Token(token);
			
			ai = null;
			inout = In; // parameter is "in" by default
			
			if (token.value == TOKrparen) {
				break;
			} else if (token.value == TOKdotdotdot) {
				varargs = 1;
				nextToken();
				break;
			} else {
				Token inoutToken = new Token(token);
				
				switch(token.value) {
					case TOKin:
						inout = In;
						nextToken();
						break;
					case TOKout:
						inout = Out;
						nextToken();
						break;
					case TOKinout:
						inout = InOut;
						nextToken();
						break;
					case TOKlazy:
						inout = Lazy;
						nextToken();
						break;
				}
				
				tb = parseBasicType();

				Identifier[] pointer2_ai = { ai };
				at = parseDeclarator(tb, pointer2_ai);
				ai = pointer2_ai[0];

				ae = null;
				if (token.value == TOKassign) // = defaultArg
				{
					nextToken();
					ae = parseAssignExp();
					hasdefault = true;
				} else {
					if (hasdefault) {
						IDElement e = ai != null ? ai : at;
						problem("Default argument expected", IProblem.SEVERITY_ERROR, IProblem.DEFAULT_ARGUMENT_EXPECTED, e.getOffset(), e.getLength());
					}
				}
				if (token.value == TOKdotdotdot) { 
					/*
					 * This is: at ai ...
					 */
					if (inout == Out || inout == InOut) {
						problem("Variadic argument cannot be out or inout", IProblem.SEVERITY_ERROR, IProblem.VARIADIC_ARGUMENT_CANNOT_BE_OUT_OR_INOUT, inoutToken.ptr, inoutToken.len);
					}
					varargs = 2;
					a = new Argument(inout, at, ai, ae);
					arguments.add(a);
					nextToken();
					break;
				}
				a = new Argument(inout, at, ai, ae);
				a.start = firstToken.ptr;
				a.length = prevToken.ptr + prevToken.len - a.start;
				arguments.add(a);
				if (token.value == TOKcomma) {
					nextToken();
				} else {
					break;
				}
			}
		}
		check(TOKrparen);
		pvarargs[0] = varargs;
		return arguments;
	}
	
	private EnumDeclaration parseEnum() {
		Token enumToken = new Token(token);

		EnumDeclaration e;
		Identifier id;
		Type t;
		
		// printf("Parser::parseEnum()\n");
		nextToken();
		if (token.value == TOKidentifier) {
			id = new Identifier(token);
			nextToken();
		} else {
			id = null;
		}

		if (token.value == TOKcolon) {
			nextToken();
			t = parseBasicType();
		} else {
			t = null;
		}

		e = new EnumDeclaration(id, t);
		e.start = enumToken.ptr;
		
		if (token.value == TOKsemicolon && id != null) {
			e.length = token.ptr + token.len - e.start;
			nextToken();			  
		} else if (token.value == TOKlcurly) {
			// printf("enum definition\n");
			e.members = new ArrayList<IEnumMember>();
			nextToken();
			String comment = token.blockComment;
			while (token.value != TOKrcurly) {
				if (token.value == TOKeof) {
					problem("Enum declaration is invalid", IProblem.SEVERITY_ERROR, IProblem.ENUM_DECLARATION_IS_INVALID, enumToken.ptr, enumToken.len);
					break;
				}
				
				if (token.value == TOKidentifier) {
					EnumMember em;
					Expression value;
					Identifier ident;

					ident = new Identifier(token);
					value = null;
					nextToken();
					if (token.value == TOKassign) {
						nextToken();
						value = parseAssignExp();
					}
					
					em = new EnumMember(ident, value);
					e.members.add(em);
					if (token.value == TOKrcurly) {
						;
					} else {
						addComment(em, comment);
						comment = null;
						check(TOKcomma);
					}
					addComment(em, comment);
					comment = token.blockComment;
				} else {
					problem("Enum member expected", IProblem.SEVERITY_ERROR, IProblem.ENUM_MEMBER_EXPECTED, token.ptr, token.len);
					nextToken();
				}
			}
			e.length = token.ptr + token.len - e.start;
			
			nextToken();
		} else {
			problem("Enum declaration is invalid", IProblem.SEVERITY_ERROR, IProblem.ENUM_DECLARATION_IS_INVALID, enumToken.ptr, enumToken.len);
		}
		
		return e;
	}
	
	@SuppressWarnings("unchecked")
	private Dsymbol parseAggregate() {
		AggregateDeclaration a = null;
		int anon = 0;
		TOK tok;
		Identifier id;
		List<TemplateParameter> tpl = null;
		
		Token firstToken = new Token(token);

		// printf("Parser::parseAggregate()\n");
		tok = token.value;
		nextToken();
		if (token.value != TOKidentifier) {
			id = null;
		} else {
			id = new Identifier(token);
			nextToken();

			if (token.value == TOKlparen) { // Class template declaration.

				// Gather template parameter list
				tpl = parseTemplateParameterList();
			}
		}

		Loc loc = new Loc(this.loc);
		switch (tok) {
		case TOKclass:
		case TOKinterface: {
			if (id == null) {
				problem("Anonymous classes not allowed", IProblem.SEVERITY_ERROR, IProblem.ANONYMOUS_CLASSES_NOT_ALLOWED,
						firstToken.ptr, firstToken.len);
			}

			// Collect base class(es)
			List<BaseClass> baseclasses = null;
			if (token.value == TOKcolon) {
				nextToken();
				baseclasses = parseBaseClasses();

				if (token.value != TOKlcurly) {
					if (baseclasses != null &&  baseclasses.size() > 0) {
						IBaseClass last = baseclasses.get(baseclasses.size() - 1);
						problem("Members expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED,
								last.getOffset(), last.getLength());
					}
				}
			}

			if (tok == TOKclass) {
				a = new ClassDeclaration(loc, id, baseclasses);
			} else {
				a = new InterfaceDeclaration(loc, id, baseclasses);
			}
			a.start = firstToken.ptr;
			break;
		}

		case TOKstruct:
			//if (id != null) {
				a = new StructDeclaration(loc, id);
				a.start = firstToken.ptr;
			//} else {
			//	anon = 1;
			//}
			break;

		case TOKunion:
			//if (id != null) {
				a = new UnionDeclaration(loc, id);
				a.start = firstToken.ptr;
			//} else {
			//	anon = 2;
			//}
			break;

		default:
			assert (false);
			break;
		}
		
		if (a != null && token.value == TOKsemicolon) {
			a.length = token.ptr + token.len - a.start;
			nextToken();
		} else if (token.value == TOKlcurly) {
			Token lcurlyToken = new Token(token);
			// printf("aggregate definition\n");
			nextToken();
			List decl = parseDeclDefs(false);
			if (token.value != TOKrcurly) {
				problem("} expected following member declarations in aggregate", IProblem.SEVERITY_ERROR, IProblem.RIGHT_CURLY_EXPECTED_FOLLOWING_MEMBER_DECLARATIONS_IN_AGGREGATE,
						lcurlyToken.ptr, lcurlyToken.len);
			}
			
			if (a != null) {
				a.length = token.ptr + token.len - a.start;
			}
			
			nextToken();
			if (anon != 0) {
				throw new IllegalStateException("Can't happen");
				/*
				 * Anonymous structs/unions are more like attributes.
				 */
				//return new AnonDeclaration(loc, anon - 1, decl);
			} else {
				a.members = decl;
			}
		} else {
			if (a.ident == null) {
				problem("{ } expected following aggregate declaration", IProblem.SEVERITY_ERROR, IProblem.CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION,
						firstToken.ptr, firstToken.len);
			} else {
				problem("{ } expected following aggregate declaration", IProblem.SEVERITY_ERROR, IProblem.CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION,
						firstToken.ptr, a.ident.start + a.ident.length - firstToken.ptr);
			}
			a = new StructDeclaration(loc, null);
		}

		if (tpl != null) {
			a.templateParameters = tpl.toArray(new ITemplateParameter[tpl.size()]);
			/*
			List decldefs;
			TemplateDeclaration tempdecl;

			// Wrap a template around the aggregate declaration
			decldefs = new ArrayList();
			decldefs.add(a);
			tempdecl = new TemplateDeclaration(loc, id, tpl, decldefs);
			return tempdecl;
			*/
		}

		return a;
	}
	
	@SuppressWarnings("unchecked")
	private List<BaseClass> parseBaseClasses() {
		PROT protection = PROTpublic;
		List<BaseClass> baseclasses = new ArrayList<BaseClass>();

	    for (; true; nextToken())
	    {
		switch (token.value)
		{
		    case TOKidentifier:
			break;
		    case TOKprivate:
			protection = PROTprivate;
			continue;
		    case TOKpackage:
			protection = PROTpackage;
			continue;
		    case TOKprotected:
			protection = PROTprotected;
			continue;
		    case TOKpublic:
			protection = PROTpublic;
			continue;
		    default:
		    	problem("Base class expected", IProblem.SEVERITY_ERROR, IProblem.BASE_CLASS_EXPECTED, token.ptr, token.len);
			return null;
		}
		BaseClass b = new BaseClass(parseBasicType(), protection);
		baseclasses.add(b);
		if (token.value != TOKcomma)
		    break;
		protection = PROTpublic;
	    }
	    return baseclasses;
	}
	
	private TemplateDeclaration parseTemplateDeclaration() {
		TemplateDeclaration tempdecl;
	    Identifier id;
	    List<TemplateParameter> tpl;
	    List<IDElement> decldefs;
	    Loc loc = new Loc(this.loc);

	    Token firstToken = new Token(token);
	    nextToken();
	    if (token.value != TOKidentifier)
	    {   
	    	problem("TemplateIdentifier expected following template", IProblem.SEVERITY_ERROR, IProblem.TEMPLATE_IDENTIFIER_EXPECTED, token.ptr, token.len);
	    	return null;
			//goto Lerr;
	    }
	    id = new Identifier(token);
	    nextToken();
	    tpl = parseTemplateParameterList();
	    if (tpl == null)
	    	//goto Lerr;
	    	return null;

	    if (token.value != TOKlcurly)
	    {	
	    	problem("Members of template declaration expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
			//goto Lerr;
	    	return null;
	    }
	    else
	    {
		nextToken();
		decldefs = parseDeclDefs(false);
		if (token.value != TOKrcurly)
		{
			problem("Template member expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
		    //goto Lerr;
			return null;
		}
		nextToken();
	    }

	    tempdecl = new TemplateDeclaration(loc, id, tpl, decldefs);
	    tempdecl.start = firstToken.ptr;
	    tempdecl.length = prevToken.ptr + prevToken.len - tempdecl.start;
	    return tempdecl;

	//Lerr:
	//    return NULL;
	}
	
	@SuppressWarnings("unchecked")
	private List<TemplateParameter> parseTemplateParameterList() {
		List<TemplateParameter> tpl;

		if (token.value != TOKlparen) {
			problem("Parenthesized TemplateParameterList expected following TemplateIdentifier", IProblem.SEVERITY_ERROR, IProblem.PARENTHESIZED_TEMPLATE_PARAMETER_LIST_EXPECTED, token.ptr, token.len);
			// goto Lerr;
			return null;
		}
		tpl = new ArrayList<TemplateParameter>();
		nextToken();

		// Get array of TemplateParameters
		if (token.value != TOKrparen) {
			
			boolean variadic = false;
			TemplateParameter tp = null;
			
			while (true) {
				Identifier tp_ident = null;
				Type tp_spectype = null;
				Type tp_valtype = null;
				Type tp_defaulttype = null;
				Expression tp_specvalue = null;
				Expression tp_defaultvalue = null;
				Token t;

				Token firstToken = new Token(token);
				
			    if (variadic)
			    {	
			    	problem("Variadic template parameter must be last one", IProblem.SEVERITY_ERROR, IProblem.VARIADIC_TEMPLATE_PARAMETER_MUST_BE_LAST_ONE, 
			    			tp.start, 
			    			tp.length);
			    	variadic = false;
			    }

				// Get TemplateParameter

				// First, look ahead to see if it is a TypeParameter or a
				// ValueParameter
				t = peek(token);
				if (token.value == TOKalias) { // AliasParameter
					nextToken();
					if (token.value != TOKidentifier) {
						problem("Identifier expected for template parameter", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
						// goto Lerr;
						return null;
					}
					tp_ident = new Identifier(token);
					nextToken();
					if (token.value == TOKcolon) // : Type
					{
						nextToken();
						tp_spectype = parseBasicType();
						tp_spectype = parseDeclarator(tp_spectype, null);
					}
					if (token.value == TOKassign) // = Type
					{
						nextToken();
						tp_defaulttype = parseBasicType();
						tp_defaulttype = parseDeclarator(tp_defaulttype, null);
					}
					tp = new TemplateAliasParameter(loc, tp_ident, tp_spectype,
							tp_defaulttype);
				} else if (t.value == TOKcolon || t.value == TOKassign
						|| t.value == TOKcomma || t.value == TOKrparen) { // TypeParameter
					if (token.value != TOKidentifier) {
						problem("Identifier expected for template parameter", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
						// goto Lerr;
						return null;
					}
					tp_ident = new Identifier(token);
					nextToken();
					if (token.value == TOKcolon) // : Type
					{
						nextToken();
						tp_spectype = parseBasicType();
						tp_spectype = parseDeclarator(tp_spectype, null);
					}
					if (token.value == TOKassign) // = Type
					{
						nextToken();
						tp_defaulttype = parseBasicType();
						tp_defaulttype = parseDeclarator(tp_defaulttype, null);
					}
					tp = new TemplateTypeParameter(loc, tp_ident, tp_spectype,
									tp_defaulttype);
				}
			    else if (token.value == TOKidentifier && t.value == TOKdotdotdot)
			    {	// ident...
					variadic = true;
					tp_ident = new Identifier(token);
					nextToken();
					nextToken();
					tp = new TemplateTupleParameter(loc, tp_ident);
				} else { // ValueParameter
					tp_valtype = parseBasicType();

					Identifier[] pointer2_tp_ident = new Identifier[] { tp_ident };
					tp_valtype = parseDeclarator(tp_valtype, pointer2_tp_ident);
					tp_ident = pointer2_tp_ident[0];
					if (tp_ident == null) {
						problem("No identifier for template value parameter", IProblem.SEVERITY_ERROR, IProblem.NO_IDENTIFIER_FOR_TEMPLATE_VALUE_PARAMETER, tp_valtype.start, tp_valtype.length);
						// goto Lerr;
						return null;
					}
					if (token.value == TOKcolon) // : CondExpression
					{
						nextToken();
						tp_specvalue = parseCondExp();
					}
					if (token.value == TOKassign) // = CondExpression
					{
						nextToken();
						tp_defaultvalue = parseCondExp();
					}
					tp = new TemplateValueParameter(loc, tp_ident, tp_valtype,
							tp_specvalue, tp_defaultvalue);
				}
				tp.start = firstToken.ptr;
				tp.length = prevToken.ptr + prevToken.len - tp.start;
				
				tpl.add(tp);
				if (token.value != TOKcomma)
					break;
				nextToken();
			}
		}
		check(TOKrparen);
		return tpl;

	// Lerr:
	// return NULL;
	}
	
	/* <-- Not used
	private TemplateInstance parseTemplateInstance() {
		TemplateInstance tempinst;
	    Identifier id;

	    //printf("parseTemplateInstance()\n");
	    nextToken();
	    if (token.value == TOKdot)
	    {
		id = Id.empty;
	    }
	    else if (token.value == TOKidentifier)
	    {	id = token.ident;
		nextToken();
	    }
	    else
	    {   error("TemplateIdentifier expected following instance");
			//goto Lerr;
	    	return null;
	    }
	    tempinst = new TemplateInstance(loc, id);
	    while (token.value == TOKdot)
	    {   nextToken();
		if (token.value == TOKidentifier)
		    tempinst.addIdent(token.ident);
		else
		{   error("identifier expected following '.' instead of '%s'", token);
		    //goto Lerr;
			return null;
		}
		nextToken();
	    }
	    tempinst.tiargs = parseTemplateArgumentList();

	    //if (!global.params.useDeprecated)
		error("instance is deprecated, use %s", tempinst);
	    return tempinst;

	//Lerr:
	    //return NULL;
	}
	*/
	
	@SuppressWarnings("unchecked")
	private Dsymbol parseMixin() {
		TemplateMixin tm;
		Identifier id;
		TypeTypeof tqual;
		List<IDElement> tiargs;
		List<Identifier> idents;
		
		Token firstToken = new Token(token);

		// printf("parseMixin()\n");
		nextToken();

		tqual = null;
		if (token.value == TOKdot) {
			id = new Identifier(Id.empty, TOKidentifier);
		} else {
			if (token.value == TOKtypeof) {
				Expression exp;

				nextToken();
				check(TOKlparen);
				exp = parseExpression();
				check(TOKrparen);
				tqual = new TypeTypeof(loc, exp);
				check(TOKdot);
			}
			if (token.value != TOKidentifier) {
				problem("Identifier expected", IProblem.SEVERITY_ERROR,
						IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				// goto Lerr;
				return null;
			}
			id = new Identifier(token);
			nextToken();
		}

		idents = new ArrayList<Identifier>();
		while (true) {
			tiargs = null;
			if (token.value == TOKnot) {
				nextToken();
				tiargs = parseTemplateArgumentList();
			}

			if (token.value != TOKdot)
				break;

			if (tiargs != null) {
				TemplateInstance tempinst = new TemplateInstance(loc, id);
				tempinst.tiargs = tiargs;
				id = tempinst;
				tiargs = null;
			}
			idents.add(id);

			nextToken();
			if (token.value != TOKidentifier) {
				problem("Identifier expected", IProblem.SEVERITY_ERROR,
						IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				break;
			}
			id = new Identifier(token);
			nextToken();
		}
		idents.add(id);

		if (token.value == TOKidentifier) {
			id = new Identifier(token);
			nextToken();
		} else {
			id = null;
		}

		tm = new TemplateMixin(loc, id, tqual, idents, tiargs);
		if (token.value != TOKsemicolon) {
			problem("Semicolon expected following mixin",
					IProblem.SEVERITY_ERROR,
					IProblem.SEMICOLON_EXPECTED, token.ptr,
					token.len);
		}
		nextToken();
		
		tm.start = firstToken.ptr;
		tm.length = prevToken.ptr + prevToken.len - tm.start;

		return tm;
	    
	    // Lerr:
	    // return NULL;
	}
	
	@SuppressWarnings("unchecked")
	private List<IDElement> parseTemplateArgumentList() {
		// printf("Parser::parseTemplateArgumentList()\n");
	    List<IDElement> tiargs = new ArrayList<IDElement>();
	    if (token.value != TOKlparen)
	    {   
	    	problem("!(TemplateArgumentList) expected following TemplateIdentifier", IProblem.SEVERITY_ERROR, IProblem.TEMPLATE_ARGUMENT_LIST_EXPECTED, token.ptr, token.len);
	    	return tiargs;
	    }
	    nextToken();

	    // Get TemplateArgumentList
	    if (token.value != TOKrparen)
	    {
		while (true)
		{
		    // See if it is an Expression or a Type
		    if (isDeclaration(token, 0, TOKreserved, null))
		    {	// Type
			Type ta;

			// Get TemplateArgument
			ta = parseBasicType();
			ta = parseDeclarator(ta, null);
			tiargs.add(ta);
		    }
		    else
		    {	// Expression
			Expression ea;

			ea = parseAssignExp();
			tiargs.add(ea);
		    }
		    if (token.value != TOKcomma)
			break;
		    nextToken();
		}
	    }
	    check(TOKrparen, "template argument list");
	    return tiargs;
	}
	
	private Import parseImport(List<IDElement> decldefs, boolean isstatic) {
		ImportDeclaration importDeclaration = new ImportDeclaration();
		importDeclaration.start = token.ptr;
		importDeclaration.imports = new ArrayList<IImport>();
		
		Import s = null;
	    Identifier id;
	    Identifier aliasid = null;
	    List<Identifier> a;
	    
	    int impStart = -1;
	    int qNameStart = -1;
	    int qNameEnd = -1;
	    
	    Token importToken = new Token(token);

	    //printf("Parser::parseImport()\n");
	    boolean repeat = true;
	    while(repeat) {
	    	repeat = false;
		    the_do:
		    do
		    {
		    	// L1:
			nextToken();
			
			if (impStart == - 1) {
				impStart = token.ptr;
			}
			
			qNameStart = token.ptr;
			
			if (token.value != TOKidentifier)
			{   
				problem("Identifier expected following import", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, importToken.ptr, importToken.len);
			    break;
			}
	
			loc = new Loc(this.loc);
			a = null;
			id = new Identifier(token);
			
			qNameEnd = token.ptr + token.len;
			
			nextToken();
			if (aliasid == null && token.value == TOKassign)
			{
			    aliasid = id;
			    // goto L1;
			    repeat = true;
			    break the_do;
			}
			while (token.value == TOKdot)
			{
			    if (a == null)
				a = new ArrayList<Identifier>();
			    a.add(id);
			    nextToken();
			    if (token.value != TOKidentifier)
			    {   
			    	problem("Identifier expected following package", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, qNameStart, token.ptr - qNameStart);
			    	break;
			    }
			    id = new Identifier(token);
			    nextToken();
			}
	
			s = new Import(a, token.ident, aliasid, isstatic);
			s.start = impStart;
			
			importDeclaration.imports.add(s);
	
			/* Look for
			 *	: alias=name, alias=name;
			 * syntax.
			 */
			if (token.value == TOKcolon)
			{
				Token dotToken = new Token(token);
				
				s.qName.start = qNameStart;
				s.qName.length = qNameEnd - qNameStart;
				
			    do
			    {	Identifier name;
				Identifier alias;
	
				nextToken();
				
				if (token.value != TOKidentifier)
				{   
					problem("Identifier expected following ':'", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, dotToken.ptr, dotToken.len);
				    break;
				}
				alias = new Identifier(token);
				
				s.length = token.ptr + token.len - s.start;
				
				nextToken();
				if (token.value == TOKassign)
				{
				    nextToken();
				    if (token.value != TOKidentifier)
				    {   
				    	problem("Identifier expected following " + alias + " = ", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, alias.start, prevToken.ptr - alias.start + prevToken.len);
				    	break;
				    }
				    name = new Identifier(token);
				    s.length = token.ptr + token.len - s.start;
				    nextToken();
				}
				else
				{   name = alias;
				    alias = null;
				}
				s.addAlias(name, alias);
			    } while (token.value == TOKcomma);
			    break;	// no comma-separated imports of this form
			} else {
				s.length = token.ptr - s.start;
				s.qName.start = qNameStart;
				s.qName.length = token.ptr - s.qName.start;
			}
	
			aliasid = null;
		    } while (token.value == TOKcomma);
	    }
	    
    	importDeclaration.length = token.ptr + token.len - importDeclaration.start;
    	decldefs.add(importDeclaration);
	    
	    if (token.value == TOKsemicolon)
	 	nextToken();
	    else
	    {
	    	problem("';' expected following import declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, importToken.ptr, importToken.len);
	    	nextToken();
	    }

	    return null;
	}
	
	private Type parseBasicType() {
		Type t;
		Identifier id;
		TypeQualified tid;
		TemplateInstance tempinst;

		// printf("parseBasicType()\n");
		switch (token.value) {
		// CASE_BASIC_TYPES_X(t):
		case TOKvoid:
		case TOKint8:
		case TOKuns8:
		case TOKint16:
		case TOKuns16:
		case TOKint32:
		case TOKuns32:
		case TOKint64:
		case TOKuns64:
		case TOKfloat32:
		case TOKfloat64:
		case TOKfloat80:
		case TOKimaginary32:
		case TOKimaginary64:
		case TOKimaginary80:
		case TOKcomplex32:
		case TOKcomplex64:
		case TOKcomplex80:
		case TOKbit:
		case TOKbool:
		case TOKchar:
		case TOKwchar:
		case TOKdchar:
			t = Type.fromTOK(token.value);
			t.start = token.ptr;
			t.length = token.len;
			nextToken();
			break;

		case TOKidentifier:
			id = new Identifier(token);
			nextToken();
			if (token.value == TOKnot) {
				nextToken();
				tempinst = new TemplateInstance(loc, id);
				tempinst.tiargs = parseTemplateArgumentList();
				
				tid = new TypeInstance(loc, tempinst);
				// goto Lident2;
				while (token.value == TOKdot) {
					nextToken();
					if (token.value != TOKidentifier) {
						problem("Identifier expected", IProblem.SEVERITY_ERROR,
								IProblem.IDENTIFIER_EXPECTED, token.ptr,
								token.len);
						break;
					}
					id = new Identifier(token);
					nextToken();
					if (token.value == TOKnot) {
						nextToken();
						tempinst = new TemplateInstance(loc, id);
						tempinst.tiargs = parseTemplateArgumentList();
						tid.addIdent((Identifier) tempinst);
					} else
						tid.addIdent(id);
				}
				t = tid;
				break;

			}
			// Lident:
			tid = new TypeIdentifier(loc, id);
			tid.start = prevToken.ptr;
			// Lident2:
			while (token.value == TOKdot) {
				nextToken();
				if (token.value != TOKidentifier) {
					problem("Identifier expected", IProblem.SEVERITY_ERROR,
							IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					break;
				}
				id = token.ident;
				nextToken();
				if (token.value == TOKnot) {
					nextToken();
					tempinst = new TemplateInstance(loc, id);
					tempinst.tiargs = parseTemplateArgumentList();
					tid.addIdent((Identifier) tempinst);
				} else
					tid.addIdent(id);
			}
			tid.length = prevToken.ptr + prevToken.len - tid.start;
			t = tid;
			break;

		case TOKdot:
			id = new Identifier(Id.empty, TOKidentifier);
			// goto Lident;
			tid = new TypeIdentifier(loc, id);
			while (token.value == TOKdot) {
				nextToken();
				if (token.value != TOKidentifier) {
					problem("Identifier expected", IProblem.SEVERITY_ERROR,
							IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					break;
				}
				id = token.ident;
				nextToken();
				if (token.value == TOKnot) {
					nextToken();
					tempinst = new TemplateInstance(loc, id);
					tempinst.tiargs = parseTemplateArgumentList();
					tid.addIdent((Identifier) tempinst);
				} else
					tid.addIdent(id);
			}
			t = tid;
			break;

		case TOKtypeof: {
			int start = token.ptr;
			Expression exp;

			nextToken();
			check(TOKlparen);
			exp = parseExpression();
			check(TOKrparen);
			tid = new TypeTypeof(loc, exp);
			tid.start = start;
			// goto Lident2;
			while (token.value == TOKdot) {
				nextToken();
				if (token.value != TOKidentifier) {
					problem("Identifier expected", IProblem.SEVERITY_ERROR,
							IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					break;
				}
				id = token.ident;
				nextToken();
				if (token.value == TOKnot) {
					nextToken();
					tempinst = new TemplateInstance(loc, id);
					tempinst.tiargs = parseTemplateArgumentList();
					tid.addIdent((Identifier) tempinst);
				} else
					tid.addIdent(id);
			}
			tid.length = prevToken.ptr + prevToken.len - tid.start;
			t = tid;
			break;
		}

		default:
			problem("Basic type expected", IProblem.SEVERITY_ERROR,
					IProblem.BASIC_TYPE_EXPECTED, token.ptr, token.len);
			t = Type.fromTOK(TOK.TOKint32);
			break;
		}
		return t;
	}
	
	private Type parseBasicType2(Type t) {
		Type ts;
	    Type ta;
	    Type subType;

	    //printf("parseBasicType2()\n");
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKmul:
		    	subType = t;
				t = new TypePointer(t);
				t.start = subType.start;
				t.length = token.ptr + token.len - t.start;
				nextToken();
				continue;

		    case TOKlbracket:
		    	if (LTORARRAYDECL) {
					// Handle []. Make sure things like
					//     int[3][1] a;
					// is (array[1] of array[3] of int)
					nextToken();
					if (token.value == TOKrbracket)
					{
						subType = t;
					    t = new TypeDArray(t);			// []
					    t.start = subType.start;
						t.length = token.ptr + token.len - t.start;
					    nextToken();
					}
					else if (isDeclaration(token, 0, TOKrbracket, null))
					{   // It's an associative array declaration
						subType = t;
					    Type index;
		
					    //printf("it's an associative array\n");
					    index = parseBasicType();
					    index = parseDeclarator(index, null);	// [ type ]
					    t = new TypeAArray(t, index);
					    t.start = subType.start;
					    t.length = token.ptr + token.len - t.start;
					    check(TOKrbracket);
					}
					else
					{
						subType = t;
						
					    //printf("it's [expression]\n");
					    Expression e = parseExpression();		// [ expression ]
					    t = new TypeSArray(t,e);
					    t.start = subType.start;
					    t.length = token.ptr + token.len - t.start;
					    check(TOKrbracket);
					}
					continue;
		    	} else {
					// Handle []. Make sure things like
					//     int[3][1] a;
					// is (array[3] of array[1] of int)
					ts = t;
					while (token.value == TOKlbracket)
					{
					    nextToken();
					    if (token.value == TOKrbracket)
					    {
						ta = new TypeDArray(t);			// []
						nextToken();
					    }
					    else if (isDeclaration(token, 0, TOKrbracket, null))
					    {   // It's an associative array declaration
						Type index;
		
						//printf("it's an associative array\n");
						index = parseBasicType();
						index = parseDeclarator(index, null);	// [ type ]
						check(TOKrbracket);
						ta = new TypeAArray(t, index);
					    }
					    else
					    {
						//printf("it's [expression]\n");
						Expression e = parseExpression();	// [ expression ]
						ta = new TypeSArray(t,e);
						check(TOKrbracket);
					    }
					    
					    if (ts != t) {
							Type pt = ts;
							while(pt.next != t) {
								pt = pt.next;
							}
							pt.next = ta;
						} else {
							ts = ta;
						}
					}
					t = ts;
					continue;
		    	}

		    case TOKdelegate:
		    case TOKfunction:
		    {	// Handle delegate declaration:
			//	t delegate(parameter list)
			//	t function(parameter list)
			List<Argument> arguments;
			int varargs = 0;
			TOK save = token.value;

			nextToken();
			
			int[] pointer2_varargs = { varargs };
			arguments = parseParameters(pointer2_varargs);
			varargs = pointer2_varargs[0];
			
			int saveStart = t.start;
			
			t = new TypeFunction(arguments, t, varargs, linkage);
			if (save == TOKdelegate) {
			    t = new TypeDelegate(t);
			}
			else {
				TypePointer tp = new TypePointer(t);
			    t = tp;	// pointer to function
			}
			t.start = saveStart;
			t.length = prevToken.ptr + prevToken.len - t.start;
			continue;
		    }

		    default:
			ts = t;
			break;
		}
		break;
	    }
	    return ts;
	}
	
	private Type parseDeclarator(Type targ, Identifier[] ident) {
		return parseDeclarator(targ, ident, null, null);
	}

	private Type parseDeclarator(Type t, Identifier[] pident, List<TemplateParameter>[] tpl, int[] identStart) {
		Type ts;
	    Type ta;

	    //printf("parseDeclarator(tpl = %p)\n", tpl);
	    t = parseBasicType2(t);

	    switch (token.value)
	    {

		case TOKidentifier:
		    if (pident != null) {
		    	pident[0] = new Identifier(token);
		    	if (identStart != null) identStart[0] = token.ptr;
		    } else {
		    	problem("Unexpected identifier in declarator", IProblem.SEVERITY_ERROR, IProblem.UNEXPECTED_IDENTIFIER_IN_DECLARATOR, token.ptr, token.len);
		    }
		    ts = t;
		    nextToken();
		    break;

		case TOKlparen:
			int oldStart = t.start;
		    nextToken();
		    ts = parseDeclarator(t, pident, null, identStart);
		    ts.start = oldStart;
		    ts.length = token.ptr + token.len - ts.start;
		    check(TOKrparen);
		    break;

		default:
		    ts = t;
		    break;
	    }

	    while (true)
	    {
		switch (token.value)
		{
	//#if CARRAYDECL
		    case TOKlbracket:
		    {	// This is the old C-style post [] syntax.
			nextToken();
			if (token.value == TOKrbracket)
			{
			    ta = new TypeDArray(t);			// []
			    nextToken();
			}
			else if (isDeclaration(token, 0, TOKrbracket, null))
			{   // It's an associative array declaration
			    Type index;

			    //printf("it's an associative array\n");
			    index = parseBasicType();
			    index = parseDeclarator(index, null, null, identStart);	// [ type ]
			    check(TOKrbracket);
			    ta = new TypeAArray(t, index);
			}
			else
			{
			    //printf("it's [expression]\n");
			    Expression e = parseExpression();		// [ expression ]
			    ta = new TypeSArray(t, e);
			    ta.start = t.start;
			    ta.length = token.ptr + token.len - ta.start;
			    check(TOKrbracket);
			}
			
			if (ts != t) {
				Type pt = ts;
				while(pt.next != t) {
					pt = pt.next;
				}
				pt.next = ta;
			} else {
				ts = ta;
			}
			t = ts;
			continue;
		    }
	//#endif
		    case TOKlparen:
		    {	List<Argument> arguments;
			int varargs = 0;

			if (tpl != null)
			{
			    /* Look ahead to see if this is (...)(...),
			     * i.e. a function template declaration
			     */
			    if (peekPastParen(token).value == TOKlparen)
			    {   // It's a function template declaration
				//printf("function template declaration\n");

				// Gather template parameter list
				tpl[0] = parseTemplateParameterList();
			    }
			}

			int[] pointer2_varargs = { varargs };
			arguments = parseParameters(pointer2_varargs);
			varargs = pointer2_varargs[0];
			
			ta = new TypeFunction(arguments, t, varargs, linkage);
			ta.start = t.start;
			ta.length = t.length;

			if (ts != t) {
				Type pt = ts;
				while(pt.next != t) {
					pt = pt.next;
				}
				pt.next = ta;
			} else {
				ts = ta;
			}
			t = ts;
			break;
		    }
		}
		break;
	    }

	    return ts;
	}
	
	@SuppressWarnings("unchecked")
	private List<IDElement> parseDeclarations() {
		int storage_class;
		int stc;
		Type ts;
		Type t;
		Type tfirst;
		Identifier ident;
		List a;
		TOK tok;
		String comment = token.blockComment;
		int commentStart = token.blockCommentPtr;
		LINK link = linkage;
		
		Token firstToken = new Token(token);

		// printf("parseDeclarations()\n");
		switch (token.value) {
		case TOKtypedef:
		case TOKalias:
			tok = token.value;
			nextToken();
			break;

		default:
			tok = TOKreserved;
			break;
		}

		storage_class = STCundefined;
		while (true) {
			switch (token.value) {
			case TOKconst:
			case TOKstatic:
			case TOKfinal:
			case TOKauto:
			case TOKoverride:
			case TOKabstract:
			case TOKsynchronized:
			case TOKdeprecated:
				stc = STC.fromToken(token.value);
				// goto L1;
				if ((storage_class & stc) != 0) {
					problem("Redundant storage class", IProblem.SEVERITY_ERROR, IProblem.REDUNDANT_STORAGE_CLASS, token.ptr, token.len);
				}
				storage_class |= stc;
				nextToken();
				continue;

			case TOKextern:
				if (peek(token).value != TOKlparen) {
					stc = STCextern;
					if ((storage_class & stc) != 0) {
						problem("Redundant storage class", IProblem.SEVERITY_ERROR, IProblem.REDUNDANT_STORAGE_CLASS, token.ptr, token.len);
					}
					storage_class |= stc;
					nextToken();
					continue;
				}

				link = parseLinkage();
				continue;

			default:
				break;
			}
			break;
		}

		a = new ArrayList();

		/*
		 * Look for auto initializers: storage_class identifier = initializer;
		 */
		if (storage_class != 0 && token.value == TOKidentifier
				&& peek(token).value == TOKassign) {
			ident = token.ident;
			nextToken();
			nextToken();
			Initializer init = parseInitializer();
			VarDeclaration v = new VarDeclaration(loc, null, ident, init);
			v.storage_class = storage_class;
			a.add(v);
			if (token.value == TOKsemicolon) {
				nextToken();
				addComment(v, comment, commentStart);
			} else {
				problem("Semicolon expected following auto declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
			}
			return a;
		}

		if (token.value == TOKclass) {
			AggregateDeclaration s;

			s = (AggregateDeclaration) parseAggregate();
			s.storage_class |= storage_class;
			a.add(s);
			addComment(s, comment, commentStart);
			return a;
		}
		
		int nextVarStart = token.ptr;
		int nextTypdefOrAliasStart = firstToken.ptr;

		ts = parseBasicType();
		ts = parseBasicType2(ts);
		tfirst = null;
		
		int[] identStart = new int[1];
		
		while (true) {
			Loc loc = new Loc(this.loc);
			List<TemplateParameter> tpl = null;

			ident = null;
			Identifier[] pointer2_ident = { ident };
			List[] pointer2_tpl = { tpl };
			t = parseDeclarator(ts, pointer2_ident, pointer2_tpl, identStart);
			ident = pointer2_ident[0];
			tpl = pointer2_tpl[0];
			assert (t != null);
			if (tfirst == null)
				tfirst = t;
			else if (t != tfirst) {
				problem("Multiple declarations must have the same type", IProblem.SEVERITY_ERROR, IProblem.MULTIPLE_DECLARATIONS_MUST_HAVE_THE_SAME_TYPE,
						ident.start, ident.length);
			}
			if (ident == null) {
				problem("No identifier for declarator", IProblem.SEVERITY_ERROR, IProblem.NO_IDENTIFIER_FOR_DECLARATION, t.getOffset(), t.getLength());
			}

			if (tok == TOKtypedef || tok == TOKalias) {
				Declaration v;
				Initializer init;
				
				init = null;
				
				Token tokAssign = new Token(token);
				if (token.value == TOKassign) {
					nextToken();
					init = parseInitializer();
				}
				if (tok == TOKtypedef) {
					TypedefDeclaration td = new TypedefDeclaration(loc, ident, t, init);
					td.start = nextTypdefOrAliasStart;
					v = td;
				} else {
					if (init != null) {
						problem("Alias cannot have initializer", IProblem.SEVERITY_ERROR, IProblem.ALIAS_CANNOT_HAVE_INITIALIZER, tokAssign.ptr, init.start + init.length - tokAssign.ptr);
					}
					AliasDeclaration al = new AliasDeclaration(loc, ident, t);
					al.start = nextTypdefOrAliasStart;
					v = al;
				}
				v.storage_class = storage_class;
				a.add(v);
				switch (token.value) {
				case TOKsemicolon:
					v.length = token.ptr + token.len - v.start;
					nextToken();
					addComment(v, comment, commentStart);
					break;

				case TOKcomma:
					v.length = prevToken.ptr + prevToken.len - v.start;
					nextToken();
					nextTypdefOrAliasStart = token.ptr;
					addComment(v, comment, commentStart);
					continue;

				default:
					problem("Semicolon expected to close declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, v.start, prevToken.ptr + prevToken.len - v.start);
					break;
				}
			} else if (t.ty == Tfunction) {
				FuncDeclaration f;
				Dsymbol s;

				f = new FuncDeclaration(loc, 0, ident, storage_class, t);
				f.start = t.start;
				addComment(f, comment, commentStart);
				parseContracts(f);
				f.length = prevToken.ptr + prevToken.len - f.start;
				
				addComment(f, null);
				if (link == linkage) {
					s = f;
				} else {
					List ax = new ArrayList();
					ax.add(f);
					s = new LinkDeclaration(link, ax);
				}
				if (tpl != null) // it's a function template
				{
					f.templateParameters = tpl.toArray(new ITemplateParameter[tpl.size()]);
					/*
					List decldefs;
					TemplateDeclaration tempdecl;

					// Wrap a template around the aggregate declaration
					decldefs = new ArrayList();
					decldefs.add(s);
					tempdecl = new TemplateDeclaration(loc, s.ident, tpl,
							decldefs);
					s = tempdecl;
					*/
				}
				addComment(s, comment);
				a.add(s);
			} else {
				VarDeclaration v;
				Initializer init;

				init = null;
				if (token.value == TOKassign) {
					nextToken();
					init = parseInitializer();
				}
				v = new VarDeclaration(loc, t, ident, init);
				v.start = nextVarStart;
				v.storage_class = storage_class;
				a.add(v);
				switch (token.value) {
				case TOKsemicolon:
					v.length = token.ptr + token.len - v.start;
					nextToken();
					addComment(v, comment, commentStart);
					break;

				case TOKcomma:
					v.length = prevToken.ptr + prevToken.len - v.start;
					nextToken();
					nextVarStart = token.ptr;
					addComment(v, comment, commentStart);
					continue;

				default:
					problem("Semicolon expected to close declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, v.start, prevToken.ptr + prevToken.len - v.start);
					break;
				}
			}
			break;
		}
		return a;
	}
	
	private void parseContracts(FuncDeclaration f) {
		// Type tb; // <-- not used
		LINK linksave = linkage;

		// The following is irrelevant, as it is overridden by sc->linkage in
		// TypeFunction::semantic
		linkage = LINKd; // nested functions have D linkage

		boolean repeat = true;
		while (repeat) {
			repeat = false;
			// L1:
			switch (token.value) {
			case TOKlcurly:
				if (f.frequire != null || f.fensure != null) {
					problem("Missing body { ... } after in or out", IProblem.SEVERITY_ERROR, IProblem.MISSING_BODY_AFTER_IN_OR_OUT,
							f.getName().getOffset(), f.getName().getLength());
				}
				f.fbody = parseStatement(PSsemi);
				f.endloc = endloc;
				f.length = prevToken.ptr + prevToken.len - f.start;
				break;

			case TOKbody:
				nextToken();
				f.fbody = parseStatement(PScurly);
				f.endloc = endloc;
				break;

			case TOKsemicolon:
				if (f.frequire != null || f.fensure != null) {
					problem("Missing body { ... } after in or out", IProblem.SEVERITY_ERROR, IProblem.MISSING_BODY_AFTER_IN_OR_OUT,
							f.getName().getOffset(), f.getName().getLength());
				}
				f.length = token.ptr + token.len - f.start;
				nextToken();
				break;

			/*
			 * #if 0 // Do we want this for function declarations, so we can do: //
			 * int x, y, foo(), z; case TOKcomma: nextToken(); continue; #endif
			 */

			/*
			 * #if 0 // Dumped feature case TOKthrow: if (!f->fthrows)
			 * f->fthrows = new Array(); nextToken(); check(TOKlparen); while
			 * (1) { tb = parseBasicType(); f->fthrows->push(tb); if
			 * (token.value == TOKcomma) { nextToken(); continue; } break; }
			 * check(TOKrparen); goto L1; #endif
			 */

			case TOKin:
				if (f.frequire != null) {
					problem("Redundant 'in' statement", IProblem.SEVERITY_ERROR, IProblem.REDUNDANT_IN_STATEMENT,
							token.ptr, token.len);
				}
				nextToken();
				
				f.frequire = parseStatement(PScurly | PSscope);
				repeat = true;
				break;

			case TOKout:
				// parse: out (identifier) { statement }
				
				if (f.fensure != null) {
					problem("Redundant 'out' statement", IProblem.SEVERITY_ERROR, IProblem.REDUNDANT_OUT_STATEMENT,
							token.ptr, token.len);
				}
				
				nextToken();
				if (token.value != TOKlcurly) {
					check(TOKlparen);
					if (token.value != TOKidentifier) {
						problem("Identifier following 'out' expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED,
								token.ptr, token.len);
					}
					f.outId = token.ident;
					nextToken();
					check(TOKrparen);
				}
				
				f.fensure = parseStatement(PScurly | PSscope);
				repeat = true;
				break;

			default:
				problem("Semicolon expected following function declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
				break;
			}
		}
		linkage = linksave;
	}
	
	private Initializer parseInitializer() {
		StructInitializer is;
		ArrayInitializer ia;
		ExpInitializer ie;
		Expression e;
		Identifier id;
		Initializer value;
		int comma;
		Loc loc = new Loc(this.loc);
		Token t;

		switch (token.value) {
		case TOKlcurly:
			is = new StructInitializer(loc);
			nextToken();
			comma = 0;
			while (true) {
				switch (token.value) {
				case TOKidentifier:
					if (comma == 1) {
						problem("Comma expected separating field initializers",
								IProblem.SEVERITY_ERROR,
								IProblem.COMMA_EXPECTED, token.ptr, token.len);
					}
					t = peek(token);
					if (t.value == TOKcolon) {
						id = token.ident;
						nextToken();
						nextToken(); // skip over ':'
					} else {
						id = null;
					}
					value = parseInitializer();
					is.addInit(id, value);
					comma = 1;
					continue;

				case TOKcomma:
					nextToken();
					comma = 2;
					continue;

				case TOKrcurly: // allow trailing comma's
					nextToken();
					break;

				default:
					value = parseInitializer();
					is.addInit(null, value);
					comma = 1;
					continue;
				}
				break;
			}
			return is;

		case TOKlbracket:
			ia = new ArrayInitializer(loc);
			nextToken();
			comma = 0;
			while (true) {
				switch (token.value) {
				default:
					if (comma == 1) {
						problem("Comma expected separating array initializers",
								IProblem.SEVERITY_ERROR,
								IProblem.COMMA_EXPECTED, token.ptr, token.len);
						nextToken();
						break;
					}
					e = parseAssignExp();
					if (e == null)
						break;
					if (token.value == TOKcolon) {
						nextToken();
						value = parseInitializer();
					} else {
						value = new ExpInitializer(e.loc, e);
						e = null;
					}
					ia.addInit(e, value);
					comma = 1;
					continue;

				case TOKlcurly:
				case TOKlbracket:
					if (comma == 1) {
						problem("Comma expected separating array initializers",
							IProblem.SEVERITY_ERROR,
							IProblem.COMMA_EXPECTED, token.ptr, token.len);
					}
					
					value = parseInitializer();
					ia.addInit(null, value);
					comma = 1;
					continue;

				case TOKcomma:
					nextToken();
					comma = 2;
					continue;

				case TOKrbracket: // allow trailing comma's
					nextToken();
					break;

				case TOKeof:
					problem("Array initializer expected",
							IProblem.SEVERITY_ERROR,
							IProblem.FOUND_SOMETHING_WHEN_EXPECTING_SOMETHING, token.ptr, token.len);
					break;
				}
				break;
			}
			return ia;

		case TOKvoid:
			t = peek(token);
			if (t.value == TOKsemicolon) {
				nextToken();
				VoidInitializer init = new VoidInitializer(loc);
				init.start = prevToken.ptr;
				init.length = prevToken.len;
				return init;
			}
			// goto Lexpression;

		default:
			// Lexpression:
			e = parseAssignExp();
			ie = new ExpInitializer(loc, e);
			return ie;
		}
	}
	
	@SuppressWarnings("unchecked") 
	public Statement parseStatement(int flags) {
		Statement s;
		Token t;
		Condition condition;
		Statement ifbody;
		Statement elsebody;
		Loc loc = new Loc(this.loc);

		// printf("parseStatement()\n");

		if ((flags & PScurly) != 0 && token.value != TOKlcurly) {
			problem("Statement expected to be { }",
					IProblem.SEVERITY_ERROR,
					IProblem.STATEMENT_EXPECTED_TO_BE_CURLIES, token.ptr, token.len);
		}

		switch (token.value) {
		case TOKidentifier:
			// Need to look ahead to see if it is a declaration, label, or
			// expression
			t = peek(token);
			if (t.value == TOKcolon) { // It's a label
				Identifier ident;

				ident = new Identifier(token);
				nextToken();
				nextToken();
				Statement body = parseStatement(PSsemi);
				s = new LabelStatement(loc, ident, body);
				s.start = ident.start;
				s.length = body.start + body.length - s.start;
				break;
			}
			// fallthrough to TOKdot
		case TOKdot:
		case TOKtypeof:
			if (isDeclaration(token, 2, TOKreserved, null)) {
				// goto Ldeclaration;
				List a;

				a = parseDeclarations();
				if (a.size() > 1) {
					List<Statement> as = new ArrayList<Statement>(a.size());
					for (int i = 0; i < a.size(); i++) {
						Dsymbol d = (Dsymbol) a.get(i);
						s = new DeclarationStatement(loc, d);
						s.start = d.start;
						s.length = d.length;
						as.add(s);
					}
					s = new CompoundStatement(loc, as);
				} else if (a.size() == 1) {
					Dsymbol d = (Dsymbol) a.get(0);
					s = new DeclarationStatement(loc, d);
					s.start = d.start;
					s.length = d.length;
				} else {
					assert (false);
					s = null;
				}
				if ((flags & PSscope) != 0) {
					s = new ScopeStatement(loc, s);
				}
				break;
			} else {
				// goto Lexp;
				Expression exp;

				exp = parseExpression();
				check(TOKsemicolon, "statement");
				s = new ExpStatement(loc, exp);
				break;
			}
			// break;

		case TOKassert:
		case TOKthis:
		case TOKsuper:
		case TOKint32v:
		case TOKuns32v:
		case TOKint64v:
		case TOKuns64v:
		case TOKfloat32v:
		case TOKfloat64v:
		case TOKfloat80v:
		case TOKimaginary32v:
		case TOKimaginary64v:
		case TOKimaginary80v:
		case TOKcharv:
		case TOKwcharv:
		case TOKdcharv:
		case TOKnull:
		case TOKtrue:
		case TOKfalse:
		case TOKstring:
		case TOKlparen:
		case TOKcast:
		case TOKmul:
		case TOKmin:
		case TOKadd:
		case TOKplusplus:
		case TOKminusminus:
		case TOKnew:
		case TOKdelete:
		case TOKdelegate:
		case TOKfunction:
		case TOKtypeid:
		case TOKis:
		case TOKlbracket:
			// Lexp:
		{
			Expression exp;

			exp = parseExpression();
			Token semiToken = new Token(token);
			check(TOKsemicolon, "statement");
			s = new ExpStatement(loc, exp);
			s.start = exp.start;
			s.length = semiToken.ptr + semiToken.len - s.start;
			break;
		}

		case TOKstatic: { // Look ahead to see if it's static assert() or
							// static if()
			Token t2;
			
			Token saveToken = new Token(token);
			
			t2 = peek(token);
			if (t2.value == TOKassert) {
				nextToken();
				s = new StaticAssertStatement(parseStaticAssert());
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;
			}
			if (t2.value == TOKif) {
				nextToken();
				condition = parseStaticIfCondition();
				// goto Lcondition;
				ifbody = parseStatement(PSsemi);
				elsebody = null;
				if (token.value == TOKelse) {
					nextToken();
					elsebody = parseStatement(PSsemi);
				}
				s = new ConditionalStatement(loc, condition, ifbody, elsebody);
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;
			}
			// goto Ldeclaration;
			List a;

			a = parseDeclarations();
			if (a.size() > 1) {
				List<Statement> as = new ArrayList<Statement>(a.size());
				for (int i = 0; i < a.size(); i++) {
					Dsymbol d = (Dsymbol) a.get(i);
					s = new DeclarationStatement(loc, d);
					s.start = d.start;
					s.length = d.length;
					as.add(s);
				}
				s = new CompoundStatement(loc, as);
			} else if (a.size() == 1) {
				Dsymbol d = (Dsymbol) a.get(0);
				s = new DeclarationStatement(loc, d);
				s.start = d.start;
				s.length = d.length;
			} else {
				assert (false);
				s = null;
			}
			if ((flags & PSscope) != 0) {
				s = new ScopeStatement(loc, s);
			}
			break;
		}

			// CASE_BASIC_TYPES:
		case TOKwchar:
		case TOKdchar:
		case TOKbit:
		case TOKbool:
		case TOKchar:
		case TOKint8:
		case TOKuns8:
		case TOKint16:
		case TOKuns16:
		case TOKint32:
		case TOKuns32:
		case TOKint64:
		case TOKuns64:
		case TOKfloat32:
		case TOKfloat64:
		case TOKfloat80:
		case TOKimaginary32:
		case TOKimaginary64:
		case TOKimaginary80:
		case TOKcomplex32:
		case TOKcomplex64:
		case TOKcomplex80:
		case TOKvoid:
		case TOKtypedef:
		case TOKalias:
		case TOKconst:
		case TOKauto:
		case TOKextern:
			// case TOKtypeof:
			// Ldeclaration:
		{
			List a;

			a = parseDeclarations();
			if (a.size() > 1) {
				List<Statement> as = new ArrayList<Statement>(a.size());
				for (int i = 0; i < a.size(); i++) {
					Dsymbol d = (Dsymbol) a.get(i);
					s = new DeclarationStatement(loc, d);
					s.start = d.start;
					s.length = d.length;
					as.add(s);
				}
				s = new CompoundStatement(loc, as);
			} else if (a.size() == 1) {
				Dsymbol d = (Dsymbol) a.get(0);
				s = new DeclarationStatement(loc, d);
				s.start = d.start;
				s.length = d.length;
			} else {
				assert (false);
				s = null;
			}
			if ((flags & PSscope) != 0) {
				s = new ScopeStatement(loc, s);
			}
			break;
		}

		case TOKstruct:
		case TOKunion:
		case TOKclass:
		case TOKinterface: {
			Dsymbol d;

			d = parseAggregate();
			s = new DeclarationStatement(loc, d);
			s.start = d.start;
			s.length = d.length;
			break;
		}

		case TOKenum: {
			Dsymbol d;

			d = parseEnum();
			s = new DeclarationStatement(loc, d);
			s.start = d.start;
			s.length = d.length;
			break;
		}

		case TOKmixin: {
			Dsymbol d;
			
			d = parseMixin();
			s = new DeclarationStatement(loc, d);
			s.start = d.start;
			s.length = d.length;
			break;
		}

		case TOKlcurly: {
			Token saveToken = new Token(token);
			
			List<Statement> statements;

			nextToken();
			statements = new ArrayList<Statement>();
			
			while (token.value != TOKrcurly) {
				if (token.value == TOKeof) {
					problem("'}' expected", IProblem.SEVERITY_ERROR, IProblem.FOUND_SOMETHING_WHEN_EXPECTING_SOMETHING, prevToken.ptr, prevToken.len);
					break;
				}
				statements.add(parseStatement(PSsemi | PScurlyscope));
			}
			endloc = new Loc(this.loc);
			s = new CompoundStatement(loc, statements);
			if ((flags & (PSscope | PScurlyscope)) != 0) {
				s = new ScopeStatement(loc, s);
			}
			s.start = saveToken.ptr;
			s.length = token.ptr + token.len - s.start;
			nextToken();
			break;
		}

		case TOKwhile: {
			Expression condition2;
			Statement body;
			
			Token saveToken = new Token(token);

			nextToken();
			check(TOKlparen);
			condition2 = parseExpression();
			check(TOKrparen);
			
			body = parseStatement(PSscope);
			s = new WhileStatement(loc, condition2, body);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKsemicolon:
			if ((flags & PSsemi) == 0) {
				problem("Use '{ }' for an empty statement, not a ';'", IProblem.SEVERITY_ERROR, IProblem.USE_BRACES_FOR_AN_EMPTY_STATEMENT, token.ptr, token.len);
			}
			nextToken();
			s = new ExpStatement(loc, null);
			break;

		case TOKdo: {
			Statement body;
			Expression condition2;
			
			Token saveToken = new Token(token);
			nextToken();
			
			body = parseStatement(PSscope);
			
			check(TOKwhile);
			check(TOKlparen);
			condition2 = parseExpression();
			check(TOKrparen);
			s = new DoStatement(loc, body, condition2);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKfor: {
			Statement init;
			Expression condition2;
			Expression increment;
			Statement body;
			
			Token saveToken = new Token(token);

			nextToken();
			check(TOKlparen);
			if (token.value == TOKsemicolon) {
				init = null;
				nextToken();
			} else {
				init = parseStatement(0);
			}
			if (token.value == TOKsemicolon) {
				condition2 = null;
				nextToken();
			} else {
				condition2 = parseExpression();
				check(TOKsemicolon, "for condition");
			}
			if (token.value == TOKrparen) {
				increment = null;
				nextToken();
			} else {
				increment = parseExpression();
				check(TOKrparen);
			}
			body = parseStatement(0);
			s = new ForStatement(loc, init, condition2, increment, body);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKforeach:
		case TOKforeach_reverse:
		{
			TOK op = token.value;
			List arguments;

			// Statement d; // <-- not used
			Statement body;
			Expression aggr;
			
			Token saveToken = new Token(token);

			nextToken();
			check(TOKlparen);

			arguments = new ArrayList();

			while (true) {
				Type tb;
				Identifier ai = null;
				Type at;
				InOut inout;
				Argument a;
				
				Token argumentStart = new Token(token);

				inout = In;
				if (token.value == TOKinout) {
					inout = InOut;
					nextToken();
				}
				if (token.value == TOKidentifier) {
					Token t2 = peek(token);
					if (t2.value == TOKcomma || t2.value == TOKsemicolon) {
						ai = new Identifier(token);
						at = null; // infer argument type
						nextToken();
						// goto Larg;
						a = new Argument(inout, at, ai, null);
						a.start = argumentStart.ptr;
						a.length = prevToken.ptr + prevToken.len - a.start;
						arguments.add(a);
						if (token.value == TOKcomma) {
							nextToken();
							continue;
						}
						break;
					}
				}
				tb = parseBasicType();

				Identifier[] pointer2_ai = { ai };
				at = parseDeclarator(tb, pointer2_ai);
				ai = pointer2_ai[0];
				if (ai == null) {
					problem("No identifier for declarator", IProblem.SEVERITY_ERROR, IProblem.NO_IDENTIFIER_FOR_DECLARATOR, at.start, at.length);
				}
				// Larg:
				a = new Argument(inout, at, ai, null);
				arguments.add(a);
				if (token.value == TOKcomma) {
					nextToken();
					continue;
				}
				break;
			}
			check(TOKsemicolon);

			aggr = parseExpression();
			check(TOKrparen);
			body = parseStatement(0);
			s = new ForeachStatement(loc, op, arguments, aggr, body);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKif: {
			Argument arg = null;
			Expression condition2;
			Statement ifbody2;
			Statement elsebody2;
			
			Token saveToken = new Token(token);

			nextToken();
			check(TOKlparen);

			if (token.value == TOKauto) {
				Token autoToken = new Token(token);
				
				nextToken();
				if (token.value == TOKidentifier) {
					Token t2 = peek(token);
					if (t2.value == TOKassign) {
						arg = new Argument(In, null, new Identifier(token), null);
						arg.start = autoToken.ptr;
						arg.length = token.ptr + token.len - arg.start;
						nextToken();
						nextToken();
					} else {
						problem("'=' expected following auto identifier", IProblem.SEVERITY_ERROR, IProblem.EQUALS_EXPECTED, token.ptr, token.len);
						// goto Lerror;
						while (token.value != TOKrcurly
								&& token.value != TOKsemicolon
								&& token.value != TOKeof)
							nextToken();
						if (token.value == TOKsemicolon)
							nextToken();
						s = null;
						break;
					}
				} else {
					problem("Identifier expected following auto", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					// goto Lerror;
					while (token.value != TOKrcurly
							&& token.value != TOKsemicolon
							&& token.value != TOKeof)
						nextToken();
					if (token.value == TOKsemicolon)
						nextToken();
					s = null;
					break;
				}
			} else {
				Token argToken = new Token(token);
				if (isDeclaration(token, 2, TOKassign, null)) {
					Type tb;
					Type at;
					Identifier ai = null;

					tb = parseBasicType();
					Identifier[] pointer2_ai = { ai };
					at = parseDeclarator(tb, pointer2_ai);
					ai = pointer2_ai[0];
					check(TOKassign);
					arg = new Argument(In, at, ai, null);
					arg.start = argToken.ptr;
					arg.length = prevToken.ptr + prevToken.len - arg.start;
				}

				// Check for " ident;"
				else if (token.value == TOKidentifier) {
					Token t2 = peek(token);
					if (t2.value == TOKcomma || t2.value == TOKsemicolon) {
						arg = new Argument(In, null, token.ident, null);
						nextToken();
						nextToken();
						// if (!global.params.useDeprecated)
						problem("if (v; e) is deprecated, use if (auto v = e)", IProblem.SEVERITY_ERROR, IProblem.DEPRECATED_IF_AUTO, token.ptr, token.len);
					}
				}
			}

			condition2 = parseExpression();
			check(TOKrparen);
			ifbody2 = parseStatement(PSscope);
			if (token.value == TOKelse) {
				nextToken();
				elsebody2 = parseStatement(PSscope);
			} else
				elsebody2 = null;
			s = new IfStatement(loc, arg, condition2, ifbody2, elsebody2);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKscope:
			Token saveToken = new Token(token);
			
			nextToken();
			check(TOKlparen);
			if (token.value != TOKidentifier) {
				problem("Scope identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				// goto Lerror;
				while (token.value != TOKrcurly && token.value != TOKsemicolon
						&& token.value != TOKeof)
					nextToken();
				if (token.value == TOKsemicolon)
					nextToken();
				s = null;
				break;
			} else {
				TOK t2 = TOKon_scope_exit;
				Identifier id = new Identifier(token);

				if (id.string.equals(Id.exit))
					t2 = TOKon_scope_exit;
				else if (id.string.equals(Id.failure))
					t2 = TOKon_scope_failure;
				else if (id.string.equals(Id.success))
					t2 = TOKon_scope_success;
				else {
					problem("Valid scope identifiers are exit, failure, or success", IProblem.SEVERITY_ERROR, IProblem.INVALID_SCOPE_IDENTIFIER, token.ptr, token.len);
				}
				nextToken();
				check(TOKrparen);
				Statement st = parseStatement(PScurlyscope);
				s = new OnScopeStatement(loc, t2, st);
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
				break;
			}

		case TOKon_scope_exit:
		case TOKon_scope_failure:
		case TOKon_scope_success: {
			
			saveToken = new Token(token);
			
			TOK t2 = token.value;
			// if (!global.params.useDeprecated)
			problem(token.toString() + " is deprecated, use scope", IProblem.SEVERITY_ERROR, IProblem.ON_SCOPE_DEPRECATED, token.ptr, token.len);
			nextToken();
			Statement st = parseStatement(PScurlyscope);
			s = new OnScopeStatement(loc, t2, st);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKdebug:
			saveToken = new Token(token);
			
			nextToken();
			condition = parseDebugCondition();
			// goto Lcondition;
			ifbody = parseStatement(PSsemi);
			elsebody = null;
			if (token.value == TOKelse) {
				nextToken();
				elsebody = parseStatement(PSsemi);
			}
			s = new ConditionalStatement(loc, condition, ifbody, elsebody);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;

		case TOKversion:
			saveToken = new Token(token);
			
			nextToken();
			condition = parseVersionCondition();
			// goto Lcondition;
			ifbody = parseStatement(PSsemi);
			elsebody = null;
			if (token.value == TOKelse) {
				nextToken();
				elsebody = parseStatement(PSsemi);
			}
			s = new ConditionalStatement(loc, condition, ifbody, elsebody);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;

		case TOKiftype:
			saveToken = new Token(token);
			
			condition = parseIftypeCondition();
			// goto Lcondition;
			ifbody = parseStatement(PSsemi);
			elsebody = null;
			if (token.value == TOKelse) {
				nextToken();
				elsebody = parseStatement(PSsemi);
			}
			s = new ConditionalStatement(loc, condition, ifbody, elsebody);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;

		// Lcondition:
		// ifbody = parseStatement(PSsemi);
		// elsebody = null;
		// if (token.value == TOKelse)
		// {
		// nextToken();
		// elsebody = parseStatement(PSsemi);
		// }
		// s = new ConditionalStatement(loc, condition, ifbody, elsebody);
		// break;

		case TOKpragma: {
			Identifier ident;
			List<Expression> args = null;
			Statement body;
			
			saveToken = new Token(token);

			nextToken();
			check(TOKlparen);
			if (token.value != TOKidentifier) {
				problem("Pragma identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				// goto Lerror;
				while (token.value != TOKrcurly && token.value != TOKsemicolon
						&& token.value != TOKeof)
					nextToken();
				if (token.value == TOKsemicolon)
					nextToken();
				s = null;
				break;
			}
			ident = new Identifier(token);
			nextToken();
			if (token.value == TOKcomma)
				args = parseArguments(); // pragma(identifier, args...);
			else
				check(TOKrparen); // pragma(identifier);
			if (token.value == TOKsemicolon) {
				nextToken();
				body = null;
			} else
				body = parseStatement(PSsemi);
			s = new PragmaStatement(loc, ident, args, body);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKswitch: {
			saveToken = new Token(token);
			
			Expression condition2;
			Statement body;

			nextToken();
			check(TOKlparen);
			condition2 = parseExpression();
			check(TOKrparen);
			body = parseStatement(PSscope);
			s = new SwitchStatement(loc, condition2, body);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKcase: {
			Expression exp;
			List<Statement> statements;
			List cases = new ArrayList(); // array of Expression's

			while (true) {
				nextToken();
				exp = parseAssignExp();
				cases.add(exp);
				if (token.value != TOKcomma)
					break;
			}
			check(TOKcolon);

			statements = new ArrayList<Statement>();
			while (token.value != TOKcase && token.value != TOKdefault
					&& token.value != TOKrcurly) {
				statements.add(parseStatement(PSsemi | PScurlyscope));
			}
			s = new CompoundStatement(loc, statements);
			s = new ScopeStatement(loc, s);

			// Keep cases in order by building the case statements backwards
			for (int i = cases.size(); i != 0; i--) {
				exp = (Expression) cases.get(i - 1);
				s = new CaseStatement(loc, exp, s);
			}
			break;
		}

		case TOKdefault: {
			List<Statement> statements;
			
			saveToken = new Token(token);

			nextToken();
			check(TOKcolon);

			statements = new ArrayList<Statement>();
			while (token.value != TOKcase && token.value != TOKdefault
					&& token.value != TOKrcurly) {
				statements.add(parseStatement(PSsemi | PScurlyscope));
			}
			s = new CompoundStatement(loc, statements);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			
			s = new ScopeStatement(loc, s);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			
			s = new DefaultStatement(loc, s);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKreturn: {
			Expression exp;

			saveToken = new Token(token);
			nextToken();
			if (token.value == TOKsemicolon) {
				exp = null;
			} else {
				exp = parseExpression();
			}
			check(TOKsemicolon, "return statement");
			s = new ReturnStatement(loc, exp);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKbreak: {
			Identifier ident;

			saveToken = new Token(token);
			nextToken();
			if (token.value == TOKidentifier) {
				ident = new Identifier(token);
				nextToken();
			} else
				ident = null;
			check(TOKsemicolon, "break statement");
			BreakStatement bs = new BreakStatement(loc, ident);
			bs.start = saveToken.ptr;
			bs.length = prevToken.ptr + prevToken.len - bs.start;
			s = bs;
			break;
		}

		case TOKcontinue: {
			Identifier ident;

			saveToken = new Token(token);
			nextToken();
			if (token.value == TOKidentifier) {
				ident = new Identifier(token);
				nextToken();
			} else
				ident = null;
			check(TOKsemicolon, "continue statement");
			ContinueStatement cs = new ContinueStatement(loc, ident);
			cs.start = saveToken.ptr;
			cs.length = prevToken.ptr + prevToken.len - cs.start;
			s = cs;
			break;
		}

		case TOKgoto: {
			Identifier ident;
			
			saveToken = new Token(token);

			nextToken();
			if (token.value == TOKdefault) {
				nextToken();
				s = new GotoDefaultStatement(loc);
			} else if (token.value == TOKcase) {
				Expression exp = null;

				nextToken();
				if (token.value != TOKsemicolon)
					exp = parseExpression();
				s = new GotoCaseStatement(loc, exp);
			} else {
				if (token.value != TOKidentifier) {
					problem("Identifier expected following goto", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					ident = null;
				} else {
					ident = new Identifier(token);
					nextToken();
				}
				s = new GotoStatement(loc, ident);
			}
			check(TOKsemicolon, "goto statement");
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKsynchronized: {
			Expression exp;
			Statement body;
			
			saveToken = new Token(token);

			nextToken();
			if (token.value == TOKlparen) {
				nextToken();
				exp = parseExpression();
				check(TOKrparen);
			} else
				exp = null;
			body = parseStatement(PSscope);
			s = new SynchronizedStatement(loc, exp, body);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKwith: {
			Expression exp;
			Statement body;
			
			saveToken = new Token(token);

			nextToken();
			check(TOKlparen);
			exp = parseExpression();
			check(TOKrparen);
			
			body = parseStatement(PSscope);
			
			s = new WithStatement(loc, exp, body);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKtry: {
			Statement body;
			List catches = null;
			Statement finalbody = null;
			
			saveToken = new Token(token);
			
			nextToken();
			body = parseStatement(PSscope);
			while (token.value == TOKcatch) {
				Statement handler;
				Catch c;
				Type t2;
				Identifier id;
				Loc loc2 = new Loc(this.loc);
				
				Token firstToken = new Token(token);

				nextToken();
				if (token.value == TOKlcurly) {
					t2 = null;
					id = null;
				} else {
					check(TOKlparen);
					t2 = parseBasicType();
					id = null;
					Identifier[] pointer2_id = { id };
					t2 = parseDeclarator(t2, pointer2_id);
					id = pointer2_id[0];
					check(TOKrparen);
				}
				handler = parseStatement(0);
				c = new Catch(loc2, t2, id, handler);
				c.start = firstToken.ptr;
				c.length = prevToken.ptr + prevToken.len - c.start;
				if (catches == null) {
					catches = new ArrayList();
				}
				catches.add(c);
			}

			if (token.value == TOKfinally) {
				nextToken();
				finalbody = parseStatement(0);
			}

			s = body;
			if (catches == null && finalbody == null) {
				problem("Catch or finally expected following try", IProblem.SEVERITY_ERROR, IProblem.CATCH_OR_FINALLY_EXPECTED, prevToken.ptr, prevToken.len);
			} else {
				if (catches != null) {
					s = new TryCatchStatement(loc, body, catches);
				}
				if (finalbody != null) {
					s = new TryFinallyStatement(loc, s, finalbody);
				}
				s.start = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.start;
			}
			break;
		}

		case TOKthrow: {
			Expression exp;
			
			saveToken = new Token(token);

			nextToken();
			exp = parseExpression();
			check(TOKsemicolon, "throw statement");
			s = new ThrowStatement(loc, exp);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;
		}

		case TOKvolatile:
			saveToken = new Token(token);
			
			nextToken();
			s = parseStatement(PSsemi | PScurlyscope);
			s = new VolatileStatement(loc, s);
			s.start = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.start;
			break;

		case TOKasm: {
			List<Statement> statements;
			Identifier label;
			Loc labelloc = new Loc();
			Token toklist;
			Token[] ptoklist = new Token[1];
			
			saveToken = new Token(token);

			// Parse the asm block into a sequence of AsmStatements,
			// each AsmStatement is one instruction.
			// Separate out labels.
			// Defer parsing of AsmStatements until semantic processing.

			nextToken();
			check(TOKlcurly);
			toklist = null;
			ptoklist[0] = toklist;
			label = null;
			statements = new ArrayList<Statement>();
			while (true) {
				switch (token.value) {
				case TOKidentifier:
					if (toklist == null) {
						// Look ahead to see if it is a label
						t = peek(token);
						if (t.value == TOKcolon) { // It's a label
							label = new Identifier(token);
							labelloc = new Loc(this.loc);
							nextToken();
							nextToken();
							continue;
						}
					}
					// TODO: goto Ldefault;

				case TOKrcurly:
					if (toklist != null || label != null) {
						problem("asm statements must end in ';'", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
					}
					break;

				case TOKsemicolon:
					s = null;
					if (toklist != null || label != null) { // Create
															// AsmStatement from
															// list of tokens
															// we've saved
						s = new AsmStatement(new Loc(this.loc), toklist);
						s.start = saveToken.ptr;
						s.length = token.ptr + token.len - s.start;
						
						toklist = null;
						ptoklist[0] = toklist;
						if (label == null) {
							s = new LabelStatement(labelloc, label, s);
							label = null;
						}
						statements.add(s);
					}
					nextToken();
					continue;

				case TOKeof:
					/* { */
					problem("Matching '}' expected, not end of file", IProblem.SEVERITY_ERROR, IProblem.MATCHING_CURLY_EXPECTED, token.ptr, token.len);
					break;

				default:
					//Ldefault:
					/*
					 * TODO: ptoklist = new Token(); memcpy(*ptoklist, &token,
					 * sizeof(Token)); ptoklist = &(*ptoklist)->next; ptoklist =
					 * NULL;
					 */

					nextToken();
					continue;
				}
				break;
			}
			s = new CompoundStatement(loc, statements);
			s.start = saveToken.ptr;
			s.length = token.ptr + token.len - s.start;
			nextToken();
			break;
		}

		default:
			problem("Statement expected", IProblem.SEVERITY_ERROR, IProblem.STATEMENT_EXPECTED, token.ptr, token.len);
			// goto Lerror;

			// Lerror:
			while (token.value != TOKrcurly && token.value != TOKsemicolon
					&& token.value != TOKeof)
				nextToken();
			if (token.value == TOKsemicolon)
				nextToken();
			s = null;
			break;
		}

		return s;
	}
	
	private void check(TOK value) {
		if (token.value != value) {
			problem("'" + value + "' expected",
					IProblem.SEVERITY_ERROR, IProblem.FOUND_SOMETHING_WHEN_EXPECTING_SOMETHING,
					token.ptr, token.len);
		}
		nextToken();
	}
	
	private void check(TOK value, String string) {
		if (token.value != value) {
			problem("'" + value + "' expected following '" + string + "'",
					IProblem.SEVERITY_ERROR, IProblem.FOUND_SOMETHING_WHEN_EXPECTING_SOMETHING,
					token.ptr, token.len);
		}
		nextToken();
	}
	
	private boolean isDeclaration(Token t, int needId, TOK endtok, Token[] pt) {
		int haveId = 0;

		Token[] pointer2_t = { t };

		if (!isBasicType(pointer2_t)) {
			return false;
		}

		t = pointer2_t[0];

		int[] pointer2_haveId = { haveId };
		if (!isDeclarator(pointer2_t, pointer2_haveId, endtok)) {
			return false;
		}

		t = pointer2_t[0];
		haveId = pointer2_haveId[0];

		if (needId == 1 || (needId == 0 && haveId == 0)
				|| (needId == 2 && haveId != 0)) {
			if (pt != null) {
				pt[0] = t;
			}
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isBasicType(Token[] pt) {
		// This code parallels parseBasicType()
		Token t = pt[0];
		// Token t2; // <-- not used
		// int parens = 0; // <-- not used

		switch (t.value) {
		case TOKwchar: case TOKdchar: case TOKbit: case TOKbool:
		case TOKchar: case TOKint8: case TOKuns8: case TOKint16:
		case TOKuns16: case TOKint32: case TOKuns32: case TOKint64:
		case TOKuns64: case TOKfloat32: case TOKfloat64: case TOKfloat80:
		case TOKimaginary32: case TOKimaginary64: case TOKimaginary80:
		case TOKcomplex32: case TOKcomplex64: case TOKcomplex80:
		case TOKvoid:
			t = peek(t);
			break;

		case TOKidentifier:
			t = peek(t);
			if (t.value == TOKnot) {
				// goto L4
				Token[] pointer2_t2 = { t };
				boolean semiResult = isBasicType_L4(pointer2_t2);
				t = pointer2_t2[0];
				if (semiResult) {
					pt[0] = t;
				}
				return semiResult;
			}
			
			// goto L3
			Token[] pointer2_t2 = { t };
			boolean semiResult = isBasicType_L3(pointer2_t2);
			t = pointer2_t2[0];
			if (semiResult) {
				pt[0] = t;
			}
			return semiResult;

		case TOKdot: {
			// goto Ldot;
			pointer2_t2 = new Token[] { t };
			semiResult = isBasicType_Ldot(pointer2_t2);
			t = pointer2_t2[0];
			if (semiResult) {
				pt[0] = t;
			}
			return semiResult;
		}

		case TOKtypeof:
			/*
			 * typeof(exp).identifier...
			 */
			t = peek(t);
			if (t.value != TOKlparen) {
				return false;
			}

			Token[] pointer2_t = { t };
			if (!skipParens(t, pointer2_t)) {
				return false;
			}

			t = pointer2_t[0];

			// goto L2;
			pointer2_t2 = new Token[] { t };
			semiResult = isBasicType_L2(pointer2_t2);
			t = pointer2_t2[0];
			if (semiResult) {
				pt[0] = t;
			}
			return semiResult;			

		default:
			return false;
		}
		pt[0] = t;
		return true;
	}
	
	private boolean isBasicType_L2(Token[] pt) {
		while (true) {
			/* L2: */
			pt[0] = peek(pt[0]);
			/* L3: */
			if (pt[0].value == TOKdot) {
				/* Ldot: */
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKidentifier) {
					return false;
				}
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKnot) {
					// goto L3
					return isBasicType_L3(pt);
				}
				/* L4: */
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKlparen) {
					return false;
				}

				if (!skipParens(pt[0], pt)) {
					return false;
				}
			} else {
				break;
			}
		}
		
		return true;
	}
	
	private boolean isBasicType_L3(Token[] pt) {
		
		boolean firstTime = true;
		while (true) {
			/* L2: */
			if (!firstTime) {
				pt[0] = peek(pt[0]);
			}
			firstTime = false;
			
			/* L3: */
			if (pt[0].value == TOKdot) {
				/* Ldot: */
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKidentifier) {
					return false;
				}
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKnot) {
					// goto L3;
					return isBasicType_L3(pt);
				}
				/* L4: */
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKlparen) {
					return false;
				}

				if (!skipParens(pt[0], pt)) {
					return false;
				}
			} else {
				break;
			}
		}
		
		return true;
	}
	
	private boolean isBasicType_Ldot(Token[] pt) {
		
		boolean firstTime = true;
		while (true) {
			/* L2: */
			if (!firstTime) {
				pt[0] = peek(pt[0]);
			}
			/* L3: */
			if (pt[0].value == TOKdot || firstTime) {
				/* Ldot: */
				firstTime = false;
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKidentifier) {
					return false;
				}
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKnot) {
					// goto L3;
					return isBasicType_L3(pt);
				}
				/* L4: */
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKlparen) {
					return false;
				}

				if (!skipParens(pt[0], pt)) {
					return false;
				}
			} else {
				break;
			}
		}
		
		return true;
	}
	
	private boolean isBasicType_L4(Token[] pt) {
		
		boolean firstTime = true;
		while (true) {
			/* L2: */
			if (!firstTime) {
				pt[0] = peek(pt[0]);
			}
			/* L3: */
			if (pt[0].value == TOKdot || firstTime) {
				/* Ldot: */
				if (!firstTime) {
					pt[0] = peek(pt[0]);
					if (pt[0].value != TOKidentifier) {
						return false;
					}
					pt[0] = peek(pt[0]);
					if (pt[0].value != TOKnot) {
						// goto L3;
						return isBasicType_L3(pt);
					}
				}
				/* L4: */
				firstTime = false;
				pt[0] = peek(pt[0]);
				if (pt[0].value != TOKlparen) {
					return false;
				}

				if (!skipParens(pt[0], pt)) {
					return false;
				}
			} else {
				break;
			}
		}
		
		return true;
	}
	
	private boolean isDeclarator(Token[] pt, int[] haveId, TOK endtok) {
// This code parallels parseDeclarator()
	    Token t = pt[0];
	    int parens;

	    // printf("Parser::isDeclarator()\n");
	    //t->print();
	    if (t.value == TOKassign)
		return false;

	    while (true)
	    {
		parens = 0;
		switch (t.value)
		{
		    case TOKmul:
		    case TOKand:
			t = peek(t);
			continue;

		    case TOKlbracket:
			t = peek(t);
			if (t.value == TOKrbracket)
			{
			    t = peek(t);
			}
			else { 
				Token[] pointer2_t = { t };
				if (isDeclaration(t, 0, TOKrbracket, pointer2_t))
				{   // It's an associative array declaration
					t = pointer2_t[0];
				    t = peek(t);
				}
				else
				{
					t = pointer2_t[0];
				    // [ expression ]
				    if (!isExpression(pointer2_t))
				    	return false;
				    
				    t = pointer2_t[0];
				    
				    if (t.value != TOKrbracket)
					return false;
				    t = peek(t);
				}
				
			}
			continue;

		    case TOKidentifier:
			if (haveId[0] != 0)
			    return false;
			haveId[0] = 1;
			t = peek(t);
			break;

		    case TOKlparen:
			t = peek(t);

			if (t.value == TOKrparen)
			    return false;		// () is not a declarator

			/* Regard ( identifier ) as not a declarator
			 * BUG: what about ( *identifier ) in
			 *	f(*p)(x);
			 * where f is a class instance with overloaded () ?
			 * Should we just disallow C-style function pointer declarations?
			 */
			if (t.value == TOKidentifier)
			{   Token t2 = peek(t);
			    if (t2.value == TOKrparen)
				return false;
			}


			Token[] pointer2_t = { t };
			if (!isDeclarator(pointer2_t, haveId, TOKrparen))
			    return false;
			
			t = pointer2_t[0];
			
			t = peek(t);
			parens = 1;
			break;

		    case TOKdelegate:
		    case TOKfunction:
			t = peek(t);
			
			pointer2_t = new Token[] { t };
			
			if (!isParameters(pointer2_t))
			    return false;
			
			t = pointer2_t[0];
			
			continue;
		}
		break;
	    }

	    while (true)
	    {
		switch (t.value)
		{
	//#if CARRAYDECL
		    case TOKlbracket:
			parens = 0;
			t = peek(t);
			if (t.value == TOKrbracket)
			{
			    t = peek(t);
			}
			else {
				Token[] pointer2_t = { t };
				if (isDeclaration(t, 0, TOKrbracket, pointer2_t))
				{   // It's an associative array declaration
					t = pointer2_t[0];
				    t = peek(t);
				}
				else
				{
					t = pointer2_t[0];
					
				    // [ expression ]
				    if (!isExpression(pointer2_t))
				    	return false;
				    
				    t = pointer2_t[0];
				    
				    
				    if (t.value != TOKrbracket)
					return false;
				    t = peek(t);
				}
			}
			continue;
	//#endif

		    case TOKlparen:
			parens = 0;
			
			
			Token[] pointer2_t = { t };
			if (!isParameters(pointer2_t))
			    return false;
			
			t = pointer2_t[0];
			
			continue;

		    // Valid tokens that follow a declaration
		    case TOKrparen:
		    case TOKrbracket:
		    case TOKassign:
		    case TOKcomma:
		    case TOKsemicolon:
		    case TOKlcurly:
		    case TOKin:
			// The !parens is to disallow unnecessary parentheses
			if (parens == 0 && (endtok == TOKreserved || endtok == t.value))
			{   pt[0] = t;
			    return true;
			}
			return false;

		    default:
			return false;
		}
	    }
	}
	
	private boolean isParameters(Token[] pt)
	{   // This code parallels parseParameters()
	    Token t = pt[0];
	    int tmp;

	    //printf("isParameters()\n");
	    if (t.value != TOKlparen)
		return false;

	    t = peek(t);
	    while (true)
	    {
		switch (t.value)
		{
		    case TOKrparen:
			break;

		    case TOKdotdotdot:
			t = peek(t);
			break;

		    case TOKin:
		    case TOKout:
		    case TOKinout:
			t = peek(t);
		    default:
		    	
		    Token[] pointer2_t = { t };
			if (!isBasicType(pointer2_t))
			    return false;
			
			t = pointer2_t[0];
			    
			tmp = 0;
			
			int[] pointer2_tmp = { tmp };
			
			if (t.value != TOKdotdotdot &&
			    !isDeclarator(pointer2_t, pointer2_tmp, TOKreserved))
			    return false;
			
			t = pointer2_t[0];
			tmp = pointer2_tmp[0];
			
			if (t.value == TOKassign)
			{   t = peek(t);
				pointer2_t[0] = t;
			    if (!isExpression(pointer2_t))
			    	return false;
			    
			    t = pointer2_t[0];
			}
			if (t.value == TOKdotdotdot)
			{
			    t = peek(t);
			    break;
			}
			if (t.value == TOKcomma)
			{   t = peek(t);
			    continue;
			}
			break;
		}
		break;
	    }
	    if (t.value != TOKrparen)
		return false;
	    t = peek(t);
	    pt[0] = t;
	    return true;
	}
	
	private boolean isExpression(Token[] pt)
	{
	    // This is supposed to determine if something is an expression.
	    // What it actually does is scan until a closing right bracket
	    // is found.

	    Token t = pt[0];
	    int brnest = 0;
	    int panest = 0;

	    for (;; t = peek(t))
	    {
		switch (t.value)
		{
		    case TOKlbracket:
			brnest++;
			continue;

		    case TOKrbracket:
			if (--brnest >= 0)
			    continue;
			break;

		    case TOKlparen:
			panest++;
			continue;

		    case TOKcomma:
			if (brnest != 0 || panest != 0)
			    continue;
			break;

		    case TOKrparen:
			if (--panest >= 0)
			    continue;
			break;

		    case TOKslice:
			if (brnest != 0)
			    continue;
			break;

		    case TOKeof:
			return false;

		    default:
			continue;
		}
		break;
	    }

	    pt[0] = t;
	    return true;
	}
	
	/**********************************************
	 * Skip over
	 *	instance foo.bar(parameters...)
	 * Output:
	 *	if (pt), *pt is set to the token following the closing )
	 * Returns:
	 *	1	it's valid instance syntax
	 *	0	invalid instance syntax
	 */

	/* <-- Not used
	private boolean isTemplateInstance(Token t, Token[] pt)
	{
	    t = peek(t);
	    if (t.value != TOKdot)
	    {
		if (t.value != TOKidentifier)
		    //goto Lfalse;
			return false;
		
		t = peek(t);
	    }
	    while (t.value == TOKdot)
	    {
		t = peek(t);
		if (t.value != TOKidentifier)
		    //goto Lfalse;
			return false;
		
		t = peek(t);
	    }
	    if (t.value != TOKlparen)
		//	goto Lfalse;
	    	return false;

	    // Skip over the template arguments
	    while (true)
	    {
		while (true)
		{
		    t = peek(t);
		    switch (t.value)
		    {
			case TOKlparen:
				
				Token[] pointer2_t = { t };
				
			    if (!skipParens(t, pointer2_t))
			    	//goto Lfalse;
			    	return false;
			    
			    t = pointer2_t[0];
			    
			    continue;
			case TOKrparen:
			    break;
			case TOKcomma:
			    break;
			case TOKeof:
			case TOKsemicolon:
			    //goto Lfalse;
				return false;
			default:
			    continue;
		    }
		    break;
		}

		if (t.value != TOKcomma)
		    break;
	    }
	    if (t.value != TOKrparen)
	    	//goto Lfalse;
	    	return false;
	    
	    t = peek(t);
	    if (pt != null)
		pt[0] = t;
	    return true;

	//Lfalse:
	//    return 0;
	}
	*/
	
	/*******************************************
	 * Skip parens, brackets.
	 * Input:
	 *	t is on opening (
	 * Output:
	 *	*pt is set to closing token, which is ')' on success
	 * Returns:
	 *	!=0	successful
	 *	0	some parsing error
	 */

	private boolean skipParens(Token t, Token[] pt)
	{
	    int parens = 0;

	    while (true)
	    {
		switch (t.value)
		{
		    case TOKlparen:
			parens++;
			break;

		    case TOKrparen:
			parens--;
			if (parens < 0)
			    //goto Lfalse;
				return false;
			if (parens == 0) {
			    //goto Ldone;
				if (pt[0] != null)
				pt[0] = t;
				return true;
			}
			break;

		    case TOKeof:
		    case TOKsemicolon:
		    	//goto Lfalse;
		    	return false;

		     default:
			break;
		}
		t = peek(t);
	    }

	  //Ldone:
	    //if (pt[0] != null)
		//pt[0] = t;
	    //return true;

	  //Lfalse:
	  //  return 0;
	}
	
	/********************************* Expression Parser ***************************/
	
	private Expression parsePrimaryExp()
	{   Expression e = null;
	    Type t;
	    Identifier id;
	    TOK save;
	    Loc loc = new Loc(this.loc);

	    switch (token.value)
	    {
		case TOKidentifier:
		    id = new Identifier(token);
		    nextToken();
		    if (token.value == TOKnot && peek(token).value == TOKlparen)
		    {	// identifier!(template-argument-list)
			TemplateInstance tempinst;

			tempinst = new TemplateInstance(loc, id);
			nextToken();
			tempinst.tiargs = parseTemplateArgumentList();
			e = new ScopeExp(loc, tempinst);
		    }
		    else
			e = new IdentifierExp(loc, id);
		    break;

		case TOKdollar:
		    if (inBrackets == 0) {
		    	problem("'$' is valid only inside [] of index or slice", IProblem.SEVERITY_ERROR, IProblem.DOLLAR_INVALID_OUTSIDE_BRACKETS, token.ptr, token.len);
		    }
		    e = new DollarExp(loc);
		    nextToken();
		    break;

		case TOKdot:
		    // Signal global scope '.' operator with "" identifier
		    e = new IdentifierExp(loc, new Identifier(Id.empty, TOKidentifier));
		    break;

		case TOKthis:
		    e = new ThisExp(loc);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKsuper:
		    e = new SuperExp(loc);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKint32v:
		    e = new IntegerExp(loc, token.numberValue, Type.tint32);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKuns32v:
		    e = new IntegerExp(loc, token.numberValue, Type.tuns32);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKint64v:
		    e = new IntegerExp(loc, token.numberValue, Type.tint64);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKuns64v:
		    e = new IntegerExp(loc, token.numberValue, Type.tuns64);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKfloat32v:
		    e = new RealExp(loc, token.numberValue, Type.tfloat32);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKfloat64v:
		    e = new RealExp(loc, token.numberValue, Type.tfloat64);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKfloat80v:
		    e = new RealExp(loc, token.numberValue, Type.tfloat80);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKimaginary32v:
		    e = new RealExp(loc, token.numberValue, Type.timaginary32);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKimaginary64v:
		    e = new RealExp(loc, token.numberValue, Type.timaginary64);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKimaginary80v:
		    e = new RealExp(loc, token.numberValue, Type.timaginary80);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKnull:
		    e = new NullExp(loc);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKtrue:
		    e = new IntegerExp(loc, 1, Type.tbool);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKfalse:
		    e = new IntegerExp(loc, 0, Type.tbool);
		    e.start = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKcharv:
		    e = new IntegerExp(loc, token.numberValue, Type.tchar);
		    nextToken();
		    break;

		case TOKwcharv:
		    e = new IntegerExp(loc, token.numberValue, Type.twchar);
		    nextToken();
		    break;

		case TOKdcharv:
		    e = new IntegerExp(loc, token.numberValue, Type.tdchar);
		    nextToken();
		    break;

		case TOKstring: {
			String s;
			int start = token.ptr; 
			int len = token.len;
			int postfix;

			// cat adjacent strings
			s = token.ustring;
			postfix = token.postfix;
			while (true) {
				int lastStringString = token.ptr;
				nextToken();
				if (token.value == TOKstring) {
					len = token.ptr + token.len - start;
					if (token.postfix != 0) {
						if (token.postfix != postfix)
							problem("Mismatched string literal postfixes '" + (char) postfix + "' and '" + (char) token.postfix + "'",
									IProblem.SEVERITY_ERROR,
									IProblem.MISMATCHED_STRING_LITERAL_POSTFIXES,
									lastStringString, token.ptr + token.len - lastStringString);
							
						postfix = token.postfix;
					}

					if (token.ustring != null)
						s += token.ustring;
				} else
					break;
			}
			e = new StringExp(loc, s, len, postfix);
			e.start = start;
			e.length = len;
			break;
		}
		
		// CASE_BASIC_TYPES_X(t):
		case TOKvoid: case TOKint8: case TOKuns8: case TOKint16:
		case TOKuns16: case TOKint32: case TOKuns32: case TOKint64:
		case TOKuns64: case TOKfloat32: case TOKfloat64:
		case TOKfloat80: case TOKimaginary32: case TOKimaginary64:
		case TOKimaginary80: case TOKcomplex32: case TOKcomplex64:
		case TOKcomplex80: case TOKbit: case TOKbool:
		case TOKchar: case TOKwchar: case TOKdchar:
			t = Type.fromTOK(token.value);
			t.start = token.ptr;
			t.length = token.len;
			nextToken();
			// L1:
			    check(TOKdot, t.toString());
			    if (token.value != TOKidentifier)
			    {
			    	problem("Identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
			    	// goto Lerr;
		    		// Anything for e, as long as it's not NULL
		    		e = new IntegerExp(loc, 0, Type.tint32);
		    		nextToken();
		    		break;
			    }
			    e = new TypeDotIdExp(loc, t, new Identifier(token));
			    nextToken();
			    break;

		case TOKtypeof:
		{   
			Token saveToken = new Token(token);
			Expression exp;

		    nextToken();
		    check(TOKlparen);
		    exp = parseExpression();
		    check(TOKrparen);
		    t = new TypeTypeof(loc, exp);
		    if (token.value == TOKdot) {
		    	// goto L1;
		    	check(TOKdot, t.toString());
			    if (token.value != TOKidentifier)
			    {   
			    	problem("Identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					// goto Lerr;
			    	// Anything for e, as long as it's not NULL
			    	e = new IntegerExp(loc, 0, Type.tint32);
			    	nextToken();
			    	break;
			    }
			    e = new TypeDotIdExp(loc, t, token.ident);
			    nextToken();
			    break;
		    }
		    	
		    e = new TypeExp(loc, t);
		    e.start = saveToken.ptr;
		    e.length = prevToken.ptr + prevToken.len - e.start;
		    break;
		}

		case TOKtypeid:
		{   Type t2;

		    nextToken();
		    check(TOKlparen, "typeid");
		    t2 = parseBasicType();
		    t2 = parseDeclarator(t2, null);	// ( type )
		    check(TOKrparen);
		    e = new TypeidExp(loc, t2);
		    break;
		}

		case TOKis:
		{   Type targ = null;
		    Identifier ident = null;
		    Type tspec = null;
		    TOK tok = TOKreserved;
		    TOK tok2 = TOKreserved;
		    Loc loc2 = new Loc(this.loc);

		    nextToken();
		    if (token.value == TOKlparen)
		    {
			nextToken();
			targ = parseBasicType();
			
			Identifier[] pointer2_ident = { ident };
			targ = parseDeclarator(targ, pointer2_ident);
			ident = pointer2_ident[0];
			
			if (token.value == TOKcolon || token.value == TOKequal)
			{
			    tok = token.value;
			    nextToken();
			    if (tok == TOKequal &&
				(token.value == TOKtypedef ||
				 token.value == TOKstruct ||
				 token.value == TOKunion ||
				 token.value == TOKclass ||
				 token.value == TOKenum ||
				 token.value == TOKinterface ||
				 token.value == TOKfunction ||
				 token.value == TOKdelegate))
			    {
				tok2 = token.value;
				nextToken();
			    }
			    else
			    {
				tspec = parseBasicType();
				tspec = parseDeclarator(tspec, null);
			    }
			}
			check(TOKrparen);
		    }
		    else
		    {   
		    	problem("(type identifier : specialization) expected following is", IProblem.SEVERITY_ERROR, IProblem.INVALID_IFTYPE_SYNTAX, token.ptr, token.len);
				// goto Lerr;
		    	// Anything for e, as long as it's not NULL
		    	e = new IntegerExp(loc2, 0, Type.tint32);
		    	nextToken();
		    	break;
		    }
		    e = new IftypeExp(loc2, targ, ident, tok, tspec, tok2);
		    break;
		}

		case TOKassert: {
			Expression msg = null;

			int start = token.ptr;
			nextToken();
			check(TOKlparen, "assert");
			e = parseAssignExp();
			if (token.value == TOKcomma) {
				nextToken();
				msg = parseAssignExp();
			}
			int end = token.ptr + token.len;
			check(TOKrparen);
			e = new AssertExp(loc, e, msg);
			e.start = start;
			e.length = end - start;
			break;
		}

		case TOKlparen:
		    if (peekPastParen(token).value == TOKlcurly)
		    {	// (arguments) { statements... }
		    	save = TOKdelegate;
				// goto case_delegate;
		    	/* function type(parameters) { body }
			     * delegate type(parameters) { body }
			     */
			    List<Argument> arguments;
			    int varargs = 0;
			    FuncLiteralDeclaration fd;
			    Type t2;

			    if (token.value == TOKlcurly)
			    {
				t2 = null;
				arguments = new ArrayList<Argument>();
			    }
			    else
			    {
				if (token.value == TOKlparen)
				    t2 = null;
				else
				{
				    t2 = parseBasicType();
				    t2 = parseBasicType2(t2);	// function return type
				}
				
				int[] pointer2_varargs = { varargs };
				arguments = parseParameters(pointer2_varargs);
				varargs = pointer2_varargs[0];
				
			    }
			    t2 = new TypeFunction(arguments, t2, varargs, linkage);
			    fd = new FuncLiteralDeclaration(loc, 0, t2, save, null);
			    parseContracts(fd);
			    e = new FuncExp(loc, fd);
			    break;
		    }
		    // ( expression )
		    int start = token.ptr;
		    nextToken();
		    e = parseExpression();
		    
		    int end = token.ptr + token.len;
		    check(TOKrparen);
		    
		    e = new ParenthesizedExpression(e);
		    e.start = start;
		    e.length = end - start;
		    break;

		case TOKlbracket:
		{   List<Expression> elements = parseArguments();

		    e = new ArrayLiteralExp(loc, elements);
		    break;
		}
		
		case TOKlcurly:
		    // { statements... }
		    save = TOKdelegate;
		    // goto case_delegate;
		    /* function type(parameters) { body }
		     * delegate type(parameters) { body }
		     */
		    List<Argument> arguments;
		    int varargs = 0;
		    FuncLiteralDeclaration fd;
		    Type t2;

		    if (token.value == TOKlcurly)
		    {
			t2 = null;
			arguments = new ArrayList<Argument>();
		    }
		    else
		    {
			if (token.value == TOKlparen)
			    t2 = null;
			else
			{
			    t2 = parseBasicType();
			    t2 = parseBasicType2(t2);	// function return type
			}
			
			int[] pointer2_varargs = { varargs };
			arguments = parseParameters(pointer2_varargs);
			varargs = pointer2_varargs[0];
			
		    }
		    t2 = new TypeFunction(arguments, t2, varargs, linkage);
		    fd = new FuncLiteralDeclaration(loc, 0, t2, save, null);
		    parseContracts(fd);
		    e = new FuncExp(loc, fd);
		    break;

		case TOKfunction:
		case TOKdelegate:
		    save = token.value;
		    nextToken();
		// case_delegate:
		{
		    /* function type(parameters) { body }
		     * delegate type(parameters) { body }
		     */
		    arguments = null;
		    varargs = 0;
		    fd = null;
		    t2 = null;

		    if (token.value == TOKlcurly)
		    {
			t2 = null;
			arguments = new ArrayList<Argument>();
		    }
		    else
		    {
			if (token.value == TOKlparen)
			    t2 = null;
			else
			{
			    t2 = parseBasicType();
			    t2 = parseBasicType2(t2);	// function return type
			}
			
			int[] pointer2_varargs = { varargs };
			arguments = parseParameters(pointer2_varargs);
			varargs = pointer2_varargs[0];
			
		    }
		    t2 = new TypeFunction(arguments, t2, varargs, linkage);
		    fd = new FuncLiteralDeclaration(loc, 0, t2, save, null);
		    parseContracts(fd);
		    e = new FuncExp(loc, fd);
		    break;
		}

		default:
			problem("Expression expected", IProblem.SEVERITY_ERROR, IProblem.EXPRESSION_EXPECTED, token.ptr, token.len);
		// Lerr:
		    // Anything for e, as long as it's not NULL
		    e = new IntegerExp(loc, 0, Type.tint32);
		    nextToken();
		    break;
	    }
	    return parsePostExp(e);
	}

	@SuppressWarnings("unchecked")
	private Expression parsePostExp(Expression e) {
		Loc loc;

		while (true) {
			loc = new Loc(this.loc);
			switch (token.value) {
			case TOKdot:
				nextToken();
				if (token.value == TOKidentifier) {
					Identifier id = new Identifier(token);

					nextToken();
					if (token.value == TOKnot && peek(token).value == TOKlparen) { // identifier!(template-argument-list)
						TemplateInstance tempinst;

						tempinst = new TemplateInstance(loc, id);
						nextToken();
						tempinst.tiargs = parseTemplateArgumentList();
						e = new DotTemplateInstanceExp(loc, e, tempinst);
					} else
						e = new DotIdExp(loc, e, id);
					continue;
				} else if (token.value == TOKnew) {
					e = parseNewExp(e);
					continue;
				} else {
					problem("Identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				}
				break;

			case TOKplusplus:
				e = new PostIncExp(loc, e);
				break;

			case TOKminusminus:
				e = new PostDecExp(loc, e);
				break;

			case TOKlparen:
				e = new CallExp(loc, e, parseArguments());
				continue;

			case TOKlbracket: { // array dereferences:
				// array[index]
				// array[]
				// array[lwr .. upr]
				Expression index;
				Expression upr;

				inBrackets++;
				nextToken();
				if (token.value == TOKrbracket) { // array[]
					e = new SliceExp(loc, e, null, null);
					nextToken();
				} else {
					index = parseAssignExp();
					if (token.value == TOKslice) { // array[lwr .. upr]
						nextToken();
						upr = parseAssignExp();
						e = new SliceExp(loc, e, index, upr);
					} else { // array[index, i2, i3, i4, ...]
						List<Expression> arguments = new ArrayList<Expression>();
						arguments.add(index);
						if (token.value == TOKcomma) {
							nextToken();
							while (true) {
								Expression arg;

								arg = parseAssignExp();
								arguments.add(arg);
								if (token.value == TOKrbracket)
									break;
								check(TOKcomma);
							}
						}
						e = new ArrayExp(loc, e, arguments);
					}
					check(TOKrbracket);
					inBrackets--;
				}
				continue;
			}

			default:
				return e;
			}
			nextToken();
	    }
	}
	
	private Expression parseUnaryExp() {
		Expression e;
		Loc loc = new Loc(this.loc);

		Token saveToken = new Token(token);

		switch (token.value) {
		case TOKand:
			nextToken();
			e = parseUnaryExp();
			e = new AddrExp(loc, e);
			break;

		case TOKplusplus:
			nextToken();
			e = parseUnaryExp();
			e = new AddAssignExp(loc, e, new IntegerExp(loc, 1, Type.tint32),
					true);
			break;

		case TOKminusminus:
			nextToken();
			e = parseUnaryExp();
			e = new MinAssignExp(loc, e, new IntegerExp(loc, 1, Type.tint32),
					true);
			break;

		case TOKmul:
			nextToken();
			e = parseUnaryExp();
			e = new PtrExp(loc, e);
			break;

		case TOKmin:
			nextToken();
			e = parseUnaryExp();
			e = new NegExp(loc, e);
			break;

		case TOKadd:
			nextToken();
			e = parseUnaryExp();
			e = new UAddExp(loc, e);
			break;

		case TOKnot:
			nextToken();
			e = parseUnaryExp();
			e = new NotExp(loc, e);
			break;

		case TOKtilde:
			nextToken();
			e = parseUnaryExp();
			e = new ComExp(loc, e);
			break;

		case TOKdelete:
			nextToken();
			e = parseUnaryExp();
			e = new DeleteExp(loc, e);
			break;

		case TOKnew:
			e = parseNewExp(null);
			break;

		case TOKcast: // cast(type) expression
		{
			Type t;

			nextToken();
			check(TOKlparen);
			t = parseBasicType();
			t = parseDeclarator(t, null); // ( type )
			check(TOKrparen);

			e = parseUnaryExp();
			e = new CastExp(loc, e, t);
			break;
		}

		case TOKlparen: {
			Token tk;
			
			Token firstToken = new Token(token);

			tk = peek(token);
			// #if CCASTSYNTAX
			// If cast
			Token[] pointer2_tk = { tk };
			if (isDeclaration(tk, 0, TOKrparen, pointer2_tk)) {
				tk = pointer2_tk[0];

				tk = peek(tk); // skip over right parenthesis
				switch (tk.value) {
				case TOKdot:
				case TOKplusplus:
				case TOKminusminus:
				case TOKnot:
				case TOKdelete:
				case TOKnew:
				case TOKlparen:
				case TOKidentifier:
				case TOKthis:
				case TOKsuper:
				case TOKint32v:
				case TOKuns32v:
				case TOKint64v:
				case TOKuns64v:
				case TOKfloat32v:
				case TOKfloat64v:
				case TOKfloat80v:
				case TOKimaginary32v:
				case TOKimaginary64v:
				case TOKimaginary80v:
				case TOKnull:
				case TOKtrue:
				case TOKfalse:
				case TOKcharv:
				case TOKwcharv:
				case TOKdcharv:
				case TOKstring:
					/*
					 * #if 0 case TOKtilde: case TOKand: case TOKmul: case
					 * TOKmin: case TOKadd: #endif
					 */
				case TOKfunction:
				case TOKdelegate:
				case TOKtypeof:
					// CASE_BASIC_TYPES: // (type)int.size
				case TOKwchar:
				case TOKdchar:
				case TOKbit:
				case TOKbool:
				case TOKchar:
				case TOKint8:
				case TOKuns8:
				case TOKint16:
				case TOKuns16:
				case TOKint32:
				case TOKuns32:
				case TOKint64:
				case TOKuns64:
				case TOKfloat32:
				case TOKfloat64:
				case TOKfloat80:
				case TOKimaginary32:
				case TOKimaginary64:
				case TOKimaginary80:
				case TOKcomplex32:
				case TOKcomplex64:
				case TOKcomplex80:
				case TOKvoid: { // (type) una_exp
					Type t;

					nextToken();
					t = parseBasicType();
					t = parseDeclarator(t, null);
					check(TOKrparen);

					// if .identifier
					if (token.value == TOKdot) {
						nextToken();
						if (token.value != TOKidentifier) {
							problem("Identifier expected following (type).", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
							return null;
						}
						e = new TypeDotIdExp(loc, t, new Identifier(token));
						nextToken();
					} else {
						e = parseUnaryExp();
						e = new CastExp(loc, e, t);
						problem("C style cast illegal, use cast(...)", IProblem.SEVERITY_ERROR, IProblem.C_STYLE_CAST_ILLEGAL, firstToken.ptr, prevToken.ptr + prevToken.len - firstToken.ptr);
					}
					return e;
				}
				}
			}
			// #endif
			e = parsePrimaryExp();
			break;
		}
		default:
			e = parsePrimaryExp();
			break;
		}
		assert (e != null);

		e.start = saveToken.ptr;
		e.length = prevToken.ptr + prevToken.len - e.start;

		return e;
	}
	
	private Expression parseMulExp()
	{   Expression e;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    e = parseUnaryExp();
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKmul: nextToken(); e2 = parseUnaryExp(); e = new MulExp(loc,e,e2); continue;
		    case TOKdiv:   nextToken(); e2 = parseUnaryExp(); e = new DivExp(loc,e,e2); continue;
		    case TOKmod:  nextToken(); e2 = parseUnaryExp(); e = new ModExp(loc,e,e2); continue;

		    default:
			break;
		}
		break;
	    }
	    return e;
	}
	
	private Expression parseAddExp()
	{   Expression e;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    e = parseMulExp();
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKadd:    nextToken(); e2 = parseMulExp(); e = new AddExp(loc,e,e2); continue;
		    case TOKmin:    nextToken(); e2 = parseMulExp(); e = new MinExp(loc,e,e2); continue;
		    case TOKtilde:  nextToken(); e2 = parseMulExp(); e = new CatExp(loc,e,e2); continue;

		    default:
			break;
		}
		break;
	    }
	    return e;
	}
	
	private Expression parseShiftExp()
	{   Expression e;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    e = parseAddExp();
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKshl:  nextToken(); e2 = parseAddExp(); e = new ShlExp(loc,e,e2);  continue;
		    case TOKshr:  nextToken(); e2 = parseAddExp(); e = new ShrExp(loc,e,e2);  continue;
		    case TOKushr: nextToken(); e2 = parseAddExp(); e = new UshrExp(loc,e,e2); continue;

		    default:
			break;
		}
		break;
	    }
	    return e;
	}
	
	private Expression parseRelExp()
	{   Expression e;
	    Expression e2;
	    TOK op;
	    Loc loc = new Loc(this.loc);

	    e = parseShiftExp();
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKlt:
		    case TOKle:
		    case TOKgt:
		    case TOKge:
		    case TOKunord:
		    case TOKlg:
		    case TOKleg:
		    case TOKule:
		    case TOKul:
		    case TOKuge:
		    case TOKug:
		    case TOKue:
			op = token.value;
			nextToken();
			e2 = parseShiftExp();
			e = new CmpExp(op, loc, e, e2);
			continue;

		    case TOKin:
			nextToken();
			e2 = parseShiftExp();
			e = new InExp(loc, e, e2);
			continue;

		    default:
			break;
		}
		break;
	    }
	    return e;
	}
	
	private Expression parseEqualExp()
	{   Expression e;
	    Expression e2;
	    Token t;
	    Loc loc = new Loc(this.loc);

	    e = parseRelExp();
	    while (true)
	    {	TOK value = token.value;

		switch (value)
		{
		    case TOKequal:
		    case TOKnotequal:
			nextToken();
			e2 = parseRelExp();
			e = new EqualExp(value, loc, e, e2);
			continue;

		    case TOKidentity:
			//if (!global.params.useDeprecated)
		    	problem("'===' is deprecated, use 'is' instead", IProblem.SEVERITY_ERROR,
		    			IProblem.THREE_EQUALS_IS_DEPRECATED, token.ptr, token.len);
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = new IdentityExp(value, loc, e, e2);
			continue;

		    case TOKnotidentity:
			//if (!global.params.useDeprecated)
		    	problem("'!==' is deprecated, use 'is' instead", IProblem.SEVERITY_ERROR,
		    			IProblem.NOT_TWO_EQUALS_IS_DEPRECATED, token.ptr, token.len);
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = new IdentityExp(value, loc, e, e2);
			continue;

		    case TOKis:
			value = TOKidentity;
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = new IdentityExp(value, loc, e, e2);
			continue;

		    case TOKnot:
			// Attempt to identify '!is'
			t = peek(token);
			if (t.value != TOKis)
			    break;
			nextToken();
			value = TOKnotidentity;
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = new IdentityExp(value, loc, e, e2);
			continue;

		    // L1:
			//nextToken();
			//e2 = parseRelExp();
			//e = new IdentityExp(value, loc, e, e2);
			//continue;

		    default:
			break;
		}
		break;
	    }
	    return e;
	}
	
	private Expression parseAndExp()
	{   Expression e;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    e = parseEqualExp();
	    while (token.value == TOKand)
	    {
		nextToken();
		e2 = parseEqualExp();
		e = new AndExp(loc,e,e2);
		loc = new Loc(this.loc);
	    }
	    return e;
	}
	
	private Expression parseXorExp()
	{   Expression e;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    e = parseAndExp();
	    while (token.value == TOKxor)
	    {
		nextToken();
		e2 = parseAndExp();
		e = new XorExp(loc, e, e2);
	    }
	    return e;
	}
	
	private Expression parseOrExp()
	{   Expression e;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    e = parseXorExp();
	    while (token.value == TOKor)
	    {
		nextToken();
		e2 = parseXorExp();
		e = new OrExp(loc, e, e2);
	    }
	    return e;
	}
	
	private Expression parseAndAndExp()
	{   Expression e;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    e = parseOrExp();
	    while (token.value == TOKandand)
	    {
		nextToken();
		e2 = parseOrExp();
		e = new AndAndExp(loc, e, e2);
	    }
	    return e;
	}
	
	private Expression parseOrOrExp()
	{   Expression e;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    e = parseAndAndExp();
	    while (token.value == TOKoror)
	    {
		nextToken();
		e2 = parseAndAndExp();
		e = new OrOrExp(loc, e, e2);
	    }
	    return e;
	}

	private Expression parseCondExp() {
		Expression e;
	    Expression e1;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    e = parseOrOrExp();
	    if (token.value == TOKquestion)
	    {
		nextToken();
		e1 = parseExpression();
		check(TOKcolon);
		e2 = parseCondExp();
		e = new CondExp(loc, e, e1, e2);
	    }
	    return e;
	}
	
	private Expression parseAssignExp()
	{   Expression e;
	    Expression e2;
	    Loc loc;

	    e = parseCondExp();
	    while (true)
	    {
		loc = new Loc(this.loc);
		switch (token.value)
		{
		case TOKassign:  nextToken(); e2 = parseAssignExp(); e = new AssignExp(loc,e,e2); continue;
		case TOKaddass:  nextToken(); e2 = parseAssignExp(); e = new AddAssignExp(loc,e,e2); continue;
		case TOKminass:  nextToken(); e2 = parseAssignExp(); e = new MinAssignExp(loc,e,e2); continue;
		case TOKmulass:  nextToken(); e2 = parseAssignExp(); e = new MulAssignExp(loc,e,e2); continue;
		case TOKdivass:  nextToken(); e2 = parseAssignExp(); e = new DivAssignExp(loc,e,e2); continue;
		case TOKmodass:  nextToken(); e2 = parseAssignExp(); e = new ModAssignExp(loc,e,e2); continue;
		case TOKandass:  nextToken(); e2 = parseAssignExp(); e = new AndAssignExp(loc,e,e2); continue;
		case TOKorass:  nextToken(); e2 = parseAssignExp(); e = new OrAssignExp(loc,e,e2); continue;
		case TOKxorass:  nextToken(); e2 = parseAssignExp(); e = new XorAssignExp(loc,e,e2); continue;
		case TOKshlass:  nextToken(); e2 = parseAssignExp(); e = new ShlAssignExp(loc,e,e2); continue;
		case TOKshrass:  nextToken(); e2 = parseAssignExp(); e = new ShrAssignExp(loc,e,e2); continue;
		case TOKushrass:  nextToken(); e2 = parseAssignExp(); e = new UshrAssignExp(loc,e,e2); continue;
		case TOKcatass:  nextToken(); e2 = parseAssignExp(); e = new CatAssignExp(loc,e,e2); continue;
	    default:
			break;
		}
		break;
	    }
	    return e;
	}
	
	public Expression parseExpression()
	{   Expression e;
	    Expression e2;
	    Loc loc = new Loc(this.loc);

	    //printf("Parser::parseExpression()\n");
	    e = parseAssignExp();
	    while (token.value == TOKcomma)
	    {
		nextToken();
		e2 = parseAssignExp();
		e = new CommaExp(loc, e, e2);
		loc = new Loc(this.loc);
	    }
	    return e;
	}
	
	/*************************
	 * Collect argument list.
	 * Assume current token is '('.
	 */
	
	@SuppressWarnings("unchecked")
	private List<Expression> parseArguments() {
		// function call
		List<Expression> arguments;
		Expression arg;
		TOK endtok;

		arguments = new ArrayList<Expression>();
		if (token.value == TOKlbracket) {
			endtok = TOKrbracket;
		} else {
			endtok = TOKrparen;
		}

		{
			nextToken();
			if (token.value != endtok) {
				while (true) {
					arg = parseAssignExp();
					arguments.add(arg);
					if (token.value == endtok)
						break;
					if (token.value == TOKeof) 
						return arguments;
					check(TOKcomma);
				}
			}
			check(endtok);
		}
		return arguments;
	}
	
	@SuppressWarnings("unchecked")
	private Expression parseNewExp(Expression thisexp) {
		Type t;
		List<Expression> newargs;
		List<Expression> arguments = null;
		Expression e;
		Loc loc = new Loc(this.loc);

		nextToken();
		newargs = null;
		if (token.value == TOKlparen) {
			newargs = parseArguments();
		}

		// An anonymous nested class starts with "class"
		if (token.value == TOKclass) {
			nextToken();
			if (token.value == TOKlparen)
				arguments = parseArguments();

			List<BaseClass> baseclasses = null;
			if (token.value != TOKlcurly)
				baseclasses = parseBaseClasses();

			Identifier id = null;
			ClassDeclaration cd = new ClassDeclaration(loc, id, baseclasses);

			if (token.value != TOKlcurly) {
				problem("{ members } expected for anonymous class", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
				cd.members = null;
			} else {
				nextToken();
				List decl = parseDeclDefs(false);
				if (token.value != TOKrcurly) {
					problem("class member expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
				}
				nextToken();
				cd.members = decl;
			}

			e = new NewAnonClassExp(loc, thisexp, newargs, cd, arguments);

			return e;
		}

		// #if LTORARRAYDECL
		t = parseBasicType();
		t = parseBasicType2(t);
		if (t.ty == Taarray) {
			Type index = ((TypeAArray) t).index;
			
			Expression e2 = index.toExpression();
			if (e2 != null) {
				arguments = new ArrayList<Expression>();
				arguments.add(e2);
				t = new TypeDArray(t.next);
			} else {
				error("need size of rightmost array, not type %s", index);
				return new NullExp(loc);
			}
		} else if (t.ty == Tsarray) {
			TypeSArray tsa = (TypeSArray) t;
			Expression e2 = tsa.dim;

			arguments = new ArrayList<Expression>();
			arguments.add(e2);
			t = new TypeDArray(t.next);
		} else if (token.value == TOKlparen) {
			arguments = parseArguments();
		}
		/*
		 * #else t = parseBasicType(); while (token.value == TOKmul) { t = new
		 * TypePointer(t); nextToken(); } if (token.value == TOKlbracket) {
		 * Expression *e;
		 * 
		 * nextToken(); e = parseAssignExp(); arguments = new Array();
		 * arguments->push(e); check(TOKrbracket); t = parseDeclarator(t, NULL);
		 * t = new TypeDArray(t); } else if (token.value == TOKlparen) arguments =
		 * parseArguments(); #endif
		 */
		e = new NewExp(loc, thisexp, newargs, t, arguments);
		return e;
	}
	
	private void addComment(AbstractElement s, String blockComment) {
		addComment(s, blockComment, -1);
	}

	private void addComment(AbstractElement s, String blockComment, int blockCommentStart) {
		s.addComment(combineComments(blockComment, token.lineComment), blockComment == null ? - 1 : blockCommentStart);
	}
	
}