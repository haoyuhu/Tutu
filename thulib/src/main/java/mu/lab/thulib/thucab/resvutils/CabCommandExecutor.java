package mu.lab.thulib.thucab.resvutils;

import java.util.List;

import mu.lab.thulib.thucab.entity.StudentAccount;

/**
 * Cab command executor
 * Created by coderhuhy on 15/11/18.
 */
public interface CabCommandExecutor {

    void addCommand(CabCommand command);

    void addCommands(List<CabCommand> commands);

    void remove(CabCommand command);

    void remove();

    void execute(StudentAccount account, CabCommand... commands);

    void execute(StudentAccount account);

    void refresh(StudentAccount account);

    void clear();

}
