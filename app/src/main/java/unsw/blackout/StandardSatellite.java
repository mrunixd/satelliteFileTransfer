package unsw.blackout;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    private int linearSpeed = 2500;
    private int range = 150000;

    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
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
