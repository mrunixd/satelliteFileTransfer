package unsw.blackout;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    private Angle initial;
    private boolean clockwise;
    private int sendingBandwidth;
    private int recievingBandwidth;
    private int storage;

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position, int linearSpeed,
            int range) {
        super(satelliteId, type, height, position, linearSpeed, range);
        this.initial = position;
        this.sendingBandwidth = 10;
        this.recievingBandwidth = 15;
        this.storage = 200;
    }

    public int getSendingBandwidth() {
        return this.sendingBandwidth;
    }

    public int getRecievingBandwidth() {
        return this.recievingBandwidth;
    }

    public int getStorage() {
        return this.storage;
    }

    public void setStorage(int sizeOfFile) {
        this.storage -= sizeOfFile;
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
