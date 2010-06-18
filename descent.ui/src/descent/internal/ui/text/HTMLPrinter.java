/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.text;


import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import descent.core.ddoc.HTMLPrinterUtils;


/**
 * Provides a set of convenience methods for creating HTML pages.
 */
public class HTMLPrinter extends HTMLPrinterUtils {

	private static RGB BG_COLOR_RGB= null;

	static {
		final Display display= Display.getDefault();
		if (display != null && !display.isDisposed()) {
			try {
				display.asyncExec(new Runnable() {
					/*
					 * @see java.lang.Runnable#run()
					 */
					public void run() {
						BG_COLOR_RGB= display.getSystemColor(SWT.COLOR_INFO_BACKGROUND).getRGB();
					}
				});
			} catch (SWTError err) {
				// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=45294
				if (err.code != SWT.ERROR_DEVICE_DISPOSED)
					throw err;
			}
		}
	}

	private HTMLPrinter() {
	}
	
	public static void insertPageProlog(StringBuffer buffer, int position, RGB bgRGB, URL styleSheetURL) {

		if (bgRGB == null)
			insertPageProlog(buffer, position, styleSheetURL);
		else {
			StringBuffer pageProlog= new StringBuffer(300);

			pageProlog.append("<html>"); //$NON-NLS-1$

			appendStyleSheetURL(pageProlog, styleSheetURL);

			pageProlog.append("<body text=\"#000000\" bgcolor=\""); //$NON-NLS-1$
			appendColor(pageProlog, bgRGB);
			pageProlog.append("\">"); //$NON-NLS-1$

			buffer.insert(position,  pageProlog.toString());
		}
	}
	public static void insertPageProlog(StringBuffer buffer, int position, RGB bgRGB, String styleSheet) {
		
		if (bgRGB == null)
			insertPageProlog(buffer, position, styleSheet);
		else {
			StringBuffer pageProlog= new StringBuffer(300);
			
			pageProlog.append("<html>"); //$NON-NLS-1$
			
			appendStyleSheetURL(pageProlog, styleSheet);
			
			pageProlog.append("<body text=\"#000000\" bgcolor=\""); //$NON-NLS-1$
			appendColor(pageProlog, bgRGB);
			pageProlog.append("\">"); //$NON-NLS-1$
			
			buffer.insert(position,  pageProlog.toString());
		}
	}

	public static void insertPageProlog(StringBuffer buffer, int position, RGB bgRGB) {
		if (bgRGB == null)
			insertPageProlog(buffer, position);
		else {
			StringBuffer pageProlog= new StringBuffer(60);
			pageProlog.append("<html><body text=\"#000000\" bgcolor=\""); //$NON-NLS-1$
			appendColor(pageProlog, bgRGB);
			pageProlog.append("\">"); //$NON-NLS-1$
			buffer.insert(position,  pageProlog.toString());
		}
	}

	private static void appendStyleSheetURL(StringBuffer buffer, String styleSheet) {
		if (styleSheet == null)
			return;
		
		buffer.append("<head><style CHARSET=\"ISO-8859-1\" TYPE=\"text/css\">"); //$NON-NLS-1$
		buffer.append(styleSheet);
		buffer.append("</style></head>"); //$NON-NLS-1$
	}
	
	private static void appendStyleSheetURL(StringBuffer buffer, URL styleSheetURL) {
		if (styleSheetURL == null)
			return;

		buffer.append("<head>"); //$NON-NLS-1$

		buffer.append("<LINK REL=\"stylesheet\" HREF= \""); //$NON-NLS-1$
		buffer.append(styleSheetURL);
		buffer.append("\" CHARSET=\"ISO-8859-1\" TYPE=\"text/css\">"); //$NON-NLS-1$

		buffer.append("</head>"); //$NON-NLS-1$
	}

	public static void appendColor(StringBuffer buffer, RGB rgb) {
		buffer.append('#');
		buffer.append(toHexString(rgb.red));
		buffer.append(toHexString(rgb.green));
		buffer.append(toHexString(rgb.blue));
	}
	
	private static String toHexString(int value) {
		String s = Integer.toHexString(value);
		if (s.length() != 2) {
			return "0" + s;
		}
		return s;
	}

	public static void insertPageProlog(StringBuffer buffer, int position) {
		insertPageProlog(buffer, position, getBgColor()); 
	}

	public static void insertPageProlog(StringBuffer buffer, int position, URL styleSheetURL) {
		insertPageProlog(buffer, position, getBgColor(), styleSheetURL); 
	}
	
	public static void insertPageProlog(StringBuffer buffer, int position, String styleSheet) {
		insertPageProlog(buffer, position, getBgColor(), styleSheet); 
	}

	private static RGB getBgColor() {
		if (BG_COLOR_RGB != null)
			return BG_COLOR_RGB;
		return new RGB(255,255, 225); // RGB value of info bg color on WindowsXP

	}

	public static void addPageProlog(StringBuffer buffer) {
		insertPageProlog(buffer, buffer.length());
	}
	
	public static void addPageEpilog(StringBuffer buffer) {
		buffer.append("</font></body></html>"); //$NON-NLS-1$
	}
	
}
