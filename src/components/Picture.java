package components;

public class Picture {
    private String file;
    private String position;

    public Picture(String file, String position) {
        this.file = file;
        this.position = position;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String toString() {
        return "(" + file + ", " + position + ")";
    }
}
