package unsw.blackout;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.HashMap;
import java.util.Map;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Device extends Entity {
    public Device(String deviceId, String type, Angle position, int range) {
        super(deviceId, type, position, range);
    }

    public Map<String, FileInfoResponse> getInfoFiles() {
        Map<String, FileInfoResponse> fileList = new HashMap<>();

        for (File file : getFiles()) {
            fileList.put(file.getName(),
                    new FileInfoResponse(file.getName(), file.getContent(), file.getContent().length(), true));
        }
        return fileList;
    }

    @Override
    public EntityInfoResponse getInfo() {
        return new EntityInfoResponse(getId(), getPosition(), RADIUS_OF_JUPITER, getType(), getInfoFiles());
    }

}
