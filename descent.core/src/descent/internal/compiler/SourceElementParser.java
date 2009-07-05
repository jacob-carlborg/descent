/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.compiler;

import java.util.List;
import java.util.Stack;

import descent.core.Flags;
import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.ISourceElementRequestor.FieldInfo;
import descent.internal.compiler.ISourceElementRequestor.MethodInfo;
import descent.internal.compiler.ISourceElementRequestor.TypeInfo;
import descent.internal.compiler.ISourceElementRequestor.TypeParameterInfo;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AggregateDeclaration;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.AliasThis;
import descent.internal.compiler.parser.AlignDeclaration;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.BaseClass;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.DeclarationExp;
import descent.internal.compiler.parser.DeclarationStatement;
import descent.internal.compiler.parser.DeleteDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.DtorDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.Id;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.InvariantDeclaration;
import descent.internal.compiler.parser.LinkDeclaration;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.ModuleDeclaration;
import descent.internal.compiler.parser.NewDeclaration;
import descent.internal.compiler.parser.PostBlitDeclaration;
import descent.internal.compiler.parser.PragmaDeclaration;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.StaticAssert;
import descent.internal.compiler.parser.StaticCtorDeclaration;
import descent.internal.compiler.parser.StaticDtorDeclaration;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.TemplateParameter;
import descent.internal.compiler.parser.TemplateParameters;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.UnitTestDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.VersionSymbol;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.compiler.parser.ast.NaiveASTFlattener;

/**
 * A source element parser extracts structural and reference information
 * from a piece of source.
 *
 * also see @ISourceElementRequestor
 *
 * The structural investigation includes:
 * - the package statement
 * - import statements
 * - top-level types: package member, member types (member types of member types...)
 * - fields
 * - methods
 *
 * If reference information is requested, then all source constructs are
 * investigated and type, field & method references are provided as well.
 *
 * Any (parsing) problem encountered is also provided.
 */
public class SourceElementParser extends AstVisitorAdapter {
	
	private final static TypeParameterInfo[] NO_TYPE_PARAMETERS = new TypeParameterInfo[0];
	
	public ISourceElementRequestor requestor;
	protected Module module;
	private final CompilerOptions options;
	private final NaiveASTFlattener flattener;
	private final Stack< Stack<AttribDeclaration> > attribDeclarationStack;
	public ASTNodeEncoder encoder;
	private char[] source;
	
	public boolean diet = true;
	public boolean recordLineSeparator = false;
	
	private Stack<ASTDmdNode> nodeStack = new Stack<ASTDmdNode>();

	/**
	 * @param surfaceDeclarations instruct the parser to ignore statements
	 * and expressions, just parse declarations.
	 */
	public SourceElementParser(
			ISourceElementRequestor requestor,
			CompilerOptions options) {
	
		this.requestor = requestor;
		this.options = options;
		this.flattener = new NaiveASTFlattener();
		this.attribDeclarationStack = new Stack< Stack<AttribDeclaration> >();
		this.attribDeclarationStack.push(new Stack<AttribDeclaration>());
	}
	
	protected int getASTlevel() {
		String source = (String) options.getMap().get(CompilerOptions.OPTION_Source);
		if (source == null || source.length() == 0) {
			return AST.D2;
		} else if (source.equals(CompilerOptions.VERSION_2_x)) {
			return AST.D2;
		} else if (source.equals(CompilerOptions.VERSION_1_x)) {
			return AST.D1;
		} else if (source.equals(CompilerOptions.VERSION_0_x)) {
			return AST.D0;
		} else {
			return AST.D2;
		}
	}
	
	public Module parseCompilationUnit(descent.internal.compiler.env.ICompilationUnit unit) {
//		long time = System.nanoTime();
		
		source = unit.getContents();
		
		ParseResult result = CompilationUnitResolver.parse(getASTlevel(), unit, options.getMap(), recordLineSeparator, true, diet);
		
		module = result.module;
		encoder = result.encoder;
		
		module.moduleName = unit.getFullyQualifiedName();
	
		requestor.enterCompilationUnit();
		module.accept(this);
		requestor.exitCompilationUnit(endOf(module));
		
//		time = System.nanoTime() - time;
//		System.out.println("SourceElementParser took: " + time + " nanoseconds to complete.");
		
		return module;
	}
	
	protected int startOf(ASTDmdNode node) {
		if (node == null) return 0;
		return node.start;
	}
	
	protected int endOf(ASTDmdNode node) {
		if (node == null) return 0;
		int end = startOf(node) + node.length - 1;
		if (end <= 0) {
			return 0;
		} else{
			return end;
		}
	}
	
	private int startOfDeclaration(ASTDmdNode node) {
		if (node == null) return 0;
		if (attribDeclarationStack.isEmpty()) {
			return startOfCommentIfAny(node);
		} else {
			Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
			if (stack.isEmpty()) {
				return startOfCommentIfAny(node);
			} else {
				for (int i = 0; i < stack.size(); i++) {
					AttribDeclaration att = stack.get(i);
					if ((att instanceof StorageClassDeclaration &&
							((StorageClassDeclaration) att).single) ||
						(att instanceof ProtDeclaration &&
								((ProtDeclaration) att).single)) {
						return startOfCommentIfAny(att);
					}
				}
				return startOfCommentIfAny(node);
			}
		}
	}	

	private int endOfDeclaration(ASTDmdNode node) {
		if (node == null) return 0;
		if (attribDeclarationStack.isEmpty()) {
			return endOfCommentIfAny(node);
		} else {
			Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
			if (stack.isEmpty()) {
				return endOfCommentIfAny(node);
			} else {
				for (int i = stack.size() - 1; i >= 0; i--) {
					AttribDeclaration att = stack.get(i);
					if ((att instanceof StorageClassDeclaration &&
							((StorageClassDeclaration) att).single) ||
						(att instanceof ProtDeclaration &&
								((ProtDeclaration) att).single)) {
						return endOfCommentIfAny(att);
					}
				}
				return endOfCommentIfAny(node);
			}
		}
	}
	
	private int startOfCommentIfAny(ASTDmdNode node) {
		List<Comment> preComments = module.getPreComments(node);
		if (preComments == null || preComments.isEmpty()) {
			return startOf(node);
		} else {
			return startOf(preComments.get(0));
		}
	}

	private int endOfCommentIfAny(ASTDmdNode node) {
		Comment postComment = module.getPostComment(node);
		if (postComment == null) {
			return endOf(node);
		} else {
			return endOf(postComment);
		}
	}
	
	protected int getFlags(ASTDmdNode node, List<Modifier> modifiers) {
		int flags = 0;
		if (modifiers != null) {
			for(Modifier modifier : modifiers) {
				flags |= modifier.getFlags();
			}
		}
		
		// Hack: protection attribute get reported wrong with
		//
		// public:
		//   int x;
		// private:
		//   int z;
		// 
		// you get both protections. So remove previous ones.
		
		long lastFlags = -1;
		
		if (!attribDeclarationStack.isEmpty()) {
			Stack<AttribDeclaration> sa = attribDeclarationStack.peek();
			for(AttribDeclaration a : sa) {
				if (a instanceof ProtDeclaration) {
					ProtDeclaration p = (ProtDeclaration) a;
					long pFlags = p.getFlags();
					if (lastFlags != -1) {
						flags &= ~lastFlags;
					}
					flags |= pFlags;
					lastFlags = pFlags;
				} else if (a instanceof StorageClassDeclaration) {
					StorageClassDeclaration p = (StorageClassDeclaration) a;
					flags |= p.getFlags();
					lastFlags = -1;
				}
			}
		}
		
		return flags;
	}
	
	private char[][] getTokens(BaseClasses baseClasses) {
		if (baseClasses == null || baseClasses.size() == 0) {
			return CharOperation.NO_CHAR_CHAR;
		}
		
		char[][] tokens = new char[baseClasses.size()][];
		for(int i = 0; i < baseClasses.size(); i++) {
			tokens[i] = getSignature(baseClasses.get(i).type);
		}
		return tokens;
	}	

	private TypeParameterInfo[] getTypeParameters(TemplateParameters parameters) {
		TypeParameterInfo[] infos = new TypeParameterInfo[parameters.size()];
		for(int i = 0; i < parameters.size(); i++) {
			TemplateParameter param = parameters.get(i);
			
			TypeParameterInfo info = new TypeParameterInfo();
			info.declarationStart = startOfDeclaration(param);
			info.declarationEnd = endOf(param);			
			info.name = param.ident.ident;
			info.signature = param.getSignature().toCharArray();
			info.nameSourceStart = startOf(param.ident);
			info.nameSourceEnd = endOf(param.ident);
			info.defaultValue = param.getDefaultValue();
			infos[i] = info;
		}
		return infos;
	}
	
	private char[][] getParameterNames(Arguments arguments) {
		if (arguments.size() == 0) return CharOperation.NO_CHAR_CHAR;
		
		char[][] names = new char[arguments.size()][];
		for(int i = 0; i < arguments.size(); i++) {
			IdentifierExp ident = arguments.get(i).ident;
			if (ident == null) {
				names[i] = CharOperation.NO_CHAR;
			} else {
				names[i] = ident.ident;	
			}
		}
		return names;
	}
	
	private char[][] getParameterTypes(Arguments arguments) {
		if (arguments.size() == 0) return CharOperation.NO_CHAR_CHAR;
		
		char[][] types = new char[arguments.size()][];
		for(int i = 0; i < arguments.size(); i++) {
			types[i] = arguments.get(i).getSignature().toCharArray();
		}
		return types;
	}
	
	private char[][] getParameterDefaultValues(Arguments arguments) {
		if (arguments.size() == 0 || arguments.get(arguments.size() - 1).defaultArg == null) {
			return null;
		}
		
		char[][] values = new char[arguments.size()][];
		for(int i = 0; i < arguments.size(); i++) {
			Expression defaultArg = arguments.get(i).defaultArg;
			if (defaultArg != null) {
				values[i] = CharOperation.subarray(source, defaultArg.start, defaultArg.start + defaultArg.length);
			}
		}
		return values;
	}
	
	private char[] getSignature(Type node) {
		if (node == null) {
			return CharOperation.NO_CHAR;
		} else {
			String sig = node.getSignature();
			return sig.toCharArray();
		}
	}
	
	// ------------------------------------------------------------------------
	
	public void visit(AggregateDeclaration node, TemplateDeclaration templateDeclaration) {
		nodeStack.push(node);
		
		switch(node.getNodeType()) {
		case ASTDmdNode.CLASS_DECLARATION:
			ClassDeclaration classDecl = (ClassDeclaration) node;
			visit(classDecl, Flags.AccClass, classDecl.baseclasses, templateDeclaration);
			break;
		case ASTDmdNode.INTERFACE_DECLARATION:
			InterfaceDeclaration intDecl = (InterfaceDeclaration) node;
			visit(intDecl, Flags.AccInterface, intDecl.baseclasses, templateDeclaration);
			break;
		case ASTDmdNode.STRUCT_DECLARATION:
			StructDeclaration strDecl = (StructDeclaration) node;
			visit(strDecl, Flags.AccStruct, null, templateDeclaration);
			break;
		case ASTDmdNode.UNION_DECLARATION:
			UnionDeclaration unDecl = (UnionDeclaration) node;
			visit(unDecl, Flags.AccUnion, null, templateDeclaration);
			break;
		}
	}
	
	private void visit(AggregateDeclaration node, int flags, BaseClasses baseClasses, TemplateDeclaration templateDeclaration) {
		nodeStack.push(node);	
		
		TypeInfo info = new TypeInfo();
		if (templateDeclaration != null) {
			info.declarationStart = startOfDeclaration(templateDeclaration);
		} else {
			info.declarationStart = startOfDeclaration(node);
		}
		info.modifiers = getFlags(node, module.getModifiers(node));
		info.modifiers |= flags;
		if (templateDeclaration != null) {
			info.modifiers |= Flags.AccTemplate;
		}
		
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceEnd = endOf(node.ident);
			info.nameSourceStart = startOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
			if (baseClasses != null && !baseClasses.isEmpty()) {
				BaseClass bc = baseClasses.get(0);
				info.nameSourceStart = bc.start;
				info.nameSourceEnd = bc.start + bc.length;
			} else {
				info.nameSourceStart = info.declarationStart;
				info.nameSourceEnd = info.declarationStart + 5; // class
			}
		}
		info.isForwardDeclaration = node.members == null;
		//info.secondary = !foundType;
		
		info.superinterfaces = getTokens(baseClasses);
		if (templateDeclaration != null) {
			info.typeParameters = getTypeParameters(templateDeclaration.parameters);
		}
		
		requestor.enterType(info);
	}
	
	@Override
	public boolean visit(ClassDeclaration node) {
		if (node.templated) {
			return true;
		}
		
		visit(node, Flags.AccClass, node.baseclasses, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(InterfaceDeclaration node) {
		if (node.templated) {
			return true;
		}
		
		visit(node, Flags.AccInterface, node.baseclasses, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(ModuleDeclaration node) {
		requestor.acceptPackage(startOfDeclaration(node), endOfDeclaration(node), node.getFQN(), node.safe);
		pushLevelInAttribDeclarationStack();
		return false;
	}
	
	@Override
	public boolean visit(StructDeclaration node) {
		if (node.templated) {
			return true;
		}
		
		visit(node, Flags.AccStruct, null, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(UnionDeclaration node) {
		if (node.templated) {
			return true;
		}
		
		visit(node, Flags.AccUnion, null, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(TemplateDeclaration node) {
		if (node.wrapper) {
			Dsymbol wrappedSymbol = node.members.get(0); // SEMANTIC
			if ((wrappedSymbol.getNodeType() == ASTDmdNode.FUNC_DECLARATION) ||
				(wrappedSymbol.getNodeType() == ASTDmdNode.CTOR_DECLARATION)) {
				visit((FuncDeclaration) wrappedSymbol, node);
				pushLevelInAttribDeclarationStack();
				return true;
			} else {
				visit((AggregateDeclaration) wrappedSymbol, node);
				pushLevelInAttribDeclarationStack();
				return true;
			}
		}
		
		nodeStack.push(node);
		
		TypeInfo info = new TypeInfo();
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = getFlags(node, module.getModifiers(node));
		info.modifiers |= Flags.AccTemplate;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		info.typeParameters = getTypeParameters(node.parameters);
		
		requestor.enterType(info);
		
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	private void visit(FuncDeclaration node, TemplateDeclaration templateDeclaration) {
		if (node instanceof CtorDeclaration) {
			visit(node, templateDeclaration, Flags.AccConstructor, ((CtorDeclaration)node).arguments, node.ident.ident);
		} else {
			TypeFunction ty = (TypeFunction) node.type;
			visit(node, templateDeclaration, 0, ty.parameters, node.ident.ident);
		}
	}

	@Override
	public boolean visit(FuncDeclaration node) {
		if (!node.templated) {
			visit(node, null);
			pushLevelInAttribDeclarationStack();
		}
		return true;
	}
	
	@Override
	public boolean visit(PostBlitDeclaration node) {
		visit(node, null, Flags.AccPostBlit, null, Id.ctor);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	private boolean visit(FuncDeclaration node, TemplateDeclaration templateDeclaration, int flags, Arguments arguments, char[] name) {
		nodeStack.push(node);
		
		MethodInfo info = new MethodInfo();
		if (templateDeclaration != null) {
			info.declarationStart = startOfDeclaration(templateDeclaration);
		} else {
			info.declarationStart = startOfDeclaration(node);
		}
		info.modifiers = getFlags(node, module.getModifiers(node));
		info.modifiers |= flags;
		if (templateDeclaration != null) {
			info.modifiers |= Flags.AccTemplate;
		}
		info.name = name;
		if (node.ident != null) {
			info.nameSourceStart = node.ident.start;
			info.nameSourceEnd = node.ident.start + node.ident.length - 1;
		}
		if (arguments != null) {
			info.parameterNames = getParameterNames(arguments);
			info.parameterTypes = getParameterTypes(arguments);
			info.parameterDefaultValues = getParameterDefaultValues(arguments);
		}
		
		if (node.type != null) {
			TypeFunction ty = (TypeFunction) node.type;
			if (ty.varargs == 1) {
				info.modifiers |= Flags.AccVarargs1;
			} else if (ty.varargs == 2) {
				info.modifiers |= Flags.AccVarargs2;
			}
			info.returnType = getSignature(ty.next);
			info.signature = getSignature(ty);
			if (ty.postModifiers != null) {
				for(Modifier modifier : ty.postModifiers) {
					info.modifiers |= modifier.getFlags();
				}
			}
		}
		if (templateDeclaration != null) {
			info.typeParameters = getTypeParameters(templateDeclaration.parameters);
		} else {
			info.typeParameters = NO_TYPE_PARAMETERS;
		}
		
		if (node.isCtorDeclaration() == null &&
				node.isDtorDeclaration() == null &&
				node.isNewDeclaration() == null &&
				node.isDeleteDeclaration() == null &&
				node.isPostBlitDeclaration() == null) {
			requestor.enterMethod(info);
		} else {
			requestor.enterConstructor(info);
		}
		return true;
	}
	
	@Override
	public boolean visit(CtorDeclaration node) {
		if (!node.templated) {
			visit(node, null, Flags.AccConstructor, node.arguments, Id.ctor);
			pushLevelInAttribDeclarationStack();
		}
		return true;
	}
	
	@Override
	public boolean visit(DtorDeclaration node) {
		visit(node, null, Flags.AccDestructor, null, Id.dtor);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(NewDeclaration node) {
		visit(node, null, Flags.AccNew, node.arguments, Id.classNew);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(DeleteDeclaration node) {
		visit(node, null, Flags.AccDelete, node.arguments, Id.classDelete);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(StaticCtorDeclaration node) {
		nodeStack.push(node);
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)), CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(StaticDtorDeclaration node) {
		nodeStack.push(node);
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)) | Flags.AccStaticDestructor, CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(InvariantDeclaration node) {
		nodeStack.push(node);
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)) | Flags.AccInvariant, CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(UnitTestDeclaration node) {
		nodeStack.push(node);
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)) | Flags.AccUnitTest, CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(VarDeclaration node) {
		if (insideFunction()) {
			return true;
		}
		
		VarDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		FieldInfo info = new FieldInfo();
		info.declarationStart = startOfDeclaration(last);
		info.modifiers = getFlags(node, module.getModifiers(node));
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		
		info.type = getSignature(node.type);
		info.initializationSource = encode(node.init);
		
		requestor.enterField(info);
		
		return true;
	}
	
	private char[] encode(ASTDmdNode node) {
		if (node == null) {
			return null;
		}
		
		char[] value = new char[node.length];
		System.arraycopy(source, node.start, value, 0, node.length);
		return ASTNodeEncoder.encoderForIndexer(value);
	}

	@Override
	public boolean visit(AliasDeclaration node) {
		if (insideFunction()) {
			return true;
		}
		
		AliasDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		FieldInfo info = new FieldInfo();
		info.declarationStart = startOfDeclaration(last);
		info.modifiers = getFlags(node, module.getModifiers(node));
		info.modifiers |= Flags.AccAlias;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		
		info.type = getSignature(node.type);
		
		requestor.enterField(info);
		
		return false;
	}
	
	@Override
	public boolean visit(AliasThis node) {
		if (insideFunction()) {
			return true;
		}
		
		FieldInfo info = new FieldInfo();
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = getFlags(node, module.getModifiers(node));
		info.modifiers |= Flags.AccAlias;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		
		// null type is "alias this"
		info.type = null;
		
		requestor.enterField(info);
		
		return false;
	}
	
	@Override
	public boolean visit(TypedefDeclaration node) {
		if (insideFunction()) {
			return true;
		}
		
		TypedefDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		FieldInfo info = new FieldInfo();
		info.declarationStart = startOfDeclaration(last);
		info.modifiers = getFlags(node, module.getModifiers(node));
		info.modifiers |= Flags.AccTypedef;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		
		info.type = getSignature(node.basetype);
		
		requestor.enterField(info);
		
		return false;
	}
	
	@Override
	public boolean visit(StaticAssert node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)) | Flags.AccStaticAssert, encode(node.exp));
		return false;
	}
	
	@Override
	public boolean visit(DebugSymbol node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)) | Flags.AccDebugAssignment, node.version.value);
		return false;
	}

	@Override
	public boolean visit(VersionSymbol node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)) | Flags.AccVersionAssignment, node.version.value);
		return false;
	}
	
	@Override
	public boolean visit(AlignDeclaration node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)) | Flags.AccAlign, node.salign == 0 ? CharOperation.NO_CHAR : String.valueOf(node.salign).toCharArray());
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(LinkDeclaration node) {
		char[] id = null;
		switch(node.linkage) {
		case LINKc: id = Id.C; break;
		case LINKcpp: id = Id.Cpp; break;
		case LINKd: id = Id.D; break;
		case LINKdefault: id = Id.empty;
		case LINKpascal: id = Id.Pascal;
		case LINKsystem: id = Id.System;
		case LINKwindows: id = Id.Windows;
		}
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)) | Flags.AccExternDeclaration, id);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(PragmaDeclaration node) {
		StringBuilder sb = new StringBuilder();
		sb.append(node.ident);
		if (node.args != null && node.args.size() > 0) {
			sb.append(": ");
			for(int i = 0; i < node.args.size(); i++) {
				if (i != 0)
					sb.append(", ");
				sb.append(node.args.get(i).toString());
			}
		}
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node, module.getModifiers(node)) | Flags.AccPragma, sb.toString().toCharArray());
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		nodeStack.push(node);
		
		// TODO Java -> D
		// Also, since the base class notation in D dosen't distinguis between
		// classes and interfaces, let's assume they are all interfaces for the moment
		TypeInfo info = new TypeInfo();
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = getFlags(node, module.getModifiers(node));
		info.modifiers |= Flags.AccEnum;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
			info.nameSourceStart = node.start;
			info.nameSourceEnd = node.start + 3; // enum
		}
		info.isForwardDeclaration = node.members == null;
		info.superinterfaces = node.memtype == null ? CharOperation.NO_CHAR_CHAR : new char[][] { getSignature(node.memtype) };
		
		requestor.enterType(info);
		
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	@Override
	public boolean visit(EnumMember node) {
		FieldInfo info = new FieldInfo();
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = Flags.AccEnum;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		
		info.initializationSource = encode(node.value);
		
		requestor.enterField(info);
		
		return false;
	}
	
	@Override
	public boolean visit(TemplateMixin node) {
		FieldInfo info = new FieldInfo();
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = getFlags(node, module.getModifiers(node));
		info.modifiers |= Flags.AccTemplateMixin;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);				
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		
		flattener.reset();
		flattener.visitTemplateMixinType(node);
		
		info.type = flattener.getResult().toCharArray();
		
		requestor.enterField(info);
		return false;
	}
	
	@Override
	public boolean visit(CompileDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node, module.getModifiers(node)) | Flags.AccMixin, encode(node.exp));
		return false;
	}
	
	@Override
	public boolean visit(ConditionalDeclaration node) {
		char[] displayString = CharOperation.NO_CHAR;;
		int flags = 0;
		switch(node.condition.getConditionType()) {
		case Condition.DEBUG: {
			DebugCondition cond = (DebugCondition) node.condition;
			displayString = cond.toCharArray();
			break;
		}
		case Condition.IFTYPE: {
			flags = Flags.AccIftypeDeclaration;
			break;
		}
		case Condition.STATIC_IF: {
			StaticIfCondition cond = (StaticIfCondition) node.condition;
			if (cond.sourceExp != null) {
				displayString = CharOperation.subarray(source, cond.sourceExp.start, cond.sourceExp.start + cond.sourceExp.length);
			}
			flags = Flags.AccStaticIfDeclaration;
			break;
		}
		case Condition.VERSION: {
			VersionCondition cond = (VersionCondition) node.condition;
			displayString = cond.toCharArray();
			flags = Flags.AccVersionDeclaration;
			break;
		}
		}
		requestor.enterConditional(startOf(node), getFlags(node, module.getModifiers(node)) | flags, displayString);
		
		Dsymbols thenDeclarations = node.decl;
		Dsymbols elseDeclarations = node.elsedecl;
		
		if (thenDeclarations != null && !thenDeclarations.isEmpty()) {
			if (elseDeclarations != null && !elseDeclarations.isEmpty()) {
				requestor.enterConditionalThen(startOfDeclaration(thenDeclarations.get(0)));
			}
			for(Dsymbol ideclaration : thenDeclarations) {
				Dsymbol declaration = ideclaration;
				declaration.accept(this);
			}
			if (elseDeclarations != null &&!elseDeclarations.isEmpty()) {
				requestor.exitConditionalThen(endOfDeclaration(thenDeclarations.get(thenDeclarations.size() - 1)));
			}
		}
		
		if (elseDeclarations != null &&!elseDeclarations.isEmpty()) {
			requestor.enterConditionalElse(startOfDeclaration(elseDeclarations.get(0)));
			for(Dsymbol ideclaration : elseDeclarations) {
				Dsymbol declaration = ideclaration;
				declaration.accept(this);
			}
			requestor.exitConditionalElse(endOfDeclaration(elseDeclarations.get(elseDeclarations.size() - 1)));
		}
		
		pushLevelInAttribDeclarationStack();
		return false;
	}
	
	@Override
	public boolean visit(Import node) {
		if (!node.first) {
			return false;
		}
		
		while(node != null) {
			int start, end;
			if (node.first) {
				start = node.firstStart;
				int otherStart = startOfDeclaration(node);
				if (otherStart < start) {
					start = otherStart;
				}
			} else {
				start = startOfDeclaration(node);
			}
			if (node.next == null) {
				end = node.start + node.lastLength - 1;
			} else {
				end = endOfDeclaration(node);
			}
			
			int flags = node.isstatic ? Flags.AccStatic : 0;
			flags |= getFlags(node, module.getModifiers(node));
			
			requestor.acceptImport(start, end, getName(node), getAlias(node), getSelectiveImportsNames(node), getSelectiveImportsAliases(node), flags);
			
			node = node.next;
		}
		pushLevelInAttribDeclarationStack();
		return false;
	}

	private String[] getSelectiveImportsAliases(Import node) {
		if (node.aliases == null || node.aliases.size() == 0) {
			return null;
		}
		
		String[] ret = new String[node.aliases.size()];
		for (int i = 0; i < node.aliases.size(); i++) {
			IdentifierExp ident = node.aliases.get(i);
			if (ident != null && ident.ident != null) {
				ret[i] = new String(ident.ident);
			}
		}
		return ret;
	}

	private String[] getSelectiveImportsNames(Import node) {
		if (node.names == null || node.names.size() == 0) {
			return null;
		}
		
		String[] ret = new String[node.names.size()];
		for (int i = 0; i < node.names.size(); i++) {
			IdentifierExp ident = node.names.get(i);
			if (ident != null && ident.ident != null) {
				ret[i] = new String(ident.ident);
			}
		}
		return ret;
	}

	private String getAlias(Import node) {
		if (node.aliasId != null) {
			return new String(node.aliasId.ident);
		}
		return null;
	}

	private String getName(Import node) {
		StringBuilder sb = new StringBuilder();
		if (node.packages != null && !node.packages.isEmpty()) {
			for (int i = 0; i < node.packages.size(); i++) {
				if (i != 0) {
					sb.append('.');
				}
				sb.append(node.packages.get(i).ident);
			}
			sb.append('.');
		}
		sb.append(node.id.ident);
		
		return sb.toString();
	}

	@Override
	public boolean visit(ProtDeclaration node) {
		pushAttribDeclaration(node);
		return true;
	}
	
	@Override
	public boolean visit(StorageClassDeclaration node) {
		pushAttribDeclaration(node);
		return true;
	}
	
	@Override
	public boolean visit(DeclarationStatement node) {
		Dsymbol dsymbol = ((DeclarationExp) node.sourceExp).declaration; // SEMANTIC
		switch(dsymbol.getNodeType()) {
		case ASTDmdNode.VAR_DECLARATION:
		case ASTDmdNode.ENUM_DECLARATION:
		case ASTDmdNode.CLASS_DECLARATION:
		case ASTDmdNode.INTERFACE_DECLARATION:
		case ASTDmdNode.STRUCT_DECLARATION:
		case ASTDmdNode.UNION_DECLARATION:
		case ASTDmdNode.FUNC_DECLARATION:
			return true;
		default:
			return false;
		}
	}
	
	private void pushAttribDeclaration(AttribDeclaration node) {
		Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
		stack.push(node);
	}
	
	protected void pushLevelInAttribDeclarationStack() {
		attribDeclarationStack.push(new Stack<AttribDeclaration>());
	}
	
	private void popLevelInAttribDeclarationStack() {
		attribDeclarationStack.pop();
	}
	
	// ------------------------------------------------------------------------	
	
	@Override
	public void endVisit(ModuleDeclaration node) {
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(ClassDeclaration node) {
		if (node.templated) {
			return;
		}
		
		nodeStack.pop();
		
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(InterfaceDeclaration node) {
		if (node.templated) {
			return;
		}
		
		nodeStack.pop();
		
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(StructDeclaration node) {
		if (node.templated) {
			return;
		}
		
		nodeStack.pop();
		
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(UnionDeclaration node) {
		if (node.templated) {
			return;
		}
		
		nodeStack.pop();
		
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(TemplateDeclaration node) {
		nodeStack.pop();
		
		int end;
		Comment postComment = module.getPostComment(node);
		if (postComment != null) {
			end = endOfDeclaration(postComment);
		} else {
			end = endOfDeclaration(node);
		}
		
		if (node.wrapper) {
			Dsymbol wrappedSymbol = node.members.get(0); // SEMANTIC
			if (wrappedSymbol.getNodeType() == ASTDmdNode.FUNC_DECLARATION) {
				requestor.exitMethod(end, -1, -1);
			} else if (wrappedSymbol.getNodeType() == ASTDmdNode.CTOR_DECLARATION) {
				requestor.exitConstructor(end);
			} else {
				requestor.exitType(end);
			}
		} else {
			requestor.exitType(end);
		}
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(FuncDeclaration node) {
		if (node.templated) {
			return;
		}
		
		nodeStack.pop();
		
		requestor.exitMethod(endOfDeclaration(node), -1, -1);
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(PostBlitDeclaration node) {
		nodeStack.pop();
		
		requestor.exitMethod(endOfDeclaration(node), -1, -1);
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(CtorDeclaration node) {
		if (node.templated) {
			return;
		}
		
		nodeStack.pop();
		
		requestor.exitConstructor(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(DtorDeclaration node) {
		nodeStack.pop();
		
		requestor.exitConstructor(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(NewDeclaration node) {
		nodeStack.pop();
		
		requestor.exitConstructor(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(DeleteDeclaration node) {
		nodeStack.pop();
		
		requestor.exitConstructor(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(StaticCtorDeclaration node) {
		nodeStack.pop();
		
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(StaticDtorDeclaration node) {
		nodeStack.pop();
		
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(InvariantDeclaration node) {
		nodeStack.pop();
		
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(UnitTestDeclaration node) {
		nodeStack.pop();
		
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(VarDeclaration node) {
		if (insideFunction()) {
			return;
		}
		
		VarDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		int initializerStart = node.sourceInit == null ? - 1 : startOf(node.sourceInit); // SEMANTIC
		int declarationSourceEnd = endOf(last);
		int declarationEnd = endOfDeclaration(node.ident);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}
	
	@Override
	public void endVisit(AliasDeclaration node) {
		if (insideFunction()) {
			return;
		}
		
		AliasDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		int initializerStart = endOf(node.ident);
		int declarationSourceEnd = endOf(last);
		int declarationEnd = endOfDeclaration(node.ident);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}
	
	@Override
	public void endVisit(AliasThis node) {
		if (insideFunction()) {
			return;
		}
		
		int initializerStart = endOf(node.ident);
		int declarationSourceEnd = endOf(node);
		int declarationEnd = endOfDeclaration(node.ident);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}

	@Override
	public void endVisit(TypedefDeclaration node) {
		if (insideFunction()) {
			return;
		}
		
		TypedefDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		int initializerStart = endOf(node.ident);
		int declarationSourceEnd = endOf(last);
		int declarationEnd = endOfDeclaration(node.ident);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}
	
	@Override
	public void endVisit(StaticAssert node) {
		requestor.exitInitializer(endOfDeclaration(node));
	}

	@Override
	public void endVisit(DebugSymbol node) {
		requestor.exitInitializer(endOfDeclaration(node));
	}

	@Override
	public void endVisit(VersionSymbol node) {
		requestor.exitInitializer(endOfDeclaration(node));
	}

	@Override
	public void endVisit(AlignDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(LinkDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(PragmaDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		nodeStack.pop();
		
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	@Override
	public void endVisit(EnumMember node) {
		int initializerStart = node.value == null ? - 1 : startOf(node.value);
		int declarationEnd = endOf(node.ident);
		int declarationSourceEnd = endOfDeclaration(node);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}

	@Override
	public void endVisit(TemplateMixin node) {
		int declarationSourceEnd = endOfDeclaration(node);
		int initializerStart = node.ident == null ? declarationSourceEnd - 1 : endOf(node.ident);			
		
		requestor.exitField(initializerStart, declarationSourceEnd, declarationSourceEnd);
	}

	@Override
	public void endVisit(CompileDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
	}
	
	@Override
	public void endVisit(ConditionalDeclaration node) {
		requestor.exitConditional(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(AnonDeclaration node) {
		nodeStack.pop();
		
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(Import node) {
		if (!node.first) {
			return;
		}
		
		popLevelInAttribDeclarationStack();
	}
	
	@Override
	public void endVisit(ProtDeclaration node) {
		popAttribDeclaration();
	}

	@Override
	public void endVisit(StorageClassDeclaration node) {
		popAttribDeclaration();
	}
	
	private void popAttribDeclaration() {
		if (!attribDeclarationStack.isEmpty()) {
			Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
			if (!stack.isEmpty()) {
				stack.pop();
			}
		}
	}
	
	// ------------------------------------------------------------------------

	@Override
	public boolean visit(AnonDeclaration node) {
		nodeStack.push(node);
		
		TypeInfo info = new TypeInfo();
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = getFlags(node, module.getModifiers(node));
		if (node.isunion) {
			info.modifiers |= Flags.AccUnion;
		} else {
			info.modifiers |= Flags.AccStruct;
		}
		
		info.name = CharOperation.NO_CHAR;
		requestor.enterType(info);
		
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	private boolean insideFunction() {
		return !nodeStack.isEmpty() && nodeStack.peek() instanceof FuncDeclaration;
	}
	
}
