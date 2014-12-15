#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/time.h>
#include <mysql.h>
#include <my_global.h>
 
void finish_with_error(MYSQL *con)
{
    fprintf(stderr, "%s\n", mysql_error(con));
    mysql_close(con);
    exit(1);
}

int main()
{
	/* connect to mysql */
	MYSQL *con = mysql_init(NULL);
	

	if (con == NULL)
 	{
    	fprintf(stderr, "mysql_init() failed\n");
    	exit(1);
  	}  
  
  	if (mysql_real_connect(con, "localhost", "root", "x94u4cl6", "mysql", 0, NULL, 0) == NULL) 
 	{
    	finish_with_error(con);
 	}

	int sockfd, num = 0, user[10] = {0}, opt = 1, clientfd, i, j;
	int addrlen, max = 10, sd, max_sd, activity, busy[10] = {0};
	struct sockaddr_in dest;
	char buffer[] = "---------------Welcome to chatroom!---------------";
	char name[10][10], friend[10][10], ban[10][10], msg[128], online[128];

	/* set of socket descriptors */
	fd_set readfds;
	
	/* create socket , same as client */
	sockfd = socket(PF_INET, SOCK_STREAM, 0);
	setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, (char *)&opt, sizeof(opt));

	/* initialize structure dest */
	bzero(&dest, sizeof(dest));
	dest.sin_family = PF_INET;
	dest.sin_port = htons(1625);
	dest.sin_addr.s_addr = INADDR_ANY;

	/* Assign a port number to socket */
	bind(sockfd, (struct sockaddr*)&dest, sizeof(dest));

	/* make it listen to socket with max 20 connections */
	listen(sockfd, max);
	addrlen = sizeof(dest);
	
	/* infinity loop -- accepting connection from client forever */
	while(1)
	{
		/* clear the socket set */
        FD_ZERO(&readfds);
  
        /* add master socket to set */
        FD_SET(sockfd, &readfds);
        max_sd = sockfd;

		for(i=0; i<max; i++)
        {
            /* socket descriptor */
            sd = user[i];
             
            /* if valid socket descriptor then add to read list */
            if(sd > 0)
                FD_SET(sd, &readfds);
             
            /* highest file descriptor number, need it for the select function */
            if(sd > max_sd)
                max_sd = sd;
        }
  
		/* wait for an activity on one of the sockets , timeout is NULL , so wait indefinitely */
        activity = select(max_sd+1, &readfds, NULL, NULL, NULL);
    
		/* If something happened on the master socket , then its an incoming connection */
        if(FD_ISSET(sockfd, &readfds))
        {
        	char rec[32];
        	int check = 0;
            clientfd = accept(sockfd, (struct sockaddr*)&dest, (socklen_t*)&addrlen);

			recv(clientfd, rec, sizeof(rec), 0);
			
			/* sign in '*' */
			if(rec[0] == '*')
			{
				char *id, *passwd, q[1024];
   				MYSQL_ROW row;
				MYSQL_RES *result;
 
				for(i=0; i<strlen(rec); i++)
					rec[i] = rec[i+1];

				id = strtok(rec, "+");
				passwd = strtok(NULL, "/");

				/* Connect to USER_inform */					
				if (mysql_query(con, "SELECT * FROM USER_inform"))
   				{
     				finish_with_error(con);
   				}
				result = mysql_store_result(con);	
				
				if (result == NULL)
  				{
     				finish_with_error(con);
  				}

				while ((row = mysql_fetch_row(result)))
  				{	
					if(strcmp(id, row[0]) == 0 && strcmp(passwd, row[1]) == 0)
					{
						if(strcmp("1", row[3]) == 0)
							check = 1;
						else
							check = 2;
						break;
					}
				}
				mysql_free_result(result);

				if(check == 1)
				{
					send(clientfd, "1\n", sizeof("1\n"), 0);
					/* GET USER's VM inform */
                	sprintf(q, "SELECT * FROM %sVM", id);
                	if (mysql_query(con, q))
               		{
                   		finish_with_error(con);
                	}
                	result = mysql_store_result(con);

                	char VM[128];
					strcpy(VM, "");
					//strcpy(VM, "1\n");
                	while ((row = mysql_fetch_row(result)))
                	{
                    	strcat(VM, row[1]);
						strcat(VM, "\n");
                    	printf("%s\n", row[1]);
                	}
                	mysql_free_result(result);
					send(clientfd, VM, sizeof(VM), 0);
				}
				else if(check == 2)
				{
					send(clientfd, "2", sizeof("2"), 0);
					send(clientfd, " ", sizeof(" "), 0);
				}
				else 
				{
					send(clientfd, "0", sizeof("0"), 0);
					send(clientfd, " ", sizeof(" "), 0);
				}
			}	
			/* create VM '!' */
			if(rec[0] == '!')
            {
				char *id, *VMname, *MEM, *RAM, *OS, q[1024];
				extern FILE *popen();
				MYSQL_ROW row;
				MYSQL_RES *result;

            	for(i=0; i<strlen(rec); i++)
            		rec[i] = rec[i+1];

				id = strtok(rec, "+");
				VMname = strtok(NULL, "+");
				MEM = strtok(NULL, "+");
				RAM = strtok(NULL, "+");
				OS = strtok(NULL, "/");

				sprintf(q, "SELECT * FROM %sVM", id);
				if (mysql_query(con, q))
                {
                    finish_with_error(con);
                }
                result = mysql_store_result(con);

				while ((row = mysql_fetch_row(result)))
                {
					if(strcmp(VMname, row[1]) == 0)
					{
						check = 1;
						break;
					}
                }
                mysql_free_result(result);

				if(check == 1)
					send(clientfd, "0", sizeof("0"), 0);
				else
				{
					send(clientfd, "1", sizeof("1"), 0);	
					sprintf(q, "INSERT INTO %sVM(VMname) VALUES('%s')", id, VMname);
					if (mysql_query(con, q))
				    	finish_with_error(con);

					sprintf(q, "qemu-img create -f raw /var/lib/libvirt/images/%s_%s.img %sG", id, VMname, MEM);
    				popen(q, "r");

					FILE *fp1, *fp2;
					char ch;
					if(strcmp(OS, "w7") == 0)
						sprintf(q, "Win7xml/%s_%s.xml", id, VMname);
					else if(strcmp(OS, "u") == 0)
						sprintf(q, "xml/%s_%s.xml", id, VMname);
					fp2=fopen(q, "w");
					for(i=1; i<=5; i++)
					{
						if(strcmp(OS, "w7") == 0)
							sprintf(q, "Win7xml/vm%d", i);
						else if(strcmp(OS, "u") == 0)
    						sprintf(q, "xml/vm%d", i);
    					fp1=fopen(q, "r");
    					while((ch=getc(fp1)) != EOF)
       				 		fprintf(fp2, "%c", ch);
						if(i == 1)
							fprintf(fp2, "  <name>%s_%s</name>\n", id, VMname);
						if(i == 2)
							fprintf(fp2, "  <memory>%s</memory>\n  <currentMemory>%s</currentMemory>\n", RAM, RAM);
						if(i == 3)
							fprintf(fp2, "      <source file='/var/lib/libvirt/images/%s_%s.img'/>\n", id, VMname);
						if(i == 4)
							fprintf(fp2, "      <target dev='%s_%s'/>\n", id, VMname);  
    				}
					printf("%s_%s has been created\n", id, VMname);
					fclose(fp1);
      				fclose(fp2);

					if(strcmp(OS, "w7") == 0)
						sprintf(q, "virsh define Win7xml/%s_%s.xml", id, VMname);
					else if(strcmp(OS, "u") == 0)
						sprintf(q, "virsh define xml/%s_%s.xml", id, VMname);
					popen(q, "r");
				}
            }
			/* turn on and turn off VM '&' */
			else if(rec[0] == '&')
			{
				char *id, *VMname, *turn, q[1024], port[10];
				extern FILE *popen();
				FILE *pp;
				
				for(i=0; i<strlen(rec); i++)
                    rec[i] = rec[i+1];

				id = strtok(rec, "+");
				VMname = strtok(NULL, "+");
				turn = strtok(NULL, "/");

				if(strcmp(turn, "open") == 0)
				{
					sprintf(q, "virsh start %s_%s", id, VMname);
					popen(q, "r");
					sprintf(q, "virsh vncdisplay %s_%s", id, VMname);
					pp = popen(q, "r");
					fgets(port, sizeof port, pp);
					pclose(pp);
					send(clientfd, port, sizeof(port), 0);		
				}
				else
				{
					sprintf(q, "virsh destroy %s_%s", id, VMname);
					popen(q, "r");
					send(clientfd, "1", sizeof("1"), 0);
				}
			}
			/* fast install '$' */
			else if(rec[0] == '$')
			{
				char *id, *VMname, *VM, q[1024];
				int check = 0;
				extern FILE *popen();
				MYSQL_ROW row;
				MYSQL_RES *result;

				for(i=0; i<strlen(rec); i++)
					rec[i] = rec[i+1];

				id = strtok(rec, "+");
				VMname = strtok(NULL, "+");
				VM = strtok(NULL, "/");

				sprintf(q, "SELECT * FROM %sVM", id);
				if (mysql_query(con, q)) finish_with_error(con);
				result = mysql_store_result(con);

				while ((row = mysql_fetch_row(result)))
				{
					if(strcmp(VMname, row[1]) == 0)
					{
						check = 1;
						break;
					}
				}	

				if(check == 1)
					send(clientfd, "0", sizeof("0"), 0);
				else
				{	
					send(clientfd, "1", sizeof("1"), 0);
					sprintf(q, "INSERT INTO %sVM(VMname) VALUES('%s')", id, VMname);
					if (mysql_query(con, q)) finish_with_error(con);
					sprintf(q, "virt-clone --connect=qemu:///system -o %s -n %s_%s -f /var/lib/libvirt/images/%s_%s.img", VM, id, VMname, id, VMname);
					popen(q, "r");
				}
			}
			/* clone VM '@' */
			else if(rec[0] == '@')
			{
				char *id, *VMname, cloneVM[32], q[1024];	
				int VMcount = 1;
				extern FILE *popen();
				MYSQL_ROW row;
                MYSQL_RES *result;

                for(i=0; i<strlen(rec); i++)
                    rec[i] = rec[i+1];

				id = strtok(rec, "+");
				VMname = strtok(NULL, "/");
				sprintf(cloneVM, "%s_01", VMname);

				sprintf(q, "SELECT * FROM %sVM", id);
                if (mysql_query(con, q)) finish_with_error(con);
                result = mysql_store_result(con);

                while ((row = mysql_fetch_row(result)))
                {
                    if(strcmp(cloneVM, row[1]) == 0)
                    {
                        VMcount++;
						if(VMcount < 10)
							sprintf(cloneVM, "%s_0%d", VMname, VMcount); 
						else
							sprintf(cloneVM, "%s_%d", VMname, VMcount);
                    }
                }
                mysql_free_result(result);
	
				sprintf(q, "INSERT INTO %sVM(VMname) VALUES('%s')", id, cloneVM);
                if (mysql_query(con, q)) finish_with_error(con);

				sprintf(q, "virt-clone --connect=qemu:///system -o %s_%s -n %s_%s -f /var/lib/libvirt/images/%s_%s.img", id, VMname, id, cloneVM, id, cloneVM);
				popen(q, "r");
				printf("%s_%s is cloning\n", id, cloneVM);
				sprintf(q, "%s_%s", id, cloneVM);
				send(clientfd, q, sizeof(q), 0);
			}
			/* destroy VM '-' */
			else if(rec[0] == '-')
			{
				char *id, *VMname, q[1024];
				extern FILE *popen();
				MYSQL_ROW row;
				MYSQL_RES *result;
				
				for(i=0; i<strlen(rec); i++)
					rec[i] = rec[i+1];

				id = strtok(rec, "+");
				VMname = strtok(NULL, "/");

				sprintf(q, "delete from %sVM where id='%s'", id, VMname);
				if (mysql_query(con, q)) finish_with_error(con);

				sprintf(q, "virsh undefine %s_%s", id, VMname);

				send(clientfd, "1", sizeof("1"), 0);
			}
			/* checking clone stat '#' */
            else if(rec[0] == '#')
			{
				char *VMname, q[1024];
				extern FILE *popen();
                FILE *pp;

				for(i=0; i<strlen(rec); i++)
                    rec[i] = rec[i+1];

				VMname = strtok(rec, "/");
				
				pp = popen("virsh list --all", "r");
				while(fgets(q, sizeof q, pp))
				{
					if(strstr(q, VMname) != NULL)
					{
						check = 1;
						break;
					}
				}
                pclose(pp);
				if(check == 1)
                	send(clientfd, "1", sizeof("1"), 0);
				else
					send(clientfd, "0", sizeof("0"), 0);
			}
			/* set time to power on or off '^' */
			else if(rec[0] == '^')
			{
				char *id, *VMname, *minute, *hour, *day, *month, *week, *work, q[1024], line[1024], ch;
				extern FILE *popen();
				FILE *fp1, *fp2, *fp3;
printf("%s\n", rec);

				for(i=0; i<strlen(rec); i++)
					rec[i] = rec[i+1];

				id = strtok(rec, "+");
				VMname = strtok(NULL, "+");
				minute = strtok(NULL, "+");
				hour = strtok(NULL, "+");
				day = strtok(NULL, "+");
				month = strtok(NULL, "+");
				week = strtok(NULL, "+");
				work = strtok(NULL, "/");

				if(strcmp(day, "a") == 0) strcpy(day, "*");
				if(strcmp(month, "a") == 0) strcpy(month, "*");
				if(strcmp(week, "a") == 0) strcpy(week, "*");

				fp1 = fopen("crontab/list1", "r");
				fp2 = fopen("crontab/list2", "w");

				if(strcmp(work, "add") == 0 || strcmp(work, "del") == 0)
				{
					sprintf(q, " %2s %2s  %2s  %2s   * root  virsh start %s_%s\n", minute, hour, day, month, id, VMname);
					while(!feof(fp1))
					{
						fgets(line, 1024, fp1);
						if(feof(fp1)) break;
						if(strcmp(q, line) != 0)
							fprintf(fp2, "%s", line);
					}
					if(strcmp(work, "add") == 0)
						fprintf(fp2, "%s", q);
				}
				else if(strcmp(work, "radd") == 0 || strcmp(work, "rdel") == 0)
				{
					sprintf(q, " %2s %2s   *   *   %s root  virsh start %s_%s\n", minute, hour, week, id, VMname);
					while(!feof(fp1))
					{
						fgets(line, 1024, fp1);
						if(feof(fp1)) break;
						if(strcmp(q, line) != 0)
							fprintf(fp2, "%s", line);
					}
					if(strcmp(work, "radd") == 0)
						fprintf(fp2, "%s", q);
				}

				fclose(fp1);
				fclose(fp2);

				fp1 = fopen("crontab/crontab", "r");
				fp2 = fopen("crontab/list2", "r");
				fp3 = fopen("crontab/list3", "w");
					
				while((ch=getc(fp1)) != EOF)
					fprintf(fp3, "%c", ch);

				while((ch=getc(fp2)) != EOF)
					fprintf(fp3, "%c", ch);

				fclose(fp1);
				fclose(fp2);
				fclose(fp3);

				popen("cp crontab/list2 crontab/list1", "r");
				popen("cp crontab/list3 /etc/crontab", "r");

				send(clientfd, "1", sizeof("1"), 0);
			}
			/* update timeset '%' */
			else if(rec[0] == '%')
			{
				char *id, q[1024], line[1024], s[1024];
				FILE *fp1;

				for(i=0; i<strlen(rec); i++)
					rec[i] = rec[i+1];

				id = strtok(rec, "/");
				strcpy(s, "");

				fp1 = fopen("crontab/list3", "r");
				sprintf(q, "root  virsh start %s", id);

				while(!feof(fp1))
				{
					fgets(line, 1024, fp1);
				    if(feof(fp1)) break;
					if(strstr(line, q) != NULL)
						strcat(s, line);
				}
				printf("%s\n", s);

				send(clientfd, s, sizeof(s), 0);
			}
			/* create account '/' */
			else if(rec[0] == '/')
			{
				char *id, *passwd, *Email, q[1024];
				extern FILE *popen();
                MYSQL_ROW row;
				MYSQL_RES *result;

                for(i=0; i<strlen(rec); i++)
                    rec[i] = rec[i+1];

                id = strtok(rec, "+");
				passwd = strtok(NULL, "+");
				Email = strtok(NULL, "/");
				//printf("%s %s %s\n", id, passwd, Email);

                /* Connect to USER_inform */
                if (mysql_query(con, "SELECT * FROM USER_inform"))
                {
                    finish_with_error(con);
                }
                result = mysql_store_result(con);

                if (result == NULL)
                {
                    finish_with_error(con);
                }

                int num_fields = mysql_num_fields(result);

                while ((row = mysql_fetch_row(result)))
                {
                    if(strcmp(id, row[0]) == 0)
					{
                        check = 1;
						break;
					}
                }
				mysql_free_result(result);

				if(check == 0)
				{
					sprintf(q, "cp ../authenticate/mail ../authenticate/%s_mail", id);
					popen(q, "r");
					for(i=0; i<5000000; i++);
					sprintf(q, "echo 'http://140.116.82.180/phpmyadmin/authen/%s.php' >> ../authenticate/%s_mail", id, id);
					popen(q, "r");
					for(i=0; i<5000000; i++);
					sprintf(q, "cp /opt/lampp/phpmyadmin/authen/sample.php /opt/lampp/phpmyadmin/authen/%s.php", id);
					popen(q, "r");
					for(i=0; i<5000000; i++);
					sprintf(q, "echo 'exec(\"sudo ./checking %s\");' >> /opt/lampp/phpmyadmin/authen/%s.php", id, id);
					popen(q, "r");
					for(i=0; i<5000000; i++);
					sprintf(q, "echo '?>' >> /opt/lampp/phpmyadmin/authen/%s.php", id);
					popen(q, "r");
					for(i=0; i<5000000; i++);
					sprintf(q, "mail -s 'vertification mail' %s < ../authenticate/%s_mail", Email, id);
					popen(q, "r");
					

					send(clientfd, "1", sizeof("1"), 0);
					sprintf(q, "CREATE TABLE %sVM(Id INT PRIMARY KEY AUTO_INCREMENT, VMname TEXT)", id);
					if (mysql_query(con, q))      
   						finish_with_error(con);
  					
					sprintf(q, "INSERT INTO USER_inform VALUES('%s', '%s', '%s', 0)", id, passwd, Email);
					if (mysql_query(con, q))     
   						finish_with_error(con);
				}	
				else 
					send(clientfd, "0", sizeof("0"), 0);
			}

			close(clientfd);
		}

		msg[0] = '\0';
	}

	/* close(server) , but never get here because of the loop */
	close(sockfd);
	mysql_close(con);

	return 0;
}

