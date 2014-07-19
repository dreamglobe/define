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

        $scope.termName = Definition.getTerm().name;
        $scope.termCat = Definition.getTerm().category;
        $scope.termDefinition = Score.getCorrectDefinition().text;
        $scope.timer = Timer.start();
        $scope.scores = Score.getList();
        $scope.numCorrectVotes = Score.getNumCorrectVotes();
        $scope.players = Player.getPlayers();
        $scope.me = Player.me();

        $scope.readyText = $scope.me.isReady? 'Esperar':'Listo';
        $scope.toggleReady = function(){
            $scope.me.isReady = !$scope.me.isReady;
            $scope.readyText = $scope.me.isReady? 'Esperar':'Listo';
            MatchServer.sendReady($scope.me.isReady);
        };
  });
