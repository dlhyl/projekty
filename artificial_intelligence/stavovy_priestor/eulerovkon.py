import time

RED = "\u001b[31m"
RST = "\u001b[0m"

sachovnica = [[0 for i in range(7)] for j in range(7)]
steps = 0
visited = [[0 for i in range(7)] for j in range(7)]

def printSach(*args):
    for y in range(len(sachovnica)):
        for x in range(len(sachovnica[y])):
            if (len(args)>0 and args[0]==y and args[1]==x):
                print(RED+"%2d"%(sachovnica[y][x])+RST, end=" ")
            else:
                print("%2d"%(sachovnica[y][x]), end=" ")
        print()
    print()

def dfs(node_y, node_x, counter, dim_y, dim_x):
    global steps
    

    if counter[0] == dim_y*dim_x: 
        return True

    if visited[node_y][node_x] == 0:
        steps+=1
        if (sachovnica[node_y][node_x] == 0):
            counter[0] += 1
            sachovnica[node_y][node_x] = counter[0]
            #printSach(node_y, node_x)
        visited[node_y][node_x] = 1

        if (node_y - 2 >= 0 and node_x+1 < dim_x) and dfs(node_y - 2,node_x+1,counter,dim_y,dim_x):
            return True
        if (node_y - 1 >= 0 and node_x+2 < dim_x) and dfs(node_y - 1,node_x+2,counter,dim_y,dim_x):
            return True
        if (node_y + 1 < dim_y and node_x+2 < dim_x) and dfs(node_y + 1,node_x+2,counter,dim_y,dim_x):
            return True
        if (node_y + 2 < dim_y and node_x+1 < dim_x) and dfs(node_y + 2,node_x+1,counter,dim_y,dim_x):
            return True
        if (node_y + 2 < dim_y and node_x-1 >= 0) and dfs(node_y + 2,node_x-1,counter,dim_y,dim_x):
            return True
        if (node_y + 1 < dim_y and node_x-2 >= 0) and dfs(node_y + 1,node_x-2,counter,dim_y,dim_x):
            return True
        if (node_y - 1 >= 0 and node_x-2 >= 0) and dfs(node_y - 1,node_x-2,counter,dim_y,dim_x):
            return True 
        if (node_y - 2 >= 0 and node_x-1 >= 0) and dfs(node_y - 2,node_x-1,counter,dim_y,dim_x):
            return True

        visited[node_y][node_x] = 0
        sachovnica[node_y][node_x] = 0
        counter[0]-=1
    return False

start = time.time()
dfs(1,1,[0],7,7)
end = time.time()
printSach()
print(steps)
print((end-start).__round__(4),"s")
