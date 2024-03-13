package unsw.blackout;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    private boolean clockwise = true;

    public RelaySatellite(String satelliteId, String type, double height, Angle position, int linearSpeed, int range) {
        super(satelliteId, type, height, position, linearSpeed, range);
    }

    public int getSendingBandwidth() {
        return 0;
    }

    public int getRecievingBandwidth() {
        return 0;
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = getLinearSpeed() / super.getHeight();
        Angle original = super.getPosition();
        Angle newAngle = super.getPosition().subtract(Angle.fromRadians(angularVelocity));

        if (original.toDegrees() > 190 || original.toDegrees() < 140) {
            if (original.toDegrees() >= 345 || original.toDegrees() < 140) {
                this.clockwise = false;
            } else {
                this.clockwise = true;
            }
            newAngle = calculateNewAngle(angularVelocity, clockwise);
            super.setPosition(newAngle);
            return;
        }

        newAngle = calculateNewAngle(angularVelocity, clockwise);

        boolean crossesClockwise = clockwise && original.toDegrees() > 140 && newAngle.toDegrees() < 140;
        boolean crossesAntiClockwise = !clockwise && original.toDegrees() < 190 && newAngle.toDegrees() > 190;

        if (crossesClockwise || crossesAntiClockwise) {
            this.clockwise = !this.clockwise;
        }

        super.setPosition(newAngle);
    }

}
