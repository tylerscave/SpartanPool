package model.schedule;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import model.Context;
import model.DataHandler;
import model.member.Member;

/**
 *
 * @author David
 */
public class Drive extends Schedulable{
    
    private int numSeats;
    private List<Integer> rideIds;
    private Route route;
    
    public Drive(int numSeats, Member member) {
        super(member);
        this.numSeats = numSeats;
        this.rideIds = new ArrayList<>();
    }

    public int getNumSeats() {
        return numSeats;
    }

    public int getRideId(int i) {
        return rideIds.get(i);
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
    
    public void addRide(Ride ride) {
        this.rideIds.add(ride.getIdNumber());
        numSeats --;
        for (Location l : ride.getRoute().getStops())
            route.addStopInRoute(l);
    }
    
    public void removeRideById(int rideId) {
        int index = -1;
        for (int i = 0; i < rideIds.size(); i++) {
            if (rideIds.get(i) == rideId) {
                index = i;
                break;
            }
        }
        if (index > -1)
            this.rideIds.remove(index);
    }
    
    public int numberOfRides() {
        return this.rideIds.size();
    }
    
    @Override
    public void remove() {
        //TODO
    }
    
    /**
     * Returns the last location that was updated on the route before the current time.
     * If there is no updated route, returns the last location on the scheduled route before the current time.
     * If the current time is before the route starts, returns null
     * @return the last location before the current time
     */
    public Location getCurrentLocation() {
        //get latest information
        DataHandler data = Context.getInstance().getDataHandler();
        Drive temp = (Drive)data.getSchedulable(getIdNumber());
        return temp.getRoute().getLocationAtTime(new GregorianCalendar());
    }
    
    /**
     * Updates the real route being traveled with the current time and location.
     * @param location the current location at the time of the update
     */
    public void updateStatus(Location location) {
        //update data
        DataHandler data = Context.getInstance().getDataHandler();
        Member member = data.getMember(getMemberId());
        for (Drive d : member.getDrives()) {
            if (d.getIdNumber() == getIdNumber())
                d.getRoute().setRealRoute(new GregorianCalendar(), location);
        }
        data.setMember(getMemberId(), member);
        
        member.addObserver(data);
        member.setChanged();
        member.notifyObservers();
        member.deleteObservers();
        //finish update data; local copy
        route.setRealRoute(new GregorianCalendar(), location);
    }

    @Override
    public GregorianCalendar getStartTime() {
        return route.getStartTime();
    }

    @Override
    public GregorianCalendar getEndTime() {
        return route.getEndTime();
    }
}
