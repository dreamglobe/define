'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:StartCtrl
 * @description
 * # StartCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('StartCtrl', ['$scope', 'MatchServer', 'Player', 'debug',
        function ($scope, MatchServer, Player, debug) {
            MatchServer.checkConnection();
            if (debug) {
                $scope.config = {
                    goalPoints: 45,
                    timeLimit: 200,
                    maximumRounds: 20
                };
                $scope.user.isLoged = true;
                $scope.user.name = 'debug';
                Player.createList(
                    [
                        {pid: 1, name: 'debug', totalScore: 0, turnScore: 0},
                        {pid: 2, name: 'debug2', totalScore: 0, turnScore: 1},
                        {pid: 3, name: 'debug3', totalScore: 0, turnScore: 2},
                        {pid: 4, name: 'debug4', totalScore: 0, turnScore: 0}
                    ], $scope.user.name);

            }
            $scope.generalStatus = true;
            $scope.endStatus = false;
            $scope.phaseStatus = false;
            $scope.hasConfig = Boolean($scope.config).valueOf();
            if ($scope.hasConfig) {
                $scope.pointFinish = Boolean($scope.config.goalPoints).valueOf();
                $scope.timeFinish = Boolean($scope.config.timeLimit).valueOf();
                $scope.roundFinish = Boolean($scope.config.maximumRounds).valueOf();
            }
            $scope.checkFinishPoints = function () {
                $scope.pointFinish = !$scope.pointFinish;
            };
            $scope.checkFinishTime = function () {
                $scope.timeFinish = !$scope.timeFinish;
            };
            $scope.checkFinishRound = function () {
                $scope.roundFinish = !$scope.roundFinish;
            };

            $scope.players = Player.getPlayers();
            $scope.startMatch = function () {
                if ($scope.hasConfig) {
                    if (!$scope.pointFinish) {
                        $scope.config.goalPoints = null;
                    }
                    if (!$scope.timeFinish) {
                        $scope.config.timeLimit = null;
                    }
                    if (!$scope.roundFinish) {
                        $scope.config.maximumRounds = null;
                    }
                    MatchServer.sendStartMatch($scope.config);
                }
            };
        }]);
