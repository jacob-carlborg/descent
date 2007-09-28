module testCodeCompletion2;

//import std.stdio;
//import std.c.stdio;

class Foo {
	int foovar;
	int foox;
	
	char baz;
}

int fooOfModule;
struct foo_t {};
int ix;

class FooBar : Foo {
	intum foobarvar;
	
	void func(int a, int);

	void test1() {
		f // non qualified ; recovery
	}
	
	void test2() {
		Foo.f // qualified ; recovery
	}
	
	void test3() {
		.f // qualified ; and . recovery
	}
	
}

import pack.mod3;
import nonexistantmodule.blah; // Test this in the face of non-existant
import nonexistantmodule;

invariant () {
	Foo. // qualified ; and . recovery
}

/// Test code completion recovery