"You Can't Say That"
By Carl Sparks (TWiST3DSOFT)
Email: mail@carldsparks.com
Source available at: http://github.com/twist3dsoft
License: GPLv3

Removes "banned words" from a text document provided. Has the ability to add/remove/view banned words. Settings and banned words are saved to a local file to allow persistent access. Basic date and time formatted logs can be created to help debug the program.

//TODO 
// - Rehandle exceptions in Settings.writeLog. writeLog() can't be //called inside of writeLog() doing so will cause a stackoverflow.
// - Add a restore default settings option
// - Actually overwrite the original text file. In the middle of //switching from overwriting the data in an array to the entire file.
//- Handle banned words as root word and with punctuation.
//- Add a GUI version

Unique Replacements
The program has the ability to accept a unique replacement for each banned word or use a static replacement. If every word you want replaced is replaced with a string such as "{REDACTED}" you want static replacements. If you simply want a new more appropriate word for the banned word you want unique replacements. NOTE: Unique replacements are enabled by default. Visit the settings menu to change this behavior.

Default Settings
uniqueReplacements=true
staticReplacement="{REDACTED}"
enableLogging=true

How logs are created
Logs will only be created if the logging option is enabled. A log file is created based on the current date. The default formatting for logs is "2014-30-05-log.txt". PROGRAMMING NOTE: Date and time is obtained via LocalDate and LocalTime. If you wish to write text to the log simply use Settings.writeLog("Log Text") if you are outside of the Settings class and writeLog("Log Text") inside of the settings class. There is no need to create an object of the Settings class.