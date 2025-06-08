package org.udesa.uno.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.udesa.uno.model.Card;
import org.udesa.uno.model.JsonCard;
import org.udesa.uno.service.UnoService;

import java.util.List;
import java.util.UUID;

@Controller// Es el
public class UnoController {
    @Autowired
    UnoService unoService;

//    @GetMapping("/")
//    public String saludo() {
//        return "index";
//    }

    @GetMapping("/hola")
    public ResponseEntity<String> holaMundo(){
        return new ResponseEntity<>("Bueenasssss \n", HttpStatus.OK);
    }

    @PostMapping("newmatch")
    public ResponseEntity newMatch(@RequestParam List<String> players) {
        return ResponseEntity.ok(unoService.newMatch(players));
    }

    @PostMapping("play/{matchId}/{player}")
    public ResponseEntity<Void> play(@PathVariable UUID matchId, @PathVariable String player, @RequestBody JsonCard card) {
        unoService.play(matchId, player, card);
        return ResponseEntity.ok().build();
    }

    @PostMapping("draw/{matchId}/{player}")
    public ResponseEntity drawCard(@PathVariable UUID matchId, @PathVariable String player) {
        unoService.drawCard(matchId, player);
        return ResponseEntity.ok("\n");
    }

    @GetMapping("activecard/{matchId}")
    public ResponseEntity activeCard(@PathVariable UUID matchId) {
        return ResponseEntity.ok(unoService.activeCard(matchId).asJson());
    }

    @GetMapping("playerhand/{matchId}")
    public ResponseEntity playerHand(@PathVariable UUID matchId) {
        return ResponseEntity.ok(unoService.playerHand(matchId).stream().map(Card::asJson));
    }
}
