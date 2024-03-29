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
package descent.internal.core.util;

import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UTFDataFormatException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import descent.core.IJavaElement;
import descent.core.IJavaModelStatusConstants;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.JavaConventions;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.PrimitiveType;
import descent.core.dom.QualifiedType;
import descent.core.dom.SimpleType;
import descent.core.dom.Type;
import descent.internal.compiler.util.SuffixConstants;
import descent.internal.core.JavaElement;
import descent.internal.core.PackageFragmentRoot;

/**
 * Provides convenient utility methods to other types in this package.
 */
public class Util {
	
	public interface Comparable {
		/**
		 * Returns 0 if this and c are equal, >0 if this is greater than c,
		 * or <0 if this is less than c.
		 */
		int compareTo(Comparable c);
	}

	public interface Comparer {
		/**
		 * Returns 0 if a and b are equal, >0 if a is greater than b,
		 * or <0 if a is less than b.
		 */
		int compare(Object a, Object b);
	}
	
	private static char[][] JAVA_LIKE_EXTENSIONS;
	public static boolean ENABLE_JAVA_LIKE_EXTENSIONS = true;
	private static final String EMPTY_ARGUMENT = "   "; //$NON-NLS-1$
	private static final String ARGUMENTS_DELIMITER = "#"; //$NON-NLS-1$
	
	/**
	 * Finds the first line separator used by the given text.
	 *
	 * @return </code>"\n"</code> or </code>"\r"</code> or  </code>"\r\n"</code>,
	 *			or <code>null</code> if none found
	 */
	public static String findLineSeparator(char[] text) {
		// find the first line separator
		int length = text.length;
		if (length > 0) {
			char nextChar = text[0];
			for (int i = 0; i < length; i++) {
				char currentChar = nextChar;
				nextChar = i < length-1 ? text[i+1] : ' ';
				switch (currentChar) {
					case '\n': return "\n"; //$NON-NLS-1$
					case '\r': return nextChar == '\n' ? "\r\n" : "\r"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		// not found
		return null;
	}

	/**
	 * Returns the line separator found in the given text.
	 * If it is null, or not found return the line delimitor for the given project.
	 * If the project is null, returns the line separator for the workspace.
	 * If still null, return the system line separator.
	 */
	public static String getLineSeparator(String text, IJavaProject project) {
		String lineSeparator = null;
		
		// line delimiter in given text
		if (text != null && text.length() != 0) {
			lineSeparator = findLineSeparator(text.toCharArray());
			if (lineSeparator != null)
				return lineSeparator;
		}
		
		// line delimiter in project preference
		IScopeContext[] scopeContext;
		if (project != null) {
			scopeContext= new IScopeContext[] { new ProjectScope(project.getProject()) };
			lineSeparator= Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null, scopeContext);
			if (lineSeparator != null)
				return lineSeparator;
		}
		
		// line delimiter in workspace preference
		scopeContext= new IScopeContext[] { new InstanceScope() };
		lineSeparator = Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null, scopeContext);
		if (lineSeparator != null)
			return lineSeparator;
		
		// system line delimiter
		return System.getProperty("line.separator");
	}
	
	/**
	 * Returns the line separator used by the given buffer.
	 * Uses the given text if none found.
	 *
	 * @return </code>"\n"</code> or </code>"\r"</code> or  </code>"\r\n"</code>
	 */
	/*
	private static String getLineSeparator(char[] text, char[] buffer) {
		// search in this buffer's contents first
		String lineSeparator = findLineSeparator(buffer);
		if (lineSeparator == null) {
			// search in the given text
			lineSeparator = findLineSeparator(text);
			if (lineSeparator == null) {
				// default to system line separator
				return getLineSeparator((String) null, (IJavaProject) null);
			}
		}
		return lineSeparator;
	}
	*/
	
	/**
	 * Combines two hash codes to make a new one.
	 */
	public static int combineHashCodes(int hashCode1, int hashCode2) {
		return hashCode1 * 17 + hashCode2;
	}
	
	/**
	 * Validate the given .class file name.
	 * A .class file name must obey the following rules:
	 * <ul>
	 * <li> it must not be null
	 * <li> it must include the <code>".class"</code> suffix
	 * <li> its prefix must be a valid identifier
	 * </ul>
	 * </p>
	 * @param name the name of a .class file
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as a .class file name, otherwise a status 
	 *		object indicating what is wrong with the name
	 */
	public static boolean isValidClassFileName(String name) {
		return JavaConventions.validateClassFileName(name).getSeverity() != IStatus.ERROR;
	}

	/**
	 * Validate the given compilation unit name.
	 * A compilation unit name must obey the following rules:
	 * <ul>
	 * <li> it must not be null
	 * <li> it must include the <code>".java"</code> suffix
	 * <li> its prefix must be a valid identifier
	 * </ul>
	 * </p>
	 * @param name the name of a compilation unit
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as a compilation unit name, otherwise a status 
	 *		object indicating what is wrong with the name
	 */
	public static boolean isValidCompilationUnitName(String name) {
		return JavaConventions.validateCompilationUnitName(name).getSeverity() != IStatus.ERROR;
	}
	
	/**
	 * Returns true if the given folder name is valid for a package,
	 * false if it is not.
	 */
	public static boolean isValidFolderNameForPackage(String folderName) {
		return JavaConventions.validateIdentifier(folderName).getSeverity() != IStatus.ERROR;
	}
	
	/**
	 * Returns true if the given name ends with one of the known java like extension.
	 * (implementation is not creating extra strings)
	 */
	public final static boolean isJavaLikeFileName(String name) {
		if (name == null) return false;
		return indexOfJavaLikeExtension(name) != -1;
	}

	/**
	 * Returns true if the given name ends with one of the known java like extension.
	 * (implementation is not creating extra strings)
	 */
	public final static boolean isJavaLikeFileName(char[] fileName) {
		if (fileName == null) return false;
		int fileNameLength = fileName.length;
		char[][] javaLikeExtensions = getJavaLikeExtensions();
		extensions: for (int i = 0, length = javaLikeExtensions.length; i < length; i++) {
			char[] extension = javaLikeExtensions[i];
			int extensionLength = extension.length;
			int extensionStart = fileNameLength - extensionLength;
			if (extensionStart-1 < 0) continue;
			if (fileName[extensionStart-1] != '.') continue;
			for (int j = 0; j < extensionLength; j++) {
				if (fileName[extensionStart + j] != extension[j])
					continue extensions;
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Returns the index of the Java like extension of the given file name
	 * or -1 if it doesn't end with a known Java like extension. 
	 * Note this is the index of the '.' even if it is not considered part of the extension.
	 */
	public static int indexOfJavaLikeExtension(String fileName) {
		int fileNameLength = fileName.length();
		char[][] javaLikeExtensions = getJavaLikeExtensions();
		extensions: for (int i = 0, length = javaLikeExtensions.length; i < length; i++) {
			char[] extension = javaLikeExtensions[i];
			int extensionLength = extension.length;
			int extensionStart = fileNameLength - extensionLength;
			int dotIndex = extensionStart - 1;
			if (dotIndex < 0) continue;
			if (fileName.charAt(dotIndex) != '.') continue;
			for (int j = 0; j < extensionLength; j++) {
				if (fileName.charAt(extensionStart + j) != extension[j])
					continue extensions;
			}
			return dotIndex;
		}
		return -1;
	}
	
	/**
	 * Returns the registered Java like extensions.
	 */
	public static char[][] getJavaLikeExtensions() {
		if (JAVA_LIKE_EXTENSIONS == null) {
			// TODO (jerome) reenable once JDT UI supports other file extensions (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=71460)
			if (!ENABLE_JAVA_LIKE_EXTENSIONS)
				JAVA_LIKE_EXTENSIONS = new char[][] {
					SuffixConstants.EXTENSION_java.toCharArray() ,
					SuffixConstants.EXTENSION_di.toCharArray()
					};
			else {
				IContentType javaContentType = Platform.getContentTypeManager().getContentType(JavaCore.JAVA_SOURCE_CONTENT_TYPE);
				HashSet fileExtensions = new HashSet();
				// content types derived from java content type should be included (https://bugs.eclipse.org/bugs/show_bug.cgi?id=121715)
				IContentType[] contentTypes = Platform.getContentTypeManager().getAllContentTypes();
				for (int i = 0, length = contentTypes.length; i < length; i++) {
					if (contentTypes[i].isKindOf(javaContentType)) { // note that javaContentType.isKindOf(javaContentType) == true
						String[] fileExtension = contentTypes[i].getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
						for (int j = 0, length2 = fileExtension.length; j < length2; j++) {
							fileExtensions.add(fileExtension[j]);
						}
					}
				}
				int length = fileExtensions.size();
				// note that file extensions contains "java" as it is defined in JDT Core's plugin.xml
				char[][] extensions = new char[length][];
				extensions[0] = SuffixConstants.EXTENSION_java.toCharArray(); // ensure that "java" is first
				int index = 1;
				Iterator iterator = fileExtensions.iterator();
				while (iterator.hasNext()) {
					String fileExtension = (String) iterator.next();
					if (SuffixConstants.EXTENSION_java.equals(fileExtension))
						continue;
					extensions[index++] = fileExtension.toCharArray();
				}
				JAVA_LIKE_EXTENSIONS = extensions;
			}
		}
		return JAVA_LIKE_EXTENSIONS;
	}
	
	/**
	 * Returns a new array adding the second array at the end of first array.
	 * It answers null if the first and second are null.
	 * If the first array is null or if it is empty, then a new array is created with second.
	 * If the second array is null, then the first array is returned.
	 * <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li><pre>
	 *    first = null
	 *    second = "a"
	 *    => result = {"a"}
	 * </pre>
	 * <li><pre>
	 *    first = {"a"}
	 *    second = null
	 *    => result = {"a"}
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    first = {"a"}
	 *    second = {"b"}
	 *    => result = {"a", "b"}
	 * </pre>
	 * </li>
	 * </ol>
	 * 
	 * @param first the first array to concatenate
	 * @param second the array to add at the end of the first array
	 * @return a new array adding the second array at the end of first array, or null if the two arrays are null.
	 */
	public static final String[] arrayConcat(String[] first, String second) {
		if (second == null)
			return first;
		if (first == null)
			return new String[] {second};

		int length = first.length;
		if (first.length == 0) {
			return new String[] {second};
		}
		
		String[] result = new String[length + 1];
		System.arraycopy(first, 0, result, 0, length);
		result[length] = second;
		return result;
	}
	
	/*
	 * Returns whether the given java element is exluded from its root's classpath.
	 * It doesn't check whether the root itself is on the classpath or not
	 */
	public static final boolean isExcluded(IJavaElement element) {
		int elementType = element.getElementType();
		switch (elementType) {
			case IJavaElement.JAVA_MODEL:
			case IJavaElement.JAVA_PROJECT:
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				return false;

			case IJavaElement.PACKAGE_FRAGMENT:
				PackageFragmentRoot root = (PackageFragmentRoot)element.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
				IResource resource = element.getResource();
				return resource != null && isExcluded(resource, root.fullInclusionPatternChars(), root.fullExclusionPatternChars());
				
			case IJavaElement.COMPILATION_UNIT:
				root = (PackageFragmentRoot)element.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
				resource = element.getResource();
				if (resource == null) 
					return false;
				if (isExcluded(resource, root.fullInclusionPatternChars(), root.fullExclusionPatternChars()))
					return true;
				return isExcluded(element.getParent());
				
			default:
				IJavaElement cu = element.getAncestor(IJavaElement.COMPILATION_UNIT);
				return cu != null && isExcluded(cu);
		}
	}
	/*
	 * Returns whether the given resource path matches one of the inclusion/exclusion
	 * patterns.
	 * NOTE: should not be asked directly using pkg root pathes
	 * @see IClasspathEntry#getInclusionPatterns
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(IPath resourcePath, char[][] inclusionPatterns, char[][] exclusionPatterns, boolean isFolderPath) {
		if (inclusionPatterns == null && exclusionPatterns == null) return false;
		return descent.internal.compiler.util.Util.isExcluded(resourcePath.toString().toCharArray(), inclusionPatterns, exclusionPatterns, isFolderPath);
	}	

	/*
	 * Returns whether the given resource matches one of the exclusion patterns.
	 * NOTE: should not be asked directly using pkg root pathes
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(IResource resource, char[][] inclusionPatterns, char[][] exclusionPatterns) {
		IPath path = resource.getFullPath();
		// ensure that folders are only excluded if all of their children are excluded
		return isExcluded(path, inclusionPatterns, exclusionPatterns, resource.getType() == IResource.FOLDER);
	}
	
	/**
	 * Returns a trimmed version the simples names returned by Signature.
	 */
	public static String[] getTrimmedSimpleNames(String name) {
		// TODO JDT signature
//		String[] result = Signature.getSimpleNames(name);
		if (name.trim().length() == 0) {
			return CharOperation.NO_STRINGS;
		}
		String[] result = name.split("\\.");
		for (int i = 0, length = result.length; i < length; i++) {
			result[i] = result[i].trim();
		}
		return result;
	}
	
	/**
	 * Scans the given string for a type signature starting at the given index
	 * and returns the index of the last character.
	 * <pre>
	 * TypeSignature:
	 *  |  BaseTypeSignature
	 *  |  ArrayTypeSignature
	 *  |  ClassTypeSignature
	 *  |  TypeVariableSignature
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a type signature
	 */
	public static int scanTypeSignature(char[] string, int start) {
		// TODO JDT signature
//		// need a minimum 1 char
//		if (start >= string.length) {
//			throw new IllegalArgumentException();
//		}
//		char c = string[start];
//		switch (c) {
//			case Signature.C_ARRAY :
//				return scanArrayTypeSignature(string, start);
//			case Signature.C_RESOLVED :
//			case Signature.C_UNRESOLVED :
//				return scanClassTypeSignature(string, start);
//			case Signature.C_TYPE_VARIABLE :
//				return scanTypeVariableSignature(string, start);
//			case Signature.C_BOOL :
//			case Signature.C_BYTE :
//			case Signature.C_CHAR :
//			case Signature.C_DOUBLE :
//			case Signature.C_FLOAT :
//			case Signature.C_INT :
//			case Signature.C_LONG :
//			case Signature.C_SHORT :
//			case Signature.C_VOID :
//				return scanBaseTypeSignature(string, start);
//			case Signature.C_CAPTURE :
//				return scanCaptureTypeSignature(string, start);
//			case Signature.C_EXTENDS:
//			case Signature.C_SUPER:
//			case Signature.C_STAR:
//				return scanTypeBoundSignature(string, start);
//			default :
//				throw new IllegalArgumentException();
//		}
		return -1;
	}
	
	/**
	 * Scans the given string for a capture of a wildcard type signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * CaptureTypeSignature:
	 *     <b>!</b> TypeBoundSignature
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a capture type signature
	 */
	public static int scanCaptureTypeSignature(char[] string, int start) {
		// TODO JDT signature
//		// need a minimum 2 char
//		if (start >= string.length - 1) {
//			throw new IllegalArgumentException();
//		}
//		char c = string[start];
//		if (c != Signature.C_CAPTURE) {
//			throw new IllegalArgumentException();
//		}
//		return scanTypeBoundSignature(string, start + 1);
		return -1;
	}
	
	/**
	 * Scans the given string for a type bound signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * TypeBoundSignature:
	 *     <b>[-+]</b> TypeSignature <b>;</b>
	 *     <b>*</b></b>
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a type variable signature
	 */
	public static int scanTypeBoundSignature(char[] string, int start) {
		// TODO JDT signature
//		// need a minimum 1 char for wildcard
//		if (start >= string.length) {
//			throw new IllegalArgumentException();
//		}
//		char c = string[start];
//		switch (c) {
//			case Signature.C_STAR :
//				return start;
//			case Signature.C_SUPER :
//			case Signature.C_EXTENDS :
//				// need a minimum 3 chars "+[I"
//				if (start >= string.length - 2) {
//					throw new IllegalArgumentException();
//				}
//				break;
//			default :
//				// must start in "+/-"
//					throw new IllegalArgumentException();
//				
//		}
//		c = string[++start];
//		switch (c) {
//			case Signature.C_CAPTURE :
//				return scanCaptureTypeSignature(string, start);
//			case Signature.C_SUPER :
//			case Signature.C_EXTENDS :
//				return scanTypeBoundSignature(string, start);
//			case Signature.C_RESOLVED :
//			case Signature.C_UNRESOLVED :
//				return scanClassTypeSignature(string, start);
//			case Signature.C_TYPE_VARIABLE :
//				return scanTypeVariableSignature(string, start);
//			case Signature.C_ARRAY :
//				return scanArrayTypeSignature(string, start);
//			case Signature.C_STAR:
//				return start;
//			default:
//				throw new IllegalArgumentException();
//		}
		return -1;
	}
	
	/**
	 * Scans the given string for an array type signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * ArrayTypeSignature:
	 *     <b>[</b> TypeSignature
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not an array type signature
	 */
	public static int scanArrayTypeSignature(char[] string, int start) {
		// TODO JDT signature
//		int length = string.length;
//		// need a minimum 2 char
//		if (start >= length - 1) {
//			throw new IllegalArgumentException();
//		}
//		char c = string[start];
//		if (c != Signature.C_ARRAY) {
//			throw new IllegalArgumentException();
//		}
//		
//		c = string[++start];
//		while(c == Signature.C_ARRAY) {
//			// need a minimum 2 char
//			if (start >= length - 1) {
//				throw new IllegalArgumentException();
//			}
//			c = string[++start];
//		}
//		return scanTypeSignature(string, start);
		return -1;
	}
	
	/**
	 * Scans the given string for a type variable signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * TypeVariableSignature:
	 *     <b>T</b> Identifier <b>;</b>
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a type variable signature
	 */
	public static int scanTypeVariableSignature(char[] string, int start) {
		// TODO JDT signature
//		// need a minimum 3 chars "Tx;"
//		if (start >= string.length - 2) { 
//			throw new IllegalArgumentException();
//		}
//		// must start in "T"
//		char c = string[start];
//		if (c != Signature.C_TYPE_VARIABLE) {
//			throw new IllegalArgumentException();
//		}
//		int id = scanIdentifier(string, start + 1);
//		c = string[id + 1];
//		if (c == Signature.C_SEMICOLON) {
//			return id + 1;
//		} else {
//			throw new IllegalArgumentException();
//		}
		return -1;
	}
	
	/**
	 * Scans the given string for a class type signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * ClassTypeSignature:
	 *     { <b>L</b> | <b>Q</b> } Identifier
	 *           { { <b>/</b> | <b>.</b> Identifier [ <b>&lt;</b> TypeArgumentSignature* <b>&gt;</b> ] }
	 *           <b>;</b>
	 * </pre>
	 * Note that although all "/"-identifiers most come before "."-identifiers,
	 * there is no syntactic ambiguity. This method will accept them without
	 * complaint.
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a class type signature
	 */
	public static int scanClassTypeSignature(char[] string, int start) {
		// TODO JDT signature
//		// need a minimum 3 chars "Lx;"
//		if (start >= string.length - 2) { 
//			throw new IllegalArgumentException();
//		}
//		// must start in "L" or "Q"
//		char c = string[start];
//		if (c != Signature.C_RESOLVED && c != Signature.C_UNRESOLVED) {
//			return -1;
//		}
//		int p = start + 1;
//		while (true) {
//			if (p >= string.length) {
//				throw new IllegalArgumentException();
//			}
//			c = string[p];
//			if (c == Signature.C_SEMICOLON) {
//				// all done
//				return p;
//			} else if (c == Signature.C_GENERIC_START) {
//				int e = scanTypeArgumentSignatures(string, p);
//				p = e;
//			} else if (c == Signature.C_DOT || c == '/') {
//				int id = scanIdentifier(string, p + 1);
//				p = id;
//			}
//			p++;
//		}
		return -1;
	}
	
	/**
	 * Scans the given string for a base type signature starting at the given index
	 * and returns the index of the last character.
	 * <pre>
	 * BaseTypeSignature:
	 *     <b>B</b> | <b>C</b> | <b>D</b> | <b>F</b> | <b>I</b>
	 *   | <b>J</b> | <b>S</b> | <b>V</b> | <b>Z</b>
	 * </pre>
	 * Note that although the base type "V" is only allowed in method return types,
	 * there is no syntactic ambiguity. This method will accept them anywhere
	 * without complaint.
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a base type signature
	 */
	public static int scanBaseTypeSignature(char[] string, int start) {
		// need a minimum 1 char
		if (start >= string.length) {
			throw new IllegalArgumentException();
		}
		char c = string[start];
		if ("BCDFIJSVZ".indexOf(c) >= 0) { //$NON-NLS-1$
			return start;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Scans the given string for an identifier starting at the given
	 * index and returns the index of the last character. 
	 * Stop characters are: ";", ":", "&lt;", "&gt;", "/", ".".
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not an identifier
	 */
	public static int scanIdentifier(char[] string, int start) {
		// need a minimum 1 char
		if (start >= string.length) { 
			throw new IllegalArgumentException();
		}
		int p = start;
		while (true) {
			char c = string[p];
			if (c == '<' || c == '>' || c == ':' || c == ';' || c == '.' || c == '/') {
				return p - 1;
			}
			p++;
			if (p == string.length) {
				return p - 1;
			}
		}
	}
	
	/**
	 * Scans the given string for a list of type argument signatures starting at
	 * the given index and returns the index of the last character.
	 * <pre>
	 * TypeArgumentSignatures:
	 *     <b>&lt;</b> TypeArgumentSignature* <b>&gt;</b>
	 * </pre>
	 * Note that although there is supposed to be at least one type argument, there
	 * is no syntactic ambiguity if there are none. This method will accept zero
	 * type argument signatures without complaint.
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a list of type arguments
	 * signatures
	 */
	public static int scanTypeArgumentSignatures(char[] string, int start) {
		// TODO JDT signature
//		// need a minimum 2 char "<>"
//		if (start >= string.length - 1) {
//			throw new IllegalArgumentException();
//		}
//		char c = string[start];
//		if (c != Signature.C_GENERIC_START) {
//			throw new IllegalArgumentException();
//		}
//		int p = start + 1;
//		while (true) {
//			if (p >= string.length) {
//				throw new IllegalArgumentException();
//			}
//			c = string[p];
//			if (c == Signature.C_GENERIC_END) {
//				return p;
//			}
//			int e = scanTypeArgumentSignature(string, p);
//			p = e + 1;
//		}
		return -1;
	}

	/**
	 * Scans the given string for a type argument signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * TypeArgumentSignature:
	 *     <b>&#42;</b>
	 *  |  <b>+</b> TypeSignature
	 *  |  <b>-</b> TypeSignature
	 *  |  TypeSignature
	 * </pre>
	 * Note that although base types are not allowed in type arguments, there is
	 * no syntactic ambiguity. This method will accept them without complaint.
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a type argument signature
	 */
	public static int scanTypeArgumentSignature(char[] string, int start) {
		// TODO JDT signature
//		// need a minimum 1 char
//		if (start >= string.length) {
//			throw new IllegalArgumentException();
//		}
//		char c = string[start];
//		switch (c) {
//			case Signature.C_STAR :
//				return start;
//			case Signature.C_EXTENDS :
//			case Signature.C_SUPER :
//				return scanTypeBoundSignature(string, start);
//			default :
//				return scanTypeSignature(string, start);
//		}
		return -1;
	}
	
	/**
	 * Compares two arrays using equals() on the elements.
	 * Neither can be null. Only the first len elements are compared.
	 * Return false if either array is shorter than len.
	 */
	public static boolean equalArrays(Object[] a, Object[] b, int len) {
		if (a == b)	return true;
		if (a.length < len || b.length < len) return false;
		for (int i = 0; i < len; ++i) {
			if (a[i] == null) {
				if (b[i] != null) return false;
			} else {
				if (!a[i].equals(b[i])) return false;
			}
		}
		return true;
	}

	/**
	 * Compares two arrays using equals() on the elements.
	 * Either or both arrays may be null.
	 * Returns true if both are null.
	 * Returns false if only one is null.
	 * If both are arrays, returns true iff they have the same length and
	 * all elements are equal.
	 */
	public static boolean equalArraysOrNull(int[] a, int[] b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		int len = a.length;
		if (len != b.length)
			return false;
		for (int i = 0; i < len; ++i) {
			if (a[i] != b[i])
				return false;
		}
		return true;
	}

	/**
	 * Compares two arrays using equals() on the elements.
	 * Either or both arrays may be null.
	 * Returns true if both are null.
	 * Returns false if only one is null.
	 * If both are arrays, returns true iff they have the same length and
	 * all elements compare true with equals.
	 */
	public static boolean equalArraysOrNull(Object[] a, Object[] b) {
		if (a == b)	return true;
		if (a == null || b == null) return false;

		int len = a.length;
		if (len != b.length) return false;
		// walk array from end to beginning as this optimizes package name cases 
		// where the first part is always the same (e.g. descent)
		for (int i = len-1; i >= 0; i--) {
			if (a[i] == null) {
				if (b[i] != null) return false;
			} else {
				if (!a[i].equals(b[i])) return false;
			}
		}
		return true;
	}
	
	/**
	 * Compares two objects using equals().
	 * Either or both array may be null.
	 * Returns true if both are null.
	 * Returns false if only one is null.
	 * Otherwise, return the result of comparing with equals().
	 */
	public static boolean equalOrNull(Object a, Object b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}
	
	/**
	 * Returns the concatenation of the given array parts using the given separator between each part.
	 * <br>
	 * <br>
	 * For example:<br>
	 * <ol>
	 * <li><pre>
	 *    array = {"a", "b"}
	 *    separator = '.'
	 *    => result = "a.b"
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    array = {}
	 *    separator = '.'
	 *    => result = ""
	 * </pre></li>
	 * </ol>
	 * 
	 * @param array the given array
	 * @param separator the given separator
	 * @return the concatenation of the given array parts using the given separator between each part
	 */
	public static final String concatWith(String[] array, char separator) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = array.length; i < length; i++) {
			buffer.append(array[i]);
			if (i < length - 1)
				buffer.append(separator);
		}
		return buffer.toString();
	}
	
	/**
	 * Returns the concatenation of the given array parts using the given separator between each
	 * part and appending the given name at the end.
	 * <br>
	 * <br>
	 * For example:<br>
	 * <ol>
	 * <li><pre>
	 *    name = "c"
	 *    array = { "a", "b" }
	 *    separator = '.'
	 *    => result = "a.b.c"
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    name = null
	 *    array = { "a", "b" }
	 *    separator = '.'
	 *    => result = "a.b"
	 * </pre></li>
	 * <li><pre>
	 *    name = " c"
	 *    array = null
	 *    separator = '.'
	 *    => result = "c"
	 * </pre></li>
	 * </ol>
	 * 
	 * @param array the given array
	 * @param name the given name
	 * @param separator the given separator
	 * @return the concatenation of the given array parts using the given separator between each
	 * part and appending the given name at the end
	 */
	public static final String concatWith(
		String[] array,
		String name,
		char separator) {
		
		if (array == null || array.length == 0) return name;
		if (name == null || name.length() == 0) return concatWith(array, separator);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = array.length; i < length; i++) {
			buffer.append(array[i]);
			buffer.append(separator);
		}
		buffer.append(name);
		return buffer.toString();
		
	}
	
	public static void sort(char[][] list) {
		if (list.length > 1)
			quickSort(list, 0, list.length - 1);
	}

	/**
	 * Sorts an array of Comparable objects in place.
	 */
	public static void sort(Comparable[] objects) {
		if (objects.length > 1)
			quickSort(objects, 0, objects.length - 1);
	}
	public static void sort(int[] list) {
		if (list.length > 1)
			quickSort(list, 0, list.length - 1);
	}

	/**
	 * Sorts an array of objects in place.
	 * The given comparer compares pairs of items.
	 */
	public static void sort(Object[] objects, Comparer comparer) {
		if (objects.length > 1)
			quickSort(objects, 0, objects.length - 1, comparer);
	}

	/**
	 * Sorts an array of objects in place, using the sort order given for each item.
	 */
	public static void sort(Object[] objects, int[] sortOrder) {
		if (objects.length > 1)
			quickSort(objects, 0, objects.length - 1, sortOrder);
	}

	/**
	 * Sorts an array of strings in place using quicksort.
	 */
	public static void sort(String[] strings) {
		if (strings.length > 1)
			quickSort(strings, 0, strings.length - 1);
	}
	
	private static void quickSort(char[][] list, int left, int right) {
		int original_left= left;
		int original_right= right;
		char[] mid= list[(left + right) / 2];
		do {
			while (compare(list[left], mid) < 0) {
				left++;
			}
			while (compare(mid, list[right]) < 0) {
				right--;
			}
			if (left <= right) {
				char[] tmp= list[left];
				list[left]= list[right];
				list[right]= tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(list, original_left, right);
		}
		if (left < original_right) {
			quickSort(list, left, original_right);
		}
	}

	/**
	 * Sort the comparable objects in the given collection.
	 */
	private static void quickSort(Comparable[] sortedCollection, int left, int right) {
		int original_left = left;
		int original_right = right;
		Comparable mid = sortedCollection[ (left + right) / 2];
		do {
			while (sortedCollection[left].compareTo(mid) < 0) {
				left++;
			}
			while (mid.compareTo(sortedCollection[right]) < 0) {
				right--;
			}
			if (left <= right) {
				Comparable tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right);
		}
	}
	private static void quickSort(int[] list, int left, int right) {
		int original_left= left;
		int original_right= right;
		int mid= list[(left + right) / 2];
		do {
			while (list[left] < mid) {
				left++;
			}
			while (mid < list[right]) {
				right--;
			}
			if (left <= right) {
				int tmp= list[left];
				list[left]= list[right];
				list[right]= tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(list, original_left, right);
		}
		if (left < original_right) {
			quickSort(list, left, original_right);
		}
	}

	/**
	 * Sort the objects in the given collection using the given comparer.
	 */
	private static void quickSort(Object[] sortedCollection, int left, int right, Comparer comparer) {
		int original_left = left;
		int original_right = right;
		Object mid = sortedCollection[ (left + right) / 2];
		do {
			while (comparer.compare(sortedCollection[left], mid) < 0) {
				left++;
			}
			while (comparer.compare(mid, sortedCollection[right]) < 0) {
				right--;
			}
			if (left <= right) {
				Object tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right, comparer);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right, comparer);
		}
	}

	/**
	 * Sort the objects in the given collection using the given sort order.
	 */
	private static void quickSort(Object[] sortedCollection, int left, int right, int[] sortOrder) {
		int original_left = left;
		int original_right = right;
		int mid = sortOrder[ (left + right) / 2];
		do {
			while (sortOrder[left] < mid) {
				left++;
			}
			while (mid < sortOrder[right]) {
				right--;
			}
			if (left <= right) {
				Object tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				int tmp2 = sortOrder[left];
				sortOrder[left] = sortOrder[right];
				sortOrder[right] = tmp2;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right, sortOrder);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right, sortOrder);
		}
	}

	/**
	 * Sort the strings in the given collection.
	 */
	private static void quickSort(String[] sortedCollection, int left, int right) {
		int original_left = left;
		int original_right = right;
		String mid = sortedCollection[ (left + right) / 2];
		do {
			while (sortedCollection[left].compareTo(mid) < 0) {
				left++;
			}
			while (mid.compareTo(sortedCollection[right]) < 0) {
				right--;
			}
			if (left <= right) {
				String tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right);
		}
	}
	
	/**
	 * Compares two byte arrays.  
	 * Returns <0 if a byte in a is less than the corresponding byte in b, or if a is shorter, or if a is null.
	 * Returns >0 if a byte in a is greater than the corresponding byte in b, or if a is longer, or if b is null.
	 * Returns 0 if they are equal or both null.
	 */
	public static int compare(byte[] a, byte[] b) {
		if (a == b)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		int len = Math.min(a.length, b.length);
		for (int i = 0; i < len; ++i) {
			int diff = a[i] - b[i];
			if (diff != 0)
				return diff;
		}
		if (a.length > len)
			return 1;
		if (b.length > len)
			return -1;
		return 0;
	}
	/**
	 * Compares two strings lexicographically. 
	 * The comparison is based on the Unicode value of each character in
	 * the strings. 
	 *
	 * @return  the value <code>0</code> if the str1 is equal to str2;
	 *          a value less than <code>0</code> if str1
	 *          is lexicographically less than str2; 
	 *          and a value greater than <code>0</code> if str1 is
	 *          lexicographically greater than str2.
	 */
	public static int compare(char[] str1, char[] str2) {
		int len1= str1.length;
		int len2= str2.length;
		int n= Math.min(len1, len2);
		int i= 0;
		while (n-- != 0) {
			char c1= str1[i];
			char c2= str2[i++];
			if (c1 != c2) {
				return c1 - c2;
			}
		}
		return len1 - len2;
	}
	
	public static void log(Throwable e) {
		log(e, e.getMessage() == null ? e.getClass().getName() : e.getClass().getName() + ": " + e.getMessage());
	}
	
	/*
	 * Add a log entry
	 */
	public static void log(Throwable e, String message) {
		Throwable nestedException;
		if (e instanceof JavaModelException 
				&& (nestedException = ((JavaModelException)e).getException()) != null) {
			e = nestedException;
		}
		IStatus status= new Status(
			IStatus.ERROR, 
			JavaCore.PLUGIN_ID, 
			IStatus.ERROR, 
			message, 
			e); 
		JavaCore.getPlugin().getLog().log(status);
	}
	
	public static void verbose(String log) {
		verbose(log, System.out);
	}
	public static synchronized void verbose(String log, PrintStream printStream) {
		int start = 0;
		do {
			int end = log.indexOf('\n', start);
			printStream.print(Thread.currentThread());
			printStream.print(" "); //$NON-NLS-1$
			printStream.print(log.substring(start, end == -1 ? log.length() : end+1));
			start = end+1;
		} while (start != 0);
		printStream.println();
	}
	
	/*
	 * Returns the index of the most specific argument paths which is strictly enclosing the path to check
	 */
	public static int indexOfEnclosingPath(IPath checkedPath, IPath[] paths, int pathCount) {

	    int bestMatch = -1, bestLength = -1;
		for (int i = 0; i < pathCount; i++){
			if (paths[i].equals(checkedPath)) continue;
			if (paths[i].isPrefixOf(checkedPath)) {
			    int currentLength = paths[i].segmentCount();
			    if (currentLength > bestLength) {
			        bestLength = currentLength;
			        bestMatch = i;
			    }
			}
		}
		return bestMatch;
	}
	
	/*
	 * Returns the index of the first argument paths which is equal to the path to check
	 */
	public static int indexOfMatchingPath(IPath checkedPath, IPath[] paths, int pathCount) {

		for (int i = 0; i < pathCount; i++){
			if (paths[i].equals(checkedPath)) return i;
		}
		return -1;
	}
	
	/**
	 * Returns the substring of the given file name, ending at the start of a
	 * Java like extension. The entire file name is returned if it doesn't end
	 * with a Java like extension.
	 */
	public static String getNameWithoutJavaLikeExtension(String fileName) {
		int index = indexOfJavaLikeExtension(fileName);
		if (index == -1)
			return fileName;
		return fileName.substring(0, index);
	}
	
	/**
	 * Converts the given relative path into a package name.
	 * Returns null if the path is not a valid package name.
	 */
	public static String packageName(IPath pkgPath) {
		StringBuffer pkgName = new StringBuffer(IPackageFragment.DEFAULT_PACKAGE_NAME);
		for (int j = 0, max = pkgPath.segmentCount(); j < max; j++) {
			String segment = pkgPath.segment(j);
			if (!isValidFolderNameForPackage(segment)) {
				return null;
			}
			pkgName.append(segment);
			if (j < pkgPath.segmentCount() - 1) {
				pkgName.append("." ); //$NON-NLS-1$
			}
		}
		return pkgName.toString();
	}
	
	/**
	 * Put all the arguments in one String.
	 */
	public static String getProblemArgumentsForMarker(String[] arguments){
		StringBuffer args = new StringBuffer(10);
		
		args.append(arguments.length);
		args.append(':');
		
			
		for (int j = 0; j < arguments.length; j++) {
			if(j != 0)
				args.append(ARGUMENTS_DELIMITER);
			
			if(arguments[j].length() == 0) {
				args.append(EMPTY_ARGUMENT);
			} else {			
				args.append(arguments[j]);
			}
		}
		
		return args.toString();
	}
	
	/**
	 * Separate all the arguments of a String made by getProblemArgumentsForMarker
	 */
	public static String[] getProblemArgumentsFromMarker(String argumentsString){
		if (argumentsString == null) return null;
		int index = argumentsString.indexOf(':');
		if(index == -1)
			return null;
		
		int length = argumentsString.length();
		int numberOfArg;
		try{
			numberOfArg = Integer.parseInt(argumentsString.substring(0 , index));
		} catch (NumberFormatException e) {
			return null;
		}
		argumentsString = argumentsString.substring(index + 1, length);
		
		String[] args = new String[length];
		int count = 0;
		
		StringTokenizer tokenizer = new StringTokenizer(argumentsString, ARGUMENTS_DELIMITER);
		while(tokenizer.hasMoreTokens()) {
			String argument = tokenizer.nextToken();
			if(argument.equals(EMPTY_ARGUMENT))
				argument = "";  //$NON-NLS-1$
			args[count++] = argument;
		}
		
		if(count != numberOfArg)
			return null;
		
		System.arraycopy(args, 0, args = new String[count], 0, count);
		return args;
	}
	
	/**
	 * Returns the given file's contents as a byte array.
	 */
	public static byte[] getResourceContentsAsByteArray(IFile file) throws JavaModelException {
		InputStream stream= null;
		try {
			stream = file.getContents(true);
		} catch (CoreException e) {
			throw new JavaModelException(e);
		}
		try {
			return descent.internal.compiler.util.Util.getInputStreamAsByteArray(stream, -1);
		} catch (IOException e) {
			throw new JavaModelException(e, IJavaModelStatusConstants.IO_EXCEPTION);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	/**
	 * Returns the given file's contents as a character array.
	 */
	public static char[] getResourceContentsAsCharArray(IFile file) throws JavaModelException {
		// Get encoding from file
		String encoding;
		try {
			encoding = file.getCharset();
		} catch(CoreException ce) {
			// do not use any encoding
			encoding = null;
		}
		return getResourceContentsAsCharArray(file, encoding);
	}
		
	public static char[] getResourceContentsAsCharArray(IFile file, String encoding) throws JavaModelException {		
		// Get file length
		// workaround https://bugs.eclipse.org/bugs/show_bug.cgi?id=130736 by using java.io.File if possible
		IPath location = file.getLocation();
		long length;
		if (location == null) {
			// non local file
			try {
				length = EFS.getStore(file.getLocationURI()).fetchInfo().getLength();
			} catch (CoreException e) {
				throw new JavaModelException(e);
			}
		} else {
			// local file
			length = location.toFile().length();
		}
		
		// Get resource contents
		InputStream stream= null;
		try {
			stream = file.getContents(true);
		} catch (CoreException e) {
			throw new JavaModelException(e, IJavaModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
		}
		try {
			return descent.internal.compiler.util.Util.getInputStreamAsCharArray(stream, (int) length, encoding);
		} catch (IOException e) {
			throw new JavaModelException(e, IJavaModelStatusConstants.IO_EXCEPTION);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	public static char[] getFileContentsAsCharArray(File file) throws JavaModelException {
		InputStream stream= null;
		try {
			stream = new FileInputStream(file);
		} catch (IOException e) {
			throw new JavaModelException(e, IJavaModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
		}
		try {
			return descent.internal.compiler.util.Util.getInputStreamAsCharArray(stream, (int) file.length(), null);
		} catch (IOException e) {
			throw new JavaModelException(e, IJavaModelStatusConstants.IO_EXCEPTION);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	/*
	 * Converts the given URI to a local file. Use the existing file if the uri is on the local file system.
	 * Otherwise fetch it.
	 * Returns null if unable to fetch it.
	 */
	public static File toLocalFile(URI uri, IProgressMonitor monitor) throws CoreException {
		IFileStore fileStore = EFS.getStore(uri);
		File localFile = fileStore.toLocalFile(EFS.NONE, monitor);
		if (localFile ==null)
			// non local file system
			localFile= fileStore.toLocalFile(EFS.CACHE, monitor);
		return localFile;
	}
	
	/**
	 * Returns the toString() of the given full path minus the first given number of segments.
	 * The returned string is always a relative path (it has no leading slash)
	 */
	public static String relativePath(IPath fullPath, int skipSegmentCount) {
		boolean hasTrailingSeparator = fullPath.hasTrailingSeparator();
		String[] segments = fullPath.segments();
		
		// compute length
		int length = 0;
		int max = segments.length;
		if (max > skipSegmentCount) {
			for (int i1 = skipSegmentCount; i1 < max; i1++) {
				length += segments[i1].length();
			}
			//add the separator lengths
			length += max - skipSegmentCount - 1;
		}
		if (hasTrailingSeparator)
			length++;

		char[] result = new char[length];
		int offset = 0;
		int len = segments.length - 1;
		if (len >= skipSegmentCount) {
			//append all but the last segment, with separators
			for (int i = skipSegmentCount; i < len; i++) {
				int size = segments[i].length();
				segments[i].getChars(0, size, result, offset);
				offset += size;
				result[offset++] = '/';
			}
			//append the last segment
			int size = segments[len].length();
			segments[len].getChars(0, size, result, offset);
			offset += size;
		}
		if (hasTrailingSeparator)
			result[offset++] = '/';
		return new String(result);
	}
	
	/*
	 * Resets the list of Java-like extensions after a change in content-type.
	 */
	public static void resetJavaLikeExtensions() {
		JAVA_LIKE_EXTENSIONS = null;
	}
	
	/**
	 * Converts a String[] to char[][].
	 */
	public static char[][] toCharArrays(String[] a) {
		int len = a.length;
		if (len == 0) return CharOperation.NO_CHAR_CHAR;
		char[][] result = new char[len][];
		for (int i = 0; i < len; ++i) {
			result[i] = a[i].toCharArray();
		}
		return result;
	}
	
	/**
	 * Return a new array which is the split of the given string using the given divider. The given end 
	 * is exclusive and the given start is inclusive.
	 * <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li><pre>
	 *    divider = 'b'
	 *    string = "abbaba"
	 *    start = 2
	 *    end = 5
	 *    result => { "", "a", "" }
	 * </pre>
	 * </li>
	 * </ol>
	 * 
	 * @param divider the given divider
	 * @param string the given string
	 * @param start the given starting index
	 * @param end the given ending index
	 * @return a new array which is the split of the given string using the given divider
	 * @throws ArrayIndexOutOfBoundsException if start is lower than 0 or end is greater than the array length
	 */
	public static final String[] splitOn(
		char divider,
		String string,
		int start,
		int end) {
		int length = string == null ? 0 : string.length();
		if (length == 0 || start > end)
			return CharOperation.NO_STRINGS;

		int wordCount = 1;
		for (int i = start; i < end; i++)
			if (string.charAt(i) == divider)
				wordCount++;
		String[] split = new String[wordCount];
		int last = start, currentWord = 0;
		for (int i = start; i < end; i++) {
			if (string.charAt(i) == divider) {
				split[currentWord++] = string.substring(last, i);
				last = i + 1;
			}
		}
		split[currentWord] = string.substring(last, end);
		return split;
	}
	
	/*
	 * Returns whether the given file name equals to the given string ignoring the java like extension
	 * of the file name.
	 * Returns false if it is not a java like file name.
	 */
	public static boolean equalsIgnoreJavaLikeExtension(String fileName, String string) {
		int fileNameLength = fileName.length();
		int stringLength = string.length();
		if (fileNameLength < stringLength) return false;
		for (int i = 0; i < stringLength; i ++) {
			if (fileName.charAt(i) != string.charAt(i)) {
				return false;
			}
		}
		char[][] javaLikeExtensions = getJavaLikeExtensions();
		suffixes: for (int i = 0, length = javaLikeExtensions.length; i < length; i++) {
			char[] suffix = javaLikeExtensions[i];
			int extensionStart = stringLength+1;
			if (extensionStart + suffix.length != fileNameLength) continue;
			if (fileName.charAt(stringLength) != '.') continue;
			for (int j = extensionStart; j < fileNameLength; j++) {
				if (fileName.charAt(j) != suffix[j-extensionStart]) 
					continue suffixes;
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Returns whether the given compound name starts with the given prefix.
	 * Returns true if the n first elements of the prefix are equals and the last element of the 
	 * prefix is a prefix of the corresponding element in the compound name.
	 */
	public static boolean startsWithIgnoreCase(String[] compoundName, String[] prefix) {
		int prefixLength = prefix.length;
		int nameLength = compoundName.length;
		if (prefixLength > nameLength) return false;
		for (int i = 0; i < prefixLength - 1; i++) {
			if (!compoundName[i].equalsIgnoreCase(prefix[i]))
				return false;
		}
		return compoundName[prefixLength-1].toLowerCase().startsWith(prefix[prefixLength-1].toLowerCase());
	}
	
	/*
	 * Returns the simple name of a local type from the given binary type name.
	 * The last '$' is at lastDollar. The last character of the type name is at end-1.
	 */
	public static String localTypeName(String binaryTypeName, int lastDollar, int end) {
		if (lastDollar > 0 && binaryTypeName.charAt(lastDollar-1) == '$') 
			// local name starts with a dollar sign
			// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=103466)
			return binaryTypeName;
		int nameStart = lastDollar+1;
		while (nameStart < end && Character.isDigit(binaryTypeName.charAt(nameStart)))
			nameStart++;
		return binaryTypeName.substring(nameStart, end);
	}
	
	/*
	 * Returns the signature of the given type.
	 */
	public static String getSignature(Type type) {
		// TODO JDT signature
//		StringBuffer buffer = new StringBuffer();
//		getFullyQualifiedName(type, buffer);
//		return Signature.createTypeSignature(buffer.toString(), false/*not resolved in source*/);
		return "i";
	}
	
	/*
	 * Appends to the given buffer the fully qualified name (as it appears in the source) of the given type
	 */
	private static void getFullyQualifiedName(Type type, StringBuffer buffer) {
		switch (type.getNodeType()) {
		/* TODO signature
			case ASTNode.ARRAY_TYPE:
				ArrayType arrayType = (ArrayType) type;
				getFullyQualifiedName(arrayType.getElementType(), buffer);
				for (int i = 0, length = arrayType.getDimensions(); i < length; i++) {
					buffer.append('[');
					buffer.append(']');
				}
				break;
			case ASTNode.PARAMETERIZED_TYPE:
				ParameterizedType parameterizedType = (ParameterizedType) type;
				getFullyQualifiedName(parameterizedType.getType(), buffer);
				buffer.append('<');
				Iterator iterator = parameterizedType.typeArguments().iterator();
				boolean isFirst = true;
				while (iterator.hasNext()) {
					if (!isFirst)
						buffer.append(',');
					else
						isFirst = false;
					Type typeArgument = (Type) iterator.next();
					getFullyQualifiedName(typeArgument, buffer);
				}
				buffer.append('>');
				break;
				*/
			case ASTNode.PRIMITIVE_TYPE:
				buffer.append(((PrimitiveType) type).getPrimitiveTypeCode().toString());
				break;
			case ASTNode.QUALIFIED_TYPE:
				getFullyQualifiedName(((QualifiedType) type).getType(), buffer);
				break;
			case ASTNode.SIMPLE_TYPE:
				buffer.append(((SimpleType) type).getName().getFullyQualifiedName());
				break;
				/* TODO signature
			case ASTNode.WILDCARD_TYPE:
				buffer.append('?');
				WildcardType wildcardType = (WildcardType) type;
				Type bound = wildcardType.getBound();
				if (bound == null) return;
				if (wildcardType.isUpperBound()) {
					buffer.append(" extends "); //$NON-NLS-1$
				} else {
					buffer.append(" super "); //$NON-NLS-1$
				}
				getFullyQualifiedName(bound, buffer);
				break;
				*/
		}
	}
	
	public static void setReadOnly(IResource resource, boolean readOnly) {
		ResourceAttributes resourceAttributes = resource.getResourceAttributes();
		if (resourceAttributes == null) return; // not supported on this platform for this resource
		resourceAttributes.setReadOnly(readOnly);
		try {
			resource.setResourceAttributes(resourceAttributes);
		} catch (CoreException e) {
			// ignore
		}
	}
	
	/**
	 * Split signatures of all levels  from a type unique key.
	 * 
	 * Example:
	 * 	For following type X<Y<Z>,V<W>,U>.A<B>, unique key is:
	 * 	"LX<LY<LZ;>;LV<LW;>;LU;>.LA<LB;>;"
	 * 
	 * 	The return splitted signatures array is:
	 * 	[
	 * 		['L','X','<','L','Y','<','L','Z',';'>',';','L','V','<','L','W',';'>',';','L','U','>',';'],
	 * 		['L','A','<','L','B',';','>',';']
	 * 
	 * @param typeSignature ParameterizedSourceType type signature
	 * @return char[][] Array of signatures for each level of given unique key
	 */
	public final static char[][] splitTypeLevelsSignature(String typeSignature) {
		// TODO JDT signature
//		// In case of IJavaElement signature, replace '$' by '.'
//		char[] source = Signature.removeCapture(typeSignature.toCharArray());
//		CharOperation.replace(source, '$', '.');
//
//		// Init counters and arrays
//		char[][] signatures = new char[10][];
//		int signaturesCount = 0;
////		int[] lengthes = new int [10];
//		int typeArgsCount = 0;
//		int paramOpening = 0;
//		
//		// Scan each signature character
//		for (int idx=0, ln = source.length; idx < ln; idx++) {
//			switch (source[idx]) {
//				case '>':
//					paramOpening--;
//					if (paramOpening == 0)  {
//						if (signaturesCount == signatures.length) {
//							System.arraycopy(signatures, 0, signatures = new char[signaturesCount+10][], 0, signaturesCount);
//						}
//						typeArgsCount = 0;
//					}
//					break;
//				case '<':
//					paramOpening++;
//					if (paramOpening == 1) {
//						typeArgsCount = 1;
//					}
//					break;
//				case '*':
//				case ';':
//					if (paramOpening == 1) typeArgsCount++;
//					break;
//				case '.':
//					if (paramOpening == 0)  {
//						if (signaturesCount == signatures.length) {
//							System.arraycopy(signatures, 0, signatures = new char[signaturesCount+10][], 0, signaturesCount);
//						}
//						signatures[signaturesCount] = new char[idx+1];
//						System.arraycopy(source, 0, signatures[signaturesCount], 0, idx);
//						signatures[signaturesCount][idx] = Signature.C_SEMICOLON;
//						signaturesCount++;
//					}
//					break;
//				case '/':
//					source[idx] = '.';
//					break;
//			}
//		}
//
//		// Resize signatures array
//		char[][] typeSignatures = new char[signaturesCount+1][];
//		typeSignatures[0] = source;
//		for (int i=1, j=signaturesCount-1; i<=signaturesCount; i++, j--){
//			typeSignatures[i] = signatures[j];
//		}
//		return typeSignatures;
		return CharOperation.NO_CHAR_CHAR;
	}
	
	/**
	 * Get all type arguments from an array of signatures.
	 * 
	 * Example:
	 * 	For following type X<Y<Z>,V<W>,U>.A<B> signatures is:
	 * 	[
	 * 		['L','X','<','L','Y','<','L','Z',';'>',';','L','V','<','L','W',';'>',';','L','U',';',>',';'],
	 * 		['L','A','<','L','B',';','>',';']
	 * 	]
	 * 	@see #splitTypeLevelsSignature(String)
	 * 	Then, this method returns:
	 * 	[
	 * 		[
	 * 			['L','Y','<','L','Z',';'>',';'],
	 * 			['L','V','<','L','W',';'>',';'],
	 * 			['L','U',';']
	 * 		],
	 * 		[
	 * 			['L','B',';']
	 * 		]
	 * 	]
	 * 
	 * @param typeSignatures Array of signatures (one per each type levels)
	 * @throws IllegalArgumentException If one of provided signature is malformed
	 * @return char[][][] Array of type arguments for each signature
	 */
	public final static char[][][] getAllTypeArguments(char[][] typeSignatures) {
		// TODO JDT signature
//		if (typeSignatures == null) return null;
//		int length = typeSignatures.length;
//		char[][][] typeArguments = new char[length][][];
//		for (int i=0; i<length; i++){
//			typeArguments[i] = Signature.getTypeArguments(typeSignatures[i]);
//		}
//		return typeArguments;
		return null;
	}
	
	/**
	 * Reads in a string from the specified data input stream. The 
	 * string has been encoded using a modified UTF-8 format. 
	 * <p>
	 * The first two bytes are read as if by 
	 * <code>readUnsignedShort</code>. This value gives the number of 
	 * following bytes that are in the encoded string, not
	 * the length of the resulting string. The following bytes are then 
	 * interpreted as bytes encoding characters in the UTF-8 format 
	 * and are converted into characters. 
	 * <p>
	 * This method blocks until all the bytes are read, the end of the 
	 * stream is detected, or an exception is thrown. 
	 *
	 * @param      in   a data input stream.
	 * @return     a Unicode string.
	 * @exception  EOFException            if the input stream reaches the end
	 *               before all the bytes.
	 * @exception  IOException             if an I/O error occurs.
	 * @exception  UTFDataFormatException  if the bytes do not represent a
	 *               valid UTF-8 encoding of a Unicode string.
	 * @see        java.io.DataInputStream#readUnsignedShort()
	 */
	public final static char[] readUTF(DataInput in) throws IOException {
		int utflen= in.readUnsignedShort();
		char str[]= new char[utflen];
		int count= 0;
		int strlen= 0;
		while (count < utflen) {
			int c= in.readUnsignedByte();
			int char2, char3;
			switch (c >> 4) {
				case 0 :
				case 1 :
				case 2 :
				case 3 :
				case 4 :
				case 5 :
				case 6 :
				case 7 :
					// 0xxxxxxx
					count++;
					str[strlen++]= (char) c;
					break;
				case 12 :
				case 13 :
					// 110x xxxx   10xx xxxx
					count += 2;
					if (count > utflen)
						throw new UTFDataFormatException();
					char2= in.readUnsignedByte();
					if ((char2 & 0xC0) != 0x80)
						throw new UTFDataFormatException();
					str[strlen++]= (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
					break;
				case 14 :
					// 1110 xxxx  10xx xxxx  10xx xxxx
					count += 3;
					if (count > utflen)
						throw new UTFDataFormatException();
					char2= in.readUnsignedByte();
					char3= in.readUnsignedByte();
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
						throw new UTFDataFormatException();
					str[strlen++]= (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
					break;
				default :
					// 10xx xxxx,  1111 xxxx
					throw new UTFDataFormatException();
			}
		}
		if (strlen < utflen) {
			System.arraycopy(str, 0, str= new char[strlen], 0, strlen);
		}
		return str;
	}
	
	/**
	 * Writes a string to the given output stream using UTF-8 
	 * encoding in a machine-independent manner. 
	 * <p>
	 * First, two bytes are written to the output stream as if by the 
	 * <code>writeShort</code> method giving the number of bytes to 
	 * follow. This value is the number of bytes actually written out, 
	 * not the length of the string. Following the length, each character 
	 * of the string is output, in sequence, using the UTF-8 encoding 
	 * for the character. 
	 *
	 * @param      str   a string to be written.
	 * @return     the number of bytes written to the stream.
	 * @exception  IOException  if an I/O error occurs.
	 * @since      JDK1.0
	 */
	public static int writeUTF(OutputStream out, char[] str) throws IOException {
		int strlen= str.length;
		int utflen= 0;
		for (int i= 0; i < strlen; i++) {
			int c= str[i];
			if ((c >= 0x0001) && (c <= 0x007F)) {
				utflen++;
			} else if (c > 0x07FF) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}
		if (utflen > 65535)
			throw new UTFDataFormatException();
		out.write((utflen >>> 8) & 0xFF);
		out.write((utflen >>> 0) & 0xFF);
		if (strlen == utflen) {
			for (int i= 0; i < strlen; i++)
				out.write(str[i]);
		} else {
			for (int i= 0; i < strlen; i++) {
				int c= str[i];
				if ((c >= 0x0001) && (c <= 0x007F)) {
					out.write(c);
				} else if (c > 0x07FF) {
					out.write(0xE0 | ((c >> 12) & 0x0F));
					out.write(0x80 | ((c >> 6) & 0x3F));
					out.write(0x80 | ((c >> 0) & 0x3F));
				} else {
					out.write(0xC0 | ((c >> 6) & 0x1F));
					out.write(0x80 | ((c >> 0) & 0x3F));
				}
			}
		}
		return utflen + 2; // the number of bytes written to the stream
	}
	
	public static boolean isReadOnly(IResource resource) {
		ResourceAttributes resourceAttributes = resource.getResourceAttributes();
		if (resourceAttributes == null) return false; // not supported on this platform for this resource
		return resourceAttributes.isReadOnly();
	}
	
	/**
	 * Removes the extension from a filename, and returns the filename
	 * without it.
	 */
	public static String removeExtension(String filename) {
		int index = filename.lastIndexOf('.');
		if (index == -1) return filename;		
		return filename.substring(0, index);
	}
	
	public static int getApiLevel(Map options) {
		String source = (String) options.get(JavaCore.COMPILER_SOURCE);
		return getApiLevel(source);
	}
	
	public static int getApiLevel(IJavaElement element) {
		return getApiLevel(element.getJavaProject());
	}
	
	public static int getApiLevel(IJavaProject project) {
		String source = project.getOption(JavaCore.COMPILER_SOURCE, true);
		return getApiLevel(source);
	}
	
	private static int getApiLevel(String source) {
		if (source == null || source.length() == 0) {
			return AST.D2;
		} else if (source.equals(JavaCore.VERSION_2_x)) {
			return AST.D2;
		} else if (source.equals(JavaCore.VERSION_1_x)) {
			return AST.D1;
		} else if (source.equals(JavaCore.VERSION_0_x)) {
			return AST.D0;
		} else {
			return AST.D2;
		}
	}
	
	/**
	 * Sorts an array of Comparable objects, returning a new array
	 * with the sorted items.  The original array is left untouched.
	 */
	public static Comparable[] sortCopy(Comparable[] objects) {
		int len = objects.length;
		Comparable[] copy = new Comparable[len];
		System.arraycopy(objects, 0, copy, 0, len);
		sort(copy);
		return copy;
	}

	/**
	 * Sorts an array of Java elements based on their toStringWithAncestors(), 
	 * returning a new array with the sorted items. 
	 * The original array is left untouched.
	 */
	public static IJavaElement[] sortCopy(IJavaElement[] elements) {
		int len = elements.length;
		IJavaElement[] copy = new IJavaElement[len];
		System.arraycopy(elements, 0, copy, 0, len);
		sort(copy, new Comparer() {
			public int compare(Object a, Object b) {
				return ((JavaElement) a).toStringWithAncestors().compareTo(((JavaElement) b).toStringWithAncestors());
			}
		});
		return copy;
	}
	
	/**
	 * Sorts an array of Strings, returning a new array
	 * with the sorted items.  The original array is left untouched.
	 */
	public static Object[] sortCopy(Object[] objects, Comparer comparer) {
		int len = objects.length;
		Object[] copy = new Object[len];
		System.arraycopy(objects, 0, copy, 0, len);
		sort(copy, comparer);
		return copy;
	}

	/**
	 * Sorts an array of Strings, returning a new array
	 * with the sorted items.  The original array is left untouched.
	 */
	public static String[] sortCopy(String[] objects) {
		int len = objects.length;
		String[] copy = new String[len];
		System.arraycopy(objects, 0, copy, 0, len);
		sort(copy);
		return copy;
	}
	
	/**
	 * Sorts an array of strings in place using quicksort
	 * in reverse alphabetical order.
	 */
	public static void sortReverseOrder(String[] strings) {
		if (strings.length > 1)
			quickSortReverse(strings, 0, strings.length - 1);
	}
	
	/**
	 * Sort the strings in the given collection in reverse alphabetical order.
	 */
	private static void quickSortReverse(String[] sortedCollection, int left, int right) {
		int original_left = left;
		int original_right = right;
		String mid = sortedCollection[ (left + right) / 2];
		do {
			while (sortedCollection[left].compareTo(mid) > 0) {
				left++;
			}
			while (mid.compareTo(sortedCollection[right]) > 0) {
				right--;
			}
			if (left <= right) {
				String tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSortReverse(sortedCollection, original_left, right);
		}
		if (left < original_right) {
			quickSortReverse(sortedCollection, left, original_right);
		}
	}
	
}
