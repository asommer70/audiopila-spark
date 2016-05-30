package com.thehoick.audiopila.model;

public class Audio {
    private int id;
    private String name;
    private String path;
    private int playbackTime;
    private int archiveId;
    private int deviceId;
    private int albumId;
    private int albumOrder;

    public Audio(String path, int archiveId) {
        this.path = path;
        String[] parts = path.split("/");
        String name = parts[parts.length - 1];
        this.name = name;

        // Maybe lookup archive based on path?
        this.archiveId = archiveId;
    }

    public int getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(int archiveId) {
        this.archiveId = archiveId;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPlaybackTime() {
        return playbackTime;
    }

    public void setPlaybackTime(int playbackTime) {
        this.playbackTime = playbackTime;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getAlbumOrder() {
        return albumOrder;
    }

    public void setAlbumOrder(int albumOrder) {
        this.albumOrder = albumOrder;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "Audio{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", playbackTime=" + playbackTime +
                ", albumId=" + albumId +
                ", albumOrder=" + albumOrder +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Audio audio = (Audio) o;

        if (id != audio.id) return false;
        if (playbackTime != audio.playbackTime) return false;
        if (albumId != audio.albumId) return false;
        if (albumOrder != audio.albumOrder) return false;
        if (name != null ? !name.equals(audio.name) : audio.name != null) return false;
        return path != null ? path.equals(audio.path) : audio.path == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + playbackTime;
        result = 31 * result + albumId;
        result = 31 * result + albumOrder;
        return result;
    }
}
