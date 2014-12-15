#include <stdio.h>
#include <string.h>
#include <time.h>
#include <netdb.h>
#include <unistd.h>
#include <malloc.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <sys/ipc.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "oracle_oci_class.h"
#include "telnet_class.h"

int exec_command(char *,char *, char **);
int strcmp1(char *,char * );
int init_telnet( telnet_conn *);

int main()
{
    int rlen,ret,timeflag,statnew=0,ret_new=0,i=0,j;
    int r_bz1,r_bz2,tt1=0;
    char dirname[30],tmpfile[20];
    char outbuf2[600000],*outstr2[6000];
    FILE *fp_new;



    oci_db *db1 = new oci_db(10); 

    if( db1->db_connect( "*1","*2","*3","") < 0  )      // *1 databaselink  *2 username  *3 passwd
    {
        printf( "Open Database Error !\n" );
        delete( db1 );
        return -1;
    }

    telnet_conn *tel2 = new telnet_conn();
    if( init_telnet( tel2 ) < 0 ) return -1;


    timeflag = 1;
    tt1=0;
    while( 1 )
    {
        tt1++;
        if( tt1 > 20 )
        {
            tel2->send_cmd( "exit",10 );
            tel2->send_cmd( "exit",10 );
            delete( tel2 );
            telnet_conn *tel2 = new telnet_conn();
            if( init_telnet( tel2 ) < 0 ) return -1;
            timeflag = 1;
            tt1 = 0;
        }

//      connect to old ecp

        if( timeflag == 1 )
        {
            //      connect to new ecp

            if( ( fp_new = fopen( "ecpnew_out.txt", "w" )) == NULL ) 
            {
                perror( "Open new tmp file error :");
                return(-1);
            }
            tel2->send_cmd( "OP:ALARM",50 );
            timeflag = 0;
        }

        while( statnew == 0 && tel2->recv_data() > 0 )  
        {
            rlen = strlen(( char *)tel2->databuf );
            fwrite( tel2->databuf, rlen, 1, fp_new );
        }

        if( statnew == 0 && strcmp1( tel2->linebuf, "dt2omp TICLI" ) == 0 )
        {
            statnew = 1;
            fclose( fp_new );
            ret_new = exec_command( "awk -f cdma_alarm.awk ecpnew_out.txt ",outbuf2,&outstr2[0] );
        }

        //      insert into database
        if( ret_new > 0  ) 
        {
            db1->db_execsql( "delete cdma_alarm" );

            for( j=1; j< ret_new; j++ )
            {
                db1->db_execsql( outstr2[j] );
            }
            db1->db_execsql( "commit" );
            ret_new = 0;            
            sleep(180);
            timeflag = 1;
            statnew = 0;
        }
        sleep(1);
    }


    tel2->send_cmd( "exit",10 );
    tel2->recv_data();
    tel2->send_cmd( "exit",10 );
    tel2->recv_data();
        

    delete( db1 );
    delete( tel2 );
return 1;
}


int exec_command( char *run_cmd,char *outbuf,char **ret )
{
    FILE *fd;
    char l_buf[200], *rev, **r1, *rc1;
    int  i=0, j, len1 ;

    rc1  = outbuf;
    r1   = ret;


    if((fd = popen(run_cmd,"r")) == NULL) { return -1;  }
    while(!feof(fd))
    {
        rev = l_buf;
        if(fgets(rev,199,fd) == NULL) {  continue; }
        *r1++  = rc1;
        while( *rev > 20 ) { *rc1++ = *rev++; }
        *rc1++ = 0;
        i++;
    }
    pclose(fd);
    if( i>0 && strcmp( ret[0], "OP:ALARM" ) == 0 )
    {
        if( strcmp( ret[i -1], "OP:ALARM OK") == 0 )
            i--;
        else
            return -1;
    }

    return( i );
}

int strcmp1( char *s1, char *s2 )
{
    if( *s1 == 0 ) return -1;
    if( *s2 == 0 ) return 1;
    while( *s1 && *s2 )
    {
        if( *s1 > *s2 )  return 1;
        if( *s1 < *s2 )  return -1;
        s1++;  s2++;
    }
    return 0;
}

int init_telnet( telnet_conn *tel2)
{
    int rlen;
          
    strcpy( tel2->strbz1, ">" );
    strcpy( tel2->strbz2, "dt2omp" );
    tel2->print_flag = 0;
    if( tel2->new_connect( "*1",23,"*2","*3" ) < 0 )   // *1  Msce ip    *2  username  *3 passwd
    {
        printf( "\n\nConnect to remote ip error\n");
        delete( tel2 );
        return -1; 
    }
    
    sleep(1);
    
    tel2->send_cmd( "TICLI",20 );
    tel2->recv_data();

    tel2->linebuf[0] = 0;
    return 1;
}

