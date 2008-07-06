package descent.internal.core;

/**
 * Element info for JarPackageFragments.
 */
class JarPackageFragmentInfo extends PackageFragmentInfo {
/**
 * Returns an array of non-java resources contained in the receiver.
 */
Object[] getNonJavaResources() {
	return this.nonJavaResources;
}
}
