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
      .otherwise({
        redirectTo: '/'
      });
  })
    .run(function($rootScope, Console){
        //$rootScope.username = null;
        $rootScope.user = {};
        $rootScope.user.name = null;
        $rootScope.Console = Console;
    });
