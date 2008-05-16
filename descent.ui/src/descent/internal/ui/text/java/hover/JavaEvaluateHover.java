package descent.internal.ui.text.java.hover;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.IWorkbenchPartOrientation;

import descent.core.ICodeAssist;
import descent.core.IEvaluationResult;
import descent.core.JavaModelException;
import descent.core.ToolFactory;
import descent.core.dom.CompilationUnit;
import descent.core.formatter.CodeFormatter;

public class JavaEvaluateHover extends AbstractJavaEditorTextHover implements
		ITextHoverExtension, IInformationProviderExtension2 {

	private IEvaluationResult result;

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		ICodeAssist resolve = getCodeAssist();
		if (resolve != null) {
			try {
				result = resolve.codeEvaluate(hoverRegion.getOffset());
				return getHoverInfo(result);

			} catch (JavaModelException x) {
				return null;
			}
		}

		return null;
	}

	private String getHoverInfo(IEvaluationResult result) {
		if (result == null) {
			//return "<html><body style='background-color:white;font-size:14px'><i>Cannot evaluate at compile-time</i></body></html>"; //$NON-NLS-1$
			return null;
		}
		
		switch(result.getKind()) {
		case IEvaluationResult.BOOL:
		case IEvaluationResult.BYTE:
		case IEvaluationResult.UBYTE:
		case IEvaluationResult.SHORT:
		case IEvaluationResult.USHORT:
		case IEvaluationResult.INT:
		case IEvaluationResult.UINT:
		case IEvaluationResult.LONG:
		case IEvaluationResult.ULONG:
		case IEvaluationResult.FLOAT:
		case IEvaluationResult.DOUBLE:
		case IEvaluationResult.REAL:
		case IEvaluationResult.IFLOAT:
		case IEvaluationResult.IDOUBLE:
		case IEvaluationResult.IREAL:
		case IEvaluationResult.CFLOAT:
		case IEvaluationResult.CDOUBLE:
		case IEvaluationResult.CREAL:
		case IEvaluationResult.CHAR:
		case IEvaluationResult.WCHAR:
		case IEvaluationResult.DCHAR:
			return result.toString();
		case IEvaluationResult.CHAR_ARRAY:
		case IEvaluationResult.WCHAR_ARRAY:
		case IEvaluationResult.DCHAR_ARRAY: {
			String text = (String) result.getValue();
			// Try to format the code, it could be a list of declarations or statements
			text = format(text);
			return text;
		}
		case IEvaluationResult.COMPILATION_UNIT: {
			CompilationUnit unit = (CompilationUnit) result.getValue();
			String text = unit.toString();
			text = format(text);
			return text;
		}
		default:
			return null;
		}		
	}

	private String format(String text) {
		CodeFormatter formatter = ToolFactory.createCodeFormatter(null);
		try {
			// The most common example is something inside a function 
			TextEdit edit = formatter.format(CodeFormatter.K_STATEMENTS,
					text, 0, text.length(), 0, "\n"); //$NON-NLS-1$
			if (edit == null) {
				// If not, try parsing a whole compilation unit
				edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT,
						text, 0, text.length(), 0, "\n"); //$NON-NLS-1$
			}
			if (edit != null) {
				Document doc = new Document(text);
				edit.apply(doc);
				text = doc.get();
			}
		} catch (Exception e) {
		}
		return text;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 0);
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getHoverControlCreator() {
		if (result == null) {
			return new AbstractReusableInformationControlCreator() {
				public IInformationControl doCreateInformationControl(Shell parent) {
					return new BrowserInformationControl(parent, SWT.NO_TRIM | SWT.TOOL, SWT.NONE, null);
				}
			};
		} else {
			return new IInformationControlCreator() {
				public IInformationControl createInformationControl(Shell parent) {
					IEditorPart editor = getEditor();
					int shellStyle = SWT.TOOL | SWT.NO_TRIM;
					if (editor instanceof IWorkbenchPartOrientation)
						shellStyle |= ((IWorkbenchPartOrientation) editor)
								.getOrientation();
					return new SourceViewerInformationControl(parent,
							shellStyle, SWT.NONE, getTooltipAffordanceString());
				}
			};
		}
	}

	/*
	 * @see IInformationProviderExtension2#getInformationPresenterControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (result == null) {
			return new AbstractReusableInformationControlCreator() {
				public IInformationControl doCreateInformationControl(Shell parent) {
					return new BrowserInformationControl(parent, SWT.NO_TRIM | SWT.TOOL, SWT.NONE, null);
				}
			};
		} else {
			return new IInformationControlCreator() {
				public IInformationControl createInformationControl(Shell parent) {
					int style = SWT.V_SCROLL | SWT.H_SCROLL;
					int shellStyle = SWT.RESIZE | SWT.TOOL;
					IEditorPart editor = getEditor();
					if (editor instanceof IWorkbenchPartOrientation)
						shellStyle |= ((IWorkbenchPartOrientation) editor)
								.getOrientation();
					return new SourceViewerInformationControl(parent,
							shellStyle, style);
				}
			};
		}
	}

}
