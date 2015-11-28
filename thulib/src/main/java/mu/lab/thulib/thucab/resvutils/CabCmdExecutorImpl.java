package mu.lab.thulib.thucab.resvutils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mu.lab.thulib.thucab.CabUtilities;
import mu.lab.thulib.thucab.ResvRecordStore;
import mu.lab.thulib.thucab.entity.StudentAccount;

/**
 * Implement of Cab command executor
 * Created by coderhuhy on 15/11/19.
 */
public class CabCmdExecutorImpl implements CabCommandExecutor {

    private static final String LogTag = CabCmdExecutorImpl.class.getSimpleName();
    protected static final int poolSize = 3;

    protected List<CabCommand> commands = new ArrayList<>();
    protected ExecutorService executor = Executors.newScheduledThreadPool(poolSize);

    private static class CabCmdExecutorHolder {
        static CabCmdExecutorImpl instance = new CabCmdExecutorImpl();
    }

    private CabCmdExecutorImpl() {
    }

    public static CabCommandExecutor getInstance() {
        return CabCmdExecutorHolder.instance;
    }

    @Override
    public void addCommand(CabCommand command) {
        commands.add(command);
    }

    @Override
    public void addCommands(List<CabCommand> cmds) {
        commands.addAll(cmds);
    }

    @Override
    public void remove(CabCommand command) {
        commands.remove(command);
    }

    @Override
    public void remove() {
        commands.clear();
    }

    @Override
    public void execute(StudentAccount account, CabCommand... command) {
        reset();
        executor.execute(generateTask(account, command));
    }

    @Override
    public void execute(StudentAccount account) {
        reset();
        List<CabCommand> tasks = new ArrayList<>();
        synchronized (CabCommandExecutor.class) {
            for (CabCommand command : this.commands) {
                tasks.add(command);
            }
            this.commands.clear();
        }
        executor.execute(generateTask(account, tasks));
    }

    @Override
    public void refresh(StudentAccount account) {
        ResvRecordStore.refresh(account);
        CabCommand command = CabCommandCreator.createAutoResvCmdGroup(account);
        this.addCommand(command);
        this.execute(account);
    }

    protected Runnable generateTask(final StudentAccount account, final List<CabCommand> cmds) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    if (CabUtilities.login(account)) {
                        for (CabCommand command : cmds) {
                            command.executeCommand();
                        }
                    }
                } catch (Exception e) {
                    Log.e(LogTag, e.getMessage(), e);
                }
            }
        };
    }

    protected Runnable generateTask(final StudentAccount account, final CabCommand... cmds) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    if (CabUtilities.login(account)) {
                        for (CabCommand command : cmds) {
                            command.executeCommand();
                        }
                    }
                } catch (Exception e) {
                    Log.e(LogTag, e.getMessage(), e);
                }
            }
        };
    }

    protected void reset() {
        if (executor.isTerminated() || executor.isShutdown()) {
            executor = Executors.newFixedThreadPool(poolSize);
        }
    }

    @Override
    public void clear() {
        executor.shutdownNow();
        commands.clear();
    }
}
