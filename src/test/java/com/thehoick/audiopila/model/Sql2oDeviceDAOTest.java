package com.thehoick.audiopila.model;

import com.thehoick.audiopila.exc.DAOException;
import jdk.nashorn.internal.runtime.ECMAException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.nio.file.NotDirectoryException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Sql2oDeviceDAOTest {
    private Sql2oDeviceDAO dao;
    Sql2o sql2o;
    Connection con;
    String hostname;
    String osName;
    Device device;

    @Before
    public void setUp() throws Exception {
        String connectionString = migrate();
        sql2o = new Sql2o(connectionString, "", "");
        dao = new Sql2oDeviceDAO(sql2o);
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
        try {
            con.createQuery(sql)
                    .executeUpdate();
        } catch (Sql2oException ex) {
//            throw new DAOException(ex, "Problem removing data.");
            ex.printStackTrace();
        }
        con.close();
    }

    @Test
    public void addingDeviceSetsId() throws Exception {
        int originalId = device.getId();

        dao.add(device);

        assertNotEquals(originalId, device.getId());
    }

    @Test
    public void allDevicesAreReturnedByFindAll() throws Exception {
        dao.add(device);
        assertEquals(dao.findAll().size(), 1);
    }

    @Test
    public void noDevicesReturnsEmptyList() throws Exception {
        assertEquals(dao.findAll().size(), 0);
    }

    @Test
    public void existingDevicesCanBeFoundById() throws Exception {
        dao.add(device);

        Device savedDevice = dao.findById(device.getId());

        assertEquals(savedDevice, device);
    }

    @Test
    public void existingDeviceCanBeUpdated() throws Exception {
        dao.add(device);

        assertEquals(device.getName(), "linux.arse");
        device = dao.update(device, "name", "linux");

        assertEquals(device.getName(), "linux");
    }

    @Test
    public void deleteRemovesExistingDevice() throws Exception {
        dao.add(device);
        assertEquals(dao.findAll().size(), 1);

        dao.destroy(device);
        Device destroyedDevice = dao.findById(device.getId());

        assertEquals(destroyedDevice, null);
        assertEquals(dao.findAll().size(), 0);
    }

    @Test
    public void listArchivesReturnsAllArchivesForDevice() throws Exception {
        dao.add(device);
        ArchiveDAO archiveDAO = new Sql2oArchiveDAO(sql2o);

        Archive archive1 = new Archive("/Users/adam/Music");
        Archive archive2 = new Archive("/Volumes/TarDisk/Music");
        Archive archive3 = new Archive("/Volumes/sands/Music");
        archive1.setDeviceId(device.getId());
        archive2.setDeviceId(device.getId());
        archive3.setDeviceId(device.getId());
        archiveDAO.add(archive1);
        archiveDAO.add(archive2);
        archiveDAO.add(archive3);
        System.out.println("archive1: " + archive1);
        System.out.println("archive2: " + archive2);
        System.out.println("archive3: " + archive3);
        System.out.println("device: " + device);

        List<Archive> archives = dao.getArchives(device.getId());
        assertEquals(3, archives.size());
    }
}