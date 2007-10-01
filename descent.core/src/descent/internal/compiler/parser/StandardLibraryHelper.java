package descent.internal.compiler.parser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;


/**
 * Contains source code of the standar library,
 * like the class object. This is a temporary workaround
 * to get ClassDeclaration's for Object, TypeInfo, etc.
 * 
 * These should be obtained from the standard library
 * configured in the project.
 */
public class StandardLibraryHelper {
	
	private final static char[] object_d = { 'o', 'b', 'j', 'e', 'c', 't', '.', 'd' };
	
	public ClassDeclaration Object;
	public ClassDeclaration ClassInfo;
	public ClassDeclaration TypeInfo;
	public ClassDeclaration TypeInfo_Class;
	public ClassDeclaration TypeInfo_Interface;
	public ClassDeclaration TypeInfo_Struct;
	public ClassDeclaration TypeInfo_Typedef;
	public ClassDeclaration TypeInfo_Pointer;
	public ClassDeclaration TypeInfo_Array;
	public ClassDeclaration TypeInfo_StaticArray;
	public ClassDeclaration TypeInfo_AssociativeArray;
	public ClassDeclaration TypeInfo_Enum;
	public ClassDeclaration TypeInfo_Function;
	public ClassDeclaration TypeInfo_Delegate;
	public ClassDeclaration TypeInfo_Tuple;
	
	private final static StandardLibraryHelper D1;
	private final static StandardLibraryHelper D2;
	
	static {
		D1 = new StandardLibraryHelper();
		D2 = new StandardLibraryHelper();
		
		D1.fill("object1.d", AST.D1);
		D2.fill("object2.d", AST.D2);
	}
	
	public final static StandardLibraryHelper getInstance(int apiLevel) {
		if (apiLevel == AST.D0 || apiLevel == AST.D1) {
			return D1;
		} else if (apiLevel == AST.D2) {
			return D2;
		} else {
			throw new IllegalStateException();
		}
	}
	
	private StandardLibraryHelper() {
		
	}
	
	private void fill(String filename, int apiLevel) {
		try {
			String source = getFile(filename);
			Parser parser = new Parser(apiLevel, source);
			parser.filename = object_d;
			Module module = parser.parseModuleObj();
			for(Dsymbol symbol : module.members) {
				if (symbol.ident == null || symbol.ident.ident == null) continue;
				
				if (CharOperation.equals(symbol.ident.ident, Id.Object)) {
					Object = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.ClassInfo)) {
					ClassInfo = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo)) {
					TypeInfo = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Class)) {
					TypeInfo_Class = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Interface)) {
					TypeInfo_Interface = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Struct)) {
					TypeInfo_Struct = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Typedef)) {
					TypeInfo_Typedef = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Pointer)) {
					TypeInfo_Pointer = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Array)) {
					TypeInfo_Array = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_StaticArray)) {
					TypeInfo_StaticArray = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_AssociativeArray)) {
					TypeInfo_AssociativeArray = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Enum)) {
					TypeInfo_Enum = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Function)) {
					TypeInfo_Function = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Delegate)) {
					TypeInfo_Delegate = (ClassDeclaration) symbol;
				} else if (CharOperation.equals(symbol.ident.ident, Id.TypeInfo_Tuple)) {
					TypeInfo_Tuple = (ClassDeclaration) symbol;
				}
			}
			CompilationUnitResolver.resolve(module);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getFile(String name) throws IOException {
		byte[] buffer = new byte[1024];
		InputStream in = StandardLibraryHelper.class.getResourceAsStream(name);
		BufferedInputStream bin = new BufferedInputStream(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedOutputStream bout = new BufferedOutputStream(out);
		while(bin.available() > 0) {
			int len = bin.read(buffer);
			out.write(buffer, 0, len);
		}
		bin.close();
		in.close();
		bout.close();
		out.close();
		
		return out.toString();
	}

}
