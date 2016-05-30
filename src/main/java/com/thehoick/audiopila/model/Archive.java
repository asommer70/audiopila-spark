package com.thehoick.audiopila.model;

import com.thehoick.audiopila.Main;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.sql2o.Sql2o;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Archive {
    private int id;
    private String name;
    private String path;
    private Date syncDate;
    private int deviceId;

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
                ", deviceId=" + deviceId +
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

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
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

    public List<Audio> refreshAudios(Sql2oSqliteDAO dao) {
        List<Audio> audios = new ArrayList<>();

        // TODO:as maybe make the file extensions list configurable to allow for additional ones.
        Collection files = FileUtils.listFiles(
                new File(this.path),
                new RegexFileFilter("([^*]+(\\.(?i)(mp3|ogg|mp4|m4a|mkv|wav))$)"),
                DirectoryFileFilter.DIRECTORY
        );

        System.out.println("files.size(): " + files.size());

        for (Object file : files) {
            Audio audio = new Audio(file.toString(), this.id);
            try {
                dao.addAudio(audio);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return audios;
    };

}
