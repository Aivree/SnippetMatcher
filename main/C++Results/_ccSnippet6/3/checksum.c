#include<iostream>
#include <string.h>
#include<bitset>
using namespace std;

unsigned long calculateChecksum(char* command)
{
	int length = sizeof (command) /sizeof (command[0]);
	bitset<8> checksum=0;
	bitset<8> dummy;
	for(int i = 0; i < length; i++){
		dummy = command[i];
		checksum = checksum^ dummy;
	}
		return checksum.to_ulong();
}


int main(){

	string s;
	cin>>s;
	int len = s.length();

	char *c = new char[len];
	strcpy(c,s.c_str());	
	
	std::cout<<calculateChecksum(c,len)<<std::endl;
}
