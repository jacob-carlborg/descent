int mx;
class Foo {
	int foox;
	Xpto xpto;
	
	static class Inner { 
		int innerx;
	}
}
class Xpto {
	char[] str;
}
template Tpl(T) {
	int num;
}

/*** Same scope root ref ***/

// Entity Identifier
Foo foo; // (type)
int dummy1 = mx; // (exp)

// ModuleRoot
.Foo foo; // (type)
int dummy2 = .mx; // (exp)

// Entity Template Instance
Tpl!(int) tpl; //  
int dummy4 = Tpl!(int); // 

// Entity Qualified
Foo.Inner inner;   // 2 identifier
.Foo.Inner inner;  // 2 module root
Tpl!(int).TplType tpltype; // 2 tpl instance
int dummy3 = Foo.foox; // 2 identifier (exp)
int dummy32 = Foo.Inner.innerx; // 2 identifier (exp)


// Base Class reference
class FooBar : Foo { } 


void func() {
//	class Foo { }
	.Foo foo; // ModuleRoot (1 outer scope)(type)(decoy)
	.mx++; // ModuleRoot (1 outer scope)(exp)(decoy)
	Foo foo; // Entity Identifier (type)
	foo++; // Entity Identifier (exp)
	mx++; // Entity Identifier (exp) (1 outer scope)
	Foo.Inner inner;  // Entity Qualified
}


