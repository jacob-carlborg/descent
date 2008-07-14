package descent.internal.building.compiler;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.building.compiler.ICompileManager;
import descent.building.compiler.IObjectFile;

public class DmdCompileManager implements ICompileManager
{
    protected static final boolean DEBUG = true;
    
    /* protected static class DmdResponseInterpreter implements IResponseInterpreter
    {
        private static final Pattern ERROR_WITH_FILENAME = Pattern.compile(
                "([^\\(\\:]*)" +          // Filename
                "(?:\\((\\d*)\\))?" +     // Line number
                "\\:\\s(.*)$"             // Message
            );
        
        /* (non-Javadoc)
         * @see descent.launching.compiler.IResponseInterpreter#interpret(java.lang.String)
         * /
        public void interpret(String line)
        {
            // TODO finish & test
            
            if(DEBUG)
                System.out.println("OUT => " + line);
            
            /*
            Matcher m = ERROR_WITH_FILENAME.matcher(line);
            if(m.find())
            {
                String file = m.group(1);
                String lineStr = m.group(2);
                String message = m.group(3);
                int lineNum = null != lineStr ? Integer.parseInt(lineStr) : -1;
                resp.addError(new BuildError(message, file, lineNum));
                return;
            }
            * /
        } */
        
        /* (non-Javadoc)
         * @see descent.launching.compiler.IResponseInterpreter#interpretError(java.lang.String)
         * /
        public void interpretError(String line)
        {
            if(DEBUG)
                System.out.println("ERR => " + line);
            
            // Keep all the interpretation in one method
            // TODO interpret(line);
        }
    } */
    
    public String[] compile(IObjectFile[] objectFiles, IProgressMonitor pm)
    {
        // TODO
        
        try
        {
            pm.beginTask("Compiling object files", 100);
            
            if(DEBUG)
            {
                System.out.println("----- DmdCompileManager#compile ---");
                for(IObjectFile obj : objectFiles)
                    System.out.println(obj);
            }
            return new String[] { };
        }
        finally
        {
            pm.done();
        }
    }
}
