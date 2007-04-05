package mmrnmhrm.ui.preferences;

import java.util.ArrayList;

import mmrnmhrm.org.eclipse.ui.internal.editors.text.OverlayPreferenceStore;
import mmrnmhrm.org.eclipse.ui.internal.editors.text.OverlayPreferenceStore.OverlayKey;
import mmrnmhrm.ui.text.color.ILangColorPreferences;
import mmrnmhrm.ui.text.color.LangColorPreferences;
import mmrnmhrm.ui.util.SWTDebug;
import mmrnmhrm.ui.util.SimpleSelectionListener;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** 
 * Generic page to configure syntax highlighting/coloring. 
 * 
 * The page consists of a tree viewer coloring item selection, and 
 * a respective color editor (enable, color, bold, italic, underline).
 */
public abstract class LangColoringPreferencePage extends AbstractPreferencePage {

	/** Category: named class to hold a list of ColoringListItem. */
	class ColoringListCategory {
		public String name;
		public ColoringListItem[] items;
		
		public ColoringListCategory(String name, ColoringListItem[] items) {
			this.name = name;
			this.items = items;
		}
		
		public String toString() {
			return name;
		}
	}

	/** A configurable unit of code syntax coloring. */
	class ColoringListItem {
		public String name;
		public String prefKey;
		
		public ColoringListItem(String name, String prefKey) {
			this.name = name;
			this.prefKey = prefKey;
		}
		
		public OverlayKey newOverlayKey(String key) {
			// XXX: I wonder if we can really treat all preference types as STRING
			return new OverlayKey(OverlayPreferenceStore.STRING, key);
		}
		
		public String toString() {
			return name;
		}

		public String getEnableKey() {
			return prefKey + ILangColorPreferences.SUFFIX_ENABLE;
		}

		public String getColorKey() {
			return prefKey + ILangColorPreferences.SUFFIX_COLOR;
		}

		public String getBoldKey() {
			return prefKey + ILangColorPreferences.SUFFIX_BOLD;
		}

		public String getItalicKey() {
			return prefKey + ILangColorPreferences.SUFFIX_ITALIC;
		}

		public String getUnderlineKey() {
			return prefKey + ILangColorPreferences.SUFFIX_UNDERLINE;
		}

		public boolean getIsEnabled() {
			return LangColorPreferences.getIsEnabled(fOverlayPrefStore, prefKey);
		}

		public RGB getColor() {
			return LangColorPreferences.getColor(fOverlayPrefStore, prefKey);
		}

		public boolean getIsBold() {
			return LangColorPreferences.getIsBold(fOverlayPrefStore, prefKey);
		}

		public boolean getIsItalic() {
			return LangColorPreferences.getIsItalic(fOverlayPrefStore, prefKey);
		}

		public boolean getIsUnderline() {
			return LangColorPreferences.getIsUnderline(fOverlayPrefStore, prefKey);
		}

	}
	
	/** Content provider for the coloring items and categories. */
	class DeeColoringContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object inputElement) {
			return catRoot;
		}
		
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ColoringListCategory) {
				ColoringListCategory elem = (ColoringListCategory) parentElement;
				return elem.items;
			}
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return element instanceof ColoringListCategory;
		}


		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	/** Tree viewer for the item selection */
	private TreeViewer fTreeViewer;
	/** Root array holding all coloring categories */
	protected ColoringListCategory[] catRoot;
	
	/** Coloring Editor controls */ 
	private Button fEnableCheckbox;
	private ColorSelector fColorSelector;
	private Label fColorSelectorLabel;
	private Button fBoldCheckBox;
	private Button fItalicCheckBox;
	private Button fUnderlineCheckBox;
	

	/** The plugin to which this page belongs. */
	private AbstractUIPlugin fPlugin;

	/** Creates a coloring preference page with the given title and no image. */
	public LangColoringPreferencePage(String title, AbstractUIPlugin plugin) {
		super(title);
		initColoringItemsList();
		fPlugin = plugin;
		IPreferenceStore parentStore = fPlugin.getPreferenceStore();
		OverlayKey[] overlayKeys = createOverlayKeys();
		fOverlayPrefStore = new OverlayPreferenceStore(parentStore, overlayKeys);
		fOverlayPrefStore.load();
		setPreferenceStore(fOverlayPrefStore);
	}	
	
	private OverlayKey[] createOverlayKeys() {
		ArrayList<OverlayKey> overlayKeys= new ArrayList<OverlayKey>();		
		for(ColoringListCategory listCat : catRoot) {
			for(ColoringListItem listItem : listCat.items) {
				overlayKeys.add(listItem.newOverlayKey(listItem.getEnableKey()));
				overlayKeys.add(listItem.newOverlayKey(listItem.getColorKey()));
				overlayKeys.add(listItem.newOverlayKey(listItem.getBoldKey()));
				overlayKeys.add(listItem.newOverlayKey(listItem.getItalicKey()));
				overlayKeys.add(listItem.newOverlayKey(listItem.getUnderlineKey()));
			}
		}
		
		return overlayKeys.toArray(new OverlayKey[0]);
	}

	/** Initializes the list of coloring items and categories.
	 * Subclasses should implement. */
	protected abstract void initColoringItemsList();
	
	/** Fires when the permanent coloring preferences have changed. */
	protected abstract void fireColoringPreferencesChanged();

	

	/** {@inheritDoc} */
	@Override
	protected Control createContents(final Composite parent) {
		
		Composite content = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout();
	    layout.marginHeight = 0;
	    layout.marginWidth = 0;
	    content.setLayout(layout);
		SWTDebug.setColor(content, SWT.COLOR_DARK_MAGENTA);

	    
	    Label label = new Label(content, SWT.LEFT);
	    label.setText("Element:");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite coloringComposite = new Composite(content, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		coloringComposite.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		coloringComposite.setLayoutData(gd);
		
		fTreeViewer = new TreeViewer(coloringComposite, SWT.SINGLE | SWT.BORDER);
	    fTreeViewer.setLabelProvider(new LabelProvider());
	    fTreeViewer.setContentProvider(new DeeColoringContentProvider());
	    fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleColoringItemSelectionChange();
			}
		});
	    fTreeViewer.setInput(this); // input doesn't matter
	    fTreeViewer.expandAll();
	    
	    gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
	    gd.verticalSpan = 2;
	    gd.widthHint = 150;
	    gd.heightHint = 150;
	    fTreeViewer.getControl().setLayoutData(gd);
	    //treeViewer.getControl().setBounds(0, 0, 500, 500);
	
		fEnableCheckbox = new Button(coloringComposite, SWT.CHECK);
	    fEnableCheckbox.setText("Enable");
	    
		Composite stylesComposite = new Composite(coloringComposite, SWT.NONE);
		stylesComposite.setLayout(new GridLayout());
		gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		gd.horizontalIndent = 10;
		stylesComposite.setLayoutData(gd);
		SWTDebug.setColor(stylesComposite, SWT.COLOR_DARK_GREEN);
	    
		Composite colorChooserComposite = new Composite(stylesComposite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		colorChooserComposite.setLayout(layout);
		
	    fColorSelectorLabel = new Label(colorChooserComposite, SWT.RIGHT);
	    fColorSelectorLabel.setText("Color:");
	    fColorSelector = new ColorSelector(colorChooserComposite);
	
	    fBoldCheckBox = new Button(stylesComposite, SWT.CHECK);
	    fBoldCheckBox.setText("Bold");
	    
	    fItalicCheckBox = new Button(stylesComposite, SWT.CHECK);
	    fItalicCheckBox.setText("Italic");
	    
	    fUnderlineCheckBox = new Button(stylesComposite, SWT.CHECK);
	    fUnderlineCheckBox.setText("Underline");
	
	    
	    label = new Label(content, SWT.LEFT);
	    label.setText("Preview:");
	    
	    StyledText preview = new StyledText(content, SWT.BORDER);
	    preview.setText("TODO");
	    preview.setEditable(false);
	    gd = new GridData(GridData.FILL_BOTH);
	    preview.setLayoutData(gd);
	    
	    enableEditing(false);
	    
    
	    fEnableCheckbox.addSelectionListener(new SimpleSelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ColoringListItem item = getSelectedColoringItem();
				fOverlayPrefStore.setValue(item.getEnableKey(), fEnableCheckbox.getSelection());
				enableEditing(fEnableCheckbox.getSelection());
			}
	    });
	    
	    fColorSelector.getButton().addSelectionListener(new SimpleSelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ColoringListItem item = getSelectedColoringItem();
				PreferenceConverter.setValue(fOverlayPrefStore, item.getColorKey(), fColorSelector.getColorValue());
			}
		});
		fBoldCheckBox.addSelectionListener(new SimpleSelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ColoringListItem item = getSelectedColoringItem();
				fOverlayPrefStore.setValue(item.getBoldKey(), fBoldCheckBox.getSelection());
			}
		});
		fItalicCheckBox.addSelectionListener(new SimpleSelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ColoringListItem item = getSelectedColoringItem();
				fOverlayPrefStore.setValue(item.getItalicKey(), fItalicCheckBox.getSelection());
			}
		});
		fUnderlineCheckBox.addSelectionListener(new SimpleSelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ColoringListItem item = getSelectedColoringItem();
				fOverlayPrefStore.setValue(item.getUnderlineKey(), fUnderlineCheckBox.getSelection());
			}
		});

		return content;
	}

	private ColoringListItem getSelectedColoringItem() {
		IStructuredSelection selection; 
		selection = (IStructuredSelection) fTreeViewer.getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof ColoringListItem)
			return (ColoringListItem) element;
		return null;
	}

	/** Enables or disables the editing controls. */
	private void enableEditing(boolean enable) {
		fEnableCheckbox.setEnabled(enable);
		fColorSelector.getButton().setEnabled(enable);
		fColorSelectorLabel.setEnabled(enable);
		fBoldCheckBox.setEnabled(enable);
		fItalicCheckBox.setEnabled(enable);
		fUnderlineCheckBox.setEnabled(enable);
	}

	private void handleColoringItemSelectionChange() {
		ColoringListItem item = getSelectedColoringItem();
		if (item == null) {
			// A category was selected, disable editing
			enableEditing(false);
			return;
		}
		enableEditing(true);
	
		fEnableCheckbox.setSelection(item.getIsEnabled());
		fColorSelector.setColorValue(item.getColor());
		fBoldCheckBox.setSelection(item.getIsBold());
		fItalicCheckBox.setSelection(item.getIsItalic());
		fUnderlineCheckBox.setSelection(item.getIsUnderline());
	}
	
	public boolean performOk() {
		super.performOk();
		fireColoringPreferencesChanged();
		return true;
	}

}