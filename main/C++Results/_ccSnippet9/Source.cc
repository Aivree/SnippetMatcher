#include<iostream>
#include<fstream>
#include<string>
#include<cstdlib>
#include<sstream>

std::string ssystem (const char *command) {
    char tmpname [L_tmpnam];
    std::tmpnam ( tmpname );
    std::string scommand = command;
    std::string cmd = scommand + " >> " + tmpname;
    std::system(cmd.c_str());
    std::ifstream file(tmpname, std::ios::in );
    std::string result;
        if (file) {
      while (!file.eof()) result.push_back(file.get());
          file.close();
    }
    remove(tmpname);
    return result;
}

    //for cygwin

int main(int argc, char *argv[])
{
        std::string bash = "FILETWO=/cygdrive/c/*\nfor f in $FILETWO\ndo\necho \"$f\"\ndone ";
    std::string in;
        std::string s = ssystem(bash.c_str());
    std::istringstream iss(s);
        std::string line;
        while ( std::getline(iss, line) )
        {
      std::cout << "LINE-> " + line + "  length: " << line.length() << std::endl;
        }
    std::cin >> in;
    return 0;
}