package mmrnmhrm.ui.outline;

import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.IDeeSourceRoot;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.graphics.Image;

import descent.internal.core.dom.ClassDeclaration;
import descent.internal.core.dom.InterfaceDeclaration;
import descent.internal.core.dom.TypedefDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.base.Entity;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.declarations.DefinitionAggregate;
import dtool.dom.declarations.DefinitionAlias;
import dtool.dom.declarations.DefinitionFunction;
import dtool.dom.declarations.DefinitionVariable;
import dtool.dom.declarations.Module.DeclarationModule;

public class DeeElementImageProvider {
	/*
	private DeeElementImageProvider instance = new DeeElementImageProvider();
	
	public DeeElementImageProvider() {}
	
	public DeeElementImageProvider getDefault() {
		return instance;
	}*/
	
	public static Image getLabelImage(Object element) {
		if(element instanceof DeeSourceFolder)
			return getImage(DeePluginImages.ELEM_SOURCEFOLDER);
		else if(element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			DeeProject deeproj = DeeModelManager.getLangProject(folder.getProject());
			if(deeproj == null)
				return null;
			
			IDeeSourceRoot bpentry = deeproj.getRoot(folder);
			
			if(bpentry instanceof DeeSourceFolder)
				return getImage(DeePluginImages.ELEM_SOURCEFOLDER);
			else 
				return null;
		} else if (element instanceof ASTNode) {
			ASTNode node = (ASTNode) element;
			return getLabelImage(node);
		} else
			return null;
	}
	
	public static Image getLabelImage(ASTNode node) {
		if(node instanceof DeclarationImport) { 
			return getImage(DeePluginImages.NODE_IMPORT);
		} else if(node instanceof DeclarationModule) {
			return getImage(DeePluginImages.ELEM_MODULE);
		} else if (node instanceof Entity) {
			return getImage(DeePluginImages.NODE_REF);
		} else if (node instanceof DefinitionAlias) {
			return getImage(DeePluginImages.ENT_ALIAS);
		} else if(node instanceof DefinitionAggregate) {
			return getImage(DeePluginImages.ENT_TEMPLATE);
		} else if(node instanceof DefinitionVariable) {
			return getImage(DeePluginImages.ENT_VARIABLE);
		} else if(node instanceof DefinitionFunction) {
			return getImage(DeePluginImages.ENT_FUNCTION);
		} 		
		
		else if(node instanceof ClassDeclaration) {
			return getImage(DeePluginImages.ENT_CLASS);
		} else if(node instanceof TypedefDeclaration) {
			return getImage(DeePluginImages.ENT_TYPEDEF);
		} else if(node instanceof InterfaceDeclaration) {
			return getImage(DeePluginImages.ENT_INTERFACE);
		} else if(node instanceof ASTNeoNode) {
			return getImage(DeePluginImages.ENT_UNKNOWN);
		} else
			return getImage(DeePluginImages.NODE_OLDAST);
	}


	private static Image getImage(String imageKey) {
		return DeePluginImages.getImage(imageKey);
	}
}
