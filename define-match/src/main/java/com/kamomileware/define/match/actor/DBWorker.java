package com.kamomileware.define.match.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kamomileware.define.model.MessageTypes;
import com.kamomileware.define.model.term.TermCard;
import com.kamomileware.define.term.repository.TermCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.kamomileware.define.model.MessageTypes.DBGetCards;

/**
 * Created by pepe on 31/08/14.
 */
@Service
@Component("workerRouter")
@Scope("prototype")
public class DBWorker extends UntypedActor{

    Random random = new Random();

    @Autowired
    TermCardRepository cardDao;

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof DBGetCards){
            DBGetCards cardMsg = (DBGetCards) message;
            List<TermCard> result = getTermCards(cardMsg);
            getSender().tell(new MessageTypes.DBCards(result), getSelf());
        } else {
            this.unhandled(message);
        }
    }

    private List<TermCard> getTermCards(DBGetCards cardMsg) {
        final int cardNbr = cardMsg.getNumber();
        final Set<Integer> blacklisted = cardMsg.getBlacklisted();
        int  max = (int) cardDao.count();
        final List<String> ids = blacklisted.isEmpty()?
                generateRandomInts(cardNbr, max)
                :generateRandomInts(cardNbr, max, blacklisted);
        final Iterable<TermCard> all = cardDao.findAll(ids);
        return Lists.newArrayList(all);
    }

    private List<String> generateRandomInts(int cardNbr, int max, Set<Integer> blacklisted) {
        List<String> ids = new ArrayList<>(cardNbr);
        while(ids.size()< cardNbr){
            int value = random.nextInt(max)+1;
            if(blacklisted.contains(value)) continue;
            ids.add(String.valueOf(value));
        }
        return ids;
    }

    private List<String> generateRandomInts(int cardNbr, int max) {
        List<String> ids = new ArrayList<>(cardNbr);
        while(ids.size()< cardNbr){
            int value = random.nextInt(max)+1;
            ids.add(String.valueOf(value));
        }
        return ids;
    }

    public static Props props() {
        return Props.create(DBWorker.class);
    }
}
