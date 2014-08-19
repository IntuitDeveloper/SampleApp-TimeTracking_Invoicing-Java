'use strict';

/* Controllers */

var controllersModule = angular.module('myApp.controllers', ['ngRoute', 'ui.bootstrap', 'ui.validate', 'myApp.services']);

controllersModule.controller('NavCtrl', ['$scope', '$routeParams', '$location', 'ModelSvc',
    function ($scope, $routeParams, $location, ModelSvc) {
        $scope.navClass = function (page) {
            var currentRoute = $location.path().substring(1);
            return page === currentRoute ? 'active' : '';
        };

        $scope.model = ModelSvc.model;
    }]);


controllersModule.controller('SetupCtrl', ['$scope', '$log', '$modal',
    'ModelSvc', 'RoleSvc',
    function ($scope, $log, $modal, ModelSvc, RoleSvc) {

        $scope.model = ModelSvc.model;

        var openRoleModal = function (create, role) {
            var modalInstance = $modal.open({
                templateUrl: 'partials/roleModalContent.html',
                controller: 'RoleModalInstanceCtrl',
                size: 'lg',
                resolve: {
                    create: function () {
                        return create;
                    },
                    role: function () {
                        return role;
                    }
                }
            });

            modalInstance.result.then(function (roleFromModal) {
                if (create) {
                    RoleSvc.createRole(roleFromModal);
                } else {
                    angular.copy(roleFromModal, role);
                    RoleSvc.saveRole(roleFromModal);
                }
            });
        }


        $scope.addRole = function () {
            openRoleModal(true);
        }

        $scope.editRole = function (role) {
            openRoleModal(false, role);
        }

        $scope.deleteRole = function(role) {
            RoleSvc.deleteRole(role);
        }

    }]);

controllersModule.controller('RoleModalInstanceCtrl',
    ['$scope', '$modalInstance', 'RoleSvc', 'create', 'role',
        function ($scope, $modalInstance, RoleSvc, create, role) {

            $scope.create = create;
            $scope.modeStr = create ? "Create" : "Edit";
            $scope.okButtonStr = create ? "Create" : "Update";
            $scope.tempRole = create ? {} : angular.copy(role);

            $scope.ok = function () {
                $modalInstance.close($scope.tempRole);
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            $scope.notDuplicateRoleName = function(name) {
                var result = RoleSvc.duplicateRoleName(name, role);
                return  !result;
            }

        }]);

controllersModule.controller('PreferencesCtrl',
    ['$scope', 'ModelSvc',
        function ($scope, ModelSvc) {

            $scope.model = ModelSvc.model;

            $scope.showConnectButton = function() {
                return $scope.model.company.connectedToQbo === false;
            }
}]);
