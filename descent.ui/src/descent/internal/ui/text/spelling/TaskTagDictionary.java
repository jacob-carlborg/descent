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

package descent.internal.ui.text.spelling;

import java.net.URL;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;

import descent.core.JavaCore;

import descent.internal.ui.text.spelling.engine.AbstractSpellDictionary;

/**
 * Dictionary for task tags.
 *
 * @since 3.0
 */
public class TaskTagDictionary extends AbstractSpellDictionary implements IPropertyChangeListener {

	/*
	 * @see descent.internal.ui.text.spelling.engine.AbstractSpellDictionary#getName()
	 */
	protected final URL getURL() {
		return null;
	}

	/*
	 * @see descent.ui.text.spelling.engine.AbstractSpellDictionary#load(java.net.URL)
	 */
	protected boolean load(final URL url) {

		final Plugin plugin= JavaCore.getPlugin();
		if (plugin != null) {

			plugin.getPluginPreferences().addPropertyChangeListener(this);
			return updateTaskTags();
		}
		return false;
	}

	/*
	 * @see org.eclipse.core.runtime.Preferences.IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public void propertyChange(final PropertyChangeEvent event) {

		if (JavaCore.COMPILER_TASK_TAGS.equals(event.getProperty()))
			updateTaskTags();
	}

	/*
	 * @see descent.ui.text.spelling.engine.ISpellDictionary#unload()
	 */
	public void unload() {

		final Plugin plugin= JavaCore.getPlugin();
		if (plugin != null)
			plugin.getPluginPreferences().removePropertyChangeListener(this);

		super.unload();
	}

	/**
	 * Handles the compiler task tags property change event.
	 */
	protected boolean updateTaskTags() {

		final String tags= JavaCore.getOption(JavaCore.COMPILER_TASK_TAGS);
		if (tags != null) {

			unload();

			final StringTokenizer tokenizer= new StringTokenizer(tags, ","); //$NON-NLS-1$
			while (tokenizer.hasMoreTokens())
				hashWord(tokenizer.nextToken());

			return true;
		}
		return false;
	}
}
