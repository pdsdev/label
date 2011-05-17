import java.io.*;
import java.util.*;
import pds.label.*;

/** 
 * Shows examples of using the PDSLabel package.
 */
class Example {
	public static void main(String[] args) {
		boolean	good = false;
		
		if(args.length < 1) {
			System.out.println("Usage: Example name {...}");
			System.out.println("Available examples are:");
			System.out.println("\tBasic: Load and print label to screen.");
			System.out.println("\tDelete: Delete an object from a label.");
			System.out.println("\tExtract: Extract an object from a label.");
			System.out.println("\tLoadWrite: Load a label and write to a different file.");
			System.out.println("\tObject: Find an object.");
			System.out.println("\tPadFile: Pad a file so every line has the same number of characters.");
			System.out.println("\tPointer: Find a pointer.");
			System.out.println("\tPointerInObject: Find a pointer in an object.");
			System.out.println("\tPointerList: Obtain a list of all pointers.");
			System.out.println("\tPointerValue: Find a pointer value and navigate label.");
			System.out.println("\tStructure: Find a structure pointer.");
			System.out.println("\tStructureReplace: Replace a structure pointer with contents of reference.");
			System.out.println("\tValue: Access value of an element");
			return;
		}
		
		if(args[0].compareToIgnoreCase("Basic") == 0) { good = true; Basic(args); }
		if(args[0].compareToIgnoreCase("Object") == 0) { good = true; Object(args); }
		if(args[0].compareToIgnoreCase("Pointer") == 0) { good = true; Pointer(args); }
		if(args[0].compareToIgnoreCase("PointerInObject") == 0) { good = true; PointerInObject(args); }
		if(args[0].compareToIgnoreCase("PointerValue") == 0) { good = true; PointerValue(args); }
		if(args[0].compareToIgnoreCase("Structure") == 0) { good = true; Structure(args); }
		if(args[0].compareToIgnoreCase("StructureReplace") == 0) { good = true; StructureReplace(args); }
		if(args[0].compareToIgnoreCase("Value") == 0) { good = true; Value(args); }
		if(args[0].compareToIgnoreCase("Delete") == 0) { good = true; Delete(args); }
		if(args[0].compareToIgnoreCase("Extract") == 0) { good = true; Extract(args); }
		if(args[0].compareToIgnoreCase("PointerList") == 0) { good = true; Extract(args); }
		if(args[0].compareToIgnoreCase("LoadWrite") == 0) { good = true; LoadWrite(args); }
		if(args[0].compareToIgnoreCase("PadFile") == 0) { good = true; PadFile(args); }
		
		if(!good) System.out.println("Unknown example: " + args[0]);
	}
	
/** 
 * Parse a label and print it to the screen.
 *
 * <DL><DT><B>Source code:</B>
 * <PRE><DD><CODE CLASS=Java>
{@link PDSLabel}	label	= new PDSLabel();

label.parse(file);				// Parse label file
label.print();					// Print label
 * </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java example basic {label}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 *
 * @since           1.0
 */
	public static void Basic(String[] args) {
		PDSLabel	label = new PDSLabel();
	
		if(args.length < 2) {
			System.out.println("Usage: example basic {label}");
			return;
		}
		
		System.out.println("== Parse and print label ============");
		label.parse(args[1]);				// Parse label file
		label.print();						// Print label
	}
/** 
 * Parse a label, locate an object and print the object to the screen.
 * This example looks for an object with the given name and then prints the
 * elements in the object to the screen.
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label	= new PDSLabel();
{@link PDSItem}		object;

label.parse(file);				// Parse label file
object = label.findObject(name);	// Find object 
label.print(System.out, 4, 20, object);			// Print object
 * </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example Object {label} {name}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 * and {name} is the name of the object to find.
 *
 * @since           1.0
 */
	public static void Object(String[] args) {
		PDSLabel	label = new PDSLabel();
		PDSItem		object;
	
		if(args.length < 3) {
			System.out.println("Usage: Example Object {label} {object}");
			return;
		}
		
		System.out.println("== Table object======================");
		label.parse(args[1]);				// Parse label file
		object = label.findObject(args[2]);	// Find object
		if(!object.valid()) {
			System.out.println("Unable to find object: " + args[2]);
		} else {
			label.print(System.out, 4, 20, object);			// Print object
		}
	}

/** 
 * Parse a label and print all elements that are pointers to the screen.
 * This example parses a label and searches for all elements which
 * are pointers and prints each one to the screen. This demonstrates
 * using regular expressions in a find in order to locate elements
 * which have a particular syntax. 
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label	= new PDSLabel();
{@link PDSItem}		item	= new PDSItem();

label.parse(file);					// Parse label file
item.empty();							// Clear item
while(true) {
	item = label.findNextElement("^*", item);	// Find next pointer
	if(!item.valid()) break;				// If none - exit loop
	label.print(System.out, 4, 20, item);				// Print item
}
 * </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example Pointer {label}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 *
 * @since           1.0
 */
	public static void Pointer(String[] args) {
		PDSLabel	label	= new PDSLabel();
		PDSItem		item	= new PDSItem();
	
		if(args.length < 2) {
			System.out.println("Usage: Example Pointer {label}");
			return;
		}
		
		System.out.println("== All pointers =====================");
		label.parse(args[1]);					// Parse label file
		item.empty();							// Clear item
		while(true) {
			item = label.findNextElement("^*", item);	// Find next pointer
			if(!item.valid()) break;				// If none - exit loop
			label.print(System.out, 4, 20, item);				// Print item
		}
		
	}
 
/** 
 * Parse a label, find an object, find pointers with the object
 * and print each pointer to the screen.
 * This example parses a label and searches for the TABLE object.
 * Then the elements of the table object are scanned for 
 * pointers. The defnition for each pointer is printed on the 
 * screen.
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label	= new PDSLabel();
{@link PDSItem}		item	= new PDSItem();

label.parse(file);					// Parse label file
object = label.findObject("TABLE");	// Find TABLE object
item = label.findElement("^*", object);	// Find first pointer in TABLE object
label.print(System.out, 4, 20, item);				// Print element
* </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example PointerInObject {label}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 *
 * @since           1.0
 */
	public static void PointerInObject(String[] args) {
		PDSLabel	label = new PDSLabel();
		PDSItem		object;
		PDSItem		item;
	
		if(args.length < 2) {
			System.out.println("Usage: Example PointerInObject {label}");
			return;
		}
		
		System.out.println("== Pointer in TABLE object ================");
		label.parse(args[1]);					// Parse label file
		object = label.findObject("TABLE");	// Find TABLE object
		item = label.findElement("^*", object);	// Find first pointer in TABLE object
		label.print(System.out, 4, 20, item);				// Print element
		
	}

 /** 
 * Parse a label, find a pointer, print the pointer and the element that
 * follows the pointer.
 * This example parses a label and searches for an element that is
 * a pointers (begins with "^"). The element is printed and the
 * element following it is also printed. This example demonstrates 
 * how to use regular expressions in a find operation and how to
 * walk through a label.
 *
 * <DL><DT><B>Source code:</B>
 * <PRE><CODE CLASS=Java>
{@link PDSLabel}	label	= new PDSLabel();
{@link PDSItem}		item	= new PDSItem();

label.parse(file);				// Parse label file
item.empty();						// Clear item
item = label.findNextElement("^*", item);	// Find first pointer
System.out.print("next element after: "); 
label.print(System.out, 4, 20, item);				// Print item found
item = label.nextElement(item);			// Step to next item
label.print(System.out, 4, 20, item);				// Print item found
 * </CODE></PRE></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example05 {label}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 *
 * @since           1.0
 */
	public static void PointerValue(String[] args) {
		PDSLabel	label	= new PDSLabel();
		PDSItem		item	= new PDSItem();
	
		if(args.length < 1) {
			System.out.println("Usage: Example PointerValue {label}");
			return;
		}
		
		System.out.println("== Next element =====================");
		label.parse(args[1]);				// Parse label file
		item.empty();						// Clear item
		item = label.findNextElement("^*", item);	// Find first pointer
		System.out.print("next element after: "); 
		label.print(System.out, 4, 20, item);				// Print item found
		item = label.nextElement(item);			// Step to next item
		label.print(System.out, 4, 20, item);				// Print item found
	}

/** 
 * Parse a label, find a pointer element, parse the file it references
 * and print it to the screen.
 * This example parses a label file and searches for a "^STRUCTURE" element.
 * The value of the element is considered a file name and that file is
 * parsed. The results are printed to the screen.
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label = new PDSLabel();
{@link PDSLabel}	partial = new PDSLabel();
{@link PDSItem}		item;
{@link PDSElement}	element;
{@link PDSValue}	value;

label.parse(file);								// Parse label file
item = label.findElement("^STRUCTURE");			// Find pointer to STRUCTURE
if(item.valid()) {								// If found
	element = label.getElement(item);			// Get element associated with item
	value = (PDSValue) element.mValue.get(0);	// Get value
	partial.parse(label.path() + value.mValue);	// Parse file
	partial.print();							// Print out label ^STRUCTURE points to
} else {
	System.out.println("Element not found!");
}
 * </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example Structure {label}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 *
 * @since           1.0
 */
	public static void Structure(String[] args) {
		PDSLabel	label = new PDSLabel();
		PDSLabel	partial = new PDSLabel();
		PDSItem		item;
		PDSElement	element;
		PDSValue	value;
	
		if(args.length < 2) {
			System.out.println("Usage: Example Structure {label}");
			return;
		}
		
		System.out.println("== File pointer load =====================");
		label.parse(args[1]);							// Parse label file
		item = label.findElement("^STRUCTURE");			// Find pointer to STRUCTURE
		if(item.valid()) {								// If found
			element = label.getElement(item);			// Get element associated with item
			value = (PDSValue) element.mValue.get(0);	// Get value
			partial.parse(label.path() + value.mValue);	// Parse file
			partial.print();							// Print out label ^STRUCTURE points to
		} else {
			System.out.println("Element not found!");
		}
	}

/** 
 * Parse a label and replace a pointer with the contents of the file
 * it references, then print the label to the screen.
 * This example parses a label and searches for the "^STRUCTURE" element.
 * The value of the element is considered the name of the file. The
 * file is parsed and inserted into the original label in place of 
 * the "^STRUCTURE" element. The resulting label is printed to the screen.
 * 
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label = new PDSLabel();
{@link PDSLabel}	partial = new PDSLabel();
{@link PDSItem}		item;
{@link PDSElement}	element;
{@link PDSValue}	value;

label.parse(file);								// Parse label file
item = label.findElement("^STRUCTURE");			// Find pointer to STRUCTURE
if(item.valid()) {								// If found
	element = label.getElement(item);			// Get element associated with item
	value = (PDSValue) element.mValue.get(0);	// Get value
	partial.parse(label.path() + value.mValue);	// Parse file
	partial.print(System.out, 4, 20);						// Print out label ^STRUCTURE points to
} else {
	System.out.println("Element not found!");
}

if(item.valid()) {
	label.replace(item, partial);		// Replace ^STRUCTURE item with parsed file content
	label.print(System.out, 4, 20);					// Print the whole label
}
* </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example StructureReplace {label}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 *
 * @since           1.0
 */
	public static void StructureReplace(String[] args) {
		PDSLabel	label = new PDSLabel();
		PDSLabel	partial = new PDSLabel();
		PDSItem		item;
		PDSElement	element;
		PDSValue	value;
	
		if(args.length < 2) {
			System.out.println("Usage: Example StructureReplace {label}");
			return;
		}
		System.out.println("== Expanding a pointer =====================");
		label.parse(args[1]);							// Parse label file
		item = label.findElement("^STRUCTURE");			// Find pointer to STRUCTURE
		if(item.valid()) {								// If found
			element = label.getElement(item);			// Get element associated with item
			value = (PDSValue) element.mValue.get(0);	// Get value
			partial.parse(label.path() + value.mValue);	// Parse file
			partial.print(System.out, 4, 20);						// Print out label ^STRUCTURE points to
		} else {
			System.out.println("Element not found!");
		}
		
		if(item.valid()) {
			label.replace(item, partial);		// Replace ^STRUCTURE item with parsed file content
			label.print(System.out, 4, 20);					// Print the whole label
		}
	}

/** 
 * Parse a label, find an element, then extract and print the value of the element.
 * This example show the two methods that can be used to find an
 * element in a label and print the value of the element.
 * Both methods search for the element with the given keyword.
 * The first method is find the element, then obtain the instance of
 * value object associated with it and print each item in the
 * value object. The second method is to indirectly access the each
 * item in the value associated with an element through the element
 * level functions.
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label	= new PDSLabel();
{@link PDSLabel}	partial	= new PDSLabel();
{@link PDSItem}		item	= new PDSItem();
{@link PDSElement}	element;
{@link PDSValue}	value;

System.out.println("== Extract an element - value item method ===========");
label.parse(file);										// Parse label file
item.empty();												// Clear item
item = label.findNextElement(name, item);		// Find item
if(item.valid()) {											// If found
	element = label.getElement(item);						// Get element associated with item
	if(element != null) {
		int		n;
		System.out.println("Keyword: " + element.mKeyword);	// Print keyword
		n = element.mValue.size();							// Determine number of values
		System.out.println("N Value: " + n);				// Print count
		for(int i = 0; i < n; i++) {			
			value = (PDSValue) element.mValue.get(i);		// Get value item
			System.out.print("Value: " + value.mValue);		// Get value of item
			System.out.println("\tUnits: " + value.mUnits);	// Get units of item
		}
	}
	label.print(System.out, 4, 20, item);								// Print formated element
} else {	// not found
	System.out.println("Unable to find item: " + name);
}

System.out.println("== Extract an element - indirect method ===========");
item.empty();												// Clear item
item = label.findNextElement(name, item);		// Find item
if(item.valid()) {											// If found
	element = label.getElement(item);						// Get element associated with item
	if(element != null) {
		int		n;
		System.out.println("Keyword: " + element.mKeyword);	// Print keyword
		n = element.valueSize();							// Determine number of values
		System.out.println("N Value: " + n);				// Print count
		for(int i = 0; i < n; i++) {
			System.out.print("Value: " + element.value(i));	// Get value for item
			System.out.println("\tUnits: " + element.units(i));	// Get units for item
		}
	}
	label.print(System.out, 4, 20, item);								// Print formatted element
} else {	// not found
	System.out.println("Unable to find item: " + name);
}
 * </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example Value {label} {name}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 * and {name} is the keyword of the elmenent to find.
 *
 * @since           1.0
 */
	public static void Value(String[] args) {
		PDSLabel	label	= new PDSLabel();
		PDSLabel	partial	= new PDSLabel();
		PDSItem		item	= new PDSItem();
		PDSElement	element;
		PDSValue	value;
	
		if(args.length < 3) {
			System.out.println("Usage: Example Value {label} {name}");
			return;
		}
		
		System.out.println("== Extract an element - value item method ===========");
		label.parse(args[1]);										// Parse label file
		item.empty();												// Clear item
		item = label.findNextElement(args[2], item);		// Find item
		if(item.valid()) {											// If found
			element = label.getElement(item);						// Get element associated with item
			if(element != null) {
				int		n;
				System.out.println("Keyword: " + element.mKeyword);	// Print keyword
				n = element.mValue.size();							// Determine number of values
				System.out.println("N Value: " + n);				// Print count
				for(int i = 0; i < n; i++) {			
					value = (PDSValue) element.mValue.get(i);		// Get value item
					System.out.print("Value: " + value.mValue);		// Get value of item
					System.out.println("\tUnits: " + value.mUnits);	// Get units of item
				}
			}
			label.print(System.out, 4, 20, item);								// Print formated element
		} else {	// not found
			System.out.println("Unable to find item: " + args[2]);
		}
		
		System.out.println("== Extract an element - indirect method ===========");
		item.empty();												// Clear item
		item = label.findNextElement(args[2], item);		// Find item
		if(item.valid()) {											// If found
			element = label.getElement(item);						// Get element associated with item
			if(element != null) {
				int		n;
				System.out.println("Keyword: " + element.mKeyword);	// Print keyword
				n = element.valueSize();							// Determine number of values
				System.out.println("N Value: " + n);				// Print count
				for(int i = 0; i < n; i++) {
					System.out.print("Value: " + element.value(i));	// Get value for item
					System.out.println("\tUnits: " + element.units(i));	// Get units for item
				}
			}
			label.print(System.out, 4, 20, item);								// Print formatted element
		} else {	// not found
			System.out.println("Unable to find item: " + args[2]);
		}
		
	}

/** 
 * Parse a label, remove an object and print the results to the screen.
 * This example parses a label, locates the object with the given name
 * and, if found, deletes the object from the label. The resulting label 
 * is printed to the screen.
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label = new PDSLabel();
{@link PDSItem}		object;

label.parse(file);				// Parse label file
object = label.findObject(name);	// Find object
if(object.valid()) label.remove(object);
label.print();
 * </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example Delete {label} {name}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 * and {name} is the name of the object to delete.
 *
 * @since           1.0
 */
	public static void Delete(String[] args) {
		PDSLabel	label = new PDSLabel();
		PDSItem		object;
	
		if(args.length < 3) {
			System.out.println("Usage: Example Delete {label} {name}");
			return;
		}
		
		System.out.println("== Delete TABLE object from label ============");
		label.parse(args[1]);				// Parse label file
		object = label.findObject(args[2]);	// Find object
		if(object.valid()) {
			label.remove(object);
			label.print();
		} else {
			System.out.println("Unable to find object: " + args[2]);
		}
	}

/** 
 * Parse a label, find an object, extract the object into a new label
 * and print it to the screen.
 * This example parses a label and looks for an object with the given name. 
 * If the object is found, then a copy is made and the copy is printed to 
 * the screen. This demonstrates how pieces of a label may be extract and used.
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label = new PDSLabel();
{@link PDSLabel}	partial;
{@link PDSItem}		object;

label.parse(file);					// Parse label file
object = label.findObject(name);		// Find object
if(object.valid()) {
	partial = label.extract(object);	// Extract object into new label
	partial.print();					// Print new label
}
* </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example Extract {label} {name}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 * and {name} is the name of the object to extract.
 *
 * @since           1.0
 */
	public static void Extract(String[] args) {
		PDSLabel	label = new PDSLabel();
		PDSLabel	partial;
		PDSItem		object;
	
		if(args.length < 3) {
			System.out.println("Usage: Example Extract {label} {object}");
			return;
		}
		
		System.out.println("== Extract object from label ============");
		label.parse(args[1]);					// Parse label file
		object = label.findObject(args[2]);		// Find object
		if(object.valid()) {
			partial = label.extract(object);	// Extract object into new label
			partial.print();					// Print new label
		} else {
			System.out.println("Unable to find object: " + args[2]);
		}
	}

/** 
 * Parse a label, find all the pointers to files and print the list 
 * of files to the screen. 
 * This example parses a file, then finds all the pointers to files
 * using a utility function. The returned list of files is printed
 * to the screen.
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label = new PDSLabel();
{@link ArrayList}	list;

label.parse(file);					// Parse label file
list = label.filePointers();
for(int i = 0; i < list.size(); i++) {
	System.out.println("File: " + (String) list.get(i));
}
* </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example PointerList {label}</DT>
 * </DL>
 * where {label} is the name of the label file to parse.
 *
 * @since           1.0
 */
	public static void PointerList(String[] args) {
		PDSLabel	label = new PDSLabel();
		ArrayList	list;
	
		if(args.length < 2) {
			System.out.println("Usage: Example PointerList {label}");
			return;
		}
		
		System.out.println("== Pointers to files ============");
		label.parse(args[1]);					// Parse label file
		list = label.filePointers();
		for(int i = 0; i < list.size(); i++) {
			System.out.println("File: " + (String) list.get(i));
		}
	}

/** 
 * Parse a label and write it to another file.
 * This example opens a label file, parses it, then writes
 * the label to another file. This is useful when loading
 * a template label, then writing the label to another 
 * file or location.
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSLabel}	label = new PDSLabel();

System.out.println("== Load label and write to a file ============");
label.parse(file);					// Parse label file
label.print(output);
* </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example LoadWrite {label} {output}</DT>
 * </DL>
 * where {label} is the name of the label file to parse
 * and {output} is the name of the file to write the label.
 *
 * @since           1.0
 */
	public static void LoadWrite(String[] args) {
		PDSLabel	label = new PDSLabel();
	
		if(args.length < 3) {
			System.out.println("Usage: Example LoadWrite {label} {output file}");
			return;
		}
		
		System.out.println("== Load label and write to a file ============");
		label.parse(args[1]);					// Parse label file
		label.print(args[2]);
	}

/** 
 * Pad a file so that every line in the file has the same number of
 * characters. The passed file is modified. 
 *
 * <DL><DT><B>Source code:</B>
 * <DD><PRE><CODE CLASS=Java>
{@link PDSUtil}	util = new PDSUtil();

System.out.println("== Pad file ============");
util.padFile(file);					// Pad file
* </CODE></PRE></DD></DL>
 * <DL>
 * <DT><B>To Run:</B>
 * 
 * <DD>java Example PadFile {label} {width}</DT>
 * </DL>
 * where {label} is the name of the label file to parse
 * and {output} is the name of the file to write the label.
 *
 * @since           1.0
 */
	public static void PadFile(String[] args) {
		PDSUtil	util = new PDSUtil();
		int		width = 80;
		
		if(args.length < 2) {
			System.out.println("Usage: Example PadFile {file} {width}");
			return;
		}
		if(args.length > 2) width = Integer.parseInt(args[2]);
		
		System.out.println("== Pad file ============");
		System.out.println("File: " + args[1]);
		util.padFile(args[1], width);					// Pad file
	}
}
