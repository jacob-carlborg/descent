package descent.ant.tasks;

import java.io.*;

public class WindowsFilenameConverter {
    
	private static boolean debug = false;
    
    public static String checkForSpaces(String filename) throws FileNotFoundException {
        String cmd;
        String dir = null;
        int endSlashIndex;
        int beginSlashIndex;
        int spaceIndex = -1;
        WindowsFilenameFormatter formatter;
        Process process;
        boolean atEnd = false;
        String newFilename = filename;
        String backslash = "\\";
        
        while(newFilename.indexOf(' ') > -1) { // got a name with spaces in it
            
            if(debug) {
                System.out.println("Converting filename" + newFilename);
            }
            
            spaceIndex = newFilename.indexOf(' ');
            endSlashIndex = newFilename.indexOf(backslash, spaceIndex);
            
            if(endSlashIndex == -1) {
                endSlashIndex = newFilename.length();
                atEnd = true;
            }
            
            dir = newFilename.substring(0, endSlashIndex);
            beginSlashIndex = dir.lastIndexOf(backslash);
            
            formatter = new WindowsFilenameFormatter();
            formatter.setFilename(dir);
            
            try {
                cmd = formatter.getCommandLine("dir /X " +
                dir.substring(0, dir.lastIndexOf(backslash) + 1));
                
                if(debug) {
                    System.out.println("Process command [" + cmd + "] ...");
                }
                
                process = Runtime.getRuntime().exec(cmd);
                formatter.setInputStream(process.getInputStream());
                
                formatter.start();
                process.waitFor();
                
                if(!atEnd) {
                    newFilename = formatter.getFilename() + backslash +
                    newFilename.substring(endSlashIndex + 1);
                } else {
                    newFilename = formatter.getFilename();
                }
                
            } catch(IOException ioe) {
                ioe.printStackTrace();
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        if(debug) {
            System.out.println("Converted filename = "+newFilename);
        }
        return newFilename;
    }
    
    static class  WindowsFilenameFormatter extends Thread {
        InputStream in;
        String filename;
        String directory;
        String file;
        String osName;
        
        public void setInputStream(InputStream inputStream) {
            this.in = inputStream;
        }
        
        private String getCommandLine(String arg) {
            osName = System.getProperty("os.name").toLowerCase();
            String cmd = "";
            
            if((osName.indexOf("nt") > -1) ||
            (osName.indexOf("windows 2000") > -1) ||
            (osName.indexOf("windows xp") > -1)) {
                cmd = "cmd.exe /C " + arg;
            } else if(osName.indexOf("windows 9") > -1) {
                cmd = "command.com /C " + arg;
            } else {
                cmd = arg;
            }
            
            return(cmd);
        }
        
        public void setFilename(String filename) throws FileNotFoundException {
            this.filename = filename;
            File f = new File( filename );
            if( !f.exists() ){
            	throw new FileNotFoundException( f.getAbsolutePath() );
            }
            directory = filename.substring(0, filename.lastIndexOf("\\"));
            file = filename.substring(filename.lastIndexOf("\\") + 1,
            filename.length());
            
            if(debug) {
                System.out.println("directory = " + directory);
                System.out.println("file = " + file);
            }
        }
        
        public String getFilename() {
            return directory + "\\" + file;
        }
        
        public void run() {
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            String[] dirInfo;
            
            try {
                while((line = br.readLine()) != null) {
                    if(line.indexOf(file) != -1) {
                        dirInfo = line.split("\\s+");
                        if(osName.indexOf("windows xp") > -1) {
                            file = dirInfo[4];
                        } else {
                            file = dirInfo[3]; // should be the 8.3 formatted filename
                        }
                        
                        continue;
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
