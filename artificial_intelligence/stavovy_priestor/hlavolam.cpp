#define _CRT_SECURE_NO_WARNINGS
#define STEP_LIMIT 10000000
#define TIME_LIMIT 15
#define uchar unsigned char
#define MKOEF 1

#define YEL "\u001b[33m"
#define RST "\u001b[0m"

#include <iostream>
#include <ctime>
#include <string>
#include <vector>
#include <queue>
#include <unordered_set>
#include <stdlib.h>

using namespace std;

unsigned int steps = 0, generated_nodes = 0;
clock_t start;
bool limitExceeded = false;

struct vrchol {
	char* state;
	char posZero;
	uchar g;
	uchar h;
	struct vrchol* parent;
};

struct CompareNodes {  // funkcia na porovnavanie hodnoty f pri vkladani do priority queue
	bool operator()(vrchol* n1, vrchol* n2)
	{
		return (n1->g + n1->h) > (n2->g + n2->h);
	}
};

unsigned abs(char number) {
	return number < 0 ? -number : number;
}

string generateState(uchar width, uchar height) {
	string variable = "()";
	int* arr = new int[width*height]();
	for (int i = 0; i < width * height; i++) {
		arr[i] = i;
	}

	for (int i = 0; i < 1000; i++) {
		int tmp = arr[i % (height*width)];
		int index = rand() % (height * width);
		arr[i % (height * width)] = arr[index];
		arr[index] = tmp;
	}

	for (int i = 0; i < height; i++) {
		variable.insert(variable.end()-1,'(');
		for (int j = 0; j < width; j++) {
			variable.insert(variable.size()-1, to_string(arr[j+i*height]));
			if (j != width - 1) variable.insert(variable.size() - 1, " ");
		}
		variable.insert(variable.end() - 1, ')');
	}

	delete[] arr;
	return variable;
}

vrchol* stringStateToNode(string s, uchar* wdth, uchar* hght) {
	int pos = 0, posEnd = 0;
	pos = s.find('(');
	s.erase(0, pos + 1);
	pos = s.find_last_of(')');
	s.erase(pos, pos + 1);

	char height = count(s.begin(), s.end(), '(');
	char width = 0, k = 0;
	vrchol* nod = new vrchol();
	while ((pos = s.find('(')) != string::npos && (posEnd = s.find(')')) != string::npos) {
		string oneLine = s.substr(pos+1, posEnd-pos-1);
		int i = 0, j = 0;
		bool space = false;
		vector<int> arr;
		for (i = 0; oneLine[i] != '\0'; i++) {
			if (oneLine[i] == ' ') {
				if (space == false) {
					j++;
				}
				space = true;
			}
			else {
				if (oneLine[i] >= '0' && oneLine[i] <= '9') {
					if (arr.size() <= j) arr.push_back(0);
					arr[j] = arr[j] * 10 + (oneLine[i] - 48);
				}
				space = false;
			}
		}
		if (width == 0) {
			width = arr.size();
			nod->state = new char[width*height]();
		}
		for (i = 0; i < arr.size(); i++, k++) {
			if (arr[i] == 0) nod->posZero = k;
			nod->state[k] = arr[i];
		}
		s.erase(0, posEnd+1);
	}
	if (wdth != nullptr and hght != nullptr) {
		*wdth = width;
		*hght = height;
	}
	return nod;
}

void printPath(vrchol* node, uchar width, uchar height) {
	if (node != nullptr) {
		printPath(node->parent, width, height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				cout << int(node->state[i * width + j]);
				cout << " ";
			}
			cout << endl;
		}
		cout << endl;
	}
}

void initialize(vrchol** start, string startString, vrchol** dest, string destString, uchar* width, uchar* height) {
	*start = stringStateToNode(startString, width, height);
	(*start)->g = 0;
	(*start)->parent = nullptr;
	*dest = stringStateToNode(destString, nullptr, nullptr);
}

uchar manhattan_distance(uchar width, uchar height, char* state, char* dest_state) {
	uchar total = 0;
	for (int i = 0; i < width * height;i++) {
		for (int j = 0; j < width * height; j++) {
			if (state[i] && state[i] == dest_state[j]) {
				total += abs(j/ width - i/width) + abs(j%width -i%width);
				break;
			}
		}
	}
	return total * MKOEF;
}

uchar misplaced_tiles(uchar width, uchar height, char* state, char* dest_state) {
	uchar total = 0;
	for (int i = 0; i < width * height; i++) {
		if (state[i] && state[i] != dest_state[i]) {
			total++;
		}
	}
	return total * MKOEF;
}

uchar manhattan_cached(uchar h, uchar realPosX, uchar realPosY, uchar destPosX, uchar destPosY, char diffX, char diffY) {
	if (diffX == 0)
		return h + MKOEF * (abs(realPosY - destPosY) - abs(realPosY + diffY - destPosY));
	else
		return h + MKOEF * (abs(realPosX - destPosX) - abs(realPosX + diffX - destPosX));
}

uchar misplaced_cached(uchar h, uchar realPosX, uchar realPosY, uchar destPosX, uchar destPosY, char diffX, char diffY) {
	return h + MKOEF * (((realPosY + diffY == destPosY) && (realPosX + diffX == destPosX)) - ((realPosY == destPosY) && (realPosX == destPosX)));
}

string stateToString(char* state, uchar width, uchar height) {
	string s;
	for (int i = 0; i < width * height; i++) {
		s += state[i]+48;
		s += " ";
	}
	return s;
}

bool stateMatch(char* node, char* dest, uchar width, uchar height) {
	for (int i = 0; i < width * height; i++) {
		if (node[i] != dest[i]) return false;
	}
	return true;
}

// funkcia na generovanie novych uzlov
vrchol* checkSuccessor(unordered_set<string>* visited, char* state, uchar zeroPosition, char diffx, char diffy, uchar width, uchar height) {
	char* newState = new char[width*height]();
	for (int i = 0; i < width * height; i++) {
		newState[i] = state[i];
	}
	char tmp = newState[zeroPosition];
	uchar newZeroPosition = zeroPosition + diffx + diffy * width;		// vytvorenie noveho stavu
	newState[zeroPosition] = state[newZeroPosition];					// pomocou kopirovania stareho stavu
	newState[newZeroPosition] = tmp;									// a vymeny dvoch policok (nuly a este 1 policka)

	string newStateString = stateToString(newState, width, height);		// konvertovanie stavu na string pre zahashovanie do tabulky

	if ((*visited).find(newStateString) == (*visited).end()) {			// ak sa stav nenachadza v hash tabulke ( nie je navstiveny)
		(*visited).insert(newStateString);
		vrchol* newNode = new vrchol();									// stav sa vlozi do hash tabulky
		newNode->state = newState;										// a vytvori sa novy vrchol
		newNode->posZero = newZeroPosition;
		return newNode;
	}
	else {
		delete[] newState;											
		return nullptr;
	}
}

vrchol* A_Star(vrchol* startNode, vrchol* destNode, uchar width, uchar height, uchar(*heuristic)(uchar h, uchar rPX, uchar rPY, uchar dPX, uchar dPY, char dX, char dY)) {
	
	char* destCacheArr = new char[width*height]();
	for (int i = 0; i < width * height; i++) 
		destCacheArr[destNode->state[i]] = (i / width) * 10 + (i % width);					// ulozenie suradnic cieloveho uzla

	priority_queue<vrchol*, vector<vrchol*>, CompareNodes> PQ;								// vytvorenie prioritneho radu (min heapu)
	unordered_set<string> visited;															// vytvorenie hash tabulky pre navstivene vrcholy
	string startNodeStateString = stateToString(startNode->state, width, height);
	PQ.push(startNode);																		// vlozenie pociatocneho uzla do min haldy
	generated_nodes++;
	visited.insert(startNodeStateString);													// vlozenie stavu pociatocneho uzla do hash tabulky
	uchar zeroIndexY, zeroIndexX, zeroIndex;

	while (!PQ.empty()) {
		vrchol* node = PQ.top();
		vrchol* childNode;
		PQ.pop();

		steps++;
		if (steps > STEP_LIMIT || (clock() - start)/(float)CLOCKS_PER_SEC > TIME_LIMIT) {
			limitExceeded = true;
			return nullptr;
		}

		if (stateMatch(node->state, destNode->state, width, height) == true) {
			return node;
		}

		zeroIndex = node->posZero;
		zeroIndexY = zeroIndex / width;
		zeroIndexX = zeroIndex % width;

		if (zeroIndexY > 0 ) {			// DOWN = posun dolu = medzera hore
			if ((childNode = checkSuccessor(&visited, node->state, zeroIndex, 0, -1, width, height)) != nullptr) {
				childNode->g = node->g + 1;
				childNode->parent = node;
				uchar posDest = destCacheArr[childNode->state[zeroIndex]];
				childNode->h = heuristic(node->h, zeroIndexX, zeroIndexY, posDest % 10, posDest / 10, 0, -1);
				PQ.push(childNode);
				generated_nodes++;
			}
		}
		if (zeroIndexX > 0) {			// RIGHT = posun doprava = medzera dolava
			if ((childNode = checkSuccessor(&visited, node->state, zeroIndex, -1, 0, width, height)) != nullptr) {
				childNode->g = node->g + 1;
				childNode->parent = node;
				uchar posDest = destCacheArr[childNode->state[zeroIndex]];
				childNode->h = heuristic(node->h, zeroIndexX, zeroIndexY, posDest % 10, posDest / 10, -1, 0);
				PQ.push(childNode);
				generated_nodes++;
			}
		}
		if (zeroIndexY < height - 1) {   // UP = posun hore = medzera dolu
			if ((childNode = checkSuccessor(&visited, node->state, zeroIndex, 0, 1, width, height)) != nullptr) {
				childNode->g = node->g + 1;
				childNode->parent = node;
				uchar posDest = destCacheArr[childNode->state[zeroIndex]];
				childNode->h = heuristic(node->h, zeroIndexX, zeroIndexY, posDest % 10, posDest / 10, 0, 1);
				PQ.push(childNode);
				generated_nodes++;
			}
		}
		if (zeroIndexX < width - 1) {	// LEFT = posun dolava = medzera doprava
			if ((childNode = checkSuccessor(&visited, node->state, zeroIndex, 1, 0, width, height)) != nullptr) {
				childNode->g = node->g + 1;
				childNode->parent = node;
				uchar posDest = destCacheArr[childNode->state[zeroIndex]];
				childNode->h = heuristic(node->h, zeroIndexX, zeroIndexY, posDest % 10, posDest / 10, 1, 0);
				PQ.push(childNode);
				generated_nodes++;
			}
		}

	}
	return nullptr;
}

int main(int argc, char** argv) {
	srand(time(NULL));
	string startingPosition, destPosition;
	char option;

	if (argc == 4) {
		startingPosition = argv[1];
		destPosition = argv[2];
		option = argv[3][0];
	}
	else {
		cout << YEL << "Vyberte moznost:" << endl;
		cout << "  << 1. Nacitat stavy zo vstupu" << endl;
		cout << "  << 2. Vygenerovat stavy" << RST << endl;

		cin >> option;

		if (option == '1') {
			cin.ignore();
			cout << YEL << "Zadajte startovaci stav vo formate ((1 2 3)(4 5 6)(7 8 0) ... ):" << RST << endl;
			getline(cin, startingPosition);
			cout << YEL << "Zadajte cielovy stav vo formate ((1 2 3)(4 5 6)(7 8 0) ... ):" << RST << endl;
			getline(cin, destPosition);
		}
		else if (option == '2') {
			int dimW, dimH;
			cout << YEL << "Zadajte sirku a vysku hlavolamu:" << RST << endl;
			cin >> dimW >> dimH;
			startingPosition = generateState(dimW, dimH);
			destPosition = generateState(dimW, dimH);
		}
		cout << YEL << "Zvolte typ heuristiky:" << endl;
		cout << "  << 1. Manhattanovska vzdialenost" << endl;
		cout << "  << 2. Hammingova vzdialenost (pocet zle polozenych policok)" << RST << endl;
		cin >> option;
	}


	vrchol* startNode = nullptr, * destNode = nullptr;
	uchar width, height;
	initialize(&startNode, startingPosition, &destNode, destPosition, &width, &height);

	uchar (*heuristic)(uchar h, uchar realPosX, uchar realPosY, uchar destPosX, uchar destPosY, char diffX, char diffY) = nullptr;

	if (option == '1') {
		heuristic = &manhattan_cached;
		startNode->h = manhattan_distance(width, height, startNode->state, destNode->state);
	}
	else if (option == '2') {
		heuristic = &misplaced_cached;
		startNode->h = misplaced_tiles(width, height, startNode->state, destNode->state);
	}
	else {
		return 1;
	}

	cout << " > Startovaci stav: " << startingPosition << endl;
	cout << " > Koncovy stav:    " << destPosition << endl;
	
	start = clock();
	vrchol* foundNode = A_Star(startNode, destNode, width, height, heuristic);
	clock_t end = clock();
	if (foundNode != nullptr) {
		printPath(foundNode, width, height);
		cout << " > HLBKA: " << (unsigned short)foundNode->g << endl;
		cout << " > CAS: " << (end - start) / (float)CLOCKS_PER_SEC << "s" << endl;
		cout << " > POCET KROKOV (SPRACOVANYCH UZLOV): "<< steps << " VYGENEROVANYCH UZLOV: " << generated_nodes << endl;
	}
	else if(limitExceeded) {
		cout << " > Vyprsal casovy alebo krokovy limit!" << endl;
		cout << " > POCET SPRACOVANYCH UZLOV (KROKOV): " << steps << " VYGENEROVANYCH UZLOV: " << generated_nodes << endl;
	}
	else {
		cout << " > Nie je mozne najst riesenie pre tento hlavolam." << endl;
		cout << " > POCET SPRACOVANYCH UZLOV (KROKOV): " << steps << " VYGENEROVANYCH UZLOV: " << generated_nodes << endl;
	}

}