// inspired from http://stackoverflow.com/questions/2901694
int getNumberOfCores();

// inspired from http://stackoverflow.com/questions/2513505/
size_t getTotalSystemMemory();

#ifdef _WIN32
#include <windows.h>
int getNumberOfCores()
{
	SYSTEM_INFO sysinfo;
	GetSystemInfo(&sysinfo);
	return sysinfo.dwNumberOfProcessors;
}
size_t getTotalSystemMemory()
{
	MEMORYSTATUSEX status;
	status.dwLength = sizeof(status);
	GlobalMemoryStatusEx(&status);
	return status.ullTotalPhys;
}
#elif MACOS
#include <sys/param.h>
#include <sys/sysctl.h>
int getNumberOfCores()
{
	int mib[2] = {CTL_HW, HW_AVAILCPU};
	uint32_t numCPU;
	size_t len = sizeof(numCPU);

	sysctl(mib, 2, &numCPU, &len, NULL, 0);

	if (count < 1)
	{
		mib[1] = HW_NCPU;
		sysctl(mib, 2, &numCPU, &size, NULL, 0);
		if(count < 1) { count = 1; }
	}
	return numCPU;
}
size_t getTotalSystemMemory()
{
	int mib[2] = {CTL_HW, HW_MEMSIZE};
	u_int namelen = sizeof(mib) / sizeof(mib[0]);
	uint64_t size;
	size_t len = sizeof(size);
	sysctl(mib, namelen, &size, &len, NULL, 0);
	return size;
}
#else
#include <unistd.h>
int getNumberOfCores()
{
	return sysconf(_SC_NPROCESSORS_ONLN);
}
size_t getTotalSystemMemory()
{
	return sysconf(_SC_PHYS_PAGES) * sysconf(_SC_PAGE_SIZE);
}
#endif

#include <stdio.h>
int main()
{
	printf("%i\n", getNumberOfCores());
	printf("%uGB\n", getTotalSystemMemory() / 1024 / 1024);
	return 0;
}
