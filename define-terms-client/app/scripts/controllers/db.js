'use strict';

/**
 * @ngdoc function
 * @name defineTermsClientApp.controller:DbCtrl
 * @description
 * # DbCtrl
 * Controller of the defineTermsClientApp
 */
angular.module('defineTermsClientApp')
  .controller('DbCtrl', [ '$scope', '$http', '$alert',
        function ($scope, $http, $alert) {

            $scope.document = {};
            $scope.setTitle = function (fileInput) {
                var file = fileInput.value;
                var filename = file.replace(/^.*[\\\/]/, '');
                var title = filename;
                $("#title").val(title);
                $("#title").focus();
                $scope.document.title = title;
            };

            $scope.loading = false;
            $scope.uploadFile = function () {
                var formData = new FormData();
                formData.append("file", file.files[0]);
                $scope.loading = true;
                $http({
                    method: 'POST',
                    url: $scope.server + $scope.importPath,
                    headers: {'Content-Type': undefined},
                    data: formData,
                    transformRequest: function (data, headersGetterFunction) {
                        return data;
                    }
                }).success(function (data, status) {
                    $scope.loading = false;
                    $alert({title: 'DB Import!', content: 'Importación realizada con exito.',
                        placement: 'top', type: 'success', show: true});
                }).error(function (data, status) {
                    $scope.loading = false;
                    $alert({title: 'DB Import!', content: 'No se ha realizado la importación',
                        placement: 'top', type: 'alert', show: true});
                });
            };
        }]);