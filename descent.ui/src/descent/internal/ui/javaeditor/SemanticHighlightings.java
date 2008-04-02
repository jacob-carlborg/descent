/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.internal.ui.javaeditor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;

import descent.core.Flags;
import descent.core.dom.ASTNode;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IBinding;
import descent.core.dom.IMethodBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.Modifier;
import descent.core.dom.QualifiedName;
import descent.core.dom.SimpleName;
import descent.core.dom.StructuralPropertyDescriptor;
import descent.core.dom.Type;
import descent.core.dom.VariableDeclarationFragment;
import descent.internal.corext.dom.Bindings;
import descent.ui.PreferenceConstants;


/**
 * Semantic highlightings
 *
 * @since 3.0
 */
public class SemanticHighlightings {

	/**
	 * A named preference part that controls the highlighting of static final fields.
	 */
	public static final String STATIC_FINAL_FIELD="staticFinalField"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of static fields.
	 */
	public static final String STATIC_FIELD="staticField"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of constants.
	 */
	public static final String CONSTANT="constant"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of fields.
	 */
	public static final String FIELD="field"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of global variables.
	 */
	public static final String GLOBAL_VARIABLE="globalVariable"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of aliases.
	 */
	public static final String ALIAS="alias"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of typedefs.
	 */
	public static final String TYPEDEF="typedef"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of method declarations.
	 */
	public static final String METHOD_DECLARATION="methodDeclarationName"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of function declarations.
	 */
	public static final String FUNCTION_DECLARATION="functionDeclarationName"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of static method invocations.
	 */
	public static final String STATIC_METHOD_INVOCATION="staticMethodInvocation"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of inherited method invocations.
	 */
	public static final String INHERITED_METHOD_INVOCATION="inheritedMethodInvocation"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of annotation element references.
	 * @since 3.1
	 */
	public static final String ANNOTATION_ELEMENT_REFERENCE="annotationElementReference"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of abstract method invocations.
	 */
	public static final String ABSTRACT_METHOD_INVOCATION="abstractMethodInvocation"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of local variables.
	 */
	public static final String LOCAL_VARIABLE_DECLARATION="localVariableDeclaration"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of local variables.
	 */
	public static final String LOCAL_VARIABLE="localVariable"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of parameter variables.
	 */
	public static final String PARAMETER_VARIABLE="parameterVariable"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of deprecated members.
	 */
	public static final String DEPRECATED="deprecatedMember"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of type parameters.
	 * @since 3.1
	 */
	public static final String TYPE_VARIABLE="typeParameter"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of methods
	 * (invocations and declarations).
	 *
	 * @since 3.1
	 */
	public static final String METHOD="method"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of functions
	 * (invocations and declarations).
	 *
	 * @since 3.1
	 */
	public static final String FUNCTION="function"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of auto(un)boxed
	 * expressions.
	 *
	 * @since 3.1
	 */
	public static final String AUTOBOXING="autoboxing"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of classes.
	 *
	 * @since 3.2
	 */
	public static final String CLASS="class"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of templates.
	 *
	 * @since 3.2
	 */
	public static final String TEMPLATE="template"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of enums.
	 *
	 * @since 3.2
	 */
	public static final String ENUM="enum"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of enum members.
	 *
	 * @since 3.2
	 */
	public static final String ENUM_MEMBER="enumMember"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of interfaces.
	 *
	 * @since 3.2
	 */
	public static final String INTERFACE="interface"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of structs.
	 *
	 * @since 3.2
	 */
	public static final String STRUCT="struct"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of structs.
	 *
	 * @since 3.2
	 */
	public static final String UNION="union"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of annotations.
	 *
	 * @since 3.2
	 */
	public static final String ANNOTATION="annotation"; //$NON-NLS-1$
	
	/**
	 * A named preference part that controls the highlighting of type arguments.
	 *
	 * @since 3.2
	 */
	public static final String TYPE_ARGUMENT="typeArgument"; //$NON-NLS-1$
	
	/**
	 * Semantic highlightings
	 */
	private static SemanticHighlighting[] fgSemanticHighlightings;

	/**
	 * Semantic highlighting for static fields.
	 */
	private static final class StaticFieldHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return STATIC_FIELD;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 192);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_staticField;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= token.getBinding();
			if (binding != null && binding.getKind() == IBinding.VARIABLE) {
				IVariableBinding var = (IVariableBinding) binding;
				IBinding declaringSymbol = var.getDeclaringSymbol();
				return var.isVariable() &&
					!var.isLocal() &&
					!var.isParameter() &&
					declaringSymbol != null &&
					declaringSymbol.getKind() == IBinding.TYPE &&
					(binding.getModifiers() & Modifier.STATIC) == Modifier.STATIC; 
			}
			return false;
		}
		
	}
	
	/**
	 * Semantic highlighting for constants.
	 */
	private static final class ConstantHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return CONSTANT;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 96);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_constant;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= token.getBinding();
			return binding != null && binding.getKind() == IBinding.VARIABLE &&
				(((IVariableBinding) binding).getModifiers() & Flags.AccConst) != 0;
		}
	}

	/**
	 * Semantic highlighting for fields.
	 */
	private static final class FieldHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return FIELD;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 192);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_field;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= token.getBinding();
			if (binding != null && binding.getKind() == IBinding.VARIABLE) {
				IVariableBinding var = (IVariableBinding) binding;
				IBinding declaringSymbol = var.getDeclaringSymbol();
				return var.isVariable() &&
					!var.isLocal() &&
					!var.isParameter() &&
					declaringSymbol != null &&
					declaringSymbol.getKind() == IBinding.TYPE;
			}
			return false;
		}
	}
	
	/**
	 * Semantic highlighting for global variables.
	 */
	private static final class GlobalVariableHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return GLOBAL_VARIABLE;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 96);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_global_variable;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= token.getBinding();
			if (binding != null && binding.getKind() == IBinding.VARIABLE) {
				IVariableBinding var = (IVariableBinding) binding;
				IBinding declaringSymbol = var.getDeclaringSymbol();
				return var.isVariable() &&
					!var.isLocal() &&
					!var.isParameter() &&
					declaringSymbol != null &&
					declaringSymbol.getKind() == IBinding.COMPILATION_UNIT;
			}
			return false;
		}
	}
	
	/**
	 * Semantic highlighting for aliases.
	 */
	private static final class AliasHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return ALIAS;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 128, 128);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_alias;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= token.getBinding();
			return binding != null && binding.getKind() == IBinding.VARIABLE &&
				((IVariableBinding) binding).isAlias();
		}
	}
	
	/**
	 * Semantic highlighting for typedefs.
	 */
	private static final class TypedefHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return TYPEDEF;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(128, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_typedef;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= token.getBinding();
			return binding != null && binding.getKind() == IBinding.VARIABLE &&
				((IVariableBinding) binding).isTypedef();
		}
	}

	/**
	 * Semantic highlighting for method declarations.
	 */
	private static final class MethodDeclarationHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return METHOD_DECLARATION;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_methodDeclaration;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#isMatched(descent.core.dom.ASTNode)
		 */
		public boolean consumes(SemanticToken token) {
			StructuralPropertyDescriptor location= token.getNode().getLocationInParent();
			if (location == FunctionDeclaration.NAME_PROPERTY) {
				IBinding binding = token.getBinding();
				if (binding != null && binding.getKind() == IBinding.METHOD) {
					IMethodBinding methodBinding = (IMethodBinding) binding;
					IBinding declaringSymbol = methodBinding.getDeclaringSymbol();
					return declaringSymbol != null && declaringSymbol.getKind() == IBinding.TYPE;
				}
			}
			return false;
		}
	}
	
	/**
	 * Semantic highlighting for function declarations.
	 */
	private static final class FunctionDeclarationHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return FUNCTION_DECLARATION;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_functionDeclaration;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#isMatched(descent.core.dom.ASTNode)
		 */
		public boolean consumes(SemanticToken token) {
			StructuralPropertyDescriptor location= token.getNode().getLocationInParent();
			if (location == FunctionDeclaration.NAME_PROPERTY) {
				IBinding binding = token.getBinding();
				if (binding != null && binding.getKind() == IBinding.METHOD) {
					IMethodBinding methodBinding = (IMethodBinding) binding;
					IBinding declaringSymbol = methodBinding.getDeclaringSymbol();
					return declaringSymbol != null && declaringSymbol.getKind() == IBinding.COMPILATION_UNIT;
				}
			}
			return false;
		}
	}

	/**
	 * Semantic highlighting for static method invocations.
	 */
	private static final class StaticMethodInvocationHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return STATIC_METHOD_INVOCATION;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_staticMethodInvocation;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#isMatched(descent.core.dom.ASTNode)
		 */
		public boolean consumes(SemanticToken token) {
			SimpleName node= token.getNode();
			if (node.isDeclaration())
				return false;

			IBinding binding= token.getBinding();
			return binding != null && binding.getKind() == IBinding.METHOD && (binding.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
		}
	}

	/**
	 * Semantic highlighting for abstract method invocations.
	 */
	private static final class AbstractMethodInvocationHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return ABSTRACT_METHOD_INVOCATION;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_abstractMethodInvocation;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#isMatched(descent.core.dom.ASTNode)
		 */
		public boolean consumes(SemanticToken token) {
			SimpleName node= token.getNode();
			if (node.isDeclaration())
				return false;

			IBinding binding= token.getBinding();
			boolean isAbstractMethod= binding != null && binding.getKind() == IBinding.METHOD && (binding.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
			if (!isAbstractMethod)
				return false;

			return true;
		}
	}

	/**
	 * Semantic highlighting for inherited method invocations.
	 */
	private static final class InheritedMethodInvocationHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return INHERITED_METHOD_INVOCATION;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_inheritedMethodInvocation;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#isMatched(descent.core.dom.ASTNode)
		 */
		public boolean consumes(SemanticToken token) {
			SimpleName node= token.getNode();
			if (node.isDeclaration())
				return false;

			IBinding binding= token.getBinding();
			if (binding == null || binding.getKind() != IBinding.METHOD)
				return false;

			ITypeBinding currentType= Bindings.getBindingOfParentType(node);
			ITypeBinding declaringType= (ITypeBinding) ((IMethodBinding) binding).getDeclaringSymbol();
			if (currentType == declaringType || currentType == null)
				return false;

			return Bindings.isSuperType(declaringType, currentType);
		}
	}

	/**
	 * Semantic highlighting for method invocations.
	 */
	private static final class MethodHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return METHOD;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_method;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#isMatched(descent.core.dom.ASTNode)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= getMethodBinding(token);
			if (binding != null && binding.getKind() == IBinding.METHOD) {
				IMethodBinding methodBinding = (IMethodBinding) binding;
				IBinding declaringSymbol = methodBinding.getDeclaringSymbol();
				return declaringSymbol != null && declaringSymbol.getKind() == IBinding.TYPE;
			}
			return false;
		}

		/**
		 * Extracts the method binding from the token's simple name. The method
		 * binding is either the token's binding (if the parent of token is a
		 * method call or declaration) or the constructor binding of a class
		 * instance creation if the node is the type name of a class instance
		 * creation.
		 *
		 * @param token the token to extract the method binding from
		 * @return the corresponding method binding, or <code>null</code>
		 */
		private IBinding getMethodBinding(SemanticToken token) {
			IBinding binding= null;
			// work around: https://bugs.eclipse.org/bugs/show_bug.cgi?id=62605
			ASTNode node= token.getNode();
			ASTNode parent= node.getParent();
			while (isTypePath(node, parent)) {
				node= parent;
				parent= parent.getParent();
			}

			/* TODO JDT UI Java -> D
			if (parent != null && node.getLocationInParent() == ClassInstanceCreation.TYPE_PROPERTY)
				binding= ((ClassInstanceCreation) parent).resolveConstructorBinding();
			else
			*/
				binding= token.getBinding();
			return binding;
		}

		/**
		 * Returns <code>true</code> if the given child/parent nodes are valid
		 * sub nodes of a <code>Type</code> ASTNode.
		 * @param child the child node
		 * @param parent the parent node
		 * @return <code>true</code> if the nodes may be the sub nodes of a type node, false otherwise
		 */
		private boolean isTypePath(ASTNode child, ASTNode parent) {
			if (parent instanceof Type) {
				/* TODO JDT UI Java -> D
				StructuralPropertyDescriptor location= child.getLocationInParent();
				return location == ParameterizedType.TYPE_PROPERTY || location == SimpleType.NAME_PROPERTY;
				*/
				return false;
			} else if (parent instanceof QualifiedName) {
				StructuralPropertyDescriptor location= child.getLocationInParent();
				return location == QualifiedName.NAME_PROPERTY;
			}
			return false;
		}
	}
	
	/**
	 * Semantic highlighting for function invocations.
	 */
	private static final class FunctionHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return FUNCTION;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_function;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#isMatched(descent.core.dom.ASTNode)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= getMethodBinding(token);
			if (binding != null && binding.getKind() == IBinding.METHOD) {
				IMethodBinding methodBinding = (IMethodBinding) binding;
				IBinding declaringSymbol = methodBinding.getDeclaringSymbol();
				return declaringSymbol != null && declaringSymbol.getKind() == IBinding.COMPILATION_UNIT;
			}
			return false;
		}

		/**
		 * Extracts the method binding from the token's simple name. The method
		 * binding is either the token's binding (if the parent of token is a
		 * method call or declaration) or the constructor binding of a class
		 * instance creation if the node is the type name of a class instance
		 * creation.
		 *
		 * @param token the token to extract the method binding from
		 * @return the corresponding method binding, or <code>null</code>
		 */
		private IBinding getMethodBinding(SemanticToken token) {
			IBinding binding= null;
			// work around: https://bugs.eclipse.org/bugs/show_bug.cgi?id=62605
			ASTNode node= token.getNode();
			ASTNode parent= node.getParent();
			while (isTypePath(node, parent)) {
				node= parent;
				parent= parent.getParent();
			}

			/* TODO JDT UI Java -> D
			if (parent != null && node.getLocationInParent() == ClassInstanceCreation.TYPE_PROPERTY)
				binding= ((ClassInstanceCreation) parent).resolveConstructorBinding();
			else
			*/
				binding= token.getBinding();
			return binding;
		}

		/**
		 * Returns <code>true</code> if the given child/parent nodes are valid
		 * sub nodes of a <code>Type</code> ASTNode.
		 * @param child the child node
		 * @param parent the parent node
		 * @return <code>true</code> if the nodes may be the sub nodes of a type node, false otherwise
		 */
		private boolean isTypePath(ASTNode child, ASTNode parent) {
			if (parent instanceof Type) {
				/* TODO JDT UI Java -> D
				StructuralPropertyDescriptor location= child.getLocationInParent();
				return location == ParameterizedType.TYPE_PROPERTY || location == SimpleType.NAME_PROPERTY;
				*/
				return false;
			} else if (parent instanceof QualifiedName) {
				StructuralPropertyDescriptor location= child.getLocationInParent();
				return location == QualifiedName.NAME_PROPERTY;
			}
			return false;
		}
	}

	/**
	 * Semantic highlighting for local variable declarations.
	 */
	private static final class LocalVariableDeclarationHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return LOCAL_VARIABLE_DECLARATION;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_localVariableDeclaration;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			SimpleName node= token.getNode();
			StructuralPropertyDescriptor location= node.getLocationInParent();
			if (location == VariableDeclarationFragment.NAME_PROPERTY) {
				IBinding binding = token.getBinding();
				return binding != null && binding instanceof IVariableBinding && ((IVariableBinding) binding).isLocal();
//				ASTNode parent= node.getParent();
//				if (parent instanceof VariableDeclaration) {
//					parent= parent.getParent();
//					return parent == null || !(parent instanceof VariableDeclaration);
//				}
			}
			return false;
		}
	}

	/**
	 * Semantic highlighting for local variables.
	 */
	private static final class LocalVariableHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return LOCAL_VARIABLE;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_localVariable;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= token.getBinding();
			if (binding != null && binding.getKind() == IBinding.VARIABLE 
					&& ((IVariableBinding) binding).isLocal()) {
				return true;
				/* TODO JDT UI binding
				ASTNode decl= token.getRoot().findDeclaringNode(binding);
				return decl instanceof VariableDeclaration;
				*/
			}
			return false;
		}
	}

	/**
	 * Semantic highlighting for parameter variables.
	 */
	private static final class ParameterVariableHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return PARAMETER_VARIABLE;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_parameterVariable;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= token.getBinding();
			return binding != null && binding.getKind() == IBinding.VARIABLE && ((IVariableBinding) binding).isParameter();
		}
	}

	/**
	 * Semantic highlighting for deprecated symbols.
	 */
	private static final class DeprecatedHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return DEPRECATED;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isStrikethroughByDefault()
		 * @since 3.1
		 */
		public boolean isStrikethroughByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return true;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_deprecatedMember;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding= token.getBinding();
			return binding != null ? binding.isDeprecated() : false;
		}
	}
	
	/**
	 * Semantic highlighting for classes.
	 * @since 3.2
	 */
	private static final class ClassHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return CLASS;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 80, 50);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_classes;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {

			// 1: match types
			SimpleName name= token.getNode();
			ASTNode node= name.getParent();
			int nodeType= node.getNodeType();
			if (isNotAggregateContainer(nodeType)) {
				return false;
			}
			while (nodeType == ASTNode.QUALIFIED_NAME) {
				node= node.getParent();
				nodeType= node.getNodeType();
				if (nodeType == ASTNode.IMPORT_DECLARATION)
					return false;
			}

			// 2: match classes
			IBinding binding= token.getBinding();
			return binding instanceof ITypeBinding && ((ITypeBinding) binding).isClass();
		}
	}
	
	/**
	 * Semantic highlighting for templates.
	 * @since 3.2
	 */
	private static final class TemplateHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return TEMPLATE;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(128, 64, 64);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_templates;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {

			// 1: match types
			SimpleName name= token.getNode();
			ASTNode node= name.getParent();
			int nodeType= node.getNodeType();
			if (nodeType != ASTNode.SIMPLE_TYPE && nodeType != ASTNode.THIS_LITERAL && nodeType != ASTNode.QUALIFIED_TYPE  && nodeType != ASTNode.QUALIFIED_NAME && nodeType != ASTNode.TEMPLATE_DECLARATION
					&& nodeType != ASTNode.ALIAS_DECLARATION_FRAGMENT && nodeType != ASTNode.TYPEDEF_DECLARATION_FRAGMENT
					&& nodeType != ASTNode.CALL_EXPRESSION // For opCall
					&& nodeType != ASTNode.DOT_IDENTIFIER_EXPRESSION
					&& nodeType != ASTNode.TYPEOF_TYPE
					&& nodeType != ASTNode.SELECTIVE_IMPORT
					&& nodeType != ASTNode.TEMPLATE_TYPE)
				return false;
			while (nodeType == ASTNode.QUALIFIED_NAME) {
				node= node.getParent();
				nodeType= node.getNodeType();
				if (nodeType == ASTNode.IMPORT_DECLARATION)
					return false;
			}

			// 2: match templates
			IBinding binding= token.getBinding();
			return binding instanceof ITypeBinding && 
				((ITypeBinding) binding).isTemplate() && 
				!((ITypeBinding) binding).isClass() && 
				!((ITypeBinding) binding).isInterface() &&
				!((ITypeBinding) binding).isUnion() &&
				!((ITypeBinding) binding).isStruct();
		}
	}
	
	/**
	 * Semantic highlighting for structs.
	 * @since 3.2
	 */
	private static final class StructHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return STRUCT;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(50, 0, 80);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_structs;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {

			// 1: match types
			SimpleName name= token.getNode();
			ASTNode node= name.getParent();
			int nodeType= node.getNodeType();
			if (isNotAggregateContainer(nodeType)) {
				return false;
			}
			while (nodeType == ASTNode.QUALIFIED_NAME) {
				node= node.getParent();
				nodeType= node.getNodeType();
				if (nodeType == ASTNode.IMPORT_DECLARATION)
					return false;
			}

			// 2: match classes
			IBinding binding= token.getBinding();
			return binding instanceof ITypeBinding && ((ITypeBinding) binding).isStruct();
		}
	}
	
	/**
	 * Semantic highlighting for unions.
	 * @since 3.2
	 */
	private static final class UnionHighlighting extends SemanticHighlighting {

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return UNION;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(50, 80, 0);
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}

		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_unions;
		}

		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {

			// 1: match types
			SimpleName name= token.getNode();
			ASTNode node= name.getParent();
			int nodeType= node.getNodeType();
			if (isNotAggregateContainer(nodeType)) {
				return false;
			}
			while (nodeType == ASTNode.QUALIFIED_NAME) {
				node= node.getParent();
				nodeType= node.getNodeType();
				if (nodeType == ASTNode.IMPORT_DECLARATION)
					return false;
			}

			// 2: match classes
			IBinding binding= token.getBinding();
			return binding instanceof ITypeBinding && ((ITypeBinding) binding).isUnion();
		}
	}

	/**
	 * Semantic highlighting for enums.
	 * @since 3.2
	 */
	private static final class EnumHighlighting extends SemanticHighlighting {
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return ENUM;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(100, 70, 50);
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_enums;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			
			// 1: match types
			SimpleName name= token.getNode();
			ASTNode node= name.getParent();
			int nodeType= node.getNodeType();
			if (isNotAggregateContainer(nodeType))
				return false;
			while (nodeType == ASTNode.QUALIFIED_NAME) {
				node= node.getParent();
				nodeType= node.getNodeType();
				if (nodeType == ASTNode.IMPORT_DECLARATION)
					return false;
			}

			// 2: match enums
			IBinding binding= token.getBinding();
			return binding instanceof ITypeBinding && ((ITypeBinding) binding).isEnum();
		}
	}
	
	/**
	 * Semantic highlighting for enum members.
	 * @since 3.2
	 */
	private static final class EnumMemberHighlighting extends SemanticHighlighting {
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return ENUM_MEMBER;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(0, 0, 96);
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return true;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return true;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_enum_members;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			IBinding binding =  token.getBinding();
			return binding != null && 
				binding.getKind() == IBinding.VARIABLE &&
				((IVariableBinding) binding).isEnumConstant();
		}
	}
	
	/**
	 * Semantic highlighting for interfaces.
	 * @since 3.2
	 */
	private static final class InterfaceHighlighting extends SemanticHighlighting {
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
		 */
		public String getPreferenceKey() {
			return INTERFACE;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
		 */
		public RGB getDefaultTextColor() {
			return new RGB(50, 63, 112);
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
		 */
		public boolean isBoldByDefault() {
			return false;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
		 */
		public boolean isItalicByDefault() {
			return false;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
		 */
		public boolean isEnabledByDefault() {
			return false;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
		 */
		public String getDisplayName() {
			return JavaEditorMessages.SemanticHighlighting_interfaces;
		}
		
		/*
		 * @see descent.internal.ui.javaeditor.SemanticHighlighting#consumes(descent.internal.ui.javaeditor.SemanticToken)
		 */
		public boolean consumes(SemanticToken token) {
			
			// 1: match types
			SimpleName name= token.getNode();
			ASTNode node= name.getParent();
			int nodeType= node.getNodeType();
			if (isNotAggregateContainer(nodeType)) {
				return false;
			}
			while (nodeType == ASTNode.QUALIFIED_NAME) {
				node= node.getParent();
				nodeType= node.getNodeType();
				if (nodeType == ASTNode.IMPORT_DECLARATION)
					return false;
			}

			// 2: match interfaces
			IBinding binding= token.getBinding();
			return binding instanceof ITypeBinding && ((ITypeBinding) binding).isInterface();
		}
	}
	
	/**
	 * A named preference that controls the given semantic highlighting's color.
	 *
	 * @param semanticHighlighting the semantic highlighting
	 * @return the color preference key
	 */
	public static String getColorPreferenceKey(SemanticHighlighting semanticHighlighting) {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey() + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_COLOR_SUFFIX;
	}

	/**
	 * A named preference that controls if the given semantic highlighting has the text attribute bold.
	 *
	 * @param semanticHighlighting the semantic highlighting
	 * @return the bold preference key
	 */
	public static String getBoldPreferenceKey(SemanticHighlighting semanticHighlighting) {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey() + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_BOLD_SUFFIX;
	}

	/**
	 * A named preference that controls if the given semantic highlighting has the text attribute italic.
	 *
	 * @param semanticHighlighting the semantic highlighting
	 * @return the italic preference key
	 */
	public static String getItalicPreferenceKey(SemanticHighlighting semanticHighlighting) {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey() + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ITALIC_SUFFIX;
	}

	/**
	 * A named preference that controls if the given semantic highlighting has the text attribute strikethrough.
	 *
	 * @param semanticHighlighting the semantic highlighting
	 * @return the strikethrough preference key
	 * @since 3.1
	 */
	public static String getStrikethroughPreferenceKey(SemanticHighlighting semanticHighlighting) {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey() + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_STRIKETHROUGH_SUFFIX;
	}

	/**
	 * A named preference that controls if the given semantic highlighting has the text attribute underline.
	 *
	 * @param semanticHighlighting the semantic highlighting
	 * @return the underline preference key
	 * @since 3.1
	 */
	public static String getUnderlinePreferenceKey(SemanticHighlighting semanticHighlighting) {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey() + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_UNDERLINE_SUFFIX;
	}

	/**
	 * A named preference that controls if the given semantic highlighting is enabled.
	 *
	 * @param semanticHighlighting the semantic highlighting
	 * @return the enabled preference key
	 */
	public static String getEnabledPreferenceKey(SemanticHighlighting semanticHighlighting) {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey() + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ENABLED_SUFFIX;
	}

	/**
	 * @return The semantic highlightings, the order defines the precedence of matches, the first match wins.
	 */
	public static SemanticHighlighting[] getSemanticHighlightings() {
		if (fgSemanticHighlightings == null)
			fgSemanticHighlightings= new SemanticHighlighting[] {
				new DeprecatedHighlighting(),
				new EnumMemberHighlighting(),
				new ConstantHighlighting(), // before field and global highlighting
				new StaticFieldHighlighting(),
				new FieldHighlighting(),
				new GlobalVariableHighlighting(),
				new AliasHighlighting(),
				new TypedefHighlighting(),
				new FunctionDeclarationHighlighting(),
				new MethodDeclarationHighlighting(),
				new StaticMethodInvocationHighlighting(),
				new AbstractMethodInvocationHighlighting(),
//				new AnnotationElementReferenceHighlighting(),
//				new InheritedMethodInvocationHighlighting(),
				new ParameterVariableHighlighting(),
				new LocalVariableDeclarationHighlighting(),
				new LocalVariableHighlighting(),
				new FunctionHighlighting(),
				new MethodHighlighting(), // before types to get ctors
				new ClassHighlighting(),
				new StructHighlighting(),
				new UnionHighlighting(),
				new EnumHighlighting(),
				new InterfaceHighlighting(),
				new TemplateHighlighting(),
			};
		return fgSemanticHighlightings;
	}

	/**
	 * Initialize default preferences in the given preference store.
	 * @param store The preference store
	 */
	public static void initDefaults(IPreferenceStore store) {
		SemanticHighlighting[] semanticHighlightings= getSemanticHighlightings();
		for (int i= 0, n= semanticHighlightings.length; i < n; i++) {
			SemanticHighlighting semanticHighlighting= semanticHighlightings[i];
			PreferenceConverter.setDefault(store, SemanticHighlightings.getColorPreferenceKey(semanticHighlighting), semanticHighlighting.getDefaultTextColor());
			store.setDefault(SemanticHighlightings.getBoldPreferenceKey(semanticHighlighting), semanticHighlighting.isBoldByDefault());
			store.setDefault(SemanticHighlightings.getItalicPreferenceKey(semanticHighlighting), semanticHighlighting.isItalicByDefault());
			store.setDefault(SemanticHighlightings.getStrikethroughPreferenceKey(semanticHighlighting), semanticHighlighting.isStrikethroughByDefault());
			store.setDefault(SemanticHighlightings.getUnderlinePreferenceKey(semanticHighlighting), semanticHighlighting.isUnderlineByDefault());
			store.setDefault(SemanticHighlightings.getEnabledPreferenceKey(semanticHighlighting), semanticHighlighting.isEnabledByDefault());
		}

		convertMethodHighlightingPreferences(store);
		convertAnnotationHighlightingPreferences(store);
	}

	/**
	 * Tests whether <code>event</code> in <code>store</code> affects the
	 * enablement of semantic highlighting.
	 *
	 * @param store the preference store where <code>event</code> was observed
	 * @param event the property change under examination
	 * @return <code>true</code> if <code>event</code> changed semantic
	 *         highlighting enablement, <code>false</code> if it did not
	 * @since 3.1
	 */
	public static boolean affectsEnablement(IPreferenceStore store, PropertyChangeEvent event) {
		String relevantKey= null;
		SemanticHighlighting[] highlightings= getSemanticHighlightings();
		for (int i= 0; i < highlightings.length; i++) {
			if (event.getProperty().equals(getEnabledPreferenceKey(highlightings[i]))) {
				relevantKey= event.getProperty();
				break;
			}
		}
		if (relevantKey == null)
			return false;

		for (int i= 0; i < highlightings.length; i++) {
			String key= getEnabledPreferenceKey(highlightings[i]);
			if (key.equals(relevantKey))
				continue;
			if (store.getBoolean(key))
				return false; // another is still enabled or was enabled before
		}

		// all others are disabled, so toggling relevantKey affects the enablement
		return true;
	}

	/**
	 * Tests whether semantic highlighting is currently enabled.
	 *
	 * @param store the preference store to consult
	 * @return <code>true</code> if semantic highlighting is enabled,
	 *         <code>false</code> if it is not
	 * @since 3.1
	 */
	public static boolean isEnabled(IPreferenceStore store) {
		SemanticHighlighting[] highlightings= getSemanticHighlightings();
		boolean enable= false;
		for (int i= 0; i < highlightings.length; i++) {
			String enabledKey= getEnabledPreferenceKey(highlightings[i]);
			if (store.getBoolean(enabledKey)) {
				enable= true;
				break;
			}
		}

		return enable;
	}

	/**
	 * In 3.0, methods were highlighted by a rule-based word matcher that
	 * matched any identifier that was followed by possibly white space and a
	 * left parenthesis.
	 * <p>
	 * With generics, this does not work any longer for constructors of generic
	 * types, and the highlighting has been moved to be a semantic highlighting.
	 * Because different preference key naming schemes are used, we have to
	 * migrate the old settings to the new ones, which is done here. Nothing
	 * needs to be done if the old settings were set to the default values.
	 * </p>
	 *
	 * @param store the preference store to migrate
	 * @since 3.1
	 */
	private static void convertMethodHighlightingPreferences(IPreferenceStore store) {
		String colorkey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + METHOD + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_COLOR_SUFFIX;
		String boldkey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + METHOD + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_BOLD_SUFFIX;
		String italickey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + METHOD + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ITALIC_SUFFIX;
		String enabledkey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + METHOD + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ENABLED_SUFFIX;

		String oldColorkey= PreferenceConstants.EDITOR_JAVA_METHOD_NAME_COLOR;
		String oldBoldkey= PreferenceConstants.EDITOR_JAVA_METHOD_NAME_BOLD;
		String oldItalickey= PreferenceConstants.EDITOR_JAVA_METHOD_NAME_ITALIC;

		if (conditionalReset(store, oldColorkey, colorkey)
				|| conditionalReset(store, oldBoldkey, boldkey)
				|| conditionalReset(store, oldItalickey, italickey)) {
			store.setValue(enabledkey, true);
		}

	}

	/**
	 * In 3.1, annotations were highlighted by a rule-based word matcher that matched any identifier
	 * preceded by an '@' sign and possibly white space.
	 * <p>
	 * This does not work when there is a comment between the '@' and the annotation, results in
	 * stale highlighting if there is a new line between the '@' and the annotation, and does not
	 * work for highlighting annotation declarations. Because different preference key naming
	 * schemes are used, we have to migrate the old settings to the new ones, which is done here.
	 * Nothing needs to be done if the old settings were set to the default values.
	 * </p>
	 * 
	 * @param store the preference store to migrate
	 * @since 3.2
	 */
	private static void convertAnnotationHighlightingPreferences(IPreferenceStore store) {
		String colorkey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + ANNOTATION + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_COLOR_SUFFIX;
		String boldkey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + ANNOTATION + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_BOLD_SUFFIX;
		String italickey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + ANNOTATION + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ITALIC_SUFFIX;
		String strikethroughKey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + ANNOTATION + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_STRIKETHROUGH_SUFFIX;
		String underlineKey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + ANNOTATION + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_UNDERLINE_SUFFIX;
		String enabledkey= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + ANNOTATION + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ENABLED_SUFFIX;
		
		String oldColorkey= PreferenceConstants.EDITOR_JAVA_ANNOTATION_COLOR;
		String oldBoldkey= PreferenceConstants.EDITOR_JAVA_ANNOTATION_BOLD;
		String oldItalickey= PreferenceConstants.EDITOR_JAVA_ANNOTATION_ITALIC;
		String oldStrikethroughKey= PreferenceConstants.EDITOR_JAVA_ANNOTATION_STRIKETHROUGH;
		String oldUnderlineKey= PreferenceConstants.EDITOR_JAVA_ANNOTATION_UNDERLINE;
		
		if (conditionalReset(store, oldColorkey, colorkey)
				|| conditionalReset(store, oldBoldkey, boldkey)
				|| conditionalReset(store, oldItalickey, italickey)
				|| conditionalReset(store, oldStrikethroughKey, strikethroughKey)
				|| conditionalReset(store, oldUnderlineKey, underlineKey)) {
			store.setValue(enabledkey, true);
		}
		
	}
	
	private static boolean isNotAggregateContainer(int nodeType) {
		return nodeType != ASTNode.SIMPLE_TYPE && nodeType != ASTNode.THIS_LITERAL && nodeType != ASTNode.QUALIFIED_TYPE  && nodeType != ASTNode.QUALIFIED_NAME && nodeType != ASTNode.AGGREGATE_DECLARATION
			&& nodeType != ASTNode.ALIAS_DECLARATION_FRAGMENT && nodeType != ASTNode.TYPEDEF_DECLARATION_FRAGMENT
			&& nodeType != ASTNode.CALL_EXPRESSION // For opCall
			&& nodeType != ASTNode.DOT_IDENTIFIER_EXPRESSION
			&& nodeType != ASTNode.TEMPLATE_TYPE
			&& nodeType != ASTNode.TYPEOF_TYPE
			&& nodeType != ASTNode.SELECTIVE_IMPORT;
	}
	
	/**
	 * If the setting pointed to by <code>oldKey</code> is not the default
	 * setting, store that setting under <code>newKey</code> and reset
	 * <code>oldKey</code> to its default setting.
	 * <p>
	 * Returns <code>true</code> if any changes were made.
	 * </p>
	 *
	 * @param store the preference store to read from and write to
	 * @param oldKey the old preference key
	 * @param newKey the new preference key
	 * @return <code>true</code> if <code>store</code> was modified,
	 *         <code>false</code> if not
	 * @since 3.1
	 */
	private static boolean conditionalReset(IPreferenceStore store, String oldKey, String newKey) {
		if (!store.isDefault(oldKey)) {
			if (store.isDefault(newKey))
				store.setValue(newKey, store.getString(oldKey));
			store.setToDefault(oldKey);
			return true;
		}
		return false;
	}

	/**
	 * Do not instantiate
	 */
	private SemanticHighlightings() {
	}
}
