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
package descent.internal.core;

import java.util.Map;

import descent.core.Flags;
import descent.core.IJavaProject;
import descent.core.ToolFactory;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.compiler.InvalidInputException;
import descent.internal.codeassist.impl.AssistOptions;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ScannerHelper;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.TypeInstance;

public class InternalNamingConventions {
	private static final char[] DEFAULT_NAME = "name".toCharArray(); //$NON-NLS-1$
	
	private static IScanner getNameScanner(CompilerOptions compilerOptions) {
		return ToolFactory.createScanner(
				false /* comment */, 
				false /* pragma */,
				false /* whitespace */,
				false /* line separator */);
	}
	public static void suggestArgumentNames(IJavaProject javaProject, char[] packageName, char[] qualifiedTypeName, int dim, char[] internalPrefix, char[][] excludedNames, INamingRequestor requestor) {
		Map options = javaProject.getOptions(true);
		CompilerOptions compilerOptions = new CompilerOptions(options);
		AssistOptions assistOptions = new AssistOptions(options);

		suggestNames(
			packageName,
			qualifiedTypeName,
			dim,
			internalPrefix,
			assistOptions.argumentPrefixes,
			assistOptions.argumentSuffixes,
			excludedNames,
			getNameScanner(compilerOptions),
			requestor);
	}
	public static void suggestFieldNames(IJavaProject javaProject, char[] packageName, char[] qualifiedTypeName, int dim, int modifiers, char[] internalPrefix, char[][] excludedNames, INamingRequestor requestor) {
		boolean isStatic = Flags.isStatic(modifiers);
		
		Map options = javaProject.getOptions(true);
		CompilerOptions compilerOptions = new CompilerOptions(options);
		AssistOptions assistOptions = new AssistOptions(options);

		suggestNames(
			packageName,
			qualifiedTypeName,
			dim,
			internalPrefix,
			isStatic ? assistOptions.staticFieldPrefixes : assistOptions.fieldPrefixes,
			isStatic ? assistOptions.staticFieldSuffixes : assistOptions.fieldSuffixes,
			excludedNames,
			getNameScanner(compilerOptions),
			requestor);
	}
	public static void suggestLocalVariableNames(IJavaProject javaProject, char[] packageName, char[] qualifiedTypeName, int dim, char[] internalPrefix, char[][] excludedNames, INamingRequestor requestor) {
		Map options = javaProject.getOptions(true);
		CompilerOptions compilerOptions = new CompilerOptions(options);
		AssistOptions assistOptions = new AssistOptions(options);

		suggestNames(
			packageName,
			qualifiedTypeName,
			dim,
			internalPrefix,
			assistOptions.localPrefixes,
			assistOptions.localSuffixes,
			excludedNames,
			getNameScanner(compilerOptions),
			requestor);
	}
	
	private static void suggestNames(
		char[] packageName,
		char[] qualifiedTypeName,
		int dim,
		char[] internalPrefix,
		char[][] prefixes,
		char[][] suffixes,
		char[][] excludedNames,
		IScanner nameScanner,
		INamingRequestor requestor){
		
		if(qualifiedTypeName == null || qualifiedTypeName.length == 0)
			return;
		
		if(internalPrefix == null) {
			internalPrefix = CharOperation.NO_CHAR;
		} else {
			internalPrefix = removePrefix(internalPrefix, prefixes);
		}
		
		char[] typeName = CharOperation.lastSegment(qualifiedTypeName, '.');
	
		if(prefixes == null || prefixes.length == 0) {
			prefixes = new char[1][0];
		} else {
			int length = prefixes.length;
			System.arraycopy(prefixes, 0, prefixes = new char[length+1][], 0, length);
			prefixes[length] = CharOperation.NO_CHAR;
		}
	
		if(suffixes == null || suffixes.length == 0) {
			suffixes = new char[1][0];
		} else {
			int length = suffixes.length;
			System.arraycopy(suffixes, 0, suffixes = new char[length+1][], 0, length);
			suffixes[length] = CharOperation.NO_CHAR;
		}
	
		char[][] tempNames = null;
	
		// compute variable name for base type
		try{
			nameScanner.setSource(typeName);
			switch (nameScanner.getNextToken()) {
			case ITerminalSymbols.TokenNamebool:
			case ITerminalSymbols.TokenNamebyte:
			case ITerminalSymbols.TokenNamecdouble:
			case ITerminalSymbols.TokenNamecent:
			case ITerminalSymbols.TokenNamecfloat:
			case ITerminalSymbols.TokenNamechar:
			case ITerminalSymbols.TokenNamecreal:
			case ITerminalSymbols.TokenNamedchar:
			case ITerminalSymbols.TokenNamedouble:
			case ITerminalSymbols.TokenNamefloat:
			case ITerminalSymbols.TokenNameidouble:
			case ITerminalSymbols.TokenNameifloat:
			case ITerminalSymbols.TokenNameint:
			case ITerminalSymbols.TokenNameireal:
			case ITerminalSymbols.TokenNamelong:
			case ITerminalSymbols.TokenNamereal:
			case ITerminalSymbols.TokenNameshort:
			case ITerminalSymbols.TokenNameubyte:
			case ITerminalSymbols.TokenNameucent:
			case ITerminalSymbols.TokenNameuint:
			case ITerminalSymbols.TokenNameulong:
			case ITerminalSymbols.TokenNameushort:
			case ITerminalSymbols.TokenNamevoid:
			case ITerminalSymbols.TokenNamewchar:
					char[] name = computeBaseTypeNames(typeName[0], excludedNames);
					if(name != null) {
						tempNames =  new char[][]{name};
					}
					break;
			}	
		} catch(InvalidInputException e){
			// ignore
		}

		// compute variable name for non base type
		if(tempNames == null) {
			tempNames = computeNames(typeName);
		}
	
		boolean acceptDefaultName = true;
		
		next : for (int i = 0; i < tempNames.length; i++) {
			char[] tempName = tempNames[i];
			if(dim > 0) {
				int length = tempName.length;
				if (tempName[length-1] == 's'){
					if(tempName.length > 1 && tempName[length-2] == 's') {
						System.arraycopy(tempName, 0, tempName = new char[length + 2], 0, length);
						tempName[length] = 'e';
						tempName[length+1] = 's';
					}
				} else if(tempName[length-1] == 'y') {
					System.arraycopy(tempName, 0, tempName = new char[length + 2], 0, length);
					tempName[length-1] = 'i';
					tempName[length] = 'e';
					tempName[length+1] = 's';
				} else {
					System.arraycopy(tempName, 0, tempName = new char[length + 1], 0, length);
					tempName[length] = 's';
				}
			}
		
			char[] unprefixedName = tempName;
			for (int j = 0; j <= internalPrefix.length; j++) {
				if(j == internalPrefix.length || CharOperation.prefixEquals(CharOperation.subarray(internalPrefix, j, -1), unprefixedName, false)) {
					tempName = CharOperation.concat(CharOperation.subarray(internalPrefix, 0, j), unprefixedName);
					if(j != 0) tempName[j] = ScannerHelper.toUpperCase(tempName[j]);
					for (int k = 0; k < prefixes.length; k++) {
						if(prefixes[k].length > 0
							&& ScannerHelper.isLetterOrDigit(prefixes[k][prefixes[k].length - 1])) {
							tempName[0] = ScannerHelper.toUpperCase(tempName[0]);
						} else {
							tempName[0] = ScannerHelper.toLowerCase(tempName[0]);
						}
						char[] prefixName = CharOperation.concat(prefixes[k], tempName);
						for (int l = 0; l < suffixes.length; l++) {
							char[] suffixName = CharOperation.concat(prefixName, suffixes[l]);
							suffixName =
								excludeNames(
									suffixName,
									prefixName,
									suffixes[l],
									excludedNames);
							try{
								nameScanner.setSource(suffixName);
								switch (nameScanner.getNextToken()) {
									case ITerminalSymbols.TokenNameIdentifier :
										int token = nameScanner.getNextToken();
										// TODO check if JDT's nameScanner.startPosition equals D's getCurrentTokenStartPosition()
										if (token == ITerminalSymbols.TokenNameEOF && nameScanner.getCurrentTokenStartPosition() == suffixName.length) {
											acceptName(suffixName, prefixes[k], suffixes[l],  k == 0, l == 0, internalPrefix.length - j, requestor);
											acceptDefaultName = false;
										}
										break;
									default:
										suffixName = CharOperation.concat(
											prefixName,
											String.valueOf(1).toCharArray(),
											suffixes[l]
										);
										suffixName =
											excludeNames(
												suffixName,
												prefixName,
												suffixes[l],
												excludedNames);
										nameScanner.setSource(suffixName);
										switch (nameScanner.getNextToken()) {
											case ITerminalSymbols.TokenNameIdentifier :
												token = nameScanner.getNextToken();
												if (token == ITerminalSymbols.TokenNameEOF && nameScanner.getCurrentTokenStartPosition() == suffixName.length) {
													acceptName(suffixName, prefixes[k], suffixes[l], k == 0, l == 0, internalPrefix.length - j, requestor);
													acceptDefaultName = false;
												}
										}
								}
							} catch(InvalidInputException e){
								// ignore
							}
						}
					}
					continue next;
				}
			}
		}
		// if no names were found
		if(acceptDefaultName) {
			char[] name = excludeNames(DEFAULT_NAME, DEFAULT_NAME, CharOperation.NO_CHAR, excludedNames);
			requestor.acceptNameWithoutPrefixAndSuffix(name, 0);
		}
	}
	
	private static void acceptName(
		char[] name,
		char[] prefix,
		char[] suffix,
		boolean isFirstPrefix,
		boolean isFirstSuffix,
		int reusedCharacters,
		INamingRequestor requestor) {
		if(prefix.length > 0 && suffix.length > 0) {
			requestor.acceptNameWithPrefixAndSuffix(name, isFirstPrefix, isFirstSuffix, reusedCharacters);
		} else if(prefix.length > 0){
			requestor.acceptNameWithPrefix(name, isFirstPrefix, reusedCharacters);
		} else if(suffix.length > 0){
			requestor.acceptNameWithSuffix(name, isFirstSuffix, reusedCharacters);
		} else {
			requestor.acceptNameWithoutPrefixAndSuffix(name, reusedCharacters);
		}
	}
	
	private static char[] computeBaseTypeNames(char firstName, char[][] excludedNames){
		char[] name = new char[]{firstName};
		
		for(int i = 0 ; i < excludedNames.length ; i++){
			if(CharOperation.equals(name, excludedNames[i], false)) {
				name[0]++;
				if(name[0] > 'z')
					name[0] = 'a';
				if(name[0] == firstName)
					return null;
				i = 0;
			}	
		}
		
		return name;
	}
	
	private static char[][] computeNames(char[] sourceName){
		char[][] names = new char[5][];
		int nameCount = 0;
		boolean previousIsUpperCase = false;
		boolean previousIsLetter = true;
		for(int i = sourceName.length - 1 ; i >= 0 ; i--){
			boolean isUpperCase = ScannerHelper.isUpperCase(sourceName[i]) || i == 0;
			boolean isLetter = ScannerHelper.isLetter(sourceName[i]);
			if(isUpperCase && /* !previousIsUpperCase && */ previousIsLetter){
				char[] name = CharOperation.subarray(sourceName,i,sourceName.length);
				if(name.length > 1){
					if(nameCount == names.length) {
						System.arraycopy(names, 0, names = new char[nameCount * 2][], 0, nameCount);
					}
					name[0] = ScannerHelper.toLowerCase(name[0]);
					names[nameCount++] = name;
				}
			}
			previousIsUpperCase = isUpperCase;
			previousIsLetter = isLetter;
		}
		if(nameCount == 0){
			names[nameCount++] = CharOperation.toLowerCase(sourceName);				
		}
		System.arraycopy(names, 0, names = new char[nameCount][], 0, nameCount);
		return names;
	}

	private static char[] excludeNames(
		char[] suffixName,
		char[] prefixName,
		char[] suffix,
		char[][] excludedNames) {
		int count = 2;
		int m = 0;
		while (m < excludedNames.length) {
			if(CharOperation.equals(suffixName, excludedNames[m], false)) {
				suffixName = CharOperation.concat(
					prefixName,
					String.valueOf(count++).toCharArray(),
					suffix
				);
				m = 0;
			} else {
				m++;
			}
		}
		return suffixName;
	}
	
	private static char[] removePrefix(char[] name, char[][] prefixes) {
		// remove longer prefix
		char[] withoutPrefixName = name;
		if (prefixes != null) {
			int bestLength = 0;
			int nameLength = name.length;
			for (int i= 0; i < prefixes.length; i++) {
				char[] prefix = prefixes[i];
				
				int prefixLength = prefix.length;
				if(prefixLength <= nameLength) {
					if(CharOperation.prefixEquals(prefix, name, false)) {
						if (prefixLength > bestLength) {
							bestLength = prefixLength;
						}
					}
				} else {
					int currLen = 0;
					for (; currLen < nameLength; currLen++) {
						if(ScannerHelper.toLowerCase(prefix[currLen]) != ScannerHelper.toLowerCase(name[currLen])) {
							if (currLen > bestLength) {
								bestLength = currLen;
							}
							break;
						}
					}
					if(currLen == nameLength && currLen > bestLength) {
						bestLength = currLen;
					}
				}
			}
			if(bestLength > 0) {
				if(bestLength == nameLength) {
					withoutPrefixName = CharOperation.NO_CHAR;
				} else {
					withoutPrefixName = CharOperation.subarray(name, bestLength, nameLength);
				}
			}
		}
//		
//		
//		// remove longer prefix
//		char[] withoutPrefixName = name;
//		if (prefixes != null) {
//			int bestLength = 0;
//			for (int i= 0; i < prefixes.length; i++) {
//				char[] prefix = prefixes[i];
//				int max = prefix.length < name.length ? prefix.length : name.length;
//				int currLen = 0;
//				for (; currLen < max; currLen++) {
//					if(Character.toLowerCase(prefix[currLen]) != Character.toLowerCase(name[currLen])) {
//						if (currLen > bestLength) {
//							bestLength = currLen;
//						}
//						break;
//					}
//				}
//				if(currLen == max && currLen > bestLength) {
//					bestLength = max;
//				}
//			}
//			if(bestLength > 0) {
//				if(bestLength == name.length) {
//					withoutPrefixName = CharOperation.NO_CHAR;
//				} else {
//					withoutPrefixName = CharOperation.subarray(name, bestLength, name.length);
//				}
//			}
//		}
		
		return withoutPrefixName;
	}
	
	public static final boolean prefixEquals(char[] prefix, char[] name) {

		int max = prefix.length;
		if (name.length < max)
			return false;
		for (int i = max;
			--i >= 0;
			) // assumes the prefix is not larger than the name
				if (prefix[i] != name[i])
					return false;
			return true;
	}
	
	/**
	 * Returns a readable name for the given type.
	 */
	public static final char[] readableName(Type type) {
		StringBuilder sb = new StringBuilder();
		
		appendReadableName(type, sb);
		
		char[] ret = new char[sb.length()];
		sb.getChars(0, sb.length(), ret, 0);
		return ret;
	}
	
	private static final void appendReadableName(Type type, StringBuilder sb) {
		switch(type.getNodeType()) {
		case ASTDmdNode.TYPE_A_ARRAY:
			TypeAArray taa = (TypeAArray) type;			
			appendUppercaseReadableName(taa.index, sb);			
			sb.append("To");
			appendUppercaseReadableName(taa.next, sb);
			break;
		case ASTDmdNode.TYPE_BASIC:
			sb.append(type.toCharArray());
			break;
		case ASTDmdNode.TYPE_D_ARRAY:
			appendReadableName(type.nextOf(), sb);
			if (sb.length() == 0 || sb.charAt(sb.length() - 1) != 's') {
				sb.append('s');
			}
			break;
		case ASTDmdNode.TYPE_DELEGATE:
			appendReadableName(type.nextOf(), sb);
			sb.append("Dg");
			break;
		case ASTDmdNode.TYPE_DOT_ID_EXP:
			sb.append(type.toCharArray());
			break;
		case ASTDmdNode.TYPE_FUNCTION:
			appendReadableName(type.nextOf(), sb);
			break;
		case ASTDmdNode.TYPE_IDENTIFIER:
			sb.append(type.toCharArray());
			break;
		case ASTDmdNode.TYPE_INSTANCE:
			TypeInstance ti = (TypeInstance) type;
			sb.append(ti.tempinst.name);
			break;
		case ASTDmdNode.TYPE_POINTER:
			if (type.nextOf().getNodeType() == ASTDmdNode.TYPE_FUNCTION) {
				appendReadableName(type.nextOf().nextOf(), sb);
				sb.append("Func");
			} else {
				sb.append('p');
				appendUppercaseReadableName(type.nextOf(), sb);
			}
			break;
		case ASTDmdNode.TYPE_S_ARRAY:
			appendReadableName(type.nextOf(), sb);
			if (sb.length() == 0 || sb.charAt(sb.length() - 1) != 's') {
				sb.append('s');
			}
			break;
		case ASTDmdNode.TYPE_SLICE:
			appendReadableName(type.nextOf(), sb);
			break;
		case ASTDmdNode.TYPE_TYPEOF:
			sb.append("typeof");
			break;
		}
	}
	
	private static final void appendUppercaseReadableName(Type type, StringBuilder sb) {
		StringBuilder sb2 = new StringBuilder();
		appendReadableName(type, sb2);
		if (sb2.length() > 0) {
			sb2.setCharAt(0, Character.toUpperCase(sb2.charAt(0)));
		}
		sb.append(sb2);
	}
	
}
