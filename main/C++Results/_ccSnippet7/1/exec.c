/**
 * Wykonywanie komend systemowych z przechwytywaniem wyniku
 * @param cmd - komenda do wykonania
 * @returns std::string - wynik wykonania
 */
std::string exec(char* cmd) {
    FILE* pipe = _popen(cmd, "r");
    if (!pipe) return "ERROR";
    char buffer[128];
    std::string result = "";
    while(!feof(pipe)) {
    	if(fgets(buffer, 128, pipe) != NULL)
    		result += buffer;
    }
    _pclose(pipe);
    return result;
}
