'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:VoteCtrl
 * @description
 * # VoteCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('VoteCtrl', function ($scope, Definition, Player, MatchServer, Timer) {
        MatchServer.checkConnection();

        $scope.term = Definition.getTerm();
        $scope.timer = Timer.start();
        $scope.players = Player.getPlayers();
        $scope.me = Player.me();
        $scope.definitions = Definition.getList();
        $scope.myDefinition = Definition.getPlayerDefinition();

        $scope.canVote = true;
        $scope.vote = function (defId) {
            $scope.canVote = false;
            Timer.timeout('vote', 5000, function () {
                $scope.canVote = true;
            });
            MatchServer.sendVote(defId);
        };
        $scope.unvote = function () {
            $scope.vote(null);
        };

        $scope.isShowVote = function (defId) {
            return $scope.canVote && $scope.me.vote !== defId;
        };
        $scope.hasVoteForMe = function (code) {
            return $scope.myDefinition.defId === code;
        };
        $scope.isMyVote = function (code) {
            return $scope.me.vote === code;
        };
        $scope.letter = function (defId) {
            return defId !== null ? Definition.getLetterByDefId(defId) : '';
        };
    });
