package es.lavanda.feed.show.exception;

public class FeedShowsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FeedShowsException(String message, Exception e) {
        super(message, e);
    }

    public FeedShowsException(String message) {
        super(message);
    }

    public FeedShowsException(Exception e) {
        super(e);
    }

}
