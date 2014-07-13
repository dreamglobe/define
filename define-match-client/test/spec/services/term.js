'use strict';

describe('Service: Term', function () {

  // load the service's module
  beforeEach(module('defineMatchClientApp'));

  // instantiate service
  var Term;
  beforeEach(inject(function (_Term_) {
    Term = _Term_;
  }));

  it('should do something', function () {
    expect(!!Term).toBe(true);
  });

});
