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
package descent.internal.corext.template.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Assert;

import descent.core.CompletionProposal;
import descent.core.CompletionRequestor;
import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.compiler.IProblem;
import descent.internal.ui.JavaPlugin;

/**
 * A completion requester to collect informations on local variables.
 * This class is used for guessing variable names like arrays, collections, etc.
 */
final class CompilationUnitCompletion extends CompletionRequestor {

	/**
	 * Describes a local variable (including parameters) inside the method where
	 * code completion was invoked. Special predicates exist to query whether
	 * a variable can be iterated over. 
	 */
	public final class LocalVariable {
		private static final int UNKNOWN= 0, NONE= 0;
		private static final int ARRAY= 1;
		private static final int COLLECTION= 2;
		private static final int ITERABLE= 4;
		
		/**
		 * The name of the local variable.
		 */
		private final String name;
		
		/**
		 * The signature of the local variable's type.
		 */
		private final String signature;
		
		/* lazily computed properties */
		private int fType= UNKNOWN;
		private int fChecked= NONE;
		private String[] fMemberTypes;
		
		private LocalVariable(String name, String signature) {
			this.name= name;
			this.signature= signature;
		}

		/**
		 * Returns the name of the variable.
		 * 
		 * @return the name of the variable
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Returns <code>true</code> if the type of the local variable is an
		 * array type.
		 * 
		 * @return <code>true</code> if the receiver's type is an array,
		 *         <code>false</code> if not
		 */
		public boolean isArray() {
			// TODO JDT signature
//			if (fType == UNKNOWN && (fChecked & ARRAY) == 0 && Signature.getTypeSignatureKind(signature) == Signature.ARRAY_TYPE_SIGNATURE)
//				fType= ARRAY;
//			fChecked |= ARRAY;
//			return fType == ARRAY;
			return false;
		}

		/**
		 * Returns <code>true</code> if the receiver's type is a subclass of
		 * <code>java.util.Collection</code>, <code>false</code> otherwise.
		 * 
		 * @return <code>true</code> if the receiver's type is a subclass of
		 *         <code>java.util.Collection</code>, <code>false</code>
		 *         otherwise
		 */
		public boolean isCollection() {
			// Collection extends Iterable
			if ((fType == UNKNOWN || fType == ITERABLE) && (fChecked & COLLECTION) == 0 && isImplementorOf("java.util.Collection")) //$NON-NLS-1$
				fType= COLLECTION;
			fChecked |= COLLECTION;
			return fType == COLLECTION;
		}

		/**
		 * Returns <code>true</code> if the receiver's type is a subclass of
		 * <code>java.lang.Iterable</code>, <code>false</code> otherwise.
		 * 
		 * @return <code>true</code> if the receiver's type is a subclass of
		 *         <code>java.lang.Iterable</code>, <code>false</code>
		 *         otherwise
		 */
		public boolean isIterable() {
			if (fType == UNKNOWN && (fChecked & ITERABLE) == 0 && isImplementorOf("java.lang.Iterable")) //$NON-NLS-1$
				fType= ITERABLE;
			fChecked |= ITERABLE;
			return fType == ITERABLE || fType == COLLECTION; // Collection extends Iterable
		}

		/**
		 * Returns <code>true</code> if the receiver's type is an implementor
		 * of <code>interfaceName</code>.
		 * 
		 * @param interfaceName the fully qualified name of the interface
		 * @return <code>true</code> if the receiver's type implements the
		 *         type named <code>interfaceName</code>
		 */
		private boolean isImplementorOf(String interfaceName) {
			return false;
			/* TODO JDT UI type hierarchy
			String implementorName= SignatureUtil.stripSignatureToFQN(signature);
			if (implementorName.length() == 0)
				return false;
			
			// try cheap test first
			if (implementorName.equals(interfaceName))
				return true;

			if (fUnit == null)
				return false;

			IJavaProject project= fUnit.getJavaProject();

			try {
				IType implementor= project.findType(implementorName);
				if (implementor == null)
					return false;

				IType interfaze= project.findType(interfaceName);
				if (interfaze == null)
					return false;

				ITypeHierarchy hierarchy= implementor.newSupertypeHierarchy(null);
				return hierarchy.contains(interfaze);
			} catch (JavaModelException e) {
				// ignore and return false
			}			
			
			return false;
			*/
		}

		/**
		 * Returns the signature of the member type.
		 * 
		 * @return the signature of the member type
		 */
		public String getMemberTypeSignature() {
			return getMemberTypeSignatures()[0];
		}
		
		/**
		 * Returns the signatures of all member type bounds.
		 * 
		 * @return the signatures of all member type bounds
		 */
		public String[] getMemberTypeSignatures() {
			// TODO JDT signature
//			if (isArray()) {
//				return new String[] {Signature.createArraySignature(Signature.getElementType(signature), Signature.getArrayCount(signature) - 1)};
//			} else if (fType == ITERABLE || fType == COLLECTION) {
//				if (fMemberTypes == null) {
//					try {
//						TypeParameterResolver util= new TypeParameterResolver(this);
//						fMemberTypes= util.computeBinding("java.lang.Iterable", 0); //$NON-NLS-1$
//					} catch (JavaModelException e) {
//						fMemberTypes= new String[0];
//					}
//				}
//				if (fMemberTypes.length > 0)
//					return fMemberTypes;
//			}
//			return new String[] {Signature.createTypeSignature("java.lang.Object", true)}; //$NON-NLS-1$
			return new String[0];
		}
		
		/**
		 * Returns the type names of all member type bounds, as they would be 
		 * appear when referenced in the current compilation unit.
		 * 
		 * @return type names of all member type bounds
		 */
		public String[] getMemberTypeNames() {
			// TODO JDT signature
//			String[] signatures= getMemberTypeSignatures();
//			String[] names= new String[signatures.length];
//			
//			for (int i= 0; i < signatures.length; i++) {
//				String sig= signatures[i];
//				String local= (String) fLocalTypes.get(Signature.getElementType(sig));
//				int dim= Signature.getArrayCount(sig);
//				if (local != null && dim > 0) {
//					StringBuffer array= new StringBuffer(local);
//					for (int j= 0; j < dim; j++)
//						array.append("[]"); //$NON-NLS-1$
//					local= array.toString();
//				}
//				if (local != null)
//					names[i]= local;
//				else
//					names[i]= Signature.getSimpleName(Signature.getSignatureSimpleName(sig));
//			}
//			return names;
			return new String[0];
		}
		
		/*
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			String type;
			switch (fType) {
				case ITERABLE:
					type= "ITERABLE"; //$NON-NLS-1$
					break;
				case COLLECTION:
					type= "COLLECTION"; //$NON-NLS-1$
					break;
				case ARRAY:
					type= "ARRAY"; //$NON-NLS-1$
					break;
				default:
					type= "UNKNOWN"; //$NON-NLS-1$
					break;
			}
			return "LocalVariable [name=\"" + name + "\" signature=\"" + signature + "\" type=\"" + type + "\" member=\"" + getMemberTypeSignature() + "\"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}
		
	}
	
	/**
	 * Given a java type, a resolver computes the bounds of type variables
	 * declared in a super type, considering any type constraints along the
	 * inheritance path.
	 */
	private final class TypeParameterResolver {
		private static final String OBJECT_SIGNATURE= "Ljava.lang.Object;"; //$NON-NLS-1$
		
		/* TODO JDT UI type hierarchy
		private final ITypeHierarchy fHierarchy;
		*/
		private final LocalVariable fVariable;
		private final IType fType;
		private final List fBounds= new ArrayList();

		/**
		 * Creates a new type parameter resolver to compute the bindings of type
		 * parameters for the declared type of <code>variable</code>. For any
		 * super type of the type of <code>variable</code>, calling
		 * {@link #computeBinding(IType, int) computeBinding} will find the type
		 * bounds of type variables in the super type, considering any type
		 * constraints along the inheritance path.
		 * 
		 * @param variable the local variable under investigation
		 * @throws JavaModelException if the type of <code>variable</code>
		 *         cannot be found
		 */
		public TypeParameterResolver(LocalVariable variable) throws JavaModelException {
			String typeName= SignatureUtil.stripSignatureToFQN(variable.signature);
			IJavaProject project= fUnit.getJavaProject();
			fType= project.findType(typeName);
			//fHierarchy= fType.newSupertypeHierarchy(null);
			fVariable= variable;
		}
		
		/**
		 * Given a type parameter of <code>superType</code> at position
		 * <code>index</code>, this method computes and returns the (lower)
		 * type bound(s) of that parameter for an instance of <code>fType</code>.
		 * <p>
		 * <code>superType</code> must be a super type of <code>fType</code>,
		 * and <code>superType</code> must have at least
		 * <code>index + 1</code> type parameters.
		 * </p>
		 * 
		 * @param superType the qualified type name of the super type to compute
		 *        the type parameter binding for
		 * @param index the index into the list of type parameters of
		 *        <code>superType</code>
		 * @throws JavaModelException if any java model operation fails
		 */
		public String[] computeBinding(String superType, int index) throws JavaModelException {
			IJavaProject project= fUnit.getJavaProject();
			return computeBinding(project.findType(superType), index);
		}

		/**
		 * Given a type parameter of <code>superType</code> at position
		 * <code>index</code>, this method computes and returns the (lower)
		 * type bound(s) of that parameter for an instance of <code>fType</code>.
		 * <p>
		 * <code>superType</code> must be a super type of <code>fType</code>,
		 * and <code>superType</code> must have at least
		 * <code>index + 1</code> type parameters.
		 * </p>
		 * 
		 * @param superType the super type to compute the type parameter binding
		 *        for
		 * @param index the index into the list of type parameters of
		 *        <code>superType</code>
		 * @throws JavaModelException if any java model operation fails
		 */
		public String[] computeBinding(IType superType, int index) throws JavaModelException {
			initBounds();
			computeTypeParameterBinding(superType, index);
			return (String[]) fBounds.toArray(new String[fBounds.size()]);
		}
		
		/**
		 * Given a type parameter of <code>superType</code> at position
		 * <code>index</code>, this method recursively computes the (lower)
		 * type bound(s) of that parameter for an instance of <code>fType</code>.
		 * <p>
		 * <code>superType</code> must be a super type of <code>fType</code>,
		 * and <code>superType</code> must have at least
		 * <code>index + 1</code> type parameters.
		 * </p>
		 * <p>
		 * The type bounds are stored in <code>fBounds</code>.
		 * </p>
		 * 
		 * @param superType the super type to compute the type parameter binding
		 *        for
		 * @param index the index into the list of type parameters of
		 *        <code>superType</code>
		 * @throws JavaModelException if any java model operation fails
		 */
		private void computeTypeParameterBinding(final IType superType, final int index) throws JavaModelException {
			/* TODO JDT UI type hierarchy
			int nParameters= superType.getTypeParameters().length;
			Assert.isTrue(nParameters > index);
			
			IType[] subTypes= fHierarchy.getSubtypes(superType);
			
			if (subTypes.length == 0) {
				// we have reached down to the base type
				Assert.isTrue(superType.equals(fType));
				
				String match= findMatchingTypeArgument(fVariable.signature, index, fUnit.findPrimaryType());
				String bound= SignatureUtil.getUpperBound(match);
				
				// use the match whether it is a concrete type or not - if not,
				// the generic type will at least be in visible in our context
				// and can be referenced
				addBound(bound);
				return;
			}
			
			IType subType= subTypes[0]; // take the first, as they all lead to fType

			String signature= findMatchingSuperTypeSignature(subType, superType);
			String match= findMatchingTypeArgument(signature, index, subType);
			
			if (isConcreteType(match, subType)) {
				addBound(match);
				return;
			}
			
			ITypeParameter[] typeParameters= subType.getTypeParameters();
			
			for (int k= 0; k < typeParameters.length; k++) {
				ITypeParameter formalParameter= typeParameters[k];
				if (formalParameter.getElementName().equals(SignatureUtil.stripSignatureToFQN(match))) {
					String[] bounds= formalParameter.getBounds();
					for (int i= 0; i < bounds.length; i++) {
						String boundSignature= Signature.createTypeSignature(bounds[i], true);
						addBound(SignatureUtil.qualifySignature(boundSignature, subType));
					}
					computeTypeParameterBinding(subType, k);
					return;
				}
			}
			
			// We have a non-concrete type argument T, but no matching type
			// parameter in the sub type. This can happen if T is declared in
			// the enclosing type. Since it the declaration is probably visible
			// then, its fine to simply copy the match to the bounds and return.
			addBound(match);
			return;
			*/
		}

		/**
		 * Finds and returns the type argument with index <code>index</code>
		 * in the given type super type signature. If <code>signature</code>
		 * is a generic signature, the type parameter at <code>index</code> is
		 * extracted. If the type parameter is an upper bound (<code>? super SomeType</code>),
		 * the type signature of <code>java.lang.Object</code> is returned.
		 * <p>
		 * Also, if <code>signature</code> has no type parameters (i.e. is a
		 * reference to the raw type), the type signature of
		 * <code>java.lang.Object</code> is returned.
		 * </p>
		 * 
		 * @param signature the super type signature from a type's
		 *        <code>extends</code> or <code>implements</code> clause
		 * @param index the index of the type parameter to extract from
		 *        <code>signature</code>
		 * @param context the type context inside which unqualified types should
		 *        be resolved
		 * @return the type argument signature of the type parameter at
		 *         <code>index</code> in <code>signature</code>
		 */
		private String findMatchingTypeArgument(String signature, int index, IType context) {
			// TODO JDT signature
//			String[] typeArguments= Signature.getTypeArguments(signature);
//			Assert.isTrue(typeArguments.length == 0 || typeArguments.length > index);
//			if (typeArguments.length == 0) {
//				// raw binding - bound to Object
//				return OBJECT_SIGNATURE;
//			} else {
//				String bound= SignatureUtil.getUpperBound(typeArguments[index]);
//				return SignatureUtil.qualifySignature(bound, context);
//			}
			return OBJECT_SIGNATURE;
		}

		/**
		 * Finds and returns the super type signature in the
		 * <code>extends</code> or <code>implements</code> clause of
		 * <code>subType</code> that corresponds to <code>superType</code>.
		 * 
		 * @param subType a direct and true sub type of <code>superType</code>
		 * @param superType a direct super type (super class or interface) of
		 *        <code>subType</code>
		 * @return the super type signature of <code>subType</code> referring
		 *         to <code>superType</code>
		 * @throws JavaModelException if extracting the super type signatures
		 *         fails, or if <code>subType</code> contains no super type
		 *         signature to <code>superType</code>
		 */
		private String findMatchingSuperTypeSignature(IType subType, IType superType) throws JavaModelException {
			String[] signatures= getSuperTypeSignatures(subType, superType);
			for (int i= 0; i < signatures.length; i++) {
				String signature= signatures[i];
				String qualified= SignatureUtil.qualifySignature(signature, subType);
				String subFQN= SignatureUtil.stripSignatureToFQN(qualified);
				
				String superFQN= superType.getFullyQualifiedName();
				if (subFQN.equals(superFQN)) {
					return signature;
				}
				
				// handle local types
				if (fLocalTypes.containsValue(subFQN)) {
					return signature;
				}
			}
			
			throw new JavaModelException(new CoreException(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), IStatus.OK, "Illegal hierarchy", null))); //$NON-NLS-1$
		}
		
		/**
		 * Returns the super interface signatures of <code>subType</code> if 
		 * <code>superType</code> is an interface, otherwise returns the super
		 * type signature.
		 * 
		 * @param subType the sub type signature
		 * @param superType the super type signature
		 * @return the super type signatures of <code>subType</code>
		 * @throws JavaModelException if any java model operation fails
		 */
		private String[] getSuperTypeSignatures(IType subType, IType superType) throws JavaModelException {
			if (superType.isInterface())
				return subType.getSuperInterfaceTypeSignatures();
			else
				return new String[] {subType.getSuperclassTypeSignature()};
		}
		
		/**
		 * Clears the collected type bounds and initializes it with
		 * <code>java.lang.Object</code>.
		 */
		private void initBounds() {
			fBounds.clear();
			fBounds.add(OBJECT_SIGNATURE); 
		}

		/**
		 * Filters the current list of type bounds through the additional type
		 * bound described by <code>boundSignature</code>.
		 * 
		 * @param boundSignature the additional bound to add to the list of
		 *        collected bounds
		 */
		private void addBound(String boundSignature) {
			if (SignatureUtil.isJavaLangObject(boundSignature))
				return;
			
			boolean found= false;
			for (ListIterator it= fBounds.listIterator(); it.hasNext();) {
				String old= (String) it.next();
				if (isTrueSubtypeOf(boundSignature, old)) {
					if (!found) {
						it.set(boundSignature);
						found= true;
					} else {
						it.remove();
					}
				}
			}
			if (!found)
				fBounds.add(boundSignature);
		}

		/**
		 * Returns <code>true</code> if <code>subTypeSignature</code>
		 * describes a type which is a true sub type of the type described by
		 * <code>superTypeSignature</code>.
		 * 
		 * @param subTypeSignature the potential subtype's signature
		 * @param superTypeSignature the potential supertype's signature
		 * @return <code>true</code> if the inheritance relationship holds
		 */
		private boolean isTrueSubtypeOf(String subTypeSignature, String superTypeSignature) {
			return false;
			/* TODO JDT UI type hierarchy
			// try cheap test first
			if (subTypeSignature.equals(superTypeSignature))
				return true;
			
			if (SignatureUtil.isJavaLangObject(subTypeSignature))
				return false; // Object has no super types
			
			if (Signature.getTypeSignatureKind(subTypeSignature) != Signature.BASE_TYPE_SIGNATURE && SignatureUtil.isJavaLangObject(superTypeSignature)) 
				return true;
			
			if (fUnit == null)
				return false;
			
			IJavaProject project= fUnit.getJavaProject();
			
			try {
				
				if ((Signature.getTypeSignatureKind(subTypeSignature) & (Signature.TYPE_VARIABLE_SIGNATURE | Signature.CLASS_TYPE_SIGNATURE)) == 0)
					return false;
				IType subType= project.findType(SignatureUtil.stripSignatureToFQN(subTypeSignature));
				if (subType == null)
					return false;
				
				if ((Signature.getTypeSignatureKind(superTypeSignature) & (Signature.TYPE_VARIABLE_SIGNATURE | Signature.CLASS_TYPE_SIGNATURE)) == 0)
					return false;
				IType superType= project.findType(SignatureUtil.stripSignatureToFQN(superTypeSignature));
				if (superType == null)
					return false;
				
				ITypeHierarchy hierarchy= subType.newSupertypeHierarchy(null);
				IType[] types= hierarchy.getAllSupertypes(subType);
				
				for (int i= 0; i < types.length; i++)
					if (types[i].equals(superType))
						return true;
			} catch (JavaModelException e) {
				// ignore and return false
			}			
			
			return false;
			*/
		}
		
		/**
		 * Returns <code>true</code> if <code>signature</code> is a concrete type signature,
		 * <code>false</code> if it is a type variable.
		 * 
		 * @param signature the signature to check
		 * @param context the context inside which to resolve the type
		 * @throws JavaModelException if finding the type fails
		 */
		private boolean isConcreteType(String signature, IType context) throws JavaModelException {
			// TODO JDT signature
//			// Inexpensive check for the variable type first
//			if (Signature.TYPE_VARIABLE_SIGNATURE == Signature.getTypeSignatureKind(signature))
//				return false;
//			
//			// try and resolve otherwise
//			if (context.isBinary()) {
//				return fUnit.getJavaProject().findType(SignatureUtil.stripSignatureToFQN(signature)) != null;
//			} else {
//				return context.resolveType(SignatureUtil.stripSignatureToFQN(signature)) != null;
//			}
			return true;
		}
	}
	
	private ICompilationUnit fUnit;

	private List fLocalVariables= new ArrayList();
	private Map fLocalTypes= new HashMap();

	private boolean fError;

	/**
	 * Creates a compilation unit completion.
	 * 
	 * @param unit the compilation unit, may be <code>null</code>.
	 */
	CompilationUnitCompletion(ICompilationUnit unit) {
		reset(unit);
		setIgnored(CompletionProposal.ANONYMOUS_CLASS_DECLARATION, true);
		setIgnored(CompletionProposal.FIELD_REF, true);
		setIgnored(CompletionProposal.ENUM_MEMBER, true);
		setIgnored(CompletionProposal.KEYWORD, true);
		setIgnored(CompletionProposal.LABEL_REF, true);
		setIgnored(CompletionProposal.METHOD_DECLARATION, true);
		setIgnored(CompletionProposal.METHOD_NAME_REFERENCE, true);
		setIgnored(CompletionProposal.METHOD_REF, true);
		setIgnored(CompletionProposal.OP_CALL, true);
		setIgnored(CompletionProposal.FUNCTION_CALL, true);
		setIgnored(CompletionProposal.COMPILATION_UNIT_REF, true);
		setIgnored(CompletionProposal.POTENTIAL_METHOD_DECLARATION, true);
		setIgnored(CompletionProposal.VARIABLE_DECLARATION, true);
		setIgnored(CompletionProposal.TYPE_REF, true);
		setIgnored(CompletionProposal.TEMPLATE_REF, true);
		setIgnored(CompletionProposal.TEMPLATED_AGGREGATE_REF, true);
		setIgnored(CompletionProposal.TEMPLATED_FUNCTION_REF, true);
	}
	
	/**
	 * Resets the completion requester.
	 * 
	 * @param unit the compilation unit, may be <code>null</code>.
	 */
	private void reset(ICompilationUnit unit) {
		fUnit= unit;
		fLocalVariables.clear();
		fLocalTypes.clear();
		if (fUnit != null) {
			// TODO JDT signature
//			try {
//				IType[] cuTypes= fUnit.getAllTypes();
//				for (int i= 0; i < cuTypes.length; i++) {
//					String fqn= cuTypes[i].getFullyQualifiedName();
//					String sig= Signature.createTypeSignature(fqn, true);
//					fLocalTypes.put(sig, cuTypes[i].getElementName());
//				}
//			} catch (JavaModelException e) {
//				// ignore
//			}
		}
		fError= false;
	}

	/*
	 * @see descent.core.CompletionRequestor#accept(descent.core.CompletionProposal)
	 */
	public void accept(CompletionProposal proposal) {
		
		String name= String.valueOf(proposal.getCompletion());
		String signature= String.valueOf(proposal.getSignature());
		
		switch (proposal.getKind()) {
			
			case CompletionProposal.LOCAL_VARIABLE_REF:
				// collect local variables
				fLocalVariables.add(new LocalVariable(name, signature));
				break;
				
			default:
				break;
		}
	}
	
	/*
	 * @see descent.core.CompletionRequestor#completionFailure(descent.core.compiler.IProblem)
	 */
	public void completionFailure(IProblem problem) {
		fError= true;
	}

	/**
	 * Tests if the code completion process produced errors.
	 * 
	 * @return <code>true</code> if there are errors, <code>false</code>
	 *         otherwise
	 */
	public boolean hasErrors() {
		return fError;
	}

	/**
	 * Returns <code>true</code> if <code>name</code> is already used locally.
	 * 
	 * @param name the identifier to check
	 * @return <code>true</code> if <code>name</code> is already used, <code>false</code> otherwise
	 */
	public boolean existsLocalName(String name) {
		for (Iterator iterator = fLocalVariables.iterator(); iterator.hasNext();) {
			LocalVariable localVariable = (LocalVariable) iterator.next();

			if (localVariable.getName().equals(name))
				return true;
		}

		return false;
	}
	
	/**
	 * Returns all local variable names.
	 * 
	 * @return all local variable names
	 */
	public String[] getLocalVariableNames() {
		String[] names= new String[fLocalVariables.size()];
		int i= 0;
		for (ListIterator iterator= fLocalVariables.listIterator(fLocalVariables.size()); iterator.hasPrevious();) {
			LocalVariable localVariable= (LocalVariable) iterator.previous();
			names[i++]= localVariable.getName();
		}		
		return names;
	}	

	/**
	 * Returns all local arrays in the order that they appear.
	 * 
	 * @return all local arrays
	 */
	public LocalVariable[] findLocalArrays() {
		List arrays= new ArrayList();

		for (ListIterator iterator= fLocalVariables.listIterator(fLocalVariables.size()); iterator.hasPrevious();) {
			LocalVariable localVariable= (LocalVariable) iterator.previous();

			if (localVariable.isArray())
				arrays.add(localVariable);
		}

		return (LocalVariable[]) arrays.toArray(new LocalVariable[arrays.size()]);
	}
	
	/**
	 * Returns all local variables implementing
	 * <code>java.util.Collection</code> in the order that they appear.
	 * 
	 * @return all local <code>Collection</code>s
	 */
	public LocalVariable[] findLocalCollections() {
		List collections= new ArrayList();

		for (ListIterator iterator= fLocalVariables.listIterator(fLocalVariables.size()); iterator.hasPrevious();) {
			LocalVariable localVariable= (LocalVariable) iterator.previous();

			if (localVariable.isCollection())
				collections.add(localVariable);
		}

		return (LocalVariable[]) collections.toArray(new LocalVariable[collections.size()]);
	}

	/**
	 * Returns all local variables implementing <code>java.lang.Iterable</code>
	 * <em>and</em> all local arrays, in the order that they appear. That is, 
	 * the returned variables can be used within the <code>foreach</code> 
	 * language construct.
	 * 
	 * @return all local <code>Iterable</code>s and arrays
	 */
	public LocalVariable[] findLocalIterables() {
		List iterables= new ArrayList();

		for (ListIterator iterator= fLocalVariables.listIterator(fLocalVariables.size()); iterator.hasPrevious();) {
			LocalVariable localVariable= (LocalVariable) iterator.previous();

			if (localVariable.isArray() || localVariable.isIterable())			
				iterables.add(localVariable);
		}

		return (LocalVariable[]) iterables.toArray(new LocalVariable[iterables.size()]);
	}

}

