'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:VoteCtrl
 * @description
 * # VoteCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('VoteCtrl', ['$scope', 'Definition', 'Player', 'MatchServer', 'Timer', 'debug',
        function ($scope, Definition, Player, MatchServer, Timer, debug) {
            MatchServer.checkConnection();
            if (debug) {
                Definition.setTerm({name: 'DebugTerm', category: {label: 'DBG', name: 'DEBUG'} });
                Player.createList(
                    [
                        {pid: 1, name: 'debug', totalScore: 0, turnScore: 0},
                        {pid: 2, name: 'debug2', totalScore: 0, turnScore: 1},
                        {pid: 3, name: 'debug3', totalScore: 0, turnScore: 2},
                        {pid: 4, name: 'debug4', totalScore: 0, turnScore: 0}
                    ], 'debug');
                Definition.createList({definitions: [
                    {defId: 2, text: 'Definition 2'},
                    {defId: 3, text: 'Definition 3'},
                    {defId: 4, text: 'Definition 4'}
                ], playerDefinition: {defId: 1, text: 'Definition 1'}});
                Timer.set(10000);
            }
            $scope.term = Definition.getTerm();
            $scope.timer = Timer.start();
            $scope.players = Player.getPlayers();
            $scope.me = Player.me();
            $scope.definitions = Definition.getList();
            $scope.myDefinition = Definition.getPlayerDefinition();
            $scope.cssAnim = {transition: 'width ' + $scope.timer / 1000 + 's linear', width: '0%'};
            $scope.canVote = true;
            $scope.vote = function (defId) {
                $scope.canVote = false;
                Timer.timeout('vote', function () {
                    $scope.canVote = true;
                }, 5000);
                MatchServer.sendVote(defId);
            };
            $scope.unvote = function () {
                $scope.vote(null);
            };
            $scope.isShowVote = function (defId) {
                return $scope.canVote && $scope.me.vote !== defId && Definition.getPlayerDefinition().defId !== defId;
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
            $scope.playerVote = function (player) {
                var vote = player.vote;
                if (vote) {
                    return $scope.letter(vote);
                }
            };
            $scope.sortTable = function(field){
                if($scope.sortField == field){
                    $scope.sortOrder = !$scope.sortOrder;
                }else{
                    $scope.sortOrder = false;
                }
                $scope.sortField = field;
            };
            $scope.sortTable('pos()');
        }]);
