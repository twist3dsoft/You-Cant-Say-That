/*
 * Name: You Can't Say That!
 * Description:
 * Author: By Carl Sparks (TWiST3DSOFT)
 * Email: mail@carldsparks.com
 * Skype: nagantarov
 * Last Update: May 18, 2014 (2:57AM EST)
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all the settings needed for "You Can't Say That!" along with the methods necessary to manipulate settings data. 
 * @author TWiST3DSOFT
 * @version 1.0 Build 1 5/15/2014
 */
public class Settings {
	private static BufferedWriter writer = null;
	private static String settingsFilePath = null;
	private static String logFilePath = null;
	private static String logTimeStamp = null;
	private static String lineFromFile = null;
	
	private static String vars[] = null;
	
	private static boolean uniqueReplacements = true;
	private static String staticReplacement = "{REDACTED}";
	private static boolean enableLogging = true;

	
	/**
	 * Writes a String to a log file with a time stamp prepended.
	 * @param logText
	 */
	public static void writeLog(String logText){
		writeLog(logText, true);
	}
	
	/**
	 * Writes a String to a log file with an optional time stamp prepended.
	 * @param logText The String that gets written to file.
	 * @param useTimeStamp true if you want a time stamp false if you do not
	 */
	public static void writeLog(String logText, boolean useTimeStamp){
		if(enableLogging){
			LocalDate currentDate = LocalDate.now(); // Gets the current calendar date
			LocalTime currentTime = LocalTime.now(); // Gets the current time 
		
			// Sets up the filename for the log. Ex: "2014-30-05-log.txt"
			logFilePath = currentDate.getYear() + "-" + currentDate.getMonth() + "-" + currentDate.getDayOfMonth() + "-log.txt";
			List<String> tempFileData = new ArrayList<String>(); // Stores the file content for the log before it gets written to file
		
			// Formatted String that gets prepended to the log text of each line
			logTimeStamp = "[" + currentDate.getMonth() + "/" + currentDate.getDayOfMonth() + "/" + currentDate.getYear() 
				+ " " + currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond() + "]";
		
			// Attempt to open an existing log file should one exist for today's date.
			try{ 
				FileInputStream fileStream = new FileInputStream(logFilePath); // Open the file
				DataInputStream in = new DataInputStream(fileStream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
				while((lineFromFile = br.readLine()) != null){
					tempFileData.add(lineFromFile); // Add each line from the file to the List
				}
				fileStream.close();
				in.close();
				br.close(); 
			}catch (Exception e){ 
				System.err.println("ERROR: " + e.getMessage());
			}
		
			if(useTimeStamp){
				// Add the parameter String to the List with the prepended time stamp.
				tempFileData.add(logTimeStamp + " " + logText);
			} else {
				// Add the parameter String to the List without the prepended time stamp
				tempFileData.add(logText);
			}
		
			// Write the List of log data to file
			try {
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(logFilePath), "utf-8"));
				// Loop through each "line" of the log List and write it to file
				for(int x = 0; x <= tempFileData.size() - 1; x++){
					writer.write(tempFileData.get(x)); 
					writer.newLine();
				}
			} catch (IOException e) {
				System.err.println("ERROR: " + e.getMessage());
			} finally {
				try {writer.close();} catch (Exception e) { 
					System.err.println("ERROR: " + e.getMessage());
				}
			}
			tempFileData.clear();
		}
	}
	
	/**
	 * Handles the display and routing of the settings menu. 
	 */
	public static void displaySettings(){
		String inputText = null;
		boolean exitSettings = false;
		boolean SettingsMenuSelected = false;
		int SettingsMenuSelection = 0;
		
		while(!exitSettings){
			System.out.println("Settings Menu:");
			System.out.println("1 ) [" + uniqueReplacements + "] Toggle Unique Replacements");
			System.out.println("2 ) [" + staticReplacement + "] Set Static Replacement");
			System.out.println("3 ) Wipe ALL banned words");
			System.out.println("4 ) [" + enableLogging + "] Toggle Logging");
			System.out.println("5 ) Restore Default Settings");
			System.out.println("6 ) Back");
			
			while(!SettingsMenuSelected){
				System.out.print("\nEnter the number of your choice: ");
				// Capture an integer from the user using Scanner. If we do not get an integer loop through again.
				if(Censor.getScanner().hasNextInt()){
					SettingsMenuSelection = Censor.getScanner().nextInt();
					SettingsMenuSelected = true;
				} else {
					System.out.println("What you entered is not a number!");
					Censor.getScanner().next();
					continue;
				}
			}
			
			try{
				switch(SettingsMenuSelection){
					case 1:
						toggleReplacementSetting();
						saveSettings();
						writeLog("Unique Replacements set to " + uniqueReplacements + "!");
						System.out.println("Unique Replacements set to " + uniqueReplacements + "!");
						break;
					case 2:
						System.out.print("Enter the static replacement for banned words: ");
						inputText = Censor.getScanner().next();
						setReplacement(inputText);
						saveSettings();
						writeLog("Static replacement changed to " + inputText);
						System.out.println("Static replacement changed to " + inputText);
						break;
					case 3:
						Censor.purgeBannedWords();
						writeLog("All banned words have been removed!");
						System.out.println("All banned words have been removed!");
						break;
					case 4:
						toggleLoggingSetting();
						writeLog("Logging setting has been toggled.!");
						System.out.println("Logging setting has been toggled.!");
						break;
					case 5:
						resetSettings();
						writeLog("Default settings have been restored!");
						break;
					case 6:
						exitSettings = true;
						writeLog("Exit settings set to true!");
						break;
					default:
						System.out.println("You entered " + SettingsMenuSelection + ". " + SettingsMenuSelection + " is not a valid choice.");
						writeLog("You entered " + SettingsMenuSelection + ". " + SettingsMenuSelection + " is not a valid choice.");
						break;
				}
			} catch(Exception e) {
				writeLog("Error: " + e.getMessage());
			}
			SettingsMenuSelected = false;
			SettingsMenuSelection = 0;
		}
	}
	
	/**
	 * Saves the settings to a file to keep the settings persistent
	 */
	public static void saveSettings(){
		settingsFilePath = "config.txt";
	
		// Since we don't need to keep the settings from the log file after they have been loaded in we can simply rewrite the file using the values loaded in or set. 
		// As you can imagine, multiple instances of the same config declaration is highly inefficient. It is because of this reason that unlike the log file we don't simply append more information to the config file. 
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(settingsFilePath), "utf-8"));
			
			writer.write("uniqueReplacements=" + uniqueReplacements); 
			writer.newLine();
			writer.write("staticReplacement=" + staticReplacement);
			writer.newLine();
			writer.write("enableLogging=" + enableLogging);
			
			writeLog("Settings saved to file!");
			
		} catch (IOException e) {
			writeLog("Error: " + e.getMessage());
		} finally {
			try {writer.close();} catch (Exception e) { 
				writeLog("Error: " + e.getMessage()); 
			}
		}
	}
	
	/**
	 * Loads settings from a config file with the name "config.txt"
	 */
	public static void loadSettings(){
		loadSettings("config.txt");
	}
	
	/**
	 * Loads settings from a config file.
	 * @param configFile The path to the config file including the filename and extension. Relative link is permitted (i.e. same directory = "config.txt").
	 */
	public static void loadSettings(String configFile){
				settingsFilePath = configFile;
			
				try{ 
					FileInputStream fileStream = new FileInputStream(settingsFilePath); // Open the file
					DataInputStream in = new DataInputStream(fileStream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
					while((lineFromFile = br.readLine()) != null){
						vars = lineFromFile.split("="); // Parse the line into multiple Strings using equals as the delimiter
						validateSettingFromFIle(vars[0], vars[1]);
					}
					fileStream.close();
					in.close();
					br.close(); 
				}catch (Exception e){ 
					writeLog("Error: " + e.getMessage());
					saveSettings();
				}
	}
	
	/**
	 * Checks settings variable names and values from the config value against actual variables storing matches as program settings. 
	 * @param variableName The name of the variable from file. (i.e. everything to the left of the equals sign in "variable=value")
	 * @param variableValue The value of the variable from file. (i.e. everything to the right of the equals sign in "variable=value")
	 */
	private static void validateSettingFromFIle(String variableName, String variableValue){
		if(variableName.equalsIgnoreCase("uniqueReplacements")){
			if(variableValue.equalsIgnoreCase("true")){
				uniqueReplacements = true;
			} else if(variableValue.equalsIgnoreCase("false")){
				uniqueReplacements = false;
			} else {
				writeLog("CONFIG ERROR: Config file contains an invalid variable value! Value entered is " + variableValue + " for variable " + variableName + "; Try using value 'True' or 'False'");
			}
		}
		
		if(variableName.equalsIgnoreCase("staticReplacement")){
			staticReplacement = variableValue;
		}
		
		if(variableName.equalsIgnoreCase("enableLogging")){
			if(variableValue.equalsIgnoreCase("true")){
				enableLogging = true;
			} else if(variableValue.equalsIgnoreCase("false")){
				enableLogging = false;
			} else {
				writeLog("CONFIG ERROR: Config file contains an invalid variable value! Value entered is " + variableValue + " for variable " + variableName + "; Try using value 'True' or 'False'");
			}
		}
	}
	
	/**
	 * Fetches the uniqueReplacements setting which describes whether or not the program should use a static replacement or dynamic replacement. Static replacement would mean that one word replaces every banned word
	 * while dynamic replacements would honor the replacement assignments for each banned word added.
	 * @return uniqueReplacements value either True for dynamic or False for static
	 */
	public static boolean getReplacementSetting(){
		return uniqueReplacements;
	}
	
	/**
	 * Switches the boolean value of uniqueReplacements. Turns static replacements on or off.
	 */
	public static void toggleReplacementSetting(){
		if(uniqueReplacements){
			uniqueReplacements = false;
		} else {
			uniqueReplacements = true;
		}
	}
	
	/**
	 * Fetches the word used for static replacements.
	 * @return staticReplacement value which defines the word that will be used should static replacements be enabled
	 */
	public static String getReplacement(){
		return staticReplacement;
	}
	
	/**
	 * Change the word that is used for static replacements.
	 * @param replacement The new static replacement string
	 */
	public static void setReplacement(String replacement){
		staticReplacement = replacement;
	}
	
	/**
	 * Determines if the program writes to a log file or not
	 * @return enableLogging 
	 */
	public static boolean getLoggingSetting(){
		return enableLogging;
	}
	
	/**
	 * Turn the logging setting on or off based on its current state.
	 */
	public static void toggleLoggingSetting(){
		if(enableLogging){
			enableLogging = false;
		} else {
			enableLogging = true;
		}
	}
	
	/**
	 * Restores the default settings
	 */
	public static void resetSettings(){
		uniqueReplacements = true;
		staticReplacement = "{REDACTED}";
		enableLogging = true;
	}
}
