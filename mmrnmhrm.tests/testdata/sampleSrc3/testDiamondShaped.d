/*** Diamond shaped and U shaped test                 ***/

import pack.sample; // side1
import pack.sample2; // side2
import pack.sample3; // U side

void func() {
  // top of diamond
  foopublicVar++; 
  pack2.foopublic.foopublicVar++;
  
  // top of U
  foopublic2Var++; 
  pack2.foopublic2.foopublic2Var++;
}
/* Make sure access to pack2.* is not broken with these configurations:

    pack2.foopublic            pack2.foopublic2  
   /		       \                 |
pack.sample    pack.sample2     pack.sample3
    \             /                /                   
   testDiamondImports
   
*/