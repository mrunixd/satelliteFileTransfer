package unsw.blackout;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.HashMap;
import java.util.Map;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class Device extends Entity {
    private int maxBandwidth = 15;

    public Device(String deviceId, String type, Angle position, int range, double height) {
        super(deviceId, type, position, range, height);
    }

    public Map<String, FileInfoResponse> getInfoFiles() {
        Map<String, FileInfoResponse> fileList = new HashMap<>();

        for (File file : getFiles()) {
            fileList.put(file.getName(),
                    new FileInfoResponse(file.getName(), file.getContent(), file.getSize(), file.isFileComplete()));
        }
        return fileList;
    }

    @Override
    public EntityInfoResponse getInfo() {
        return new EntityInfoResponse(getId(), getPosition(), RADIUS_OF_JUPITER, getType(), getInfoFiles());
    }

    @Override
    public int calcSendingBandwidth() {

        return maxBandwidth;
    }

    @Override
    public int calcRecievingBandwidth() {
        return maxBandwidth;
    }

    @Override
    public int calcSendingBandwidth(int files) {

        return maxBandwidth;
    }

    @Override
    public int calcRecievingBandwidth(int files) {
        return maxBandwidth;
    }

    @Override
    public boolean checkSendingBandwidth() {
        return true;
    }

    @Override
    public boolean checkRecievingBandwidth() {
        return true;
    }

}
