#ifndef _AVLTREE_
#define _AVLTREE_

#include "Item.h"

typedef struct AVL_node* link;

struct AVL_node {
	void *item;
	link l, r;
	int height;
};

void AVL_init(link *head);

void AVL_delete(link*head, Key k, Key (*key)(void *), void (*deleteItem)(void *));

void AVL_insert(link*head, void * item, Key (*key)(void *));

void *AVL_search(link head, Key v, Key (*key)(void *));

void AVL_free(link*head, Key (*key)(void *), void (*deleteItem)(void *));

void AVL_sort(link head, void (*visit)(void *));

#endif