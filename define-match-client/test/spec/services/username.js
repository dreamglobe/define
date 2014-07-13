'use strict';

describe('Service: username', function () {

  // load the service's module
  beforeEach(module('defineMatchClientApp'));

  // instantiate service
  var username;
  beforeEach(inject(function (_username_) {
    username = _username_;
  }));

  it('should do something', function () {
    expect(!!username).toBe(true);
  });

});
