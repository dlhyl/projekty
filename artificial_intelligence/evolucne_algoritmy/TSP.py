import random
import time
import matplotlib.pyplot as plt
from matplotlib.ticker import FormatStrFormatter
import matplotlib
matplotlib.use("TkAgg")
from math import dist, exp
####################################################################################################################################################
###   >>>>>>>>>>>>>>>>>>>>>>>>>                                     Constants                                          <<<<<<<<<<<<<<<<<<<<<<<<< ###

mapWidth = 200
mapHeight = 200
numOfCities = 30
showLiveAnimation = True
showFinalGraphs = False

# tabu search
iterations = 500
tabuSizeKoef = 1

# simulated annealing
initialTemp = 100
coolingRate = 0.999

# genetic algorithm
populationSize = 50
maxGenerations = 300
mutationRate = 0.01
selectionMethod = 2       # 1 - Tournament(2), 2 - Tournament(3), 3 - Roulette

######################################################################################################################################################

def showUI():
    global numOfCities,mapWidth,mapHeight, showFinalGraphs, showLiveAnimation

    while True:
        print(" Urcte akciu:")
        print(" (1) Vzorova mapa s predvolenymi parametrami")
        print(" (2) Vzorova mapa s uzivatelom urcenymi parametrami")
        print(" (3) Nahodna mapa s predvolenymi parametrami")
        print(" (4) Nahodna mapa s uzivatelom urcenymi parametrami")
        action = int(input())
        if action == 1 or action == 2:
            numOfCities=20
            cities = [(60, 200), (180, 200), (100, 180), (140, 180), (20, 160), (80, 160), (200, 160), (140, 140), (40, 120), (120, 120), (180, 100), (60, 80), (100, 80), (180, 60), (20, 40), (100, 40), (200, 40), (20, 20), (60, 20), (160, 20)]
            distances = [ [ round(getEuclideanDistance(cities[i], cities[j]), 3) for i in range(numOfCities) ] for j in range(numOfCities) ]
        elif action == 3 or action == 4:
            cities, distances = generateCityMatrix(numOfCities)
        else:
            print(" ~ Zadajte validnu moznost!")
            continue
        break

    if action == 2 or action == 4:
        global iterations, tabuSizeKoef, initialTemp, coolingRate, maxGenerations, populationSize, mutationRate, selectionMethod
        if action == 4:
            print(" > Zadajte pocet miest a rozmery mapy:")
            numOfCities,mapWidth,mapHeight = list(map(int,input().split()))
        print(" ~ Zadajte parametre pre Tabu Search ~")
        print("  >> Koeficient velkosti tabu listu: ",end="")
        tabuSizeKoef = float(input())
        print("  >> Pocet iteracii: ",end="")
        iterations = int(input())
        print(" ~ Zadajte parametre pre Simulovane zihanie ~")
        print("  >> Pociatocna teplota: ",end="")
        initialTemp = float(input())
        print("  >> Miera ochladzovania: ",end="")
        coolingRate = float(input())
        print(" ~ Zadajte parametre pre Geneticky algoritmus ~")
        print("  >> Velkost populacie: ",end="")
        populationSize = int(input())
        print("  >> Pravdepodobnost mutacie: ",end="")
        mutationRate = float(input())
        print("  >> Selekcia 1 - Tournament(2), 2 - Tournament(3), 3 - Roulette: ",end="")
        selectionMethod = int(input())
        print("  >> Pocet generacii: ",end="")
        maxGenerations = int(input())

    print(" > Chcete vizualizaciu vysledkov na grafe? (Y/N)")
    showFinalGraphs = True if input()=='Y' else False
    print(" > Chcete animaciu na grafe? (Y/N)")
    showLiveAnimation = True if input()=='Y' else False
    return cities, distances
       
def getEuclideanDistance(cityA, cityB):         # calculate euclidean distance between two points
    return dist(cityA, cityB)

def createCityCoordinates():                    # create random city coordinates
    return (random.randint(0,mapWidth-1),random.randint(0,mapHeight-1))    

def generateCityMatrix(size):                   # create random cities and calculate distances between cities
    cities = [ createCityCoordinates() for j in range(size) ]
    distances = [ [ round(getEuclideanDistance(cities[i], cities[j]), 3) for i in range(size) ] for j in range(size) ]
    return cities, distances

def generateCitySequence():                     # generate random city sequence
    sequence = [ i for i in range(numOfCities)]
    random.shuffle(sequence)
    return sequence

def getPathCost(sequence, distances):           # calculate path cost of city sequence (permutation)
    cost = 0
    for index in range(numOfCities - 1):
        cost += distances[sequence[index]][sequence[index+1]]
    cost += distances[sequence[0]][sequence[numOfCities - 1]]
    return round(cost, 3)

def swap(sequence, a, b):                       # swap two cities in sequence
    tmp = sequence[a]
    sequence[a] = sequence[b]
    sequence[b] = tmp

def acceptProbability(cost, bestCost, temp):   # probabilty function for simulated annealing (SA)
    if cost < bestCost:
        return 1
    return exp((bestCost-cost)/temp)

def evaluatePopulation(population, distances):  # evaluate chromosomes (GA)
    for i in range(populationSize):
        population[i].append(getPathCost(population[i][0], distances)) 

def geneticSelection(population):               # parent selection (GA)
    if selectionMethod == 1:           # Tournament (2)
        return min(random.sample(population, 2), key=lambda p: p[1])
    
    if selectionMethod == 2:           # Tournament (3)
        return min(random.sample(population, 3), key=lambda p: p[1])

    if selectionMethod == 3:           # Roulette
        diff = max(i[1] for i in population)+1
        total = sum(diff-i[1] for i in population)
        threshhold = random.uniform(0.0, total-1)
        tmpSum = 0
        for i in range(populationSize):
            tmpSum+=diff-population[i][1]
            if tmpSum > threshhold:
                return population[i]

def geneticCrossover(p1, p2):                   # Order Crossover (OX) for genetic algorithm
    children = [[],[]]
    parents = [p1,p2]
    start,end = [random.randrange(0,numOfCities//2),random.randrange(numOfCities//2,numOfCities)]

    for i in range(2):
        childInterSection=parents[i][start:end]
        parentSeq=parents[1-i][end:]+parents[1-i][:end]
        for val in childInterSection:
            parentSeq.remove(val)
        childInterSection+=parentSeq[:numOfCities-end]
        children[i]+=parentSeq[numOfCities-end:]
        children[i]+=childInterSection
    return children

def geneticMutation(chrom):                     # mutation for genetic algorithm
    for _ in range(numOfCities//2):
        if random.random() < mutationRate:
            randIndex1 = random.randrange(0,numOfCities)
            randIndex2 = random.randrange(0,numOfCities)
            while randIndex2 == randIndex1:
                randIndex2 = random.randrange(0,numOfCities)
            swap(chrom,randIndex1, randIndex2)

def geneticAlgorithm(cities, distances):
    population = [[generateCitySequence()] for i in range(populationSize)]          # create initial population with random chromosomes
    evaluatePopulation(population, distances)
    bestInitial = min(population, key=lambda p: p[1])
    averageInitial = sum(el[1] for el in population)/populationSize
    graphData=[]; lastBest=0
    for genN in range(maxGenerations):

        newPopulation = []
        while len(newPopulation)<populationSize:                                    # creation of new population
            p1,p2 = geneticSelection(population),geneticSelection(population)       # select parents
            children = geneticCrossover(p1[0],p2[0])                                # create children using crossover on parents
            for child in children:                                              
                geneticMutation(child)                                              # mutate child chromosomes with probability (mutationRate)
                if len(newPopulation)<populationSize:
                    newPopulation.append([child])                                   # add child to the new population

        population = newPopulation
        evaluatePopulation(population, distances)                                   # evaluate population
        bestChrom = min(population, key=lambda p: p[1])
        average = sum(el[1] for el in population)/populationSize
        WorstChrom = max(population, key=lambda p: p[1])[1]
        if showLiveAnimation and bestChrom[1] < lastBest:
            graphData.append((bestChrom[1],bestChrom[0].copy()))
        # print(" > Gen# %4d Best Distance: %7.2f   Worst Distance: %7.2f   Average Distance: %7.2f     Mutation Rate: %7.2f" %(genN+1, bestChrom[1], WorstChrom[1], average, mutationRate)) 
        lastBest = bestChrom[1]

    return bestInitial[1],bestInitial[0],bestChrom[1],bestChrom[0], graphData, averageInitial, average


def simulatedAnnealingSearch(cities, distances):
    initialSequence = generateCitySequence()                                        # create initial sequence
    seq = initialSequence.copy()
    bestSeqCost = initialSequenceCost = getPathCost(initialSequence, distances)
    temp = initialTemp
    SeqData = []
    absZero = 1-coolingRate                                                         # termination temperature

    while temp > absZero:
        randIndex1 = random.randint(0,numOfCities-1)
        seqCost = getPathCost(seq, distances)
        randIndex2 = random.randint(0,numOfCities-1)
        while randIndex2 == randIndex1:
            randIndex2 = random.randint(0,numOfCities-1)
        
        swap(seq, randIndex1, randIndex2)                                           # create a neighbour by swaping two random cities
        neighbourCost = getPathCost(seq, distances)

        numba = random.random()
        if (numba < acceptProbability(neighbourCost, seqCost, temp)):               # move to the neighbour if is accepted by the probability
            seqCost = neighbourCost
        else:
            swap(seq, randIndex1, randIndex2)

        if showLiveAnimation and seqCost < bestSeqCost:
            SeqData.append((seqCost, seq.copy()))
            bestSeqCost = seqCost

        temp*=coolingRate                                                           # update temperature

    return initialSequenceCost, initialSequence, seqCost, seq, SeqData

def tabuSearch(cities, distances):
    count = 0
    tabuList = []
    initialSequence = generateCitySequence()                                        # create initial sequence
    initialSequenceCost = getPathCost(initialSequence, distances)
    bestSeqCost = bestNeighbourCost = initialSequenceCost
    bestNeighbourSeq = initialSequence.copy()
    bestSequence = initialSequence.copy()
    bestSeqData = []
    tabuSize = numOfCities*tabuSizeKoef                                             # tabu list size = k*N

    while count < iterations:
        count += 1
        seq = bestNeighbourSeq.copy()
        bestNeighbourCost = -1
        for i in range(numOfCities):                                                # generate neighbourhood by swapping each city with random city
            
            randIndex = random.randint(0,i+1)
            swap(seq, i, randIndex if randIndex<numOfCities else 0)

            seqCost = getPathCost(seq,distances)
            if (seq not in tabuList) and (seqCost < bestNeighbourCost or bestNeighbourCost == -1):  # find the best neighbour in the neighbourhood which is not in the tabu list
                bestNeighbourCost = seqCost
                bestNeighbourSeq = seq.copy()
            
            swap(seq, i, randIndex if randIndex<numOfCities else 0)

        if bestNeighbourCost < bestSeqCost:                                         # if the best neighbour is better than best found result, change it to the neighbour
            bestSeqCost = bestNeighbourCost
            bestSequence = bestNeighbourSeq.copy()
            if showLiveAnimation:
                bestSeqData.append((bestSeqCost, bestSequence.copy()))
            
        tabuList.append(bestNeighbourSeq.copy())                                    # add the best neighbour to the tabulist

        if len(tabuList) > tabuSize:                                                # if the size of the tabu list exceeded its limit, remove the first element (oldest sequence)
            tabuList.pop(0)
        
    return initialSequenceCost, initialSequence, bestSeqCost, bestSequence, bestSeqData


################################################################
# Test function to find the best parameters for each algorithm #
################################################################
def test1(testSize, saveFile = False):
    global numOfCities, iterations, tabuSizeKoef, initialTemp, coolingRate, populationSize, mutationRate, selectionMethod

    for numOfCities in [10,20,30,50]:
        cities, distances = generateCityMatrix(numOfCities)
        print(" ################## Tabu Search #####################")
        fig, (ax1,ax2) = plt.subplots(1,2, figsize=(16,6))
        fig.subplots_adjust(wspace=0.5)
        fig.suptitle("Tabu Search - "+str(numOfCities)+" miest")
        for iterations in [100, 250, 500, 1000, 5000]:
            timeData=[]
            distData=[]
            for tabuSizeKoef in [1/2, 2/3, 1, 3/2, 2, 5]:
                start = time.time()
                data=[0,0,0]
                for _ in range(testSize):
                    inSeqCost,inSeq,bestSeqCost,bestSeq,grap = tabuSearch(cities, distances)
                    data[0]+=inSeqCost
                    data[1]+=bestSeqCost
                end = time.time()
                data[2] = end-start
                for index in range(3):
                    data[index]/=testSize
                timeData.append(data[2])
                distData.append(bestSeqCost)
                print("%d,%d,%f,%.3f,%.3f,%.4f"%(numOfCities,iterations,tabuSizeKoef,data[0],data[1],data[2]))
            
            ax1.plot(["1/2", "2/3", "1", "3/2", "2", "5"], timeData, '-o',label=str(iterations))
            ax1.yaxis.set_major_formatter(FormatStrFormatter('%.3fsec'))
            ax1.legend(title="Iterácie",loc='upper left')
            ax1.set_xlabel("Tabu list koeficient")
            ax1.set_ylabel("Čas (sekundy)")
           
            ax2.plot(["1/2", "2/3", "1", "3/2", "2", "5"], distData, '-o',label=str(iterations))
            ax2.yaxis.set_major_formatter(FormatStrFormatter('%.2fkm'))
            ax2.legend(title="Iterácie",loc='upper right')
            ax2.set_xlabel("Tabu list koeficient")
            ax2.set_ylabel("Najkratšia vzdialenosť (km)")
        print(" ################## /\ /\ /\ /\ #####################")
        print()
        if saveFile:
            fig.savefig('images/tabu'+str(numOfCities)+'.png')
        plt.clf()
        fig, (ax1,ax2) = plt.subplots(1,2, figsize=(16,6))
        fig.subplots_adjust(wspace=0.5)
        fig.suptitle("Simulované žíhanie - "+str(numOfCities)+" miest")
        print(" ################## Simulated Annealing #####################")
        for initialTemp in [0.1, 1, 10, 100, 1000]:
            timeData=[]
            distData=[]
            for coolingRate in [0.9,0.99,0.999,0.9999]:
                start = time.time()
                data=[0,0,0]
                for _ in range(testSize):
                    inSeqCost,inSeq,bestSeqCost,bestSeq,grap = simulatedAnnealingSearch(cities, distances)
                    data[0]+=inSeqCost
                    data[1]+=bestSeqCost
                end = time.time()
                data[2] = end-start
                for index in range(3):
                    data[index]/=testSize
                timeData.append(data[2])
                distData.append(bestSeqCost)
            ax1.plot(['0.9','0.99','0.999','0.9999'], timeData, '-o',label=str(initialTemp))
            ax1.yaxis.set_major_formatter(FormatStrFormatter('%.3fsec'))
            ax1.legend(title="Počiatočná teplota",loc='upper left')
            ax1.set_xlabel("Koeficient ochladzovania")
            ax1.set_ylabel("Čas (sekundy)")
        
            ax2.plot(['0.9','0.99','0.999','0.9999'], distData, '-o',label=str(initialTemp))
            ax2.yaxis.set_major_formatter(FormatStrFormatter('%.2fkm'))
            ax2.legend(title="Počiatočná teplota",loc='upper right')
            ax2.set_xlabel("Koeficient ochladzovania")
            ax2.set_ylabel("Najkratšia vzdialenosť (km)")
            print("%d,%f,%f,%.3f,%.3f,%.4f"%(numOfCities,initialTemp,coolingRate,data[0],data[1],data[2]))
        print(" ################## /\ /\ /\ /\ #####################\n")
        if saveFile:
            fig.savefig('images/annealing'+str(numOfCities)+'.png')
        print(" ################## Genetic Algorithm #####################")
        for selectionMethod in [1,2,3]:
            plt.clf()
            fig, (ax1,ax2) = plt.subplots(1,2, figsize=(16,6))
            fig.subplots_adjust(wspace=0.5)
            fig.suptitle(("Genetický algoritmus - "+str(numOfCities)+" miest, ")+("Tournament(2)" if selectionMethod==1 else "Tournament(3)" if selectionMethod==2  else "Roulette")+" selection")
            for populationSize in [25, 50, 75, 100]:
                timeData=[]
                distData=[]
                for mutationRate in [0.0, 0.01, 0.02, 0.03]:
                    
                        start = time.time()
                        data=[0,0,0,0,0]
                        for _ in range(testSize//5):
                            datas = geneticAlgorithm(cities, distances)
                            for index, el in enumerate(datas):
                                data[index]+=el
                        end = time.time()
                        data[4] = end-start
                        for index in range(len(data)):
                            data[index]/=testSize//5
                        timeData.append(data[4])
                        distData.append(data[2])
                        print("%d,%d,%d,%d,%f,%.3f,%.3f,%.3f,%.3f,%.4f"%(numOfCities,populationSize,maxGenerations,selectionMethod,mutationRate,data[0],data[1],data[2],data[3],data[4]))

                ax1.plot(['0.0', '0.01', '0.02', '0.03'], timeData, '-o',label=str(populationSize))
                ax1.yaxis.set_major_formatter(FormatStrFormatter('%.3fsec'))
                ax1.legend(title="Veľkosť populácie",loc='upper left')
                ax1.set_xlabel("Pravdepodobnosť mutácie")
                ax1.set_ylabel("Čas (sekundy)")
            
                ax2.plot(['0.0', '0.01', '0.02', '0.03'], distData, '-o',label=str(populationSize))
                ax2.yaxis.set_major_formatter(FormatStrFormatter('%.2fkm'))
                ax2.legend(title="Veľkosť populácie",loc='upper right')
                ax2.set_xlabel("Pravdepodobnosť mutácie")
                ax2.set_ylabel("Najkratšia vzdialenosť (km)")

            if saveFile:
                fig.savefig('images/genetic'+str(numOfCities)+'-'+str(selectionMethod)+'.png')
            plt.clf()
        print(" ################## /\ /\ /\ /\ #####################\n")


##################################################################
# Test function to compare all algorithms with preset parameters #
##################################################################
def test2(testSize, saveFile=False):
    global numOfCities, iterations, tabuSizeKoef, initialTemp, coolingRate,  populationSize, mutationRate, selectionMethod

    initialTemp=100
    coolingRate=0.999

    tabuSizeKoef=1
    iterations=500

    populationSize=50
    mutationRate=0.01
    selectionMethod=2

    timeData = [[[] for i in range(2)] for j in range(3)]
    plotData = [[[] for i in range(2)] for j in range(3)]
    for numI,numOfCities in enumerate([10,20,30,50]):
        cities, distances = generateCityMatrix(numOfCities)
        for index,f in enumerate([tabuSearch, simulatedAnnealingSearch, geneticAlgorithm]):
            start = time.time()
            distTotal = 0
            for _ in range(testSize):
                data = f(cities, distances)
                distTotal+=data[2]    
            end = time.time()
            timeData[index][0].append((end-start)/testSize)
            plotData[index][1].append(distTotal/testSize)
            print(index," - ",numOfCities, (end-start)/testSize, distTotal/testSize)

    fig, (ax1,ax2) = plt.subplots(1,2, figsize=(16,6))
    for i,f in enumerate(["Tabu Search","Simulované žíhanie","Genetický algoritmus"]):
        ax1.plot(['10','20','30','50'], timeData[i][0], '-o',label=f)
        ax1.yaxis.set_major_formatter(FormatStrFormatter('%.3fsec'))
        ax1.legend(title="Algoritmus",loc='upper left')
        ax1.set_xlabel("Počet miest")
        ax1.set_ylabel("Čas (sekundy)")
            
        ax2.plot(['10','20','30','50'], plotData[i][1], '-o',label=f)
        ax2.yaxis.set_major_formatter(FormatStrFormatter('%.2fkm'))
        ax2.legend(title="Algoritmus",loc='upper left')
        ax2.set_xlabel("Počet miest")
        ax2.set_ylabel("Najkratšia vzdialenosť (km)")

    plt.show()
    if saveFile:
        fig.savefig('images/comparison.png')

##########################################################
#                          MAIN                          #
##########################################################
def main():
    random.seed()
    index = 0
    cities, distances = showUI()

    timeData = {"TS":[],"SA":[],"GA":[]}
    graphData = {"TS":[],"SA":[],"GA":[]}

    start = time.time()
    graphData["TS"] = tabuSearch(cities, distances)
    end = time.time()
    timeData["TS"] = round(end - start, 3)

    start = time.time()
    graphData["SA"] = simulatedAnnealingSearch(cities, distances)
    end = time.time()
    timeData["SA"] = round(end - start, 3)

    start = time.time()
    graphData["GA"] = geneticAlgorithm(cities, distances)
    end = time.time()
    timeData["GA"] = round(end - start, 3)

    print("Vysledky pre [",numOfCities,"] miest:")
    for key,value in graphData.items():
        if len(value) > 0:
            print(" > %-20s\t Trvanie: %.4fsec, Vzdielenost: %.3fkm"%({"TS":"Tabu Search", "SA":"Simulovane Zihanie","GA":"Geneticky Algoritmus"}[key],timeData[key],value[2]))

    if (showFinalGraphs or showLiveAnimation):
        numOfRows = len(graphData.values())-list(graphData.values()).count([])
        numOfRows=3
        if numOfRows == 0:
            return
        fig, axs = plt.subplots(numOfRows, 2, figsize=(10, 1+3*numOfRows))
        nameData = {"TS":"Tabu Search", "SA":"Simulovane Zihanie","GA":"Geneticky Algoritmus"}
        plt.subplots_adjust(top=0.92, bottom=0.08, hspace=0.6, wspace=0.3)
        for rowIndex, axes in enumerate(axs):  
            GRI = list(graphData.keys())[rowIndex]
            if numOfRows-rowIndex == 0:
                break
            plt.figtext(0.5, (1 - (1/numOfRows)*rowIndex - (numOfRows-rowIndex-1)*0.02) if numOfRows>1 else 0.975, nameData[GRI]+" - "+str(timeData[GRI])+" sec", size="large", ha='center', va='center')
            try:
                len(axes)
            except:
                axes=axs
            
            for colIndex, ax in enumerate(axes):
                if showLiveAnimation and colIndex==1:
                    arrSize = len(graphData[GRI][4])
                    indexx = 1
                    while arrSize > 30:
                        if indexx >= arrSize:
                            indexx = 1
                        graphData[GRI][4].pop(indexx)
                        indexx+=arrSize//30
                        arrSize-=1
                    pauseDur = 5/len(graphData[GRI][4]) if 5/len(graphData[GRI][4]) < 0.01 else 0.01
                    for csst,sseq in graphData[GRI][4]:
                        ax.plot(*zip(*[cities[x] for x in sseq] + [cities[sseq[0]]]), '-o')
                        ax.set_title("Best Sequence - "+str(csst)+" km")
                        plt.savefig("2gif/"+str(index)+".png")
                        index+=1
                        plt.pause(pauseDur)
                        ax.cla()
                ax.plot(*zip(*[cities[x] for x in graphData[GRI][2*colIndex+1]] + [cities[graphData[GRI][2*colIndex+1][0]]]), '-o')
                ax.set_title(("Initial Sequence - " if colIndex==0 else "Best Sequence - ")+str(graphData[GRI][2*colIndex])+" km")         
        plt.show()


if __name__ == "__main__":
    main()
    # test1(10, True)
    # test2(100)