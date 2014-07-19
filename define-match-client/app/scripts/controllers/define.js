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
        $scope.termName = Definition.getTerm().name;
        $scope.termCat = Definition.getTerm().category;
        $scope.$watch('termName');
        $scope.$watch('termCat');
        $scope.players = Player.getPlayers();
        $scope.definition = '';
        $scope.lastDefinition = '';
        $scope.sendDefinition = function (){
            $scope.lastDefinition = $scope.definition;
            MatchServer.sendDefinition($scope.lastDefinition);
        };
    });
