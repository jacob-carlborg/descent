package descent.internal.debug.ui.model;

import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpression;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import descent.core.IJavaElement;
import descent.core.IMember;
import descent.internal.debug.ui.BreakpointUtils;
import descent.ui.JavaElementLabels;

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
		try {
			if (element instanceof IWatchExpression) {
				return getWatchExpressionText((IWatchExpression) element);
			} else if (element instanceof IVariable) {
				return getVariableText((IVariable) element);
			} else if (element instanceof IStackFrame) {
				return getStackFrameText((IStackFrame) element);
			} else if (element instanceof IThread) {
				return getThreadText((IThread) element);
			} else if (element instanceof IBreakpoint) {
				return getBreakpointText((IBreakpoint) element);
			}
		} catch (CoreException e) {
			return super.getText(element);
		}
		return super.getText(element);
	}

	private String getBreakpointText(IBreakpoint breakpoint) throws CoreException {
		if (breakpoint instanceof ILineBreakpoint) {
			return getLineBreakpointText((ILineBreakpoint) breakpoint);
		}
		return "";
	}

	private String getLineBreakpointText(ILineBreakpoint breakpoint) throws CoreException {
		StringBuilder sb = new StringBuilder();
		appendFileName(breakpoint, sb);
		appendLineNumber(breakpoint, sb);
		
		IMember member = BreakpointUtils.getMember(breakpoint);
		if (member != null) {
			sb.append(" - ");
			appendElementPath(breakpoint, member, sb);
		}
		return sb.toString();
	}
	
	private String getThreadText(IThread thread) throws CoreException {
		StringBuilder sb = new StringBuilder();
		sb.append("Thread [");
		sb.append(thread.getName());
		sb.append("]");
		if (thread.isSuspended()) {
			IBreakpoint[] breakpoints = thread.getBreakpoints();
			if (breakpoints.length > 0) {
				sb.append(" (Suspended at breakpoint ");
				sb.append(getBreakpointText(breakpoints[0]));
				sb.append(")");
			} else {
				sb.append(" (Suspended)");
			}
			
		} else if (thread.isTerminated()) {
			sb.append(" (Terminated)");
		} else {
			sb.append(" (Running)");
		}
		return sb.toString();
	}

	private String getWatchExpressionText(IWatchExpression exp) throws CoreException {
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		sb.append(exp.getExpressionText());
		sb.append("\"");
		if (exp.getValue() != null && exp.getValue().getValueString() != null && exp.getValue().getValueString().length() > 0) {
			sb.append(" = ");
			sb.append(exp.getValue().getValueString());
		}
		return sb.toString();
	}
	
	private String getVariableText(IVariable variable) throws DebugException {
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		sb.append(variable.getName());
		sb.append("\"");
		if (variable.getValue() != null && variable.getValue().getValueString() != null && variable.getValue().getValueString().length() > 0) {
			sb.append(" = ");
			sb.append(variable.getValue().getValueString());
		}
		return sb.toString();
	}
	
	private String getStackFrameText(IStackFrame frame) {
		return frame.toString();		
	}
	
	protected StringBuilder appendFileName(ILineBreakpoint breakpoint, StringBuilder label) throws CoreException {
		label.append(breakpoint.getMarker().getResource().getName());
		return label;
	}
	
	protected StringBuilder appendLineNumber(ILineBreakpoint breakpoint, StringBuilder label) throws CoreException {
		int lineNumber= breakpoint.getLineNumber();
		if (lineNumber > 0) {
			label.append(" ["); //$NON-NLS-1$
			label.append("line:"); 
			label.append(' ');
			label.append(lineNumber);
			label.append(']');

		}
		return label;
	}
	
	private void appendElementPath(ILineBreakpoint breakpoint, IJavaElement element, StringBuilder sb) {
		Stack<IJavaElement> elements = new Stack<IJavaElement>();
		while(element != null && element.getElementType() != IJavaElement.COMPILATION_UNIT) {
			elements.add(element);
			element = element.getParent();
		}
		
		while(!elements.isEmpty()) {
			IJavaElement popped = elements.pop();
			sb.append(JavaElementLabels.getElementLabel(popped, JavaElementLabels.M_PARAMETER_TYPES));
			if (!elements.isEmpty()) {
				sb.append(".");
			}
		}
	}


}
