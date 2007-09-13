module testCodeCompletion3;

import pack.sample : 
	SampleClass, SampleClassAlias = SampleClassB;

template Tpl(T) {
}

void func(Tuple!(1, 2) a) {
	Tpl  ! (int);
	foo2  . sdf;
	
	alias Tuple!(1, 2) tuple;
	tuple[0].as;
}


import pack.mod3; // Test complete here, several offsets
