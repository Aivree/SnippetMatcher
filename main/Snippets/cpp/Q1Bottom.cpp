#include <regex>
#include <string>
#include <vector>

std::vector<string> Tokenize( const string str, const std::regex regex )
{
    using namespace std;

    std::vector<string> result;

    sregex_token_iterator it( str.begin(), str.end(), regex, -1 );
    sregex_token_iterator reg_end;

    for ( ; it != reg_end; ++it ) {
        if ( !it->str().empty() ) //token could be empty:check
            result.emplace_back( it->str() );
    }

    return result;
}