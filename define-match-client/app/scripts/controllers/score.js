'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:ScoreCtrl
 * @description
 * # ScoreCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('ScoreCtrl', ['$scope', 'Definition', 'Player', 'MatchServer', 'Score', 'Timer', 'debug',
        function ($scope, Definition, Player, MatchServer, Score, Timer, debug) {
            MatchServer.checkConnection();
            if (debug) {
                Definition.setTerm({name: 'DebugTerm', category: {label: 'DBG', name: 'DEBUG'} });
                $scope.user.isLoged = true;
                $scope.user.name = 'debug';
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
                    {defId: 4, text: 'Definition 4'},
                    {defId: 5, text: 'Definition 5 Definition 5 Definition 5 '}
                ], playerDefinition: {defId: 1, text: 'Definition 1'}});
                Score.createList({
                    scores: [
                        {pid: 1, defId: 1, voteScore: 1, turnScore: 3, totalScore: 3, pidVoters: [3], correctDefinition: true, player: Player.get(1)},
                        {pid: 2, defId: 2, voteScore: 0, turnScore: 0, totalScore: 0, pidVoters: [1, 2, 3, 4], correctDefinition: false, player: Player.get(2)},
                        {pid: 3, defId: 3, voteScore: 0, turnScore: 2, totalScore: 2, pidVoters: [], correctDefinition: true, player: Player.get(3)},
                        {pid: 4, defId: 4, voteScore: 1, turnScore: 1, totalScore: 1, pidVoters: [2], correctDefinition: false, player: Player.get(4)}
                    ],
                    correctDef: {defId: 5, text: 'Definition 5 Definition 5 Definition 5 '},
                    numCorrectVotes: 2
                });
                Timer.set(10000);
            }
            $scope.term = Definition.getTerm();
            $scope.termDefinition = Score.getCorrectDefinition().text;
            $scope.timer = Timer.start(true);
            $scope.scores = Score.getList();
            $scope.numCorrectVotes = Score.getNumCorrectVotes();
            $scope.players = Player.getPlayers();
            $scope.me = Player.me();

            $scope.readyText = $scope.me.isReady ? 'Esperar' : 'Listo';
            $scope.toggleReady = function () {
                $scope.me.isReady = !$scope.me.isReady;
                $scope.readyText = $scope.me.isReady ? 'Esperar' : 'Listo';
                MatchServer.sendReady($scope.me.isReady);
            };
            $scope.sortTable = function (field) {
                if ($scope.sortField == field) {
                    $scope.sortOrder = !$scope.sortOrder;
                } else {
                    $scope.sortOrder = false;
                }
                $scope.sortField = field;
            };
            $scope.sortTable('-turnScore');
        }]);
