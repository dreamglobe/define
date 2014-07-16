'use strict';

describe('Directive: ngbkFocus', function () {

  // load the directive's module
  beforeEach(module('defineMatchClientApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<ngbk-focus></ngbk-focus>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the ngbkFocus directive');
  }));
});
