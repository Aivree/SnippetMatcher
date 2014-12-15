#pragma once
#include "include.h"
#include <string>
#include <vector>
#include <algorithm>

//#using "system.dll"
//#using <mscorlib.dll>

namespace nsl
{

    namespace win_lib
    {

        namespace convert
        {

            using std::string;
            using std::vector;
			using namespace std;
#ifdef __cplusplus_cli

            std::string managed2str( System::String ^s ); // конверсия управляемой строки в обычную
            std::wstring managed2wstr( System::String ^s ); // конверсия управляемой строки в обычную unicode

            inline System::String ^str2managed( const std::string &s ) // конверсия обычной строки в управляемую
            {
                return gcnew System::String( s.c_str( ) ); // создание новой строки
            }

            inline System::String ^str2managed( const std::wstring &s ) // конверсия обычной строки в управляемую unicode
            {
                return gcnew System::String( s.c_str( ) ); // создание новой строки
            }

            array<System::String ^> ^stringvector2managed( const std::vector<std::string> &v ); // конверсия массива строк в управляемый
            template <class T> std::vector<T> array2vector( array<T> ^arr ); // конверсия упр. массива в вектор
            template <class T> array<T> ^vector2array( const std::vector<T> &v ); // конверсия вектора в управляемый
            template <class T> array<T> ^make_array( void *data, DWORD size ); // создание массива
            inline std::string floatstrprep( const std::string &str ); // приготовка строки с числом плавающей точки
            template <class T> bool atof( const std::string &str, T *out ); // конверсия строки в число

            //========================================

            template <class T> bool strtonum( const std::string &str, T *out )
            {
                T tval;
                try
                {
                    tval = boost::lexical_cast<T > ( boost::trim_copy( str ) );
                }
                catch ( boost::bad_lexical_cast & )
                {
                    return false;
                }
                if ( out ) *out = tval;
                return true;
            }

            inline std::string floatstrprep( const std::string &str )
            {
                string tstr = str;
                std::replace( tstr.begin( ), tstr.end( ), ',', '.' );
                return tstr;
            }

            template <class T> bool atof( const string &str, T *out = NULL )
            {
                T val;
                return strtonum( floatstrprep( str ), &val ) ? ( out ? *out = val, true : true ) : false;
            }

            template <class T> std::vector<T> array2vector( array<T> ^arr )
            {
                typedef std::vector<T> res_type;
                res_type vec;
                if (arr->Length==0) return vec;
                pin_ptr<T> ptr = &arr[0];
                copy( &ptr[0], &ptr[arr->Length], std::back_inserter<res_type > ( vec ) );
                return vec;
            }

            template <class T, class Cont> void array2cont( array<T> ^arr, Cont &cont )
            {
                cont.clear( );
                if (arr->Length==0) return;
                pin_ptr<T> ptr = &arr[0];
                typedef typename Cont res_type; //std::deque<T> res_type;
                //res_type vec;
                T *beg = &ptr[0];
                T *end = &ptr[arr->Length];
                copy( beg, end, std::back_inserter<res_type > ( cont ) );
                //return vec;
            }

            template <class T> array<T> ^vector2array( const std::vector<T> &v )
            {
                array<T> ^arr = gcnew array<T > ( v.size( ) );
                if (v.size()==0) return arr;
                pin_ptr<T> ptr = &arr[0];
                copy( v.begin( ), v.end( ), &ptr[0] );
                return arr;
            }

            template <class T, class Iter> array<T> ^vector2array( Iter beg, Iter end )
            {
                DWORD size = std::distance( beg, end );
                array<T> ^arr = gcnew array<T > ( size );
                if (arr->Length==0) return arr;
                pin_ptr<T> ptr = &arr[0];
                T *res = &ptr[0];
                copy( beg, end, res );
                return arr;
            }

            template <class T>
            array<T> ^make_array( void *data, DWORD size )
            {
                array<T> ^arr = gcnew array<T > ( size );
                if (arr->Length==0) return arr;
                pin_ptr<T> ptr = &arr[0];
                T *dptr = static_cast < T * > ( data );
                copy( &dptr[0], &dptr[size], &ptr[0] );
                return arr;
            }

#endif

        }

    }

}
