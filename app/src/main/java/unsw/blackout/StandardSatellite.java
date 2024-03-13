package unsw.blackout;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    private int sendingBandwidth;
    private int recievingBandwidth;

    public StandardSatellite(String satelliteId, String type, double height, Angle position, int linearSpeed,
            int range) {
        super(satelliteId, type, height, position, linearSpeed, range);
        this.sendingBandwidth = 1;
        this.recievingBandwidth = 1;
    }

    public int getSendingBandwidth() {
        return this.sendingBandwidth;
    }

    public int getRecievingBandwidth() {
        return this.recievingBandwidth;
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = getLinearSpeed() / super.getHeight();
        Angle newAngle = super.getPosition().subtract(Angle.fromRadians(angularVelocity));

        super.setPosition(newAngle);
    }

}
