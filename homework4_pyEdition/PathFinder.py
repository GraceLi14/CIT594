import math
import heapq
import sys

class PathFinder:
    #constructor
    def __init__(self):
        #adjacency list of nodes and their neighbors
        self.graph = {}
        #maps each stop id to its respective node object
        self.stations = {}

    class Station:
        """
        Represents a station
        id = unique stop_id from CSV
        name = station name
        latitude = station latitude
        longitude = station longitude
        """
        #constructor
        def __init__(self, id, name, latitude, longitude):
            #stop id
            self.id = id
            #stop name
            self.name = name
            #latitude of station location
            self.lat = latitude
            #logitutde of station location
            self.lon = longitude


    def loadStations(self, nodesFile):
        '''
        Reads SEPTA nodes CSV files and builds a map of stop_id to station information
        :param nodesFile: nodesFile name of CSV file containing station information
        '''

        #read through CSV
        with open(nodesFile, 'r') as file:
            #skip header
            file.readline()
            #read next line
            line = file.readline()

            #read lines until the end
            while line is not None and line != '':
                #formatting
                line = line.strip()
                #as long as data is not empty, split the data by its commas
                if line != '':
                    data = line.split(',')
                    #as long as the data has 4 components, assign each data component to its respective label
                    if len(data) == 4:
                        stationID = data[0].strip()
                        name = data[1].strip()
                        latitude = float(data[2].strip())
                        longitude = float(data[3].strip())
                        #add station to the map
                        self.stations[stationID] = self.Station(stationID, name, latitude, longitude)

                #read the next line
                line = file.readline()


    def loadEdges(self, edgesFile):
        '''
        Reads SEPTA edges CSV file and builds an undirected weight graph
        Outer key is station stop_id
        Inner keys are neighboring stations' stop_id
        Each inner value is Haversine distance between two stations
        :param edgesFile:name of CSV file containing edge data
        '''

        #make sure station data loaded first
        if self.stations == {}:
            raise ValueError('No stations loaded')

        #read through CSV
        with open(edgesFile, 'r') as file:
            #skip header
            file.readline()
            #read next line
            line = file.readline()

            #read lines until the end
            while line is not None and line != '':
                #formatting
                line = line.strip()
                # as long as data is not empty, split the data by its commas
                if line != '':
                    data = line.split(',')
                    #as long as the data has 2 components, assign each data component to its respective label
                    if len(data) == 2:
                        sourceID = data[0].strip()
                        targetID = data[1].strip()

                        #lookup Station object for source station and target station respectively
                        targetStation = self.stations.get(targetID)
                        sourceStation = self.stations.get(sourceID)

                        #if stations both exist
                        if sourceStation is not None and targetStation is not None:
                            #calculate straight-line distance between two stations
                            distance = self.haversine(sourceStation.lat, sourceStation.lon, targetStation.lat, targetStation.lon)

                            #if graph does not have source station, create empty neighbor map for it
                            if sourceID not in self.graph:
                                self.graph[sourceID] = {}

                            #if graph does not have target station, create empty neighbor map for it
                            if targetID not in self.graph:
                                self.graph[targetID] = {}

                            #add edge from source to target with its Haversine distance
                            self.graph[sourceID][targetID] = distance
                            #add edge from target to source with its Haversine distance
                            self.graph[targetID][sourceID] = distance

                #read the next line
                line = file.readline()




    def haversine(self, latA, lonA, latB, lonB):
        '''
        Calculates Haversine distance in kilometers between two points on Earth

        :param lat1: latitude of first point in degrees
        :param lon1: longitude of first point in degrees
        :param lat2: latitude of second point in degrees
        :param lon2: longitude of second point in degrees
        :return: Haversine distance in kilometers
        '''

        #radius of earth in km
        earthRadius = 6371

        #convert differences in latitude and longitude from degrees to radians
        dLat = math.radians(latB - latA)
        dLon = math.radians(lonB - lonA)

        #convert original latitudes from degrees to radians
        lat1Rad = math.radians(latA)
        lat2Rad = math.radians(latB)

        # apply Haversine formula
        a = (math.sin(dLat / 2) ** 2) + \
            (math.cos(lat1Rad) * math.cos(lat2Rad) * (math.sin(dLon / 2) ** 2))

        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

        # distance = earth radius × central angle
        return earthRadius * c

    def aStar(self, startStationID, targetStationID):
        """
        Run A* algorithm to find the shortest path from startStationID to targetStationID
        :param startStationID: start station ID
        :param targetStationID: target station ID
        :return: "NONE" if no valid paths OR return two-line string:
        line 1 = String of shortest path of stop_ids strung together by arrows
        line 2 = total path distance rounded to 2 decimal places
        """
        #stores map of each station to the distance from start
        distance = {}
        #stores each station's total cost which is distance + heuristic
        estimate = {}
        #maps each station to previous station in best known path
        predecessor = {}

        #initialize all stations with infinite distance and estimate and no predecessor
        for station in self.stations.keys():
            distance[station] = math.inf
            estimate[station] = math.inf
            predecessor[station] = None

        #if startStationID or targetStationID not in stations map, print "NONE" and return
        if startStationID not in self.stations or targetStationID not in self.stations:
            return "NONE"

        #if start and target station are the same, print just the station with distance of 0
        if startStationID == targetStationID:
            return startStationID + "\n0.00"

        #distance from start station to itself is 0
        distance[startStationID] = 0.0

        #get both start and target station objects
        startStation = self.stations[startStationID]
        targetStation = self.stations[targetStationID]

        #calculate heuristic from start station directly to target station
        haversineSourceTarget = self.haversine(startStation.lat, startStation.lon, targetStation.lat, targetStation.lon)

        #set start station's estimated total cost
        estimate[startStationID] = haversineSourceTarget

        #keep track of stations fully processed
        visited = set()

        #min-heap ordered by smallest estimated total cost then alphabetical station ID
        toVisit = []

        #pushing each station total cost estimate and its respective station ID as value into the min-heap
        #python compares tuples in order of first element (estimate) and then second element (alphabetical)
        for stationID in estimate.keys():
            heapq.heappush(toVisit, (estimate[stationID], stationID))

        #continue until no stations to process
        while toVisit:
            #remove station with the smallest estimate
            currentStationID = heapq.heappop(toVisit)[1]

            #if station already visited/processed, skip
            if (currentStationID in visited):
                continue

            #if best remaining estimate is infinity, no reachable path exists to target
            if(estimate[currentStationID] == math.inf):
                break

            #if target is reached, stop search
            if(currentStationID == targetStationID):
                break

            #mark current station as processed
            visited.add(currentStationID)

            #if current station has no neighbors, skip it
            if(currentStationID not in self.graph):
                continue

            #get all neighboring stations to current station
            neighbors = self.graph[currentStationID]
            #iterate through all neighbors to try relaxing each one
            for neighborID in neighbors.keys():
                #if already visited/processed, skip
                if neighborID in visited:
                    continue

                #store neighbor's old estimate so we can see if there's an improvement
                oldEstimate = estimate[neighborID]

                #attempt to improve path to neighbor
                self.relaxAStar(currentStationID, neighborID, targetStationID, distance, estimate, predecessor)

                #if neighbor estimate improved, add back into heap
                if(estimate[neighborID] < oldEstimate):
                    heapq.heappush(toVisit,(estimate[neighborID], neighborID))

        #if target has no predecessor, no path from start to target was found so print "NONE"
        if predecessor.get(targetStationID) is None:
            return "NONE"

        #set currentNode as target station
        currentNode = targetStationID
        #set pathToTarget as list
        pathToTarget = []

        #reconstruct path backward from target
        while currentNode is not None:
            pathToTarget.insert(0,currentNode)
            currentNode = predecessor[currentNode]

        #join list together with -> and convert to String
        pathString = "->".join(pathToTarget)
        #return string of path and distance in km (2 decimal places)
        return pathString + "\n" + format(distance[targetStationID], ".2f")

    def relaxAStar(self, nodeCurrent, nodeVisited, targetStationID, distance, estimate, predecessor):
        '''
        Going from start node to nodeVisited through nodeCurrent gives a shorter path than the currently known one.
        This method updates distance, estimate and predecessor.
        :param nodeCurrent: current station being explored
        :param nodeVisited: neighboring station of nodeCurrent
        :param targetStationID: destination station ID
        :param distance: maps each station to its current best known distance from the source
        :param estimate: maps each station to its current estimated total cost
        :param predecessor: maps each station to the previous station on the best known path
        '''
        #station object for neighbor of current station
        visitedStation = self.stations[nodeVisited]
        #station object for target station
        targetStation = self.stations[targetStationID]
        #get weight of edge between nodeCurrent and nodeVisited
        weight = self.graph[nodeCurrent][nodeVisited]

        #if going through nodeCurrent is shorter than to nodeVisited, update path information for nodeVisited
        if(distance[nodeVisited] > distance[nodeCurrent] + weight):
            #update shortest known distance to nodeVisited
            distance[nodeVisited] = distance[nodeCurrent] + weight
            #update estimated total cost which is actual distance from source to nodeVisited + heuristic distance from nodeVisited to targetStation
            estimate[nodeVisited] = distance[nodeVisited] + self.haversine(visitedStation.lat, visitedStation.lon, targetStation.lat, targetStation.lon)
            #record best path to nodeVisited comes through nodeCurrent
            predecessor[nodeVisited] = nodeCurrent

    if __name__ == "__main__":
        # autograder expects exactly 5 arguments after the script name
        if len(sys.argv) != 6:
            print("NONE")
            sys.exit()

        # read command-line arguments
        nodesFile = sys.argv[1]
        edgesFile = sys.argv[2]
        command = sys.argv[3]
        source_id = sys.argv[4]
        target_id = sys.argv[5]

        # only valid command for this assignment is "astar"
        if command != "astar":
            print("NONE")
            sys.exit()

        # build graph and run A* search
        finder = PathFinder()
        finder.loadStations(nodesFile)
        finder.loadEdges(edgesFile)

        # print either the two-line result or NONE
        print(finder.aStar(source_id, target_id))








