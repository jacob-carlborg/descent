                                                 
int mx;                                          
                                                 
class Foo {                                      
   int a = mx;                                   
   FooBar foobar;                                
                                                 
   void func(int a) {                            
       b = mx;                                   
       a++;                                      
       .a++;                                     
       Foo.foobar.sx++;                          
       FooBar.Inner.z++;                         
   }                                             
}                                                
                                                 
FooBar.Inner inner;                              
                                                 
class FooBar : Foo {                             
  int sx;                                        
                                                 
  class Inner {                                  
     int z;                                      
  }                                              
}                                                
                                                 
                                                 
