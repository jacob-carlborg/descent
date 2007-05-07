module pack.foo;

import pack.fbar;

class Foo {}

.Foo foo;

int foo;

a.asf!(int, Foo, 23).a var;

int a;

int x1, x2;

char[] func() {
	Foo foo;
	asf!(int, Foo, 23)++;
	a++;
	a.a++;
	Foo.dind.a++;
	a = int.sizeof;

	a = (Foo).sizeof;
	a = typeid(Foo);
	
	try { 
	a++;
	} catch(E e) {
	} finally {
	b++;
	}

	
}

class Foo {
	int as = "das";

	
	this(char ch) {

	}
	
}

Foo foo2;
alias .b.c.d myalias;

template Tpl() {}
