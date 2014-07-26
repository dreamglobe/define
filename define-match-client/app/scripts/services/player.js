'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.Player
 * @description
 * # Player
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .factory('Player', function ($rootScope, _, Console) {
        // AngularJS will instantiate a singleton by calling "new" on this function

        var players = {};
        var playerPid;

        var fields = ['pid', 'name', 'totalScore', 'turnScore', 'score', 'hasChange'];
        var fieldsWithNotifier = ['isReady'];
        var fieldsChangeReady = ['definition', 'vote'];
        var allFields = _.union(fields, fieldsWithNotifier, fieldsChangeReady);

        function updateScope(fn){
            if(!$rootScope.$$phase) {
                $rootScope.$apply(fn);
            }
        }

        function Player(pid, name, totalScore, turnScore) {
            Object.defineProperty(this, '__',  // Define property for field values
                { value: {} });
            var self = this;
            _.each(allFields, function (p) {
                self.__[allFields[p]] = undefined;
            });

            this.pid = pid;
            this.name = name;
            this.totalScore = totalScore;
            this.turnScore = turnScore; // previous Turn Score

            this.definition = false;
            this.vote = null;
            this.isReady = false;

            //  object maintenance
            this.hasChange = false;
            players[pid] = this;
        }

        // object accessors
        (function defineNormalFields(fields) {
            fields.forEach(function (fieldName) {
                Object.defineProperty(Player.prototype, fieldName, {
                    get: function () {
                        return this.__[fieldName];
                    },
                    set: function (newValue) {
                        this.__[fieldName] = newValue;
                    }
                });
            });
        })(fields);

        (function defineFieldsThatTrackChange(fields) {
            fields.forEach(function (fieldName) {
                Object.defineProperty(Player.prototype, fieldName, {
                    get: function () {
                        return this.__[fieldName];
                    },
                    set: function (newValue) {
                        var sameValue = this.__[fieldName] === newValue;
                        if (!sameValue) {
                            if (!this.__.hasChange) {
                                this.__.hasChange = true;
                            }
                            var self = this;
                            self.__[fieldName] = newValue;

                        }

                    }
                });
            });
        })(fieldsWithNotifier);

        (function defineFieldsThatAffectReady(fields) {
            fields.forEach(function (fieldName) {
                Object.defineProperty(Player.prototype, fieldName, {
                    get: function () {
                        return this.__[fieldName];
                    },
                    set: function (newValue) {
                        var sameValue = this.__.vote === newValue;
                        if (!sameValue) {
                            var self = this;
                            updateScope(function () {
                                self.__[fieldName] = newValue;
                                self.__.isReady = newValue !== null;
                                self.__.hasChange = true;
                            });
                        }
                    }
                });
            });
        })(fieldsChangeReady);

        // Player methods
        Player.prototype.newRound = function () {
            this.resetReady();
            this.definition = null;
            this.vote = null;
            this.score = null;
        };

        Player.prototype.consolidateScores = function () {
            if (this.score) {
                this.totalScore = this.score.totalScore;
                this.turnScore = this.score.turnScore;
            }
        };

        Player.prototype.resetReady = function () {
            this.isReady = false;
        };

        Player.prototype.resetChange = function () {
            this.__.hasChange = false;
        };

        // Static methods
        Player.create = function (data) {
            if (data.pid) {
                Console.log('Nuevo usuario: ' + data.name + ' (' + data.pid + ')');
                return new Player(
                    data.pid,
                    data.name,
                    data.totalScore,
                    data.turnScore
                );
            } else {
                Console.log('Nuevo usuario no válido: ' + data);
            }
        };

        Player.remove = function (pid) {
            if (pid !== playerPid) {
                Console.log('Usuario ' + players[pid].name + ' (' + pid + ') eliminado.');
                delete players[pid];
            }
        };

        Player.createList = function (list, name) {
            players = {};
            playerPid = _.find(list, function (p) {return p.name === name;}).pid;
            _.each(list, function (p) {Player.create(p);});
            Console.logUserList(players);
            return players;
        };

        Player.startDefinitionPhase = function () {
            Player.newRound();
        };

        Player.startVotePhase = function () {
            Player.resetReady();
        };

        Player.startResultPhase = function () {
            Player.resetReady();
        };

        Player.resetReady = function () {
            _.each(players, function (p) {
                p.resetReady();
            });
        };

        Player.resetChange = function () {
            _.each(players, function (p) {
                p.resetChange();
            });
        };

        Player.newRound = function () {
            _.each(players, function (p) {
                p.newRound();
            });
        };

        Player.getPlayers = function () {
            return players;
        };

        Player.get = function (pid) {
            return players[pid];
        };

        Player.me = function () {
            return players[playerPid];
        };

        return Player;
    });
