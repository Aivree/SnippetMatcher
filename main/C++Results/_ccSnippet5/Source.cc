/ USING ANOTHER STRING - AUTO MEMORY MANAGEMENT, EXCEPTION SAFE
std::string old_x = x;
// - old_x will not be affected by subsequent modifications to x...
// - you can use `&old_x[0]` to get a writable char* to old_x's textual content
// - you can use resize() to reduce/expand the string
//   - resizing isn't possible from within a function passed only the char* address

std::string old_x = x.c_str(); // old_x will terminate early if x embeds NUL
// Copies ASCIIZ data but could be less efficient as it needs to scan memory to
// find the NUL terminator indicating string length before allocating that amount
// of memory to copy into, or more efficient if it ends up allocating/copying a
// lot less content.
// Example, x == "ab\0cd" -> old_x == "ab".

// USING A VECTOR OF CHAR - AUTO, EXCEPTION SAFE, HINTS AT BINARY CONTENT, GUARANTEED CONTIGUOUS EVEN IN C++03
std::vector<char> old_x(x.data(), x.size());       // without the NUL
std::vector<char> old_x(x.c_str(), x.size() + 1);  // with the NUL

// USING STACK WHERE MAXIMUM SIZE OF x IS KNOWN TO BE COMPILE-TIME CONSTANT "N"
// (a bit dangerous, as "known" things are sometimes wrong and often become wrong)
char y[N + 1];
strcpy(y, x.c_str());

// USING STACK WHERE UNEXPECTEDLY LONG x IS TRUNCATED (e.g. Hello\0->Hel\0)
char y[N + 1];
strncpy(y, x.c_str(), N);  // copy at most N, zero-padding if shorter
y[N] = '\0';               // ensure NUL terminated

// USING THE STACK TO HANDLE x OF UNKNOWN (BUT SANE) LENGTH
char* y = alloca(x.size() + 1);
strcpy(y, x.c_str());

// USING THE STACK TO HANDLE x OF UNKNOWN LENGTH (NON-STANDARD GCC EXTENSION)
char y[x.size() + 1];
strcpy(y, x.c_str());

// USING new/delete HEAP MEMORY, MANUAL DEALLOC, NO INHERENT EXCEPTION SAFETY
char* y = new char[x.size() + 1];
strcpy(y, x.c_str());
//     or as a one-liner: char* y = strcpy(new char[x.size() + 1], x.c_str());
// use y...
delete[] y; // make sure no break, return, throw or branching bypasses this

// USING new/delete HEAP MEMORY, SMART POINTER DEALLOCATION, EXCEPTION SAFE
// see boost shared_array usage in Johannes Schaub's answer

// USING malloc/free HEAP MEMORY, MANUAL DEALLOC, NO INHERENT EXCEPTION SAFETY
char* y = strdup(x.c_str());
// use y...
free(y);