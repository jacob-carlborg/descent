package descent.internal.ui.text.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.ChildListPropertyDescriptor;
import descent.core.dom.CompilationUnit;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IMethodBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.NewAnonymousClassExpression;
import descent.core.dom.rewrite.ASTRewrite;
import descent.core.dom.rewrite.ITrackedNodePosition;
import descent.core.dom.rewrite.ImportRewrite;
import descent.core.dom.rewrite.ListRewrite;
import descent.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import descent.core.formatter.IndentManipulation;
import descent.internal.corext.codemanipulation.CodeGenerationSettings;
import descent.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import descent.internal.corext.codemanipulation.StubUtility;
import descent.internal.corext.codemanipulation.StubUtility2;
import descent.internal.corext.dom.Bindings;
import descent.internal.corext.dom.NodeFinder;
import descent.internal.corext.util.Strings;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.preferences.JavaPreferencesSettings;

public class OverrideCompletionProposal extends JavaTypeCompletionProposal implements ICompletionProposalExtension4 {

	private IJavaProject fJavaProject;
	private String fMethodName;
	private String[] fParamTypes;

	public OverrideCompletionProposal(IJavaProject jproject, ICompilationUnit cu, String methodName, String[] paramTypes, int start, int length, String displayName, String completionProposal) {
		super(completionProposal, cu, start, length, null, displayName, 0);
		Assert.isNotNull(jproject);
		Assert.isNotNull(methodName);
		Assert.isNotNull(paramTypes);
		Assert.isNotNull(cu);

		fParamTypes= paramTypes;
		fMethodName= methodName;

		fJavaProject= jproject;
		
		StringBuffer buffer= new StringBuffer();
		buffer.append(completionProposal);
		buffer.append(" {};"); //$NON-NLS-1$
		
		setReplacementString(buffer.toString());
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getPrefixCompletionText(org.eclipse.jface.text.IDocument,int)
	 */
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		return fMethodName;
	}

	/*
	 * @see JavaTypeCompletionProposal#updateReplacementString(IDocument,char,int,ImportRewrite)
	 */
	protected boolean updateReplacementString(IDocument document, char trigger, int offset, ImportRewrite importRewrite) throws CoreException, BadLocationException {
		final IDocument buffer= new Document(document.get());
		int index= offset - 1;
		while (index >= 0 && Character.isJavaIdentifierPart(buffer.getChar(index)))
			index--;
		final int length= offset - index - 1;
		buffer.replace(index + 1, length, " "); //$NON-NLS-1$
		final ASTParser parser= ASTParser.newParser(fJavaProject.getApiLevel());
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setSource(fCompilationUnit);
		// TODO why it uses the document source instead of the compilation unit?
		// Maybe because there may be a difference between the working copy and the document?
//		parser.setSource(buffer.get().toCharArray());
//		parser.setUnitName(fCompilationUnit.getResource().getFullPath().toString());
//		parser.setProject(fCompilationUnit.getJavaProject());
		
		final CompilationUnit unit= (CompilationUnit) parser.createAST(new NullProgressMonitor());
		ITypeBinding binding= null;
		ChildListPropertyDescriptor descriptor= null;
		ASTNode node= NodeFinder.perform(unit, index + 1, 0);
		
		while(node != null && !(node instanceof AggregateDeclaration)) {
			node = node.getParent();
		}
		
		if (node instanceof NewAnonymousClassExpression) {
			
		} else if (node instanceof AggregateDeclaration) {
			final AggregateDeclaration declaration= ((AggregateDeclaration) node);
			descriptor= declaration.DECLARATIONS_PROPERTY;
			binding= declaration.resolveBinding();
		}
		
		ImportRewriteContext context;
		if (importRewrite != null) {
			context= new ContextSensitiveImportRewriteContext(unit, offset, importRewrite);
		} else {
			importRewrite= StubUtility.createImportRewrite(unit, true); // create a dummy import rewriter to have one 
			context= new ImportRewriteContext() { // forces that all imports are fully qualified
				public int findInContext(String qualifier, String name, int kind) {
					return RES_NAME_CONFLICT;
				}
			};
		}
		
		if (binding != null) {
			ASTRewrite rewrite= ASTRewrite.create(unit.getAST());
			IMethodBinding[] bindings= StubUtility2.getOverridableMethods(rewrite.getAST(), binding, true);
			if (bindings != null && bindings.length > 0) {
				List candidates= new ArrayList(bindings.length);
				IMethodBinding method= null;
				for (index= 0; index < bindings.length; index++) {
					if (bindings[index].getName().equals(fMethodName) && bindings[index].getParameterTypes().length == fParamTypes.length)
						candidates.add(bindings[index]);
				}
				if (candidates.size() > 1) {
					method= Bindings.findMethodInHierarchy(binding, fMethodName, fParamTypes);
					if (method == null) {
						ITypeBinding objectType= rewrite.getAST().resolveWellKnownType("java.lang.Object"); //$NON-NLS-1$
						method= Bindings.findMethodInType(objectType, fMethodName, fParamTypes);
					}
				} else if (candidates.size() == 1)
					method= (IMethodBinding) candidates.get(0);
				if (method != null) {
					CodeGenerationSettings settings= JavaPreferencesSettings.getCodeGenerationSettings(fJavaProject);
					ListRewrite rewriter= rewrite.getListRewrite(node, descriptor);
					String key= method.getKey();
					FunctionDeclaration stub= null;
					for (index= 0; index < bindings.length; index++) {
						if (key.equals(bindings[index].getKey())) {
							stub= StubUtility2.createImplementationStub(fCompilationUnit, rewrite, importRewrite, context, bindings[index], binding.getName(), settings, false);
							if (stub != null)
								rewriter.insertFirst(stub, null);
							break;
						}
					}
					if (stub != null) {
						IDocument contents= new Document(fCompilationUnit.getBuffer().getContents());
						IRegion region= contents.getLineInformationOfOffset(getReplacementOffset());
						ITrackedNodePosition position= rewrite.track(stub);
						String indent= IndentManipulation.extractIndentString(contents.get(region.getOffset(), region.getLength()), settings.tabWidth, settings.indentWidth);
						try {
							rewrite.rewriteAST(contents, fJavaProject.getOptions(true)).apply(contents, TextEdit.UPDATE_REGIONS);
						} catch (MalformedTreeException exception) {
							JavaPlugin.log(exception);
						} catch (BadLocationException exception) {
							JavaPlugin.log(exception);
						}
						setReplacementString(IndentManipulation.changeIndent(Strings.trimIndentation(contents.get(position.getStartPosition(), position.getLength()), settings.tabWidth, settings.indentWidth, false), 0, settings.tabWidth, settings.indentWidth, indent, TextUtilities.getDefaultLineDelimiter(contents)));
					}
				}
			}
		}
		return true;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension4#isAutoInsertable()
	 */
	public boolean isAutoInsertable() {
		return false;
	}
}
