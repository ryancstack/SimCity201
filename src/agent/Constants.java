/*

Copyright (c) 2000-2003 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Except as contained in this notice, the name of Stanford University shall not
be used in advertising or otherwise to promote the sale, use or other dealings
in this Software without prior written authorization from Stanford University.

*/
package agent;


/**
 * Constants of general use
 */
public interface Constants {
	/**
	 * Constants for movement tiles
	 */
	public static final int ROAD = 1;
	public static final int SIDEWALK = 2;
	public static final int CROSSWALK = 3;
	
	
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;
	public static final int SUNDAY = 7;
	
	/**
	 * The divisor for shortening time
	 */
	public static final long DIVISOR = 1000;
    /**
     * The number of milliseconds in a second
     */
    public static final long SECOND = 1000/DIVISOR;
    /**
     * The number of milliseconds in a minute
     */
    public static final long MINUTE = 60 * SECOND/DIVISOR;
    /**
     * The number of milliseconds in an hour
     */
    public static final long HOUR = 60 * MINUTE/DIVISOR;
    /**
     * The number of milliseconds in a day
     */
    public static final long DAY = 24 * HOUR/DIVISOR;
    /**
     * The number of milliseconds in a week
     */
    public static final long WEEK = 7 * DAY/DIVISOR;

    /**
     * The line separator string on this system
     */
    public static String EOL = System.getProperty("line.separator");

    /**
     * The default encoding used when none is detected
     */
    public static String DEFAULT_ENCODING = "ISO-8859-1";

}
