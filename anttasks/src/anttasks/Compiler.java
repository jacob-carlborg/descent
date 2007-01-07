/**
 * Ant Task for building D programs.
 *
 * Authors:
 *  Frank Benoit (benoit at tionex dot de)
 *
 * License:
 *  Public Domain
 */
package anttasks;

abstract class Compiler{
	protected final D dTask;
	Compiler(D d) {
		dTask = d;
	}
	public abstract void calcDependencies();
	public abstract void build();
	public abstract void cleanup();
}