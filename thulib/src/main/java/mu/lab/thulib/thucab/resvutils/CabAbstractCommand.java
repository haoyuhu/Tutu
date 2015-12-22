package mu.lab.thulib.thucab.resvutils;

/**
 * Cab abstract command
 * Created by coderhuhy on 15/11/16.
 */
public abstract class CabAbstractCommand implements CabCommand {

    protected ExecutorResultObserver observer;
    protected CommandKind cmdKind;

    public CabAbstractCommand(ExecutorResultObserver observer, CommandKind cmdKind) {
        this.observer = observer;
        this.cmdKind = cmdKind;
    }

}
