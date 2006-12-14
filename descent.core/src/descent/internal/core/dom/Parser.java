package descent.internal.core.dom;

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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;
import descent.core.dom.IBaseClass;
import descent.core.dom.IElement;
import descent.internal.core.dom.FunctionLiteralDeclarationExpression.Syntax;
import descent.internal.core.dom.IsTypeSpecializationExpression.TypeSpecialization;

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
			
			Name name = null;
			
			md = new ModuleDeclaration(ast);
			
			nextToken();
			if (token.value != TOKidentifier) {
				problem("Identifier expected following module", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
				// goto Lerr;
				return parseModule_LErr();
			} else {
				
				name = newSimpleNameForCurrentToken();
				
				while (nextToken() == TOKdot) {
					nextToken();
					
					if (token.value != TOKidentifier) {
						problem("Identifier expected following package", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
						return parseModule_LErr();
					}
					
					name = newQualifiedNameForCurrentToken(name);
				}

				md.setName(name);
				md.setSourceRange(start, token.ptr + token.len - start);
				mod.md = md;

				if (token.value != TOKsemicolon) {
					problem("';' expected following module declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
				}
				
				nextToken();
				
				md.comments = moduleDocComments;
				adjustLastDocComment();
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
	private List<Declaration> parseDeclDefs(boolean once) {
		Object[] tempObj;

		ASTNode s;
		List<Declaration> decldefs;
		List<Declaration> a = new ArrayList<Declaration>();
		List<Declaration> aelse;
		PROT prot;
		int stc;
		Condition condition;
		
		Token saveToken;
		boolean[] isSingle = new boolean[1];
		
		// printf("Parser::parseDeclDefs()\n");
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
					
					StaticIfDeclaration staticIf = new StaticIfDeclaration(ast);
					if (((StaticIfCondition) condition).exp != null) {
						staticIf.setExpression(((StaticIfCondition) condition).exp);
					}
					staticIf.thenDeclarations().addAll(a);
					if (aelse != null) {
						staticIf.elseDeclarations().addAll(aelse);
					}
					staticIf.setSourceRange(staticToken.ptr, prevToken.ptr + prevToken.len - staticToken.ptr);
					
					s = staticIf;
					break;
				} else if (token.value == TOKimport) {
					s = parseImport(decldefs, true);
					ImportDeclaration id = (ImportDeclaration) decldefs.get(decldefs.size() -1);
					id.setStatic(true);
					id.length += id.startPosition - staticToken.ptr;
					id.startPosition = staticToken.ptr;
				} else {
					stc = STCstatic;
					// goto Lstc2;
					tempObj = parseDeclDefs_Lstc2(stc, a, isSingle);
					a = (List<Declaration>) tempObj[0];
					stc = ((Integer) tempObj[1]);
					s = (ASTNode) tempObj[2];
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
				stc = STC.fromToken(token.value);
				int mod = STC.getModifiers(stc);
				// goto Lstc;
				saveToken = new Token(token);
				nextToken(); 
				tempObj = parseDeclDefs_Lstc2(stc, a, isSingle);
				a = (List) tempObj[0];
				stc = ((Integer) tempObj[1]);
				s = (ASTNode) tempObj[2];
				
				if (a != null && a.size() == 1) {
					if (isSingle[0]) {
						s = (ASTNode) a.get(0);
						s.modifierFlags |= mod;
					} else {
						for(Declaration elem : a) {
							elem.modifierFlags |= mod;
						}
					}
				}
				s.startPosition = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.startPosition;
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
					s = (ASTNode) tempObj[2];
					break;
				}
				{
					LINK linksave = linkage;
					linkage = parseLinkage();
					a = parseBlock();
					
					ExternDeclaration externDeclaration = new ExternDeclaration(ast);
					externDeclaration.setLinkage(linkage.getLinkage());
					externDeclaration.declarations().addAll(a);
					externDeclaration.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
					s = externDeclaration;
					
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
						s = (ASTNode) a.get(0);
						s.modifierFlags |= protection;
					} else {
						ProtectionDeclaration protectionDeclaration = new ProtectionDeclaration(ast);
						protectionDeclaration.setModifierFlags(prot.getModifiers());
						protectionDeclaration.declarations().addAll(a);
						s = protectionDeclaration;
						
						for(IElement elem : a) {
							((ASTNode) elem).modifierFlags |= protection;
						}
					}
				} else {
					ProtectionDeclaration protectionDeclaration = new ProtectionDeclaration(ast);
					protectionDeclaration.setModifierFlags(prot.getModifiers());
					s = protectionDeclaration;
				}
				s.startPosition = saveToken.ptr;
				s.length = prevToken.ptr + prevToken.len - s.startPosition;
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

				AlignDeclaration alignDeclaration = new AlignDeclaration(ast);
				alignDeclaration.setAlign((int) n);
				alignDeclaration.declarations().addAll(a);
				alignDeclaration.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				s = alignDeclaration;
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
				
				PragmaDeclaration pragmaDeclaration = new PragmaDeclaration(ast);
				pragmaDeclaration.setName(newSimpleNameForIdentifier(ident));
				if (args != null) {
					pragmaDeclaration.arguments().addAll(args);
				}
				if (a != null) {
					pragmaDeclaration.declarations().addAll(a);
				}
				pragmaDeclaration.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				
				s = pragmaDeclaration;
				break;
			}

			case TOKdebug:
				saveToken = new Token(token);
				
				nextToken();
				if (token.value == TOKassign) {
					nextToken();
					if (token.value == TOKidentifier) {
						Version version = new Version(ast);
						version.setValue(token.ident.string);
						version.setSourceRange(token.ptr, token.len);
						
						DebugAssignment da = new DebugAssignment(ast);
						da.setVersion(version);
						
						s = da;
					} else if (token.value == TOKint32v) {
						Version version = new Version(ast);
						version.setValue(String.valueOf(token.numberValue));
						version.setSourceRange(token.ptr, token.len);
						
						DebugAssignment da = new DebugAssignment(ast);
						da.setVersion(version);
						
						s = da;
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

				DebugCondition debugCondition = parseDebugCondition();
				
				DebugDeclaration debugDeclaration = new DebugDeclaration(ast);
				if (debugCondition.id != null) {
					Version version = new Version(ast);
					version.setValue(debugCondition.id.string);
					version.setSourceRange(debugCondition.id.startPosition, debugCondition.id.length);
					
					debugDeclaration.setVersion(version);
				}
				
				debugDeclaration.thenDeclarations().addAll(parseBlock());
				if (token.value == TOKelse) {
					nextToken();
					debugDeclaration.elseDeclarations().addAll(parseBlock());
				}
				
				debugDeclaration.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				
				s = debugDeclaration;
				break;

			case TOKversion:
				saveToken = new Token(token);
				
				nextToken();
				if (token.value == TOKassign) {
					nextToken();
					if (token.value == TOKidentifier) {
						Version version = new Version(ast);
						version.setValue(token.ident.string);
						version.setSourceRange(token.ptr, token.len);
						
						VersionAssignment va = new VersionAssignment(ast);
						va.setVersion(version);
						
						s = va;
					} else if (token.value == TOKint32v) {
						Version version = new Version(ast);
						version.setValue(String.valueOf(token.numberValue));
						version.setSourceRange(token.ptr, token.len);
						
						VersionAssignment da = new VersionAssignment(ast);
						da.setVersion(version);
						
						s = da;
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
				
				VersionDeclaration versionDeclaration = new VersionDeclaration(ast);
				if (versionCondition.id != null) {
					Version version = new Version(ast);
					version.setValue(versionCondition.id.string);
					version.setSourceRange(versionCondition.id.startPosition, versionCondition.id.length);
					
					versionDeclaration.setVersion(version);
				}
				
				versionDeclaration.thenDeclarations().addAll(parseBlock());
				if (token.value == TOKelse) {
					nextToken();
					versionDeclaration.elseDeclarations().addAll(parseBlock());
				}
				
				versionDeclaration.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				
				s = versionDeclaration;
				break;

			case TOKiftype:
				saveToken = new Token(token);
				
				IftypeCondition iftypeCondition = parseIftypeCondition();
				
				IftypeDeclaration iftypeDeclaration = new IftypeDeclaration(ast);
				if (iftypeCondition != null) {
					iftypeDeclaration.setKind(iftypeCondition.getKind());
					iftypeDeclaration.setName(newSimpleNameForIdentifier(iftypeCondition.ident));
					iftypeDeclaration.setTestType(iftypeCondition.targ);
					iftypeDeclaration.setMatchingType(iftypeCondition.tspec);
				}
				
				iftypeDeclaration.thenDeclarations().addAll(parseBlock());
				if (token.value == TOKelse) {
					nextToken();
					iftypeDeclaration.elseDeclarations().addAll(parseBlock());
				}
				
				iftypeDeclaration.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				
				s = iftypeDeclaration;
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
				decldefs.add((Declaration) s);
				s.comments = lastComments;
				adjustLastDocComment();
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
	private Object[] parseDeclDefs_Lstc2(int stc, List<Declaration> a, boolean[] isSingle) {
		boolean repeat = true;
		while(repeat) {
			switch (token.value)
			{
			    case TOKconst:	  stc |= STCconst; nextToken(); break;
			    case TOKfinal:	  stc |= STCfinal; nextToken(); break;
			    case TOKauto:	  stc |= STCauto; nextToken(); break;
			    case TOKscope:	  stc |= STC.STCscope; nextToken(); break;
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
		    VarDeclaration v = new VarDeclaration(ast, null, ident, init);
		    v.storage_class = stc;
		    v.modifierFlags = STC.getModifiers(stc);
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
	
	private List<Declaration> parseBlock() {
		return parseBlock(new boolean[1]);
	}
	
	private List<Declaration> parseBlock(boolean[] isSingle) {
		List<Declaration> a = null;
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
	    
	    StaticAssert staticAssert = new StaticAssert(ast);
	    staticAssert.setExpression(exp);
	    staticAssert.setMessage(msg);
	    return staticAssert;
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
			link = LINKd; // default
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
			c = new DebugCondition(mod, level, id);
		} else {
			c = new DebugCondition(mod, 1, null);
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
		c = new VersionCondition(mod, level, id);
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
		SimpleName name = new SimpleName(ast);
		name.setSourceRange(token.ptr, token.len);
		name.setIdentifier("this");
		
		FunctionDeclaration constructor = new FunctionDeclaration(ast);
		constructor.setSourceRange(token.ptr, 0);
		constructor.setKind(FunctionDeclaration.Kind.CONSTRUCTOR);
		constructor.setName(name);
				
		nextToken();
		int[] varargs = new int[1];
		List<Argument> arguments = parseParameters(varargs);
		
		constructor.arguments().addAll(arguments);
		constructor.setVariadic(varargs[0] != 0);
	    parseContracts(constructor);
	    return constructor;
	}
	
	private FunctionDeclaration parseDtor() {
		Token firstToken = new Token(token);
		nextToken();
		Token secondToken = new Token(token);
		
		check(TOKthis);
	    check(TOKlparen);
	    check(TOKrparen);
		
		SimpleName name = new SimpleName(ast);
		name.setSourceRange(firstToken.ptr, secondToken.ptr + secondToken.len - firstToken.ptr);
		name.setIdentifier("~this");
		
		FunctionDeclaration destructor = new FunctionDeclaration(ast);
		destructor.setSourceRange(firstToken.ptr, 0);
		destructor.setKind(FunctionDeclaration.Kind.DESTRUCTOR);
		destructor.setName(name);
	    parseContracts(destructor);
	    return destructor;
	}
	
	private FunctionDeclaration parseStaticCtor() {
		SimpleName name = new SimpleName(ast);
		name.setSourceRange(token.ptr, token.len);
		name.setIdentifier("this");
		
	    FunctionDeclaration staticConstructor = new FunctionDeclaration(ast);
		staticConstructor.setSourceRange(token.ptr, 0);
		staticConstructor.setKind(FunctionDeclaration.Kind.STATIC_CONSTRUCTOR);
		staticConstructor.setName(name);
		
		nextToken();
	    check(TOKlparen);
	    check(TOKrparen);
		
	    parseContracts(staticConstructor);
	    return staticConstructor;
	}
	
	private FunctionDeclaration parseStaticDtor() {
		Token firstToken = new Token(token);
		nextToken();
		Token secondToken = new Token(token);
		
		check(TOKthis);
	    check(TOKlparen);
	    check(TOKrparen);
	    
	    SimpleName name = new SimpleName(ast);
		name.setSourceRange(firstToken.ptr, secondToken.ptr + secondToken.len - firstToken.ptr);
		name.setIdentifier("~this");
		
		FunctionDeclaration staticDestructor = new FunctionDeclaration(ast);
		staticDestructor.setSourceRange(firstToken.ptr, 0);
		staticDestructor.setKind(FunctionDeclaration.Kind.STATIC_DESTRUCTOR);
		staticDestructor.setName(name);
	    parseContracts(staticDestructor);
	    return staticDestructor;
	}
	
	private InvariantDeclaration parseInvariant() {
		int start = token.ptr;
	    nextToken();

	    InvariantDeclaration invariant = new InvariantDeclaration(ast);
	    invariant.setBody(parseStatement(PScurly));
	    invariant.setSourceRange(start, prevToken.ptr + prevToken.len - start);
	    return invariant;
	}
	
	private UnitTestDeclaration parseUnitTest() {
		int start = token.ptr;
		nextToken();
		
		UnitTestDeclaration unitTest = new UnitTestDeclaration(ast);
	    unitTest.setBody(parseStatement(PScurly));
	    unitTest.setSourceRange(start, prevToken.ptr + prevToken.len - start);
	    return unitTest;
	}
	
	private FunctionDeclaration parseNew() {
		SimpleName name = new SimpleName(ast);
		name.setSourceRange(token.ptr, token.len);
		name.setIdentifier("new");
		
		FunctionDeclaration newDeclaration = new FunctionDeclaration(ast);
		newDeclaration.setSourceRange(token.ptr, 0);
		newDeclaration.setKind(FunctionDeclaration.Kind.NEW);
		newDeclaration.setName(name);
				
		nextToken();
		int[] varargs = new int[1];
		List<Argument> arguments = parseParameters(varargs);
		
		newDeclaration.arguments().addAll(arguments);
		newDeclaration.setVariadic(varargs[0] != 0);
	    parseContracts(newDeclaration);
	    return newDeclaration;
	}
	
	private FunctionDeclaration parseDelete() {
		SimpleName name = new SimpleName(ast);
		name.setSourceRange(token.ptr, token.len);
		name.setIdentifier("delete");
		
		FunctionDeclaration deleteDeclaration = new FunctionDeclaration(ast);
		deleteDeclaration.setSourceRange(token.ptr, 0);
		deleteDeclaration.setKind(FunctionDeclaration.Kind.DELETE);
		deleteDeclaration.setName(name);
				
		nextToken();
		int[] varargs = new int[1];
		List<Argument> arguments = parseParameters(varargs);
		
		if (varargs[0] != 0) {
	    	problem("... not allowed in delete function parameter list", 
	    			IProblem.SEVERITY_ERROR, 
	    			IProblem.VARIADIC_NOT_ALLOWED_IN_DELETE, 
	    			deleteDeclaration.getName());
	    }
		
		deleteDeclaration.arguments().addAll(arguments);
	    parseContracts(deleteDeclaration);
	    return deleteDeclaration;
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
						IElement e = ai != null ? ai : at;
						problem("Default argument expected", IProblem.SEVERITY_ERROR, IProblem.DEFAULT_ARGUMENT_EXPECTED, e.getStartPosition(), e.getLength());
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
					
					a = new Argument(ast);
					a.setPassageMode(inout);
					a.setType(at);
					a.setName(newSimpleNameForIdentifier(ai));
					a.setDefaultValue(ae);
					arguments.add(a);
					nextToken();
					break;
				}
				
				a = new Argument(ast);
				a.setPassageMode(inout);
				a.setType(at);
				a.setName(newSimpleNameForIdentifier(ai));
				a.setDefaultValue(ae);
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

		e = new EnumDeclaration(newSimpleNameForIdentifier(id), t);
		e.startPosition = enumToken.ptr;
		
		if (token.value == TOKsemicolon && id != null) {
			e.length = token.ptr + token.len - e.startPosition;
			nextToken();			  
		} else if (token.value == TOKlcurly) {
			// printf("enum definition\n");
			nextToken();
			String comment = token.string;
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
					
					em = new EnumMember(newSimpleNameForIdentifier(ident), value);
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
						IBaseClass last = baseClasses.get(baseClasses.size() - 1);
						problem("Members expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED,
								last.getStartPosition(), last.getLength());
					}
				}
			}
			
			AggregateDeclaration aggregateDeclaration = new AggregateDeclaration(ast);
			aggregateDeclaration.setName(newSimpleNameForIdentifier(id));
			if (baseClasses != null) {
				aggregateDeclaration.baseClasses().addAll(baseClasses);
			}
			aggregateDeclaration.setKind(tok == TOKclass ? AggregateDeclaration.Kind.CLASS : AggregateDeclaration.Kind.INTERFACE);
			a = aggregateDeclaration;
			a.startPosition = firstToken.ptr;
			break;
		}

		case TOKstruct:
			//if (id != null) {
			
				AggregateDeclaration structDeclaration = new AggregateDeclaration(ast);
				structDeclaration.setKind(AggregateDeclaration.Kind.STRUCT);
				structDeclaration.setName(newSimpleNameForIdentifier(id));
				a = structDeclaration;
				a.startPosition = firstToken.ptr;
			//} else {
			//	anon = 1;
			//}
			break;

		case TOKunion:
			//if (id != null) {
				AggregateDeclaration unionDeclaration = new AggregateDeclaration(ast);
				unionDeclaration.setKind(AggregateDeclaration.Kind.UNION);
				unionDeclaration.setName(newSimpleNameForIdentifier(id));
				a = unionDeclaration;
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
			// printf("aggregate definition\n");
			nextToken();
			List decl = parseDeclDefs(false);
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
			
			AggregateDeclaration structDeclaration = new AggregateDeclaration(ast);
			structDeclaration.setKind(AggregateDeclaration.Kind.STRUCT);
			a = structDeclaration;
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
		decldefs = parseDeclDefs(false);
		if (token.value != TOKrcurly)
		{
			problem("Template member expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
		    //goto Lerr;
			return null;
		}
		nextToken();
	    }

	    tempdecl = new TemplateDeclaration(ast);
	    tempdecl.setName(newSimpleNameForIdentifier(id));
	    tempdecl.templateParameters().addAll(tpl);
	    tempdecl.declarations().addAll(decldefs);
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
					
					AliasTemplateParameter aliasTemplateParameter = new AliasTemplateParameter(ast);
					aliasTemplateParameter.setName(newSimpleNameForIdentifier(tp_ident));
					aliasTemplateParameter.setSpecificType(tp_spectype);
					aliasTemplateParameter.setDefaultType(tp_defaulttype);
					
					tp = aliasTemplateParameter;
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
					
					TypeTemplateParameter typeTemplateParameter = new TypeTemplateParameter(ast);
					typeTemplateParameter.setName(newSimpleNameForIdentifier(tp_ident));
					typeTemplateParameter.setSpecificType(tp_spectype);
					typeTemplateParameter.setDefaultType(tp_defaulttype);
					
					tp = typeTemplateParameter;
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
					
					TupleTemplateParameter tupleTemplateParameter = new TupleTemplateParameter(ast);
					tupleTemplateParameter.setName(newSimpleNameForIdentifier(tp_ident));
					
					tp = tupleTemplateParameter;
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
					
					ValueTemplateParameter valueTemplateParameter = new ValueTemplateParameter(ast);
					valueTemplateParameter.setType(tp_valtype);
					valueTemplateParameter.setName(newSimpleNameForIdentifier(tp_ident));
					valueTemplateParameter.setSpecificValue(tp_specvalue);
					valueTemplateParameter.setDefaultValue(tp_defaultvalue);
					
					tp = valueTemplateParameter;
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
	private Declaration parseMixin() {
		TemplateMixin tm;
		Identifier id = null;
		TypeofType tqual;
		List<IElement> tiargs;
		List<Identifier> idents;
		
		Token firstToken = new Token(token);

		// printf("parseMixin()\n");
		nextToken();

		tqual = null;
		if (token.value == TOKdot) {
			/*
			id = new Identifier(Id.empty, TOKidentifier);
			id.startPosition = token.ptr;
			*/
		} else {
			if (token.value == TOKtypeof) {
				Expression exp;

				nextToken();
				check(TOKlparen);
				exp = parseExpression();
				check(TOKrparen);
				
				TypeofType typeofType = new TypeofType(ast);
				typeofType.setExpression(exp);
				tqual = typeofType;
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
				TemplateInstance tempinst = new TemplateInstance(id);
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

		tm = new TemplateMixin(id, tqual, idents, tiargs);
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
	private List<IElement> parseTemplateArgumentList() {
		// printf("Parser::parseTemplateArgumentList()\n");
	    List<IElement> tiargs = new ArrayList<IElement>();
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
	
	private Import parseImport(List<Declaration> decldefs, boolean isstatic) {
		ImportDeclaration importDeclaration = new ImportDeclaration(ast);

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
					
					name = newQualifiedNameForCurrentToken(name);
					nextToken();
				}

				theImport = new Import(ast);
				theImport.setName(name);
				theImport.setAlias(alias);

				importDeclaration.imports().add(theImport);

				/*
				 * Look for : alias=name, alias=name; syntax.
				 */
				if (token.value == TOKcolon) {
					Token dotToken = new Token(token);

					do {
						SelectiveImport selectiveImport = new SelectiveImport(ast);
						
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
		
		importDeclaration.setSourceRange(start, token.ptr + token.len - start);

		if (token.value == TOKsemicolon) {
			nextToken();
		} else {
			problem("';' expected following import declaration",
					IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED,
					token.ptr, token.len);
			nextToken();
		}
		
		decldefs.add(importDeclaration);

		return null;
	}
	
	private Type parseBasicType() {
		Type t = null;
		Identifier id = null;
		TypeQualified tid = null;
		TemplateInstance tempinst = null;
		
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
			t = newPrimitiveTypeFromCurrentToken(token);
			nextToken();
			break;

		case TOKidentifier:
			id = new Identifier(token);
			nextToken();
			if (token.value == TOKnot) {
				nextToken();
				tempinst = new TemplateInstance(id);
				tempinst.tiargs = parseTemplateArgumentList();
				
				tid = new TypeInstance(ast, tempinst);
				// goto Lident2;
				{
				Identifier[] p_id = { id };
				TemplateInstance[] p_tempinst = { tempinst };
				TypeQualified[] p_tid = { tid };
				Type[] p_t = { t };
				parseBasicType_Lident2(p_id, p_tempinst, p_tid, p_t);
				id = p_id[0];
				tempinst = p_tempinst[0];
				tid = p_tid[0];
				t = p_t[0];
				}
				break;

			}
			// Lident:
			tid = new TypeIdentifier(ast, id);
			tid.startPosition = prevToken.ptr;
			// Lident2:
			{
			Identifier[] p_id = { id };
			TemplateInstance[] p_tempinst = { tempinst };
			TypeQualified[] p_tid = { tid };
			Type[] p_t = { t };
			parseBasicType_Lident2(p_id, p_tempinst, p_tid, p_t);
			id = p_id[0];
			tempinst = p_tempinst[0];
			tid = p_tid[0];
			t = p_t[0];
			}
			break;

		case TOKdot:
			id = new Identifier(Id.empty, TOKidentifier);
			// goto Lident;
			tid = new TypeIdentifier(ast, id);
			{
			Identifier[] p_id = { id };
			TemplateInstance[] p_tempinst = { tempinst };
			TypeQualified[] p_tid = { tid };
			Type[] p_t = { t };
			parseBasicType_Lident2(p_id, p_tempinst, p_tid, p_t);
			id = p_id[0];
			tempinst = p_tempinst[0];
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
			
			TypeofType typeofType = new TypeofType(ast);
			typeofType.setExpression(exp);
			tid = typeofType;
			tid.startPosition = start;
			
			// goto Lident2;
			{
			Identifier[] p_id = { id };
			TemplateInstance[] p_tempinst = { tempinst };
			TypeQualified[] p_tid = { tid };
			Type[] p_t = { t };
			parseBasicType_Lident2(p_id, p_tempinst, p_tid, p_t);
			id = p_id[0];
			tempinst = p_tempinst[0];
			tid = p_tid[0];
			t = p_t[0];
			}
			break;
		}

		default:
			problem("Basic type expected", IProblem.SEVERITY_ERROR,
					IProblem.BASIC_TYPE_EXPECTED, token.ptr, token.len);
		
			PrimitiveType pt = new PrimitiveType(ast);
			pt.setPrimitiveTypeCode(PrimitiveType.Code.INT);
		
			t =  pt;
			break;
		}
		return t;
	}
	
	private void parseBasicType_Lident2(Identifier[] id, TemplateInstance[] tempinst, TypeQualified[] tid, Type[] t) {
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
				tempinst[0] = new TemplateInstance(id[0]);
				tempinst[0].tiargs = parseTemplateArgumentList();
				tid[0].addIdent((Identifier) tempinst[0]);
			} else
				tid[0].addIdent(id[0]);
		}
		tid[0].length = prevToken.ptr + prevToken.len - tid[0].startPosition;
		t[0] = tid[0];
	}
	
	private Type parseBasicType2(Type t) {
		Type ts;
		Type ta;
		Type subType;

		// printf("parseBasicType2()\n");
		while (true) {
			switch (token.value) {
			case TOKmul:
				subType = t;
				
				PointerType pointerType = new PointerType(ast);
				pointerType.setComponentType(t);
				pointerType.setSourceRange(t.startPosition, token.ptr + token.len - t.startPosition);
				t = pointerType;
				
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
						
						DynamicArrayType dynamicArrayType = new DynamicArrayType(ast);
						dynamicArrayType.setComponentType(t);
						dynamicArrayType.setSourceRange(t.getStartPosition(), token.ptr + token.len - t.getStartPosition());
						t = dynamicArrayType;
						
						nextToken();
					} else if (isDeclaration(token, 0, TOKrbracket, null)) { // It's
																				// an
																				// associative
																				// array
																				// declaration
						subType = t;
						Type index;

						// printf("it's an associative array\n");
						index = parseBasicType();
						index = parseDeclarator(index, null); // [ type ]
						
						AssociativeArrayType associativeArrayType = new AssociativeArrayType(ast);
					    associativeArrayType.setComponentType(t);
					    associativeArrayType.setKeyType(index);
					    associativeArrayType.setSourceRange(subType.getStartPosition(), token.ptr + token.len - subType.getStartPosition());
						t = associativeArrayType;
						
						check(TOKrbracket);
					} else {
						subType = t;

						// printf("it's [expression]\n");
						inBrackets++;
						Expression e = parseExpression(); // [ expression ]
					    if (token.value == TOKslice) {
							Expression e2;

							nextToken();
							e2 = parseExpression(); // [ exp .. exp ]
							
							SliceType sliceType = new SliceType(ast);
							sliceType.setComponentType(t);
							sliceType.setFromExpression(e);
							sliceType.setToExpression(e2);
							t = sliceType;
						} else {
							StaticArrayType staticArrayType = new StaticArrayType(ast);
							staticArrayType.setComponentType(t);
							staticArrayType.setSize(e);
							t = staticArrayType;
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
							
							DynamicArrayType dynamicArrayType = new DynamicArrayType(ast);
							dynamicArrayType.setComponentType(t);
							ta = dynamicArrayType;
							
							nextToken();
						} else if (isDeclaration(token, 0, TOKrbracket, null)) { // It's
																					// an
																					// associative
																					// array
																					// declaration
							Type index;

							// printf("it's an associative array\n");
							index = parseBasicType();
							index = parseDeclarator(index, null); // [ type ]
							check(TOKrbracket);
							
							AssociativeArrayType associativeArrayType = new AssociativeArrayType(ast);
						    associativeArrayType.setComponentType(t);
						    associativeArrayType.setKeyType(index);
							
							ta = associativeArrayType;
						} else {
							// printf("it's [expression]\n");
							Expression e = parseExpression(); // [ expression
																// ]
							StaticArrayType staticArrayType = new StaticArrayType(ast);
							staticArrayType.setComponentType(t);
							staticArrayType.setSize(e);
							ta = staticArrayType;
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
				
				DelegateType delegateType = new DelegateType(ast);
				delegateType.setReturnType(typeFunction.getReturnType());
				delegateType.setFunctionPointer(save == TOKfunction);
				delegateType.arguments().addAll(typeFunction.arguments);
				delegateType.setVariadic(varargs != 0);
				t = delegateType;
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
				DynamicArrayType dynamicArrayType = new DynamicArrayType(ast);
				dynamicArrayType.setComponentType(t);
				dynamicArrayType.setSourceRange(t.getStartPosition(), token.ptr + token.len - t.getStartPosition());
			    ta = dynamicArrayType;
			    
			    nextToken();
			}
			else if (isDeclaration(token, 0, TOKrbracket, null))
			{   // It's an associative array declaration
			    Type index;

			    //printf("it's an associative array\n");
			    index = parseBasicType();
			    index = parseDeclarator(index, null, null, identStart);	// [ type ]
			    check(TOKrbracket);
			    
			    AssociativeArrayType associativeArrayType = new AssociativeArrayType(ast);
			    associativeArrayType.setComponentType(t);
			    associativeArrayType.setKeyType(index);
			    ta = associativeArrayType;
			}
			else
			{
			    //printf("it's [expression]\n");
			    Expression e = parseExpression();		// [ expression ]
			    
			    StaticArrayType staticArrayType = new StaticArrayType(ast);
				staticArrayType.setComponentType(t);
				staticArrayType.setSize(e);
			    
			    ta = staticArrayType;
			    ta.startPosition = t.startPosition;
			    ta.length = token.ptr + token.len - ta.startPosition;
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
				//printf("function template declaration\n");

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
					DelegateType delegateType = new DelegateType(ast);
					delegateType.setReturnType(typeFunction.getReturnType());
					delegateType.setFunctionPointer(true);
					delegateType.arguments().addAll(typeFunction.arguments);
					delegateType.setVariadic(varargs != 0);
					
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
	
	@SuppressWarnings("unchecked")
	private List<Declaration> parseDeclarations() {
		int storage_class;
		int stc;
		Type ts;
		Type t;
		Type tfirst;
		Identifier ident;
		List a;
		TOK tok;
		LINK link = linkage;
		
		List<Comment> lastComments = getLastDocComments();
		
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
			case TOKscope:
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
			VarDeclaration v = new VarDeclaration(ast, null, ident, init);
			v.storage_class = storage_class;
			v.modifierFlags = STC.getModifiers(storage_class);
			a.add(v);
			if (token.value == TOKsemicolon) {
				nextToken();
				v.comments = lastComments;
				adjustLastDocComment();
			} else {
				problem("Semicolon expected following auto declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, token.ptr, token.len);
			}
			return a;
		}

		if (token.value == TOKclass) {
			AggregateDeclaration s;

			s = (AggregateDeclaration) parseAggregate();
			// TODO CHECK DMD s.storage_class |= storage_class;
			s.modifierFlags = STC.getModifiers(storage_class);
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
		
		while (true) {
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
						ident.startPosition, ident.length);
			}
			if (ident == null) {
				problem("No identifier for declarator", IProblem.SEVERITY_ERROR, IProblem.NO_IDENTIFIER_FOR_DECLARATION, t.getStartPosition(), t.getLength());
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
					TypedefDeclaration td = new TypedefDeclaration(ast);
					if (ident != null) {
						td.setName(newSimpleNameForIdentifier(ident));
					}
					td.setType(t);
					td.setInitializer(init);
					td.startPosition = nextTypdefOrAliasStart;
					v = td;
				} else {
					if (init != null) {
						problem("Alias cannot have initializer", IProblem.SEVERITY_ERROR, IProblem.ALIAS_CANNOT_HAVE_INITIALIZER, tokAssign.ptr, init.startPosition + init.length - tokAssign.ptr);
					}
					AliasDeclaration aliasDeclaration = new AliasDeclaration(ast);
					aliasDeclaration.setName(newSimpleNameForIdentifier(ident));
					aliasDeclaration.setType(t);
					aliasDeclaration.startPosition = nextTypdefOrAliasStart;
					v = aliasDeclaration;
				}
				v.storage_class = storage_class;
				a.add(v);
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
				
				SimpleName name = new SimpleName(ast);
				name.setSourceRange(ident.startPosition, ident.length);
				name.setIdentifier(ident.string);
				
				FunctionDeclaration function = new FunctionDeclaration(ast);
				function.setKind(FunctionDeclaration.Kind.FUNCTION);
				function.setSourceRange(token.ptr, 0);
				function.setName(name);
				function.arguments().addAll(typeFunction.getArguments());
				function.setVariadic(typeFunction.varargs);
				function.setReturnType(typeFunction.getReturnType());

				function.comments = lastComments;
				adjustLastDocComment();
				
				function.startPosition = t.startPosition;
				function.comments = lastComments;
				
				parseContracts(function);
				
				Dsymbol s;
				if (link == linkage) {
					s = function;
				} else {
					ExternDeclaration externDeclaration = new ExternDeclaration(ast);
					externDeclaration.setLinkage(link.getLinkage());
					externDeclaration.declarations().add(function);
					s = externDeclaration;
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
				VarDeclaration v;
				Initializer init;

				init = null;
				if (token.value == TOKassign) {
					nextToken();
					init = parseInitializer();
				}
				v = new VarDeclaration(ast, t, ident, init);
				v.startPosition = nextVarStart;
				v.storage_class = storage_class;
				v.modifierFlags = STC.getModifiers(storage_class);
				a.add(v);
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
					nextVarStart = token.ptr;
					v.comments = lastComments;
					adjustLastDocComment();
					continue;

				default:
					problem("Semicolon expected to close declaration", IProblem.SEVERITY_ERROR, IProblem.SEMICOLON_EXPECTED, v.startPosition, prevToken.ptr + prevToken.len - v.startPosition);
					break;
				}
			}
			break;
		}
		return a;
	}
	
	private void parseContracts(FunctionDeclaration f) {
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
					ie = new ExpressionInitializer(e);
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
			
			is = new StructInitializer();
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
					is.addInit(id, value);
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
					is.addInit(null, value);
					comma = 1;
					continue;
				}
				break;
			}
			
			return is;

		case TOKlbracket:
			ia = new ArrayInitializer(ast);
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
						value = new ExpressionInitializer(e);
						e = null;
					}
					ArrayInitializerFragment fragment = new ArrayInitializerFragment(ast);
					fragment.setInitializer(value);
					if (e == null) {
						fragment.setSourceRange(value.getStartPosition(), value.getLength());
					} else {
						fragment.setExpression(e);
						fragment.setSourceRange(e.getStartPosition(), value.getStartPosition() + value.getLength() - e.getStartPosition());
					}
					ia.fragments().add(fragment);
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
					
					fragment = new ArrayInitializerFragment(ast);
					fragment.setSourceRange(value.getStartPosition(), value.getLength());
					fragment.setInitializer(value);
					
					ia.fragments().add(fragment);
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
				VoidInitializer init = new VoidInitializer(ast);
				init.startPosition = prevToken.ptr;
				init.length = prevToken.len;
				return init;
			}
			// goto Lexpression;

		default:
			// Lexpression:
			e = parseAssignExp();
			ie = new ExpressionInitializer(e);
			return ie;
		}
	}
	
	@SuppressWarnings("unchecked") 
	public Statement parseStatement(int flags) {
		Statement s = null;
		Token t;

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
				
				LabelStatement labelStatement = new LabelStatement(ast);
				labelStatement.setLabel(newSimpleNameForIdentifier(ident));
				labelStatement.setBody(body);
				labelStatement.setSourceRange(ident.startPosition, body.startPosition + body.length - ident.startPosition);
				s = labelStatement;
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
				Expression exp;

				exp = parseExpression();
				check(TOKsemicolon, "statement");
				
				ExpressionStatement expressionStatement = new ExpressionStatement(ast);
				expressionStatement.setExpression(exp);
				s = expressionStatement;
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
			
			ExpressionStatement expressionStatement = new ExpressionStatement(ast);
			expressionStatement.setExpression(exp);
			expressionStatement.setSourceRange(exp.getStartPosition(), semiToken.ptr + semiToken.len - exp.getStartPosition());
			s = expressionStatement;
			break;
		}

		case TOKstatic: { // Look ahead to see if it's static assert() or
							// static if()
			Token t2;
			
			Token saveToken = new Token(token);
			
			t2 = peek(token);
			if (t2.value == TOKassert) {
				nextToken();
				
				StaticAssertStatement staticAssertStatement = new StaticAssertStatement(ast);
				staticAssertStatement.setStaticAssert(parseStaticAssert());
				staticAssertStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				
				s = staticAssertStatement;
				break;
			}
			if (t2.value == TOKif) {
				nextToken();
				
				StaticIfStatement staticIfStatement = new StaticIfStatement(ast);
				StaticIfCondition staticIfCondition = parseStaticIfCondition();
				staticIfStatement.setExpression(staticIfCondition.exp);
				staticIfStatement.setThenBody(parseStatement(PSsemi));
				if (token.value == TOKelse) {
					nextToken();
					staticIfStatement.setElseBody(parseStatement(PSsemi));
				}
				staticIfStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				s = staticIfStatement;
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
			AggregateDeclaration d;

			d = parseAggregate();
			
			DeclarationStatement ds = new DeclarationStatement(ast);
			ds.setDeclaration(d);
			ds.setSourceRange(d.startPosition, d.length);
			s = ds;
			break;
		}

		case TOKenum: {
			Declaration d;

			d = parseEnum();
			
			DeclarationStatement ds = new DeclarationStatement(ast);
			ds.setDeclaration(d);
			ds.setSourceRange(d.startPosition, d.length);
			s = ds;
			break;
		}

		case TOKmixin: {
			Declaration d;
			
			d = parseMixin();
			
			DeclarationStatement ds = new DeclarationStatement(ast);
			ds.setDeclaration(d);
			ds.setSourceRange(d.startPosition, d.length);
			s = ds;
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
			
			Block block = new Block(ast);
			block.statements().addAll(statements);
			block.setSourceRange(saveToken.ptr, token.ptr + token.len - saveToken.ptr);
			s = block;
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
			
			Token saveToken = new Token(token);

			nextToken();
			check(TOKlparen);
			condition2 = parseExpression();
			check(TOKrparen);
			
			body = parseStatement(PSscope);
			
			WhileStatement whileStatement = new WhileStatement(ast);
			whileStatement.setExpression(condition2);
			whileStatement.setBody(body);
			whileStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			s = whileStatement;
			break;
		}

		case TOKsemicolon:
			if ((flags & PSsemi) == 0) {
				problem("Use '{ }' for an empty statement, not a ';'", IProblem.SEVERITY_ERROR, IProblem.USE_BRACES_FOR_AN_EMPTY_STATEMENT, token.ptr, token.len);
			}
			nextToken();
			
			s = new ExpressionStatement(ast);
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
			s = new DoStatement(body, condition2);
			s.startPosition = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.startPosition;
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
			body = parseStatement(PSscope);
			s = new ForStatement(init, condition2, increment, body);
			s.startPosition = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.startPosition;
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
			
			Token saveToken = new Token(token);

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
						a = new Argument(ast);
						a.setPassageMode(inout);
						a.setType(at);
						a.setName(newSimpleNameForIdentifier(ai));
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
				a = new Argument(ast);
				a.setPassageMode(inout);
				a.setType(at);
				a.setName(newSimpleNameForIdentifier(ai));
				a.setSourceRange(argumentStart.ptr, prevToken.ptr + prevToken.len - argumentStart.ptr);
				a.startPosition = argumentStart.ptr;
				a.length = prevToken.ptr + prevToken.len - a.startPosition;
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
			
			ForeachStatement fs = new ForeachStatement(ast);
			fs.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			fs.setReverse(op == TOK.TOKforeach_reverse);
			fs.setExpression(aggr);
			fs.arguments().addAll(arguments);
			fs.setBody(body);
			
			s = fs;
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
						arg = new Argument(ast);
						arg.setPassageMode(Argument.PassageMode.IN);
						arg.setName(newSimpleNameForCurrentToken());
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

					arg = new Argument(ast);
					arg.setPassageMode(Argument.PassageMode.IN);
					arg.setType(at);
					arg.setName(newSimpleNameForIdentifier(ai));
					arg.setSourceRange(argToken.ptr, prevToken.ptr + prevToken.len - argToken.ptr);
					
					check(TOKassign);					
				}

				// Check for " ident;"
				else if (token.value == TOKidentifier) {
					Token t2 = peek(token);
					if (t2.value == TOKcomma || t2.value == TOKsemicolon) {
						arg = new Argument(ast);
						arg.setPassageMode(Argument.PassageMode.IN);
						arg.setName(newSimpleNameForCurrentToken());
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
			s = new IfStatement(arg, condition2, ifbody2, elsebody2);
			s.startPosition = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.startPosition;
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
				
				ScopeStatement scope = new ScopeStatement(ast);
				scope.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				scope.setEvent(t2 == TOKon_scope_exit ? ScopeStatement.Event.EXIT : (t2 == TOKon_scope_failure ? ScopeStatement.Event.FAILURE : ScopeStatement.Event.SUCCESS));
				scope.setBody(st);
				
				s = scope;
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
			
			ScopeStatement scope = new ScopeStatement(ast);
			scope.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			scope.setEvent(t2 == TOKon_scope_exit ? ScopeStatement.Event.EXIT : (t2 == TOKon_scope_failure ? ScopeStatement.Event.FAILURE : ScopeStatement.Event.SUCCESS));
			scope.setBody(st);
			
			s = scope;
			break;
		}

		case TOKdebug:
			saveToken = new Token(token);
			
			nextToken();
			
			DebugStatement debugStatement = new DebugStatement(ast);
			DebugCondition debugCondition = parseDebugCondition();
			if (debugCondition.id != null) {
				Version version = new Version(ast);
				version.setValue(debugCondition.id.string);
				debugStatement.setVersion(version);
			}
			debugStatement.setThenBody(parseStatement(PSsemi));
			if (token.value == TOKelse) {
				nextToken();
				debugStatement.setElseBody(parseStatement(PSsemi));
			}
			debugStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			s = debugStatement;
			break;

		case TOKversion:
			saveToken = new Token(token);
			
			nextToken();
			
			VersionStatement versionStatement = new VersionStatement(ast);
			VersionCondition versionCondition = parseVersionCondition();
			if (versionCondition.id != null) {
				Version version = new Version(ast);
				version.setValue(versionCondition.id.string);
				versionStatement.setVersion(version);
			}
			versionStatement.setThenBody(parseStatement(PSsemi));
			if (token.value == TOKelse) {
				nextToken();
				versionStatement.setElseBody(parseStatement(PSsemi));
			}
			versionStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			s = versionStatement;
			break;

		case TOKiftype:
			saveToken = new Token(token);
			
			IftypeStatement iftypeStatement = new IftypeStatement(ast);
			IftypeCondition iftypeCondition = parseIftypeCondition();
			if (iftypeCondition != null) {
				iftypeStatement.setKind(iftypeCondition.getKind());
				iftypeStatement.setName(newSimpleNameForIdentifier(iftypeCondition.ident));
				iftypeStatement.setTestType(iftypeCondition.targ);
				iftypeStatement.setMatchingType(iftypeCondition.tspec);
			}
			iftypeStatement.setThenBody(parseStatement(PSsemi));
			if (token.value == TOKelse) {
				nextToken();
				iftypeStatement.setElseBody(parseStatement(PSsemi));
			}
			iftypeStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			s = iftypeStatement;
			break;

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
			} else {
				body = parseStatement(PSsemi);
			}
			
			PragmaStatement pragmaStatement = new PragmaStatement(ast);
			pragmaStatement.setName(newSimpleNameForIdentifier(ident));
			if (args != null) {
				pragmaStatement.arguments().addAll(args);
			}
			pragmaStatement.setBody(body);
			pragmaStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			
			s = pragmaStatement;
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
			
			SwitchStatement ss = new SwitchStatement(ast);
			ss.setExpression(condition2);
			ss.setBody(body);
			ss.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			
			s = ss;
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
			
			Block block = new Block(ast);
			block.statements().addAll(statements);
			s = block;
			
			/*
			s = new ScopeStatement(s);
			*/

			// Keep cases in order by building the case statements backwards
			for (int i = cases.size(); i != 0; i--) {
				exp = (Expression) cases.get(i - 1);
				
				CaseStatement cs = new CaseStatement(ast);
				cs.setExpression(exp);
				cs.setBody(s);
				s = cs;
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
			
			Block block = new Block(ast);
			block.statements().addAll(statements);
			block.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			s = block;
			
			/*
			s = new ScopeStatement(s);
			s.startPosition = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.startPosition;
			*/
			
			DefaultStatement defaultStatement = new DefaultStatement(ast);
			defaultStatement.setBody(s);
			defaultStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			s = defaultStatement;
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
			
			ReturnStatement returnStatement = new ReturnStatement(ast);
			returnStatement.setExpression(exp);
			returnStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			
			s = returnStatement;
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
			
			BreakStatement breakStatement = new BreakStatement(ast);
			breakStatement.setLabel(newSimpleNameForIdentifier(ident));
			breakStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			s = breakStatement;
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
			
			
			ContinueStatement continueStatement = new ContinueStatement(ast);
			continueStatement.setLabel(newSimpleNameForIdentifier(ident));
			continueStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			s = continueStatement;
			break;
		}

		case TOKgoto: {
			Identifier ident;
			
			saveToken = new Token(token);

			nextToken();
			if (token.value == TOKdefault) {
				nextToken();
				s = new GotoDefaultStatement(ast);
			} else if (token.value == TOKcase) {
				Expression exp = null;

				nextToken();
				if (token.value != TOKsemicolon)
					exp = parseExpression();
				
				GotoCaseStatement gotoCaseStatement = new GotoCaseStatement(ast);
				gotoCaseStatement.setLabel(exp);
				s = gotoCaseStatement;
			} else {
				if (token.value != TOKidentifier) {
					problem("Identifier expected following goto", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					ident = null;
				} else {
					ident = new Identifier(token);
					nextToken();
				}
				
				GotoStatement gotoStatement = new GotoStatement(ast);
				if (ident != null) {
					gotoStatement.setLabel(newSimpleNameForIdentifier(ident));
				}
				s = gotoStatement;
			}
			check(TOKsemicolon, "goto statement");
			s.startPosition = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.startPosition;
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
			
			SynchronizedStatement synchronizedStatement = new SynchronizedStatement(ast);
			synchronizedStatement.setExpression(exp);
			synchronizedStatement.setBody(body);
			synchronizedStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			
			s = synchronizedStatement;
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
			
			WithStatement withStatement = new WithStatement(ast);
			withStatement.setExpression(exp);
			withStatement.setBody(body);
			withStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			s = withStatement;
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
				
				CatchClause catchClause = new CatchClause(ast);
				catchClause.setType(t2);
				catchClause.setName(newSimpleNameForIdentifier(id));
				catchClause.setBody(handler);
				catchClause.setSourceRange(firstToken.ptr, prevToken.ptr + prevToken.len - firstToken.ptr);
				c = catchClause;
				
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
				TryStatement tryStatement = new TryStatement(ast);
				tryStatement.setBody((Block) body);
				if (catches != null) {
					tryStatement.catchClauses().addAll(catches);
				}
				tryStatement.setFinally((Block) finalbody);
				tryStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
				s = tryStatement;
			}
			break;
		}

		case TOKthrow: {
			Expression exp;
			
			saveToken = new Token(token);

			nextToken();
			exp = parseExpression();
			check(TOKsemicolon, "throw statement");
			
			ThrowStatement throwStatement = new ThrowStatement(ast);
			throwStatement.setExpression(exp);
			throwStatement.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
			
			s = throwStatement;
			break;
		}

		case TOKvolatile:
			saveToken = new Token(token);
			
			nextToken();
			s = parseStatement(PSsemi | PScurlyscope);
			s = new VolatileStatement(s);
			s.startPosition = saveToken.ptr;
			s.length = prevToken.ptr + prevToken.len - s.startPosition;
			break;

		case TOKasm: {
			List<Statement> statements;
			Identifier label;
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
						s = new AsmStatement(toklist);
						s.startPosition = saveToken.ptr;
						s.length = token.ptr + token.len - s.startPosition;
						
						toklist = null;
						ptoklist[0] = toklist;
						if (label == null) {
							LabelStatement labelStatement = new LabelStatement(ast);
							labelStatement.setLabel(newSimpleNameForIdentifier(label));
							labelStatement.setBody(s);
							s = labelStatement;
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
			
			Block block = new Block(ast);
			block.statements().addAll(statements);
			block.setSourceRange(saveToken.ptr, token.ptr + token.len - saveToken.ptr);
			s = block;
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

		// Changed from DMD... TODO remove it!
		if (s == null) return new Block(ast);
		
		return s;
	}
	
	private void parseStatement_Ldeclaration(Statement[] s, int flags) {
		List a;

		a = parseDeclarations();
		if (a.size() > 1) {
			List<Statement> as = new ArrayList<Statement>(a.size());
			for (int i = 0; i < a.size(); i++) {
				Declaration d = (Declaration) a.get(i);
				
				DeclarationStatement ds = new DeclarationStatement(ast);
				ds.setDeclaration(d);
				ds.setSourceRange(d.startPosition, d.length);
				s[0] = ds;
				as.add(s[0]);
			}
			
			Block block = new Block(ast);
			block.statements().addAll(as);
			s[0] = block;
		} else if (a.size() == 1) {
			Declaration d = (Declaration) a.get(0);
			
			DeclarationStatement ds = new DeclarationStatement(ast);
			ds.setDeclaration(d);
			ds.setSourceRange(d.startPosition, d.length);
			s[0] = ds;
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

	    switch (token.value)
	    {
		case TOKidentifier:
		    id = new Identifier(token);
		    nextToken();
		    if (token.value == TOKnot && peek(token).value == TOKlparen)
		    {	// identifier!(template-argument-list)
			TemplateInstance tempinst;

			tempinst = new TemplateInstance(id);
			nextToken();
			tempinst.tiargs = parseTemplateArgumentList();
			e = new ScopeExp(tempinst);
		    }
		    else
			e = newSimpleNameForIdentifier(id);
		    break;

		case TOKdollar:
		    if (inBrackets == 0) {
		    	problem("'$' is valid only inside [] of index or slice", IProblem.SEVERITY_ERROR, IProblem.DOLLAR_INVALID_OUTSIDE_BRACKETS, token.ptr, token.len);
		    }
		    e = new DollarLiteral(ast);
		    nextToken();
		    break;

		case TOKdot:
		    // Signal global scope '.' operator with "" identifier
			e = null;
		    // e = new SimpleName(new Identifier(Id.empty, TOKidentifier));
		    break;

		case TOKthis:
		    e = new ThisLiteral(ast);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKsuper:
		    e = new SuperLiteral(ast);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKint32v:
		    e = new IntegerExp(token.numberValue, PrimitiveType.Code.INT);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKuns32v:
		    e = new IntegerExp(token.numberValue, PrimitiveType.Code.UINT);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKint64v:
		    e = new IntegerExp(token.numberValue, PrimitiveType.Code.LONG);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKuns64v:
		    e = new IntegerExp(token.numberValue, PrimitiveType.Code.ULONG);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKfloat32v:
		    e = new RealExp(token.numberValue, PrimitiveType.Code.FLOAT);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKfloat64v:
		    e = new RealExp(token.numberValue, PrimitiveType.Code.DOUBLE);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKfloat80v:
		    e = new RealExp(token.numberValue, PrimitiveType.Code.REAL);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKimaginary32v:
		    e = new RealExp(token.numberValue, PrimitiveType.Code.IFLOAT);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKimaginary64v:
		    e = new RealExp(token.numberValue, PrimitiveType.Code.IDOUBLE);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKimaginary80v:
		    e = new RealExp(token.numberValue, PrimitiveType.Code.IREAL);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKnull:
		    e = new NullLiteral(ast);
		    e.startPosition = token.ptr;
		    e.length = token.len;
		    nextToken();
		    break;

		case TOKtrue:
		case TOKfalse:
			BooleanLiteral booleanLiteral = new BooleanLiteral(ast);
			booleanLiteral.setBooleanValue(token.value == TOK.TOKtrue);
			booleanLiteral.setSourceRange(token.ptr, token.len);
			e = booleanLiteral;
		    nextToken();
		    break;

		case TOKcharv:
		    e = new IntegerExp(token.numberValue, PrimitiveType.Code.CHAR);
		    nextToken();
		    break;

		case TOKwcharv:
		    e = new IntegerExp(token.numberValue, PrimitiveType.Code.WCHAR);
		    nextToken();
		    break;

		case TOKdcharv:
		    e = new IntegerExp(token.numberValue, PrimitiveType.Code.DCHAR);
		    nextToken();
		    break;

		case TOKstring: {
			String s;
			int start = token.ptr; 
			int len = token.len;
			int postfix;

			// cat adjacent strings
			s = token.string;
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

					if (token.string != null)
						s += token.string;
				} else
					break;
			}
			e = new StringExp(s, len, postfix);
			e.startPosition = start;
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
			t = newPrimitiveTypeFromCurrentToken(token);
			nextToken();
			// L1:
			    check(TOKdot, t.toString());
			    if (token.value != TOKidentifier)
			    {
			    	problem("Identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
			    	// goto Lerr;
		    		// Anything for e, as long as it's not NULL
		    		e = new IntegerExp(BigInteger.ZERO, PrimitiveType.Code.INT);
		    		nextToken();
		    		break;
			    }
			    e = new TypeDotIdExp(ast, t, new Identifier(token));
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
		    
		    TypeofType typeofType = new TypeofType(ast);
			typeofType.setExpression(exp);
			t = typeofType;
			
		    if (token.value == TOKdot) {
		    	// goto L1;
		    	check(TOKdot, t.toString());
			    if (token.value != TOKidentifier)
			    {   
			    	problem("Identifier expected", IProblem.SEVERITY_ERROR, IProblem.IDENTIFIER_EXPECTED, token.ptr, token.len);
					// goto Lerr;
			    	// Anything for e, as long as it's not NULL
			    	e = new IntegerExp(BigInteger.ZERO, PrimitiveType.Code.INT);
			    	nextToken();
			    	break;
			    }
			    e = new TypeDotIdExp(ast, t, new Identifier(token));
			    nextToken();
			    break;
		    }
		    	
		    e = new TypeExp(ast, t);
		    e.startPosition = saveToken.ptr;
		    e.length = prevToken.ptr + prevToken.len - e.startPosition;
		    break;
		}

		case TOKtypeid:
		{   Type t2;

		    nextToken();
		    check(TOKlparen, "typeid");
		    t2 = parseBasicType();
		    t2 = parseDeclarator(t2, null);	// ( type )
		    check(TOKrparen);
		    e = new TypeidExp(ast, t2);
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
				e = new IntegerExp(BigInteger.ZERO, PrimitiveType.Code.INT);
				nextToken();
				break;
			}
			
			if (token2.value == TOK.TOKreserved) {
				IsTypeExpression isTypeExpression = new IsTypeExpression(ast);
				isTypeExpression.setName(newSimpleNameForIdentifier(ident));
				isTypeExpression.setType(targ);
				isTypeExpression.setSpecialization(tspec);
				isTypeExpression.setSameComparison(tok == TOK.TOKequal);
				e = isTypeExpression;
			} else {
				IsTypeSpecializationExpression isTypeSpecializationExpression = new IsTypeSpecializationExpression(ast);
				isTypeSpecializationExpression.setName(newSimpleNameForIdentifier(ident));
				isTypeSpecializationExpression.setType(targ);
				TypeSpecialization specialization = null;
				switch(token2.value) {
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
				isTypeSpecializationExpression.setSpecialization(specialization);
				isTypeSpecializationExpression.setSameComparison(tok == TOK.TOKequal);
				e = isTypeSpecializationExpression;
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
			
			AssertExpression assertExpression = new AssertExpression(ast);
			assertExpression.setExpression(e);
			assertExpression.setMessage(msg);
			assertExpression.setSourceRange(start, end - start);
			e = assertExpression;
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

			e = new ParenthesizedExpression(e);
			e.startPosition = start;
			e.length = end - start;
			break;

		case TOKlbracket:
		{   List<Expression> elements = parseArguments();

		    e = new ArrayLiteral(elements);
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
		    e = new IntegerExp(BigInteger.ZERO, PrimitiveType.Code.INT);
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
						TemplateInstance tempinst;

						tempinst = new TemplateInstance(id);
						nextToken();
						tempinst.tiargs = parseTemplateArgumentList();
						e = new DotTemplateInstanceExp(e, tempinst);
					} else {
						DotIdentifierExpression die = new DotIdentifierExpression(ast);
						die.setExpression(e);
						die.setName(newSimpleNameForIdentifier(id));
						if (e == null) {
							die.setSourceRange(prevToken.ptr, token.ptr + token.len - prevToken.ptr);
						} else {
							die.setSourceRange(e.getStartPosition(), id.startPosition + id.length - e.getStartPosition());
						}
						
						e = die;
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
				e = new CallExpression(e, parseArguments());
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
					e = new SliceExpression(e, null, null);
					nextToken();
				} else {
					index = parseAssignExp();
					if (token.value == TOKslice) { // array[lwr .. upr]
						nextToken();
						upr = parseAssignExp();
						e = new SliceExpression(e, index, upr);
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
						
						ArrayAccess arrayAccess = new ArrayAccess(ast);
						arrayAccess.setArray(e);
						arrayAccess.indexes().addAll(arguments);
						
						e = arrayAccess;
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
			
			DeleteExpression deleteExpression = new DeleteExpression(ast);
			deleteExpression.setExpression(e);
			e = deleteExpression;
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
			e = new CastExpression(e, t);
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
						e = new TypeDotIdExp(ast, t, new Identifier(token));
						nextToken();
					} else {
						e = parseUnaryExp();
						e = new CastExpression(e, t);
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
		DmdFuncLiteralDeclaration fd;
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
		
		fd = new DmdFuncLiteralDeclaration(ast);
		fd.arguments().addAll(arguments);
		parseContracts(fd);
		
		FunctionLiteralDeclarationExpression expression = new FunctionLiteralDeclarationExpression(ast);
		expression.setSyntax(syntax); 
		expression.arguments().addAll(fd.arguments());
		expression.setVariadic(fd.isVariadic());
		expression.setPrecondition(fd.getPrecondition());
		expression.setPostcondition(fd.getPostcondition());
		expression.setPostconditionVariableName(fd.getPostconditionVariableName());
		expression.setBody(fd.getBody());
		
		e[0] = expression;
	}
	
	private Expression parseMulExp()
	{   Expression e;
	    Expression e2;

	    e = parseUnaryExp();
	    while (true)
	    {
		switch (token.value)
		{
		    case TOKmul: nextToken(); e2 = parseUnaryExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.TIMES); continue;
		    case TOKdiv:   nextToken(); e2 = parseUnaryExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.DIVIDE); continue;
		    case TOKmod:  nextToken(); e2 = parseUnaryExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.REMAINDER); continue;

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
		    case TOKadd:    nextToken(); e2 = parseMulExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.PLUS); continue;
		    case TOKmin:    nextToken(); e2 = parseMulExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.MINUS); continue;
		    case TOKtilde:  nextToken(); e2 = parseMulExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.CONCATENATE); continue;

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
		    case TOKshl:  nextToken(); e2 = parseAddExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.LEFT_SHIFT);  continue;
		    case TOKshr:  nextToken(); e2 = parseAddExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.RIGHT_SHIFT_SIGNED);  continue;
		    case TOKushr: nextToken(); e2 = parseAddExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED); continue;

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
		    case TOKlt: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.LESS); continue;
		    case TOKle: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.LESS_EQUALS); continue;
		    case TOKgt: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.GREATER); continue;
		    case TOKge: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.GREATER_EQUALS); continue;
		    case TOKunord: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.NOT_LESS_GREATER_EQUALS); continue;
		    case TOKlg: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.LESS_GREATER); continue;
		    case TOKleg: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.LESS_GREATER_EQUALS); continue;
		    case TOKule: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.NOT_GREATER); continue;
		    case TOKul: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.NOT_GREATER_EQUALS); continue;
		    case TOKuge: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.NOT_LESS); continue;
		    case TOKug: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.NOT_LESS_EQUALS); continue;
		    case TOKue: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.NOT_LESS_GREATER); continue;
		    case TOKin: nextToken(); e2 = parseShiftExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.IN); continue;

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
				e = new InfixExpression(e,e2,InfixExpression.Operator.EQUALS);
				continue;

		    case TOKnotequal:
				nextToken();
				e2 = parseRelExp();
				e = new InfixExpression(e,e2,InfixExpression.Operator.NOT_EQUALS);
				continue;

		    case TOKidentity:
			//if (!global.params.useDeprecated)
		    	problem("'===' is no longer legal, use 'is' instead", IProblem.SEVERITY_ERROR,
		    			IProblem.THREE_EQUALS_IS_NO_LONGER_LEGAL, token.ptr, token.len);
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = new InfixExpression(e,e2,InfixExpression.Operator.IS);
			continue;

		    case TOKnotidentity:
			//if (!global.params.useDeprecated)
		    	problem("'!==' is no longer legal, use 'is' instead", IProblem.SEVERITY_ERROR,
		    			IProblem.NOT_TWO_EQUALS_IS_NO_LONGER_LEGAL, token.ptr, token.len);
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = new InfixExpression(e,e2,InfixExpression.Operator.NOT_IS);
			continue;

		    case TOKis:
			value = TOKidentity;
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = new InfixExpression(e,e2,InfixExpression.Operator.IS);
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
			e = new InfixExpression(e,e2,InfixExpression.Operator.NOT_IS);
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
			e = new InfixExpression(e,e2,InfixExpression.Operator.AND);
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
			e = new InfixExpression(e,e2,InfixExpression.Operator.XOR);
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
			e = new InfixExpression(e,e2,InfixExpression.Operator.OR);
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
			e = new InfixExpression(e,e2,InfixExpression.Operator.AND_AND);
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
			e = new InfixExpression(e,e2,InfixExpression.Operator.OR_OR);
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
			e = new ConditionalExpression(e, e1, e2);
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
		case TOKassign:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.ASSIGN); continue;
		case TOKaddass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.PLUS_ASSIGN); continue;
		case TOKminass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.MINUS_ASSIGN); continue;
		case TOKmulass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.TIMES_ASSIGN); continue;
		case TOKdivass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.DIVIDE_ASSIGN); continue;
		case TOKmodass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.REMAINDER_ASSIGN); continue;
		case TOKandass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.AND_ASSIGN); continue;
		case TOKorass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.OR_ASSIGN); continue;
		case TOKxorass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.XOR_ASSIGN); continue;
		case TOKshlass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.LEFT_SHIFT_ASSIGN); continue;
		case TOKshrass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.RIGHT_SHIFT_SIGNED_ASSIGN); continue;
		case TOKushrass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN); continue;
		case TOKcatass:  nextToken(); e2 = parseAssignExp(); e = new InfixExpression(e,e2,InfixExpression.Operator.CONCATENATE_ASSIGN); continue;
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

		// printf("Parser::parseExpression()\n");
		e = parseAssignExp();
		while (token.value == TOKcomma) {
			nextToken();
			e2 = parseAssignExp();
			e = new InfixExpression(e,e2,InfixExpression.Operator.COMMA);
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
			
			AggregateDeclaration cd = new AggregateDeclaration(ast);
			cd.setKind(AggregateDeclaration.Kind.CLASS);
			cd.setName(newSimpleNameForIdentifier(id));
			if (baseClasses != null) {
				cd.baseClasses().addAll(baseClasses);
			}

			if (token.value != TOKlcurly) {
				problem("{ members } expected for anonymous class", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
				cd.declarations().clear();
			} else {
				nextToken();
				List decl = parseDeclDefs(false);
				if (token.value != TOKrcurly) {
					problem("class member expected", IProblem.SEVERITY_ERROR, IProblem.MEMBERS_EXPECTED, token.ptr, token.len);
				}
				nextToken();
				cd.declarations().addAll(decl);
			}

			e = new NewAnonClassExp(thisexp, newargs, cd, arguments);

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
				
				DynamicArrayType dynamicArrayType = new DynamicArrayType(ast);
				dynamicArrayType.setComponentType((Type) TypeAdapter.getAdapter(t).getNext());
				
				t = dynamicArrayType;
			} else {
				problem("Need size of rightmost array", IProblem.SEVERITY_ERROR, IProblem.NEED_SIZE_OF_RIGHTMOST_ARRAY, index.startPosition, index.length);
				return new NullLiteral(ast);
			}
		} else if (TypeAdapter.getAdapter(t).getTY() == Tsarray) {
			StaticArrayType tsa = (StaticArrayType) t;
			Expression e2 = tsa.getSize();

			arguments = new ArrayList<Expression>();
			arguments.add(e2);
			
			DynamicArrayType dynamicArrayType = new DynamicArrayType(ast);
			dynamicArrayType.setComponentType(TypeAdapter.getAdapter(t).getNext());
			
			t = dynamicArrayType;
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
		e = new NewExpression(thisexp, newargs, t, arguments);
		return e;
	}
	
	private void addComment(ASTNode s, String blockComment) {
		addComment(s, blockComment, -1);
	}

	private void addComment(ASTNode s, String blockComment, int blockCommentStart) {
		// TODO MARS s.addComment(combineComments(blockComment, token.lineComment), blockComment == null ? - 1 : blockCommentStart);
	}
	
	public PrimitiveType newPrimitiveTypeFromCurrentToken(Token token) {
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
	
	private QualifiedName newQualifiedNameForCurrentToken(Name name) {
		QualifiedName qualifiedName = new QualifiedName(ast);
		qualifiedName.setQualifier(name);
		qualifiedName.setName(newSimpleNameForCurrentToken());
		qualifiedName.setSourceRange(name.getStartPosition(), token.ptr + token.len - name.getStartPosition());
		return qualifiedName;
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