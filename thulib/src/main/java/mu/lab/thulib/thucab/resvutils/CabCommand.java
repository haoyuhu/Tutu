package mu.lab.thulib.thucab.resvutils;

/**
 * Cab command
 * Created by coderhuhy on 15/11/15.
 */
public interface CabCommand {

    void executeCommand() throws Exception;

    class CabCommandException extends Exception {

        String details;

        public CabCommandException(String details) {
            super(details);
            this.details = details;
        }

        public String getDetails() {
            return details;
        }
    }

}
