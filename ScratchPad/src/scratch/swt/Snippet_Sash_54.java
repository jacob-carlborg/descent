/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package scratch.swt;

/*
 * Sash example snippet: create a sash (allow it to be moved)
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;

public class Snippet_Sash_54 {
	
	public static void main (String [] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setSize(400, 300);
		shell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA));
		
		final Composite container = new Composite(shell, SWT.NONE);
		container.setBounds(30, 30, 200, 200);
		container.setLayout(GridLayoutFactory.fillDefaults().margins(0,5).spacing(0, 0).create());

		Composite foo = new Composite(container, SWT.NONE);
		final GridData griddata = GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 102).create();
		foo.setLayoutData(griddata);
		foo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));
		
		assertTrue(foo.getBounds().height != 102);
		container.layout(true, true);
		assertTrue(foo.getBounds().height == 102);
		
		final Sash sash = new Sash(container, SWT.BORDER | SWT.HORIZONTAL);
		sash.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
//		sash.setBounds(0, 0, 100, 5);
		sash.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				griddata.heightHint = e.y;
				container.layout(true, true);
//				sash.setBounds (e.x, e.y, e.width, e.height);
			}
		});
		container.layout(true, true);
		shell.open ();
		sash.setFocus ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
} 
