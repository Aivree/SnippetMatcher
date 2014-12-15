//------------------------------------------------------------------------------
#include <stdio.h>
#include <stdlib.h>

int main( )
{
    FILE *fp, *shfp = NULL;
    int filecount = 0;
    char buf[4096], shname[1024];

    fp = fopen( "gen_lfs_sys_sh.data", "rb" );

    if ( fp == NULL ) {
        fprintf( stderr, "can't open data\n" );
        exit( 1 );
    }

    while( fgets( buf, sizeof( buf ), fp ) != NULL )
    {
        if ( memcmp( buf, "----------------------------------------", 10 ) == 0 )
        {
            filecount ++;
            sprintf( shname, "%d.sh", filecount );
           
            if ( shfp != NULL ) {
                fclose( shfp );
            }
            shfp = fopen( shname, "w+" );
            if ( shfp == NULL ) {
                fprintf( stderr, "can't open %s\n", shname );
                exit( 1 );
            }
            fprintf( shfp, "#!/bin/bash\n" );
            fprintf( shfp, "echo \"%d\" > lfsstep\n", filecount );
            continue;
        }

        if ( shfp != NULL )
        {
            /*if ( buf[0] == '#' ) {
            }*/
            
            fprintf( shfp, "%s", buf );
        }

    }

    fclose( fp );

    fp = fopen( "lfsend", "w+" );

    if ( fp == NULL )
    {
        fprintf( stderr, "can't open /tmp/lfsend\n" );
        exit( 1 );
    }

    fprintf( fp, "%d", filecount );

    fclose( fp );
    
    return 0;
}
