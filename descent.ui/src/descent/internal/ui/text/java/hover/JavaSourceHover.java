package descent.internal.ui.text.java.hover;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.IWorkbenchPartOrientation;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.ILocalVariable;
import descent.core.IMember;
import descent.core.ISourceReference;
import descent.core.ITypeParameter;
import descent.core.JavaModelException;

import descent.internal.corext.codemanipulation.StubUtility;
import descent.internal.corext.util.Strings;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.text.JavaCodeReader;

/**
 * Provides source as hover info for Java elements.
 */
public class JavaSourceHover extends AbstractJavaEditorTextHover implements ITextHoverExtension, IInformationProviderExtension2 {

	/*
	 * @see JavaElementHover
	 */
	protected String getHoverInfo(IJavaElement[] result) {
		int nResults= result.length;

		if (nResults > 1)
			return null;

		IJavaElement curr= result[0];
		if ((curr instanceof IMember || curr instanceof ILocalVariable || curr instanceof ITypeParameter || curr instanceof ICompilationUnit) && curr instanceof ISourceReference) {
			try {
				String source= ((ISourceReference) curr).getSource();
				if (source == null)
					return null;

				source= removeLeadingComments(source);
				String delim= StubUtility.getLineDelimiterUsed(result[0]);

				String[] sourceLines= Strings.convertIntoLines(source);
				String firstLine= sourceLines[0];
				if (!Character.isWhitespace(firstLine.charAt(0)))
					sourceLines[0]= ""; //$NON-NLS-1$
				Strings.trimIndentation(sourceLines, curr.getJavaProject());

				if (!Character.isWhitespace(firstLine.charAt(0)))
					sourceLines[0]= firstLine;

				source= Strings.concatenate(sourceLines, delim);

				return source;

			} catch (JavaModelException ex) {
			}
		}

		return null;
	}

	private String removeLeadingComments(String source) {
		JavaCodeReader reader= new JavaCodeReader();
		IDocument document= new Document(source);
		int i;
		try {
			reader.configureForwardReader(document, 0, document.getLength(), true, false);
			int c= reader.read();
			while (c != -1 && (c == '\r' || c == '\n')) {
				c= reader.read();
			}
			i= reader.getOffset();
			reader.close();
		} catch (IOException ex) {
			i= 0;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException ex) {
				JavaPlugin.log(ex);
			}
		}

		if (i < 0)
			return source;
		return source.substring(i);
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				IEditorPart editor= getEditor(); 
				int shellStyle= SWT.TOOL | SWT.NO_TRIM;
				if (editor instanceof IWorkbenchPartOrientation)
					shellStyle |= ((IWorkbenchPartOrientation)editor).getOrientation();
				return new SourceViewerInformationControl(parent, shellStyle, SWT.NONE, getTooltipAffordanceString());
			}
		};
	}

	/*
	 * @see IInformationProviderExtension2#getInformationPresenterControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int style= SWT.V_SCROLL | SWT.H_SCROLL;
				int shellStyle= SWT.RESIZE | SWT.TOOL;
				IEditorPart editor= getEditor(); 
				if (editor instanceof IWorkbenchPartOrientation)
					shellStyle |= ((IWorkbenchPartOrientation)editor).getOrientation();
				return new SourceViewerInformationControl(parent, shellStyle, style);
			}
		};
	}
}
