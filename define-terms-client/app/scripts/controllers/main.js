'use strict';

/**
 * @ngdoc function
 * @name defineTermsClientApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the defineTermsClientApp
 */
angular.module('defineTermsClientApp')
    .controller('MainCtrl', [ '$scope', 'CardService', 'TermService', '$location', '$alert', '$q',
        function ($scope, CardService, TermService, $location, $alert, $q) {
            var loadTerms = function (data) {
                $scope.terms = data._embedded.terms;
                $scope.page = data.page;
                return data;
            };
            var loadCardOrder = function (data) {
                return CardService.get(data.order);
            };
            var loadCard = function (data) {
                var cats = ['NP', 'CH', 'SG', 'AB'];
                for (var index in  cats) {
                    var catName = cats[index];
                    if (!data.definitions[catName]) {
                        data.definitions[ catName] = {category:catName};
                    }
                }
                $scope.card = data;
                return data;
            };
            var updateNew = function (data) {
                //$scope.card =  data;
                $scope.prepareNewCard();
                return data;
            };
            var alertNew = function (card) {
                $alert({title: 'Tarjeta creada #'+card.order, content: '', placement: 'top-right', type: 'success', show: true,
                    duration: '6', animation: 'am-fade-and-slide-top', container: 'body', keyboard: 'true'});
                return card;
            };
            var alertUpdate = function (card) {
                $alert({title: 'Tarjeta actualizada #'+card.order, content: '', placement: 'top-right', type: 'success', show: true,
                    duration: '6', animation: 'am-fade-and-slide-top', container: 'body', keyboard: 'true'});
                return card;
            };
            var alertError = function (error) {
                $alert({title: 'Error loading Terms', content: error.message, placement: 'top-right', type: 'danger', show: true,
                    duration: '6', animation: 'am-fade-and-slide-top', container: 'body', keyboard: 'true'});
                return error;
            };

            var getPage = function (){
                var search = $location.search();
                return {num: search.page || 0, size: search.size || 16, sort: search.sort || 'name,desc'};
            };
            $scope.focusMe = false;
            $scope.terms =[];
            $scope.page = getPage();
            TermService.getList($scope.page).then(loadTerms).catch(alertError);

            $scope.searchByName = function(name){
                TermService.searchByName(name).then(loadTerms).catch(alertError);
            };

            $scope.loadCard = function (term) {
                $scope.card = TermService.getCard(term)
                    .then(loadCardOrder)
                    .then(loadCard)
                    .catch(alertError);
                $scope.saveActionLabel = 'Actualizar';
            };
            $scope.prepareNewCard = function () {
                $scope.focusMe = false;
                $scope.card = {definitions: {'AB': {}, 'CH': {}, 'NP': {}, 'SG': {}}};
                $scope.saveActionLabel = 'Crear';
            };
            $scope.saveCard = function () {
                $scope.focusMe = false;
                var card = $scope.card;
                var result;
                if(card.order && card.order >0){
                    result = CardService.update(card).then(alertUpdate).catch(alertError);
                }else {
                    card.definitions['AB'].category='AB';
                    card.definitions['CH'].category='CH';
                    card.definitions['NP'].category='NP';
                    card.definitions['SG'].category='SG';
                    result = CardService.create(card)
                        .then(updateNew)
                        .then(alertNew)
                        .catch(alertError);
                }
                $q.when(result).then(function(){
                    return TermService.getList(getPage()).then(loadTerms).catch(alertError);
                });
            };
        }]);
