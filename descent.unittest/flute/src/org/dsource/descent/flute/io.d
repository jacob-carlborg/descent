module org.dsource.descent.flute.io;

version(Tango)
{
	
}
else
{
	import std.file : exists, read;
	import std.stdio: writef, fflush, stdout;
}

private IOProvider ioProvider;

public IOProvider getIOProvider()
{
	if(null is ioProvider)
	{
		ioProvider = new PhobosStdioProvider();
	}
	
	return ioProvider;
}

public interface IOProvider
{
	/**
	 * Prints the given string to the output (either a socket or stdout).
	 * 
	 * Params:
	 *     str = The string to print
	 */
	public void write(string str);
	
	/**
	 * Flushes the output stream. Should be called before waiting for user input and
	 * before possibly lengthy operations to alert users.
	 */
	public void flush();

	/**
	 * Reads a line (terminated by a CRLF) from the socket and returns it.
	 * 
	 * Returns: The next line from the input stream
	 */
	public string readln();
	
	/**
	 * Provides a library-independent interface for accessing a file.
	 * It is assumed the file is a smallish UTF-8 text file.
	 * 
	 * Returns: a new handle to the file or null if the file could not be
	 *          found
	 */
	protected FileHandle getFileHandle(string filename);
}

/**
 * Provides a library-independent interface for reading from a smallish UTF-8
 * text file in a line-by-line manner.
 */
private interface FileHandle
{
	/**
	 * Iterates over the contents of a file line-by line.
	 */
	public int opApply(int delegate(ref string) dg);
}

version(Tango)
{
	
}
else
{
	private abstract class PhobosIOProvider : IOProvider
	{
		protected final FileHandle getFileHandle(string filename)
		{
			if(exists(filename))
			{
				return new class(filename) FileHandle
				{
					private string filename;
					
					public this(string filename)
					{
						this.filename = filename;
					}
					
					public int opApply(int delegate(ref string) dg)
					{
						// Read the file
						char[] text = cast(char[]) read(filename);
						
						// Iterate through the lines
						uint lineStart = 0;
						uint i = 0;
						for(; i < test.length; i++)
						{
							char c = text[i];
							if (c == '\n')
							{
								uint lineEnd = i;
								
								// Handle a \r\n
								if(lineEnd && (text[lineEnd - 1] == '\r'))
									lineEnd--;
								
								char[] line = text[lineStart .. lineEnd];
								lineStart = i + 1;
								
								int result = dg(line);
								if(result)
									return result;
							}
						}
						
						// Slurp up the remainder of the text as the final line
						if(lineStart < text.length)
						{
							char[] line = text[lineStart .. i];
							int result = dg(line);
							if(result)
								return result;
						}
						
						return 0;
					}
				};
			}
			else
			{
				return null;
			}
		}
	}
	
	private final class PhobosStdioProvider : PhobosIOProvider
	{	
		public void write(string str)
		{
			writef(str);
		}
		
		public void flush()
		{
			fflush(stdout);
		}

		public string readln()
		{
			return cinReadln();
		}
	}
}