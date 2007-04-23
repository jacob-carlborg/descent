package mmrnmhrm.ui.preferences;

import java.util.ArrayList;

import mmrnmhrm.org.eclipse.ui.internal.editors.text.OverlayPreferenceStore;
import mmrnmhrm.org.eclipse.ui.internal.editors.text.OverlayPreferenceStore.OverlayKey;
import mmrnmhrm.ui.text.color.ILangColorPreferences;
import mmrnmhrm.ui.text.color.LangColorPreferences;
import mmrnmhrm.ui.util.ColumnComposite;
import mmrnmhrm.ui.util.DialogComposite;
import mmrnmhrm.ui.util.EmptyLabel;
import mmrnmhrm.ui.util.RowComposite;
import mmrnmhrm.ui.util.SimpleSelectionListener;
import mmrnmhrm.ui.util.fields.ItemSelectionListField;
import mmrnmhrm.ui.util.fields.ItemSelectionListField.SelectionListCategory;
import mmrnmhrm.ui.util.fields.ItemSelectionListField.SelectionListItem;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/** 
 * Generic page to configure syntax highlighting/coloring. 
 * 
 * The page consists of a tree viewer coloring item selection, and 
 * a respective color editor (enable, color, bold, italic, underline).
 * 
 */
public abstract class LangColoringPreferencePage extends AbstractPreferencePage {


	/** A unit of code coloring configuration. */
	class ColoringListItem extends ItemSelectionListField.SelectionListItem {
		public String prefKey;
		
		public ColoringListItem(String name, String prefKey) {
			super(name);
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
		
	protected ColoringListItem createSelectionItem(String string, String prefKey) {
		return new ColoringListItem(string, prefKey);
	}
	

	/** Coloring item selection list */
	private ItemSelectionListField fItemSelectionList;
	/** Root array holding all coloring categories */
	protected SelectionListCategory[] catRoot;
	
	/** Coloring Editor controls */ 
	private Button fEnableCheckbox;
	private Composite editComposite;
	private ColorSelector fColorSelector;
	private Label fColorSelectorLabel;
	private Button fBoldCheckBox;
	private Button fItalicCheckBox;
	private Button fUnderlineCheckBox;
	private DialogComposite styleComposite;
	
	
	/** Creates a coloring preference page with the given title and no image. */
	public LangColoringPreferencePage(String title) {
		super(title);
		initColoringItemsList();
		fOverlayPrefStore = new OverlayPreferenceStore(
				getPreferenceStore(), createOverlayKeys());
		fOverlayPrefStore.load();
	}	
	
	
	private OverlayKey[] createOverlayKeys() {
		ArrayList<OverlayKey> overlayKeys= new ArrayList<OverlayKey>();		
		for(SelectionListCategory listCat : catRoot) {
			for(SelectionListItem listSelItem : listCat.items) {
				ColoringListItem listItem = (ColoringListItem) listSelItem;
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
	

	/** {@inheritDoc} */
	@Override
	protected Control createContents(final Composite parent) {
		
		Composite content = new RowComposite(parent);
	    
		new EmptyLabel(content);
		Composite coloringComposite = new ColumnComposite(content, 2);

		fItemSelectionList = new ItemSelectionListField(catRoot);
		fItemSelectionList.setLabelText("Element:");
		fItemSelectionList.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				handleColoringItemSelectionChange();
			}
		});
		fItemSelectionList.doFillWithoutGrid(coloringComposite);
		
		LayoutUtil.setWidthHint(fItemSelectionList.getTreeControl(null), 150);	
		LayoutUtil.setHeightHint(fItemSelectionList.getTreeControl(null), 150);	


		editComposite = new RowComposite(coloringComposite);
		editComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		new EmptyLabel(editComposite);
		fEnableCheckbox = new Button(editComposite, SWT.CHECK);
	    fEnableCheckbox.setText("Enable");
    
   
		styleComposite = new RowComposite(editComposite);
		LayoutUtil.setHorizontalIndent(styleComposite, 15);
	    
		Composite colorChooserComposite = new ColumnComposite(styleComposite, 2);
	    fColorSelectorLabel = new Label(colorChooserComposite, SWT.RIGHT);
	    fColorSelectorLabel.setText("Color:");
	    fColorSelector = new ColorSelector(colorChooserComposite);
	
	    fBoldCheckBox = new Button(styleComposite, SWT.CHECK);
	    fBoldCheckBox.setText("Bold");
	    
	    fItalicCheckBox = new Button(styleComposite, SWT.CHECK);
	    fItalicCheckBox.setText("Italic");
	    
	    fUnderlineCheckBox = new Button(styleComposite, SWT.CHECK);
	    fUnderlineCheckBox.setText("Underline");
	
	    enableEditing(false);
	    
	    // controllers
	    fEnableCheckbox.addSelectionListener(new SimpleSelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ColoringListItem item = getSelectedColoringItem();
				fOverlayPrefStore.setValue(item.getEnableKey(), fEnableCheckbox.getSelection());
				enableStyleEditing(fEnableCheckbox.getSelection());
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
		

		// ---  the code previewer  --- 
	    Label label = new Label(content, SWT.LEFT);
	    label.setText("Preview:");
	    
	    StyledText preview = new StyledText(content, SWT.BORDER);
	    preview.setText("TODO");
	    preview.setEditable(false);
	    preview.setLayoutData(new GridData(GridData.FILL_BOTH));

		return content;
	}


	private ColoringListItem getSelectedColoringItem() {
		Object element = fItemSelectionList.getSelectedItem();
		if (element instanceof ColoringListItem)
			return (ColoringListItem) element;
		return null;
	}

	/** Enables or disables color editing. */
	private void enableEditing(boolean enable) {
		fEnableCheckbox.setEnabled(enable);
		enableStyleEditing(enable && fEnableCheckbox.getSelection());
	}
	
	/** Enables or disables the color style editing. */
	private void enableStyleEditing(boolean enable) {
		styleComposite.recursiveSetEnabled(enable);
	}

	private void handleColoringItemSelectionChange() {
		ColoringListItem item = getSelectedColoringItem();
		if (item == null) {
			// A category was selected, disable editing
			enableEditing(false);
			return;
		}
	
		fEnableCheckbox.setSelection(item.getIsEnabled());
		fColorSelector.setColorValue(item.getColor());
		fBoldCheckBox.setSelection(item.getIsBold());
		fItalicCheckBox.setSelection(item.getIsItalic());
		fUnderlineCheckBox.setSelection(item.getIsUnderline());

		enableEditing(true);
	}
	
}