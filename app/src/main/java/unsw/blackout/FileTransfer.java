package unsw.blackout;

public class FileTransfer {
    private Entity sender;
    private Entity recipient;
    private int transferredBytes;
    private File file;
    private int size;

    public FileTransfer(Entity sender, Entity recipient, File file) {
        this.sender = sender;
        this.recipient = recipient;
        this.file = file;
        this.transferredBytes = 0;
        this.size = file.getContent().length();
    }

    public Entity getSender() {
        return this.sender;
    }

    public Entity getRecipient() {
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

    public int getSize() {
        return this.size;
    }

    public static boolean teleportGlitchApplies(Entity sender, Entity recipient) {
        return (sender instanceof TeleportingSatellite && ((TeleportingSatellite) sender).didTeleport())
                || (recipient instanceof TeleportingSatellite && ((TeleportingSatellite) recipient).didTeleport());
    }
}
