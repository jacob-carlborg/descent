package descent.core.dom;



/**
 * Extern declaration AST node type.
 *
 * <pre>
 * ExternDeclaration:
 *    { Modifier } <b>extern</b> [ <b>(</b> [ | D | C | C++ | Windows | Pascal ]<b>)</b> ] { Declaration }
 * </pre>
 */
public class ExternDeclaration {
	
	/**
	 * A kind of linkage.
	 */
	public static enum Linkage {
		/** Link to D code by default*/
		DEFAULT(""),
		/** Link to D code */
		D("D"),
		/** Link to C code */
		C("C"),
		/** Link to C++ code */
		CPP("C++"),
		/** Link to Windows code */
		WINDOWS("Windows"),
		/** Link to Pascal code */
		PASCAL("Pascal"),
		/** Link to System (Windows or C)  code */
		SYSTEM("System"),		
		;
		
		/**
		 * The token for the linkage.
		 */
		private String token;
		
		/**
		 * Creates a new linkage with the given token.
		 * <p>
		 * Note: this constructor is private. The only instances
		 * ever created are the ones for the standard linkage.
		 * </p>
		 * 
		 * @param token the character sequence for the linkage
		 */
		private Linkage(String token) {
			this.token = token;
		}
		
		/**
		 * Returns the character sequence for the linkage.
		 * 
		 * @return the character sequence for the linkage
		 */
		public String toString() {
			return token;
		}
	}

}
