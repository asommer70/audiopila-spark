package com.thehoick.audiopila.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DeviceDAOTest {
    private SqliteDAO dao;
    Sql2o sql2o;
    Connection con;
    String hostname;
    String osName;
    Device device;

    @Before
    public void setUp() throws Exception {
        String connectionString = migrate();
        sql2o = new Sql2o(connectionString, "", "");
        dao = new Sql2oSqliteDAO(sql2o);
        con = sql2o.open();

        hostname = java.net.InetAddress.getLocalHost().getHostName();
        osName = System.getProperty("os.name");
        device = new Device(hostname, osName);
    }

    public String migrate() {
        String dbPath = this.getClass().getResource("/db/").toString();

        // Live database.
//        String connectionString = "jdbc:sqlite:" + dbPath + "audiopila.db";
        // Test database.
        String connectionString = "jdbc:sqlite:" + dbPath + "test.db";

        // Uncomment to perform migration.
        // May have to delete database first to avoide checksum mismatch...
//        Flyway flyway = new Flyway();
//        flyway.setDataSource(connectionString, null, null);
//        flyway.migrate();

        return connectionString;
    }

    @After
    public void tearDown() throws Exception {
        // Clear the data manually.
        String sql = "delete from devices; delete from archives; delete from sqlite_sequence;";
        con.createQuery(sql)
                .executeUpdate();
        con.close();
    }

    @Test
    public void addingDeviceSetsId() throws Exception {
        int originalId = device.getId();

        dao.addDevice(device);

        assertNotEquals(originalId, device.getId());
    }

    @Test
    public void allDevicesAreReturnedByFindAll() throws Exception {
        dao.addDevice(device);
        assertEquals(dao.findDevices().size(), 1);
    }

    @Test
    public void noDevicesReturnsEmptyList() throws Exception {
        assertEquals(dao.findDevices().size(), 0);
    }

    @Test
    public void existingDevicesCanBeFoundById() throws Exception {
        dao.addDevice(device);

        Device savedDevice = dao.findDeviceById(device.getId());

        assertEquals(savedDevice, device);
    }

    @Test
    public void existingDevicesCanBeFoundByName() throws Exception {
        dao.addDevice(device);

        Device savedDevice = dao.findDeviceByName("linux.arse");

        assertEquals(savedDevice, device);
    }

    @Test
    public void existingDeviceCanBeUpdated() throws Exception {
        dao.addDevice(device);

        assertEquals(device.getName(), "linux.arse");
        device = dao.updateDevice(device, "name", "linux");

        assertEquals(device.getName(), "linux");
    }

    @Test
    public void deleteRemovesExistingDevice() throws Exception {
        dao.addDevice(device);
        assertEquals(dao.findDevices().size(), 1);

        dao.destroyDevice(device);
        Device destroyedDevice = dao.findDeviceById(device.getId());

        assertEquals(destroyedDevice, null);
        assertEquals(dao.findDevices().size(), 0);
    }

    @Test
    public void listArchivesReturnsAllArchivesForDevice() throws Exception {
        dao.addDevice(device);
        SqliteDAO archiveDAO = new Sql2oSqliteDAO(sql2o);

        Archive archive1 = new Archive("/Users/adam/Music");
        Archive archive2 = new Archive("/Volumes/TarDisk/Music");
        Archive archive3 = new Archive("/Volumes/sands/Music");
        archive1.setDeviceId(device.getId());
        archive2.setDeviceId(device.getId());
        archive3.setDeviceId(device.getId());
        archiveDAO.addArchive(archive1);
        archiveDAO.addArchive(archive2);
        archiveDAO.addArchive(archive3);
        System.out.println("archive1: " + archive1);
        System.out.println("archive2: " + archive2);
        System.out.println("archive3: " + archive3);
        System.out.println("device: " + device);

        List<Archive> archives = dao.getDeviceArchives(device.getId());
        assertEquals(3, archives.size());
    }
}