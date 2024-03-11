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

    public Entity(String id, String type, Angle position, int range) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.range = range;
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

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public abstract EntityInfoResponse getInfo();

}
