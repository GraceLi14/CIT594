import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
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



}
