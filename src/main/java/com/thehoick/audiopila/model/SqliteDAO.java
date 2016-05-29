package com.thehoick.audiopila.model;

import com.sun.java.browser.dom.DOMAccessException;
import com.thehoick.audiopila.exc.DAOException;

import java.util.List;

public interface SqliteDAO {
    void addArchive(Archive archive) throws DAOException;
    List<Archive> findArchives(int deviceId) throws DAOException;
    Archive findArchiveById(int archiveId) throws DAOException;
    Archive updateArchive(Archive archive, String field, String value) throws DAOException;
    void destroyArchive(Archive archive) throws DAOException;

    void addDevice(Device device) throws DAOException;
    List<Device> findDevices() throws DAOException;
    Device findDeviceById(int deviceId) throws DAOException;
    Device findDeviceByName(String name) throws DOMAccessException;
    Device updateDevice(Device device, String field, String value) throws DAOException;
    void destroyDevice(Device device) throws DAOException;
    List<Archive> getDeviceArchives(int deviceId) throws DAOException;
}
