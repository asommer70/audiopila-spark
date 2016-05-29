package com.thehoick.audiopila.model;

import com.thehoick.audiopila.exc.DAOException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oDeviceDAO implements DeviceDAO {
    private final Sql2o sql2o;

    public Sql2oDeviceDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Device device) throws DAOException {
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
    public List<Device> findAll() throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from devices")
                    .executeAndFetch(Device.class);
        }
    }

    @Override
    public Device findById(int deviceId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from devices where id = :id;")
                    .addParameter("id", deviceId)
                    .executeAndFetchFirst(Device.class);
        }
    }

    @Override
    public Device update(Device device, String field, String value) throws DAOException {
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
    public void destroy(Device device) throws DAOException {
        try (Connection con = sql2o.open()) {
            con.createQuery("delete from devices where id = :id;")
                    .addParameter("id", device.getId())
                    .executeUpdate();
        }
    }

    @Override
    public List<Archive> getArchives(int deviceId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from archives where deviceId = :id")
                    .addParameter("id", deviceId)
                    .executeAndFetch(Archive.class);
        }
    }
}
