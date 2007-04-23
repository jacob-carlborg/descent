package mmrnmhrm.core.build;

public class DeeCEManager {

	
	public static IDeeCE[] getAvailableDCEs()  {
		return new IDeeCE[] { 
			new IDeeCE() {

				public String getName() {
					return "DMD path default";
				}
			
			}
		};
	}
}
