package descent.core.trace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import descent.internal.core.trace.Fan;
import descent.internal.core.trace.Trace;
import descent.internal.core.trace.TraceNode;

/**
 * A trace parser parses a trace.log file generated by a D compiler.
 */
public class TraceParser {
	
	/**
	 * Parses a trace.log file given a reader.
	 * @param reader a reader
	 * @return the trace
	 * @throws IOException if an IOException happens
	 * @throws ParseException if the trace.log file has an invalid format
	 */
	public ITrace parse(Reader reader) throws IOException, ParseException {
		Trace trace = new Trace();
		
		BufferedReader bReader = new BufferedReader(reader);
		String line;
		String text;
		TraceNode currentNode = null;
		List<Fan> currentFans = new ArrayList<Fan>();
		int lineNumber = 0;
		
	loop:
		while((line = bReader.readLine()) != null) {
			lineNumber++;
			
			// Skip empty lines
			if (line.length() == 0) {
				continue;
			}
			
			char first = line.charAt(0);
			switch(first) {
			
			// The '-' separates trace nodes
			case '-':
			// The "=" ends the fans section
			case '=':
			{
				
				// May be the first '--------' line
				if (currentNode == null) {
					continue;
				}
				
				if (currentFans.size() > 0) {
					currentNode.setFanOut(currentFans.toArray(new IFan[currentFans.size()]));
					currentFans.clear();
				}
				
				trace.addNode(currentNode);
				
				if (first == '=') {
					break loop;
				}				
				break;
			}
				
			// Spaces are in the case of a fan in or fan out
			case ' ':
			case '\t': {
				StringTokenizer tokenizer = new StringTokenizer(line);
				if (tokenizer.hasMoreTokens()) {
					text = tokenizer.nextToken();
					// This should be the number of calls
					try {
						long time = Long.parseLong(text);
						if (tokenizer.hasMoreTokens()) {
							text = tokenizer.nextToken();
							currentFans.add(new Fan(trace, text, time));
						}
					} catch (NumberFormatException e) {
						throw new ParseException("Expecting <number> <signature> in line " + lineNumber, lineNumber);
					}
				}
				break;
			}
				
			default:
				// It's a <signature> <number> <number> <number> line
				StringTokenizer tokenizer = new StringTokenizer(line);
				if (tokenizer.hasMoreTokens()) {
					String signature = tokenizer.nextToken();
					if (tokenizer.hasMoreTokens()) {
						text = tokenizer.nextToken();
						try {
							long numberOfCalls = Long.parseLong(text);
							if (tokenizer.hasMoreTokens()) {
								text = tokenizer.nextToken();
								long ticks = Long.parseLong(text);
								if (tokenizer.hasMoreTokens()) {
									text = tokenizer.nextToken();
									long treeTicks = Long.parseLong(text);
									currentNode = new TraceNode(signature, numberOfCalls, ticks, treeTicks, currentFans.size() == 0 ? null : currentFans.toArray(new IFan[currentFans.size()]));
									currentFans.clear();
								}
							}
						} catch (NumberFormatException e) {
							throw new ParseException("Expecting <signature> <number> <number> <number> in line " + lineNumber, lineNumber);
						}
					}
				}
				break;
			}
		}
		
		while((line = bReader.readLine()) != null) {
			lineNumber++;
			
			// Skip empty lines
			if (line.length() == 0) {
				continue;
			}
			
			for(int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				switch(c) {
				case '0': case '1': case '2': case '3': case '4': case '5':
				case '6': case '7': case '8': case '9':
					// If it's a number, it's a trace node line
					StringTokenizer tokenizer = new StringTokenizer(line);
					if (tokenizer.hasMoreTokens()) {
						text = tokenizer.nextToken();
						try {
							long numCalls = Long.parseLong(text);
							if (tokenizer.hasMoreTokens()) {
								text = tokenizer.nextToken();
								long treeTime = Long.parseLong(text);
								if (tokenizer.hasMoreTokens()) {
									text = tokenizer.nextToken();
									long funcTime = Long.parseLong(text);
									if (tokenizer.hasMoreTokens()) {
										text = tokenizer.nextToken();
										long perCall = Long.parseLong(text);
										if (tokenizer.hasMoreTokens()) {
											text = tokenizer.nextToken();
											
											TraceNode node = (TraceNode) trace.getNode(text);
											if (node == null) {
												throw new ParseException("Signature " + text + " previously not defined in line " + lineNumber, lineNumber);
											}
											
											node.setNumberOfCalls(numCalls);
											node.setTreeTime(treeTime);
											node.setFunctionTime(funcTime);
											node.setFunctionTimePerCall(perCall);
										}
									}
								}
							}
						} catch (NumberFormatException e) {
							throw new ParseException("Expecting <number> <number> <number> <number> <signature> in line " + lineNumber, lineNumber);
						}
					}
					break;
				default:
					// If it's a text, it's a header
					continue;
				}
			}
		}
		
		return trace;
	}

}
