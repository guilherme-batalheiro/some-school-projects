/*
 * File: AVL_tree.c
 *
 * Author: Guilherme Batalheiro (ist199075)
 *
 * Description:
 * The functions for the file system.
*/
#include "File_System.h"

int add_directory_and_file(Item rootDirectory, char *dirName, char *value){
	/* return 1 if was successfully */
	Item newDir = NULL;
	Item newFile = NULL;
	char *token = NULL, s[2] = "/", *auxDirName = NULL;

	auxDirName = (char *)malloc(sizeof(char) * (strlen(dirName) + 1));
	if(auxDirName == NULL)
		return 0;
	strcpy(auxDirName, dirName);

	token = strtok(auxDirName, s);

	/* travel all the directories */
	while(token != NULL){
		newDir = AVL_search(((Dir *)rootDirectory) -> childrenDirectories, token, key_directory);
		if(newDir == NULL){
			/* check it the directory doesn't exists */
			newDir = new_directory(token, rootDirectory);
			if(newDir == NULL){
				free(token);
				free(auxDirName);
				return 0;
			}
			AVL_insert(&((Dir *)rootDirectory) -> childrenDirectories, newDir, key_directory);
		}
 
		rootDirectory = newDir;
		token = strtok(NULL, s);
	}

	newFile = new_file(dirName, value);
	if(newFile == NULL){
		free(token);
		free(auxDirName);
		free(((Dir *)newDir) -> dirName);
		free(newDir);
		return 0;
    }

	if(((Dir *)newDir) -> file != NULL){
		deleteItemFile(((Dir *)newDir) -> file );
	}

	/* add the file to the directory */
    ((Dir *)newDir) -> file = (File *)newFile;
	
	free(token);
	free(auxDirName);
	return 1;
}

Item search_file_from_directory(Item rootDirectory, char *dirName){
	/* return Item if was successfully found or NULL if it failed */
	Item directory;
	char *token, s[2] = "/", *auxDirName;

	auxDirName = (char *)malloc(sizeof(char) * (strlen(dirName) + 1));
	if(auxDirName == NULL)
		return NULL;
	
	strcpy(auxDirName, dirName);
	token = strtok(auxDirName, s);
	while(token != NULL){
		directory = AVL_search(((Dir *)rootDirectory) -> childrenDirectories, token, key_directory);
		if(directory == NULL){
			printf("not found\n");
			free(auxDirName);
			return NULL;
		}
		rootDirectory = directory;
		token = strtok(NULL, s);
	}


	free(auxDirName);
	return directory;
}

Item search_directory_from_file(Item head, char *value){
	/* return Item if was successfully found or NULL if it failed */
	Item firstDirectoryLinear, file;

	firstDirectoryLinear = head;
	do{
		if(((Dir *)head) -> file != NULL){ /* if the directory have a file print it */
			if(strcmp(((Dir *)head) -> file -> value, value) == 0){
				return head; 
			}
		}

		if(((Dir *)head) -> linearHeadDir != NULL ){
			file = search_directory_from_file(((Dir *)head) -> linearHeadDir, value);
			if(file != NULL){
				return file;
			}
		}

		head = ((Dir *)head) -> nextDir;
	}while(head != firstDirectoryLinear);

	return NULL;
}

void print_directories(Item head){
	/* printf all the directories*/
	Item firstDirectoryLinear;

	firstDirectoryLinear = head;
	do{
		if(((Dir *)head) -> file != NULL) /* if the directory have a file print it */
			printf("%s %s\n", ((Dir *)head)  -> file -> fileDirectory, ((Dir *)head) -> file -> value);

		if(((Dir *)head) -> linearHeadDir != NULL )
			print_directories(((Dir *)head) -> linearHeadDir);

		head = ((Dir *)head) -> nextDir;
	}while(head != firstDirectoryLinear);
}

void list_directories(Item rootDirectory, char *dirName){
	/* list the subconjun directories*/
	Item parent;

	if(strcmp(dirName, "") == 0){
		parent = rootDirectory;
	} else {
		parent = search_file_from_directory(rootDirectory, dirName);
		if(parent == NULL)
			return;
	}

	AVL_sort(((Dir *)parent) -> childrenDirectories, visit);
}

int delete_directory(Item rootDirectory, char *dirName){
	/* return 1 if was successfully */
	Item directory, parentDirectory;
	char *token, s[2] = "/", *auxDirName;

	if(strcmp(dirName, "") == 0){
		/* delete the rootFile childrens */
		((Dir *)rootDirectory) -> linearHeadDir = NULL;
		AVL_free(&(((Dir *)rootDirectory) -> childrenDirectories), key_directory, deleteItemDir);
		return 1;
	}

	auxDirName = (char *)malloc(sizeof(char) * (strlen(dirName) + 1));
	if(auxDirName == NULL)
		return 0;

	strcpy(auxDirName, dirName);
	token = strtok(auxDirName, s);
	
	while(token != NULL){
		directory = AVL_search(((Dir *)rootDirectory) -> childrenDirectories, token, key_directory);
		if(directory == NULL){
			free(auxDirName);
			return 0;
		}
		parentDirectory = rootDirectory;
		rootDirectory = directory;
		token = strtok(NULL, s);
	}

	/* fix the linearHeadDir of the parent */ 
	if(eq(key_directory(((Dir *)parentDirectory) -> linearHeadDir), key_directory((Dir *)directory))){
		/* check if we eleminated the headLinear */
		if(eq(key_directory(((Dir *)parentDirectory) -> linearHeadDir), key_directory((Dir *)(((Dir *)parentDirectory) -> linearHeadDir -> nextDir))))
			/* check if is the last one */
			((Dir *)parentDirectory) -> linearHeadDir = NULL;
		else
			((Dir *)parentDirectory) -> linearHeadDir = ((Dir *)parentDirectory) -> linearHeadDir -> nextDir;
	}

	AVL_delete(&(((Dir *)parentDirectory) -> childrenDirectories), key_directory(directory), key_directory, deleteItemDir);

	free(auxDirName);
	return 1;
}
		
/* TESTES */
/*
int main(){
    link fileTree;
    Item rootDir;

    rootDir = new_directory(" ", NULL);

    AVL_init(&fileTree);

    ((Dir *)rootDir) -> rootFile = &fileTree;

    add_directory_and_file(rootDir, "/um", "lá vai uma");
	add_directory_and_file(rootDir, "/dois", "dois lá vão duas");
	add_directory_and_file(rootDir, "/três", "três pombinhas a voar");

	list_directories(rootDir, "");

	deleteItemDir(rootDir);
	AVL_free(&fileTree, key_file, deleteItemFile);
    return 0;
}
*/