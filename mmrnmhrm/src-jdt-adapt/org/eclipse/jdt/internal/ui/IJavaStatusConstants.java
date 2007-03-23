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
package org.eclipse.jdt.internal.ui;

/**
 * Defines status codes relevant to the Java UI plug-in. When a 
 * Core exception is thrown, it contain a status object describing
 * the cause of the exception. The status objects originating from the
 * Java UI plug-in use the codes defined in this interface.
  */
public interface IJavaStatusConstants {

	// Java UI status constants start at 10000 to make sure that we don't
	// collide with resource and java model constants.
	
	public static final int INTERNAL_ERROR= 10001;
	

 }
