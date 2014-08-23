package com.kamomileware.define.term.service;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by pepe on 9/08/14.
 */
public interface DBService {

    void clear();

    void importBD(String jsonDump);

    String export() throws JsonProcessingException;
}
