package com.kamomileware.define.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
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

    private Cancellable latchTimer;
    private int counter = 1;
    private long startPhaseTime = 0;

    protected Round<ActorRef> round ;

    private RoundPhase state = RoundPhase.STOPPED;

    protected void init(){
        ;
    }

    abstract protected void transition(RoundPhase oldState, RoundPhase newState);

    protected void setState(RoundPhase s){
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

    protected long getPhaseMillisLeft(long phaseSeconds){
        long timeleft = (phaseSeconds*1000) - (System.currentTimeMillis()-this.startPhaseTime);
        return timeleft < 0 ? 0 : timeleft;
    }

    protected int startLatch(){
        this.latchTimer = this.startLatch(this.round.getRoundNumber(), new FiniteDuration(round.getPhaseTotalDuration(state),TimeUnit.SECONDS));
        return this.counter;
    }

    protected int startLatchExtend(){
        this.latchTimer = this.startLatch(this.round.getRoundNumber(),
                new FiniteDuration(round.getPhaseTotalDuration(state),TimeUnit.SECONDS));
        return this.counter;
    }

    protected void cancelLatch() {
        this.latchTimer.cancel();
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
