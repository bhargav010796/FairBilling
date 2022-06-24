# FairBilling

I have divided code into 3 Steps 

1. Reading file from an arg
2. Group the Data based on username
3. Caluclating Session and Duration and placed into Result Model


Step 1:
 
 Reading file from main method argument 0.
 Spliting logs with \n(new line) to a List of strings and spliting again each line to a conceret object(InputModel - for ease use).
 
Step 2:

Grouping the InuptModel on username.

Step 3:

Looping the grouped data
* check whether user is having start, if Yes add session and finding for end if present find the difference between start and end for that user session and calculate duration 
* If there is an “End” with no possible matching start, the startTime has the earliest time of any record in the file and using diffTimeData method will update duaration to the result Model.
* If there is a “Start” with no possible matching “End”, the endTime has consider the lastest time of any record in the file and using diffTimeData methodwill update duaration to the result Model.

Eample:

> javac FairBilling.java
> java FairBilling <log-file-name>
 
 Note: Just for this program, added all classes and resuable method in one file. Ideally we can split them on thier own. From past experience it's good have a diffTimeData on it's own method which give us fexlibility for testing/mocking. 
