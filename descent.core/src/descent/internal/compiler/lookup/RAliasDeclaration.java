package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.internal.compiler.parser.IAliasDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;

public class RAliasDeclaration extends RDeclaration implements IAliasDeclaration {
	
	private boolean typeResolved;
	private Type type;

	public RAliasDeclaration(IField element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public Type getType() {
		return type();
	}
	
	@Override
	public Type type() {
		if (!typeResolved) {
			typeResolved = true;
			
			type = getTypeFromField(false);
			IDsymbol sym = type.toDsymbol(getScope(), context);
			if (sym == null) {
				type = type.semantic(Loc.ZERO, getScope(), context);
				merge(type);
			}
		}
		return type;
	}
	
	@Override
	public IDsymbol toAlias(SemanticContext context) {
		Type type = getTypeFromField(false);
		IDsymbol alias = type.toDsymbol(getScope(), context);
		if (alias != null) {
			return alias;
		}
		return this;
	}
	
	@Override
	public IAliasDeclaration isAliasDeclaration() {
		return this;
	}
	
	public char getSignaturePrefix() {
		return ISignatureConstants.ALIAS;
	}
	
	@Override
	public String kind() {
		return "alias";
	}

}
