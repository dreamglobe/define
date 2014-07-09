'use strict';


/**
 * @ngdoc function
 * @name deffineApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the deffineApp
 */
angular.module('deffineApp').controller('AngularControllerTest', function () {
    })
    .factory('Items', function(){
        var items = {};
        items.query = function() {
            return [
                {title:'Paint potes', description: 'Pots full of paint', price: 3.95},
                {title: 'Polka puntos', description: 'Dots with polka', price: 2.95},
                {title: 'Pebbles', description: 'Just little rocks', price: 6.95}
            ];
        };
        return items;
    })
    .controller('CartController', function ($scope) {
        $scope.bill = {};
        $scope.items = [
            {title: 'Paint pots', quantity: 8, price: 3.95},
            {title: 'Polka dots', quantity: 17, price: 12.95},
            {title: 'Pebbles', quantity: 5, price: 6.95}
        ];
        var calculateTotal = function () {
            var total = 0;
            for (var i = 0, len = $scope.items.length; i < len; i++) {
                total = total + $scope.items[i].price * $scope.items[i].quantity;
            }
            $scope.bill.total = total;
            $scope.bill.discount = total > 100 ? 10 : 0;
            $scope.bill.subtotal = total - $scope.bill.discount;
        };
        $scope.$watch('items', calculateTotal, true);
    })
    .controller('ShoppingController', function($scope, Items) {
        $scope.items = Items.query();
    })
    .filter('titleCase', function() {
        var titleCaseFilter = function(input){
            var words = input.split(' ');
            for(var i=0; i<words.length; i++){
                words[i]=words[i].charAt(0).toUpperCase() + words[i].slice(1);
            }
            return words.join(' ');
        };
        return titleCaseFilter;
    });

