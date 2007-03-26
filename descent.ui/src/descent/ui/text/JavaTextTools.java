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
package descent.ui.text;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import descent.internal.ui.text.JavaColorManager;
import descent.internal.ui.text.JavaCommentScanner;
import descent.internal.ui.text.JavaPartitionScannerFactory;
import descent.internal.ui.text.SingleTokenJavaScanner;
import descent.internal.ui.text.java.JavaCodeScanner;
import descent.internal.ui.text.javadoc.JavaDocScanner;


/**
 * Tools required to configure a Java text viewer.
 * The color manager and all scanner exist only one time, i.e.
 * the same instances are returned to all clients. Thus, clients
 * share those tools.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class JavaTextTools {

	/**
	 * Array with legal content types.
	 * @since 3.0
	 */
	private final static String[] LEGAL_CONTENT_TYPES= new String[] {
		IJavaPartitions.JAVA_DOC,
		IJavaPartitions.JAVA_MULTI_LINE_COMMENT,
		IJavaPartitions.JAVA_MULTI_LINE_PLUS_COMMENT,
		IJavaPartitions.JAVA_MULTI_LINE_PLUS_DOC_COMMENT,
		IJavaPartitions.JAVA_SINGLE_LINE_COMMENT,
		IJavaPartitions.JAVA_SINGLE_LINE_DOC_COMMENT,
		IJavaPartitions.JAVA_STRING,
		IJavaPartitions.JAVA_PRAGMA,
		IJavaPartitions.JAVA_CHARACTER
	};

	/**
	 * This tools' preference listener.
	 */
	private class PreferenceListener implements IPropertyChangeListener, Preferences.IPropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			adaptToPreferenceChange(event);
		}
		public void propertyChange(Preferences.PropertyChangeEvent event) {
			adaptToPreferenceChange(new PropertyChangeEvent(event.getSource(), event.getProperty(), event.getOldValue(), event.getNewValue()));
		}
	}

	/** The color manager. */
	private JavaColorManager fColorManager;
	/** The Java source code scanner. */
	private JavaCodeScanner fCodeScanner;
	/** The Java multi-line comment scanner. */
	private JavaCommentScanner fMultilineCommentScanner;
	/** The Java single-line comment scanner. */
	private JavaCommentScanner fSinglelineCommentScanner;
	/** The Java string scanner. */
	private SingleTokenJavaScanner fStringScanner;
	/** The Java pragma scanner. */
	private SingleTokenJavaScanner fPragmaScanner;
	/** The JavaDoc scanner. */
	private JavaDocScanner fJavaDocScanner;
	/** The Java partitions scanner. */
	private IPartitionTokenScanner fPartitionScanner;
	/** The preference store. */
	private IPreferenceStore fPreferenceStore;
	/**
	 * The core preference store.
	 * @since 2.1
	 */
	private Preferences fCorePreferenceStore;
	/** The preference change listener */
	private PreferenceListener fPreferenceListener= new PreferenceListener();


	/**
	 * Creates a new Java text tools collection.
	 *
	 * @param store the preference store to initialize the text tools. The text tool
	 *			instance installs a listener on the passed preference store to adapt itself to
	 *			changes in the preference store. In general <code>PreferenceConstants.
	 *			getPreferenceStore()</code> should be used to initialize the text tools.
	 * @see descent.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.0
	 */
	public JavaTextTools(IPreferenceStore store) {
		this(store, null, true);
	}

	/**
	 * Creates a new Java text tools collection.
	 *
	 * @param store the preference store to initialize the text tools. The text tool
	 *			instance installs a listener on the passed preference store to adapt itself to
	 *			changes in the preference store. In general <code>PreferenceConstants.
	 *			getPreferenceStore()</code> should be used to initialize the text tools.
	 * @param autoDisposeOnDisplayDispose if <code>true</code>  the color manager
	 *			automatically disposes all managed colors when the current display gets disposed
	 *			and all calls to {@link org.eclipse.jface.text.source.ISharedTextColors#dispose()} are ignored.
	 * @see descent.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.1
	 */
	public JavaTextTools(IPreferenceStore store, boolean autoDisposeOnDisplayDispose) {
		this(store, null, autoDisposeOnDisplayDispose);
	}

	/**
	 * Creates a new Java text tools collection.
	 * @param store the preference store to initialize the text tools. The text tool
	 *			instance installs a listener on the passed preference store to adapt itself to
	 *			changes in the preference store. In general <code>PreferenceConstants.
	 *			getPreferenceStore()</code> should be used to initialize the text tools.
	 * @param coreStore optional preference store to initialize the text tools. The text tool
	 *			instance installs a listener on the passed preference store to adapt itself to
	 *			changes in the preference store.
	 * @see descent.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.1
	 */
	public JavaTextTools(IPreferenceStore store, Preferences coreStore) {
		this(store, coreStore, true);
	}

	/**
	 * Creates a new Java text tools collection.
	 *
	 * @param store the preference store to initialize the text tools. The text tool
	 *			instance installs a listener on the passed preference store to adapt itself to
	 *			changes in the preference store. In general <code>PreferenceConstants.
	 *			getPreferenceStore()</code> should be used to initialize the text tools.
	 * @param coreStore optional preference store to initialize the text tools. The text tool
	 *			instance installs a listener on the passed preference store to adapt itself to
	 *			changes in the preference store.
	 * @param autoDisposeOnDisplayDispose 	if <code>true</code>  the color manager
	 *			automatically disposes all managed colors when the current display gets disposed
	 *			and all calls to {@link org.eclipse.jface.text.source.ISharedTextColors#dispose()} are ignored.
	 * @see descent.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.1
	 */
	public JavaTextTools(IPreferenceStore store, Preferences coreStore, boolean autoDisposeOnDisplayDispose) {
		fPreferenceStore= store;
		fPreferenceStore.addPropertyChangeListener(fPreferenceListener);

		fCorePreferenceStore= coreStore;
		if (fCorePreferenceStore != null)
			fCorePreferenceStore.addPropertyChangeListener(fPreferenceListener);

		fColorManager= new JavaColorManager(autoDisposeOnDisplayDispose);
		fCodeScanner= new JavaCodeScanner(fColorManager, store);
		fMultilineCommentScanner= new JavaCommentScanner(fColorManager, store, coreStore, IJavaColorConstants.JAVA_MULTI_LINE_COMMENT);
		fSinglelineCommentScanner= new JavaCommentScanner(fColorManager, store, coreStore, IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT);
		fStringScanner= new SingleTokenJavaScanner(fColorManager, store, IJavaColorConstants.JAVA_STRING);
		fPragmaScanner= new SingleTokenJavaScanner(fColorManager, store, IJavaColorConstants.JAVA_PRAGMA);
		fJavaDocScanner= new JavaDocScanner(fColorManager, store, coreStore);
		fPartitionScanner= JavaPartitionScannerFactory.newJavaPartitionScanner();
	}

	/**
	 * Disposes all the individual tools of this tools collection.
	 */
	public void dispose() {

		fCodeScanner= null;
		fMultilineCommentScanner= null;
		fSinglelineCommentScanner= null;
		fStringScanner= null;
		fPragmaScanner= null;
		fJavaDocScanner= null;
		fPartitionScanner= null;

		if (fColorManager != null) {
			fColorManager.dispose();
			fColorManager= null;
		}

		if (fPreferenceStore != null) {
			fPreferenceStore.removePropertyChangeListener(fPreferenceListener);
			fPreferenceStore= null;

			if (fCorePreferenceStore != null) {
				fCorePreferenceStore.removePropertyChangeListener(fPreferenceListener);
				fCorePreferenceStore= null;
			}

			fPreferenceListener= null;
		}
	}

	/**
	 * Returns the color manager which is used to manage
	 * any Java-specific colors needed for such things like syntax highlighting.
	 * <p>
	 * Clients which are only interested in the color manager of the Java UI
	 * plug-in should use {@link descent.ui.JavaUI#getColorManager()}.
	 * </p>
	 *
	 * @return the color manager to be used for Java text viewers
	 * @see descent.ui.JavaUI#getColorManager()
	 */
	public IColorManager getColorManager() {
		return fColorManager;
	}

	/**
	 * Returns a scanner which is configured to scan
	 * Java-specific partitions, which are multi-line comments,
	 * Javadoc comments, and regular Java source code.
	 *
	 * @return a Java partition scanner
	 */
	public IPartitionTokenScanner getPartitionScanner() {
		return fPartitionScanner;
	}

	/**
	 * Factory method for creating a Java-specific document partitioner
	 * using this object's partitions scanner. This method is a
	 * convenience method.
	 *
	 * @return a newly created Java document partitioner
	 */
	public IDocumentPartitioner createDocumentPartitioner() {
		return new FastPartitioner(getPartitionScanner(), LEGAL_CONTENT_TYPES);
	}

	/**
	 * Adapts the behavior of the contained components to the change
	 * encoded in the given event.
	 *
	 * @param event the event to which to adapt
	 * @since 2.0
	 * @deprecated As of 3.0, no replacement
	 */
	protected void adaptToPreferenceChange(PropertyChangeEvent event) {
		if (fCodeScanner.affectsBehavior(event))
			fCodeScanner.adaptToPreferenceChange(event);
		if (fMultilineCommentScanner.affectsBehavior(event))
			fMultilineCommentScanner.adaptToPreferenceChange(event);
		if (fSinglelineCommentScanner.affectsBehavior(event))
			fSinglelineCommentScanner.adaptToPreferenceChange(event);
		if (fStringScanner.affectsBehavior(event))
			fStringScanner.adaptToPreferenceChange(event);
		if (fPragmaScanner.affectsBehavior(event))
			fPragmaScanner.adaptToPreferenceChange(event);
		if (fJavaDocScanner.affectsBehavior(event))
			fJavaDocScanner.adaptToPreferenceChange(event);
	}

	/**
	 * Sets up the Java document partitioner for the given document for the default partitioning.
	 *
	 * @param document the document to be set up
	 * @since 3.0
	 */
	public void setupJavaDocumentPartitioner(IDocument document) {
		setupJavaDocumentPartitioner(document, IDocumentExtension3.DEFAULT_PARTITIONING);
	}

	/**
	 * Sets up the Java document partitioner for the given document for the given partitioning.
	 *
	 * @param document the document to be set up
	 * @param partitioning the document partitioning
	 * @since 3.0
	 */
	public void setupJavaDocumentPartitioner(IDocument document, String partitioning) {
		IDocumentPartitioner partitioner= createDocumentPartitioner();
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3= (IDocumentExtension3) document;
			extension3.setDocumentPartitioner(partitioning, partitioner);
		} else {
			document.setDocumentPartitioner(partitioner);
		}
		partitioner.connect(document);
	}

	/**
	 * Returns this text tool's preference store.
	 *
	 * @return the preference store
	 * @since 3.0
	 */
	protected IPreferenceStore getPreferenceStore() {
		return fPreferenceStore;
	}

	/**
	 * Returns this text tool's core preference store.
	 *
	 * @return the core preference store
	 * @since 3.0
	 */
	protected Preferences getCorePreferenceStore() {
		return fCorePreferenceStore;
	}
}
