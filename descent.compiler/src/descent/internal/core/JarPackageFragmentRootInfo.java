package descent.internal.core;

/**
 * The element info for <code>JarPackageFragmentRoot</code>s.
 */
class JarPackageFragmentRootInfo extends PackageFragmentRootInfo {
/**
 * Returns an array of non-java resources contained in the receiver.
 */
public Object[] getNonJavaResources() {
	fNonJavaResources = NO_NON_JAVA_RESOURCES;
	return fNonJavaResources;
}
}
