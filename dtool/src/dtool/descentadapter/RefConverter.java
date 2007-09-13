package dtool.descentadapter;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import descent.internal.compiler.parser.TypeExp;
import descent.internal.compiler.parser.TypeInstance;
import dtool.dom.expressions.ExpReference;
import dtool.dom.references.RefIdentifier;
import dtool.dom.references.RefTypeSlice;
import dtool.dom.references.Reference;
import dtool.dom.references.ReferenceConverter;
import dtool.dom.references.TypeDelegate;
import dtool.dom.references.TypeDynArray;
import dtool.dom.references.TypeFunction;
import dtool.dom.references.TypeMapArray;
import dtool.dom.references.TypePointer;
import dtool.dom.references.TypeStaticArray;
import dtool.dom.references.TypeTypeof;

/**
 * This class is a mixin. 
 * Do not use it, instead use it's subclass: {@link DeclarationConverter}
 */
abstract class RefConverter extends CoreConverter {
	

	public boolean visit(FuncLiteralDeclaration elem) {
		Assert.fail("Converted by parent");
		return false;	
	}
	
	public boolean visit(TypeExp node) {
		return endAdapt(new ExpReference(node));
	}
	
	
	public boolean visit(DotIdExp node) {
		return endAdapt(new ExpReference(node));
	}
		
	public boolean visit(DotTemplateInstanceExp node) {
		return endAdapt(new ExpReference(node));
	}

	/* ---- References & co. --- */
	
	public boolean visit(TemplateInstanceWrapper node) {
		return endAdapt(new ExpReference(node));
	}

	public boolean visit(descent.internal.compiler.parser.IdentifierExp elem) {
		/*if(elem.ident.equals("")) {
			return endAdapt(null);
		}*/
		//return endAdapt(new RefIdentifier(elem));
		return endAdapt(new ExpReference(elem));
	}
	
	public boolean visit(descent.internal.compiler.parser.TypeBasic elem) {
		return endAdapt(new RefIdentifier(elem));
	}
	
	public boolean visit(descent.internal.compiler.parser.TemplateInstance elem) {
		return endAdapt(ReferenceConverter.convertTemplateInstance(elem));
	}
	
	public boolean visit(descent.internal.compiler.parser.TypeIdentifier elem) {
		Reference rootent = ReferenceConverter.convertTypeIdentifier_ToRoot(elem);
		return endAdapt(ReferenceConverter.convertTypeQualified(rootent, elem));
	}

	public boolean visit(TypeInstance elem) {
		return endAdapt(ReferenceConverter.convertTypeInstance(elem));
	}
	
	public boolean visit(descent.internal.compiler.parser.TypeTypeof elem) {
		Reference rootent = new TypeTypeof(elem);
		return endAdapt(ReferenceConverter.convertTypeQualified(rootent, elem));
	}

	
	public boolean visit(descent.internal.compiler.parser.TypeAArray elem) {
		return endAdapt(new TypeMapArray(elem));
	}
	
	public boolean visit(descent.internal.compiler.parser.TypeDArray elem) {
		return endAdapt(new TypeDynArray(elem));
	}
	
	public boolean visit(descent.internal.compiler.parser.TypeSArray elem) {
		return endAdapt(new TypeStaticArray(elem));
	}
	
	
	public boolean visit(descent.internal.compiler.parser.TypeDelegate elem) {
		return endAdapt(new TypeDelegate(elem));
	}
	
	public boolean visit(descent.internal.compiler.parser.TypeFunction elem) {
		return endAdapt(new TypeFunction(elem));
	}
	
	public boolean visit(descent.internal.compiler.parser.TypePointer elem) {
		return endAdapt(TypePointer.convertTypePointer(elem));
	}
	
	public boolean visit(descent.internal.compiler.parser.TypeSlice elem) {
		return endAdapt(new RefTypeSlice(elem));
	}


}