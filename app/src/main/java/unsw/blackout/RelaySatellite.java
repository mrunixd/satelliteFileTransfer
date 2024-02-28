package unsw.blackout;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    private int linearSpeed = 1500;
    private int range = 300000;

    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = linearSpeed / super.getHeight();
        Angle newAngle = super.getPosition().subtract(Angle.fromRadians(angularVelocity));

        super.setPosition(newAngle);
    }
}
