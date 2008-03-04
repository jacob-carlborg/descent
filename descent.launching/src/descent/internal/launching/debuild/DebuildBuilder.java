package descent.internal.launching.debuild;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import descent.launching.IExecutableTarget;
import descent.launching.BuildProcessor.BuildCancelledException;
import descent.launching.BuildProcessor.BuildFailedException;

/**
 * The main engine of the descent remote builder. Given an executable target
 * (info on what type of executable is needed) and a project, performs the
 * build. The publuc interface of this class can be accessed via the
 * {@link #build(IExecutableTarget, IProgressMonitor)} method,
 * which will initiat a build.
 * 
 * @author Robert Fraser
 */
public class DebuildBuilder
{
	/**
	 * Public interface to the debuild builder, which initiates a new build
	 * based on the given executable target. The target
	 * should contain information on what is to be built. Returns the path to
	 * the executable file if one is built (or already exists in the project).
	 * Will always return non-null, and will throw either a BuildFailedException
	 * or a BuildCancelledException if the buikd does not succeed.
	 * 
	 * @param target information about the target executable to be built
	 * @param pm     a monitor to track the progress of the build
	 * @return       the path to the executable file,
	 */
	public static String build(IExecutableTarget target, IProgressMonitor pm)
	{
		DebuildBuilder builder = new DebuildBuilder(new BuildRequest(target));
		return builder.build(pm);
	}
	
	/* package */ static final boolean DEBUG = true;
	
	private final BuildRequest req;
	private final ErrorReporter err;
	private String[] importPath;
	private ObjectFile[] objectFiles;
	
	private DebuildBuilder(BuildRequest req)
	{
		this.req = req;
		this.err = new ErrorReporter(req.getProject());
	}
	
	private String build(IProgressMonitor pm)
	{
		if(null == pm)
			pm = new NullProgressMonitor();
		
		if(pm.isCanceled())
			throw new BuildCancelledException();
		
		try
		{
			pm.beginTask("Building D application", 100);
			
			// Usually, a little work has been done by now. Move the progress bar to keep the
			// user in a pleasent and productive mood
			pm.worked(5);
			
			// First, create the import path from the project properties, etc.
			importPath = createImportPath(new SubProgressMonitor(pm, 5));
			
			// Then, recursively collect dependancies for all the object files
			objectFiles = RecursiveDependancyCollector.getObjectFiles(
					req.getProject(),
					req.getCompilationUnits(),
					new SubProgressMonitor(pm, 40));
			
			// TODO
			throw new BuildFailedException();
		}
		finally
		{
			pm.done();
		}
	}
	
	
	private String[] createImportPath(IProgressMonitor pm)
	{
		try
		{
			pm.beginTask("Building import path", 5);
			
			// TODO
			return new String[] {};
		}
		finally
		{
			pm.done();
		}
	}
}

/*
id = new Identifier(name, 0);
m = new Module((char *) files.data[i], id, global.params.doDocComments, global.params.doHdrGeneration);
global.cmodules->push(m);
}

// Read files, parse them
for (i = 0; i < global.cmodules->dim; i++)
{
m = (Module *)global.cmodules->data[i];
if (global.params.verbose)
    printf("parse     %s\n", m->toChars());
if (!Module::rootModule)
    Module::rootModule = m;
m->importedFrom = m;
//m->deleteObjFile();
m->read(0);
m->parse();
if (m->isDocFile)
{
    m->gendocfile();

    // Remove m from list of modules
    global.cmodules->remove(i);
    i--;
}
}
if (global.errors)
fatal();
#ifdef _DH
if (global.params.doHdrGeneration)
{
/* Generate 'header' import files.
 * Since 'header' import files must be independent of command
 * line switches and what else is imported, they are generated
 * before any semantic analysis.
 * /
for (i = 0; i < global.cmodules->dim; i++)
{
    m = (Module *)global.cmodules->data[i];
    if (global.params.verbose)
        printf("import    %s\n", m->toChars());
    m->genhdrfile();
}
}
if (global.errors)
fatal();
#endif

// parse pragmas
for (i = 0; i < global.cmodules->dim; i++)
{
m = (Module *)global.cmodules->data[i];
if (global.params.verbose)
    printf("meta      %s\n", m->toChars());
m->parsepragmas();
}
if (global.errors)
fatal();

class GroupedCompile {
    public:
    Array imodules, ofiles;
    Array origonames, newonames;
};
Array GroupedCompiles;
GroupedCompiles.push((void *) new GroupedCompile);

// Generate compile commands
for (i = 0; i < global.cmodules->dim; i++)
{
m = (Module *)global.cmodules->data[i];
    GroupedCompile *gc;
    
    int renames = 0; // rename count
    
    if (global.params.fullqobjs)
    {
        // find the right compile to add this to
        unsigned int cmp;
        if (global.params.oneatatime) {
            cmp = GroupedCompiles.dim;
        } else {
            for (cmp = 0; cmp < GroupedCompiles.dim; cmp++) {
                gc = (GroupedCompile *) GroupedCompiles.data[cmp];
            
                if (!stringInArray(&(gc->ofiles), m->objfile->name->str)) {
                    // add it
                    gc->ofiles.push((void *) m->objfile->name->str);
                    break;
                }
            }
        }
        if (cmp == GroupedCompiles.dim) {
            gc = new GroupedCompile;
            GroupedCompiles.push(gc);
            gc->ofiles.push((void *) m->objfile->name->str);
        }
        
        // the output file name is guessable now that we have the module name
        if (m->md) {
            const char *mname = m->md->id->string;
            
            // add the module name
            char *ofname = (char *) mem.malloc(strlen(mname) + strlen(global.obj_ext) + 2);
            sprintf(ofname, "%s.%s", mname, global.obj_ext);
            
            // for docs as well
            char *odname = (char *) mem.malloc(strlen(mname) + 6);
            sprintf(odname, "%s.html", mname);
            char *origdname = mem.strdup(odname);
            
            // figure out what we should really be using to combine them
            std::string sep = ".";
            if (masterConfig.find("") != masterConfig.end() &&
                masterConfig[""].find("objmodsep") != masterConfig[""].end())
                sep = masterConfig[""]["objmodsep"];
            
            // now add all the package names
            Array *packages = m->md->packages;
            if (packages) {
                for (int j = packages->dim - 1; j >= 0; j--) {
                    Identifier *id = (Identifier *) packages->data[j];
                    
                    char *newfname = (char *) mem.malloc(strlen(id->string) + strlen(ofname) + 2);
                    sprintf(newfname, "%s%s%s", id->string, sep.c_str(), ofname);
                    mem.free(ofname);
                    ofname = newfname;
                    
                    char *newdname = (char *) mem.malloc(strlen(id->string) + strlen(odname) + 2);
                    sprintf(newdname, "%s.%s", id->string, odname);
                    mem.free(odname);
                    odname = newdname;
                }
            } else {
                // to make sure there's no overlap, always add something
                char *newfname = (char *) mem.malloc(strlen(ofname) + 2);
                sprintf(newfname, "_%s", ofname);
                mem.free(ofname);
                ofname = newfname;
                odname = NULL;
            }
            
            // then add the objdir
            char *newofname = FileName::combine(global.params.objdir, ofname);
            mem.free(ofname);
            ofname = newofname;
            
            if (global.params.docdir) {
                if (odname) {
                    char *newodname = FileName::combine(global.params.docdir, odname);
                    mem.free(odname);
                    odname = newodname;
                }
                
                char *neworigdname = FileName::combine(global.params.docdir, origdname);
                mem.free(origdname);
                origdname = neworigdname;
            }
            
            // make sure the name gets changed later
            gc->origonames.push((void *) m->objfile->name->str);
            gc->newonames.push((void *) ofname);
            renames++;
            
            m->objfile = new File(ofname);
            
            // as well as the doc names (if applicable)
            if (global.params.fullqdocs && odname) {
                gc->origonames.push((void *) origdname);
                gc->newonames.push((void *) odname);
                renames++;
                
            } else {
                mem.free(origdname);
                mem.free(odname);
                
            }
            
        } else {
            // ignore gcstats (argh)
            if (strcmp(m->srcfile->name->name(), "gcstats.d") &&
                !global.params.listonly &&
                !global.params.listfiles &&
                !global.params.listnffiles) {
                fprintf(stderr, "WARNING: Module %s does not have a module declaration. This can cause problems\n"
                                "         with rebuild's -oq option. If an error occurs, fix this first.\n",
                        m->srcfile->name->name());
            }
            
            // then rename it to nmd_<name>, to at least try to avoid conflicts
            char *ofname = (char *) mem.malloc(
                strlen(m->objfile->name->name()) + 5);
            sprintf(ofname, "nmd_%s", m->objfile->name->name());
            char *oname = FileName::combine(global.params.objdir,
                                            ofname);
            mem.free(ofname);
            
            gc->origonames.push((void *) m->objfile->name->str);
            gc->newonames.push((void *) oname);
            renames++;
            m->objfile = new File(oname);
            
        }
    } else {
        // just add it
        gc = (GroupedCompile *) GroupedCompiles.data[0];
        gc->ofiles.push((void *) m->objfile->name->str);
    }
    
    char ignore = 0;
    // don't generate if we should ignore this module
    if (m->nolink)
        ignore = 1;
    if (m->md) {
        std::string modname = m->md->id->string;
        if (m->md->packages) {
            for (int j = m->md->packages->dim - 1; j >= 0; j--) {
                modname = std::string(((Identifier *) m->md->packages->data[j])->string) +
                    "." + modname;
            }
        }
            
        if (masterConfig.find("") != masterConfig.end() &&
            masterConfig[""].find("ignore") != masterConfig[""].end()) {
            std::string modIgnoreList = masterConfig[""]["ignore"];
            
            // split by ' '
            while (modIgnoreList.length()) {
                std::string modIgnore;
                int loc = modIgnoreList.find(' ', 0);
                
                if (loc == std::string::npos) {
                    modIgnore = modIgnoreList;
                    modIgnoreList = "";
                } else {
                    modIgnore = modIgnoreList.substr(0, loc);
                    modIgnoreList = modIgnoreList.substr(loc + 1);
                }
                    
                // check it
                if (modname.substr(0, modIgnore.length()) == modIgnore) {
                    ignore = 1;
                }
            }
        }
    }
    
if (global.params.obj) {
        if (!ignore) {
            if (!global.params.objfiles)
                global.params.objfiles = new Array();
            global.params.objfiles->push(m->objfile->name->str);
            global.params.genobjfiles->push(m->objfile->name->str);
        }
        
        // figure out the most recent dependency
        struct stat sbuf;
        time_t newest = 0;
        Array modsToTest;
        if (!global.params.fullbuild) {
            modsToTest.push((void *) m);
            for (unsigned int j = 0; j < modsToTest.dim; j++) {
                Module *mtest = (Module *) modsToTest.data[j];
                
                // test this dependency
                if (stat(mtest->srcfile->name->str, &sbuf) == 0) {
                    if (sbuf.st_mtime > newest)
                        newest = sbuf.st_mtime;
                }
                
                // now add its dependencies
                for (unsigned int k = 0; k < mtest->aimports.dim; k++) {
                    // check if it's already there
                    for (unsigned int l = 0; l < modsToTest.dim; l++) {
                        if (mtest->aimports.data[k] ==
                            modsToTest.data[l]) goto noAddDep;
                    }
                    modsToTest.push(mtest->aimports.data[k]);
                    noAddDep: 0;
                }
            }
        }
        
        if (!ignore && global.params.listobjfiles)
            printf("%s\n", m->objfile->name->str);
        
        // now check if we should ignore it because of its age
        if (!global.params.fullbuild &&
            newest != 0 &&
            stat(m->objfile->name->str, &sbuf) == 0 &&
            newest < sbuf.st_mtime) {
            ignore = 1;
        }
        
    }
    
    if (!ignore) {
        gc->imodules.push((void *) m);
    } else if (global.params.fullqobjs) {
        // we generated a rename, so remove it
        for (; renames > 0; renames--) {
            gc->origonames.pop();
            gc->newonames.pop();
        }
    }
    
if (global.params.verbose)
    printf("code      %s\n", m->toChars());
    
    /* if (global.params.doDocComments)
        m->gendocfile(); * /
    
    // now possibly reflect this and add the reflected module as well
    if (global.params.reflect && m->md) {
        if (!(m->md->packages) ||
            strcmp(((Identifier *) m->md->packages->data[0])->string,
                   "reflected") != 0) {
            // this isn't a reflected module: reflect it and add the reflected module
            std::string cmd = "drefgen ";
            cmd += m->srcfile->name->str;
            if (system(cmd.c_str()) != 0)
                error("Failed to reflect %s", m->srcfile->name->str);
            
            if (global.params.verbose)
                printf("reflect   %s\n", m->toChars());
            
            // get the new filename
            std::string fn = "reflected" DIRSEP;
            if (m->md->packages) {
                for (int pkg = 0; pkg < m->md->packages->dim; pkg++) {
                    fn += ((Identifier *) m->md->packages->data[pkg])->string;
                    fn += DIRSEP;
                }
            }
            fn += m->md->id->string;
            fn += ".";
            fn += global.mars_ext;
            
            // now add the module to the list
            global.cmodules->push(new Module(
                mem.strdup(fn.c_str()), m->md->id, global.params.doDocComments, global.params.doHdrGeneration));
        }
    }
}


// Generate candydoc modules.ddoc if requested
if (global.params.candydoc &&
    global.params.docdir) {
    char *modulesddoc = (char *) mem.malloc(strlen(global.params.docdir) +
                                            24);
    sprintf(modulesddoc, "%s" DIRSEP "candydoc" DIRSEP "modules.ddoc", global.params.docdir);
    
    // format: MODULES =\n\t$(MODULE ...)\n\t$(MODULE ...)
    FILE *mddf = fopen(modulesddoc, "w");
    if (!mddf) {
        error("Failed to open candydoc/modules.ddoc");
    } else {
        fprintf(mddf, "MODULES =\n");
        
        for (i = 0; i < GroupedCompiles.dim; i++) {
            GroupedCompile *gc = (GroupedCompile *) GroupedCompiles.data[i];
            
            for (unsigned int j = 0; j < gc->imodules.dim; j++) {
                Module *m = (Module *) gc->imodules.data[j];
                if (m->md) {
                    if (global.params.fullqdocs)
                        fprintf(mddf, "\t$(MODULE_FULL %s)\n", m->md->toChars());
                    else
                        fprintf(mddf, "\t$(MODULE %s)\n", m->md->toChars());
                }
            }
        }
    }
    fclose(mddf);
    
    // now add candydoc to the compile flags
    compileFlags += " ";
    compileFlags += global.params.docdir;
    compileFlags += DIRSEP "candydoc" DIRSEP "candy.ddoc ";
    compileFlags += global.params.docdir;
    compileFlags += DIRSEP "candydoc" DIRSEP "modules.ddoc";
}

mem.fullcollect();

// Now do the actual compilation
for (unsigned int j = 0; global.params.obj && j < GroupedCompiles.dim; j++) {
    GroupedCompile *gc = (GroupedCompile *) GroupedCompiles.data[j];
    
    if (gc->imodules.dim == 0) continue;
    
    // make a string of the file names
    std::string infiles;
    for (unsigned int k = 0; k < gc->imodules.dim; k++) {
        Module *m = (Module *) gc->imodules.data[k];
        infiles += m->srcfile->name->str;
        infiles += " ";
    }
    
    // then compile
    runCompile(infiles);
    
    // and rename
    for (unsigned int k = 0; k < gc->origonames.dim; k++) {
        if (global.params.listonly) {
            printf("mv -f %s %s\n",
                   gc->origonames.data[k], gc->newonames.data[k]);
            
        } else {
            if (access((char *) gc->origonames.data[k], F_OK) == 0) {
                if (global.params.verbose)
                    printf("rename    %s to %s\n",
                           (char *) gc->origonames.data[k],
                           (char *) gc->newonames.data[k]);
                
                remove((char *) gc->newonames.data[k]); // ignore errors
                rename((char *) gc->origonames.data[k],
                       (char *) gc->newonames.data[k]); // ignore errors
            }
        }
    }
}

mem.fullcollect();

//backend_term();
if (global.errors)
fatal();

if (!global.params.objfiles->dim)
{
if (global.params.link)
    error("no object files to link");
}
else
{
if (global.params.link)
        status = runLINK();
    
    if (global.params.clean)
        runClean();

if (global.params.run)
{
    if (!status)
    {
        status = runProgram();

        /* Delete .obj files and .exe file
         * /
        for (i = 0; i < global.cmodules->dim; i++)
        {
            m = (Module *)global.cmodules->data[i];
            m->deleteObjFile();
        }
        deleteExeFile();
    }
}
}

return status;
}
*/