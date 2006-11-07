package descent.ui.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import descent.ui.DescentUI;

/**
 * For now, if a marker is present in a line, it returns
 * it's message.
 */
public class DAnnotationHover implements IAnnotationHover {
	
	public String getHoverInfo(ISourceViewer viewer, int line) {
		IAnnotationModel model = viewer.getAnnotationModel();
		if (model == null) return null;
		
		List<String> a = new ArrayList<String>();
		
		try {
			Iterator it = model.getAnnotationIterator();
			while(it.hasNext()) {
				Object o = it.next();
				if (o instanceof MarkerAnnotation) {
					MarkerAnnotation marker = (MarkerAnnotation) o;
					Position pos = model.getPosition(marker);
					int posLine = viewer.getDocument().getLineOfOffset(pos.getOffset());
					if (posLine == line) {
						a.add((String) marker.getMarker().getAttribute(IMarker.MESSAGE));
					}
				}
			}
			if (a.size() == 0) {
				return null;
			} else if (a.size() == 1) {
				return a.get(0);
			} else {
				StringBuilder s = new StringBuilder();
				s.append("Multiple markers at this line\n");
				for(String x : a) {
					s.append("\t- ");
					s.append(x);
					s.append("\n");
				}
				s.delete(s.length() - 2, s.length());
				return s.toString();
			}
		} catch (CoreException e) {
			DescentUI.log(e);
			return null;
		} catch (BadLocationException e) {
			DescentUI.log(e);
			return null;
		}
	}

}
