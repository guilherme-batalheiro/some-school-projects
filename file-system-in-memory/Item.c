/*
 * File: AVL_tree.c
 *
 * Author: Guilherme Batalheiro (ist199075)
 *
 * Description:
 * The functions for the Item.
*/
#include "Item.h"
#include "AVL_tree.h"

char *key_directory(Item a){
	return ((Dir*)a) -> dirName;
}

char *key_file(Item a){
	return ((File*)a) -> value;
}

Item new_directory(char *dirName, Item parentDirItem){
	/* Create and return a new directory, or return NULL if the creation fails. */
	Dir *newDir, *parentDir;
	Item newItem;
	
	parentDir = (Dir *)parentDirItem;

	newDir = (Dir *)malloc(sizeof(Dir));
	if(newDir == NULL)
		return NULL;

	newDir -> file = NULL;
	newDir -> linearHeadDir = NULL;

	/* Add the new directory ​to the linked list.
	 *	linearHeadDir = \A
	 *  newDir = \D
	 *	   
	 * 	  ,-/A<----->/C<----->/B--.  »  ,-/A<----->/C<----->/B<----->/D--.   
	 *	  '  ^                 ^  '  »  '  ^                          ^  '
	 *    '  '-----------------'--´  »  '  '--------------------------'--'
	 * 	  '--------------------'     »  '-----------------------------'
	 * 
	*/


	newDir -> nextDir = newDir;
	newDir -> prevDir = newDir;
	
	if(parentDir != NULL){

		if((parentDir -> linearHeadDir) == NULL){
			/* If the parent doesn't have a linked list. */
			parentDir -> linearHeadDir = newDir;
		} else {
			/* Else add it to the linked list. */
			newDir -> prevDir = parentDir -> linearHeadDir -> prevDir;
			newDir -> nextDir = parentDir -> linearHeadDir;
			parentDir -> linearHeadDir -> prevDir -> nextDir = newDir;
		}
		/* Add it to the linked list as last item. */
		parentDir -> linearHeadDir -> prevDir = newDir;
	}

	/*  create dirName */
	newDir -> dirName = (char *)malloc(sizeof(char) * (strlen(dirName) + 1));
	if(newDir -> dirName == NULL){
		free(newDir);
		return NULL;
	}
	strcpy(newDir -> dirName, dirName);

	/*  initiate the AVLtree aka childrenDirectories */
	AVL_init(&newDir -> childrenDirectories);

	newItem = newDir;
    return newItem;
}

Item new_file(char *fileDirectory, char *value){
	/* Create and return a new file, or return NULL if the creation fails. */
	File *newfile;
	void *newItem;

	newfile = (File *)malloc(sizeof(struct file));
	if(newfile == NULL)
		return NULL;

	newfile -> value = (char *)malloc(sizeof(char) * (strlen(value) + 1));
	if(newfile -> value == NULL){
		free(newfile);
		return NULL;
	}
	
    strcpy(newfile -> value, value);

	newfile -> fileDirectory = (char *)malloc(sizeof(char) * (strlen(fileDirectory) + 1));
	if(newfile -> fileDirectory == NULL){
		free(newfile);
		free(newfile -> value);
		return NULL;
	}

	strcpy(newfile -> fileDirectory, fileDirectory);

	newItem = newfile;

	return newItem;
}

void deleteItemFile(Item a){
	/* Free item type file */

	free(((File*)a) -> fileDirectory);
	free(((File*)a) -> value);
    free((File*)a);
}

void deleteItemDir(Item a){
	/* Free item type directory */
    Dir *A;

    A = (Dir*)a;

	if(*A -> dirName != '\0' && strcmp(A -> dirName, A -> nextDir -> dirName) != 0){
		/* if it is not the last element */
		A -> nextDir -> prevDir = A -> prevDir;
		A -> prevDir -> nextDir = A -> nextDir;
	}
	
	if(A -> childrenDirectories != NULL) /* if the tree is not NULL delete the tree */
		AVL_free(&(A -> childrenDirectories), key_directory, deleteItemDir);

	if(A -> file != NULL){
		/* remove the file from the tree */
		deleteItemFile(A -> file);
	}

	A -> nextDir = NULL;
	A -> prevDir = NULL;
	A -> linearHeadDir = NULL;
	
	free(A-> dirName);
	free(A);
}

void visit(Item item){
	printf("%s\n", ((Dir*)item) -> dirName);
}