package mmrnmhrm.ui.outline;

import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.IBuildPathEntry;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.graphics.Image;

import descent.internal.core.dom.ClassDeclaration;
import descent.internal.core.dom.InterfaceDeclaration;
import descent.internal.core.dom.TypedefDeclaration;
import dtool.dom.base.ASTNeoNode;
import dtool.dom.base.ASTNode;
import dtool.dom.base.DeclarationImport;
import dtool.dom.base.DefinitionAggregate;
import dtool.dom.base.DefinitionAlias;
import dtool.dom.base.DefinitionFunction;
import dtool.dom.base.DefinitionVariable;
import dtool.dom.base.Entity;
import dtool.dom.base.Module.DeclarationModule;

public class DeeElementImageProvider {
	/*
	private DeeElementImageProvider instance = new DeeElementImageProvider();
	
	public DeeElementImageProvider() {}
	
	public DeeElementImageProvider getDefault() {
		return instance;
	}*/
	
	public static Image getLabelImage(Object element) {
		if(element instanceof DeeSourceFolder)
			return getImage(DeePluginImages.IMAGE_PACKAGEFOLDER);
		else if(element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			DeeProject deeproj = DeeModel.getDeeProject(folder.getProject());
			if(deeproj == null)
				return null;
			
			IBuildPathEntry bpentry = deeproj.getEntry(folder.getProjectRelativePath());
			
			if(bpentry instanceof DeeSourceFolder)
				return getImage(DeePluginImages.IMAGE_PACKAGEFOLDER);
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
			return getImage(DeePluginImages.ELEM_IMPORT);
		} else if(node instanceof DeclarationModule) {
			return getImage(DeePluginImages.ELEM_MODULE);
		} else if (node instanceof Entity) {
			return getImage(DeePluginImages.ELEM_REF);
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
			return getImage(DeePluginImages.ELEM_OLDAST);
	}


	private static Image getImage(String imageKey) {
		return DeePluginImages.getImage(imageKey);
	}
}
