from queue import PriorityQueue
import time
import sys

def findInState(el, state):
    for rowGoalIndex, rowGoal in enumerate(state):
        for colGoalIndex, GoalItem in enumerate(rowGoal):
            if (el == GoalItem):
                return rowGoalIndex, colGoalIndex

def manhattan_distance(puzzle_state, destination_state):
    total = 0
    for rowIndex, row in enumerate(puzzle_state):
        for columnIndex, item in enumerate(row):
            if item != 0:
                (endRowIndex, endColIndex) = findInState(item, destination_state)
                total += abs(endRowIndex - rowIndex) + abs(endColIndex - columnIndex)
    return total          

def misplaced_items(puzzle_state, destination_state):
    count = 0
    for rowIndex, row in enumerate(puzzle_state):
        for columnIndex, item in enumerate(row):
            if item != destination_state[rowIndex][columnIndex] and item != 0:
                count+=1
    return count

def move(y, x, diffx, diffy, state, W, H):
    newState = []
    for i in range(H):
        newState.append([])
        for j in range(W):
            newState[i].append(state[i][j])
    tmp = newState[y][x]
    newState[y][x] = newState[y+diffy][x+diffx]
    newState[y+diffy][x+diffx] = tmp
    return newState

def stateToStr(state, W, H):
    res = ""
    for i in range(H):
        for j in range(W):
            res += str(state[i][j])+"."
    return res

def printPath(node):
    if node.parent is not None:
        printPath(node.parent)
    for line in node.state:
        print(line)
    print()

def parseInput(inputString):
    src = inputString.split('(',1)[1].rsplit(')',1)[0].split(')')
    for index,string in enumerate(src):
        if len(string):
            src[index] = [int(i) for i in string.split('(')[1].split(' ') if i] 
        else:
            src.remove(src[index])
    return src

class PuzzleSolver:
    def __init__(self, sourceState, destinationState, width, height):
        self.sourceState = sourceState
        self.destinationState = destinationState
        self.width = width
        self.height = height

    def analyzeSuccessor(self, node, diffx, diffy):
        global count
        newState = move(node.zeroPosition[0], node.zeroPosition[1], diffx, diffy, node.state, self.width, self.height) # Posun dole (medzera hore)
        # newState = move(node[1][0], node[1][1], diffx, diffy, node[0], self.width, self.height) # Posun dole (medzera hore)
        newStateStr = stateToStr(newState, self.width, self.height)
                
        if newStateStr not in self.visited:
            count+=1
            child = Node(newState, (node.zeroPosition[0]+diffy, node.zeroPosition[1]+diffx), self.destinationState, node.g + 1, node)
            # child = [newState, (node[1][0]+diffy, node[0][1][1]+diffx), self.destinationState, node[4], node]
            self.PQ.put((child.cost(),count, child))
            self.visited.add(newStateStr)
    
    def solve(self):
        global steps, count
        (rowI, colI) = findInState(0, self.sourceState)
        Start = Node(self.sourceState, (rowI, colI), self.destinationState)
        # Start = [self.sourceState, (rowI, colI), self.destinationState, 0, None]

        self.PQ = PriorityQueue()                                # vytvorena min. halda
        self.PQ.put((Start.cost(), count, Start))                # vlozeny startovaci vrchol do haldy
        self.visited = set()                                     # vytvoreny set - hash tabulka pre navstivene vrcholy (stavy)
        self.visited.add(stateToStr(Start.state, self.width, self.height)) # vlozeny stav startoveho vrcholu do hash tabulky navstivenych
        
        while not self.PQ.empty():
            strt = time.time()
            (score, cnt, node) = self.PQ.get()
            steps+=1

            print(node.state)

            if node.state == self.destinationState:
                return node

            if node.zeroPosition[0] > 0:                         # Posun dole (medzera hore)
                self.analyzeSuccessor(node, 0, -1)
                
            if node.zeroPosition[1] > 0:                         # Posun doprava (medzera dolava)
                self.analyzeSuccessor(node, -1, 0)

            if node.zeroPosition[0] < self.height - 1:                # Posun hore (medzera dole)
                self.analyzeSuccessor(node, 0, 1)


            if node.zeroPosition[1] < self.width - 1:                 # Posun dolava (medzera doprava)
                self.analyzeSuccessor(node, 1, 0)

        return None

class Node:
    def __init__(self, state, zeroPosition, destination_state, g_cost = 0, parent = None):
        self.state = state
        self.zeroPosition = zeroPosition
        self.g = g_cost
        self.h = heuristic(state, destination_state)
        self.parent = parent

    def cost(self):
        return self.h + self.g


steps = 0
count = 0

if (len(sys.argv) > 1):
    source = parseInput(sys.argv[1])
    destination = parseInput(sys.argv[2])
    heurOption = sys.argv[3]

else:
    print("Zadajte vstupny stav hlavolamu:")
    source = parseInput(input())
    print("Zadajte konecny stav hlavolamu:")
    destination = parseInput(input())
    print("Zvolte typ heuristiky:\n1 - manhattanovska vzdialenost (bez medzery)\n2 - pocet nespravne polozenych policok (bez medzery)")
    heurOption = input()

if (heurOption == '1'):
    heuristic = manhattan_distance
elif (heurOption == '2'):
    heuristic = misplaced_items
else:
    print("Zadali ste nespravnu moznost!")
    quit()

Hght = len(source)
Wdth = len(source[0])


startTime = time.time()

PS = PuzzleSolver(source, destination, Wdth, Hght)
Finished = PS.solve()

endTime = time.time()
# if Finished is not None:
#     printPath(Finished)

print(" <<<      STEPS : %d       >>>"%(steps))
print(" <<     NODE COUNT : %d     >>"%(count))
print(" <<<   DURATION : %fs   >>>"%((endTime-startTime)))
 