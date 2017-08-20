package ch.umb.hackathon.naowatson.delegate;

import ch.umb.hackathon.naowatson.NaoConnection;
import com.aldebaran.qi.helper.proxies.ALBehaviorManager;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class Greetings implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String speech = (String) delegateExecution.getVariable("speech");
        System.out.println("--> DEBUG: " + speech);

        ALBehaviorManager alBehaviorManager = new ALBehaviorManager(NaoConnection.app.session());
        alBehaviorManager.runBehavior("User/dialog_move_hands/animations/Wave01");

        ALTextToSpeech tts = new ALTextToSpeech(NaoConnection.app.session());
        tts.setVolume(1f);
        tts.setLanguage("English");

        tts.say(speech);
    }
}
