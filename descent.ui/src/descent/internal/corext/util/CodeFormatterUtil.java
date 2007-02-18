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
package descent.internal.corext.util;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.formatter.DefaultCodeFormatterConstants;
import descent.internal.ui.JavaPlugin;

// TODO JDT UI format: some methods were removed
public class CodeFormatterUtil {
		
	/**
	 * Gets the current tab width.
	 * 
	 * @param project The project where the source is used, used for project
	 *        specific options or <code>null</code> if the project is unknown
	 *        and the workspace default should be used
	 * @return The tab width
	 */
	public static int getTabWidth(IJavaProject project) {
		/*
		 * If the tab-char is SPACE, FORMATTER_INDENTATION_SIZE is not used
		 * by the core formatter.
		 * We piggy back the visual tab length setting in that preference in
		 * that case.
		 */
		String key;
		if (JavaCore.SPACE.equals(getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)))
			key= DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
		else
			key= DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE;
		
		return getCoreOption(project, key, 4);
	}

	/**
	 * Returns the current indent width.
	 * 
	 * @param project the project where the source is used or <code>null</code>
	 *        if the project is unknown and the workspace default should be used
	 * @return the indent width
	 * @since 3.1
	 */
	public static int getIndentWidth(IJavaProject project) {
		String key;
		if (DefaultCodeFormatterConstants.MIXED.equals(getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)))
			key= DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
		else
			key= DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE;
		
		return getCoreOption(project, key, 4);
	}

	/**
	 * Returns the possibly <code>project</code>-specific core preference
	 * defined under <code>key</code>.
	 * 
	 * @param project the project to get the preference from, or
	 *        <code>null</code> to get the global preference
	 * @param key the key of the preference
	 * @return the value of the preference
	 * @since 3.1
	 */
	private static String getCoreOption(IJavaProject project, String key) {
		if (project == null)
			return JavaCore.getOption(key);
		return project.getOption(key, true);
	}

	/**
	 * Returns the possibly <code>project</code>-specific core preference
	 * defined under <code>key</code>, or <code>def</code> if the value is
	 * not a integer.
	 * 
	 * @param project the project to get the preference from, or
	 *        <code>null</code> to get the global preference
	 * @param key the key of the preference
	 * @param def the default value
	 * @return the value of the preference
	 * @since 3.1
	 */
	private static int getCoreOption(IJavaProject project, String key, int def) {
		try {
			return Integer.parseInt(getCoreOption(project, key));
		} catch (NumberFormatException e) {
			return def;
		}
	}
	
	private static String getOldAPICompatibleResult(String string, TextEdit edit, int indentationLevel, int[] positions, String lineSeparator, Map options) {
		Position[] p= null;
		
		if (positions != null) {
			p= new Position[positions.length];
			for (int i= 0; i < positions.length; i++) {
				p[i]= new Position(positions[i], 0);
			}
		}
		String res= evaluateFormatterEdit(string, edit, p);
		
		if (positions != null) {
			for (int i= 0; i < positions.length; i++) {
				Position curr= p[i];
				positions[i]= curr.getOffset();
			}
		}			
		return res;
	}
	
	/**
	 * Evaluates the edit on the given string.
	 * @throws IllegalArgumentException If the positions are not inside the string, a
	 *  IllegalArgumentException is thrown.
	 */
	public static String evaluateFormatterEdit(String string, TextEdit edit, Position[] positions) {
		try {
			Document doc= createDocument(string, positions);
			edit.apply(doc, 0);
			if (positions != null) {
				for (int i= 0; i < positions.length; i++) {
					Assert.isTrue(!positions[i].isDeleted, "Position got deleted"); //$NON-NLS-1$
				}
			}
			return doc.get();
		} catch (BadLocationException e) {
			JavaPlugin.log(e); // bug in the formatter
			Assert.isTrue(false, "Formatter created edits with wrong positions: " + e.getMessage()); //$NON-NLS-1$
		}
		return null;
	}
			
	private static TextEdit shifEdit(TextEdit oldEdit, int diff) {
		TextEdit newEdit;
		if (oldEdit instanceof ReplaceEdit) {
			ReplaceEdit edit= (ReplaceEdit) oldEdit;
			newEdit= new ReplaceEdit(edit.getOffset() - diff, edit.getLength(), edit.getText());
		} else if (oldEdit instanceof InsertEdit) {
			InsertEdit edit= (InsertEdit) oldEdit;
			newEdit= new InsertEdit(edit.getOffset() - diff,  edit.getText());
		} else if (oldEdit instanceof DeleteEdit) {
			DeleteEdit edit= (DeleteEdit) oldEdit;
			newEdit= new DeleteEdit(edit.getOffset() - diff,  edit.getLength());
		} else if (oldEdit instanceof MultiTextEdit) {
			newEdit= new MultiTextEdit();			
		} else {
			return null; // not supported
		}
		TextEdit[] children= oldEdit.getChildren();
		for (int i= 0; i < children.length; i++) {
			TextEdit shifted= shifEdit(children[i], diff);
			if (shifted != null) {
				newEdit.addChild(shifted);
			}
		}
		return newEdit;
	}
		
	private static Document createDocument(String string, Position[] positions) throws IllegalArgumentException {
		Document doc= new Document(string);
		try {
			if (positions != null) {
				final String POS_CATEGORY= "myCategory"; //$NON-NLS-1$
				
				doc.addPositionCategory(POS_CATEGORY);
				doc.addPositionUpdater(new DefaultPositionUpdater(POS_CATEGORY) {
					protected boolean notDeleted() {
						if (fOffset < fPosition.offset && (fPosition.offset + fPosition.length < fOffset + fLength)) {
							fPosition.offset= fOffset + fLength; // deleted positions: set to end of remove
							return false;
						}
						return true;
					}
				});
				for (int i= 0; i < positions.length; i++) {
					try {
						doc.addPosition(POS_CATEGORY, positions[i]);
					} catch (BadLocationException e) {
						throw new IllegalArgumentException("Position outside of string. offset: " + positions[i].offset + ", length: " + positions[i].length + ", string size: " + string.length());   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					}
				}
			}
		} catch (BadPositionCategoryException cannotHappen) {
			// can not happen: category is correctly set up
		}
		return doc;
	}
	
}
