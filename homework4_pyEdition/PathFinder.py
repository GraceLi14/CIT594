import math


class PathFinder:

    def __init__(self):
        self.graph = {}
        self.stations = {}

    class Station:
        def __init__(self, id, name, latitude, longitude):
            self.id = id
            self.name = name
            self.lat = latitude
            self.lon = longitude


    def loadStations(self, nodesFile):
        '''
        Reads SEPTA nodes CSV files and builds a map of stop_id to station information
        :param nodesFile: nodesFile name of CSV file containing station information
        '''
        with open(nodesFile, 'r') as file:
            #skip header
            file.readline()
            line = file.readline()

            while line is not None and line != '':
                line = line.strip()
                if line != '':
                    data = line.split(',')
                    if len(data) == 4:
                        stationID = data[0].strip()
                        name = data[1].strip()
                        latitude = float(data[2].strip())
                        longitude = float(data[3].strip())
                        self.stations[stationID] = self.Station(stationID, name, latitude, longitude)

                line = file.readline()


    def loadEdges(self, edgesFile):
        '''
        Reads SEPTA edges CSV file and builds an undirected weight graph
        Outer key is station stop_id
        Inner keys are neighboring stations' stop_id
        Each inner value is Haversine distance between two stations
        :param edgesFile:name of CSV file containing edge data
        '''
        if self.stations == {}:
            raise ValueError('No stations loaded')

        with open(edgesFile, 'r') as file:
            # skip header
            file.readline()
            line = file.readline()
            while line is not None and line != '':
                line = line.strip()
                if line != '':
                    data = line.split(',')
                    if len(data) == 2:
                        sourceID = data[0].strip()
                        targetID = data[1].strip()

                        targetStation = self.stations.get(targetID)
                        sourceStation = self.stations.get(sourceID)

                        if sourceStation is not None and targetStation is not None:
                            distance = self.haversine(sourceStation.lat, sourceStation.lon, targetStation.lat, targetStation.lon)

                            if sourceID not in self.graph:
                                self.graph[sourceID] = {}

                            if targetID not in self.graph:
                                self.graph[targetID] = {}

                            self.graph[sourceID][targetID] = distance
                            self.graph[targetID][sourceID] = distance

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

        earthRadius = 6371

        dLat = math.radians(latB - latA)
        dLon = math.radians(lonB - lonA)

        lat1Rad = math.radians(latA)
        lat2Rad = math.radians(latB)

        # apply Haversine formula
        a = (math.sin(dLat / 2) ** 2) + \
            (math.cos(lat1Rad) * math.cos(lat2Rad) * (math.sin(dLon / 2) ** 2))

        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

        # distance = earth radius × central angle
        return earthRadius * c




