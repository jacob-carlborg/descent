package descent.internal.compiler.parser;

public final class StringTableHolder implements IStringTableHolder {
	
	private StringTable stringTable;

	public StringTable getStringTable() {
		if (stringTable == null) {
			stringTable = new StringTable();
		}
		return stringTable;
	}

}
