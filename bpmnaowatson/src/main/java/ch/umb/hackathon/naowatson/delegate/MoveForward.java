package ch.umb.hackathon.naowatson.delegate;

import ch.umb.hackathon.naowatson.NaoConnection;
import com.aldebaran.qi.helper.proxies.ALMotion;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class MoveForward implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Integer steps = (Integer) delegateExecution.getVariable("steps");

        ALMotion alMotion = new ALMotion(NaoConnection.app.session());

        float distance = calculateWalkingDistance(steps);
        alMotion.moveTo(distance, 0f, 0f);
    }

    private float calculateWalkingDistance(Integer steps) {
        float distance = 0f;
        distance = 0.02f * steps;
        return distance;
    }
}
