/**
 * Provides a set of (simple) library-independent I/O operations depending on
 * what version is specified. If FluteCommandLine is specified, will provide
 * console stdio, which is useful mainly for testing purposes. Otherwise, will
 * provide socket I/O.
 * 
 * Also provides the means for getting the configuration file and exports a
 * library-independent "find" function.
 */
module org.dsource.descent.flute.io;

version(Tango)
{
	
}
else
{
	public import std.string : find;
	
	import std.c.stdlib : exit, EXIT_SUCCESS, EXIT_FAILURE;
	import std.file : exists, read;

	version(FluteCommandLine)
	{
		import std.stdio : writef, fflush, stdout, cinReadln = readln;
	}
	else
	{
		import std.ctype : isdigit;
		import std.socket : Socket, TcpSocket, AddressFamily, InternetAddress,
			SocketShutdown, SocketException;
		import std.socketstream : SocketStream;
		import std.stdio: writef;
		import std.stream : Stream;
		import std.string : atoi;
	}
}

/**
 * Gets a new instance of IO provider. This instance should be explicitly
 * deleted when done. This should only be called once (since trying to open a
 * socket on the same port more than once will obviously not work too well).
 */
public IOProvider getIOProvider(string[string] config)
{
	return new IOProviderImpl(config);
}

/**
 * Finds needle in the hasytack or returns -1 on failure (Tango's find returns
 * the length of the string while Phobos's returns -1. The function exported
 * from this module acts as Phobos's).
 */
// uint find(string haystack, string needle)

/**
 * Reads the configuaration file. The file should be a file in the working
 * diretcory called ".fluteconfig" and have a set of keys and values on the
 * same line separated by an =, with newlines separating the pairs, like so:
 * 
 * port=30587
 * stacktrace=on
 * 
 * There must be a newline after the last pair.
 */
// string[string] readConfig()

/**
 * The interface for issuing I/O commands. This is an abstract class instead of
 * an interface to get around the limitation in D that interfaces can't be
 * deleted.
 */
public abstract class IOProvider
{
	/**
	 * Prints the given string to the output (either a socket or stdout).
	 * 
	 * Params:
	 *     str = The string to print
	 */
	public abstract void write(string str);
	
	/**
	 * Flushes the output stream. Should be called before waiting for user input
	 * and before possibly lengthy operations to alert users.
	 */
	public abstract void flush();

	/**
	 * Reads a line (terminated by a CRLF) from the socket and returns it.
	 * 
	 * Returns: The next line from the input stream
	 */
	public abstract string readln();
}

private const string CONFIG_FILE_NAME = ".fluteconfig";
private const string PORT_ATTR = "port";

version(Tango)
{
	
}
else
{
	string[string] readConfig()
	{
		if(!exists(CONFIG_FILE_NAME))
		{
			writef("Couldn't open config file %s\n", CONFIG_FILE_NAME);
			exit(EXIT_FAILURE);
		}
		
		string contents = cast(string) read(CONFIG_FILE_NAME);
		return parseConfigFile(contents);
	}
	
	version(FluteCommandLine)
	{
		private final class IOProviderImpl : IOProvider
		{
			private this(string[string] config)
			{
				// Nothing to do
			}
			
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
	else
	{
		private final class IOProviderImpl : IOProvider
		{
			Socket serv;
			Stream stream;
			
			public this(string[string] config)
			{	
				try
				{
					string* portStr = PORT_ATTR in config;
					if(!portStr)
					{
						writef("No port defined in config file\n");
						exit(EXIT_FAILURE);
					}
					uint port = atoi(*portStr);
					
					serv = new TcpSocket(AddressFamily.INET);
					serv.bind(new InternetAddress("127.0.0.1", port));
					serv.listen(0);
					Socket conn = serv.accept();
					stream = new SocketStream(conn);
				}
				catch(SocketException se)
				{
					writef("Couldn't create socket; error code %d\n", 
						se.errorCode);
					exit(se.errorCode);
				}
			}
			
			public ~this()
			{
				try
				{
					stream.close();
					serv.shutdown(SocketShutdown.BOTH);
					serv.close();
				}
				catch(Exception e)
				{
					// Ignore
				}
			}
			
			public void write(string str)
			{
				stream.writeString(str);
			}
			
			public void flush()
			{
				stream.flush();
			}
			
			public string readln()
			{
				return stream.readLine();
			}
		}
	}
}

/**
 * Process the contents of the config file.
 * 
 * Params:
 *     content = the contents of the config file
 * Returns: the configuration data as a string -> string hash
 */
private string[string] parseConfigFile(string content)
{
	// This function splits the line and adds it to the config
	string[string] config;
	void processLine(string line)
	{
		int pos = find(line, '=');
		if(pos <= 0 || pos + 1 == line.length)
			return;
		
		string key = line[0 .. pos];
		string value = line[pos + 1 .. $];
		config[key]= value;
	}
	
	// Enumerate through the lines, passing them to processLine
	uint start = 0;
	foreach (i, c; content)
	{
		if (c == '\n')
		{
			uint end = i;
			if(end && (content[end - 1] == '\r'))
				--end;
			processLine(content[start .. end]);
			start = i + 1;
		}
	}
	
	return config;
}
