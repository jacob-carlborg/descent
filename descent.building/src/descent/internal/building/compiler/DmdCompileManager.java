package descent.internal.building.compiler;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.building.compiler.IBuildManager;
import descent.building.compiler.ICompileManager;
import descent.building.compiler.IExecutionCallback;
import descent.building.compiler.IExecutionMonitor;
import descent.building.compiler.IObjectFile;
import descent.building.compiler.IResponseInterpreter;

public class DmdCompileManager implements ICompileManager
{
    private static final class DmdResponseInterpreter implements IResponseInterpreter
    {
        private static final Pattern ERROR_WITH_FILENAME = Pattern.compile(
                "([^\\(\\:]*)" +          // Filename
                "(?:\\((\\d*)\\))?" +     // Line number
                "\\:\\s(.*)$"             // Message
            );
        
        private final IObjectFile objFile;
        
        public DmdResponseInterpreter(IObjectFile objFile)
        {
            this.objFile = objFile;
        }
        
        public void interpretError(String line)
        {
            // Keep all the interpretation in one method
            interpret(line);
        }
        
        public void interpret(String line)
        {
            // TODO
            /* Matcher m = ERROR_WITH_FILENAME.matcher(line);
            if(m.find())
            {
                String file = m.group(1);
                String lineStr = m.group(2);
                String message = m.group(3);
                int lineNum = null != lineStr ? Integer.parseInt(lineStr) : -1;
                return;
            } */
        }
    }
    
    private final IBuildManager buildMgr;
    private final IExecutionMonitor executor;
    
    public DmdCompileManager(IBuildManager buildMgr)
    {
        this.buildMgr = buildMgr;
        this.executor = buildMgr.getExecutionMonitor();
    }

    /* (non-Javadoc)
     * @see descent.building.compiler.ICompileManager#compile(descent.building.compiler.IObjectFile[], org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean compile(IObjectFile[] objectFiles, final IProgressMonitor pm)
    {
        try
        {
            pm.beginTask("Compiling object files", objectFiles.length);
            IExecutionCallback markProgress = new IExecutionCallback() { public void taskCompleted(int result) { pm.worked(1); } };
            
            System.out.println("----- DmdCompileManager#compile ---");
            for(IObjectFile obj : objectFiles)
            {
                System.out.println(obj);
                executor.exec(getCommand(obj), new DmdResponseInterpreter(obj), markProgress);
            }
            executor.syncPreviousTasks(pm);
            return false;
        }
        finally
        {
            pm.done();
        }
    }
    
    private String getCommand(IObjectFile obj)
    {
        // TODO
        StringBuilder cmd = new StringBuilder();
        cmd.append("dmd ");
        cmd.append(obj.getInputFile());
        cmd.append(" -c");
        cmd.append(" -of" + obj.getOutputFile().getAbsolutePath());
        for(File importPath : buildMgr.getImportPaths())
        {
            cmd.append(" -I");
            cmd.append(importPath.toString());
        }
        
        return cmd.toString();
    }
}
