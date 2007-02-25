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
package descent.internal.corext.refactoring;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import descent.core.search.SearchMatch;
import descent.core.search.SearchRequestor;

/**
 * Collects the results returned by a <code>SearchEngine</code>.
 */
public class CollectingSearchRequestor extends SearchRequestor {
	private ArrayList fFound;

	public CollectingSearchRequestor() {
		fFound= new ArrayList();
	}
	
	/* (non-Javadoc)
	 * @see descent.core.search.SearchRequestor#acceptSearchMatch(descent.core.search.SearchMatch)
	 */
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (RefactoringSearchEngine.isFiltered(match))
			return;

		fFound.add(match);
	}

	/**
	 * @return a List of {@link SearchMatch}es (not sorted)
	 */
	public List/*<SearchMatch>*/ getResults() {
		return fFound;
	}
}


