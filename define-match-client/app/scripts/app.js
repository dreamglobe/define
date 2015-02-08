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
        'ngTouch',
        'ui.bootstrap'
    ])
    .constant('authUrl', './login/')
    .constant('logoutUrl', './logout/')
    .constant('wsBroker', '/define/crossgate/')
    .constant('debug', false)
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.withCredentials = true;
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }])
    .config([ '$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
                redirectTo: '/login'
            })
            .when('/about', {
                templateUrl: 'views/about.html',
                controller: 'AboutCtrl'
            })
            .when('/login', {
                title: 'Acceso',
                templateUrl: 'views/login.html',
                controller: 'LoginCtrl'
            })
            .when('/define', {
                title: 'Definición',
                templateUrl: 'views/match-define.html',
                controller: 'DefineCtrl'
            })
            .when('/vote', {
                title: 'Votación',
                templateUrl: 'views/match-vote.html',
                controller: 'VoteCtrl'
            })
            .when('/score', {
                title: 'Puntuación',
                templateUrl: 'views/match-score.html',
                controller: 'ScoreCtrl'
            })
            .when('/start', {
                title: 'Inicio',
                templateUrl: 'views/match-start.html',
                controller: 'StartCtrl'
            })
            .when('/final', {
                title: 'Resultado',
                templateUrl: 'views/match-final.html',
                controller: 'ScoreCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
        $locationProvider.html5Mode(true);
    }])
    .run(['$rootScope', 'Console', 'MatchServer', '$route', '$location', function ($rootScope, Console, MatchServer, $route, $location) {
        if (typeof String.prototype.startsWith != 'function') {
            String.prototype.startsWith = function (str){
                return this.slice(0, str.length) == str;
            };
        }
        $rootScope.user = {};
        $rootScope.user.name = null;
        $rootScope.Console = Console;
        $rootScope.logout = MatchServer.logout;
        $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
            $rootScope.viewName = current.title;
        });
    }]);
