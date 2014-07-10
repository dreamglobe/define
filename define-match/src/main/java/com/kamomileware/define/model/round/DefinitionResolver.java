package com.kamomileware.define.model.round;

/**
 * Created by pepe on 10/07/14.
 */
public interface DefinitionResolver {
    TermDefinition resolveDefinitionId(Integer id);
    MatchConfiguration getMatchConf();
}
