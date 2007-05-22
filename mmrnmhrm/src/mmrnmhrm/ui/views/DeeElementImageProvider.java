package mmrnmhrm.ui.views;

import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.DeeSourceLib;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.swt.graphics.Image;

import util.tree.IElement;
import descent.internal.core.dom.TypedefDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.base.Entity;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.declarations.DefinitionAggregate;
import dtool.dom.declarations.DefinitionAlias;
import dtool.dom.declarations.DefinitionFunction;
import dtool.dom.declarations.DefinitionTemplate;
import dtool.dom.declarations.DefinitionVariable;
import dtool.dom.declarations.Module.DeclarationModule;

public class DeeElementImageProvider {
	
	public static Image getElementImage(IElement element) {
		if (element instanceof ASTNode) {
			return getNodeImage((ASTNode) element);
		} else if(element instanceof DeeSourceFolder) {
			return getImage(DeePluginImages.ELEM_SOURCEFOLDER);
		} else if(element instanceof DeeSourceLib) {
			return getImage(DeePluginImages.ELEM_LIBRARY);
		} else  
			return null;
	}
	
	
	public static Image getNodeImage(ASTNode node) {
		if(node instanceof DeclarationImport) { 
			return getImage(DeePluginImages.NODE_IMPORT);
		} else if(node instanceof DeclarationModule) {
			return getImage(DeePluginImages.ELEM_PACKAGE);
		} else if (node instanceof Entity) {
			return getImage(DeePluginImages.NODE_REF);
		} else if (node instanceof DefinitionAlias) {
			return getImage(DeePluginImages.ENT_ALIAS);
		} else if(node instanceof DefinitionTemplate) {
			return getImage(DeePluginImages.ENT_TEMPLATE);
		} else if(node instanceof DefinitionVariable) {
			return getImage(DeePluginImages.ENT_VARIABLE);
		} else if(node instanceof DefinitionFunction) {
			return getImage(DeePluginImages.ENT_FUNCTION);
		} else
			
		if(node instanceof DefinitionAggregate) {
			/*DefinitionAggregate aggregate = (DefinitionAggregate) node;
			if(aggregate.getElementType() == )*/
			return getImage(DeePluginImages.ENT_CLASS);
		} else if(node instanceof TypedefDeclaration) {
			return getImage(DeePluginImages.ENT_TYPEDEF);
		} else if(node instanceof ASTNeoNode) {
			return getImage(DeePluginImages.ENT_UNKNOWN);
		} else
			return getImage(DeePluginImages.NODE_OLDAST);
	}


	private static Image getImage(String imageKey) {
		return DeePluginImages.getImage(imageKey);
	}


}
