package mmrnmhrm.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class DeePluginImages {
	
	public static final IPath ICONS_PATH= new Path("$nl$/icons/");

	// Registry must be on top, to be initialized firt 
	private static ImageRegistry registry = DeePlugin.getInstance().getImageRegistry();

	
	public static final String IMAGE_PACKAGEFOLDER = createImage_Obj("dee_packagefolder.gif");;

	public static final String ELEM_OLDAST = createImage_Obj("elem_oldast.gif");
	public static final String ELEM_IMPORT = createImage_Obj("elem_import.gif");
	public static final String ELEM_PRE = createImage_Obj("elem_pre.gif");
	public static final String ELEM_REF = createImage_Obj("elem_ref.gif");

	public static final String ENT_ALIAS = createImage_Obj("ent_alias.gif");
	public static final String ENT_CLASS = createImage_Obj("ent_class.gif");
	public static final String ENT_ENUM = createImage_Obj("ent_enum.gif");
	public static final String ENT_INTERFACE = createImage_Obj("ent_interface.gif");
	public static final String ENT_STRUCT = createImage_Obj("ent_struct.gif");
	public static final String ENT_TEMPLATE= createImage_Obj("ent_template.gif");
	public static final String ENT_TYPEDEF = createImage_Obj("ent_typedef.gif");
	public static final String ENT_UNION = createImage_Obj("ent_union.gif");
	public static final String ENT_UNKNOWN = createImage_Obj("ent_unknown.gif");

	public static final String ENT_VARIABLE = createImage_Obj("ent_variable.gif");;
	public static final String ENT_FUNCTION = createImage_Obj("ent_function.gif");;
	public static final String ELEM_MODULE = createImage_Obj("ent_module.gif");;



	private DeePluginImages() {} // Don't instantiate
	
	private static String createImage_Obj(String imageName) {
		String imgPath = ICONS_PATH + "obj16/" + imageName;
		registry.put(imageName, DeeUI.getImageDescriptor(imgPath));
		return imageName;
	}

	public static Image getImage(String imageKey) {
		return registry.get(imageKey);
	}
}
