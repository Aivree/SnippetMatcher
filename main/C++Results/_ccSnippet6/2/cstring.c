#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <os/oscommon.h>
#include <stl/cstring.h>

/** @cond INTERNAL_ONLY */
struct cstring_
{
  char *c_str;
  unsigned int length;
  unsigned int capacity;
};
/** @endcond */

static char *__string_atleast(cstring_t sobj, unsigned int sz)
{
  unsigned int new_size;
  char *new_ptr;

  if( !sobj )
    return NULL;

  if( sz <= sobj->capacity )
    return sobj->c_str;

  new_size = (unsigned int) MAX((float)sz, (float)sobj->capacity * 1.5);
  new_ptr = realloc(sobj->c_str, new_size);

  if( !new_ptr )
    return NULL;

  sobj->c_str = new_ptr;
  sobj->capacity = new_size;
  return new_ptr;
}

bool cstring_init(cstring_t sobj)
{
  if( !sobj )
    return false;

  sobj->c_str = NULL;
  sobj->length = 0;
  sobj->c_str = __string_atleast(sobj, 32);

  return sobj->c_str != NULL;
}

bool cstring_destroy(cstring_t sobj)
{
  if( !sobj )
    return false;

  if( sobj->c_str )
    free(sobj->c_str);

  sobj->c_str = NULL;
  sobj->length = 0;
  sobj->capacity = 0;

  return true;
}

bool cstring_append(cstring_t sobj, const char *a)
{
  if( !sobj || !a )
    return false;

  return true;
}

/*
bool cstring_prepend(cstring_t sobj, const char *p);
bool cstring_insert(cstring_t sobj, unsigned int index, const char *str);
*/

const char *string_get(cstring_t sobj)
{
  if( !sobj )
    return false;

  return sobj->c_str;
}

const char *string_set(cstring_t sobj, const char *str)
{
  if( !sobj || !str )
    return NULL;

  if( !__string_atleast(sobj, strlen(str)) )
    return false;

  strcpy(sobj->c_str, str);

  return sobj->c_str;
}

bool cstring_erase(cstring_t sobj)
{
  if( !sobj )
    return false;

  if( sobj->c_str )
    memset(sobj->c_str, 0, sobj->length);

  sobj->length = 0;
  return true;
}

unsigned int cstring_length(cstring_t sobj)
{
  if( !sobj )
    return 0;

  return sobj->length;
}

unsigned int cstring_capacity(cstring_t sobj)
{
  if( !sobj )
    return 0;

  return sobj->capacity;
}

bool cstring_request_capacity(cstring_t sobj, unsigned int cap)
{
  return __string_atleast(sobj, cap) != NULL;
}
