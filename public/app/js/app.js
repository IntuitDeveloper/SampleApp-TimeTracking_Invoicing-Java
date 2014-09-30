'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', [
    'ngRoute',
    'ui.bootstrap',
    'ui.validate',
    'myApp.services',
    'myApp.controllers',
    'myApp.directives',
    'angularSpinner',
]).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/settings', {templateUrl: 'partials/settings.html', controller: 'SettingsCtrl'});
        $routeProvider.when('/timeentry', {templateUrl: 'partials/timeentry.html', controller: 'TimeEntryCtrl'});
        $routeProvider.when('/invoices', {templateUrl: 'partials/invoices.html', controller: 'InvoiceCtrl'});
        $routeProvider.otherwise({redirectTo: '/settings'});
    }])
    .run(['InitializerSvc', function (InitializerSvc) {
        InitializerSvc.initialize();
    }]);



