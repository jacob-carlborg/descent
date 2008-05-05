/**
 * Provides a set of (simple) library-independent I/O operations depending on
 * what version is specified. If FluteCommandLine is specified, will provide
 * console stdio, which is useful mainly for testing purposes. Otherwise, will
 * provide socket I/O.
 * 
 * The socket will always bind to the localhost, and will opn a port determined
 * by the contents of the ".fluteio" file (without the quotes) in the current
 * working directory. This file's sole contents should be an ASCII-encoded
 * decimal integer between 1024 and 65535m representing the port flute should
 * open on. If this file does not exist or is in the wrong format, an error will
 * be printed to stdout and the application will exit.
 */
module org.dsource.descent.flute.io;

version(Tango)
{
	
}
else
{
	version(FluteCommandLine)
	{
		import std.stdio : writef, fflush, stdout, cinReadln = readln;
	}
	else
	{
		import std.c.stdlib : exit, EXIT_SUCCESS, EXIT_FAILURE;
		import std.ctype : isdigit;
		import std.file : exists, read;
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
public IOProvider getIOProvider()
{
	return new IOProviderImpl();
}

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

version(Tango)
{
	
}
else
{
	version(FluteCommandLine)
	{
		private final class IOProviderImpl : IOProvider
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
	else
	{
		private final class IOProviderImpl : IOProvider
		{
			Socket serv;
			Stream stream;
			
			public this()
			{
				writef("Port %s\n", getPort());
				exit(EXIT_SUCCESS);
				
				try
				{
					serv = new TcpSocket(AddressFamily.INET);
					serv.bind(new InternetAddress("127.0.0.1", getPort()));
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
			
			private const string CONFIG_FILE_NAME = ".fluteport";
			
			private uint getPort()
			{
				if(!exists(CONFIG_FILE_NAME))
				{
					writef("Couldn't open config file %s\n", CONFIG_FILE_NAME);
					exit(EXIT_FAILURE);
				}
				
				string contents = cast(string) read(CONFIG_FILE_NAME);
				
				uint i = 0;
				while(i < contents.length && isdigit(contents[i]))
					i++;
				
				if(0 == i)
				{
					writef("Invalid flute config file %s\n", CONFIG_FILE_NAME);
					exit(EXIT_FAILURE);
				}
				
				string portStr = contents[0 .. i];
				int portNum = atoi(portStr);
				
				if(portNum < 1024 || portNum > 65535)
				{
					writef("Invalid port %d - Must be in the range 1024 .. 65535\n",
						portNum);
					exit(EXIT_FAILURE);
				}
				
				return portNum;
			}
		}
	}
}