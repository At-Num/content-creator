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

package atnum.content.core.recording.file;

import static java.util.Objects.isNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import atnum.content.core.io.RandomAccessAudioStream;
import atnum.content.core.io.RandomAccessStream;
import atnum.content.core.model.Document;
import atnum.content.core.recording.RecordedAudio;
import atnum.content.core.recording.RecordedDocument;
import atnum.content.core.recording.RecordedEvents;
import atnum.content.core.recording.Recording;
import atnum.content.core.recording.RecordingHeader;
import atnum.content.core.util.FileUtils;

public class RecordingFileReader {

	public static Recording read(File srcFile) throws IOException, IncompatibleFileFormatException {
		FileInputStream inputStream = new FileInputStream(srcFile);
		RecordingHeader header = new RecordingHeader();
		
		// Read the file header.
		int headerLength = header.getHeaderLength();
		byte[] headerData = new byte[headerLength];
		inputStream.read(headerData);

		header.parseFrom(headerData);
		
		if (header.getVersion() != Recording.FORMAT_VERSION) {
			inputStream.close();
			
			throw new IncompatibleFileFormatException("Incompatible file format");
		}
		
		// Read events.
		int eventsLength = header.getEventsLength();
		byte[] eventData = new byte[eventsLength];
		inputStream.read(eventData);

		// Read document.
		int docLength = header.getDocumentLength();
		byte[] documentData = new byte[docLength];
		inputStream.read(documentData);

		// Read audio data.
		int audioLength = header.getAudioLength();
		
		RandomAccessStream raStream = new RandomAccessStream(srcFile, headerLength + eventsLength + docLength, audioLength);
		RandomAccessAudioStream audioStream = new RandomAccessAudioStream(raStream);

		inputStream.close();

		Recording recording = new Recording();
		recording.setSourceFile(srcFile);
		recording.setRecordingHeader(header);
		recording.setRecordedEvents(new RecordedEvents(eventData));
		recording.setRecordedDocument(new RecordedDocument(documentData));
		recording.setRecordedAudio(new RecordedAudio(audioStream));

		Document document = recording.getRecordedDocument().getDocument();

		if (isNull(document.getName())) {
			document.setTitle(FileUtils.stripExtension(srcFile.getName()));
		}

		return recording;
	}

	public static RecordedAudio getRecordedAudio(File srcFile)
			throws IOException, IncompatibleFileFormatException {
		try (FileInputStream inputStream = new FileInputStream(srcFile)) {
			RecordingHeader header = new RecordingHeader();

			int headerLength = header.getHeaderLength();
			byte[] headerData = new byte[headerLength];
			inputStream.read(headerData);

			header.parseFrom(headerData);

			if (header.getVersion() != Recording.FORMAT_VERSION) {
				inputStream.close();

				throw new IncompatibleFileFormatException(
						"Incompatible file format");
			}

			int eventsLength = header.getEventsLength();
			int docLength = header.getDocumentLength();
			int audioLength = header.getAudioLength();

			RandomAccessStream raStream = new RandomAccessStream(srcFile,
					headerLength + eventsLength + docLength, audioLength);
			RandomAccessAudioStream audioStream = new RandomAccessAudioStream(
					raStream);

			return new RecordedAudio(audioStream);
		}
	}
}
