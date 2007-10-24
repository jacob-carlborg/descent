package descent.internal.codeassist;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import descent.core.CompletionContext;
import descent.core.CompletionProposal;
import descent.core.CompletionRequestor;
import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.internal.codeassist.complete.CompletionOnArgumentName;
import descent.internal.codeassist.complete.CompletionOnBreakStatement;
import descent.internal.codeassist.complete.CompletionOnCaseStatement;
import descent.internal.codeassist.complete.CompletionOnContinueStatement;
import descent.internal.codeassist.complete.CompletionOnDotIdExp;
import descent.internal.codeassist.complete.CompletionOnGotoStatement;
import descent.internal.codeassist.complete.CompletionOnImport;
import descent.internal.codeassist.complete.CompletionOnModuleDeclaration;
import descent.internal.codeassist.complete.CompletionOnTypeDotIdExp;
import descent.internal.codeassist.complete.CompletionOnVersionCondition;
import descent.internal.codeassist.complete.CompletionParser;
import descent.internal.codeassist.complete.ICompletionOnKeyword;
import descent.internal.codeassist.impl.Engine;
import descent.internal.compiler.env.AccessRestriction;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.CastExp;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.ErrorExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
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
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypeStruct;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.UnitTestDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VarExp;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.compiler.util.HashtableOfObject;
import descent.internal.core.INamingRequestor;
import descent.internal.core.InternalNamingConventions;
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
	
	private static final char[][] allTypesProperties = { Id.init, Id.__sizeof, Id.alignof, Id.mangleof, Id.stringof };
	private static final char[][] integralTypesProperties = { Id.max, Id.min };
	private static final char[][] floatingPointTypesProperties = { Id.infinity, Id.nan, Id.dig, Id.epsilon, Id.mant_dig, Id.max_10_exp, Id.max_exp, Id.min_10_exp, Id.min_exp, Id.max, Id.min };
	
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
			
			parser = new CompletionParser(AST.D2, source);
			parser.filename = this.fileName;
			parser.cursorLocation = completionPosition;
			
			this.module = parser.parseModuleObj();
			ASTDmdNode assistNode = parser.getAssistNode();
			
			this.requestor.acceptContext(buildContext());
			
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
				for(ICompletionOnKeyword node : parser.getKeywordCompletions()) {
					this.startPosition = actualCompletionPosition - node.getToken().length;
					this.endPosition = actualCompletionPosition;
					findKeywords(node.getToken(), node.getPossibleKeywords(), node.canCompleteEmptyToken());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.requestor.endReporting();
		}
	}

	private CompletionContext buildContext() {
		// TODO Descent completion context
		CompletionContext context = new CompletionContext();
		context.setOffset(this.offset);
		return context;
	}

	private void doSemantic(Module module) {
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
		}
		
		SemanticContext context = new SemanticContext(null, module, global);
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
	
	private void completeCaseStatement(CompletionOnCaseStatement node) {
		doSemantic(module);
		
		// Check to see if the condition is an enum, and suggest
		// the members of it
		SwitchStatement sw = node.sw;
		if (sw == null) {
			return;
		}
		
		// TODO maybe make each node implement something like
		// "findEnumDeclaration"
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
		
		EnumDeclaration enumDeclaration = typeEnum.sym;
		if (enumDeclaration == null) {
			return;
		}
		
		if (enumDeclaration.members == null) {
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
	
	private void completeTypeDotIdExp(CompletionOnTypeDotIdExp node) {
		Type type = node.type;
		if (type == null) {
			return;
		}
		
		// For now, just complete basic types and enums
		
		// If it's a basic type, no semantic analysis is needed
		if (type.getNodeType() == ASTDmdNode.TYPE_BASIC) {
			char[] name = computePrefixAndSourceRange(node.ident);
			completeTypeBasic((TypeBasic) type, name);
		} else {
			// else, do semantic, then see what the type is
			// it's typeof(exp)
			doSemantic(module);
			
			completeType(node.resolvedType, node.ident);
		}
	}
	
	private void completeDotIdExp(CompletionOnDotIdExp node) {
		if (node.e1 == null) {
			return;
		}
		
		doSemantic(module);

		completeType(node.e1.type, node.ident);
	}

	private void completeType(Type type, IdentifierExp ident) {
		if (type == null) {
			return;
		}
		
		char[] name = computePrefixAndSourceRange(ident);		
		switch(type.getNodeType()) {
		case ASTDmdNode.TYPE_BASIC:
			completeTypeBasic((TypeBasic) type, name);
			break;
		case ASTDmdNode.TYPE_CLASS:
			completeTypeClass((TypeClass) type, name);
			break;
		case ASTDmdNode.TYPE_STRUCT:
			completeTypeStruct((TypeStruct) type, name);
			break;
		case ASTDmdNode.TYPE_ENUM:
			completeTypeEnum((TypeEnum) type, name);
			break;
		}
	}

	private void completeTypeBasic(TypeBasic type, char[] name) {
		// Nothing for type void
		if (type.ty == TY.Tvoid) {
			return;
		}
		
		suggestProperties(name, allTypesProperties);
		
		if (type.isintegral()) {
			suggestProperties(name, integralTypesProperties);
		}
		
		if (type.isfloating()) {
			suggestProperties(name, floatingPointTypesProperties);
		}
	}
	
	private void completeTypeClass(TypeClass type, char[] name) {
		ClassDeclaration decl = type.sym;
		if (decl == null) {
			return;
		}
		
		// Suggest fields
		suggestFields(decl.fields, name);
		
		// Then virtual functions
		suggestFunctions(decl.vtbl, name);
		
		// Then functions
		suggestFunctions(decl.vtblFinal, name);
		
		// And also all type's properties
		suggestProperties(name, allTypesProperties);
	}
	
	private void completeTypeStruct(TypeStruct type, char[] name) {
		StructDeclaration decl = type.sym;
		if (decl == null) {
			return;
		}
		
		// Suggest fields
		suggestFields(decl.fields, name);
		
		// And functions
		suggestFunctions(decl.members, name);
		
		// And also all type's properties
		suggestProperties(name, allTypesProperties);
	}
	
	private void completeTypeEnum(TypeEnum type, char[] name) {
		EnumDeclaration enumDeclaration = type.sym;
		if (enumDeclaration == null) {
			return;
		}
		
		// Suggest enum members
		completeEnumMembers(name, enumDeclaration, new HashtableOfCharArrayAndObject(), false /* don't use fqn */);
		
		// And also all type's properties
		suggestProperties(name, allTypesProperties);
	}
	
	private void suggestFields(List<VarDeclaration> list, char[] name) {
		if (list == null) {
			return;
		}
		
		for(VarDeclaration var : list) {
			if (CharOperation.prefixEquals(name, var.ident.ident, false)) {
				CompletionProposal proposal = this.createProposal(CompletionProposal.FIELD_REF, this.actualCompletionPosition);
				proposal.setName(var.ident.ident);
				proposal.setCompletion(var.ident.ident);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
			}
		}
	}
	
	private void suggestFunctions(List list, char[] name) {
		if (list == null) {
			return;
		}
		
		for(Object obj : list) {
			if (!(obj instanceof FuncDeclaration)) {
				continue;
			}
			
			FuncDeclaration func = (FuncDeclaration) obj;
			if (CharOperation.prefixEquals(name, func.ident.ident, false)) {
				CompletionProposal proposal = this.createProposal(CompletionProposal.METHOD_REF, this.actualCompletionPosition);
				proposal.setName(getNameForFuncProposal(func));
				proposal.setCompletion(getCompletionForFuncProposal(func));
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
			}
		}
	}

	private char[] getCompletionForFuncProposal(FuncDeclaration func) {
		StringBuilder sb = new StringBuilder();
		sb.append(func.ident.ident);
		sb.append('(');
		TypeFunction tf = (TypeFunction) func.type;
		if (tf.parameters != null) {						
			for(int i = 0; i < tf.parameters.size(); i++) {
				if (i != 0) {
					sb.append(", ");
				}
				
				Argument arg = tf.parameters.get(i);
				if (arg.ident != null && arg.ident.ident != null) {
					sb.append(arg.ident.ident);
				}
			}
		}
		sb.append(')');
		
		char[] ret = new char[sb.length()];
		sb.getChars(0, sb.length(), ret, 0);
		return ret;
	}

	private char[] getNameForFuncProposal(FuncDeclaration func) {
		StringBuilder sb = new StringBuilder();
		sb.append(func.ident.ident);
		sb.append('(');
		
		TypeFunction tf = (TypeFunction) func.type;
		if (tf.parameters != null) {						
			for(int i = 0; i < tf.parameters.size(); i++) {
				if (i != 0) {
					sb.append(", ");
				}
				
				Argument arg = tf.parameters.get(i);
				if (arg.type != null) {
					sb.append(arg.type.toCharArray());
					sb.append(' ');
				}
				
				if (arg.ident != null && arg.ident.ident != null) {
					sb.append(arg.ident.ident);
				}
			}
		}
		sb.append(')');
		
		char[] ret = new char[sb.length()];
		sb.getChars(0, sb.length(), ret, 0);
		return ret;
	}

	private void suggestProperties(char[] name, char[][] properties) {
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
	
	private void completeEnumMembers(char[] name, EnumDeclaration enumDeclaration, HashtableOfCharArrayAndObject excludedNames, boolean useQualifiedName) {
		for(Dsymbol symbol : enumDeclaration.members) {
			if (!(symbol instanceof EnumMember)) {
				continue;
			}
			
			EnumMember member = (EnumMember) symbol;
			
			char[] proposition;
			if (useQualifiedName) {
				proposition = CharOperation.concat(enumDeclaration.ident.ident, member.ident.ident, '.');
			} else {
				proposition = member.ident.ident;
			}
			if (!excludedNames.containsKey(proposition) &&
					CharOperation.prefixEquals(name, proposition, false)) {
				CompletionProposal proposal = this.createProposal(CompletionProposal.FIELD_REF, this.actualCompletionPosition);
				proposal.setName(proposition);
				proposal.setCompletion(proposition);
				proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
				CompletionEngine.this.requestor.accept(proposal);
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

	public void acceptType(char[] packageName, char[] typeName, char[][] enclosingTypeNames, int modifiers, AccessRestriction accessRestriction) {
	}

}
