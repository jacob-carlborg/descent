package descent.internal.corext.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.core.resources.IResource;

import descent.core.IJavaElement;
import descent.core.search.IJavaSearchScope;

/**
 * A type info element that represent an unresolveable type. This can happen if
 * the search engine reports a type name that doesn't exist in the workspace.
 */
public class UnresolvableTypeInfo extends TypeInfo {
	
	private final String fPath;
	
	public UnresolvableTypeInfo(String pkg, String name, char[][] enclosingTypes, int modifiers, String path) {
		super(pkg, name, enclosingTypes, modifiers);
		fPath= path;
	}
	
	public boolean equals(Object obj) {
		if (!UnresolvableTypeInfo.class.equals(obj.getClass()))
			return false;
		UnresolvableTypeInfo other= (UnresolvableTypeInfo)obj;
		return doEquals(other) && fPath.equals(other.fPath);
	}
	
	public int getElementType() {
		return TypeInfo.UNRESOLVABLE_TYPE_INFO;
	}
	
	public String getPath() {
		return fPath;
	}
	
	public IPath getPackageFragmentRootPath() {
		return new Path(fPath);
	}
	
	public String getPackageFragmentRootName() {
		return fPath;
	}
	
	protected IJavaElement getContainer(IJavaSearchScope scope) {
		return null;
	}
	
	public long getContainerTimestamp() {
		return IResource.NULL_STAMP;
	}
	
	public boolean isContainerDirty() {
		return false;
	}
}
