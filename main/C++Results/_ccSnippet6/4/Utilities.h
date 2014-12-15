#ifndef UTILITIES_H
#define UTILITIES_H
#include <string>
#include <sstream>
class Utilities
{
public:
	static char* getParentFolder(const char* path)
	{
		char* folderPath;
		folderPath = new char[strlen(path)];
		string str = path;
		int i = str.find_last_of('/') + 1;
		strcpy(folderPath,str.substr(0,i).c_str());
		folderPath[i+1] = 0;
		return folderPath;
	}
	static char* charcat(char* char1,char* char2)
	{
		char* result;
		result = (char*)malloc(strlen(char1)+strlen(char2)+1);
		strcpy(result,char1);
		strcat(result,char2);
		return result;
	}
	static char* toChar(string str)
	{
		char* c = new char[str.length()];
		strcpy(c,str.c_str());
		return c;
	}
	static char* toChar(int n)
	{
		ostringstream os;
		os<<n;
		return toChar(os.str());
	}
	static char* toChar(float n)
	{
		ostringstream os;
		os<<n;
		return toChar(os.str());
	}
	static string toString(int n)
	{
		ostringstream os;
		os<<n;
		return os.str();
	}
	static string toString(float n)
	{
		ostringstream os;
		os<<n;
		return os.str();
	}
};
#endif