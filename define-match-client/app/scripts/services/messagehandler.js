'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.MessageHandler
 * @description
 * # MessageHandler
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .service('MessageHandler', function MessageHandler($location, Console, Timer, Term, Player, Definition) {
        // AngularJS will instantiate a singleton by calling "new" on this function
        return {
            onConnect: function () {
                Console.log('Conexión abierta');
            },
            onClose: function (msg) {
                Console.log('Conexión cerrada: ' + msg);
                $location.path('/');
            },
            onMessage: function (msg) {
                var payload = JSON && JSON.parse(msg.data);
                switch(payload.type){
                    case 'Start':
                        Console.log("Iniciando ... ");
                        break;

                    case 'Stop':
                        Console.log("La partida ha parado. ");
                        Timer.cancel();
                        $location.path('/stopped');
                        break;

                    case 'PlayerList':
                        Player.createList(payload);
                        break;

                    case 'RegisterUser':
                        Player.create(payload);
                        break;

                    case 'RemoveUser':
                        Player.remove(payload.pid);
                        break;

                    case 'ResultForRegisterUser':

                        break;

                    case 'StartDefinition':
                        Timer.start(payload.time);
                        Player.startDefinitionPhase();
                        Term.set(payload.term);
                        Console.log('Inicio de fase Definición');
                        $location.path('/define');
                        break;

                    case 'StartVote':
                        Timer.start(payload.time);
                        Player.startVotePhase(payload.playerDefinition);
                        Definition.createList(payload.definitions);
                        Console.log('Inicio de fase Votación');
                        $location.path('/vote');
                        break;

                    case 'Result':
                        Timer.start(payload.time);
                        Player.startResultPhase(payload.scores);
                        Definition.correctDefinition(payload.correctDefId, payload.correctNumVotes);
                        Console.log('Inicio de fase Resultados');
                        $location.path('/vote');
                        break;

                    case 'UserDefinition':

                        break;

                    case 'UserVote':

                        break;

                    case 'UserReady':

                        break;
                    default:

                }
            }
        }
    });
