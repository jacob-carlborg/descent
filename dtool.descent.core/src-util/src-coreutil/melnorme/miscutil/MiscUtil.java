/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.miscutil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MiscUtil {

	/** Combines two hash codes to make a new one. */
	public static int combineHashCodes(int hashCode1, int hashCode2) {
		return hashCode1 * 17 + hashCode2;
	}

	/** Runs a shell command as a Process and waits for it to terminate. 
	 * Assumes the process does not read input. 
	 * @return exit value of the process */
	public static int runShellCommand(String directory, String cmd, String... args) throws IOException {
		ProcessBuilder procBuilder = new ProcessBuilder();
		ArrayList<String> cmdList = new ArrayList<String>(args.length+1);
		cmdList.add(cmd);
		cmdList.addAll(Arrays.asList(args));
		procBuilder.command(cmdList);
		procBuilder.redirectErrorStream(true);
		procBuilder.directory(new File(directory));
	
		Process process = procBuilder.start();
		InputStream inputStream = process.getInputStream();
		int ch;
		while ((ch = inputStream.read()) != -1) {
			//read proccess's stdout and stderr
			if(false)
				System.out.print((char)ch);
		}
		
		try {
			return process.waitFor();
		} catch (InterruptedException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}

	/**
	 * Counts the active flags in the given bitfield
	 */
	public static int countActiveFlags(int bitfield, int[] flags) {
		int count = 0;
		for (int i = 0; i < flags.length; i++) {
			if((bitfield & flags[i]) != 0)
				count++;
		}
		return count;
	}

}
