'use strict';

/**
 * @ngdoc function
 * @name deffineApp.controller:VoteCtrl
 * @description
 * # VoteCtrl
 * Controller of the deffineApp
 */
angular.module('deffineApp')
  .controller('VoteCtrl', function ($scope) {
        $scope.checkLogin();
        $scope.$watch($scope.votesEmitted)
        $scope.nextLetter = function (index) {
            return String.fromCharCode(65+index);
        };
        $scope.letterSelected = function (userId) {
            var i = 0;
            var responseId = $scope.votesEmitted[userId];
            for(var item in $scope.definitions){
                if($scope.definitions[item].defId == responseId){
                    return this.nextLetter(i);
                }
                i=i+1;
            }
            return this.nextLetter(i);
        };
        $scope.hasVoteForMe = function(code){
            return $scope.playerDefinition.defId == code;
        };
  });
