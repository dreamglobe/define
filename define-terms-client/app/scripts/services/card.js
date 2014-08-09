'use strict';

/**
 * @ngdoc service
 * @name defineTermsClientApp.card
 * @description
 * # card
 * Factory in the defineTermsClientApp.
 */
angular.module('defineTermsClientApp')
  .factory('CardCrud', ['$resource', function ($resource) {
    return $resource("/cards/:order",
        {order:"@order"},
        {
            "update":{method:"PUT"}
        });
  }]);
