package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.IMethod;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IAggregateDeclaration;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.InlineScanState;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.util.Util;

import static descent.internal.compiler.parser.STC.STCstatic;

public class RFuncDeclaration extends RDeclaration implements IFuncDeclaration {
	
	private TypeFunction type;
	private FuncDeclaration func; // The underlying function, only for interpreting

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
		if (func == null) {
			ISourceReference r = (ISourceReference) element;
			try {
				// We build the function from the source in order to interpret it
				// But we have to also include in the source:
				// - the original imports
				// - an import to this function's module, in order to not resolve
				//   again what is already resolved.
				
				int importCount = 1;
				
				// Build import statement to my module
				RModule rmodule = (RModule) getModule();
				ICompilationUnit unit = (ICompilationUnit) rmodule.element;				
				
				StringBuilder fullSource = new StringBuilder();
				fullSource.append("import ");
				fullSource.append(unit.getFullyQualifiedName());
				fullSource.append(";");
				
				// Now append this module's imports
				for(IDsymbol s : rmodule.members()) {
					Import imp = s.isImport();
					if (imp != null) {
						fullSource.append("import ");
						fullSource.append(imp.toString());
						fullSource.append(";");
						importCount++;
					}
				}
				
				fullSource.append(r.getSource());
				
				Parser parser = new Parser(Util.getApiLevel(element), fullSource.toString());
				parser.nextToken();
				Module m = parser.parseModuleObj();
				m.ident(getModule().ident());
				
				func = (FuncDeclaration) m.members.get(importCount);
				
				m.semantic(context);
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		return func.interpret(istate, arguments, context);
	}
	
	public boolean isVirtual(SemanticContext context) {
		return SemanticMixin.isVirtual(this, context);
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
	
	@Override
	public IAggregateDeclaration isThis() {
		return SemanticMixin.isThis(this);
	}

	public void nestedFrameRef(boolean nestedFrameRef) {
		// TODO Auto-generated method stub
		
	}

	public IFuncDeclaration overloadExactMatch(Type t, SemanticContext context) {
		return SemanticMixin.overloadExactMatch(this, t, context);
	}

	public IFuncDeclaration overloadResolve(Expressions arguments, SemanticContext context, ASTDmdNode caller) {
		return SemanticMixin.overloadResolve(this, arguments, context, caller);
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
		return SemanticMixin.overrides(this, fd, context);
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
				
				Type retType;
				
				if (isCtorDeclaration() != null) {
					// For a constructor, the return type is the
					// type of its class
					retType = parent().type();
				} else {
					retType = getTypeFromSignature(retTypeSig);
				}
				
				Arguments args = new Arguments();
				
				String[] paramNames = method.getParameterNames();
				String[] paramTypesSig = method.getParameterTypes();
				
				for(int i = 0; i < paramNames.length; i++) {
					// TODO storage class and default value
					args.add(new Argument(
							0, // storage class
							getTypeFromSignature(paramTypesSig[i]), // type 
							new IdentifierExp(paramNames[i].toCharArray()), // name 
							null // default value
							));
				}
				
				// TODO link
				type = new TypeFunction(args, retType, (getFlags() & Flags.AccVarargs) == 0 ? 0 : 1, LINK.LINKd);
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
	
	@Override
	public String getSignature() {
		// TODO Auto-generated method stub
		return super.getSignature();
	}

}
