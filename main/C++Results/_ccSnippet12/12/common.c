/*
 * common.c
 * helper functions
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <errno.h>

#include "common.h"

int strUpper(char *buffer) {
	while (*buffer) {
		*buffer = toupper(*buffer);
		++buffer;
	}

	return 0;
}

int trim(char *buffer) {
	int n = strlen(buffer) - 1;

	while (!isalnum(buffer[n]) && n >= 0) {
		buffer[n--] = '\0';
	}

	return 0;
}

int readline(int sockfd, void *vptr, int maxlen) {
	int n, rc;
	char c, *buffer;

	buffer = vptr;

	for (n = 1; n < maxlen; n++) {
		if ((rc = read(sockfd, &c, 1)) == 1) {
			/* TODO DEBUG printf("%c", c); */
			*buffer++ = c;
			if (c == '\n') {
				break;
			}
		} else if (rc == 0) {
			if (n == 1) {
				return 1;
			} else {
				break;
			}
		} else {
			if (errno == EINTR) {
				continue;
			}
			printerr("error in readline");
		}
	}

	*buffer = 0;
	return n;
}

int writeline(int sockfd, void *vptr, int len) {
	int n, nw;
	char *buf;

	buf = vptr;
	n = len;

	while (n > 0) {
		if ((nw = write(sockfd, buf, n)) <= 0) {
			if (errno == EINTR) {
				nw = 0;
			} else {
				printerr("error in writeline");
			}
		}
		n -= nw;
		buf += nw;
	}

	return n;
}

void printerr(char *msg) {
	fprintf(stderr, "# ERROR: %s\n", msg);
	perror("## PERROR ##");
	exit(-1);
}
