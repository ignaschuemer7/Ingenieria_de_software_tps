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
import java.util.stream.Stream;

@Controller// Es el
public class UnoController {
    @Autowired
    UnoService unoService;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "Error: " + exception.getMessage() );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegal(IllegalArgumentException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( "Error: " + exception.getMessage() );
    }

    @PostMapping("newmatch")
    public ResponseEntity<UUID> newMatch(@RequestParam List<String> players) {
        return ResponseEntity.ok(unoService.newMatch(players));
    }

    @PostMapping("play/{matchId}/{player}")
    public ResponseEntity<Void> play(@PathVariable UUID matchId, @PathVariable String player, @RequestBody JsonCard card) {
        unoService.play(matchId, player, card);
        return ResponseEntity.ok().build();
    }

    @PostMapping("draw/{matchId}/{player}")
    public ResponseEntity<Void> drawCard(@PathVariable UUID matchId, @PathVariable String player) {
        unoService.drawCard(matchId, player);
        return ResponseEntity.ok().build();
    }

    @GetMapping("activecard/{matchId}")
    public ResponseEntity<JsonCard> activeCard(@PathVariable UUID matchId) {
        return ResponseEntity.ok(unoService.activeCard(matchId).asJson());
    }

    @GetMapping("playerhand/{matchId}")
    public ResponseEntity<Stream<JsonCard>> playerHand(@PathVariable UUID matchId) {
        return ResponseEntity.ok(unoService.playerHand(matchId).stream().map(Card::asJson));
    }
}
