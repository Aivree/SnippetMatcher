#include<string>
#include<iostream>

int main(int argc, char* argv[])
{
    std::string strILoveYou = "i<3U";

    const char * szMe2 = strILoveYou.c_str();

    char * notConstantCharPtr = new char[strILoveYou.length()];
    strcpy(notConstantCharPtr, strILoveYou.c_str());

    cout    <<  strILoveYou         <<  endl;
    cout    <<  szMe2               <<  endl;
    cout    <<  notConstantCharPtr  <<  endl;

    return 0;
}