'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.WSClient
 * @description
 * # WSClient
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .service('MatchServer', function MatchServer($rootScope, $http, $location, MessageHandler, authUrl, wsBroker, logoutUrl) {
        var connector;
        var sessionId;
        var errorMessage;

        function registerWs(response) {
            if (response !== 'KO') {
                sessionId = response;
                connector = new SockJS(wsBroker, undefined,
                    {cookie: true, protocols_whitelist: ['websocket']
                        //, jsessionid: true,    protocols_whitelist: ['websocket', 'xdr-streaming', 'xhr-streaming', 'iframe-eventsource', 'xdr-polling', 'xhr-polling']
                    });
                connector.onopen = MessageHandler.onConnect;
                connector.onmessage = MessageHandler.onMessage;
                connector.onclose = MessageHandler.onClose;
                return true;
            }
        }

        function errorLogin(response) {
            errorMessage = response;
            return false;
        }

        function errorLogout(response) {
            errorMessage = response;
            return false;
        }

        return {
            sendDefinition: function (response) {
                connector.send('{"type":"ClientResponse", "response":"' + response + '"}"');
            },
            sendReady: function (ready) {
                connector.send('{"type":"ClientReady", "ready":' + ready + '}"');
            },
            sendVote: function (voteId) {
                connector.send('{"type":"ClientVote", "voteId":' + voteId + '}"');
            },
            sendStartMatch: function (config){
                var messageJSON = JSON && JSON.stringify({type:"ClientStartMatch", config : config },
                function(key, value){
                    if(value==null) return null;
                    return value;
                });
                connector.send(messageJSON);
            },
            login: function (username, password) {
                errorMessage = '';
                $http.post(authUrl, username, password)
                    .success(registerWs)
                    .error(errorLogin);
            },
            logout: function (code, reason) {
                errorMessage = '';
                if(MessageHandler.isConnected()) {
                    connector.close(code, reason);
                }
                if(sessionId){
                    sessionId = null;
                    $http.get(logoutUrl)
                        .error(errorLogout);
                }
                $rootScope.user = {};
            },
            getErrorMessage: function () {
                return errorMessage;
            },
            getSessionId: function(){
                return sessionId;
            },
            checkConnection: function(){
                if(!MessageHandler.isConnected()){
                    $location.path('/');
                }
            }
        };
    })
;
