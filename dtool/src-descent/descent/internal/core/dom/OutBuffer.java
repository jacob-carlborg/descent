package descent.internal.core.dom;

public class OutBuffer {
	
	public StringBuilder data;
	
	public OutBuffer() {
		data = new StringBuilder();
	}

	public void reset() {
		data.delete(0, data.length());
	}

	public void writeByte(int b) {
		data.append((char) b);
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

}
