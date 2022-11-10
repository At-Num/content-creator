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

package atnum.content.swing.app;

import static java.util.Objects.requireNonNull;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import atnum.content.core.ExecutableException;
import atnum.content.core.app.ApplicationBase;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.ApplicationFactory;
import atnum.content.core.app.GraphicalApplication;
import atnum.content.core.app.configuration.Configuration;
import atnum.content.core.app.view.Screens;
import atnum.content.core.bus.ApplicationBus;
import atnum.content.core.bus.event.WindowBootBoundsEvent;
import atnum.content.core.bus.event.WindowResizeBoundsEvent;
import atnum.content.core.presenter.MainPresenter;

public abstract class SwingApplication extends ApplicationBase implements GraphicalApplication {

	private static final Logger LOG = LogManager.getLogger(SwingApplication.class);

	private JFrame window;


	@Override
	protected void initInternal(String[] args) throws ExecutableException {
		final CountDownLatch initLatch = new CountDownLatch(1);
		final ApplicationFactory appFactory;
		final ApplicationContext appContext;
		final MainPresenter<?> mainPresenter;

		try {
			appFactory = createApplicationFactory();
			requireNonNull(appFactory, "Application factory must not be null");

			appContext = appFactory.getApplicationContext();
			requireNonNull(appContext, "Application context must not be null");

			mainPresenter = appFactory.getStartPresenter();
			requireNonNull(mainPresenter, "Start presenter was not initialized");
		}
		catch (Exception e) {
			throw new ExecutableException(e);
		}

		final Configuration config = appContext.getConfiguration();

		FutureTask<Void> initTask = new FutureTask<>(() -> {
			try {
				mainPresenter.setArgs(args);
				mainPresenter.initialize();
			}
			catch (Exception e) {
				LOG.error("Initialize start-presenter failed", e);

				System.exit(-1);
			}

			if (!Component.class.isAssignableFrom(mainPresenter.getView().getClass())) {
				throw new Exception("Start view must be a subclass of java.awt.Component");
			}

			mainPresenter.setOnClose(() -> {
				try {
					destroy();
				}
				catch (ExecutableException e) {
					LOG.error("Destroy application failed", e);

					System.exit(-1);
				}
			});

			window = new JFrame();
			window.setTitle("AtNum Content Creator");
			window.setUndecorated(true);
			window.getContentPane().add((Component) mainPresenter.getView());

			if (config.getStartFullscreen()) {
				GraphicsDevice device = Screens.getScreenDevice(window);
				window.setUndecorated(true);

				window.setBounds(device.getDefaultConfiguration().getBounds());
				window.setResizable(false);

				window.validate();
			}
			else {
				window.pack();

				if (config.getStartMaximized()) {
					window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
				}
			}

			initLatch.countDown();

			return null;
		});

		SwingUtilities.invokeLater(initTask);

		try {
			// Wait until the initial/start view has been initialized.
			initLatch.await();

			// Get potential exceptions.
			initTask.get();


		}
		catch (Exception e) {
			throw new ExecutableException(e);
		}

		if (!OPEN_FILES.isEmpty()) {
			mainPresenter.openFile(OPEN_FILES.get(0));
			OPEN_FILES.clear();
		}
	}

	@Override
	protected void startInternal() throws ExecutableException {
		final CountDownLatch startLatch = new CountDownLatch(1);
		var centerAdjust = 0.6;


		FutureTask<Void> startTask = new FutureTask<>(() -> {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			var wCAdjust= dim.width/2-window.getSize().width/2;
			wCAdjust = (int) (wCAdjust - wCAdjust*centerAdjust);
			var hCAdjust= dim.width/2-window.getSize().width/2;
			hCAdjust = (int) (hCAdjust - hCAdjust*centerAdjust);
			window.setLocation(wCAdjust, hCAdjust);
			window.setVisible(true);
			//window.setLocationRelativeTo(null);
			startLatch.countDown();

			return null;
		});

		SwingUtilities.invokeLater(startTask);

		try {
			startLatch.await();

			// Get potential exceptions.
			startTask.get();
		}
		catch (Exception e) {
			throw new ExecutableException(e);
		}
		window.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				Component c = (Component)evt.getSource();
				ApplicationBus.post(new WindowResizeBoundsEvent(c.getBounds()));

			}
		});
		//5 to 3
		window.setMinimumSize(new Dimension(1280,720));

		ApplicationBus.post(new WindowBootBoundsEvent(window.getBounds()));
	}

	@Override
	protected void stopInternal() throws ExecutableException {
		window.dispose();
	}

	@Override
	protected void destroyInternal() throws ExecutableException {

	}
}
