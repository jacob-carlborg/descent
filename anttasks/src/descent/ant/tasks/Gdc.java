/**
 * Ant Task for building D programs.
 *
 * Authors:
 *  Frank Benoit (benoit at tionex dot de)
 *
 * License:
 *  Public Domain
 */

package descent.ant.tasks;

import org.apache.tools.ant.BuildException;

class Gdc extends Compiler{

	/**
	 * @param d
	 */
	Gdc(D d) {
		super(d);
	}
	public void build() {
		throw new BuildException( "GDC not yet implemented" );
	}
	public void calcDependencies() {
	}

	public void cleanup() {
	}
}