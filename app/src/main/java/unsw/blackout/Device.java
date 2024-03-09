package unsw.blackout;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.ArrayList;
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
    private List<File> files = new ArrayList<>();

    public Device(String deviceId, String type, Angle position) {
        this.deviceId = deviceId;
        this.type = type;
        this.position = position;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void addFile(String filename, String content) {
        File file = new File(filename, content);
        files.add(file);
    }

    public Map<String, FileInfoResponse> getFiles() {
        Map<String, FileInfoResponse> fileList = new HashMap<>();

        for (File file : files) {
            fileList.put(file.getName(),
                    new FileInfoResponse(file.getName(), file.getContent(), file.getContent().length(), true));
        }
        return fileList;
    }

    public Angle getPosition() {
        return this.position;
    }

    @Override
    public EntityInfoResponse getInfo() {
        return new EntityInfoResponse(this.deviceId, this.position, RADIUS_OF_JUPITER, this.type, getFiles());
    }

}
