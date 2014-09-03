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

            $location.path('/settings');
            expect(scope.navClass('settings')).toBe('active');

//            $location.path('/preferences');
//            expect(scope.navClass('preferences')).toBe('active');
//            expect(scope.navClass('setup')).toBe('');

        });
    });

    describe('Unit: SettingsCtrl', function () {


        var ctrl, scope, ModelSvc, SyncRequestSvc, CompanySvc;

        beforeEach(module('myApp.controllers'));

        beforeEach(inject(function ($rootScope, $controller, _ModelSvc_, _SyncRequestSvc_, _CompanySvc_) {

            //create an empty scope
            scope = $rootScope.$new();

            //declare the controller and inject our empty scope
            ctrl = $controller('SettingsCtrl', {$scope: scope});


            ModelSvc = _ModelSvc_;
            SyncRequestSvc = _SyncRequestSvc_;
            CompanySvc = _CompanySvc_;

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
            ModelSvc.model.company.connectedToQbo = false;
            expect(scope.showConnectButton()).toBeTruthy();

            //if the company is connected to QBO, we need to hide the connect button
            ModelSvc.model.company.connectedToQbo = true;
            expect(scope.showConnectButton()).toBeFalsy();
        });

        it('should have a functions for disabling the sync buttons', function () {
            expect(scope.disableEmployeeSyncButton).toBeDefined();
            expect(scope.disableCustomersSyncButton).toBeDefined();
            expect(scope.disableServiceItemsSyncButton).toBeDefined();
        });

        it('should disable sync buttons when not connected to QBO ', function () {
            ModelSvc.model.company.connectedToQbo = false;
            ModelSvc.model.company.customersSynced = false;
            ModelSvc.model.company.employeesSynced = false;
            ModelSvc.model.company.serviceItemsSycned = false;
            expect(scope.disableEmployeeSyncButton()).toBeTruthy();
            expect(scope.disableCustomersSyncButton()).toBeTruthy();
            expect(scope.disableServiceItemsSyncButton()).toBeTruthy();
        });

        it('should not disable sync buttons when connected to QBO and not synced yet', function () {
            ModelSvc.model.company.connectedToQbo = true;
            expect(scope.disableEmployeeSyncButton()).toBeFalsy();
            expect(scope.disableCustomersSyncButton()).toBeFalsy();
            expect(scope.disableServiceItemsSyncButton()).toBeFalsy();
        });

        it('should disable sync buttons when connected to QBO and synced', function () {
            ModelSvc.model.company.employeesSynced = true;
            expect(scope.disableEmployeeSyncButton()).toBeTruthy();

            ModelSvc.model.company.customersSynced = true;
            expect(scope.disableCustomersSyncButton()).toBeTruthy();

            ModelSvc.model.company.serviceItemsSycned = true;
            expect(scope.disableServiceItemsSyncButton()).toBeTruthy();
        });

        it('should have a scope variables for storing sync messages', function () {
            expect(scope.syncCustomersMessage).toBeDefined();
            expect(scope.syncServiceItemsMessage).toBeDefined();
            expect(scope.syncEmployeesMessage).toBeDefined();
        });

        it('should have functions for initiating data sync', function () {
            expect(scope.syncCustomers).toBeDefined();
            expect(scope.syncServiceItems).toBeDefined();
            expect(scope.syncEmployees).toBeDefined();
        });


        it('should call the SyncRequestSvc.sendServiceItemsSyncRequest when syncServiceItems is called', function () {
            spyOn(SyncRequestSvc, 'sendServiceItemsSyncRequest');

            scope.syncServiceItems();

            expect(SyncRequestSvc.sendServiceItemsSyncRequest).toHaveBeenCalled();

        });

        it('should call the SyncRequestSvc.sendCustomerSyncRequest when syncCustomers is called', function () {
            spyOn(SyncRequestSvc, 'sendCustomerSyncRequest');

            scope.syncCustomers();

            expect(SyncRequestSvc.sendCustomerSyncRequest).toHaveBeenCalled();

        });

        it('should call the SyncRequestSvc.sendEmployeeSyncRequest when syncEmployees is called', function () {
            spyOn(SyncRequestSvc, 'sendEmployeeSyncRequest');

            scope.syncEmployees();

            expect(SyncRequestSvc.sendEmployeeSyncRequest).toHaveBeenCalledWith(ctrl['syncCompleted']);
        });

        it('should have a private function called syncCompleted that updates the customer sync message', function () {
            expect(ctrl.syncCompleted).toBeDefined();

            spyOn(CompanySvc, 'initializeModel');

            ctrl.syncCompleted({
                successful: true,
                message: "123",
                type: "Customer"
            });

            expect(scope.syncCustomersMessage).toEqual("123");
            expect(CompanySvc.initializeModel).toHaveBeenCalled();
        });

        it('should have a private function called syncCompleted that updates the employee sync message', function () {
            expect(ctrl.syncCompleted).toBeDefined();

            spyOn(CompanySvc, 'initializeModel');

            ctrl.syncCompleted({
                successful: true,
                message: "abc",
                type: "Employee"
            });

            expect(scope.syncEmployeesMessage).toEqual("abc");
            expect(CompanySvc.initializeModel).toHaveBeenCalled();
        });

        it('should have a private function called syncCompleted that updates the service item sync message', function () {
            expect(ctrl.syncCompleted).toBeDefined();

            spyOn(CompanySvc, 'initializeModel');

            ctrl.syncCompleted({
                successful: true,
                message: "987",
                type: "ServiceItem"
            });

            expect(scope.syncServiceItemsMessage).toEqual("987");
            expect(CompanySvc.initializeModel).toHaveBeenCalled();
        });
        
        
    });
});



