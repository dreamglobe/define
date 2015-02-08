package com.kamomileware.define.match.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import com.kamomileware.define.model.round.Round;
import com.kamomileware.define.model.round.RoundPhase;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static com.kamomileware.define.model.MessageTypes.Latch;

/**
 * Base class for Match actor with the FSM definition as seen
 * in <a href="http://doc.akka.io/docs/akka/2.2.3/java/fsm.html">http://doc.akka.io/docs/akka/2.2.3/java/fsm.html</a>
 *
 * @version ${project.version} Created by pepe on 29/06/14.
 */
public abstract class MatchFSM extends UntypedActor{

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private Cancellable latchTimer;
    private long startPhaseTime = 0;

    protected Round<ActorRef> round ;

    private RoundPhase state = RoundPhase.STOPPED;

    protected void init(){
        ;
    }

    abstract protected void transition(RoundPhase oldState, RoundPhase newState);

    protected void setState(RoundPhase s){
        log.info("Changing State to '{}'", s);
        markStartPhaseTime();
        transition(this.state, s);
        if(this.state != s){
            this.state = s;
        }
    }

    protected RoundPhase getState(){
        return this.state;
    }

    private void markStartPhaseTime(){
        this.startPhaseTime = System.currentTimeMillis();
    }

    protected long getPhaseMillisLeft(long phaseMillis){
        long timeleft = (phaseMillis) - (System.currentTimeMillis()-this.startPhaseTime);
        return timeleft < 0 ? 0 : timeleft;
    }

    protected long startLatch(RoundPhase state){
        this.round.resetExtendedState(state);
        final long phaseTotalDuration = this.round.getPhaseDurationInMillis(state);
        final int roundNumber = this.round.getRoundNumber();
        log.info("Starting latch '{}' for '{}' millis", state, phaseTotalDuration);
        final FiniteDuration delay = new FiniteDuration(phaseTotalDuration, TimeUnit.MILLISECONDS);
        this.latchTimer = this.startLatch(roundNumber, delay);
        return phaseTotalDuration;
    }

    protected long startLatchExtend(RoundPhase state){
        final int roundNumber = this.round.getRoundNumber();
        final long phaseExtendedDuration = round.getPhaseDurationInMillis(state);
        log.info("Starting latch '{}' for '{}' millis", state, phaseExtendedDuration);
        final FiniteDuration delay = new FiniteDuration(phaseExtendedDuration, TimeUnit.MILLISECONDS);
        this.latchTimer = this.startLatch(roundNumber, delay);
        return phaseExtendedDuration;
    }

    protected void cancelLatch() {
        if(this.latchTimer!=null) {
            log.info("Cancelling latch for phase '{}' ", state);
            this.latchTimer.cancel();
        }
    }

    private Cancellable startLatch(int seq, FiniteDuration delay) {
        final Latch message = new Latch(seq, delay.toMillis());
        final ActorSystem system = this.context().system();
        return system.scheduler().scheduleOnce(delay, this.self(), message, system.dispatcher(), null);
    }

    protected void switchBehaviour(Procedure<Object> behaviour) {
        this.getContext().unbecome();
        this.getContext().become(behaviour);
    }
}
