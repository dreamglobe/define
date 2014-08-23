'use strict';

/**
 * @ngdoc overview
 * @name defineTermsClientApp
 * @description
 * # defineTermsClientApp
 *
 * Main module of the application.
 */
angular
    .module('defineTermsClientApp', [
        'ngAnimate',
        'ngCookies',
        'ngResource',
        'ngRoute',
        'ngSanitize',
        'ngTouch',
        'mgcrea.ngStrap.tab',
        'mgcrea.ngStrap.modal',
        'mgcrea.ngStrap.alert'
    ])
    .constant('server','http://localhost:8080/')
    .constant('importPath','db/init/')
    .constant('exportPath','db/export/')
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.withCredentials = true;
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/main.html',
                controller: 'MainCtrl'
            })
            .when('/db', {
                templateUrl: 'views/db.html',
                controller: 'DbCtrl'
            })
            .when('/about', {
                templateUrl: 'views/about.html',
                controller: 'AboutCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    })
    .run(['$rootScope', 'server', 'importPath',  'exportPath', function ($rootScope, server, importPath, exportPath) {
        $rootScope.server = server;
        $rootScope.importPath = importPath;
        $rootScope.exportPath = exportPath;
    }]);
