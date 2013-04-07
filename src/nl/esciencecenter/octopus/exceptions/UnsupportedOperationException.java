package nl.esciencecenter.octopus.exceptions;

import java.net.URI;

public class UnsupportedOperationException extends OctopusException {

    private static final long serialVersionUID = 1L;

    public UnsupportedOperationException(String s, String adaptorName, URI uri) {
        super(s, adaptorName, uri);
    }

    public UnsupportedOperationException(String message, Throwable t, String adaptorName, URI uri) {
        super(message, t, adaptorName, uri);
    }
}
