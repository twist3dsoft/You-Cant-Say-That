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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * TODO CHECKLIST
 * - Actually overwrite the original text file. In the middle of 
 *   switching from overwriting the data in an array to the entire file.
 * - Handle banned words as root word and with punctuation.
 * - Add a GUI version
 */

/**
 * The main class for "You Can't Say That!"
 * @author TWiST3DSOFT
 * @version 1.0 Build 1 5/15/2014
 * 
 */
public class Censor {
	private static String title = "You Can't Say That!";
	private static String author = "By Carl Sparks (TWiST3DSOFT)";
	private static String srclink = "http://github.com/twist3dsoft";
	private static String license = "Copyright 2014, Carl Sparks. GPL-3; See LICENSE; tl;dr = http://goo.gl/5wKYDZ";

	private static Scanner input = new Scanner(System.in);
	private static boolean exitApp = false;
	private static boolean menuSelectionEntered = false;
	private static int MenuSelection = 0;
	private static ArrayList<BannedWord> BannedWordsList = new ArrayList();
	private static String[] vars;
	
	public static void main(String[] args) {
				Settings.writeLog("You can't Say That initiated...");
		
				// Introduction
				System.out.println("*************************************");
				System.out.println("* " + title);
				System.out.println("* " + author);
				System.out.println("* " + srclink);
				System.out.println("* " + license);
				System.out.println("*************************************\n");
				
				//Initialize
				init();

				while(!exitApp){
					System.out.println("Main Menu");
					System.out.println("1 ) Censor a Document");
					System.out.println("2 ) View Banned Words");
					System.out.println("3 ) Add Banned Words");
					System.out.println("4 ) Remove Banned Words");
					System.out.println("5 ) Settings"); 
					System.out.println("6 ) About");
					System.out.println("7 ) Exit");
					
					while(!menuSelectionEntered){
						System.out.print("\nEnter the number of your choice: ");
						// Capture an integer from the user using Scanner. If we do not get an integer loop through again.
						if(input.hasNextInt()){
							MenuSelection = input.nextInt();
							menuSelectionEntered = true;
						} else {
							System.out.println("What you entered is not a number!");
							input.next();
							continue;
						}
						Settings.writeLog(MenuSelection + " entered as main menu selection");
					}
					
					try{
						switch(MenuSelection){
							case 1:
								System.out.println("-------------------------------------");
								censor();
								Settings.writeLog("running censor method");
								System.out.println("-------------------------------------\n");
								break;
							case 2:
								System.out.println("-------------------------------------");
								displayBannedWords();
								Settings.writeLog("running display banned words method");
								System.out.println("-------------------------------------\n");
								break;
							case 3:
								System.out.println("-------------------------------------");
								addBannedWord();
								saveBannedWords();
								Settings.writeLog("running add banned word method");
								System.out.println("-------------------------------------\n");
								break;
							case 4:
								System.out.println("-------------------------------------");
								deleteBannedWord();
								saveBannedWords();
								Settings.writeLog("running delete banned word method");
								System.out.println("-------------------------------------\n");
								break;
							case 5:
								System.out.println("-------------------------------------");
								Settings.displaySettings();
								Settings.writeLog("running display settings method");
								System.out.println("-------------------------------------\n");
								break;
							case 6:
								System.out.println("-------------------------------------");
								about();
								Settings.writeLog("running about method");
								System.out.println("-------------------------------------\n");
								break;
							case 7:
								exitApp = true;
								Settings.writeLog("exit app set true");
								break;
							default:
								System.out.println("You entered " + MenuSelection + ". " + MenuSelection + " is not a valid choice.");
								Settings.writeLog("invalid menu selection entered: " + MenuSelection);
								break;
						}
					} catch(Exception e) {
						Settings.writeLog("Error: " + e.getMessage());
					}
					MenuSelection = 0; // Reset the menu selection so we can loop through it again
					menuSelectionEntered = false; // Tell the program that the user has not entered anything for a menu selection
				}
				input.close();
	}
	
	/**
	 * Loads essential settings from local files to program variables.
	 */
	private static void init(){	
		// Create a banned word and add it to the list of banned words
		// This is just dummy banned word for now
		/*
		BannedWord fake = new BannedWord();
		fake.setBannedString("Bad");
		fake.setReplacement("good");
		BannedWordsList.add(fake);
		*/
		
		loadBannedWords();
		Settings.loadSettings();
	}
	
	/**
	 * Takes a file and checks each word against the banned words using isBanned() 
	 */
	private static void censor(){
		List<String> tempFileData = new ArrayList<String>(); // Stores the file content for the log before it gets written to file
		BufferedWriter writer = null;
		Scanner textInput = new Scanner(System.in);
		String filePath = null;
		String lineFromFile = null;
		String tempString = null;
		int BannedWordCounter = 0;
		
		System.out.print("Enter the file path to the text file(if in the same directory just enter 'filename'.txt: ");
		filePath = textInput.nextLine(); // Catch the file path provided by the user
		System.out.println(""); // Used to add a space after the Scanner; This feels so dirty.
		
		Settings.writeLog("attempting to open file " + filePath + "...");
		try{ // Attempt to process the file
			FileInputStream fileStream = new FileInputStream(filePath); // Open the file
			DataInputStream in = new DataInputStream(fileStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			// Read from the file one line at a time
			while((lineFromFile = br.readLine()) != null){
				vars = lineFromFile.split(" "); // Parse the line into multiple Strings using space as the delimiter
				for(int x = 0; x <= vars.length - 1; x++){
					
					if(isBanned(vars[x], x)){
						Settings.writeLog("Checking if " + vars[x] + " is a banned word");
						// Add to the counter
						BannedWordCounter++;
					}
					tempString = tempString + " " + vars[x];
					//System.out.print(vars[x] + " ");
				}
				tempFileData.add(tempString);
			}
			vars = null;
			fileStream.close();
			in.close();
			br.close();
		}catch (Exception e){ // If there was an error processing the file or its data output the error to the user
			Settings.writeLog("ERROR: " + e.getMessage());
		}
		
		Settings.writeLog("tempFileData size: " + tempFileData.size());
		
		// Write the changed document to file
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath), "utf-8"));
			// Loop through each "line" of the log List and write it to file
			for(int x = 0; x <= tempFileData.size() - 1; x++){
				System.out.println("tempFileData " + x + ": " + tempFileData.get(x) );
				/*
				writer.write(tempFileData.get(x)); 
				writer.newLine();
			   */
			}
		} catch (IOException e) {
			Settings.writeLog("Error: " + e.getMessage());
		} finally {
			try {writer.close();} catch (Exception e) { 
				Settings.writeLog("Error: " + e.getMessage()); 
			}
		}
		
		// Display results of the censor
		if(BannedWordCounter > 1){
			System.out.println("\n\nCensor complete! " + BannedWordCounter + " words have been censored.\n");
			Settings.writeLog("\n\nCensor complete! " + BannedWordCounter + " words have been censored.\n");
		} else if(BannedWordCounter == 1){
			System.out.println("\n\nCensor complete! " + BannedWordCounter + " word has been censored.\n");
			Settings.writeLog("\n\nCensor complete! " + BannedWordCounter + " word has been censored.\n");
		} else {
			System.out.println("\n\nCensor complete! No words have been censored.\n");
			Settings.writeLog("\n\nCensor complete! No words have been censored.\n");
		}
		textInput.close();
	}
	
	/**
	 * Determines if a word is banned by comparing each word against the banned words. If the word is banned it is changed on the spot
	 * based on the uniqueReplacements setting.
	 * @param word The word being checked
	 * @param wordID The array index of the word from the vars[]
	 * @return True if the word is banned. False if the word is not banned.
	 */
	private static boolean isBanned(String word, int wordID){
		boolean bannedWordFound = false;
		
		for(int x = 0; x <= BannedWordsList.size() - 1; x++){
			if(BannedWordsList.get(x).toString().equalsIgnoreCase(word)){
				Settings.writeLog(vars[wordID] + " is banned");
				
				if(Settings.getReplacementSetting()){
					vars[wordID] = getBannedWord(x).getReplacement();
				} else {
					vars[wordID] = Settings.getReplacement();
				}
				
				bannedWordFound = true;
			}
		}
		
		if(bannedWordFound){ 
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Prints a list of the banned words with human readable indexes (i.e. Array index + 1)
	 */
	private static void displayBannedWords(){
		System.out.println("Banned Words: Showing " + BannedWordsList.size());
		for(int x = 0; x <= BannedWordsList.size() - 1; x++){
			System.out.println((x + 1) + ". " + getBannedWord(x).getBannedString() + " -> " + getBannedWord(x).getReplacement());
		}
	}
	
	/**
	 * Prompts the user for a banned word and its replacement word then adds it to the list of banned words
	 */
	private static void addBannedWord(){
		String bannedWord = null;
		String replacementWord = null;
		BannedWord banned = new BannedWord();
		
		System.out.print("Enter the word you would like to add to the banned list: ");
		bannedWord = input.next();
		
		System.out.print("Enter the word you would like to replace the banned word: ");
		replacementWord = input.next();
		
		Settings.writeLog("New banned word added '" + bannedWord + "' with replacement '" + replacementWord + "'");
		
		banned.setBannedString(bannedWord);
		banned.setReplacement(replacementWord);
		
		BannedWordsList.add(banned);
		saveBannedWords();
		System.out.println("Banned word '" + bannedWord + "' with replacement '" + replacementWord + "' has been added!");
	}
	
	/**
	 * Removes the record for a banned word from the pool of banned words that are checked.
	 */
	private static void deleteBannedWord(){
		int bannedWord = 0;
		boolean removePrompt = false;
		
		displayBannedWords();
		
		while(!removePrompt){
			System.out.print("Enter the number of the banned word you want to remove: ");
			if(input.hasNextInt()){
				bannedWord = input.nextInt();
				bannedWord--;
				removePrompt = true;
			} else {
				System.out.println("What you entered is not a number!");
				input.next();
				continue;
			}
		}
		
		Settings.writeLog("removing " + BannedWordsList.get(bannedWord).getBannedString());
		BannedWordsList.remove(bannedWord);
		saveBannedWords();
		System.out.println("Banned word # " + (bannedWord + 1) + " has been deleted!");
	}
	
	/**
	 * Displays information about the software
	 */
	private static void about(){
		System.out.println("Title: " + title);
		System.out.println("Author: " + author);
		System.out.println("Source: " + srclink);
		System.out.println("License: " + license);
	}
	
	/**
	 * Get the Banned Word object from the BannedWordsList
	 * @param x Refers to the Array index of a specific BannedWord object
	 * @return A BannedWord object associated with the index specified
	 */
	private static BannedWord getBannedWord(int x){
		return BannedWordsList.get(x);
	}
	
	/**
	 * Clears the BannedWordsList List
	 */
	public static void purgeBannedWords(){
		Settings.writeLog("purging banned words");
		BannedWordsList.clear();
		saveBannedWords();
	}
	
	private static void loadBannedWords(){
		loadBannedWords("banned-words.txt");
	}
	
	/**
	 * Loads banned words from file into the BannedWordsList
	 */
	private static void loadBannedWords(String configFile){
		String bannedFilePath = configFile;
		String lineFromFile = null;
		
		try{ 
			FileInputStream fileStream = new FileInputStream(bannedFilePath); // Open the file
			DataInputStream in = new DataInputStream(fileStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
			while((lineFromFile = br.readLine()) != null){
				vars = lineFromFile.split("-"); // Parse the line into multiple Strings using equals as the delimiter

				BannedWord newBannedWord = new BannedWord();
				newBannedWord.setBannedString(vars[0]);
				newBannedWord.setReplacement(vars[1]);
				BannedWordsList.add(newBannedWord);
			}
			vars = null;
			fileStream.close();
			in.close();
			br.close(); 
		}catch (Exception e){ 
			Settings.writeLog("Error: " + e.getMessage());
		}
	}
	
	/**
	 * 
	 */
	private static void saveBannedWords(){
		BufferedWriter writer = null;
		String bannedFilePath = "banned-words.txt";
		
		// Write the List of log data to file
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(bannedFilePath), "utf-8"));
			// Loop through each "line" of the log List and write it to file
			for(int x = 0; x <= BannedWordsList.size() - 1; x++){
					writer.write(BannedWordsList.get(x).getBannedString() + "-" + BannedWordsList.get(x).getReplacement()); 
					writer.newLine();
			}
		} catch (IOException e) {
			Settings.writeLog("Error: " + e.getMessage());
		} finally {
			try {writer.close();} catch (Exception e) { 
				Settings.writeLog("Error: " + e.getMessage()); 
			}
		}
	}
}
