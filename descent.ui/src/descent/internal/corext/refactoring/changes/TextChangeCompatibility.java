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
package descent.internal.corext.refactoring.changes;

import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

import org.eclipse.core.runtime.Assert;

import org.eclipse.ltk.core.refactoring.CategorizedTextEditGroup;
import org.eclipse.ltk.core.refactoring.GroupCategorySet;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextEditChangeGroup;


/**
 * A utility class to provide compatibility with the old
 * text change API of adding text edits directly and auto
 * inserting them into the tree.
 */
public class TextChangeCompatibility {

	public static void addTextEdit(TextChange change, String name, TextEdit edit) {
		Assert.isNotNull(change);
		Assert.isNotNull(name);
		Assert.isNotNull(edit);
		TextEdit root= change.getEdit();
		if (root == null) {
			root= new MultiTextEdit();
			change.setEdit(root);
		}
		insert(root, edit);
		change.addTextEditGroup(new TextEditGroup(name, edit));
	}
	
	public static void addTextEdit(TextChange change, String name, TextEdit edit, GroupCategorySet groupCategories) {
		Assert.isNotNull(change);
		Assert.isNotNull(name);
		Assert.isNotNull(edit);
		TextEdit root= change.getEdit();
		if (root == null) {
			root= new MultiTextEdit();
			change.setEdit(root);
		}
		insert(root, edit);
		change.addTextEditChangeGroup(new TextEditChangeGroup(
			change,
			new CategorizedTextEditGroup(name, edit, groupCategories)));
	}
	
	public static void addTextEdit(TextChange change, String name, TextEdit[] edits) {
		Assert.isNotNull(change);
		Assert.isNotNull(name);
		Assert.isNotNull(edits);
		TextEdit root= change.getEdit();
		if (root == null) {
			root= new MultiTextEdit();
			change.setEdit(root);
		}
		for (int i= 0; i < edits.length; i++) {
			insert(root, edits[i]);
		}
		change.addTextEditGroup(new TextEditGroup(name, edits));
	}
	
	public static void insert(TextEdit parent, TextEdit edit) {
		if (!parent.hasChildren()) {
			parent.addChild(edit);
			return;
		}
		TextEdit[] children= parent.getChildren();
		// First dive down to find the right parent.
		for (int i= 0; i < children.length; i++) {
			TextEdit child= children[i];
			if (covers(child, edit)) {
				insert(child, edit);
				return;
			}
		}
		// We have the right parent. Now check if some of the children have to
		// be moved under the new edit since it is covering it.
		int removed= 0;
		for (int i= 0; i < children.length; i++) {
			TextEdit child= children[i];
			if (covers(edit, child)) {
				parent.removeChild(i - removed++);
				edit.addChild(child);
			}
		}
		parent.addChild(edit);
	}
	
	private static boolean covers(TextEdit thisEdit, TextEdit otherEdit) {
		if (thisEdit.getLength() == 0)	// an insertion point can't cover anything
			return false;
		
		int thisOffset= thisEdit.getOffset();
		int thisEnd= thisEdit.getExclusiveEnd();	
		if (otherEdit.getLength() == 0) {
			int otherOffset= otherEdit.getOffset();
			return thisOffset < otherOffset && otherOffset < thisEnd;
		} else {
			int otherOffset= otherEdit.getOffset();
			int otherEnd= otherEdit.getExclusiveEnd();
			return thisOffset <= otherOffset && otherEnd <= thisEnd;
		}
	}		
	
}
