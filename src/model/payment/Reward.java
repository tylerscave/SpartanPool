package model.payment;

import model.Member;
import model.schedule.Ride;

public abstract class Reward {
    
    protected RewardCalculator rewardCalculator;
    
    public Reward(RewardCalculator rewardCalculator) {
        this.rewardCalculator = rewardCalculator;
    }
    
    public abstract Object findReward(Member recipient, Ride ride);
    
    public abstract boolean resolveReward(Member recipient, Ride ride, Object compensation);
}