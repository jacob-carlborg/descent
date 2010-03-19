package com.hexapixel.framework.glazed.glazednatgridviewer;

import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.eclipse.swt.SWT;

import com.hexapixel.framework.glazed.griddata.IDataObject;

public abstract class AbstractDataComparator implements Comparator<IDataObject> {

    private int        _col;
    private int        _dir;

    private DateFormat _df;

    public AbstractDataComparator(int col, int dir) {
        _col = col;
        _dir = dir;
        _df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    }

    @Override
    public int compare(IDataObject o1, IDataObject o2) {
        String str1 = o1.getColumnText(_col);
        String str2 = o2.getColumnText(_col);

        return doDefaultComparison(str1, str2);
    }

    /**
     * This method will try to do a comparison based on the data in the cell.<br><br>
     * - It starts with Double<br>
     * - Continues with Date<br>
     * - Finishes with String
     * 
     * @param str String to compare
     * @param str2 Other string to compare
     * @return comparison value. returns zero if any string is null.
     */
    public int doDefaultComparison(String str, String str2) {
        if (str == null || str2 == null) return 0;
                
        int ret = 0;

        try {
            Double d1 = Double.valueOf(str);
            Double d2 = Double.valueOf(str2);

            ret = d1.compareTo(d2);
        } catch (Exception err) { 
            // try date parsing
            try {
                Date d1 = _df.parse(str);
                Date d2 = _df.parse(str2);
                ret = d1.compareTo(d2);
            }
            catch (Exception err2) {
                ret = str.compareTo(str2);
            }
        }

        if (_dir == SWT.DOWN) ret = -ret;

        return ret;
    }

}
