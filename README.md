## Expectations
To receive detailed feedback from reviewers how it could be improved to match BigID company standards if any issues with performance or memory.

## Overview
Simple java program for finding specific strings in a large text. 

Print result in format:

```Carl-->[{charOffset=19, lineOffset=18672}, {charOffset=20, lineOffset=19840}]```


## Project structure
Project consists of the three modules.
1. ```main``` module reads a file and immediately sends chunks of the 1000 lines to the matcher asynchronous
2. ```matcher``` - gets a text string as input and searches for matches of a given set of strings. 
The result is a map from a word to its location(s) in the text 
3. ```aggregator``` - combine the results and prints the results.


## Usage
Check file ```big.txt``` with large text is in the project root

Navigate to the ```Main.java``` of the ```com.bigId.fileReader``` package of the ```main``` module

Run ```main``` method

## Assumptions

1. **lineOffset** related to the entire text file, starting from first line
2. **charOffset** related to the line, starting from 1 for each particular line where word was found
3. Searching names in text with exact matching and case-sensitive (pattern ```%s([^A-Za-z]|$)```).

   Example line text:  **John** on the call with **John**ny: will match only **John** at the beginning of the line

   Example line text:  **John's** notebook: will match

   Example line text:  **John[text]** notebook: will match

   Example line text:  **John,** text after a comma: will match

