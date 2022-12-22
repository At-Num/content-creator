/*
 * Copyright (C) 2020 TU Darmstadt, Department of Computer Science,
 * Embedded Systems and Applications Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package atnum.content.presenter.api.service;

import static java.util.Objects.nonNull;

import java.awt.*;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.inject.Inject;
import javax.inject.Singleton;

import atnum.content.core.bus.event.RecordFileNameEvent;
import atnum.content.presenter.api.recording.LectureScreenRecorder;
import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import atnum.content.core.ExecutableBase;
import atnum.content.core.ExecutableException;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.audio.AudioFormat;
import atnum.content.core.bus.event.WindowBootBoundsEvent;
import atnum.content.core.model.Time;
import atnum.content.core.util.ProgressCallback;
import atnum.content.presenter.api.event.RecordingTimeEvent;

@Singleton
public class RecordingService extends ExecutableBase {
	private final static Logger LOG = LogManager.getLogger(LectureScreenRecorder.class);

	private final ApplicationContext context;

	private IdleTimer recordingTimer;

	private Rectangle bounds;

	private final  LectureScreenRecorder screenRecorder;


	@Inject
	public RecordingService(ApplicationContext context, LectureScreenRecorder screenRecorder)  {
		this.context = context;
		this.screenRecorder = screenRecorder;
	}

	@Subscribe
	public void onEvent(final WindowBootBoundsEvent event) {
		bounds =  event.getBounds();
		LOG.debug("onEvent WindowBootBoundsEvent getHeight {} ", event.getBounds().getBounds().getHeight() );
		screenRecorder.setRecorderBounds(bounds);
	}

	public void onFileNameUpdate( String fileName ) {
		System.out.println("RecordingService onFileNameUpdate "+fileName);
		LOG.debug("RecordingService onFileNameUpdate fileName {} ", fileName );
		screenRecorder.setFileName(fileName);
	}


	public void setAudioFormat(AudioFormat audioFormat) {
		screenRecorder.setAudioFormat(audioFormat);
	}

	public String getBestRecordingName() {
		return screenRecorder.getBestRecordingName();
	}

	public CompletableFuture<Void> writeRecording(File file,
			ProgressCallback callback) {
		return CompletableFuture.runAsync(() -> {
			try {
			}
			catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	public void discardRecording() throws ExecutableException {
		if (nonNull(recordingTimer)) {
			recordingTimer.stop();
		}
	}

	@Override
	protected void initInternal() throws ExecutableException {
		screenRecorder.init();
	}

	@Override
	protected void startInternal() throws ExecutableException {
		screenRecorder.start();

	}

	@Override
	protected void suspendInternal() throws ExecutableException {

	}

	@Override
	protected void stopInternal() throws ExecutableException {
		screenRecorder.stop();
		//audioRecorder.stop();
	}

	@Override
	protected void destroyInternal() throws ExecutableException {

	}

	private void fireTimeChanged() {
		Time time = new Time(1000);
		context.getEventBus().post(new RecordingTimeEvent(time));
	}

	private class IdleTimer extends Timer {

		private static final int IDLE_TIME = 1000;

		private TimerTask idleTask;


		void runTask() {
			idleTask = new TimerTask() {

				@Override
				public void run() {
					fireTimeChanged();
				}
			};

			schedule(idleTask, 0, IDLE_TIME);
		}

		void stop() {
			if (idleTask != null) {
				idleTask.cancel();
			}

			purge();
		}
	}
}
