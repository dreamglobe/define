'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.MessageHandler
 * @description
 * # MessageHandler
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .service('MessageHandler', ['$rootScope', '$location', 'Console', 'Timer', 'Player', 'Definition', 'Score',
        function ($rootScope, $location, Console, Timer, Player, Definition, Score) {
        var isConnected = false;
        function updateScope(){
            if(!$rootScope.$$phase) {
                $rootScope.$apply();
            }
        }

        function startVote(payload) {
            Timer.set(payload.time);
            Player.startVotePhase();
            Definition.createList(payload);
            Console.log('Inicio de fase Votación');
            $location.path('/vote');
            updateScope();
        }

        function startShowScores(payload) {
            Timer.set(payload.time);
            Player.startResultPhase();
            Score.createList(payload);
            Console.log('Inicio de fase Resultados');
            $location.path('/score');
            updateScope();
        }

        return {
            isConnected: function(){
                return isConnected;
            },
            onConnect: function () {
                isConnected = true;
                Console.log('Conexión abierta');
            },
            onClose: function (msg) {
                isConnected = false;
                Console.log('Conexión cerrada: ' + msg);
                $location.path('/');
            },
            onMessage: function (msg) {
                var payload = JSON && JSON.parse(msg.data);
                switch(payload.type){
                    case 'Start':
                        Console.log('Iniciando ... ');
                        break;

                    case 'Stop':
                        Console.log('La partida ha parado. ');
                        Timer.cancel();
                        $location.path('/stopped');
                        $rootScope.$apply();
                        break;

                    case 'PlayerList':
                        Player.createList(payload.players, $rootScope.user.name);
                        updateScope();
                        break;

                    case 'RemoveUser':
                        Player.remove(payload.pid);
                        updateScope();
                        break;

                    case 'RegisterUser':
                        Player.create(payload);
                        updateScope();
                        break;

                    case 'Starting':
                        $rootScope.config = payload.config;
                        $location.path('/start');
                        updateScope();
                        break;

                    case 'StartDefinition':
                        Timer.set(payload.time);
                        Player.startDefinitionPhase();
                        Definition.setTerm(payload.term);
                        Console.log('Inicio de fase Definición');
                        $location.path('/define');
                        updateScope();
                        break;

                    case 'RegisterUserInVote':
                        Definition.setTerm(payload.term);
                        Definition.createList(payload);
                        startVote(payload);
                        break;

                    case 'StartVote':
                        startVote(payload);
                        break;

                    case 'RegisterUserInShowScores':
                        Definition.setTerm(payload.term);
                        Definition.createList(payload);
                        startShowScores(payload);
                        break;

                    case 'StartShowScores':
                        startShowScores(payload);
                        break;

                    case 'ShowEndScores':
                        Score.createList(payload);
                        $location.path('/final');
                        updateScope();
                        break;

                    case 'UserDefinition':
                        Player.get(payload.pid).definition = true;
                        break;

                    case 'UserVote':
                        Player.get(payload.pid).vote = payload.voteId;
                        break;

                    case 'UserReady':
                        Player.get(payload.pid).isReady = payload.ready;
                        break;

                    case 'ExtendTime':
                        Timer.set(payload.time);
                        Timer.start();
                        updateScope();
                        break;

                    default:

                }
            }
        };
    }]);
