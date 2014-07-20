'use strict';

/**
 * @ngdoc overview
 * @name defineMatchClientApp
 * @description
 * # defineMatchClientApp
 *
 * Main module of the application.
 */
angular
    .module('defineMatchClientApp', [
        'ngAnimate',
        'ngCookies',
        'ngResource',
        'ngRoute',
        'ngSanitize',
        'ngTouch'
    ])
    .constant('authUrl', 'http://10.1.1.102:8080/login')
    .constant('logoutUrl', 'http://10.1.1.102:8080/logout')
    .constant('wsBroker', 'http://10.1.1.102:8080/crossgate')
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.withCredentials = true;
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                redirectTo: '/login'
            })
            .when('/about', {
                templateUrl: 'views/about.html',
                controller: 'AboutCtrl'
            })
            .when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginCtrl'
            })
            .when('/define', {
                templateUrl: 'views/define.html',
                controller: 'DefineCtrl'
            })
            .when('/vote', {
                templateUrl: 'views/vote.html',
                controller: 'VoteCtrl'
            })
            .when('/result', {
              templateUrl: 'views/result.html',
              controller: 'ResultCtrl'
            })
            .when('/score', {
              templateUrl: 'views/score.html',
              controller: 'ScoreCtrl'
            })
            .when('/start', {
              templateUrl: 'views/start.html',
              controller: 'StartCtrl'
            })
            .when('/final', {
              templateUrl: 'views/final.html',
              controller: 'FinalCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    })
    .run(function ($rootScope, Console, MatchServer) {
        //$rootScope.username = null;
        $rootScope.user = {};
        $rootScope.user.name = null;
        $rootScope.Console = Console;
        $rootScope.logout = MatchServer.logout;
    });
