dmd -g -unittest -version=FluteCommandLine -of..\bin\test sample\module1.d sample\foo\module3.d sample\foo\bar\module2.d ..\..\flute\src\cn\kuehne\flectioned.d ..\..\flute\src\org\dsource\descent\flute\flute.d ..\..\flute\src\org\dsource\descent\flute\io.d -IC:\dmd\src\phobos C:\d\dmd\lib\WS2_32.lib