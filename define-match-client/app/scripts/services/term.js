'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.Term
 * @description
 * # Term
 * Factory in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .factory('Term', function () {

        var actualTerm = null;

        function Term(name, category){
            this.name = name;
            this.category = category;
        }

        // Public API here
        return {
            get: function () {
                return actualTerm;
            },
            set: function (data){
                actualTerm = new Term(
                    data.name,
                    data.category
                );
            }
        };
    });
