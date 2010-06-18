package dtool.descentadapter;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import descent.internal.compiler.parser.TypeExp;
import descent.internal.compiler.parser.TypeInstance;
import dtool.ast.expressions.ExpReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefTypeSlice;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.references.TypeDelegate;
import dtool.ast.references.TypeDynArray;
import dtool.ast.references.TypeFunction;
import dtool.ast.references.TypeMapArray;
import dtool.ast.references.TypePointer;
import dtool.ast.references.TypeStaticArray;

/**
 * This class is a mixin. 
 * Do not use it, instead use it's subclass: {@link DeclarationConverter}
 */
abstract class RefConverter extends CoreConverter {
	

	@Override
	public boolean visit(FuncLiteralDeclaration elem) {
		Assert.fail("Converted by parent");
		return false;	
	}
	
	@Override
	public boolean visit(TypeExp node) {
		return endAdapt(new ExpReference(node));
	}
	
	
	@Override
	public boolean visit(DotIdExp node) {
		return endAdapt(new ExpReference(node));
	}
		
	@Override
	public boolean visit(DotTemplateInstanceExp node) {
		return endAdapt(new ExpReference(node));
	}

	/* ---- References & co. --- */
	
	@Override
	public boolean visit(TemplateInstanceWrapper node) {
		return endAdapt(new ExpReference(node));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.IdentifierExp elem) {
		/*if(elem.ident.equals("")) {
			return endAdapt(null);
		}*/
		//return endAdapt(new RefIdentifier(elem));
		return endAdapt(new ExpReference(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeBasic elem) {
		return endAdapt(new RefIdentifier(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateInstance elem) {
		return endAdapt(ReferenceConverter.convertTemplateInstance(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeIdentifier elem) {
		return endAdapt(ReferenceConverter.convertTypeIdentifier(elem));
	}

	@Override
	public boolean visit(TypeInstance elem) {
		return endAdapt(ReferenceConverter.convertTypeInstance(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeTypeof elem) {
		return endAdapt(ReferenceConverter.convertTypeTypeOf(elem));
	}

	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeAArray elem) {
		return endAdapt(new TypeMapArray(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeDArray elem) {
		return endAdapt(new TypeDynArray(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeSArray elem) {
		return endAdapt(new TypeStaticArray(elem));
	}
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeDelegate elem) {
		return endAdapt(new TypeDelegate(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeFunction elem) {
		return endAdapt(new TypeFunction(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypePointer elem) {
		return endAdapt(TypePointer.convertTypePointer(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeSlice elem) {
		return endAdapt(new RefTypeSlice(elem));
	}


}