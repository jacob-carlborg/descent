package com.hexapixel.framework.glazed.griddata;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Sample data object interface for use in table.
 *
 */
public interface IDataObject {

    /**
     * Returns the background color for an object at a certain column index.
     * 
     * @param index Column index
     * @param even if Row is even or odd
     * @return Color or null
     */
	public Color getBackground(int index, boolean even);
	
	/**
	 * Returns the foreground color for an object at a certain column index.
	 * 
	 * @param index Column index
     * @param even if Row is even or odd
	 * @return Color or null
	 */
	public Color getForeground(int index, boolean even);	
	
	/**
	 * Returns an image for a certain column index.
	 * 
	 * @param index Column index
	 * @return Image or null
	 */
	public Image getColumnImage(int index);
	
	/**
	 * Returns the text to be displayed at a certain column index. Return value may not be null.
	 * 
	 * @param index Column index
	 * @return Text, never null
	 */
	public String getColumnText(int index);
	
	/**
	 * Returns the font to be used for a given column index.
	 * 
	 * @param index Column index
	 * @return Font or null
	 */
	public Font getFont(int index);
		
	/**
	 * Text to be used as a "match" when a search is done on this object.
	 * 
	 * @param column Column index
	 * @return String or null
	 */
	public String getSearchMatchText(int column);

	/**
	 * Text to be used to match filters, usually all columns text combined together.
	 * 
	 * @return String
	 */
	public String getFilterMatchText();
	
	 /**
     * Returns the foreground highlight color for a column. This method is only called if the object
     * is currently set to be highlighted.
     * 
     * @param column Column index
     * @return Color or null
     */
	public Color getHighlightForegroundColor(int column);
	
	/**
	 * Returns the background highlight color for a column. This method is only called if the object
	 * is currently set to be highlighted.
	 * 
	 * @param column Column index
	 * @return Color or null
	 */
	public Color getHighlightBackgroundColor(int column);

}
