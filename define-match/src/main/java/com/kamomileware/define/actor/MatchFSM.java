package com.kamomileware.define.actor;

import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.japi.Procedure;
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

    protected static final FiniteDuration PHASE_DURATION = new FiniteDuration(61, TimeUnit.SECONDS);
    protected static final FiniteDuration EXTEND_DURATION = new FiniteDuration(31, TimeUnit.SECONDS);
    private Cancellable latchTimer;
    private int counter = 1;
    private long startPhaseTime = 0;


    protected enum State { STOPPED, PHASE_RESPONSE, PHASE_VOTE, PHASE_RESULT; }

    private State state = State.STOPPED;

    protected void init(){
        ;
    }

    protected void setState(State s){
        transition(this.state, s);
        markStartPhaseTime();
        if(this.state != s){
            this.state = s;
        }
    }

/*
    protected State getState(){
        return this.state;
    }
*/

    abstract protected void transition(State oldState, State newState);


    private void markStartPhaseTime(){
        this.startPhaseTime = System.currentTimeMillis();
    }

    protected long getPhaseMillisLeft(FiniteDuration duration){
        long timeleft = duration.toMillis() - (System.currentTimeMillis()-this.startPhaseTime);
        return timeleft < 0? 0 : timeleft;
    }

    protected int startLatch(){
        this.latchTimer = this.startLatch(++this.counter, this.PHASE_DURATION);
        return this.counter;
    }

    protected int startLatchExtend(){
        this.latchTimer = this.startLatch(this.counter, this.EXTEND_DURATION);
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
