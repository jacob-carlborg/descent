/*** Selective import test                                             ***/

import pack.mod1;
// XXX: Extra imports before the selective import are not supported 
import /*pack.mod2,*/ pack.sample : 
 	SampleClass, SampleClassAlias = SampleClassB, 
 	sampleVar, sampleVarAlias = sampleVarB;
import mod2alias = pack.mod2;

// XXX: The following is not suppported (aliasing and selective) :
//  import samplealias = pack.sample : SampleClassB, sampleVarB;

alias pack.sample modref; // fail, no FQN with selective

SampleClass sampleclass;
SampleClassAlias sampleclass2;
SampleClassB sampleclass3; // fail

void func() {
	SampleClass.foo++; 
	SampleClassAlias.foo++;
	SampleClassB.foo++; // fail

	foopublicImportVar++; // fail
	fooprivateImportVar++; // fail

	pack2.foopublic.foopublicImportVar++; // fail
	pack2.fooprivate.fooprivateImportVar++; // fail
}