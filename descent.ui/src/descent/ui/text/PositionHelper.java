package descent.ui.text;

import org.eclipse.jface.text.Position;

import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasDeclarationFragment;
import descent.core.dom.DebugAssignment;
import descent.core.dom.DebugDeclaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.MixinDeclaration;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.Name;
import descent.core.dom.PragmaDeclaration;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.VersionAssignment;
import descent.core.dom.VersionDeclaration;

public class PositionHelper {
	
	/**
	 * Given an element, it returns the main element of it.
	 * For example, if the element is a IModuleDeclaration, it returns
	 * it's qualified name.
	 */
	public static Position getElementOfInterest(ASTNode element) {
		Name name;
		
		switch(element.getNodeType()) {
		case ASTNode.MODULE_DECLARATION:
			ModuleDeclaration md = (ModuleDeclaration) element;
			name = md.getName();
			if (name != null) element = name;
			break;
		case ASTNode.AGGREGATE_DECLARATION:
			AggregateDeclaration aggr = (AggregateDeclaration) element;
			name = aggr.getName();
			if (name != null) element = name;
			break;
		case ASTNode.FUNCTION_DECLARATION:
			FunctionDeclaration func = (FunctionDeclaration) element;
			name = func.getName();
			if (name != null) element = name;
			break;
		case ASTNode.ENUM_DECLARATION:
			EnumDeclaration e = (EnumDeclaration) element;
			name = e.getName();
			if (name != null) element = name;
			break;
		case ASTNode.ENUM_MEMBER:
			EnumMember em = (EnumMember) element;
			name = em.getName();
			if (name != null) element = name;
			break;
		case ASTNode.VARIABLE_DECLARATION:
			/* TODO fixme
			IVariableDeclaration var = (IVariableDeclaration) element;
			name = var.getName();
			if (name != null) element = name;
			*/
			break;
		case ASTNode.TYPEDEF_DECLARATION:
			/* TODO fixme
			ITypedefDeclaration td = (ITypedefDeclaration) element;
			name = td.getName();
			if (name != null) element = name;
			*/
			break;
		case ASTNode.UNIT_TEST_DECLARATION:
			/* TODO fixme
			IUnitTestDeclaration u = (IUnitTestDeclaration) element;
			name = u.getName();
			if (name != null) element = name;
			*/
			break;
		case ASTNode.INVARIANT_DECLARATION:
			/* TODO fixme
			IInvariantDeclaration inv = (IInvariantDeclaration) element;
			name = inv.getName();
			if (name != null) element = name;
			*/
			break;
		case ASTNode.TEMPLATE_DECLARATION:
			TemplateDeclaration t = (TemplateDeclaration) element;
			name = t.getName();
			if (name != null) element = name;
			break;
		case ASTNode.ALIAS_DECLARATION_FRAGMENT:
			AliasDeclarationFragment fragment = (AliasDeclarationFragment) element;
			name = fragment.getName();
			if (name != null) element = name;
			break;
		case ASTNode.VERSION_DECLARATION:
			if (((VersionDeclaration) element).getVersion() != null) element = ((VersionDeclaration) element).getVersion();
			break;
		case ASTNode.DEBUG_DECLARATION:
			if (((DebugDeclaration) element).getVersion() != null) {
				element = ((DebugDeclaration) element).getVersion();
			}
			break;
		case ASTNode.DEBUG_ASSIGNMENT:
			DebugAssignment da = (DebugAssignment) element;
			element = da.getVersion();
			break;
		case ASTNode.VERSION_ASSIGNMENT:
			VersionAssignment va = (VersionAssignment) element;
			element = va.getVersion();
			break;			
		case ASTNode.PRAGMA_DECLARATION:
			PragmaDeclaration pd = (PragmaDeclaration) element;
			name = pd.getName();
			if (name != null) element = name;
			break;
		case ASTNode.MIXIN_DECLARATION:
			MixinDeclaration mix = (MixinDeclaration) element;
			name = mix.getName();
			if (name != null) element = name;
			break;
		}
		return new Position(element.getStartPosition(), element.getLength());
	}

}
