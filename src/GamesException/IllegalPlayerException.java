package GamesException;

public class IllegalPlayerException extends Exception {
    public IllegalPlayerException(){super("Illegal parameters.");}
    public IllegalPlayerException(String e){super(e);}
}
