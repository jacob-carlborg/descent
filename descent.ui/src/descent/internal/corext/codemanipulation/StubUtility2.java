package descent.internal.corext.codemanipulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.core.IMethod;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.AliasTemplateParameter;
import descent.core.dom.Argument;
import descent.core.dom.Block;
import descent.core.dom.CallExpression;
import descent.core.dom.DotIdentifierExpression;
import descent.core.dom.Expression;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IMethodBinding;
import descent.core.dom.ITemplateParameterBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.ReturnStatement;
import descent.core.dom.Statement;
import descent.core.dom.TemplateParameter;
import descent.core.dom.Type;
import descent.core.dom.rewrite.ASTRewrite;
import descent.core.dom.rewrite.ImportRewrite;
import descent.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import descent.internal.corext.dom.ASTNodeFactory;
import descent.internal.corext.dom.ASTNodes;
import descent.internal.corext.dom.Bindings;
import descent.ui.CodeGeneration;

public class StubUtility2 {
	
	public static IMethodBinding[] getOverridableMethods(AST ast, ITypeBinding typeBinding, boolean isSubType) {
		List allMethods= new ArrayList();
		IMethodBinding[] typeMethods= typeBinding.getDeclaredMethods();
		for (int index= 0; index < typeMethods.length; index++) {
			final long modifiers= typeMethods[index].getModifiers();
			if (!typeMethods[index].isConstructor() && !Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers))
				allMethods.add(typeMethods[index]);
		}
		ITypeBinding clazz= typeBinding.getSuperclass();
		while (clazz != null) {
			IMethodBinding[] methods= clazz.getDeclaredMethods();
			for (int offset= 0; offset < methods.length; offset++) {
				final long modifiers= methods[offset].getModifiers();
				if (!methods[offset].isConstructor() && !Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers)) {
					if (findOverridingMethod(methods[offset], allMethods) == null)
						allMethods.add(methods[offset]);
				}
			}
			clazz= clazz.getSuperclass();
		}
		clazz= typeBinding;
		while (clazz != null) {
			ITypeBinding[] superInterfaces= clazz.getInterfaces();
			for (int index= 0; index < superInterfaces.length; index++) {
				getOverridableMethods(ast, superInterfaces[index], allMethods);
			}
			clazz= clazz.getSuperclass();
		}
		if (typeBinding.isInterface())
			getOverridableMethods(ast, ast.resolveWellKnownType("java.lang.Object"), allMethods); //$NON-NLS-1$
		if (!isSubType)
			allMethods.removeAll(Arrays.asList(typeMethods));
		long modifiers= 0;
		if (!typeBinding.isInterface()) {
			for (int index= allMethods.size() - 1; index >= 0; index--) {
				IMethodBinding method= (IMethodBinding) allMethods.get(index);
				modifiers= method.getModifiers();
				if (Modifier.isFinal(modifiers))
					allMethods.remove(index);
			}
		}
		return (IMethodBinding[]) allMethods.toArray(new IMethodBinding[allMethods.size()]);
	}
	
	private static IMethodBinding findOverridingMethod(IMethodBinding method, List allMethods) {
		for (int i= 0; i < allMethods.size(); i++) {
			IMethodBinding curr= (IMethodBinding) allMethods.get(i);
			if (Bindings.areOverriddenMethods(curr, method) || Bindings.isSubsignature(curr, method))
				return curr;
		}
		return null;
	}
	
	private static void getOverridableMethods(AST ast, ITypeBinding superBinding, List allMethods) {
		IMethodBinding[] methods= superBinding.getDeclaredMethods();
		for (int offset= 0; offset < methods.length; offset++) {
			final long modifiers= methods[offset].getModifiers();
			if (!methods[offset].isConstructor() && !Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers)) {
				if (findOverridingMethod(methods[offset], allMethods) == null && !Modifier.isStatic(modifiers))
					allMethods.add(methods[offset]);
			}
		}
		ITypeBinding[] superInterfaces= superBinding.getInterfaces();
		for (int index= 0; index < superInterfaces.length; index++) {
			getOverridableMethods(ast, superInterfaces[index], allMethods);
		}
	}
	
	public static FunctionDeclaration createImplementationStub(ICompilationUnit unit, ASTRewrite rewrite, ImportRewrite imports, ImportRewriteContext context, IMethodBinding binding, String type, CodeGenerationSettings settings, boolean deferred) throws CoreException {
		Assert.isNotNull(imports);
		Assert.isNotNull(rewrite);

		AST ast= rewrite.getAST();

		FunctionDeclaration decl= ast.newFunctionDeclaration();
		decl.modifiers().addAll(getImplementationModifiers(ast, binding, deferred));

		decl.setName(ast.newSimpleName(binding.getName()));
//		decl.setConstructor(false);

		ITemplateParameterBinding[] typeParams= binding.getTypeParameters();
		
		List<TemplateParameter> typeParameters= decl.templateParameters();
		for (int i= 0; i < typeParams.length; i++) {
			ITemplateParameterBinding curr= typeParams[i];
			AliasTemplateParameter newTypeParam= ast.newAliasTemplateParameter();
			newTypeParam.setName(ast.newSimpleName(curr.getName()));
//			ITypeBinding[] typeBounds= curr.getTypeBounds();
//			if (typeBounds.length != 1 || !"java.lang.Object".equals(typeBounds[0].getQualifiedName())) {//$NON-NLS-1$
//				List newTypeBounds= newTypeParam.typeBounds();
//				for (int k= 0; k < typeBounds.length; k++) {
//					newTypeBounds.add(imports.addImport(typeBounds[k], ast, context));
//				}
//			}
			typeParameters.add(newTypeParam);
		}

		decl.setReturnType(imports.addImport(binding.getReturnType(), ast, context));

		List parameters= createParameters(unit.getJavaProject(), imports, context, ast, binding, decl);

//		List thrownExceptions= decl.thrownExceptions();
//		ITypeBinding[] excTypes= binding.getExceptionTypes();
//		for (int i= 0; i < excTypes.length; i++) {
//			String excTypeName= imports.addImport(excTypes[i], context);
//			thrownExceptions.add(ASTNodeFactory.newName(ast, excTypeName));
//		}

		String delimiter= unit.findRecommendedLineSeparator();
		if (!deferred) {
			Map options= unit.getJavaProject().getOptions(true);

			Block body= ast.newBlock();
			decl.setBody(body);

			String bodyStatement= ""; //$NON-NLS-1$
			ITypeBinding declaringType= binding.getDeclaringSymbol();
			if (Modifier.isAbstract(binding.getModifiers()) || declaringType.isInterface()) {
				Expression expression= ASTNodeFactory.newDefaultExpression(ast, decl.getReturnType());
				if (expression != null) {
					ReturnStatement returnStatement= ast.newReturnStatement();
					returnStatement.setExpression(expression);
					bodyStatement= ASTNodes.asFormattedString(returnStatement, 0, delimiter, options);
				}
			} else {
				DotIdentifierExpression dotId = ast.newDotIdentifierExpression();
				dotId.setExpression(ast.newSuperLiteral());
				dotId.setName(ast.newSimpleName(binding.getName()));
				
				CallExpression invocation = ast.newCallExpression();
				invocation.setExpression(dotId);
				
				Argument varDecl= null;
				for (Iterator iterator= parameters.iterator(); iterator.hasNext();) {
					varDecl= (Argument) iterator.next();
					invocation.arguments().add(ast.newSimpleName(varDecl.getName().getIdentifier()));
				}
				Expression expression= invocation;
				Type returnType= decl.getReturnType();
				if (returnType instanceof PrimitiveType && ((PrimitiveType) returnType).getPrimitiveTypeCode().equals(PrimitiveType.Code.VOID)) {
					bodyStatement= ASTNodes.asFormattedString(ast.newExpressionStatement(expression), 0, delimiter, options);
				} else {
					ReturnStatement returnStatement= ast.newReturnStatement();
					returnStatement.setExpression(expression);
					bodyStatement= ASTNodes.asFormattedString(returnStatement, 0, delimiter, options);
				}
			}

			String placeHolder= CodeGeneration.getMethodBodyContent(unit, type, binding.getName(), false, bodyStatement, delimiter);
			if (placeHolder != null) {
				ASTNode todoNode= rewrite.createStringPlaceholder(placeHolder, ASTNode.RETURN_STATEMENT);
				body.statements().add((Statement) todoNode);
			}
		}

//		if (settings != null && settings.createComments) {
//			String string= CodeGeneration.getMethodComment(unit, type, decl, binding, delimiter);
//			if (string != null) {
//				Javadoc javadoc= (Javadoc) rewrite.createStringPlaceholder(string, ASTNode.JAVADOC);
//				decl.setJavadoc(javadoc);
//			}
//		}
//		if (settings != null && settings.overrideAnnotation && JavaModelUtil.is50OrHigher(unit.getJavaProject())) {
//			addOverrideAnnotation(unit.getJavaProject(), rewrite, decl, binding);
//		}

		return decl;
	}
	
	private static List getImplementationModifiers(AST ast, IMethodBinding method, boolean deferred) {
		long modifiers= method.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.PRIVATE& ~Modifier.PUBLIC;
		if (deferred) {
			modifiers= modifiers & ~Modifier.PROTECTED;
		}
		modifiers= modifiers | Modifier.OVERRIDE;
		return ASTNodeFactory.newModifiers(ast, modifiers);
	}
	
	private static List createParameters(IJavaProject project, ImportRewrite imports, ImportRewriteContext context, AST ast, IMethodBinding binding, FunctionDeclaration decl) {
		List<Argument> parameters= decl.arguments();
		ITypeBinding[] params= binding.getParameterTypes();
		String[] paramNames= StubUtility.suggestArgumentNames(project, binding);
		for (int i= 0; i < params.length; i++) {
			Argument var = ast.newArgument();
			if (binding.getVarargs() != IMethod.VARARGS_NO && i == params.length - 1) {
				StringBuffer buffer= new StringBuffer(imports.addImport(params[i], context));
				var.setType(ASTNodeFactory.newType(ast, buffer.toString()));
				decl.setVariadic(true);
			} else
				var.setType(imports.addImport(params[i], ast, context));
			var.setName(ast.newSimpleName(paramNames[i]));
			parameters.add(var);
		}
		return parameters;
	}

}
