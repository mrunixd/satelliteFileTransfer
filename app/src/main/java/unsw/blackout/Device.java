package unsw.blackout;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.HashMap;
import java.util.Map;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Device implements Entity {
    private String deviceId;
    private String type;
    private Angle position;
    private Map<String, FileInfoResponse> fileList = new HashMap<>();

    public Device(String deviceId, String type, Angle position) {
        this.deviceId = deviceId;
        this.type = type;
        this.position = position;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void addFile(String filename, String content) {
        fileList.put(filename, new FileInfoResponse(filename, content, content.length(), true));
    }

    public Map<String, FileInfoResponse> getFiles() {
        return this.fileList;
    }

    public Angle getPosition() {
        return this.position;
    }

    @Override
    public EntityInfoResponse getInfo() {
        EntityInfoResponse info = new EntityInfoResponse(this.deviceId, this.position, RADIUS_OF_JUPITER, this.type,
                fileList);
        return info;
    }

}
