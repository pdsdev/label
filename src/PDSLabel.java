package pds.label;

import java.io.*;
import java.util.*;

/**
 * PDSLabel is a class that contians all information regarding a
 * PDS label entity. A PDS label entity consists of one or more 
 * elements as specified in the PDS Object Defnition Language (ODL). 
 * Each element may be a simple line of text, a block of 
 * commented text, or a keyword/value pair. Comments and values 
 * may extend over more than one physical line.
 *
 * @author      Todd King
 * @author      Planetary Data System
 * @version     1.0, 02/21/03
 * @since       1.0
 */
 	
// Container for a label 
public class PDSLabel {
	/** The list of elements in the label */
 	public ArrayList mElement	= new ArrayList();
 	/** The path and file name used when loading a label from a file.*/
 	public String	mPathName	= "";
 	
 	/** Creates an instance of a PDSLabel */
 	public PDSLabel() {
 	}
 	
 	/** 
     * Returns a string with the release information for this compilation.
     *
     * @return	a string contining the release information for this compilation.
     * @since           1.0
     */
	public String version() {
		return "1.0.0.3";
 	}
 	
    /** 
     * Reset all internal variables to the initial state.
	 *
     * @since           1.0
     */
 	public void reset() {
 		mElement = new ArrayList();
 	}
 	
    /** 
     * Determines if a file contains a PDS label.
     * If the first 14 characters are either "PDS_VERSION_ID" 
     * or "CCSD3ZF0000100" then the file is considered 
     * to contain a PDS label.
	 *
     * @param pathName  the fully qualified path and name of the file to parse.
     *
     * @return          <code>true</code> if the file contains a label.
     *                  <code>false</code> otherwise.
     *
     * @since           1.0
     */
 	public boolean isLabel(String pathName) {

		FileInputStream file;
		String	buffer	= "";
		int		i;
		int		c		= 0;
		boolean	label	=	false;
				
		try {
			file = new FileInputStream(pathName);
			i = 0;
			while((c = file.read()) != -1) {
				buffer += (char) c;
				i++;
				if(i == 14) break;
			}
			if(buffer.compareTo("CCSD3ZF0000100") == 0) label = true;
			if(buffer.compareTo("PDS_VERSION_ID") == 0) label = true;
		} catch(IOException FileNotFoundException) {
			System.out.println("Unable to open file: " + pathName);
		}
		return label;
 	}
 	
    /** 
     * Parses a file containing a PDS label into its constitute elments.
	 * The path and name of the file are passed to the method which is
	 * opened and parsed.
	 *
     * @param pathName  the fully qualified path and name of the file to parse.
     *
     * @return          <code>true</code> if the file could be opened;
     *                  <code>false</code> otherwise.
     * @since           1.0
     */
 	public boolean parse(String pathName) {
		FileInputStream file;
		boolean			status;
		
		mPathName = pathName;
 		
		try {
			file = new FileInputStream(mPathName);
			status = parse(file);
			file.close();
		} catch(IOException FileNotFoundException) {
			System.out.println("Unable to open file: " + mPathName);
			return false;
		}
		
		return status;
 	}
 	
    /** 
     * Parses a file containing a PDS label into its constitute elments.
	 * The file to parse must be previously opened and a FileInputStream
	 * pointing to the file is passed.
	 *
     * @param file		a connection to a pre-opened file.
     * @return          <code>true</code> if the file could be read;
     *                  <code>false</code> otherwise.
     * @since           1.0
     */
 	public boolean parse(FileInputStream file) 	{
		boolean	more = true;
		PDSElement element;
		int		lineCount = 0;
		
		reset();
		
		while(more) {
			element = new PDSElement();
			element.mLineCount = lineCount;
			mElement.add(element);
			more = element.parse(file);
			if(more) {
				lineCount = element.mLineCount;
			} else {
				if(element.mSyntaxError) System.out.println("Syntax error near line "  + lineCount);
			}
		}
		
		return true;
 	}
 	
    /** 
     * Returns the path portion of the fully qualified name of the file
     * which was parsed.
	 *
     * @return          the path to the file most recently parsed. The returned value
     *					will be blank if no file has been parsed or if a FileInputStream
     *					was used when parsing the file.
     * @since           1.0
     */
 	public String path() {
 		int		n;
 		
 		// Try with unix style
 		n = mPathName.lastIndexOf('/');
 		if(n != -1) return mPathName.substring(0, n+1);
 		
 		// Try with DOS style
 		n = mPathName.lastIndexOf('\\');
 		if(n != -1) return mPathName.substring(0, n+1);

 		return "";
 	}
 	
    /** 
 	 * Find the object with the given name.
 	 * Looks for elements with the keyword "OBJECT" and a value of the
 	 * given name. If such an element is found then the corresponding 
 	 * element with the keyword "END_OBJECT" is located. The passed name 
 	 * can contain regular expressions. Any occurance of a "*" in the 
 	 * string is converted to the regular expression ".*" to match 
 	 * any number of characters. 
 	 * The search begins at the first element in the label and extends 
 	 * to the last.
	 *
	 * @param name	the name of the object to find. This can contain regular expressions.
	 *
     * @return          a {@link PDSItem} the indicates the start and end of the object within
     *					the label.
     *
     * @since           1.0
     */
 	public PDSItem findObject(String name) {
 		return findObject(name, 0, -1);
 	}
 	
    /** 
 	 * Find the object with the given name within a partion of a label.
 	 * Looks for elements with the keyword "OBJECT" and a value of the
 	 * given name. If such an element is found then the corresponding 
 	 * element with the keyword "END_OBJECT" is located. The passed name 
 	 * can contain regular expressions. Any occurance of a "*" in the 
 	 * string is converted to the regular expression ".*" to match 
 	 * any number of characters. 
 	 * The search begins at the first element indicated the {@link PDSItem} 
 	 * and extends to the last.
	 *
	 * @param name		the name of the element to find. This can contain regular expressions.
	 * @param item		the item the search is to constrained within. If item is null then the
	 *					search will span the entire label.
	 *
     * @return          a {@link PDSItem} the indicates the start and end of the object within
     *					the label.
     *
     * @since           1.0
     */
 	public PDSItem findObject(String name, PDSItem item) {
 		if(item == null) return findObject(name);
 		return findObject(name, item.mStart, item.mEnd);
 	}
 	
    /** 
 	 * Find the object with the given name within a partion of a label.
 	 * Looks for elements with the keyword "OBJECT" and a value of the
 	 * given name. If such an element is found then the corresponding 
 	 * element with the keyword "END_OBJECT" is located. The passed name 
 	 * can contain regular expressions. Any occurance of a "*" in the 
 	 * string is converted to the regular expression ".*" to match 
 	 * any number of characters. 
 	 * The search begins at the element at the startAt position in the 
 	 * list and extends to the element at the endAt position.
	 *
	 * @param name		the name of the element to find. This can contain regular expressions.
	 * @param startAt	the index of the element at which to start the search.
	 * @param endAt		the index of the element at which to end the search.
	 *
     * @return          a {@link PDSItem} the indicates the start and end of the object within
     *					the label.
     *
     * @since           1.0
     */
 	public PDSItem findObject(String name, int startAt, int endAt) {
 		int			i, k;
 		PDSElement	element;
 		PDSValue	value;
 		PDSItem		item = new PDSItem();
 		
 		if(startAt == -1) startAt = 0;
 		if(endAt == -1) endAt = mElement.size();
 	
 		name = name.replaceAll("\\*", ".*");
 		
 		// Search for start of object
 		for(i = startAt; i < endAt; i++) {
 			element = (PDSElement) mElement.get(i);
 			if(element.mKeyword.compareTo("OBJECT") == 0) {
 				if(element.mValue.size() == 0) continue;
 				value = (PDSValue) element.mValue.get(0);
 				if(value.mValue.matches(name)) { item.mStart = i; break; }
 			}
 		}
 		if(item.mStart == -1) return null;	// Not found

		// Search for matching end of object
		k = 0;
 		for(i = item.mStart; i < endAt; i++) {
 			element = (PDSElement) mElement.get(i);
 			if(element.mKeyword.compareTo("OBJECT") == 0) k++;
 			if(element.mKeyword.compareTo("END_OBJECT") == 0) {
 				k--;
 				if(k == 0) { item.mEnd = i+1; break; }
 			}
 		}
 		
 		return item;	// Not found
 	}
 	
    /** 
 	 * Find the value assocated with an element with the given name.
 	 * The passed name can contain regular expressions. Any occurance
 	 * of a "*" in the string is converted to the regular expression ".*"
 	 * to match any number of characters. Also any occurance of "^" is converted
 	 * to a literal "^" since it is a valid character in keywords.
 	 * The search begins at the first element in the label and extends to the last.
	 *
	 * @param name		the name of the element to find. This can contain regular expressions.
	 *
     * @return          a {@link String} containing the first value associated with the
     *					named element. If the element was not found the value returned is empty.
     *
     * @since           1.0
     */
 	public String getElementValue(String name) {
 		PDSItem		item;
 		PDSElement	element;
 		
 		item = findElement(name, 0, -1);
 		if(!item.valid()) return "";
 		
 		element = getElement(item);
 		return element.value(0);
 	}
 	
    /** 
 	 * Find the element with the given name.
 	 * The passed name can contain regular expressions. Any occurance
 	 * of a "*" in the string is converted to the regular expression ".*"
 	 * to match any number of characters. Also any occurance of "^" is converted
 	 * to a literal "^" since it is a valid character in keywords.
 	 * The search begins at the first element in the label and extends to the last.
	 *
	 * @param name		the name of the element to find. This can contain regular expressions.
	 *
     * @return          a {@link PDSItem} the indicates the start and end of the object within
     *					the label.
     *
     * @since           1.0
     */
 	public PDSItem findElement(String name) {
 		return findElement(name, 0, -1);
 	}
 	
    /** 
 	 * Find the element with the given name constrained to some portion of the label.
 	 * The passed name can contain regular expressions. Any occurance
 	 * of a "*" in the string is converted to the regular expression ".*"
 	 * to match any number of characters. Also any occurance of "^" is converted
 	 * to a literal "^" since it is a valid character in keywords.
 	 * The search begins at the first element indicated in the passed {@link PDSItem}
 	 * and extends to the last item.
	 *
	 * @param name		the name of the element to find. This can contain regular expressions.
	 * @param item		a {@link PDSItem} to limit the search within. If <i>item</i> is <b>null</b>
	 *					then search the entire label.
	 *
     * @return          a {@link PDSItem} that indicates the location of the element 
     *					within the label.
     *
     * @since           1.0
     */
 	public PDSItem findElement(String name, PDSItem item) {
 		if(item == null) return findElement(name);
 		return findElement(name, item.mStart, item.mEnd);
 	}
 	
    /** 
 	 * Find the element with the given name constrained to some portion of the label.
 	 * The passed name can contain regular expressions. Any occurance
 	 * of a "*" in the string is converted to the regular expression ".*"
 	 * to match any number of characters. Also any occurance of "^" is converted
 	 * to a literal "^" since it is a valid character in keywords.
 	 * The search begins at the first element indicated in the passed {@link PDSItem}
 	 * and extends to the last item.
	 *
	 * @param name		the name of the element to find. This can contain regular expressions.
	 * @param startAt	the index of the element to begin the search. If <i>startAt</i> 
	 *					is -1 then search from the beginning of the label.
	 * @param endAt		the index of the element to end the search. If <i>endAt</i> 
	 *					is -1 then search to the end of the label.
	 *
     * @return          a {@link PDSItem} that indicates the location of the element 
     *					within the label.
     *
     * @since           1.0
     */
 	public PDSItem findElement(String name, int startAt, int endAt) {
 		int			i, k;
 		PDSElement	element;
 		PDSValue	value;
 		PDSItem		item = new PDSItem();
 		
 		if(startAt == -1) startAt = 0;
 		if(endAt == -1) endAt = mElement.size();
 		
 		name = name.replaceAll("\\^", "\\\\^");
 		name = name.replaceAll("\\*", ".*");
 		
 		// Search for start of object
 		for(i = startAt; i < endAt; i++) {
 			element = (PDSElement) mElement.get(i);
 			if(element.mKeyword.matches(name)) { item.mStart = i; item.mEnd = i+1; break; }
 		}
 		
 		return item;	// Not found
 	}
 	
    /** 
 	 * Find the next element with the given name starting at some point within the label.
 	 * The passed name can contain regular expressions. Any occurance
 	 * of a "*" in the string is converted to the regular expression ".*"
 	 * to match any number of characters. Also any occurance of "^" is converted
 	 * to a literal "^" since it is a valid character in keywords.
 	 * The search begins at the first element indicated in the passed {@link PDSItem} and 
 	 * extends to the end of the label.
	 *
	 * @param name		the name of the element to find. This can contain regular expressions.
	 * @param item		a {@link PDSItem} indicating where to begin the search.
	 *
     * @return          a {@link PDSItem} the indicates the start and end of the object within
     *					the label.
     *
     * @since           1.0
     */
 	public PDSItem findNextElement(String name, PDSItem item) {
 		if(item == null) return findElement(name);
 		return findElement(name, item.mEnd, -1);
 	}
 	
    /** 
 	 * Find the next element after the given item.
	 *
	 * @param item		a {@link PDSItem} indicating where to begin the search.
	 *
     * @return          a {@link PDSItem} the indicates the start and end of the object within
     *					the label.
     *
     * @since           1.0
     */
 	public PDSItem nextElement(PDSItem item) {
 		PDSItem		nextItem = new PDSItem();
 		if(item.mEnd < mElement.size()) { nextItem.mStart = item.mEnd; nextItem.mEnd = nextItem.mStart+1; }
 		
 		return nextItem;
 	}
 	
    /** 
 	 * Return the element data associated with an item.
 	 * 
	 * @param item		a {@link PDSItem} to return the element data for. The data associated with the
	 *					first element of the item is returned.
	 *
     * @return          a {@link PDSElement} associated witht he first element of the item.
     *					the label. If no element is associated with the item <b>null</b>
     *					is returned.
     *
     * @since           1.0
     */
 	public PDSElement getElement(PDSItem item)
 	{
 		if(item.valid() && item.mStart < mElement.size()) return (PDSElement) mElement.get(item.mStart);
 		return null;
 	}
 	
    /** 
 	 * Replace an item in a label with another label.
 	 * 
	 * @param item		a {@link PDSItem} indicating the elements to replace.
	 * @param label		the label to place where <i>item</i> is currently.
	 *
     * @since           1.0
     */
 	public void replace(PDSItem item, PDSLabel label) {
 		insertAfter(item, label);
 		remove(item);
 	}
 	
    /** 
 	 * Add an element to the end of a label.
 	 * 
	 * @param element	the element to add to the end of the label.
	 *
     * @since           1.0
     */
 	public void add(PDSElement element) {
 		mElement.add(element);
 	}
 	
    /** 
 	 * Add a label  to the end of a label.
 	 * 
	 * @param label		the label to add to the end of the label.
	 *
     * @since           1.0
     */
 	public void add(PDSLabel label) {
 		mElement.addAll(label.mElement);
 	}
 	
    /** 
 	 * Insert a label before another element in this label. 
 	 * If the element location is invalid nothing is done.
 	 * 
	 * @param item		the location of the element before which the label is inserted.
	 * @param label		the label to place before the passed item.
	 *
     * @since           1.0
     */
 	public void insertBefore(PDSItem item, PDSLabel label) {
 		if(!item.valid()) return;
 		mElement.addAll(item.mStart, label.mElement);
 	}
 	
    /** 
 	 * Insert a label after another element in this label. 
 	 * If the location of the element is passed the end of 
 	 * this label the label is appended to this label. 
 	 * If the element location is invalid nothing is done.
 	 * 
	 * @param item		the location of the element after which the label is inserted.
	 * @param label		the label to place after the passed item.
	 *
     * @since           1.0
     */
 	public void insertAfter(PDSItem item, PDSLabel label) {
 		if(!item.valid()) return;
		if(item.mEnd >= mElement.size()) add(label);
		else mElement.addAll(item.mEnd, label.mElement);
 	}
 	
    /** 
 	 * Insert an element before another element in this label. 
 	 * If the element location is invalid nothing is done.
 	 * 
	 * @param item		the location of the element before which the element is inserted.
	 * @param element	the element to place before the passed item.
	 *
     * @since           1.0
     */
 	public void insertBefore(PDSItem item, PDSElement element) {
 		if(!item.valid()) return;
 		mElement.add(item.mStart, element);
 	}
 	
    /** 
 	 * Insert an element after another element in this label. 
 	 * If the location of the element is passed the end of 
 	 * this label the label is appended to this label. 
 	 * If the element location is invalid nothing is done.
 	 * 
	 * @param item		the location of the element after which the element is inserted.
	 * @param element	the element to place after the passed item.
	 *
     * @since           1.0
     */
 	public void insertAfter(PDSItem item, PDSElement element) {
 		if(!item.valid()) return;
		if(item.mEnd >= mElement.size()) add(element);
		else mElement.add(item.mEnd, element);
 	}
 	
    /** 
 	 * Remove a range of elements from the label.
 	 * If the range of elements in the passed item is invalid nothing is done.
 	 * 
	 * @param item		the location of the elements to remove.
	 *
     * @since           1.0
     */
 	public void remove(PDSItem item) {
 		if(!item.valid()) return;
 		if(item.mEnd > mElement.size()) item.mEnd = mElement.size();
 		for(int i = item.mStart; i < item.mEnd; i++) mElement.remove(item.mStart);
 	}
 	
    /** 
 	 * Extract a portion of a label into a new instance of label.
 	 * The range of elements to extract is given in a {@link PDSItem}
 	 * 
	 * @param item		the location of the elements to extract.
	 *
     * @since           1.0
     */
 	public PDSLabel extract(PDSItem item) {
 		PDSLabel	label = new PDSLabel();
 		PDSElement	element;
 		PDSElement	newElement;
 		
 		if(item.valid()) {
	 		for(int i = item.mStart; i < item.mEnd; i++) {
	 			element = (PDSElement) mElement.get(i);
	 			newElement = element.copy();
	 			label.mElement.add(newElement);
	 		}
 		}
		return label;
 	}
 	
 	/** 
     * Search the label and return a list to all points to files.
     * A pointer to a file is defined as any pointer keyword with
     * a quoted value. 
     *
     * @return		a list of file names pointed to within the label. 
     *				The list is in the form of an {@link ArrayList} of 
     *				{@link String} objects. If no pointers are found then
     *				<code>null</code> is returned.
     * @since           1.0
     */
	public ArrayList filePointers() {
		PDSItem		item = new PDSItem();
		ArrayList	list = new ArrayList();
		PDSElement	element;
		PDSValue	value;
		
		item.empty();
		item = findNextElement("^*", item);
		while(item.valid()) {
			element = getElement(item);
			for(int i = 0; i < element.mValue.size(); i++) {
				value = (PDSValue) element.mValue.get(i);
				if(value.mType == PDSValue.TYPE_STRING) {
					String buffer = new String();
					buffer = value.mValue;
					list.add(buffer);
				}
			}
			item = findNextElement("^*", item);
		}
		if(list.size() == 0) return null;
		
		return list;
 	}
 	
    /** 
     * Print all elements in the label according to PDS specifications 
     * for label files using default indent and equal sign placement. 
	 * 
     * @since           1.0
     */
 	public void print() {
 		print(System.out);
 	}
 	
    /** 
     * Print all elements in the label according to PDS specifications 
     * for label files using default indent and equal sign placement. 
	 * 
     * @since           1.0
     */
 	public void print(String pathName) {
		FileOutputStream	file;
		PrintStream			out;
 		mPathName = pathName;
 		
		try {
			file = new FileOutputStream(mPathName);
			out = new PrintStream(file);
	 		print(out);
		} catch(IOException FileNotFoundException) {
			System.out.println("Unable to open file: " + mPathName);
			return;
		}
 	}
 	
    /** 
     * Print all elements in the label according to PDS specifications 
     * for label files using default indent and equal sign placement. 
	 * 
     * @param out    	the stream to print the element to.
     *
     * @since           1.0
     */
 	public void print(PrintStream out) {
 		print(out, 2, 29, 0, -1);
 	}
 	
    /** 
     * Print all elements in the label according to PDS specifications 
     * for label files. Each line that is output can be indented with the equal sign
     * (when present) placed at a fixed position. Output printed to System.out.
     * Each occurance of an OBJECT is indented on level.
	 * 
     * @param out    	the stream to print the element to.
     * @param indent    the number of spaces to indent for each level.
     * @param equal		the number of spaces from the end of the indent
     *					to align the equal sign for elements which have 
     *					a keyword and value.
     *
     * @since           1.0
     */
 	public void print(PrintStream out, int indent, int equal) {
 		print(out, indent, equal, 0, -1);
 	}
 	
    /** 
     * Print a range of elements in the label according to PDS specifications 
     * for label files. Each line that is output can be indented with the equal sign
     * (when present) placed at a fixed position. Output printed to System.out.
     * Each occurance of an OBJECT is indented on level.
	 * 
     * @param out    	the stream to print the element to.
     * @param indent    the number of spaces to indent for each level.
     * @param equal		the number of spaces from the end of the indent
     *					to align the equal sign for elements which have 
     *					a keyword and value.
     * @param item		the item to output.
     *
     * @since           1.0
     */
 	public void print(PrintStream out, int indent, int equal, PDSItem item) {
 		if(item == null) print(out, indent, equal);
 		else print(out, indent, equal, item.mStart, item.mEnd);
 	}
 	
 	/** 
     * Print a range of elements in the label according to PDS specifications 
     * for label files. Each line that is output can be indented with the equal sign
     * (when present) placed at a fixed position. Output printed to System.out.
     * Each occurance of an OBJECT is indented on level.
	 * 
     * @param out    	the stream to print the element to.
     * @param indent    the number of spaces to indent for each level.
     * @param equal		the number of spaces from the end of the indent
     *					to align the equal sign for elements which have 
     *					a keyword and value.
     * @param startAt	the first element to output.
     * @param endAt		the last element to output.
     *
     * @since           1.0
     */
	public void print(PrintStream out, int indent, int equal, int startAt, int endAt) {
 		PDSElement	element;
 		PDSValue	value;
 		int			i;
 		int			level = 0;
 		
 		if(startAt == -1) startAt = 0;
 		if(endAt == -1) endAt = mElement.size();
 		
 		for(i = startAt; i < endAt; i++) {
 			element = (PDSElement) mElement.get(i);
			if(element.mKeyword.compareTo("END_OBJECT") == 0) level--;
 			element.print(out, indent, equal, level);
			if(element.mKeyword.compareTo("OBJECT") == 0) level++;
		}
 	}
}

 