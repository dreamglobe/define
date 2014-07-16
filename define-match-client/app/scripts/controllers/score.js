'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:ScoreCtrl
 * @description
 * # ScoreCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
  .controller('ScoreCtrl', function ($scope, Player, Definition, MatchServer, Score, Timer) {
        MatchServer.checkConnection();

        $scope.term = Definition.getTerm();
        $scope.termDefinition = Score.getCorrectDefinition().text;
        $scope.timer = Timer.start();
        $scope.scores = Score.getList();
        $scope.numCorrectVotes = Score.getNumCorrectVotes();
        $scope.players = Player.getPlayers();
        $scope.me = Player.me();

        $scope.readyText = $scope.me.ready? 'Esperar':'Listo';
        $scope.toggleReady = function(){
            $scope.me.ready = !$scope.me.ready;
            $scope.readyText = $scope.me.ready? 'Esperar':'Listo';
            MatchServer.sendReady($scope.me.ready);
        };
  });
