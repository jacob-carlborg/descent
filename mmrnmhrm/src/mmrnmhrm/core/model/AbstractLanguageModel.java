package mmrnmhrm.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import util.ArrayUtil;
import util.Assert;

public class AbstractLanguageModel {

	public AbstractLanguageModel() {
		super();
	}
	
	protected void addNature(IProject project, String natureID) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		Assert.isTrue(ArrayUtil.contains(natures, natureID) == false);
		String[] newNatures = ArrayUtil.append(natures, natureID);
		description.setNatureIds(newNatures);
		project.setDescription(description, null); 
	}
}