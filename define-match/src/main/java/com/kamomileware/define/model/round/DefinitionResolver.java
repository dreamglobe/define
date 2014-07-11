package com.kamomileware.define.model.round;

import java.util.Optional;

/**
 * Created by pepe on 10/07/14.
 */
public interface DefinitionResolver {
    Optional<TermDefinition> findDefinitionById(Integer id);
    MatchConfiguration getMatchConf();
}
