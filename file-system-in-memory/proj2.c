/*
 * File: AVL_tree.c
 *
 * Author: Guilherme Batalheiro (ist199075)
 *
 * Description:
 * Main function.
*/
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "File_System.h"
#include "proj2.h"

int filter_directory(char **dirNotFiltered){
	/*  filters the directory char and returns 1 if success */
	int in = 0, directoryLen = 1;
	char *directory = NULL, *ptrDirNotFiltered = NULL;

	directory = (char *)malloc(sizeof(char) * (directoryLen + 1));
	if(directory == NULL)
		return 0;
	
	directory[0] = '/';
    ptrDirNotFiltered = *dirNotFiltered;

	while(*ptrDirNotFiltered != '\0' && *ptrDirNotFiltered != ' ' && *ptrDirNotFiltered != '\n'
		   && *ptrDirNotFiltered != '\t'){
		if(*ptrDirNotFiltered != '/'){
			in = 1;
			directoryLen += 1;
			directory = (char *)realloc(directory, sizeof(char) * (directoryLen + 1));
			if(directory == NULL)
				return 0;
			directory[directoryLen - 1] = *ptrDirNotFiltered;
		} else {
			if(in && *ptrDirNotFiltered != '\n'){
				directoryLen += 1;
				directory = (char *)realloc(directory, sizeof(char) * (directoryLen + 1));
				if(directory == NULL)
					return 0;
				directory[directoryLen - 1] = *ptrDirNotFiltered;
				in = 0;
			}
		}
		ptrDirNotFiltered++;
	}

	directory[directoryLen] = '\000';

	memcpy(*dirNotFiltered, directory, directoryLen + 1);

	free(directory);

	return 1;
}

int cmd_set(Item rootDirectory, char *instruct){
    /* return 1 if successfully or 0 if it failed */
	char directory[MAX_CHAR_INSTRUCT], value[MAX_CHAR_INSTRUCT], *ptr;
	directory[0] = '\0';

	sscanf(instruct, "%*s %s %[^\n]", directory, value);

	ptr = directory;

	if(filter_directory(&ptr) == 0)
		return 0;

	if(!add_directory_and_file(rootDirectory, directory, value))
		return 0;

	return 1;
}

void cmd_print(Item rootDirectory){
	if(((Dir*)rootDirectory) -> linearHeadDir != NULL) /* check if the root have children */
		print_directories(((Dir*)rootDirectory) -> linearHeadDir);
}

void cmd_list(Item rootDirectory, char *instruct){
	char *ptr = NULL, directory[MAX_CHAR_INSTRUCT];
	directory[0] = '\0';

	sscanf(instruct, "%*s %s", directory);

	if(strcmp(directory, "") != 0){
		ptr = directory;	
		filter_directory(&ptr);
	}

	list_directories(rootDirectory, directory);
}

void cmd_find(Item rootDirectory, char *instruct){
	Item directoryItem;
	char *ptr, directory[MAX_CHAR_INSTRUCT];

	sscanf(instruct, "%*s %s", directory);

	ptr = directory;
	
	filter_directory(&ptr);

	directoryItem = search_file_from_directory(rootDirectory, directory);
	if(directoryItem != NULL){
		if(((Dir*)directoryItem) -> file != NULL)
			printf("%s\n", ((Dir*)directoryItem) -> file -> value);
		else
			printf("no data\n");
	}
}

void cmd_search(Item rootDirectory, char *instruct){
	Item directoryItem;
	char value[MAX_CHAR_INSTRUCT];

	sscanf(instruct, "%*s %[^\n]", value);

	directoryItem = search_directory_from_file( rootDirectory, value);
	if(directoryItem != NULL){
		printf("%s\n", ((Dir *)directoryItem) -> file -> fileDirectory);
	} else{
		printf("not found\n");
	}
}

void cmd_delete(Item rootDirectory, char *instruct){
	char *ptr, directory[MAX_CHAR_INSTRUCT];
	directory[0] = '\0';

	sscanf(instruct, "%*s %s", directory);

	if(strcmp(directory, "") != 0){
		ptr = directory;	
		filter_directory(&ptr);
	}

	if(!delete_directory(rootDirectory, directory))
		printf("not found\n");
}

void cmd_help(){
    printf("%s", HELP_MESSAGE);
}

int main(){
    char instruct[MAX_CHAR_INSTRUCT], cmdName[MAX_COMMAND_CHAR], *check;
    Item rootDirectory;

	rootDirectory = new_directory(" ", NULL);
    if(rootDirectory == NULL){
        printf("No memory.\n");
		return 0;
    }

	/* the size of the command is set to 7 cuz the biggets string have 6 characters */
	while(1){
		check = fgets(instruct,  MAX_CHAR_INSTRUCT, stdin);
		if (check == NULL || strchr(instruct, '\n') == NULL){
			break;
		}
		sscanf(instruct, "%s", cmdName);
		
		if(strcmp(cmdName, HELP_COMMAND) == 0)
			cmd_help();
		else if(strcmp(cmdName, QUIT_COMMAND) == 0)
			break;
		else if(strcmp(cmdName, SET_COMMAND ) == 0){		
			if(!cmd_set(rootDirectory, instruct)){
				printf(" No memory.\n ");
				break;
			}
		} else if(strcmp(cmdName, PRINT_COMMAND) == 0)
			cmd_print(rootDirectory);
		else if(strcmp(cmdName, FIND_COMMAND) == 0)
			cmd_find(rootDirectory, instruct);
		else if(strcmp(cmdName, LIST_COMMAND) == 0)
			cmd_list(rootDirectory, instruct);
		else if(strcmp(cmdName, SEARCH_COMMAND) == 0)
			cmd_search(rootDirectory, instruct);
		else if(strcmp(cmdName, DELETE_COMMAND) == 0)
			cmd_delete(rootDirectory, instruct);
		instruct[0] = '\0';
    }

	deleteItemDir(rootDirectory);
    return 0;
}