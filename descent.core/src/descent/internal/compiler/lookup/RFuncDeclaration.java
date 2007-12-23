package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.IMethod;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.IAggregateDeclaration;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.InlineScanState;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.VarDeclaration;

public class RFuncDeclaration extends RDeclaration implements IFuncDeclaration {

	public RFuncDeclaration(IMethod element) {
		super(element);
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
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNested() {
		// TODO Auto-generated method stub
		return false;
	}

	public void nestedFrameRef(boolean nestedFrameRef) {
		// TODO Auto-generated method stub
		
	}

	public IFuncDeclaration overloadExactMatch(Type t, SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public IFuncDeclaration overloadResolve(Expressions arguments, SemanticContext context, ASTDmdNode caller) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDeclaration overnext() {
		// TODO Auto-generated method stub
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

	public VarDeclaration vthis() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IFuncDeclaration isFuncDeclaration() {
		return this;
	}

}
