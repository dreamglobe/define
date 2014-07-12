'use strict';

/**
 * @ngdoc service
 * @name deffineApp.messageHandler
 * @description
 * # messageHandler
 * Service in the deffineApp.
 */
angular.module('deffineApp')
  .service('Messagehandler', function Messagehandler($rootScope, Console, $location) {
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
                        $rootScope.isReady = {};
                        $rootScope.canVote=true;
                        $rootScope.votesEmitted = {};
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
                        $rootScope.isReady = {};
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
                        $rootScope.votesEmitted[packet.userId] = packet.voteId;
                        $rootScope.isReady[packet.userId] = packet.voteId!=null;
                        $rootScope.$apply();
                        break;

                    case 'UserReady':
                        $rootScope.isReady[packet.userId] = packet.ready;
                        $rootScope.$apply();
                        Console.log('User '+ $rootScope.players[packet.userId].name + ' is ready');
                        break;

                    case 'RegisterUser':
                        $rootScope.players[packet.pid] = packet;
                        $rootScope.$apply();
                        Console.log('New user: ' + packet.name + ' ( ' + packet.pid + ')');
                        break;

                    case 'RemoveUser':
                        Console.log('User ' + $rootScope.players[packet.userId] + ' leaves');
                        delete $rootScope.players[packet.userId];
                        delete $rootScope.isReady[packet.userId];
                        delete $rootScope.isResponsed[packet.userId];
                        $rootScope.$apply();
                        break;

                    case 'UsersList':
                        $rootScope.players = {};
                        var users = packet.users;
                        for(var i in  users){
                            $rootScope.players[users[i].pid]= users[i];
                        }
                        $rootScope.$apply();
                        Console.logUserList(packet.users);
                        break;

                    case 'Start':
                        Console.log("Iniciating...");

                    case 'Stop':
                        $location.path('/stopped');
                        $rootScope.mytimeout.cancel();
                        break;
                    default:
                        Console.log('Unkonwn message: ' + packet.type);
                }
            }
        };
  });
