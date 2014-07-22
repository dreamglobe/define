'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.MessageHandler
 * @description
 * # MessageHandler
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .service('MessageHandler', function MessageHandler($rootScope, $location, Console, Timer, Player, Definition, Score) {
        var isConnected = false;
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
                        $rootScope.$apply();
                        break;

                    case 'RemoveUser':
                        Player.remove(payload.pid);
                        $rootScope.$apply();
                        break;

                    case 'RegisterUser':
                        Player.create(payload);
                        $rootScope.$apply();
                        break;

                    case 'Starting':
                        $rootScope.config = payload.config;
                        $location.path('/start');
                        $rootScope.$apply();
                        break;

                    case 'StartDefinition':
                        Timer.set(payload.time);
                        Player.startDefinitionPhase();
                        Definition.setTerm(payload.term);
                        Console.log('Inicio de fase Definición');
                        $location.path('/define');
                        $rootScope.$apply();
                        break;

                    case 'RegisterUserInVote':
                        Definition.setTerm(payload.term);
                        Definition.createList(payload);
                        // extend StartVote
                    case 'StartVote':
                        Timer.set(payload.time);
                        Player.startVotePhase();
                        Definition.createList(payload);
                        Console.log('Inicio de fase Votación');
                        $location.path('/vote');
                        $rootScope.$apply();
                        break;

                    case 'RegisterUserInShowScores':
                        Definition.setTerm(payload.term);
                        Definition.createList(payload);
                    // extend StartShowScores
                    case 'StartShowScores':
                        Timer.set(payload.time);
                        Player.startResultPhase();
                        Score.createList(payload);
                        Console.log('Inicio de fase Resultados');
                        $location.path('/score');
                        $rootScope.$apply();
                        break;

                    case 'ShowEndScores':
                        Score.createList(payload);
                        $location.path('/final');
                        $rootScope.$apply();
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
                    default:

                }
            }
        };
    });
