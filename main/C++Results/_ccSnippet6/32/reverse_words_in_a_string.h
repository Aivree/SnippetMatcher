/*
 * Author : Jeremy Zhao
 * Email  : jqzhao@live.com
 * Date   : 2014/11/03
 *
 * Source : https://oj.leetcode.com/problems/reverse-words-in-a-string/
 * Problem:	Reverse Words in a String
 * Description: 
 *	Given an input string, reverse the string word by word.
 *	For example,
 * 	Given s = "the sky is blue",
 *	return "blue is sky the".
 *
 */
#include <string.h>
#include <string>
using std::string;

class Solution {
	public:
	    void reverseWords(string &s) {
			int length = s.size();
			if (length == 0) {
				return;
			}
			string reversed_string;
			char str[length + 1];
			strcpy(str, s.c_str());
			reverse(str, 0, length - 1);

			int begin = 0;
			int end = 0;
			int i;
			for (i = 0; i < length; i++) {
				if (str[i] != ' ') {
					end = i;
				}
				else {
					if (begin != i) {
						reverse(str, begin, end);
						reversed_string += string(str + begin, end - begin + 1);
						reversed_string += " ";
					} 
					begin = i + 1;
				}
			}

			if (begin <= end) {
				reverse(str, begin, end); 
				reversed_string += string(str + begin, end - begin + 1);
			}
			strcpy(str, reversed_string.c_str());
			length = reversed_string.size();
			for (i = length - 1; i >= 0 && str[i] == ' '; i--) {
			}
			end = i;
			s = string(str, end + 1);
		}

		void reverse(char str[], int begin, int end) {
			int middle = (end - begin) / 2;
			char ch;
			for (int i = 0; i <= middle; i++) {
				ch = str[begin + i];
				str[begin + i] = str[end - i];
				str[end - i] = ch;
			}
		}
};
