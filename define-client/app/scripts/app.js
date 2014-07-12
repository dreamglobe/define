/**
 * @ngdoc overview
 * @name deffineApp
 * @description
 * # deffineApp
 *
 * Main module of the application.
 */
angular
    .module('deffineApp', [
        'ngAnimate',
        'ngCookies',
        'ngResource',
        'ngRoute',
        'ngSanitize',
        'ngTouch'
    ])
    .config(function ($logProvider){
        $logProvider.debugEnabled = true;
    })
    .config(function ($routeProvider) {
        'use strict';
        $routeProvider
            .when('/', {
                redirectTo: 'login'
            })
            .when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginCtrl'
            })
            .when('/answer', {
                templateUrl: 'views/answer.html',
                controller: 'AnswerCtrl'
            })
            .when('/vote', {
                templateUrl: 'views/vote.html',
                controller: 'VoteCtrl'
            })
            .when('/result', {
              templateUrl: 'views/result.html',
              controller: 'ResultCtrl'
            })
            .when('/stopped', {
              templateUrl: 'views/stopped.html',
              controller: 'StoppedCtrl'
            })
            .otherwise({
                redirectTo: '/'
            })
            .when('/angular1', {
                templateUrl: 'views/angular1.html'
            })
            .when('/angular2', {
                templateUrl: 'views/angular2.html',
                controller: 'CartController'
            })
            ;
    })
    .config(['$httpProvider', function ($httpProvider) {
        'use strict';
        $httpProvider.defaults.withCredentials = true;
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }])
    .run(function($rootScope, $location, Console, $timeout){
        $rootScope.reset = function($rootScope, $location, Console) {
            $rootScope.socket = null;
            $rootScope.user = {};
            $rootScope.user.name = null;
            $rootScope.user.pid = null;
            $rootScope.players = {};
            $rootScope.definitions = {};
            $rootScope.playerDefinition = {};
            $rootScope.isReady = {};
            $rootScope.isResponsed = {};
            $rootScope.votesEmitted = {};
            $rootScope.result = {};
            $rootScope.canVote = true;
            $rootScope.checkLogin = function () {
                if (!$rootScope.socket) {
                    $location.path('/');
                }
            };
            $rootScope.sendDefinition = function (response) {
                Console.log('sendDefinition :: Not connected');
            };
            $rootScope.sendReady = function (ready) {
                Console.log('sendReady :: Not connected');
            };
            $rootScope.sendVote = function (ready) {
                Console.log('sendReady :: Not connected');
            };

            $rootScope.counter = 60;
            $rootScope.mytimeout = null;


            $rootScope.onTimeout = function(){
                $rootScope.counter--;
                $rootScope.$apply();
                $rootScope.mytimeout = $timeout($rootScope.onTimeout,1000);
            };
            $rootScope.resetTimeout = function(timeout){
                if($rootScope.mytimeout!=null){
                    $timeout.cancel($rootScope.mytimeout);
                }
                $rootScope.counter=Math.floor(timeout/1000);
                var frecuency = timeout%1000
                $rootScope.mytimeout = $timeout($rootScope.onTimeout,frecuency==0?1000:frecuency);
            };
        };
        $rootScope.updateResults = function (){
            $rootScope.correctDefId = null;
            $rootScope.correctNumVotes = 0;
            for( var i in $rootScope.result){
                var score = $rootScope.result[i];
                var turnPoints =  score.turnPoints;
                $rootScope.players[score.pid].totalPoints += turnPoints;
                $rootScope.players[score.pid].lastPhasePoints = turnPoints;
            }
        };
        $rootScope.reset($rootScope, $location, Console);
        $rootScope.Console = Console;
        $rootScope.timeout = $timeout;
    });

     