package unsw.blackout;

public class File {
    private String name;
    private String content;

    public File(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return this.name;
    }

    public String getContent() {
        return this.content;
    }
}
