'use strict';

/* Services */


var timetrackingServices = angular.module('myApp.services', ['ngResource', 'ui.bootstrap']);

timetrackingServices.factory('InitializerSvc',
    ['$rootScope', 'RootUrlSvc', 'CompanySvc', 'CustomerSvc', 'ServiceItemSvc', 'EmployeeSvc', 'TimeActivitySvc', 'InvoiceSvc', 'SystemPropertySvc',
        function ($rootScope, RootUrlSvc, CompanySvc, CustomerSvc, ServiceItemSvc, EmployeeSvc, TimeActivitySvc, InvoiceSvc, SystemPropertySvc) {

            var initialize = function () {

                $rootScope.$on('api.loaded', function () {
                    SystemPropertySvc.initializeModel();
                    CompanySvc.initialize();
                    CompanySvc.initializeModel();
                });

                $rootScope.$on('model.company.change', function () {
                    InvoiceSvc.initializeModel();
                    ServiceItemSvc.initializeModel();
                    CustomerSvc.initializeModel();
                    EmployeeSvc.initializeModel();
                    TimeActivitySvc.initializeModel();
                });

                RootUrlSvc.initialize();

                $rootScope.$on('$viewContentLoaded', function (scope, next, current) {
                    /*
                     Every time we load a new view, we need to reinitialize the intuit anywhere library
                     so that the connect to quickbooks button is rendered properly
                     */
                    intuit.ipp.anywhere.init();
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
        model.config = {};

        var isCompanyInitialized = function () {
            return !angular.equals({}, model.company)
        }

        return {
            model: model,
            isCompanyInitialized: isCompanyInitialized
        }
    }]);

//a service which reads the root of the API and stores all the resource urls
timetrackingServices.factory('RootUrlSvc', ['$resource', '$rootScope', '$location',
    function ($resource, $rootScope, $location) {

        var rootUrls = {};
        var apiRoot = function () {
            return $location.protocol() + "://" + $location.host() + ":9001";
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

                /*
                 Initializing the intuit anywhere javascript here because at this point we know the local id of the company
                 in the sample app. We need this information in the first OAUTH rest endpoint so that we can save the request token
                 and request token secret on the appropriate company in the local database.
                 */
                var grantUrl = RootUrlSvc.oauthGrantUrl() + '?appCompanyId=' + ModelSvc.model.company.id;
                intuit.ipp.anywhere.setup({
                    grantUrl: grantUrl,
                    datasources: {
                        quickbooks: true,
                        payments: false

                    }
                });
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
            $http.post(RootUrlSvc.rootUrls.syncRequest, {type: entityType, companyId: ModelSvc.model.company.id},
                {headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json"
                }})
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

        var timeActivityResource;

        var initializeModel = function () {

            timeActivityResource = $resource(RootUrlSvc.rootUrls.timeActivities, {},
                {
                    query: {
                        url: ModelSvc.model.company._links.timeActivities.href,
                        method: 'GET',
                        params: {
                            projection: 'summary'
                        },
                        isArray: false
                    },
                    save: {
                        method: 'POST',
                        params: {
                            projection: 'summary'
                        }
                    }

                });

            timeActivityResource.query(function (data) {
                if (data._embedded) {
                    ModelSvc.model.company.timeActivities = data._embedded.timeActivities;
                } else {
                    ModelSvc.model.company.timeActivities = [];
                }
            });
        };

        var createTimeActivity = function (timeActivity, callback) {
            timeActivityResource.save(timeActivity, function (responseFromServer) {
                ModelSvc.model.company.timeActivities.push(responseFromServer);
                callback(responseFromServer);
            });
        };

        return {
            initializeModel: initializeModel,
            createTimeActivity: createTimeActivity
        }
    }]);

timetrackingServices.factory('InvoiceSvc', ['$resource', '$rootScope', 'RootUrlSvc', 'ModelSvc',
    function ($resource, $rootScope, RootUrlSvc, ModelSvc) {

        var Invoice;

        var initializeModel = function () {
            Invoice = $resource(RootUrlSvc.rootUrls.invoices + "/:invoiceId", {},
                {
                    query: {
                        url: ModelSvc.model.company._links.invoices.href,
                        params: {
                            projection: 'summary'
                        },
                        isArray: false
                    },
                    update: {
                        method: 'PUT'
                    }
                });
        };

        var _getInvoices = function () {
            Invoice.query(function (data) {
                if (data._embedded) {
                    ModelSvc.model.company.pendingInvoices = [];
                    ModelSvc.model.company.billedInvoices = [];

                    angular.forEach(data._embedded.invoices, function (invoice) {
                        if (invoice.summary.status === 'Pending') {
                            ModelSvc.model.company.pendingInvoices.push(invoice);
                        } else {
                            ModelSvc.model.company.billedInvoices.push(invoice);
                        }
                    });

                } else {
                    ModelSvc.model.company.pendingInvoices = [];
                    ModelSvc.model.company.billedInvoices = [];
                }
            });
        };

        var getInvoices = function () {
            if (ModelSvc.isCompanyInitialized()) {
                _getInvoices();
            } else {
                $rootScope.$on('model.company.change', _getInvoices);
            }
        };

        var submitInvoiceForBilling = function (invoiceSummary, callback) {
            Invoice.get({invoiceId: invoiceSummary.id}, function (invoice) {
                invoice.status = 'ReadyToBeBilled';
                invoice.$update({invoiceId: invoiceSummary.id},
                    function (updatedInvoice) {

                        var index = ModelSvc.model.company.pendingInvoices.indexOf(invoiceSummary);
                        ModelSvc.model.company.pendingInvoices.splice(index, 1);
                        ModelSvc.model.company.billedInvoices.push(invoiceSummary);
                        invoiceSummary.qboId = updatedInvoice.qboId;
                        callback(updatedInvoice);
                    });
            })
        };

        return {
            initializeModel: initializeModel,
            getInvoices: getInvoices,
            submitInvoiceForBilling: submitInvoiceForBilling
        }
    }
]);

timetrackingServices.factory('BusyModalSvc', ['$modal',
    function ($modal) {

        var openBusyModal = function () {
            var modalInstance = $modal.open({
                templateUrl: 'partials/busyModal.html',
                backdrop: 'static',
                size: 'lg'
            });

            return modalInstance;
        };

        var closeBusyModal = function (modalInstance) {
            modalInstance.dismiss();
        };

        return {
            openBusyModal: openBusyModal,
            closeBusyModal: closeBusyModal
        }
    }
]);

timetrackingServices.factory('DeepLinkSvc', ['ModelSvc',
    function (ModelSvc) {

        var getQboDeepLinkURLRoot = function () {
            return "https://" + ModelSvc.model.systemProperties.qboUiHostname + "/login?";
        };

        var getMultipleEntitiesUrl = function (entityType) {
            return getQboDeepLinkURLRoot() + "deeplinkcompanyid=" + ModelSvc.model.company.qboId + "&pagereq=" + entityType;
        };

        var getSingleEntityUrl = function (entityType, entityId) {
            return getQboDeepLinkURLRoot() + "pagereq=" + entityType + "?txnId=" + entityId + "&deeplinkcompanyid=" + ModelSvc.model.company.qboId;
        };

        var getCustomersLink = function () {
            return getMultipleEntitiesUrl("customers");
        };

        var getEmployeesLink = function () {
            return getMultipleEntitiesUrl("employees");
        };

        var getSalesLink = function () {
            return getMultipleEntitiesUrl("sales");
        };

        var getItemsLink = function () {
            return getMultipleEntitiesUrl("items");
        };

        var getInvoiceLink = function (invoice) {
            return getSingleEntityUrl("invoice", invoice.qboId);
        };

        return {
            getCustomersLink: getCustomersLink,
            getEmployeesLink: getEmployeesLink,
            getItemsLink: getItemsLink,
            getInvoiceLink: getInvoiceLink,
            getSalesLink: getSalesLink
        }
    }
]);

timetrackingServices.factory('SystemPropertySvc', [ '$resource', 'RootUrlSvc', 'ModelSvc',
    function ($resource, RootUrlSvc, ModelSvc) {

        var SystemProperty;

        var initializeModel = function () {
            SystemProperty = $resource(RootUrlSvc.rootUrls.systemProperties, {},
                {
                    query: {
                        isArray: false
                    }
                });
            SystemProperty.query(function (data) {
                ModelSvc.model.systemProperties = {};

                if (data._embedded) {
                    angular.forEach(data._embedded.systemProperties, function (systemProperty) {
                        ModelSvc.model.systemProperties[systemProperty.key] = systemProperty.value;
                    });
                }
            });
        }

        return {
            initializeModel: initializeModel
        }
    }]);

timetrackingServices.factory('TrackingSvc', [function () {
    return {

        trackPage: function (pageName, event, properties) {
            var props = properties || {};
            props['site_section'] = 'sampleapps';
            pageName = 'sampleapps/timetracking/' + pageName;

            wa.trackPage(pageName, event, properties);
        },

        trackEvent: function (event, properties) {
            var props = properties || {};
            props['site_section'] = 'sampleapps';

            wa.trackEvent(event, properties);
        }

    };
}]);