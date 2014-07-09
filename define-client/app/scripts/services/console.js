'use strict';

/**
 * @ngdoc service
 * @name deffineApp.Console
 * @description
 * # Console
 * Service in the deffineApp.
 */
angular.module('deffineApp')
  .service('Console', function Console($rootScope) {
        var isVisible = false;
    return {
        isVisible: function(){
            return isVisible;
        },
        toggle: function(){
            isVisible = !isVisible;
        },
        actionText: function(){
            return isVisible? 'Ocultar consola':'Mostrar consola';
        },
        log: function(message){
            var console = document.getElementById('console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.innerHTML = message;
            console.appendChild(p);
            while (console.childNodes.length > 25) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        },
        logResults: function(responses){
            this.log('Results:');
            for(var key in responses){
                this.log($rootScope.userNames[key] + ': ' + responses[key]);
            }
        },
        logUserList: function(users){
            this.log('Users:\n');
            var list = '<ul>';
            for(var userKey in users){
                list += '<li>'+ $rootScope.userNames[userKey] + '</li>\n';
            }
            list += '</ul>'
            this.log(list);
        }
    };
  });
