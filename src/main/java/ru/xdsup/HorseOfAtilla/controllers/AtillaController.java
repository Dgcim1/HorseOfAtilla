package ru.xdsup.HorseOfAtilla.controllers;

import lombok.val;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.xdsup.HorseOfAtilla.Utils;
import ru.xdsup.HorseOfAtilla.core.Board;
import ru.xdsup.HorseOfAtilla.core.figures.King;
import ru.xdsup.HorseOfAtilla.core.figures.Knight;
import ru.xdsup.HorseOfAtilla.dto.AtillaRequest;
import ru.xdsup.HorseOfAtilla.dto.AtillaResponse;
import ru.xdsup.HorseOfAtilla.services.AtillaServiceFactory;

import java.util.Arrays;
import java.util.stream.Collectors;
import ru.xdsup.HorseOfAtilla.services.AtillaSolverService;

@Controller
public class AtillaController {

    private final AtillaServiceFactory factory;

    public AtillaController(AtillaServiceFactory factory) {
        this.factory = factory;
    }

    @GetMapping
    public String get(){
        return "index";
    }

    @PostMapping(value = "/atilla", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AtillaResponse> simple(@RequestBody AtillaRequest request) {
        Board board = new Board();
        board.setStartPosition(Utils.fromChessNotation(request.getHorse()));
        board.setKing(new King(Utils.fromChessNotation(request.getKing())));
        board.setKnight(new Knight(Utils.fromChessNotation(request.getHorse())));
        board.setFires(Arrays.stream(request.getFire())
                .map(Utils::fromChessNotation)
                .collect(Collectors.toSet()));
        AtillaSolverService service = "queue".equals(request.getFindType())
                ? factory.getQueueService()
                : "heuristic".equals(request.getFindType())
                ? factory.getHeuristicsServiceManhattan()
                : "heuristic3".equals(request.getFindType())
                ? factory.getHeuristicsServiceHorse()
                : factory.getStackService(request.getMaxMoveCount());
        val response = new AtillaResponse();
        response.setKing(request.getKing());
        response.setFire(request.getFire());
        val stopwatch = new StopWatch();
        stopwatch.start();
        String resolve = service.analyze(board);
        stopwatch.stop();
        val statistic = service.getStatistic();
        Boolean isResolved = resolve != null;
        statistic.nanoTime = stopwatch.getNanoTime();
        if(isResolved) response.setPath(Utils.chessNotationToArray(resolve));
        response.setIsResolved(isResolved);
        response.setStatistic(statistic);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
