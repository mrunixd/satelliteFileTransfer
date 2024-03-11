package unsw.blackout;

public class FileTransfer {
    private Entity sender;
    private Entity recipient;
    private int transferredBytes;
    private File file;

    public FileTransfer(Entity sender, Entity recipient, File file) {
        this.sender = sender;
        this.recipient = recipient;
        this.file = file;
        this.transferredBytes = 0;
    }

    public Entity getSender() {
        return this.sender;
    }

    public Entity getrecipient() {
        return this.recipient;
    }

    public File getFile() {
        return this.file;
    }

    public int getTransferredBytes() {
        return this.transferredBytes;
    }

    public void setTransferredBytes(int transferredBytes) {
        this.transferredBytes = transferredBytes;
    }
}