'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.Timer
 * @description
 * # Timer
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .service('Timer', function Timer($timeout) {
        // AngularJS will instantiate a singleton by calling "new" on this function
        var counter = 0;
        var timer;
        var timers = {};

        function tick() {
            counter--;
            timer = $timeout(tick, 1000);
        }

        function calcMillisInSecond(millis) {
            var secondFraction = millis % 1000;
            return secondFraction == 0 ? 1000 : secondFraction;
        }

        return {
            start: function (millis) {
                if (timer != null) {
                    $timeout.cancel(timer);
                }
                counter = Math.floor(millis/1000);
                timer = $timeout(tick, calcMillisInSecond(millis));
            },
            stop: function () {
                if (timer != null) {
                    $timeout.cancel(timer);
                }
            },
            get: function () {
                return counter;
            },
            timeout: function (name, millis, fn) {
                timers[name] = $timeout(millis, fn);
            },
            cancelTimeout: function(name){
                return timer[name] && $timeout.cancel(timer[name]);
            }
        };
    });
