'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.Console
 * @description
 * # Console
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .service('Console', ['_', function Console(_) {
        var isVisible = false;
        return {
            isVisible: function () {
                return isVisible;
            },
            toggle: function () {
                isVisible = !isVisible;
            },
            actionText: function () {
                return isVisible ? 'Ocultar consola' : 'Mostrar consola';
            },
            logResults: function (responses) {
                var message = 'Results:<ul>';
                _.each(responses, function (r, k) {
                    message += '<li>' + k + ': ' + r + '</li>';
                });
                message += '</ul>';
                this.log(message);
            },
            logUserList: function (users) {
                var message = 'Users:<ul>';
                _.each(users, function (user) {
                    message += '<li>' + user.name + '</li>\n';
                });
                message += '</ul>';
                this.log(message);
            },
            log: function (message) {
                var console = document.getElementById('console');
                var p = document.createElement('p');
                p.style.wordWrap = 'break-word';
                p.innerHTML = message;
                console.appendChild(p);
                while (console.childNodes.length > 25) {
                    console.removeChild(console.firstChild);
                }
                console.scrollTop = console.scrollHeight;
            }
        };
    }]);
