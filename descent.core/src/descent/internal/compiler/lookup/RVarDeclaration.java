package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IExpInitializer;
import descent.internal.compiler.parser.IInitializer;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class RVarDeclaration extends RDeclaration implements IVarDeclaration {

	public RVarDeclaration(IField element) {
		super(element);
	}

	public int canassign() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Expression callAutoDtor() {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkNestedReference(Scope sc, Loc loc, SemanticContext context) {
		// TODO Auto-generated method stub
		
	}

	public boolean ctorinit() {
		// TODO Auto-generated method stub
		return false;
	}

	public void ctorinit(boolean c) {
		// TODO Auto-generated method stub
		
	}

	public IExpInitializer getExpInitializer(SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public IInitializer init() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void init(IInitializer init) {
		// TODO Auto-generated method stub
		
	}

	public int inuse() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean noauto() {
		// TODO Auto-generated method stub
		return false;
	}

	public int offset() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void offset(int offset) {
		// TODO Auto-generated method stub
		
	}

	public Expression value() {
		// TODO Auto-generated method stub
		return null;
	}

	public void value(Expression value) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public IVarDeclaration isVarDeclaration() {
		return this;
	}

}
