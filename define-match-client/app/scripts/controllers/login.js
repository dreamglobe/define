'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:LoginCtrl
 * @description
 * # LoginCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('LoginCtrl', function ($scope, $rootScope, MatchServer) {

        $scope.doLogin = function(){
            MatchServer.login($rootScope.user.name)
        };

        $scope.message = MatchServer.getErrorMessage();

    });
