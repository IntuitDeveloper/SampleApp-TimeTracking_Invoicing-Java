'use strict';

/* jasmine specs for services go here */

describe('Unit: Services', function () {

    beforeEach(module('myApp.services'));

    var rootResource = 'http://localhost:8080';
    var rootCompaniesResource = 'http://localhost:8080/companies';

    var apiRootResponse = {
        _links: {
            companies: {
                href: rootCompaniesResource || "{?page,size,sort}"
            }
        }
    };

    describe('Unit: InitializerSvc', function() {
        var InitializerSvc, CompanySvc, RootUrlSvc;

        beforeEach(inject(function (_InitializerSvc_, _CompanySvc_, _RootUrlSvc_) {
            InitializerSvc = _InitializerSvc_;
            CompanySvc = _CompanySvc_;
            RootUrlSvc = _RootUrlSvc_;
        }));

        it('should have a function called initialize that initializes other services', function() {
            expect(InitializerSvc.initialize).toBeDefined();
            spyOn(CompanySvc, 'initialize');
            spyOn(RootUrlSvc, 'initialize');


            InitializerSvc.initialize();

            expect(CompanySvc.initialize).toHaveBeenCalled();
            expect(RootUrlSvc.initialize).toHaveBeenCalled();
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

        it('should have a onCompanyChange() function', function () {
            expect(ModelSvc.onCompanyChange).toBeDefined();
        });

        it('should have a broadcastCompanyChange() function', function () {
            expect(ModelSvc.broadcastCompanyChange).toBeDefined();
        });

        it('should allow you to register for company change events', function () {

            var callback = jasmine.createSpy();

            ModelSvc.onCompanyChange($rootScope, callback);
            ModelSvc.broadcastCompanyChange();
            expect(callback).toHaveBeenCalled();
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

        it('should have an onApiLoaded function', function () {
            expect(RootUrlSvc.onApiLoaded).toBeDefined();
        });

        it('should call the root resource when initialized(), store the appropriate resources, and call all registered callbacks', function() {

            var callback = jasmine.createSpy();
            RootUrlSvc.onApiLoaded($rootScope, callback);

            $httpBackend.expectGET(rootResource);
            RootUrlSvc.initialize();
            $httpBackend.flush();

            var expectedRootUrls = {
                companies: rootCompaniesResource,
            }

            expect(RootUrlSvc.rootUrls).toEqual(expectedRootUrls);
            expect(callback).toHaveBeenCalled();
        });

        if('should have an apiRoot function which returns the root of the api', function() {
            expect(RootUrlSvc.oauthGrantUrl).toBeDefined();
            expect(RootUrlSvc.oauthGrantUrl()).toEqual(rootResource+"/requesttoken")
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
                        id: '234',
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

        if('should have a getCompanies function', function() {
            expect(CompanySvc.getCompanies).toBeDefined();
        });

        it('should register a callback with the RootUrlSvc on initialization', function() {
            spyOn(RootUrlSvc, 'onApiLoaded');
            CompanySvc.initialize();
            expect(RootUrlSvc.onApiLoaded).toHaveBeenCalledWith($rootScope, CompanySvc.getCompanies);
        });

        it('should call the companies endpoint, update the model, and trigger the ModelSvc.onCompanyChange function() on getCompanies()', function() {

            spyOn(ModelSvc, 'broadcastCompanyChange');

            $httpBackend.expectGET(rootCompaniesResource);
            spyOn(intuit.ipp.anywhere, 'setup');
            CompanySvc.getCompanies();
            $httpBackend.flush();

            expect(ModelSvc.broadcastCompanyChange).toHaveBeenCalled();
            expect(intuit.ipp.anywhere.setup).toHaveBeenCalledWith({grantUrl: RootUrlSvc.oauthGrantUrl()+ "?appCompanyId=" + ModelSvc.model.company.id});

            expect(ModelSvc.model.companies).toEqual(companiesRootResponse._embedded.companies);
            expect(ModelSvc.model.company).toEqual(companiesRootResponse._embedded.companies[0]);

        });
    });
});
