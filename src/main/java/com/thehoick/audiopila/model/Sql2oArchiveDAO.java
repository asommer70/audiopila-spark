package com.thehoick.audiopila.model;

import com.thehoick.audiopila.exc.DAOException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oArchiveDAO implements ArchiveDAO {
    private final Sql2o sql2o;

    public Sql2oArchiveDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Archive archive) throws DAOException {
        String sql = "INSERT INTO archives(name, path) VALUES (:name, :path)";
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
    public List<Archive> findAll() throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from archives")
                    .executeAndFetch(Archive.class);
        }
    }

    @Override
    public Archive findById(int archiveId) throws DAOException {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * from archives where id = " + archiveId)
                    .executeAndFetch(Archive.class).get(0);
        }
    }
}
