package dtool.ast.references;

import java.util.List;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.TypeInstance;
import descent.internal.compiler.parser.TypeQualified;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.Expression;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IDefUnitReferenceNode;


/** 
 * Helper class for converting references from the DMD AST 
 */
public abstract class ReferenceConverter {

	public static Reference convertType(Type type, ASTConversionContext convContext) {
		Reference entity = (Reference) DescentASTConverter.convertElem(type, convContext);
		return entity;
	}

	private static Reference convertTypeQualified(TypeQualified elem, Reference rootRef
			, ASTConversionContext convContext) {
		if(elem.idents != null && elem.idents.size() > 0){
			return createQualifiedRefFromIdents(elem.start, rootRef, elem.idents, elem.idents.size(), convContext);
		} else {
			return rootRef;
		}
	}

	public static Reference convertTypeIdentifier(TypeIdentifier elem, ASTConversionContext convContext) {
		Reference rootRef = ReferenceConverter.convertTypeIdentifier_ToRoot(elem, convContext);
		return convertTypeQualified(elem, rootRef, convContext);
	}
	
	public static Reference convertTypeInstance(TypeInstance elem, ASTConversionContext convContext) {
		Reference rootRef = convertTemplateInstance(elem.tempinst, convContext);
		return convertTypeQualified(elem, rootRef, convContext);
	}
	
	public static Reference convertTypeTypeOf(descent.internal.compiler.parser.TypeTypeof elem
			, ASTConversionContext convContext) {
		Reference rootRef = new TypeTypeof(elem, convContext);
		return convertTypeQualified(elem, rootRef, convContext);
	}
	
	static Reference createQualifiedRefFromIdents(int startPos, Reference rootRef,
			List<IdentifierExp> idents, int endix, ASTConversionContext convContext) {
		Assert.isTrue(endix >= 0);

		if( endix == 0 ) {
			return rootRef;
		}
		
		CommonRefQualified ref;
		if(endix == 1 && rootRef == null) {
			RefModuleQualified entroot = new RefModuleQualified(idents.get(endix-1), convContext);
			ref = entroot;
		} else {
			RefQualified qref = new RefQualified();
			qref.root = createQualifiedRefFromIdents(startPos, rootRef, idents, endix-1, convContext);
			qref.subref = CommonRefSingle.convertToSingleRef(idents.get(endix-1), convContext);
			ref = qref;
		}
		ref.setStart(startPos);
		ref.setEndPos(ref.subref.getEndPos());
		return ref;
		
	}

	public static Reference convertTypeIdentifier_ToRoot(TypeIdentifier elem, ASTConversionContext convContext) {
		Reference rootent;
		if(elem.ident.ident.length == 0) { 
			/*rootent = new EntModuleRoot();
			rootent.startPos = elem.idents.get(0).startPos-1;
			rootent.setEndPos(rootent.startPos+1);*/
			rootent = null;
		} else {
			rootent = CommonRefSingle.convertToSingleRef(elem.ident, convContext);
		}
		return rootent;
	}
	
	public static Reference convertTemplateInstance(TemplateInstance tplInstance, ASTConversionContext convContext) {
		return convertTemplateInstance(tplInstance, tplInstance.tiargs, convContext);
	}
	
	public static Reference convertTemplateInstance(TemplateInstance tplInstance, List<ASTDmdNode> tiargs
			, ASTConversionContext convContext) {
		IdentifierExp tplIdent = tplInstance.name;
		return new RefTemplateInstance(tplInstance, tplIdent, tiargs, convContext);
	}
	
	public static Reference convertDotIdexp(DotIdExp elem, ASTConversionContext convContext) {
		
		IDefUnitReferenceNode rootent;
		Expression expTemp = Expression.convert(elem.e1, convContext);
		if(expTemp instanceof ExpReference) {
			rootent = ((ExpReference) expTemp).ref;
			
			if(rootent == null || elem.e1.length == 0) {
				return new RefModuleQualified(elem.ident, convContext);
			}
		} else {
			rootent = expTemp;
		}

		
		RefQualified newelem = new RefQualified();
		newelem.setSourceRange(elem);
		newelem.root = rootent;
		newelem.subref = CommonRefSingle.convertToSingleRef(elem.ident, convContext);

		// Fix some DMD missing ranges 
		if(newelem.hasNoSourceRangeInfo()) {
			try {
				int newstartPos= newelem.getRootAsNode().getStartPos();
				int newendPos= newelem.subref.getEndPos()-newelem.getRootAsNode().getStartPos();
				newelem.setSourceRange(newstartPos, newendPos);
			} catch (RuntimeException re) {
				throw new UnsupportedOperationException(re);
			}
		}
		return newelem;
	}
	
	public static Reference convertDotTemplateIdexp(DotTemplateInstanceExp elem, ASTConversionContext convContext) {
		
		IDefUnitReferenceNode rootent;
		Expression expTemp = Expression.convert(elem.e1, convContext);
		if(expTemp instanceof ExpReference) {
			rootent = ((ExpReference) expTemp).ref;

			if(rootent == null) {
				return new RefModuleQualified(elem.ti.name, convContext);
			}
		} else {
			rootent = expTemp;
		}

		
		RefQualified newelem = new RefQualified();
		newelem.setSourceRange(elem);
		newelem.root = rootent;
		newelem.subref = new RefTemplateInstance(elem.ti, convContext);

		return newelem;
	}

}

