package org.udesa.uno.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        return new ResponseEntity<>("Bueenasssss", HttpStatus.OK);
    }

    @PostMapping("newmatch")
    public ResponseEntity newMatch(@RequestParam List<String> players) {
        return ResponseEntity.ok(unoService.newMatch(players));
    }
//
//    @PostMapping("play/{matchId}/{player}")
//    public ResponseEntity play(@PathVariable UUID matchId, @PathVariable String player, @RequestBody JsonCard card) {
//    }
//
//    @PostMapping("draw/{matchId}/{player}")
//    public ResponseEntity drawCard(@PathVariable UUID matchId, @RequestParam String player) {
//    }
//
//    @GetMapping("activecard/{matchId}")
//    public ResponseEntity activeCard(@PathVariable UUID matchId) {
//    }
//
//    @GetMapping("playerhand/{matchId}")
//    public ResponseEntity playerHand(@PathVariable UUID matchId) {
//        return ResponseEntity.ok(unoService.playerHand(matchId).stream().map(each -> each.asJson()));
//    }
}
