package com.hexapixel.framework.glazed.griddata;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * One DataFilter is one Filter to be used in a TableViewer This object is
 * Cloneable.
 */
public class DataFilter extends ViewerFilter {

	private String _name;
	private boolean _wholeWordsMatch;
	private boolean _caseSensitive;
	private boolean _regex;

	private String _searchString;
	private String _uuid;

	/**
	 * Creates an empty DataFilter.
	 */
	public DataFilter() {
		init();
	}

	/**
	 * Creates a new DataFilter.
	 * 
	 * @param name
	 *            Human-readable name
	 * @param wholeWordsMatch
	 *            set to true when this filter is a whole word match
	 * @param caseSensitive
	 *            set to true if case sensitive match
	 * @param regex
	 *            set to true if this search string is a regular expression
	 * @param searchString
	 *            search string to match against text
	 */
	public DataFilter(String name, boolean wholeWordsMatch,
			boolean caseSensitive, boolean regex, String searchString) {
		init();
		this._name = name;
		this._wholeWordsMatch = wholeWordsMatch;
		this._caseSensitive = caseSensitive;
		this._regex = regex;
		this._searchString = searchString;
	}

	private void init() {
		_uuid = UUID.randomUUID().toString();
	}

	public void setId(String id) {
		if (id == null)
			return;

		_uuid = id;
	}

	public String getId() {
		return _uuid;
	}

	public boolean isWholeWordsMatch() {
		return _wholeWordsMatch;
	}

	public void setWholeWordsMatch(boolean wholeWordsMatch) {
		this._wholeWordsMatch = wholeWordsMatch;
	}

	public boolean isCaseSensitive() {
		return _caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this._caseSensitive = caseSensitive;
	}

	public boolean isRegex() {
		return _regex;
	}

	public void setRegex(boolean regex) {
		this._regex = regex;
	}

	public String getSearchString() {
		return _searchString;
	}

	public void setSearchString(String searchString) {
		this._searchString = searchString;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	/**
	 * Tests a regular expression pattern. If it is not a valid pattern an
	 * exception will be thrown, otherwise the method returns true.
	 * 
	 * @param regex
	 *            Regular expression
	 * @return true if matches
	 * @throws PatternSyntaxException
	 *             if pattern is invalid
	 */
	public boolean testRegex(String regex) throws PatternSyntaxException {
		if (regex == null || regex.length() == 0)
			return true;

		// will throw exception
		Pattern.compile(regex);

		return true;
	}

	/**
	 * Checks whether a given string matches whatever is set as the searchString
	 * on the class.
	 * 
	 * @param str
	 *            String to match
	 * @return true if contains search, false if not
	 * @throws PatternSyntaxException
	 *             if Regular Expression pattern is invalid
	 * @throws Exception
	 */
	public boolean matches(String str) throws PatternSyntaxException, Exception {
		// both null, return true
		if (str == null && _searchString == null)
			return true;

		// one or the other null, return false
		if (str == null || _searchString == null)
			return false;

		// both empty, return true
		if (str.length() == 0 && _searchString.length() == 0)
			return true;

		String stringToFind = _searchString;

		// if non-case sensitive, lowercase it all
		if (!_caseSensitive) {
			stringToFind = stringToFind.toLowerCase();
			str = str.toLowerCase();
		}

		// if it's a simple search, do it by hand
		if (!_wholeWordsMatch && !_regex)
			return (str.indexOf(stringToFind) > -1);

		// whole word match, create a regex from it
		else if (_wholeWordsMatch && !_regex) {		    
			Pattern p = Pattern.compile("\\b" + _searchString + "\\b", _caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(str);
			return m.find();
		}

		if (_regex) {
			// whole words doesn't really make sense for regex
			Pattern p = Pattern.compile(stringToFind, _caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(str);
			return m.find();
		}

		return false;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		try {
			if (element instanceof IDataObject) {
				IDataObject dobj = (IDataObject) element;

				return (matches(dobj.getFilterMatchText()));
			}

		} catch (Exception err) {
			err.printStackTrace();
		}

		return false;
	}

	public String toString() {
		return "[DataFilter: " + _searchString + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new DataFilter(getName(), isWholeWordsMatch(),
				isCaseSensitive(), isRegex(), getSearchString());
	}

	public String getFlagsString() {
		List<String> ret = new ArrayList<String>();
		if (_wholeWordsMatch)
			ret.add("Whole Word");
		if (_caseSensitive)
			ret.add("Case Sensitive");
		if (_regex)
			ret.add("Regex");

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < ret.size(); i++) {
			buf.append(ret.get(i));
			if (i != ret.size() - 1)
				buf.append(", ");
		}

		return buf.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_uuid == null) ? 0 : _uuid.hashCode());
		return result;
	}

	@Override
	/*
	 * We check equals against the UUID of this class as if the name changes and
	 * a filter is applied, it would not be detected as active due to the name
	 * change. But a filter will always equal another filter if the unique ID is
	 * the same.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataFilter other = (DataFilter) obj;
		if (_uuid == null) {
			if (other._uuid != null)
				return false;
		} else if (!_uuid.equals(other._uuid))
			return false;
		return true;
	}

}
