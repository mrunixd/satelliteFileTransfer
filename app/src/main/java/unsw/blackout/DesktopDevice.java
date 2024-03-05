package unsw.blackout;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    private int range = 200000;

    public DesktopDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position);
        //TODO Auto-generated constructor stub
    }

    public int getRange() {
        return this.range;
    }

}
