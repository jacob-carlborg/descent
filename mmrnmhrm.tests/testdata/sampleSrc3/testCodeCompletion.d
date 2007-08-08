module testCodeCompletion;

//import std.stdio;
//import std.c.stdio;

class Foo {
	int foovar;
	int foox;
	
	char baz;
}

version(Debug) {
	int fooOfModule;
	version(Debug2) {
		int frak;
	}
}

alias Foo fooalias;
struct foo_t {};
int ix;

class FooBar : Foo {
	intum foobarvar;
	
	void func(int a, int);
	
	void test(int fParam) {
		;// 0 char prefix
		f; // 1 char prefix 
		foo; // 3 char prefix
		
		fo; // 2 char prefix, with common prefix
		
		f; // Also test interactive keyboard events?
		
		Foo ; // Qualified 0 char prefix
		Foo.f; // Qualified 1 char prefix
		
		 ; // Module Qualified 0 char prefix
		.f; // Module Qualified 1 char prefix
		
		Xpto.xptofoo.f; // Module Qualified 
	}
}

class Xpto {
	static Foo xptofoo;
	static FooBar xptofoobar;
} 
