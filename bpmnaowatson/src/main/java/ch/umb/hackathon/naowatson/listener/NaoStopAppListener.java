package ch.umb.hackathon.naowatson.listener;

import ch.umb.hackathon.naowatson.NaoConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class NaoStopAppListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        NaoConnection.app.stop();
    }
}
