package descent.core.dom;

import static descent.internal.core.parser.LINK.LINKc;
import static descent.internal.core.parser.LINK.LINKcpp;
import static descent.internal.core.parser.LINK.LINKd;
import static descent.internal.core.parser.LINK.LINKdefault;
import static descent.internal.core.parser.LINK.LINKpascal;
import static descent.internal.core.parser.LINK.LINKwindows;
import static descent.internal.core.parser.TOK.TOKalias;
import static descent.internal.core.parser.TOK.TOKand;
import static descent.internal.core.parser.TOK.TOKandand;
import static descent.internal.core.parser.TOK.TOKassert;
import static descent.internal.core.parser.TOK.TOKassign;
import static descent.internal.core.parser.TOK.TOKauto;
import static descent.internal.core.parser.TOK.TOKcase;
import static descent.internal.core.parser.TOK.TOKcatch;
import static descent.internal.core.parser.TOK.TOKclass;
import static descent.internal.core.parser.TOK.TOKcolon;
import static descent.internal.core.parser.TOK.TOKcomma;
import static descent.internal.core.parser.TOK.TOKdefault;
import static descent.internal.core.parser.TOK.TOKdelegate;
import static descent.internal.core.parser.TOK.TOKdot;
import static descent.internal.core.parser.TOK.TOKdotdotdot;
import static descent.internal.core.parser.TOK.TOKelse;
import static descent.internal.core.parser.TOK.TOKenum;
import static descent.internal.core.parser.TOK.TOKeof;
import static descent.internal.core.parser.TOK.TOKequal;
import static descent.internal.core.parser.TOK.TOKfinally;
import static descent.internal.core.parser.TOK.TOKfunction;
import static descent.internal.core.parser.TOK.TOKidentifier;
import static descent.internal.core.parser.TOK.TOKidentity;
import static descent.internal.core.parser.TOK.TOKif;
import static descent.internal.core.parser.TOK.TOKimport;
import static descent.internal.core.parser.TOK.TOKinout;
import static descent.internal.core.parser.TOK.TOKint32v;
import static descent.internal.core.parser.TOK.TOKinterface;
import static descent.internal.core.parser.TOK.TOKis;
import static descent.internal.core.parser.TOK.TOKlbracket;
import static descent.internal.core.parser.TOK.TOKlcurly;
import static descent.internal.core.parser.TOK.TOKlparen;
import static descent.internal.core.parser.TOK.TOKmodule;
import static descent.internal.core.parser.TOK.TOKnew;
import static descent.internal.core.parser.TOK.TOKnot;
import static descent.internal.core.parser.TOK.TOKnotidentity;
import static descent.internal.core.parser.TOK.TOKon_scope_exit;
import static descent.internal.core.parser.TOK.TOKon_scope_failure;
import static descent.internal.core.parser.TOK.TOKon_scope_success;
import static descent.internal.core.parser.TOK.TOKor;
import static descent.internal.core.parser.TOK.TOKoror;
import static descent.internal.core.parser.TOK.TOKplusplus;
import static descent.internal.core.parser.TOK.TOKquestion;
import static descent.internal.core.parser.TOK.TOKrbracket;
import static descent.internal.core.parser.TOK.TOKrcurly;
import static descent.internal.core.parser.TOK.TOKreserved;
import static descent.internal.core.parser.TOK.TOKrparen;
import static descent.internal.core.parser.TOK.TOKsemicolon;
import static descent.internal.core.parser.TOK.TOKslice;
import static descent.internal.core.parser.TOK.TOKstring;
import static descent.internal.core.parser.TOK.TOKstruct;
import static descent.internal.core.parser.TOK.TOKthis;
import static descent.internal.core.parser.TOK.TOKtilde;
import static descent.internal.core.parser.TOK.TOKtypedef;
import static descent.internal.core.parser.TOK.TOKtypeof;
import static descent.internal.core.parser.TOK.TOKunion;
import static descent.internal.core.parser.TOK.TOKwhile;
import static descent.internal.core.parser.TOK.TOKxor;
import static descent.internal.core.parser.TY.Taarray;
import static descent.internal.core.parser.TY.Tfunction;
import static descent.internal.core.parser.TY.Tsarray;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;
import descent.core.dom.FunctionLiteralDeclarationExpression.Syntax;
import descent.core.dom.IsTypeSpecializationExpression.TypeSpecialization;
import descent.core.dom.Modifier.ModifierKeyword;
import descent.internal.core.parser.DebugCondition;
import descent.internal.core.parser.IDmdType;
import descent.internal.core.parser.Id;
import descent.internal.core.parser.Identifier;
import descent.internal.core.parser.IftypeCondition;
import descent.internal.core.parser.LINK;
import descent.internal.core.parser.StaticIfCondition;
import descent.internal.core.parser.TOK;
import descent.internal.core.parser.Token;
import descent.internal.core.parser.TypeAdapter;
import descent.internal.core.parser.VersionCondition;
import descent.internal.core.parser.global;

public class Parser extends Lexer {
	
	public final static boolean LTORARRAYDECL = true;
	
	public final static int PSsemi = 1;		// empty ';' statements are allowed
	public final static int PSscope = 2;	// start a new scope
	public final static int PScurly = 4;	// { } statement is required
	public final static int PScurlyscope = 8;	// { } starts a new scope
	
	ModuleDeclaration md;
	
	int lastDocCommentRead = 0;
	int inBrackets;
	
	LINK linkage = LINK.LINKd;

	public Parser(String source) {
		super(source);
		
		nextToken();
	}
	
	public Parser(String source, int base, int begoffset, 
			int endoffset) {
		super(source, base, begoffset, endoffset);
		
		nextToken();
	}
	
	private void adjustLastDocComment() {
		lastDocCommentRead = comments.size();
	}
	
	@SuppressWarnings("unchecked")
	public List<Declaration> parseModule() {
	    List<Declaration> decldefs = new ArrayList<Declaration>();
	    List<Comment> moduleDocComments = getLastDocComments();

		// ModuleDeclation leads off
		if (token.value == TOKmodule) {
			int start = token.ptr;
			
			nextToken();
			if (token.value != TOKidentifier) {
				problem("Identifier expected following module", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				// goto Lerr;
				return parseModule_LErr();
			} else {
				Name name = newSimpleNameForCurrentToken();
				while (nextToken() == TOKdot) {
					nextToken();
					if (token.value != TOKidentifier) {
						problem("Identifier expected following package", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
						return parseModule_LErr();
					}
					name = newQualifiedNameFromCurrentToken(name);
				}

				md = newModuleDeclaration(name);
				md.setSourceRange(start, token.ptr + token.len - start);
				md.comments = moduleDocComments;
				adjustLastDocComment();
				mod.setModuleDeclaration(md);

				if (token.value != TOKsemicolon) {
					problem("';' expected following module declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
				}
				
				nextToken();				
			}
		}

		decldefs = parseDeclDefs(false, new ArrayList<Modifier>());
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
	private List<Declaration> parseDeclDefs(boolean once, List<Modifier> modifiers) {
		Object[] tempObj;

		Declaration s = null;
		List<Declaration> decldefs;
		List<Declaration> a = new ArrayList<Declaration>();
		List<Declaration> aelse;
		
		Token saveToken;
		boolean[] isSingle = new boolean[1];
		
		decldefs = new ArrayList<Declaration>();
		do {
			List<Comment> lastComments = getLastDocComments();
			
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
				s = parseImport(false);
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
				s = parseInvariant();
				break;

			case TOKunittest:
				saveToken = new Token(token);
				s = parseUnitTest();
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
					StaticIfCondition condition = parseStaticIfCondition();
					a = parseBlock();
					aelse = null;
					if (token.value == TOKelse) {
						nextToken();
						aelse = parseBlock();
					}					
					s = newStaticIfDeclaration(condition, a, aelse);
					s.setSourceRange(staticToken.ptr, prevToken.ptr + prevToken.len - staticToken.ptr);
					break;
				} else if (token.value == TOKimport) {
					s = parseImport(true);
					ImportDeclaration id = (ImportDeclaration) s;
					id.setSourceRange(staticToken.ptr, id.startPosition - staticToken.ptr);
				} else {
					// goto Lstc2;
					
					Modifier modifier = newModifierFromTokenAndKeyword(staticToken, ModifierKeyword.STATIC_KEYWORD);
					modifiers.add(modifier);
					
					ModifierDeclaration.Syntax[] syntax = new ModifierDeclaration.Syntax[1];
					tempObj = parseDeclDefs_Lstc2(a, isSingle, syntax, modifiers);
					a = (List<Declaration>) tempObj[0];
					s = (Declaration) tempObj[1];
					
					if (a != null && a.size() > 0) {
						if (isSingle[0]) {						
							s = (Declaration) a.get(0);
						} else {
							s = newModifierDeclaration(modifier, syntax[0], a);
						}
					} else {
						if (!isSingle[0]) {
							s = newModifierDeclaration(modifier, syntax[0], a);
						}
					}
				}
				if (s != null) {
					s.startPosition = staticToken.ptr;
					s.length = prevToken.ptr + prevToken.len - s.startPosition;
				}
				break;

			case TOKconst:
			case TOKfinal:
			case TOKauto:
			case TOKscope:
			case TOKoverride:
			case TOKabstract:
			case TOKsynchronized:
			case TOKdeprecated:
				Modifier modifier = newModifierFromCurrentToken();
				modifiers.add(modifier);
				
				// goto Lstc;
				saveToken = new Token(token);
				nextToken();
				
				ModifierDeclaration.Syntax[] syntax = new ModifierDeclaration.Syntax[1];
				tempObj = parseDeclDefs_Lstc2(a, isSingle, syntax, modifiers);
				a = (List) tempObj[0];
				s = (Declaration) tempObj[1];
				
				if (a != null && a.size() > 0) {
					if (isSingle[0]) {
						s = (Declaration) a.get(0);
					} else {
						if (!isSingle[0]) {
							s = newModifierDeclaration(modifier, syntax[0], a);
						}
					}
				}
				s.startPosition = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.startPosition;
				break;

			case TOKextern:
				saveToken = new Token(token);
				
				if (peek(token).value != TOKlparen) {
					// goto Lstc;
					nextToken();
					
					syntax = new ModifierDeclaration.Syntax[1];
					tempObj = parseDeclDefs_Lstc2(a, isSingle, syntax, modifiers);
					a = (List) tempObj[0];
					s = (Declaration) tempObj[1];
					break;
				}
				{
					LINK linksave = linkage;
					linkage = parseLinkage();
					a = parseBlock();
					
					s = newExternDeclaration(linkage, a);
					s.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
					
					linkage = linksave;
					break;
				}
			case TOKprivate:
			case TOKpackage:
			case TOKprotected:
			case TOKpublic:
			case TOKexport:
				
				modifier = newModifierFromCurrentToken();
				modifiers.add(modifier);
				
				// goto Lprot;
				saveToken = new Token(token);
				nextToken();
				
				syntax = new ModifierDeclaration.Syntax[1]; 
				a = parseBlock(isSingle, syntax, modifiers);
				if (a != null) {
					if (isSingle[0]) {						
						s = (Declaration) a.get(0);
					} else {
						s = newModifierDeclaration(modifier, syntax[0], a);
					}
				} else {
					if (!isSingle[0]) {
						s = newModifierDeclaration(modifier, syntax[0], a);
					}
				}
				if (s != null) {
					s.startPosition = saveToken.ptr;
					s.length = prevToken.ptr + prevToken.len - s.startPosition;
				}
				break;
				
			case TOKalign: {
				long n;

				s = null;
				saveToken = new Token(token);
				nextToken();
				if (token.value == TOKlparen) {
					nextToken();
					if (token.value == TOKint32v)
						n = token.numberValue.intValue();
					else {
						problem("Integer expected", IProblem.SEVERITY_ERROR, IProblem.INTEGER_EXPECTED, token.ptr, token.len);
						n = 1;
					}
					nextToken();
					check(TOKrparen);
				} else {
					n = global.structalign; // default
				}

				a = parseBlock();

				s = newAlignDeclaration((int) n, a);
				s.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
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
				
				s = newPragmaDeclaration(ident, args, a);
				s.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				break;
			}

			case TOKdebug:
				saveToken = new Token(token);
				
				nextToken();
				if (token.value == TOKassign) {
					nextToken();
					if (token.value == TOKidentifier || token.value == TOKint32v) {
						s = newDebugAssignmentForCurrentToken();
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
						s.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
					}
					
					break;
				}

				DebugCondition debugCondition = parseDebugCondition();
				
				a = parseBlock();
				aelse = null;
				if (token.value == TOKelse) {
					nextToken();
					aelse = parseBlock();
				}
				
				s = newDebugDeclaration(debugCondition, a, aelse);
				s.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				break;

			case TOKversion:
				saveToken = new Token(token);
				
				nextToken();
				if (token.value == TOKassign) {
					nextToken();
					if (token.value == TOKidentifier || token.value == TOKint32v) {
						s = newVersionAssignmentForCurrentToken();
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
						s.startPosition = saveToken.ptr;
						s.length = prevToken.ptr + prevToken.len - s.startPosition;
					}
					
					break;
				}
				
				VersionCondition versionCondition = parseVersionCondition();
				
				a = parseBlock();
				aelse = null;
				if (token.value == TOKelse) {
					nextToken();
					aelse = parseBlock();
				}
				
				s = newVersionDeclaration(versionCondition, a, aelse);				
				s.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				break;

			case TOKiftype:
				saveToken = new Token(token);
				
				IftypeCondition iftypeCondition = parseIftypeCondition();
				
				a = parseBlock();
				aelse = null;
				if (token.value == TOKelse) {
					nextToken();
					aelse = parseBlock();
				}				

				s = newIftypeDeclaration(iftypeCondition, a, aelse);
				s.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
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
				s.modifiers().addAll(modifiers);
				modifiers.clear();
				
				s.comments = lastComments;
				adjustLastDocComment();
				decldefs.add(s);				
			}
		} while (!once);
		return decldefs;
	}
	
	private Declaration parseDeclDefs_Lerror() {
		while (token.value != TOKsemicolon && token.value != TOKeof)
		    nextToken();
		nextToken();
		return null;
	}
	
	/*
	// a, aelse, s
	private Object[] parseDeclDefs_Lcondition(Condition condition) {
		List<Declaration> a = parseBlock();
		List<Declaration> aelse = null;
		if (token.value == TOKelse)
		{   nextToken();
		    aelse = parseBlock();
		}
		Dsymbol s = new ConditionalDeclaration(condition, a, aelse);
		return new Object[] { a, aelse, s };
	}
	*/
	
	// a, stc, s
	private Object[] parseDeclDefs_Lstc2(List<Declaration> a, boolean[] isSingle, ModifierDeclaration.Syntax[] syntax, List<Modifier> modifiers) {
		// TODO
		boolean repeat = true;
		while(repeat) {
			switch (token.value)
			{
			    case TOKconst:	  modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.CONST_KEYWORD)); nextToken(); break;
			    case TOKfinal:	  modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.FINAL_KEYWORD)); nextToken(); break;
			    case TOKauto:	  nextToken(); modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.AUTO_KEYWORD)); break;
			    case TOKscope:	  nextToken(); modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.SCOPE_KEYWORD)); break;
			    case TOKoverride:	  nextToken(); modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.OVERRIDE_KEYWORD)); break;
			    case TOKabstract:	  nextToken(); modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.ABSTRACT_KEYWORD)); break;
			    case TOKsynchronized: nextToken(); modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD)); break;
			    case TOKdeprecated:   nextToken(); modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.DEPRECATED_KEYWORD)); break;
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
			isSingle[0] = true;
			
		    Identifier ident = token.ident;
		    nextToken();
		    nextToken();
		    Initializer init = parseInitializer();
		    
		    VariableDeclaration variableDeclaration = newVariableDeclaration(null);
		    variableDeclaration.fragments().add(newVariableDeclarationFragment(ident, init));
		    
		    if (token.value != TOKsemicolon) {
		    	problem("Semicolon expected following auto declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
		    } else {
		    	nextToken();
		    }
		    
		    return new Object[] { a, variableDeclaration }; 
		}
		else
		{   
			a = parseBlock(isSingle, syntax, modifiers);
			
			if (isSingle[0]) {
				return new Object[] { a, a.get(0) };
			} else {
				
				ModifierDeclaration modifierDeclaration = newModifierDeclaration(null, syntax[0], a);				
			    return new Object[] { a, modifierDeclaration };
			}			 
		}
	}
	
	private List<Declaration> parseBlock() {
		return parseBlock(null, null, new ArrayList<Modifier>());
	}
	
	private List<Declaration> parseBlock(boolean[] isSingle,
			ModifierDeclaration.Syntax[] syntax, List<Modifier> modifiers) {
		List<Declaration> a = null;

		switch (token.value) {
		case TOKsemicolon:
			problem("Declaration expected following attribute",
					IProblem.SEVERITY_ERROR, IProblem.DECLARATION_EXPECTED,
					token.ptr, token.len);
			nextToken();
			break;

		case TOKlcurly:
			nextToken();
			if (syntax != null) {
				syntax[0] = ModifierDeclaration.Syntax.CURLY_BRACES;
			}
			a = parseDeclDefs(false, modifiers);
			if (token.value != TOKrcurly) { /* { */
				problem("Matching '}' expected", IProblem.SEVERITY_ERROR,
						IProblem.MATCHING_CURLY_EXPECTED, token.ptr, token.len);
			} else
				nextToken();
			break;

		case TOKcolon:
			nextToken();
			if (syntax != null) {
				syntax[0] = ModifierDeclaration.Syntax.COLON;
			}
			// #if 1
			// a = null;
			// #else
			a = parseDeclDefs(false, modifiers); // grab declarations up to closing curly
										// bracket
			// #endif
			break;

		default:
			a = parseDeclDefs(true, modifiers);
			if (isSingle != null) {
				isSingle[0] = true;
			}
			break;
		}
		return a;
	}
	
	private StaticAssert parseStaticAssert() {
		Expression exp;
		Expression msg = null;

		nextToken();
		check(TOKlparen);
		exp = parseAssignExp();
		if (token.value == TOKcomma) {
			nextToken();
			msg = parseAssignExp();
		}
		check(TOKrparen);
		check(TOKsemicolon);

		return newStaticAssert(exp, msg);
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
				problem("valid linkage identifiers are D, C, C++, Pascal, Windows", IProblem.SEVERITY_ERROR, IProblem.INVALID_LINKAGE_IDENTIFIER, id.startPosition, id.length);
				link = LINKd;
			}
		} else {
			link = LINKdefault; // default
		}
		check(TOKrparen);
		return link;
	}
	
	private DebugCondition parseDebugCondition() {
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
				level = token.numberValue.intValue();
			} else {
				problem("Identifier or integer expected",
						IProblem.SEVERITY_ERROR,
						IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, token.ptr,
						token.len);
			}
			nextToken();
			check(TOKrparen);
			c = new DebugCondition(level, id);
		} else {
			c = new DebugCondition(1, null);
		}
		if (id == null && idToken != null) {
			c.id = new Identifier(String.valueOf(level), TOK.TOKint32);
			c.id.startPosition = idToken.ptr;
			c.id.length = idToken.len;
		}
		return c;
	}
	
	private VersionCondition parseVersionCondition() {
		VersionCondition c;
		long level = 1;
		Identifier id = null;

		Token idToken = null;

		if (token.value == TOKlparen) {
			nextToken();
			if (token.value == TOKidentifier) {
				id = new Identifier(token);
			} else if (token.value == TOKint32v) {
				idToken = new Token(token);
				level = token.numberValue.intValue();
			} else {
				problem("Identifier or integer expected",
						IProblem.SEVERITY_ERROR,
						IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, token.ptr,
						token.len);
			}
			nextToken();
			check(TOKrparen);
		} else {
			problem("(condition) expected following version",
					IProblem.SEVERITY_ERROR,
					IProblem.CONDITION_EXPECTED_FOLLOWING_VERSION, token.ptr,
					token.len);
		}
		c = new VersionCondition(level, id);
		if (id == null && idToken != null) {
			c.id = new Identifier(String.valueOf(level), TOK.TOKint32);
			c.id.startPosition = idToken.ptr;
			c.id.length = idToken.len;
		}
		return c;
	}
	
	private StaticIfCondition parseStaticIfCondition() {
		Expression exp;
		nextToken();
		if (token.value == TOKlparen) {
			nextToken();
			exp = parseAssignExp();
			check(TOKrparen);
		} else {
			problem("(expression) expected following version",
					IProblem.SEVERITY_ERROR, IProblem.EXPRESSION_EXPECTED,
					token.ptr, token.len);
			exp = null;
		}
		return new StaticIfCondition(exp);
	}
	
	private IftypeCondition parseIftypeCondition() {
		Type targ;
		Identifier[] ident = new Identifier[1];
		Type tspec = null;
		TOK tok = TOKreserved;

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
			problem(
					"(type identifier : specialization) expected following iftype",
					IProblem.SEVERITY_ERROR, IProblem.INVALID_IFTYPE_SYNTAX,
					token.ptr, token.len);
			return null;
		}

		problem(
				"iftype(condition) is deprecated, use static if (is(condition))",
				IProblem.SEVERITY_WARNING, IProblem.IFTYPE_DEPRECATED,
				firstToken.ptr, firstToken.len);

		return new IftypeCondition(targ, ident[0], tok, tspec);
	}
	
	private FunctionDeclaration parseCtor() {
		SimpleName name = newSimpleNameForCurrentToken();
		
		int[] varargs = new int[1];
	    nextToken();
	    List<Argument> arguments = parseParameters(varargs);
	    FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.CONSTRUCTOR, null, name, arguments, varargs[0]);
	    f.startPosition = name.getStartPosition();
	    parseContracts(f);
	    return f;
	}
	
	private FunctionDeclaration parseDtor() {
		Token firstToken = new Token(token);
		nextToken();
		Token secondToken = new Token(token);
		
		check(TOKthis);
	    check(TOKlparen);
	    check(TOKrparen);
		
		SimpleName name = newSimpleName();
		name.setSourceRange(firstToken.ptr, secondToken.ptr + secondToken.len - firstToken.ptr);
		name.setIdentifier("~this");
		
		FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.DESTRUCTOR, null, name, null, 0);
		f.startPosition = firstToken.ptr;
	    parseContracts(f);
	    return f;
	}
	
	private FunctionDeclaration parseStaticCtor() {
		SimpleName name = newSimpleNameForCurrentToken();
		
		nextToken();
	    check(TOKlparen);
	    check(TOKrparen);

	    FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.STATIC_CONSTRUCTOR, null, name, null, 0);
		f.setSourceRange(token.ptr, 0);
	    parseContracts(f);
	    return f;
	}
	
	private FunctionDeclaration parseStaticDtor() {
		Token firstToken = new Token(token);
		nextToken();
		Token secondToken = new Token(token);
		
		check(TOKthis);
	    check(TOKlparen);
	    check(TOKrparen);
	    
	    SimpleName name = newSimpleName();
		name.setSourceRange(firstToken.ptr, secondToken.ptr + secondToken.len - firstToken.ptr);
		name.setIdentifier("~this");
		
		FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.STATIC_DESTRUCTOR, null, name, null, 0);
		f.setSourceRange(firstToken.ptr, 0);
	    parseContracts(f);
	    return f;
	}
	
	private InvariantDeclaration parseInvariant() {
		int start = token.ptr;
	    nextToken();

	    InvariantDeclaration invariant = newInvariantDeclaration();
	    invariant.setBody(parseStatement(PScurly));
	    invariant.setSourceRange(start, prevToken.ptr + prevToken.len - start);
	    return invariant;
	}
	
	private UnitTestDeclaration parseUnitTest() {
		int start = token.ptr;
		nextToken();
		
		UnitTestDeclaration unitTest = newUnitTestDeclaration();
	    unitTest.setBody(parseStatement(PScurly));
	    unitTest.setSourceRange(start, prevToken.ptr + prevToken.len - start);
	    return unitTest;
	}
	
	private FunctionDeclaration parseNew() {
		SimpleName name = newSimpleNameForCurrentToken();
				
		nextToken();
		int[] varargs = new int[1];
		List<Argument> arguments = parseParameters(varargs);
		
		FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.NEW, null, name, arguments, varargs[0]);
		f.startPosition = name.getStartPosition();
	    parseContracts(f);
	    return f;
	}
	
	private FunctionDeclaration parseDelete() {
		SimpleName name = newSimpleNameForCurrentToken();
		
		nextToken();
		int[] varargs = new int[1];
		List<Argument> arguments = parseParameters(varargs);
		
		if (varargs[0] != 0) {
	    	problem("... not allowed in delete function parameter list", 
	    			IProblem.SEVERITY_ERROR, 
	    			IProblem.VARIADIC_NOT_ALLOWED_IN_DELETE, 
	    			name);
	    }
		
		FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.DELETE, null, name, arguments, varargs[0]);
		f.startPosition = name.getStartPosition();
		f.arguments().addAll(arguments);
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
			Argument.PassageMode inout;
			Expression ae;
			
			Token firstToken = new Token(token);
			
			ai = null;
			inout = Argument.PassageMode.IN; // parameter is "in" by default
			
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
						inout = Argument.PassageMode.IN;
						nextToken();
						break;
					case TOKout:
						inout = Argument.PassageMode.OUT;
						nextToken();
						break;
					case TOKinout:
						inout = Argument.PassageMode.INOUT;
						nextToken();
						break;
					case TOKlazy:
						inout = Argument.PassageMode.LAZY;
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
						int start, length;
						if (ai == null) {
							start = at.getStartPosition();
							length = at.getLength();
						} else {
							start = ai.startPosition;
							length = ai.length;
						}
						problem("Default argument expected", IProblem.SEVERITY_ERROR, IProblem.DEFAULT_ARGUMENT_EXPECTED, start, length);
					}
				}
				if (token.value == TOKdotdotdot) { 
					/*
					 * This is: at ai ...
					 */
					if (inout == Argument.PassageMode.OUT || inout == Argument.PassageMode.INOUT) {
						problem("Variadic argument cannot be out or inout", IProblem.SEVERITY_ERROR, IProblem.VARIADIC_ARGUMENT_CANNOT_BE_OUT_OR_INOUT, inoutToken.ptr, inoutToken.len);
					}
					varargs = 2;
					
					a = newArgument(inout, at, ai, ae);
					arguments.add(a);
					nextToken();
					break;
				}
				
				a = newArgument(inout, at, ai, ae);
				a.setSourceRange(firstToken.ptr, prevToken.ptr + prevToken.len - firstToken.ptr);
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

		Identifier id;
		Type t;
		
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
		
		EnumDeclaration e = newEnumDeclaration(id, t);
		e.startPosition = enumToken.ptr;
		
		if (token.value == TOKsemicolon && id != null) {
			e.length = token.ptr + token.len - e.startPosition;
			nextToken();			  
		} else if (token.value == TOKlcurly) {
			nextToken();
			String comment = token.string;
			while (token.value != TOKrcurly) {
				if (token.value == TOKeof) {
					problem("Enum declaration is invalid", IProblem.SEVERITY_ERROR, IProblem.ENUM_DECLARATION_IS_INVALID, enumToken.ptr, enumToken.len);
					break;
				}
				
				if (token.value == TOKidentifier) {
					Expression value;
					Identifier ident;

					ident = new Identifier(token);
					value = null;
					nextToken();
					if (token.value == TOKassign) {
						nextToken();
						value = parseAssignExp();
					}
					
					EnumMember em = newEnumMember(newSimpleNameForIdentifier(ident), value);
					e.enumMembers().add(em);
					if (token.value == TOKrcurly) {
						;
					} else {
						addComment(em, comment);
						comment = null;
						check(TOKcomma);
					}
					addComment(em, comment);
					// TODO MARS comment = token.comment;
				} else {
					problem("Enum member expected", IProblem.SEVERITY_ERROR, IProblem.ENUM_MEMBER_EXPECTED, token.ptr, token.len);
					nextToken();
				}
			}
			e.length = token.ptr + token.len - e.startPosition;
			
			nextToken();
		} else {
			problem("Enum declaration is invalid", IProblem.SEVERITY_ERROR, IProblem.ENUM_DECLARATION_IS_INVALID, enumToken.ptr, enumToken.len);
		}
		
		return e;
	}
	
	@SuppressWarnings("unchecked")
	private AggregateDeclaration parseAggregate() {
		AggregateDeclaration a = null;
		int anon = 0;
		TOK tok;
		Identifier id;
		List<TemplateParameter> tpl = null;
		
		Token firstToken = new Token(token);

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

		switch (tok) {
		case TOKclass:
		case TOKinterface: {
			if (id == null) {
				problem("Anonymous classes not allowed", IProblem.SEVERITY_ERROR, IProblem.ANONYMOUS_CLASSES_NOT_ALLOWED,
						firstToken.ptr, firstToken.len);
			}

			// Collect base class(es)
			List<BaseClass> baseClasses = null;
			if (token.value == TOKcolon) {
				nextToken();
				baseClasses = parseBaseClasses();

				if (token.value != TOKlcurly) {
					if (baseClasses != null &&  baseClasses.size() > 0) {
						BaseClass last = baseClasses.get(baseClasses.size() - 1);
						problem("Members expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED,
								last.getStartPosition(), last.getLength());
					}
				}
			}
			
			a = newAggregateDeclaration(tok, id, baseClasses);
			a.startPosition = firstToken.ptr;
			break;
		}

		case TOKstruct:
			//if (id != null) {
			
				a = newAggregateDeclaration(tok, id, null);
				a.startPosition = firstToken.ptr;
			//} else {
			//	anon = 1;
			//}
			break;

		case TOKunion:
			//if (id != null) {
				a = newAggregateDeclaration(tok, id, null);
				a.startPosition = firstToken.ptr;
			//} else {
			//	anon = 2;
			//}
			break;

		default:
			assert (false);
			break;
		}
		
		if (a != null && token.value == TOKsemicolon) {
			a.length = token.ptr + token.len - a.startPosition;
			nextToken();
		} else if (token.value == TOKlcurly) {
			Token lcurlyToken = new Token(token);
			nextToken();
			List decl = parseDeclDefs(false, new ArrayList<Modifier>());
			if (token.value != TOKrcurly) {
				problem("} expected following member declarations in aggregate", IProblem.SEVERITY_ERROR, IProblem.RIGHT_CURLY_EXPECTED_FOLLOWING_MEMBER_DECLARATIONS_IN_AGGREGATE,
						lcurlyToken.ptr, lcurlyToken.len);
			}
			
			if (a != null) {
				a.length = token.ptr + token.len - a.startPosition;
			}
			
			nextToken();
			if (anon != 0) {
				throw new IllegalStateException("Can't happen");
				/*
				 * Anonymous structs/unions are more like attributes.
				 */
				//return new AnonDeclaration(loc, anon - 1, decl);
			} else {
				a.declarations().addAll(decl);
			}
		} else {
			if (a.getName() == null) {
				problem("{ } expected following aggregate declaration", IProblem.SEVERITY_ERROR, IProblem.CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION,
						firstToken.ptr, firstToken.len);
			} else {
				problem("{ } expected following aggregate declaration", IProblem.SEVERITY_ERROR, IProblem.CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION,
						firstToken.ptr, a.getName().getStartPosition() + a.getName().getLength() - firstToken.ptr);
			}
			
			a = newAggregateDeclaration(TOK.TOKstruct, null, null);
		}

		if (tpl != null) {
			a.templateParameters().addAll(tpl);
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
	
	private List<BaseClass> parseBaseClasses() {
		List<BaseClass> baseclasses = new ArrayList<BaseClass>();
		Modifier modifier = null;

		for (; true; nextToken()) {
			switch (token.value) {
			case TOKidentifier:
				break;
			case TOKprivate:
			case TOKpackage:
			case TOKprotected:
			case TOKpublic:
				modifier = newModifierFromCurrentToken();
				continue;
			default:
				problem("Base class expected", IProblem.SEVERITY_ERROR,
						IProblem.BASE_CLASS_EXPECTED, token.ptr, token.len);
				return null;
			}
			BaseClass b = newBaseClass(parseBasicType(), modifier);
			baseclasses.add(b);
			if (token.value != TOKcomma) {
				break;
			}
		}
		return baseclasses;
	}
	
	private TemplateDeclaration parseTemplateDeclaration() {
	    Identifier id;
	    List<TemplateParameter> tpl;
	    List<Declaration> decldefs;

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
		decldefs = parseDeclDefs(false, new ArrayList<Modifier>());
		if (token.value != TOKrcurly)
		{
			problem("Template member expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
		    //goto Lerr;
			return null;
		}
		nextToken();
	    }

	    
	    TemplateDeclaration tempdecl = newTemplateDeclaration(id, tpl, decldefs);
	    tempdecl.setSourceRange(firstToken.ptr, prevToken.ptr + prevToken.len - firstToken.ptr);
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
			
			boolean isvariadic = false;
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
					
					tp = newAliasTemplateParamete(tp_ident, tp_spectype, tp_defaulttype);
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
					
					tp = newTypeTemplateParameter(tp_ident, tp_spectype, tp_defaulttype);
				}
			    else if (token.value == TOKidentifier && t.value == TOKdotdotdot)
			    {	// ident...
			    	if (isvariadic) {
			    		problem("Variadic template parameter must be last one", IProblem.SEVERITY_ERROR, IProblem.VARIADIC_TEMPLATE_PARAMETER_MUST_BE_LAST_ONE, 
				    			token.ptr, 
				    			t.ptr + t.len - token.ptr);
			    	}
					isvariadic = true;
					tp_ident = new Identifier(token);
					nextToken();
					nextToken();
					
					tp = newTupleTemplateParameter(tp_ident);
				} else { // ValueParameter
					tp_valtype = parseBasicType();

					Identifier[] pointer2_tp_ident = new Identifier[] { tp_ident };
					tp_valtype = parseDeclarator(tp_valtype, pointer2_tp_ident);
					tp_ident = pointer2_tp_ident[0];
					if (tp_ident == null) {
						problem("No identifier for template value parameter", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, t.ptr, t.len);
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
					
					tp = newValueTemplateParameter(tp_ident, tp_valtype, tp_specvalue, tp_defaultvalue);
				}
				tp.startPosition = firstToken.ptr;
				tp.length = prevToken.ptr + prevToken.len - tp.startPosition;
				
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
	
	@SuppressWarnings("unchecked")
	private Declaration parseMixin() {
		MixinDeclaration tm;
		Identifier id = null;
		List<ASTNode> tiargs;
		
		Type type = null;
		Token firstToken = new Token(token);

		nextToken();
		
		int start = token.ptr;
		boolean foundOneType = false;

		if (token.value == TOKdot) {
			/*
			id = new Identifier(Id.empty, TOKidentifier);
			id.startPosition = token.ptr;
			*/
			foundOneType = true;
		} else {
			if (token.value == TOKtypeof) {
				Expression exp;

				nextToken();
				check(TOKlparen);
				exp = parseExpression();
				check(TOKrparen);
				
				type = newTypeofType(exp);
				type.setSourceRange(start, prevToken.ptr + prevToken.len - start);
				check(TOKdot);
				
				foundOneType = true;
			}
			if (token.value != TOKidentifier) {
				problem("Identifier expected", IProblem.SEVERITY_ERROR,
						IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				// goto Lerr;
				return null;
			}
			id = new Identifier(token);
			nextToken();
			
			if (token.value != TOKnot) {
				if (type == null) {
					type = newSimpleType(id);
					foundOneType = true;
				} else {
					type = newQualifiedType(type, newSimpleType(id), type.getStartPosition());
					foundOneType = true;
				}
			}
		}

		while (true) {
			tiargs = null;
			if (token.value == TOKnot) {
				nextToken();
				tiargs = parseTemplateArgumentList();
				
				if (foundOneType) {
					TemplateType templateType = newTemplateType(id, tiargs);
					templateType.setSourceRange(id.startPosition, prevToken.ptr + prevToken.len - id.startPosition);
					type = newQualifiedType(type, templateType, start);
				} else {
					type = newTemplateType(id, tiargs);
					type.setSourceRange(id.startPosition, prevToken.ptr + prevToken.len - id.startPosition);
				}
			}
			
			foundOneType = true;

			if (token.value != TOKdot)
				break;

			nextToken();
			if (token.value != TOKidentifier) {
				problem("Identifier expected", IProblem.SEVERITY_ERROR,
						IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				break;
			}
			id = new Identifier(token);
			nextToken();
			
			if (token.value != TOKnot) {
				type = newQualifiedType(type, newSimpleType(id), type.getStartPosition());
			}
		}

		if (token.value == TOKidentifier) {
			id = new Identifier(token);
			nextToken();
		} else {
			id = null;
		}
		
		tm = newMixinDeclaration(type, id);

		//tm = new MixinDeclaration(ast, id, tqual, idents, tiargs);
		if (token.value != TOKsemicolon) {
			problem("Semicolon expected following mixin",
					IProblem.SEVERITY_ERROR,
					IProblem.SEMICOLON_EXPECTED, token.ptr,
					token.len);
		}
		nextToken();
		
		tm.startPosition = firstToken.ptr;
		tm.length = prevToken.ptr + prevToken.len - tm.startPosition;

		return tm;
	    
	    // Lerr:
	    // return NULL;
	}

	@SuppressWarnings("unchecked")
	private List<ASTNode> parseTemplateArgumentList() {
	    List<ASTNode> tiargs = new ArrayList<ASTNode>();
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
	
	private ImportDeclaration parseImport(boolean isstatic) {
		ImportDeclaration importDeclaration = newImportDeclaration();

		Import theImport = null;
		SimpleName alias = null;
		SimpleName simpleName = null;
		Name name = null;
		int start = token.ptr;
		int theImportStart = 0;
		
		boolean repeat = true;
		while (repeat) {
			repeat = false;
		
		// ---	
		the_do:
		// ---
			do {
				// L1:
				nextToken();
				
				if (alias == null) {
					theImportStart = token.ptr;
				}

				if (token.value != TOKidentifier) {
					problem("Identifier expected following import",
							IProblem.SEVERITY_ERROR,
							IProblem.IDENTIFIER_EXPECTED, token.ptr,
							token.len);
					break;
				}
				
				name = newSimpleNameForCurrentToken();

				nextToken();

				if (alias == null && token.value == TOKassign) {
					alias = (SimpleName) name;
					// goto L1;
					repeat = true;
					break the_do;
				}

				while (token.value == TOKdot) {
					nextToken();
					if (token.value != TOKidentifier) {
						problem("Identifier expected following package",
								IProblem.SEVERITY_ERROR,
								IProblem.IDENTIFIER_EXPECTED, name
										.getStartPosition(), token.ptr
										- name.getStartPosition());
						break;
					}
					
					name = newQualifiedNameFromCurrentToken(name);
					nextToken();
				}

				theImport = newImport(name, alias);
				importDeclaration.imports().add(theImport);

				/*
				 * Look for : alias=name, alias=name; syntax.
				 */
				if (token.value == TOKcolon) {
					Token dotToken = new Token(token);

					do {
						SelectiveImport selectiveImport = newSelectiveImport();
						
						nextToken();

						if (token.value != TOKidentifier) {
							problem("Identifier expected following ':'",
									IProblem.SEVERITY_ERROR,
									IProblem.IDENTIFIER_EXPECTED, dotToken.ptr,
									dotToken.len);
							break;
						}
						
						alias = newSimpleNameForCurrentToken();

						nextToken();
						if (token.value == TOKassign) {
							nextToken();
							if (token.value != TOKidentifier) {
								problem("Identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
								break;
							}
							
							simpleName = newSimpleNameForCurrentToken();
							
							selectiveImport.setAlias(alias);
							selectiveImport.setName(simpleName);
							selectiveImport.setSourceRange(alias.getStartPosition(), simpleName.getStartPosition() + simpleName.getLength() - alias.getStartPosition());
							
							nextToken();
						} else {
							selectiveImport.setName(alias);
							selectiveImport.setSourceRange(alias.getStartPosition(), alias.getLength());
						}
						
						theImport.setSourceRange(theImportStart, prevToken.ptr + prevToken.len - theImportStart);
						theImport.selectiveImports().add(selectiveImport);
					} while (token.value == TOKcomma);
					break; // no comma-separated imports of this form
				} else {
					theImport.setSourceRange(theImportStart, prevToken.ptr + prevToken.len - theImportStart);
				}

				alias = null;
			} while (token.value == TOKcomma);
		}
		
		importDeclaration.setStatic(isstatic);
		importDeclaration.setSourceRange(start, token.ptr + token.len - start);

		if (token.value == TOKsemicolon) {
			nextToken();
		} else {
			problem("';' expected following import declaration",
					IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED,
					token.ptr, token.len);
			nextToken();
		}

		return importDeclaration;
	}
	
	private Type parseBasicType() {
		Type t = null;
		Identifier id = null;
		Type tid = null;
		
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
			t = newPrimitiveTypeFromCurrentToken();
			nextToken();
			break;

		case TOKidentifier:
			id = new Identifier(token);
			nextToken();
			if (token.value == TOKnot) {
				nextToken();
				
				List<ASTNode> arguments = parseTemplateArgumentList();
				tid = newTemplateType(id, arguments);
				tid.setSourceRange(id.startPosition, prevToken.ptr + prevToken.len - id.startPosition);
				// goto Lident2;
				{
				Identifier[] p_id = { id };
				Type[] p_tid = { tid };
				Type[] p_t = { t };
				parseBasicType_Lident2(p_id, p_tid, p_t, id.startPosition);
				id = p_id[0];
				tid = p_tid[0];
				t = p_t[0];
				}
				break;

			}
			// Lident:
			tid = newSimpleType(id);
			tid.startPosition = prevToken.ptr;
			// Lident2:
			{
			Identifier[] p_id = { id };
			Type[] p_tid = { tid };
			Type[] p_t = { t };
			parseBasicType_Lident2(p_id, p_tid, p_t, id.startPosition);
			id = p_id[0];
			tid = p_tid[0];
			t = p_t[0];
			}
			break;

		case TOKdot:			
			// goto Lident;
			if (id == null) {
				tid = null;
			} else {
				tid = newSimpleType(id);
			}
			{
			Identifier[] p_id = { id };
			Type[] p_tid = { tid };
			Type[] p_t = { t };
			parseBasicType_Lident2(p_id, p_tid, p_t, token.ptr);
			id = p_id[0];
			tid = p_tid[0];
			t = p_t[0];
			}
			break;

		case TOKtypeof: {
			int start = token.ptr;
			Expression exp;

			nextToken();
			check(TOKlparen);
			exp = parseExpression();
			check(TOKrparen);
			
			tid = newTypeofType(exp);
			tid.setSourceRange(start, prevToken.ptr + prevToken.len - start);
			
			// goto Lident2;
			{
			Identifier[] p_id = { id };
			Type[] p_tid = { tid };
			Type[] p_t = { t };
			parseBasicType_Lident2(p_id, p_tid, p_t, start);
			id = p_id[0];
			tid = p_tid[0];
			t = p_t[0];
			}
			break;
		}

		default:
			problem("Basic type expected", IProblem.SEVERITY_ERROR,
					IProblem.BASIC_TYPE_EXPECTED, token.ptr, token.len);
		
			// TODO what to do?
			PrimitiveType pt = new PrimitiveType(ast);
			pt.setPrimitiveTypeCode(PrimitiveType.Code.INT);		
			t =  pt;
			break;
		}
		return t;
	}

	private void parseBasicType_Lident2(Identifier[] id, Type[] tid, Type[] t, int start) {
		while (token.value == TOKdot) {
			nextToken();
			if (token.value != TOKidentifier) {
				problem("Identifier expected", IProblem.SEVERITY_ERROR,
						IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				break;
			}
			id[0] = new Identifier(token);
			nextToken();
			if (token.value == TOKnot) {
				nextToken();
				
				List<ASTNode> arguments = parseTemplateArgumentList();
				TemplateType templateType = newTemplateType(id[0], arguments);
				templateType.setSourceRange(id[0].startPosition, prevToken.ptr + prevToken.len - id[0].startPosition);
				
				tid[0] = newQualifiedType(tid[0], templateType, start);
				tid[0].setSourceRange(start, prevToken.ptr + prevToken.len - start);
			} else {
				tid[0] = newQualifiedType(tid[0], id[0], start);
			}
		}
		tid[0].length = prevToken.ptr + prevToken.len - tid[0].startPosition;
		t[0] = tid[0];
	}

	private Type parseBasicType2(Type t) {
		Type ts;
		Type ta;
		Type subType;

		while (true) {
			switch (token.value) {
			case TOKmul:
				subType = t;
				
				t = newPointerType(subType);
				t.setSourceRange(subType.getStartPosition(), token.ptr + token.len - subType.getStartPosition());
				
				nextToken();
				continue;

			case TOKlbracket:
				if (LTORARRAYDECL) {
					// Handle []. Make sure things like
					// int[3][1] a;
					// is (array[1] of array[3] of int)
					nextToken();
					if (token.value == TOKrbracket) {
						subType = t;
						
						t = newDynamicArrayType(subType);
						t.setSourceRange(subType.getStartPosition(), token.ptr + token.len - subType.getStartPosition());
						
						nextToken();
					} else if (isDeclaration(token, 0, TOKrbracket, null)) { // It's
																				// an
																				// associative
																				// array
																				// declaration
						subType = t;
						Type index;

						index = parseBasicType();
						index = parseDeclarator(index, null); // [ type ]
						
						t = newAssociativeArrayType(t, index);
						check(TOKrbracket);
					} else {
						subType = t;

						inBrackets++;
						Expression e = parseExpression(); // [ expression ]
					    if (token.value == TOKslice) {
							Expression e2;

							nextToken();
							e2 = parseExpression(); // [ exp .. exp ]
							
							t = newSliceType(t, e, e2);
						} else {
							t = newStaticArrayType(t, e);
						}
						t.startPosition = subType.startPosition;
						t.length = token.ptr + token.len - t.startPosition;
						
						inBrackets--;
						
						check(TOKrbracket);
					}
					continue;
				} else {
					// Handle []. Make sure things like
					// int[3][1] a;
					// is (array[3] of array[1] of int)
					ts = t;
					while (token.value == TOKlbracket) {
						nextToken();
						if (token.value == TOKrbracket) {// []
							ta = newDynamicArrayType(t);
							nextToken();
						} else if (isDeclaration(token, 0, TOKrbracket, null)) { // It's
																					// an
																					// associative
																					// array
																					// declaration
							Type index;

							index = parseBasicType();
							index = parseDeclarator(index, null); // [ type ]
							
							ta = newAssociativeArrayType(t, index);
							
							check(TOKrbracket);
						} else {
							Expression e = parseExpression(); // [ expression
																// ]
							ta = newStaticArrayType(t, e);
							check(TOKrbracket);
						}

						if (ts != t) {
							IDmdType pt = TypeAdapter.getAdapter(ts);
							while (pt.getNext() != t) {
								pt = TypeAdapter.getAdapter(pt.getNext());
							}
							pt.setNext(ta);
						} else {
							ts = ta;
						}
					}
					t = ts;
					continue;
				}

			case TOKdelegate:
			case TOKfunction: { // Handle delegate declaration:
				// t delegate(parameter list)
				// t function(parameter list)
				List<Argument> arguments;
				int varargs = 0;
				TOK save = token.value;

				nextToken();

				int[] pointer2_varargs = { varargs };
				arguments = parseParameters(pointer2_varargs);
				varargs = pointer2_varargs[0];

				int saveStart = t.startPosition;

				DmdTypeFunction typeFunction = new DmdTypeFunction(ast, arguments, t, varargs != 0, linkage);
				
				t = newDelegateType(save, typeFunction, varargs);
				t.setSourceRange(saveStart, prevToken.ptr + prevToken.len - saveStart);
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
			int oldStart = t.startPosition;
		    nextToken();
		    ts = parseDeclarator(t, pident, null, identStart);
		    ts.startPosition = oldStart;
		    ts.length = token.ptr + token.len - ts.startPosition;
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
			if (token.value == TOKrbracket) // []
			{
			    ta = newDynamicArrayType(t);
			    ta.setSourceRange(t.getStartPosition(), token.ptr + token.len - t.getStartPosition());
			    
			    nextToken();
			}
			else if (isDeclaration(token, 0, TOKrbracket, null))
			{   // It's an associative array declaration
			    Type index;

			    index = parseBasicType();
			    index = parseDeclarator(index, null, null, identStart);	// [ type ]
			    
			    ta = newAssociativeArrayType(t, index);
			    
			    check(TOKrbracket);
			}
			else
			{
			    Expression e = parseExpression();		// [ expression ]
			    
			    ta = newStaticArrayType(t, e);
			    ta.setSourceRange(t.startPosition, token.ptr + token.len - ta.startPosition);
			    check(TOKrbracket);
			}
			
			if (ts != t) {
				IDmdType pt = TypeAdapter.getAdapter(ts);
				while(pt.getNext() != t) {
					pt = TypeAdapter.getAdapter(pt.getNext());
				}
				pt.setNext(ta);
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

				// Gather template parameter list
				tpl[0] = parseTemplateParameterList();
			    }
			}

			int[] pointer2_varargs = { varargs };
			arguments = parseParameters(pointer2_varargs);
			varargs = pointer2_varargs[0];
			
			ta = new DmdTypeFunction(ast, arguments, t, varargs != 0, linkage);
			ta.startPosition = t.startPosition;
			ta.length = t.length;
			
			DmdTypeFunction typeFunction = (DmdTypeFunction) ta;

			if (ts != t) {
				IDmdType pt = TypeAdapter.getAdapter(ts);
				IDmdType previous = null;
				while(pt.getNext() != t) {
					previous = pt;
					pt = TypeAdapter.getAdapter(pt.getNext());
				}
				
				if (pt.getAdaptedType() instanceof PointerType) {
					DelegateType delegateType = newDelegateType(TOKfunction, typeFunction, varargs);
					if (previous == null) {
						ts = delegateType;
					} else {
						previous.setNext(delegateType);
					}
				} else {
					throw new RuntimeException("Not expected");
				}
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
	
	// TODO add modifiers to typedef, alias and variable declarations
	@SuppressWarnings("unchecked")
	private List<Declaration> parseDeclarations() {
		Type ts;
		Type t;
		Type tfirst;
		Identifier ident;
		List a;
		TOK tok;
		LINK link = linkage;
		
		List<Comment> lastComments = getLastDocComments();
		List<Modifier> modifiers = new ArrayList<Modifier>();
		
		Token firstToken = new Token(token);

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

		while (true) {
			switch (token.value) {
			case TOKconst:
			case TOKstatic:
			case TOKfinal:
			case TOKauto:
			case TOKscope:
			case TOKoverride:
			case TOKabstract:
			case TOKsynchronized:
			case TOKdeprecated:
				Modifier currentModifier = newModifierFromCurrentToken();
				for(Modifier previousModifier : modifiers) {
					if (previousModifier.getModifierKeyword().equals(currentModifier.getModifierKeyword())) {
						problem("Redundant storage class", IProblem.SEVERITY_ERROR, IProblem.REDUNDANT_STORAGE_CLASS, currentModifier);
					}
				}
				modifiers.add(currentModifier);
				nextToken();
				continue;

			case TOKextern:
				if (peek(token).value != TOKlparen) {
					currentModifier = newModifierFromCurrentToken();
					for(Modifier previousModifier : modifiers) {
						if (previousModifier.getModifierKeyword().equals(currentModifier.getModifierKeyword())) {
							problem("Redundant storage class", IProblem.SEVERITY_ERROR, IProblem.REDUNDANT_STORAGE_CLASS, currentModifier);
						}
					}
					modifiers.add(currentModifier);
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
		if (modifiers.size() > 0 && token.value == TOKidentifier
				&& peek(token).value == TOKassign) {
			ident = token.ident;
			nextToken();
			nextToken();
			Initializer init = parseInitializer();
			
			VariableDeclaration variableDeclaration = newVariableDeclaration(null);
			variableDeclaration.modifiers().addAll(modifiers);
			
			variableDeclaration.fragments().add(newVariableDeclarationFragment(ident, init));
			
			a.add(variableDeclaration);
			if (token.value == TOKsemicolon) {
				nextToken();
				variableDeclaration.comments = lastComments;
				adjustLastDocComment();
			} else {
				problem("Semicolon expected following auto declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
			}
			return a;
		}

		if (token.value == TOKclass) {
			AggregateDeclaration s;

			s = (AggregateDeclaration) parseAggregate();
			s.modifiers().addAll(modifiers);
			a.add(s);
			s.comments = lastComments;
			adjustLastDocComment();
			return a;
		}
		
		int nextVarStart = token.ptr;
		int nextTypdefOrAliasStart = firstToken.ptr;

		ts = parseBasicType();
		ts = parseBasicType2(ts);
		tfirst = null;
		
		int[] identStart = new int[1];
		AliasDeclaration aliasDeclaration = null;
		TypedefDeclaration typedefDeclaration = null;
		VariableDeclaration variableDeclaration = null;
		boolean addDelcaration = true;
		
		while (true) {
			List<TemplateParameter> tpl = null;
			addDelcaration = true;

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
						ident.startPosition, ident.length);
			}
			if (ident == null) {
				problem("No identifier for declarator", IProblem.SEVERITY_ERROR, IProblem.NO_IDENTIFIER_FOR_DECLARATION, t.getStartPosition(), t.getLength());
			}
			
			if (tok != TOKalias && aliasDeclaration != null) {
				a.add(aliasDeclaration);
				aliasDeclaration = null;
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
					if (typedefDeclaration == null) {
						typedefDeclaration = newTypedefDeclaration(t);
						typedefDeclaration.startPosition = nextTypdefOrAliasStart;
					} else {
						addDelcaration = false;
					}
					
					typedefDeclaration.fragments().add(newTypedefDeclarationFragment(ident, init));
					v = typedefDeclaration;
				} else {
					if (init != null) {
						problem("Alias cannot have initializer", IProblem.SEVERITY_ERROR, IProblem.ALIAS_CANNOT_HAVE_INITIALIZER, tokAssign.ptr, init.startPosition + init.length - tokAssign.ptr);
					}
					
					if (aliasDeclaration == null) {
						aliasDeclaration = newAliasDeclaration(t);
						aliasDeclaration.startPosition = nextTypdefOrAliasStart;
					} else {
						addDelcaration = false;
					}
					
					if (ident != null) {
						AliasDeclarationFragment fragment = newAliasDeclarationFragment(ident);
						aliasDeclaration.fragments().add(fragment);
					}

					v = aliasDeclaration;
				}
				if (addDelcaration) {
					a.add(v);
				}
				switch (token.value) {
				case TOKsemicolon:
					v.length = token.ptr + token.len - v.startPosition;
					nextToken();
					v.comments = lastComments;
					adjustLastDocComment();
					break;

				case TOKcomma:
					v.length = prevToken.ptr + prevToken.len - v.startPosition;
					nextToken();
					nextTypdefOrAliasStart = token.ptr;
					v.comments = lastComments;
					adjustLastDocComment();
					continue;

				default:
					problem("Semicolon expected to close declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, v.startPosition, prevToken.ptr + prevToken.len - v.startPosition);
					break;
				}
			} else if (TypeAdapter.getAdapter(t).getTY() == Tfunction) {
				DmdTypeFunction typeFunction = (DmdTypeFunction) t;
				
				SimpleName name = newSimpleNameForIdentifier(ident);
				
				FunctionDeclaration function = newFunctionDeclaration(FunctionDeclaration.Kind.FUNCTION, 
						typeFunction.getReturnType(), name, typeFunction.getArguments(), typeFunction.varargs ? 1 : 0);
				function.startPosition = t.startPosition;
				function.comments = lastComments;
				adjustLastDocComment();
				
				parseContracts(function);
				
				Declaration s;
				if (link == linkage) {
					s = function;
				} else {
					List<Declaration> ax = new ArrayList<Declaration>();
					ax.add(function);
					s = newExternDeclaration(link, ax);
				}
				if (tpl != null) // it's a function template
				{
					function.templateParameters().addAll(tpl);
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
				s.comments = lastComments;
				adjustLastDocComment();
				a.add(s);
			} else {
				Initializer init;

				init = null;
				if (token.value == TOKassign) {
					nextToken();
					init = parseInitializer();
				}
				
				if (variableDeclaration == null) {
					variableDeclaration = newVariableDeclaration(t);
					variableDeclaration.startPosition = nextVarStart;
				} else {
					addDelcaration = false;
				}
				
				variableDeclaration.fragments().add(newVariableDeclarationFragment(ident, init));
				
				if (addDelcaration) {
					a.add(variableDeclaration);
				}
				switch (token.value) {
				case TOKsemicolon:
					variableDeclaration.length = token.ptr + token.len - variableDeclaration.startPosition;
					nextToken();
					variableDeclaration.comments = lastComments;
					adjustLastDocComment();
					break;

				case TOKcomma:
					variableDeclaration.length = prevToken.ptr + prevToken.len - variableDeclaration.startPosition;
					nextToken();
					nextVarStart = token.ptr;
					variableDeclaration.comments = lastComments;
					adjustLastDocComment();
					continue;

				default:
					problem("Semicolon expected to close declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, variableDeclaration.startPosition, prevToken.ptr + prevToken.len - variableDeclaration.startPosition);
					break;
				}
			}
			break;
		}
		
		return a;
	}
	
	private void parseContracts(FunctionDeclaration f) {
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
				if (f.getPrecondition() != null || f.getPostcondition() != null) {
					problem("Missing body { ... } after in or out", IProblem.SEVERITY_ERROR, IProblem.MISSING_BODY_AFTER_IN_OR_OUT,
							f.getName().getStartPosition(), f.getName().getLength());
				}
				f.setBody(parseStatement(PSsemi));
				f.length = prevToken.ptr + prevToken.len - f.startPosition;
				break;

			case TOKbody:
				nextToken();
				f.setBody(parseStatement(PScurly));
				f.length = prevToken.ptr + prevToken.len - f.startPosition;
				break;

			case TOKsemicolon:
				if (f.getPrecondition() != null || f.getPostcondition() != null) {
					problem("Missing body { ... } after in or out", IProblem.SEVERITY_ERROR, IProblem.MISSING_BODY_AFTER_IN_OR_OUT,
							f.getName().getStartPosition(), f.getName().getLength());
				}
				f.length = token.ptr + token.len - f.startPosition;
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
				if (f.getPrecondition() != null) {
					problem("Redundant 'in' statement", IProblem.SEVERITY_ERROR, IProblem.REDUNDANT_IN_STATEMENT,
							token.ptr, token.len);
				}
				nextToken();
				
				f.setPrecondition(parseStatement(PScurly | PSscope));
				repeat = true;
				break;

			case TOKout:
				// parse: out (identifier) { statement }
				
				if (f.getPostcondition() != null) {
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
					f.setPostconditionVariableName(newSimpleNameForCurrentToken());
					nextToken();
					check(TOKrparen);
				}
				
				f.setPostcondition(parseStatement(PScurly | PSscope));
				repeat = true;
				break;

			default:
				problem("Semicolon expected following function declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
				break;
			}
		}
		linkage = linksave;
	}
	
	public Initializer parseInitializer() {
		StructInitializer is;
		ArrayInitializer ia;
		ExpressionInitializer ie;
		Expression e;
		Identifier id;
		Initializer value;
		int comma;
		Token t;
		int braces = 0;
		
		Token saveToken = new Token(token);

		switch (token.value) {
		case TOKlcurly:
		    /* Scan ahead to see if it is a struct initializer or
		     * a function literal.
		     * If it contains a ';', it is a function literal.
		     * Treat { } as a struct initializer.
		     */
		    braces = 1;
			for (t = peek(token); true; t = peek(t)) {
				switch (t.value) {
				case TOKsemicolon:
				case TOKreturn:
					// goto Lexpression;
					e = parseAssignExp();
					ie = newExpressionInitializer(e);
					return ie;

				case TOKlcurly:
					braces++;
					continue;

				case TOKrcurly:
					if (--braces == 0) {
						break;
					}
					continue;

				default:
					continue;
				}
				break;
			}
			
			is = newStructInitializer();
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
						id = new Identifier(token);
						nextToken();
						nextToken(); // skip over ':'
					} else {
						id = null;
					}
					value = parseInitializer();
					
					is.fragments().add(newStructInitializerFragment(id, value));
					
					comma = 1;
					continue;

				case TOKcomma:
					nextToken();
					comma = 2;
					continue;

				case TOKrcurly: // allow trailing comma's
					is.startPosition = saveToken.ptr;
					is.length = token.ptr + token.len - is.startPosition;
					nextToken();
					break;

				default:
					value = parseInitializer();
				
					is.fragments().add(newStructInitializerFragment(null, value));
					
					comma = 1;
					continue;
				}
				break;
			}
			
			return is;

		case TOKlbracket:
			ia = newArrayInitializer();
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
						value = newExpressionInitializer(e);
						e = null;
					}
					ia.fragments().add(newArrayInitializerFragment(e, value));
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
					
					ia.fragments().add(newArrayInitializerFragment(null, value));
					comma = 1;
					continue;

				case TOKcomma:
					nextToken();
					comma = 2;
					continue;

				case TOKrbracket: // allow trailing comma's
					ia.startPosition = saveToken.ptr;
					ia.length = token.ptr + token.len - ia.startPosition;
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
				return newVoidInitializerForToken(prevToken);
			}
			// goto Lexpression;

		default:
			// Lexpression:
			e = parseAssignExp();
			ie = newExpressionInitializer(e);
			return ie;
		}
	}
	
	@SuppressWarnings("unchecked") 
	public Statement parseStatement(int flags) {
		Statement s = null;
		Token t;
		Statement ifbody;
	    Statement elsebody;

		if ((flags & PScurly) != 0 && token.value != TOKlcurly) {
			problem("Statement expected to be { }",
					IProblem.SEVERITY_ERROR,
					IProblem.STATEMENT_EXPECTED_TO_BE_CURLIES, token.ptr, token.len);
		}
		
		Token saveToken = new Token(token);

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
				
				s = newLabelStatement(ident, body);
				break;
			}
			// fallthrough to TOKdot
		case TOKdot:
		case TOKtypeof:
			if (isDeclaration(token, 2, TOKreserved, null)) {
				// goto Ldeclaration;
				Statement[] ps = { s };
				parseStatement_Ldeclaration(ps, flags);
				s = ps[0];
				break;
			} else {
				// goto Lexp;
				Expression exp = parseExpression();
				check(TOKsemicolon, "statement");
				s = newExpressionStatement(exp);
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
			Expression exp = parseExpression();
			check(TOKsemicolon, "statement");
			s = newExpressionStatement(exp);
			s.setSourceRange(exp.getStartPosition(), prevToken.ptr + prevToken.len - exp.getStartPosition());
			break;
		}

		case TOKstatic: { // Look ahead to see if it's static assert() or
							// static if()
			Token t2;
			
			t2 = peek(token);
			if (t2.value == TOKassert) {
				nextToken();
				
				s = newStaticAssertStatement(parseStaticAssert());
				break;
			}
			if (t2.value == TOKif) {
				nextToken();
				
				StaticIfCondition staticIfCondition = parseStaticIfCondition();
				
				// goto Lcondition
				ifbody = parseStatement(0 /*PSsemi*/);
				elsebody = null;			
				if (token.value == TOKelse) {
					nextToken();
					elsebody = parseStatement(0 /*PSsemi*/);
				}
				
				s = newStaticIfStatement(staticIfCondition, ifbody, elsebody);
				break;
			}
			// goto Ldeclaration;
			Statement[] ps = { s };
			parseStatement_Ldeclaration(ps, flags);
			s = ps[0];
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
			Statement[] ps = { s };
			parseStatement_Ldeclaration(ps, flags);
			s = ps[0];
			break;
		}

		case TOKstruct:
		case TOKunion:
		case TOKclass:
		case TOKinterface: {
			Declaration d;

			d = parseAggregate();
			s = newDeclarationStatement(d);
			break;
		}

		case TOKenum: {
			Declaration d;

			d = parseEnum();
			s = newDeclarationStatement(d);
			break;
		}

		case TOKmixin: {
			Declaration d;
			
			d = parseMixin();
			s = newDeclarationStatement(d);
			break;
		}

		case TOKlcurly: {
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
			
			s = newBlock(statements);
			/*
			if ((flags & (PSscope | PScurlyscope)) != 0) {
				s = new ScopeStatement(s);
			}
			*/
			nextToken();
			break;
		}

		case TOKwhile: {
			Expression condition2;
			Statement body;
			
			nextToken();
			check(TOKlparen);
			condition2 = parseExpression();
			check(TOKrparen);
			
			body = parseStatement(PSscope);
			
			s = newWhileStatement(condition2, body);
			break;
		}

		case TOKsemicolon:
			if ((flags & PSsemi) == 0) {
				problem("Use '{ }' for an empty statement, not a ';'", IProblem.SEVERITY_ERROR, IProblem.USE_BRACES_FOR_AN_EMPTY_STATEMENT, token.ptr, token.len);
			}
			nextToken();
			
			s = newExpressionStatement(null);
			break;

		case TOKdo: {
			Statement body;
			Expression condition2;
			
			nextToken();
			
			body = parseStatement(PSscope);
			
			check(TOKwhile);
			check(TOKlparen);
			condition2 = parseExpression();
			check(TOKrparen);
			s = newDoStatement(body, condition2);
			break;
		}

		case TOKfor: {
			Statement init;
			Expression condition2;
			Expression increment;
			Statement body;
			
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
			body = parseStatement(PSscope);
			s = newForStatement(init, condition2, increment, body);
			/*
			if (init != null) {
				s = new ScopeStatement(s);
				s.startPosition = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.startPosition;
			}
			*/
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
			
			nextToken();
			check(TOKlparen);

			arguments = new ArrayList();

			while (true) {
				Type tb;
				Identifier ai = null;
				Type at;
				Argument.PassageMode inout;
				Argument a;
				
				Token argumentStart = new Token(token);

				inout = Argument.PassageMode.IN;
				if (token.value == TOKinout) {
					inout = Argument.PassageMode.INOUT;
					nextToken();
				}
				if (token.value == TOKidentifier) {
					Token t2 = peek(token);
					if (t2.value == TOKcomma || t2.value == TOKsemicolon) {
						ai = new Identifier(token);
						at = null; // infer argument type
						nextToken();
						// goto Larg;
						a = newArgument(inout, at, ai, null);
						a.setSourceRange(argumentStart.ptr, prevToken.ptr + prevToken.len - argumentStart.ptr);
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
					problem("No identifier for declarator", IProblem.SEVERITY_ERROR, IProblem.NO_IDENTIFIER_FOR_DECLARATOR, at.startPosition, at.length);
				}
				// Larg:
				a = newArgument(inout, at, ai, null);
				a.setSourceRange(argumentStart.ptr, prevToken.ptr + prevToken.len - argumentStart.ptr);
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
			
			s = newForeachStatement(op, arguments, aggr, body);
			break;
		}

		case TOKif: {
			Argument arg = null;
			Expression condition2;
			Statement ifbody2;
			Statement elsebody2;
			
			nextToken();
			check(TOKlparen);

			if (token.value == TOKauto) {
				Token autoToken = new Token(token);
				
				nextToken();
				if (token.value == TOKidentifier) {
					Token t2 = peek(token);
					if (t2.value == TOKassign) {
						arg = newArgument(Argument.PassageMode.IN, null, token.ident, null);
						arg.setSourceRange(autoToken.ptr, token.ptr + token.len - autoToken.ptr);
						
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

					arg = newArgument(Argument.PassageMode.IN, at, ai, null);
					arg.setSourceRange(argToken.ptr, prevToken.ptr + prevToken.len - argToken.ptr);
					
					check(TOKassign);					
				}

				// Check for " ident;"
				else if (token.value == TOKidentifier) {
					Token t2 = peek(token);
					if (t2.value == TOKcomma || t2.value == TOKsemicolon) {
						arg = newArgument(Argument.PassageMode.IN, null, token.ident, null);
						arg.setSourceRange(argToken.ptr, token.ptr + token.len - argToken.ptr);
						
						nextToken();
						nextToken();
						
						// if (!global.params.useDeprecated)
						problem("if (v; e) is deprecated, use if (auto v = e)", IProblem.SEVERITY_ERROR, IProblem.DEPRECATED_IF_AUTO, argToken.ptr, token.ptr + token.len - argToken.ptr);
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
			s = newIfStatement(arg, condition2, ifbody2, elsebody2);
			break;
		}

		case TOKscope:
		    if (peek(token).value != TOKlparen) {
		    	// goto Ldeclaration
		    	// scope used as storage class
				Statement[] ps = { s };
				parseStatement_Ldeclaration(ps, flags);
				s = ps[0];
				break;
		    }
			
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
				
				s = newScopeStatement(t2, st);
				break;
			}

		case TOKon_scope_exit:
		case TOKon_scope_failure:
		case TOKon_scope_success: {
			
			TOK t2 = token.value;
			
			// if (!global.params.useDeprecated)
			problem(token.toString() + " is deprecated, use scope", IProblem.SEVERITY_ERROR, IProblem.ON_SCOPE_DEPRECATED, token.ptr, token.len);
			nextToken();
			Statement st = parseStatement(PScurlyscope);
			
			s = newScopeStatement(t2, st);
			break;
		}

		case TOKdebug:
			nextToken();
			
			DebugCondition condition = parseDebugCondition();
			
			// goto Lcondition
			ifbody = parseStatement(0 /*PSsemi*/);
			elsebody = null;			
			if (token.value == TOKelse) {
				nextToken();
				elsebody = parseStatement(0 /*PSsemi*/);
			}
			
			s = newDebugStatement(condition, ifbody, elsebody);
			break;

		case TOKversion:
			nextToken();
			
			VersionCondition versionCondition = parseVersionCondition();
			
			// goto Lcondition
			ifbody = parseStatement(0 /*PSsemi*/);
			elsebody = null;			
			if (token.value == TOKelse) {
				nextToken();
				elsebody = parseStatement(0 /*PSsemi*/);
			}
			
			s = newVersionStatement(versionCondition, ifbody, elsebody);
			break;

		case TOKiftype:
			IftypeCondition iftypeCondition = parseIftypeCondition();
			
			// goto Lcondition
			ifbody = parseStatement(0 /*PSsemi*/);
			elsebody = null;			
			if (token.value == TOKelse) {
				nextToken();
				elsebody = parseStatement(0 /*PSsemi*/);
			}
			
			s = newIftypeStatement(iftypeCondition, ifbody, elsebody);
			break;

		case TOKpragma: {
			Identifier ident;
			List<Expression> args = null;
			Statement body;
			
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
			} else {
				body = parseStatement(PSsemi);
			}
			
			s = newPragmaStatement(ident, args, body);
			break;
		}

		case TOKswitch: {
			Expression condition2;
			Statement body;

			nextToken();
			check(TOKlparen);
			condition2 = parseExpression();
			check(TOKrparen);
			body = parseStatement(PSscope);
			
			s = newSwitchStatement(condition2, body);
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
			
			s = newBlock(statements);
			
			/*
			s = new ScopeStatement(s);
			*/

			// Keep cases in order by building the case statements backwards
			for (int i = cases.size(); i != 0; i--) {
				exp = (Expression) cases.get(i - 1);
				s = newCaseStatement(exp, s);
			}
			break;
		}

		case TOKdefault: {
			List<Statement> statements;
			
			nextToken();
			check(TOKcolon);

			statements = new ArrayList<Statement>();
			while (token.value != TOKcase && token.value != TOKdefault
					&& token.value != TOKrcurly) {
				statements.add(parseStatement(PSsemi | PScurlyscope));
			}
			
			s = newBlock(statements);
			s.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			
			/*
			s = new ScopeStatement(s);
			s.startPosition = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.startPosition;
			*/
			
			s = newDefaultStatement(s);
			break;
		}

		case TOKreturn: {
			Expression exp;

			nextToken();
			if (token.value == TOKsemicolon) {
				exp = null;
			} else {
				exp = parseExpression();
			}
			check(TOKsemicolon, "return statement");
			
			s = newReturnStatement(exp);
			break;
		}

		case TOKbreak: {
			Identifier ident;

			nextToken();
			if (token.value == TOKidentifier) {
				ident = new Identifier(token);
				nextToken();
			} else
				ident = null;
			check(TOKsemicolon, "break statement");
			
			s = newBreakStatement(ident);
			break;
		}

		case TOKcontinue: {
			Identifier ident;

			nextToken();
			if (token.value == TOKidentifier) {
				ident = new Identifier(token);
				nextToken();
			} else {
				ident = null;
			}
			
			check(TOKsemicolon, "continue statement");
			
			s = newContinueStatement(ident);
			break;
		}

		case TOKgoto: {
			Identifier ident;
			
			nextToken();
			if (token.value == TOKdefault) {
				nextToken();
				s = newGotoDefaultStatement();
			} else if (token.value == TOKcase) {
				Expression exp = null;

				nextToken();
				if (token.value != TOKsemicolon) {
					exp = parseExpression();
				}
				
				s = newGotoCaseStatement(exp);
			} else {
				if (token.value != TOKidentifier) {
					problem("Identifier expected following goto", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					ident = null;
				} else {
					ident = new Identifier(token);
					nextToken();
				}
				
				s = newGotoStatement(ident);
			}
			check(TOKsemicolon, "goto statement");
			break;
		}

		case TOKsynchronized: {
			Expression exp;
			Statement body;
			
			nextToken();
			if (token.value == TOKlparen) {
				nextToken();
				exp = parseExpression();
				check(TOKrparen);
			} else
				exp = null;
			body = parseStatement(PSscope);
			
			s = newSynchronizedStatement(exp, body);
			break;
		}

		case TOKwith: {
			Expression exp;
			Statement body;
			
			nextToken();
			check(TOKlparen);
			exp = parseExpression();
			check(TOKrparen);
			
			body = parseStatement(PSscope);
			
			s = newWithStatement(exp, body);
			break;
		}

		case TOKtry: {
			Statement body;
			List catches = null;
			Statement finalbody = null;
			
			nextToken();
			body = parseStatement(PSscope);
			while (token.value == TOKcatch) {
				Statement handler;
				CatchClause c;
				Type t2;
				Identifier id;
				
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
				
				c = newCatchClause(t2, id, handler);
				c.setSourceRange(firstToken.ptr, prevToken.ptr + prevToken.len - firstToken.ptr);
				
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
				s = newTryStatement(body, catches, finalbody);
			}
			break;
		}

		case TOKthrow: {
			Expression exp;
			
			nextToken();
			exp = parseExpression();
			check(TOKsemicolon, "throw statement");
			
			s = newThrowStatement(exp);
			break;
		}

		case TOKvolatile:
			nextToken();
			s = parseStatement(PSsemi | PScurlyscope);
			s = newVolatileStatement(s);
			break;

		case TOKasm: {
			List<Statement> statements;
			Identifier label;
			Token toklist;
			Token[] ptoklist = new Token[1];
			
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
						s = new AsmStatement(ast, toklist);
						
						toklist = null;
						ptoklist[0] = toklist;
						if (label == null) {
							s = newLabelStatement(label, s);
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
			
			s = newBlock(statements);;
			
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
		
		if (s != null) {
			s.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
		} else {
			// Changed from DMD... TODO remove it!
			s = newBlock(null);
		}
		
		return s;
	}
	
	private void parseStatement_Ldeclaration(Statement[] s, int flags) {
		List a;

		a = parseDeclarations();
		if (a.size() > 1) {
			List<Statement> as = new ArrayList<Statement>(a.size());
			for (int i = 0; i < a.size(); i++) {
				Declaration d = (Declaration) a.get(i);
				s[0] = newDeclarationStatement(d);
				as.add(s[0]);
			}
			
			s[0] = newBlock(as);
		} else if (a.size() == 1) {
			Declaration d = (Declaration) a.get(0);
			s[0] = newDeclarationStatement(d);
		} else {
			assert (false);
			s[0] = null;
		}
		/*
		if ((flags & PSscope) != 0) {
			s[0] = new ScopeStatement(s[0]);
		}
		*/
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
				    // [ expression .. expression ]
				    if (!isExpression(pointer2_t)) {
				    	t = pointer2_t[0];
				    	return false;
				    }
				    t = pointer2_t[0];
				    
				    if (t.value == TOKslice)
				    {	
				    	t = peek(t);
				    	pointer2_t[0] = t;
						if (!isExpression(pointer2_t)) {
							t = pointer2_t[0];
						    return false;
					    }
						t = pointer2_t[0];
					}
				    
				    if (t.value != TOKrbracket) {
				    	return false;
				    }
				    
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

	    switch (token.value)
	    {
		case TOKidentifier:
		    id = new Identifier(token);
		    nextToken();
		    if (token.value == TOKnot && peek(token).value == TOKlparen)
		    {	// identifier!(template-argument-list)
		    	
		    	nextToken();
		    	List<ASTNode> arguments = parseTemplateArgumentList();
				TemplateType templateType = newTemplateType(id, arguments);
				templateType.setSourceRange(id.startPosition, prevToken.ptr + prevToken.len - id.startPosition);
				e = newTypeExpression(templateType);
		    }
		    else {
		    	e = newSimpleNameForIdentifier(id);
		    }
		    break;

		case TOKdollar:
		    if (inBrackets == 0) {
		    	problem("'$' is valid only inside [] of index or slice", IProblem.SEVERITY_ERROR, IProblem.DOLLAR_INVALID_OUTSIDE_BRACKETS, token.ptr, token.len);
		    }
		    e = newDollarLiteral();
		    nextToken();
		    break;

		case TOKdot:
		    // Signal global scope '.' operator with "" identifier
			e = null;
		    // e = new SimpleName(new Identifier(Id.empty, TOKidentifier));
		    break;

		case TOKthis:
		    e = newThisLiteralForCurrentToken();
		    nextToken();
		    break;

		case TOKsuper:
		    e = newSuperLiteral();
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

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
		    e = newNumberLiteralForCurrentToken();
		    nextToken();
		    break;

		case TOKnull:
		    e = newNullLiteralForCurrentToken();
		    nextToken();
		    break;

		case TOKtrue:
		case TOKfalse:
			e = newBooleanLiteralForCurrentToken();
		    nextToken();
		    break;

		case TOKcharv:
		case TOKwcharv:
		case TOKdcharv:
			e = newCharacterLiteralForCurrentToken();
		    nextToken();
		    break;

		case TOKstring: {
			
			int start = token.ptr;
			StringsExpression stringsExpression = newStringsExpression();
			StringLiteral stringLiteral = newStringLiteralForCurrentToken();
			stringsExpression.stringLiterals().add(stringLiteral);
			
			int postfix;
			boolean moreThanOne = false;

			// cat adjacent strings
			postfix = token.postfix;
			while (true) {
				nextToken();
				if (token.value == TOKstring) {
					moreThanOne = true;
					if (token.postfix != 0) {
						if (token.postfix != postfix) {
							problem("Mismatched string literal postfixes '" + (char) postfix + "' and '" + (char) token.postfix + "'",
									IProblem.SEVERITY_ERROR,
									IProblem.MISMATCHED_STRING_LITERAL_POSTFIXES,
									stringLiteral.getStartPosition(), token.ptr + token.len - stringLiteral.getStartPosition());
						}							
						postfix = token.postfix;
					}

					if (token.string != null) {
						stringLiteral = newStringLiteralForCurrentToken();
						stringsExpression.stringLiterals().add(stringLiteral);
					}
				} else
					break;
			}
			
			if (moreThanOne) {
				stringsExpression.setSourceRange(start, prevToken.ptr + prevToken.len - start);			
				e = stringsExpression;
			} else {
				e = stringLiteral;
			}
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
			t = newPrimitiveTypeFromCurrentToken();
			nextToken();
			// L1:
			    check(TOKdot, t.toString());
			    if (token.value != TOKidentifier)
			    {
			    	problem("Identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
			    	// goto Lerr;
		    		// Anything for e, as long as it's not NULL
			    	e = newNumberLiteralForCurrentToken();
		    		nextToken();
		    		break;
			    }
			    e = newTypeDotIdentifierExpression(t, token);
			    nextToken();
			    break;

		case TOKtypeof:
		{   
			Expression exp;

		    nextToken();
		    check(TOKlparen);
		    exp = parseExpression();
		    check(TOKrparen);
		    
			t = newTypeofType(exp);
			
		    if (token.value == TOKdot) {
		    	// goto L1;
		    	check(TOKdot, t.toString());
			    if (token.value != TOKidentifier)
			    {   
			    	problem("Identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					// goto Lerr;
			    	// Anything for e, as long as it's not NULL
			    	e = newNumberLiteralForCurrentToken();
			    	nextToken();
			    	break;
			    }
			    e = newTypeDotIdentifierExpression(t, token);
			    nextToken();
			    break;
		    }
		    	
		    e = newTypeExpression(t);
		    break;
		}

		case TOKtypeid:
		{   Type t2;

		    nextToken();
		    check(TOKlparen, "typeid");
		    t2 = parseBasicType();
		    t2 = parseDeclarator(t2, null);	// ( type )
		    check(TOKrparen);
		    e = newTypeidExpression(t2);
		    break;
		}

		case TOKis:
		{
			Type targ = null;
			Identifier ident = null;
			Type tspec = null;
			TOK tok = TOKreserved;
			Token token2 = new Token();
			token2.value = TOKreserved;

			nextToken();
			if (token.value == TOKlparen) {
				nextToken();
				targ = parseBasicType();

				Identifier[] pointer2_ident = { ident };
				targ = parseDeclarator(targ, pointer2_ident);
				ident = pointer2_ident[0];

				if (token.value == TOKcolon || token.value == TOKequal) {
					tok = token.value;
					nextToken();
					if (tok == TOKequal
							&& (token.value == TOKtypedef
									|| token.value == TOKstruct
									|| token.value == TOKunion
									|| token.value == TOKclass
									|| token.value == TOK.TOKsuper
									|| token.value == TOKenum
									|| token.value == TOKinterface
									|| token.value == TOKfunction
									|| token.value == TOKdelegate || token.value == TOK.TOKreturn)) {
						token2 = new Token(token);
						nextToken();
					} else {
						tspec = parseBasicType();
						tspec = parseDeclarator(tspec, null);
					}
				}
				check(TOKrparen);
			} else {
				problem(
						"(type identifier : specialization) expected following is",
						IProblem.SEVERITY_ERROR,
						IProblem.INVALID_IFTYPE_SYNTAX, token.ptr, token.len);
				// goto Lerr;
				// Anything for e, as long as it's not NULL
				e = newNumberLiteralForCurrentToken();
				nextToken();
				break;
			}
			
			if (token2.value == TOK.TOKreserved) {
				e = newIsTypeExpression(ident, targ, tspec, tok);
			} else {
				e = newIsTypeSpecializationExpression(ident, targ, tok, token2.value);
			}
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
			
			e = newAssertExpression(e, msg);
			e.setSourceRange(start, end - start);
			break;
		}

		case TOKlparen:
		    if (peekPastParen(token).value == TOKlcurly) { // (arguments) {
															// statements... }
				save = TOKdelegate;
				// goto case_delegate;
				{
				Expression[] pe = { e };
				parsePrimaryExp_case_delegate(pe, Syntax.EMPTY);
				e = pe[0];
				}
				break;
			}
			// ( expression )
			int start = token.ptr;
			nextToken();
			e = parseExpression();

			int end = token.ptr + token.len;
			check(TOKrparen);

			e = newParenthesizedExpression(e);
			e.startPosition = start;
			e.length = end - start;
			break;

		case TOKlbracket:
		{   List<Expression> elements = parseArguments();

		    e = newArrayLiteral(elements);
		    break;
		}
		
		case TOKlcurly:
		    // { statements... }
			save = TOKdelegate;
			// goto case_delegate;
			{
			Expression[] pe = { e };
			parsePrimaryExp_case_delegate(pe, Syntax.EMPTY);
			e = pe[0];
			}
			break;

		case TOKfunction:
		case TOKdelegate:
		    save = token.value;
			nextToken();
			// case_delegate:
			{
				Expression[] pe = { e };
				parsePrimaryExp_case_delegate(pe, save == TOKfunction ? Syntax.FUNCTION : Syntax.DELEGATE);
				e = pe[0];
				break;
			}

		default:
			problem("Expression expected", IProblem.SEVERITY_ERROR, IProblem.EXPRESSION_EXPECTED, token.ptr, token.len);
		// Lerr:
		    // Anything for e, as long as it's not NULL
			e = newNumberLiteralForCurrentToken();
		    nextToken();
		    break;
	    }
	    return parsePostExp(e);
	}

	@SuppressWarnings("unchecked")
	private Expression parsePostExp(Expression e) {
		while (true) {
			switch (token.value) {
			case TOKdot:
				nextToken();
				if (token.value == TOKidentifier) {
					Identifier id = new Identifier(token);

					nextToken();
					if (token.value == TOKnot && peek(token).value == TOKlparen) { // identifier!(template-argument-list)
						nextToken();
						
						List<ASTNode> arguments = parseTemplateArgumentList();
						TemplateType templateType = newTemplateType(id, arguments);
						templateType.setSourceRange(id.startPosition, prevToken.ptr + prevToken.len - id.startPosition);
						
						e = newDotTemplateTypeExpression(e, templateType);
					} else {
						e = newDotIdentifierExpression(e, id);
					}
					continue;
				} else if (token.value == TOKnew) {
					e = parseNewExp(e);
					continue;
				} else {
					problem("Identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				}
				break;

			case TOKplusplus:
				e = newPostfixExpression(e, PostfixExpression.Operator.INCREMENT);
				break;

			case TOKminusminus:
				e = newPostfixExpression(e, PostfixExpression.Operator.DECREMENT);
				break;

			case TOKlparen:
				e = newCallExpression(e, parseArguments());
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
					e = newSliceExpression(e, null, null);
					nextToken();
				} else {
					index = parseAssignExp();
					if (token.value == TOKslice) { // array[lwr .. upr]
						nextToken();
						upr = parseAssignExp();
						e = newSliceExpression(e, index, upr);
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
						
						e = newArrayAccess(e, arguments);
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

		Token saveToken = new Token(token);

		switch (token.value) {
		case TOKand:
			nextToken();
			e = parseUnaryExp();
			e = newPrefixExpression(e, PrefixExpression.Operator.ADDRESS);
			break;

		case TOKplusplus:
			nextToken();
			e = parseUnaryExp();
			e = newPrefixExpression(e, PrefixExpression.Operator.INCREMENT);
			break;

		case TOKminusminus:
			nextToken();
			e = parseUnaryExp();
			e = newPrefixExpression(e, PrefixExpression.Operator.DECREMENT);
			break;

		case TOKmul:
			nextToken();
			e = parseUnaryExp();
			e = newPrefixExpression(e, PrefixExpression.Operator.POINTER);
			break;

		case TOKmin:
			nextToken();
			e = parseUnaryExp();
			e = newPrefixExpression(e, PrefixExpression.Operator.NEGATIVE);
			break;

		case TOKadd:
			nextToken();
			e = parseUnaryExp();
			e = newPrefixExpression(e, PrefixExpression.Operator.POSITIVE);
			break;

		case TOKnot:
			nextToken();
			e = parseUnaryExp();
			e = newPrefixExpression(e, PrefixExpression.Operator.NOT);
			break;

		case TOKtilde:
			nextToken();
			e = parseUnaryExp();
			e = newPrefixExpression(e, PrefixExpression.Operator.INVERT);
			break;

		case TOKdelete:
			nextToken();
			e = parseUnaryExp();
			e = newDeleteExpression(e);
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
			e = newCastExpression(e, t);
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
						e = newTypeDotIdentifierExpression(t, token);
						nextToken();
					} else {
						e = parseUnaryExp();
						e = newCastExpression(e, t);
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

		e.startPosition = saveToken.ptr;
		e.length = prevToken.ptr + prevToken.len - e.startPosition;

		return e;
	}

	private void parsePrimaryExp_case_delegate(Expression[] e, Syntax syntax) {
		List<Argument> arguments;
		int varargs = 0;
		FunctionDeclaration fd;
		Type t2;

		if (token.value == TOKlcurly) {
			t2 = null;
			arguments = new ArrayList<Argument>();
		} else {
			if (token.value == TOKlparen)
				t2 = null;
			else {
				t2 = parseBasicType();
				t2 = parseBasicType2(t2); // function return type
			}

			int[] pointer2_varargs = { varargs };
			arguments = parseParameters(pointer2_varargs);
			varargs = pointer2_varargs[0];

		}
		
		t2 = new DmdTypeFunction(ast, arguments, t2, varargs != 0, linkage);
		
		// fd dosen't make it to the AST, that's why it's not created via a newXXX...
		fd = new FunctionDeclaration(ast);
		fd.arguments().addAll(arguments);
		parseContracts(fd);
		
		e[0] = newFunctionLiteralDeclarationExpression(syntax, fd);
	}
	
	private Expression parseMulExp()
	{   Expression e;
	    Expression e2;

	    e = parseUnaryExp();
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKmul: nextToken(); e2 = parseUnaryExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.TIMES); continue;
		    case TOKdiv:   nextToken(); e2 = parseUnaryExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.DIVIDE); continue;
		    case TOKmod:  nextToken(); e2 = parseUnaryExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.REMAINDER); continue;

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

	    e = parseMulExp();
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKadd:    nextToken(); e2 = parseMulExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.PLUS); continue;
		    case TOKmin:    nextToken(); e2 = parseMulExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.MINUS); continue;
		    case TOKtilde:  nextToken(); e2 = parseMulExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.CONCATENATE); continue;

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

	    e = parseAddExp();
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKshl:  nextToken(); e2 = parseAddExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.LEFT_SHIFT);  continue;
		    case TOKshr:  nextToken(); e2 = parseAddExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.RIGHT_SHIFT_SIGNED);  continue;
		    case TOKushr: nextToken(); e2 = parseAddExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED); continue;

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

	    e = parseShiftExp();
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKlt: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.LESS); continue;
		    case TOKle: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.LESS_EQUALS); continue;
		    case TOKgt: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.GREATER); continue;
		    case TOKge: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.GREATER_EQUALS); continue;
		    case TOKunord: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.NOT_LESS_GREATER_EQUALS); continue;
		    case TOKlg: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.LESS_GREATER); continue;
		    case TOKleg: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.LESS_GREATER_EQUALS); continue;
		    case TOKule: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.NOT_GREATER); continue;
		    case TOKul: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.NOT_GREATER_EQUALS); continue;
		    case TOKuge: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.NOT_LESS); continue;
		    case TOKug: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.NOT_LESS_EQUALS); continue;
		    case TOKue: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.NOT_LESS_GREATER); continue;
		    case TOKin: nextToken(); e2 = parseShiftExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.IN); continue;

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

	    e = parseRelExp();
	    while (true)
	    {	TOK value = token.value;

		switch (value)
		{
		    case TOKequal:
		    	nextToken();
				e2 = parseRelExp();
				e = newInfixExpression(e,e2,InfixExpression.Operator.EQUALS);
				continue;

		    case TOKnotequal:
				nextToken();
				e2 = parseRelExp();
				e = newInfixExpression(e,e2,InfixExpression.Operator.NOT_EQUALS);
				continue;

		    case TOKidentity:
			//if (!global.params.useDeprecated)
		    	problem("'===' is no longer legal, use 'is' instead", IProblem.SEVERITY_ERROR,
		    			IProblem.THREE_EQUALS_IS_NO_LONGER_LEGAL, token.ptr, token.len);
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.IDENTITY);
			continue;

		    case TOKnotidentity:
			//if (!global.params.useDeprecated)
		    	problem("'!==' is no longer legal, use 'is' instead", IProblem.SEVERITY_ERROR,
		    			IProblem.NOT_TWO_EQUALS_IS_NO_LONGER_LEGAL, token.ptr, token.len);
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.NOT_IDENTITY);
			continue;

		    case TOKis:
			value = TOKidentity;
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.IS);
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
			e = newInfixExpression(e,e2,InfixExpression.Operator.NOT_IS);
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
	
	private Expression parseAndExp() {
		Expression e;
		Expression e2;

		e = parseEqualExp();
		while (token.value == TOKand) {
			nextToken();
			e2 = parseEqualExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.AND);
		}
		return e;
	}
	
	private Expression parseXorExp() {
		Expression e;
		Expression e2;

		e = parseAndExp();
		while (token.value == TOKxor) {
			nextToken();
			e2 = parseAndExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.XOR);
		}
		return e;
	}
	
	private Expression parseOrExp() {
		Expression e;
		Expression e2;

		e = parseXorExp();
		while (token.value == TOKor) {
			nextToken();
			e2 = parseXorExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.OR);
		}
		return e;
	}
	
	private Expression parseAndAndExp() {
		Expression e;
		Expression e2;

		e = parseOrExp();
		while (token.value == TOKandand) {
			nextToken();
			e2 = parseOrExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.AND_AND);
		}
		return e;
	}
	
	private Expression parseOrOrExp() {
		Expression e;
		Expression e2;

		e = parseAndAndExp();
		while (token.value == TOKoror) {
			nextToken();
			e2 = parseAndAndExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.OR_OR);
		}
		return e;
	}

	private Expression parseCondExp() {
		Expression e;
		Expression e1;
		Expression e2;

		e = parseOrOrExp();
		if (token.value == TOKquestion) {
			nextToken();
			e1 = parseExpression();
			check(TOKcolon);
			e2 = parseCondExp();
			e = newConditionalExpression(e, e1, e2);
		}
		return e;
	}
	
	private Expression parseAssignExp()
	{   Expression e;
	    Expression e2;

	    e = parseCondExp();
	    while (true)
	    {
		switch (token.value)
		{
		case TOKassign:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.ASSIGN); continue;
		case TOKaddass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.PLUS_ASSIGN); continue;
		case TOKminass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.MINUS_ASSIGN); continue;
		case TOKmulass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.TIMES_ASSIGN); continue;
		case TOKdivass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.DIVIDE_ASSIGN); continue;
		case TOKmodass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.REMAINDER_ASSIGN); continue;
		case TOKandass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.AND_ASSIGN); continue;
		case TOKorass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.OR_ASSIGN); continue;
		case TOKxorass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.XOR_ASSIGN); continue;
		case TOKshlass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.LEFT_SHIFT_ASSIGN); continue;
		case TOKshrass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.RIGHT_SHIFT_SIGNED_ASSIGN); continue;
		case TOKushrass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN); continue;
		case TOKcatass:  nextToken(); e2 = parseAssignExp(); e = newInfixExpression(e,e2,InfixExpression.Operator.CONCATENATE_ASSIGN); continue;
	    default:
			break;
		}
		break;
	    }
	    return e;
	}
	
	public Expression parseExpression() {
		Expression e;
		Expression e2;

		e = parseAssignExp();
		while (token.value == TOKcomma) {
			nextToken();
			e2 = parseAssignExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.COMMA);
		}
		return e;
	}
	
	/***************************************************************************
	 * Collect argument list. Assume current token is '('.
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
		List<Expression> newargs = null;
		List<Expression> arguments = null;
		Expression e;

		nextToken();
		if (token.value == TOKlparen) {
			newargs = parseArguments();
		}

		// An anonymous nested class starts with "class"
		if (token.value == TOKclass) {
			nextToken();
			if (token.value == TOKlparen)
				arguments = parseArguments();

			List<BaseClass> baseClasses = null;
			if (token.value != TOKlcurly)
				baseClasses = parseBaseClasses();

			Identifier id = null;
			
			AggregateDeclaration cd = newAggregateDeclaration(TOKclass, id, baseClasses);

			if (token.value != TOKlcurly) {
				problem("{ members } expected for anonymous class", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
				cd.declarations().clear();
			} else {
				nextToken();
				List decl = parseDeclDefs(false, new ArrayList<Modifier>());
				if (token.value != TOKrcurly) {
					problem("class member expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
				}
				nextToken();
				cd.declarations().addAll(decl);
			}

			e = newNewAnonymousClassExpression(ast, thisexp, newargs, cd, arguments);

			return e;
		}

		// #if LTORARRAYDECL
		t = parseBasicType();
		t = parseBasicType2(t);
		if (TypeAdapter.getAdapter(t).getTY() == Taarray) {
			Type index = (Type) ((AssociativeArrayType) t).getKeyType();
			
			Expression e2 = TypeAdapter.getAdapter(index).toExpression();
			if (e2 != null) {
				arguments = new ArrayList<Expression>();
				arguments.add(e2);
				
				t = newDynamicArrayType((Type) TypeAdapter.getAdapter(t).getNext());
			} else {
				problem("Need size of rightmost array", IProblem.SEVERITY_ERROR, IProblem.NEED_SIZE_OF_RIGHTMOST_ARRAY, index.startPosition, index.length);
				return newNullLiteralForCurrentToken();
			}
		} else if (TypeAdapter.getAdapter(t).getTY() == Tsarray) {
			StaticArrayType tsa = (StaticArrayType) t;
			Expression e2 = tsa.getSize();

			arguments = new ArrayList<Expression>();
			arguments.add(e2);
			
			t = newDynamicArrayType((Type) TypeAdapter.getAdapter(t).getNext());
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
		e = newNewExpression(ast, thisexp, newargs, t, arguments);
		return e;
	}

	private void addComment(ASTNode s, String blockComment) {
		addComment(s, blockComment, -1);
	}

	private void addComment(ASTNode s, String blockComment, int blockCommentStart) {
		// TODO MARS s.addComment(combineComments(blockComment, token.lineComment), blockComment == null ? - 1 : blockCommentStart);
	}
	
	public PrimitiveType newPrimitiveTypeFromCurrentToken() {
		PrimitiveType type = new PrimitiveType(ast);
		type.setSourceRange(token.ptr, token.len);
		
		switch(token.value) {
			case TOKvoid:	 type.setPrimitiveTypeCode(PrimitiveType.Code.VOID); break;
			case TOKint8:	 type.setPrimitiveTypeCode(PrimitiveType.Code.BYTE); break;
			case TOKuns8:	 type.setPrimitiveTypeCode(PrimitiveType.Code.UBYTE); break;
			case TOKint16:	 type.setPrimitiveTypeCode(PrimitiveType.Code.SHORT); break;
			case TOKuns16:	 type.setPrimitiveTypeCode(PrimitiveType.Code.USHORT); break;
			case TOKint32:	 type.setPrimitiveTypeCode(PrimitiveType.Code.INT); break;
			case TOKuns32:	 type.setPrimitiveTypeCode(PrimitiveType.Code.UINT); break;
			case TOKint64:	 type.setPrimitiveTypeCode(PrimitiveType.Code.LONG); break;
			case TOKuns64:	 type.setPrimitiveTypeCode(PrimitiveType.Code.ULONG); break;
			case TOKfloat32: type.setPrimitiveTypeCode(PrimitiveType.Code.FLOAT); break;
			case TOKfloat64: type.setPrimitiveTypeCode(PrimitiveType.Code.DOUBLE); break;
			case TOKfloat80: type.setPrimitiveTypeCode(PrimitiveType.Code.REAL); break;
			case TOKimaginary32: type.setPrimitiveTypeCode(PrimitiveType.Code.IFLOAT); break;
			case TOKimaginary64: type.setPrimitiveTypeCode(PrimitiveType.Code.IDOUBLE); break;
			case TOKimaginary80: type.setPrimitiveTypeCode(PrimitiveType.Code.IREAL); break;
			case TOKcomplex32: type.setPrimitiveTypeCode(PrimitiveType.Code.COMPLEX32); break;
			case TOKcomplex64: type.setPrimitiveTypeCode(PrimitiveType.Code.COMPLEX64); break;
			case TOKcomplex80: type.setPrimitiveTypeCode(PrimitiveType.Code.COMPLEX80); break;
			case TOKbit:	 type.setPrimitiveTypeCode(PrimitiveType.Code.BIT); break;
			case TOKbool:	 type.setPrimitiveTypeCode(PrimitiveType.Code.BOOL); break;
			case TOKchar:	 type.setPrimitiveTypeCode(PrimitiveType.Code.CHAR); break;
			case TOKwchar:	 type.setPrimitiveTypeCode(PrimitiveType.Code.WCHAR); break;
			case TOKdchar:	 type.setPrimitiveTypeCode(PrimitiveType.Code.DCHAR); break;
			default:
				throw new IllegalStateException("Can't happen");
		}
		
		return type;
	}
	
	private PostfixExpression newPostfixExpression(Expression expression, PostfixExpression.Operator operator) {
		PostfixExpression postfixExpression = new PostfixExpression(ast);
		postfixExpression.setExpression(expression);
		postfixExpression.setOperator(operator);
		return postfixExpression;
	}
	
	private PrefixExpression newPrefixExpression(Expression expression, PrefixExpression.Operator operator) {
		PrefixExpression prefixExpression = new PrefixExpression(ast);
		prefixExpression.setExpression(expression);
		prefixExpression.setOperator(operator);
		return prefixExpression;
	}
	
	private SimpleName newSimpleNameForCurrentToken() {
		SimpleName simpleName = new SimpleName(ast);
		simpleName.setIdentifier(token.ident.string);
		simpleName.setSourceRange(token.ptr, token.len);
		return simpleName;
	}
	
	private SimpleName newSimpleNameForToken(Token token) {
		SimpleName simpleName = new SimpleName(ast);
		simpleName.setIdentifier(token.ident.string);
		simpleName.setSourceRange(token.ptr, token.len);
		return simpleName;
	}
	
	public SimpleName newSimpleNameForIdentifier(Identifier id) {
		if (id == null) return null;
		
		SimpleName simpleName = new SimpleName(ast);
		simpleName.setIdentifier(id.string);
		simpleName.setSourceRange(id.startPosition, id.length);
		return simpleName;
	}
	
	public static SimpleName newSimpleNameForIdentifierWithAST(Identifier id, AST ast) {
		if (id == null) return null;
		
		SimpleName simpleName = new SimpleName(ast);
		simpleName.setIdentifier(id.string);
		simpleName.setSourceRange(id.startPosition, id.length);
		return simpleName;
	}
	
	private QualifiedName newQualifiedNameFromCurrentToken(Name name) {
		QualifiedName qualifiedName = new QualifiedName(ast);
		qualifiedName.setQualifier(name);
		qualifiedName.setName(newSimpleNameForCurrentToken());
		qualifiedName.setSourceRange(name.getStartPosition(), token.ptr + token.len - name.getStartPosition());
		return qualifiedName;
	}
	
	private Modifier newModifierFromCurrentToken() {
		Modifier modifier = new Modifier(ast);
		modifier.setModifierKeyword(ModifierKeyword.toKeyword(token.toString()));
		modifier.setSourceRange(token.ptr, token.len);
		return modifier;
	}
	
	private Modifier newModifierFromTokenAndKeyword(Token token, Modifier.ModifierKeyword keyword) {
		Modifier modifier = new Modifier(ast);
		modifier.setModifierKeyword(keyword);
		modifier.setSourceRange(token.ptr, token.len);
		return modifier;
	}
	
	private NumberLiteral newNumberLiteralForCurrentToken() {
		NumberLiteral number = new NumberLiteral(ast);
		if (token.string != null) {
			number.setToken(token.string);
		}
		number.setSourceRange(token.ptr, token.len);
		return number;
	}
	
	private CharacterLiteral newCharacterLiteralForCurrentToken() {
		CharacterLiteral number = new CharacterLiteral(ast);
		if (token.string != null) {
			number.setEscapedValue(token.string);
		}
		number.setSourceRange(token.ptr, token.len);
		return number;
	}
	
	private StringLiteral newStringLiteralForCurrentToken() {
		StringLiteral string = new StringLiteral(ast);
		string.setEscapedValue(token.string);
		string.setSourceRange(token.ptr, token.len);
		return string;
	}
	
	private ExpressionInitializer newExpressionInitializer(Expression expression) {
		ExpressionInitializer initializer = new ExpressionInitializer(ast);
		if (expression != null) {
			initializer.setExpression(expression);
			initializer.setSourceRange(expression.getStartPosition(), expression.getLength());
		}
		return initializer;
	}
	
	private EnumMember newEnumMember(SimpleName name, Expression value) {
		EnumMember enumMember = new EnumMember(ast);
		enumMember.setName(name);
		if (value == null) {
			enumMember.setSourceRange(name.getStartPosition(), name.length);
		} else {
			enumMember.setValue(value);
			enumMember.setSourceRange(name.getStartPosition(), value.getStartPosition() + value.getLength() - name.getStartPosition());
		}
		return enumMember;
	}
	
	private VolatileStatement newVolatileStatement(Statement body) {
		VolatileStatement volatileStatement = new VolatileStatement(ast);
		volatileStatement.setBody(body);
		return volatileStatement;
	}
	
	private IfStatement newIfStatement(Argument argument, Expression expression, Statement thenBody, Statement elseBody) {
		IfStatement ifStatement = new IfStatement(ast);
		ifStatement.setArgument(argument);
		ifStatement.setExpression(expression);
		ifStatement.setThenBody(thenBody);
		ifStatement.setElseBody(elseBody);
		return ifStatement;
	}
	
	private ForStatement newForStatement(Statement initializer, Expression condition, Expression increment, Statement body) {
		ForStatement forStatement = new ForStatement(ast);
		forStatement.setInitializer(initializer);
		forStatement.setCondition(condition);
		forStatement.setIncrement(increment);
		forStatement.setBody(body);
		return forStatement;
	}
	
	private DoStatement newDoStatement(Statement body, Expression expression) {
		DoStatement doStatement = new DoStatement(ast);
		doStatement.setBody(body);
		doStatement.setExpression(expression);
		return doStatement;
	}
	
	private ArrayLiteral newArrayLiteral(List<Expression> arguments) {
		ArrayLiteral arrayLiteral = new ArrayLiteral(ast);
		if (arguments != null) {
			arrayLiteral.arguments().addAll(arguments);
		}
		return arrayLiteral;
	}
	
	private CallExpression newCallExpression(Expression expression, List<Expression> arguments) {
		CallExpression callExpression = new CallExpression(ast);
		callExpression.setExpression(expression);
		if (arguments != null) {
			callExpression.arguments().addAll(arguments);
		}
		return callExpression;
	}
	
	private CastExpression newCastExpression(Expression expression, Type type) {
		CastExpression castExpression = new CastExpression(ast);
		castExpression.setExpression(expression);
		castExpression.setType(type);
		return castExpression;
	}

	public ConditionalExpression newConditionalExpression(Expression expression, Expression thenExpression, Expression elseExpression) {
		ConditionalExpression conditionalExpression = new ConditionalExpression(ast);
		conditionalExpression.setExpression(expression);
		conditionalExpression.setThenExpression(thenExpression);
		conditionalExpression.setElseExpression(elseExpression);
		conditionalExpression.setSourceRange(expression.getStartPosition(), elseExpression.getStartPosition() + elseExpression.getLength() - expression.getStartPosition());
		return conditionalExpression;
	}
	
	private InfixExpression newInfixExpression(Expression e1, Expression e2, InfixExpression.Operator operator) {
		InfixExpression infixExpression = new InfixExpression(ast);
		infixExpression.setLeftOperand(e1);
		infixExpression.setOperator(operator);
		infixExpression.setRightOperand(e2);
		infixExpression.setSourceRange(e1.getStartPosition(), e2.getStartPosition() + e2.getLength() - e1.getStartPosition());
		return infixExpression;
	}
	
	private SliceExpression newSliceExpression(Expression expression, Expression fromExpression, Expression toExpression) {
		SliceExpression sliceExpression = new SliceExpression(ast);
		sliceExpression.setExpression(expression);
		if (fromExpression != null) {
			sliceExpression.setFromExpression(fromExpression);
		}
		if (toExpression != null) {
			sliceExpression.setToExpression(toExpression);
		}
		return sliceExpression;
	}
	
	private ParenthesizedExpression newParenthesizedExpression(Expression expression) {
		ParenthesizedExpression parenthesizedExpression = new ParenthesizedExpression(ast);
		parenthesizedExpression.setExpression(expression);
		return parenthesizedExpression;
	}
	
	private ModuleDeclaration newModuleDeclaration(Name name) {
		ModuleDeclaration moduleDeclaration = new ModuleDeclaration(ast);
		moduleDeclaration.setName(name);
		return moduleDeclaration;
	}
	
	private StaticIfDeclaration newStaticIfDeclaration(StaticIfCondition condition, List<Declaration> thenDeclarations, List<Declaration> elseDeclarations) {
		StaticIfDeclaration staticIf = new StaticIfDeclaration(ast);
		if (condition.exp != null) {
			staticIf.setExpression(condition.exp);
		}
		if (thenDeclarations != null) {
			staticIf.thenDeclarations().addAll(thenDeclarations);
		}
		if (elseDeclarations != null) {
			staticIf.elseDeclarations().addAll(elseDeclarations);
		}
		return staticIf;
	}
	
	private AggregateDeclaration newAggregateDeclaration(TOK tok, Identifier id, List<BaseClass> baseClasses) {
		AggregateDeclaration classDeclaration = new AggregateDeclaration(ast);
		switch(tok) {
		case TOKclass:
			classDeclaration.setKind(AggregateDeclaration.Kind.CLASS);
			break;
		case TOKinterface:
			classDeclaration.setKind(AggregateDeclaration.Kind.INTERFACE);
			break;
		case TOKstruct:
			classDeclaration.setKind(AggregateDeclaration.Kind.STRUCT);
			break;
		case TOKunion:
			classDeclaration.setKind(AggregateDeclaration.Kind.UNION);
			break;
		default:
			throw new RuntimeException("Can't happen");
		}
		if (id != null) {
			classDeclaration.setName(newSimpleNameForIdentifier(id));
		}
		if (baseClasses != null) {
			classDeclaration.baseClasses().addAll(baseClasses);
		}
		return classDeclaration;
	}
	
	private AliasDeclaration newAliasDeclaration(Type t) {
		AliasDeclaration aliasDeclaration = new AliasDeclaration(ast);
		aliasDeclaration.setType(t);
		return aliasDeclaration;
	}
	
	private AliasDeclarationFragment newAliasDeclarationFragment(Identifier id) {
		AliasDeclarationFragment fragment = new AliasDeclarationFragment(ast);
		if (id != null) {
			fragment.setName(newSimpleNameForIdentifier(id));
			fragment.setSourceRange(id.startPosition, id.length);
		}
		return fragment;
	}
	
	private InvariantDeclaration newInvariantDeclaration() {
		return new InvariantDeclaration(ast);
	}
	
	private UnitTestDeclaration newUnitTestDeclaration() {
		UnitTestDeclaration unitTest = new UnitTestDeclaration(ast);
		return unitTest;
	}
	
	private BaseClass newBaseClass(Type type, Modifier modifier) {
		BaseClass baseClass = new BaseClass(ast);
		baseClass.setType(type);
		baseClass.setModifier(modifier);
		return baseClass;
	}
	
	private AliasTemplateParameter newAliasTemplateParamete(Identifier id, Type specificType, Type defaultType) {
		AliasTemplateParameter parameter = new AliasTemplateParameter(ast);
		parameter.setName(newSimpleNameForIdentifier(id));
		parameter.setSpecificType(specificType);
		parameter.setDefaultType(defaultType);
		return parameter;
	}
	
	private AlignDeclaration newAlignDeclaration(int align, List<Declaration> declarations) {
		AlignDeclaration alignDeclaration = new AlignDeclaration(ast);
		alignDeclaration.setAlign(align);
		if (declarations != null) {
			alignDeclaration.declarations().addAll(declarations);
		}
		return alignDeclaration;
	}
	
	private Argument newArgument(Argument.PassageMode passageMode, Type type, Identifier id, Expression expression) {
		Argument argument = new Argument(ast);
		argument.setPassageMode(passageMode);
		argument.setType(type);
		argument.setName(newSimpleNameForIdentifier(id));
		argument.setDefaultValue(expression);
		return argument;
	}
	
	private ArrayAccess newArrayAccess(Expression expression, List<Expression> arguments) {
		ArrayAccess arrayAccess = new ArrayAccess(ast);
		arrayAccess.setArray(expression);
		arrayAccess.indexes().addAll(arguments);
		return arrayAccess;
	}
	
	private ArrayInitializer newArrayInitializer() {
		return new ArrayInitializer(ast);
	}
	
	private ArrayInitializerFragment newArrayInitializerFragment(Expression expression, Initializer initializer) {
		ArrayInitializerFragment fragment = new ArrayInitializerFragment(ast);
		fragment.setInitializer(initializer);
		if (expression == null) {
			fragment.setSourceRange(initializer.getStartPosition(), initializer.getLength());
		} else {
			fragment.setExpression(expression);
			fragment.setSourceRange(expression.getStartPosition(), initializer.getStartPosition() + initializer.getLength() - expression.getStartPosition());
		}
		return fragment;
	}
	
	private AssertExpression newAssertExpression(Expression expression, Expression message) {
		AssertExpression assertExpression = new AssertExpression(ast);
		if (expression != null) {
			assertExpression.setExpression(expression);
		}
		assertExpression.setMessage(message);
		return assertExpression;
	}
	
	private AssociativeArrayType newAssociativeArrayType(Type componentType, Type keyType) {
		AssociativeArrayType associativeArray = new AssociativeArrayType(ast);
	    associativeArray.setComponentType(componentType);
	    associativeArray.setKeyType(keyType);
	    associativeArray.setSourceRange(componentType.getStartPosition(), token.ptr + token.len - componentType.getStartPosition());
	    return associativeArray;
	}
	
	private Block newBlock(List<Statement> statements) {
		Block block = new Block(ast);
		if (statements != null) {
			block.statements().addAll(statements);
		}
		return block;
	}
	
	private BooleanLiteral newBooleanLiteralForCurrentToken() {
		BooleanLiteral booleanLiteral = new BooleanLiteral(ast);
		booleanLiteral.setBooleanValue(token.value == TOK.TOKtrue);
		booleanLiteral.setSourceRange(token.ptr, token.len);
		return booleanLiteral;
	}
	
	private BreakStatement newBreakStatement(Identifier label) {
		BreakStatement breakStatement = new BreakStatement(ast);
		breakStatement.setLabel(newSimpleNameForIdentifier(label));
		return breakStatement;
	}
	
	private CaseStatement newCaseStatement(Expression expression, Statement body) {
		CaseStatement caseStatement = new CaseStatement(ast);
		caseStatement.setExpression(expression);
		caseStatement.setBody(body);
		return caseStatement;
	}
	
	private CatchClause newCatchClause(Type type, Identifier id, Statement body) {
		CatchClause catchClause = new CatchClause(ast);
		catchClause.setType(type);
		catchClause.setName(newSimpleNameForIdentifier(id));
		catchClause.setBody(body);
		return catchClause;
	}
	
	private ContinueStatement newContinueStatement(Identifier label) {
		ContinueStatement continueStatement = new ContinueStatement(ast);
		continueStatement.setLabel(newSimpleNameForIdentifier(label));
		return continueStatement;
	}
	
	private DebugAssignment newDebugAssignmentForCurrentToken() {
		DebugAssignment debugAssignment = new DebugAssignment(ast);
		debugAssignment.setVersion(newVersionForCurrentToken());
		return debugAssignment;
	}
	
	private Version newVersionForCurrentToken() {
		Version version;
		
		if (token.value == TOKint32v) {
			version = new Version(ast);
			version.setValue(String.valueOf(token.numberValue));
			version.setSourceRange(token.ptr, token.len);
		} else if (token.value == TOKidentifier) {
			version = new Version(ast);
			version.setValue(token.ident.string);
			version.setSourceRange(token.ptr, token.len);
		} else {
			throw new RuntimeException("Can't happen");
		}
		
		return version;
	}
	
	private Version newVersion(Identifier id) {
		Version version = new Version(ast);
		version.setValue(id.string);
		version.setSourceRange(id.startPosition, id.length);
		return version;
	}
	
	private DebugDeclaration newDebugDeclaration(DebugCondition debugCondition, List<Declaration> thenDeclarations, List<Declaration> elseDeclarations) {
		DebugDeclaration debugDeclaration = new DebugDeclaration(ast);
		if (debugCondition.id != null) {
			debugDeclaration.setVersion(newVersion(debugCondition.id));
		}		
		if (thenDeclarations != null) {
			debugDeclaration.thenDeclarations().addAll(thenDeclarations);
		}
		if (elseDeclarations != null) {
			debugDeclaration.elseDeclarations().addAll(elseDeclarations);
		}		
		return debugDeclaration;
	}
	
	private DebugStatement newDebugStatement(DebugCondition debugCondition, Statement thenBody, Statement elseBody) {
		DebugStatement debugStatement = new DebugStatement(ast);
		if (debugCondition.id != null) {
			debugStatement.setVersion(newVersion(debugCondition.id));
		}
		if (thenBody != null) {
			debugStatement.setThenBody(thenBody);
		}
		if (elseBody != null) {
			debugStatement.setElseBody(elseBody);
		}
		return debugStatement;
	}
	
	private DeclarationStatement newDeclarationStatement(Declaration declaration) {
		DeclarationStatement declarationStatement = new DeclarationStatement(ast);
		declarationStatement.setDeclaration(declaration);
		declarationStatement.setSourceRange(declaration.getStartPosition(), declaration.getLength());
		return declarationStatement;
	}
	
	private DefaultStatement newDefaultStatement(Statement body) {
		DefaultStatement defaultStatement = new DefaultStatement(ast);
		defaultStatement.setBody(body);
		return defaultStatement;
	}
	
	private DelegateType newDelegateType(TOK save, DmdTypeFunction typeFunction, int varargs) {
		DelegateType delegateType = new DelegateType(ast);
		delegateType.setReturnType(typeFunction.getReturnType());
		delegateType.setFunctionPointer(save == TOKfunction);
		delegateType.arguments().addAll(typeFunction.arguments);
		delegateType.setVariadic(varargs != 0);
		return delegateType;
	}
	
	private DeleteExpression newDeleteExpression(Expression expression) {
		DeleteExpression deleteExpression = new DeleteExpression(ast);
		deleteExpression.setExpression(expression);
		return deleteExpression;
	}
	
	private DollarLiteral newDollarLiteral() {
		return new DollarLiteral(ast);
	}
	
	private DotIdentifierExpression newDotIdentifierExpression(Expression e, Identifier id) {
		DotIdentifierExpression die = new DotIdentifierExpression(ast);
		die.setExpression(e);
		die.setName(newSimpleNameForIdentifier(id));
		if (e == null) {
			die.setSourceRange(prevToken.ptr, token.ptr + token.len - prevToken.ptr);
		} else {
			die.setSourceRange(e.getStartPosition(), id.startPosition + id.length - e.getStartPosition());
		}
		return die;
	}
	
	private DynamicArrayType newDynamicArrayType(Type componentType) {
		DynamicArrayType dynamicArrayType = new DynamicArrayType(ast);
		dynamicArrayType.setComponentType(componentType);
		return dynamicArrayType;
	}
	
	private EnumDeclaration newEnumDeclaration(Identifier id, Type type) {
		EnumDeclaration enumDeclaration = new EnumDeclaration(ast);
		enumDeclaration.setName(newSimpleNameForIdentifier(id));
		enumDeclaration.setBaseType(type);
		return enumDeclaration;
	}
	
	private ExpressionStatement newExpressionStatement(Expression expression) {
		ExpressionStatement expressionStatement = new ExpressionStatement(ast);
		if (expression != null) {
			expressionStatement.setExpression(expression);
		}
		return expressionStatement;
	}
	
	private ExternDeclaration newExternDeclaration(LINK link, List<Declaration> declarations) {
		ExternDeclaration externDeclaration = new ExternDeclaration(ast);
		externDeclaration.setLinkage(link.getLinkage());
		if (declarations != null) {
			externDeclaration.declarations().addAll(declarations);
		}
		return externDeclaration;
	}
	
	private ForeachStatement newForeachStatement(TOK op, List<Argument> arguments, Expression aggr, Statement body) {
		ForeachStatement foreachStatement = new ForeachStatement(ast);
		foreachStatement.setReverse(op == TOK.TOKforeach_reverse);
		foreachStatement.setExpression(aggr);
		foreachStatement.arguments().addAll(arguments);
		foreachStatement.setBody(body);
		return foreachStatement;
	}
	
	private FunctionDeclaration newFunctionDeclaration(FunctionDeclaration.Kind kind, Type returnType, SimpleName name, List<Argument> arguments, int varargs) {
		FunctionDeclaration function = new FunctionDeclaration(ast);
		function.setKind(kind);
		if (returnType != null) {
			function.setReturnType(returnType);
		}
		if (name != null) {
			function.setName(name);
		}
		if (arguments != null) {
			function.arguments().addAll(arguments);
		}
		function.setVariadic(varargs != 0);
		return function;
	}
	
	private FunctionLiteralDeclarationExpression newFunctionLiteralDeclarationExpression(FunctionLiteralDeclarationExpression.Syntax syntax, FunctionDeclaration fd) {
		FunctionLiteralDeclarationExpression expression = new FunctionLiteralDeclarationExpression(ast);
		expression.setSyntax(syntax); 
		expression.arguments().addAll(fd.arguments());
		expression.setVariadic(fd.isVariadic());
		expression.setPrecondition(fd.getPrecondition());
		expression.setPostcondition(fd.getPostcondition());
		expression.setPostconditionVariableName(fd.getPostconditionVariableName());
		expression.setBody(fd.getBody());
		return expression;
	}
	
	private GotoCaseStatement newGotoCaseStatement(Expression expression) {
		GotoCaseStatement gotoCaseStatement = new GotoCaseStatement(ast);
		gotoCaseStatement.setLabel(expression);
		return gotoCaseStatement;
	}
	
	private GotoDefaultStatement newGotoDefaultStatement() {
		return new GotoDefaultStatement(ast);
	}
	
	private GotoStatement newGotoStatement(Identifier ident) {
		GotoStatement gotoStatement = new GotoStatement(ast);
		if (ident != null) {
			gotoStatement.setLabel(newSimpleNameForIdentifier(ident));
		}
		return gotoStatement;
	}
	
	private IftypeDeclaration newIftypeDeclaration(IftypeCondition iftypeCondition, List<Declaration> thenDeclarations, List<Declaration> elseDeclarations) {
		IftypeDeclaration iftypeDeclaration = new IftypeDeclaration(ast);
		if (iftypeCondition != null) {
			iftypeDeclaration.setKind(iftypeCondition.getKind());
			iftypeDeclaration.setName(newSimpleNameForIdentifier(iftypeCondition.ident));
			iftypeDeclaration.setTestType(iftypeCondition.targ);
			iftypeDeclaration.setMatchingType(iftypeCondition.tspec);
		}
		if (thenDeclarations != null) {
			iftypeDeclaration.thenDeclarations().addAll(thenDeclarations);
		}
		if (elseDeclarations != null) {
			iftypeDeclaration.elseDeclarations().addAll(elseDeclarations);
		}
		return iftypeDeclaration;
	}
	
	private IftypeStatement newIftypeStatement(IftypeCondition iftypeCondition, Statement thenBody, Statement elseBody) {
		IftypeStatement iftypeStatement = new IftypeStatement(ast);
		if (iftypeCondition != null) {
			iftypeStatement.setKind(iftypeCondition.getKind());
			iftypeStatement.setName(newSimpleNameForIdentifier(iftypeCondition.ident));
			iftypeStatement.setTestType(iftypeCondition.targ);
			iftypeStatement.setMatchingType(iftypeCondition.tspec);
		}
		if (thenBody != null) {
			iftypeStatement.setThenBody(thenBody);
		}
		if (elseBody != null) {
			iftypeStatement.setElseBody(elseBody);
		}
		return iftypeStatement;
	}
	
	private Import newImport(Name name, SimpleName alias) {
		Import anImport = new Import(ast);
		anImport.setName(name);
		anImport.setAlias(alias);
		return anImport;
	}
	
	private ImportDeclaration newImportDeclaration() {
		return new ImportDeclaration(ast);
	}
	
	private IsTypeExpression newIsTypeExpression(Identifier ident, Type targ, Type tspec, TOK tok) {
		IsTypeExpression isTypeExpression = new IsTypeExpression(ast);
		isTypeExpression.setName(newSimpleNameForIdentifier(ident));
		isTypeExpression.setType(targ);
		isTypeExpression.setSpecialization(tspec);
		isTypeExpression.setSameComparison(tok == TOK.TOKequal);
		return isTypeExpression;
	}
	
	private IsTypeSpecializationExpression newIsTypeSpecializationExpression(Identifier ident, Type targ, TOK tok, TOK tok2) {
		IsTypeSpecializationExpression exp = new IsTypeSpecializationExpression(ast);
		exp.setName(newSimpleNameForIdentifier(ident));
		exp.setType(targ);
		TypeSpecialization specialization = null;
		switch(tok2) {
		case TOKtypedef: specialization = TypeSpecialization.TYPEDEF; break;
		case TOKstruct: specialization = TypeSpecialization.STRUCT; break;
		case TOKunion: specialization = TypeSpecialization.UNION; break;
		case TOKclass: specialization = TypeSpecialization.CLASS; break;
		case TOKsuper: specialization = TypeSpecialization.SUPER; break;
		case TOKenum: specialization = TypeSpecialization.ENUM; break;
		case TOKinterface: specialization = TypeSpecialization.INTERFACE; break;
		case TOKfunction: specialization = TypeSpecialization.FUNCTION; break;
		case TOKdelegate: specialization = TypeSpecialization.DELEGATE; break;
		case TOKreturn: specialization = TypeSpecialization.RETURN; break;
		default: throw new RuntimeException("Can't happen");
		}
		exp.setSpecialization(specialization);
		exp.setSameComparison(tok == TOK.TOKequal);
		return exp;
	}
	
	private LabelStatement newLabelStatement(Identifier ident, Statement body) {
		LabelStatement labelStatement = new LabelStatement(ast);
		labelStatement.setLabel(newSimpleNameForIdentifier(ident));
		labelStatement.setBody(body);
		labelStatement.setSourceRange(ident.startPosition, body.startPosition + body.length - ident.startPosition);
		return labelStatement;
	}
	
	private ModifierDeclaration newModifierDeclaration(Modifier modifier, ModifierDeclaration.Syntax syntax, List<Declaration> declarations) {
		ModifierDeclaration modifierDeclaration = new ModifierDeclaration(ast);
		if (modifier != null) {
			modifierDeclaration.setModifier(modifier);
		}
		if (syntax != null) {
			modifierDeclaration.setSyntax(syntax);
		}
		if (declarations != null) {
			modifierDeclaration.declarations().addAll(declarations);
		}
		return modifierDeclaration;
	}
	
	private NullLiteral newNullLiteralForCurrentToken() {
		NullLiteral nullLiteral = new NullLiteral(ast);
		nullLiteral.setSourceRange(token.ptr, token.len);
	    return nullLiteral;
	}
	
	private PointerType newPointerType(Type componentType) {
		PointerType pointerType = new PointerType(ast);
		pointerType.setComponentType(componentType);
		return pointerType;
	}
	
	private PragmaDeclaration newPragmaDeclaration(Identifier ident, List<Expression> arguments, List<Declaration> declarations) {
		PragmaDeclaration pragmaDeclaration = new PragmaDeclaration(ast);
		pragmaDeclaration.setName(newSimpleNameForIdentifier(ident));
		if (arguments != null) {
			pragmaDeclaration.arguments().addAll(arguments);
		}
		if (declarations != null) {
			pragmaDeclaration.declarations().addAll(declarations);
		}
		return pragmaDeclaration;
	}
	
	private PragmaStatement newPragmaStatement(Identifier ident, List<Expression> arguments, Statement body) {
		PragmaStatement pragmaStatement = new PragmaStatement(ast);
		pragmaStatement.setName(newSimpleNameForIdentifier(ident));
		if (arguments != null) {
			pragmaStatement.arguments().addAll(arguments);
		}
		pragmaStatement.setBody(body);
		return pragmaStatement;
	}
	
	private ReturnStatement newReturnStatement(Expression expression) {
		ReturnStatement returnStatement = new ReturnStatement(ast);
		returnStatement.setExpression(expression);
		return returnStatement;
	}
	
	private ScopeStatement newScopeStatement(TOK tok, Statement body) {
		ScopeStatement scope = new ScopeStatement(ast);
		scope.setEvent(tok == TOKon_scope_exit ? ScopeStatement.Event.EXIT : (tok == TOKon_scope_failure ? ScopeStatement.Event.FAILURE : ScopeStatement.Event.SUCCESS));
		scope.setBody(body);
		return scope;
	}
	
	private SelectiveImport newSelectiveImport() {
		return new SelectiveImport(ast);
	}
	
	private SimpleName newSimpleName() {
		return new SimpleName(ast);
	}
	
	private SliceType newSliceType(Type type, Expression fromExpression, Expression toExpression) {
		SliceType sliceType = new SliceType(ast);
		sliceType.setComponentType(type);
		sliceType.setFromExpression(fromExpression);
		sliceType.setToExpression(toExpression);
		return sliceType;
	}
	
	private StaticArrayType newStaticArrayType(Type type, Expression size) {
		StaticArrayType staticArrayType = new StaticArrayType(ast);
		staticArrayType.setComponentType(type);
		staticArrayType.setSize(size);
		return staticArrayType;
	}
	
	private StaticAssert newStaticAssert(Expression expression, Expression message) {
		StaticAssert staticAssert = new StaticAssert(ast);
	    staticAssert.setExpression(expression);
	    staticAssert.setMessage(message);
	    return staticAssert;
	}
	
	private StaticAssertStatement newStaticAssertStatement(StaticAssert staticAssert) {
		StaticAssertStatement staticAssertStatement = new StaticAssertStatement(ast);
		staticAssertStatement.setStaticAssert(staticAssert);
		return staticAssertStatement;
	}
	
	private StaticIfStatement newStaticIfStatement(StaticIfCondition staticIfCondition, Statement thenBody, Statement elseBody) {
		StaticIfStatement staticIfStatement = new StaticIfStatement(ast);
		staticIfStatement.setExpression(staticIfCondition.exp);
		staticIfStatement.setThenBody(thenBody);
		staticIfStatement.setElseBody(elseBody);
		return staticIfStatement;
	}
	
	private StringsExpression newStringsExpression() {
		return new StringsExpression(ast);
	}
	
	private StructInitializer newStructInitializer() {
		return new StructInitializer(ast);
	}
	
	private StructInitializerFragment newStructInitializerFragment(Identifier id, Initializer value) {
		StructInitializerFragment fragment = new StructInitializerFragment(ast);
		fragment.setName(newSimpleNameForIdentifier(id));
		fragment.setInitializer(value);
		if (id == null) {
			fragment.setSourceRange(value.getStartPosition(), value.getLength());
		} else {
			fragment.setSourceRange(id.startPosition, value.getStartPosition() + value.getLength() - id.startPosition);
		}
		return fragment;
	}
	
	private SuperLiteral newSuperLiteral() {
		return new SuperLiteral(ast);
	}
	
	private SwitchStatement newSwitchStatement(Expression expression, Statement body) {
		SwitchStatement switchStatement = new SwitchStatement(ast);
		switchStatement.setExpression(expression);
		switchStatement.setBody(body);
		return switchStatement;
	}
	
	private SynchronizedStatement newSynchronizedStatement(Expression expression, Statement body) {
		SynchronizedStatement synchronizedStatement = new SynchronizedStatement(ast);
		synchronizedStatement.setExpression(expression);
		synchronizedStatement.setBody(body);
		return synchronizedStatement;
	}
	
	private TemplateDeclaration newTemplateDeclaration(Identifier id, List<TemplateParameter> templateParameters, List<Declaration> declarations) {
		TemplateDeclaration tempdecl = new TemplateDeclaration(ast);
	    tempdecl.setName(newSimpleNameForIdentifier(id));
	    tempdecl.templateParameters().addAll(templateParameters);
	    tempdecl.declarations().addAll(declarations);
	    return tempdecl;
	}
	
	private ThisLiteral newThisLiteralForCurrentToken() {
		ThisLiteral thisLiteral = new ThisLiteral(ast);
		thisLiteral.setSourceRange(token.ptr, token.len);
		return thisLiteral;
	}
	
	private ThrowStatement newThrowStatement(Expression expression) {
		ThrowStatement throwStatement = new ThrowStatement(ast);
		throwStatement.setExpression(expression);
		return throwStatement;
	}
	
	private TryStatement newTryStatement(Statement body, List<CatchClause> catches, Statement finalbody) {
		TryStatement tryStatement = new TryStatement(ast);
		tryStatement.setBody((Block) body);
		if (catches != null) {
			tryStatement.catchClauses().addAll(catches);
		}
		tryStatement.setFinally((Block) finalbody);
		return tryStatement;
	}
	
	private TupleTemplateParameter newTupleTemplateParameter(Identifier id) {
		TupleTemplateParameter tupleTemplateParameter = new TupleTemplateParameter(ast);
		tupleTemplateParameter.setName(newSimpleNameForIdentifier(id));
		return tupleTemplateParameter;
	}
	
	private TypedefDeclaration newTypedefDeclaration(Type type) {
		TypedefDeclaration typedefDeclaration = new TypedefDeclaration(ast);
		typedefDeclaration.setType(type);
		return typedefDeclaration;
	}
	
	private TypedefDeclarationFragment newTypedefDeclarationFragment(Identifier ident, Initializer init) {
		TypedefDeclarationFragment fragment = new TypedefDeclarationFragment(ast);
		SimpleName name = newSimpleNameForIdentifier(ident);
		fragment.setName(name);
		if (init == null) {
			fragment.setSourceRange(name.getStartPosition(), name.getLength());
		} else {
			fragment.setInitializer(init);
			fragment.setSourceRange(name.getStartPosition(), ident.startPosition + ident.length - name.getStartPosition());
		}
		return fragment;
	}
	
	private TypeofType newTypeofType(Expression expression) {
		TypeofType typeofType = new TypeofType(ast);
		typeofType.setExpression(expression);
		return typeofType;
	}
	
	private TypeTemplateParameter newTypeTemplateParameter(Identifier id, Type specificType, Type defaultType) {
		TypeTemplateParameter typeTemplateParameter = new TypeTemplateParameter(ast);
		typeTemplateParameter.setName(newSimpleNameForIdentifier(id));
		typeTemplateParameter.setSpecificType(specificType);
		typeTemplateParameter.setDefaultType(defaultType);
		return typeTemplateParameter;
	}
	
	private ValueTemplateParameter newValueTemplateParameter(Identifier tp_ident, Type tp_valtype, Expression tp_specvalue, Expression tp_defaultvalue) {
		ValueTemplateParameter valueTemplateParameter = new ValueTemplateParameter(ast);
		valueTemplateParameter.setType(tp_valtype);
		valueTemplateParameter.setName(newSimpleNameForIdentifier(tp_ident));
		valueTemplateParameter.setSpecificValue(tp_specvalue);
		valueTemplateParameter.setDefaultValue(tp_defaultvalue);
		return valueTemplateParameter;
	}
	
	private VariableDeclaration newVariableDeclaration(Type type) {
		VariableDeclaration variableDeclaration = new VariableDeclaration(ast);
		variableDeclaration.setType(type);
		return variableDeclaration;
	}
	
	private VariableDeclarationFragment newVariableDeclarationFragment(Identifier ident, Initializer init) {
		VariableDeclarationFragment fragment = new VariableDeclarationFragment(ast);
		if (ident != null) {
			fragment.setName(newSimpleNameForIdentifier(ident));
		}
		fragment.setInitializer(init);
		return fragment;
	}
	
	private VersionAssignment newVersionAssignmentForCurrentToken() {
		VersionAssignment versionAssignment = new VersionAssignment(ast);
		versionAssignment.setVersion(newVersionForCurrentToken());
		return versionAssignment;
	}
	
	private VersionDeclaration newVersionDeclaration(VersionCondition versionCondition, List<Declaration> thenDeclarations, List<Declaration> elseDeclarations) {
		VersionDeclaration versionDeclaration = new VersionDeclaration(ast);
		if (versionCondition.id != null) {
			versionDeclaration.setVersion(newVersion(versionCondition.id));
		}
		if (thenDeclarations != null) {
			versionDeclaration.thenDeclarations().addAll(thenDeclarations);
		}
		if (elseDeclarations != null) {
			versionDeclaration.elseDeclarations().addAll(elseDeclarations);
		}
		return versionDeclaration;
	}
	
	private VersionStatement newVersionStatement(VersionCondition versionCondition, Statement thenBody, Statement elseBody) {
		VersionStatement versionStatement = new VersionStatement(ast);
		if (versionCondition.id != null) {
			versionStatement.setVersion(newVersion(versionCondition.id));
		}
		versionStatement.setThenBody(thenBody);
		versionStatement.setElseBody(elseBody);
		return versionStatement;
	}
	
	private VoidInitializer newVoidInitializerForToken(Token token) {
		VoidInitializer voidInitializer = new VoidInitializer(ast);
		voidInitializer.setSourceRange(token.ptr, token.len);
		return voidInitializer;
	}
	
	private WhileStatement newWhileStatement(Expression expression, Statement body) {
		WhileStatement whileStatement = new WhileStatement(ast);
		whileStatement.setExpression(expression);
		whileStatement.setBody(body);
		return whileStatement;
	}
	
	private WithStatement newWithStatement(Expression expression, Statement body) {
		WithStatement withStatement = new WithStatement(ast);
		withStatement.setExpression(expression);
		withStatement.setBody(body);
		return withStatement;
	}
	
	private Expression newNewAnonymousClassExpression(AST ast, Expression thisexp, List<Expression> newargs, AggregateDeclaration cd, List<Expression> arguments) {
		NewAnonymousClassExpression expression = new NewAnonymousClassExpression(ast);
		expression.setExpression(thisexp);
		if (newargs != null) {
			expression.newArguments().addAll(newargs);
		}
		if (arguments != null) {
			expression.constructorArguments().addAll(arguments);
		}
		expression.baseClasses().addAll(cd.baseClasses());
		expression.declarations().addAll(cd.declarations());
		return expression;
	}

	private NewExpression newNewExpression(AST ast, Expression thisexp, List<Expression> newargs, Type t, List<Expression> arguments) {
		NewExpression newExpression = new NewExpression(ast);
		newExpression.setExpression(thisexp);
		if (newargs != null) {
			newExpression.newArguments().addAll(newargs);
		}
		newExpression.setType(t);
		if (arguments != null) {
			newExpression.constructorArguments().addAll(arguments);
		}
		return newExpression;
	}
	
	private TypeExpression newTypeExpression(Type type) {
		TypeExpression typeExpression = new TypeExpression(ast);
		typeExpression.setType(type);
		return typeExpression;
	}
	
	private TypeDotIdentifierExpression newTypeDotIdentifierExpression(Type t, Token token) {
		TypeDotIdentifierExpression typeDot = new TypeDotIdentifierExpression(ast);
		typeDot.setType(t);
		typeDot.setName(newSimpleNameForToken(token));
		return typeDot;
	}
	
	private QualifiedType newQualifiedType(Type type, Identifier identifier, int start) {
		return newQualifiedType(type, newSimpleType(identifier), start);
	}
	
	private QualifiedType newQualifiedType(Type qualifier, Type type, int start) {
		QualifiedType qualifiedType = new QualifiedType(ast);
		qualifiedType.setQualifier(qualifier);
		qualifiedType.setType(type);
		qualifiedType.setSourceRange(start, type.getStartPosition() + type.getLength() - start);
		return qualifiedType;
	}
	
	private SimpleType newSimpleType(Identifier id) {
		SimpleType simpleType = new SimpleType(ast);
		if (id != null) {
			simpleType.setName(newSimpleNameForIdentifier(id));
			simpleType.setSourceRange(id.startPosition, id.length);
		}
		return simpleType;
	}
	
	private TemplateType newTemplateType(Identifier identifier, List<ASTNode> arguments) {
		TemplateType templateType = new TemplateType(ast);
		templateType.setName(newSimpleNameForIdentifier(identifier));
		if (templateType != null) {
			templateType.arguments().addAll(arguments);
		}
		return templateType;
	}
	
	private DotTemplateTypeExpression newDotTemplateTypeExpression(Expression e, TemplateType templateType) {
		DotTemplateTypeExpression dot = new DotTemplateTypeExpression(ast);
		dot.setExpression(e);
		dot.setTemplateType(templateType);
		return dot;
	}
	
	private MixinDeclaration newMixinDeclaration(Type type, Identifier id) {
		MixinDeclaration mixin = new MixinDeclaration(ast);
		mixin.setType(type);
		mixin.setName(newSimpleNameForIdentifier(id));
		return mixin;
	}
	
	private TypeidExpression newTypeidExpression(Type type) {
		TypeidExpression typeid = new TypeidExpression(ast);
		typeid.setType(type);
		return typeid;
	}
	
	private List<Comment> getLastDocComments() {
		List<Comment> toReturn = new ArrayList<Comment>();
		for(int i = comments.size() - 1; i >= lastDocCommentRead; i--) {
			Comment comment = comments.get(i);
			if (comment.isDocComment()) {
				toReturn.add(comment);
			} else {
				break;
			}
		}
		return toReturn;
	}
	
}