package com.thehoick.audiopila.model;

import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.Date;

public class Archive {
    private int id;
    private String name;
    private String path;
    private Date syncDate;

    public Archive(String path) throws NotDirectoryException {
        // Check path exists.
        if (Files.notExists(Paths.get(path))) {
            throw new NotDirectoryException("Not a valid Archive directory.");
        }

        this.path = path;
        String[] parts = path.split("/");
        if (parts.length >= 2) {
            this.name = parts[parts.length - 2] + " " + parts[parts.length - 1];
        } else {
            this.name = parts[parts.length - 1];
        }
    }

    @Override
    public String toString() {
        return "Archive{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", syncDate=" + syncDate +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Date getSyncDate() {
        return syncDate;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Archive archive = (Archive) o;

        if (id != archive.id) return false;
        if (name != null ? !name.equals(archive.name) : archive.name != null) return false;
        if (!path.equals(archive.path)) return false;
        return syncDate != null ? syncDate.equals(archive.syncDate) : archive.syncDate == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + path.hashCode();
        result = 31 * result + (syncDate != null ? syncDate.hashCode() : 0);
        return result;
    }
}
