planesList = []

def addPlane(planesList, name, takeoffTime, landingTime):
    totalTime = takeoffTime + landingTime
    planesList.append([name, totalTime])

def sortPlanes(planesList):
    n = len(planesList)
    for i in range(n - 1):
        for j in range(n - i - 1):
            if planesList[j][1] > planesList[j + 1][1]:
                planesList[j], planesList[j + 1] = planesList[j + 1], planesList[j]

def resolveTies(planesList):
    for i in range(len(planesList) - 1):
        if planesList[i][1] == planesList[i + 1][1]:
            print(f"There is a tie between {planesList[i][0]} and {planesList[i+1][0]}")
            w1 = int(input(f"Enter the weight of plane {planesList[i][0]}: "))
            w2 = int(input(f"Enter the weight of plane {planesList[i+1][0]}: "))
            if w1 > w2:
                planesList[i], planesList[i + 1] = planesList[i + 1], planesList[i]

def displaySchedule(planesList):
    print("Planes sorted by SJF (Shortest Job First):")
    for plane in planesList:
        print(f"Plane {plane[0]} - Total Runway Usage Time: {plane[1]} seconds")

n = int(input("Enter the number of planes: "))

for i in range(n):
    name = input(f"Enter the name of plane {i+1}: ")
    takeoffTime = int(input(f"Enter the takeoff time for {name} (in seconds): "))
    landingTime = int(input(f"Enter the landing time for {name} (in seconds): "))
    addPlane(planesList, name, takeoffTime, landingTime)

sortPlanes(planesList)
resolveTies(planesList)
displaySchedule(planesList)