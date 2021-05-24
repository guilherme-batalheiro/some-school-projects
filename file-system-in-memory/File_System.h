#ifndef _FILE_SYSTEM_
#define _FILE_SYSTEM_

#include "AVL_tree.h"

int add_directory_and_file(Item rootDirectory, char *dirName, char *value);

Item search_file_from_directory(Item rootDirectory, char *dirName);

Item search_directory_from_file(Item head, char *fileName);

void print_directories(Item head);

void list_directories(Item rootDirectory, char *dirName);

int delete_directory(Item auxParentDirectory, char *dirName);

#endif