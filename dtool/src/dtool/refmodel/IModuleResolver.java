package dtool.refmodel;

import dtool.dom.definitions.Module;

public interface IModuleResolver {

	Module findModule(Module refModule, String packageName, String moduleName);

}
