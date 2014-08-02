'use strict';

/**
 * @ngdoc service
 * @name defineMatchClientApp.score
 * @description
 * # score
 * Service in the defineMatchClientApp.
 */
angular.module('defineMatchClientApp')
    .factory('Score', ['_', 'Player', 'Definition', function (_, Player, Definition) {

        var playerScores = [];
        var correctDefinition;
        var numCorrectVotes;

        function Score(pid, defId, voteScore, turnScore, totalScore, pidVoters, isCorrectDefinition) {
            this.pid = pid;
            this.defId = defId;
            this.voteScore = voteScore;
            this.turnScore = turnScore;
            this.totalScore = totalScore;
            this.pidVoters = pidVoters;
            this.isCorrectDefinition = isCorrectDefinition;

            this.player = Player.get(pid);
            this.player.score = this;
            this.player.consolidateScores();
            this.definition = Definition.get(defId);
        }

        Score.prototype.getVoters = function () {
            return _.map(this.pidVoters, function (pid) {
                return Player.get(pid).name;
            });
        };

        Score.createList = function (result) {
            playerScores = [];
            _.each(result.scores, function (s) {
                playerScores.push(
                    new Score(s.pid,
                        s.defId,
                        s.voteScore,
                        s.turnScore,
                        s.totalScore,
                        s.pidVoters,
                        s.correctDefinition));
            });
            correctDefinition = result.correctDef;
            numCorrectVotes = result.numCorrectVotes;
            return playerScores;
        };

        Score.addScoreForRegisterUser = function (pid) {
            playerScores.push(
                new Score(pid,
                    -1,
                    0,
                    0,
                    0,
                    [],
                    false));
        };

        Score.removeScoreForRemoveUser = function (pid) {
            var player = Player.get(pid);
            var pos = playerScores.indexOf(player.score);
            if(pos>=0){
                playerScores.splice(pos,1);
            }
        };


        Score.getList = function () {
            return playerScores;
        };

        Score.getCorrectDefinition = function () {
            return correctDefinition;
        };

        Score.getNumCorrectVotes = function () {
            return numCorrectVotes;
        };

        return Score;
    }]);
