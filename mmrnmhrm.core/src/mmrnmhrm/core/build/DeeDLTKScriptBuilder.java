package mmrnmhrm.core.build;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.StringUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IScriptBuilder;

import dtool.Logg;

@Deprecated
public class DeeDLTKScriptBuilder implements IScriptBuilder {

	//@Override
	public IStatus[] buildModelElements(IScriptProject project, List elements,
			IProgressMonitor monitor) {
		Logg.nolog.println(">> Requested buildModelElements for:\n" +
				StringUtil.collToString(elements, "\n") );
		//IStatus[] status;
		return null;
	}

	//@Override
	public IStatus[] buildResources(IScriptProject project, List resources,
			IProgressMonitor monitor) {
		Logg.nolog.println(">> Requested buildResources for:\n" +
				StringUtil.collToString(resources, "\n") );
		return null;
	}

	//@Override
	public List getDependencies(IScriptProject project, List resources) {
		Logg.nolog.println(">> Requested deps for:\n" +
				StringUtil.collToString(resources, "\n") );
		ArrayList<Object> list = new ArrayList<Object>(1);
		//list.add(project);
		return list;
	}

}
