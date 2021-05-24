/*
 * File: AVL_tree.c
 *
 * Author: Guilherme Batalheiro (ist199075)
 *
 * Description:
 * The functions for the AVL Tree struct are in this file.
*/
#include "AVL_tree.h"

int height(link h){
	if (h == NULL) return 0;
	return h -> height;
}

void update_height(link h){
	int height_left = height(h -> l);
	int height_right = height(h -> r);
	
	h -> height = height_left > height_right ? height_left + 1 : height_right + 1;
}

link rotL(link h){
	link x = h -> r;
	
	h -> r = x -> l;
	x -> l = h;
	update_height(h);
	update_height(x);
	
	return x;
}

link rotR(link h){
	link x = h -> l;
	
	h -> l = x -> r;
	x -> r = h;
	
	update_height(h);
	update_height(x);
	
	return x;
}

link rotLR(link h){
	if(h == NULL) 
		return h;
	
	h -> l = rotL(h->l);
	
	return rotR(h);
}

link rotRL(link h){
	if(h == NULL)
		return h;
	
	h -> r = rotR(h->r);
	
	return rotL(h);
}

int Balance(link h){
	if(h == NULL) return 0;
	
	return height(h -> l) - height(h -> r);
}

link AVL_balance(link h) {
	int balanceFactor;
    
	if(h == NULL) 
		return h;
    
	balanceFactor= Balance(h);
    
	if(balanceFactor>1) {
		if(Balance(h -> l) >= 0) 
			h = rotR(h);
		else
			h = rotLR(h);
	} else if(balanceFactor < -1){
		if (Balance(h -> r) <= 0)
			h = rotL(h);
		else
			h = rotRL(h);
	} else
		update_height(h);
	return h;
} 

link new_link(void *item, link  l, link r){
    link newItem = (link)malloc(sizeof(struct AVL_node));

	newItem -> item = item;
	newItem -> l = l;
	newItem -> r = r;
	newItem -> height = 1;

	return newItem;
}

link insert(link h, void *item, Key (*key)(void *)){
	if (h == NULL)
		return new_link(item, NULL, NULL);
	if (less(key(item), key(h -> item)))
		h -> l = insert(h -> l, item, key);
	else
		h -> r = insert(h -> r, item, key);
	h = AVL_balance(h);
	return h;
}

void AVL_insert(link *head, void *item, Key (*key)(void *)){
	*head = insert(*head, item, key);
}

void *search(link h, Key v, Key (*key)(void *)){
	if (h == NULL)
		return NULL;
	if (eq(v, key(h -> item)))
		return h -> item;
	if (less(v, key(h -> item)))
		return search(h -> l, v, key);
	else
		return search(h -> r, v, key);
}

void *AVL_search(link head, Key v, Key (*key)(void *)){
	return search(head, v, key);
}

link max(link h) {
	if (h==NULL || h ->r == NULL)
		return h;
	else 
		return max(h -> r);
}

link delete(link h, Key k, Key (*key)(void *), void (*deleteItem)(void *)){
	if (h==NULL)
		return h;
	else if (less(k, key(h -> item))) 
		h -> l = delete(h -> l, k, key, deleteItem);
	else if (less(key(h-> item), k)) 
		h -> r = delete(h->r, k, key, deleteItem);
	else{
		if (h -> l != NULL && h -> r != NULL){ /* case 3 */
			link aux = max(h -> l);
			{ void * x; x = h -> item; h -> item = aux -> item; aux -> item = x; } 
			h -> l = delete(h -> l, key(aux -> item), key, deleteItem);
		}
		else {                                 /* case 1 and 2 */
			link aux = h;

			if(h -> l == NULL && h -> r == NULL) /* case 1 */
				h = NULL;
			else if(h -> l == NULL)            /* case 2a */
				h = h -> r;
			else                               /* case 2b */
				h = h -> l;
			deleteItem(aux -> item);
			free(aux);
		}
	}
    
	h = AVL_balance(h);
	return h;
}

void AVL_delete(link*head, Key k, Key (*key)(void *), void (*deleteItem)(void *)){
	*head = delete(*head, k, key, deleteItem);
}

link freeT(link h, Key (*key)(void *), void (*deleteItem)(void *)){
	if (h == NULL)
		return h;
	h -> l = freeT(h -> l, key, deleteItem);
	h -> r = freeT(h -> r, key, deleteItem);

	return delete(h, key(h -> item), key, deleteItem);
}

void AVL_free(link*head, Key (*key)(void *), void (*deleteItem)(void *)){
	*head = freeT(*head, key, deleteItem);
}

void AVL_init(link *head){
	*head = NULL;
}

void sort(link h, void (*visit)(void *)){
	if (h == NULL)
		return;
	sort(h -> l, visit);
	visit(h -> item);
	sort(h -> r, visit);
}

void AVL_sort(link head, void (*visit)(void *)){
	sort(head, visit);
}