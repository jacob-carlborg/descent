/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Aaron Luchko, aluchko@redhat.com - 105926 [Formatter] Exporting Unnamed profile fails silently
 *******************************************************************************/
package descent.internal.ui.preferences.formatter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.preferences.IScopeContext;

import org.eclipse.core.resources.IProject;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import descent.ui.JavaUI;

import descent.internal.ui.preferences.PreferencesAccess;
import descent.internal.ui.preferences.formatter.ProfileManager.Profile;



/**
 * The code formatter preference page. 
 */

public class CodeFormatterConfigurationBlock extends ProfileConfigurationBlock {
    
    private static final String FORMATTER_DIALOG_PREFERENCE_KEY= "formatter_page"; //$NON-NLS-1$

	private static final String DIALOGSTORE_LASTSAVELOADPATH= JavaUI.ID_PLUGIN + ".codeformatter"; //$NON-NLS-1$
    
	/**
     * Some D source code used for preview.
     */
    protected final String PREVIEW= "module HelloWorld;import std.stdio;void main(char[][] args){writefln(\"Hello World, Reloaded\");foreach(argc,argv;args){" + //$NON-NLS-1$
    		"CmdLin cl=new CmdLin(argc,argv);writefln(cl.argnum,cl.suffix,\" arg: %s\",cl.argv);delete cl;}" + //$NON-NLS-1$
    		"struct specs{int count,allocated;}specs argspecs(){specs* s=new specs;s.count=args.length;" + //$NON-NLS-1$
    		"s.allocated=typeof(args).sizeof;foreach(argv;args)s.allocated+=argv.length*typeof(argv[0]).sizeof;return *s;}" + //$NON-NLS-1$
    		"writefln(\"argc = %d, \"~\"allocated = %d\",argspecs().count,argspecs().allocated);}class CmdLin{private int _argc;" + //$NON-NLS-1$
    		"private char[] _argv;public:this(int argc,char[] argv){_argc=argc;_argv=argv;}int argnum(){return _argc+1;}" + //$NON-NLS-1$
    		"char[] argv(){return _argv;}char[] suffix(){char[] suffix=\"th\";switch(_argc){case 0:suffix=\"st\";break;case 1:" + //$NON-NLS-1$
    		"suffix=\"nd\";break;case 2:suffix=\"rd\";break;default:break;}return suffix;}}"; //$NON-NLS-1$ */
    
	private class PreviewController implements Observer {

		public PreviewController(ProfileManager profileManager) {
			profileManager.addObserver(this);
			fJavaPreview.setWorkingValues(profileManager.getSelected().getSettings());
			fJavaPreview.update();
		}

		public void update(Observable o, Object arg) {
			final int value= ((Integer)arg).intValue();
			switch (value) {
			case ProfileManager.PROFILE_CREATED_EVENT:
			case ProfileManager.PROFILE_DELETED_EVENT:
			case ProfileManager.SELECTION_CHANGED_EVENT:
			case ProfileManager.SETTINGS_CHANGED_EVENT:
				fJavaPreview.setWorkingValues(((ProfileManager)o).getSelected().getSettings());
				fJavaPreview.update();
			}
		}

	}

    
    /**
	 * The JavaPreview.
	 */
	private JavaPreview fJavaPreview;

	/**
	 * Create a new <code>CodeFormatterConfigurationBlock</code>.
	 */
	public CodeFormatterConfigurationBlock(IProject project, PreferencesAccess access) {
		super(project, access, DIALOGSTORE_LASTSAVELOADPATH);
	}

	protected IProfileVersioner createProfileVersioner() {
	    return new ProfileVersioner();
    }
	
	protected ProfileStore createProfileStore(IProfileVersioner versioner) {
	    return new FormatterProfileStore(versioner);
    }
	
	protected ProfileManager createProfileManager(List profiles, IScopeContext context, PreferencesAccess access, IProfileVersioner profileVersioner) {
	    return new FormatterProfileManager(profiles, context, access, profileVersioner);
    }
	
	protected void configurePreview(Composite composite, int numColumns, ProfileManager profileManager) {
		createLabel(composite, FormatterMessages.CodingStyleConfigurationBlock_preview_label_text, numColumns);
		CompilationUnitPreview result= new CompilationUnitPreview(profileManager.getSelected().getSettings(), composite);
        result.setPreviewText(PREVIEW);
		fJavaPreview= result;

		final GridData gd = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = numColumns;
		gd.verticalSpan= 7;
		gd.widthHint = 0;
		gd.heightHint = 0;
		fJavaPreview.getControl().setLayoutData(gd);
		
		new PreviewController(profileManager);
	}

    
    protected ModifyDialog createModifyDialog(Shell shell, Profile profile, ProfileManager profileManager, ProfileStore profileStore, boolean newProfile) {
        return new FormatterModifyDialog(shell, profile, profileManager, profileStore, newProfile, FORMATTER_DIALOG_PREFERENCE_KEY, DIALOGSTORE_LASTSAVELOADPATH);
    }
}
