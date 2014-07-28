'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.Definition
 * @description
 * # Definition
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .service('Definition', ['_', '$rootScope', function (_, $rootScope) {

        var turnTerm;
        var turnDefinitions = [];
        var posByDefId = {};
        var playerDefinition;

        function updateScope(fn){
            if(!$rootScope.$$phase) {
                $rootScope.$apply(fn);
            }
        }

        function Definition(defId, text) {
            this.defId = defId;
            this.text = text;
        }

        function Term(name, category) {
            this.name = name;
            this.category = category;
        }

        Term.create = function (data) {
            return new Term(data.name, data.category);
        };

        // Static Methods (Service)
        Definition.createList = function (data) {
            turnDefinitions = [];
            posByDefId = {};
            _.each(data.definitions, function (d, index) {
                turnDefinitions.push(new Definition(d.defId, d.text));
                posByDefId[d.defId] = index;
            });
            if(data.playerDefinition) {
                var playerDef = Definition.setPlayerDefinition(data.playerDefinition);
                turnDefinitions.push(playerDef);
                posByDefId[playerDefinition.defId] = data.definitions.length;
            }
            return turnDefinitions;
        };

        Definition.setPlayerDefinition = function (definition) {
            playerDefinition = new Definition(definition.defId, definition.text);
            return playerDefinition;
        };

        Definition.setTerm = function (term) {
            turnTerm = Term.create(term);
            updateScope();
        };

        Definition.getList = function () {
            return turnDefinitions;
        };

        Definition.get = function(defId){
            return turnDefinitions[posByDefId[defId]];
        };

        Definition.getLetterByDefId = function (defId) {
            return String.fromCharCode(65 + posByDefId[defId]);
        };

        Definition.getPlayerDefinition = function () {
            return playerDefinition;
        };

        Definition.getTerm = function () {
            return turnTerm;
        };

        return Definition;
    }]);
