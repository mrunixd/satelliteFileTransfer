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
    }

    public void removeFileTransfers(List<FileTransfer> finished) {
        transfers.removeAll(finished);
    }

    public boolean incrementState(FileTransfer fileTransfer, List<String> entitiesInRange) {
        Entity sender = fileTransfer.getSender();
        Entity recipient = fileTransfer.getRecipient();

        if (!entitiesInRange.contains(recipient.getId())) {
            if (sender instanceof Device && recipient instanceof TeleportingSatellite) {
                TeleportingSatellite satellite = (TeleportingSatellite) recipient;
                if (satellite.didTeleport()) {
                    String newContent = fileTransfer.getFile().teleportGlitch();

                    recipient.removeFile(fileTransfer.getFile().getName());

                    sender.removeFile(fileTransfer.getFile().getName());
                    sender.addFile(fileTransfer.getFile().getName(), newContent, newContent.length());
                } else {
                    recipient.removeFile(fileTransfer.getFile().getName());
                }
            } else if (sender instanceof TeleportingSatellite) {
                TeleportingSatellite satellite = (TeleportingSatellite) sender;
                if (satellite.didTeleport()) {
                    String newContent = fileTransfer.getFile().teleportGlitch();

                    sender.removeFile(fileTransfer.getFile().getName());
                    sender.addFile(fileTransfer.getFile().getName(), newContent, newContent.length());
                    recipient.removeFile(fileTransfer.getFile().getName());
                    recipient.addFile(fileTransfer.getFile().getName(), newContent, newContent.length());
                } else {
                    recipient.removeFile(fileTransfer.getFile().getName());
                }
            }

            sender.addFilesSending(-1);
            recipient.addFilesRecieving(-1);
            return false;

        } else {
            int curr = fileTransfer.getTransferredBytes();
            int recievingBandwidth = recipient.calcRecievingBandwidth();
            int sendingBandwidth = sender.calcSendingBandwidth();

            curr += recievingBandwidth < sendingBandwidth ? recievingBandwidth : sendingBandwidth;
            fileTransfer.setTransferredBytes(curr);
            curr = curr < fileTransfer.getFile().getSize() ? curr : fileTransfer.getFile().getSize();

            String newContent = fileTransfer.getFile().getContent().substring(0, curr);

            recipient.removeFile(fileTransfer.getFile().getName());
            recipient.addFile(fileTransfer.getFile().getName(), newContent, fileTransfer.getSize());

            if (fileTransfer.getTransferredBytes() == fileTransfer.getFile().getSize()) {
                sender.addFilesSending(-1);
                recipient.addFilesRecieving(-1);
                return false;
            }
        }
        return true;
    }
}
