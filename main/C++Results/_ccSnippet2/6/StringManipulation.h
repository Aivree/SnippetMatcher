#include <vector>
#include <string>
#include <sstream>

using namespace std;

// Given a string and a character delimiter, this method will create a vector 
// of the substrings of the string split up by the given delimiter.
vector<string> &split (const string &s, char delim) {
    vector<string> *elems = new vector<string>();
    stringstream ss(s);
    string item;
    while (getline (ss, item, delim))
        elems->push_back (item);
    return *elems;
}

// Convert a given data type (that has the << operator defined) to a string
template<class T>
string toString (const T &t) {
    stringstream ss;
    ss << t;
    return ss.str ();
}

// Trim a specific character from the head of a string
string trimHead (const string &str, const char ch) {
    string retVal = str;
    
    while (retVal.size () > 0 && retVal[0] == ch) 
        retVal = retVal.substr (1);
    
    return retVal;
}

// Trim a specific character from the tail of a string
string trimTail (const string &str, const char ch) {
    string retVal = str;
    
    while (retVal.size () > 0 && retVal[retVal.size() - 1] == ch)
        retVal = retVal.substr (0, retVal.size () - 1);
    
    return retVal;
}

// Trim a specific character from the head and tail of a string
string trim (const string &str, const char ch) {
    string newStr = str;
    
    newStr = trimHead (newStr, ch);
    newStr = trimTail (newStr, ch);
    
    return newStr;
}

// Trim spaces from the head and tail of a string
string trim (const string &str) {
    return trim (str, ' ');
}