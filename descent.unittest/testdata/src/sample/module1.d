module sample.module1;

import std.socket : SocketException;
import std.thread : Thread;

class ArgumentException : Exception
{
	this(char[] msg)
	{
		super(msg);
	}
}

unittest
{
	// PASS
}

class Bar
{
	unittest
	{
		assert(false, "O RLY?");
	}
	
	unittest
	{
		// Some blank lines
		
		
		
		// To test stack tracing
		throw new SocketException("YA RLY!", 69);
	}
	
	unittest
	{
		// Take a REALLY long time
		for(int i = 0; i < 1000000000; i++)
		{
			// Wait
		}
	}
}

unittest
{
	// Some more blank lines
	
	
	
	
	// To test assertion failure line/file
	assert(false);
}

unittest
{
	throw new ArgumentException("NO WAI!");
}

int main(char[][] args)
{
	assert(false, "Main reached :-(");
}