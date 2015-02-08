'use strict';

describe('Filter: pages', function () {

  // load the filter's module
  beforeEach(module('defineTermsClientApp'));

  // initialize a new instance of the filter before each test
  var pages;
  beforeEach(inject(function ($filter) {
    pages = $filter('pages');
  }));

  it('should return the input prefixed with "pages filter:"', function () {


  });

});
