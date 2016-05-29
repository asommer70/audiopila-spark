package com.thehoick.audiopila.model;

import com.thehoick.audiopila.exc.DAOException;

import java.util.List;

public interface DeviceDAO {
    void add(Device device) throws DAOException;

    List<Device> findAll() throws DAOException;

    Device findById(int deviceId) throws DAOException;

    Device update(Device device, String field, String value) throws DAOException;

    void destroy(Device device) throws DAOException;

    List<Archive> getArchives(int deviceId) throws DAOException;
}
