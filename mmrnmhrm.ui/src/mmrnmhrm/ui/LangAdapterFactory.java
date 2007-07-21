package mmrnmhrm.ui;

import mmrnmhrm.core.model.lang.ELangElementTypes;
import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IContainmentAdapter;
import org.eclipse.ui.IContributorResourceAdapter;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ide.IContributorResourceAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.tasklist.ITaskListResourceAdapter;


public class LangAdapterFactory implements IAdapterFactory, IContributorResourceAdapter {

	
	private static final Class[] ADAPTER_LIST= new Class[] {
		IResource.class,
		IContributorResourceAdapter.class,
	};
	
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IResource.class.equals(adapterType)) {
			return getResource((ILangElement) adaptableObject);
		} if (IFolder.class.equals(adapterType)) {
			return getResource((ILangElement) adaptableObject);
		}if (IContributorResourceAdapter.class.equals(adapterType)) {
			return this;
		}
		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}
	
	private IResource getResource(ILangElement element) {
		switch (element.getElementType()) {
		
		case ELangElementTypes.COMPILATION_UNIT:
			return (IFile) element.getUnderlyingResource();
		case ELangElementTypes.SOURCEROOT:
		case ELangElementTypes.PACKAGE_FRAGMENT:
			return (IFolder) element.getUnderlyingResource();
		case ELangElementTypes.PROJECT:
			return (IProject) element.getUnderlyingResource();
		case ELangElementTypes.MODELROOT:
			return (IWorkspaceRoot) element.getUnderlyingResource();
		default:
			return null;
		}
	}

	public IResource getAdaptedResource(IAdaptable adaptable) {
		if(adaptable instanceof ILangElement)
			return ((ILangElement) adaptable).getUnderlyingResource();
		return null;
	}

}
