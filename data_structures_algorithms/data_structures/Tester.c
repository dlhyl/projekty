#define _CRT_SECURE_NO_WARNINGS
#include "rbtree.c"
#include "mySplay.c"
#include "MyHashTable.c"
#include "hashmap.c"

#include <time.h>

#define RED "\x1b[31m" 
#define GREEN "\x1b[32m"
#define YELLOW "\x1b[33m"
#define BLUE "\x1b[34m"
#define MAGENTA "\x1b[35m"
#define CYAN "\x1b[36m"
#define RESET "\x1b[0m"


#define NICEDEBUG 0
#define SHOWTESTCOUNTCONSOLE 1

void swap(int* a, int* b)
{
	int temp = *a;
	*a = *b;
	*b = temp;
}

void shuffle(int arr[], int n)
{
	for (int i = n - 1; i > 0; i--)
	{
		int j = (rand() & 0x7FFF);
			j <<= 15;
			j |= (rand() & 0x7FFF);
			j %= (i + 1);

		swap(&arr[i], &arr[j]);
	}
}

void printFormattedNumber(int number) {

	if (number < 1000)
		printf("%d", number);
	else if (number < 1000000)
		printf("%dk", number / 1000);
	else
		printf("%dM", number/1000000);

}

void printInorderRB(rbt_tree_node_t* node)
{
	if (node == NULL)
		return;

	printInorderRB(node->left);

	if (node->color) 
		printf("%d ", node->key);
	else 
		printf(RED "%d " RESET, node->key);

	printInorderRB(node->right);
}

void printInorderSplay(SPLAYNODE* node)
{
	if (node == NULL)
		return;

	printInorderSplay(node->left);

		printf("%d ", node->data);

	printInorderSplay(node->right);
}

void removeMyHT(HASHTABLE* table) {


	for (int i = 0; i < table->size; i++) {
		HASHTABLE_ENTRY* tmp = table->array[i];
		while (tmp != NULL) {
			HASHTABLE_ENTRY* tmp2 = tmp;
			tmp = tmp->next;
			free(tmp2);
		}
	}

	free(table->array);

	free(table);
}

void freeRB(rbt_tree_node_t* node) {
	if (!node) return;
	freeRB(node->left);
	freeRB(node->right);
	free(node);
	node = NULL;
}

void testSplayInsert(int iterations,int insertions, int* pole) {
	int failed = 0;
	SPLAYNODE** rootArray = calloc(iterations, sizeof(SPLAYNODE*));
	
	
	clock_t t1 = clock();
	for (int j = 0; j < iterations;j++) {
		for (int i = 0; i < insertions; i++) {
			if (!insertSplay(pole[i], &rootArray[j]))
				failed++;
		}
	}
	clock_t t2 = clock();

	for (int j = 0; j < iterations; j++) {
		removesplaytree(rootArray[j]);
	}
	free(rootArray);
	
	if (NICEDEBUG) {
		printf(YELLOW"-----------SPLAY TREE INSERTION TEST RESULTS-----------\nMADE "CYAN"%d"YELLOW" TESTS. \nIN EACH TEST INSERTED "CYAN"%d"YELLOW" NODES.\n", iterations, insertions);
		printf(YELLOW"Test took "MAGENTA"%d ms."YELLOW" ("MAGENTA"%.4lf us"YELLOW" per inserted node) \n", t2 - t1, (double)(t2 - t1) / (iterations * insertions) * 1000);
		printf(YELLOW"FAILED TOTAL OF "CYAN"%d"YELLOW" NODES ("CYAN"%d IN EACH TEST"YELLOW" - DUPLICIT VALUES).\n\n" RESET, failed, failed / iterations);

	}
	else {
		printf("%lf\t", (double)(t2 - t1) / (iterations * insertions) * 1000);
	}

}

void testRbInsert(int iterations,int insertions, int* pole) {
	rbt_tree_t** rootArray = malloc(iterations * sizeof(rbt_tree_t*));

	clock_t t1 = clock();
	int failed = 0;
	for (int j = 0; j < iterations; j++) {
		rootArray[j] = rbt_create();
		for (int i = 0; i < insertions; i++) {
			rbt_tree_node_t* n = malloc(sizeof(rbt_tree_node_t));
			n->key = pole[i];

			if (!rbt_insert(rootArray[j], n))
				failed++;
		}
	}
	clock_t t2 = clock();

	for (int j = 0; j < iterations; j++) {
		rbt_release(rootArray[j]);
	}
	free(rootArray);

	if (NICEDEBUG) {
		printf(YELLOW"-----------RB TREE INSERTION TEST RESULTS-----------\nMADE "CYAN"%d"YELLOW" TESTS. \nIN EACH TEST INSERTED "CYAN"%d"YELLOW" NODES.\n", iterations,insertions);
		printf(YELLOW"Test took "MAGENTA"%d ms."YELLOW" ("MAGENTA"%.4lf us"YELLOW" per inserted node) \n", t2 - t1, (double)(t2 - t1) / (iterations * insertions) * 1000);
		printf(YELLOW"FAILED TOTAL OF "CYAN"%d"YELLOW" NODES ("CYAN"%d IN EACH TEST"YELLOW" - DUPLICIT VALUES).\n\n" RESET, failed, failed/iterations);
	}
	else {
		printf("%lf\t", (double)(t2 - t1) / (iterations * insertions) * 1000);
	}
}

void testRbSearch(int iterations, int searches, rbt_tree_t* rb, int* pole) {
	
	int failed = 0;
	clock_t t1 = clock();
	for (int j = 0; j < iterations; j++) {
		for (int i = 0; i < searches; i++) {

			if (rbt_search(rb, pole[i]) == NULL)
				failed++;
		}
	}
	clock_t t2 = clock();

	if (NICEDEBUG) {
		printf(YELLOW"-----------RB TREE SEARCH TEST RESULTS-----------\nMADE "CYAN"%d"YELLOW" TESTS. \nIN EACH TEST SEARCHED "CYAN"%d"YELLOW" NODES.\n", iterations, searches);
		printf(YELLOW"Test took "MAGENTA"%d ms."YELLOW" ("MAGENTA"%.4lf us"YELLOW" per inserted node) \n", t2 - t1, (double)(t2 - t1) / (iterations * searches) * 1000);
		printf(YELLOW"FAILED TOTAL OF "CYAN"%d"YELLOW" NODES ("CYAN"%d IN EACH TEST"YELLOW" - DUPLICIT VALUES).\n\n" RESET, failed, failed / iterations);

	}
	else {
		printf("%lf\t", (double)(t2 - t1) / (iterations * searches) * 1000);
	}
}

void testMyHashInsert(int iterations, int insertions, int* pole) {
	HASHTABLE** rootArray = malloc(iterations * sizeof(HASHTABLE*));

	int failed = 0;
	clock_t t1 = clock();
	for (int j = 0; j < iterations; j++) {
		rootArray[j] = newTable();
		for (int i = 0; i < insertions; i++) {
			if (!insert_LL(&rootArray[j], pole[i]))
				failed++;
				
		}
	}
	clock_t t2 = clock();

	for (int j = 0; j < iterations; j++) {
		removeMyHT(rootArray[j]);
	}
	free(rootArray);

	if (NICEDEBUG) {
		printf(YELLOW"-----------MY HASH TABLE (CHAINING) INSERTION TEST RESULTS-----------\nMADE "CYAN"%d"YELLOW" TESTS. \nIN EACH TEST INSERTED "CYAN"%d"YELLOW" NODES.\n", iterations, insertions);
		printf(YELLOW"Test took "MAGENTA"%d ms."YELLOW" ("MAGENTA"%.4lf us"YELLOW" per inserted node) \n", t2 - t1, (double)(t2 - t1) / (insertions * iterations) * 1000);
		//printf("TOTAL OF "BLUE"%u"YELLOW" COLLISIONS FOUND ("BLUE"%.4lf per inserted entry"YELLOW").\n"RESET, collisions, collisions / (double)insertions);
		printf(YELLOW"FAILED TOTAL OF "CYAN"%d"YELLOW" NODES ("CYAN"%d IN EACH TEST"YELLOW" - DUPLICIT VALUES).\n\n" RESET, failed, failed / iterations);
	} else {
		printf("%lf\t", (double)(t2 - t1) / (iterations * insertions) * 1000);
	}
}

void testMyHashSearch(int iterations, int searches, HASHTABLE* table, int* pole) {

	clock_t t1 = clock();
	for (int j = 0; j < iterations; j++) {
		for (int i = 0; i < searches; i++) {
			search_LL(table, pole[i]);
		}
	}
	clock_t t2 = clock();
	if (NICEDEBUG) {
		printf(YELLOW"-----------MY HASH TABLE (CHAINING) SEARCH TEST RESULTS-----------\nMADE "CYAN"%d"YELLOW" TESTS. \nIN EACH TEST SEARCHED "CYAN"%d"YELLOW" NODES.\n", iterations, searches);
		printf(YELLOW"Test took "MAGENTA"%d ms."YELLOW" ("MAGENTA"%.4lf us"YELLOW" per inserted node) \n\n", t2 - t1, (double)(t2 - t1) / (iterations * searches) * 1000);
	}
	else {
		printf("%lf\t", (double)(t2 - t1) / (iterations * searches) * 1000);
	}
}

void testProbingHashInsert(int iterations, int insertions, int* pole) {

	HashMap** rootArray = malloc(iterations * sizeof(HashMap*));

	int failed = 0;
	clock_t t1 = clock();
	for (int j = 0; j < iterations; j++) {
		rootArray[j] = new_hashmap();
		for (int i = 0; i < insertions; i++) {
			if (hashmap_insert(rootArray[j], pole[i], pole[i])) {
				//printf("Error on inserting %d:%d\n", i, i);
				failed++;
			}
		}
	}
	clock_t t2 = clock();

	for (int j = 0; j < iterations; j++) {
		destroy_hashmap(rootArray[j]);
	}
	free(rootArray);

	if (NICEDEBUG) {
		printf(YELLOW"-----------HASH TABLE (LINEAR PROBING) INSERTION TEST RESULTS-----------\nMADE "CYAN"%d"YELLOW" TESTS. \nIN EACH TEST INSERTED "CYAN"%d"YELLOW" NODES.\n", iterations, insertions);
		printf(YELLOW"Test took "MAGENTA"%d ms."YELLOW" ("MAGENTA"%.4lf us"YELLOW" per inserted node) \n", t2 - t1, (double)(t2 - t1) / (insertions * iterations) * 1000);
		//printf("TOTAL OF "BLUE"%u"YELLOW" COLLISIONS FOUND ("BLUE"%.4lf per inserted entry"YELLOW").\n"RESET, collisions, collisions / (double)insertions);
		printf(YELLOW"FAILED TOTAL OF "CYAN"%d"YELLOW" NODES ("CYAN"%d IN EACH TEST"YELLOW" - DUPLICIT VALUES).\n\n" RESET, failed, failed / iterations);
	} else {
		printf("%lf\t", (double)(t2 - t1) / (iterations * insertions) * 1000);
	}
}

void testProbingSearch(int iterations, int searches, HashMap* table, int* pole) {

	
	int res;
	clock_t tttt1 = clock();
	for (int j = 0; j < iterations; j++) {
		for (int i = 0; i < searches; i++) {
			hashmap_get(table, pole[i], &res);
		}
	}
	clock_t tttt2 = clock();
	if (NICEDEBUG) {
		printf(YELLOW"-----------HASH TABLE (LINEAR PROBING) SEARCH TEST RESULTS-----------\nMADE "CYAN"%d"YELLOW" TESTS. \nIN EACH TEST SEARCHED "CYAN"%d"YELLOW" NODES.\n", iterations, searches);
		printf(YELLOW"Test took "MAGENTA"%d ms."YELLOW" ("MAGENTA"%.4lf us"YELLOW" per inserted node) \n", tttt2 - tttt1, ((double)(tttt2 - tttt1) / (searches * iterations)) * 1000);
	} else
		printf("%lf\t",((double)(tttt2 - tttt1) / (searches * iterations)) * 1000);
}

void testerInsertions(int iterations, unsigned int insertions, int orderCase) {
	if (SHOWTESTCOUNTCONSOLE) {
		printFormattedNumber(insertions);
		printf("\t");
	}
	/* orderCase:
	1 - asc insert
	2 - desc insert
	3 - random insert 0-1000M with duplicit values 
	4 - random insert without duplicit values
	*/
	int* pole = (int*)malloc(insertions * sizeof(int));

	for (unsigned int j = 0; j < insertions; j++) {

		switch (orderCase){
		case 1:
		case 4:
			pole[j] = j;
			break;
		case 2: 
			pole[j] = insertions - j;
			break;
		case 3:
			pole[j] = (rand() & 0x7FFF);
			pole[j] <<= 15;
			pole[j] = pole[j] | (rand() & 0x7FFF);
			break;

		default:
			pole[j] = j;
			break;
		}
	}

	if (orderCase == 4)
		shuffle(pole,insertions);

	testSplayInsert(iterations,insertions,pole);
	
	testRbInsert(iterations,insertions,pole);
	
	testMyHashInsert(iterations, insertions, pole);
	
	testProbingHashInsert(iterations, insertions, pole);
	printf("\n");

	free(pole);

}

void testerSearch(int iterations, int searches, int orderCase, double searchLast) {
	if (SHOWTESTCOUNTCONSOLE) {
		printFormattedNumber(searches);
		printf("\t");
	}
	/* orderCase:	1 - asc insert - asc search
					2 - asc insert - desc search
					3 - desc insert - asc search
					4 - desc insert - desc search
					5 - asc insert - ZigZag search (1, n, 2, n-1, 3, n-2, ...)
					6 - random insert 0-1000M with duplicit values - random search
					7 - random insert without duplicit values - random search
					8 - random insert without duplicit values - search [searchLast]% last elements
	*/

	/////////////////////// INSERTED ARRAY ////////////////////////////////
	unsigned int* pole = (unsigned int*)malloc(searches * sizeof(unsigned int));
	for (unsigned int j = 0; j < searches; j++) {

		switch (orderCase) {
			//asc
		case 1:
		case 2:
		case 5:
		case 7:
		case 8:
			pole[j] = j;
			break;
			//desc
		case 3:
		case 4:
			pole[j] = searches - j;
			break;
			//rand <0 - 1000M> with duplicit values
		case 6:
			pole[j] = (rand() & 0x7FFF);
			pole[j] <<= 15;
			pole[j] |= (rand() & 0x7FFF);
			break;
		default:
			pole[j] = j;
			break;
		}
	}

	if (orderCase == 7 || orderCase == 8)
		shuffle(pole, searches);


	/////////////////////// SEARCH ARRAY /////////////////////////////////
	unsigned int* pole2 = (unsigned int*)malloc(searches * sizeof(unsigned int));
	for (unsigned int j = 0; j < searches; j++) {
		//pole2[j] = pole[insertions-1-(j%(((insertions/10000)==0)?1:(insertions/10000)))];
		int indexLast;
		switch (orderCase) {

			//asc
		case 1:
		case 4:
		case 6:
		case 7:
			pole2[j] = pole[j];
			break;

			//desc
		case 2:
		case 3:
			pole2[j] = pole[searches - 1 -j];
			break;

			//zigzag
		case 5:
			pole2[j] = pole[j % 2 ? j/2 : (searches - 1 - j/2)];
			break;

			//search [lastElement]% elements
		case 8:
			indexLast = (int)(pole[j] % ((int)(searches * (searchLast / 100)) ?  (int)(searches * (searchLast / 100)) : 1));
			pole2[j] = pole[searches - 1 - indexLast];
			break;

		default:
			pole2[j] = pole[j];
			break;
		}
	}

	if (orderCase == 6 || orderCase == 7) 
		shuffle(pole2, searches);


	//////////////////////// SPLAY SEARCH TEST ////////////////////////////
	SPLAYNODE** rootArray = calloc(iterations,sizeof(SPLAYNODE*));
	for (int j = 0; j < iterations; j++) {
		for (int i = 0; i < searches; i++) {
			insertSplay(pole[i], &rootArray[j]);
		}
	}

	clock_t t11 = clock();
	for (int j = 0; j < iterations; j++) {
		for (int i = 0; i < searches; i++) {
			searchSplay(pole2[i], &rootArray[j]);
		}
	}
	clock_t t22 = clock();

	if (NICEDEBUG) {
		printf(YELLOW"-----------SPLAY TREE SEARCH TEST RESULTS-----------\nMADE "CYAN"%d"YELLOW" TESTS. \nIN EACH TEST SEARCHED "CYAN"%d"YELLOW" NODES.\n", iterations, searches);
		printf(YELLOW"Test took "MAGENTA"%d ms."YELLOW" ("MAGENTA"%.4lf us"YELLOW" per searched node) \n", t22 - t11, (double)(t22 - t11) / (iterations * searches) * 1000);
	}
	else {
		printf("%lf\t", (double)(t22 - t11) / (iterations * searches) * 1000);
	}

	for (int j = 0; j < iterations; j++) {
		removesplaytree(rootArray[j]);
	}
	free(rootArray);


	rbt_tree_t* rb = rbt_create();
	HASHTABLE* myHT = newTable();
	HashMap* htMap = new_hashmap();


	/////////////////////////// RED BLACK SEARCH TEST /////////////////////////////////////

	for (int i = 0; i < searches; i++) {
		rbt_tree_node_t* n = malloc(sizeof(rbt_tree_node_t));
		n->key = pole[i];
		rbt_insert(rb, n);
	}
	testRbSearch(iterations, searches, rb, pole2);
	freeRB(rb->root);


	/////////////////////////// HT CHAINING SEARCH TEST /////////////////////////////////////

	for (int i = 0; i < searches; i++) {
		insert_LL(&myHT, pole[i]);
	}
	testMyHashSearch(iterations, searches, myHT, pole2);
	removeMyHT(myHT);


	/////////////////////////// HT LINEAR PROBING SEARCH TEST /////////////////////////////////////

	for (int i = 0; i < searches; i++) {
		hashmap_insert(htMap, pole[i], pole[i]);
	}
	testProbingSearch(iterations, searches, htMap, pole2);
	destroy_hashmap(htMap);

	free(pole);
	free(pole2);
	printf("\n");
}

void testSearchAll(int caseType, double searchLastPercentage) {
	if (SHOWTESTCOUNTCONSOLE) printf("\t");
	if (!NICEDEBUG) printf("SPLAY\t\tRED-BLACK\tHT CHAINING\tHT LINEAR PROBING\n");
	//printf("%d %lf\n",caseType,searchLastPercentage);

	testerSearch(500000, 10, caseType, searchLastPercentage);	// 500 000 testov, v kazdom         10 searchov, search podla caseType a searchLastPercentage
	testerSearch(10000, 100, caseType, searchLastPercentage);	//	10 000 testov, v kazdom        100 searchov, search podla caseType a searchLastPercentage
	testerSearch(5000, 500, caseType, searchLastPercentage);	//	 5 000 testov, v kazdom        500 searchov, search podla caseType a searchLastPercentage
	testerSearch(1000, 1000, caseType, searchLastPercentage);	//    1000 testov, v kazdom      1 000 searchov, search podla caseType a searchLastPercentage
	testerSearch(1000, 5000, caseType, searchLastPercentage);	//    1000 testov, v kazdom      5 000 searchov, search podla caseType a searchLastPercentage
	testerSearch(100, 10000, caseType, searchLastPercentage);	//	   100 testov, v kazdom     10 000 searchov, search podla caseType a searchLastPercentage
	testerSearch(10, 100000, caseType, searchLastPercentage);	//	    10 testov, v kazdom    100 000 searchov, search podla caseType a searchLastPercentage
	testerSearch(5, 1000000, caseType, searchLastPercentage);	//		 5 testov, v kazdom  1 000 000 searchov, search podla caseType a searchLastPercentage
	testerSearch(1, 10000000, caseType, searchLastPercentage);	//		 1 testov, v kazdom 10 000 000 searchov, search podla caseType a searchLastPercentage
}

void testInsertAll(int orderCase) {
	if (SHOWTESTCOUNTCONSOLE) printf("\t");
	if (!NICEDEBUG) printf("SPLAY\t\tRED-BLACK\tHT CHAINING\tHT LINEAR PROBING\n");
	testerInsertions(100000,10, orderCase);			// 100 000 testov, v kazdom 10 insertov
	testerInsertions(10000, 100, orderCase);		// 10 000 testov, v kazdom 100 insertov
	testerInsertions(1000, 500, orderCase);			// 1 000 testov, v kazdom 500 insertov
	testerInsertions(1000, 1000, orderCase);		// 1 000 testov, v kazdom 1000 insertov
	testerInsertions(100, 5000, orderCase);			// 100 testov, v kazdom 5000 insertov
	testerInsertions(100, 10000, orderCase);		// 100 testov, v kazdom 10 000 insertov
	testerInsertions(20, 100000, orderCase);		// 20 testov, v kazdom 100 000 insertov
	testerInsertions(5, 1000000, orderCase);		// 5 testov, v kazdom 1 000 000 insertov
	testerInsertions(1, 10000000, orderCase);		// 1 test, v kazdom 10 000 000 insertov
	printf("\n");
}

int main(int argc, char* argv[]) {

	srand(time(NULL));
	//printf("%d\n",argc);

	//EXTERNE VOLANIE Z COMMAND LINE
	if (argc >= 2) {
		
		if (!strcmp(argv[1],"insert"))
		{
			/* orderCase:
				1 - asc insert
				2 - desc insert
				3 - random insert 0-1000M with duplicit values
				4 - random insert without duplicit values (shuffled)
			*/
			if (argc == 3)
				testInsertAll((int)(argv[2][0] - 48));
		}
		else if (!strcmp(argv[1], "search")) {

			/* orderCase:	
				1 - asc insert - asc search
				2 - asc insert - desc search
				3 - desc insert - asc search
				4 - desc insert - desc search
				5 - asc insert - ZigZag search
				6 - random insert with duplicit values - random search
				7 - random insert without duplicit values - random search
				8 - random insert without duplicit values - search [searchLast]% last elements
*/			if(argc == 4)
				testSearchAll((int)(argv[2][0]-48),atof(argv[3]));
		}
	}
	else {
		//VOLANIE Z VYVOJOVEHO PROSTREDIA
		int casetype,caseIns,caseSearch; double lastPercentage;

		while (1) {
			printf(YELLOW "Zadajte cislo:\n");
			printf(CYAN "1. TEST INSERT\n");
			printf( "2. TEST SEARCH\n");
			printf( "3. EXIT\n"RESET);
			if (scanf("%d", &casetype) > 0) {
				switch (casetype) {
				case 1:
					printf(YELLOW "Zadajte typ testu:\n");
					printf(CYAN "1 - asc insert\n2 - desc insert\n3 - random insert 0 - 1000M with duplicit values\n4 - random insert 0 - N without duplicit values(shuffled)\n"RESET);
					if (scanf("%d", &caseIns) > 0) {
						testInsertAll(caseIns);
					}
					break;
				case 2:
					printf(YELLOW "Zadajte typ testu:\n");
					printf(CYAN"1 - asc insert - asc search\n2 - asc insert - desc search\n3 - desc insert - asc search\n4 - desc insert - desc search\n5 - asc insert - ZigZag search\n6 - random insert with duplicit values - random search\n7 - random insert without duplicit values - random search\n8 - random insert without duplicit values - search[searchLast] %% last elements\n"RESET);
					if (scanf("%d", &caseSearch) > 0) {
						if (caseSearch == 8) {
							printf(YELLOW "Zadajte kolko percent posledne vlozenych prvkov mam hladat:\n"RESET);
							scanf("%lf",&lastPercentage);
							testSearchAll(caseSearch,lastPercentage);
						}
						else {
							testSearchAll(caseSearch,100.0);
						}

					}
					break;
				case 3:
					exit(0);
				default:
					break;
				}
			}

		}
		


	}
	return 0;
}