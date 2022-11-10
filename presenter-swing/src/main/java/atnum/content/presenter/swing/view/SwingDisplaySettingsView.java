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

package atnum.content.presenter.swing.view;

import java.awt.Container;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import atnum.content.core.app.configuration.ScreenConfiguration;
import atnum.content.core.beans.BooleanProperty;
import atnum.content.core.beans.Converter;
import atnum.content.core.beans.ObjectProperty;
import atnum.content.core.geometry.Rectangle2D;
import atnum.content.core.graphics.Color;
import atnum.content.core.view.Action;
import atnum.content.presenter.api.presenter.DisplaySettingsPresenter;
import atnum.content.presenter.api.view.DisplaySettingsView;
import atnum.content.swing.beans.ConvertibleObjectProperty;
import atnum.content.swing.components.ColorChooserButton;
import atnum.content.swing.components.DisplayPanel;
import atnum.content.swing.converter.ColorConverter;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;

@SwingView(name = "display-settings", presenter = DisplaySettingsPresenter.class)
public class SwingDisplaySettingsView extends JPanel implements DisplaySettingsView {

	private JCheckBox autoPresentationCheckBox;

	private ColorChooserButton colorChooserButton;

	private Container displayContainer;

	private DisplayPanel displayPanel;

	private JButton closeButton;

	private JButton resetButton;


	SwingDisplaySettingsView() {
		super();
	}

	@Override
	public void setEnableDisplaysOnStart(BooleanProperty enable) {
		SwingUtils.bindBidirectional(autoPresentationCheckBox, enable);
	}

	@Override
	public void setDisplayBackgroundColor(ObjectProperty<Color> color) {
		SwingUtils.bindBidirectional(colorChooserButton, new ConvertibleObjectProperty<>(color,
				ColorConverter.INSTANCE));
	}

	@Override
	public void setScreens(List<ScreenConfiguration> screens) {
		SwingUtils.invoke(() -> {
			displayContainer.removeAll();

			for (int i = 0; i < screens.size(); i++) {
				ScreenConfiguration screenConfig = screens.get(i);
				Rectangle2D bounds = screenConfig.getScreen().getBounds();

				String id = String.valueOf(i);
				JCheckBox checkBox = new JCheckBox(String.format("%s: %s",
						id, ScreenBoundsConverter.INSTANCE.to(bounds)));
				checkBox.setSelected(screenConfig.getEnabled());

				SwingUtils.bindBidirectional(checkBox, screenConfig.enabledProperty());

				displayContainer.add(checkBox);
			}

			displayPanel.setScreens(screens);

			displayContainer.revalidate();
			displayContainer.repaint();
		});
	}

	@Override
	public void setOnClose(Action action) {
		SwingUtils.bindAction(closeButton, action);
	}

	@Override
	public void setOnReset(Action action) {
		SwingUtils.bindAction(resetButton, action);
	}



	/**
	 * Screen bounds of Rectangle2D to String converter.
	 */
	private static class ScreenBoundsConverter implements Converter<Rectangle2D, String> {

		static final ScreenBoundsConverter INSTANCE = new ScreenBoundsConverter();


		@Override
		public String to(Rectangle2D bounds) {
			return String.format("[%d, %d, %d, %d]", (int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
		}

		@Override
		public Rectangle2D from(String value) {
			return new Rectangle2D();
		}

	}
}
