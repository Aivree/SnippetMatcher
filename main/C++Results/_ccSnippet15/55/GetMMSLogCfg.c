#include "vdmp/syspub.h"

#define WHITES  " \t\n\r"  /*¿Õ°××Ö·û*/

char *ltrim(char *str, int len);
char *rtrim(char *str, int len);

int GetMMSLogCfg()
{
    char filename[201];
    char line[101];
    char logsize[101];
    char tmpStr[101];
    char *name;
    char *value;
    FILE *fp;

    sprintf(filename, "%s/etc/MMSLOG.cfg", (char *)getenv("MMSPATH"));
    fp = fopen(filename, "r");
    if (fp == NULL)
    {
       MMSLOG("E", "OPEN CONFIG FILE [%s] ERROR!", filename);
       return -1;
    }
    if (fgets(line, 100, fp) == NULL)
    {
       MMSLOG("E", "READ CONFIG FILE [%s] ERROR!", filename);
       return -5;
    }

    while(!feof(fp))
    {
       if (*line == '#' || *line == '[' || strlen(line) == 0)
       {
          fgets(line, 100, fp);
          continue;
       }

       DeleteNote(line);

       if (memcmp(line, "LOGPATH", 7) == 0)
       {
          memset(tmpStr, 0x00, sizeof(tmpStr));
          Get_Config_Item(line, tmpStr);
          sprintf(mmsLog.logPath, "%s/%s", (char *)getenv("HOME"), tmpStr);
       }
       else if (memcmp(line, "LOGNAME", 7) == 0)
       {
          Get_Config_Item(line, mmsLog.logName);
       }
       else if (memcmp(line, "LOGLEVEL", 8) == 0)
       {
          Get_Config_Item(line, mmsLog.logLevel);
       }
       else if (memcmp(line, "LOGSIZE", 7) == 0)
       {
          Get_Config_Item(line, logsize);
          mmsLog.logSize = atol(logsize);
       }

       fgets(line, 100, fp);
    }

    fclose(fp);

    return 0;
}

int Get_Config_Item(char *line, char *dest)
{
   int i;
   int len;
   char tmpstr[1001];

   len = strlen(line);
   for(i = 0; i < len; i ++)
     if (*(line + i) == '=') break;
   strcpy(tmpstr, line + i + 1);
   rtrim(tmpstr, strlen(tmpstr));
   strcpy(dest, tmpstr);

   return 0;
}

int Is_White(char ch, char *whites)
{
   int len = 0;

   while(*(whites + len))
   {
      if (ch == *(whites + len)) return 1;
      len++;
   }

   return 0;
}

char *ltrim(char *str, int len)
{
   int i = 0;
   char temp[1001];
	
   while(Is_White(*(str + i), WHITES) && i < len) i++;

   if (i == len)
   {
      *str = 0;
      return NULL;
   }
   else
   {
      memcpy(temp, str + i, len -i);
      strcpy(str, temp);
      return str;
   }
}

char *rtrim(char *str, int len)
{
   int i = len - 1;
	
   while(Is_White(*(str + i), WHITES) && i >= 0) i--;

   if (i == -1)
   {
      *str = 0;
      return NULL;
   }
   else
   {
      *(str + i + 1) = 0;
      return str;
   }
}

