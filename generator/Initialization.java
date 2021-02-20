package generator;

import Entity.Passenger;
import Entity.Record;
import Entity.Station;
import Entity.Trip;
import Setting.Setting;
import fileIO.MyFileReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Initialization {

    private int recordRawCount;

    //    private HashMap<String, HashMap> passengerHashMap; // <CardID, <Route, ArrayList<Trip>>>
    private HashMap<String, Station> stationHashMap; // <station id, station>
    //    private HashMap<String, Route> routeHashMap;
    private HashMap<String, HashMap> tripHashMap; // <Day, <RouteID, <CardID, ArrayList<Trip>>>>
    private HashMap<String, Integer> dayIndexHashMap; // day-string -> dayIndex
    private HashMap<String, Passenger> passengerList;


    public Initialization() {
        System.out.println("Start Initializing Data");
        stationHashMap = new HashMap<String, Station>();
//        routeHashMap = new HashMap<String, Route>();
        tripHashMap = new HashMap<String, HashMap>();
//        passengerHashMap = new HashMap<>();
        dayIndexHashMap = new HashMap<>();
        passengerList = new HashMap<>();

        ArrayList<Record> recordList;

        recordList = loadRecord();
        generateTrips(recordList);
//        removePassenger(recordList);
        //removeRoute();

        System.out.println("3. Initialization done");
        printLine();
        System.out.println(" - Station Number " + stationHashMap.size());
        //System.out.println(" - Trip Number " + tripQueue.size());
        System.out.println(" - Passenger Number " + passengerList.size());
        printLine();
    }

    private void getRecordInformation(ArrayList<Record> recordList) {
        long totalTime = 0;
        long aveTime = 0;
        for (Record nextRecord : recordList) {
            totalTime += (nextRecord.getEndTime().getTime() - nextRecord.getBeginTime().getTime());
        }
        aveTime = totalTime / recordList.size();
        System.out.println("Dataset Information");
        System.out.println("Avg time " + aveTime / 1000);
    }

    private ArrayList<Record> loadRecord() {
        System.out.println("1. Load record data");

        String line, cardID, day, route, beginStation, endStation;
        Date beginTime, endTime;
        String[] content;
        Record record;
        int count = 0;

        ArrayList<Record> recordList = new ArrayList<>();
        Set<String> daySet = new HashSet<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //String filePath = separator + "src" + separator + "Data" + separator + "TripRecord.csv";
        String filePath = Setting.FILE;
        MyFileReader myFileReader = new MyFileReader(filePath);//bus master data sr pla 2016.csv
        myFileReader.getNextLine(); //skip the first line
        line = myFileReader.getNextLine();

        while (line != null) {
            content = line.replace("\"", "").split(";");
            //content = line.split(";");
            if (content.length > 10)
                if (content[1].equals("BUS"))
                    if (!content[0].equals("null") && !content[3].equals("null") && !content[5].equals("null") && !content[10].equals("null")) {
                        try {
                            beginStation = content[8];
                            endStation = content[9];
                            route = content[10];
                            cardID = content[0];
                            day = content[2];
                            daySet.add(day);
                            beginTime = format.parse(content[2] + " " + content[3]);
                            endTime = format.parse(content[4] + " " + content[5]);
                            record = new Record(cardID, beginTime, endTime, day, beginStation, endStation, route);
                            recordList.add(record);
                        } catch (ParseException e) {
                            System.out.println(line);
                            e.printStackTrace();
                        }

                        if (count % 1000000 == 0)
                            System.out.println("Record count " + count / 1000000 + "M");
                        count++;
                    }
            line = myFileReader.getNextLine();
        }
        this.recordRawCount = count;

        //generating selected Days
        ArrayList<String> dayList = new ArrayList<>(daySet);
        Collections.sort(dayList);
        dayList = new ArrayList<>(dayList.subList(0, Setting.PERIOD));
        generateDayIndexHashMap(dayList);

        getRecordInformation(recordList);
        return recordList;
    }

    private void generateStation(String stationID, Trip trip) {
        Station station;
        if (stationHashMap.containsKey(stationID)) {
            station = stationHashMap.get(stationID);
        } else {
            station = new Station(stationID);
            stationHashMap.put(stationID, station);
        }
        station.addTrip(trip);
    }

    private void generateDayIndexHashMap(ArrayList<String> dayList) {
        for (int i = 0; i < dayList.size(); i++) {
            dayIndexHashMap.put(dayList.get(i), i);
        }

    }

    private void generateTrips(ArrayList<Record> recordList) {
        System.out.println("2. Generate trip data");
        String cardID;
        int count = 0;
        int max = 0;
        HashMap<String, Integer> rideCountHashMap = new HashMap<>();// count ride number of passengers

        System.out.println(" - 1) Counting personal ride number");
        for (Record nextRecord : recordList) {
            if (!dayIndexHashMap.containsKey(nextRecord.getDay()))
                continue;

            cardID = nextRecord.getCardID();
            if (rideCountHashMap.containsKey(cardID))
                rideCountHashMap.put(cardID, rideCountHashMap.get(cardID) + 1);
            else
                rideCountHashMap.put(cardID, 1);
            if (rideCountHashMap.get(cardID) > max)
                max = rideCountHashMap.get(cardID);
        }
        System.out.println("   * Max ride number " + max);
        System.out.println("   * Raw passenger number " + rideCountHashMap.size());


        System.out.println(" - 2) Generate Trip/Passenger Hash Map, and TripQueue");
        for (Record nextRecord : recordList) {

            if (!dayIndexHashMap.containsKey(nextRecord.getDay()))
                continue;
            if (rideCountHashMap.get(nextRecord.getCardID()) < Setting.PERIOD * Setting.PHI)
                continue;

            generateTrip(nextRecord);
            count++;
        }

        System.out.println(" - Actual trips " + count);
        System.out.println(" - Generating Finished");
        //removePassenger(rideCountHashMap);

    }


    private Trip generateTrip(Record nextRecord) {
        String cardID, day, route, beginStation, endStation;
        Date beginTime, endTime;

        beginStation = nextRecord.getBeginStation();
        endStation = nextRecord.getEndStation();
        route = nextRecord.getRoute();
        cardID = nextRecord.getCardID();
        day = nextRecord.getDay();

        beginTime = nextRecord.getBeginTime();
        endTime = nextRecord.getEndTime();

        HashMap<String, HashMap> routeDayHashMap; // day,  <RouteID, <CardID, ArrayList<Trip>>>
        HashMap<String, ArrayList<Trip>> tripDayHashMap; // routeID,  <CardID, ArrayList<Trip>>
        ArrayList<Trip> tripList;

        //routes in one day
        if (tripHashMap.containsKey(day)) {
            routeDayHashMap = tripHashMap.get(day);
        } else {
            routeDayHashMap = new HashMap<String, HashMap>();
            tripHashMap.put(day, routeDayHashMap);
        }

        //trips in one route / one day
        if (routeDayHashMap.containsKey(route)) {
            tripDayHashMap = routeDayHashMap.get(route);
        } else {
            tripDayHashMap = new HashMap<>();
            routeDayHashMap.put(route, tripDayHashMap);
        }

        //trip in trips / one route / one day
        if (tripDayHashMap.containsKey(cardID)) {
            tripList = tripDayHashMap.get(cardID);
        } else {
            tripList = new ArrayList<Trip>();
            tripDayHashMap.put(cardID, tripList);
        }
        Trip newTrip = new Trip(cardID);
        generateStation(beginStation, newTrip);
        generateStation(endStation, newTrip);
        newTrip.setBeginStation(stationHashMap.get(beginStation));
        newTrip.setEndStation(stationHashMap.get(endStation));
        newTrip.setBeginTime(beginTime);
        newTrip.setEndTime(endTime);
        newTrip.setDay(day);
        newTrip.setRouteID(route);

        newTrip.setDayIndex(dayIndexHashMap.get(day));
        tripList.add(newTrip);
        generatePassengerList(newTrip);

        return newTrip;
    }

    private void generatePassengerList(Trip trip) {
        Passenger passenger;
        String cardID = trip.getCardID();
        if (!passengerList.containsKey(cardID)) {
            passenger = new Passenger(cardID);
            passengerList.put(cardID, passenger);
        } else {
            passenger = passengerList.get(cardID);
        }
        passenger.addTrip(trip);

//
//        HashMap<String, ArrayList<Trip>> personalTripHashmap;
//        ArrayList<Trip> personalTripList;
//        if (!passengerHashMap.containsKey(trip.getCardID())) {
//            personalTripHashmap = new HashMap<>();
//            passengerHashMap.put(trip.getCardID(), personalTripHashmap);
//        } else {
//            personalTripHashmap = passengerHashMap.get(trip.getCardID());
//        }
//        if (!personalTripHashmap.containsKey(trip.getRouteID())) {
//            personalTripList = new ArrayList<>();
//            personalTripHashmap.put(trip.getRouteID(), personalTripList);
//        } else {
//            personalTripList = personalTripHashmap.get(trip.getRouteID());
//        }
//        personalTripList.add(trip);
    }

//    private void generatePassengerHashMap(Trip trip) {
//        HashMap<String, ArrayList<Trip>> personalTripHashmap;
//        ArrayList<Trip> personalTripList;
//        if (!passengerHashMap.containsKey(trip.getCardID())) {
//            personalTripHashmap = new HashMap<>();
//            passengerHashMap.put(trip.getCardID(), personalTripHashmap);
//        } else {
//            personalTripHashmap = passengerHashMap.get(trip.getCardID());
//        }
//        if (!personalTripHashmap.containsKey(trip.getRouteID())) {
//            personalTripList = new ArrayList<>();
//            personalTripHashmap.put(trip.getRouteID(), personalTripList);
//        } else {
//            personalTripList = personalTripHashmap.get(trip.getRouteID());
//        }
//        personalTripList.add(trip);
//    }

//    private ArrayList<Record> removeRoute(ArrayList<Record> recordList) {
//        printLine();
//        System.out.println("Remove Route");
//        int maxCount = 0;
//        String route;
//
//        HashMap<String, Integer> routeCountHashMap = new HashMap<>(); // record the number of trip of each route
//
//        for (Record record : recordList) {
//            route = record.getRoute();
//            if (routeCountHashMap.containsKey(route))
//                routeCountHashMap.put(route, routeCountHashMap.get(route) + 1);
//            else
//                routeCountHashMap.put(route, 1);
//            if (routeCountHashMap.get(route) > maxCount)
//                maxCount = routeCountHashMap.get(route);
//        }
//        System.out.println("Record Number (raw) " + recordList.size());
//        System.out.println("Route Number (raw) " + routeHashMap.size());
//        System.out.print("Max riding count " + maxCount);
//        maxCount *= Setting.PHI;
//        System.out.println(" | remove threshold " + Setting.PERIOD);
//
//        for (Map.Entry<String, Integer> routeCountEntity : routeCountHashMap.entrySet()) {
//            if (routeCountEntity.getValue() < Setting.PHI) {
//                route = routeCountEntity.getKey();
//                routeHashMap.remove(route);
//            }
//        }
//
//        ArrayList<Record> newRecordList = new ArrayList<>(recordList.size());
//        for (Record record : recordList) {
//            route = record.getRoute();
//            if (routeCountHashMap.get(route) > maxCount)
//                newRecordList.add(record);
//        }
//        recordList = newRecordList;
//        System.out.println("Record Number (final) " + recordList.size());
//        System.out.println("Route Number (final) " + routeHashMap.size());
//        printLine();
//        return newRecordList;
//    }
//
//    private void removePassenger(ArrayList<Record> recordList) {
//
//        System.out.println("Remove Passenger");
//        int minimumCount = Setting.PERIOD * Setting.PHI;// 2 times a day
//        int recordNum = recordList.size();
//
//        System.out.println("Record number (raw) " + recordNum);
//        System.out.println("Passenger number (raw) " + passengerHashMap.keySet().size());
//
//        String[] cardIDList = new String[passengerHashMap.size()];
//        passengerHashMap.keySet().toArray(cardIDList);
//
//        for (String cardID : cardIDList) {
//
//            //each entity is all trips of one passenger
//            HashMap<String, ArrayList<Trip>> oneRouteHashMap = passengerHashMap.get(cardID);
//            int count = 0;
//            //each entity is all trips of one route of one passenger
//            for (Map.Entry<String, ArrayList<Trip>> tripList : oneRouteHashMap.entrySet()) {
//                count += tripList.getValue().size();
//            }
//            if (count < minimumCount) {
//                passengerHashMap.remove(cardID);
//                recordNum -= count;
//
//            }
//        }
//
//        System.out.println("Record number (final) " + recordNum);
//        System.out.println("Passenger number (final) " + passengerHashMap.size());
//    }

//    private void removePassenger(HashMap<String, Integer> rideCountHashMap) {
//
//        int days = Setting.PERIOD;
//        // remove passengers that only takes bus few times
//        printLine();
//        System.out.println("Remove Passenger");
//
//        int minimumCount = days * Setting.PHI;// 2 times a day
//        int recordNum = recordList.size();
//        String cardID;
//
//        System.out.println("Record number (raw) " + recordNum);
//        System.out.println("Passenger number (raw) " + passengerHashMap.keySet().size());
//        System.out.println("Remove threshold " + minimumCount + "(riding time)");
//
//        HashMap<String, HashMap> newPassengerHashMap = new HashMap<String, HashMap>();
//        for (Map.Entry<String, Integer> rideCountEntity : rideCountHashMap.entrySet()) {
//            cardID = rideCountEntity.getKey();
//            if (rideCountEntity.getValue() >= minimumCount) {
//                cardID = rideCountEntity.getKey();
//                newPassengerHashMap.put(cardID, passengerHashMap.get(cardID));
//            } else {
//                if (passengerHashMap.containsKey(cardID))
//                    recordNum -= rideCountEntity.getValue();
//            }
//        }
//
//        System.out.println("Record number (final) " + recordNum);
//        System.out.println("Passenger number (final) " + newPassengerHashMap.size());
//        passengerHashMap = newPassengerHashMap;
//        printLine();
//    }

    public HashMap<String, HashMap> getTripHashMap() {
        return tripHashMap;
    }

//    public HashMap<String, HashMap> getPassengerHashMap() {
//        return passengerHashMap;
//    }

    public HashMap<String, Passenger> getPassengerList() {
        return passengerList;
    }

    public HashMap<String, Station> getStationHashMap() {
        return stationHashMap;
    }

    private void printLine() {
        System.out.println("---------------------------------------------------------");
    }

}
