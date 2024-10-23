package name.robertburrelldonkin.library.services;

public class NoAvailableCopiesException extends RuntimeException {
    public NoAvailableCopiesException() {
        super("No copies available");
    }
}
