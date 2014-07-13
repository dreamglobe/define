'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:DefineCtrl
 * @description
 * # DefineCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('DefineCtrl', function ($scope, Player, Term) {
        // TODO: check login
        $scope.term = Term.get();
        $scope.players = Player.getPlayers();
    });
