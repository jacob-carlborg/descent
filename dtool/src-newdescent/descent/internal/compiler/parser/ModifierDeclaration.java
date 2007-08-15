package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class ModifierDeclaration extends Dsymbol  {
	
	public final Modifier modifier;
	public final List<Dsymbol> declarations;

	public ModifierDeclaration(Loc loc, Modifier modifier, List<Dsymbol> declarations) {
		super(loc);
		this.modifier = modifier;
		this.declarations = declarations;		
	}
	
	@Override
	public int getNodeType() {
		return MODIFIER_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifier);
			TreeVisitor.acceptChildren(visitor, declarations);
		}
		visitor.endVisit(this);
	}

}
