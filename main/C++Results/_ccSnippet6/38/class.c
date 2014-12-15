#include"log.h"
#include<stdarg.h>
#include<stdio.h>
#include<stdlib.h>
#include<string.h>

struct class {
	size_t size;
	void* (*ctor)(void *_this, va_list *args);
	void  (*dtor)(void *_this);
};

struct stringClass {
	struct class _class;
	/* mutator */
	void* (*reverse)(void *_this);
	/* accessor */
	const char* (*c_str)(void *_this);
};

struct string {
	struct stringClass *_stringClass;
	char *text;
};

struct string_int{
	struct stringClass *_stringClass;
	int *text;
	size_t text_length;
	char *_text;
};

struct stringClass _string;
struct stringClass _string_int;

void* string_int_ctor(void *_this, va_list *args)
{
	struct string_int *this = _this;
	const char *init_text = va_arg(args, char*);
	size_t text_len = strlen(init_text);
	size_t i = 0;
	this->text_length = text_len;
	this->_stringClass = &_string_int;
	this->text = malloc((text_len+1)*sizeof(int));
	for (i = 0; i<text_len; i++) {
		this->text[i] = init_text[i];
	}
	return(this);
}

void string_int_dtor(void *_this)
{
	struct string_int *this = _this;
	if (this) {
		if (this->text)
			free(this->text);
		if (this->_text)
			free(this->_text);
	}
}

void* string_int_reverse(void *this)
{
	return(this);
}

const char* string_int_c_str(void *_this)
{
	struct string_int *this = _this;
	if (this->_text)
		free(this->_text);
	if (this->text_length) {
		size_t i = 0;
		this->_text = malloc(this->text_length * sizeof(char));
		for (i = 0; i<this->text_length; i++) {
			this->_text[i] = this->text[i];
		}
		return(this->_text);
	}
	return(NULL);
}

struct stringClass _string_int = {
	_class: {
		size: sizeof(struct string_int),
		ctor: string_int_ctor,
		dtor: string_int_dtor,
	},
	reverse: string_int_reverse,
	c_str: string_int_c_str,
};

void* string_ctor(void *_this, va_list *args)
{
	struct string *this = _this;
	const char *init_text = va_arg(args, char*);
	size_t text_len = strlen(init_text);
	this->_stringClass = &_string; 
	this->text = malloc((text_len+1)*sizeof(char));
	strcpy(this->text, init_text);
	LOGI("%s", this->text);
	return(this);
}

void string_dtor(void *_this)
{
	struct string *this = _this;
	if (this)
		if (this->text)
			free(this->text);
}

void* string_reverse(void *this)
{
	return(this);
}

const char* string_c_str(void *_this)
{
	struct string *this = _this;
	if (this)
		return(this->text);
	return(NULL);
}

struct stringClass _string = {
	_class: {
		size: sizeof(struct string),
		ctor: string_ctor,
		dtor: string_dtor,
	},
	reverse: string_reverse,
	c_str: string_c_str,
};

void* new(void *_class, ...)
{
	struct class *_this = _class; 
	void *this = malloc(_this->size);
	LOGI("class->size = %lu", _this->size);
	if (_this->ctor) {
		va_list va;
		va_start(va, _class);
		_this->ctor(this, &va);
		va_end(va);
	}
	return(this);
}

void delete(void *_class, void *_obj)
{
	struct class *this = _class;
	if (this) {
		if (this->dtor)
			this->dtor(_obj);
		free(_obj);
	}
}

int main(void)
{
	struct string *s1 = new(&_string, "SHIT HAPPENS");
	struct string_int *s2 = new(&_string_int, "FOREST GUMP");
	LOGI("%s", s1->_stringClass->c_str(s1));
	LOGI("%s", s2->_stringClass->c_str(s2));
	delete(&_string, s1);
	delete(&_string_int, s2);
	return(0);
}
