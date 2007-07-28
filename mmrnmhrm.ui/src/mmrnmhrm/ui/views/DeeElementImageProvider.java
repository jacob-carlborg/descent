package mmrnmhrm.ui.views;

import melnorme.miscutil.tree.IElement;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.DeeSourceLib;
import mmrnmhrm.core.model.DeePackageFragment;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.swt.graphics.Image;

import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.definitions.DefinitionAggregate;
import dtool.dom.definitions.DefinitionAlias;
import dtool.dom.definitions.DefinitionEnum;
import dtool.dom.definitions.DefinitionFunction;
import dtool.dom.definitions.DefinitionTemplate;
import dtool.dom.definitions.DefinitionTypedef;
import dtool.dom.definitions.DefinitionVariable;
import dtool.dom.definitions.Module.DeclarationModule;
import dtool.dom.references.Entity;

public class DeeElementImageProvider {
	
	public static Image getElementImage(IElement element) {
		if (element instanceof ASTNode) {
			return getNodeImage((ASTNode) element);
		} else if(element instanceof DeePackageFragment) {
			return getImage(DeePluginImages.ELEM_PACKAGE);
		} else if(element instanceof CompilationUnit) {
			return getImage(DeePluginImages.ELEM_FILE);
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
		} else if(node instanceof DefinitionTypedef) {
			return getImage(DeePluginImages.ENT_TYPEDEF);
		} else if(node instanceof DefinitionEnum) {
			return getImage(DeePluginImages.ENT_ENUM);
		} else if(!(node instanceof ASTNeoNode)) {
			return getImage(DeePluginImages.NODE_OLDAST);
		} else
			return getImage(DeePluginImages.NODE_OTHER);
	}


	private static Image getImage(String imageKey) {
		return DeePluginImages.getImage(imageKey);
	}


}
