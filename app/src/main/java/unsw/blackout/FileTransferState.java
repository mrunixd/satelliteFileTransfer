package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

public class FileTransferState {
    private List<FileTransfer> transfers;

    public FileTransferState() {
        transfers = new ArrayList<>();
    }

    public List<FileTransfer> getTransfers() {
        return this.transfers;
    }

    public void addFileTransfer(Entity sender, Entity recipient, File file) {
        FileTransfer transferFile = new FileTransfer(sender, recipient, file);
        transfers.add(transferFile);
        sender.addFilesSending(1);
        recipient.addFilesRecieving(1);

        if (recipient instanceof Satellite) {
            ((Satellite) recipient).decreaseStorage(file.getSize());
        }

        recipient.addFile(file.getName(), "", file.getSize());
    }

    public void removeFileTransfers(List<FileTransfer> finished) {
        transfers.removeAll(finished);
    }

    public boolean incrementState(FileTransfer fileTransfer, List<String> entitiesInRange) {
        Entity sender = fileTransfer.getSender();
        Entity recipient = fileTransfer.getRecipient();

        if (!entitiesInRange.contains(recipient.getId())) {
            recipient.removeFile(fileTransfer.getFile().getName());
            String newContent = fileTransfer.getFile().teleportGlitch();

            if (FileTransfer.teleportGlitchApplies(sender, recipient)) {
                sender.removeFile(fileTransfer.getFile().getName());
                sender.addFile(fileTransfer.getFile().getName(), newContent, newContent.length());
                if (sender instanceof TeleportingSatellite) {
                    recipient.addFile(fileTransfer.getFile().getName(), newContent, newContent.length());
                }
            }
            return false;

        } else {
            int transferredBytes = fileTransfer.getTransferredBytes();
            int bandwidth = Math.min(recipient.calcRecievingBandwidth(), sender.calcSendingBandwidth());

            fileTransfer.setTransferredBytes(Math.min(transferredBytes + bandwidth, fileTransfer.getFile().getSize()));
            String newContent = fileTransfer.getFile().getContent().substring(0, fileTransfer.getTransferredBytes());

            recipient.removeFile(fileTransfer.getFile().getName());
            recipient.addFile(fileTransfer.getFile().getName(), newContent, fileTransfer.getSize());

            if (fileTransfer.getTransferredBytes() == fileTransfer.getFile().getSize()) {
                return false;
            }
        }
        return true;
    }
}
