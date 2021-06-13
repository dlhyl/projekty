#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define DEBUG 0
#define INF 0xffffffff

#define RED "\x1b[31m" 
#define GREEN "\x1b[32m"
#define YELLOW "\x1b[33m"
#define BLUE "\x1b[34m"
#define MAGENTA "\x1b[35m"
#define CYAN "\x1b[36m"
#define RESET "\x1b[0m"

typedef struct node {
	unsigned short x;
	unsigned short y;
	unsigned int cost;
	char typ;
	struct node* prev;
}NODE;

typedef struct myHeap {
	int kapacita;
	int pocetPrvkov;
	NODE** array;
}HALDA;

typedef struct princezneCesty {
	int** arr;
	int* dlzka;
	int* cost;
}princezneCesty;

void heapFromBottom(HALDA* h, int Index);

HALDA* initHeap(int capacity) {
	HALDA* newHeap = (HALDA*)malloc(sizeof(HALDA));

	newHeap->kapacita = capacity;
	newHeap->pocetPrvkov = 0;
	newHeap->array = (NODE**)malloc(capacity * sizeof(NODE*));

	return newHeap;
}

NODE* createNode(unsigned short x, unsigned short y, unsigned int cost, char typ, NODE* prev) {
	NODE* newNode = (NODE*)malloc(sizeof(NODE));
	newNode->cost = cost;
	newNode->x = x;
	newNode->y = y;
	newNode->typ = typ;
	newNode->prev = prev;
	return newNode;
}

void insertToHeap(HALDA* heap, NODE* node) {
	
	// insertne prvok na koniec haldy a pomocou swapov najde pre prvok vhodne miesto
	if (heap->pocetPrvkov < heap->kapacita) {
		heap->array[heap->pocetPrvkov] = node;
		heapFromBottom(heap,heap->pocetPrvkov);
		heap->pocetPrvkov++;
	}
}

void heapFromBottom(HALDA* heap, int Index) {
	NODE* temp_node;
	int rodic_index = (Index - 1) / 2;

	// pokial ma node mensi alebo rovny cost ako jeho rodic, urobi swap
	if (heap->array[rodic_index]->cost > heap->array[Index]->cost) {

		//swap rodic a dieta
		temp_node = heap->array[rodic_index];
		heap->array[rodic_index] = heap->array[Index];
		heap->array[Index] = temp_node;

		//heapify z rodica
		heapFromBottom(heap, rodic_index);
	}
}

void heapFromTop(HALDA* h, int parentIndex) {
	int left_child_index = parentIndex * 2 + 1;
	int right_child_index = parentIndex * 2 + 2;
	NODE* temp_node;
	int min_index;


	// najde minimum z oboch deti, ak neexistuju tak min == parentIndex
	if (left_child_index < h->pocetPrvkov && h->array[left_child_index]->cost < h->array[parentIndex]->cost)
		min_index = left_child_index;
	else
		min_index = parentIndex;

	if (right_child_index < h->pocetPrvkov && h->array[right_child_index]->cost < h->array[min_index]->cost)
		min_index = right_child_index;

	// spravi swap rodica s mensim dietatom a zavola heapify z minimalneho dietata
	if (min_index != parentIndex) {

		temp_node = h->array[min_index];
		h->array[min_index] = h->array[parentIndex];
		h->array[parentIndex] = temp_node;

		heapFromTop(h, min_index);
	}
}

NODE* getMinFromHeap(HALDA* heap) {
	NODE* min_node;

	min_node = heap->array[0];
	heap->array[0] = heap->array[heap->pocetPrvkov - 1];
	heap->pocetPrvkov -= 1;
	heapFromTop(heap, 0);

	return min_node;
}

void freeHeap(HALDA* h) {
	free(h->array);
	free(h);
}

unsigned int getCostOfPlace(char c) {
	switch (c){
		case 'C':
		case 'D':
		case 'P':
			return 1;
			break;
		case 'H':
			return 2;
			break;
		case 'N':
			return INF;
			break;
	}
}

void mergePaths(int** arr1, int len1, int* arr2, int len2) {
	*arr1 = realloc(*arr1,(len1 + len2) * sizeof(int));

	for (int i = len1; i < len1 + len2; i++) {
		(*arr1)[i] = arr2[i - len1];
	}

	free(arr2);
}

void vytvorCestuZNode_Reverse(NODE* node, int** arr, int* index, int count) {
	if (node == NULL) {
		*arr = malloc(2 * count * sizeof(int));
		*index = count;
		return;
	}
	vytvorCestuZNode_Reverse(node->prev, arr, index, count + 1);
	arr[0][(count)*2] = node->x;
	arr[0][(count)*2+1] = node->y;

}

void vytvorCestuZNode(NODE* node, int** arr, int* index, int count) {
	if (node == NULL) {
		*arr = malloc(2*count*sizeof(int));
		*index = 0;
		return;
	}
	vytvorCestuZNode(node->prev, arr, index, count + 1);
	arr[0][2*(*index)] = node->x;
	arr[0][2*(*index) + 1] = node->y;
	(*index) += 1;
}

void freeNodeMap(NODE*** map, int n, int m) {
	for (int i = 0; i < n;i++) {
		for (int j = 0; j < m;j++) {
			free(map[i][j]);
		}
		free(map[i]);
	}
	free(map);
}

void freePrincezneStruct(princezneCesty** princezne, int pocetPr) {
	for (int i = 0; i < pocetPr; i++) {
		for (int j = 0; j < pocetPr+1; j++) {
			free(princezne[i]->arr[j]);
		}
		free(princezne[i]->arr);
		free(princezne[i]->cost);
		free(princezne[i]->dlzka);
		free(princezne[i]);
	}
	free(princezne);
}

void printArrayCoordinates(int* arr, int pocet) {
	for (int i = 0; i < pocet; i++) {
		printf("[%d, %d] ", arr[2 * i], arr[2 * i + 1]);
		if (i != pocet - 1) printf("-> ");
	}
}

void printarray(int arr[], int size)
{
	int i, j;
	for (i = 0; i < size; i++)
	{
		printf("%d\t", arr[i]);
	}
	printf("\n");
}

void swap(int* a, int* b)
{
	int* temp;
	temp = *a;
	*a = *b;
	*b = temp;
}

void shuffle(char* array, int n)
{
	if (n > 1)
	{
		for (int i = 0; i < n - 1; i++)
		{
			int j = i + rand() / (RAND_MAX / (n - i) + 1);
			char t = array[j];
			array[j] = array[i];
			array[i] = t;
		}
	}
}

void permutation(princezneCesty** prArr,int* arr, int start, int end, int** minArr, unsigned int* minCost)
{
	if (start == end)
	{
		// najprv je priradena cesta Drak -> princezna
		int sum = prArr[arr[0]]->cost[0]; 
		for (int i = 1; i <= end; i++) {
			// potom su pripocitane cesty Princezna -> Princezna
			sum += prArr[arr[i-1]]->cost[arr[i] + 1];
		}

		if (sum < (*minCost))
		{
			*minCost = sum;
			for (int i = 0; i <= end; i++) {
				minArr[0][i] = arr[i];
			}
		}

		return;
	}
	int i;
	for (i = start; i <= end; i++)
	{
		swap(&arr[i], &arr[start]);	
		permutation(prArr,arr, start + 1, end, minArr, minCost);
		swap(&arr[i], &arr[start]);
	}
}

void printCharMap(char** map, int n, int m) {
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			switch (map[i][j]) {
			case 'D':
				printf(RED);
				break;
			case 'P':
				printf(MAGENTA);
				break;
			case 'C':
				printf(GREEN);
				break;
			case 'H':
				printf(CYAN);
				break;
			case 'N':
				break;
			}
			printf("%1c", map[i][j]);
			printf(RESET);
		}
		printf("\n");
	}
}

void printPathMap(NODE*** mapaNodov, NODE* node, int n, int m) {
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			switch (mapaNodov[i][j]->typ) {
			case 'D':
				printf(RED);
				break;
			case 'P':
				printf(MAGENTA);
				break;
			case 'C':
				printf(GREEN);
				break;
			case 'H':
				printf(CYAN);
				break;
			case 'N':
				break;
			}
			if (mapaNodov[i][j] == node)
				printf("[%d]", mapaNodov[i][j]->cost == INF ? -1 : mapaNodov[i][j]->cost);
			else
				printf("%2d ", mapaNodov[i][j]->cost == INF ? -1 : mapaNodov[i][j]->cost);
			printf(RESET);
		}
		printf("\n");
	}
}

NODE*** initPathMap(char** map, int n, int m, int* pocetPrincezien, NODE* princezny[5], NODE** Drak) {
	NODE*** pathMap = (NODE***)malloc(n * sizeof(NODE**));

	for (int i = 0; i < n;i++) {
		pathMap[i] = (NODE**)malloc(m*sizeof(NODE*));
		for (int j = 0; j < m; j++) {
			pathMap[i][j] = (NODE*)malloc(sizeof(NODE));
			pathMap[i][j]->x = j;
			pathMap[i][j]->y = i;
			pathMap[i][j]->cost = INF;
			pathMap[i][j]->prev = NULL;
			pathMap[i][j]->typ = map[i][j];

			if (pathMap[i][j]->typ == 'D') {
				Drak[0] = pathMap[i][j];
			}
			if (pathMap[i][j]->typ == 'P') {
				princezny[(*pocetPrincezien)] = pathMap[i][j];
				(*pocetPrincezien)++;
			}
		}
	}
	return pathMap;
}

void resetPathMap(NODE*** pathMap, int n, int m) {
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			pathMap[i][j]->cost = INF;
			pathMap[i][j]->prev = NULL;
		}
	}
}

char** generateMap(int height, int width, int numOfPrincesses, float chodnikProb, float hustyChodnikProb, float prekazkaProb ) {

	if (chodnikProb + hustyChodnikProb + prekazkaProb != (float)100) {
		printf("Sucet pravdepodobnosti vyskytu musi byt 100.\n");
		return NULL;
	}
	
	int chodnikThreshold = (int)((chodnikProb/100) * (1 << 15));
	int hustyChodnikThreshold = (int)(chodnikThreshold + (hustyChodnikProb/100) * (1 << 15));

	//printf(RED"%d %d\n"RESET,chodnikThreshold,hustyChodnikThreshold);

	char** mapka = (char**)malloc(height*sizeof(char*));
	for (int i = 0; i < height;i++) {
		mapka[i] = (char*)malloc(width * sizeof(char));
	}

	char* pomocnaMapka1D = (char*)malloc(width*height*sizeof(char));
	pomocnaMapka1D[0] = 'D';
	for (int i = 0; i < numOfPrincesses;i++) {
		pomocnaMapka1D[i + 1] = 'P';
	}

	for (int i = numOfPrincesses+1; i < width * height;i++) {
		int randomNumber = rand();

		if (randomNumber < chodnikThreshold)
			pomocnaMapka1D[i] = 'C';
		else if (randomNumber < hustyChodnikThreshold)
			pomocnaMapka1D[i] = 'H';
		else pomocnaMapka1D[i] = 'N';
	}

	shuffle(pomocnaMapka1D,width*height);

	for (int i = 0; i < width * height; i++) {
		mapka[i / width][i % width] = pomocnaMapka1D[i];
	}

	free(pomocnaMapka1D);

	return mapka;
}


void searchNeighbour(HALDA* h, NODE* node, NODE* neighbour) {
	if (neighbour->typ != 'N')
	{
		unsigned int newCost = node->cost + getCostOfPlace(neighbour->typ);
		// ak je neprejdeny alebo je prejdeny ale novy cost je mensi, pridame do haldy
		if (neighbour->cost == INF)
		{
			neighbour->prev = node;
			neighbour->cost = newCost;
			insertToHeap(h, neighbour);
		}
		else if (neighbour->cost > node->cost + newCost) {
			printf("NOVY COST\n");
			neighbour->prev = node;
			neighbour->cost = newCost;
			insertToHeap(h, neighbour);
		}
	}
}

void dijkstra(char** map, NODE*** pathMap, int startX, int startY, int pocetDestNodov, int n, int m) {
	
	// reset mapy vrcholov - posluzi na zistenie najlacnejsej cesty z bodu A do bodu B
	resetPathMap(pathMap,n,m);

	// vytvorime haldu
	HALDA* h = initHeap(n * m);

	// startovaci prvok inicializujeme a vlozime do haldy
	pathMap[startY][startX]->cost = getCostOfPlace(map[startY][startX]);

	if (pathMap[startY][startX]->cost == INF)
		return;

	insertToHeap(h, pathMap[startY][startX]);

	while (h->pocetPrvkov) {

		//vytiahne minimum z haldy
		NODE* minNode = getMinFromHeap(h);

		// podmienka na zastavenie Start -> Drak
		if (pocetDestNodov < 0 && minNode->typ == 'D') {
			freeHeap(h);
				return;
		}
		// podmienka na zastavenie Princezna -> {Vsetky princezne + Drak}, musi najst vsetky princezne aj draka
		else if (minNode->typ == 'D' || minNode->typ == 'P') {
			pocetDestNodov--;
			if (!pocetDestNodov) { freeHeap(h); return; }
		}

		//prezrie vsetkych susedov a nepreskumanych alebo lacnejsich da do haldy
		if (minNode->x > 0) { // LAVY SUSED
			searchNeighbour(h,minNode, pathMap[minNode->y][minNode->x-1]);
		}
		if (minNode->y > 0) { // HORNY SUSED
			searchNeighbour(h,minNode, pathMap[minNode->y-1][minNode->x]);
		}
		if (minNode->x < m - 1) { // PRAVY SUSED
			searchNeighbour(h, minNode, pathMap[minNode->y][minNode->x+1]);
		}
		if (minNode->y < n - 1) { // DOLNY SUSED
			searchNeighbour(h, minNode, pathMap[minNode->y + 1][minNode->x]);
		}
	}
	freeHeap(h);
}



int* zachran_princezne(char** mapa, int n, int m, int t, int* dlzka_cesty) {

	int pocetPrincezien=0;
	NODE** princezneNODES = (NODE**)malloc(5*sizeof(NODE*));
	NODE* Drak;
	
	// inicializacia vlastneho grafu
	NODE*** mapaNodov = initPathMap(mapa,n,m,&pocetPrincezien, princezneNODES,&Drak);

	// dijkstra start -> drak
	dijkstra(mapa,mapaNodov,0,0,-1,n,m);
	int* StartDrakCesta;
	int StartDrakPocetVrcholov = 0; 
	int drakCost = mapaNodov[Drak->y][Drak->x]->cost;
	vytvorCestuZNode(Drak, &StartDrakCesta,&StartDrakPocetVrcholov,0);

	if (DEBUG) {
		printf("Mapa najkratsich vzdialenosti zo startovacej pozicie:\n");
		printPathMap(mapaNodov, mapaNodov[0][0], n, m);
		printf("Najkratsia cesta zo startu k drakovi ma %d policok:\n", StartDrakPocetVrcholov);
		printArrayCoordinates(StartDrakCesta, StartDrakPocetVrcholov);
		printf("\n\n");
	}

	// overenie, ci sa da dostat k drakovi
	if (mapaNodov[Drak->y][Drak->x]->cost == INF) {
		printf(RED "Neplatna mapa! Nepodarilo sa zabit Draka!\n"RESET);
		*dlzka_cesty = 0;
		return NULL;
	}

	// overenie, ci je dostatok casu na zabitie draka
	if (mapaNodov[Drak->y][Drak->x]->cost > t) {
		//printf("%d %d\n",mapaNodov[Drak->y][Drak->x]);
		printf(RED"Nestihol si zabit draka!\n"RESET);
		*dlzka_cesty = 0;
		return NULL;
	}


	princezneCesty** princezne = (princezneCesty**)malloc(pocetPrincezien * sizeof(princezneCesty*));
	for (int ix = 0; ix < pocetPrincezien; ix++) {
		princezne[ix] = (princezneCesty*)malloc(sizeof(princezneCesty));
		princezne[ix]->arr = (int**)malloc((pocetPrincezien+1) * sizeof(int*));
		princezne[ix]->cost = (int*)malloc((pocetPrincezien + 1) * sizeof(int));
		princezne[ix]->dlzka = (int*)malloc((pocetPrincezien + 1) * sizeof(int));

		// dijkstra z kazdej princezne -> kazda princezna + drak
		dijkstra(mapa, mapaNodov, princezneNODES[ix]->x, princezneNODES[ix]->y,pocetPrincezien+1, n, m);
		princezne[ix]->cost[0] = mapaNodov[Drak->y][Drak->x]->cost;

		//vytvorenie cesty princezna -> drak
		vytvorCestuZNode_Reverse(Drak, &(princezne[ix]->arr[0]), &(princezne[ix]->dlzka[0]), 0);

		// overenie ci sa da dostat k princeznam/drakovi
		for (int ii = 0; ii < pocetPrincezien; ii++)
		{
			//printf("%d ", mapaNodov[princezneNODES[ii]->y][princezneNODES[ii]->x]->cost == INF);
			if (mapaNodov[princezneNODES[ii]->y][princezneNODES[ii]->x]->cost == INF) {
				printf(RED"Neplatna mapa! Nepodarilo sa zachranit princeznu!\n"RESET);
				*dlzka_cesty = 0;
				return NULL;
			}
		}
		//printf("\n");

		if (DEBUG) {
			printf("\nMapa najkratsich vzdialenosti z princeznej %d:\n", ix + 1);
			printPathMap(mapaNodov, princezneNODES[ix], n, m);
			printf("L %2d C %2d : ", princezne[ix]->dlzka[0], princezne[ix]->cost[0]);
			printArrayCoordinates(princezne[ix]->arr[0], princezne[ix]->dlzka[0]);
			printf("\n");
		}

		// vytvorenie cesty medzi princeznami
		for (int jj = 0; jj < pocetPrincezien; jj++) {
			princezne[ix]->cost[jj+1] = mapaNodov[princezneNODES[jj]->y][princezneNODES[jj]->x]->cost;
			vytvorCestuZNode(princezneNODES[jj], &(princezne[ix]->arr[jj+1]), &(princezne[ix]->dlzka[jj+1]), 0);

			if (DEBUG) {
				printf("L %2d C %2d : ", princezne[ix]->dlzka[jj + 1], princezne[ix]->cost[jj + 1]);
				printArrayCoordinates(princezne[ix]->arr[jj + 1], princezne[ix]->dlzka[jj + 1]);
				printf("\n");
			}
		}
	}
	
	// permutacie na zistenie najlacnejsej moznosti
	int* prArr = malloc(pocetPrincezien * sizeof(int));
	for (int i = 0; i < pocetPrincezien;i++) {
		prArr[i] = i;
	}
	int* lowestArr = calloc(pocetPrincezien, sizeof(int));
	int lowestCost = INF;
	permutation(princezne,prArr,0,pocetPrincezien-1,&lowestArr,&lowestCost);

	if (DEBUG) {
		printf("LC : %d\n", lowestCost);
		printf("%d %d %d %d %d\n", lowestArr[0], lowestArr[1], lowestArr[2], lowestArr[3], lowestArr[4]);
		printf("\n\n");
	}

	// vytvorenie finalnej cesty
	int finalArrayLength = StartDrakPocetVrcholov;
	finalArrayLength += princezne[lowestArr[0]]->dlzka[0];
	for (int i = 1; i < pocetPrincezien; i++) {
		finalArrayLength += princezne[lowestArr[i - 1]]->dlzka[lowestArr[i] + 1];
		//finalArrayLength += princezne[lowestArr[i]]->dlzka[i];
	}
	finalArrayLength -= pocetPrincezien;

	
	int* finalArray = malloc(finalArrayLength*2*sizeof(int));
	int i,j;

	// start -> drak
	for (i = 0; i < StartDrakPocetVrcholov*2;i++) {
		finalArray[i] = StartDrakCesta[i];
	}

	// drak -> princezna
	for ( j = 0; j < princezne[lowestArr[0]]->dlzka[0]*2; j++,i++) {
		finalArray[i-2] = princezne[lowestArr[0]]->arr[0][j];
	}

	// princezne -> princezne
	for (j = 1; j < pocetPrincezien; j++) {
		for (int k = 2; k < princezne[lowestArr[j - 1]]->dlzka[lowestArr[j] + 1]*2; k++,i++) {
			finalArray[i-2]= princezne[lowestArr[j - 1]]->arr[lowestArr[j] + 1][k];
		}

	}

	lowestCost += drakCost-pocetPrincezien;
	
	if (DEBUG) {
		printf("\n");
		printf(RED"DLZKA ARRAYU: %d\tCAS: %d\n"RESET, finalArrayLength, lowestCost);
		printArrayCoordinates(finalArray, finalArrayLength);
		printf("\n");
	}

	freeNodeMap(mapaNodov, n, m);
	freePrincezneStruct(princezne, pocetPrincezien);
	free(princezneNODES);
	free(StartDrakCesta);

	*dlzka_cesty = finalArrayLength;
	return finalArray;

}

int main()
{
	srand(time(NULL));
	char** mapa;
	int i, test, dlzka_cesty, cas, * cesta;
	int n = 0, m = 0, t = 0, princess;
	float cP, hcP, nP;
	FILE* f;
	while (1) {
		printf(YELLOW"Zadajte cislo testu:\n0. ukonci program\n1. nacitanie mapy zo suboru\n2. preddefinovana mapa\n3. vlastna mapa\n4. generator mapy\n"RESET);
		scanf("%d", &test);
		dlzka_cesty = 0;
		n = m = t = 0;
		switch (test) {
		case 0://ukonci program
			return 0;
		case 1://nacitanie mapy zo suboru
			f = fopen("test8.txt", "r");
			if (f)
				fscanf(f, "%d %d %d", &n, &m, &t);
			else {
				printf("NO FILE!\n");
				continue;
			}
			mapa = (char**)malloc(n * sizeof(char*));
			for (i = 0; i < n; i++) {
				mapa[i] = (char*)malloc(m * sizeof(char));
				for (int j = 0; j < m; j++) {
					char policko = fgetc(f);
					if (policko == '\n') policko = fgetc(f);
					mapa[i][j] = policko;
				}
			}
			fclose(f);
			cesta = zachran_princezne(mapa, n, m, t, &dlzka_cesty);
			break;
		case 2://nacitanie preddefinovanej mapy
			n = 10;
			m = 10;
			t = 12;
			mapa = (char**)malloc(n * sizeof(char*));
			mapa[0] = "CCHCNHCCHN";
			mapa[1] = "NNCCCHHCCC";
			mapa[2] = "DNCCNNHHHC";
			mapa[3] = "CHHHCCCCCC";
			mapa[4] = "CCCCCNHHHH";
			mapa[5] = "PCHCCCNNNN";
			mapa[6] = "NNNNNHCCCC";
			mapa[7] = "CCCCCPCCCC";
			mapa[8] = "CCCNNHHHHH";
			mapa[9] = "HHHPCCCCCC";
			cesta = zachran_princezne(mapa, n, m, t, &dlzka_cesty);
			break;
		case 3: //pridajte vlastne testovacie vzorky
			n = 10;
			m = 10;
			t = 40;
			mapa = (char**)malloc(n * sizeof(char*));
			mapa[0] = "CCHNCDNPNH";
			mapa[1] = "NCCNCCCCCH";
			mapa[2] = "HCNCCHNNCH";
			mapa[3] = "PCHNCHCCCC";
			mapa[4] = "CCCNCCCPHC";
			mapa[5] = "CCNCCNNHCC";
			mapa[6] = "HCCPCCCNCN";
			mapa[7] = "HHCCCHCCHC";
			mapa[8] = "CCCHHCCNPC";
			mapa[9] = "CCCCCCCHNC";
			cesta = zachran_princezne(mapa, n, m, t, &dlzka_cesty);
			break;
		case 4:
			
			printf(CYAN"Zadajte vysku a sirku mapy: "RESET);
			scanf("%d %d",&n,&m);
			printf(CYAN"\nZadajte pocet princezien a pravdepodobnost vyskytu chodnikov, hustych chodnikov a prekazok : "RESET);
			scanf("%d %f %f %f",&princess,&cP,&hcP,&nP);
			mapa = generateMap(n,m,princess,cP,hcP,nP);

			if(mapa != NULL) printCharMap(mapa,n,m);
			printf("\n");
			t = rand() % (n * m);
			printf("t=%d\n",t);
			cesta = zachran_princezne(mapa,n,m,t,&dlzka_cesty);
			break;
		default:
			continue;
		}
		//printf("DLZKA - %d\n",dlzka_cesty);
		cas = 0;
		for (i = 0; i < dlzka_cesty; i++) {
			printf("%d %d\n", cesta[i * 2], cesta[i * 2 + 1]);
			if (mapa[cesta[i * 2 + 1]][cesta[i * 2]] == 'H')
				cas += 2;
			else
				cas += 1;
			if (mapa[cesta[i * 2 + 1]][cesta[i * 2]] == 'D' && cas > t)
				printf("Nestihol si zabit draka!\n");
			if (mapa[cesta[i * 2 + 1]][cesta[i * 2]] == 'N')
				printf("Prechod cez nepriechodnu prekazku!\n");
			if (i > 0 && abs(cesta[i * 2 + 1] - cesta[(i - 1) * 2 + 1]) + abs(cesta[i * 2] - cesta[(i - 1) * 2]) > 1)
				printf("Neplatny posun Popolvara!\n");
		}
		printf("%d\n", cas);
		if (cesta != NULL) free(cesta);
		//for (i = 0; i < n; i++) {
		//	free(mapa[i]);
		//}
		if (mapa!= NULL) free(mapa);
	}
	return 0;
}