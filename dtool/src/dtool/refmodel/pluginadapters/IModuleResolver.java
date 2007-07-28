package dtool.refmodel.pluginadapters;

import org.eclipse.core.runtime.CoreException;

import dtool.dom.definitions.Module;

/** Kludge for plugin DTool to interface with a plugin that knows how to
 * find modules (such as mmrnmhrm.core's DeeModelRoot)
 */
public interface IModuleResolver {

	Module findModule(Module refModule, String packageName, String moduleName)
			throws CoreException;
 
}
