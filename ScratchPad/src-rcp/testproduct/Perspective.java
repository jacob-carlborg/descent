package testproduct;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import testproduct.views.SampleView;
import testproduct.views.TestView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);
		
		layout.addStandaloneView(TestView.VIEW_ID, false, IPageLayout.LEFT, 0.5f, editorArea);
//		layout.addStandaloneView(SampleView.ID, true, IPageLayout.RIGHT, 1.0f, editorArea);
		layout.addView(SampleView.ID, IPageLayout.RIGHT, 0.5f, editorArea);
	}

}
