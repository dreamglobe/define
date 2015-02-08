package com.kamomileware.define.model.round;

import com.kamomileware.define.model.match.MatchConfiguration;

import java.util.Optional;

/**
 * Created by pepe on 10/07/14.
 */
public interface DefinitionResolver {
    Optional<TermDefinition> findDefinitionById(Integer id);
    MatchConfiguration getMatchConf();
}
