package unsw.blackout;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    private int linearSpeed = 1000;
    private int range = 200000;
    private Angle initial;
    private boolean clockwise = true;

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        this.initial = position;
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = linearSpeed / super.getHeight();
        Angle newAngle;
        double teleportDegVal = (initial.toDegrees() + 180) % 360;

        if (this.clockwise) {
            newAngle = super.getPosition().subtract(Angle.fromRadians(angularVelocity));
        } else {
            newAngle = super.getPosition().add(Angle.fromRadians(angularVelocity));
        }

        double newAngleDeg = newAngle.toDegrees();
        if (newAngleDeg < 0) {
            newAngleDeg += 360;
        } else if (newAngleDeg >= 360) {
            newAngleDeg -= 360;
        }
        newAngle = Angle.fromDegrees(newAngleDeg);

        boolean crossesClockwise = (this.clockwise && newAngle.toDegrees() < teleportDegVal
                && super.getPosition().toDegrees() >= teleportDegVal);

        boolean crossesAntiClockwise = (!this.clockwise && newAngle.toDegrees() > teleportDegVal
                && super.getPosition().toDegrees() <= teleportDegVal);

        if (crossesClockwise || crossesAntiClockwise) {
            // Teleport back to the initial position
            newAngle = this.initial;
            this.clockwise = !this.clockwise;
        }

        super.setPosition(newAngle);
    }
}
