package ch.umb.hackathon.naowatson.delegate;

import ch.umb.hackathon.naowatson.NaoConnection;
import com.aldebaran.qi.helper.proxies.ALMotion;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class TurnLeft implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        ALMotion alMotion = new ALMotion(NaoConnection.app.session());
        float rotation = (float) (Math.PI / 2d);
        alMotion.moveTo(0f, 0f, rotation);
    }
}
