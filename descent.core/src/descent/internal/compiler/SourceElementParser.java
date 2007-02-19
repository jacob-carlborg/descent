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

import java.util.HashMap;
import java.util.List;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.ASTVisitor;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.Argument;
import descent.core.dom.BaseClass;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ConstructorDeclaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.Import;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.Name;
import descent.core.dom.QualifiedName;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.TemplateParameter;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;
import descent.internal.compiler.ISourceElementRequestor.FieldInfo;
import descent.internal.compiler.ISourceElementRequestor.MethodInfo;
import descent.internal.compiler.ISourceElementRequestor.TypeInfo;
import descent.internal.compiler.ISourceElementRequestor.TypeParameterInfo;
import descent.internal.compiler.util.HashtableOfObjectToInt;

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
public class SourceElementParser extends ASTVisitor {
	
	public ISourceElementRequestor requestor;
	int fieldCount;
	boolean reportReferenceInfo;
	char[][] typeNames;
	char[][] superTypeNames;
	int nestedTypeIndex;
	HashtableOfObjectToInt sourceEnds = new HashtableOfObjectToInt();
	HashMap nodesToCategories = new HashMap(); // a map from ASTNode to char[][]
	
	private boolean foundType = false;

	public SourceElementParser(
			ISourceElementRequestor requestor) {
		
	
		this.requestor = requestor;
		typeNames = new char[4][];
		superTypeNames = new char[4][];
		nestedTypeIndex = 0;
	}

	public CompilationUnit parseCompilationUnit(ICompilationUnit unit, boolean resolveBindings) {
		ASTParser parser = ASTParser.newParser(AST.D1);
		parser.setSource(unit);
		parser.setResolveBindings(resolveBindings);
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		
		requestor.enterCompilationUnit();
		compilationUnit.accept(this);
		requestor.exitCompilationUnit(compilationUnit.getStartPosition() + compilationUnit.getLength());
		
		return compilationUnit;
	}
	
	private char[][] getTokens(Name name) {
		int depth = getDepth(name);
		char[][] tokens = new char[depth][];
		
		depth--;		
		while(name.isQualifiedName()) {
			QualifiedName qName = (QualifiedName) name;
			tokens[depth] = qName.getName().getIdentifier().toCharArray();
			name = qName.getQualifier();
			depth--;
		}
		
		tokens[depth] = name.getFullyQualifiedName().toCharArray();
		
		return tokens;
	}
	
	private int getDepth(Name name) {
		if (name.isSimpleName()) {
			return 1;
		} else {
			return 1 + getDepth(((QualifiedName) name).getQualifier());
		}
	}
	
	private int getFlags(List<Modifier> modifiers) {
		int flags = 0;
		for(Modifier modifier : modifiers) {
			flags |= modifier.getModifierKeyword().toFlagValue();
		}
		return flags;
	}
	
	private char[][] getTokens(List<BaseClass> baseClasses) {
		char[][] tokens = new char[baseClasses.size()][];
		for(int i = 0; i < baseClasses.size(); i++) {
			tokens[i] = baseClasses.get(i).getType().toString().toCharArray();
		}
		return tokens;
	}
	
	private TypeParameterInfo[] getTypeParameters(List<TemplateParameter> parameters) {
		TypeParameterInfo[] infos = new TypeParameterInfo[parameters.size()];
		for(int i = 0; i < parameters.size(); i++) {
			TemplateParameter param = parameters.get(i);
			
			TypeParameterInfo info = new TypeParameterInfo();
			info.annotationPositions = new long[0];
			info.bounds = new char[0][];
			info.declarationEnd = param.getStartPosition() + param.getLength() - 1;
			info.declarationStart = param.getStartPosition();
			info.name = param.getName().getIdentifier().toCharArray();
			info.nameSourceEnd = param.getName().getStartPosition() + param.getName().getLength() - 1;
			info.nameSourceStart = param.getName().getStartPosition();
			infos[i] = info;
		}
		return infos;
	}
	
	private char[][] getParameterNames(List<Argument> arguments) {
		char[][] names = new char[arguments.size()][];
		for(int i = 0; i < arguments.size(); i++) {
			names[i] = arguments.get(i).getName().getIdentifier().toCharArray();
		}
		return names;
	}
	
	private char[][] getParameterTypes(List<Argument> arguments) {
		char[][] types = new char[arguments.size()][];
		for(int i = 0; i < arguments.size(); i++) {
			types[i] = arguments.get(i).getType().toString().toCharArray();
		}
		return types;
	}
	
	@Override
	public boolean visit(ModuleDeclaration node) {
		requestor.acceptPackage(node.getStartPosition(), node.getStartPosition() + node.getLength() - 1, node.getName().getFullyQualifiedName().toCharArray());
		return false;
	}
	
	@Override
	public boolean visit(ImportDeclaration node) {
		// TODO Java -> D
		if (node.imports().size() == 1) {
			Import imp = node.imports().get(0);
			if (imp.getAlias() == null && imp.selectiveImports().size() == 0) {
				requestor.acceptImport(node.getStartPosition(), node.getStartPosition() + node.getLength() - 1, getTokens(imp.getName()), false, node.isStatic() ? Flags.AccStatic : Flags.AccDefault);
			}
		}
		return false;
	}
	
	@Override
	public boolean visit(AggregateDeclaration node) {
		// TODO Java -> D
		// Also, since the base class notation in D dosen't distinguis between
		// classes and interfaces, let's assume they are all interfaces for the moment
		TypeInfo info = new TypeInfo();
		info.annotationPositions = new long[0];
		info.categories = new char[0][];
		info.declarationStart = node.getStartPosition();
		info.modifiers = getFlags(node.modifiers());
		switch(node.getKind()) {
		case INTERFACE:
			info.modifiers |= Flags.AccInterface;
			break;
		case STRUCT:
			info.modifiers |= Flags.AccStruct;
			break;
		case UNION:
			info.modifiers |= Flags.AccUnion;
			break;
		}
		info.name = node.getName().getFullyQualifiedName().toCharArray();
		info.nameSourceEnd = node.getName().getStartPosition() + node.getName().getLength() - 1;
		info.nameSourceStart = node.getName().getStartPosition();
		info.secondary = !foundType;
		info.superclass = new char[0];
		info.superinterfaces = getTokens(node.baseClasses());		
		info.typeParameters = getTypeParameters(node.templateParameters());
		
		foundType = true;
		
		requestor.enterType(info);
		
		return true;
	}
	
	@Override
	public void endVisit(AggregateDeclaration node) {
		requestor.exitType(node.getStartPosition() + node.getLength() - 1);
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		// TODO Java -> D
		// Also, since the base class notation in D dosen't distinguis between
		// classes and interfaces, let's assume they are all interfaces for the moment
		TypeInfo info = new TypeInfo();
		info.annotationPositions = new long[0];
		info.categories = new char[0][];
		info.declarationStart = node.getStartPosition();
		info.modifiers = getFlags(node.modifiers());
		info.modifiers |= Flags.AccEnum;
		if (node.getName() != null) {
			info.name = node.getName().getFullyQualifiedName().toCharArray();
			info.nameSourceEnd = node.getName().getStartPosition() + node.getName().getLength() - 1;
			info.nameSourceStart = node.getName().getStartPosition();
		}
		info.secondary = !foundType;
		info.superclass = new char[0];
		if (node.getBaseType() != null) {
			info.superinterfaces = new char[][] { node.getBaseType().toString().toCharArray() };
		}
		
		foundType = true;
		
		requestor.enterType(info);
		
		return true;
	}
	
	@Override
	public void endVisit(EnumDeclaration node) {
		requestor.exitType(node.getStartPosition() + node.getLength() - 1);
	}
	
	@Override
	public boolean visit(TemplateDeclaration node) {
		// TODO Java -> D
		TypeInfo info = new TypeInfo();
		info.annotationPositions = new long[0];
		info.categories = new char[0][];
		info.declarationStart = node.getStartPosition();
		info.modifiers = getFlags(node.modifiers());
		info.modifiers |= Flags.AccTemplate;
		info.name = node.getName().getFullyQualifiedName().toCharArray();
		info.nameSourceEnd = node.getName().getStartPosition() + node.getName().getLength() - 1;
		info.nameSourceStart = node.getName().getStartPosition();
		info.secondary = !foundType;
		info.superclass = new char[0];
		
		foundType = true;
		
		requestor.enterType(info);
		
		return true;
	}
	
	@Override
	public void endVisit(TemplateDeclaration node) {
		requestor.exitType(node.getStartPosition() + node.getLength() - 1);
	}
	
	@Override
	public boolean visit(ConstructorDeclaration node) {
		if (node.getParent().getNodeType() == ASTNode.AGGREGATE_DECLARATION) {
			if (node.getKind() == ConstructorDeclaration.Kind.CONSTRUCTOR) {
				MethodInfo info = new MethodInfo();
				info.annotationPositions = new long[0];
				info.categories = new char[0][];
				info.declarationStart = node.getStartPosition();
				info.exceptionTypes = new char[0][];
				info.isAnnotation = false;
				info.isConstructor = true;
				info.modifiers = getFlags(node.modifiers());
				// TODO correct the name and related fields
				info.name = new char[0];
				info.nameSourceEnd = 0;
				info.nameSourceStart = 0;
				info.parameterNames = getParameterNames(node.arguments());
				info.parameterTypes = getParameterTypes(node.arguments());
				info.returnType = "void".toCharArray();
				info.typeParameters = new TypeParameterInfo[0];
				
				requestor.enterConstructor(info);
			} else if (node.getKind() == ConstructorDeclaration.Kind.STATIC_CONSTRUCTOR) {
				requestor.enterInitializer(node.getStartPosition(), getFlags(node.modifiers()));
			}
		}
		return true;
	}
	
	@Override
	public void endVisit(ConstructorDeclaration node) {
		if (node.getParent().getNodeType() == ASTNode.AGGREGATE_DECLARATION) {
			if (node.getKind() == ConstructorDeclaration.Kind.CONSTRUCTOR ||
					node.getKind() == ConstructorDeclaration.Kind.STATIC_CONSTRUCTOR) {
				requestor.exitConstructor(node.getStartPosition() + node.getLength() - 1);
			}
		}
	}
	
	@Override
	public boolean visit(FunctionDeclaration node) {
		if (node.getParent().getNodeType() == ASTNode.AGGREGATE_DECLARATION) {
			MethodInfo info = new MethodInfo();
			info.annotationPositions = new long[0];
			info.categories = new char[0][];
			info.declarationStart = node.getStartPosition();
			info.exceptionTypes = new char[0][];
			info.isAnnotation = false;
			info.isConstructor = false;
			info.modifiers = getFlags(node.modifiers());
			info.name = node.getName().getIdentifier().toCharArray();
			info.nameSourceEnd = node.getName().getStartPosition() + node.getName().getLength() - 1;
			info.nameSourceStart = node.getName().getStartPosition();
			info.parameterNames = getParameterNames(node.arguments());
			info.parameterTypes = getParameterTypes(node.arguments());
			info.returnType = node.getReturnType().toString().toCharArray();
			info.typeParameters = getTypeParameters(node.templateParameters());
			
			requestor.enterMethod(info);
		}
		return true;
	}
	
	@Override
	public void endVisit(FunctionDeclaration node) {
		if (node.getParent().getNodeType() == ASTNode.AGGREGATE_DECLARATION) {
			requestor.exitMethod(node.getStartPosition() + node.getLength() - 1, -1, -1);
		}
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		VariableDeclaration var = (VariableDeclaration) node.getParent();
		
		if (var.getParent().getNodeType() == ASTNode.AGGREGATE_DECLARATION) {		
			FieldInfo info = new FieldInfo();
			info.annotationPositions = new long[0];
			info.categories = new char[0][];
			
			if (var.fragments().get(0) == node) {
				info.declarationStart = var.getStartPosition();
			} else {
				info.declarationStart = node.getStartPosition();
			}
			
			info.modifiers = getFlags(var.modifiers());
			info.name = node.getName().getIdentifier().toCharArray();
			info.nameSourceEnd = node.getName().getStartPosition() + node.getName().getLength() - 1;
			info.nameSourceStart = node.getName().getStartPosition();
			info.type = var.getType().toString().toCharArray();
			
			requestor.enterField(info);
		}
		
		return false;
	}
	
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		VariableDeclaration var = (VariableDeclaration) node.getParent();
		
		if (var.getParent().getNodeType() == ASTNode.AGGREGATE_DECLARATION) {
			int initializerStart = node.getInitializer() == null ? - 1 : node.getInitializer().getStartPosition();
			int declarationEnd = node.getName().getStartPosition() + node.getName().getLength() - 1;
			int declarationSourceEnd = var.getStartPosition() + var.getLength() - 1;
			
			requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
		}
	}
	
}
