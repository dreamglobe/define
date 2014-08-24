'use strict';

/**
 * @ngdoc service
 * @name defineTermsClientApp.card
 * @description
 * # card
 * Factory in the defineTermsClientApp.
 */
angular.module('defineTermsClientApp')
    .factory('CardMesh', ['$rootScope', '$resource', function ($rootScope, $resource) {
        return $resource($rootScope.server + "/card/:id",
            {id:'@order'},
            {update:'PUT'});
    }])
    .factory('CardResource', [ '$rootScope', '$resource', function ($rootScope, $resource) {
        return $resource($rootScope.server + 'cards/:id');
    }])
    .service('CardService', [ 'CardMesh', 'CardResource', function (CardMesh, CardResource) {
        this.getResource = function (id) {
            return CardResource.get({id: id}).$promise;
        };
        this.getList = function (page) {
            if (page == undefined) {
                return CardResource.get().$promise;
            } else {
                return CardResource.get({
                        size: page.limit,
                        page: page.num
                    }
                ).$promise;
            }
        };
        this.create = function(newCard){
            return CardMesh.put(newCard).$promise;
        };
        this.update = function(updated){
            return CardMesh.update(newCard).$promise;
        };
    }]);