'use strict';

/**
 * @ngdoc function
 * @name defineTermsClientApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the defineTermsClientApp
 */
angular.module('defineTermsClientApp')
    .controller('MainCtrl', [ '$scope', 'CardService', 'TermService', '$location', '$alert',
        function ($scope, CardService, TermService, $location, $alert) {
            var loadTerms = function (data) {
                $scope.terms = data._embedded.terms;
                $scope.page = data.page;
            };
            var loadCardOrder = function (data) {
                return CardService.get(data.order);
            };
            var loadCard = function (data) {
                $scope.card = data;
            };
            var alertError = function (error) {
                $alert({title: 'Error loading Terms', content: error.message, placement: 'top', type: 'alert', show: true,
                    duration: '3', animation: 'am-fade-and-slide-top', container: 'body', keyboard: 'true'});
            };
            var search = $location.search();
            var page = {num: search.page || 0, size: search.size || 16, sort: search.sort || 'name,desc'};
            TermService.getList(page).then(loadTerms).catch(alertError);

            $scope.loadCard = function (term) {
                $scope.card = TermService.getCard(term)
                    .then(loadCardOrder)
                    .then(loadCard)
                    .catch(alertError);
            };
            $scope.prepareNewCard = function () {
                $scope.card = {definitions: {'AB': '', 'CH': '', 'NP': '', 'SG': ''}};
            };
        }]);
