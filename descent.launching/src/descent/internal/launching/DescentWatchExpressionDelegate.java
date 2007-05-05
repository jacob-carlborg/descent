package descent.internal.launching;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;

import descent.internal.launching.model.DescentStackFrame;
import descent.launching.model.ICli;
import descent.launching.model.IDescentVariable;

public class DescentWatchExpressionDelegate implements IWatchExpressionDelegate {

	public void evaluateExpression(final String expression, final IDebugElement context, IWatchExpressionListener listener) {
		if (context instanceof DescentStackFrame) {
			final DescentStackFrame stackFrame = (DescentStackFrame) context;
			final ICli cli = stackFrame.getCli();
			try {
				final IDescentVariable variable = cli.evaluateExpression(stackFrame.getNumber(), expression);
				if (variable == null) {
					listener.watchEvaluationFinished(new IWatchExpressionResult() {
						public String[] getErrorMessages() {
							return new String[] { "\"" + expression + "\" cannot be evaluated" };
						}
						public DebugException getException() {
							return null;
						}
						public String getExpressionText() {
							return null;
						}
						public IValue getValue() {
							return null;
						}
						public boolean hasErrors() {
							return true;
						}
					});
				} else {
					listener.watchEvaluationFinished(new IWatchExpressionResult() {
						public String[] getErrorMessages() {
							return null;
						}
						public DebugException getException() {
							return null;
						}
						public String getExpressionText() {
							return expression;
						}
						public IValue getValue() {
							try {
								return variable.getValue();
							} catch (DebugException e) {
								e.printStackTrace();
								return null;
							}
						}
						public boolean hasErrors() {
							return false;
						}
					});
				}
			} catch (final IOException e) {
				e.printStackTrace();
				listener.watchEvaluationFinished(new IWatchExpressionResult() {
					public String[] getErrorMessages() {
						return new String[] { e.getMessage() };
					}
					public DebugException getException() {
						return null;
					}
					public String getExpressionText() {
						return null;
					}
					public IValue getValue() {
						return null;
					}
					public boolean hasErrors() {
						return true;
					}
				});
			}
		}
	}

}
