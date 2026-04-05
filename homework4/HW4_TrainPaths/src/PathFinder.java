import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Builds a weighted graph of SEPTA stations and uses A* search
 * to find the shortest path between two stations.
 * Stations are loaded from septa_nodes.csv, edges are loaded from septa_edges.csv,
 * and edge weights are computed using Haversine distance in kilometers.
 */
public class PathFinder {
    //fields
    //adjacency list of nodes and their neighbors
    Map<String,Map<String, Double>> graph;
    //maps each stop id to its respective node object
    Map<String, Station> stations;

    //constructor
    public PathFinder() {
        this.graph = new HashMap<>();
        this.stations = new HashMap<>();
    }
    /**
     * Represents a station.
     * id = unique stop_id from the CSV
     * name = station name
     * latitude = station latitude
     * longitude = station longitude
     */
    static class Station{
        //stop id
        String id;
        //stop name
        String name;
        //latitude of station location
        double latitude;
        //longitude of station location
        double longitude;

        //constructor
        public Station(String id, String name, double latitude, double longitude) {
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /**
     * Reads SEPTA nodes CSV files and builds a map of stop_id to station information
     * @param nodesFile name of CSV file containing station information
     */
    public void loadStations(String nodesFile) {
        //read through CSV with BufferedReader
        try{
            BufferedReader br = new BufferedReader(new FileReader(nodesFile));
            //skip header
            br.readLine();
            //read next line
            String line = br.readLine();
            //read lines until the end
            while (line != null) {
                line = line.trim();
                if(!line.isEmpty()) {
                    //split data by comma
                    String[] data = line.split(",");
                    //assign each data component to its respective label
                    if(data.length == 4) {
                        String id = data[0].trim();
                        String name = data[1].trim();
                        double latitude = Double.parseDouble(data[2].trim());
                        double longitude = Double.parseDouble(data[3].trim());
                        //add station to the map
                        this.stations.put(id, new Station(id,name,latitude,longitude));
                    }
                }
                //read the next line
                line = br.readLine();
            }
            br.close();
        //if file name wrong or can't be found
        } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
        //catch any problem while reading file
        } catch (
        IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads SEPTA edges CSV files and builds undirected weighted graph.
     * Outer key is station stop_id.
     * Inner keys are neighboring stations' stop_id.
     * Each inner value is Haversine distance between two stations.
     *
     * @param edgesFile name of CSV file containing edge data
     */
    public void loadEdges(String edgesFile) {
        //make sure station data loaded first
        if (this.stations.isEmpty()) {
            throw new IllegalStateException("Stations must be loaded before edges.");
        }

        //read through CSV with BufferedReader
        try{
            BufferedReader br = new BufferedReader(new FileReader(edgesFile));
            //skip header
            br.readLine();
            //read next line
            String line = br.readLine();
            //read csv file until the end
            while (line != null) {
                line = line.trim();
                if(!line.isEmpty()) {
                    //split data by comma
                    String[] data = line.split(",");
                    //assign each data component to its respective label
                    if(data.length == 2) {
                        String source_id = data[0].trim();
                        String target_id = data[1].trim();

                        //lookup Station object for source station and target station respectively
                        Station sourceStation = stations.get(source_id);
                        Station targetStation = stations.get(target_id);

                        //if stations both exist
                        if(sourceStation != null && targetStation != null) {
                            //calculate straight-line distance between two stations
                            double distance =  haversine(sourceStation.latitude, sourceStation.longitude,
                                    targetStation.latitude, targetStation.longitude);

                            //if source station not in graph, create empty neighbor map for it
                            if(!this.graph.containsKey(source_id)) {
                                this.graph.put(source_id, new HashMap<>());
                            }
                            //if target station not in graph, create empty neighbor map for it
                            if(!this.graph.containsKey(target_id)) {
                                this.graph.put(target_id, new HashMap<>());
                            }

                            //add edge from source to target with its Haversine distance
                            this.graph.get(source_id).put(target_id, distance);
                            //add edge from target to source with its Haversine distance
                            this.graph.get(target_id).put(source_id, distance);

                        }
                    }
                }
                line = br.readLine();
            }
            br.close();
            //if file name wrong or can't be found
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
            //catch any problem while reading file
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates Haversine distance in kilometers between two points on Earth
     *
     * @param latA latitude of first point in degrees
     * @param lonA longitude of first point in degrees
     * @param latB latitude of second point in degrees
     * @param lonB longitude of second point in degrees
     * @return distance between two points in kilometers
     */
    public double haversine(double latA, double lonA, double latB, double lonB) {
        //radius of earth in km
        double earthRadius = 6371;

        // convert differences in latitude and longitude from degrees to radians
        double dLat = Math.toRadians(latB - latA);
        double dLon = Math.toRadians(lonB - lonA);

        // convert original latitudes from degrees to radians
        double lat1Rad = Math.toRadians(latA);
        double lat2Rad = Math.toRadians(latB);
        // apply Haversine formula
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // distance = earth radius × central angle
        return earthRadius * c;
    }

    /**
     * Run A* algorithm to find shortest path from start station to target station
     * @param startStationID start station ID
     * @param targetStationID target station ID
     * @return "NONE" if there are no valid paths or two-line string:
     * line 1 = String of shortest path of stop_ids strung together by arrows
     * line 2 = total path distance rounded to 2 decimals
     */
    public String aStar(String startStationID, String targetStationID) {
        //maps each station to the distance from start
        HashMap<String, Double> distance = new HashMap<>();
        //maps each station its total cost which is distance + heuristic
        HashMap<String, Double> estimate = new HashMap<>();
        //maps each station to previous station on the best known path
        HashMap<String, String> predecessor = new HashMap<>();

        //initialize all stations with infinite distance/estimate and no predecessor
        for (String stationID : this.stations.keySet()) {
            distance.put(stationID, Double.POSITIVE_INFINITY);
            estimate.put(stationID, Double.POSITIVE_INFINITY);
            predecessor.put(stationID, null);
        }

        //if either start or target station doesn't exist in the stations graph, print "NONE" and return
        if (!this.stations.containsKey(startStationID) || !this.stations.containsKey(targetStationID)) {
            return "NONE";
        }

        //if start and target stations are the same, print just the station with distance of 0
        if(startStationID.equals(targetStationID)) {
            return startStationID + "\n0.00";
        }

        //distance from start station to itself is 0
        distance.put(startStationID, 0.0);

        //look up station object for start station
        Station startStation = this.stations.get(startStationID);
        //look up station object for target station
        Station targetStation = this.stations.get(targetStationID);

        //calculate heuristic from start station directly to target station
        double haversineSourceTarget = haversine(
                startStation.latitude, startStation.longitude,
                targetStation.latitude, targetStation.longitude
        );

        //set the start station's estimated total cost
        estimate.put(startStationID, haversineSourceTarget);
        //keep track of stations fully prcoessed
        Set<String> visited = new HashSet<>();

        //min-heap ordered by smallest estimated total cost, then alphbetical station ID
        PriorityQueue<String> toVisit = new PriorityQueue<>(
                //if station a has smaller estimate, it should come first
                (a,b) -> {
                    if (estimate.get(a) < (estimate.get(b))) {
                        return -1;
                    //if station b has smaller estimate, a should come later
                    } else if (estimate.get(a) > (estimate.get(b))) {
                        return 1;
                    //if it's a tie, break tie alphabetically by station ID
                    } else {
                        return a.compareTo(b);
                    }
                }
        );

        //add all stations to priority queue
        for (String stationID : this.stations.keySet()) {
            toVisit.offer(stationID);
        }

        //continue until there's no more stations to process
        while (!toVisit.isEmpty()) {
            //remove station with the smallest estimate
            String currentStationID = toVisit.poll();

            //skip this station if already visited/processed
            if(visited.contains(currentStationID)) {
                continue;
            }

            //if best remaining estimate is infinity, no reachable path exists to target
            if(estimate.get(currentStationID) == Double.POSITIVE_INFINITY) {
                break;
            }

            //if target is reached, stop search
            if(currentStationID.equals(targetStationID)) {
                break;
            }

            //mark current station as processed
            visited.add(currentStationID);

            //if current station has no neighbors in graph, skip it
            if (!this.graph.containsKey(currentStationID)) {
                continue;
            }

            //get all neighboring station IDs of current station
            Set<String> neighbors = this.graph.get(currentStationID).keySet();

            //try relaxing edge to each neighbor
            for(String neighborID : neighbors) {
                //skip neighbors that are fully processed
                if(visited.contains(neighborID)) {
                    continue;
                }
                //save neighbor's old estimate so we can see if there's improvement
                double oldEstimate = estimate.get(neighborID);
                //attempt to improve path to this neighbor
                relaxAStar(currentStationID, neighborID, targetStationID, distance, estimate, predecessor);

                //if neighbor's estimate improved, reinsert ot heap
                if(oldEstimate > estimate.get(neighborID)){
                    toVisit.offer(neighborID);
                }
            }

            }
        //if target has no predecessor, no path from start to target was found so print "NONE"
        if (predecessor.get(targetStationID) == null) {
            return "NONE";
        }

        //build the path to target
        String currentNode = targetStationID;
        //stores final path in reverse-building order
        StringBuilder pathToTarget = new StringBuilder();

        //follow predecessor links backward until start is passed
        while(currentNode != null) {
            //if this is first station to be added, add without arrows
            if (pathToTarget.isEmpty()) {
                pathToTarget.insert(0,currentNode);
            //otherwise, add with arrows
            } else {
                pathToTarget.insert(0, currentNode + "->");
            }

            //move backward to get previous station on the path
            currentNode = predecessor.get(currentNode);
        }

        //convert completed path into string
        String pathString = pathToTarget.toString();
        //return path on line 1 and final total distance on line 2
        return pathString + "\n" + String.format("%.2f", distance.get(targetStationID));
        }

    /**
     * Going from start node to nodeVisited through nodeCurrent gives a shorter path than the currently known one.
     * This method updates distance, estimate and predecessor.
     * @param nodeCurrent current station being explored
     * @param nodeVisited neighboring station of nodeCurrent
     * @param targetStationID destination station ID
     * @param distance maps each station to its current best known distance from the source
     * @param estimate maps each station to its current estimated total cost
     * @param predecessor maps each station to the previous station on the best known path
     */
    public void relaxAStar(String nodeCurrent,
                           String nodeVisited,
                           String targetStationID,
                           HashMap<String, Double> distance,
                           HashMap<String, Double> estimate,
                           HashMap<String, String> predecessor) {
        //look up Station object for neighbor being checked
        Station visitedStation = this.stations.get(nodeVisited);
        //look up Station object for target station
        Station targetStation = this.stations.get(targetStationID);
        //get weight of edge between nodeCurrent and nodeVisited
        double weight = this.graph.get(nodeCurrent).get(nodeVisited);

        //if going through nodeCurrent is shorter than to nodeVisited, update path information for nodeVisited
        if(distance.get(nodeVisited) > distance.get(nodeCurrent) + weight) {
            //update the shortest known distance to nodeVisited
            distance.put(nodeVisited, distance.get(nodeCurrent) + weight);
            //update the estimated total cost which is actual distance from source to nodeVisited + heuristic distance from nodeVisited to targetStation
            estimate.put(nodeVisited, distance.get(nodeVisited) +
                    haversine(visitedStation.latitude, visitedStation.longitude, targetStation.latitude, targetStation.longitude));
            //record best path to nodeVisited comes through nodeCurrent
            predecessor.put(nodeVisited, nodeCurrent);
        }

    }
    //main method
    public static void main(String[] args) {
        //need septa_nodes, septa_edges, command line, source_id, target_id as arguments
        //if missing any of the 5 arguments, print "NONE"
        if(args.length != 5) {
            System.out.println("NONE");
            return;
        }

        //assign components of the args to their respective variables
        String nodesFile = args[0];
        String edgesFile = args[1];
        String command = args[2];
        String source_id = args[3];
        String target_id = args[4];

        //if command is not astar, not a valid command so print "NONE"
        if (!command.equals("astar")) {
            System.out.println("NONE");
            return;
        }

        //initialize PathFinder
        PathFinder finder = new PathFinder();
        //load files
        finder.loadStations(nodesFile);
        finder.loadEdges(edgesFile);

        //print shortest path and length of that path in km from source_id to target_id
        System.out.println(finder.aStar(source_id, target_id));

    }

    }



