package descent.internal.core.hierarchy;

/**
 * This is the public entry point to resolve type hierarchies.
 *
 * When requesting additional types from the name environment, the resolver
 * accepts all forms (binary, source & compilation unit) for additional types.
 *
 * Side notes: Binary types already know their resolved supertypes so this
 * only makes sense for source types. Even though the compiler finds all binary
 * types to complete the hierarchy of a given source type, is there any reason
 * why the requestor should be informed that binary type X subclasses Y &
 * implements I & J?
 */

import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.internal.compiler.IProblemFactory;
import descent.internal.compiler.env.IGenericType;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.core.Openable;

//TODO JDT Type Hierarchy NOW!
public class HierarchyResolver {

	private final INameEnvironment nameEnvironment;
	private final Map options;
	private final HierarchyBuilder builder;
	private final IProblemFactory factory;

	public HierarchyResolver(INameEnvironment nameEnvironment, Map options, HierarchyBuilder builder, IProblemFactory factory) {
		this.nameEnvironment = nameEnvironment;
		this.options = options;
		this.builder = builder;
		this.factory = factory;
	}

	public void resolve(IGenericType type) {
		// TODO JDT Type Hierarchy NOW!
	}

	public void resolve(Openable[] openables, HashSet localTypes, IProgressMonitor monitor) {
		// TODO JDT Type Hierarchy NOW!
	}
	
}
