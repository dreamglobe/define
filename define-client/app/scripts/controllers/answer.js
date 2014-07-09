'use strict';

/**
 * @ngdoc function
 * @name deffineApp.controller:AnswerCtrl
 * @description
 * # AnswerCtrl
 * Controller of the deffineApp
 */
angular.module('deffineApp')
    .controller('AnswerCtrl', function ($scope, $rootScope, $timeout) {
        $rootScope.checkLogin();
    });
