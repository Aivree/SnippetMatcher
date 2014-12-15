#if defined(_WIN32) || defined(_WIN64)

#include <glib.h>
#include <windows.h>

// http://stackoverflow.com/questions/150355/programmatically-find-the-number-of-cores-on-a-machine
guint g_get_num_processors (void)
{
  #if defined(_WIN32) || defined(_WIN64)
    SYSTEM_INFO sysinfo;
    GetSystemInfo( &sysinfo );
    return sysinfo.dwNumberOfProcessors;
  #elif defined(BSD)
    int mib[4];
    size_t len = sizeof (numCPU);

    /* set the mib for hw.ncpu */
    mib[0] = CTL_HW;
    mib[1] = HW_AVAILCPU;  // alternatively, try HW_NCPU;

    /* get the number of CPUs from the system */
    sysctl (mib, 2, &numCPU, &len, NULL, 0);

    if (numCPU < 1)
      {
        mib[1] = HW_NCPU;
        sysctl (mib, 2, &numCPU, &len, NULL, 0);

        if( numCPU < 1 )
          {
            numCPU = 1;
          }
      }
  #else
    return sysconf (_SC_NPROCESSORS_ONLN);
  #endif
}

#endif
