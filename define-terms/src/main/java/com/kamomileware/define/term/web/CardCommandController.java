package com.kamomileware.define.term.web;

import com.kamomileware.define.model.term.TermCard;
import com.kamomileware.define.term.repository.TermCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by pepe on 5/08/14.
 */
@Controller
@RequestMapping("/cards")
public class CardCommandController {

    @Autowired
    TermCardRepository cardDao;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TermCard> createOrder(@RequestBody TermCard card, UriComponentsBuilder builder) {
        final TermCard termCard = cardDao.addNewCard(card);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                builder.path("/cards/{order}")
                    .buildAndExpand(Long.toString(termCard.getOrder())).toUri()
        );

        return new ResponseEntity<>(termCard, headers, HttpStatus.CREATED);
    }
}
