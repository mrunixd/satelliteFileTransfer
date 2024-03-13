package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

public abstract class Entity {
    private String id;
    private String type;
    private Angle position;
    private int range;
    private List<File> files = new ArrayList<>();
    private int filesSending;
    private int filesRecieving;

    public Entity(String id, String type, Angle position, int range) {
        this.id = id;
        this.type = type;
        this.position = position;
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

    public abstract EntityInfoResponse getInfo();

    public abstract boolean checkSendingBandwidth();

    public abstract boolean checkRecievingBandwidth();

    public abstract int calcSendingBandwidth();

    public abstract int calcRecievingBandwidth();

    public abstract int calcSendingBandwidth(int files);

    public abstract int calcRecievingBandwidth(int files);

}
