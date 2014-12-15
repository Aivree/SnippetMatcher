#include <boost/algorithm/string.hpp>
using namespace std;
using namespace boost::algorithm;

string str1(" hello world! ");
trim(str1);

// str1 is now "hello world!"
// Use trim_right() if only trailing whitespace is to be removed.