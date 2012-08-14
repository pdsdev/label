package pds.label;

import java.io.*;
import java.util.*;

/**
 * PDSUtil is a class that contains utility methods which are useful for
 * working with instances of PDSLabel and PDS labeled products.
 *
 * @author      Todd King
 * @author      Planetary Data System
 * @version     1.0, 04/10/03
 * @since       1.0
 */
public class PDSUtil {
 	/** Array of lines of text */
 	ArrayList<String>		mLine = new ArrayList<String>();
 	
 	/** PDS convention for a newline (carriage return followed by newline) */
 	String			mNewline = "\r\n";
 	
 	/** Creates and instance of a PDSUtil */
 	public PDSUtil() {
 	}
 	
    /** 
     * Reads the contents of a file and pads each line to the have
     * the given length. Once the file is read and the lines padded,
     * the new lines are written back to the same file. The length of
     * the new lines includes the PDS standard line terminate which
     * is the two byte sequence of carriage return and newline.
	 *
     * @param pathName  the fully qualified path and name of the file to parse.
     * @param length	the number of characters each line must have.
     *
     * @return          <code>true</code> if the file could be opened;
     *                  <code>false</code> otherwise.
     * @since           1.0
     */
 	public boolean padFile(String pathName, int length) {
 		if(!loadFile(pathName)) return false;
 		trimRight();
 		pad(length - mNewline.length());
 		return writeFile(pathName);
 	}
 
    /** 
     * Pad each line with spaces to the given length. 
	 *
     * @param length	the number of characters each line must have.
     *
     * @since           1.0
     */
 	public void pad(int length) {
 		String			buffer = "";
 		String			spaces = "";
		int				n;
 		
 		for(int i = 0; i < length; i++) spaces += " ";
	
		for(int i = 0; i < mLine.size(); i++) {
			buffer = mLine.get(i).toString();
			n = length - buffer.length();
			if(n > 0) mLine.set(i, buffer + spaces.substring(0, n));
		}
 	}
 	
    /** 
     * Trim any white space from the right side of each line.
	 *
     *
     * @since           1.0
     */
 	public void trimRight() {
 		String			buffer = "";
		int				i, j;
 		
		for(i = 0; i < mLine.size(); i++) {
			buffer = mLine.get(i).toString();
			buffer = buffer.trim();
			if(buffer.length() == 0) { mLine.set(i, ""); continue; }
			buffer = mLine.get(i).toString();
			for(j = buffer.length() - 1; j >= 0; j--) {
				if(!Character.isWhitespace(buffer.charAt(j))) {
					mLine.set(i, buffer.substring(0, j+1));
					break;
				}
			}
		}
 	}
 	
    /** 
     * Reads the contents of a file as a set of text strings terminated
     * with a newline character.
	 *
     * @param pathName  the fully qualified path and name of the file to parse.
     *
     * @return          <code>true</code> if the file could be opened;
     *                  <code>false</code> otherwise.
     * @since           1.0
     */
 	public boolean loadFile(String pathName) {
 		// Open file and read contents
		try {
			BufferedReader	file = new BufferedReader(new FileReader(pathName));
			String			buffer;
			
			while ((buffer = file.readLine()) != null){
				mLine.add(buffer);
			}

			file.close();
		} catch(Exception e) { return false; }
		
		return true;
 	}

    /** 
     * Writes the lines to a file. When written ach line is terminated
     * with the PDS newline convention (carriage return followed by newline)
	 *
     * @param pathName  the fully qualified path and name of the file to parse.
     *
     * @return          <code>true</code> if the file could be opened;
     *                  <code>false</code> otherwise.
     * @since           1.0
     */
 	public boolean writeFile(String pathName) {
		// Pad lines and write output to same file
		try{
			PrintStream file = new PrintStream(new FileOutputStream(pathName, false));
			String		buffer;
			
			for(int i = 0; i < mLine.size(); i++) {
				buffer = mLine.get(i).toString();
				file.print(buffer);
				file.print(mNewline);
			}
			
			file.close();
		} catch(Exception e) { return false; }
		
		return true;
 	}
}
 
 	
