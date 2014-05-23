/*
 * Name: You Can't Say That!
 * Description:
 * Author: By Carl Sparks (TWiST3DSOFT)
 * Email: mail@carldsparks.com
 * Skype: nagantarov
 * Last Update: May 15, 2014 (8:50PM EST)
 * Source available at: http://github.com/twist3dsoft
 * License: GPLv3
 * Copyright: 2014
 * 
   This file is part of "You Can't Say That".

    "You Can't Say That" is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    "You Can't Say That" is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with "You Can't Say That".  If not, see <http://www.gnu.org/licenses/>.
 */

/** Class that defines the banned word and its properties. Contains the banned word and its replacement.
 * 
 * @author TWiST3DSOFT
 * @version 1.0 Build 1 5/15/2014
 *
 */
public class BannedWord {
	private String bannedString = null;
	private String replacementString = null;
	
	/** Fetches the banned word and returns is as a string.
	 * @return The banned word as a String*/
	public String getBannedString(){
		return bannedString;
	}
	
	/** Set which word or phrase is banned
	 *  
	 * @param word The String that is considered banned
	 */
	public void setBannedString(String word){
		bannedString = word;
	}
	
	/** Fetches the replacement for the banned word. The word returned should be used to stand in for the banned word.
	 * @return replacementString The word that replaces the banned word
	 */
	public String getReplacement(){
		return replacementString;
	}
	
	/** Set the word or phrase that replaces the banned word
	 * 
	 * @param word The word or phrase replacing the banned word
	 */
	public void setReplacement(String word){
		replacementString = word;
	}
}
