'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:LoginCtrl
 * @description
 * # LoginCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
    .controller('LoginCtrl', ['$scope','MatchServer', function ($scope, MatchServer) {

        $scope.doLogin = function(){
            MatchServer.login($scope.user.name);
            $scope.message = MatchServer.getErrorMessage();
        };
    }]);
