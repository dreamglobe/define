'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:DefineCtrl
 * @description
 * # DefineCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('DefineCtrl',  ['$scope', 'Definition', 'Player', 'MatchServer', 'Timer', 'debug',
        function ($scope, Definition, Player, MatchServer, Timer, debug) {
        MatchServer.checkConnection();

        if(debug){
            Definition.setTerm({name:'DebugTerm', category:{label:'DBG', name:'DEBUG'} });
            Player.createList(
                [{pid:1,name:'debug',totalScore:0,turnScore:0},
                    {pid:2,name:'debug',totalScore:0,turnScore:1},
                    {pid:3,name:'debug',totalScore:0,turnScore:2},
                    {pid:4,name:'debug',totalScore:0,turnScore:0}
            ], 'debug');
            Timer.set(30000);
        }

        $scope.timer = Timer.start();
        $scope.term = Definition.getTerm();
        $scope.players = Player.getPlayers();
        $scope.definition = '';
        $scope.lastDefinition = '';
        $scope.sendDefinition = function (){
            $scope.lastDefinition = $scope.definition;
            MatchServer.sendDefinition($scope.lastDefinition);
        };
        $scope.playerReady =function(player){
            if(player.definition){
                return '<span class="glyphicon glyphicon-ok"></span>';
            }
        };
    }]);
