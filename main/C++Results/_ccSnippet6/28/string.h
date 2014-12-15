#ifndef STRING_H
#define STRING_H

#define _CRT_SECURE_NO_WARNINGS

#include <iostream>

class ErrorsArea{
public:
	int idx;

	ErrorsArea(int idx);
};

inline ErrorsArea::ErrorsArea(int idx) : idx(idx){};

class String{
private:
	class Rep{
	private:
		friend class String;

		char* string;
		int	value;
		int length;
	public:
		Rep(const char* string);
		~Rep();
		Rep* CopyRep();

		friend std::ostream& operator << (std::ostream& stream, const String& string);
	};

	class Substr{
	private:
		String& string;
		int idx;
		int lenght;
	public:
		Substr(String& string, int idx, int lenght);
		Substr operator = (const String& string);
		void operator & ();
		operator String ();
	};

	class Char{
	private:
		String& string;
		int idx;
		void operator &();
	public:
		Char(String& string, int idx);
		Char operator = (char symb);
		operator char();
	};

	friend class Substr;
	friend class Char;
	Rep* rep;

	void BindRep(Rep* rep);
	void UnbindRep();
public:
	String(const char*);
	String(const String&);
	String();
	~String();

	int lenght() const;
	char* c_str() const;

	Char operator[] (int idx);
	Substr SubStr(int idx, int lenght);

	void remove(int idx, int lenght);
	void insert(int idx, const String& string);
	void insert(int idx, char symb);

	String operator= (const String& string);
	String operator+ (const String& string) const;
	String operator* (int count) const;
	void operator+= (char symb);
	void operator+= (const String& string);

	bool operator== (const String& string) const;
	bool operator!= (const String& string) const;
	bool operator> (const String& string) const;
	bool operator>= (const String& string) const;
	bool operator< (const String& string) const;
	bool operator<= (const String& string) const;

	friend std::istream& operator >> (std::istream& stream, String& string);
};

inline bool String::operator== (const String& string) const{
	return (strcmp(rep->string, string.rep->string)) == 0;
}

inline bool String::operator!= (const String& string) const{
	return (strcmp(rep->string, string.rep->string)) != 0;
}

inline bool String::operator> (const String& string) const{
	return (strcmp(rep->string, string.rep->string)) > 0;
}

inline bool String::operator>= (const String& string) const{
	return (strcmp(rep->string, string.rep->string)) >= 0;
}

inline bool String::operator< (const String& string) const{
	return (strcmp(rep->string, string.rep->string)) < 0;
}

inline bool String::operator<= (const String& string) const{
	return (strcmp(rep->string, string.rep->string)) <= 0;
}

inline void String::operator+=(char symb){

	char* tmp = new char[this->lenght() + 2];
	strncpy(tmp, this->c_str(), this->lenght());
	tmp[this->lenght()] = symb;
	tmp[this->lenght() + 1] = '\0';

	UnbindRep();
	BindRep(new Rep(tmp));
	delete[] tmp;
}

inline void String::operator+=(const String& string){

	char* tmp = new char[this->lenght() + string.lenght() + 1];
	strncpy(tmp, this->c_str(), this->lenght());
	strncpy(tmp + this->lenght(), string.c_str(), string.lenght());
	tmp[this->lenght() + string.lenght()] = '\0';

	UnbindRep();
	BindRep(new Rep(tmp));
	delete[] tmp;
}

inline String String::operator*(int count) const{
	char* tmp = new char[this->lenght() * count + 1];
	tmp[0] = '\0';
	for (int i = 0; i < count; i++){
		strncat(tmp, this->c_str(), this->lenght());
	}
	String A(tmp);
	delete[] tmp;

	return A;
}

inline String String::operator+(const String& string) const{
	char* tmp = new char[this->lenght() + string.lenght() + 1];
	tmp[0] = '\0';
	strncat(tmp, this->c_str(), this->lenght());
	strncat(tmp, string.c_str(), string.lenght());
	String A(tmp);
	delete[] tmp;

	return A;
}

inline void String::remove(int idx, int lenght){
	if (idx < 0 || idx >= this->lenght() || lenght + idx > this->lenght()){
		throw ErrorsArea(idx);
	}
	else {
		char* tmp = new char[this->lenght() - lenght + 1];
		strncpy(tmp, this->c_str(), idx);
		strncpy(tmp + idx, this->c_str() + idx + lenght, this->lenght() - lenght);
		tmp[this->lenght() - lenght] = '\0';
		UnbindRep();
		BindRep(new Rep(tmp));
		delete[] tmp;
	}
}

inline void String::insert(int idx, char symb){
	if (idx < 0 || idx >= this->lenght()){
		throw ErrorsArea(idx);
	}
	else {
		char* tmp = new char[this->lenght() + 2];
		strncpy(tmp, this->c_str(), idx);
		tmp[idx] = symb;
		strncpy(tmp + idx + 1, this->c_str() + idx, this->lenght() - idx);
		tmp[this->lenght() + 1] = '\0';
		UnbindRep();
		BindRep(new Rep(tmp));
		delete[] tmp;
	}
}

inline void String::insert(int idx, const String& string){
	if (idx < 0 || idx >= this->lenght()){
		throw ErrorsArea(idx);
	}
	else {
		char* tmp = new char[this->lenght() + string.lenght() + 1];
		strncpy(tmp, this->c_str(), idx);
		strncpy(tmp + idx, string.c_str(), string.lenght());
		strncpy(tmp + idx + string.lenght(), this->c_str() + idx, this->lenght() - idx);
		tmp[this->lenght() + string.lenght()] = '\0';
		UnbindRep();
		BindRep(new Rep(tmp));
		delete[] tmp;
	}
}

inline String::Rep::~Rep(){
	delete[] string;
}

inline void String::BindRep(Rep* rep){
	this->rep = rep;
	this->rep->value++;
}

inline String String::operator= (const String& string){
	UnbindRep();
	BindRep(string.rep);
	return *this;
}

inline String::Substr::operator String(){
	char* tmp = new char[this->lenght + 1];
	strncpy(tmp, this->string.c_str() + idx, this->lenght);
	tmp[this->lenght] = '\0';
	String A(tmp);
	delete[] tmp;

	return A;
}

inline String::Substr String::SubStr(int idx, int lenght){
	return Substr(*this, idx, lenght);
}

inline void String::UnbindRep(){
	if (rep->value == 1){
		delete rep;
	}
	else {
		rep->value--;
	}
}

inline int String::lenght() const{
	return rep->length;
}

inline char* String::c_str() const{
	char* tmp = new char[rep->length + 1];
	strcpy(tmp, rep->string);
	return tmp;
}

inline String::Char String::operator[](int idx){
	if (idx < 0 || idx >= this->lenght()){
		throw ErrorsArea(idx);
	}
	else {
		return Char(*this, idx);
	}
}

inline String::Char::operator char(){
	return string.rep->string[idx];
}

inline String::Char::Char(String& string, int idx) : string(string), idx(idx){}

inline String::Substr::Substr(String& string, int idx, int lenght) : string(string), idx(idx), lenght(lenght){}

inline String::String(const char* date){
	rep = new Rep(date);
}

inline String::String(const String& str){
	BindRep(str.rep);
}

inline String::String(){
	rep = new Rep("");
}

inline String::~String(){
	UnbindRep();
}

inline void String::Char::operator& (){}

inline void String::Substr::operator& (){}
#endif