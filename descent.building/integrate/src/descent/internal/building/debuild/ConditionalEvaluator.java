package descent.internal.building.debuild;

import java.util.Collection;

import descent.core.IConditional;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;

/**
 * Class designed to evaluate conditional compilation expressions (verison, debug
 * and hopefully static if statements)
 * 
 * @author Robert Fraser
 */
/* package */ final class ConditionalEvaluator
{
    private BuildRequest req;
    
    public ConditionalEvaluator(BuildRequest req)
    {
        this.req = req;
    }
    
    public boolean isActive(IConditional cond) throws JavaModelException
    {
        // TODO figure this part out
        if(cond.isStaticIfDeclaration() || cond.isIftypeDeclaration())
            return true;
        
        String id = cond.getElementName().trim();
        
        // Check for predefined versions
        if(cond.isVersionDeclaration() && BuilderUtil.isPredefinedVersion(id))
            return evalPredefinedVersion(id);
        
        // Check for version/debug "levels"
        if(Character.isDigit(id.charAt(0)))
        {
            try
            {
                int value = Integer.parseInt(id);
                Integer level = cond.isVersionDeclaration() ? req.getVersionLevel() :
                        req.getDebugLevel();
                if(null == level)
                    return false;
                return value <= level;
            }
            catch(NumberFormatException e)
            {
                // An invalid identifier = never true
                return false;
            }
        }
        
        // Check for identifiers
        Collection<String> searchSpace = cond.isVersionDeclaration() ?
                req.getVersionIdents() : req.getDebugIdents();
        return searchSpace.contains(id);
    }
    
    private boolean evalPredefinedVersion(String id)
    {
        // TODO
        return false;
    }
}
