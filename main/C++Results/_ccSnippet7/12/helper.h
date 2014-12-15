#ifndef HELPER_H
#define HELPER_H


#include <unittest++/UnitTest++.h>
#include "./../cpp/Master/types.h"

/**
 * @brief execute system command and fetch stdout
 * @see http://stackoverflow.com/questions/478898/how-to-execute-a-command-and-get-output-of-command-within-c
 * @param cmd command
 * @return output
 */
str exec(const char* cmd) {
    FILE* pipe = popen(cmd, "r");
    if (!pipe) return "ERROR";
    char buffer[128];
    std::string result = "";
    while(!feof(pipe)) {
        if(fgets(buffer, 128, pipe) != NULL)
            result += buffer;
    }
    pclose(pipe);
    return result;
}

/**
 * @brief returns Mathematica equivalent of z
 * @param z
 * @return string Mathematica expression
 */
str getComplexAsStr(cplx z) {
	stringstream ss;
	ss << "ImportString[\\\""<<setw(10)<<scientific<<z.real()<<"\\\",\\\"Table\\\"][[1,1]]+ImportString[\\\""<<setw(10)<<scientific<<z.imag()<<"\\\",\\\"Table\\\"][[1,1]]*I";
	return ss.str();
}

/**
 * @brief check complex number against Mathematica
 * @param expectedExpr expected Mathematica expression
 * @param res actual result
 * @param eps relative distance for real and imginary part
 */
void checkComplexInMathematica(str expectedExpr, cplx res, double eps){
	str cmd = "math -noprompt -run \"z=N["+expectedExpr+",10];Print[ToString[Re[z]]];Print[ToString[Im[z]]];Exit[];\"";
	//cout<<cmd<<endl;
	str out = exec(cmd.c_str());
	stringstream in;
	in.str(out);
	double re, im;
	char s;
	/* Output is e.g.:
"-0.6509231993"
"-0.3016403205"
	*/
	in >> s >> re >> s >> s >> im;
	CHECK_CLOSE(re,res.real(),fabs(eps*re));
	CHECK_CLOSE(im,res.imag(),fabs(eps*im));
}

#endif // HELPER_H
