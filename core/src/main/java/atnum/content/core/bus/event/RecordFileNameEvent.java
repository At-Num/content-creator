package atnum.content.core.bus.event;

public class RecordFileNameEvent {
    private final String fileName;

    public RecordFileNameEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        System.out.println("RecordFileNameEvent getFileName := "+fileName);
        return fileName;
    }
}
