/*
TODO:

*/

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MIN_SIZE_PAYLOAD 8
#define MIN_SIZE (MIN_SIZE_PAYLOAD+sizeof(ALOKOVANY_BLOK))
#define DEBUG 0

#define C_RED     "\x1b[31m"
#define C_GREEN   "\x1b[32m"
#define C_YELLOW  "\x1b[33m"
#define C_BLUE    "\x1b[34m"
#define C_MAGENTA "\x1b[35m"
#define C_CYAN    "\x1b[36m"
#define C_RESET   "\x1b[0m"

#define uint unsigned int
#define offsetNaPrvyVolnyBlok ((HLAVICKA_PAMATE*)(HEADER_PTR))->first_freeblock_offset

// hlavicka celkoveho bloku pamate
//	4 byte = velkost spravovanej pamate
//	4 byte = offset na prvy volny blok
typedef struct hlavicka_pamate {
	uint size;
	uint first_freeblock_offset;
} HLAVICKA_PAMATE;

// hlavicka alokovaneho bloku
//	4 byte = velkost alokovaneho bloku
typedef struct alokovany_blok {
	uint size;
} ALOKOVANY_BLOK;

// hlavicka volneho bloku
//	4 byte = velkost volneho bloku
//	4 byte = offset na predosly volny blok
//	4 byte = offset na dalsi volny blok
typedef struct volny_blok {
	uint size;
	uint offset_prev;
	uint offset_next;
} VOLNY_BLOK;

//globalny pointer na zaciatok haldy (celkoveho bloku pamate)
void* HEADER_PTR = NULL;

void* memory_alloc(uint size);
int memory_free(void* valid_ptr);
int memory_check(void* ptr);
void memory_init(void* ptr, uint size);
double test();
double make1000Tests(uint start, uint end, uint memory_size, char* pointer);
void testSingleNumber(uint number, uint memory_size, char* pointer);
void echoMemory();

VOLNY_BLOK* offsetToVolnyBlok(uint offset) {
	return ((VOLNY_BLOK*)( (uint)HEADER_PTR + (uint)(offset) ));
}

VOLNY_BLOK* addressToVolnyBlok(uint address) {
	return ((VOLNY_BLOK*)((uint) address));
}

uint BlokToOffset(void* blok) {
	return (uint)(blok) - (uint)(HEADER_PTR);
}

ALOKOVANY_BLOK* addressToAlokovanyBlok(uint address) {
	return ((ALOKOVANY_BLOK*)((uint)address));
}

void sortList(VOLNY_BLOK* pointer) {
	VOLNY_BLOK* prvyBlok = offsetToVolnyBlok(offsetNaPrvyVolnyBlok);

	//zoznam je prazdny ALEBO alokovany blok je najmensi => blok pojde na zaciatok zoznamu
	if ((uint)prvyBlok == (uint)HEADER_PTR || prvyBlok->size >= pointer->size)
	{
		if ((uint)prvyBlok != (uint)HEADER_PTR)
			prvyBlok->offset_prev = BlokToOffset(pointer);
		pointer->offset_next = BlokToOffset(prvyBlok);
		pointer->offset_prev = 0;
		offsetNaPrvyVolnyBlok = BlokToOffset(pointer);
	}

	//alokovany blok nie je najmensi, prehladavame zoznam na sortnutie podla velkosti
	else {

		while (prvyBlok->offset_next && offsetToVolnyBlok(prvyBlok->offset_next)->size < pointer->size) {
			prvyBlok = offsetToVolnyBlok((uint)prvyBlok->offset_next);
		}

		if (prvyBlok->offset_next)
			offsetToVolnyBlok(prvyBlok->offset_next)->offset_prev = BlokToOffset(pointer);
		pointer->offset_next = prvyBlok->offset_next;
		pointer->offset_prev = BlokToOffset(prvyBlok);
		prvyBlok->offset_next = BlokToOffset(pointer);

	}
}

void removeFromList(VOLNY_BLOK* pointer) {
	//z predchadzajuceho
	if (pointer->offset_prev)
		offsetToVolnyBlok(pointer->offset_prev)->offset_next = pointer->offset_next;

	//zo zaciatku
	else
		offsetNaPrvyVolnyBlok = pointer->offset_next;

	//z nasledujuceho
	if (pointer->offset_next)
		offsetToVolnyBlok(pointer->offset_next)->offset_prev = pointer->offset_prev;
}

int main() {

	srand(time(NULL));

	char region[50000];

	memory_init(region, 100);

	// TEST1 A
	//// BLOKY ROVNAKEJ VELKOSTI 8-24 Bytov v 50B celkovej pamati
	for (int i = 8; i <= 24; i++) {
		testSingleNumber(i, 50, region);
		printf("\n");
	}

	// TEST1 B
	//// BLOKY ROVNAKEJ VELKOSTI 8-24 Bytov v 100B celkovej pamati
	for (int i = 8; i <= 24; i++) {
		testSingleNumber(i, 100, region);
		printf("\n");
	}

	 //TEST1 C
	/// BLOKY ROVNAKEJ VELKOSTI 8-24 Bytov v 200B celkovej pamati
	for (int i = 8; i <= 24; i++) {
		testSingleNumber(i, 200, region);
		printf("\n");
	}

	make1000Tests(8, 5000, 1000, region);

	make1000Tests(8, 5000, 2000, region);

	make1000Tests(8, 5000, 3000, region);

	make1000Tests(8, 5000, 4000, region);

	make1000Tests(8, 5000, 5000, region);

	make1000Tests(8, 5000, 10000, region);

	make1000Tests(1000, 5000, 50000, region);

	return 0;
}


void* memory_alloc(uint size){
	
	VOLNY_BLOK* blok = offsetToVolnyBlok( offsetNaPrvyVolnyBlok );

	// kedze zoradujem volne bloky pri uvolnovani blokov vo funkcii memory_free,
	// vyhladavacou metodou FIRST FIT najdem vzdy najvhodnejsi blok
	// cize prakticky funguje ako BEST FIT

	while ( blok != HEADER_PTR ) {
		
		//	volny blok ma dostatok miesta na alokaciu (hlavicka + pozadovana velkost na data)
		if ((uint)blok->size >= size + sizeof(ALOKOVANY_BLOK)) {
			
			//	blok je dostatocne velky na rozdelenie
			if ((uint)blok->size >= size + sizeof(ALOKOVANY_BLOK) + MIN_SIZE)
			{
	
				VOLNY_BLOK* splitnutyBlok = addressToVolnyBlok( (uint)blok + size + sizeof(ALOKOVANY_BLOK) );
				
				splitnutyBlok->size = blok->size - size - sizeof(ALOKOVANY_BLOK);
				
				blok->size = size+sizeof(ALOKOVANY_BLOK);

				removeFromList(blok);

				sortList(splitnutyBlok);

				if (DEBUG) printf(C_RED "[%d - %d]" C_RESET " Successfully allocated block of %d Bytes. [+4B HEADER]\n", (uint)blok, (uint)blok+blok->size,size);
				
				return (void*)((uint)blok + sizeof(ALOKOVANY_BLOK));
			}

			//	blok nie je dostatocne velky na rozdelenie ( PADDING )
			//	alokuje cely volny blok s paddingom
			else {

				removeFromList(blok);

				if (DEBUG) printf(C_RED "[%d - %d]" C_RESET " Successfully allocated block of %d Bytes. [+4B HEADER +%dB PADDING]\n", (uint)blok, (uint)blok + blok->size, size,blok->size-size-4);

				return (void*)( (uint) blok + sizeof(ALOKOVANY_BLOK));

			}
		}

		//	prejde na dalsi volny blok v zozname
		else if (blok->offset_next != 0) {
			
			blok = offsetToVolnyBlok((uint)((VOLNY_BLOK*)blok)->offset_next);

		}

		//	koniec zoznamu, nemozno alokovat pozadovane velku pamat
		else {

			if (DEBUG) printf(C_YELLOW "Allocation of %d Bytes not successful. /FREE BLOCK FOUND, NOT ENOUGH SPACE/\n" C_RESET, size);
			return NULL;
		}
	}

	if (DEBUG) printf(C_YELLOW "Allocation of %d Bytes not successful. /NO FREE BLOCK FOUND/\n" C_RESET, size);
	return NULL;
}


//	Pri uvolnovani pamate hladam susedov alokovaneho bloku, ak zistim, ze je nejaky sused volny blok
//	tak ho mergujem s alokovanym blokom, potom blok sortujem do spajaneho zoznamu volnych blokov podla velkosti
//	cim zarucim v pridelovani pamate, ze prvy vhodny blok bude aj najlepsi
int memory_free(void* valid_ptr){

	//pointer na zaciatok alokovaneho bloku
	ALOKOVANY_BLOK* blokPtr = addressToAlokovanyBlok((uint)valid_ptr - sizeof(ALOKOVANY_BLOK));

	// EXISTUJE ASPON JEDEN VOLNY BLOK V ZOZNAME
	if ( offsetNaPrvyVolnyBlok ) {

		//skontrolujem ci sa nachadza PRED/ZA alokovanym blokom volny blok
		//ak ano
		//	odstranim ich zo zoznamu
		//	mergnem ich do jedneho velkeho bloku
		//	mergnuty blok sortnem do zoznamu

		//vytvorim si dva pomocne pointre :
		// -posledne slovo bloku pred alokovanym blokom (zaciatocna adresa alok. bloku - 4 bytes)
		// -prve slovo bloku za alokovanym blokom (zaciatocna adresa alok. bloku + size alok. bloku)
		//prehladam zoznam volnych blokov a budem hladat tieto adresy pointrov vo volnych blokoch

		void* ptrPredBlokom = (void*)((uint)blokPtr - sizeof(uint));
		void* ptrZaBlokom = (void*)((uint)blokPtr + (uint)blokPtr->size);

		VOLNY_BLOK* predchadzajuci = NULL;
		VOLNY_BLOK* nasledujuci = NULL;

		VOLNY_BLOK* prvyBlok = offsetToVolnyBlok(offsetNaPrvyVolnyBlok);

		do {
			//pred blokom je volny blok
			if ((uint)ptrPredBlokom == (uint)prvyBlok + (uint)prvyBlok->size - 4) {
				predchadzajuci = prvyBlok;

				//ak sme nasli aj nasledujuci, mozeme breaknut prehladavanie zoznamu
				if (nasledujuci) break;
			}

			//za blokom je volny blok
			if ((uint)ptrZaBlokom == (uint)prvyBlok) {
				nasledujuci = prvyBlok;

				//ak sme nasli predchadzajuci, mozeme breaknut prehladavanie zoznamu
				if (predchadzajuci) break;
			}

			prvyBlok = offsetToVolnyBlok( (uint)(prvyBlok->offset_next) );

		} while ((uint)prvyBlok != (uint)HEADER_PTR);

		//odstranim predchadzajuci zo zoznamu + mergnem predchadzajuci
		if (predchadzajuci) {

			removeFromList(predchadzajuci);

			if (DEBUG) printf(C_GREEN " - Mergujem [%d - %d] predchadzajuci blok s "C_RED"[%d - %d]"C_GREEN" alokovanym.\n" C_RESET, (uint)predchadzajuci, (uint)predchadzajuci + predchadzajuci->size, (uint)blokPtr, (uint)blokPtr + blokPtr->size);
			
			predchadzajuci->size += blokPtr->size;		
			blokPtr = predchadzajuci;

		}

		//odstranim nasledujuci zo zoznamu + mergnem nasledujuci
		if (nasledujuci) {

			removeFromList(nasledujuci);

			if (DEBUG) printf(C_GREEN " - Mergujem "C_RED"[%d - %d]"C_GREEN" alokovany blok s [%d - %d] nasledujucim.\n" C_RESET, (uint)blokPtr,(uint)blokPtr+blokPtr->size, (uint)nasledujuci, (uint)nasledujuci+nasledujuci->size);
			
			blokPtr->size += nasledujuci->size;
		}
		
		sortList(blokPtr);
		if (DEBUG) printf(C_GREEN "[%u - %u] Successfully freed memory\n" C_RESET, (uint)blokPtr, (uint)blokPtr + blokPtr->size);

		return 0;

	}

	// NEEXISTUJE VOLNY BLOK V ZOZNAME
	else {

		//alokovany blok pretypujem na volny a nastavim mu atributy (offsety: prev = 0, next = 0)
		//offset prveho volneho bloku v hlavicke hlavnej pamate nastavim na offset nasho bloku

		((VOLNY_BLOK*)blokPtr)->size = blokPtr->size;
		((VOLNY_BLOK*)blokPtr)->offset_next = 0;
		((VOLNY_BLOK*)blokPtr)->offset_prev = 0;
		offsetNaPrvyVolnyBlok = BlokToOffset(blokPtr);

		if (DEBUG) printf(C_GREEN "[%u - %u] Successfully freed %d Bytes of memory\n" C_RESET, (uint)blokPtr, (uint)blokPtr+blokPtr->size ,blokPtr->size);
		return 0;
	}


	return 1;
}

int memory_check(void* ptr){

	ALOKOVANY_BLOK* prvyBlok = offsetToVolnyBlok( sizeof(HLAVICKA_PAMATE) );
	uint velkostPamate = ((HLAVICKA_PAMATE*)(HEADER_PTR))->size;

	//overim ci sa vobec nachadza ptr v intervale nasej spravovanej pamate
	if ((uint)ptr < (uint)HEADER_PTR + sizeof(HLAVICKA_PAMATE) || (uint)ptr >= (uint)HEADER_PTR + velkostPamate)
		return 0;

	while ( (uint) prvyBlok  < (uint)HEADER_PTR + velkostPamate) {
		
		//	ak sme sa dostali na blok s adresou vacsou ako skumany pointer tak je pointer neplatny
		if ((uint)ptr < (uint)prvyBlok)
			return 0;

		//	ptr ukazuje na 5. Byte bloku (zaciatok datovej casti)
		//	treba este overit ci skumany blok je volny alebo alokovany
		//	preiterovanim explicitneho zoznamu volnych blokov
		if ((uint)ptr == (uint)prvyBlok + sizeof(ALOKOVANY_BLOK))
		{
			VOLNY_BLOK* freeBlok = offsetToVolnyBlok(offsetNaPrvyVolnyBlok);

			while ((uint)freeBlok != (uint)HEADER_PTR)
			{
				if ((uint)freeBlok == (uint)prvyBlok)
					return 0;
				freeBlok = offsetToVolnyBlok(freeBlok->offset_next);
			}
			return 1;
		}

		prvyBlok = addressToAlokovanyBlok((uint)prvyBlok+prvyBlok->size);
	}

	return 0;
}

void memory_init(void* ptr, uint size){

	if (ptr != NULL) {

		//nastavime globalny pointer na ptr VP
		(char*)HEADER_PTR = (char*)ptr;

		//vytvorime hlavicku pamate ( 8 B )
		((HLAVICKA_PAMATE*)((uint) HEADER_PTR))->size = size;
		((HLAVICKA_PAMATE*)((uint) HEADER_PTR))->first_freeblock_offset = sizeof(HLAVICKA_PAMATE);

		//vytvorime volny blok s offsetom velkosti hlavicky haldy ( 12 B )
		offsetToVolnyBlok(sizeof(HLAVICKA_PAMATE))->size = size - sizeof(HLAVICKA_PAMATE);
		offsetToVolnyBlok(sizeof(HLAVICKA_PAMATE))->offset_prev = 0;
		offsetToVolnyBlok(sizeof(HLAVICKA_PAMATE))->offset_next = 0;

		if(DEBUG) printf(C_CYAN "[%u] Initiated %d Bytes of memory.\n" C_RESET,(uint)HEADER_PTR,size);

	}	

}

void echoMemory() {
	uint free = 0;
	uint count = 0;
	uint total = ((HLAVICKA_PAMATE*)HEADER_PTR)->size;

	VOLNY_BLOK* tmp = offsetToVolnyBlok(offsetNaPrvyVolnyBlok);

	if (offsetNaPrvyVolnyBlok)  {
		while (tmp != HEADER_PTR) {
			free += tmp->size;
			count++;
			tmp = offsetToVolnyBlok(tmp->offset_next);
		}
	}
	
	printf(" [ "C_BLUE" HEADER: 8B "C_YELLOW"(%.2lf%%)"C_GREEN" FREE(%u BLOCKS): %u "C_YELLOW"(%.2lf%%) "C_RED"ALLOCATED: %u "C_YELLOW"(%.2lf%%)"C_RESET" ] \n",800.0/total,count,free,(double)(free*100)/total,total-free-8, (total - free - 8)*100/(double)total);

}


///////////////////////////////////////////////////////////////////////////////////////////////////////
///												TESTY												///
///////////////////////////////////////////////////////////////////////////////////////////////////////
void testSingleNumber(uint number, uint memory_size, char*pointer) {

	uint numRealAlloc = 0, numIdealAlloc = 0, sizeIdealAlloc = 0, sizeRealAlloc = 0;

	printf(C_CYAN"[TESTING SINGLE NUMBER %d in %dB REGION]\n"C_RESET, number, memory_size);

	memory_init(pointer, memory_size);

	//echoMemory();

	while ( sizeIdealAlloc+number < memory_size) {

		
		if (memory_alloc(number) != NULL)
		{
			numRealAlloc++;
			sizeRealAlloc += number;
		}
		//printf("allocating ... "); echoMemory();
		numIdealAlloc++;
		sizeIdealAlloc += number;
	}

	printf(C_YELLOW"--------------- TEST RESULTS --------------\n");
	printf("Initated "C_MAGENTA"%u Bytes "C_YELLOW"of memory\n",memory_size);
	printf("Allocated "C_GREEN"%u "C_YELLOW"blocks with total size "C_GREEN"%u Bytes"C_RED" (%.2f%%)"C_YELLOW".\n",numRealAlloc,sizeRealAlloc, (double)(sizeRealAlloc*100)/memory_size);
	printf(C_CYAN "Efficiency = %.2lf%%\n"C_RESET,(double)(numRealAlloc*100/numIdealAlloc));
	printf(C_YELLOW" ------------------------------------------\n"C_RESET);
}

double test(uint start, uint end, uint memory_size, char* pointer) {

	if (DEBUG) printf(C_MAGENTA"[TEST - %dB REGION]\n"C_RESET,memory_size);
	
	memory_init(pointer,memory_size);
	
	uint randNumber = rand() % (end - start + 1) + start;

	uint numRealAlloc = 0, numIdealAlloc = 0, sizeIdealAlloc = 0, sizeRealAlloc = 0;

	//printf(C_CYAN"[TESTING RANDOM NUMBERS FROM <%u,%u> in %dB REGION]\n"C_RESET, start,end, memory_size);

	while (sizeIdealAlloc + randNumber < memory_size) {


		if (memory_alloc(randNumber) != NULL)
		{
			numRealAlloc++;
			sizeRealAlloc += randNumber;
		}

		//printf("allocating %u Bytes ... ",randNumber); echoMemory();

		numIdealAlloc++;
		sizeIdealAlloc += randNumber;

		randNumber = rand() % (end - start + 1) + start;
	}

	//printf(C_YELLOW"--------------- TEST RESULTS --------------\n");
	//printf("Initated "C_MAGENTA"%u Bytes "C_YELLOW"of memory\n", memory_size);
	//printf("Allocated "C_GREEN"%u "C_YELLOW"blocks with total size "C_GREEN"%u Bytes"C_RED" (%.2f%%)"C_YELLOW".\n", numRealAlloc, sizeRealAlloc, (double)(sizeRealAlloc * 100) / memory_size);
	//printf(C_CYAN "Efficiency = %.2lf%%\n"C_RESET, (double)(numRealAlloc * 100 / numIdealAlloc));
	//printf(C_YELLOW" ------------------------------------------\n"C_RESET);

	return numIdealAlloc?(double)(numRealAlloc * 100 / numIdealAlloc):0;
}

double make1000Tests(uint start, uint end, uint memory_size, char* pointer) {
	double eff = 0;
	for (int i = 0; i < 1000; i++) {

		eff = eff + test(start, end, memory_size, pointer);
	}
	printf(C_YELLOW"--------------- MASS TEST RESULTS --------------\n");
	printf("BLOCKS OF RANDOM SIZE %u - %uB\n",start,end);
	printf("Initiated %u Bytes of memory\n", memory_size);
	printf("EFFICIENCY: %.2lf\n",eff/1000);
	printf(C_YELLOW"------------------------------------------------\n"C_RESET);
}