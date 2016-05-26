package com.thehoick.audiopila.model;

import java.util.List;

public interface ArchiveDAO {
    boolean add(Archive archive);

    List<Archive> findAll();
}
