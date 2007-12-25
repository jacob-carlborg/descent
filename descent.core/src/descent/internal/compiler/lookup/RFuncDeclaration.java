package descent.internal.compiler.lookup;

import java.util.List;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.STC.STCstatic;

import descent.core.IMethod;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.HdrGenState;
import descent.internal.compiler.parser.IAggregateDeclaration;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InlineScanState;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.OutBuffer;
import descent.internal.compiler.parser.Problem;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.ASTDmdNode.Match;
import descent.internal.core.util.Util;

public class RFuncDeclaration extends RDeclaration implements IFuncDeclaration {
	
	private TypeFunction type;

	public RFuncDeclaration(IMethod element, SemanticContext context) {
		super(element, context);
	}

	public boolean canInline(boolean hasthis, boolean hdrscan, SemanticContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canInline(boolean hasthis, SemanticContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public Expression doInline(InlineScanState iss, Expression ethis, List arguments, SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean inferRetType() {
		// TODO Auto-generated method stub
		return false;
	}

	public Expression interpret(InterState istate, Expressions arguments, SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public IAggregateDeclaration isMember2() {
		IAggregateDeclaration ad;

		ad = null;
		for (IDsymbol s = this; s != null; s = s.parent()) {
			ad = s.isMember();
			if (ad != null) {
				break;
			}
			if (s.parent() == null || s.parent().isTemplateInstance() == null) {
				break;
			}
		}
		return ad;
	}

	public boolean isNested() {
		return ((storage_class() & STCstatic) == 0)
			&& (toParent2().isFuncDeclaration() != null);
	}

	public void nestedFrameRef(boolean nestedFrameRef) {
		// TODO Auto-generated method stub
		
	}

	public IFuncDeclaration overloadExactMatch(Type t, SemanticContext context) {
		// TODO Auto-generated method stub
		return this;
	}

	public IFuncDeclaration overloadResolve(Expressions arguments, SemanticContext context, ASTDmdNode caller) {
		// TODO don't duplicate code
		TypeFunction tf;
		Match m = new Match();
		m.last = MATCHnomatch;
		ASTDmdNode.overloadResolveX(m, this, arguments, context);

		if (m.count == 1) // exactly one match
		{
			return m.lastf;
		} else {
			OutBuffer buf = new OutBuffer();

			if (arguments != null) {
				HdrGenState hgs = new HdrGenState();

				ASTDmdNode.argExpTypesToCBuffer(buf, arguments, hgs, context);
			}

			if (m.last == MATCHnomatch) {
				tf = (TypeFunction) type;

				context.acceptProblem(Problem.newSemanticTypeError(IProblem.ParametersDoesNotMatchParameterTypes, caller, new String[] { kindForError(context) + Argument.argsTypesToChars(tf.parameters, tf.varargs, context), buf.toChars() }));
				return m.anyf; // as long as it's not a FuncAliasDeclaration
			} else {
				TypeFunction t1 = (TypeFunction) m.lastf.type();
				TypeFunction t2 = (TypeFunction) m.nextf.type();

				context.acceptProblem(Problem.newSemanticTypeError(IProblem.CalledWithArgumentTypesMatchesBoth, caller, new String[] { buf.toChars(), m.lastf.toPrettyChars(context), Argument
						.argsTypesToChars(t1.parameters, t1.varargs,
								context), m.nextf
						.toPrettyChars(context), Argument
						.argsTypesToChars(t2.parameters, t2.varargs,
								context) }));
				return m.lastf;
			}
		}
	}

	private String kindForError(SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDeclaration overnext() {
		boolean foundMe = false;
		
		IScopeDsymbol parentS = (IScopeDsymbol) parent;
		for(IDsymbol s : parentS.members()) {
			if (s == this) {
				foundMe = true;
			} else if (foundMe && CharOperation.equals(s.ident().ident, ident().ident)) {
				IDeclaration d = s.isDeclaration();
				if (d != null) {
					return d;
				}
			}
		}
		return null;
	}

	public boolean overrides(IFuncDeclaration fd, SemanticContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public Type tintro() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Type getType() {
		return type();
	}
	
	@Override
	public Type type() {
		// TODO Auto-generated method stub
		if (type == null) {
			try {
				IMethod method = (IMethod) element;
				String retTypeSig = method.getReturnType();
				Type retType = getType(retTypeSig);
				
				Arguments args = new Arguments();
				
				String[] paramNames = method.getParameterNames();
				String[] paramTypesSig = method.getParameterTypes();
				
				for(int i = 0; i < paramNames.length; i++) {
					// TODO storage class and default value
					args.add(new Argument(
							0, // storage class
							getType(paramTypesSig[i]), // type 
							new IdentifierExp(paramNames[i].toCharArray()), // name 
							null // default value
							));
				}
				
				// TODO link and varargs
				type = new TypeFunction(args, retType, 0, LINK.LINKd);
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		return type;
	}

	public VarDeclaration vthis() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IFuncDeclaration isFuncDeclaration() {
		return this;
	}

}
