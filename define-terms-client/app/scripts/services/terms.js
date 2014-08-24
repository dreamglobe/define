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
  .service('TermService', function (TermResource) {
        this.getResource = function (id) {
            return TermResource.get({id: id}).$promise;
        };
        this.getList = function (page){
            if(page == undefined){
                return TermResource.get().$promise;
            }else{
                return TermResource.get({
                        size: page.size,
                        page: page.num
                    }
                ).$promise;
            }
        };
    });
