package descent.debug.core.utils;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.debug.core.IDescentLaunchConfigurationConstants;

/**
 * Utilities for string arguments.
 */
public class ArgumentUtils {
	
	private static class ArgumentParser {

		private String fArgs;

		private int fIndex = 0;

		private int ch = -1;

		public ArgumentParser( String args ) {
			fArgs = args;
		}

		public String[] parseArguments() {
			ArrayList v = new ArrayList();
			ch = getNext();
			while( ch > 0 ) {
				while( Character.isWhitespace( (char)ch ) )
					ch = getNext();
				if ( ch == '"' ) {
					v.add( parseString() );
				}
				else {
					v.add( parseToken() );
				}
			}
			String[] result = new String[v.size()];
			v.toArray( result );
			return result;
		}

		private int getNext() {
			if ( fIndex < fArgs.length() )
				return fArgs.charAt( fIndex++ );
			return -1;
		}

		private String parseString() {
			StringBuffer buf = new StringBuffer();
			ch = getNext();
			while( ch > 0 && ch != '"' ) {
				if ( ch == '\\' ) {
					ch = getNext();
					if ( ch != '"' ) { // Only escape double quotes
						buf.append( '\\' );
					}
				}
				if ( ch > 0 ) {
					buf.append( (char)ch );
					ch = getNext();
				}
			}
			ch = getNext();
			return buf.toString();
		}

		private String parseToken() {
			StringBuffer buf = new StringBuffer();
			while( ch > 0 && !Character.isWhitespace( (char)ch ) ) {
				if ( ch == '\\' ) {
					ch = getNext();
					if ( ch > 0 ) {
						if ( ch != '"' ) { // Only escape double quotes
							buf.append( '\\' );
						}
						buf.append( (char)ch );
						ch = getNext();
					}
					else if ( ch == -1 ) { // Don't lose a trailing backslash
						buf.append( '\\' );
					}
				}
				else if ( ch == '"' ) {
					buf.append( parseString() );
				}
				else {
					buf.append( (char)ch );
					ch = getNext();
				}
			}
			return buf.toString();
		}
	}

	/**
	 * For given launch configuration returns the program arguments as 
	 * an array of individual arguments.
	 */
	public static String[] getProgramArgumentsArray( ILaunchConfiguration config ) throws CoreException {
		return parseArguments( getProgramArguments( config ) );
	}

	/**
	 * Returns the program arguments as a String.
	 */
	public static String getProgramArguments(ILaunchConfiguration config) throws CoreException {
		String args = config.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (String)null);
		if (args != null) {
			args = getStringVariableManager().performStringSubstitution(args);
		}
		return args;
	}

	/**
	 * Convenience method.
	 */
	public static IStringVariableManager getStringVariableManager() {
		return VariablesPlugin.getDefault().getStringVariableManager();
	}

	private static String[] parseArguments( String args ) {
		if ( args == null )
			return new String[0];
		ArgumentParser parser = new ArgumentParser( args );
		String[] res = parser.parseArguments();
		return res;
	}
	
	/**
	 * If the path contains spaces, the string is returned
	 * eclosed with double quotes. Otherwise, the string
	 * is returned without modification.
	 * @param string a string
	 * @return the resulting string
	 */
	public static String toStringArgument(String string) {
		if (string.indexOf(' ') != -1) {
			return "\"" + string + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			return string;
		}
	}

}
