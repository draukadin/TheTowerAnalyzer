package com.pphi.tower.model.googledrive;

import java.util.Objects;

public class DriveFile {
    private final String id;
    private final String name;
    private final String contents;

    public DriveFile(String id, String name, String contents) {
        this.id = id;
        this.name = name;
        this.contents = contents;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String contents() {
        return contents;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DriveFile) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contents);
    }

    @Override
    public String toString() {
        return "DriveFile[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "contents=" + contents + ']';
    }
}
