/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.unittest.ui;

import org.eclipse.swt.graphics.Image;

import descent.internal.unittest.DescentUnittestPlugin;

/**
 * Manages a set of images that can show progress in the image itself.
 */
public class ProgressImages {
	private static final int PROGRESS_STEPS= 9;
	
	private static final String BASE= "prgss/"; //$NON-NLS-1$
	private static final String FAILURE= "ff"; //$NON-NLS-1$
	private static final String OK= "ss"; //$NON-NLS-1$
	
	private Image[] fOKImages= new Image[PROGRESS_STEPS];
	private Image[] fFailureImages= new Image[PROGRESS_STEPS];
	
	private void load() {
		if (isLoaded())
			return;
			
		for (int i= 0; i < PROGRESS_STEPS; i++) {
			String okname= BASE+OK+Integer.toString(i+1)+".gif"; //$NON-NLS-1$ 
			fOKImages[i]= createImage(okname);
			String failurename= BASE+FAILURE+Integer.toString(i+1)+".gif"; //$NON-NLS-1$ 
			fFailureImages[i]= createImage(failurename);
		}
	}

	private Image createImage(String name) {
		return DescentUnittestPlugin.getImageDescriptor(name).createImage();
	}
	
	public void dispose() {
		if (!isLoaded())
			return; 
				
		for (int i= 0; i < PROGRESS_STEPS; i++) {
			fOKImages[i].dispose();
			fOKImages[i]= null;
			fFailureImages[i].dispose();
			fFailureImages[i]= null;
		}
	}
	
	public Image getImage(int current, int total, int errors, int failures) {
		if (!isLoaded())
			load();
			
		if (total == 0)
			return fOKImages[0];
		int index= ((current*PROGRESS_STEPS)/total)-1;
		index= Math.min(Math.max(0, index), PROGRESS_STEPS-1);

		if (errors + failures == 0)
			return fOKImages[index];
		return fFailureImages[index];
	}
	
	private boolean isLoaded() {
		return fOKImages[0] != null;
	}	
}
