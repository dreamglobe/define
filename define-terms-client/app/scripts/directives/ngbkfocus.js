'use strict';

/**
 * @ngdoc directive
 * @name defineTermsClientApp.directive:ngbkFocus
 * @description
 * # ngbkFocus
 */
angular.module('defineTermsClientApp')
    .directive('ngbkFocus', [ '$timeout', function ($timeout) {
        return {
            scope: { trigger: '@ngbkFocus' },
            link: function(scope, element) {
                scope.$watch('trigger', function(value) {
                    if(value === "true") {
                        $timeout(function() {
                            element[0].focus();
                        }, 100);
                    }
                });
            }
        };
    }]);
