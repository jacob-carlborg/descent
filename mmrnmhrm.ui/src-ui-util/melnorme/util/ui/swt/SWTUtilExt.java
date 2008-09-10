/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.util.ui.swt;

import java.util.Random;

import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Miscelleanous class with some SWT utils.
 * @see SWTUtil 
 */
public class SWTUtilExt {
		
	/** Controls the enablement of composite color helpers. 
	 * (random backgroung color)*/
	public static boolean enableDebugColorHelpers = false;
	
	private static Random rnd = new Random(0);

	/** Sets the background color of the given Control to the system color
	 * of the given id, if color helpers are enabled. */
	public static void setDebugColor(Control control, int id) {
		if(enableDebugColorHelpers)
			control.setBackground(Display.getDefault().getSystemColor(id));
	}

	/** Sets the background color of the given Control to the given color,
	 * if color helpers are enabled. */
	public static void setDebugColor(Control control, Color color) {
		if(enableDebugColorHelpers)
			control.setBackground(color);
	}
	
	/** Sets the background color of the given Control to a random color,
	 * if color helpers are enabled. */
	public static void setRandomDebugColor(Control control) {
		if(enableDebugColorHelpers) {
			Color color = new Color(Display.getDefault(), 
					rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
			control.setBackground(color);
		}
	}

	/** Sets the enable state of the composite's children, recursively. */
	public static void recursiveSetEnabled(Composite composite, boolean enabled) {
		for(Control control : composite.getChildren() ) {
			if(control instanceof Composite) {
				recursiveSetEnabled((Composite) control, enabled);
			}
			control.setEnabled(enabled);
		}
	}
	

	/** Runs the runnable in the SWT thread. 
	 * (Simply runs the runnable if the current thread is the UI thread,
	 * otherwise calls the runnable in asyncexec.) */
	public static void runInSWTThread(Runnable runnable) {
		if(Display.getCurrent() == null) {
			Display.getDefault().asyncExec(runnable);
		} else {
			runnable.run();
		}
	}

}