'use strict';

/* jasmine specs for services go here */

describe('Unit: Services', function () {

    beforeEach(module('myApp.services'));

    var rootResource = 'http://localhost:9001';
    var rootCompaniesResource = 'http://localhost:9001/companies';
    var rootEmployeesResource = 'http://localhost:9001/employees';
    var rootCustomersResource = 'http://localhost:9001/customers';
    var rootServiceItemsResource = 'http://localhost:9001/serviceItem';
    var syncRequestResource = 'http://localhost:9001/syncrequest';

    var templateThing = "{?page,size,sort}";

    var apiRootResponse = {
        _links: {
            companies: {
                href: rootCompaniesResource || templateThing
            },
            employees: {
                href: rootEmployeesResource || templateThing
            },
            customers: {
                href: rootCustomersResource || templateThing
            },
            serviceItems: {
                href: rootServiceItemsResource || templateThing
            },
            syncRequest: {
                href: syncRequestResource
            }
        }
    };

    describe('Unit: InitializerSvc', function () {
        var $rootScope, InitializerSvc, CompanySvc, RootUrlSvc, CustomerSvc, ServiceItemSvc, EmployeeSvc, TimeActivitySvc, InvoiceSvc, SystemPropertySvc;

        beforeEach(inject(function (_InitializerSvc_, _CompanySvc_, _RootUrlSvc_, _$rootScope_, _EmployeeSvc_, _CustomerSvc_, _ServiceItemSvc_, _TimeActivitySvc_, _InvoiceSvc_, _SystemPropertySvc_) {

            InitializerSvc = _InitializerSvc_;
            CompanySvc = _CompanySvc_;
            RootUrlSvc = _RootUrlSvc_;
            $rootScope = _$rootScope_;
            EmployeeSvc = _EmployeeSvc_;
            CustomerSvc = _CustomerSvc_;
            ServiceItemSvc = _ServiceItemSvc_;
            TimeActivitySvc = _TimeActivitySvc_;
            InvoiceSvc = _InvoiceSvc_;
            SystemPropertySvc = _SystemPropertySvc_;

        }));

        it('should have a function called initialize that initializes other services', function () {
            expect(InitializerSvc.initialize).toBeDefined();
            spyOn(RootUrlSvc, 'initialize');

            InitializerSvc.initialize();

            expect(RootUrlSvc.initialize).toHaveBeenCalled();
        });

        it('initialize() should call initialize on other services when api.loaded event is fired', function () {

            InitializerSvc.initialize();

            spyOn(CompanySvc, 'initialize');
            spyOn(CompanySvc, 'initializeModel');
            spyOn(SystemPropertySvc, 'initializeModel');

            $rootScope.$broadcast('api.loaded');

            expect(CompanySvc.initialize).toHaveBeenCalled();
            expect(CompanySvc.initializeModel).toHaveBeenCalled();
            expect(SystemPropertySvc.initializeModel).toHaveBeenCalled();
        });

        it('initialize() should call initializeModel on other services when the company is changed', function () {

            InitializerSvc.initialize();

            spyOn(CustomerSvc, 'initializeModel');
            spyOn(ServiceItemSvc, 'initializeModel');
            spyOn(EmployeeSvc, 'initializeModel');
            spyOn(InvoiceSvc, 'initializeModel');
            spyOn(TimeActivitySvc, 'initializeModel');

            $rootScope.$broadcast('model.company.change');

            expect(CustomerSvc.initializeModel).toHaveBeenCalled();
            expect(ServiceItemSvc.initializeModel).toHaveBeenCalled();
            expect(EmployeeSvc.initializeModel).toHaveBeenCalled();
            expect(InvoiceSvc.initializeModel).toHaveBeenCalled();
            expect(TimeActivitySvc.initializeModel).toHaveBeenCalled();
        });

        it('should initialize the intuit javsacript library on $viewContentLoaded', function () {
            InitializerSvc.initialize();

            spyOn(intuit.ipp.anywhere, 'init');

            $rootScope.$broadcast('$viewContentLoaded');

            expect(intuit.ipp.anywhere.init).toHaveBeenCalled();

        });
    });

    describe('Unit: ModelSvc', function () {

        var ModelSvc, $rootScope;

        beforeEach(inject(function (_ModelSvc_, _$rootScope_) {
            ModelSvc = _ModelSvc_;
            $rootScope = _$rootScope_;
        }));


        it('should have a root model object defined', function () {
            expect(ModelSvc.model).toBeDefined();
        });

        it('should have a company defined on the root model object', function () {
            expect(ModelSvc.model.company).toBeDefined();
        });
    });

    describe('Unit: RootUrlSvc', function () {
        var RootUrlSvc, $rootScope, $httpBackend;


        beforeEach(inject(function (_RootUrlSvc_, _$rootScope_, $injector, $location) {
            RootUrlSvc = _RootUrlSvc_;
            $rootScope = _$rootScope_;
            $httpBackend = $injector.get('$httpBackend');

            spyOn($location, "host").andReturn("localhost");

            $httpBackend.whenGET(rootResource).respond(apiRootResponse);
        }));

        it('should have an interface defined', function () {
            expect(RootUrlSvc.rootUrls).toBeDefined();
            expect(RootUrlSvc.initialize).toBeDefined();
        });

        it('should have an initialize function', function () {
            expect(RootUrlSvc.initialize).toBeDefined();
        });

        it('should call the root resource when initialized(), store the appropriate resources, and call all registered callbacks', function () {

            $httpBackend.expectGET(rootResource);
            RootUrlSvc.initialize();

            spyOn($rootScope, '$broadcast').andCallThrough();

            $httpBackend.flush();

            var expectedRootUrls = {
                companies: rootCompaniesResource,
                employees: rootEmployeesResource,
                customers: rootCustomersResource,
                serviceItems: rootServiceItemsResource,
                syncRequest: syncRequestResource
            }

            expect(RootUrlSvc.rootUrls).toEqual(expectedRootUrls);
            expect($rootScope.$broadcast).toHaveBeenCalledWith('api.loaded');

        });

        it('should have an  function which returns the oauth grant url', function () {
            expect(RootUrlSvc.oauthGrantUrl).toBeDefined();
            expect(RootUrlSvc.oauthGrantUrl()).toEqual(rootResource + "/request_token")
        });
    });

    describe('Unit: CompanySvc', function () {
        var $httpBackend, $rootScope, CompanySvc, ModelSvc, RootUrlSvc;

        var companiesRootResponse = {
            _embedded: {
                companies: [
                    {
                        name: "Company 1",
                        id: '123'
                    },
                    {
                        name: "Company 2",
                        id: '234'
                    }
                ]
            }
        };

        beforeEach(inject(function (_CompanySvc_, _RootUrlSvc_, _ModelSvc_, $injector, _$rootScope_, $location) {
            CompanySvc = _CompanySvc_;
            $httpBackend = $injector.get('$httpBackend');
            $rootScope = _$rootScope_;
            ModelSvc = _ModelSvc_;
            RootUrlSvc = _RootUrlSvc_;

            spyOn($location, "host").andReturn("localhost");

            $httpBackend.whenGET(rootCompaniesResource).respond(companiesRootResponse);

            RootUrlSvc.rootUrls.companies = rootCompaniesResource;
        }));

        it('should have an initialize function', function () {
            expect(CompanySvc.initialize).toBeDefined();
        });

        it('should have an initializeModel function', function () {
            expect(CompanySvc.initializeModel).toBeDefined();
        });

        it('should call the companies endpoint, update the model, and broadcast model.company.change event on initializeModel()', function () {

            spyOn($rootScope, '$broadcast').andCallThrough();
            spyOn(intuit.ipp.anywhere, 'setup');

            $httpBackend.expectGET(rootCompaniesResource);
            CompanySvc.initialize();
            CompanySvc.initializeModel();
            $httpBackend.flush();


            expect($rootScope.$broadcast).toHaveBeenCalledWith('model.company.change');
            expect(intuit.ipp.anywhere.setup).toHaveBeenCalledWith({grantUrl: RootUrlSvc.oauthGrantUrl() + "?appCompanyId=" + ModelSvc.model.company.id});

            expect(ModelSvc.model.companies).toEqual(companiesRootResponse._embedded.companies);
            expect(ModelSvc.model.company).toEqual(companiesRootResponse._embedded.companies[0]);

        });
    });

    describe('Unit: ServiceItemSvc', function () {
        var $httpBackend, $rootScope, ServiceItemSvc, ModelSvc, RootUrlSvc;

        var serviceItemsForCompany1URL = "http://localhost:9001/companies/1/serviceItems";

        var serviceItemsForCompany1Response = {
            _embedded: {
                serviceItems: [
                    {
                        name: "Research",
                        id: '123'
                    },
                    {
                        name: "Deposition",
                        id: '234'
                    }
                ]
            }
        };

        beforeEach(inject(function (_ServiceItemSvc_, _RootUrlSvc_, _ModelSvc_, $injector, _$rootScope_, $location) {
            ServiceItemSvc = _ServiceItemSvc_;
            $httpBackend = $injector.get('$httpBackend');
            $rootScope = _$rootScope_;
            ModelSvc = _ModelSvc_;
            RootUrlSvc = _RootUrlSvc_;

            spyOn($location, "host").andReturn("localhost");

            $httpBackend.whenGET(serviceItemsForCompany1URL).respond(serviceItemsForCompany1Response);

            ModelSvc.model.company = {
                _links: {
                    serviceItems: {
                        href: serviceItemsForCompany1URL
                    }
                }
            };

            RootUrlSvc.rootUrls.companies = rootCompaniesResource;
        }));

        it('should have an initializeModel function', function () {
            expect(ServiceItemSvc.initializeModel).toBeDefined();
        });

        it('should call the service item resource on initializeModel', function () {

            $httpBackend.expectGET(serviceItemsForCompany1URL);
            ServiceItemSvc.initializeModel();
            $httpBackend.flush();

            expect(ModelSvc.model.company.serviceItems).toEqual(serviceItemsForCompany1Response._embedded.serviceItems);
        });

    });

    describe('Unit: CustomerSvc', function () {
        var $httpBackend, $rootScope, CustomerSvc, ModelSvc, RootUrlSvc;

        var customersForCompany1URL = "http://localhost:9001/companies/1/customers";

        var customersForCompany1Response = {
            _embedded: {
                customers: [
                    {
                        foo: "bar",
                        id: '123'
                    },
                    {
                        hello: "World",
                        id: '234'
                    }
                ]
            }
        };

        beforeEach(inject(function (_CustomerSvc_, _RootUrlSvc_, _ModelSvc_, $injector, _$rootScope_, $location) {
            CustomerSvc = _CustomerSvc_;
            $httpBackend = $injector.get('$httpBackend');
            $rootScope = _$rootScope_;
            ModelSvc = _ModelSvc_;
            RootUrlSvc = _RootUrlSvc_;

            spyOn($location, "host").andReturn("localhost");

            $httpBackend.whenGET(customersForCompany1URL).respond(customersForCompany1Response);

            ModelSvc.model.company = {
                _links: {
                    customers: {
                        href: customersForCompany1URL
                    }
                }
            };

            RootUrlSvc.rootUrls.companies = rootCompaniesResource;
        }));

        it('should have an initializeModel function', function () {
            expect(CustomerSvc.initializeModel).toBeDefined();
        });

        it('should call the customer resource on initializeModel', function () {

            $httpBackend.expectGET(customersForCompany1URL);
            CustomerSvc.initializeModel();
            $httpBackend.flush();

            expect(ModelSvc.model.company.customers).toEqual(customersForCompany1Response._embedded.customers);
        });

    });

    describe('Unit: EmployeeSvc', function () {
        var $httpBackend, $rootScope, EmployeeSvc, ModelSvc, RootUrlSvc;

        var employeesForCompany1URL = "http://localhost:9001/companies/1/employees";

        var employeesForCompany1Response = {
            _embedded: {
                employees: [
                    {
                        foo: "bar",
                        id: '123'
                    },
                    {
                        hello: "World",
                        id: '234'
                    }
                ]
            }
        };

        beforeEach(inject(function (_EmployeeSvc_, _RootUrlSvc_, _ModelSvc_, $injector, _$rootScope_, $location) {
            EmployeeSvc = _EmployeeSvc_;
            $httpBackend = $injector.get('$httpBackend');
            $rootScope = _$rootScope_;
            ModelSvc = _ModelSvc_;
            RootUrlSvc = _RootUrlSvc_;

            spyOn($location, "host").andReturn("localhost");

            $httpBackend.whenGET(employeesForCompany1URL).respond(employeesForCompany1Response);

            ModelSvc.model.company = {
                _links: {
                    employees: {
                        href: employeesForCompany1URL
                    }
                }
            };

            RootUrlSvc.rootUrls.companies = rootCompaniesResource;
        }));

        it('should have an initializeModel function', function () {
            expect(EmployeeSvc.initializeModel).toBeDefined();
        });

        it('should call the employee resource on initializeModel', function () {

            $httpBackend.expectGET(employeesForCompany1URL);
            EmployeeSvc.initializeModel();
            $httpBackend.flush();

            expect(ModelSvc.model.company.employees).toEqual(employeesForCompany1Response._embedded.employees);
        });

    });

    describe('Unit: SyncRequestSvc', function () {
        var $httpBackend, $rootScope, SyncRequestSvc, RootUrlSvc, ModelSvc;

        var syncResponse = {
            foo: 'bar',
            hello: 'world'
        };

        beforeEach(inject(function (_SyncRequestSvc_, _RootUrlSvc_, $injector, _$rootScope_, $location, _ModelSvc_) {
            $httpBackend = $injector.get('$httpBackend');
            $rootScope = _$rootScope_;
            SyncRequestSvc = _SyncRequestSvc_;
            RootUrlSvc = _RootUrlSvc_;
            ModelSvc = _ModelSvc_;

            spyOn($location, "host").andReturn("localhost");

            RootUrlSvc.rootUrls.syncRequest = syncRequestResource;
            ModelSvc.model.company = {
                id: "1"
            };
        }));

        it('should have an sendCustomerSyncRequest function', function () {
            expect(SyncRequestSvc.sendCustomerSyncRequest).toBeDefined();
        });

        it('should have an sendServiceItemsSyncRequest function', function () {
            expect(SyncRequestSvc.sendServiceItemsSyncRequest).toBeDefined();
        });

        it('should have an sendEmployeeSyncRequest function', function () {
            expect(SyncRequestSvc.sendEmployeeSyncRequest).toBeDefined();
        });

        it('should have call the syncRequest endpoint on sendCustomerSyncRequest', function () {
            $httpBackend.whenPOST(syncRequestResource).respond(syncResponse);

            var callback = jasmine.createSpy();

            $httpBackend.expectPOST(syncRequestResource, {type: 'Customer', companyId: ModelSvc.model.company.id});

            SyncRequestSvc.sendCustomerSyncRequest(callback);
            $httpBackend.flush();

            expect(callback).toHaveBeenCalled();
        });

        it('should have call the syncRequest endpoint on sendEmployeeSyncRequest', function () {
            $httpBackend.whenPOST(syncRequestResource).respond(syncResponse);

            var callback = jasmine.createSpy();

            $httpBackend.expectPOST(syncRequestResource, {type: 'Employee', companyId: ModelSvc.model.company.id});

            SyncRequestSvc.sendEmployeeSyncRequest(callback);
            $httpBackend.flush();

            expect(callback).toHaveBeenCalled();
        });

        it('should have call the syncRequest endpoint on sendServiceItemsSyncRequest', function () {
            $httpBackend.whenPOST(syncRequestResource).respond(syncResponse);

            var callback = jasmine.createSpy();

            $httpBackend.expectPOST(syncRequestResource, {type: 'ServiceItem', companyId: ModelSvc.model.company.id});

            SyncRequestSvc.sendServiceItemsSyncRequest(callback);
            $httpBackend.flush();

            expect(callback).toHaveBeenCalled();
        });
    });

    describe('Unit: TimeActivitySvc', function () {
        var $httpBackend, $rootScope, TimeActivitySvc, ModelSvc, RootUrlSvc;

        var timeActivityRootResource = "http://localhost:9001/timeActivities";

        var companyId = '1234';
        var companyTimeActivitiesURL = "http://localhost:9001/companies/" + companyId + "/timeActivities";


        beforeEach(inject(function (_TimeActivitySvc_, _RootUrlSvc_, _ModelSvc_, $injector, _$rootScope_, $location) {
            TimeActivitySvc = _TimeActivitySvc_;
            $httpBackend = $injector.get('$httpBackend');
            $rootScope = _$rootScope_;
            ModelSvc = _ModelSvc_;
            RootUrlSvc = _RootUrlSvc_;

            RootUrlSvc.rootUrls = {
                timeActivities: timeActivityRootResource
            }

            spyOn($location, "host").andReturn("localhost");

            ModelSvc.model.company = {
                id: companyId,
                _links: {
                    timeActivities: {
                        href: companyTimeActivitiesURL
                    }
                }
            };


        }));

        it('should have an initializeModel function', function () {
            expect(TimeActivitySvc.initializeModel).toBeDefined();
        });

        it('should have a createTimeActivity function', function () {
            expect(TimeActivitySvc.createTimeActivity).toBeDefined();
        });

        it('should call the time activity resource on initializeModel', function () {

            var timeActivity = {
                foo: "bar"
            };

            var expectedResponse = {
                _embedded: {
                    timeActivities: [timeActivity]
                }
            };

            var expectedURL = companyTimeActivitiesURL + "?projection=summary";

            $httpBackend.whenGET(expectedURL).respond(expectedResponse);

            $httpBackend.expectGET(expectedURL);
            TimeActivitySvc.initializeModel();

            $httpBackend.flush();

            expect(ModelSvc.model.company.timeActivities).toEqual([timeActivity]);

        });

        it('should call the time activity resource on createTimeActivity', function () {
            var timeActivity = {
                foo: "bar"
            };

            var expectedResponse = {
                hello: "world"
            };

            var callback = jasmine.createSpy();

            var expectedURL = timeActivityRootResource + "?projection=summary";

            $httpBackend.whenPOST(expectedURL).respond(expectedResponse);
            $httpBackend.expectGET(companyTimeActivitiesURL + "?projection=summary").respond({});
            $httpBackend.expectPOST(expectedURL, timeActivity);

            TimeActivitySvc.initializeModel();
            TimeActivitySvc.createTimeActivity(timeActivity, callback);

            $httpBackend.flush();

            expect(ModelSvc.model.company.timeActivities[0].hello).toEqual("world");

            expect(callback).toHaveBeenCalled();

        });
    });

    describe('Unit: InvoiceSvc', function () {
        var $httpBackend, $rootScope, InvoiceSvc, ModelSvc, RootUrlSvc;

        var invoiceRootResource = "http://localhost:9001/invoices";
        var companyId = '1234';
        var companyInvoicesUrl = "http://localhost:9001/companies/" + companyId + "/invoices";

        beforeEach(inject(function (_InvoiceSvc_, _RootUrlSvc_, _ModelSvc_, $injector, _$rootScope_, $location) {
            InvoiceSvc = _InvoiceSvc_;
            $httpBackend = $injector.get('$httpBackend');
            $rootScope = _$rootScope_;
            ModelSvc = _ModelSvc_;
            RootUrlSvc = _RootUrlSvc_;

            RootUrlSvc.rootUrls = {
                invoices: invoiceRootResource
            };

            ModelSvc.model.company.id = companyId;
            ModelSvc.model.company._links = {
                invoices: {
                    href: companyInvoicesUrl
                }
            };

            ModelSvc.model.company.pendingInvoices = [];
            ModelSvc.model.company.billedInvoices = [];


            spyOn($location, "host").andReturn("localhost");

        }));

        it('should have an initializeModel function', function () {
            expect(InvoiceSvc.initializeModel).toBeDefined();
        });

        it('should have a getInvoices function', function () {

            var pendingInvoice =
            {
                foo: "bar",
                summary: {
                    status: "Pending"
                }
            };

            var billedInvoice = {
                hello: "world",
                summary: {
                    status: "Billed"
                }
            }

            var response = {_embedded: {
                invoices: [pendingInvoice, billedInvoice]
            }};

            expect(InvoiceSvc.getInvoices).toBeDefined();

            var expectedURL = companyInvoicesUrl + "?projection=summary";

            $httpBackend.whenGET(expectedURL).respond(response);

            $httpBackend.expectGET(expectedURL);

            InvoiceSvc.initializeModel();
            InvoiceSvc.getInvoices();

            $httpBackend.flush();

            expect(ModelSvc.model.company.pendingInvoices).toEqual([pendingInvoice]);
            expect(ModelSvc.model.company.billedInvoices).toEqual([billedInvoice]);

        });

        it('should have a submitInvoiceForBilling function', function () {
            expect(InvoiceSvc.submitInvoiceForBilling).toBeDefined();


            var invoiceId = 3333;
            var invoiceSummary = {id: invoiceId};
            var callback = jasmine.createSpy();
            var putResponse = {hello: "world"};

            var invoiceUrl = invoiceRootResource + "/" + invoiceId;
            $httpBackend.whenGET(invoiceUrl).respond({});
            $httpBackend.expectGET(invoiceUrl);
            $httpBackend.whenPUT(invoiceUrl).respond(putResponse);
            $httpBackend.expectPUT(invoiceUrl, {status: 'ReadyToBeBilled'});

            InvoiceSvc.initializeModel();
            InvoiceSvc.submitInvoiceForBilling(invoiceSummary, callback);

            $httpBackend.flush();

            expect(callback).toHaveBeenCalled();
            expect(callback.mostRecentCall.args[0].hello).toEqual("world");

        });


    });

    describe('Unit: BusyModalSvc', function () {
        var $modal, BusyModalSvc;

        beforeEach(inject(function ($injector, _BusyModalSvc_) {
            $modal = $injector.get('$modal');
            BusyModalSvc = _BusyModalSvc_;
        }));

        it('should have a openBusyModal function', function () {
            expect(BusyModalSvc.openBusyModal).toBeDefined();

            var expectedModal = {foo: "bar"};

            spyOn($modal, 'open').andReturn(expectedModal);

            var result = BusyModalSvc.openBusyModal();

            expect($modal.open).toHaveBeenCalledWith({
                templateUrl: "partials/busyModal.html",
                backdrop: 'static',
                size: 'lg'
            });

            expect(result).toBe(expectedModal)
        });

        it('should have a closeBusyModal function', function () {

            expect(BusyModalSvc.closeBusyModal).toBeDefined();

            var dismiss = jasmine.createSpy('dismiss');

            var modal = {dismiss: dismiss};

            BusyModalSvc.closeBusyModal(modal);

            expect(dismiss).toHaveBeenCalled();
        });

    });

    describe('Unit: DeepLinkSvc', function () {
        var ModelSvc, DeepLinkSvc;

        var companyQboId = "1234567";

        beforeEach(inject(function (_ModelSvc_, _DeepLinkSvc_) {
            ModelSvc = _ModelSvc_;
            DeepLinkSvc = _DeepLinkSvc_;

            ModelSvc.model = {};
            ModelSvc.model.company = {qboId: companyQboId};
            ModelSvc.model.systemProperties = {qboUiHostname: "qa.qbo.intuit.com"};
        }));

        it('should have a getCustomersLink', function () {
            expect(DeepLinkSvc.getCustomersLink).toBeDefined();
        });

        it('should have a getEmployeesLink', function () {
            expect(DeepLinkSvc.getEmployeesLink).toBeDefined();
        });

        it('should have a getItemsLink', function () {
            expect(DeepLinkSvc.getItemsLink).toBeDefined();
        });

        it('should have a getInvoiceLink', function () {
            expect(DeepLinkSvc.getInvoiceLink).toBeDefined();
        });

        it('should have a getSalesLink', function () {
            expect(DeepLinkSvc.getSalesLink).toBeDefined();
        });

        it('should return the correct customers link when getCustomersLink() is called', function () {
            expect(DeepLinkSvc.getCustomersLink()).toEqual("https://qa.qbo.intuit.com/login?deeplinkcompanyid=" + companyQboId + "&pagereq=customers");
        });

        it('should return the correct customers link when getEmployeesLink() is called', function () {
            expect(DeepLinkSvc.getEmployeesLink()).toEqual("https://qa.qbo.intuit.com/login?deeplinkcompanyid=" + companyQboId + "&pagereq=employees");
        });

        it('should return the correct items link when getItemsLink() is called', function () {
            expect(DeepLinkSvc.getItemsLink()).toEqual("https://qa.qbo.intuit.com/login?deeplinkcompanyid=" + companyQboId + "&pagereq=items");
        });

        it('should return  the correct invoice link when getInvoiceLink() is called', function () {
            var invoiceQboId = "123";
            var invoice = {qboId: invoiceQboId};

            expect(DeepLinkSvc.getInvoiceLink(invoice)).toEqual("https://qa.qbo.intuit.com/login?pagereq=invoice?txnId=" + invoiceQboId + "&deeplinkcompanyid=" + companyQboId);
        });

        it('should return  the correct sales link when getSalesLink() is called', function () {

            expect(DeepLinkSvc.getSalesLink()).toEqual("https://qa.qbo.intuit.com/login?deeplinkcompanyid=" + companyQboId + "&pagereq=sales");
        });
    });

    describe('Unit: SystemPropertySvc', function () {
        var $httpBackend, $rootScope, SystemPropertySvc, ModelSvc, RootUrlSvc;

        var systemPropertyRootResource = "http://localhost:9001/systemProperties"

        beforeEach(inject(function (_SystemPropertySvc_, _RootUrlSvc_, _ModelSvc_, $injector, _$rootScope_, $location) {
            SystemPropertySvc = _SystemPropertySvc_;
            $httpBackend = $injector.get('$httpBackend');
            $rootScope = _$rootScope_;
            ModelSvc = _ModelSvc_;
            RootUrlSvc = _RootUrlSvc_;

            RootUrlSvc.rootUrls = {
                systemProperties: systemPropertyRootResource
            };

            spyOn($location, "host").andReturn("localhost");

        }));

        it('should have an initializeModel method that calls the System property resource', function () {
            expect(SystemPropertySvc.initializeModel).toBeDefined();

            var response = {
                _embedded: {
                    systemProperties: [
                        {
                            key: "theKey",
                            value: "theValue"
                        },
                        {
                            key: "anotherKey",
                            value: "anotherValue"
                        }
                    ]
                }
            };

            $httpBackend.whenGET(systemPropertyRootResource).respond(response);
            $httpBackend.expectGET(systemPropertyRootResource);

            SystemPropertySvc.initializeModel();

            $httpBackend.flush();

            expect(ModelSvc.model.systemProperties.theKey).toEqual("theValue");
            expect(ModelSvc.model.systemProperties.anotherKey).toEqual("anotherValue");

        });
    });
});
