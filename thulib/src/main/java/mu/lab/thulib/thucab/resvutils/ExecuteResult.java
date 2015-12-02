package mu.lab.thulib.thucab.resvutils;

/**
 * Execute result
 * Created by coderhuhy on 15/12/2.
 */
public class ExecuteResult {

    enum CommandResultState {
        Conflict, NetworkFailure, Recommendation, Success, Local
    }

    private ExecutorResultObserver observer;
    private CommandResultState state;
    private CommandKind kind;

    public ExecuteResult(CommandKind kind, ExecutorResultObserver observer, CommandResultState state) {
        this.observer = observer;
        this.kind = kind;
        this.state = state;
    }

    public boolean hasObserver() {
        return observer != null;
    }

    public ExecutorResultObserver getObserver() {
        return this.observer;
    }

    public CommandResultState getResultState() {
        return this.state;
    }

    public CommandKind getCommandKind() {
        return this.kind;
    }

}
