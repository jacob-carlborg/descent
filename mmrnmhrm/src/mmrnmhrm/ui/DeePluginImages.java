package mmrnmhrm.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class DeePluginImages {
	
	public static final IPath ICONS_PATH= new Path("$nl$/icons/");

	
	public static final ImageDescriptor IMAGE_PACKAGEFOLDER = createImage_Obj("dee_packagefolder.gif");;

	public static final ImageDescriptor ELEM_OLDAST = createImage_Obj("elem_oldast.gif");
	public static final ImageDescriptor ELEM_IMPORT = createImage_Obj("elem_import.gif");
	public static final ImageDescriptor ELEM_PRE = createImage_Obj("elem_pre.gif");
	public static final ImageDescriptor ELEM_REF = createImage_Obj("elem_ref.gif");

	public static final ImageDescriptor ENT_ALIAS = createImage_Obj("ent_alias.gif");
	public static final ImageDescriptor ENT_CLASS = createImage_Obj("ent_class.gif");
	public static final ImageDescriptor ENT_ENUM = createImage_Obj("ent_enum.gif");
	public static final ImageDescriptor ENT_INTERFACE = createImage_Obj("ent_interface.gif");
	public static final ImageDescriptor ENT_STRUCT = createImage_Obj("ent_struct.gif");
	public static final ImageDescriptor ENT_TEMPLATE= createImage_Obj("ent_template.gif");
	public static final ImageDescriptor ENT_TYPEDEF = createImage_Obj("ent_typedef.gif");
	public static final ImageDescriptor ENT_UNION = createImage_Obj("ent_union.gif");
	public static final ImageDescriptor ENT_UNKNOWN = createImage_Obj("ent_unknown.gif");

	public static final ImageDescriptor ENT_VARIABLE = createImage_Obj("ent_variable.gif");;
	public static final ImageDescriptor ENT_FUNCTION = createImage_Obj("ent_function.gif");;
	public static final ImageDescriptor ELEM_MODULE = createImage_Obj("ent_module.gif");;

	private static ImageDescriptorRegistry registry = DeeUI.getImageDescriptorRegistry();


	private DeePluginImages() {} // Don't instantiate
	
	private static ImageDescriptor createImage_Obj(String string) {
		return DeeUI.getImageDescriptor("icons/obj16/" + string);
	}

	public static Image getImage(ImageDescriptor imageDesc) {
		return registry.get(imageDesc);
	}
}
