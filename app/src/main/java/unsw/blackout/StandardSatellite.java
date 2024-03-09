package unsw.blackout;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    private int linearSpeed = 2500;
    private int range = 150000;

    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = linearSpeed / super.getHeight();
        Angle newAngle = super.getPosition().subtract(Angle.fromRadians(angularVelocity));

        super.setPosition(newAngle);
    }

    public int getRange() {
        return this.range;
    }
}
