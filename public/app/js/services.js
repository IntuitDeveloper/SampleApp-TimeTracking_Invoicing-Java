'use strict';

/* Services */


var timetrackingServices = angular.module('myApp.services', ['ngResource']);

timetrackingServices.value('version', '0.1');

timetrackingServices.factory('InitializerSvc',
    ['$rootScope', 'RootUrlSvc', 'CompanySvc', 'CustomerSvc', 'ServiceItemSvc', 'EmployeeSvc', 'TimeActivitySvc',
        function ($rootScope, RootUrlSvc, CompanySvc, CustomerSvc, ServiceItemSvc, EmployeeSvc, TimeActivitySvc) {

            var initialized = false;

            var initialize = function () {

                $rootScope.$on('api.loaded', function () {
                    CompanySvc.initialize();
                    TimeActivitySvc.initialize();
                    CompanySvc.initializeModel();
                });

                $rootScope.$on('model.company.change', function () {
                    ServiceItemSvc.initializeModel();
                    CustomerSvc.initializeModel();
                    EmployeeSvc.initializeModel();
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
timetrackingServices.factory('ModelSvc', [
    function () {

        var model = {};
        model.company = {};

        return {
            model: model
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

        return {
            initialize: initialize,
            rootUrls: rootUrls,
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
                $rootScope.$broadcast('model.company.change');

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


        var initializeModel = function () {

            var serviceItemResource = $resource(ModelSvc.model.company._links.serviceItems.href, {}, { query: {method: 'GET', isArray: false} });

            serviceItemResource.query(function (data) {
                var serviceItems = data._embedded.serviceItems;
                ModelSvc.model.company.serviceItems = serviceItems;
            });
        }

        return {
            initializeModel: initializeModel
        }
    }]);


timetrackingServices.factory('CustomerSvc', ['$resource', '$rootScope', 'RootUrlSvc', 'ModelSvc',
    function ($resource, $rootScope, RootUrlSvc, ModelSvc) {

        var initializeModel = function () {
            var customerResource = $resource(ModelSvc.model.company._links.customers.href, {}, { query: {method: 'GET', isArray: false} });
            customerResource.query(function (data) {
                var customers = data._embedded.customers;
                ModelSvc.model.company.customers = customers;

            });
        }

        return {
            initializeModel: initializeModel
        }
    }]);

timetrackingServices.factory('EmployeeSvc', ['$resource', '$rootScope', 'RootUrlSvc', 'ModelSvc',
    function ($resource, $rootScope, RootUrlSvc, ModelSvc) {

        var initializeModel = function () {
            var employeeResource = $resource(ModelSvc.model.company._links.employees.href, {}, { query: {method: 'GET', isArray: false} });
            employeeResource.query(function (data) {
                var employees = data._embedded.employees;
                ModelSvc.model.company.employees = employees;

            });
        }

        return {
            initializeModel: initializeModel
        }
    }]);

timetrackingServices.factory('SyncRequestSvc', ['$http', '$rootScope', 'RootUrlSvc', 'ModelSvc',
    function ($http, $rootScope, RootUrlSvc, ModelSvc) {

        var sendSyncRequest = function (entityType, successCallback, errorCallback) {
            $http.post(RootUrlSvc.rootUrls.syncRequest, {type: entityType, companyId: ModelSvc.model.company.id})
                .success(successCallback);
        };

        return {
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

timetrackingServices.factory('TimeActivitySvc', ['$resource', '$rootScope', 'RootUrlSvc', 'ModelSvc',
    function ($resource, $rootScope, RootUrlSvc, ModelSvc) {

        var rootTimeActivityResource;

        var initialize = function () {
            rootTimeActivityResource = $resource(RootUrlSvc.rootUrls.timeActivities);
        };

        var createTimeActivity = function (timeActivity, callback) {
            rootTimeActivityResource.save(timeActivity).$promise.then(function (responseFromServer) {
                callback(responseFromServer);
            });
        };

        return {
            initialize: initialize,
            createTimeActivity: createTimeActivity
        }
    }]);
