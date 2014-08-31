package com.kamomileware.define.term.web;

import com.kamomileware.define.model.term.TermCard;
import com.kamomileware.define.term.repository.TermCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;

/**
 * Created by pepe on 5/08/14.
 */
@Controller
@RequestMapping("/card")
public class CardCommandController {

    @Autowired
    TermCardRepository cardDao;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TermCard> createOrder(@RequestBody TermCard card, UriComponentsBuilder builder) {
        final TermCard termCard = cardDao.addNew(card);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                builder.path("/cards/{order}")
                    .buildAndExpand(Long.toString(termCard.getOrder())).toUri()
        );

        return new ResponseEntity<>(termCard, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/duplicates", method = RequestMethod.GET)
    public ResponseEntity<Set<String>> updateOrder(UriComponentsBuilder builder) {
        final Set<String> duplicates = cardDao.findDuplicateTerms();
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(duplicates, headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/{cardOrder}", method = RequestMethod.POST)
    public ResponseEntity<TermCard> updateOrder(@PathVariable("cardOrder") Long order, @RequestBody TermCard card, UriComponentsBuilder builder) {
        if(!order.equals(card.getOrder())){
            return new ResponseEntity<>(card, null, HttpStatus.BAD_REQUEST);
        }
        final TermCard termCard = cardDao.updateWithTerms(card);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                builder.path("/cards/{order}")
                        .buildAndExpand(Long.toString(termCard.getOrder())).toUri()
        );

        return new ResponseEntity<>(termCard, headers, HttpStatus.CREATED);
    }
}
