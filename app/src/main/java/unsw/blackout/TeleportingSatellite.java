package unsw.blackout;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    private boolean clockwise;
    private int sendingBandwidth;
    private int recievingBandwidth;

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position, int linearSpeed,
            int range) {
        super(satelliteId, type, height, position, linearSpeed, range);
        this.sendingBandwidth = 10;
        this.recievingBandwidth = 15;
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
        double teleportDegVal = 180;
        Angle newAngle;

        newAngle = calculateNewAngle(angularVelocity, clockwise);

        boolean crossesClockwise = (!this.clockwise && newAngle.toDegrees() > teleportDegVal
                && super.getPosition().toDegrees() <= teleportDegVal);

        boolean crossesAntiClockwise = (this.clockwise && newAngle.toDegrees() < teleportDegVal
                && super.getPosition().toDegrees() >= teleportDegVal);

        if (crossesClockwise || crossesAntiClockwise) {
            // Teleport back to the initial position
            newAngle = Angle.fromDegrees(0);
            this.clockwise = !this.clockwise;
        }

        super.setPosition(newAngle);
    }
}
