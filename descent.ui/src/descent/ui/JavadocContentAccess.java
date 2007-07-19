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
package descent.ui;
 
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import descent.core.IBuffer;
import descent.core.ICompilationUnit;
import descent.core.IDocumented;
import descent.core.IJavaElement;
import descent.core.IMethod;
import descent.core.IPackageDeclaration;
import descent.core.IParent;
import descent.core.ISourceRange;
import descent.core.JavaModelException;
import descent.core.ToolFactory;
import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.formatter.CodeFormatter;
import descent.internal.ui.infoviews.Ddoc;
import descent.internal.ui.infoviews.DdocMacros;
import descent.internal.ui.infoviews.DdocParser;
import descent.internal.ui.infoviews.DdocSection;
import descent.internal.ui.infoviews.DdocSection.Parameter;
import descent.internal.ui.text.HTMLPrinter;
import descent.ui.text.IJavaColorConstants;

/**
 * Helper needed to get the content of a Javadoc comment.
 * 
 * <p>
 * This class is not intended to be subclassed or instantiated by clients.
 * </p>
 *
 * @since 3.1
 */
public class JavadocContentAccess {
	
	private static Set<String> redSections;
	static {
		redSections = new TreeSet<String>();
		redSections.add("Bugs"); //$NON-NLS-1$
		redSections.add("Deprecated"); //$NON-NLS-1$
	}
	
	private JavadocContentAccess() {
		// do not instantiate
	}
	
	/**
	 * Gets a reader for an IMember's Javadoc comment content from the source attachment.
	 * The content does contain only the text from the comment without the Javadoc leading star characters.
	 * Returns <code>null</code> if the member does not contain a Javadoc comment or if no source is available.
	 * @param member The member to get the Javadoc of.
	 * @param allowInherited For methods with no (Javadoc) comment, the comment of the overridden class
	 * is returned if <code>allowInherited</code> is <code>true</code>.
	 * @return Returns a reader for the Javadoc comment content or <code>null</code> if the member
	 * does not contain a Javadoc comment or if no source is available
	 * @throws JavaModelException is thrown when the elements javadoc can not be accessed
	 */
	public static Reader getContentReader(IDocumented member, boolean allowInherited) throws JavaModelException {
		Ddoc ddoc = getDdoc(member);
		// Merge parents macros
		if (ddoc != null) {
			IJavaElement parent = member.getParent();
			while(parent instanceof IDocumented) {
				Ddoc otherDdoc = getDdoc((IDocumented) parent);
				if (otherDdoc != null) {
					ddoc.mergeMacros(otherDdoc);
				}
				parent = parent.getParent();
			}
			if (parent instanceof ICompilationUnit && !(member instanceof IPackageDeclaration)) {
				ICompilationUnit unit = (ICompilationUnit) parent;
				if (unit.getPackageDeclarations().length > 0) {
					Ddoc otherDdoc = getDdoc(unit.getPackageDeclarations()[0]);
					if (otherDdoc != null) {
						ddoc.mergeMacros(otherDdoc);
					}
				}
			}
			return getDdocReader(ddoc, member);
		}
		return null;
	}
	
	private static Ddoc getDdoc(IDocumented member) throws JavaModelException {
		IBuffer buf= member.getOpenable().getBuffer();
		if (buf == null) {
			return null; // no source attachment found
		}
		
		ISourceRange[] javadocRanges = member.getJavadocRanges();
		if (javadocRanges != null && javadocRanges.length > 0) {
			Ddoc ddoc = null;
			for(ISourceRange javadocRange : javadocRanges) {
				DdocParser parser = new DdocParser(buf.getText(javadocRange.getOffset(), javadocRange.getLength()));
				Ddoc ddoc2 = parser.parse();
				if (ddoc == null) {
					ddoc = ddoc2;
				} else {
					ddoc.merge(ddoc2);
				}
			}
			if (ddoc.isDitto()) {
				return findDittoOwner(member, ddoc);
			}
			return ddoc;
		}
		
		return null;
	}
	
	private static Ddoc findDittoOwner(IJavaElement member, Ddoc ddoc) throws JavaModelException {
		IJavaElement memberParent = member.getParent();
		if (memberParent instanceof IParent) {
			IParent parent = (IParent) memberParent;
			IJavaElement[] siblings = parent.getChildren();
			for(int i = 0; i < siblings.length; i++) {
				if (siblings[i].equals(member)) {
					for(int j = i - 1; j >= 0; j--) {
						IJavaElement target = siblings[j];
						if (target instanceof IDocumented) {
							Ddoc targetDdoc = getDdoc((IDocumented) target);
							if (targetDdoc != null && !targetDdoc.isDitto()) {
								return targetDdoc;
							}
						}
					}
					break;
				}
			}
		}
		return ddoc;
	}

	private static Reader getDdocReader(Ddoc ddoc, IDocumented member) {
		return new StringReader(transform(ddoc, member));
	}

	private static String transform(Ddoc ddoc, IDocumented member) {
		String showParameterTypesString = PreferenceConstants.getPreference(PreferenceConstants.DDOC_SHOW_PARAMETER_TYPES, null);
		boolean showParameterTypes = showParameterTypesString == null ? false : StringConverter.asBoolean(showParameterTypesString);
		
		Map<String, String> defaultMacros = DdocMacros.getDefaultMacros();
		Map<String, String> macros = mergeMacros(ddoc, defaultMacros);
		
		Map<String, String> parameters;
		if (showParameterTypes && member != null && member.getElementType() == IJavaElement.METHOD) {
			IMethod method = (IMethod) member;
			try {
				String[] parameterNames = method.getParameterNames();
				String[] parameterTypes = method.getRawParameterTypes();
				
				parameters = new HashMap<String, String>();
				for(int i = 0; i < parameterNames.length && i < parameterTypes.length; i++) {
					parameters.put(parameterNames[i], parameterTypes[i]);
				}
			} catch (JavaModelException e) {
				parameters = Collections.EMPTY_MAP;
			}
		} else {
			parameters = Collections.EMPTY_MAP;
		}
		
		StringBuffer buffer = new StringBuffer();
		
		for(DdocSection section : ddoc.getSections()) {
			switch(section.getKind()) {
			case DdocSection.NORMAL_SECTION:
				String text = DdocMacros.replaceMacros(section.getText(), macros);
				
				if (section.getName() != null) {
					buffer.append("<dl>"); //$NON-NLS-1$
					buffer.append("<dt>"); //$NON-NLS-1$
					
					boolean red = redSections.contains(section.getName());
					if (red) {
						buffer.append("<span style=\"color:red\">"); //$NON-NLS-1$
					}
					
					buffer.append(section.getName().replace('_', ' '));
					buffer.append(":"); //$NON-NLS-1$
					
					if (red) {
						buffer.append("</span>"); //$NON-NLS-1$
					}
					
					buffer.append("</dt>"); //$NON-NLS-1$
					buffer.append("<dd>"); //$NON-NLS-1$
					buffer.append(text);
					buffer.append("</dd>"); //$NON-NLS-1$					
					buffer.append("</dl>"); //$NON-NLS-1$
				} else {
					buffer.append(text);
				}
				break;
			case DdocSection.PARAMS_SECTION:
				buffer.append("<dl>"); //$NON-NLS-1$
				buffer.append("<dt>"); //$NON-NLS-1$
				buffer.append("Parameters:"); //$NON-NLS-1$
				buffer.append("</dt>"); //$NON-NLS-1$
				for(Parameter parameter : section.getParameters()) {
					buffer.append("<dd>"); //$NON-NLS-1$
					
					String type = parameters.get(parameter.getName());
					if (type != null) {
						buffer.append(type);
						buffer.append(" "); //$NON-NLS-1$
					}
					
					buffer.append("<b>"); //$NON-NLS-1$
					buffer.append(parameter.getName());
					buffer.append("</b>"); //$NON-NLS-1$
					buffer.append(" "); //$NON-NLS-1$
					buffer.append(parameter.getText());
					buffer.append("</dd>"); //$NON-NLS-1$
					buffer.append("<br/>"); //$NON-NLS-1$
				}
				buffer.append("</dl>"); //$NON-NLS-1$
				break;
			case DdocSection.CODE_SECTION:
				buffer.append("<dl>"); //$NON-NLS-1$
				buffer.append("<dd class=\"code\">"); //$NON-NLS-1$
				try {
					appendCode(buffer, section.getText());
				} catch (Exception e) {
					buffer.append(section.getText());
				}
				buffer.append("</dd>"); //$NON-NLS-1$
				buffer.append("</dl>"); //$NON-NLS-1$				
				break;
			}
			
			HTMLPrinter.addParagraph(buffer, ""); //$NON-NLS-1$
		}
		
		return buffer.toString();
	}

	private static Map<String, String> mergeMacros(Ddoc ddoc, Map<String, String> macros) {
		macros = new HashMap<String, String>(macros);
		if (ddoc.getMacrosSection() != null) {
			for(Parameter param : ddoc.getMacrosSection().getParameters()) {
				macros.put(param.getName(), param.getText());
			}
		}
		return macros;
	}

	private static void appendCode(StringBuffer buffer, String text) throws Exception {
		CodeFormatter formatter = ToolFactory.createCodeFormatter(null);
		try {
			// The most common example is something inside a function 
			TextEdit edit = formatter.format(CodeFormatter.K_STATEMENTS, text, 0, text.length(), 0, "\n"); //$NON-NLS-1$
			if (edit == null) {
				// If not, try parsing a whole compilation unit
				edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, text, 0, text.length(), 0, "\n"); //$NON-NLS-1$
			}
			if (edit != null) {
				Document doc = new Document(text);
				edit.apply(doc);
				text = doc.get();
			}
		} catch (Exception e) {
		}
		
		IScanner scanner = ToolFactory.createScanner(true, true, true, false);
		scanner.setSource(text.toCharArray());
		
		int token;
		while((token = scanner.getNextToken()) != ITerminalSymbols.TokenNameEOF) {
			String raw = scanner.getRawTokenSourceAsString();
			String styleClassName = null;
			switch(token) {
			case ITerminalSymbols.TokenNameabstract:
			case ITerminalSymbols.TokenNamealias:
			case ITerminalSymbols.TokenNamealign:
			case ITerminalSymbols.TokenNameasm:
			case ITerminalSymbols.TokenNameassert:
			case ITerminalSymbols.TokenNameauto:
			case ITerminalSymbols.TokenNamebody:
			case ITerminalSymbols.TokenNamebreak:
			case ITerminalSymbols.TokenNamecase:
			case ITerminalSymbols.TokenNamecast:
			case ITerminalSymbols.TokenNamecatch:
			case ITerminalSymbols.TokenNameclass:
			case ITerminalSymbols.TokenNameconst:
			case ITerminalSymbols.TokenNamecontinue:
			case ITerminalSymbols.TokenNamedebug:
			case ITerminalSymbols.TokenNamedefault:
			case ITerminalSymbols.TokenNamedelegate:
			case ITerminalSymbols.TokenNamedelete:
			case ITerminalSymbols.TokenNamedeprecated:
			case ITerminalSymbols.TokenNamedo:
			case ITerminalSymbols.TokenNameelse:
			case ITerminalSymbols.TokenNameenum:
			case ITerminalSymbols.TokenNameexport:
			case ITerminalSymbols.TokenNameextern:
			case ITerminalSymbols.TokenNamefinal:
			case ITerminalSymbols.TokenNamefinally:
			case ITerminalSymbols.TokenNamefor:
			case ITerminalSymbols.TokenNameforeach:
			case ITerminalSymbols.TokenNameforeach_reverse:
			case ITerminalSymbols.TokenNamefunction:
			case ITerminalSymbols.TokenNamegoto:
			case ITerminalSymbols.TokenNameif:
			case ITerminalSymbols.TokenNameiftype:
			case ITerminalSymbols.TokenNameimport:
			case ITerminalSymbols.TokenNamein:
			case ITerminalSymbols.TokenNameinout:
			case ITerminalSymbols.TokenNameinterface:
			case ITerminalSymbols.TokenNameinvariant:
			case ITerminalSymbols.TokenNameis:
			case ITerminalSymbols.TokenNamelazy:
			case ITerminalSymbols.TokenNamemixin:
			case ITerminalSymbols.TokenNamemodule:
			case ITerminalSymbols.TokenNamenew:
			case ITerminalSymbols.TokenNameout:
			case ITerminalSymbols.TokenNameoverride:
			case ITerminalSymbols.TokenNamepackage:
			case ITerminalSymbols.TokenNamepragma:
			case ITerminalSymbols.TokenNameprivate:
			case ITerminalSymbols.TokenNameprotected:
			case ITerminalSymbols.TokenNamepublic:
			case ITerminalSymbols.TokenNamescope:
			case ITerminalSymbols.TokenNamestatic:
			case ITerminalSymbols.TokenNamestruct:
			case ITerminalSymbols.TokenNamesuper:
			case ITerminalSymbols.TokenNameswitch:
			case ITerminalSymbols.TokenNamesynchronized:
			case ITerminalSymbols.TokenNametemplate:
			case ITerminalSymbols.TokenNamethis:
			case ITerminalSymbols.TokenNamethrow:
			case ITerminalSymbols.TokenNametry:
			case ITerminalSymbols.TokenNametypedef:
			case ITerminalSymbols.TokenNametypeid:
			case ITerminalSymbols.TokenNametypeof:
			case ITerminalSymbols.TokenNameunion:
			case ITerminalSymbols.TokenNameunittest:
			case ITerminalSymbols.TokenNameversion:
			case ITerminalSymbols.TokenNamevolatile:
			case ITerminalSymbols.TokenNamewhile:
			case ITerminalSymbols.TokenNamewith:
				
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
				styleClassName = IJavaColorConstants.JAVA_KEYWORD;
				break;
			case ITerminalSymbols.TokenNamereturn:
				styleClassName = IJavaColorConstants.JAVA_KEYWORD_RETURN;
				break;
			case ITerminalSymbols.TokenNameAND:
			case ITerminalSymbols.TokenNameAND_AND:
			case ITerminalSymbols.TokenNameAND_EQUAL:
			case ITerminalSymbols.TokenNameCOLON:
			case ITerminalSymbols.TokenNameCOMMA:
			case ITerminalSymbols.TokenNameDIVIDE:
			case ITerminalSymbols.TokenNameDIVIDE_EQUAL:
			case ITerminalSymbols.TokenNameDOLLAR:
			case ITerminalSymbols.TokenNameDOT:
			case ITerminalSymbols.TokenNameDOT_DOT:
			case ITerminalSymbols.TokenNameDOT_DOT_DOT:
			case ITerminalSymbols.TokenNameEQUAL:
			case ITerminalSymbols.TokenNameEQUAL_EQUAL:
			case ITerminalSymbols.TokenNameEQUAL_EQUAL_EQUAL:
			case ITerminalSymbols.TokenNameGREATER:
			case ITerminalSymbols.TokenNameGREATER_EQUAL:
			case ITerminalSymbols.TokenNameLBRACE:
			case ITerminalSymbols.TokenNameLBRACKET:
			case ITerminalSymbols.TokenNameLEFT_SHIFT:
			case ITerminalSymbols.TokenNameLEFT_SHIFT_EQUAL:
			case ITerminalSymbols.TokenNameLESS:
			case ITerminalSymbols.TokenNameLESS_EQUAL:
			case ITerminalSymbols.TokenNameLESS_GREATER:
			case ITerminalSymbols.TokenNameLESS_GREATER_EQUAL:
			case ITerminalSymbols.TokenNameLPAREN:
			case ITerminalSymbols.TokenNameMINUS:
			case ITerminalSymbols.TokenNameMINUS_EQUAL:
			case ITerminalSymbols.TokenNameMINUS_MINUS:
			case ITerminalSymbols.TokenNameMULTIPLY:
			case ITerminalSymbols.TokenNameMULTIPLY_EQUAL:
			case ITerminalSymbols.TokenNameNOT:
			case ITerminalSymbols.TokenNameNOT_EQUAL:
			case ITerminalSymbols.TokenNameNOT_EQUAL_EQUAL:
			case ITerminalSymbols.TokenNameNOT_GREATER:
			case ITerminalSymbols.TokenNameNOT_GREATER_EQUAL:
			case ITerminalSymbols.TokenNameNOT_LESS:
			case ITerminalSymbols.TokenNameNOT_LESS_EQUAL:
			case ITerminalSymbols.TokenNameNOT_LESS_GREATER:
			case ITerminalSymbols.TokenNameNOT_LESS_GREATER_EQUAL:
			case ITerminalSymbols.TokenNameOR:
			case ITerminalSymbols.TokenNameOR_EQUAL:
			case ITerminalSymbols.TokenNameOR_OR:
			case ITerminalSymbols.TokenNamePLUS:
			case ITerminalSymbols.TokenNamePLUS_EQUAL:
			case ITerminalSymbols.TokenNamePLUS_PLUS:
			case ITerminalSymbols.TokenNameQUESTION:
			case ITerminalSymbols.TokenNameRBRACE:
			case ITerminalSymbols.TokenNameRBRACKET:
			case ITerminalSymbols.TokenNameREMAINDER:
			case ITerminalSymbols.TokenNameREMAINDER_EQUAL:
			case ITerminalSymbols.TokenNameRIGHT_SHIFT:
			case ITerminalSymbols.TokenNameRIGHT_SHIFT_EQUAL:
			case ITerminalSymbols.TokenNameRPAREN:
			case ITerminalSymbols.TokenNameSEMICOLON:
			case ITerminalSymbols.TokenNameTILDE:
			case ITerminalSymbols.TokenNameTILDE_EQUAL:
			case ITerminalSymbols.TokenNameUNSIGNED_RIGHT_SHIFT:
			case ITerminalSymbols.TokenNameUNSIGNED_RIGHT_SHIFT_EQUAL:
			case ITerminalSymbols.TokenNameXOR:
			case ITerminalSymbols.TokenNameXOR_EQUAL:
				styleClassName = IJavaColorConstants.JAVA_OPERATOR;				
				break;
			case ITerminalSymbols.TokenNamePRAGMA:
				styleClassName = IJavaColorConstants.JAVA_PRAGMA;
				break;
			case ITerminalSymbols.TokenNameCharacterLiteral:
			case ITerminalSymbols.TokenNameStringLiteral:
				styleClassName = IJavaColorConstants.JAVA_STRING;
				break;
			case ITerminalSymbols.TokenNameCOMMENT_BLOCK:
				styleClassName = IJavaColorConstants.JAVA_MULTI_LINE_COMMENT;
				break;
			case ITerminalSymbols.TokenNameCOMMENT_DOC_BLOCK:
				styleClassName = IJavaColorConstants.JAVADOC_DEFAULT;
				break;
			case ITerminalSymbols.TokenNameCOMMENT_DOC_LINE:
				styleClassName = IJavaColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT;
				raw += "<br/>"; //$NON-NLS-1$
				break;
			case ITerminalSymbols.TokenNameCOMMENT_DOC_PLUS:
				styleClassName = IJavaColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT;
				break;
			case ITerminalSymbols.TokenNameCOMMENT_LINE:
				styleClassName = IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT;
				raw += "<br/>"; //$NON-NLS-1$
				break;
			case ITerminalSymbols.TokenNameCOMMENT_PLUS:
				styleClassName = IJavaColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT;
				break;
			case ITerminalSymbols.TokenNameWHITESPACE:
				styleClassName = null;
			default:
				styleClassName = IJavaColorConstants.JAVA_DEFAULT;
			}
			if (styleClassName != null) {
				buffer.append("<span class=\"" + styleClassName + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			if (token == ITerminalSymbols.TokenNameWHITESPACE) {
				raw = raw.replace(" ", "&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
				raw = raw.replace("\n", "<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
				raw = raw.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			buffer.append(raw);
			if (styleClassName != null) {
				buffer.append("</span>"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Gets a reader for an IMember's Javadoc comment content from the source attachment.
	 * and renders the tags in HTML. 
	 * Returns <code>null</code> if the member does not contain a Javadoc comment or if no source is available.
	 * 
	 * @param member				the member to get the Javadoc of.
	 * @param allowInherited		for methods with no (Javadoc) comment, the comment of the overridden
	 * 									class is returned if <code>allowInherited</code> is <code>true</code>
	 * @param useAttachedJavadoc	if <code>true</code> Javadoc will be extracted from attached Javadoc
	 * 									if there's no source
	 * @return a reader for the Javadoc comment content in HTML or <code>null</code> if the member
	 * 			does not contain a Javadoc comment or if no source is available
	 * @throws JavaModelException is thrown when the elements Javadoc can not be accessed
	 * @since 3.2
	 */
	public static Reader getHTMLContentReader(IDocumented member, boolean allowInherited, boolean useAttachedJavadoc) throws JavaModelException {
		Reader contentReader= getContentReader(member, allowInherited);
		if (contentReader != null) {
			// return new JavaDoc2HTMLTextReader(contentReader);
			return contentReader;
		}
		
		if (useAttachedJavadoc && member.getOpenable().getBuffer() == null) { // only if no source available
			/* TODO JDT UI attached javadoc
			String s= member.getAttachedJavadoc(null);
			if (s != null)
				return new StringReader(s);
			*/
		}
		return null;
	}

	private static Reader findDocInHierarchy(IMethod method) throws JavaModelException {
		/* TODO JDT UI type hierarchy
		IType type= method.getDeclaringType();
		ITypeHierarchy hierarchy= type.newSupertypeHierarchy(null);
		
		MethodOverrideTester tester= new MethodOverrideTester(type, hierarchy);
		
		IType[] superTypes= hierarchy.getAllSupertypes(type);
		for (int i= 0; i < superTypes.length; i++) {
			IType curr= superTypes[i];
			IMethod overridden= tester.findOverriddenMethodInType(curr, method);
			if (overridden != null) {
				Reader reader= getContentReader(overridden, false);
				if (reader != null) {
					return reader;
				}
			}
		}
		*/
		return null;
	}		

}
