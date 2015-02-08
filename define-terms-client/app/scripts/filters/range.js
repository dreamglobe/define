'use strict';

/**
 * @ngdoc filter
 * @name defineTermsClientApp.filter:range
 * @function
 * @description
 * # range
 * Filter in the defineTermsClientApp.
 */
angular.module('defineTermsClientApp')
    .filter('range', function () {
        return function (input, start, end) {
            start = parseInt(start);
            end = parseInt(end);
            for (var i = start; i < end; i++)
                input.push(i);
            return input;
        };
    });
