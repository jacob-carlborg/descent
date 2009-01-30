package descent.ui.metrics.properties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import descent.ui.metrics.MetricsPlugin;

public final class ExcludedResources {
    private static final String EXCLUDED_RESOURCES_FILENAME = "excludes";
    private static final String EXCLUDED_FILE_PREFIX = "<file>";
    private static final String EXCLUDED_REGEX_PREFIX = "<regex>";

    private boolean changed;
    private IFile[] excludedFiles;
    private Map regexToPatternMap;
    private Perl5Compiler regexCompiler;
    private Perl5Matcher matcher;
    private IProject project;

    public ExcludedResources(IProject project) throws IOException {
        this.project = project;

        regexCompiler = new Perl5Compiler();
        matcher = new Perl5Matcher();

        initializeExcludedResources();
        changed = false;
    }

    public void setExcludedFiles(IFile[] files) {
        excludedFiles = new IFile[0];
        for (int i = 0; i < files.length; i++) {
            addFile(files[i]);
        }
    }

    public IFile[] getFiles() {
        return excludedFiles;
    }

    public void removeRegex(String regex) {
        regexToPatternMap.remove(regex);
    }

    public void removeFile(IFile file) {
        for (int i = 0; i < excludedFiles.length; i++) {
            if (excludedFiles[i].equals(file)) {
                removeFileAt(i);
                return;
            }
        }
    }

    private void removeFileAt(int index) {
        IFile[] newExcludedFiles = new IFile[excludedFiles.length - 1];
        if (index == 0) {
            System.arraycopy(excludedFiles, 1, newExcludedFiles, 0, newExcludedFiles.length);
        } else if (index == newExcludedFiles.length) {
            System.arraycopy(excludedFiles, 0, newExcludedFiles, 0, newExcludedFiles.length);
        } else {
            System.arraycopy(excludedFiles, 0, newExcludedFiles, 0, index);
            System.arraycopy(excludedFiles, index + 1, newExcludedFiles, index, newExcludedFiles.length - index);
        }

        excludedFiles = newExcludedFiles;
    }

    public boolean hasRegex(String regex) {
        return regexToPatternMap.containsKey(regex);
    }

    public boolean isValidRegex(String regex) {
        try {
            regexCompiler.compile(regex);
            return true;
        } catch (MalformedPatternException mpex) {
            return false;
        }
    }

    public void addRegex(String regex) throws MalformedPatternException {
        Pattern pattern = regexCompiler.compile(regex);
        regexToPatternMap.put(regex, pattern);
        changed = true;
    }

    public String[] getRegexes() {
        return (String[]) regexToPatternMap.keySet().toArray(new String[regexToPatternMap.size()]);
    }

    public boolean contains(IFile file) {
        return containsFile(file) || containsMatchingRegex(file.getProjectRelativePath().toString());
    }

    public IProject getProject() {
        return project;
    }

    private boolean containsFile(IFile file) {
        for (int i = 0; i < excludedFiles.length; i++) {
            if (excludedFiles[i].equals(file)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsMatchingRegex(String path) {
        if (regexToPatternMap.size() > 0) {
            Iterator iter = regexToPatternMap.values().iterator();
            while (iter.hasNext()) {
                if (matcher.matches(path, (Pattern) iter.next())) {
                    return true;
                }
            }
        }

        return false;
    }

    public void store() throws CoreException {
        if (!changed) {
            return;
        }

        try {
            storeChanges();
        } catch (IOException ioex) {
            throw new CoreException(new Status(IStatus.WARNING, MetricsPlugin.PLUGIN_ID, IStatus.OK, "Failed to save the excluded resources list", ioex));
        }

        changed = false;
    }

    private void storeChanges() throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(getFile())));
        writePrefixedItems(writer, ExcludedResources.EXCLUDED_FILE_PREFIX, getExcludedFilePathCollection());
        writePrefixedItems(writer, ExcludedResources.EXCLUDED_REGEX_PREFIX, regexToPatternMap.keySet());
        writer.close();
    }

    private Collection getExcludedFilePathCollection() {
        Collection collection = new HashSet(excludedFiles.length);
        for (int i = 0; i < excludedFiles.length; i++) {
            collection.add(excludedFiles[i].getProjectRelativePath().toString());
        }

        return collection;
    }

    private void writePrefixedItems(PrintWriter writer, String prefix, Collection items) {
        Iterator iter = items.iterator();
        while (iter.hasNext()) {
            writer.println(prefix + iter.next());
        }
    }

    private void initializeExcludedResources() throws IOException {
        excludedFiles = new IFile[0];
        regexToPatternMap = new HashMap();

        File file = getFile();
        if (!file.exists()) {
            return;
        }

        initializeExcludedResourcesFromFile(file);
    }

    private void initializeExcludedResourcesFromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                initializeExcludedResourceFromFileLine(line.trim());
            }
        } finally {
            reader.close();
        }
    }

    private void initializeExcludedResourceFromFileLine(String line) {
        if (line.startsWith(ExcludedResources.EXCLUDED_FILE_PREFIX)) {
            addPath(line.substring(ExcludedResources.EXCLUDED_FILE_PREFIX.length()));
        } else if (line.startsWith(ExcludedResources.EXCLUDED_REGEX_PREFIX)) {
            try {
                addRegex(line.substring(ExcludedResources.EXCLUDED_REGEX_PREFIX.length()));
            } catch (MalformedPatternException mpex) {
                // Ignore the pattern
            }
        } else {
            addPath(line);
        }
    }

    private void addPath(String stringPath) {
        IFile file = project.getFile(new Path(stringPath));
        if (file.exists()) {
            addFile(file);
        }
    }

    private void addFile(IFile file) {
        if (file.getFileExtension() == null || !file.getFileExtension().equals("d")) {
            return;
        }

        IFile[] newFiles = new IFile[excludedFiles.length + 1];
        System.arraycopy(excludedFiles, 0, newFiles, 0, excludedFiles.length);
        newFiles[excludedFiles.length] = file;
        excludedFiles = newFiles;
        changed = true;
    }

    private File getFile() {
        return project.getWorkingLocation(MetricsPlugin.PLUGIN_ID).append(EXCLUDED_RESOURCES_FILENAME).toFile();
    }
}
