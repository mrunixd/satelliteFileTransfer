package unsw.blackout;

public class File {
    private String name;
    private String content;
    private int size;

    public File(String name, String content, int size) {
        this.name = name;
        this.content = content;
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public String getContent() {
        return this.content;
    }

    public int getSize() {
        return this.size;
    }
}
