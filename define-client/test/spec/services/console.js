'use strict';

describe('Service: Console', function () {

  // load the service's module
  beforeEach(module('deffineApp'));

  // instantiate service
  var Console;
  beforeEach(inject(function (_Console_) {
    Console = _Console_;
  }));

  it('should do something', function () {
    expect(!!Console).toBe(true);
  });

});
