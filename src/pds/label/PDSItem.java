package pds.label;

import java.io.*;
import java.util.*;

/**
 * PDSItem is a class that descibes the location within a
 * {@link PDSLabel} of one or more elements.
 *
 * @author      Todd King
 * @author      Planetary Data System
 * @version     1.0, 02/21/03
 * @since       1.0
 */
public class PDSItem {
	/** The index of the first element of the item */ 	
	public int		mStart = -1;
	/** The index of the end element of the item */
	public int		mEnd = -1;
	
 	/** Creates an instance of a PDSItem */
 	public PDSItem() {
 	}
 	
    /** 
     * Determines if a PDSItem is valid. A valid PDSItem is set to 
     * point to a range of elements within a {@link PDSLabel}.
	 * 
     * @return          <code>true</code> if a range is set.
     *					<code>false</code> is no range is set.
     *
     * @since           1.0
     */
	public boolean valid() 
	{ 
		return isValid(); 
	}
	
    /** 
     * Determines if a PDSItem is valid. A valid PDSItem is set to 
     * point to a range of elements within a {@link PDSLabel}.
	 * 
     * @return          <code>true</code> if a range is set.
     *					<code>false</code> is no range is set.
     *
     * @since           1.0
     */
	public boolean isValid() {
		if(mStart == -1) return false;
		if(mEnd == -1) return false;
		return true;
	}
	
    /** 
     * Clears all settings of the item. Once an item is cleared it no longer
     * points to a valid range of elements.
	 * 
     * @since           1.0
     */
	public void empty() {
		mStart = -1;
		mEnd = -1;
	}
	
}
