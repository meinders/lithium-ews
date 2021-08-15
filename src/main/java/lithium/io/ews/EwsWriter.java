/*
 * Copyright 2013-2021 Gerrit Meinders
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
package lithium.io.ews;

import java.awt.*;
import java.io.*;
import java.nio.charset.*;
import java.util.List;
import java.util.*;
import java.util.zip.*;

import lithium.io.*;
import lithium.io.rtf.*;

/**
 * Writes schedules in the EWS file format.
 *
 * @author Gerrit Meinders
 */
public class EwsWriter
{
    private static final int SCHEDULE_ENTRY_LENGTH = 1816;
    private static final int SCHEDULE_ENTRY_PRESENTATION_HEADER_LENGTH = 48;

    private final OutputStream _out;

    private Charset _charset = Charset.forName(Config.charset);

    public EwsWriter(final OutputStream out) {
        _out = out;
    }

    public Charset getCharset() {
        return _charset;
    }

    public void setCharset(final Charset charset) {
        _charset = charset;
    }

    public void write(final Schedule schedule)
            throws IOException {
        writeImpl(schedule);
    }

    private void writeImpl(final Schedule schedule)
            throws IOException {
        final List<ScheduleEntry> entries = schedule.getEntries();
        final int scheduleLength = entries.size() * SCHEDULE_ENTRY_LENGTH;
        int cumulativeContentLength = 0;

        writeScheduleHeader(entries);

        // Add entries
        for (final ScheduleEntry entry : entries) {
            writeEntryInformation(entry, scheduleLength, cumulativeContentLength);

            cumulativeContentLength += getEntryContentLength(entry);
            cumulativeContentLength += getPresentationLength(entry);
            cumulativeContentLength += getBackgroundContentLength(entry);
            cumulativeContentLength += getMediaContentLength(entry);
        }

        // Add content
        for (final ScheduleEntry entry : entries) {
            writeContentForEntry(entry);

            if ( entry.getType() == ScheduleEntry.Type.PRESENTATION ) {
                writePresentationForEntry( entry);
            }

            writeBackgroundMediaForEntry( entry);
            writeMediaForEntry(entry);
        }
    }

    private void writeScheduleHeader(List<ScheduleEntry> entries) throws IOException {
        writeString("EasyWorship Schedule File Version    5");
        writeInt(0x00001a00);
        writeInt(0);
        writeInt(0x00004014);
        writeInt(0);
        writeShort(0x4014);
        writeInt(entries.size());
        writeShort(SCHEDULE_ENTRY_LENGTH);
    }

    private void writeEntryInformation(ScheduleEntry entry, int scheduleLength, int cumulativeContentLength) throws IOException {
        // Song information
        writePaddedString(entry.getTitle(), 51);            // title
        writePaddedString(entry.getMediaResource(), 256);   // mediaResource
        writePaddedString(entry.getAuthor(), 51);           // author
        writePaddedString(entry.getCopyright(), 101);       // copyright
        writePaddedString(entry.getAdministrator(), 51);    // administrator

        writeBackgroundInformation(entry);

        writeTimestamp(entry.getTimestamp());

        final int contentPointer = 62 + scheduleLength + cumulativeContentLength;
        writeInt(contentPointer);

        writeZeroes(16);    // Skip
        writeInt(parseEntryType(entry.getType()));  // Entry type

        writeZeroes(4);    // Skip
	    final int presentationMagicValue = entry.getPresentation() != null
	                                       ? entry.getPresentation().getMagicValue()
	                                       : 0;
	    writeInt( presentationMagicValue );
        writeZeroes(4);    // Skip
        writeInt( entry.getType() == ScheduleEntry.Type.PRESENTATION ? 1 : 0 );          // isPresentation
        writeInt( getPresentationLength( entry ));            // presentationLength

        _out.write(0);          // customFontSettings
        _out.write(1);          // fontSizeAutomatic
        _out.write(0);          // Skip
        _out.write(0);          // Skip
        writeInt(0);            // fontSize limit
        _out.write(1);          // useDefaultFont
        writeZeroes(255);   // fontName
        writeInt(1);            // foregroundAutomatic
        writeInt(0);            // foregroundColor
        writeInt(1);            // shadowAutomatic
        writeInt(0);            // shadowColor
        writeInt(1);            // outlineAutomatic
        writeInt(0);            // outlineColor
        _out.write(parseTristate(null));    // shadowEnabled
        _out.write(parseTristate(null));    // outlineEnabled
        _out.write(parseTristate(null));    // boldEnabled
        _out.write(parseTristate(null));    // italicEnabled
        _out.write(parseHorizontalAlignment(ScheduleEntry.HorizontalAlignment.DEFAULT));    // horizontalTextAlignment
        _out.write(parseVerticalAlignment(ScheduleEntry.VerticalAlignment.DEFAULT));        // verticalTextAlignment
        _out.write(1);          // defaultTextMargins
        writeInt(0);            // textMarginLeft
        writeInt(0);            // textMarginTop
        writeInt(0);            // textMarginRight
        writeInt(0);            // textMarginBottom

        // More song information
        writePaddedString(entry.getNotes(), 161);
        writeZeroes(94);    // skip
        writePaddedString(entry.getSongNumber(), 11);
        _out.write(0);    // skip
        _out.write(0);    // media embedded
        writeZeroes(57);        // skip

        final int originalResourceLength = getOriginalResourceLength( entry );
        writeInt(originalResourceLength);
        writeZeroes(12);        // skip

        final int mediaContentPointer = getMediaContentPointer( entry, contentPointer );
        writeInt(mediaContentPointer);

        writeZeroes(20);    // skip

        final ScheduleEntry.AspectRatio aspectRatio = getAspectRatio( entry );
        writeInt(parseAspectRatio(aspectRatio));
        writeZeroes(292);    // skip
    }

    private void writeBackgroundInformation(ScheduleEntry entry) throws IOException {
        boolean defaultBackground = false;
        ScheduleEntry.BackgroundType backgroundType = ScheduleEntry.BackgroundType.COLOR;
        Color backgroundColor = Color.BLACK;
        Color gradientColor1 = new Color(0, 0, 128);
        Color gradientColor2 = Color.black;
        ScheduleEntry.GradientStyle gradientStyle = ScheduleEntry.GradientStyle.DIAGONAL_UP;
        ScheduleEntry.GradientVariant gradientVariant = ScheduleEntry.GradientVariant.LINEAR;
        String backgroundName = "";

        if (entry.getBackground() instanceof ColorBackground) {
            ColorBackground background = (ColorBackground) entry.getBackground();

            backgroundType = ScheduleEntry.BackgroundType.COLOR;
            backgroundColor = background.getColor();
        } else if (entry.getBackground() instanceof GradientBackground) {
            GradientBackground background = (GradientBackground) entry.getBackground();

            backgroundType = ScheduleEntry.BackgroundType.GRADIENT;
            gradientColor1 = background.getColor1();
            gradientColor2 = background.getColor2();
            gradientStyle = background.getStyle();
            gradientVariant = background.getVariant();
        } else if (entry.getBackground() instanceof ImageBackground) {
            ImageBackground background = (ImageBackground) entry.getBackground();

            if (background.isTiled()) {
                backgroundType = ScheduleEntry.BackgroundType.IMAGE_TILED;
            } else {
                backgroundType = ScheduleEntry.BackgroundType.IMAGE_SCALED;
            }

            backgroundName = background.getName();
        } else if (entry.getBackground() instanceof VideoBackground) {
            VideoBackground background = (VideoBackground) entry.getBackground();

            backgroundType = ScheduleEntry.BackgroundType.VIDEO;
            backgroundName = background.getName();
        } else if (entry.getBackground() instanceof LiveVideoBackground) {
            LiveVideoBackground background = (LiveVideoBackground) entry.getBackground();

            backgroundType = ScheduleEntry.BackgroundType.LIVE_VIDEO;
            backgroundName = background.getName();
        } else {
            defaultBackground = true;
        }

        _out.write( entry.getType() == ScheduleEntry.Type.PRESENTATION ? 0 : 1);    // Is background set
        _out.write(defaultBackground ? 1 : 0);
        writeInt(parseBackgroundType(backgroundType));
        writeInt(parseColor(backgroundColor));
        writeInt(parseColor(gradientColor1));
        writeInt(parseColor(gradientColor2));
        _out.write(parseGradientStyle(gradientStyle));
        _out.write(parseGradientVariant(gradientVariant));
        writeInt(0);         // skip
        _out.write(0x00);    // skip
        _out.write(0x00);    // skip
        writePaddedString(backgroundName, 256);
    }

    private int getOriginalResourceLength( final ScheduleEntry entry )
    {
        if ( entry.getType() == ScheduleEntry.Type.PRESENTATION) {
            return ((BinaryContent) entry.getContent()).getBytes().length;
        }
        return 0;
    }

    private int getMediaContentPointer( final ScheduleEntry entry, final int contentPointer )
            throws IOException {
        if ( entry.getBackground() instanceof VideoBackground) {
            final int contentLength = getEntryContentLength( entry ) + getBackgroundContentLength( entry );
            return contentPointer + contentLength;
        } else if ( entry.getType() == ScheduleEntry.Type.PRESENTATION) {
            return contentPointer;
        }
        return 0;
    }

    private ScheduleEntry.AspectRatio getAspectRatio( final ScheduleEntry entry )
    {
        if ( entry.getBackground() instanceof ImageBackground) {
            return ((ImageBackground) entry.getBackground()).getAspectRatio();
        } else if ( entry.getBackground() instanceof LiveVideoBackground) {
            return ((LiveVideoBackground) entry.getBackground()).getAspectRatio();
        } else if ( entry.getBackground() instanceof VideoBackground) {
            return ((VideoBackground) entry.getBackground()).getAspectRatio();
        }
        return ScheduleEntry.AspectRatio.STRETCH;
    }

    private int getEntryContentLength(ScheduleEntry entry) throws IOException {
        final Content content = entry.getContent();
        if (content instanceof TextContent) {
            final TextContent textContent = (TextContent) content;
            final byte[] bytes = RtfWriter.writeToBytes(textContent.getText());

            final ByteArrayOutputStream compressedOut = new ByteArrayOutputStream();
            final DeflaterOutputStream deflaterOut = new DeflaterOutputStream(compressedOut);
            deflaterOut.write(bytes);
            deflaterOut.close();
            return compressedOut.size() + 14;
        } else if (content instanceof BinaryContent) {
            final BinaryContent binaryContent = (BinaryContent) content;
            final byte[] bytes = binaryContent.getBytes();
            return bytes.length + 4 + (binaryContent.isPrecededByZeros() ? 4 : 0);
        } else if (content != null) {
            throw new IllegalArgumentException("Unsupported content: " + content);
        }
        return 0;
    }

    private int getBackgroundContentLength(ScheduleEntry entry) {
        if (entry.getBackground() instanceof ImageBackground) {
            final byte[] bytes = ((ImageBackground) entry.getBackground()).getImage().getBytes();
            return 4 + bytes.length;
        } else if (entry.getBackground() instanceof VideoBackground) {
            final byte[] bytes = ((VideoBackground) entry.getBackground()).getImage().getBytes();
            return 4 + bytes.length;
        }
        return 0;
    }

    private int getMediaContentLength(ScheduleEntry entry) {
        if (entry.getBackground() instanceof VideoBackground) {
            final byte[] bytes = ((VideoBackground) entry.getBackground()).getVideo().getBytes();
            return 4 + 4 + bytes.length;    // Some types have 4 additional zero bytes. Don't know why.
        }
        return 0;
    }

    private int getPresentationLength( final ScheduleEntry entry) {
        if ( entry.getType() != ScheduleEntry.Type.PRESENTATION
             || entry.getPresentation() == null) {
            return 0;
        }

        final int headerLength = 4 + SCHEDULE_ENTRY_PRESENTATION_HEADER_LENGTH;
        final int slidesLength = entry.getPresentation().getSlides().size() * 16;
        final int slidesContentLength = entry.getPresentation()
                                             .getSlides()
                                             .stream()
                                             // Math: 1 = unknown bit; 4 = content length
                                             .map( it -> 1 + 4 + it.getContent().length )
                                             .reduce( 0, Integer::sum );
        return headerLength + slidesLength + slidesContentLength;
    }

    private void writeContentForEntry(ScheduleEntry entry) throws IOException {
        final Content content = entry.getContent();
        if (content instanceof TextContent) {
            final TextContent textContent = (TextContent) content;
            final byte[] bytes = RtfWriter.writeToBytes(textContent.getText());

            final ByteArrayOutputStream compressedOut = new ByteArrayOutputStream();
            final DeflaterOutputStream deflaterOut = new DeflaterOutputStream(compressedOut);
            deflaterOut.write(bytes);
            deflaterOut.close();

            final byte[] compressedContent = compressedOut.toByteArray();
            writeInt(compressedContent.length + 10);    // Content length
            _out.write(compressedContent);  // Including checksum (last 4 bytes)
            _out.write(0x51);       // Skip?
            _out.write(0x4b);       // Skip?
            _out.write(0x03);       // Skip?
            _out.write(0x04);       // Skip?
            writeInt(bytes.length); // decompressedLength
            _out.write(0x08);
            _out.write(0x0);
        } else if (content instanceof BinaryContent) {
            final BinaryContent binaryContent = (BinaryContent) content;
            final byte[] bytes = binaryContent.getBytes();
            writeInt(bytes.length);
            if (binaryContent.isPrecededByZeros()) {
                writeInt( 0 );
            }
            _out.write(bytes);
        } else if (content != null) {
            throw new IllegalArgumentException("Unsupported content: " + content);
        }
    }

    private void writeBackgroundMediaForEntry( ScheduleEntry entry) throws IOException {
        if (entry.getBackground() instanceof ImageBackground) {
            final byte[] bytes = ((ImageBackground) entry.getBackground()).getImage().getBytes();
            writeInt(bytes.length); // length
            _out.write(bytes);      // data
        } else if (entry.getBackground() instanceof VideoBackground) {
            final byte[] bytes = ((VideoBackground) entry.getBackground()).getImage().getBytes();
            writeInt(bytes.length); // length
            _out.write(bytes);      // data
        }
    }

    private void writeMediaForEntry(ScheduleEntry entry) throws IOException {
        if (entry.getBackground() instanceof VideoBackground) {
            final byte[] bytes = ((VideoBackground) entry.getBackground()).getVideo().getBytes();
            writeInt(bytes.length); // length
            writeZeroes(4);     // Some types have 4 additional zero bytes. Don't know why.
            _out.write(bytes);      // data
        }
    }

    private void writePresentationForEntry( final ScheduleEntry entry ) throws IOException {
        if (entry.getPresentation() == null) {
            return;
        }

	    writePresentationHeaderForEntry( entry );
	    writeSlidesForEntry(entry);
    }

	private void writePresentationHeaderForEntry( final ScheduleEntry entry )
			throws IOException {
		writeInt( SCHEDULE_ENTRY_PRESENTATION_HEADER_LENGTH );   // Length of the following header
		writePaddedString( "$ezwppstream$", 16 );   // Identifier
		_out.write( 0x02 );
		writeZeroes( 7 );
		writeInt( entry.getPresentation().getMagicValue());

        // Math: identifier = 16; 0x02 = 1; unknown = 7; magicValue = 4; 2x slideCount = 2x4
		writeZeroes( SCHEDULE_ENTRY_PRESENTATION_HEADER_LENGTH - 16 - 1 - 7 - 4 - 8 );

		writeInt( entry.getPresentation().getSlides().size() );
		writeInt( entry.getPresentation().getSlides().size() );
	}

	private void writeSlidesForEntry( final ScheduleEntry entry )
            throws IOException {
        // Start contentPointer relative to the start of the presentation header
        // Math: 4 = headerLength value
        int contentPointer = 4 + SCHEDULE_ENTRY_PRESENTATION_HEADER_LENGTH
                             + entry.getPresentation().getSlides().size() * 16;

		for ( final Slide slide : entry.getPresentation().getSlides()) {
            writeSlide( slide, contentPointer );

            // Math: 1 = 0x01; 4 = contentLength value
            contentPointer += 1 + 4 + slide.getContent().length;
		}

		for ( final Slide slide : entry.getPresentation().getSlides()) {
            writeSlideContentForSlide( slide );
		}
	}

    private void writeSlide( final Slide slide, final int contentPointer )
            throws IOException {
        writeInt( contentPointer );
        _out.write( slide.getUnknown() );
    }

    private void writeSlideContentForSlide( final Slide slide )
            throws IOException {
	    _out.write( 1 );
	    writeInt( slide.getContent().length );
    	_out.write( slide.getContent() );
    }

    private void writeZeroes(final int count)
            throws IOException {
        for (int i = 0; i < count; i++) {
            _out.write(0);
        }
    }

    private void writeString(final String string)
            throws IOException {
        _out.write(string.getBytes(getCharset()));
    }

    private void writeTimestamp(final Date date)
            throws IOException {
        if (date == null) {
            writeDouble(0.0);
        } else {
            final Calendar calendar = Calendar.getInstance();

            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            final Date startOfDay = calendar.getTime();

            // It appears that 1 January 1900 does not compute.
            calendar.set(1899, Calendar.DECEMBER, 30, 0, 0, 0);
            final Date epoch = calendar.getTime();

            // TODO: Not accurate for various reasons. Use Joda-Time.
            final long dateDifference = date.getTime() - epoch.getTime();
            final long timeDifference = date.getTime() - startOfDay.getTime();
            final double timestamp = (double) (dateDifference / 86400000L) + (double) timeDifference / 86400000.0;

            writeDouble(timestamp);
        }
    }

    private void writeDouble(final double d)
            throws IOException {
        writeLong(Double.doubleToLongBits(d));
    }

    private void writeLong(final long i)
            throws IOException {
        writeInt((int) i);
        writeInt((int) (i >> 32));
    }

    private void writeInt(final int i)
            throws IOException {
        _out.write(i & 0xff);
        _out.write((i >> 8) & 0xff);
        _out.write((i >> 16) & 0xff);
        _out.write((i >> 24) & 0xff);
    }

    private void writeShort(final int i)
            throws IOException {
        _out.write(i & 0xff);
        _out.write((i >> 8) & 0xff);
    }

    private void writePaddedString(final String string, final int length)
            throws IOException {
        if (string == null) {
            writeZeroes(length);
        } else {
            final byte[] bytes = string.getBytes(getCharset());
            _out.write(bytes);
            writeZeroes(length - bytes.length);
        }
    }

    private int parseBackgroundType(ScheduleEntry.BackgroundType value) {
        if (value == null) {
            return 0;
        }

        switch (value) {
            case COLOR:
                return 0;
            case GRADIENT:
                return 1;
            case IMAGE_TILED:
                return 2;
            case IMAGE_SCALED:
                return 3;
            case VIDEO:
                return 4;
            case LIVE_VIDEO:
                return 5;
            default:
                return 0;
        }
    }

    private int parseAspectRatio(ScheduleEntry.AspectRatio value) {
        if (value == null) {
            return 0;
        }

        switch (value) {
            case MAINTAIN:
                return 1;
            case STRETCH:
                return 2;
            case ZOOM:
                return 3;
            default:
                return 0;
        }
    }

    private byte parseHorizontalAlignment(ScheduleEntry.HorizontalAlignment value) {
        if (value == null) {
            return 3;
        }

        switch (value) {
            case LEFT:
                return 0;
            case CENTER:
                return 1;
            case RIGHT:
                return 2;
            default:
                return 3;
        }
    }

    private byte parseVerticalAlignment(ScheduleEntry.VerticalAlignment value) {
        if (value == null) {
            return 3;
        }

        switch (value) {
            case TOP:
                return 0;
            case CENTER:
                return 1;
            case BOTTOM:
                return 2;
            default:
                return 3;
        }
    }

    private byte parseTristate(Boolean value) {
        if (value == Boolean.FALSE) {
            return 0;
        } else if (value == Boolean.TRUE) {
            return 1;
        }
        return 2;
    }

    private byte parseGradientStyle(ScheduleEntry.GradientStyle value) {
        if (value == null) {
            return 0;
        }

        switch (value) {
            case HORIZONTAL:
                return 0;
            case VERTICAL:
                return 1;
            case DIAGONAL_UP:
                return 2;
            case DIAGONAL_DOWN:
                return 3;
            default:
                return 0;
        }
    }

    private byte parseGradientVariant(ScheduleEntry.GradientVariant value) {
        if (value == null) {
            return 0;
        }

        switch (value) {
            case LINEAR:
                return 0;
            case LINEAR_REVERSED:
                return 1;
            case BILINEAR:
                return 2;
            case BILINEAR_REVERSED:
                return 3;
            default:
                return 0;
        }
    }

    private int parseEntryType(ScheduleEntry.Type value) {
        if (value == null) {
            return 1;
        }

        switch (value) {
            case SONG:
                return 1;
            case SCRIPTURE:
                return 2;
            case PRESENTATION:
                return 3;
            case VIDEO:
                return 4;
            case LIVE_VIDEO:
                return 5;
            case IMAGE:
                return 7;
            case AUDIO:
                return 8;
            case WEB:
                return 9;
            default:
                return 1;
        }
    }

    private int parseColor(Color value) {
        if (value == null) {
            return 0;
        }
        return (value.getBlue() << 16) |
               (value.getGreen() << 8) |
               (value.getRed());
    }
}
