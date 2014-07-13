'use strict';

describe('Service: MessageHandler', function () {

  // load the service's module
  beforeEach(module('defineMatchClientApp'));

  // instantiate service
  var MessageHandler;
  beforeEach(inject(function (_MessageHandler_) {
    MessageHandler = _MessageHandler_;
  }));

  it('should do something', function () {
    expect(!!MessageHandler).toBe(true);
  });

});
