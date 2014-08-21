'use strict';

/* jasmine specs for controllers go here */
describe("Unit: Controllers", function () {


    describe('Unit: NavCtrl', function () {

        var LocationMock = function (initialPath) {
            var pathStr = initialPath || '';
            this.path = function (pathArg) {
                return pathArg ? pathStr = pathArg : pathStr;
            };
        };

        var ctrl, scope, $location;

        beforeEach(module('myApp.controllers'));

        beforeEach(inject(function ($rootScope, $controller, $injector) {

            //create an empty scope
            scope = $rootScope.$new();
            $location = $injector.get('$location');
            spyOn($location, 'path').andCallFake(new LocationMock().path);

            //declare the controller and inject our empty scope
            ctrl = $controller('NavCtrl', {$scope: scope});

        }));

        it('should have a nav controller defined', function () {
            expect(ctrl).toBeDefined();
        });

        it('should have the model on the scope object', function () {
            expect(scope.model).toBeDefined();
        });

        it('should have a navClass function for setting the navbar correctly', function () {
            expect(scope.navClass).toBeDefined();

            $location.path('/setup');
            expect(scope.navClass('setup')).toBe('active');
            expect(scope.navClass('preferences')).toBe('');

            $location.path('/preferences');
            expect(scope.navClass('preferences')).toBe('active');
            expect(scope.navClass('setup')).toBe('');

        });
    });

    describe('Unit: SettingsCtrl', function () {


        var ctrl, scope, modelSvc;

        beforeEach(module('myApp.controllers'));

        beforeEach(inject(function ($rootScope, $controller, ModelSvc) {

            //create an empty scope
            scope = $rootScope.$new();

            //declare the controller and inject our empty scope
            ctrl = $controller('SettingsCtrl', {$scope: scope});

            modelSvc = ModelSvc;

        }));

        it('should have a settings controller defined', function () {
            expect(ctrl).toBeDefined();
        });

        it('should have the model on the scope object', function () {
            expect(scope.model).toBeDefined();
        });

        it('should have a showConnectButton function for showing the connect button', function () {
            expect(scope.showConnectButton).toBeDefined();

            //if the company is not connected to QBO, we need to show the connect button
            modelSvc.model.company.connectedToQbo = false;
            expect(scope.showConnectButton()).toBeTruthy();

            //if the company is connected to QBO, we need to hide the connect button
            modelSvc.model.company.connectedToQbo = true;
            expect(scope.showConnectButton()).toBeFalsy();
        });

        it('should have a functions function for disabling the sync buttons ', function() {
            expect(scope.disableEmployeeSyncButton).toBeDefined();

            modelSvc.model.company.connectedToQbo = false;
            modelSvc.model.company.customersSynced = false;
            modelSvc.model.company.employeesSynced = false;
            modelSvc.model.company.serviceItemsSycned = false;
            expect(scope.disableEmployeeSyncButton()).toBeTruthy();
            expect(scope.disableCustomersSyncButton()).toBeTruthy();
            expect(scope.disableServiceItemsSyncButton()).toBeTruthy();

            modelSvc.model.company.connectedToQbo = true;
            expect(scope.disableEmployeeSyncButton()).toBeFalsy();
            expect(scope.disableCustomersSyncButton()).toBeFalsy();
            expect(scope.disableServiceItemsSyncButton()).toBeFalsy();

            modelSvc.model.company.employeesSynced = true;
            expect(scope.disableEmployeeSyncButton()).toBeTruthy();

            modelSvc.model.company.customersSynced = true;
            expect(scope.disableCustomersSyncButton()).toBeTruthy();

            modelSvc.model.company.serviceItemsSycned = true;
            expect(scope.disableServiceItemsSyncButton()).toBeTruthy();

        });
    });
});



