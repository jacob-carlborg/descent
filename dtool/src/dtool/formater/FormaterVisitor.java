package dtool.formater;

import java.io.OutputStream;

import descent.core.domX.ASTVisitor;
import dtool.dom.base.ASTNeoNode;


public class FormaterVisitor extends ASTVisitor {
	
	FormaterScribe scribe; 
	
	public FormaterVisitor(OutputStream out, char[] sbuf) {
		scribe = new FormaterScribe(out, sbuf);
	}
	
	public FormaterVisitor(OutputStream out, String str) {
		// TODO: Formatter
		//scribe = new FormaterScribe(out, sbuf);
	}
	
	// ***************************** //
	
	public boolean visit(ASTNeoNode element) {
		scribe.synchToElementAndIndent(element); 
		scribe.write("««" + element.toString() + "»»");
		scribe.writeElement(element);
		return false;
	}
/*
	// -------------------- //
	public boolean visit(Module element) {
		if(element.md != null) 
			element.md.accept(this);
		traverseMany(element.getDeclarationDefinitions());
		return false;
	}
	public boolean visit(ModuleDeclaration element) {
		scribe.synchToElementAndIndent(element); 
		scribe.write("module "+ element.qName + ";"); // TODO: refactor .qname? 
		scribe.advancePos(element.getLength());
		scribe.requireNewLine(element);
		return false;
	}	


	public boolean visit(ImportDeclaration element) {
		scribe.synchToElementAndIndent(element); 
		scribe.write(TokenExt.protectionAttributesToString(element.getModifiers()));
		scribe.writeElement(element);
		scribe.requireNewLine(element);
		return false;
	}	


	// --------- DEFINITION -------- //
	
	private void formatDeclarationPrelude(Declaration element) {
		scribe.synchToElementAndIndent(element); 
		scribe.write(TokenExt.protectionAttributesToString(element.getModifiers()));
		scribe.write("DEF ");
		scribe.write(TokenExt.storageClassToString(element.getModifiers()));
	}
	
	public boolean visit(VarDeclaration element) {
		formatDeclarationPrelude(element);
		scribe.write(element.getType() + " " + element.getName() +	";");
		scribe.advancePos(element.getLength());
		scribe.requireNewLine(element);
		return false;
	}

	public boolean visit(FuncDeclaration element) {
		formatDeclarationPrelude(element);
		scribe.writeElement(element);
		scribe.requireNewLine(element);
		return false;
	}
*/
	/*public boolean visit(StorageClassDeclaration element) {
	//formatDeclarationPrelude(element);
	scribe.write("storage dec " + element.stc);
	traverseMany(element.getDeclarationDefinitions());
	return false;
}*/


}

