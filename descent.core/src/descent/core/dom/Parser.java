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
import java.util.StringTokenizer;

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

class Parser extends Lexer {
	
	public final static boolean LTORARRAYDECL = true;
	
	public final static int PSsemi = 1;		// empty ';' statements are allowed
	public final static int PSscope = 2;	// start a new scope
	public final static int PScurly = 4;	// { } statement is required
	public final static int PScurlyscope = 8;	// { } starts a new scope

	private ModuleDeclaration md;
	private int inBrackets;	
	private LINK linkage = LINK.LINKd;
	
	AST ast;
	CompilationUnit compilationUnit;
	List<Comment> comments;
	List<Pragma> pragmas;
	private int lastDocCommentRead = 0;
	private boolean appendLeadingComments = true;

	public Parser(AST ast, String source) {
		this(ast, source, 0, source.length());
	}
	
	public Parser(AST ast, char[] source) {
		this(ast, source, 0, source.length);
	}
	
	public Parser(AST ast, String source, int offset, int length) {
		this(ast, source.toCharArray(), offset, length);
	}
	
	public Parser(AST ast, char[] source, int offset, 
			int length) {
		super(source, offset, length, 
				true /* tokenize comments */, 
				true /* tokenize pragmas */,
				false /* don't tokenize whitespace */, 
				true /* record line separators */);
		
		this.ast = ast;
		compilationUnit = new CompilationUnit(ast);
		comments = new ArrayList<Comment>();
		pragmas = new ArrayList<Pragma>();
		
		nextToken();
	}
	
	@Override
	public TOK nextToken() {
		TOK tok = super.nextToken();
		while(tok == TOK.TOKlinecomment || tok == TOK.TOKdoclinecomment ||
			  tok == TOK.TOKblockcomment || tok == TOK.TOKdocblockcomment ||
			  tok == TOK.TOKpluscomment || tok == TOK.TOKdocpluscomment) {
			
			Comment comment;
			switch(tok) {
			case TOKlinecomment:
				comment = new CodeComment(ast);
				comment.setKind(CodeComment.Kind.LINE_COMMENT);
				break;
			case TOKblockcomment:
				comment = new CodeComment(ast);
				comment.setKind(CodeComment.Kind.BLOCK_COMMENT);
				break;
			case TOKpluscomment:
				comment = new CodeComment(ast);
				comment.setKind(CodeComment.Kind.PLUS_COMMENT);
				break;
			case TOKdoclinecomment:
				comment = new DDocComment(ast);
				comment.setKind(CodeComment.Kind.LINE_COMMENT);
				break;
			case TOKdocblockcomment:
				comment = new DDocComment(ast);
				comment.setKind(CodeComment.Kind.BLOCK_COMMENT);
				break;
			case TOKdocpluscomment:
				comment = new DDocComment(ast);
				comment.setKind(CodeComment.Kind.PLUS_COMMENT);
				break;
			default:
				throw new IllegalStateException("Can't happen");
			}
			
			comment.setSourceRange(token.ptr, token.len);
			if (comment.isDDocComment()) {
				DDocComment ddocComment = (DDocComment) comment;
				ddocComment.setText(token.string);
			}
			
			comments.add(comment);
			attachCommentToCurrentToken(comment);
			
			tok = super.nextToken();
		}
		
		while(tok == TOK.TOKPRAGMA) {
			if (token.ptr == 0 && token.string.length() > 1 && token.string.charAt(1) == '!') {
				// Script line
				Pragma pragma = new Pragma(ast);
				pragma.setSourceRange(0, token.len);
				pragmas.add(pragma);
			} else {
				Pragma pragma = new Pragma(ast);
				pragma.setSourceRange(token.ptr, token.len);
				pragmas.add(pragma);
				
				// Let's see if it's correct
				StringTokenizer st = new StringTokenizer(token.string.substring(1));
				if (st.countTokens() != 3) {
					error("#line integer [\"filespec\"]\\n expected", IProblem.InvalidPragmaSyntax, token);
					setMalformed(pragma);
				} else {
					String value = st.nextToken();
					if (!"line".equals(value)) {
						error("#line integer [\"filespec\"]\\n expected", IProblem.InvalidPragmaSyntax, token);
						setMalformed(pragma);
					} else {
						value = st.nextToken();
						try {
							int num = Integer.parseInt(value);
							if (num <= 0) throw new NumberFormatException();
							
							value = st.nextToken();
							if (!"__FILE__".equals(value)) {
								if (value.length() < 2 || value.charAt(0) != '"' || value.charAt(value.length() - 1) != '"') {
									error("#line integer [\"filespec\"]\\n expected", IProblem.InvalidPragmaSyntax, token);
									setMalformed(pragma);
								}
							}
						} catch (NumberFormatException e) {
							error("#line integer [\"filespec\"]\\n expected", IProblem.InvalidPragmaSyntax, token);
							setMalformed(pragma);
						}
					}
				}
			}
			
			tok = nextToken();
		}
		
		return tok;
	}
	
	@Override
	protected void newline(boolean inComment) {
		super.newline(inComment);
		if (!inComment) {
			appendLeadingComments = false;
		}
	}
	
	private void attachCommentToCurrentToken(Comment comment) {
		if (!appendLeadingComments || !comment.isDDocComment()) return;
		
		prevToken.leadingComment = (DDocComment) comment;
	}
	
	@SuppressWarnings("unchecked")
	public List<Declaration> parseModule() {
	    List<Declaration> decldefs = new ArrayList<Declaration>();
	    List<DDocComment> moduleDocComments = getLastDocComments();

		// ModuleDeclation leads off
		if (token.value == TOKmodule) {
			int start = token.ptr;
			
			nextToken();
			if (token.value != TOKidentifier) {
				parsingErrorDeleteToken(prevToken);
				
				decldefs = parseDeclDefs(false, new ArrayList<Modifier>());
				if (token.value != TOKeof) {
					parsingErrorDeleteToken(token);
				}
				return decldefs;
			} else {
				Name name = newSimpleNameForCurrentToken();
				while (nextToken() == TOKdot) {
					nextToken();
					if (token.value != TOKidentifier) {
						parsingErrorInsertTokenAfter(prevToken, ";");
						
						decldefs = parseDeclDefs(false, new ArrayList<Modifier>());
						if (token.value != TOKeof) {
							parsingErrorDeleteToken(token);
						}
						return decldefs;
					}
					name = newQualifiedNameFromCurrentToken(name);
				}

				md = newModuleDeclaration(name);
				md.setSourceRange(start, token.ptr + token.len - start);
				md.preDDocs().addAll(moduleDocComments);
				adjustLastDocComment();
				compilationUnit.setModuleDeclaration(md);

				if (token.value != TOKsemicolon) {
					setMalformed(md);
					setRecovered(md.getName());
					parsingErrorInsertTokenAfter(prevToken, ";");
				} else {
					nextToken();
				}
				
				attachLeadingComments(md);
				adjustPossitionAccordingToComments(md, md.preDDocs(), md.getPostDDoc());
			}
		}

		decldefs = parseDeclDefs(false, new ArrayList<Modifier>());
		if (token.value != TOKeof) {
			parsingErrorDeleteToken(token);
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
		boolean[] isSingle = new boolean[1];
		
		decldefs = new ArrayList<Declaration>();
		do {
			List<DDocComment> lastComments = getLastDocComments();
			
			int start = token.ptr;
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
				s = parseInvariant();
				break;

			case TOKunittest:
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
					break;
				} else if (token.value == TOKimport) {
					s = parseImport(true);
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
							// We don't want the modifier to be as a modifier in the
							// modifier declaration
							modifiers.remove(modifiers.size() - 1);
						}
					}
				}
				break;

			case TOKextern:
				if (peek(token).value != TOKlparen) {
					modifier = newModifierFromCurrentToken();
					
					// goto Lstc;
					nextToken();
					
					syntax = new ModifierDeclaration.Syntax[1];
					tempObj = parseDeclDefs_Lstc2(a, isSingle, syntax, modifiers);
					a = (List) tempObj[0];
					s = (Declaration) tempObj[1];
					s.modifiers().add(modifier);
					break;
				}
				{
					LINK linksave = linkage;
					linkage = parseLinkage();
					a = parseBlock();
					s = newExternDeclaration(linkage, a);
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
				nextToken();
				
				syntax = new ModifierDeclaration.Syntax[1]; 
				a = parseBlock(isSingle, syntax, modifiers);
				if (a != null) {
					if (isSingle[0]) {	
						s = (Declaration) a.get(0);						
					} else {
						s = newModifierDeclaration(modifier, syntax[0], a);
						// We don't want the modifier to be as a modifier in the
						// modifier declaration
						modifiers.remove(modifiers.size() - 1);
					}
				} else {
					if (!isSingle[0]) {
						s = newModifierDeclaration(modifier, syntax[0], a);
						// We don't want the modifier to be as a modifier in the
						// modifier declaration
						modifiers.remove(modifiers.size() - 1);
					}
				}
				break;
				
			case TOKalign: {
				long n;

				s = null;
				nextToken();
				if (token.value == TOKlparen) {
					nextToken();
					if (token.value == TOKint32v)
						n = token.numberValue.intValue();
					else {
						parsingErrorInsertTokenAfter(prevToken, "Integer");
						n = 1;
					}
					nextToken();
					check(TOKrparen);
				} else {
					n = global.structalign; // default
				}
				a = parseBlock();
				s = newAlignDeclaration((int) n, a);
				break;
			}

			case TOKpragma: {
				Identifier ident;
				List<Expression> args = null;

				nextToken();
				check(TOKlparen);
				if (token.value != TOKidentifier) {
					parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
				break;
			}

			case TOKdebug:
				nextToken();
				if (token.value == TOKassign) {
					nextToken();
					if (token.value == TOKidentifier || token.value == TOKint32v) {
						s = newDebugAssignmentForCurrentToken();
					} else {
						parsingErrorInsertTokenAfter(prevToken, "Identifier or Integer");
						s = null;
					}
					nextToken();
					if (token.value != TOKsemicolon) {
						parsingErrorInsertTokenAfter(prevToken, ";");
					}
					nextToken();
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
				break;

			case TOKversion:
				nextToken();
				if (token.value == TOKassign) {
					nextToken();
					if (token.value == TOKidentifier || token.value == TOKint32v) {
						s = newVersionAssignmentForCurrentToken();
					} else {
						parsingErrorInsertTokenAfter(prevToken, "Identifier or Integer");
						s = null;
					}
					nextToken();
					if (token.value != TOKsemicolon) {
						parsingErrorInsertTokenAfter(prevToken, ";");
					}
					nextToken();
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
				break;

			case TOKiftype:				
				IftypeCondition iftypeCondition = parseIftypeCondition();
				
				a = parseBlock();
				aelse = null;
				if (token.value == TOKelse) {
					nextToken();
					aelse = parseBlock();
				}				

				s = newIftypeDeclaration(iftypeCondition, a, aelse);
				break;

			case TOKsemicolon: // empty declaration
				nextToken();
				continue;

			default:
				parsingErrorDeleteToken(token);
				nextToken();
				continue;
			}
			if (s != null) {
				s.setSourceRange(start, prevToken.ptr + prevToken.len - start);
				s.modifiers().addAll(modifiers);
				modifiers.clear();
				s.preDDocs().addAll(lastComments);
				adjustLastDocComment();
				attachLeadingComments(s);				
				adjustPossitionAccordingToComments(s, s.preDDocs(), s.getPostDDoc());
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
	
	private Object[] parseDeclDefs_Lstc2(List<Declaration> a, boolean[] isSingle, ModifierDeclaration.Syntax[] syntax, List<Modifier> modifiers) {
		Token firstToken = new Token(prevToken);
		
		boolean repeat = true;
		while(repeat) {
			switch (token.value)
			{
			    case TOKconst:	  modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.CONST_KEYWORD)); nextToken(); break;
			    case TOKfinal:	  modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.FINAL_KEYWORD)); nextToken(); break;
			    case TOKauto:	  modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.AUTO_KEYWORD)); nextToken(); break;
			    case TOKscope:	  modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.SCOPE_KEYWORD)); nextToken(); break;
			    case TOKoverride:	  modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.OVERRIDE_KEYWORD)); nextToken(); break;
			    case TOKabstract:	  modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.ABSTRACT_KEYWORD)); nextToken(); break;
			    case TOKsynchronized: modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD)); nextToken(); break;
			    case TOKdeprecated:   modifiers.add(newModifierFromTokenAndKeyword(token, Modifier.ModifierKeyword.DEPRECATED_KEYWORD)); nextToken(); break;
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
		    
		    check(TOKsemicolon);
		    
		    attachLeadingComments(variableDeclaration);
		    adjustPossitionAccordingToComments(variableDeclaration, variableDeclaration.preDDocs(), variableDeclaration.getPostDDoc());
		    return new Object[] { a, variableDeclaration }; 
		}
		else
		{   
			a = parseBlock(isSingle, syntax, modifiers);
			
			if (isSingle[0]) {
				if (a.size() == 0) {
					parsingErrorDeleteToken(firstToken);
					return new Object[] { a, null };
				} else {
					return new Object[] { a, a.get(0) };
				}
			} else {
				
				//ModifierDeclaration modifierDeclaration = newModifierDeclaration(modifiers.get(modifiers.size() - 1), syntax[0], a);				
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
			parsingErrorInsertToComplete(prevToken, "Declaration", "Declaration");
			nextToken();
			break;

		case TOKlcurly:
			nextToken();
			if (syntax != null) {
				syntax[0] = ModifierDeclaration.Syntax.CURLY_BRACES;
			}
			a = parseDeclDefs(false, new ArrayList<Modifier>());
			if (token.value != TOKrcurly) { /* { */
				parsingErrorInsertTokenAfter(prevToken, "}");
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
			a = parseDeclDefs(false, new ArrayList<Modifier>()); // grab declarations up to closing curly
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
			int lineNumber = token.lineNumber;

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
				error("Valid linkage identifiers are D, C, C++, Pascal, Windows", IProblem.InvalidLinkageIdentifier, lineNumber, id.startPosition, id.length);
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
				parsingErrorInsertTokenAfter(prevToken, "Identifier or Integer");
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
				parsingErrorInsertTokenAfter(prevToken, "Identifier or Integer");
			}
			nextToken();
			check(TOKrparen);
		} else {
			parsingErrorInsertToComplete(prevToken, "(condition)", "VersionDeclaration");
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
			parsingErrorInsertToComplete(prevToken, "(expression)", "StaticIfDeclaration");
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
			parsingErrorInsertToComplete(prevToken, "(type identifier : specialization)", "IftypeDeclaration");
			return null;
		}

		error(
				"iftype(condition) is deprecated, use static if (is(condition))",
				IProblem.IftypeDeprecated, firstToken.lineNumber,
				firstToken.ptr, firstToken.len);

		return new IftypeCondition(targ, ident[0], tok, tspec);
	}
	
	private FunctionDeclaration parseCtor() {
		int start = token.ptr;
		
		SimpleName name = newSimpleNameForCurrentToken();
		
		int[] varargs = new int[1];
	    nextToken();
	    List<Argument> arguments = parseParameters(varargs);
	    FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.CONSTRUCTOR, null, name, arguments, varargs[0]);
	    parseContracts(f);
	    f.setSourceRange(start, prevToken.ptr + prevToken.len - start);
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
		name.internalSetIdentifier("~this");
		
		FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.DESTRUCTOR, null, name, null, 0);
	    parseContracts(f);
	    f.setSourceRange(firstToken.ptr, prevToken.ptr + prevToken.len - firstToken.ptr);
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
		name.internalSetIdentifier("~this");
		
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
		int start = token.ptr;
		
		SimpleName name = newSimpleNameForCurrentToken();
				
		nextToken();
		int[] varargs = new int[1];
		List<Argument> arguments = parseParameters(varargs);
		
		FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.NEW, null, name, arguments, varargs[0]);
	    parseContracts(f);
	    f.setSourceRange(start, prevToken.ptr + prevToken.len - start);
	    return f;
	}
	
	private FunctionDeclaration parseDelete() {
		int start = token.ptr;
		int startLine = token.lineNumber;
		
		SimpleName name = newSimpleNameForCurrentToken();
		
		nextToken();
		int[] varargs = new int[1];
		List<Argument> arguments = parseParameters(varargs);
		
		if (varargs[0] != 0) {
	    	error("... not allowed in delete function parameter list", 
	    			
	    			IProblem.VariadicNotAllowedInDelete, 
	    			startLine, name);
	    }
		
		FunctionDeclaration f = newFunctionDeclaration(FunctionDeclaration.Kind.DELETE, null, name, arguments, varargs[0]);
		f.arguments().addAll(arguments);
	    parseContracts(f);
	    f.setSourceRange(start, prevToken.ptr + prevToken.len - start);
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
			inout = Argument.PassageMode.DEFAULT;
			
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
						parsingErrorInsertTokenAfter(prevToken, "default argument");
					}
				}
				if (token.value == TOKdotdotdot) { 
					/*
					 * This is: at ai ...
					 */
					if (inout == Argument.PassageMode.OUT || inout == Argument.PassageMode.INOUT) {
						error("Variadic argument cannot be out or inout", IProblem.VariadicArgumentCannotBeOutOrInout, inoutToken.lineNumber, inoutToken.ptr, inoutToken.len);
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
		
		if (token.value == TOKsemicolon && id != null) {
			e.setSourceRange(enumToken.ptr, token.ptr + token.len - enumToken.ptr);
			nextToken();			  
		} else if (token.value == TOKlcurly) {
			nextToken();
			while (token.value != TOKrcurly) {
				if (token.value == TOKeof) {
					error("Enum declaration is invalid", IProblem.EnumDeclarationIsInvalid, enumToken.lineNumber, enumToken.ptr, enumToken.len);
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
						check(TOKcomma);
					}
				} else {
					parsingErrorInsertToComplete(prevToken, "EnumMember", "EnumDeclaration");
					nextToken();
				}
			}
			e.setSourceRange(enumToken.ptr, token.ptr + token.len - enumToken.ptr);
			
			nextToken();
		} else {
			error("Enum declaration is invalid", IProblem.EnumDeclarationIsInvalid, enumToken.lineNumber, enumToken.ptr, enumToken.len);
		}
		
		return e;
	}
	
	@SuppressWarnings("unchecked")
	private AggregateDeclaration parseAggregate() {
		AggregateDeclaration a = null;
		Identifier id;
		List<BaseClass> baseClasses = null;
		List<TemplateParameter> tpl = null;
		
		Token firstToken = new Token(token);
		
		nextToken();
		if (token.value != TOKidentifier) {
			// Change from DMD
			id = null;
		} else {
			id = new Identifier(token);
			nextToken();
			if (token.value == TOKlparen) {
				// Gather template parameter list
				tpl = parseTemplateParameterList();
			}
		}

		switch (firstToken.value) {
		case TOKclass:
		case TOKinterface:
			if (id == null) {
				parsingErrorInsertTokenAfter(firstToken, "Identifier");
			}

			// Collect base class(es)
			if (token.value == TOKcolon) {
				nextToken();				
				baseClasses = parseBaseClasses();				
			}
			break;			
		case TOKstruct:
		case TOKunion:
			break;

		default:
			throw new IllegalStateException("Can't happen");
		}
		
		if (token.value == TOKsemicolon) {
			a = newAggregateDeclaration(firstToken.value, id, baseClasses, tpl);
			nextToken();
		} else if (token.value == TOKlcurly) {
			a = newAggregateDeclaration(firstToken.value, id, baseClasses, tpl);
			
			nextToken();
			List decl = parseDeclDefs(false, new ArrayList<Modifier>());
			if (token.value != TOKrcurly) {
				parsingErrorInsertTokenAfter(prevToken, "}");
			}
			
			a.declarations().addAll(decl);
			
			nextToken();			
		} else {
			if (id == null) {
				// A single "class" makes no declaration
				if (firstToken.value == TOKstruct || firstToken.value == TOKunion) {
					String word = toWord(firstToken.value.toString());
					parsingErrorInsertToComplete(firstToken,  word + "Body", word + "Declaration");
				}
				a = null;
			} else {
				// We've got at least "class Identifier", make a declaration out of it
				String word = toWord(firstToken.value.toString());
				parsingErrorInsertToComplete(prevToken,  word + "Body", word + "Declaration");
				a = newAggregateDeclaration(firstToken.value, id, baseClasses, tpl);
			}		
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
				parsingErrorInsertTokenAfter(prevToken, "Type");
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
	    	parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
	    	parsingErrorInsertToComplete(prevToken, "TemplateBodyDeclaration", "TemplateDeclaration");
			//goto Lerr;
	    	return null;
	    }
	    else
	    {
		nextToken();
		decldefs = parseDeclDefs(false, new ArrayList<Modifier>());
		if (token.value != TOKrcurly)
		{
			parsingErrorInsertToComplete(prevToken, "TemplateBodyDeclaration", "TemplateDeclaration");
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
			parsingErrorInsertToComplete(prevToken, "Parenthesized TemplateParameterList", "TemplateDeclaration");
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
						parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
						parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
			    		error("Variadic template parameter must be last one", IProblem.VariadicTemplateParameterMustBeTheLastOne, 
			    				t.lineNumber, token.ptr, 
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
						error("No identifier for template value parameter", IProblem.NoIdentifierForTemplateValueParameter, t.lineNumber, t.ptr, t.len);
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
				tp.setSourceRange(firstToken.ptr, prevToken.ptr + prevToken.len - firstToken.ptr);
				
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
				parsingErrorDeleteToken(prevToken);
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
				parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
			parsingErrorInsertTokenAfter(prevToken, ";");
		} else {
			nextToken();
		}
		
		tm.setSourceRange(firstToken.ptr, prevToken.ptr + prevToken.len - firstToken.ptr);

		return tm;
	    
	    // Lerr:
	    // return NULL;
	}

	@SuppressWarnings("unchecked")
	private List<ASTNode> parseTemplateArgumentList() {
	    List<ASTNode> tiargs = new ArrayList<ASTNode>();
	    if (token.value != TOKlparen)
	    {   
	    	parsingErrorInsertToComplete(prevToken, "!(TemplateArgumentList)", "TemplateType");
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
	    check(TOKrparen);
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
					parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
						parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
					do {
						SelectiveImport selectiveImport = newSelectiveImport();
						
						nextToken();

						if (token.value != TOKidentifier) {
							parsingErrorInsertTokenAfter(prevToken, "Identifier");
							break;
						}
						
						alias = newSimpleNameForCurrentToken();

						nextToken();
						if (token.value == TOKassign) {
							nextToken();
							if (token.value != TOKidentifier) {
								parsingErrorInsertTokenAfter(prevToken, "Identifier");
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

		check(TOKsemicolon);

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
			parsingErrorInsertTokenAfter(prevToken, "Type");
			break;
		}
		return t;
	}

	private void parseBasicType_Lident2(Identifier[] id, Type[] tid, Type[] t, int start) {
		while (token.value == TOKdot) {
			nextToken();
			if (token.value != TOKidentifier) {
				parsingErrorInsertTokenAfter(prevToken, "Identifier");
				break;
			}
			id[0] = new Identifier(token);
			nextToken();
			if (token.value == TOKnot) {
				nextToken();
				
				List<ASTNode> arguments = parseTemplateArgumentList();
				TemplateType templateType = newTemplateType(id[0], arguments);
				templateType.setSourceRange(start, prevToken.ptr + prevToken.len - start);
				
				tid[0] = newQualifiedType(tid[0], templateType, start);
				tid[0].setSourceRange(start, prevToken.ptr + prevToken.len - start);
			} else {
				tid[0] = newQualifiedType(tid[0], id[0], start);
			}
		}
		tid[0].setSourceRange(start, prevToken.ptr + prevToken.len - start);
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
					    t.setSourceRange(subType.getStartPosition(), token.ptr + token.len - subType.getStartPosition());
						
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

				int saveStart = t.getStartPosition();

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
		// This may happen if a basic type was not find
		if (t == null) {
			return null;
		}
		
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
		    	error("Unexpected identifier in declarator", IProblem.UnexpectedIdentifierInDeclarator, token.lineNumber, token.ptr, token.len);
		    }
		    ts = t;
		    nextToken();
		    break;

		case TOKlparen:
			int oldStart = t.getStartPosition();
		    nextToken();
		    ts = parseDeclarator(t, pident, null, identStart);
		    ts.setSourceRange(oldStart, token.ptr + token.len - oldStart);
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
			    ta.setSourceRange(t.getStartPosition(), token.ptr + token.len - ta.getStartPosition());
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
			ta.setSourceRange(t.getStartPosition(), t.getLength());
			
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
	
	@SuppressWarnings("unchecked")
	private List<Declaration> parseDeclarations() {
		Type ts;
		Type t;
		Type tfirst;
		Identifier ident;
		List a;
		TOK tok;
		LINK link = linkage;
		
		List<DDocComment> lastComments = getLastDocComments();
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
						error("Redundant storage class", IProblem.RedundantStorageClass, token.lineNumber, currentModifier);
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
							error("Redundant storage class", IProblem.RedundantStorageClass, token.lineNumber, currentModifier);
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
				variableDeclaration.preDDocs().addAll(lastComments);
				adjustLastDocComment();
			} else {
				parsingErrorInsertTokenAfter(prevToken, ";");
			}
			
			attachLeadingComments(variableDeclaration);
			adjustPossitionAccordingToComments(variableDeclaration, variableDeclaration.preDDocs(), variableDeclaration.getPostDDoc());
			
			return a;
		}

		if (token.value == TOKclass) {
			AggregateDeclaration s;

			s = (AggregateDeclaration) parseAggregate();
			s.modifiers().addAll(modifiers);
			a.add(s);
			s.preDDocs().addAll(lastComments);
			adjustLastDocComment();
			return a;
		}
		
		int nextVarStart = token.ptr;
		int nextTypdefOrAliasStart = firstToken.ptr;

		ts = parseBasicType();
		if (ts == null) {
			return a;
		}
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
				error("Multiple declarations must have the same type", IProblem.MultipleDeclarationsMustHaveTheSameType,
						 ident.lineNumber, ident.startPosition, ident.length);
			}
			if (ident == null) {
				parsingErrorInsertTokenAfter(prevToken, "Identifier");
				return a;
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
					} else {
						addDelcaration = false;
					}
					
					typedefDeclaration.fragments().add(newTypedefDeclarationFragment(ident, init));
					v = typedefDeclaration;
				} else {
					if (init != null) {
						error("Alias cannot have initializer", IProblem.AliasCannotHaveInitializer, tokAssign.lineNumber, tokAssign.ptr,  init.getStartPosition() + init.getLength() - tokAssign.ptr);
					}
					
					if (aliasDeclaration == null) {
						aliasDeclaration = newAliasDeclaration(t);
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
					v.setSourceRange(nextTypdefOrAliasStart, token.ptr + token.len - nextTypdefOrAliasStart);
					nextToken();
					v.preDDocs().addAll(lastComments);
					adjustLastDocComment();
					attachLeadingComments(v);				
					adjustPossitionAccordingToComments(v, v.preDDocs(), v.getPostDDoc());
					break;

				case TOKcomma:
					v.setSourceRange(nextTypdefOrAliasStart, prevToken.ptr + prevToken.len - nextTypdefOrAliasStart);
					nextToken();
					continue;

				default:
					parsingErrorInsertTokenAfter(prevToken, ";");
					break;
				}
			} else if (TypeAdapter.getAdapter(t).getTY() == Tfunction) {
				DmdTypeFunction typeFunction = (DmdTypeFunction) t;
				
				SimpleName name = newSimpleNameForIdentifier(ident);
				
				FunctionDeclaration function = newFunctionDeclaration(FunctionDeclaration.Kind.FUNCTION, 
						typeFunction.getReturnType(), name, typeFunction.getArguments(), typeFunction.varargs ? 1 : 0);
				function.preDDocs().addAll(lastComments);
				adjustLastDocComment();
				
				parseContracts(function);
				function.setSourceRange(t.getStartPosition(), prevToken.ptr + prevToken.len - t.getStartPosition());
				
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
				s.preDDocs().addAll(lastComments);
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
				} else {
					addDelcaration = false;
				}
				
				variableDeclaration.fragments().add(newVariableDeclarationFragment(ident, init));
				
				if (addDelcaration) {
					a.add(variableDeclaration);
				}
				switch (token.value) {
				case TOKsemicolon:
					variableDeclaration.setSourceRange(nextVarStart, token.ptr + token.len - nextVarStart);
					nextToken();
					variableDeclaration.preDDocs().addAll(lastComments);
					adjustLastDocComment();
					attachLeadingComments(variableDeclaration);
					adjustPossitionAccordingToComments(variableDeclaration, variableDeclaration.preDDocs(), variableDeclaration.getPostDDoc());
					break;

				case TOKcomma:
					variableDeclaration.setSourceRange(nextVarStart, prevToken.ptr + prevToken.len - nextVarStart);
					nextToken();
					continue;

				default:
					parsingErrorInsertTokenAfter(prevToken, ";");
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
					parsingErrorInsertToComplete(prevToken, "body { ... }", "FunctionDeclaration");
				}
				f.setBody(parseStatement(PSsemi));
				//f.length = prevToken.ptr + prevToken.len - f.startPosition;
				break;

			case TOKbody:
				nextToken();
				f.setBody(parseStatement(PScurly));
				//f.length = prevToken.ptr + prevToken.len - f.startPosition;
				break;

			case TOKsemicolon:
				if (f.getPrecondition() != null || f.getPostcondition() != null) {
					parsingErrorInsertToComplete(prevToken, "body { ... }", "FunctionDeclaration");
				}
				//f.length = token.ptr + token.len - f.startPosition;
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
					error("Redundant 'in' statement", IProblem.RedundantInStatement,
							token);
				}
				nextToken();
				
				f.setPrecondition(parseStatement(PScurly | PSscope));
				repeat = true;
				break;

			case TOKout:
				// parse: out (identifier) { statement }
				
				if (f.getPostcondition() != null) {
					error("Redundant 'out' statement", IProblem.RedundantOutStatement,
							token.lineNumber, token.ptr, token.len);
				}
				
				nextToken();
				if (token.value != TOKlcurly) {
					check(TOKlparen);
					if (token.value != TOKidentifier) {
						parsingErrorInsertTokenAfter(prevToken, "Identifier");
					} else {
						f.setPostconditionVariableName(newSimpleNameForCurrentToken());
						nextToken();
					}
					
					check(TOKrparen);
				}
				
				f.setPostcondition(parseStatement(PScurly | PSscope));
				repeat = true;
				break;

			default:
				parsingErrorInsertTokenAfter(prevToken, ";");
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
						parsingErrorInsertTokenAfter(prevToken, ",");
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
					is.setSourceRange(saveToken.ptr, token.ptr + token.len - saveToken.ptr);
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
						parsingErrorInsertTokenAfter(prevToken, ",");
						nextToken();
						ia.setSourceRange(saveToken.ptr, token.ptr + token.len - saveToken.ptr);
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
						parsingErrorInsertTokenAfter(prevToken, ",");
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
					ia.setSourceRange(saveToken.ptr, token.ptr + token.len - saveToken.ptr);
					nextToken();					
					break;

				case TOKeof:
					parsingErrorInsertTokenAfter(prevToken, "ArryaInitializer");
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
			error("Statement expected to be { }",
					IProblem.StatementExpectedToBeCurlies, token);
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
				check(TOKsemicolon);
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
			check(TOKsemicolon);
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
					parsingErrorInsertTokenAfter(prevToken, "}");
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
				error("Use '{ }' for an empty statement, not a ';'", IProblem.UseBracesForAnEmptyStatement, token);
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
				check(TOKsemicolon);
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

				inout = Argument.PassageMode.DEFAULT;
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
				
				int lineNumber = token.lineNumber;
				at = parseDeclarator(tb, pointer2_ai);
				ai = pointer2_ai[0];
				if (ai == null) {
					error("No identifier for declarator", IProblem.NoIdentifierForDeclarator, lineNumber, at);
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
						arg = newArgument(Argument.PassageMode.DEFAULT, null, token.ident, null);
						arg.setSourceRange(autoToken.ptr, token.ptr + token.len - autoToken.ptr);
						
						nextToken();
						nextToken();
					} else {
						parsingErrorInsertTokenAfter(prevToken, "=");
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
					parsingErrorInsertTokenAfter(prevToken, "Identifier");
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

					arg = newArgument(Argument.PassageMode.DEFAULT, at, ai, null);
					arg.setSourceRange(argToken.ptr, prevToken.ptr + prevToken.len - argToken.ptr);
					
					check(TOKassign);					
				}

				// Check for " ident;"
				else if (token.value == TOKidentifier) {
					Token t2 = peek(token);
					if (t2.value == TOKcomma || t2.value == TOKsemicolon) {
						arg = newArgument(Argument.PassageMode.DEFAULT, null, token.ident, null);
						arg.setSourceRange(argToken.ptr, token.ptr + token.len - argToken.ptr);
						
						nextToken();
						nextToken();
						
						// if (!global.params.useDeprecated)
						error("if (v; e) is deprecated, use if (auto v = e)", IProblem.IfAutoDeprecated, argToken.lineNumber, argToken.ptr, token.ptr + token.len - argToken.ptr);
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
		    	Modifier modifier = newModifierFromCurrentToken();
				Statement[] ps = { s };
				parseStatement_Ldeclaration(ps, flags);
				s = ps[0];
				if (s instanceof DeclarationStatement) {
					((DeclarationStatement) s).getDeclaration().modifiers().add(modifier);
				}
				break;
		    }
			
			nextToken();
			check(TOKlparen);
			if (token.value != TOKidentifier) {
				parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
					error("Valid scope identifiers are exit, failure, or success", IProblem.InvalidScopeIdentifier, token);
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
			error(token.toString() + " is deprecated, use scope", IProblem.OnScopeDeprecated, token);
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
				parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
			check(TOKsemicolon);
			
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
			check(TOKsemicolon);
			
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
			
			check(TOKsemicolon);
			
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
					parsingErrorInsertTokenAfter(prevToken, "Identifier");
					ident = null;
				} else {
					ident = new Identifier(token);
					nextToken();
				}
				
				s = newGotoStatement(ident);
			}
			check(TOKsemicolon);
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
				parsingErrorInsertToComplete(prevToken, "Catch or finally", "TryStatement");
			} else {
				s = newTryStatement(body, catches, finalbody);
			}
			break;
		}

		case TOKthrow: {
			Expression exp;
			
			nextToken();
			exp = parseExpression();
			check(TOKsemicolon);
			
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
			List<Token> toklist = new ArrayList<Token>();
			
			// Parse the asm block into a sequence of AsmStatements,
			// each AsmStatement is one instruction.
			// Separate out labels.
			// Defer parsing of AsmStatements until semantic processing.

			nextToken();
			check(TOKlcurly);
			label = null;
			statements = new ArrayList<Statement>();
			while (true) {
				switch (token.value) {
				case TOKidentifier:
					if (toklist.isEmpty()) {
						// Look ahead to see if it is a label
						t = peek(token);
						if (t.value == TOKcolon) { // It's a label
							label = new Identifier(token);
							nextToken();
							nextToken();
							continue;
						}
					}
					// goto Ldefault;
					toklist.add(new Token(token));
					nextToken();
					continue;

				case TOKrcurly:
					if (!toklist.isEmpty() || label != null) {
						parsingErrorInsertTokenAfter(prevToken, ";");
					}
					break;

				case TOKsemicolon:
					s = null;
					// Create AsmStatement from list of tokens we've saved
					s = newAsmStatement(ast, toklist);
					if (toklist.isEmpty()) {
						s.setSourceRange(token.ptr, token.len);
					} else {
						s.setSourceRange(toklist.get(0).ptr, token.ptr + token.len - toklist.get(0).ptr);
					}
					
					toklist.clear();
					
					if (label != null) {
						s = newLabelStatement(label, s);
						label = null;
					}
					statements.add(s);
					nextToken();
					continue;

				case TOKeof:
					/* { */
					parsingErrorInsertTokenAfter(prevToken, "}");
					break;

				default:
					toklist.add(new Token(token));
					nextToken();
					continue;
				}
				break;
			}
			
			s = newAsmBlock(statements);;
			
			nextToken();
			break;
		}

		default:
			parsingErrorInsertTokenAfter(prevToken, "Statement");
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
			// Changed from DMD... TODO support better syntax recovery and remove it!
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
			parsingErrorInsertTokenAfter(prevToken, value.toString());
		} else {
			nextToken();
		}
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
		    	error("'$' is valid only inside [] of index or slice", IProblem.DollarInvalidOutsideBrackets, token);
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
		    e = newSuperLiteralForCurrentToken();
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
			int startLine = token.lineNumber;
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
							error("Mismatched string literal postfixes '" + (char) postfix + "' and '" + (char) token.postfix + "'",
									IProblem.MismatchedStringLiteralPostfixes,
									startLine, stringLiteral.getStartPosition(), token.ptr + token.len - stringLiteral.getStartPosition());
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
			    check(TOKdot);
			    if (token.value != TOKidentifier)
			    {
			    	parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
		    	check(TOKdot);
			    if (token.value != TOKidentifier)
			    {   
			    	parsingErrorInsertTokenAfter(prevToken, "Identifier");
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
		    check(TOKlparen);
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
				parsingErrorInsertToComplete(prevToken, "(type identifier : specialization)", "IftypeDeclaration");
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
			check(TOKlparen);
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
			e.setSourceRange(start, end - start);
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
			parsingErrorInsertTokenAfter(prevToken, "Expression");
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
		int start = e == null ? prevToken.ptr : e.getStartPosition();
		
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
					parsingErrorInsertTokenAfter(prevToken, "Identifier");
				}
				break;

			case TOKplusplus:
				e = newPostfixExpression(e, PostfixExpression.Operator.INCREMENT);
				e.setSourceRange(start, prevToken.ptr + prevToken.len - start);
				break;

			case TOKminusminus:
				e = newPostfixExpression(e, PostfixExpression.Operator.DECREMENT);
				e.setSourceRange(start, prevToken.ptr + prevToken.len - start);
				break;

			case TOKlparen:
				e = newCallExpression(e, parseArguments());
				e.setSourceRange(start, prevToken.ptr + prevToken.len - start);
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
				
				e.setSourceRange(start, prevToken.ptr + prevToken.len - start);
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
							parsingErrorInsertTokenAfter(prevToken, "Identifier");
							// Change from DMD
							e = newTypeDotIdentifierExpression(t);
							e.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
							return e;
						}
						e = newTypeDotIdentifierExpression(t, token);
						e.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
						nextToken();
					} else {
						e = parseUnaryExp();
						e = newCastExpression(e, t);
						e.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);
						error("C style cast illegal, use cast(...)", IProblem.CStyleCastIllegal, firstToken.lineNumber, firstToken.ptr, prevToken.ptr + prevToken.len - firstToken.ptr);
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

		e.setSourceRange(saveToken.ptr, prevToken.ptr + prevToken.len - saveToken.ptr);

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
		    	error("'===' is no longer legal, use 'is' instead",
		    			IProblem.ThreeEqualsIsNoLongerLegal, token.lineNumber, token.ptr, token.len);
			//goto L1;
			nextToken();
			e2 = parseRelExp();
			e = newInfixExpression(e,e2,InfixExpression.Operator.IDENTITY);
			continue;

		    case TOKnotidentity:
			//if (!global.params.useDeprecated)
		    	error("'!==' is no longer legal, use 'is' instead",
		    			IProblem.NotTwoEqualsIsNoLongerLegal, token.lineNumber, token.ptr, token.len);
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
		case TOKassign:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.ASSIGN); continue;
		case TOKaddass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.PLUS_ASSIGN); continue;
		case TOKminass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.MINUS_ASSIGN); continue;
		case TOKmulass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.TIMES_ASSIGN); continue;
		case TOKdivass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.DIVIDE_ASSIGN); continue;
		case TOKmodass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.REMAINDER_ASSIGN); continue;
		case TOKandass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.AND_ASSIGN); continue;
		case TOKorass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.OR_ASSIGN); continue;
		case TOKxorass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.XOR_ASSIGN); continue;
		case TOKshlass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.LEFT_SHIFT_ASSIGN); continue;
		case TOKshrass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN); continue;
		case TOKushrass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN); continue;
		case TOKcatass:  nextToken(); e2 = parseAssignExp(); e = newAssignment(e,e2,Assignment.Operator.CONCATENATE_ASSIGN); continue;
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
			
			AggregateDeclaration cd = newAggregateDeclaration(TOKclass, id, baseClasses, null);

			if (token.value != TOKlcurly) {
				parsingErrorInsertToComplete(prevToken, "{ members }", "AnnonymousClassDeclaration");
				cd.declarations().clear();
			} else {
				nextToken();
				List decl = parseDeclDefs(false, new ArrayList<Modifier>());
				if (token.value != TOKrcurly) {
					parsingErrorInsertToComplete(prevToken, "ClassBody", "AnnonymousClassDeclaration");
				}
				nextToken();
				cd.declarations().addAll(decl);
			}

			e = newNewAnonymousClassExpression(ast, thisexp, newargs, cd, arguments);

			return e;
		}

		// #if LTORARRAYDECL
		int lineNumber = token.lineNumber;
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
				error("Need size of rightmost array", IProblem.NeedSizeOfRightmostArray, lineNumber, index);
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
		postfixExpression.setOperand(expression);
		postfixExpression.setOperator(operator);
		return postfixExpression;
	}
	
	private PrefixExpression newPrefixExpression(Expression expression, PrefixExpression.Operator operator) {
		PrefixExpression prefixExpression = new PrefixExpression(ast);
		prefixExpression.setOperand(expression);
		prefixExpression.setOperator(operator);
		return prefixExpression;
	}
	
	private SimpleName newSimpleNameForCurrentToken() {
		SimpleName simpleName = new SimpleName(ast);
		simpleName.internalSetIdentifier(token.ident.string);
		simpleName.setSourceRange(token.ptr, token.len);
		return simpleName;
	}
	
	private SimpleName newSimpleNameForToken(Token token) {
		SimpleName simpleName = new SimpleName(ast);
		simpleName.internalSetIdentifier(token.ident.string);
		simpleName.setSourceRange(token.ptr, token.len);
		return simpleName;
	}
	
	public SimpleName newSimpleNameForIdentifier(Identifier id) {
		if (id == null) return null;
		
		SimpleName simpleName = new SimpleName(ast);
		simpleName.internalSetIdentifier(id.string);
		simpleName.setSourceRange(id.startPosition, id.length);
		return simpleName;
	}
	
	public static SimpleName newSimpleNameForIdentifierWithAST(Identifier id, AST ast) {
		if (id == null) return null;
		
		SimpleName simpleName = new SimpleName(ast);
		simpleName.internalSetIdentifier(id.string);
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
			number.internalSetEscapedValue(token.string);
		}
		number.setSourceRange(token.ptr, token.len);
		return number;
	}
	
	private StringLiteral newStringLiteralForCurrentToken() {
		StringLiteral string = new StringLiteral(ast);
		string.internalSetEscapedValue(token.string);
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
			enumMember.setSourceRange(name.getStartPosition(), name.getLength());
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
	
	private Assignment newAssignment(Expression e1, Expression e2, Assignment.Operator operator) {
		Assignment assignment = new Assignment(ast);
		assignment.setLeftHandSide(e1);
		assignment.setOperator(operator);
		assignment.setRightHandSide(e2);
		assignment.setSourceRange(e1.getStartPosition(), e2.getStartPosition() + e2.getLength() - e1.getStartPosition());
		return assignment;
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
	
	private AggregateDeclaration newAggregateDeclaration(TOK tok, Identifier id, List<BaseClass> baseClasses, List<TemplateParameter> templateParameters) {
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
		if (templateParameters != null) {
			classDeclaration.templateParameters().addAll(templateParameters);
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
		if (modifier == null) {
			baseClass.setSourceRange(type.getStartPosition(), type.getLength());
		} else {
			baseClass.setModifier(modifier);
			baseClass.setSourceRange(modifier.getStartPosition(), type.getStartPosition() + type.getLength() - modifier.getStartPosition());
		}
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
	
	private AsmBlock newAsmBlock(List<Statement> statements) {
		AsmBlock asmBlock = new AsmBlock(ast);
		if (statements != null) {
			asmBlock.statements().addAll(statements);
		}
		return asmBlock;
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
	
	private SwitchCase newCaseStatement(Expression expression, Statement body) {
		SwitchCase caseStatement = new SwitchCase(ast);
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
			version.internalSetValue(String.valueOf(token.numberValue));
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
		version.internalSetValue(id.string);
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
		
		// This is because otherwise setComponentType fails because
		// componentType may have a parent already
		ASTNode parent = componentType.getParent();
		if (parent != null) {
			parent.setStructuralProperty(componentType.getLocationInParent(), new PrimitiveType(ast));
		}
		
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
	
	private LabeledStatement newLabelStatement(Identifier ident, Statement body) {
		LabeledStatement labelStatement = new LabeledStatement(ast);
		labelStatement.setLabel(newSimpleNameForIdentifier(ident));
		labelStatement.setBody(body);
		labelStatement.setSourceRange(ident.startPosition, body.getStartPosition() + body.getLength() - ident.startPosition);
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
	
	private StaticArrayType newStaticArrayType(Type componentType, Expression size) {
		StaticArrayType staticArrayType = new StaticArrayType(ast);
		staticArrayType.setComponentType(componentType);
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
	
	private SuperLiteral newSuperLiteralForCurrentToken() {
		SuperLiteral superLiteral =	new SuperLiteral(ast);
		superLiteral.setSourceRange(token.ptr, token.len);
		return superLiteral;
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
		if (ident == null && init == null) {
			
		} else if (ident != null && init != null) {
			fragment.setSourceRange(ident.startPosition, init.getStartPosition() + init.getLength() - ident.startPosition);
		} else if (ident != null && init == null) {
			fragment.setSourceRange(ident.startPosition, ident.length);
		} else {
			fragment.setSourceRange(init.getStartPosition(), init.getLength());
		}
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
		typeExpression.setSourceRange(type.getStartPosition(), type.getLength());
		return typeExpression;
	}
	
	private TypeDotIdentifierExpression newTypeDotIdentifierExpression(Type t, Token token) {
		TypeDotIdentifierExpression typeDot = new TypeDotIdentifierExpression(ast);
		typeDot.setType(t);
		typeDot.setName(newSimpleNameForToken(token));
		return typeDot;
	}
	
	private TypeDotIdentifierExpression newTypeDotIdentifierExpression(Type t) {
		TypeDotIdentifierExpression typeDot = new TypeDotIdentifierExpression(ast);
		typeDot.setType(t);
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
	
	private AsmStatement newAsmStatement(AST ast, List<Token> toklist) {
		AsmStatement asmStatement = new AsmStatement(ast);
		for(Token token : toklist) {
			AsmToken asmToken = new AsmToken(ast);
			asmToken.setToken(token.toString());
			asmToken.setSourceRange(token.ptr, token.len);
			asmStatement.tokens().add(asmToken);
		}
		return asmStatement;
	}
	
	private List<DDocComment> getLastDocComments() {
		List<DDocComment> toReturn = new ArrayList<DDocComment>();
		for(int i = comments.size() - 1; i >= lastDocCommentRead; i--) {
			Comment comment = comments.get(i);
			if (comment.isDDocComment()) {
				toReturn.add((DDocComment) comment);
			} else {
				break;
			}
		}
		return toReturn;
	}
	
	private void adjustLastDocComment() {
		lastDocCommentRead = comments.size();
	}
	
	private void attachLeadingComments(Declaration declaration) {
		if (prevToken.leadingComment != null) {
			declaration.setPostDDoc(prevToken.leadingComment);
		}
	}
	
	private void attachLeadingComments(ModuleDeclaration declaration) {
		if (prevToken.leadingComment != null) {
			declaration.setPostDDoc(prevToken.leadingComment);
		}
	}
	
	private void parsingErrorInsertTokenAfter(Token targetToken, String expected) {
		error("Syntax error on token \"" + targetToken + "\", " + expected + " expected after this token", IProblem.ParsingErrorInsertTokenAfter, targetToken.lineNumber, targetToken.ptr, targetToken.len);
	}
	
	private void parsingErrorDeleteToken(Token targetToken) {
		error("Syntax error on token \"" + targetToken + "\", delete this token", IProblem.ParsingErrorDeleteToken, targetToken.lineNumber, targetToken.ptr, targetToken.len);
	}
	
	private void parsingErrorInsertToComplete(Token targetToken, String insert, String toComplete) {
		error("Syntax error, insert \"" + insert + "\" to complete " + toComplete, IProblem.ParsingErrorInsertToComplete, targetToken.lineNumber, targetToken.ptr, targetToken.len);
	}
	
	private String toWord(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	
	private void adjustPossitionAccordingToComments(ASTNode node, List<DDocComment> preDDocs, DDocComment postDDoc) {
		if (preDDocs.isEmpty() && postDDoc == null) {
			return;
		}
		
		int start = node.getStartPosition();
		int end = start + node.getLength();
		
		if (!preDDocs.isEmpty()) {
			start = preDDocs.get(0).getStartPosition();
		}
		
		if (postDDoc != null) {
			end = postDDoc.getStartPosition() + postDDoc.getLength();
		}
		
		node.setSourceRange(start, end - start);
	}
	
}