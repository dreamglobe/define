'use strict';

describe('Controller: StoppedCtrl', function () {

  // load the controller's module
  beforeEach(module('deffineApp'));

  var StoppedCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    StoppedCtrl = $controller('StoppedCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
