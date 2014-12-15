#Readme

This package has dependency on the BeautifulSoup python libarary which to parse HTML.
use the command 'sudo python setup.py install' from the main directory to install the dependency.

To execute the script for a particular snippet, follow the following instructions

1) Make 1 entry per snippet in the main/Snippets/snippetSourceInfo.txt file

Entry format is

<id>:<language>:<path of file containing the snippet>:<space seperated keywords>

For example:- 
1:csharp:Snippets/csharp/Q1Right.cs:int double TimeSpan DateTime UtcNow Math Abs Floor Ticks TotalSeconds Seconds Minutes Hours Days Convert ToInt32

2) Run the file driver.py

3) The results for the script will be stored in the _<language><id> folder in the _results.txt file.
