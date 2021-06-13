/*
	xdlhyl			 AIS ID: 102897

	Moja implementacia hashovej tabulky.
	Pouziva spajane zoznamy na riesenie kolizii.
*/

#ifndef __hashtableld__
#define __hashtableld__

#include <stdlib.h>
#include <stdio.h>


#define DEBUG 0
#define HASHTABLESIZE 8 // defaultna velkost tabulky
#define RESIZETHRESHOLD 80 // threshold (v percentach) na zmenu velkosti tabulky
#define RESIZECOEFF 2 // koeficient zvacsenia tabulky

//struktura na vkladany prvok
typedef struct hashtable_entry {
	int key;
	struct hashtable_entry* next;
}HASHTABLE_ENTRY;

//struktura na tabulku
typedef struct hashtablee {
	unsigned int size;
	unsigned int filledSize;
	HASHTABLE_ENTRY** array;
}HASHTABLE;

HASHTABLE* newTable();
void printTable(HASHTABLE* table);
int insert_LL(HASHTABLE** hashtable, int key);
void _resizeTable(HASHTABLE* hashtable);

//hashova funkcia
static unsigned int _hash(int key, unsigned int size) {
	return key > 0 ? key % size : (-key) % size;
}

//vypis tabulky vo forme index -> element -> dalsi_element -> dalsi.., vypise iba indexy na ktorych su prvky
void printTable(HASHTABLE* table) {
	for (unsigned int i = 0; i < table->size; i++) {
		if (table->array[i] != NULL) {
			printf("Index %d : ", i);

			HASHTABLE_ENTRY* tmp = table->array[i];
			while (tmp != NULL) {
				printf("[%d]", tmp->key);
				if (tmp->next != NULL)
					printf(" -> ");
				tmp = tmp->next;
			}
			printf("\n");
		}
	}
}

//zmena velkosti tabulky
void _resizeTable(HASHTABLE* hashtable) {
	if (DEBUG) printf("RESIZING TABLE FROM %u to %u (REACHED %d%%  - %u).\n", hashtable->size, hashtable->size * 2, RESIZETHRESHOLD, hashtable->filledSize);
	
	HASHTABLE_ENTRY** oldArr = hashtable->array;
	int oldSize = hashtable->size;

	hashtable->filledSize = 0;
	hashtable->size *= RESIZECOEFF;

	hashtable->array = (HASHTABLE_ENTRY**)calloc(hashtable->size ,sizeof(HASHTABLE_ENTRY*));

	for (unsigned int i = 0; i < oldSize; i++) {
		HASHTABLE_ENTRY* tmp = oldArr[i];
		while (tmp != NULL) {
			HASHTABLE_ENTRY* newEntry;
			if ((newEntry = malloc(sizeof(HASHTABLE_ENTRY))) == NULL)
				return;

			unsigned int hashedKey = _hash(tmp->key, hashtable->size);

			newEntry->key = tmp->key;
			newEntry->next = (hashtable)->array[hashedKey];
			hashtable->array[hashedKey] = newEntry;
			hashtable->filledSize++;

			HASHTABLE_ENTRY* ptr = tmp;
			tmp = tmp->next;
			free(ptr);
		}
	}
	free(oldArr);

}

void* search_LL(HASHTABLE* hashtable, int key) {
	unsigned int hashedKey = _hash(key, hashtable->size);

	HASHTABLE_ENTRY* tmp = hashtable->array[hashedKey];

	while (tmp!= NULL) {
		if (tmp->key == key)
			return tmp;
		else
			tmp = tmp->next;
	}

	return NULL;
}

int insert_LL(HASHTABLE** hashtable, int key) {

	// zvacsenie tabulky po dosiahnuti tresholdu
	if ((float)((*hashtable)->filledSize * 100) / (*hashtable)->size > RESIZETHRESHOLD) {
		_resizeTable(*hashtable);
	}

	// alokovanie noveho prvku
	HASHTABLE_ENTRY* newEntry;
	if ((newEntry = malloc(sizeof(HASHTABLE_ENTRY))) == NULL)
		return;

	unsigned int hashedKey = _hash(key, (*hashtable)->size);

	HASHTABLE_ENTRY* tmp = (*hashtable)->array[hashedKey];

	// overenie duplicity -> duplicitny prvok nevklada
	while (tmp != NULL) {
		if (tmp->key == key)
			return 0;
		else
			tmp = tmp->next;
	}

	// vlozenie prvku na zaciatok spajaneho zoznamu, ktory sa nachadza v poli na indexe vypocitanom hashovou funkciou
	newEntry->key = key;
	newEntry->next = (*hashtable)->array[hashedKey];
	(*hashtable)->array[hashedKey] = newEntry;
	(*hashtable)->filledSize++;

	return 1;
}

// vytvorenie novej tabulky s velkostou z makra
HASHTABLE* newTable() {
	HASHTABLE* hashtable;

	if ((hashtable = (HASHTABLE*)malloc(sizeof(HASHTABLE))) == NULL)
		return NULL;

	if ((hashtable->array = calloc(HASHTABLESIZE, sizeof(HASHTABLE_ENTRY*))) == NULL)
		return NULL;

	hashtable->size = HASHTABLESIZE;
	hashtable->filledSize = 0;

	return hashtable;
}
#endif