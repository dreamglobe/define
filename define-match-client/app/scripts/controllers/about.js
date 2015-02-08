'use strict';

/**
 * @ngdoc function
 * @name defineMatchClientApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the defineMatchClientApp
 */
angular.module('defineMatchClientApp')
  .controller('AboutCtrl', ['$scope', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  }]);
