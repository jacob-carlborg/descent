package descent.internal.ui.javaeditor;

import org.eclipse.jface.action.IAction;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;


/**
 * Java element hyperlink.
 *
 * @since 3.1
 */
public class JavaElementHyperlink implements IHyperlink {

	private final IRegion fRegion;
	private final IAction fOpenAction;


	/**
	 * Creates a new Java element hyperlink.
	 */
	public JavaElementHyperlink(IRegion region, IAction openAction) {
		Assert.isNotNull(openAction);
		Assert.isNotNull(region);

		fRegion= region;
		fOpenAction= openAction;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getHyperlinkRegion()
	 * @since 3.1
	 */
	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#open()
	 * @since 3.1
	 */
	public void open() {
		fOpenAction.run();
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getTypeLabel()
	 * @since 3.1
	 */
	public String getTypeLabel() {
		return null;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getHyperlinkText()
	 * @since 3.1
	 */
	public String getHyperlinkText() {
		return null;
	}
}
