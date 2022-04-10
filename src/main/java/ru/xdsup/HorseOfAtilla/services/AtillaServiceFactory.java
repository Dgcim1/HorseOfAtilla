package ru.xdsup.HorseOfAtilla.services;

import lombok.extern.log4j.Log4j2;
import lombok.var;
import org.springframework.stereotype.Service;
import ru.xdsup.HorseOfAtilla.core.figures.Coord;

@Service
@Log4j2
public class AtillaServiceFactory {
    public AtillaSolverService getQueueService(){
        return new AtillaSolverWithQueueService();
    }

    public AtillaSolverService getStackService(int maxCountSteps){
        return new AtillaSolverWithStackService(maxCountSteps);
    }

    public static final int maxSize = 500;
    public static final int KING_NOT_DEFEAT_POINT = 100;

    public AtillaSolverService getHeuristicsServiceManhattan(){
        // эвристика манхэттенское расстояние
        return new AtillaSolverWithHeuristicsService(maxSize, board -> {
            Coord start = board.getKnight().getCoords();
            Coord end = board.isKingDefeated()
                    ? board.getStartPosition()
                    : board.getKing().getCoords();
            int length = Math.abs(start.getX() - end.getX()) + Math.abs(start.getY() - end.getY());
            int kingDefeatedPoints = board.isKingDefeated() ? 0 : KING_NOT_DEFEAT_POINT;
            int result = board.getMoveCount() + length + kingDefeatedPoints;
            return result;
        });
    }

    public AtillaSolverService getHeuristicsServiceHorse(){
        // эвристика "Метрика коня" http://kvant.mccme.ru/1981/10/metrika_konya.htm
        return new AtillaSolverWithHeuristicsService(maxSize, board -> {
            Coord start = board.getKnight().getCoords();
            Coord end = board.isKingDefeated()
                    ? board.getStartPosition()
                    : board.getKing().getCoords();
            // TODO: доделать
            var range = start.getHorseRange(end);
            range += board.isKingDefeated() ? 0 : KING_NOT_DEFEAT_POINT;
            return range;
        });
    }
}
