/*******************************************************
 * 公    司: 大连同方软银科技有限公司
 * 程 序 名: GetXipSysCfg.c
 * 程序功能: 获取渠道平台系统配置信息
 * 输入参数:
 *           渠道平台系统配置文件
 * 输出参数:
 *           渠道平台系统配置全局变量
 * 返 回 值:
 *           =0   成功
 *           <0   失败
 * 作    者: yangwc
 * 开发日期: 2007/1
 * 修 改 人:
 * 修改日期:
*******************************************************/
#include "kernel/syspub.h"

#include "kernel/kmonpub.h"

#define WHITES  " \t\n\r"  /*空白字符*/

char *ltrim(char *str, int len);
char *rtrim(char *str, int len);

int GetXipSysCfg()
{
    char filename[201];
    char line[101];
    char logsize[101];
    char logpath[101];
    char monport[11];
    char *name;
    char *value;
    FILE *fp;

    sprintf(filename, "%s/etc/XIPSYS.cfg", (char *)getenv("HOME"));
    fp = fopen(filename, "r");
    if (fp == NULL)
    {
       XIPLOG("E", "OPEN CONFIG FILE [%s] ERROR!", filename);
       return -1;
    }
    if (fgets(line, 100, fp) == NULL)
    {
       XIPLOG("E", "READ CONFIG FILE [%s] ERROR!", filename);
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

       if (memcmp(line, "XIPLOGHOMEPATH", strlen("XIPLOGHOMEPATH")) == 0)
       {
          Get_Config_Item(line, xipLogHomePath);
       }
       else if (memcmp(line, "APPLOGHOMEPATH", strlen("APPLOGHOMEPATH")) == 0)
       {
          Get_Config_Item(line, appLogHomePath);
       }
       else if (memcmp(line, "APPUPFILEHOMEPATH", strlen("APPUPFILEHOMEPATH")) == 0)
       {
          Get_Config_Item(line, appUpFileHomePath);
       }
       else if (memcmp(line, "APPDWFILEHOMEPATH", strlen("APPDWFILEHOMEPATH")) == 0)
       {
          Get_Config_Item(line, appDwFileHomePath);
       }
       else if (memcmp(line, "APPLOGMONUSE", strlen("APPLOGMONUSE")) == 0)
       {
          Get_Config_Item(line, appLogMonUse);
       }
       else if (memcmp(line, "APPLOGMONIP", strlen("APPLOGMONIP")) == 0)
       {
          Get_Config_Item(line, appLogMonIp);
       }
       else if (memcmp(line, "APPLOGMONPORT", strlen("APPLOGMONPORT")) == 0)
       {
          Get_Config_Item(line, monport);
          appLogMonPort = atol(monport);
       }
       else if (memcmp(line, "LOGPATH", strlen("LOGPATH")) == 0)
       {
          memset(logpath, 0x00, sizeof(logpath));
          Get_Config_Item(line, logpath);
          sprintf(xipLog.logPath, "%s/%s", xipLogHomePath, logpath);
       }
       else if (memcmp(line, "LOGNAME", strlen("LOGNAME")) == 0)
       {
          Get_Config_Item(line, xipLog.logName);
       }
       else if (memcmp(line, "LOGLEVEL", strlen("LOGLEVEL")) == 0)
       {
          Get_Config_Item(line, xipLog.logLevel);
       }
       else if (memcmp(line, "LOGSIZE", strlen("LOGSIZE")) == 0)
       {
          Get_Config_Item(line, logsize);
          xipLog.logSize = atol(logsize);
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

