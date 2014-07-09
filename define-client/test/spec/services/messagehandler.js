'use strict';

describe('Service: messageHandler', function () {

  // load the service's module
  beforeEach(module('deffineApp'));

  // instantiate service
  var messageHandler;
  beforeEach(inject(function (_messageHandler_) {
    messageHandler = _messageHandler_;
  }));

  it('should do something', function () {
    expect(!!messageHandler).toBe(true);
  });

});
