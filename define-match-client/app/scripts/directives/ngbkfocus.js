'use strict';

/**
 * @ngdoc directive
 * @name defineMatchClientApp.directive:ngbkFocus
 * @description
 * # ngbkFocus
 */
angular.module('defineMatchClientApp')
    .directive('ngbkFocus', function () {
        return {
            link: function (scope, element) {
                element[0].focus();
            }
        };
    });
