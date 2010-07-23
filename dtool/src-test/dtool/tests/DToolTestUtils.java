package dtool.tests;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import melnorme.miscutil.StreamUtil;

public class DToolTestUtils {
	
	public static <T extends Exception> void throwIf(boolean condition, String message) throws RuntimeException {
		if(condition) {
			throw new RuntimeException(message);
		}
	}
	
	public static void unzipFile(File zipFile, File parentDir) throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		try {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			
			while(entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				
				File entryFile = new File(parentDir, entry.getName());
				
				if(entry.isDirectory()) {
					entryFile.mkdirs();
					continue;
				}
				
				entryFile.getParentFile().mkdirs();
				
				StreamUtil.copyStream(zip.getInputStream(entry), 
						new BufferedOutputStream(new FileOutputStream(entryFile)), true);
			}
			
		} finally {
			zip.close();
		}
	}
	
	public static void deleteDir(File dir) {
		if(!dir.exists()) 
			return;
		
		File[] listFiles = dir.listFiles();
		for(File childFile : listFiles) {
			if(childFile.isFile()) {
				childFile.delete();
			} else {
				deleteDir(childFile);
			}
		}
		if(dir.delete() == false) {
			throw new RuntimeException("Failed to delete dir");
		}
	}
	
}
