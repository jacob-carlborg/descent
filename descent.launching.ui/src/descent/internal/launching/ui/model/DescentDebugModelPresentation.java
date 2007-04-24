package descent.internal.launching.ui.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpression;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class DescentDebugModelPresentation extends LabelProvider implements IDebugModelPresentation {

	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
		}
		listener.detailComputed(value, detail);
	}

	public void setAttribute(String attribute, Object value) {
	}

	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint) {
			return "descent.ui.CompilationUnitEditor";
		}
		return null;
	}

	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile)element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile)((ILineBreakpoint)element).getMarker().getResource());
		}
		return null;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof IWatchExpression) {
			IWatchExpression exp = (IWatchExpression) element;
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("\"");
				sb.append(exp.getExpressionText());
				sb.append("\"");
				if (exp.getValue() != null && exp.getValue().getValueString() != null) {
					sb.append(" = ");
					sb.append(exp.getValue().getValueString());
				}
				return sb.toString();
			} catch (DebugException e) {
				e.printStackTrace();
			}
		} else if (element instanceof IVariable) {
			IVariable variable = (IVariable) element;
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("\"");
				sb.append(variable.getName());
				sb.append("\"");
				if (variable.getValue() != null && variable.getValue().getValueString() != null) {
					sb.append(" = ");
					sb.append(variable.getValue().getValueString());
				}
				return sb.toString();
			} catch (DebugException e) {
				e.printStackTrace();
			}
		}
		return super.getText(element);
	}

}
