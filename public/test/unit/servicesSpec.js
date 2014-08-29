'use strict';

/* jasmine specs for services go here */

describe('Unit: Services', function () {

    beforeEach(module('myApp.services'));

    var rootResource = 'http://localhost:8080';
    var rootCompaniesResource = 'http://localhost:8080/companies';
    var rootEmployeesResource = 'http://localhost:8080/employees';
    var rootCustomersResource = 'http://localhost:8080/customers';
    var rootServiceItemsResource = 'http://localhost:8080/serviceItem';
    var syncRequestResource = 'http://localhost:8080/syncrequest';

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

    describe('Unit: InitializerSvc', function() {
        var $rootScope, InitializerSvc, CompanySvc, RootUrlSvc, CustomerSvc, ServiceItemSvc, EmployeeSvc;

        beforeEach(inject(function (_InitializerSvc_, _CompanySvc_, _RootUrlSvc_, _$rootScope_, _EmployeeSvc_, _CustomerSvc_, _ServiceItemSvc_) {

            InitializerSvc = _InitializerSvc_;
            CompanySvc = _CompanySvc_;
            RootUrlSvc = _RootUrlSvc_;
            $rootScope = _$rootScope_;
            EmployeeSvc = _EmployeeSvc_;
            CustomerSvc = _CustomerSvc_;
            ServiceItemSvc = _ServiceItemSvc_;

        }));

        it('should have a function called initialize that initializes other services', function() {
            expect(InitializerSvc.initialize).toBeDefined();
            spyOn(RootUrlSvc, 'initialize');

            InitializerSvc.initialize();

            expect(RootUrlSvc.initialize).toHaveBeenCalled();
        });

        it('initialize() should call initialize on other services when api.loaded event is fired', function () {

            InitializerSvc.initialize();

            spyOn(CompanySvc, 'initialize');
            spyOn(CustomerSvc, 'initialize');
            spyOn(ServiceItemSvc, 'initialize');
            spyOn(EmployeeSvc, 'initialize');
            spyOn(CompanySvc, 'initializeModel');

            $rootScope.$broadcast('api.loaded');

            expect(CompanySvc.initialize).toHaveBeenCalled();
            expect(CustomerSvc.initialize).toHaveBeenCalled();
            expect(ServiceItemSvc.initialize).toHaveBeenCalled();
            expect(EmployeeSvc.initialize).toHaveBeenCalled();
            expect(CompanySvc.initializeModel).toHaveBeenCalled();
        });

        it('initialize() should call initializeModel on other services when the company is changed', function () {

            InitializerSvc.initialize();

            spyOn(CustomerSvc, 'initializeModel');
            spyOn(ServiceItemSvc, 'initializeModel');
            spyOn(EmployeeSvc, 'initializeModel');

            $rootScope.$broadcast('model.company.change');

            expect(CustomerSvc.initializeModel).toHaveBeenCalled();
            expect(ServiceItemSvc.initializeModel).toHaveBeenCalled();
            expect(EmployeeSvc.initializeModel).toHaveBeenCalled();
        });

        it('should initialize the intuit javsacript library on $viewContentLoaded', function () {
            InitializerSvc.initialize();

            spyOn(intuit.ipp.anywhere, 'init');

            $rootScope.$broadcast('$viewContentLoaded');

            expect(intuit.ipp.anywhere.init).not.toHaveBeenCalled();

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

        it('should call the root resource when initialized(), store the appropriate resources, and call all registered callbacks', function() {

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
            _embedded : {
                companies : [
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

        beforeEach(inject(function (_CompanySvc_,_RootUrlSvc_, _ModelSvc_, $injector, _$rootScope_, $location) {
            CompanySvc = _CompanySvc_;
            $httpBackend = $injector.get('$httpBackend');
            $rootScope = _$rootScope_;
            ModelSvc = _ModelSvc_;
            RootUrlSvc = _RootUrlSvc_;

            spyOn($location, "host").andReturn("localhost");

            $httpBackend.whenGET(rootCompaniesResource).respond(companiesRootResponse);

            RootUrlSvc.rootUrls.companies = rootCompaniesResource;
        }));

        it('should have an initialize function', function() {
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
            expect(intuit.ipp.anywhere.setup).toHaveBeenCalledWith({grantUrl: RootUrlSvc.oauthGrantUrl()+ "?appCompanyId=" + ModelSvc.model.company.id});

            expect(ModelSvc.model.companies).toEqual(companiesRootResponse._embedded.companies);
            expect(ModelSvc.model.company).toEqual(companiesRootResponse._embedded.companies[0]);

        });
    });

    describe('Unit: ServiceItemSvc', function () {
        var $httpBackend, $rootScope, ServiceItemSvc, ModelSvc, RootUrlSvc;

        var serviceItemsForCompany1URL = "http://localhost:8080/companies/1/serviceItems";

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

        it('should have an initialize function', function () {
            expect(ServiceItemSvc.initialize).toBeDefined();
        });

        it('should have an initializeModel function', function () {
            expect(ServiceItemSvc.initializeModel).toBeDefined();
        });

        it('should call the service item resource on initializeModel', function () {
            ServiceItemSvc.initialize();

            $httpBackend.expectGET(serviceItemsForCompany1URL);
            ServiceItemSvc.initializeModel();
            $httpBackend.flush();

            expect(ModelSvc.model.company.serviceItems).toEqual(serviceItemsForCompany1Response._embedded.serviceItems);
        });

    });

    describe('Unit: CustomerSvc', function () {
        var $httpBackend, $rootScope, CustomerSvc, ModelSvc, RootUrlSvc;

        var customersForCompany1URL = "http://localhost:8080/companies/1/customers";

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

        it('should have an initialize function', function () {
            expect(CustomerSvc.initialize).toBeDefined();
        });

        it('should have an initializeModel function', function () {
            expect(CustomerSvc.initializeModel).toBeDefined();
        });

        it('should call the customer resource on initializeModel', function () {
            CustomerSvc.initialize();

            $httpBackend.expectGET(customersForCompany1URL);
            CustomerSvc.initializeModel();
            $httpBackend.flush();

            expect(ModelSvc.model.company.customers).toEqual(customersForCompany1Response._embedded.customers);
        });

    });

    describe('Unit: EmployeeSvc', function () {
        var $httpBackend, $rootScope, EmployeeSvc, ModelSvc, RootUrlSvc;

        var employeesForCompany1URL = "http://localhost:8080/companies/1/employees";

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

        it('should have an initialize function', function () {
            expect(EmployeeSvc.initialize).toBeDefined();
        });

        it('should have an initializeModel function', function () {
            expect(EmployeeSvc.initializeModel).toBeDefined();
        });

        it('should call the customer resource on initializeModel', function () {
            EmployeeSvc.initialize();

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


});
