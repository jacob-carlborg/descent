package mmrnmhrm.tests.adapters;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;

public class Mock_Document implements IDocument {

	public void addDocumentListener(IDocumentListener listener) {
	}

	public void addDocumentPartitioningListener(
			IDocumentPartitioningListener listener) {
		throw new UnsupportedOperationException();
	}

	public void addPosition(Position position) throws BadLocationException {
		throw new UnsupportedOperationException();

	}

	public void addPosition(String category, Position position)
			throws BadLocationException, BadPositionCategoryException {
		throw new UnsupportedOperationException();

	}

	public void addPositionCategory(String category) {
		throw new UnsupportedOperationException();

	}

	public void addPositionUpdater(IPositionUpdater updater) {
		throw new UnsupportedOperationException();

	}

	public void addPrenotifiedDocumentListener(IDocumentListener documentAdapter) {
		throw new UnsupportedOperationException();

	}

	public int computeIndexInCategory(String category, int offset)
			throws BadLocationException, BadPositionCategoryException {
		throw new UnsupportedOperationException();
		
	}

	public int computeNumberOfLines(String text) {
		throw new UnsupportedOperationException();
		
	}

	public ITypedRegion[] computePartitioning(int offset, int length)
			throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public boolean containsPosition(String category, int offset, int length) {
		throw new UnsupportedOperationException();
	}

	public boolean containsPositionCategory(String category) {
		throw new UnsupportedOperationException();
	}

	public String get() {
		throw new UnsupportedOperationException();
		
	}

	public String get(int offset, int length) throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public char getChar(int offset) throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public String getContentType(int offset) throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public IDocumentPartitioner getDocumentPartitioner() {
		throw new UnsupportedOperationException();
		
	}

	public String[] getLegalContentTypes() {
		throw new UnsupportedOperationException();
		
	}

	public String[] getLegalLineDelimiters() {
		throw new UnsupportedOperationException();
		
	}

	public int getLength() {
		throw new UnsupportedOperationException();
		
	}

	public String getLineDelimiter(int line) throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public IRegion getLineInformation(int line) throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public IRegion getLineInformationOfOffset(int offset)
			throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public int getLineLength(int line) throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public int getLineOfOffset(int offset) throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public int getLineOffset(int line) throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public int getNumberOfLines() {
		throw new UnsupportedOperationException();
		
	}

	public int getNumberOfLines(int offset, int length)
			throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public ITypedRegion getPartition(int offset) throws BadLocationException {
		throw new UnsupportedOperationException();
		
	}

	public String[] getPositionCategories() {
		throw new UnsupportedOperationException();
		
	}

	public IPositionUpdater[] getPositionUpdaters() {
		throw new UnsupportedOperationException();
		
	}

	public Position[] getPositions(String category)
			throws BadPositionCategoryException {
		throw new UnsupportedOperationException();
		
	}

	public void insertPositionUpdater(IPositionUpdater updater, int index) {
		throw new UnsupportedOperationException();

	}

	public void removeDocumentListener(IDocumentListener listener) {
		throw new UnsupportedOperationException();

	}

	public void removeDocumentPartitioningListener(
			IDocumentPartitioningListener listener) {
		throw new UnsupportedOperationException();

	}

	public void removePosition(Position position) {
		throw new UnsupportedOperationException();

	}

	public void removePosition(String category, Position position)
			throws BadPositionCategoryException {
		throw new UnsupportedOperationException();

	}

	public void removePositionCategory(String category)
			throws BadPositionCategoryException {
		throw new UnsupportedOperationException();

	}

	public void removePositionUpdater(IPositionUpdater updater) {
		throw new UnsupportedOperationException();

	}

	public void removePrenotifiedDocumentListener(
			IDocumentListener documentAdapter) {
		throw new UnsupportedOperationException();

	}

	public void replace(int offset, int length, String text)
			throws BadLocationException {
		throw new UnsupportedOperationException();

	}

	public int search(int startOffset, String findString,
			boolean forwardSearch, boolean caseSensitive, boolean wholeWord)
			throws BadLocationException {
		throw new UnsupportedOperationException();
	}

	public void set(String text) {
		throw new UnsupportedOperationException();

	}

	public void setDocumentPartitioner(IDocumentPartitioner partitioner) {
		throw new UnsupportedOperationException();

	}

}
