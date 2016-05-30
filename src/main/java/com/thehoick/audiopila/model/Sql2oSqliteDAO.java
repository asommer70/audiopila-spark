package com.thehoick.audiopila.model;

import com.sun.java.browser.dom.DOMAccessException;
import com.thehoick.audiopila.exc.DAOException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oSqliteDAO implements SqliteDAO {
    private final Sql2o sql2o;

    public Sql2oSqliteDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void addArchive(Archive archive) throws DAOException {
        String sql = "INSERT INTO archives(name, path, deviceId) VALUES (:name, :path, :deviceId)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql)
                    .bind(archive)
                    .executeUpdate()
                    .getKey();
            archive.setId(id);
        } catch (Sql2oException ex) {
            throw new DAOException(ex, "Problem adding archive.");
        }
    }

    @Override
    public List<Archive> findArchives(int deviceId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from archives where deviceId = :id")
                    .addParameter("id", deviceId)
                    .executeAndFetch(Archive.class);
        }
    }

    @Override
    public Archive findArchiveById(int archiveId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from archives where id = :id;")
                    .addParameter("id", archiveId)
                    .executeAndFetchFirst(Archive.class);
        }
    }

    @Override
    public Archive findArchiveByPath(String path) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from archives where path = :path;")
                    .addParameter("id", path)
                    .executeAndFetchFirst(Archive.class);
        }
    }

    @Override
    public Archive updateArchive(Archive archive, String field, String value) throws DAOException {
        try (Connection con = sql2o.open()) {
            con.createQuery("update archives set " + field + " = :value where id = :id;")
                    .addParameter("value", value)
                    .addParameter("id", archive.getId())
                    .executeUpdate();

            return con.createQuery("SELECT * from archives where id = :id;")
                    .addParameter("id", archive.getId())
                    .executeAndFetchFirst(Archive.class);
        }
    }

    @Override
    public void destroyArchive(Archive archive) throws DAOException {
        try (Connection con = sql2o.open()) {
            con.createQuery("delete from archives where id = :id;")
                    .addParameter("id", archive.getId())
                    .executeUpdate();
        }
    }


    public void addDevice(Device device) throws DAOException {
        String sql = "INSERT INTO devices(name, platform) VALUES (:name, :platform)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql)
                    .bind(device)
                    .executeUpdate()
                    .getKey();
            device.setId(id);
        } catch (Sql2oException ex) {
            throw new DAOException(ex, "Problem adding device.");
        }
    }

    @Override
    public List<Device> findDevices() throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from devices")
                    .executeAndFetch(Device.class);
        }
    }

    @Override
    public Device findDeviceById(int deviceId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from devices where id = :id;")
                    .addParameter("id", deviceId)
                    .executeAndFetchFirst(Device.class);
        }
    }

    @Override
    public Device findDeviceByName(String name) throws DOMAccessException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from devices where name = :name;")
                    .addParameter("name", name)
                    .executeAndFetchFirst(Device.class);
        }
    }

    @Override
    public Device updateDevice(Device device, String field, String value) throws DAOException {
        try (Connection con = sql2o.open()) {
            con.createQuery("update devices set " + field + " = :value where id = :id;")
                    .addParameter("value", value)
                    .addParameter("id", device.getId())
                    .executeUpdate();

            return con.createQuery("SELECT * from devices where id = :id;")
                    .addParameter("id", device.getId())
                    .executeAndFetchFirst(Device.class);
        }
    }

    @Override
    public void destroyDevice(Device device) throws DAOException {
        try (Connection con = sql2o.open()) {
            con.createQuery("delete from devices where id = :id;")
                    .addParameter("id", device.getId())
                    .executeUpdate();
        }
    }

    @Override
    public List<Archive> getDeviceArchives(int deviceId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from archives where deviceId = :id")
                    .addParameter("id", deviceId)
                    .executeAndFetch(Archive.class);
        }
    }

    @Override
    public void addAudio(Audio audio) throws Exception {
        String sql = "INSERT INTO audios(name, path, archiveId) VALUES (:name, :path, :archiveId)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql)
                    .bind(audio)
                    .executeUpdate()
                    .getKey();
            audio.setId(id);
        } catch (Sql2oException ex) {
            throw new DAOException(ex, "Problem adding device.");
        }
    }

    @Override
    public Audio findAudioById(int audioId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from audios where id = :id")
                    .addParameter("id", audioId)
                    .executeAndFetchFirst(Audio.class);
        }
    }

    @Override
    public List<Audio> findAudiosByArchive(int archiveId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from audios where archiveId = :archiveId")
                    .addParameter("archiveId", archiveId)
                    .executeAndFetch(Audio.class);
        }
    }

    @Override
    public List<Audio> findAudiosByDevice(int deviceId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery(" select distinct archives.deviceId, " +
                    "audios.id, audios.name, audios.path, audios.playbackTime, audios.albumId, audios.albumOrder " +
                    "from audios, archives where archives.deviceId = :deviceId")
                    .addParameter("deviceId", deviceId)
                    .executeAndFetch(Audio.class);
        }
    }

    @Override
    public void destroyAudio(Audio audio) throws DAOException {
        try (Connection con = sql2o.open()) {
            con.createQuery("delete from audios where id = :id;")
                    .addParameter("id", audio.getId())
                    .executeUpdate();
        }
    }

    @Override
    public Audio updateAudio(Audio audio, String field, String value) throws Exception {
        try (Connection con = sql2o.open()) {
            con.createQuery("update audios set " + field + " = :value where id = :id;")
                    .addParameter("value", value)
                    .addParameter("id", audio.getId())
                    .executeUpdate();

            return con.createQuery("SELECT * from audios where id = :id;")
                    .addParameter("id", audio.getId())
                    .executeAndFetchFirst(Audio.class);
        }
    }
}
