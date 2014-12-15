#include "vdmp/syspub.h"

int GetSysInfo()
{
    char filename[201];
    char line[101];
    char tmpStr[101];
    FILE *fp;
    int  i = 0;
    int  ret = 0;

    memset(line, 0x00, sizeof(line));

    sprintf(filename, "%s/etc/MMSSYS.cfg", (char *)getenv("MMSPATH"));
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
          memset(line, 0x00, sizeof(line));
          fgets(line, 100, fp);
          continue;
       }

       DeleteNote(line);

       if (memcmp(line, "CUSTNAME", 8) == 0)
       {
          memset(tmpStr, 0x00, sizeof(tmpStr));
          Get_Config_Item(line, tmpStr);
          strcpy(g_custname, tmpStr);
       }
       else if (memcmp(line, "XIPVER", 6) == 0)
       {
          memset(tmpStr, 0x00, sizeof(tmpStr));
          Get_Config_Item(line, tmpStr);
          strcpy(g_xipver, tmpStr);
          ret = SplitVersion(g_xipver);
          if (ret)
          {
             MMSLOG("E", "Version Formater [%s] ERROR!", g_xipver);
             return ret;
          }
       }
       else if (memcmp(line, "FUPPATH", 7) == 0)
       {
          memset(tmpStr, 0x00, sizeof(tmpStr));
          Get_Config_Item(line, tmpStr);
          sprintf(g_fuppath, "%s/%s", (char *)getenv("HOME"), tmpStr);
       }
       else if (memcmp(line, "FDWPATH", 7) == 0)
       {
          memset(tmpStr, 0x00, sizeof(tmpStr));
          Get_Config_Item(line, tmpStr);
          sprintf(g_fdwpath, "%s/%s", (char *)getenv("HOME"), tmpStr);
       }
       else if (memcmp(line, "FUPGPATH", 8) == 0)
       {
          memset(tmpStr, 0x00, sizeof(tmpStr));
          Get_Config_Item(line, tmpStr);
          sprintf(g_fupgpath, "%s/%s", (char *)getenv("HOME"), tmpStr);
       }
       else if (memcmp(line, "TEMPLPATH", 9) == 0)
       {
          memset(tmpStr, 0x00, sizeof(tmpStr));
          Get_Config_Item(line, tmpStr);
          sprintf(g_templpath, "%s/%s", (char *)getenv("HOME"), tmpStr);
       }

       fgets(line, 100, fp);
    }

    fclose(fp);

    return 0;
}

int SplitVersion(char *allVer)
{
    char *p = NULL;
    char *q = NULL;
    char mVer[7];
    char sVer[7];

    p = allVer;

    q = strchr(p, '.');
    if (q != NULL)
    {
       memset(mVer, 0x00, sizeof(mVer));
       memcpy(mVer, p, q - p);
       strtrim(mVer);
       if (strlen(mVer) > 2)
          return -5;
       else
          strcpy(g_xipmver, mVer); 

       memset(sVer, 0x00, sizeof(sVer));
       strcpy(sVer, q+1);
       strtrim(sVer);
       if (strlen(sVer) > 1)
          return -10;
       else
          strcpy(g_xipsver, sVer); 
    }
    else
    {
       return -15;
    }
    
    return 0;

}

