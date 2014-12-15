#pragma once
#include <string>
#include <time.h>
#include <functional> 
#include <algorithm>
#include <sstream>

using namespace std;

//* STRIGN MANIPULATION */
//TRIMMING
// trim from end
static inline string &rtrim(string &s) {
		s.erase(find_if(s.rbegin(), s.rend(), not1(ptr_fun<int, int>(isspace))).base(),	s.end());
		return s;
}
//// trim from start
static inline string &ltrim(string &s) {
		s.erase(s.begin(), find_if(s.begin(), s.end(), not1(ptr_fun<int, int>(isspace))));
		return s;
}
//// trim from both ends
static inline std::string &trim(std::string &s) {
		return ltrim(rtrim(s));
}

string SysStrToStr(System::String^ s ){
    using System::IntPtr;
    using System::Runtime::InteropServices::Marshal;

	string os;
    const char* chars = (const char*)(Marshal::StringToHGlobalAnsi(s)).ToPointer( );
    os = chars;
    Marshal::FreeHGlobal(IntPtr((void*)chars));
	return os;
}

string TimeToString(time_t date, char *format = "%Y-%m-%d"){
	time(&date);
	struct tm  *timeStruct = localtime(&date);
	char buffer [80];
	strftime(buffer,80,format,timeStruct);

	return (string)buffer;
}

time_t TimeToSeconds(int year, int month, int day){
	time_t temp = time(0);
	struct tm  *timeStruct = localtime(&temp);
	
	timeStruct->tm_year = year - 1900;
	timeStruct->tm_mon = month -1;
	timeStruct->tm_mon = day;
	
	return mktime(timeStruct);
}

double string_to_double(const string& s) {
   istringstream i(s);
   double x;
   if (!(i >> x))
	 return 0;
   return x;
 }


