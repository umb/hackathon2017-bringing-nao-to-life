package ch.umb.hackathon.naowatson.delegate;

import ch.umb.hackathon.naowatson.NaoConnection;
import ch.umb.hackathon.naowatson.model.ChatMessage;
import ch.umb.hackathon.naowatson.service.SpeechToTextUsingWatson;
import ch.viascom.groundwork.foxhttp.FoxHttpClient;
import ch.viascom.groundwork.foxhttp.FoxHttpRequest;
import ch.viascom.groundwork.foxhttp.FoxHttpResponse;
import ch.viascom.groundwork.foxhttp.body.request.RequestObjectBody;
import ch.viascom.groundwork.foxhttp.builder.FoxHttpClientBuilder;
import ch.viascom.groundwork.foxhttp.log.DefaultFoxHttpLogger;
import ch.viascom.groundwork.foxhttp.parser.GsonParser;
import ch.viascom.groundwork.foxhttp.type.RequestType;
import com.aldebaran.qi.helper.proxies.ALBehaviorManager;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.Map;

public class Dialog implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        boolean danced = false;
        boolean behavior = true;
        int counter = 0;
        Map<String, Object> context = null;

        do {
            counter++;
            SpeechToTextUsingWatson speechToTextUsingWatson = new SpeechToTextUsingWatson();
            String recognizedText = speechToTextUsingWatson.recognizeTextFromMicrophone();
            System.out.println("Recognized Text = " + recognizedText);

            ALBehaviorManager alBehaviorManager = new ALBehaviorManager(NaoConnection.app.session());
            ALTextToSpeech tts = new ALTextToSpeech(NaoConnection.app.session());

            if (recognizedText.contains("dance")) {
                tts.say("Yes sure! What about some Tai Chi.");

                alBehaviorManager.runBehavior("System/animations/Stand/Waiting/PlayHands_1");
                alBehaviorManager.runBehavior("User/taichi-dance-free");
                danced = true;
            } else {
                ChatMessage message = new ChatMessage(context, recognizedText, false);

                FoxHttpClient client = new FoxHttpClientBuilder(new GsonParser())
                        .setFoxHttpLogger(new DefaultFoxHttpLogger(true))
                        .build();

                FoxHttpRequest request = new FoxHttpRequest(client);
                request.setUrl("https://umbchat.herokuapp.com/chat/message");
                request.setRequestBody(new RequestObjectBody(message));
                request.setRequestType(RequestType.POST);
                FoxHttpResponse response = request.execute();

                ChatMessage chatMessage = response.getParsedBody(ChatMessage.class);

                if (!chatMessage.isCompleted()) {
                    context = chatMessage.getContext();
                } else {
                    context = null;
                }

                String responseText = chatMessage.getText().replace("UMB", "U-M-B");

                System.out.println(responseText);

                // Make your robot say something
                tts.setVolume(1f);

                if (responseText.contains("Unterwegs mit Begeisterung")) {
                    tts.setLanguage("German");
                } else {
                    tts.setLanguage("English");
                }
                tts.say(responseText);

                if (behavior) {
                    alBehaviorManager.runBehavior("User/dialog_move_hands/animations/Wave01");
                    behavior = false;
                } else {
                    alBehaviorManager.runBehavior("System/animations/Stand/Waiting/PlayHands_1");
                    behavior = true;
                }

            }
        } while (!danced && counter < 10);
    }
}
