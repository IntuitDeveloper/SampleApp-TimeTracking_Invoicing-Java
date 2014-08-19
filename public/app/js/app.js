'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', [
    'ngRoute',
    'ui.bootstrap',
    'ui.validate',
    'myApp.filters',
    'myApp.services',
    'myApp.directives',
    'myApp.controllers'
]).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/preferences', {templateUrl: 'partials/preferences.html', controller: 'PreferencesCtrl'});
        $routeProvider.otherwise({redirectTo: '/preferences'});
    }])
    .run(['InitializerSvc', function(InitializerSvc) {
        InitializerSvc.initialize();
    }]);

