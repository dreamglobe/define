'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.Timer
 * @description
 * # Timer
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .factory('Timer', function (_, $rootScope, $timeout) {
        // AngularJS will instantiate a singleton by calling "new" on this function


        function Timer() {
            this.millis = 0;
            this.counter = 0;
            this.timer = null;
            this.timers = {};
        }

        var t = new Timer();

        function tick () {
            t.counter--;
            t.timer = $timeout(tick, 1000);
        }

        function calcMillisInSecond(millis) {
            var secondFraction = millis % 1000;
            return secondFraction === 0 ? 1000 : secondFraction;
        }

        Timer.prototype.set = function(millis){
            this.millis = millis;
        };

        Timer.prototype.start = function () {
            if (this.timer !== null) {
                $timeout.cancel(this.timer);
            }
            _.each(this.timers, function (t) {
                $timeout.cancel(t);
            });
            this.counter = Math.floor(this.millis / 1000);
            this.timer = $timeout(tick, calcMillisInSecond(this.millis));
            return this;
        };

        Timer.prototype.stop = function () {
            if (this.timer !== null) {
                $timeout.cancel(this.timer);
            }
        };

        Timer.prototype.timeout = function (name, millis, fn) {
            this.timers[name] = $timeout(fn, millis);
        };

        Timer.prototype.cancelTimeout = function (name) {
            return this.timer[name] && $timeout.cancel(this.timer[name]);
        };

        return t;
    });
