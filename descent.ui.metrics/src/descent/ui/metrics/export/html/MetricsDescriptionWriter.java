package descent.ui.metrics.export.html;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.MetricsPlugin;

class MetricsDescriptionWriter {
    private final File directory;

    public MetricsDescriptionWriter(File directory) {
        this.directory = directory;
    }
    
    public void write(IProgressMonitor monitor) {
        monitor.subTask("Copying metric descriptions");
        
        Enumeration enumeration = MetricsPlugin.getDefault().getBundle().getEntryPaths("/docs/descriptions/");
        while (enumeration.hasMoreElements()) {
            String entryPath = (String) enumeration.nextElement();
            copyToDescriptionsDirectory(entryPath);
        }
        
        monitor.worked(1);
    }

    private void copyToDescriptionsDirectory(String entryPath) {
        URL entry = MetricsPlugin.getDefault().getBundle().getEntry(entryPath);
        try {
            copyToDescriptionsDirectory(entry);
        } catch (IOException e) {
            MetricsPlugin.log(e);
        }
    }

    private void copyToDescriptionsDirectory(URL entry) throws IOException {
        String file = new File(entry.getFile()).getName();
        InputStream inputStream = null;
        try {
            inputStream = entry.openStream();
            copyToDirectory(inputStream, file);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    private void copyToDirectory(InputStream inputStream, String name) throws IOException {
        BufferedOutputStream outputStream = null;
        File destination = new File(directory, name);
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(destination), 16384);
            copyContents(inputStream, outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private void copyContents(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bytes = new byte[2048];
        int bytesRead;
        while ((bytesRead = inputStream.read(bytes)) > 0) {
            outputStream.write(bytes, 0, bytesRead);
        }
    }
}
