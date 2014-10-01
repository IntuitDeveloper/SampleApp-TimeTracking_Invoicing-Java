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


        var ctrl, scope, ModelSvc, SyncRequestSvc, CompanySvc, BusyModalSvc;

        beforeEach(module('myApp.controllers'));

        beforeEach(inject(function ($rootScope, $controller, _ModelSvc_, _SyncRequestSvc_, _CompanySvc_, _BusyModalSvc_) {

            //create an empty scope
            scope = $rootScope.$new();

            //declare the controller and inject our empty scope
            ctrl = $controller('SettingsCtrl', {$scope: scope});


            ModelSvc = _ModelSvc_;
            SyncRequestSvc = _SyncRequestSvc_;
            CompanySvc = _CompanySvc_;
            BusyModalSvc = _BusyModalSvc_;

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

        it('should have a private function called syncCompleted that cleans up after a syncing operation', function () {

            scope.busyModal = {foo: "bar"};

            expect(ctrl.syncCompleted).toBeDefined();

            spyOn(CompanySvc, 'initializeModel');
            spyOn(BusyModalSvc, 'closeBusyModal');

            ctrl.syncCompleted({
                successful: true,
                message: "123"
            });

            expect(CompanySvc.initializeModel).toHaveBeenCalled();
            expect(BusyModalSvc.closeBusyModal).toHaveBeenCalledWith(scope.busyModal);
        });

        it('should disable view in qbo buttons when not connected to QBO', function () {
            ModelSvc.model.company.connectedToQbo = false;
            ModelSvc.model.company.customersSynced = false;
            ModelSvc.model.company.employeesSynced = false;
            ModelSvc.model.company.serviceItemsSynced = false;
            expect(scope.disableViewEmployeesInQBOButton()).toBeTruthy();
            expect(scope.disableViewCustomersInQBOButton()).toBeTruthy();
            expect(scope.disableViewItemsInQBOButton()).toBeTruthy();
        });

        it('should disable view in qbo buttons when connected to QBO and not synced yet', function () {
            ModelSvc.model.company.connectedToQbo = true;
            ModelSvc.model.company.customersSynced = false;
            ModelSvc.model.company.employeesSynced = false;
            ModelSvc.model.company.serviceItemsSynced = false;
            expect(scope.disableViewEmployeesInQBOButton()).toBeTruthy();
            expect(scope.disableViewCustomersInQBOButton()).toBeTruthy();
            expect(scope.disableViewItemsInQBOButton()).toBeTruthy();
        });

        it('should not disable view in qbo buttons when connected to QBO and synced', function () {
            ModelSvc.model.company.connectedToQbo = true;

            ModelSvc.model.company.employeesSynced = true;
            expect(scope.disableViewEmployeesInQBOButton()).toBeFalsy();

            ModelSvc.model.company.customersSynced = true;
            expect(scope.disableViewCustomersInQBOButton()).toBeFalsy();

            ModelSvc.model.company.serviceItemsSynced = true;
            expect(scope.disableViewItemsInQBOButton()).toBeFalsy();
        });

        it('should have a scope function called openEmployeesScreenInQBO()', function () {
            expect(scope.openEmployeesScreenInQBO)
        });
    });

    describe('Unit: TimeEntryCtrl', function () {
        var ctrl, scope, $filter, TimeActivitySvc, CompanySvc, ModelSvc;

        beforeEach(module('myApp.controllers'));

        beforeEach(inject(function ($rootScope, $controller, _$filter_, _TimeActivitySvc_, _CompanySvc_, _ModelSvc_) {
            //create an empty scope
            scope = $rootScope.$new();

            //declare the controller and inject our empty scope
            ctrl = $controller('TimeEntryCtrl', {$scope: scope});

            $filter = _$filter_;
            TimeActivitySvc = _TimeActivitySvc_;
            CompanySvc = _CompanySvc_;
            ModelSvc = _ModelSvc_;
        }));

        it('should have a time entry controller defined', function () {
            expect(ctrl).toBeDefined();
        });

        it('should have the model on the scope object', function () {
            expect(scope.model).toBeDefined();
        });

        it('should have a scope variable for the selected employee', function () {
            expect(scope.selectedEmployee).toBeDefined();
        });

        it('should have a scope variable for the selected customer', function () {
            expect(scope.selectedCustomer).toBeDefined();
        });

        it('should have a scope variable for the selected service item', function () {
            expect(scope.selectedServiceItem).toBeDefined();
        });

        it('should have a scope variable for the description', function () {
            expect(scope.description).toBeDefined();
        });

        it('should have a scope variable for the selected date', function () {
            expect(scope.selectedDate).toBeDefined();
        });

        it('should have a scope variable for the selected duration', function () {
            expect(scope.selectedDuration).toBeDefined();
        });

        it('should have a scope variable to determine whether to show the alert bar', function () {
            expect(scope.showAlert).toBeDefined();
        });

        it('should have a scope variable to determine whether to show the alert bar', function () {
            expect(scope.alertMessage).toBeDefined();
        });

        it('should have a scope variable called datePickerOpen', function () {
            expect(scope.datePickerOpened).toBeDefined();
        });

        it('should have a function called saveTimeActivity which calls the TimeActivitySvc', function () {
            expect(scope.saveTimeActivity).toBeDefined();

            spyOn(TimeActivitySvc, 'createTimeActivity');
            spyOn(scope, 'clearTimeActivity')

            var selectedServiceItemHref = "http://localhost:9001/serviceItems/1";
            var selectedEmployeeHref = "http://localhost:9001/employees/1";
            var selectedCustomerHref = "http://localhost:9001/employees/1";
            var selectedCompanyHref = "http://localhost:9001/companies/1";

            scope.selectedServiceItem = {
                _links: {
                    self: {
                        href: selectedServiceItemHref
                    }
                }
            };

            scope.selectedEmployee = {
                _links: {
                    self: {
                        href: selectedEmployeeHref
                    }
                }
            };

            scope.selectedCustomer = {
                _links: {
                    self: {
                        href: selectedCustomerHref
                    }
                }
            };

            ModelSvc.model.company = {
                _links: {
                    self: {
                        href: selectedCompanyHref
                    }
                }
            };

            scope.selectedDuration = new Date();
            scope.selectedDuration.setHours(1);
            scope.selectedDuration.setMinutes(30);

            scope.selectedDate = new Date();
            scope.selectedDate.setDate(4);
            scope.selectedDate.setMonth(8);
            scope.selectedDate.setYear(2014);

            scope.description = 'How now brown cow?';

            scope.saveTimeActivity();

            expect(TimeActivitySvc.createTimeActivity).toHaveBeenCalledWith({
                minutes: 90,
                date: '2014-09-04',
                description: 'How now brown cow?',
                serviceItem: selectedServiceItemHref,
                employee: selectedEmployeeHref,
                customer: selectedCustomerHref,
                company: selectedCompanyHref
            }, ctrl.showSuccessfulAlert);

            expect(scope.clearTimeActivity).toHaveBeenCalled();
        });

        it('should have a function called clearTimeActivity that resets the time activity scope variables', function () {

            expect(scope.clearTimeActivity).toBeDefined();

            scope.selectedDate = 'foo';
            scope.selectedDuration = new Date();
            scope.selectedDuration.setHours(1);
            scope.selectedDuration.setMinutes(30);
            scope.selectedEmployee = 'bar';
            scope.seletedCustomer = 'abc';
            scope.selectedServiceItem = 'xyz';
            scope.description = 'Who watches the water?';

            scope.clearTimeActivity();

            expect(scope.selectedDate).toBeNull();
            expect(scope.selectedDuration.getHours()).toEqual(0);
            expect(scope.selectedDuration.getMinutes()).toEqual(15);
            expect(scope.selectedEmployee).toBeNull();
            expect(scope.selectedCustomer).toBeNull();
            expect(scope.selectedServiceItem).toBeNull();
            expect(scope.description).toBeNull();
        });

        it('should have a function called openDatePicker', function () {
            scope.opened = false;

            expect(scope.openDatePicker).toBeDefined();

            var fakeEvent = {
                preventDefault: function () {

                },
                stopPropagation: function () {

                }
            };

            spyOn(fakeEvent, 'preventDefault');
            spyOn(fakeEvent, 'stopPropagation');

            scope.openDatePicker(fakeEvent);

            expect(fakeEvent.preventDefault).toHaveBeenCalled();
            expect(fakeEvent.stopPropagation).toHaveBeenCalled();
            expect(scope.datePickerOpened).toBeTruthy();

        });

        it('should have a function called timeChanged that doesnt allow a selectedDuration of 0:00', function () {
            scope.selectedDuration = new Date();
            scope.selectedDuration.setHours(0);
            scope.selectedDuration.setMinutes(0);

            spyOn(ctrl, 'resetSelectedDuration').andCallThrough();

            scope.timeChanged();

            expect(ctrl.resetSelectedDuration).toHaveBeenCalled();

            expect(scope.selectedDuration.getHours()).toEqual(0);
            expect(scope.selectedDuration.getMinutes()).toEqual(15);
        });
    });

    describe('Unit: InvoiceCtrl', function () {
        var ctrl, scope, InvoiceSvc, ModelSvc, BusyModalSvc;

        beforeEach(module('myApp.controllers'));

        beforeEach(inject(function ($rootScope, $controller, _InvoiceSvc_, _ModelSvc_, _BusyModalSvc_) {
            //create an empty scope
            scope = $rootScope.$new();

            ModelSvc = _ModelSvc_;
            InvoiceSvc = _InvoiceSvc_;
            BusyModalSvc = _BusyModalSvc_;


            //declare the controller and inject our empty scope
            ctrl = $controller('InvoiceCtrl', {$scope: scope});

        }));

        it('should have a invoice controller defined', function () {
            expect(ctrl).toBeDefined();
        });

        it('should have the model on the scope object', function () {
            expect(scope.model).toBeDefined();
        });

        it('should have a scope variable to determine whether to show the alert bar', function () {
            expect(scope.showAlert).toBeDefined();
        });

        it('should have a scope variable to determine whether to show the alert bar', function () {
            expect(scope.alertMessage).toBeDefined();
        });

        it('should have a expandServiceItemSummary function', function () {
            expect(scope.expandServiceItemSummary).toBeDefined();

            var invoice = {};
            scope.expandServiceItemSummary(invoice);
            expect(invoice.showServiceItemSummaries).toBeTruthy();

            invoice = {showServiceItemSummaries: true};
            scope.expandServiceItemSummary(invoice);
            expect(invoice.showServiceItemSummaries).toBeFalsy();
        });

        it('should have a showSuccessfulAlert function', function () {

            expect(scope.showAlert).toBeFalsy();

            scope.busyModal = {foo: "bar"};
            spyOn(BusyModalSvc, 'closeBusyModal');

            var qboId = 1234;
            ctrl.showSuccessfulAlert({qboId: qboId});

            expect(scope.alertMessage).toEqual("Invoice successfully created and pushed to QBO (QBO ID = " + qboId + ")");
            expect(scope.showAlert).toBeTruthy();
            expect(BusyModalSvc.closeBusyModal).toHaveBeenCalledWith(scope.busyModal);
        });
    });
});



