package ch.umb.hackathon.naowatson.listener;

import ch.umb.hackathon.naowatson.NaoConnection;
import com.aldebaran.qi.Application;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class NaoStartAppListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {

        NaoConnection connection = new NaoConnection();

        String naoip = (String) delegateExecution.getVariable("naoip");

        String robotUrl = "tcp://" + naoip + ":9559";
        System.out.println(robotUrl);
        NaoConnection.app = new Application(new String[]{}, robotUrl);
        NaoConnection.app.start(); // will throw if connection fails

        System.out.println("Successfully connected to the robot");
    }
}
