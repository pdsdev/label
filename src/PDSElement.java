package pds.label;

import java.io.*;
import java.util.*;
import java.lang.Character;
/**
 * PDSElement is a class that contains a single definition or line 
 * as specified in the PDS Object Defnition Language (ODL). 
 * In this context a line may be an simple line of text, a block of 
 * commented text, or a keyword/value pair. A value may extend 
 * over more than one physical line if it is quoted or part of 
 * a value set.
 *
 * @author      Todd King
 * @author      Planetary Data System
 * @version     1.0, 02/21/03
 * @since       1.0
 */
public class PDSElement {
 	// Enumeration of possible value type
 	/** Unspecified grouping type. */	public static final int	TYPE_NONE		= 0;
 	/** Ordered list of values. */		public static final int	TYPE_ORDERED	= 1;	
 	/** An unordered list of values. */	public static final int	TYPE_UNORDERED	= 2;
 	
 	/** The text found before the equal sign of an element. If the element does not
 	 * contain and equal sign the value is blank.
 	 */
 	public String		mKeyword = "";
 	
 	/** An array containing a PDSValue object for each value following
 	* 						the equal sign in the element. There may be any number of
 	*						values.
 	*/
 	public ArrayList	mValue = new ArrayList();
 	
 	/** The basic type of the value. Possible values include:
 	 *						<dt>TYPE_NONE</dt><dd>Unspecified grouping type</dd>
 	 *						<dt>TYPE_ORDERED</dt><dd>An ordered list of values</dd>
 	 *						<dt>TYPE_UNORDERED</dt><dd>A unordered list of values</dd>
 	 */
 	public int			mType = TYPE_NONE;
 	
 	/** The comment text found within the element. If the element does not contain a comment the vlaue is blank.
 	 */
 	public String		mComment = "";
 	
 	/** The raw line as read from the file.
 	 */
 	public String		mRaw = "";
 	
 	/** Maximum line length when printing. Really its 80 including the DOS style new line (2 bytes). 
 	 * All lines are wrapped if they exceed this length
 	 */
 	public int					mMaxLength = 78;	
 	
 	/** The count of the number of physical lines parsed into this element
 	 */
 	 public int				mLineCount = 0;
 	 
 	/** Indicates whether a symtax error occurred will parsing or reading a line.
 	 */
 	 public boolean			mSyntaxError = false;
 	 
 	/** Creates an instance of a PDSElement */
 	public PDSElement() {
 	}
 	
    /** 
     * Parses the next element from a file stream. An element
	 * is a single definition or line as specified in the PDS 
	 * Object Defnition Language (ODL). In this context a line 
	 * may be an simple line of text, a block of commented text, 
	 * or a keyword/value pair. A value may extend over more than 
	 * one physical line if it is quoted or part of a value set.
	 * 
     * @param file      the input file stream
     * @return          <code>true</code> if an element was parsed from the stream;
     *                  <code>false</code> if the end of file or an error was encountered.
     * @see             FileInputStream
     * @since           1.0
     */
	public boolean parse(FileInputStream file) {
		int			lastC = 0;
		int			c = 0;
		int			quote = ' ';
		int			i;
		boolean		inQuote = false;
		boolean		inBlock = false;
		boolean		inComment = false;
		boolean		inUnits = false;
		boolean		add = true;
		String		buffer;
		PDSValue	value;
		
		mRaw = readLine(file);
		if(mRaw == null) return false;	// End of file or error
		
		mRaw = mRaw.trim();
		if(mRaw.length() == 0) return true;
				
		// Now parse the raw string
		// Remove comment from string
		inComment = false;
		inQuote = false;
		lastC = 0;
		buffer = "";
		for(i = 0; i < mRaw.length(); i++) {
			add = true;
			c = mRaw.charAt(i);
			if(!inComment) {
				if(c == '"') { if(inQuote) inQuote = false; else inQuote = true; }
			}
			if(!inQuote) {	// Check if in comment
				if(lastC == '/' && c == '*') { buffer = buffer.substring(0, buffer.length() - 1); inComment = true; }
				if(lastC == '*' && c == '/') { inComment = false; add = false; }
			}
			lastC = c;
			if(inComment) { mComment += (char) c; continue; }
			
			if(add) buffer += (char) c;
		}
		if(mComment.length() > 0) mComment = mComment.substring(1);	// Remove leading "*";
		if(mComment.length() > 0) mComment = mComment.substring(0, mComment.length() - 1);	// Remove trailing "*"
		
		buffer = buffer.trim();
		if(buffer.length() == 0) return true;
		
		// Extract keyword and parse value
		i = buffer.indexOf('=');
		if(i == -1) { // Just a word - check if "END"
			mKeyword = buffer; 
			if(mKeyword.compareTo("END") == 0) return false; 
			return true;
		}
		
		mKeyword = buffer.substring(0, i);
		mKeyword = mKeyword.trim();
		buffer = buffer.substring(i+1);
		buffer = buffer.trim();
		if(buffer.length() == 0) return true;	// Nothing after "="
		
		return parseValue(buffer);
	}

    /** 
     * Read the next element definition from an input file stream.
	 * An element may extend over more than one physical line if it 
	 * is quoted or part of a value set.
	 * 
     * @param file      the input file stream
     *
     * @return          <code>true</code> if an element was parsed from the stream;
     *                  <code>false</code> if the end of file or an error was encountered.
     *
     * @see             FileInputStream
     * @since           1.0
     */
	public String readLine(FileInputStream file) {
		int			lastC = 0;
		int			c = 0;
		int			quote = ' ';
		boolean		inQuote = false;
		boolean		inBlock = false;
		boolean		inComment = false;
		boolean		inUnits = false;
		String		buffer;
		
		mSyntaxError = false;
		mLineCount = 0;
		buffer = "";
		try {
			while((c = file.read()) != -1) {
				buffer += (char) c;
				if(c == '\n') mLineCount = 0;
				if(!inComment) {	// Check for quoting
					if(c == '"') { if(inQuote) inQuote = false; else inQuote = true; }
				}
				if(!inQuote) {	// Check if in comment
					if(lastC == '/' && c == '*') inComment = true;
					if(lastC == '*' && c == '/') inComment = false;
				}
				lastC = c;
				if(inQuote) continue;
				if(inComment) continue;
				
				if(c == '\'') { if(inBlock) inBlock = false; else inBlock = true; }
				if(c == '(' || c == '{') { if(inBlock) { mSyntaxError = true; return null; } inBlock = true; }
				if(c == ')' || c == '}') { if(!inBlock) { mSyntaxError = true; return null; } inBlock = false; }
				if(c == '<') { if(inUnits) { mSyntaxError = true; return null; } inUnits = true; }
				if(c == '>') { if(!inUnits) { mSyntaxError = true; return null; } inUnits = false; }
				
				if(c == '\n' && !inQuote && !inBlock && !inUnits) break;
			}
		} catch(IOException ioe) {
			return null;
		}
		if(c == -1 && buffer.length() == 0) return null;	// End of file
		
		return buffer;
	}
		
    /** 
     * Parses a string as a value according to the PDS 
	 * Object Defnition Language (ODL). In this context a value
	 * can be a stirng, literal or list of values. Lists may
	 * be ordered (enclosed in parenthesis () )  or unordered (enclosed
	 * in curly braces {} ). A value may extend over more than 
	 * one physical line if it is quoted or part of a value set.
	 * 
     * @param buffer    the string to parse as a value.
     *
     * @return          <code>true</code> if an value was parsed from properly;
     *                  <code>false</code> if the an error was encountered.
     *
     * @since           1.0
     */
	public boolean parseValue(String buffer) {
		int			c = 0;
		int			quote = ' ';
		int			i;
		boolean		inQuote = false;
		boolean		inComment = false;
		boolean		inUnits = false;
		PDSValue	value;

		inQuote = false;
		inUnits = false;
		value = new PDSValue();
		mValue.add(value);
		c = buffer.charAt(0);
		if(c == '{')	mType = TYPE_ORDERED;
		if(c == '(')	mType = TYPE_UNORDERED;
		
		for(i = 0; i < buffer.length(); i++) {
			c = buffer.charAt(i);
			
			if(inQuote && c == quote) { inQuote = false; quote = ' '; continue; }
			if(!inQuote && Character.isWhitespace((char) c)) continue;	// Ignore white space
			
			if(!inQuote && (c == '"' || c == '\'')) { 
				inQuote = true; 
				if(c == '"')	{ quote = '"'; value.mType = PDSValue.TYPE_STRING; }
				if(c == '\'')	{ quote = '\''; value.mType = PDSValue.TYPE_LITERAL; }
				continue;
			}
			if(inQuote) { value.mValue += (char) c; continue; }
			
			if(c == '{' || c == '(' || c == '}' || c == ')') continue;	// List markers
			if(c == '<') { inUnits = true; continue; }
			if(c == '>') { inUnits = false; continue; }
			
			if(!inUnits && c == ',') { value = new PDSValue(); mValue.add(value); continue; }
			
			if(inUnits) value.mUnits += (char) c;
			else value.mValue += (char) c;
		}	
		
		return true;
	}
	
    /** 
     * Returns the number of values in the value list. 
     * An element may contain any number of values, including 0.
     * For example, an element that is just comment text will 
     * have a value size of 0. 
	 * 
     * @return          the number of values in the value list.
     * @since           1.0
     */
	public int valueSize() {
		return mValue.size();
	}
	
    /** 
     * Returns the value associated with the value item in the 
     * value array that is assocaited with the given index.
     * If the index is out of range a blank value is returned.
	 * 
     * @param index     the index of the value to return.
     * @return          a string containing the value associated with the given index. 
     * @since           1.0
     */
	public String value(int index) {
		PDSValue	value;
		
		if(index < 0 || index >= mValue.size()) return "";
		
		value = (PDSValue) mValue.get(index);
		return value.mValue;
	}
	
    /** 
     * Returns the units associated with the value item in the 
     * value array that is assocaited with the given index.
     * If the index is out of range a blank value is returned.
	 * 
     * @param index     the index of the value to return.
     * @return          a string containing the units associated with the given index. 
     * @since           1.0
     */
	public String units(int index) {
		PDSValue	value;
		
		if(index < 0 || index >= mValue.size()) return "";
		
		value = (PDSValue) mValue.get(index);
		return value.mUnits;
	}
	
    /** 
     * Create a copy of the element and return a new instance
     * of a PDSElement. 
	 * 
     * @return          a new instance of a PDSElement with the same content as this element.
     * @since           1.0
     */
	public PDSElement copy() {
		PDSElement	element = new PDSElement();
		
		element.mComment = mComment;
		element.mKeyword = mKeyword;
		element.mRaw = mRaw;
		element.mType = mType;
		element.mValue = mValue;
		
		return element;
	}

    /** 
     * Print the element according to PDS specifications for label
     * files to Syste.out. The line that is output can be indented with the equal sign
     * (when present) placed at a fixed position. The line is printed to System.out.
	 * 
     * @param indent    the number of spaces to indent for each level.
     * @param equal		the number of spaces from the end of the indent
     *					to align the equal sign for elements which have 
     *					a keyword and value.
     * @param level		the current level of indenting. The number of spaces
     *					the element will be indented is level*indent
     * @since           1.0
     */
	public void print(int indent, int equal, int level) {
		print(System.out, indent, equal, level);
	}
	
    /** 
     * Print the element according to PDS specifications for label
     * files. The line that is output can be indented with the equal sign
     * (when present) placed at a fixed position. The line is printed to System.out.
	 * 
     * @param out    	the stream to print the element to.
     * @param indent    the number of spaces to indent for each level.
     * @param equal		the number of spaces from the end of the indent
     *					to align the equal sign for elements which have 
     *					a keyword and value.
     * @param level		the current level of indenting. The number of spaces
     *					the element will be indented is level*indent
     * @since           1.0
     */
	public void print(PrintStream out, int indent, int equal, int level) {
		int			i, k, n;
		PDSValue	value;
		int			col;
		
		// Print keyword
		if(mKeyword.length() != 0) {
			n = indent * level;
			printSpaces(out, n);
			out.print(mKeyword);
		}
		
				
		// Print value
		n = mValue.size();
		if(n != 0) {
			k = equal - mKeyword.length();
			if(k < 0) k = 1;
			printSpaces(out, k);
			out.print("= ");
			if(n > 1) {	// If more than one value in list
				switch(mType) {
					case TYPE_ORDERED:
						out.print("{");
					break;
					case TYPE_UNORDERED:
						out.print("(");
					break;
				}
			}
			
			// Print each value
			col = equal + 2;	// Start of value
			for(i = 0; i < n; i++) {
				if(i != 0) out.print(", ");
				value = (PDSValue) mValue.get(i);
				col += value.length();
				if(col > mMaxLength && !value.isQuoted()) { out.print("\r\n"); col = equal + 2; printSpaces(out, col); }
				value.print(out, equal+2, 4, mMaxLength);
			}
			
			if(n > 1) {	// If more than one value in list
				switch(mType) {
					case TYPE_ORDERED:
						out.print("}");
					break;
					case TYPE_UNORDERED:
						out.print(")");
					break;
				}
			}
		}
		
		// Print comment
		if(mComment.length() > 0) {
			if(mKeyword.length() > 0) out.print(" ");
			out.print("/*" + mComment + "*/");
		}
		
		out.print("\r\n");
	} 

    /** 
     * Print a string of spaces to an output stream. 
 	 * 
     * @param out    	the stream to print the element to.
     * @param count     the number of spaces to print.
     *
     * @since           1.0
     */
	public void printSpaces(PrintStream out, int count) {
		for(int i = 0; i < count; i++) out.print(" ");
	}
	
}
	
 