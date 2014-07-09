'use strict';

/**
 * @ngdoc directive
 * @name deffineApp.directive:ngbkFocus
 * @description
 * # ngbkFocus
 */
angular.module('deffineApp')
  .directive('ngbkFocus', function() {
    return {
        link: function(scope, element) {
            element[0].focus();
        }
    };
});