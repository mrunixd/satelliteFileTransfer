package unsw.blackout;

import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

import java.util.ArrayList;
import java.util.List;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

public abstract class Entity {
    private String id;
    private String type;
    private Angle position;
    private double height;
    private int range;
    private List<File> files = new ArrayList<>();
    private int filesSending;
    private int filesRecieving;

    public Entity(String id, String type, Angle position, int range, double height) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.height = height;
        this.range = range;
        this.filesSending = 0;
        this.filesRecieving = 0;
    }

    public String getId() {
        return this.id;
    }

    public Angle getPosition() {
        return this.position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public int getRange() {
        return this.range;
    }

    public String getType() {
        return this.type;
    }

    public double getHeight() {
        return this.height;
    }

    public void addFile(String filename, String content, int size) {
        File file = new File(filename, content, size);
        files.add(file);
    }

    public List<File> getFiles() {
        return this.files;
    }

    public void removeFile(String fileName) {
        File fileToRemove = null;
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                fileToRemove = file;
            }
        }

        this.files.remove(fileToRemove);
    }

    public int getFilesSending() {
        return this.filesSending;
    }

    public int getFilesRecieving() {
        return this.filesRecieving;
    }

    public void addFilesSending(int numFiles) {
        this.filesSending += numFiles;
    }

    public void addFilesRecieving(int numFiles) {
        this.filesRecieving += numFiles;
    }

    public static boolean notSupportedTransfer(Entity a, Entity b) {
        boolean standardAndDesktop = b instanceof StandardSatellite && a.getType().equals("DesktopDevice");
        boolean desktopAndStandard = a instanceof StandardSatellite && b.getType().equals("DesktopDevice");
        return standardAndDesktop || desktopAndStandard;
    }

    public static boolean entitiesAreCommunicable(Entity a, Entity b) {
        double distance = getDistance(a.getHeight(), a.getPosition(), b.getHeight(), b.getPosition());
        boolean isVisible = isVisible(a.getHeight(), a.getPosition(), b.getHeight(), b.getPosition());

        return distance < a.getRange() && distance > 0 && isVisible;
    }

    public abstract EntityInfoResponse getInfo();

    public abstract boolean checkSendingBandwidth();

    public abstract boolean checkRecievingBandwidth();

    public abstract int calcSendingBandwidth();

    public abstract int calcRecievingBandwidth();

    public abstract int calcSendingBandwidth(int files);

    public abstract int calcRecievingBandwidth(int files);

}
