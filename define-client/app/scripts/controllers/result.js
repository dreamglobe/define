'use strict';

/**
 * @ngdoc function
 * @name deffineApp.controller:ResultCtrl
 * @description
 * # ResultCtrl
 * Controller of the deffineApp
 */
angular.module('deffineApp')
  .controller('ResultCtrl', function ($scope) {
    $scope.checkLogin();
    $scope.ready = {state:false, label:'Listo'};
    $scope.toggleReady = function (){
        $scope.ready.state = !$scope.ready.state;
        $scope.sendReady($scope.ready.state);
        $scope.ready.label = $scope.ready.state?
            'Esperar':'Listo';
    };
    $scope.getDefinition = function(key){
        if(key == $scope.playerDefinition.defId){
            return $scope.playerDefinition.definition;
        }

        for(var i in $scope.definitions){
            if($scope.definitions[i].defId == key){
                return $scope.definitions[i].definition;
            }
        }
        return null;
    };
  });
