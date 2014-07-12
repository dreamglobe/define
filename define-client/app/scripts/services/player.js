'use strict';

/**
 * @ngdoc service
 * @name deffineApp.Player
 * @description
 * # Player
 * Service in the deffineApp.
 */
angular.module('deffineApp')
    .factory('Player', function ( ) {
        // AngularJS will instantiate a singleton by calling "new" on this function
        var players = [];
        var playerByPid = {};

        function Player(pid, name, isReady, totalPoints, lastPhasePoints) {
            this.pid = pid;
            this.name = name;
            this.isReady = isReady;
            this.totalPoints = totalPoints;
            this.lastPhasePoints = lastPhasePoints;
            this.canVote = true;
            this.vote = null;
            this.hasResponse = false;
            playerByPid[pid] = this;
            players.push(this);
        }

        Player.prototype.newRound = function () {
            this.resetReady();
            this.resetVote();
            this.consolidateScores();
        };

        Player.prototype.consolidateScores = function () {
            this.totalPoints += this.lastPhasePoints;
            this.lastPhasePoints = 0;
        }

        Player.prototype.resetReady = function () {
            this.isReady = false;
        };

        Player.prototype.setReady = function(isReady){
            this.isReady = isReady;
        };

        Player.prototype.resetVote = function () {
            this.vote = null;
        };

        Player.prototype.setVote = function(voteId){
            this.vote = voteId;
            this.setReady(true);
        };

        Player.build = function (data) {
            if (data.pid) {
                return new Player(
                    data.pid,
                    data.name,
                    data.isReady,
                    data.totalPoints,
                    data.lastPhasePoints
                );
            }
        };

        /* FactoryMethod */
        Player.remove = function (pid){
            delete playerByPid[pid];
        };

        Player.buildList = function (list){
            playerByPid = {};
            if(list.length > 0){

                for(var i in  list){
                    Player.build(list[i]);
                }
                return playerByPid;
            } else {
                return {};
            }
        };

        Player.getPlayers = function (){
            return players;
        }


        Player.resetReady = function(){
            for(var pid in playerByPid){
                player[pid].resetReady();
            }
        };

        Player.newRound = function(){
            for(var pid in playerByPid){
                player[pid].newRound();
            }
        };

        Player.get = function( pid ){
            return playerByPid[pid];
        };

        return Player;
    });
