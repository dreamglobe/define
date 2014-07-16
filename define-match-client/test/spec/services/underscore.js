'use strict';

describe('Service: underscore', function () {

  // load the service's module
  beforeEach(module('defineMatchClientApp'));

  // instantiate service
  var underscore;
  beforeEach(inject(function (_underscore_) {
    underscore = _underscore_;
  }));

  it('should do something', function () {
    expect(!!underscore).toBe(true);
  });

});
