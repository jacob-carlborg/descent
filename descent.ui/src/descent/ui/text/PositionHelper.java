package descent.ui.text;

import org.eclipse.jface.text.Position;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IAliasDeclaration;
import descent.core.dom.IConditionAssignment;
import descent.core.dom.IConditionalDeclaration;
import descent.core.dom.IDElement;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IEnumDeclaration;
import descent.core.dom.IEnumMember;
import descent.core.dom.IFunctionDeclaration;
import descent.core.dom.IInvariantDeclaration;
import descent.core.dom.IMixinDeclaration;
import descent.core.dom.IModuleDeclaration;
import descent.core.dom.IName;
import descent.core.dom.IPragmaDeclaration;
import descent.core.dom.ITemplateDeclaration;
import descent.core.dom.ITypedefDeclaration;
import descent.core.dom.IUnitTestDeclaration;
import descent.core.dom.IVariableDeclaration;
import descent.core.dom.IVersionDeclaration;

public class PositionHelper {
	
	/**
	 * Given an element, it returns the main element of it.
	 * For example, if the element is a IModuleDeclaration, it returns
	 * it's qualified name.
	 */
	public static Position getElementOfInterest(IDElement element) {
		IName name;
		
		switch(element.getElementType()) {
		case IDElement.MODULE_DECLARATION:
			IModuleDeclaration md = (IModuleDeclaration) element;
			name = md.getQualifiedName();
			if (name != null) element = name;
			break;
		case IDElement.AGGREGATE_DECLARATION:
			IAggregateDeclaration aggr = (IAggregateDeclaration) element;
			name = aggr.getName();
			if (name != null) element = name;
			break;
		case IDElement.FUNCTION_DECLARATION:
			IFunctionDeclaration func = (IFunctionDeclaration) element;
			name = func.getName();
			if (name != null) element = name;
			break;
		case IDElement.ENUM_DECLARATION:
			IEnumDeclaration e = (IEnumDeclaration) element;
			name = e.getName();
			if (name != null) element = name;
			break;
		case IDElement.ENUM_MEMBER:
			IEnumMember em = (IEnumMember) element;
			name = em.getName();
			if (name != null) element = name;
			break;
		case IDElement.VARIABLE_DECLARATION:
			IVariableDeclaration var = (IVariableDeclaration) element;
			name = var.getName();
			if (name != null) element = name;
			break;
		case IDElement.TYPEDEF_DECLARATION:
			ITypedefDeclaration td = (ITypedefDeclaration) element;
			name = td.getName();
			if (name != null) element = name;
			break;
		case IDElement.UNITTEST_DECLARATION:
			IUnitTestDeclaration u = (IUnitTestDeclaration) element;
			name = u.getName();
			if (name != null) element = name;
			break;
		case IDElement.INVARIANT_DECLARATION:
			IInvariantDeclaration inv = (IInvariantDeclaration) element;
			name = inv.getName();
			if (name != null) element = name;
			break;
		case IDElement.TEMPLATE_DECLARATION:
			ITemplateDeclaration t = (ITemplateDeclaration) element;
			name = t.getName();
			if (name != null) element = name;
			break;
		case IDElement.ALIAS_DECLARATION:
			IAliasDeclaration a = (IAliasDeclaration) element;
			name = a.getName();
			if (name != null) element = name;
			break;
		case IDElement.CONDITIONAL_DECLARATION:
			IConditionalDeclaration c = (IConditionalDeclaration) element;
			switch(c.getConditionalDeclarationType()) {
			case IConditionalDeclaration.CONDITIONAL_VERSION:
				name = ((IVersionDeclaration) c).getVersion();
				break;
			case IConditionalDeclaration.CONDITIONAL_DEBUG:
				name = ((IDebugDeclaration) c).getDebug();
				break;
			default: name = null;
			}
			if (name != null) element = name;
			break;
		case IDElement.CONDITION_ASSIGNMENT:
			IConditionAssignment va = (IConditionAssignment) element;
			name = va.getValue();
			if (name != null) element = name;
			break;
		case IDElement.PRAGMA_DECLARATION:
			IPragmaDeclaration pd = (IPragmaDeclaration) element;
			name = pd.getIdentifier();
			if (name != null) element = name;
			break;
		case IDElement.MIXIN_DECLARATION:
			IMixinDeclaration mix = (IMixinDeclaration) element;
			name = mix.getName();
			if (name != null) element = name;
			break;
		}
		return new Position(element.getOffset(), element.getLength());
	}

}
