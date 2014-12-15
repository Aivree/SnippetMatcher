#include <cstdio>
#include <string>
#include <algorithm>
using namespace std;

bool checkans;

void check(string gen, string ans) {
	checkans=1;
	char genc[8], ansc[8];
	int genalph[25] = {0}, ansalph[25]={0};
	strcpy(genc,gen.c_str());
	strcpy(ansc,ans.c_str());
	for (int chi = 0; chi<=8;  chi++) {
        genalph[genc[chi]-'A']++;
        ansalph[ansc[chi]-'A']++;
    }
    for (int j = 0; j < 26; j++){
	    if ((genalph [j] > 8) || (ansalph[j] > 8)) continue;
        if (genalph[j] < ansalph[j]) {
            checkans = false;
			break;	
		}
	}
}

void judge (int pt, bool checkans,string ans) {
	if (checkans==true) {
		if (ans.length()==9) {
			pt=pt+18;
			}
		else {
			pt=pt+ans.length();
			}
		printf ("Your answer is accepted \n");
	}
	else {
		printf ("But you have used letters that are not given.  \n");
	}
}
