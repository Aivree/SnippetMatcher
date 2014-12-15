/*

  LIST.C
  ======
  (c) Paul Griffiths, 1999
  Email: mail@paulgriffiths.net

  Implementation of linked list functions

*/


#include <stdlib.h>
#include <ctype.h>
#include "list.h"


/*  Our linked list node structure  */

typedef struct node {
    char *phrase[2];
    struct node *next;
} node;


/*  Pointers to start and end of node  */

node *startnode = NULL, *endnode = NULL;


/*  Adds an node onto the end of the list  */

int AppendItem(char *phrase1, char *phrase2) {

    node *tempnode;


    /*  Allocate memory for a new node  */

    if ( !endnode ) {

	/*  If endnode is NULL, we have an empty 
	    list, so create the first element now.  */

	if ( ! (startnode = malloc(sizeof(node))) )
	    return 1;
	endnode = startnode;
	startnode->next = NULL;
    }
    else {

	/*  Otherwise, make a new node and make
	    our current last node point to it.   */

	if ( ! (tempnode = malloc(sizeof(node))) )
	    return 1;
	endnode->next = tempnode;
	endnode = tempnode;
    }

    
    /*  Initialise node members  */

    endnode->next = NULL;

    if ( ! (endnode->phrase[0] = malloc(strlen(phrase1)+1)) )
	return 1;
    strcpy(endnode->phrase[0], phrase1);

    if ( ! (endnode->phrase[1] = malloc(strlen(phrase2)+1)) )
	return 1;
    strcpy(endnode->phrase[1], phrase2);


    return 0;
}


/*  Retrieves from the list the item at the specified index  */

int GetItem(int index, char **phrase1, char **phrase2) {

    int n = 0;
    node *tempnode = startnode;


    /*  Cycle through list to specified index  */

    while ( index > n++ )
	tempnode = tempnode->next;


    /*  Update function arguments to point to the data  */

    *phrase1 = tempnode->phrase[0];
    *phrase2 = tempnode->phrase[1];

    return 0;
}


/*  Free's all the malloc'ed memory in our list  */

int FreeList() {

    node *tempnode = startnode;

    while ( tempnode ) {
	free(tempnode->phrase[0]);
	free(tempnode->phrase[1]);
	startnode = tempnode->next;
	free(tempnode);
	tempnode = startnode;
    }

    return 0;
}


/*  Remove trailing whitespace and '\n's from a string  */

int Trim(char *buffer) {

    int n = strlen(buffer) - 1;

    while ( !isalnum(buffer[n]) )
	buffer[n--] = '\0';

    return 0;
}
