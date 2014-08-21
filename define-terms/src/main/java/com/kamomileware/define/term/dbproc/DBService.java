package com.kamomileware.define.term.dbproc;

/**
 * Created by pepe on 9/08/14.
 */
public interface DBService {

    void clear();

    void importBD(String jsonDump);

    String export();
}
