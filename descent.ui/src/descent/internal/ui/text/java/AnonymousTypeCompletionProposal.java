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
package descent.internal.ui.text.java;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.swt.graphics.Image;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.dom.rewrite.ImportRewrite;
import descent.core.formatter.CodeFormatter;
import descent.internal.corext.template.java.SignatureUtil;
import descent.internal.corext.util.CodeFormatterUtil;
import descent.internal.corext.util.JavaModelUtil;
import descent.internal.corext.util.Strings;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;

public class AnonymousTypeCompletionProposal extends JavaTypeCompletionProposal implements ICompletionProposalExtension4 {

	private String fDeclarationSignature;
	private IType fSuperType;

	public AnonymousTypeCompletionProposal(IJavaProject jproject, ICompilationUnit cu, int start, int length, String constructorCompletion, String displayName, String declarationSignature, int relevance) {
		super(constructorCompletion, cu, start, length, null, displayName, relevance);
		Assert.isNotNull(declarationSignature);
		Assert.isNotNull(jproject);
		Assert.isNotNull(cu);

		fDeclarationSignature= declarationSignature;
		fSuperType= getDeclaringType(jproject, SignatureUtil.stripSignatureToFQN(String.valueOf(declarationSignature)));

		setImage(getImageForType(fSuperType));
		setCursorPosition(constructorCompletion.indexOf('(') + 1);
	}

	private int createDummy(String name, StringBuffer buffer) throws JavaModelException {
		String lineDelim= "\n"; // Using newline is ok since source is used in dummy compilation unit //$NON-NLS-1$
		buffer.append("class "); //$NON-NLS-1$
		buffer.append(name);
		if (fSuperType.isInterface())
			buffer.append(" implements "); //$NON-NLS-1$
		else
			buffer.append(" extends "); //$NON-NLS-1$
		if (fDeclarationSignature != null)
			buffer.append(Signature.toString(fDeclarationSignature,
					false /* don't fully qualify names */));
		else
			buffer.append(fSuperType.getFullyQualifiedParameterizedName());
		int start= buffer.length();
		buffer.append("{"); //$NON-NLS-1$
		buffer.append(lineDelim);
		buffer.append(lineDelim);
		buffer.append("}"); //$NON-NLS-1$
		return start;
	}

	private boolean createStubs(StringBuffer buffer, ImportRewrite importRewrite) throws CoreException {
		/* TODO JDT UI code assist
		if (importRewrite == null)
			return false;
		if (fSuperType == null)
			return true;
		ICompilationUnit copy= null;
		try {
			final String name= "Type" + System.currentTimeMillis(); //$NON-NLS-1$
			copy= fCompilationUnit.getPrimary().getWorkingCopy(null);
			final StringBuffer contents= new StringBuffer();
			int start= 0;
			int end= 0;
			ISourceRange range= fSuperType.getSourceRange();
			final boolean sameUnit= range != null && fCompilationUnit.equals(fSuperType.getCompilationUnit());
			final StringBuffer dummy= new StringBuffer();
			final int length= createDummy(name, dummy);
			contents.append(fCompilationUnit.getBuffer().getContents());
			if (sameUnit) {
				final int size= range.getOffset() + range.getLength();
				start= size + length;
				end= contents.length() - size;
				contents.insert(size, dummy.toString());
			} else {
				range= fCompilationUnit.getTypes()[0].getSourceRange();
				start= range.getOffset() + length;
				end= contents.length() - range.getOffset();
				contents.insert(range.getOffset(), dummy.toString());
			}
			copy.getBuffer().setContents(contents.toString());
			JavaModelUtil.reconcile(copy);
			final ASTParser parser= ASTParser.newParser(AST.D2);
			parser.setResolveBindings(true);
			parser.setSource(copy);
			final CompilationUnit unit= (CompilationUnit) parser.createAST(new NullProgressMonitor());
			IType type= null;
			IType[] types= copy.getAllTypes();
			for (int index= 0; index < types.length; index++) {
				IType result= types[index];
				if (result.getElementName().equals(name)) {
					type= result;
					break;
				}
			}
			if (type != null && type.exists()) {
				ITypeBinding binding= null;
				final AbstractTypeDeclaration declaration= (AbstractTypeDeclaration) ASTNodes.getParent(NodeFinder.perform(unit, type.getNameRange()), AbstractTypeDeclaration.class);
				if (declaration != null) {
					binding= declaration.resolveBinding();
					if (binding != null) {
						IMethodBinding[] bindings= StubUtility2.getOverridableMethods(unit.getAST(), binding, true);
						CodeGenerationSettings settings= JavaPreferencesSettings.getCodeGenerationSettings(fSuperType.getJavaProject());
						String[] keys= null;
						if (!fSuperType.isInterface() && !fSuperType.isAnnotation()) {
							OverrideMethodDialog dialog= new OverrideMethodDialog(JavaPlugin.getActiveWorkbenchShell(), null, type, true);
							dialog.setGenerateComment(false);
							dialog.setElementPositionEnabled(false);
							if (dialog.open() == Window.OK) {
								Object[] selection= dialog.getResult();
								if (selection != null) {
									ArrayList result= new ArrayList(selection.length);
									for (int index= 0; index < selection.length; index++) {
										if (selection[index] instanceof IMethodBinding)
											result.add(((IBinding) selection[index]).getKey());
									}
									keys= (String[]) result.toArray(new String[result.size()]);
									settings.createComments= dialog.getGenerateComment();
								}
							}
						} else {
							settings.createComments= false;
							List list= new ArrayList();
							for (int index= 0; index < bindings.length; index++) {
								if (Modifier.isAbstract(bindings[index].getModifiers()))
									list.add(bindings[index].getKey());
							}
							keys= (String[]) list.toArray(new String[list.size()]);
						}
						if (keys == null) {
							setReplacementString(""); //$NON-NLS-1$
							setReplacementLength(0);
							return false;
						}
						ASTRewrite rewrite= ASTRewrite.create(unit.getAST());
						ListRewrite rewriter= rewrite.getListRewrite(declaration, declaration.getBodyDeclarationsProperty());
						String key= null;
						MethodDeclaration stub= null;
						for (int index= 0; index < keys.length; index++) {
							key= keys[index];
							for (int offset= 0; offset < bindings.length; offset++) {
								if (key.equals(bindings[offset].getKey())) {
									stub= StubUtility2.createImplementationStub(copy, rewrite, importRewrite, bindings[offset], binding.getName(), binding.isInterface(), settings);
									if (stub != null)
										rewriter.insertFirst(stub, null);
									break;
								}
							}
						}
						IDocument document= new Document(copy.getBuffer().getContents());
						try {
							rewrite.rewriteAST(document, fCompilationUnit.getJavaProject().getOptions(true)).apply(document, TextEdit.UPDATE_REGIONS);
							buffer.append(document.get(start, document.getLength() - start - end));
						} catch (MalformedTreeException exception) {
							JavaPlugin.log(exception);
						} catch (BadLocationException exception) {
							JavaPlugin.log(exception);
						}
					}
				}
			}
			return true;
		} finally {
			if (copy != null)
				copy.discardWorkingCopy();
		}
		*/
		return true;
	}

	private IType getDeclaringType(IJavaProject project, String typeName) {
		try {
			return JavaModelUtil.findType(project, typeName);
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
		return null;
	}

	private Image getImageForType(IType type) {
		String imageName= JavaPluginImages.IMG_OBJS_CLASS; // default
		if (type != null) {
			try {
				if (type.isAnnotation()) {
					imageName= JavaPluginImages.IMG_OBJS_ANNOTATION;
				} else if (type.isInterface()) {
					imageName= JavaPluginImages.IMG_OBJS_INTERFACE;
				}
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		return JavaPluginImages.get(imageName);
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension4#isAutoInsertable()
	 */
	public boolean isAutoInsertable() {
		return false;
	}

	protected boolean updateReplacementString(IDocument document, char trigger, int offset, ImportRewrite impRewrite) throws CoreException, BadLocationException {
		String replacementString= getReplacementString();

		// construct replacement text: an expression to be formatted
		StringBuffer buf= new StringBuffer("new A("); //$NON-NLS-1$
		buf.append(replacementString);

		if (!replacementString.endsWith(")")) { //$NON-NLS-1$
			buf.append(')');
		}

		if (!createStubs(buf, impRewrite)) {
			return false;
		}
		if (document.getChar(offset) != ')')
			buf.append(';');

		// use the code formatter
		String lineDelim= TextUtilities.getDefaultLineDelimiter(document);
		final IJavaProject project= fCompilationUnit.getJavaProject();
		IRegion region= document.getLineInformationOfOffset(getReplacementOffset());
		int indent= Strings.computeIndentUnits(document.get(region.getOffset(), region.getLength()), project);

		String replacement= CodeFormatterUtil.format(CodeFormatter.K_EXPRESSION, buf.toString(), 0, null, lineDelim, project);
		replacement= Strings.changeIndent(replacement, 0, project, CodeFormatterUtil.createIndentString(indent, project), lineDelim);
		setReplacementString(replacement.substring(replacement.indexOf('(') + 1));

		int pos= offset;
		while (pos < document.getLength() && Character.isWhitespace(document.getChar(pos))) {
			pos++;
		}

		if (pos < document.getLength() && document.getChar(pos) == ')') {
			setReplacementLength(pos - offset + 1);
		}
		return true;
	}
}
