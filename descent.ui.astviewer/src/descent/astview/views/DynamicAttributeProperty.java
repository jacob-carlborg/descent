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

package descent.astview.views;

import org.eclipse.swt.graphics.Image;

import descent.core.dom.ASTNode;


public abstract class DynamicAttributeProperty extends ExceptionAttribute {

	protected static final String N_A= "N/A"; //$NON-NLS-1$
	private final Object fParent;
	private final String fName;
	
	private Object fViewerElement;
	private String fLabel= "<unknown>";
	
	public DynamicAttributeProperty(Object parentAttribute, String name) {
		fParent= parentAttribute;
		fName= name;
	}

	public Object getParent() {
		return fParent;
	}

	public Object[] getChildren() {
		return EMPTY;
	}
	
	public void setViewerElement(Object viewerAttribute) {
		if (fViewerElement == viewerAttribute)
			return;
		
		fViewerElement= viewerAttribute;
		fException= null;
		Object trayObject= unwrapAttribute(fParent);
		StringBuffer buf= new StringBuffer(fName);
		if (viewerAttribute != null) {
			Object viewerObject= unwrapAttribute(viewerAttribute);
			try {
				String queryResult= executeQuery(viewerObject, trayObject);
				buf.append(queryResult);
			} catch (RuntimeException e) {
				fException= e;
				buf.append(e.getClass().getName());
				buf.append(" for \""); //$NON-NLS-1$
				if (viewerObject == null)
					buf.append("null"); //$NON-NLS-1$
				else
					buf.append('"').append(objectToString(viewerObject));
				buf.append("\" and "); //$NON-NLS-1$
				buf.append(objectToString(trayObject)).append('"');
			}
		} else {
			buf.append(N_A);
		}
		fLabel= buf.toString();
	}

	private String objectToString(Object object) {
		return String.valueOf(object);
	}

	/**
	 * @param attribute an attribute
	 * @return the object inside the attribute, or <code>null</code> iff none
	 */
	public static Object unwrapAttribute(Object attribute) {
		if (attribute instanceof JavaElement) {
			return ((JavaElement) attribute).getJavaElement();
		} else if (attribute instanceof ASTNode) {
			return attribute;
		} else {
			return null;
		}
	}
	
	/**
	 * Executes this dynamic attribute property's query in a protected environment.
	 * A {@link RuntimeException} thrown by this method is made available via
	 * {@link #getException()}. 
	 * 
	 * @param viewerObject the object of the element selected in the AST viewer, or <code>null</code> iff none
	 * @param trayObject the object of the element selected in the comparison tray, or <code>null</code> iff none
	 * @return this property's result
	 */
	protected abstract String executeQuery(Object viewerObject, Object trayObject);

	public String getLabel() {
		return fLabel;
	}

	public Image getImage() {
		return null;
	}
}
