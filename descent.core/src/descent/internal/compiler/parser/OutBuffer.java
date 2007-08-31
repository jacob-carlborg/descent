package descent.internal.compiler.parser;

public class OutBuffer {
	
	public StringBuilder data;
	
	public OutBuffer() {
		data = new StringBuilder();
	}
	
	public String extractData() {
		String s = data.toString();
		data.setLength(0);
		return s;
	}

	public void reset() {
		data.setLength(0);
	}

	public void writeByte(int b) {
		data.append((char) b);
	}
	
	public void writeByte(char b) {
		data.append(b);
	}
	
	public void printf(String s) {
		data.append(s);
	}
	
	public void writenl() {
		data.append("\n");
	}
	
	public void writebyte(char c) {
		data.append(c);
	}
	
	public void writestring(String s) {
		data.append(s);
	}
	
	public void writestring(char[] s) {
		data.append(s);
	}
	
	public void writeUTF8(int b) {
	    if (b <= 0x7F)
	    {
	    	data.append((char)b);
	    }
	    else if (b <= 0x7FF)
	    {
	    	data.append((char)((b >> 6) | 0xC0));
	    	data.append((char)((b & 0x3F) | 0x80));
	    }
	    else if (b <= 0xFFFF)
	    {
	    	data.append((char)((b >> 12) | 0xE0));
	    	data.append((char)(((b >> 6) & 0x3F) | 0x80));
	    	data.append((char)((b & 0x3F) | 0x80));
	    }
	    else if (b <= 0x1FFFFF)
	    {
	    	data.append((char)((b >> 18) | 0xF0));
	    	data.append((char)(((b >> 12) & 0x3F) | 0x80));
	    	data.append((char)(((b >> 6) & 0x3F) | 0x80));
	    	data.append((char)((b & 0x3F) | 0x80));
	    }
	    else if (b <= 0x3FFFFFF)
	    {
	    	data.append((char)((b >> 24) | 0xF8));
	    	data.append((char)(((b >> 18) & 0x3F) | 0x80));
	    	data.append((char)(((b >> 12) & 0x3F) | 0x80));
	    	data.append((char)(((b >> 6) & 0x3F) | 0x80));
	    	data.append((char)((b & 0x3F) | 0x80));
	    }
	    else if (b <= 0x7FFFFFFF)
	    {
	    	data.append((char)((b >> 30) | 0xFC));
	    	data.append((char)(((b >> 24) & 0x3F) | 0x80));
	    	data.append((char)(((b >> 18) & 0x3F) | 0x80));
	    	data.append((char)(((b >> 12) & 0x3F) | 0x80));
	    	data.append((char)(((b >> 6) & 0x3F) | 0x80));
	    	data.append((char)((b & 0x3F) | 0x80));
	    }
	    else
	    	throw new IllegalStateException("Can't happen");
	}
	
	@Override
	public String toString() {
		return data.toString();
	}

	public void write(OutBuffer argbuf) {
		data.append(argbuf.data);
	}

	public String toChars() {
		return data.toString();
	}

	public void write4(int c) {
		// TODO semantic
	}

	public void writeUTF16(int i) {
		// TODO semantic
	}

	public void prependstring(String string) {
		data.insert(0, string);
	}

	public void prependbyte(char c) {
		data.insert(0, c);
	}

	public void printf(long level) {
		data.append(level);
	}

}
