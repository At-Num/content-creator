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

package atnum.content.presenter.swing.inject.guice;

import atnum.content.core.view.*;
import atnum.content.presenter.api.view.*;
import atnum.content.presenter.swing.view.*;
import atnum.content.swing.view.*;
import com.google.inject.AbstractModule;

import atnum.content.core.inject.DIViewContextFactory;

public class ViewModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ViewContextFactory.class).to(DIViewContextFactory.class);

		bind(AboutView.class).to(SwingAboutView.class);
		bind(AdjustAudioCaptureLevelView.class).to(SwingAdjustAudioCaptureLevelView.class);
		bind(ConfirmStopRecordingView.class).to(SwingConfirmStopRecordingView.class);
		bind(DisplaySettingsView.class).to(SwingDisplaySettingsView.class);
		bind(DirectoryChooserView.class).to(SwingDirectoryChooserView.class);
		bind(FileChooserView.class).to(SwingFileChooserView.class);
		bind(GeneralSettingsView.class).to(SwingGeneralSettingsView.class);
		bind(MainView.class).to(SwingMainView.class);
		bind(MenuView.class).to(SwingMenuView.class);
		bind(SoundSettingsView.class).to(SwingSoundSettingsView.class);
		bind(NotificationView.class).to(SwingNotificationView.class);
		bind(NotificationPopupView.class).to(SwingNotificationPopupView.class);
		bind(NotificationPopupManager.class).to(SwingNotificationPopupManager.class);
		bind(ProgressView.class).to(SwingProgressView.class);
		bind(QuitRecordingView.class).to(SwingQuitRecordingView.class);
		bind(RestoreRecordingView.class).to(SwingRestoreRecordingView.class);
		bind(SaveDocumentsView.class).to(SwingSaveDocumentsView.class);
		bind(SaveDocumentOptionView.class).to(SwingSaveDocumentOptionView.class);
		bind(SaveRecordingView.class).to(SwingSaveRecordingView.class);
		bind(SettingsView.class).to(SwingSettingsView.class);
		bind(SlidesView.class).to(SwingSlidesView.class);
		bind(SlideViewAddressOverlay.class).to(SwingSlideViewAddressOverlay.class);
		bind(StartView.class).to(SwingStartView.class);
		bind(StartRecordingView.class).to(SwingStartRecordingView.class);
		bind(TextBoxView.class).to(SwingTextBoxView.class);
		bind(TeXBoxView.class).to(SwingTeXBoxView.class);
		bind(ToolbarView.class).to(SwingToolbarView.class);
		bind(ToolSettingsView.class).to(SwingToolSettingsView.class);
		bind(WhiteboardSettingsView.class).to(SwingWhiteboardSettingsView.class);
	}
}
