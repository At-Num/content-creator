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

package atnum.content.core.codec;

/**
 * Available codecs to the system.
 *
 * @author Alex Andres
 */
public enum CodecID {

	/** H.264/MPEG-4 video codec */
	H264,

	/** H.265/HEVC video codec */
	H265,

	/** VP9 video codec */
	VP9,

	/** Advanced Audio Coding */
	AAC,

	/** MP3 audio codec */
	MP3,

	/** Opus Interactive Audio Codec */
	OPUS,

	/** Ogg Vorbis audio codec */
	VORBIS,

	/** Wav audio codec */
	WAV

}
