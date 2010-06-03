package descent.internal.core.search;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.search.IJavaSearchScope;
import descent.core.search.SearchParticipant;
import descent.core.search.SearchPattern;
import descent.internal.compiler.util.SimpleSet;
import descent.internal.core.index.Index;

public class SubTypeSearchJob extends PatternSearchJob {

SimpleSet indexes = new SimpleSet(5);

public SubTypeSearchJob(SearchPattern pattern, SearchParticipant participant, IJavaSearchScope scope, IndexQueryRequestor requestor) {
	super(pattern, participant, scope, requestor);
}
public void finished() {
	Object[] values = this.indexes.values;
	for (int i = 0, l = values.length; i < l; i++)
		if (values[i] != null)
			((Index) values[i]).stopQuery();
}
public boolean search(Index index, IProgressMonitor progressMonitor) {
	if (index == null) return COMPLETE;
	if (!indexes.includes(index)) {
		indexes.add(index);
		index.startQuery();
	}
	return super.search(index, progressMonitor);
}
}
