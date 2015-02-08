'use strict';

describe('Controller: DbctrlCtrl', function () {

  // load the controller's module
  beforeEach(module('defineTermsClientApp'));

  var DbctrlCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    DbctrlCtrl = $controller('DbCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {

  });
});
