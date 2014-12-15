
int main(void)
{
char str[256];
int procCount = -1; // to offset for the first entry
FILE *fp;

if( (fp = fopen("/proc/stat", "r")) )
{
  while(fgets(str, sizeof str, fp))
  if( !memcmp(str, "cpu", 3) ) procCount++;
}

if ( procCount == -1)
{
printf("Unable to get proc count. Defaulting to 2");
procCount=2;
}

printf("Proc Count:%d\n", procCount);
return 0;
}