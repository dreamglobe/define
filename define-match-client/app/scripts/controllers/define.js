'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:DefineCtrl
 * @description
 * # DefineCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('DefineCtrl', function ($scope, Player, Definition, MatchServer, Timer) {
        MatchServer.checkConnection();

        $scope.timer = Timer.start();
        $scope.term = Definition.getTerm();
        $scope.players = Player.getPlayers();
        $scope.definition = '';
        $scope.sendDefinition = function (){
            MatchServer.sendDefinition($scope.definition);
        };
    });
