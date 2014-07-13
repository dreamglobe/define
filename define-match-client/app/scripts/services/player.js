'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.Player
 * @description
 * # Player
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .factory('Player', function (_, Console) {
        // AngularJS will instantiate a singleton by calling "new" on this function

        var players = {};
        var playerPid;

        var fields = ['pid', 'name', 'totalScore', 'turnScore', 'hasChange'];
        var fieldsWithNotifier = ['isReady', 'canVote'];
        var fieldsChangeReady = ['definition', 'vote'];
        var all_fields = _.union(fields, fieldsWithNotifier, fieldsChangeReady);

        function Player(pid, name, totalScore, turnScore) {
            Object.defineProperty(this, '__',  // Define property for field values
                { value: {} });

            for (var p in all_fields) {
                this.__[all_fields[p]] = undefined;
            }

            this.pid = pid;
            this.name = name;
            this.totalScore = totalScore;
            this.turnScore = turnScore; // previous Turn Score

            this.definition = null;
            this.vote = null;
            this.isReady = false;

            this.canVote = true;

            //  object maintenance
            this.hasChange = false;
            players[pid] = this;
        };

        // object accessors
        (function define_normal_fields(fields) {
            fields.forEach(function (field_name) {
                Object.defineProperty(Player.prototype, field_name, {
                    get: function () {
                        return this.__ [field_name];
                    },
                    set: function (new_value) {
                        this.__[field_name] = new_value;
                    }
                })
            })
        })(fields);

        (function define_fields_that_track_change(fields) {
            fields.forEach(function (field_name) {
                Object.defineProperty(Player.prototype, field_name, {
                    get: function () {
                        return this.__ [field_name];
                    },
                    set: function (new_value) {
                        var sameValue = this.__[field_name] === newValue;
                        if (!sameValue && !this.__.hasChange) this.__.hasChange = true;
                        if (!sameValue) this.__[field_name] = new_value;
                    }
                })
            })
        })(fieldsWithNotifier);

        (function define_fields_that_affect_ready(fields) {
            fields.forEach(function (field_name) {
                Object.defineProperty(Player.prototype, field_name, {
                    get: function () {
                        return this.__[field_name];
                    },
                    set: function (new_value) {
                        var sameValue = this.__['vote'] === new_value;
                        if (!sameValue) {
                            this.__[field_name] = new_value;
                            this.__['isReady'] = new_value != null;
                            this.__['hasChange'] = true;
                        }
                    }
                })
            })
        })(fieldsChangeReady);

        // Player methods
        Player.prototype.newRound = function () {
            this.consolidateScores();
            this.resetReady();
            this.definition = null;
            this.vote = null;
            this.score = null;
        };

        Player.prototype.consolidateScores = function () {
            this.totalScore = this.score.totalScore;
            this.turnScore = this.score.turnScore;
        };

        Player.prototype.resetReady = function () {
            this.isReady = false;
        };

        // Static methods
        Player.create = function (data) {
            if (data.pid) {
                return new Player(
                    data.pid,
                    data.name,
                    data.totalScore,
                    data.turnScore
                );
                Console.log('Nuevo usuario: ' + data.name + ' (' + data.pid + ')');
            } else {
                Console.log('Nuevo usuario no válido: ' + data);
            }
        };

        Player.remove = function (pid) {
            if (pid != playerPid) {
                Console.log('Usuario ' + player[pid].name + ' (' + pid + ') eliminado.');
                delete player[pid];
            }
        };

        Player.createList = function (list, myPid) {
            players = {};
            playerPid = myPid;
            _.each(list, function (p) {
                Player.build(p)
            });
            Console.logUserList(players);
            return players;
        };

        Player.startDefinitionPhase = function () {
            Player.newRound();
        };

        Player.startVotePhase = function (playerDefinition) {
            Player.resetReady();
            players[playerPid].definition = playerDefinition;
        };

        Player.startResultPhase = function (scores) {
            Player.resetReady();
            // TODO : pass players the scores
        };

        Player.resetReady = function () {
            _.each(player, function (p) {
                p.resetReady()
            });
        };

        Player.newRound = function () {
            _.each(player, function (p) {
                p.newRound()
            });
        };

        Player.hasChange = function () {
            _.any(players, function (p) {
                return p.hasChange()
            });
        }

        Player.getPlayers = function () {
            return players;
        }

        Player.get = function (pid) {
            return playerByPid[pid];
        };

        return Player;
    });
