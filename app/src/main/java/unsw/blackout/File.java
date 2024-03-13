package unsw.blackout;

import java.util.List;

public class File {
    private String name;
    private String content;
    private int size;
    private boolean isComplete;

    public File(String name, String content, int size) {
        this.name = name;
        this.content = content;
        this.size = size;

        if (content.length() == size) {
            isComplete = true;
        }
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

    public static File checkFileExists(List<File> files, String fileName) {
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public boolean isFileComplete() {
        return this.isComplete;
    }

    public String teleportGlitch() {
        return this.content.replaceAll("t", "");
    }
}
