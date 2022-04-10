package ru.xdsup.HorseOfAtilla;

import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import ru.xdsup.HorseOfAtilla.core.figures.Coord;

import java.util.Arrays;

public class Utils
{
	static String alphabet = "ABCDEFGH";
	static String horizontal = "87654321";
	public static String toChessNotation(int x, int y){
		char ch = alphabet.charAt(x);
		String coord = ch + "" +(8-y);
		return coord;
	}

	public static Coord fromChessNotation(String str){
		val x = Arrays.asList(ArrayUtils.toObject(alphabet.toCharArray())).indexOf(str.charAt(0));
		val y = Arrays.asList(ArrayUtils.toObject(horizontal.toCharArray())).indexOf(str.charAt(1));
		return new Coord(x, y);
	}

	public static String toChessNotation(Coord coord){
		return toChessNotation(coord.getX(), coord.getY());
	}

	public static String toStrArray(String chessNotation){
		return "\"" + chessNotation.replace("-", "\", \"")+ "\"";
	}

	public static String[] chessNotationToArray(String chessNotation){
		return chessNotation.split("-");
	}

	public static String getFormula(long depth, long countNodes) {
		String x = "";
		for (long i = depth; i != 0; i--)
			x += "x^" + i + "+";
		return x + (-countNodes + 1) + "=0";
	}
}
