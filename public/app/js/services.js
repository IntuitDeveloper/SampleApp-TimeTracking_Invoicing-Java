'use strict';

/* Services */


var timetrackingServices = angular.module('myApp.services', ['ngResource']);

timetrackingServices.value('version', '0.1');

timetrackingServices.factory('InitializerSvc', ['$rootScope', 'RootUrlSvc', 'CompanySvc', 'CustomerSvc', 'ServiceItemSvc',
    function ($rootScope, RootUrlSvc, CompanySvc, CustomerSvc, ServiceItemSvc) {

        var initialized = false;

        var initialize = function () {

            $rootScope.$on('api.loaded', function () {
                CompanySvc.initialize();
                CustomerSvc.initialize();
                ServiceItemSvc.initialize();

                CompanySvc.initializeModel();
            });

            $rootScope.$on('model.company.change', function () {
                ServiceItemSvc.initializeModel();
                CustomerSvc.initializeModel();
            });

            RootUrlSvc.initialize();

            $rootScope.$on('$viewContentLoaded', function (scope, next, current) {
                /*
                 Every time we load a new view, we need to reinitialize the intuit anywhere library
                 so that the connect to quickbooks button is rendered properly
                 */
                if (initialized) { //only reinitialize from the 2nd time onwards
                    intuit.ipp.anywhere.init();
                }
                initialized = true;
            });
        };

        return {
            initialize: initialize
        }
    }]);

//A service which contains the current model (e.g. companies, items, etc)
timetrackingServices.factory('ModelSvc', ['$rootScope',
    function ($rootScope) {

        var model = {};
        model.company = {};

        var broadcastCompanyChange = function () {
            $rootScope.$broadcast('model.company.change');
        };

        var onCompanyChange = function ($scope, callback) {
            $scope.$on('model.company.change', function () {
                callback(model);
            });
        };

        return {
            model: model,
            onCompanyChange: onCompanyChange,
            broadcastCompanyChange: broadcastCompanyChange
        }
    }]);

//a service which reads the root of the API and stores all the resource urls
timetrackingServices.factory('RootUrlSvc', ['$resource', '$rootScope', '$location',
    function ($resource, $rootScope, $location) {

        var rootUrls = {};
        var apiRoot = function () {
            return $location.protocol() + "://" + $location.host() + ":8080";
        };

        var initialize = function () {
            $resource(apiRoot()).get(function (data) {
                var links = data._links;
                for (var link in  links) {
                    var href = links[link].href;
//                    console.log("Discovered the URL for " + link + ": " + href);
                    rootUrls[link] = href.split(/\{/)[0]; //chop off the template stuff
                }
                rootUrls['syncRequest'] = apiRoot() + "/syncrequest";  // non-discoverable
                $rootScope.$broadcast('api.loaded');  //broadcast an event so that the CompanySvc can know to load the companies
            });
        };

        var oauthGrantUrl = function () {
            return apiRoot() + "/request_token";
        }

        var onApiLoaded = function ($scope, callback) {
            $scope.$on('api.loaded', function () {
                callback();
            });
        };

        return {
            initialize: initialize,
            rootUrls: rootUrls,
            onApiLoaded: onApiLoaded,
            oauthGrantUrl: oauthGrantUrl
        }
    }]);

//A service which deals with CRUD operations for companies
timetrackingServices.factory('CompanySvc', ['$resource', '$rootScope', 'RootUrlSvc', 'ModelSvc',
    function ($resource, $rootScope, RootUrlSvc, ModelSvc) {

        var Company;

        var initialize = function () {
            Company = $resource(RootUrlSvc.rootUrls.companies + ':companyId', {}, { query: {method: 'GET', isArray: false} });
        };

        var initializeModel = function () {
            Company.query(function (data) {
                var companies = data._embedded.companies;
                ModelSvc.model.companies = companies;
                ModelSvc.model.company = companies[0]; //select the first company for now
                ModelSvc.broadcastCompanyChange();

                var grantUrl = RootUrlSvc.oauthGrantUrl() + '?appCompanyId=' + ModelSvc.model.company.id;
                intuit.ipp.anywhere.setup({
                    grantUrl: grantUrl});
            });
        };

        return {
            initialize: initialize,
            initializeModel: initializeModel
        }

    }]);


timetrackingServices.factory('ServiceItemSvc', ['$resource', '$rootScope', 'RootUrlSvc', 'ModelSvc',
    function ($resource, $rootScope, RootUrlSvc, ModelSvc) {

        var ServiceItem;

        var initialize = function () {
            ServiceItem = $resource(RootUrlSvc.rootUrls.serviceItems, {}, { query: {method: 'GET', isArray: false} });
        };

        var initializeModel = function () {
            ServiceItem.query(function (data) {
                var serviceItems = data._embedded.serviceItems;
                ModelSvc.model.company.serviceItems = serviceItems;
            });
        }

        return {
            initialize: initialize,
            initializeModel: initializeModel
        }
    }]);


timetrackingServices.factory('CustomerSvc', ['$resource', '$rootScope', 'RootUrlSvc', 'ModelSvc',
    function ($resource, $rootScope, RootUrlSvc, ModelSvc) {

        var Customer;

        var initialize = function () {
            Customer = $resource(RootUrlSvc.rootUrls.customers, {}, { query: {method: 'GET', isArray: false} });
        };

        var initializeModel = function () {
            Customer.query(function (data) {
                var customers = data._embedded.customers;
                ModelSvc.model.company.customers = customers;

            });
        }

        return {
            initialize: initialize,
            initializeModel: initializeModel
        }
    }]);


timetrackingServices.factory('SyncRequestSvc', ['$http', '$rootScope', 'RootUrlSvc', 'ModelSvc',
    function ($http, $rootScope, RootUrlSvc, ModelSvc) {

        var sendSyncRequest = function (entityType, successCallback, errorCallback) {
            $http.post(RootUrlSvc.rootUrls.syncRequest, {type: entityType, companyId: ModelSvc.model.company.id})
                .success(successCallback);
        };

        var initialize = function () {

        };

        return {
            initialize: initialize,
            sendCustomerSyncRequest: function (callback) {
                sendSyncRequest('Customer', callback);
            },
            sendServiceItemsSyncRequest: function (callback) {
                sendSyncRequest('ServiceItem', callback)
            },
            sendEmployeeSyncRequest: function (callback) {
                sendSyncRequest('Employee', callback)
            }
        }
    }]);
