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
            this.started = 0;
            this.millis = 0;
            this.secLeft = 0;
            this.percent = 0;
            this.status = '';
            this.statusChange=0;
            this.timer = null;
            this.timers = {};
        }

        var t = new Timer();

        function tick () {
            t.secLeft--;
            t.timer = $timeout(tick, 1000);
        }

        function progressTick() {
            t.percent = (t.millisLeft() * 100) / t.millis;
            t.timers['progress'] = $timeout(progressTick, 100);
        }

        function changeStatusInfo(){
            t.status = 'progress-bar-info';
            t.timers['status'] = $timeout(changeStatusWarn, t.statusChange);
        }
        function changeStatusWarn(){
            t.status = 'progress-bar-warning';
            t.timers['status'] = $timeout(changeStatusDanger, t.statusChange);
        }
        function changeStatusDanger(){
            t.status = 'progress-bar-danger';
        }

        function calcMillisInSecond(millis) {
            var secondFraction = millis % 1000;
            return secondFraction === 0 ? 1000 : secondFraction;
        }

        Timer.prototype.millisLeft = function(){
            return this.millis - (Date.now() - this.started);
        };

        Timer.prototype.getSeconds = function(){
            return Math.abs(this.secLeft);
        };

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
            this.status='';
            this.statusChange = this.millis/4;
            this.started = Date.now();
            this.secLeft = Math.floor(this.millis / 1000);
            this.timer = $timeout(tick, calcMillisInSecond(this.millis));
            this.timers['progress'] = $timeout(progressTick, 100);
            this.timers['status'] = $timeout(changeStatusInfo, this.statusChange);
            return this;
        };

        Timer.prototype.stop = function () {
            if (this.timer !== null) {
                $timeout.cancel(this.timer);
            }
        };

        Timer.prototype.timeout = function (name, millis, func) {
            this.timers[name] = $timeout(func, millis);
        };

        Timer.prototype.cancelTimeout = function (name) {
            return this.timer[name] && $timeout.cancel(this.timer[name]);
        };

        return t;
    });
