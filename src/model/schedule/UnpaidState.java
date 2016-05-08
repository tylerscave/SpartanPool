package model.schedule;

/**
 *
 * @author David
 */
public class UnpaidState extends RideState{

    public UnpaidState(RideStatus rs) {
        super(rs);
    }
    
    @Override
    public void update() {
        //Route already complete; do nothing
    }

    @Override
    public String getStatus() {
        return RideStatus.UNPAID;
    }

    @Override
    public void cancel() {
        //ride already happened; do nothing
    }

    @Override
    public void pay() {
        rideStatus.setRideState(new PaidState(rideStatus));
    }
}
