'use strict';

describe('Service: Definition', function () {

  // load the service's module
  beforeEach(module('defineMatchClientApp'));

  // instantiate service
  var Definition;
  beforeEach(inject(function (_Definition_) {
    Definition = _Definition_;
  }));

  it('should do something', function () {
    expect(!!Definition).toBe(true);
  });

});
