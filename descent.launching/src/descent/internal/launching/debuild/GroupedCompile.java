package descent.internal.launching.debuild;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;

/**
 * Groups one or more object files together to be executed as a single compilation
 * command (note that they're not actually object files, instead source files
 * :-)).
 * 
 * @author Robert Fraser
 */
/* package */ abstract class GroupedCompile implements Iterable<ObjectFile>
{
    public abstract Iterator<ObjectFile> iterator();
    public abstract int size();
}

/**
 * A compile containing only a single file. This is a separate class for memory
 * efficiency reasons, since an ArrayList for one file is a big investment, and
 * in one-at-a-time compile mode, there will be quite a few single-file
 * compiles.
 * 
 * @author Robert Fraser
 */
/* package */ class SingleFileCompile extends GroupedCompile
{
    private final ObjectFile objectFile;
    
    /**
     * Create a new single-file compilation group with the given object file
     * 
     * @param objectFile
     */
    public SingleFileCompile(ObjectFile objectFile)
    {
        this.objectFile = objectFile;
    }
    
    public int size()
    {
        return objectFile == null ? 0 : 1;
    }
    
    @Override
    public Iterator<ObjectFile> iterator()
    {   
        return new Iterator<ObjectFile>()
        {
            public boolean hasNext = objectFile != null;
            
            public boolean hasNext()
            {
                return hasNext;
            }

            public ObjectFile next()
            {
                hasNext = false;
                return objectFile;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }
    
}

/**
 * A compile that groups multiple files
 * 
 * @author Robert Fraser
 */
/* package */ class MultiFileCompile extends GroupedCompile
{
    private final List<ObjectFile> objectFiles = new ArrayList<ObjectFile>();
    
    /**
     * Create a new empty multi-file compile
     */
    public MultiFileCompile()
    {
        // Nothing to do
    }
    
    /**
     * Adds the object file to this command's list of object files if and only
     * if the object file hasn't already been added.
     * 
     * @param obj the object file to add
     */
    public void add(ObjectFile obj)
    {
        if(DebuildBuilder.DEBUG)
            Assert.isTrue(obj.shouldBuild());
        
        if(!objectFiles.contains(obj))
            objectFiles.add(obj);
    }
    
    public Iterator<ObjectFile> iterator()
    {
        return objectFiles.iterator();
    }
    
    public int size()
    {
        return objectFiles.size();
    }
}