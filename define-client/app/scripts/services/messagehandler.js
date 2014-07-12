'use strict';

/**
 * @ngdoc service
 * @name deffineApp.messageHandler
 * @description
 * # messageHandler
 * Service in the deffineApp.
 */
angular.module('deffineApp')
  .service('Messagehandler', function Messagehandler($rootScope, Console, Player, $location) {
    return {
            onConnect : function(){
                Console.log('Connection open');
//                setInterval(function() {
//                    // Prevent server read timeout.
//                    CrossGate.socket.send('ping');
//                }, 5000);
            },
            onClose : function(){
                Console.log('Info: WebSocket closed.');
                $location.path('/');
            },
            onMessage : function(message) {
                var packet = JSON && JSON.parse(message.data) ;
                switch (packet.type) {

                    case 'StartDefinition':
                        $rootScope.updateResults();
                        $rootScope.isResponsed = {};
                        $location.path('/answer');
                        Console.log('Response phase started ' + packet.time);
                        $rootScope.resetTimeout(packet.time);
                        break;

                    case 'StartVote':
                        Player.resetReady();
                        $rootScope.canVote=true;
                        $rootScope.definitions = packet.definitions;
                        $rootScope.playerDefinition = packet.playerDefinition;
                        $rootScope.resetTimeout(packet.time);
                        $location.path('/vote');
                        Console.log('Vote phase started ' + packet.time);
                        break;

                    case 'ResultForRegisterUser':
                        $rootScope.definitions = packet.definitions;
                        $rootScope.playerDefinition = packet.playerDefinition;

                    case 'Result':
                        Player.resetReady();
                        $rootScope.resetTimeout(packet.time);
                        $rootScope.result = packet.scores;
                        $rootScope.correctDefId = packet.correctDefId;
                        $rootScope.correctNumVotes = packet.correctNumVotes;
                        $location.path('/result');
                        Console.logResults(packet.scores);
                        break;

                    case 'UserDefinition':
                        $rootScope.isResponsed[packet.userId] = true;
                        $rootScope.$apply();
                        Console.log('User '+ $rootScope.players[packet.userId].name + ' has answered');
                        break;

                    case 'UserVote':
                        Player.get(packet.userId).setVote(packet.voteId);
                        break;

                    case 'UserReady':
                        Player.get(packet.userId).setReady(packet.ready);
                        Console.log('User '+ $rootScope.players[packet.userId].name + ' is ready');
                        break;

                    case 'RegisterUser':
                        Player.build(packet);
                        Console.log('New user: ' + packet.name + ' ( ' + packet.pid + ')');
                        break;

                    case 'RemoveUser':
                        Console.log('User ' + $rootScope.players[packet.userId] + ' leaves');
                        Player.remove(packet.userId);
                        break;

                    case 'PlayerList':
                        Player.buildList(packet.players);
                        Console.logUserList(packet.players);
                        break;

                    case 'Start':
                        Console.log("Iniciating...");

                    case 'Stop':
                        $location.path('/stopped');
                        $rootScope.mytimeout.cancel();
                        break;
                    default:
                        Console.log('Unkonwn message: ' + packet.type + " -- " + packet);
                }
            }
        };
  });
