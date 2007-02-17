/***************************************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package descent.internal.ui.navigator;

import descent.internal.ui.filters.EmptyPackageFilter;




/**
 * Filters out all empty package fragments unless the mode of the viewer is set to hierarchical
 * layout.
 * 
 * This filter is only applicable to instances of the Common Navigator.
 */
public class NonEssentialEmptyPackageFilter extends NonEssentialElementsFilter {
  
	public NonEssentialEmptyPackageFilter() {
		super(new EmptyPackageFilter());
	}
}
