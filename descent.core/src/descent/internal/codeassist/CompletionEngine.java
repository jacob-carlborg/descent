package descent.internal.codeassist;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import descent.core.CompletionContext;
import descent.core.CompletionProposal;
import descent.core.CompletionRequestor;
import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DdocMacros;
import descent.core.ddoc.DdocParser;
import descent.core.ddoc.DdocSection;
import descent.core.ddoc.DdocSection.Parameter;
import descent.internal.codeassist.complete.CompletionOnArgumentName;
import descent.internal.codeassist.complete.CompletionOnBreakStatement;
import descent.internal.codeassist.complete.CompletionOnCaseStatement;
import descent.internal.codeassist.complete.CompletionOnContinueStatement;
import descent.internal.codeassist.complete.CompletionOnDotIdExp;
import descent.internal.codeassist.complete.CompletionOnGotoStatement;
import descent.internal.codeassist.complete.CompletionOnImport;
import descent.internal.codeassist.complete.CompletionOnJavadocImpl;
import descent.internal.codeassist.complete.CompletionOnModuleDeclaration;
import descent.internal.codeassist.complete.CompletionOnTypeDotIdExp;
import descent.internal.codeassist.complete.CompletionOnVersionCondition;
import descent.internal.codeassist.complete.CompletionParser;
import descent.internal.codeassist.complete.ICompletionOnKeyword;
import descent.internal.codeassist.impl.Engine;
import descent.internal.compiler.env.AccessRestriction;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.lookup.DescentModuleFinder;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.BaseClass;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.CastExp;
import descent.internal.compiler.parser.Chars;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.ErrorExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IEnumDeclaration;
import descent.internal.compiler.parser.IEnumMember;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.IStructDeclaration;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.Id;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.InvariantDeclaration;
import descent.internal.compiler.parser.LabelStatement;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.SwitchStatement;
import descent.internal.compiler.parser.TY;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeClass;
import descent.internal.compiler.parser.TypeEnum;
import descent.internal.compiler.parser.TypeExp;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypeStruct;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.UnitTestDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VarExp;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.compiler.util.HashtableOfObject;
import descent.internal.core.CancelableNameEnvironment;
import descent.internal.core.INamingRequestor;
import descent.internal.core.InternalNamingConventions;
import descent.internal.core.JavaProject;
import descent.internal.core.SearchableEnvironment;
import descent.internal.core.util.Util;

/**
 * This class is the entry point for source completions.
 * It contains two public APIs used to call CodeAssist on a given source with
 * a given environment, assisting position and storage (and possibly options).
 */
public class CompletionEngine extends Engine 
	implements RelevanceConstants, ISearchRequestor {
	
	public static boolean DEBUG = false;
	public static boolean PERF = false;
	
	private static final char[][] allTypesProperties = { Id.init, Id.__sizeof, Id.alignof, Id.mangleof, Id.stringof };
	private static final char[][] integralTypesProperties = { Id.max, Id.min };
	private static final char[][] floatingPointTypesProperties = { Id.infinity, Id.nan, Id.dig, Id.epsilon, Id.mant_dig, Id.max_10_exp, Id.max_exp, Id.min_10_exp, Id.min_exp, Id.max, Id.min };
	private static final char[][] staticAndDynamicArrayProperties = { Id.dup, Id.sort, Id.length, Id.ptr, Id.reverse };
	private static final char[][] associativeArrayProperties = { Id.length, Id.keys, Id.values, Id.rehash };
	private static final char[][] ddocSections = { "Authors".toCharArray(), "Bugs".toCharArray(), "Date".toCharArray(), "Deprecated".toCharArray(), "Examples".toCharArray(), "History".toCharArray(), "License".toCharArray(), "Returns".toCharArray(), "See_Also".toCharArray(), "Standards".toCharArray(), "Throws".toCharArray(), "Version".toCharArray(), "Copyright".toCharArray(), "Params".toCharArray(), "Macros".toCharArray() };
	private static final char[][] specialTokens = { Id.FILE, Id.LINE, Id.DATE, Id.TIME, Id.TIMESTAMP, Id.VERSION, Id.VENDOR };
	
	IJavaProject javaProject;
	CompletionParser parser;
	CompletionRequestor requestor;
	
	Module module;
	
	char[] fileName = null;
	char[] sourceUnitFqn = null;
	int startPosition, actualCompletionPosition, endPosition, offset;
	
	char[] source;
	char[] completionToken;
	char[] qualifiedCompletionToken;
	
	public HashtableOfObject typeCache;
	
	HashtableOfObject knownCompilationUnits = new HashtableOfObject(10);
	HashtableOfObject knownKeywords = new HashtableOfObject(10);
	
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
			
			parser = new CompletionParser(Util.getApiLevel(this.compilerOptions.getMap()), source, this.fileName);
			parser.cursorLocation = completionPosition;
			parser.nextToken();
			
			this.module = parser.parseModuleObj();
			this.module.moduleName = sourceUnit.getFullyQualifiedName();
			ASTDmdNode assistNode = parser.getAssistNode();
			
			this.requestor.acceptContext(buildContext(parser));
			
			// First the assist node
			if (assistNode != null) {
				if (assistNode instanceof CompletionOnModuleDeclaration && 
						!requestor.isIgnored(CompletionProposal.PACKAGE_REF)) {
					CompletionOnModuleDeclaration node = (CompletionOnModuleDeclaration) assistNode;
					completeModuleDeclaration(node);
				} else if (assistNode instanceof CompletionOnImport &&
						!requestor.isIgnored(CompletionProposal.PACKAGE_REF)) {
					CompletionOnImport node = (CompletionOnImport) assistNode;
					completeImport(node);
				} else if (assistNode instanceof CompletionOnArgumentName) {
					CompletionOnArgumentName node = (CompletionOnArgumentName) assistNode;
					completeArgumentName(node);
				} else if (assistNode instanceof CompletionOnGotoStatement) {
					CompletionOnGotoStatement node = (CompletionOnGotoStatement) assistNode;
					completeGotoBreakOrContinueStatement(node.ident, false /* don't check in loop */);
				} else if (assistNode instanceof CompletionOnBreakStatement) {
					CompletionOnBreakStatement node = (CompletionOnBreakStatement) assistNode;
					completeGotoBreakOrContinueStatement(node.ident, true /* check in loop */);
				} else if (assistNode instanceof CompletionOnContinueStatement) {
					CompletionOnContinueStatement node = (CompletionOnContinueStatement) assistNode;
					completeGotoBreakOrContinueStatement(node.ident, true /* check in loop */);
				} else if (assistNode instanceof CompletionOnVersionCondition) {
					CompletionOnVersionCondition node = (CompletionOnVersionCondition) assistNode;
					completeVersionCondition(node);
				} else if (assistNode instanceof CompletionOnCaseStatement) {
					CompletionOnCaseStatement node = (CompletionOnCaseStatement) assistNode;
					completeCaseStatement(node);
				} else if (assistNode instanceof CompletionOnTypeDotIdExp) {
					CompletionOnTypeDotIdExp node = (CompletionOnTypeDotIdExp) assistNode;
					completeTypeDotIdExp(node);
				} else if (assistNode instanceof CompletionOnDotIdExp) {
					CompletionOnDotIdExp node = (CompletionOnDotIdExp) assistNode;
					completeDotIdExp(node);
				}
			}
			
			// Then the keywords
			if (parser.getKeywordCompletions() != null && 
					!requestor.isIgnored(CompletionProposal.KEYWORD)) {
				
				char[] prefix = findTextBeforeCursor();
				this.startPosition = this.actualCompletionPosition - prefix.length;
				this.endPosition = this.actualCompletionPosition;
				
				for(ICompletionOnKeyword node : parser.getKeywordCompletions()) {
					findKeywords(prefix, node.getPossibleKeywords(), node.canCompleteEmptyToken());
				}
				
				// Also suggest the special tokens, if at least
				// one character was types (otherwise, it's annoying)
				if (prefix.length > 0) {
					findKeywords(prefix, specialTokens, true);
				}
			}
			
			// Then ddoc
			if (parser.getJavadocCompletion() != null) {
				CompletionOnJavadocImpl node = parser.getJavadocCompletion();
				completeJavadoc(node);
			}
		} catch (Exception e) {
			Util.log(e);
		} finally {
			this.requestor.endReporting();
		}
	}

	private CompletionContext buildContext(CompletionParser parser) {
		// TODO Descent completion context
		CompletionContext context = new CompletionContext();
		context.setOffset(this.offset);
		return context;
	}

	private void doSemantic(Module module) throws JavaModelException {
		Global global = new Global();
		
		IPackageFragmentRoot[] roots;
		try {
			roots = this.javaProject.getAllPackageFragmentRoots();
			for(IPackageFragmentRoot root : roots) {
				if (root.getResource() == null) {
					global.path.add(root.getPath().toOSString());
				} else {
					global.path.add(root.getResource().getLocation().toOSString());
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		SemanticContext context = new SemanticContext(
				null, 
				module, 
				this.javaProject,
				new DescentModuleFinder(new CancelableNameEnvironment((JavaProject) this.javaProject, null, null)),
				global);
		module.semantic(context);
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
		
		if (CharOperation.prefixEquals(fqnBeforeCursor, sourceUnitFqn, false)) {
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
		char[] name = computePrefixAndSourceRange(node.ident);
		findVariableNames(name, node.type, findExcludedNames(node));
	}
	
	private void completeGotoBreakOrContinueStatement(IdentifierExp ident, final boolean inLoop) {
		final HashtableOfCharArrayAndObject labelsMap = new HashtableOfCharArrayAndObject();		
		module.accept(new AstVisitorAdapter() {
			
			private FuncDeclaration func;
			
			@Override
			public boolean visit(FuncDeclaration node) {
				return visitFunc(node);
			}
			
			@Override
			public boolean visit(UnitTestDeclaration node) {
				return visitFunc(node);
			}
			
			@Override
			public boolean visit(InvariantDeclaration node) {
				return visitFunc(node);
			}
			
			@Override
			public boolean visit(FuncLiteralDeclaration node) {
				return visitFunc(node);
			}
			
			private boolean visitFunc(FuncDeclaration node) {
				if (inRange(node)) {
					func = node;
					labelsMap.clear();
					return true;
				} else {
					return false;
				}
			}
			
			@Override
			public boolean visit(LabelStatement node) {
				if (func != null && func.start <= node.start && node.start <= func.start + func.length
						&& (!inLoop || isRelevant(node))) {
					labelsMap.put(node.ident.ident, this);
				}
				return true;
			}
			
			private boolean inRange(ASTDmdNode node) {
				return node.start <= CompletionEngine.this.actualCompletionPosition &&
					CompletionEngine.this.actualCompletionPosition <= node.start + node.length;
			}
			
			private boolean isRelevant(LabelStatement node) {
				Statement stm = node.statement;
				while(stm != null) {
					int type = stm.getNodeType();
					if (type == ASTDmdNode.WHILE_STATEMENT ||
						type == ASTDmdNode.DO_STATEMENT ||
						type == ASTDmdNode.FOR_STATEMENT) {
						return true;
					} else if (type == ASTDmdNode.COMPOUND_STATEMENT) {
						CompoundStatement cs = (CompoundStatement) stm;
						if (cs.sourceStatements != null && cs.sourceStatements.size() > 0) {
							stm = cs.sourceStatements.get(0);
						} else {
							return false;
						}
					} else {
						return false;
					}
				}
				return false;
			}

			@Override
			public boolean visit(EnumDeclaration node) {
				return false;
			}			
			@Override
			public boolean visit(ClassDeclaration node) {
				return inRange(node);
			}			
			@Override
			public boolean visit(InterfaceDeclaration node) {
				return inRange(node);
			}			
			@Override
			public boolean visit(StructDeclaration node) {
				return inRange(node);
			}			
			@Override
			public boolean visit(UnionDeclaration node) {
				return inRange(node);
			}			
			@Override
			public boolean visit(TemplateDeclaration node) {
				return inRange(node);
			}			
		});
		
		char[][] labels = labelsMap.keys();
		if (labels != null) {
			char[] prefix = computePrefixAndSourceRange(ident);			
			for(char[] label : labels) {
				if (label != null) {
					if (CharOperation.prefixEquals(prefix, label, false) && !CharOperation.equals(prefix, label)) {
						CompletionProposal proposal = this.createProposal(CompletionProposal.LABEL_REF, this.actualCompletionPosition);
						proposal.setName(label);
						proposal.setCompletion(label);
						proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
						CompletionEngine.this.requestor.accept(proposal);
					}
				}
			}
		}
	}
	
	private void completeVersionCondition(CompletionOnVersionCondition node) {
		char[] name = node.ident == null ? CharOperation.NO_CHAR : node.ident;
		this.startPosition = this.actualCompletionPosition - name.length;
		this.endPosition = this.actualCompletionPosition;
		
		// Suggest the versions found in the source file
		HashtableOfCharArrayAndObject versions = parser.versions;
		if (versions == null) {
			versions = new HashtableOfCharArrayAndObject();
		}
		
		// And also the predefined
		for(char[] predefined : VersionCondition.resevered) {
			versions.put(predefined, this);
		}
		
		for(char[] id : versions.keys()) {
			if (id == null) {
				continue;
			}
			
			if (CharOperation.prefixEquals(name, id, false) && !CharOperation.equals(name, id)) {
				CompletionProposal proposal = this.createProposal(CompletionProposal.VERSION_REF, this.actualCompletionPosition);
				proposal.setName(id);
				proposal.setCompletion(id);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
			}
		}
	}
	
	private void completeCaseStatement(CompletionOnCaseStatement node) throws JavaModelException {
		doSemantic(module);
		
		// Check to see if the condition is an enum, and suggest
		// the members of it
		SwitchStatement sw = node.sw;
		if (sw == null) {
			return;
		}
		
		TypeEnum typeEnum;
		
		Expression condition = sw.condition;
		if (condition == null) {
			return;
		} else if (condition instanceof VarExp) {
			VarExp var = (VarExp) condition;
			Type type = var.type;
			if (type == null || !(type instanceof TypeEnum)) {
				return;
			}
			
			typeEnum = (TypeEnum) type;	
		} else if (condition instanceof CastExp) {
			CastExp castExp = (CastExp) condition;
			if (castExp.e1 == null || !(castExp.e1 instanceof VarExp)) {
				return;
			}
			
			VarExp varExp = (VarExp) castExp.e1;
			if (varExp.var == null || !(varExp.var instanceof VarDeclaration)) {
				return;
			}
			
			VarDeclaration varDeclaration = (VarDeclaration) varExp.var;
			if (varDeclaration.type == null || !(varDeclaration.type instanceof TypeEnum)) {
				return;
			}
			
			typeEnum = (TypeEnum) varDeclaration.type;
		} else {
			return;
		}
		
		IEnumDeclaration enumDeclaration = typeEnum.sym;
		if (enumDeclaration == null) {
			return;
		}
		
		if (enumDeclaration.members() == null) {
			return;
		}
		
		char[] name;
		Expression caseExp = node.originalExpression;
		if (caseExp instanceof ErrorExp) {
			name = CharOperation.NO_CHAR;
			this.startPosition = this.actualCompletionPosition;
			this.endPosition = this.actualCompletionPosition;
		} else if (caseExp instanceof IdentifierExp) {
			IdentifierExp idExp = (IdentifierExp) caseExp;
			name = idExp.ident;
			this.startPosition = idExp.start;
			this.endPosition = this.actualCompletionPosition;
		} else {
			return;
		}
		
		HashtableOfCharArrayAndObject excludedNames = new HashtableOfCharArrayAndObject();
		if (sw.cases != null) {
			for(Object caseObject : sw.cases) {
				CaseStatement caseStatement = (CaseStatement) caseObject;
				Expression exp = caseStatement.sourceExp;
				if (exp != null) {
					excludedNames.put(exp.toCharArray(), this);
				}
			}
		}
		
		completeEnumMembers(name, enumDeclaration, excludedNames, true /* use fqn */);
	}
	
	private void completeTypeDotIdExp(CompletionOnTypeDotIdExp node) throws JavaModelException {
		Type type = node.type;
		if (type == null) {
			return;
		}
		
		// If it's a basic type, no semantic analysis is needed
		if (type.getNodeType() == ASTDmdNode.TYPE_BASIC) {
			char[] name = computePrefixAndSourceRange(node.ident);
			completeTypeBasicProperties((TypeBasic) type, name);
		} else {
			// else, do semantic, then see what the type is
			// it's typeof(exp)
			doSemantic(module);
			
			completeType(node.resolvedType, node.ident, true /* only statics */);
		}
	}

	private void completeDotIdExp(CompletionOnDotIdExp node) throws JavaModelException {
		if (node.e1 == null) {
			return;
		}
		
		doSemantic(module);

		completeExpression(node.e1, node.ident);
	}

	private void completeExpression(Expression e1, IdentifierExp ident) {
		if (e1 instanceof VarExp) {
			VarExp var = (VarExp) e1;
			IDeclaration decl = var.var;
			if (decl == null) {
				return;
			}
			
			Type type = decl.type();
			completeType(type, ident, false /* not only statics */);
		} else if (e1 instanceof CallExp) {
			Type type = ((CallExp) e1).type;
			completeType(type, ident, false /* not only statics */);
		} else if (e1 instanceof TypeExp) {
			Type type = e1.type;
			completeType(type, ident, true /* only statics */);
		}
	}

	private void completeType(Type type, IdentifierExp ident, boolean onlyStatics) {
		if (type == null) {
			return;
		}
		
		char[] name = computePrefixAndSourceRange(ident);		
		switch(type.getNodeType()) {
		case ASTDmdNode.TYPE_BASIC:
			completeTypeBasicProperties((TypeBasic) type, name);
			break;
		case ASTDmdNode.TYPE_CLASS:
			completeTypeClass((TypeClass) type, name, onlyStatics);
			break;
		case ASTDmdNode.TYPE_STRUCT:
			completeTypeStruct((TypeStruct) type, name, onlyStatics);
			break;
		case ASTDmdNode.TYPE_ENUM:
			completeTypeEnum((TypeEnum) type, name);
			break;
		case ASTDmdNode.TYPE_S_ARRAY:
		case ASTDmdNode.TYPE_D_ARRAY:
			completeTypeStaticOrDynamicArrayProperties(name);
			break;
		case ASTDmdNode.TYPE_A_ARRAY:
			completeTypeAssociativeArrayProperties(name);
			break;
		}
	}

	private void completeTypeBasicProperties(TypeBasic type, char[] name) {
		// Nothing for type void
		if (type.ty == TY.Tvoid) {
			return;
		}
		
		suggestProperties(name, allTypesProperties, R_DEFAULT);
		
		if (type.isintegral()) {
			suggestProperties(name, integralTypesProperties, R_INTERESTING_BUILTIN_PROPERTY);
		}
		
		if (type.isfloating()) {
			suggestProperties(name, floatingPointTypesProperties, R_INTERESTING_BUILTIN_PROPERTY);
		}
	}
	
	private void completeTypeStaticOrDynamicArrayProperties(char[] name) {
		suggestProperties(name, allTypesProperties, R_DEFAULT);
		suggestProperties(name, staticAndDynamicArrayProperties, R_INTERESTING_BUILTIN_PROPERTY);
	}
	
	private void completeTypeAssociativeArrayProperties(char[] name) {
		suggestProperties(name, allTypesProperties, R_DEFAULT);
		suggestProperties(name, associativeArrayProperties, R_INTERESTING_BUILTIN_PROPERTY);
	}
	
	private void completeTypeClass(TypeClass type, char[] name, boolean onlyStatics) {
		// Keep a hashtable of already used signatures, in order to avoid
		// suggesting overriden functions
		HashtableOfCharArrayAndObject funcSignatures = new HashtableOfCharArrayAndObject();
		
		completeTypeClassRecursively(type, name, onlyStatics, funcSignatures);
		
		// And also all type's properties
		suggestProperties(name, allTypesProperties, R_DEFAULT);
	}
	
	private void completeTypeClassRecursively(TypeClass type, char[] name, boolean onlyStatics, HashtableOfCharArrayAndObject funcSignatures) {
		IClassDeclaration decl = type.sym;
		if (decl == null) {
			return;
		}
		
		// Suggest members
		suggestMembers(decl.members(), name, onlyStatics, funcSignatures);
		
		// Suggest superclass and superinterface members
		BaseClasses baseClasses = decl.baseclasses();
		for(BaseClass baseClass : baseClasses) {
			completeTypeClassRecursively((TypeClass) baseClass.base.type(), name, onlyStatics, funcSignatures);
		}
	}
	
	private void completeTypeStruct(TypeStruct type, char[] name, boolean onlyStatics) {
		IStructDeclaration decl = type.sym;
		if (decl == null) {
			return;
		}
		
		// Suggest members
		suggestMembers(decl.members(), name, onlyStatics, null);
		
		// And also all type's properties
		suggestProperties(name, allTypesProperties, R_DEFAULT);
	}
	
	private void completeTypeEnum(TypeEnum type, char[] name) {
		IEnumDeclaration enumDeclaration = type.sym;
		if (enumDeclaration == null) {
			return;
		}
		
		// Suggest enum members
		completeEnumMembers(name, enumDeclaration, new HashtableOfCharArrayAndObject(), false /* don't use fqn */);
		
		// And also all type's properties
		suggestProperties(name, allTypesProperties, R_DEFAULT);
		
		// If the base type of the enum is integral, also suggest integer properties
		if (enumDeclaration.memtype().isintegral()) {
			suggestProperties(name, integralTypesProperties, R_INTERESTING_BUILTIN_PROPERTY);
		}
	}
	
	private void suggestMembers(Dsymbols members, char[] name, boolean onlyStatics, HashtableOfCharArrayAndObject funcSignatures) {
		suggestMembers(members, name, onlyStatics, 0, funcSignatures);
	}
	
	private void suggestMembers(Dsymbols members, char[] name, boolean onlyStatics, long flags, HashtableOfCharArrayAndObject funcSignatures) {
		if (members == null || members.isEmpty()) {
			return;
		}
		
		for(IDsymbol member : members) {
			suggestMember(member, name, onlyStatics, flags, funcSignatures);
		}
	}
	
	private void suggestMember(IDsymbol member, char[] name, boolean onlyStatics, long flags, HashtableOfCharArrayAndObject funcSignatures) {
		// TODO Descent don't allow all of the attrib declarations to enter
		if (member instanceof AttribDeclaration) {
			AttribDeclaration attrib = (AttribDeclaration) member;
			suggestMembers(attrib.decl, name, onlyStatics, flags | attrib.getFlags(), funcSignatures);
			return;
		}
		
		
		// Types act like statics
		boolean isType = member instanceof IClassDeclaration || 
						member instanceof IStructDeclaration ||
						member instanceof IEnumDeclaration;
		
			// Filter statics if only statics were requested
		IDeclaration decl = member.isDeclaration();
		if (decl != null) {
			if (!isType && !decl.isStatic() && !decl.isConst() && onlyStatics) {
				return;
			}
		}
		
		// See if it's a variable
		IVarDeclaration var = member.isVarDeclaration();
		if (var != null) {
			if (CharOperation.prefixEquals(name, var.ident().ident, false)) {
				CompletionProposal proposal = this.createProposal(CompletionProposal.FIELD_REF, this.actualCompletionPosition);
				proposal.setName(var.ident().ident);
				proposal.setCompletion(var.ident().ident);
				proposal.setSignature(var.getSignature().toCharArray());
				proposal.setFlags(flags | var.getFlags());
				proposal.setRelevance(R_INTERESTING);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
			}
			return;
		}
		
		// See if it's a function
		IFuncDeclaration func = member.isFuncDeclaration();
		if (func != null) {
			// Don't suggest already suggested functions
			char[] signature = func.getSignature().toCharArray();
			if (funcSignatures != null && funcSignatures.containsKey(signature)) {
				return;
			}
			
			if (CharOperation.prefixEquals(name, func.ident().ident, false)) {
				CompletionProposal proposal = this.createProposal(CompletionProposal.METHOD_REF, this.actualCompletionPosition);
				proposal.setName(func.ident().ident);
				proposal.setCompletion((func.ident().toString() + "()").toCharArray());
				proposal.setSignature(func.getSignature().toCharArray());
				proposal.setFlags(flags | func.getFlags());
				proposal.setRelevance(R_INTERESTING);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
				
				if (funcSignatures != null) {
					funcSignatures.put(signature, proposal);
				}
			}
			return;
		}
		
		if (isType) {
			if (CharOperation.prefixEquals(name, member.ident().ident, false)) {
				CompletionProposal proposal = this.createProposal(CompletionProposal.TYPE_REF, this.actualCompletionPosition);
				proposal.setName(member.ident().ident);
				proposal.setCompletion(member.ident().ident);
				proposal.setSignature(member.getSignature().toCharArray());
				proposal.setFlags(flags | member.getFlags());
				proposal.setRelevance(R_INTERESTING);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
			}
		}
	}

	private void suggestProperties(char[] name, char[][] properties, int relevance) {
		for(char[] property : properties) {
			if (CharOperation.prefixEquals(name, property, false)) {
				CompletionProposal proposal = this.createProposal(CompletionProposal.FIELD_REF, this.actualCompletionPosition);
				proposal.setName(property);
				proposal.setCompletion(property);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
			}
		}
	}
	
	private void completeEnumMembers(char[] name, IEnumDeclaration enumDeclaration, HashtableOfCharArrayAndObject excludedNames, boolean useQualifiedName) {
		for(IDsymbol symbol : enumDeclaration.members()) {
			IEnumMember member = symbol.isEnumMember();
			if (member == null) {
				continue;
			}
			
			char[] proposition;
			if (useQualifiedName) {
				proposition = CharOperation.concat(enumDeclaration.ident().ident, member.ident().ident, '.');
			} else {
				proposition = member.ident().ident;
			}
			if (!excludedNames.containsKey(proposition) && (
					CharOperation.prefixEquals(name, proposition, false) || 
					CharOperation.prefixEquals(name, member.ident().ident, false)
					)) {
				CompletionProposal proposal = this.createProposal(CompletionProposal.FIELD_REF, this.actualCompletionPosition);
				proposal.setName(member.ident().ident);
				proposal.setCompletion(proposition);
				proposal.setSignature(member.getSignature().toCharArray());
				proposal.setFlags(member.getFlags());		
				proposal.setRelevance(R_INTERESTING);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
			}
		}
	}
	
	private void completeJavadoc(CompletionOnJavadocImpl node) {
		// A very na�ve solution for now...
		char[] ddocSource = node.getSource();
		DdocParser parser = new DdocParser(ddocSource);
		Ddoc ddoc = parser.parse();
		
		char[] prefix = findTextBeforeCursor();
		this.startPosition = this.actualCompletionPosition - prefix.length;
		this.endPosition = this.actualCompletionPosition;
		
		if (isInDdocSectionName(node.getStart(), prefix.length + 1)) {
			suggestDdocSectionNames(ddoc, prefix);
		}
		suggestDdocMacroNames(ddoc, node.getOtherDdocs(), prefix);
	}

	private char[] findTextBeforeCursor() {
		StringBuilder sb = new StringBuilder();
		for(int i = actualCompletionPosition - 1; i >= 0 && 
			isJavadocLetter(this.source[i]); i--) {
			sb.insert(0, this.source[i]);
		}
		
		char[] ret = new char[sb.length()];
		sb.getChars(0, sb.length(), ret, 0);
		return ret;
	}
	
	private boolean isInDdocSectionName(int start, int backwards) {
		// Check to see if we are in the beginning of the line
		for(int i = actualCompletionPosition - backwards; i >= 0; i--) {
			char c = this.source[i];
			switch(c) {
			case ' ':
			case '\t':
			case '\r':
				continue;
			case '\n':
				return true;
			case '*':
				boolean foundAnotherSpace = false;
				for(int j = i - 1; j >= 0; j--) {
					char d = this.source[j];
					switch(d) {
					case ' ':
					case '\t':
					case '\r':
						foundAnotherSpace = true;
						continue;
					case '\n':
						return true;
					case '*':
						if (foundAnotherSpace) {
							return false;
						} else {
							continue;
						}
					case '/':
						return j == start;
					default:
						return false;
					}
				}
				break;
			default:
				return false;
			}
		}
		return true;
	}

	private boolean isJavadocLetter(char c) {
		return Chars.isalpha(c) || c == '_' || c == '$';
	}

	private void suggestDdocSectionNames(Ddoc ddoc, char[] prefix) {
		HashtableOfCharArrayAndObject excludedNames = new HashtableOfCharArrayAndObject();
		for (int i = 0; i < ddoc.getSections().length; i++) {
			DdocSection ddocSection = ddoc.getSections()[i];
			if (ddocSection.getName() != null) {
				excludedNames.put(ddocSection.getName().toCharArray(), this);
			}
		}
		
		// Suggest sections that doesn't already exist
		for(char[] name : ddocSections) {
			if (!excludedNames.containsKey(name) && 
					CharOperation.prefixEquals(prefix, name, false)) {
				char[] nameColon = new char[name.length + 1];
				System.arraycopy(name, 0, nameColon, 0, name.length);
				nameColon[nameColon.length - 1] = ':';
				
				CompletionProposal proposal = this.createProposal(CompletionProposal.KEYWORD, this.actualCompletionPosition);
				proposal.setName(nameColon);
				proposal.setCompletion(nameColon);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
			}
		}		
	}
	
	private void suggestDdocMacroNames(Ddoc ddoc, List<char[]> otherDdocs, char[] prefix) {
		Map<String, String> macros = new HashMap<String, String>(DdocMacros.getDefaultMacros());
		mergeMacros(macros, ddoc);
		
		if (otherDdocs != null) {
			for(char[] otherDdoc : otherDdocs) {
				DdocParser parser = new DdocParser(otherDdoc);
				Ddoc other = parser.parse();
				mergeMacros(macros, other);
			}
		}
		
		for(Map.Entry<String, String> entry : macros.entrySet()) {
			String macroName = entry.getKey();
			String macroValue = entry.getValue();
			
			char[] name = new char[macroName.length() + 1];
			System.arraycopy(macroName.toCharArray(), 0, name, 1, macroName.length());
			name[0] = '$';
			if (CharOperation.prefixEquals(prefix, name, false)) {
				// TODO document this somewhere: the "name" is the
				// macro replacement, while the completion is the
				// name of the macro
				CompletionProposal proposal = this.createProposal(CompletionProposal.DDOC_MACRO, this.actualCompletionPosition);
				proposal.setName(macroValue.toCharArray());
				proposal.setCompletion(name);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
			}
		}
	}

	private void mergeMacros(Map<String, String> macros, Ddoc ddoc) {
		DdocSection macrosSection = ddoc.getMacrosSection();
		if (macrosSection != null) {
			if (macrosSection != null) {
				Parameter[] params = macrosSection.getParameters();
				for(Parameter param : params)  {
					if (param.getName() != null) {
						macros.put(param.getName(), param.getText());
					}
				}
			}
		}
	}

	private char[] computePrefixAndSourceRange(IdentifierExp ident) {
		char[] prefix;
		if (ident == null || ident.ident == null || CharOperation.equals(ident.ident, CharOperation.NO_CHAR)) {
			this.startPosition = this.actualCompletionPosition;
			this.endPosition = this.actualCompletionPosition;
			prefix = CharOperation.NO_CHAR;
		} else {
			this.startPosition = ident.start;
			this.endPosition = ident.start + ident.length;
			prefix = ident.ident;
		}
		return prefix;
	}
	
	private char[][] findExcludedNames(CompletionOnArgumentName node) {
		TypeFunction parent = node.parentType;
		if (parent == null || parent.parameters == null) {
			return CharOperation.NO_CHAR_CHAR;
		}
		
		char[][] excluded = new char[parent.parameters.size() - 1][];
		for(int i = 0, j = 0; i < parent.parameters.size(); i++) {
			Argument arg = parent.parameters.get(i);
			if (arg == node) {
				continue;
			}
			
			if (arg.ident == null || arg.ident.ident == null) {
				excluded[j] = CharOperation.NO_CHAR;
			} else {
				excluded[j] = arg.ident.ident;
			}
			j++;
		}
		return excluded;
	}

	private void findVariableNames(char[] name, Type type, char[][] excludeNames){
		// compute variable name for non base type
		final char[] t = name;
		INamingRequestor namingRequestor = new INamingRequestor() {
			public void acceptNameWithPrefixAndSuffix(char[] name, boolean isFirstPrefix, boolean isFirstSuffix, int reusedCharacters) {
				accept(
						name,
						(isFirstPrefix ? R_NAME_FIRST_PREFIX : R_NAME_PREFIX) + (isFirstSuffix ? R_NAME_FIRST_SUFFIX : R_NAME_SUFFIX),
						reusedCharacters);
			}

			public void acceptNameWithPrefix(char[] name, boolean isFirstPrefix, int reusedCharacters) {
				accept(name, isFirstPrefix ? R_NAME_FIRST_PREFIX :  R_NAME_PREFIX, reusedCharacters);
			}

			public void acceptNameWithSuffix(char[] name, boolean isFirstSuffix, int reusedCharacters) {
				accept(name, isFirstSuffix ? R_NAME_FIRST_SUFFIX : R_NAME_SUFFIX, reusedCharacters);
			}

			public void acceptNameWithoutPrefixAndSuffix(char[] name,int reusedCharacters) {
				accept(name, 0, reusedCharacters);
			}
			void accept(char[] name, int prefixAndSuffixRelevance, int reusedCharacters){
				if (CharOperation.prefixEquals(t, name, false)) {
					int relevance = computeBaseRelevance();
					relevance += computeRelevanceForInterestingProposal();
					relevance += computeRelevanceForCaseMatching(t, name);
					relevance += prefixAndSuffixRelevance;
					if(reusedCharacters > 0) relevance += R_NAME_LESS_NEW_CHARACTERS;
					//relevance += computeRelevanceForRestrictions(IAccessRule.K_ACCESSIBLE); // no access restriction for variable name
					
					// accept result
					//CompletionEngine.this.noProposal = false;
					if(!CompletionEngine.this.requestor.isIgnored(CompletionProposal.VARIABLE_DECLARATION)) {
						CompletionProposal proposal = CompletionEngine.this.createProposal(CompletionProposal.VARIABLE_DECLARATION, CompletionEngine.this.actualCompletionPosition);
						//proposal.setSignature(getSignature(typeBinding));
						//proposal.setPackageName(q);
						//proposal.setTypeName(displayName);
						proposal.setName(name);
						proposal.setCompletion(name);
						//proposal.setFlags(Flags.AccDefault);
						proposal.setReplaceRange(CompletionEngine.this.startPosition - CompletionEngine.this.offset, CompletionEngine.this.endPosition - CompletionEngine.this.offset);
						proposal.setRelevance(relevance);
						CompletionEngine.this.requestor.accept(proposal);
					}
				}
			}
		};
		
		InternalNamingConventions.suggestArgumentNames(
				this.javaProject, 
				CharOperation.NO_CHAR, 
				InternalNamingConventions.readableName(type), 
				0, 
				name, 
				excludeNames, 
				namingRequestor);
	}
	
	private void findKeywords(char[] keyword, char[][] choices, boolean canCompleteEmptyToken) {
		if(choices == null || choices.length == 0) return;
		
		int length = keyword.length;
		if (canCompleteEmptyToken || length > 0)
			for (int i = 0; i < choices.length; i++)
				if (length <= choices[i].length
					&& CharOperation.prefixEquals(keyword, choices[i], false /* ignore case */
				)){
					int relevance = computeBaseRelevance();
					relevance += computeRelevanceForInterestingProposal();
					relevance += computeRelevanceForCaseMatching(keyword, choices[i]);
//					relevance += computeRelevanceForRestrictions(IAccessRule.K_ACCESSIBLE); // no access restriction for keywors
					
//					if(CharOperation.equals(choices[i], Keywords.TRUE) || CharOperation.equals(choices[i], Keywords.FALSE)) {
//						relevance += computeRelevanceForExpectingType(TypeBinding.BOOLEAN);
//						relevance += computeRelevanceForQualification(false);
//					}
//					this.noProposal = false;
					if(!this.requestor.isIgnored(CompletionProposal.KEYWORD)) {
						if (knownKeywords.containsKey(choices[i])) {
							continue;
						}
						knownKeywords.put(choices[i], this);
						
						CompletionProposal proposal = this.createProposal(CompletionProposal.KEYWORD, this.actualCompletionPosition);
						proposal.setName(choices[i]);
						proposal.setCompletion(choices[i]);
						proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
						proposal.setRelevance(relevance);
						this.requestor.accept(proposal);
					}
				}
	}
	
	int computeBaseRelevance(){
		return R_DEFAULT;
	}
	
	int computeRelevanceForInterestingProposal(){
		return R_INTERESTING;
	}
	
	int computeRelevanceForCaseMatching(char[] token, char[] proposalName){
		if (this.options.camelCaseMatch) {
			if(CharOperation.equals(token, proposalName, true /* do not ignore case */)) {
				return R_CASE + R_EXACT_NAME;
			} else if (CharOperation.prefixEquals(token, proposalName, true /* do not ignore case */)) {
				return R_CASE;
			} else if (CharOperation.camelCaseMatch(token, proposalName)){
				return R_CAMEL_CASE;
			} else if(CharOperation.equals(token, proposalName, false /* ignore case */)) {
				return R_EXACT_NAME;
			}
		} else if (CharOperation.prefixEquals(token, proposalName, true /* do not ignore case */)) {
			if(CharOperation.equals(token, proposalName, true /* do not ignore case */)) {
				return R_CASE + R_EXACT_NAME;
			} else {
				return R_CASE;
			}
		} else if(CharOperation.equals(token, proposalName, false /* ignore case */)) {
			return R_EXACT_NAME;
		}
		return 0;
	}

	protected CompletionProposal createProposal(int kind, int completionOffset) {
		CompletionProposal proposal = CompletionProposal.create(kind, completionOffset - this.offset);
		proposal.javaProject = javaProject;
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
		proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
		this.requestor.accept(proposal);
	}

	public void acceptPackage(char[] packageName) {
		
	}

	public void acceptType(char[] packageName, char[] typeName, char[][] enclosingTypeNames, long modifiers, AccessRestriction accessRestriction) {
	}

}
