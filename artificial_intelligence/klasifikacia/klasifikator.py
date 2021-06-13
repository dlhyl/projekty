import matplotlib.pyplot as plt
import random
from math import dist
import time

minX = -5000
maxX = 5000
minY = -5000
maxY = 5000

points = {}

class Point:
    def __init__(self, x, y, obj):
        self.x = x
        self.y = y
        self.classification = obj

class PointClassification:
    def __init__(self):
        self.color=""
    def __hash__(self):
        return hash(self.color)
    def __eq__(self, another):
        return hasattr(another, 'color') and self.color == another.color

class R(PointClassification):
    def __init__(self):
        self.points = [Point(x,y,self) for x,y in [[-4500, -4400], [-4100, -3000], [-1800, -2400], [-2500, -3400], [-2000, -1400]]]
        self.color = "red"

class G(PointClassification):
    def __init__(self):
        self.points = [Point(x,y,self) for x,y in [[+4500, -4400], [+4100, -3000], [+1800, -2400], [+2500, -3400], [+2000, -1400]]]
        self.color = "green"

class B(PointClassification):
    def __init__(self):
        self.points = [Point(x,y,self) for x,y in [[-4500, +4400], [-4100, +3000], [-1800, +2400], [-2500, +3400], [-2000, +1400]]]
        self.color = "blue"

class P(PointClassification):
    def __init__(self):
        self.points = [Point(x,y,self) for x,y in [[+4500, +4400], [+4100, +3000], [+1800, +2400], [+2500, +3400], [+2000, +1400]]]
        self.color = "purple"

""" 
    Funkcia classify postupne pocita vzdialenosti k vsetkym uz klasifikovanym bodom a priebezne si uklada k najlepsich.
    Potom vyberie najpocetnejsiu triedu z k najlepsich a vrati ju.
"""
def classify(X,Y,k):
    k_nearest = {}; k_nearest_count = 0; k_max_value = ()
    for pointCoords,pointClass in points.items():
        distance = dist((X,Y), pointCoords)
        if k_nearest_count < k:
            k_nearest_count+=1
            k_nearest[pointCoords] = (distance,pointCoords,pointClass)
            k_max_value = max(k_nearest.values(), key=lambda x: x[0])
        elif distance < k_max_value[0]:
            k_nearest.pop(k_max_value[1])
            k_nearest[pointCoords] = (distance,pointCoords,pointClass)
            k_max_value = max(k_nearest.values(), key=lambda x: x[0])

    counts = {}
    for index, (d,p,pointClass) in enumerate(k_nearest.values()):
        try:
            counts[pointClass]+=1
        except KeyError:
            counts[pointClass]=1
    return max(counts.items(), key=lambda x: x[1])[0]


"""
    Funkcia na generovanie bodov kazdej zo 4 tried bez opakovania suradnic s danymi podmienkami (generovanie v rozsahu s pravdepodobnostou)
    Vracia pole bodov.
"""
def generateNewPoints(classifications, numOfEachClassification):
    random.shuffle(classifications)
    pointHS = set([(point.x,point.y) for classification in classifications for point in classification.points])
    points = []
    for _ in range(numOfEachClassification):
        for classification in classifications:
            xRange = minX,maxX; yRange = minY,maxY
            if isinstance(classification, R) and random.random()<0.99: # X < +500 a Y < +500
                xRange = minX,499; yRange = minY,499

            if isinstance(classification, G) and random.random()<0.99: # X > −500 a Y < +500
                xRange = -499,maxX; yRange = minY,499

            if isinstance(classification, B) and random.random()<0.99: # X < +500 a Y > −500
                xRange = minX,499; yRange = -499,maxY

            if isinstance(classification, P) and random.random()<0.99: # X > −500 a Y > −500
                xRange = -499,maxX; yRange = -499,maxY

            (x,y) = random.randint(xRange[0],xRange[1]),random.randint(yRange[0],yRange[1])  # X < +500 a Y < +500
            while (x,y) in pointHS:
                (x,y) = random.randint(xRange[0],xRange[1]),random.randint(yRange[0],yRange[1])
            pointHS.add((x,y))
            points.append(Point(x,y,classification))
    return points

def main():
    global points

    numOfPointsEachClass=5000
    pocetPokusov = 1
    zobrazitGrafy = True

    while True:
        choice = int(input("Vyberte moznost:\n(1) - spustenie s predvolenymi parametrami [#%d bodov, %d pokus(ov), %s]\n(2) - vlastne parametre\n" %(numOfPointsEachClass*4,pocetPokusov,"Zobrazit grafy" if zobrazitGrafy else "Ulozit grafy na disk")))
        if choice == 1:
            break
        elif choice == 2:
            numOfPointsEachClass = int(input("Zadajte pocet bodov pre jednu triedu: \n"))
            pocetPokusov = int(input("Zadajte pocet pokusov pre jeden experiment: \n"))
            zobrazitGrafy = bool(int((input("Zadajte 1 pre zobrazenie grafov a 0 pre ulozenie grafov na disk: \n"))))
            break
        else:
            print("Zadali ste zly vstup.")
    
    r,g,b,p = R(), G(), B(), P()

    for pokus in range(1,pocetPokusov+1):

        newPoints = generateNewPoints([r,g,b,p],numOfPointsEachClass)
        print("Vygenerovalo sa %d bodov."%(numOfPointsEachClass*4))
        colormap = {"red":"darkred","green":"mediumseagreen","blue":"lightskyblue","purple":"mediumorchid"}

        
        for k in [1,3,7,15]:

            """
                Klasifikacia vsetkych bodov.
            """
            start = time.time()
            sameClassification=0
            classifyingPoints=[Point(point.x,point.y,point.classification) for point in newPoints]
            points = dict(zip([(point.x,point.y) for classification in [r,g,b,p] for point in classification.points],[point.classification for classification in [r,g,b,p] for point in classification.points]))
            for index,point in enumerate(classifyingPoints):
                newClassification = classify(point.x,point.y,k)
                if newClassification == point.classification:
                    sameClassification+=1
                else:
                    point.classification=newClassification
                points[(point.x,point.y)]=point.classification

            duration = time.time() - start
            successrate = sameClassification/(numOfPointsEachClass*4)

            print(" Pocet bodov = ",numOfPointsEachClass*4,", k = ",k)
            print(" Duration = ",duration,"sec")
            print(" Success rate = ",successrate*100,"%")
            
            plt.figure(figsize=(9,8))
            plt.clf()
            plt.xlim(left=minX,right=maxX)
            plt.ylim(top=maxY,bottom=minY)
            plt.xticks([i for i in range(minX, maxX+1, 1000)])
            plt.yticks([i for i in range(minY, maxY+1, 1000)])

            plt.title("K = %d, #%d points, success rate = %.2f%%\ntime elapsed = %.2fsec"%(k,numOfPointsEachClass*4,successrate*100, duration))
            plt.scatter([point[0] for point in points.keys()],[point[1] for point in points.keys()],c=[colormap[classif.color] for classif in points.values()])
            if not zobrazitGrafy:
                plt.savefig(str(pokus)+"_"+str(k)+"-"+str(numOfPointsEachClass*4))

        if zobrazitGrafy:
            plt.show()

if __name__ == "__main__":
    main()