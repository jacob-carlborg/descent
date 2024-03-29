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
package descent.internal.compiler.util;

public interface SuffixConstants {
	public final static String EXTENSION_class = "obj"; //$NON-NLS-1$
	public final static String EXTENSION_CLASS = "OBJ"; //$NON-NLS-1$
	public final static String EXTENSION_java = "d"; //$NON-NLS-1$
	public final static String EXTENSION_JAVA = "D"; //$NON-NLS-1$
	public final static String EXTENSION_di = "di"; //$NON-NLS-1$
	public final static String EXTENSION_DI = "DI"; //$NON-NLS-1$
	
	public final static String SUFFIX_STRING_class = "." + EXTENSION_class; //$NON-NLS-1$
	public final static String SUFFIX_STRING_CLASS = "." + EXTENSION_CLASS; //$NON-NLS-1$
	public final static String SUFFIX_STRING_java = "." + EXTENSION_java; //$NON-NLS-1$
	public final static String SUFFIX_STRING_JAVA = "." + EXTENSION_JAVA; //$NON-NLS-1$
	public final static String SUFFIX_STRING_di = "." + EXTENSION_di; //$NON-NLS-1$
	public final static String SUFFIX_STRING_DI = "." + EXTENSION_DI; //$NON-NLS-1$
	
	public final static char[] SUFFIX_class = SUFFIX_STRING_class.toCharArray();
	public final static char[] SUFFIX_CLASS = SUFFIX_STRING_CLASS.toCharArray();
	public final static char[] SUFFIX_java = SUFFIX_STRING_java.toCharArray();
	public final static char[] SUFFIX_JAVA = SUFFIX_STRING_JAVA.toCharArray();
	public final static char[] SUFFIX_di = SUFFIX_STRING_di.toCharArray();
	public final static char[] SUFFIX_DI = SUFFIX_STRING_DI.toCharArray();
	
	public final static String EXTENSION_jar = "jar"; //$NON-NLS-1$
	public final static String EXTENSION_JAR = "JAR"; //$NON-NLS-1$
	public final static String EXTENSION_zip = "zip"; //$NON-NLS-1$
	public final static String EXTENSION_ZIP = "ZIP"; //$NON-NLS-1$

	public final static String SUFFIX_STRING_jar = "." + EXTENSION_jar; //$NON-NLS-1$
	public final static String SUFFIX_STRING_JAR = "." + EXTENSION_JAR; //$NON-NLS-1$
	public final static String SUFFIX_STRING_zip = "." + EXTENSION_zip; //$NON-NLS-1$
	public final static String SUFFIX_STRING_ZIP = "." + EXTENSION_ZIP; //$NON-NLS-1$

	public final static char[] SUFFIX_jar = SUFFIX_STRING_jar.toCharArray();
	public final static char[] SUFFIX_JAR = SUFFIX_STRING_JAR.toCharArray();
	public final static char[] SUFFIX_zip = SUFFIX_STRING_zip.toCharArray(); 
	public final static char[] SUFFIX_ZIP = SUFFIX_STRING_ZIP.toCharArray();
}

