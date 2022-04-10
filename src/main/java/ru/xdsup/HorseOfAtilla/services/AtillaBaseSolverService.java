package ru.xdsup.HorseOfAtilla.services;

import java.util.function.Predicate;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ru.xdsup.HorseOfAtilla.Utils;
import ru.xdsup.HorseOfAtilla.core.Board;
import ru.xdsup.HorseOfAtilla.core.Statistic;
import ru.xdsup.HorseOfAtilla.core.figures.Coord;
import ru.xdsup.HorseOfAtilla.core.figures.Knight;

@Log4j2
public abstract class AtillaBaseSolverService implements AtillaSolverService
{
	@Getter
	Statistic statistic;

	@Override
	public String analyze(Board initialState)
	{
		statistic = new Statistic();
		//1 - помещаем в список открытых вершин нач сост
		addPotentialState(initialState);
		Board potentialState;
		//2 цикл поиска
		do
		{
			//2.1 извлекаем вершину из поиска и удаляем ее (2.3)
			potentialState = getPotentialState();
			statistic.statesHandled ++;
			if(statistic.statesHandled  % 1000 == 0)
				log.info("Обработано " + statistic.statesHandled  + " вершин");
			//2.2 проверяем на соответст целевому состоянию
			if(isEndState(potentialState)){
					log.info("Решение найдено! Обработано " + statistic.statesHandled  + " вершин");
					statistic.moves = potentialState.getMoveCount();
					statistic.averageBranchingFactor = 1 + (statistic.statesHandled-1.)/statistic.statesGenerate;
					statistic.formula = Utils.getFormula(statistic.moves, statistic.statesHandled);
					return potentialState.toString();
			}
			//2.3 помещаем в список закрытых вершин (в нашем случае просто удаляем из списка открытых)
			//2.4 процедура раскрытия вершины
			generatePotentialStates(potentialState);
		} while (getCountPotentialStates() > 0);
		//3 решения нет
		log.info("решения нет");
		return null;
	}

	/** Возвращает следующее состояние из поиска (списка открытых вершин) и удаляет ее.
	 * @return Удаленное состояние.
	 */
	protected abstract Board getPotentialState();

	protected boolean isEndState(Board state){
		return state.isEndState();
	}

	protected void generatePotentialStates(Board board)
	{
		Knight.getAvailablePoints(board.getKnight().getCoords()).stream()
				.filter(getCoordPredicate(board))
				.map(board::moveKhignt)
				.forEach(this::addPotentialStateWithStatictic);
	}

	private void addPotentialStateWithStatictic(Board board){
		statistic.statesGenerate++;
		addPotentialState(board);
	}

	/** Добавляет новое состояние в список открытых вершин.
	 * @param board Новое состояние.
	 */
	protected abstract void addPotentialState(Board board);

	/** Возвращает количество вершин в списке открытых вершин.
	 * @return Количество вершин в списке открытых вершин.
	 */
	protected abstract int getCountPotentialStates();



	protected Predicate<Coord> getCoordPredicate(Board board)
	{
		return p->  board.isAvailableMove(p);
	}
}
