
int mx;                                          
                                          
int mxref = mx; // same scope (to module)

class Foo {
	int mxref = mx; // 1 outer scope (to module)
	int foox;
	
	int func(int a) in { 
		int mx;	int foox; // decoys
	} out(mx) {
		int mx;	int foox; // decoys
	} body	{
		{ 
			int mx;	int foox; // decoys
		}
		mx++; // 2 outer scope (to module)
		foox++; // 1 outer scope (to class)
		a++; // 1 outer scope (to function param)
	}	
}

class FooBar : Foo, IFooBar {

	void func(int a) {
		foox++; // 1 super scope (to class)
		ibarx++; // 2 super scope (to interface)
		ifoobarx++; // 1 super scope (to interface)
	}

}

interface IBar {
	static int ibarx;
}

interface IFooBar : IBar {
	static int ifoobarx = ibarx; // 1 super scope (to interface)
}
