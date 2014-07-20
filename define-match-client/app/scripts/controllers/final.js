'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:FinalCtrl
 * @description
 * # FinalCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
  .controller('FinalCtrl', function ($scope, Player, Definition, MatchServer, Score) {
        MatchServer.checkConnection();

        $scope.termName = Definition.getTerm().name;
        $scope.termCat = Definition.getTerm().category;
        $scope.termDefinition = Score.getCorrectDefinition().text;
        $scope.scores = Score.getList();
        $scope.numCorrectVotes = Score.getNumCorrectVotes();
        $scope.players = Player.getPlayers();
        $scope.me = Player.me();

        $scope.readyText = $scope.me.isReady? 'Esperar':'Listo';
        $scope.finish = function(){
            MatchServer.logout("01","User logout");
        };
  });
