package mmrnmhrm.ui.util;

import java.util.Random;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Miscelleanous class with some SWT utils.
 * @see SWTUtil 
 */
public class SWTUtil2 {
		
	/** Controls the enablement of color helpers */
	public static final boolean enableColorHelpers = true;
	
	private static Random rnd = new Random(0);

	/** Sets the background color of the given Control to the system color
	 * of the given id, if color helpers are enabled. */
	public static void setColor(Control control, int id) {
		if(enableColorHelpers)
			control.setBackground(Display.getDefault().getSystemColor(id));
	}

	/** Sets the background color of the given Control to the given color,
	 * if color helpers are enabled. */
	public static void setColor(Control control, Color color) {
		if(enableColorHelpers)
			control.setBackground(color);
	}
	
	/** Sets the background color of the given Control to a random color,
	 * if color helpers are enabled. */
	public static void setRandomColor(Control control) {
		if(enableColorHelpers) {
			Color color = new Color(Display.getDefault(), 
					rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
			control.setBackground(color);
		}
	}

	/** Gets FontMetrics for the given control */
	protected static FontMetrics getFontMetrics(Control testControl) {
		FontMetrics fontMetrics;
		// Compute and store a font metric
		GC gc = new GC(testControl);
		gc.setFont(JFaceResources.getDialogFont());
		fontMetrics = gc.getFontMetrics();
		gc.dispose();
		return fontMetrics;
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

	/** Inits layout with dialog unit constants. testControl is used to obtain
	 * FontMetrics */
	public static void initGridLayout(GridLayout layout, Control testControl) {
		FontMetrics fontMetrics = getFontMetrics(testControl);
	
		layout.horizontalSpacing = Dialog.convertHorizontalDLUsToPixels(
				fontMetrics, IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = Dialog.convertHorizontalDLUsToPixels(
				fontMetrics, IDialogConstants.VERTICAL_SPACING);
		layout.marginWidth = Dialog.convertHorizontalDLUsToPixels(fontMetrics,
				IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = Dialog.convertHorizontalDLUsToPixels(fontMetrics,
				IDialogConstants.VERTICAL_MARGIN);
	}

}