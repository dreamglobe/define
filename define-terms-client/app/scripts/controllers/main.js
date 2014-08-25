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
                return data;
            };
            var loadCardOrder = function (data) {
                return CardService.get(data.order);
            };
            var loadCard = function (data) {
                $scope.card = data;
                return data;
            };
            var updateNew = function (data) {
                $scope.card.order = data.order;
                return data;
            };
            var alertNew = function (card) {
                $alert({title: 'Tarjeta creada #'+card.order, content: '', placement: 'top', type: 'success', show: true,
                    duration: '3', animation: 'am-fade-and-slide-top', container: 'body', keyboard: 'true'});
                return card;
            };
            var alertUpdate = function (card) {
                $alert({title: 'Tarjeta actualizada #'+card.order, content: '', placement: 'top', type: 'success', show: true,
                    duration: '3', animation: 'am-fade-and-slide-top', container: 'body', keyboard: 'true'});
                return card;
            };
            var alertError = function (error) {
                $alert({title: 'Error loading Terms', content: error.message, placement: 'top', type: 'danger', show: true,
                    duration: '6', animation: 'am-fade-and-slide-top', container: 'body', keyboard: 'true'});
                return error;
            };

            var getPage = function (){
                var search = $location.search();
                return {num: search.page || 0, size: search.size || 16, sort: search.sort || 'name,desc'};
            }
            $scope.terms =[];
            $scope.page = getPage();
            TermService.getList($scope.page).then(loadTerms).catch(alertError);

            $scope.loadCard = function (term) {
                $scope.card = TermService.getCard(term)
                    .then(loadCardOrder)
                    .then(loadCard)
                    .catch(alertError);
                $scope.saveActionLabel = 'Actualizar';
            };
            $scope.prepareNewCard = function () {
                $scope.card = {definitions: {'AB': {}, 'CH': {}, 'NP': {}, 'SG': {}}};
                $scope.saveActionLabel = 'Crear';
            };
            $scope.saveCard = function () {
                var card = $scope.card;
                if(card.order && card.order >0){
                    CardService.update(card).then(alertUpdate).catch(alertError);
                }else {
                    card.definitions['AB'].category='AB';
                    card.definitions['CH'].category='CH';
                    card.definitions['NP'].category='NP';
                    card.definitions['SG'].category='SG';
                    CardService.create(card).then(updateNew).then(alertNew).catch(alertError);
                }
                TermService.getList(getPage()).then(loadTerms).catch(alertError);
            };
        }]);
