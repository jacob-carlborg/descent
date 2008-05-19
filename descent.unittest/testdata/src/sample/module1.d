module sample.module1;

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
		throw new Exception("YA RLY!");
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
	throwAnException();
}

void throwAnException()
{
	// Another couple lines to test line info
	
	
	
	doTheThrowing();
}

void doTheThrowing()
{
	// More blank lines
	
	
	
	throw new Exception("NO WAI!");
}

int main(char[][] args)
{
	assert(false, "Main reached :-(");
}