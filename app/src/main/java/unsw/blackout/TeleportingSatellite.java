package unsw.blackout;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    private int linearSpeed = 1000;
    private int range = 200000;

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = -1 * (linearSpeed / super.getHeight());
        Angle angle = Angle.fromRadians(angularVelocity);

        super.setPosition(super.getPosition().add(angle));
    }
}
