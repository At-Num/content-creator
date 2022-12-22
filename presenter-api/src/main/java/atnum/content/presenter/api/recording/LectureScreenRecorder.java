package atnum.content.presenter.api.recording;

import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import atnum.content.core.ExecutableException;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.configuration.AudioConfiguration;
import atnum.content.core.audio.AudioFormat;
import atnum.content.core.audio.AudioSystemProvider;
import atnum.content.core.bus.ApplicationBus;
import atnum.content.core.bus.event.RecordActionEvent;
import atnum.content.core.bus.event.WindowResizeBoundsEvent;
import atnum.content.core.recording.LectureRecorder;
import atnum.content.presenter.api.event.RecordingStateEvent;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.TargetDataLine;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static java.util.Objects.nonNull;
import static org.monte.media.AudioFormatKeys.EncodingKey;
import static org.monte.media.AudioFormatKeys.FrameRateKey;
import static org.monte.media.AudioFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.AudioFormatKeys.MIME_AVI;
import static org.monte.media.AudioFormatKeys.MediaType;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.AudioFormatKeys.MimeTypeKey;
import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;


public class  LectureScreenRecorder  extends LectureRecorder {

    private final static Logger LOG = LogManager.getLogger(LectureScreenRecorder.class);

    private final AudioSystemProvider audioSystemProvider;

    private final AudioConfiguration audioConfig;

    private final String recDir;


    private AudioFormat audioFormat;

    private Rectangle bounds;

    private ScreenRecorder screenRecorder;
    private String fileName;


    public LectureScreenRecorder(AudioSystemProvider audioSystemProvider, ApplicationContext context, String recDir) {
        LOG.debug("constructor recDir {} bounds {} ", recDir, bounds );
        this.audioSystemProvider = audioSystemProvider;
        this.audioConfig = context.getConfiguration().getAudioConfig(); //
        this.recDir = recDir;
        context.getEventBus().register(this);
    }


    private void screenRecorderFactory( ) {
        GraphicsConfiguration gc = GraphicsEnvironment//
                .getLocalGraphicsEnvironment()//
                .getDefaultScreenDevice()//
                .getDefaultConfiguration();

        LOG.debug("screenRecorderFactory recDir {} ", this.recDir  );
        try {
            screenRecorder = new ScreenRecorder(gc, bounds,
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, COMPRESSOR_NAME_AVI_TECHSMITH_SCREEN_CAPTURE,
                            WidthKey, new Dimension(640, 480).width,
                            HeightKey, new Dimension(640, 480).height,
                            DepthKey, 16, FrameRateKey, Rational.valueOf(10),
                            QualityKey, 1.0f,
                            KeyFrameIntervalKey, 10 * 60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ScreenRecorder.ENCODING_WHITE_CURSOR,
                    FrameRateKey, Rational.valueOf(30)),
                    new Format(MediaTypeKey, MediaType.AUDIO,
                    SampleRateKey, Rational.valueOf(44100),
                    SampleSizeInBitsKey,  16,
                    ChannelsKey, 2), new File(this.recDir), fileName );
        } catch (IOException | AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRecorderBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    @Override
    protected void fireStateChanged() {
        // for now dont need to update related components state as we will just dump the recording regardles
        //LOG.debug("fireStateChanged getState "+getState() );
        ApplicationBus.post(new RecordingStateEvent(getState()));
    }

    @Subscribe
    public void onEvent(final WindowResizeBoundsEvent event) {
        this.bounds =  event.getBounds();
        LOG.debug("onEvent WindowResizeBoundsEvent (w,h) ({}, {})",this.bounds.getWidth() ,this.bounds .getHeight() );
    }

    public void onEvent(final RecordActionEvent event) {
        LOG.debug("RecordActionEvent "+event.toString()  );
    }

    @Override
    protected void initInternal() throws ExecutableException {
        LOG.debug("initInternal bounds {} ", bounds );
    }

    @Override
    protected void startInternal() throws ExecutableException {
        LOG.debug("startInternal " );
        try {
            if(nonNull(screenRecorder)) {
                screenRecorder.start();
            } else {
                screenRecorderFactory();
                screenRecorder.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void stopInternal() throws ExecutableException {
        LOG.debug("stopInternal " );
        try {
            screenRecorder.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void destroyInternal() throws ExecutableException {
        LOG.debug("destroyInternal " );
    }

    @Override
    public long getElapsedTime() {
        return 0;
    }

    public void setAudioFormat(AudioFormat format) {
        audioFormat = format;
    }

    public String getBestRecordingName() {
        return "someTimeStamp";
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
