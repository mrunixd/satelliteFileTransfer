package unsw.blackout;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Device implements Entity {
    private String deviceId;
    private String type;
    private Angle position;
    private List<Device> deviceList;
    private List<Satellite> satelliteList;
    private Map<String, FileInfoResponse> fileList = new HashMap<>();

    public Device(String deviceId, String type, Angle position, List<Device> deviceList,
            List<Satellite> satelliteList) {
        this.deviceId = deviceId;
        this.type = type;
        this.position = position;
        this.deviceList = deviceList;
        this.satelliteList = satelliteList;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void addFile(String filename, String content) {
        fileList.put(filename, new FileInfoResponse(filename, content, content.length(), true));
    }

    public Angle getPosition() {
        return this.position;
    }

    public List<Device> getDeviceList() {
        return this.deviceList;
    }

    public List<Satellite> getSatelliteList() {
        return this.satelliteList;
    }

    @Override
    public EntityInfoResponse getInfo() {
        EntityInfoResponse info = new EntityInfoResponse(this.deviceId, this.position, RADIUS_OF_JUPITER, this.type,
                fileList);
        return info;
    }

}
