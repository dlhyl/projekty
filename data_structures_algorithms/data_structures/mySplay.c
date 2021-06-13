/*
	xdlhyl			 AIS ID: 102897

	Moja implementacia Splay stromu.

*/

#ifndef __splay__
#define __splay__

#include <stdio.h>
#include <stdlib.h>

//struktura na vkladany prvok
typedef struct SplayNode {
	int data;
	struct SplayNode* left, *right, *parent;
} SPLAYNODE;

static int isRightChild(SPLAYNODE* node) {
	return node->parent->right == node;
}

static void ZigRotation(SPLAYNODE* node) {
	//zig rotacia je ekvivalent pravej rotacie
	//treba osetrit aj pointre na rodica

	// Parent's Left Child = Node's Right Child
	node->parent->left = node->right;

	// Node Right Child's Parent = Parent
	if (node->right != NULL) 
		node->right->parent = node->parent;

	// Node's Right Child = Parent
	node->right = node->parent;

	// Node's parent = GrandParent
	node->parent = node->right->parent;

	// If GrandParent exists, change its child to Node instead of Parent
	if (node->right->parent != NULL)
		(isRightChild(node->right) == 1) ? (node->right->parent->right = node): (node->right->parent->left = node);

	// Parent's parent = Node
	node->right->parent = node;

}

static void ZagRotation(SPLAYNODE* node) {
	//zag rotacia je ekvivalent lavej rotacie
	//treba osetrit aj pointre na rodica

	// Parent's Right Child = Node's Left Child
	node->parent->right = node->left;

	// Node Left Child's Parent = Parent
	if (node->left != NULL)
		node->left->parent = node->parent;

	// Node's Left Child = Parent
	node->left = node->parent;

	// Node's parent = GrandParent
	node->parent = node->left->parent;

	// If GrandParent exists, change its child to Node instead of Parent
	if (node->left->parent != NULL)
		(isRightChild(node->left) == 1) ? (node->left->parent->right = node) : (node->left->parent->left = node);

	// Parent's parent = Node
	node->left->parent = node;
}

static void ZigZigRotation(SPLAYNODE* node) {
	// Left-Left case
	// spravi Zig rotaciu parenta (GrandParenta posunie nizsie)
	// a potom Zig rotaciu node-u (Parenta posunie nizsie)
	ZigRotation(node->parent);
	ZigRotation(node);
}

static void ZagZagRotation(SPLAYNODE* node) {
	// Right-Right case
	// spravi Zag rotaciu parenta (GrandParenta posunie nizsie)
	// a potom Zag rotaciu node-u (Parenta posunie nizsie)
	ZagRotation(node->parent);
	ZagRotation(node);
}

static void ZigZagRotation(SPLAYNODE* node) {
	// Right-Left case
	// spravi Zig rotaciu node-u (Parenta posunie nizsie)
	// a potom Zag rotaciu node-u (GrandParenta posunie nizsie)
	ZigRotation(node);
	ZagRotation(node);
}

static void ZagZigRotation(SPLAYNODE* node) {
	// Left-Right case
	// spravi Zag rotaciu node-u (Parenta posunie nizsie)
	// a potom Zig rotaciu node-u (GrandParenta posunie nizsie)
	ZagRotation(node);
	ZigRotation(node);
}

static SPLAYNODE* allocateNode(int data, void* right, void* left, void* parent) {
	SPLAYNODE* node = (SPLAYNODE*)malloc(sizeof(SPLAYNODE));

	node->parent = parent;
	node->data = data;
	node->left = left;
	node->right = right;
	
	return node;
}

void removesplaytree(SPLAYNODE* node)
{
	// iterativny sposob uvolnenia pamate vsetkych vrcholov zo stromu

	while (node != NULL)
	{
		if (node->left != NULL) {
			node = node->left;
		}
		else if (node->right != NULL) {
			node = node->right;
		}
		else {
			SPLAYNODE* tmp = node;
			node = node->parent;
			if (node != NULL) {
				if (node->left == tmp) node->left = NULL;
				else node->right = NULL;
			}
			free(tmp);
		}
	}
}

static void Splay(SPLAYNODE* node, SPLAYNODE** root) {

	//robi rotacie pokial node nebude root
	while (node->parent != NULL) {
	
		//ked je dieta rootu => robi Zig alebo Zag rotaciu
		if (node->parent->parent == NULL) {
			if (isRightChild(node)) {
				//	splayRotations++;
				ZagRotation(node);
			} else {
				//splayRotations++;
				ZigRotation(node);
			}
		}

		//ostatne situacie
		else {
			short int isParentRightChild = isRightChild(node->parent);
			short int isNodeRightChild = isRightChild(node);

			//right - right => ZagZagRotation
			if (isParentRightChild && isNodeRightChild) {
				ZagZagRotation(node);
			}
			//left - left => ZigZigRotation
			else if (!isParentRightChild && !isNodeRightChild) {
				ZigZigRotation(node);
			}
			//right - left => ZigZagRotation
			else if (isParentRightChild && !isNodeRightChild) {
				ZigZagRotation(node);
			}
			//left - right => ZagZigRotation
			else {
				ZagZigRotation(node);
			}
		}
	}
	//nastavi node na root
	*root = node;
}

int insertSplay(int data, SPLAYNODE** root) {
	
	SPLAYNODE* tmp = *root;

	//strom je prazdny, insertujem root
	if (tmp == NULL) {
		*root = allocateNode(data,NULL,NULL,NULL);
		return 1;
	}

	//inak najdem miesto pre node, vlozim ho a zavolam nan funkciu Splay
	while (tmp != NULL) {
		if (tmp->data < data) {
			if (tmp->right == NULL) {
				tmp->right = allocateNode(data, NULL, NULL, tmp);
				Splay(tmp->right, root);
			}
			tmp = tmp->right;
		}
		else if (tmp->data > data) {
			if (tmp->left == NULL) {
				tmp->left = allocateNode(data, NULL, NULL, tmp);
				Splay(tmp->left, root);
			}
			tmp = tmp->left;
		}
		//duplicitna hodnota -> nevklada
		else
			return 0;
	}
	return 1;
}

void* searchSplay(int data,SPLAYNODE** root) {
	SPLAYNODE* node = *root;

	//binarnym vyhladavanim sa pokusi najst vrchol; ak najde vrchol, tak nan zavola funkciu Splay
	while (node != NULL) {
		if (node->data < data)
			node = node->right;
		else if (node->data > data)
			node = node->left;
		else
		{
			Splay(node,root);
			return *root;
		}
	}
	return NULL;
}
#endif