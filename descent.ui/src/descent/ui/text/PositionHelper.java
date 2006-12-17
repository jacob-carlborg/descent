package descent.ui.text;

import org.eclipse.jface.text.Position;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IElement;
import descent.core.dom.IEnumDeclaration;
import descent.core.dom.IEnumMember;
import descent.core.dom.IFunctionDeclaration;
import descent.core.dom.IMixinDeclaration;
import descent.core.dom.IModuleDeclaration;
import descent.core.dom.IName;
import descent.core.dom.IPragmaDeclaration;
import descent.core.dom.ITemplateDeclaration;
import descent.core.dom.ITypedefDeclaration;
import descent.core.dom.IVariableDeclaration;
import descent.core.dom.IVersionDeclaration;
import descent.internal.core.dom.AliasDeclarationFragment;
import descent.internal.core.dom.DebugAssignment;
import descent.internal.core.dom.VersionAssignment;

public class PositionHelper {
	
	/**
	 * Given an element, it returns the main element of it.
	 * For example, if the element is a IModuleDeclaration, it returns
	 * it's qualified name.
	 */
	public static Position getElementOfInterest(IElement element) {
		IName name;
		
		switch(element.getNodeType0()) {
		case IElement.MODULE_DECLARATION:
			IModuleDeclaration md = (IModuleDeclaration) element;
			name = md.getName();
			if (name != null) element = name;
			break;
		case IElement.AGGREGATE_DECLARATION:
			IAggregateDeclaration aggr = (IAggregateDeclaration) element;
			name = aggr.getName();
			if (name != null) element = name;
			break;
		case IElement.FUNCTION_DECLARATION:
			IFunctionDeclaration func = (IFunctionDeclaration) element;
			name = func.getName();
			if (name != null) element = name;
			break;
		case IElement.ENUM_DECLARATION:
			IEnumDeclaration e = (IEnumDeclaration) element;
			name = e.getName();
			if (name != null) element = name;
			break;
		case IElement.ENUM_MEMBER:
			IEnumMember em = (IEnumMember) element;
			name = em.getName();
			if (name != null) element = name;
			break;
		case IElement.VARIABLE_DECLARATION:
			IVariableDeclaration var = (IVariableDeclaration) element;
			name = var.getName();
			if (name != null) element = name;
			break;
		case IElement.TYPEDEF_DECLARATION:
			/* TODO fixme
			ITypedefDeclaration td = (ITypedefDeclaration) element;
			name = td.getName();
			if (name != null) element = name;
			*/
			break;
		case IElement.UNIT_TEST_DECLARATION:
			/* TODO fixme
			IUnitTestDeclaration u = (IUnitTestDeclaration) element;
			name = u.getName();
			if (name != null) element = name;
			*/
			break;
		case IElement.INVARIANT_DECLARATION:
			/* TODO fixme
			IInvariantDeclaration inv = (IInvariantDeclaration) element;
			name = inv.getName();
			if (name != null) element = name;
			*/
			break;
		case IElement.TEMPLATE_DECLARATION:
			ITemplateDeclaration t = (ITemplateDeclaration) element;
			name = t.getName();
			if (name != null) element = name;
			break;
		case IElement.ALIAS_DECLARATION_FRAGMENT:
			AliasDeclarationFragment fragment = (AliasDeclarationFragment) element;
			name = fragment.getName();
			if (name != null) element = name;
			break;
		case IElement.VERSION_DECLARATION:
			if (((IVersionDeclaration) element).getVersion() != null) element = ((IVersionDeclaration) element).getVersion();
			break;
		case IElement.DEBUG_DECLARATION:
			if (((IDebugDeclaration) element).getVersion() != null) {
				element = ((IDebugDeclaration) element).getVersion();
			}
			break;
		case IElement.DEBUG_ASSIGNMENT:
			DebugAssignment da = (DebugAssignment) element;
			element = da.getVersion();
			break;
		case IElement.VERSION_ASSIGNMENT:
			VersionAssignment va = (VersionAssignment) element;
			element = va.getVersion();
			break;			
		case IElement.PRAGMA_DECLARATION:
			IPragmaDeclaration pd = (IPragmaDeclaration) element;
			name = pd.getName();
			if (name != null) element = name;
			break;
		case IElement.MIXIN_DECLARATION:
			IMixinDeclaration mix = (IMixinDeclaration) element;
			name = mix.getName();
			if (name != null) element = name;
			break;
		}
		return new Position(element.getStartPosition(), element.getLength());
	}

}
