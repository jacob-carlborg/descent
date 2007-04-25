package mmrnmhrm.core.build;

public class DeeCEManager {

	
	public static IDeeCE[] getAvailableDCEs()  {
		return new IDeeCE[] { 
			new IDeeCE() {

				public String getName() {
					return "DMD path default";
				}
				
				@Override
				public String toString() {
					return getName();
				}
			
			}
		};
	}

	public static IDeeCE getDefaultCompiler() {
		return getAvailableDCEs()[0];
	}
}
