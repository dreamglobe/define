'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:StartCtrl
 * @description
 * # StartCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('StartCtrl', function ($scope, MatchServer, Player) {
        MatchServer.checkConnection();
        $scope.hasConfig = Boolean($scope.config).valueOf();
        if($scope.hasConfig) {
            $scope.pointFinish = Boolean($scope.config.goalPoints).valueOf();
            $scope.timeFinish = Boolean($scope.config.timeLimit).valueOf();
            $scope.roundFinish = Boolean($scope.config.maximumRounds).valueOf();
        }
        $scope.checkFinishPoints = function(){
            $scope.pointFinish = !$scope.pointFinish
        };
        $scope.checkFinishTime = function(){
            $scope.timeFinish = !$scope.timeFinish;
        };
        $scope.checkFinishRound = function(){
            $scope.roundFinish = !$scope.roundFinish;
        };

        $scope.players = Player.getPlayers();
        $scope.startMatch = function () {
            if($scope.hasConfig) {
                if(!$scope.pointFinish) {
                    $scope.config.goalPoints = null;
                }
                if(!$scope.timeFinish) {
                    $scope.config.timeLimit = null;
                }
                if(!$scope.roundFinish) {
                    $scope.config.maximumRounds = null;
                }
                MatchServer.sendStartMatch($scope.config);
            }
        };
    });
