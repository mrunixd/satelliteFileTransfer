package unsw.blackout;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    private int linearSpeed = 1500;
    private int range = 300000;
    private boolean clockwise = true;

    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = linearSpeed / super.getHeight();
        Angle original = super.getPosition();
        Angle newAngle = super.getPosition().subtract(Angle.fromRadians(angularVelocity));

        if (this.clockwise) {
            newAngle = super.getPosition().subtract(Angle.fromRadians(angularVelocity));
        } else {
            newAngle = super.getPosition().add(Angle.fromRadians(angularVelocity));
        }

        newAngle = placeDegreesInRange(newAngle);

        if (original.toDegrees() > 190 || original.toDegrees() < 140) {
            // newAngle = direction needed to move in to either 140 or 190
            if (original.toDegrees() < 345 && original.toDegrees() > 190) {
                this.clockwise = true;
            } else {
                this.clockwise = false;
            }
            super.setPosition(newAngle);
            return;
        }

        boolean crossesClockwise = clockwise && original.toDegrees() > 140 && newAngle.toDegrees() < 140;
        boolean crossesAntiClockwise = !clockwise && original.toDegrees() < 190 && newAngle.toDegrees() > 140;

        if (crossesClockwise || crossesAntiClockwise) {
            this.clockwise = !this.clockwise;
        }
        super.setPosition(newAngle);
    }
}
