import random
import math
import time
import matplotlib.pyplot as plt
import matplotlib
matplotlib.use('tkagg')

#constant values
max_generations = 5000
population_size = 150
mutation_min = 0.01
mutation_max = 0.3
crossover_rate = 0.98
parent_selection = 1 # 1 - Tournament Selection        2 - Roulette Wheel Selection
colors = ['\33[31m','\33[33m','\33[34m','\33[35m','\33[36m','\33[91m','\33[92m','\33[93m','\33[94m','\33[95m','\33[96m','\33[97m','\33[32m']
creset = '\033[0m'
showAverageAndBest = False
drawAnimation = False

def createMap(size_x, size_y, stones):
    arr = [[0 for i in range(size_x)] for i in range(size_y)]
    for stone_x, stone_y in stones:
        arr[stone_y][stone_x] = -1
    return arr

def countFitness(Map, size_x, size_y):
    count = 0
    for y in range(size_y):
        for x in range(size_x):
            if Map[y][x] > 0:
                count+=1
    return count

def is_in_Map(x, y, size_x, size_y):
       return True if ((x>-1 and x<size_x) and (y>-1 and y<size_y)) else False

def checkPosition(Map, size_x, size_y, x, dif_x, y, dif_y):
    if is_in_Map(x + dif_x, y + dif_y, size_x, size_y) and Map[y + dif_y][x + dif_x] == 0:
        return (dif_x, dif_y)
    elif is_in_Map(x + dif_x, y + dif_y, size_x, size_y):
        return True
    return False

def evaluateChromosome(size_x, size_y, Map, chromosome, returnMap=False):

    for rowI in range(size_y):
        for colI in range(size_x):
            if Map[rowI][colI] > 0:
                Map[rowI][colI] = 0  
    
    if returnMap and drawAnimation:
        for rowI in range(size_y):
            for colI in range(size_x):
                if Map[rowI][colI] < 0:
                    ax = plt.gca()
                    text = ax.text(colI + 0.5, size_y - 1-  rowI + 0.5, "K" if Map[rowI][colI] == -1 else Map[rowI][colI],ha="center", va="center", color="gray", fontweight="bold")

    stepCounter = 1
    for genX, genY, genDirec in chromosome:
        if Map[genY][genX] != 0:
            continue

        diffX = 1 if genX == 0 else -1 if genX == size_x -1 else 0
        diffY = 0 if genX == 0 or genX == size_x-1 else 1 if genY == 0 else -1
        tempX, tempY = genX, genY
        moves = []

        while 1:
            Map[tempY][tempX] = stepCounter                                             # Harvest current field

            moves.append((tempX, tempY))

            if is_in_Map(tempX+diffX, tempY+diffY, size_x, size_y) == False:            # Monk is on the border of the map -> move to next gene
                break
            
            if Map[tempY+diffY][tempX+diffX] == 0:                                      # Harvest the next field
                tempY += diffY
                tempX += diffX
                continue

            if diffY == 0:  #moving horizontal
                possibleMoves = [checkPosition(Map, size_x, size_y, tempX, 0, tempY, -diffX), checkPosition(Map, size_x, size_y, tempX, 0, tempY, diffX)]
            else:           #moving vertical
                possibleMoves = [checkPosition(Map, size_x, size_y, tempX, diffY, tempY, 0), checkPosition(Map, size_x, size_y, tempX, -diffY, tempY, 0)]

            legit = sum(type(item) == type(()) for item in possibleMoves)
            if legit == 1:
                for itemIndex in range(2):
                    if type(possibleMoves[itemIndex]) == type(()):
                        diffX,diffY = possibleMoves[itemIndex]

            elif legit == 2:
                if genDirec == "l":
                    diffX,diffY = possibleMoves[0]
                else:
                    diffX,diffY = possibleMoves[1]
        
            else:
                if possibleMoves.count(True) == 2:
                    for tx,ty in moves:
                        Map[ty][tx] = 0
                    break
                break

            tempY += diffY
            tempX += diffX

        stepCounter+=1

        if returnMap and drawAnimation:
            ax = plt.gca()

            summ = 3*0.75
            while summ >= 3*0.75:
                c1,c2,c3 = random.random(), random.random(), random.random()
                summ = c1+c2+c3

            randomColor = (c1,c2,c3)
            for movX, movY in moves:
                text = ax.text(movX+0.5, size_y - 1-  movY+0.5, Map[movY][movX],ha="center", va="center", color=randomColor, fontweight="bold")
                plt.pause(0.1)
        
    fitness = countFitness(Map, size_x, size_y)

    if returnMap == True:
        s = ""
        for row in Map:
            for col in row:
                s += ' K ' if col == -1 else colors[col%len(colors)]+'%2d '% col+creset 
            s += '\n'
        return s
                
    return fitness

def evaluatePopulation(size_x, size_y, Map, population):
    fitnesses = []
    for chrom in population:
        fitnesses.append(evaluateChromosome(size_x, size_y, Map, chrom))
    return fitnesses


def createInitialPop(numOfGenes, size_x, size_y):
    chromosomes = []
    for chrom in range(population_size):
        genes = []
        for num in range(2*(size_x+size_y)):
            if num<size_x:
                genes.append((num, 0, random.choice(['l','r']))) # top
            elif num<size_x+size_y:
                genes.append((size_x-1, num-size_x, random.choice(['l','r']))) # right
            elif num<2*size_x+size_y:
                genes.append((num-size_x-size_y, size_y-1, random.choice(['l','r']))) # bottom
            else:
                genes.append((0, num-2*size_x-size_y, random.choice(['l','r']))) # left

        random.shuffle(genes)

        chromosomes.append(genes[:numOfGenes])

    return chromosomes

def select_parent(chromosomes, fitnesses):
    ############################# SELECT DOPISAT ##############################
    if parent_selection == 1: #Tournament Selection
        chrom_array = []
        for _ in range(0, 3):
            index = random.randint(0, population_size-1)
            chrom_array.append(index)
            
        maxIndex = 0
        maxValue = fitnesses[chrom_array[0]]    
        for chromIndex in range(1,len(chrom_array)):
            if fitnesses[chrom_array[chromIndex]] > maxValue:
                maxValue = fitnesses[chrom_array[chromIndex]]
                maxIndex = chrom_array[chromIndex]
        return chromosomes[maxIndex]

def crossover(parent1, parent2, numOfGenes):
    genes_1 = []
    genes_2 = []
    parents = [parent1, parent2]

    ########### uniform crossover ############
    parentChoice = random.randint(0,1)
    for geneIndex in range(numOfGenes):
        if random.random() < 0.5:
            genes_1.append(parents[parentChoice][geneIndex])
            genes_2.append(parents[1-parentChoice][geneIndex])
        else:
            genes_1.append(parents[1-parentChoice][geneIndex])
            genes_2.append(parents[parentChoice][geneIndex])
    return genes_1, genes_2

def mutation(chromosome, numOfGenes, mut_rate, size_x, size_y):
    chance = random.random()

    
    for _ in range(numOfGenes // 4):
        if (random.random() < mut_rate):
            geneIndex = random.randint(0,numOfGenes-1)
            gene = chromosome[geneIndex]
            chance = random.random()
            if chance < 0.3:
                chromosome[geneIndex] = (gene[0], gene[1], random.choice(['l','r']))
            elif chance < 0.7:
                index2 = random.randint(0, numOfGenes - 1)
                while index2==geneIndex:
                    index2 = random.randint(0, numOfGenes - 1)
                tmp = chromosome[index2]
                chromosome[index2] = chromosome[geneIndex]
                chromosome[geneIndex] = tmp
            else:
                side = random.randint(1,4)
                if side == 1: # left side
                    chromosome[geneIndex] = (0,random.randint(0, size_y - 1), gene[2])
                elif side == 2: # top side
                    chromosome[geneIndex] = (random.randint(0, size_x - 1), 0, gene[2])
                elif side == 3: # right side
                    chromosome[geneIndex] = (size_x - 1, random.randint(0,  size_y- 1), gene[2])
                else: # bottom side
                    chromosome[geneIndex] = (random.randint(0, size_x - 1), size_y - 1, gene[2])

def geneticAlgorithm(size_x, size_y, stones):
    mutation_rate = mutation_min
    numOfStones = len(stones)
    numOfGenes = size_x+size_y+len(stones)
    max_possible_fitness = size_x*size_y-numOfStones

    start = time.time()
    population = createInitialPop(numOfGenes, size_x, size_y)
    
    Map = createMap(size_x, size_y, stones)
    fitnesses = evaluatePopulation(size_x, size_y, Map, population)
    lastAverage = 0
    for generation in range(1, max_generations+1):

        newChromosomes = []
        for __ in range(0, population_size//2):
            if random.random() < crossover_rate:
                parent1 = select_parent(population, fitnesses)
                parent2 = select_parent(population, fitnesses)

                children = crossover(parent1, parent2, numOfGenes)
                for i in range(2):
                    mutation(children[i], numOfGenes,mutation_rate, size_x, size_y)
                    newChromosomes.append(children[i])

        while len(newChromosomes) < population_size:
            newChromosomes.append(select_parent(population, fitnesses))

        population = newChromosomes
        fitnesses = evaluatePopulation(size_x, size_y, Map, population)

        minValue = fitnesses[0]
        bestChromosome = population[0]
        maxValue = fitnesses[0]
        total = 0
        for index, fitness in enumerate(fitnesses):
            total += fitness
            if maxValue < fitness:
                maxValue = fitness
                bestChromosome = population[index]
            if minValue > fitness:
                minValue = fitness
        avgValue = total/population_size

        if (lastAverage and abs((lastAverage-avgValue)/lastAverage)<0.02 and mutation_rate < mutation_max):
            mutation_rate+=0.01
        else:
            mutation_rate = mutation_min

        lastAverage = total/population_size

        # if generation%5 == 0:
        #     print(bestChromosome)
        #     bestChromMap = evaluatePopulation(size_x, size_y, stones, [bestChromosome], True)
        #     im = ax.imshow(bestChromMap, cmap=plt.cm.gist_rainbow)
        #     fig.suptitle(str(maxValue))
        #     for i in range(size_y):
        #         for j in range(size_x):
        #             text = ax.text(j, i, bestChromMap[i][j],ha="center", va="center", color="w", fontweight="bold")
        #     plt.pause(0.2)
        #     plt.cla()
        
        print('generacia: %4d, max: %4d, best: %4d, min: %4d, avg: %7.2f, mutation_rate: %4.2f'%(generation, max_possible_fitness, maxValue, minValue, total/population_size,mutation_rate))

        if max_possible_fitness == maxValue:

            end = time.time()
            
            print(evaluateChromosome(size_x, size_y, Map, bestChromosome, True)) 
            print(" > Fitness - %d, Maximum possible fitness - %d"%(maxValue, max_possible_fitness))
            print(" > Total time: %.4f (s)         Number of generations: %d" % (end - start, generation))
            print()
            plt.show()
            return

    end = time.time()
    print(" > DID NOT FIND SOLUTION IN LIMIT")
    print(" > Best fitness - %d, Maximum possible fitness - %d"%(maxValue, max_possible_fitness))
    print(" > Total time: %.4f (s)         Number of generations: %d" % (end - start, generation))
        
def main():
    random.seed()
    size_x = 12
    size_y = 10
    stones = [(1, 2), (5, 1), (4, 3), (2, 4), (8, 6), (9, 6)]

    if drawAnimation:
        fig, ax = plt.subplots()
        ax.set_xticks([i for i in range(0,size_x+1)])
        ax.set_xticks([i+0.5 for i in range(0,size_x)], minor=True)
        ax.set_xticklabels([str(i) for i in range(0,size_x)], minor=True)

        ax.set_yticks([i for i in range(0,size_y+1)])
        ax.set_yticks([i+0.5 for i in range(0,size_y)], minor=True)
        ax.set_yticklabels([str(size_y-1-i) for i in range(0,size_y)], minor=True)
        ax.tick_params(which='both', length=0)
        plt.setp([ax.get_xmajorticklabels(),ax.get_ymajorticklabels()], visible=False)

        plt.grid(which='major')
        
        
        
    geneticAlgorithm(size_x, size_y, stones)

main()