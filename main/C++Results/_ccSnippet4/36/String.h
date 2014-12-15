/***************************************************************
 * File: String.h
 * Author: Joe <tongwei521@gmail.com>
 *
 */

#ifndef STRING_H_INCLUDED
#define STRING_H_INCLUDED

#include <iostream>
#include <iterator>
#include <algorithm>
#include <cctype>
#include <cstring>
#include <assert.h>


namespace Joe {
#define SmallOrLarge 31
class String {
public:
    /* default constructor */
    String();
    /* construct a string with a char*/
    String(char ch);
    /* constructor wiht the char * as a parameter */
    String(const char *str);
    /* use a iterater range to construct a string*/
    template <typename IT>
    String(IT start, IT end);
    /* copy constructor */
    String(const String &other);
    /* destructor */
    ~String();


    /* define the type: iterrator */
    typedef char * iterator;
    /* define the type: const_iterator */
    typedef const char * const_iterator;

    /*
     * typedef the stl interator for the class.
     * Type: reverse_iterator
     * Type: const_reverse_iterator
     */
    typedef std::reverse_iterator<iterator> reverse_iterator;
    typedef std::reverse_iterator<const_iterator> const_reverse_iterator;

    /* iterator begin(), get the begin of the range*/
    iterator begin();
    const_iterator begin() const;
    /* iterator end(), get the end of the range*/
    iterator end();
    const_iterator end() const;

    /* reverse_iterator rbegin(); */
    reverse_iterator rbegin();
    const_reverse_iterator rbegin() const;

    /* reverse_iterator rend(); */
    reverse_iterator rend();
    const_reverse_iterator rend() const;

    /* get the size of the string */
    std::size_t size() const;
    /* is the string is empty */
    bool is_empty() const;

    /*
     * iterator insert, use different iterators to realize various inserts
     * insert a range
     */
    template <typename IT>
    iterator insert(iterator pos, IT start, IT end);
    /* iterator insert, insert a char element */
    iterator insert(iterator pos, char ele);
    /* iterator erase */
    iterator erase(iterator pos);
    /* iterator erase a range*/
    iterator erase(iterator start, iterator end);
    /* swap the content with other string */
    void swap(String &other);
    /* get the capacity of the string */
    size_t capacity() const;
    /* reverse the string */
    void reserve(std::size_t space);

    /* assign operator */
    String &operator =(const String &other);
    /* [] operator, as a left value */
    char &operator [](std::size_t index);
    /* [] operator, as a const value */
    char operator [](std::size_t index) const;
    /* += operator */
    String &operator +=(const String &other);
    /* "*" operator, pythonic */
   // String &operator *(const int times);

    const char* c_str() const;

    /*get the substring*/
    //template <typename IT>
    //String &substring(IT start, IT end);
    String &substring(std::size_t index1, std::size_t index2);

    /* a constant to catagory a string is small or large */
    //static const size_t SmallOrLarge = 31;    /* default constructor */
private:


    /* the LargeString Structure */
    struct LargeString {
        char* elems;
        size_t size;    // size of the string
        size_t max;     // capacity
    };
    /* the SmallString Structure */
    struct SmallString {
        unsigned char size;         // size
        char elems[SmallOrLarge];   // the buffer
    };

    /*a union of small and large*/
    union UnionString {
        LargeString largeString;
        SmallString smallString;
    };

    UnionString m_str;
    bool m_is_small;
};

/* implementations of member methods */

/* Public Scope */
/* constructors implementations */

/* default constructor */
String::String() {
    m_is_small = true;
    m_str.smallString.size = 0;
}

/* construct a string with a char*/
String::String(char ch) {
    m_is_small = true;
    m_str.smallString.size = 1;
    m_str.smallString.elems[0] = ch;
}

/* constructor wiht the char * as a parameter */
String::String(const char *str) {
    const size_t length = std::strlen(str);
    m_is_small = (length <= SmallOrLarge);
    if (!m_is_small) {
        m_str.largeString.size = length;
        m_str.largeString.max = length;
        m_str.largeString.elems = new char[m_str.largeString.max];
    }
    else {
        m_str.largeString.size = (unsigned char)length;
    }
    std::copy(str, str + length, begin());
}

/* use a iterater range to construct a string*/
template <typename IT>
String::String(IT start, IT end) {
    m_is_small = true;
    m_str.smallString.size = 0;
    insert(begin(), start, end);
}


/* copy constructor */
String::String(const String &other) {
    m_is_small = other.m_is_small;
    if (!m_is_small) {
        m_str.largeString.size = other.m_str.largeString.size;
        m_str.largeString.max = other.m_str.largeString.max;
        m_str.largeString.elems = new char[m_str.largeString.max];
    }
    else {
        m_str.smallString.size = other.m_str.smallString.size;
    }
    std::copy(other.begin(), other.end(), begin());
}

/* destructor implementation*/
String::~String() {
    if (!m_is_small) {
        delete [] m_str.largeString.elems;
    }
}

const char* String::c_str() const {
    String* me = const_cast<String*>(this);
    me->reserve(size() + 1);
    *me->end() = 0;
    return begin();
}

/* iterator begin(), get the begin of the range*/
String::iterator String::begin() {
    return m_is_small ? m_str.smallString.elems : m_str.largeString.elems;
}
String::const_iterator String::begin() const {
    return m_is_small ? m_str.smallString.elems : m_str.largeString.elems;
}

/* iterator end(), get the end of the range*/
String::iterator String::end() {
    return begin() + size();
}
String::const_iterator String::end() const {
    return begin() + size();
}

/* reverse_iterator rbegin(); */
String::reverse_iterator String::rbegin() {
    return reverse_iterator(end());
}
String::const_reverse_iterator String::rbegin() const {
    return const_reverse_iterator(end());
}

/* reverse_iterator rend(); */
String::reverse_iterator String::rend() {
    return reverse_iterator(begin());
}
String::const_reverse_iterator String::rend() const {
    return const_reverse_iterator(begin());
}

/* get the size of the string */
std::size_t String::size() const {
    return m_is_small ? m_str.smallString.size : m_str.largeString.size;
}
/* is the string is empty */
bool String::is_empty() const {
    return 0 == size();
}

/*
 * iterator insert, use different iterators to realize various inserts
 * insert a range
 */
template <typename IT>
String::iterator String::insert(iterator pos, IT start, IT end) {
    for (; start != end; ++start, ++pos) {
        pos = insert(pos, *start);
    }
    return pos;
}

/* iterator insert, insert a char element */
String::iterator String::insert(iterator pos, char ele) {
    ptrdiff_t offset = pos - begin();
    reserve(size() + 1);
    pos = offset + begin();
    std::copy_backward(pos, end(), end() + 1);
    *pos = ele;

    if (!m_is_small) {
        m_str.largeString.size ++;
    }
    else {
        m_str.smallString.size ++;
    }
    return pos;
}

/* iterator erase */
String::iterator String::erase(iterator pos) {
    return erase(pos, pos + 1);
}

/* iterator erase a range*/
String::iterator String::erase(iterator start, iterator end) {
    const ptrdiff_t num = end - start;
    std::copy(start + num, end, start);

    if (!m_is_small) {
        m_str.largeString.size -= num;
    }
    else {
        m_str.smallString.size -= num;
    }
    return start;
}

/* swap the content with other string */
void String::swap(String &other) {
    std::swap(m_is_small, other.m_is_small);
    std::swap(m_str, other.m_str);
}

/* get the capacity of the string */
std::size_t String::capacity() const {
  return m_is_small ? SmallOrLarge : m_str.largeString.max;
}

/* reverse the string */
void String::reserve(std::size_t space) {
    if (space <= capacity()) {
        return;
    }
    size_t curSize = size();
    size_t newSize = capacity();
    while (newSize < space) {
        newSize *= 2;
    }

    char* newElems = new char[newSize];
    std::copy(begin(), end(), newElems);

    if (!m_is_small) {
        delete [] m_str.largeString.elems;
    }
    else {
        m_str.largeString.size = curSize;
    }
    m_is_small = false;

    m_str.largeString.max = newSize;
    m_str.largeString.elems = newElems;
}

/* assign operator */
String &String::operator =(const String &other) {

}
/* [] operator, as a left value */
char &String::operator [](std::size_t index) {
    return *(begin() + index);
}

/* [] operator, as a const value */
char String::operator [](std::size_t index) const {
    return *(begin() + index);
}

/* += operator */
String &String::operator +=(const String &other) {
    reserve(size() + other.size());
    std::copy(other.begin(), other.end(), end());

    if (!m_is_small) {
        m_str.largeString.size += other.size();
    }
    else {
        m_str.smallString.size += other.size();
    }
    return *this;
}

/* some functions whose parameter is string , but not a method */
/* << and >> operator overload */
std::ostream &operator <<(std::ostream &out, const String &str) {
    return out << str.c_str();
}
std::istream &operator >>(std::istream &in, String &str) {
    String result;
    std::istream::sentry s(in);
    if (s) {
        char ch;
        while (in.get(ch)) {
            if (std::isspace(ch)) {
            in.unget();
            break;
            }
            result += ch;
        }
        std::swap(str, result);
    }
    return in;
}

/* + operator */
const String operator + (const String& lhs, const String& rhs) {
    return String(lhs) += rhs;
}
/* * operator */
const String operator *(const String& str, const int times) {
    String result;
    if (times > 0) {
        for (int i=0; i < times; i++) {
            result += str;
        }
        return result;
    }
    return str;
}

/* get the substring of the string */
//template <typename IT>
//String &String::substring(IT start, IT end) {
//    String sub(start, end);
 //   return sub;
//}

String &String::substring(std::size_t index1, std::size_t index2) {
    String result;
    assert(index1 <= index2);
    for (int i = index1; i < index2; ++i) {
        String ch(*(begin() + i));
        result += ch;
    }
    return result;
}
/* comparison operators*/
bool operator <(const String &lhs, const String &rhs) {
    return std::lexicographical_compare(lhs.begin(), lhs.end(), rhs.begin(), rhs.end());
}
bool operator <=(const String &lhs, const String &rhs) {
    return !(rhs < lhs);
}
bool operator ==(const String &lhs, const String &rhs) {
    return lhs.size() == rhs.size() && std::equal(lhs.begin(), lhs.end(), rhs.begin());

}
bool operator !=(const String &lhs, const String &rhs) {
    return !(rhs == lhs);
}
bool operator >=(const String &lhs, const String &rhs) {
    return !(lhs < rhs);
}
bool operator >(const String &lhs, const String &rhs) {
    return rhs < lhs;
}

}
#endif // STRING_H_INCLUDED
