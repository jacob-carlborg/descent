package descent.internal.core.ctfe;

import java.util.ArrayList;
import java.util.List;

import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDescentStackFrame;
import descent.core.ctfe.IDescentVariable;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.TupleDeclaration;
import descent.internal.compiler.parser.VarDeclaration;

public class VariablesFinder {
	
	private final IDebugElementFactory fElementFactory;

	public VariablesFinder(IDebugElementFactory elementFactory) {
		this.fElementFactory = elementFactory;
	}
	
	public IDescentVariable[] getVariables(IDescentStackFrame sf) {
		if (sf == null)
			return new IDescentVariable[0];
		
		List<IDescentVariable> vars = new ArrayList<IDescentVariable>();
		InterState is = sf.getInterState();
		Scope scope = sf.getScope();
		HashtableOfCharArrayAndObject ids = new HashtableOfCharArrayAndObject();
		
		fillVariables(sf.getNumber(), scope, vars, ids);
		if (is != null) {
			fillVariables(sf.getNumber(), is, vars, ids);
		}
		return vars.toArray(new DescentVariable[vars.size()]);
	}
	
	private void fillVariables(int stackFrame, Scope currentScope, List<IDescentVariable> vars, HashtableOfCharArrayAndObject ids) {
		if (currentScope.scopesym != null && currentScope.scopesym.symtab != null) {
			fillVariables(stackFrame, currentScope.scopesym.symtab, vars, ids);
		}
		
		if (currentScope.enclosing != null) {
			fillVariables(stackFrame, currentScope.enclosing, vars, ids);
		}
	}
	
	private void fillVariables(int stackFrame, InterState is, List<IDescentVariable> vars, HashtableOfCharArrayAndObject ids) {
		if (is.vars != null) {
			for(Dsymbol dsymbol : is.vars) {
				IDescentVariable var = toVariable(stackFrame, dsymbol);
				if (var == null)
					continue;
				
				if (!ids.containsKey(dsymbol.ident.ident)) {
					vars.add(var);
					ids.put(dsymbol.ident.ident, this);
				}
			}
		}
		
		if (is.fd != null && is.fd.localsymtab != null) {
			fillVariables(stackFrame, is.fd.localsymtab, vars, ids);
		}
	}
	
	private void fillVariables(int stackFrame, DsymbolTable symtab, List<IDescentVariable> vars, HashtableOfCharArrayAndObject ids) {
		for(char[] key : symtab.keys()) {
			if (key == null)
				continue;
			
			Dsymbol dsymbol = symtab.lookup(key);
			IDescentVariable var = toVariable(stackFrame, dsymbol);
			if (var == null)
				continue;
			
			if (!ids.containsKey(dsymbol.ident.ident)) {
				vars.add(var);
				ids.put(dsymbol.ident.ident, this);
			}
		}
	}
	
	private IDescentVariable toVariable(int stackFrame, Dsymbol dsymbol) {
		if (dsymbol instanceof VarDeclaration) {
			VarDeclaration var = (VarDeclaration) dsymbol;
			if (var.value != null) {
				return fElementFactory.newVariable(0, var.ident.toString(), var.value);
			} else if (var.isTemplateArgument() && var.init != null) {
				if (var.init instanceof ExpInitializer) {
					return fElementFactory.newVariable(0, var.ident.toString(), ((ExpInitializer) var.init).exp);
				}
			}
		} else if (dsymbol instanceof AliasDeclaration) {
			AliasDeclaration alias = (AliasDeclaration) dsymbol;
			
			if (alias.aliassym != null) {
				return fElementFactory.newVariable(stackFrame, alias.ident.toString(), alias.aliassym.ident.toString());
			} else if (alias.type != null){
				return fElementFactory.newVariable(stackFrame, alias.ident.toString(), alias.type);
			}
		} else if (dsymbol instanceof TupleDeclaration) {
			TupleDeclaration tuple = (TupleDeclaration) dsymbol;
			
			return fElementFactory.newVariable(stackFrame, tuple.ident.toString(), tuple);
		}
		return null;
	}

}
