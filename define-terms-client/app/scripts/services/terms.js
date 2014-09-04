'use strict';

/**
 * @ngdoc service
 * @name defineTermsClientApp.terms
 * @description
 * # terms
 * Service in the defineTermsClientApp.
 */
angular.module('defineTermsClientApp')
    .factory('TermResource', [ '$rootScope', '$resource', function ($rootScope, $resource) {
        return $resource($rootScope.server + 'terms/:id');
    }])
    .factory('TermSearchResource', [ '$rootScope', '$resource', function ($rootScope, $resource) {
        return $resource($rootScope.server + 'terms/search/findByNameLike');
    }])
    .factory('TermCardResource', [ '$rootScope', '$resource', function ($rootScope, $resource) {
        return $resource($rootScope.server + 'terms/:id/card',{id:'@name'});
    }])
    .service('TermService',[ 'TermResource', 'TermCardResource', 'TermSearchResource',
        function (TermResource, TermCardResource, TermSearchResource) {
        this.getResource = function (id) {
            return TermResource.get({id: id}).$promise;
        };
        this.getList = function (page) {
            if (page == undefined) {
                return TermResource.get().$promise;
            } else {
                return TermResource.get({
                        size: page.size,
                        page: page.num
                    }
                ).$promise;
            }
        };
        this.getCard = function (term) {
            return TermCardResource.get({id:term.name}).$promise;
        };
        this.searchByName = function(query){
            return TermSearchResource.get({
                name: query,
            }).$promise;
        }
    }]);
