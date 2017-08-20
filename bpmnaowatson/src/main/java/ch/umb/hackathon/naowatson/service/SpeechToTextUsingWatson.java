package ch.umb.hackathon.naowatson.service;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import javax.sound.sampled.*;

public class SpeechToTextUsingWatson {

    SpeechToText service = new SpeechToText();
    boolean keepListeningOnMicrophone = true;
    String transcribedText = "";

    public SpeechToTextUsingWatson() {
        service = new SpeechToText();
        service.setUsernameAndPassword("9ad68dd0-857b-4499-87b9-7bad3328fdd1", "mEiBeN2nHdvx");
    }

    public String recognizeTextFromMicrophone() {
        keepListeningOnMicrophone = true;
        try {
            // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
            int sampleRate = 16000;
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Line not supported");
                return null;
            }

            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            AudioInputStream audio = new AudioInputStream(line);

            RecognizeOptions options = new RecognizeOptions.Builder()
                    .continuous(true)
                    .interimResults(true)
                    .inactivityTimeout(5) // use this to stop listening when the speaker pauses, i.e. for 5s
                    .contentType(HttpMediaType.AUDIO_RAW + "; rate=" + sampleRate)
                    .build();

            service.recognizeUsingWebSocket(audio, options, new BaseRecognizeCallback() {
                @Override
                public void onTranscription(SpeechResults speechResults) {
                    // System.out.println(speechResults);
                    String transcript = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                    if (speechResults.getResults().get(0).isFinal()) {
                        keepListeningOnMicrophone = false;
                        transcribedText = transcript;
                        System.out.println("Sentence " + (speechResults.getResultIndex() + 1) + ": " + transcript + "\n");
                    } else {
                        System.out.print(transcript + "\r");
                    }
                }
            });

            do {
                Thread.sleep(1000);
            } while (keepListeningOnMicrophone);

            // closing the WebSockets underlying InputStream will close the WebSocket itself.
            line.stop();
            line.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return transcribedText;
    }
}
