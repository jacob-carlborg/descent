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

package descent.internal.ui.javaeditor;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.swt.widgets.Display;

import descent.core.BufferChangedEvent;
import descent.core.IBuffer;
import descent.core.IBufferChangedListener;
import descent.core.IOpenable;
import descent.core.JavaModelException;
import descent.internal.ui.JavaPlugin;


/**
 * Adapts <code>IDocument</code> to <code>IBuffer</code>. Uses the
 * same algorithm as the text widget to determine the buffer's line delimiter.
 * All text inserted into the buffer is converted to this line delimiter.
 * This class is <code>public</code> for test purposes only.
 */
public class DocumentAdapter implements IBuffer, IDocumentListener {

		/**
		 * Internal implementation of a NULL instanceof IBuffer.
		 */
		static private class NullBuffer implements IBuffer {
			public void addBufferChangedListener(IBufferChangedListener listener) {}
			public void append(char[] text) {}
			public void append(String text) {}
			public void close() {}
			public char getChar(int position) { return 0; }
			public char[] getCharacters() { return null; }
			public String getContents() { return null; }
			public int getLength() { return 0; }
			public IOpenable getOwner() { return null; }
			public String getText(int offset, int length) { return null; }
			public IResource getUnderlyingResource() { return null; }
			public boolean hasUnsavedChanges() { return false; }
			public boolean isClosed() { return false; }
			public boolean isReadOnly() { return true; }
			public void removeBufferChangedListener(IBufferChangedListener listener) {}
			public void replace(int position, int length, char[] text) {}
			public void replace(int position, int length, String text) {}
			public void save(IProgressMonitor progress, boolean force) throws JavaModelException {}
			public void setContents(char[] contents) {}
			public void setContents(String contents) {}
		}


		/** NULL implementing <code>IBuffer</code> */
		public final static IBuffer NULL= new NullBuffer();


		/**
		 *  Executes a document set content call in the UI thread.
		 */
		protected class DocumentSetCommand implements Runnable {

			private String fContents;

			public void run() {
				if (!isClosed())
					fDocument.set(fContents);
			}

			public void set(String contents) {
				fContents= contents;
				Display.getDefault().syncExec(this);
			}
		}

		/**
		 * Executes a document replace call in the UI thread.
		 */
		protected class DocumentReplaceCommand implements Runnable {

			private int fOffset;
			private int fLength;
			private String fText;

			public void run() {
				try {
					if (!isClosed())
						fDocument.replace(fOffset, fLength, fText);
				} catch (BadLocationException x) {
					// ignore
				}
			}

			public void replace(int offset, int length, String text) {
				fOffset= offset;
				fLength= length;
				fText= text;
				Display.getDefault().syncExec(this);
			}
		}

	private static final boolean DEBUG_LINE_DELIMITERS= true;

	private IOpenable fOwner;
	private IFile fFile;
	private ITextFileBuffer fTextFileBuffer;
	private IDocument fDocument;

	private DocumentSetCommand fSetCmd= new DocumentSetCommand();
	private DocumentReplaceCommand fReplaceCmd= new DocumentReplaceCommand();

	private Set fLegalLineDelimiters;

	private List<IBufferChangedListener> fBufferListeners= new ArrayList<IBufferChangedListener>(3);
	private IStatus fStatus;

	/*
	 * @since 3.2
	 */
	private IPath fPath;


	/**
	 * Constructs a new document adapter.
	 *
	 * @since 3.2
	 */
	public DocumentAdapter(IOpenable owner, IPath path) {
		Assert.isLegal(path != null);
		
		fOwner= owner;
		fPath= path;
		
		initialize();
	}
	
	/**
	 * Constructs a new document adapter.
	 */
	public DocumentAdapter(IOpenable owner, IFile file) {

		fOwner= owner;
		fFile= file;
		fPath= fFile.getFullPath();

		initialize();
	}

	private void initialize() {
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		try {
			manager.connect(fPath, new NullProgressMonitor());
			fTextFileBuffer= manager.getTextFileBuffer(fPath);
			fDocument= fTextFileBuffer.getDocument();
		} catch (CoreException x) {
			fStatus= x.getStatus();
			fDocument= manager.createEmptyDocument(fPath);
			if (fDocument instanceof ISynchronizable)
				((ISynchronizable)fDocument).setLockObject(new Object());
		}
		fDocument.addPrenotifiedDocumentListener(this);
	}

	/**
	 * Returns the status of this document adapter.
	 */
	public IStatus getStatus() {
		if (fStatus != null)
			return fStatus;
		if (fTextFileBuffer != null)
			return fTextFileBuffer.getStatus();
		return null;
	}

	/**
	 * Returns the adapted document.
	 *
	 * @return the adapted document
	 */
	public IDocument getDocument() {
		return fDocument;
	}

	/*
	 * @see IBuffer#addBufferChangedListener(IBufferChangedListener)
	 */
	public void addBufferChangedListener(IBufferChangedListener listener) {
		Assert.isNotNull(listener);
		if (!fBufferListeners.contains(listener))
			fBufferListeners.add(listener);
	}

	/*
	 * @see IBuffer#removeBufferChangedListener(IBufferChangedListener)
	 */
	public void removeBufferChangedListener(IBufferChangedListener listener) {
		Assert.isNotNull(listener);
		fBufferListeners.remove(listener);
	}

	/*
	 * @see IBuffer#append(char[])
	 */
	public void append(char[] text) {
		append(new String(text));
	}

	/*
	 * @see IBuffer#append(String)
	 */
	public void append(String text) {
		if (DEBUG_LINE_DELIMITERS) {
			validateLineDelimiters(text);
		}
		fReplaceCmd.replace(fDocument.getLength(), 0, text);
	}

	/*
	 * @see IBuffer#close()
	 */
	public void close() {

		if (isClosed())
			return;

		IDocument d= fDocument;
		fDocument= null;
		d.removePrenotifiedDocumentListener(this);

		if (fTextFileBuffer != null) {
			ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
			try {
				manager.disconnect(fTextFileBuffer.getLocation(), new NullProgressMonitor());
			} catch (CoreException x) {
				// ignore
			}
			fTextFileBuffer= null;
		}

		fireBufferChanged(new BufferChangedEvent(this, 0, 0, null));
		fBufferListeners.clear();
	}

	/*
	 * @see IBuffer#getChar(int)
	 */
	public char getChar(int position) {
		try {
			return fDocument.getChar(position);
		} catch (BadLocationException x) {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	/*
	 *  @see IBuffer#getCharacters()
	 */
	public char[] getCharacters() {
		String content= getContents();
		return content == null ? null : content.toCharArray();
	}

	/*
	 * @see IBuffer#getContents()
	 */
	public String getContents() {
		return fDocument.get();
	}

	/*
	 * @see IBuffer#getLength()
	 */
	public int getLength() {
		return fDocument.getLength();
	}

	/*
	 * @see IBuffer#getOwner()
	 */
	public IOpenable getOwner() {
		return fOwner;
	}

	/*
	 * @see IBuffer#getText(int, int)
	 */
	public String getText(int offset, int length) {
		try {
			return fDocument.get(offset, length);
		} catch (BadLocationException x) {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	/*
	 * @see IBuffer#getUnderlyingResource()
	 */
	public IResource getUnderlyingResource() {
		return fFile;
	}

	/*
	 * @see IBuffer#hasUnsavedChanges()
	 */
	public boolean hasUnsavedChanges() {
		return fTextFileBuffer != null ? fTextFileBuffer.isDirty() : false;
	}

	/*
	 * @see IBuffer#isClosed()
	 */
	public boolean isClosed() {
		return fDocument == null;
	}

	/*
	 * @see IBuffer#isReadOnly()
	 */
	public boolean isReadOnly() {
		if (fTextFileBuffer != null)
			return !fTextFileBuffer.isCommitable();
		
		IResource resource= getUnderlyingResource();
		if (resource == null)
			return true;
			
		final ResourceAttributes attributes= resource.getResourceAttributes();
		return attributes == null ? false : attributes.isReadOnly();
	}

	/*
	 * @see IBuffer#replace(int, int, char[])
	 */
	public void replace(int position, int length, char[] text) {
		replace(position, length, new String(text));
	}

	/*
	 * @see IBuffer#replace(int, int, String)
	 */
	public void replace(int position, int length, String text) {
		if (DEBUG_LINE_DELIMITERS) {
			validateLineDelimiters(text);
		}
		fReplaceCmd.replace(position, length, text);
	}

	/*
	 * @see IBuffer#save(IProgressMonitor, boolean)
	 */
	public void save(IProgressMonitor progress, boolean force) throws JavaModelException {
		try {
			if (fTextFileBuffer != null)
				fTextFileBuffer.commit(progress, force);
		} catch (CoreException e) {
			throw new JavaModelException(e);
		}
	}

	/*
	 * @see IBuffer#setContents(char[])
	 */
	public void setContents(char[] contents) {
		setContents(new String(contents));
	}

	/*
	 * @see IBuffer#setContents(String)
	 */
	public void setContents(String contents) {
		int oldLength= fDocument.getLength();

		if (contents == null) {

			if (oldLength != 0)
				fSetCmd.set(""); //$NON-NLS-1$

		} else {

			// set only if different
			if (DEBUG_LINE_DELIMITERS) {
				validateLineDelimiters(contents);
			}

			if (!contents.equals(fDocument.get()))
				fSetCmd.set(contents);
		}
	}


	private void validateLineDelimiters(String contents) {

		if (fLegalLineDelimiters == null) {
			// collect all line delimiters in the document
			HashSet<String> existingDelimiters= new HashSet<String>();

			for (int i= fDocument.getNumberOfLines() - 1; i >= 0; i-- ) {
				try {
					String curr= fDocument.getLineDelimiter(i);
					if (curr != null) {
						existingDelimiters.add(curr);
					}
				} catch (BadLocationException e) {
					JavaPlugin.log(e);
				}
			}
			if (existingDelimiters.isEmpty()) {
				return; // first insertion of a line delimiter: no test
			}
			fLegalLineDelimiters= existingDelimiters;

		}

		DefaultLineTracker tracker= new DefaultLineTracker();
		tracker.set(contents);

		int lines= tracker.getNumberOfLines();
		if (lines <= 1)
			return;

		for (int i= 0; i < lines; i++) {
			try {
				String curr= tracker.getLineDelimiter(i);
				if (curr != null && !fLegalLineDelimiters.contains(curr)) {
					StringBuffer buf= new StringBuffer("WARNING: javaeditor.DocumentAdapter added new line delimiter to code: "); //$NON-NLS-1$
					for (int k= 0; k < curr.length(); k++) {
						if (k > 0)
							buf.append(' ');
						buf.append((int)curr.charAt(k));
					}
					IStatus status= new Status(IStatus.WARNING, JavaPlugin.getPluginId(), IStatus.OK, buf.toString(), new Throwable());
					JavaPlugin.log(status);
				}
			} catch (BadLocationException e) {
				JavaPlugin.log(e);
			}
		}
	}

	/*
	 * @see IDocumentListener#documentAboutToBeChanged(DocumentEvent)
	 */
	public void documentAboutToBeChanged(DocumentEvent event) {
		// there is nothing to do here
	}

	/*
	 * @see IDocumentListener#documentChanged(DocumentEvent)
	 */
	public void documentChanged(DocumentEvent event) {
		fireBufferChanged(new BufferChangedEvent(this, event.getOffset(), event.getLength(), event.getText()));
	}

	private void fireBufferChanged(BufferChangedEvent event) {
		if (fBufferListeners != null && fBufferListeners.size() > 0) {
			Iterator<IBufferChangedListener> e= new ArrayList<IBufferChangedListener>(fBufferListeners).iterator();
			while (e.hasNext())
				((IBufferChangedListener) e.next()).bufferChanged(event);
		}
	}
}
