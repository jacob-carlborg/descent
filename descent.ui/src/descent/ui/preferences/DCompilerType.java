package descent.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.IStatus;

import descent.internal.ui.util.StatusInfo;

public class DCompilerType {

	private static final DCompilerType dmdLinux    = new DCompilerType( "dmdLinux");
	private static final DCompilerType dmdWindows  = new DCompilerType( "dmdWindows");
	private static final DCompilerType gdc         = new DCompilerType( "gdc");
	
	private final String typeName;
	
	private DCompilerType( String typeName ){
		this.typeName = typeName;
		
	}
	
	public IStatus validateInstallLocation(File root) {
		if( this == dmdLinux ){
			File comp = new File( root, "dmd/bin/dmd" );
			if( comp.canRead() ){
				IStatus res = new StatusInfo( IStatus.ERROR, "Cannot find compiler binary" );
			}
		}
		else if( this == dmdWindows ){
			File comp = new File( root, "dmd\\bin\\dmd.exe" );
			if( comp.canRead() ){
				IStatus res = new StatusInfo( IStatus.ERROR, "Cannot find compiler binary" );
			}
		}
		else if( this == gdc ){
			File comp = new File( root, "dmd/bin/dmd" );
			if( comp.canRead() ){
				IStatus res = new StatusInfo( IStatus.ERROR, "Cannot find compiler binary" );
			}
		}
		return new StatusInfo( IStatus.OK, "Compiler location is good" );
	}

	public String getName() {
		return typeName;
	}

	public static DCompilerType[] getTypes() {
		return new DCompilerType[]{
				dmdLinux, dmdWindows, gdc
		};
	}

}
