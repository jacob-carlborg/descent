package dtool.refmodel;

/** Something that is able to provide one scope. */
public interface IScopeAdaptable {
	
	/** Get the IScope that can be provided.
	 * This can involve resolving operations. */
	IScope getAdaptedScope();
}
