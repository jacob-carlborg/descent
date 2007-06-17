package descent.internal.ui.text.java;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

/**
 * Map of parameters for the specific content assist command.
 * 
 * @since 3.2
 */
public final class ContentAssistComputerParameter implements IParameterValues {
	/*
	 * @see org.eclipse.core.commands.IParameterValues#getParameterValues()
	 */
	public Map getParameterValues() {
		Collection descriptors= CompletionProposalComputerRegistry.getDefault().getProposalCategories();
		Map map= new HashMap(descriptors.size());
		for (Iterator it= descriptors.iterator(); it.hasNext();) {
			CompletionProposalCategory category= (CompletionProposalCategory) it.next();
			map.put(category.getDisplayName(), category.getId());
		}
		return map;
	}
}
