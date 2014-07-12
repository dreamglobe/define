'use strict';

/**
 * @ngdoc function
 * @name deffineApp.controller:LoginCtrl
 * @description
 * # LoginCtrl
 * Controller of the deffineApp
 */
angular.module('deffineApp')
    .constant('authUrl', 'http://10.1.1.102:8080/login')
    .constant('wsBroker', 'http://10.1.1.102:8080/crossgate')
    .controller('LoginCtrl', function ($scope, $http, $location, $rootScope, authUrl, wsBroker, Messagehandler, Console, $timeout) {
        $scope.message = '';
        $scope.logUser = function () {
            //$scope.message = 'Thanks, ' + $scope.user.name + ', we are implementing login';
            $scope.message = '';
            $http.post(authUrl, $scope.user.name).success(function (sessionId) {
                if (sessionId === 'KO') {
                    $scope.message = 'Could not log in. Try another name';
                } else {
                    $rootScope.SessionId = sessionId;
                    $rootScope.user = $scope.user;
                    $rootScope.players={};
                    Console.log('Registering user');
                    var socket = new SockJS(wsBroker, undefined,
                                    {debug:true,
                                    cookie:true});
                    socket.onopen = Messagehandler.onConnect;
                    socket.onmessage = Messagehandler.onMessage;
                    socket.onclose = Messagehandler.onClose;
                    $rootScope.socket = socket;
                    $rootScope.sendDefinition = function (response) {
                        $rootScope.socket.send('{"type":"ClientResponse", "response":"'+ response + '"}"');
                    };
                    $rootScope.sendReady = function (ready) {
                        $rootScope.socket.send('{"type":"ClientReady", "ready":' + ready + '}"');
                    };
                    $rootScope.sendVote = function (voteId) {
                        if($rootScope.canVote){
                            $rootScope.socket.send('{"type":"ClientVote", "voteId":' + voteId + '}"');
                            $rootScope.canVote = false;
                            $timeout(function(){
                                $rootScope.canVote = true;
                            }, 5000);
                        }
                    };
                }
            }).error(function (e) {
                $scope.message = 'Error in login: ' + e;
            });
        };

    })

;
