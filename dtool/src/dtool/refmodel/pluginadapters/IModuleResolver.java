package dtool.refmodel.pluginadapters;

import dtool.dom.definitions.Module;

/** 
 * Adapter interface for plugin DTool to interface with a host plugin that 
 * knows how to find modules (such as mmrnmhrm.core's DeeModelRoot)
 */
public interface IModuleResolver {

	/** Finds a module with the given Fully Quallified name.
	 * @param refSourceModule The module where the reference originates.
	 * @param packageNames The name of packages of the module to find.
	 * @param module The name of the modules to find. */
	Module findModule(Module refSourceModule, String[] packages, String module)
			throws Exception;
 
}
