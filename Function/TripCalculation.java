package Function;

import Setting.Setting;
import Entity.Trip;

import java.util.ArrayList;
import java.util.Date;

public class TripCalculation {

    private final double EC50 = Setting.EC50;
    private final double N = Setting.N;

    private int getContactTime(Trip trip1, Trip trip2) {
        Date beginTime, endTime;
        int time = 0;
        if (trip1.getBeginTime().after(trip2.getEndTime()) ||
                trip1.getEndTime().before(trip2.getBeginTime())) {
            return time;
        } else {
            if (trip1.getBeginTime().before(trip2.getBeginTime()))
                beginTime = trip2.getBeginTime();
            else
                beginTime = trip1.getBeginTime();
            if (trip1.getEndTime().before(trip2.getEndTime()))
                endTime = trip1.getEndTime();
            else
                endTime = trip2.getEndTime();
            time = (int) (endTime.getTime() - beginTime.getTime()) / 1000;
        }
        return time;
    }

    public double getInfRatio(Trip trip1, Trip trip2) {
        return getInfPro(getContactTime(trip1, trip2));
    }

    public double getInfPro(int time) {
        // the influence probability between two trips
        //double infProb = 1 / (Math.pow(Math.E, (-0.1) * N * (Math.log(EC50) - Math.log(time))));
        if (time == 0)
            return 0;
        if (Setting.infModel == 1)
            return (1 / (1 + Math.pow((Setting.EC50 / time), N)));
        else {
            if (time >= Setting.infThreshold)
                return 1.0;
            else
                return 0.0;
        }
    }

    public double getInfRatio(ArrayList<Trip> trips1, ArrayList<Trip> trips2) {
        double p; // the probability of not getting the infection
        double infRatio = 1.0;
        for (Trip trip1 : trips1) {
            p = 1.0;
            for (Trip trip2 : trips2) {
                p *= (1 - getInfPro(getContactTime(trip1, trip2)));
            }
            infRatio *= p;
        }
        infRatio = 1.0 - infRatio; // the probability of getting the infection
        return infRatio;
    }
}
