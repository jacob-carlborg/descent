package descent.core.dom;

import java.util.HashMap;
import java.util.Map;

import descent.core.WorkingCopyOwner;

public class DefaultBindingResolver extends BindingResolver {
	
	/*
	 * Holds on binding tables that can be shared by several ASTs.
	 */
	static class BindingTables {
	
		/**
		 * This map is used to get a binding from its binding key.
		 */
		Map bindingKeysToBindings;
		
		BindingTables() {
			this.bindingKeysToBindings = new HashMap();
		}
	
	}
	
	/**
	 * This map is used to get an ast node from its binding (new binding) or DOM
	 */
	Map bindingsToAstNodes;
	
	/*
	 * The shared binding tables accros ASTs.
	 */
	BindingTables bindingTables;
	
	/**
	 * The working copy owner that defines the context in which this resolver is creating the bindings.
	 */
	WorkingCopyOwner workingCopyOwner;
	
	/**
	 * Constructor for DefaultBindingResolver.
	 */
	DefaultBindingResolver(WorkingCopyOwner workingCopyOwner, BindingTables bindingTables) {
		this.bindingsToAstNodes = new HashMap();
		this.bindingTables = bindingTables;
		this.workingCopyOwner = workingCopyOwner;
	}

}
