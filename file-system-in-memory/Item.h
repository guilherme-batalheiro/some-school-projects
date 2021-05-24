#ifndef _ITEM_
#define _ITEM_

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define less(a, b) (strcmp(a, b) < 0)
#define eq(a, b) (strcmp(a, b) == 0)

typedef char *Key;

typedef void *Item;

typedef struct file{
	char *fileDirectory;
	char *value;
} File;

typedef struct directory {
    char *dirName;

    /*linear connection */
	struct directory *nextDir;
	struct directory *prevDir;
	struct directory *linearHeadDir;

    /* childrenDirectories */
	struct AVL_node *childrenDirectories;

	File *file;
} Dir;

char *key_directory(Item a);

char *key_file(Item a);

Item new_file(char *fileDirectory, char *value);

Item new_directory(char *dirName, Item parentDirItem);

void deleteItemFile(Item a);

void deleteItemDir(Item a);

void visit(Item item);

#endif