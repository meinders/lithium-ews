/*
 * Copyright 2013-2014 Gerrit Meinders
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

import lithium.io.Config;
import lithium.io.rtf.RtfWriter;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

/**
 * Writes schedules in the EWS file format.
 *
 * @author Gerrit Meinders
 */
public class EwsWriter {
    private static final int SCHEDULE_ENTRY_LENGTH = 1816;

    private final OutputStream _out;

    private Charset _charset = Charset.forName(Config.charset);

    public EwsWriter(final OutputStream out) {
        _out = out;
    }

    public EwsWriter(final File file) throws IOException {
        file.createNewFile();
        _out = new FileOutputStream(file);
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
            cumulativeContentLength += getBackgroundContentLength(entry);
            cumulativeContentLength += getMediaContentLength(entry);
        }

        // Add content
        for (final ScheduleEntry entry : entries) {
            writeContentForEntry(entry);
            writeBackgroundForEntry(entry);
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
        writeZeroes(15);    // Skip
        _out.write(ScheduleEntry.Type.PRESENTATION.equals(entry.getType()) ? 1 : 0);          // isPresentation
        writeInt(0);            // presentationLength

        _out.write(entry.isCustomFontSettings() ? 1 : 0);          // customFontSettings
        _out.write(entry.isFontSizeAutomatic() ? 1 : 0);          // fontSizeAutomatic
        _out.write(0);          // Skip
        _out.write(0);          // Skip
        writeInt(entry.getFontSize());            // fontSize limit
        _out.write(entry.isUseDefaultFont() ? 1 : 0);          // useDefaultFont
        writePaddedString(entry.getFontName(), 255);   // fontName
        writeInt(entry.isForegroundAutomatic() ? 1 : 0);            // foregroundAutomatic
        writeInt(parseColor(entry.getForegroundColor()));            // foregroundColor
        writeInt(entry.isShadowAutomatic() ? 1 : 0);            // shadowAutomatic
        writeInt(parseColor(entry.getShadowColor()));            // shadowColor
        writeInt(entry.isOutlineAutomatic() ? 1 : 0);            // outlineAutomatic
        writeInt(parseColor(entry.getOutlineColor()));            // outlineColor
        _out.write(parseTristate(entry.getShadowEnabled()));    // shadowEnabled
        _out.write(parseTristate(entry.getOutlineEnabled()));    // outlineEnabled
        _out.write(parseTristate(entry.getBoldEnabled()));    // boldEnabled
        _out.write(parseTristate(entry.getItalicEnabled()));    // italicEnabled
        _out.write(parseHorizontalAlignment(entry.getHorizontalTextAlignment()));    // horizontalTextAlignment
        _out.write(parseVerticalAlignment(entry.getVerticalTextAlignment()));        // verticalTextAlignment
        _out.write(entry.isDefaultTextMargins() ? 1 : 0);          // defaultTextMargins
        writeInt(entry.getTextMarginLeft());            // textMarginLeft
        writeInt(entry.getTextMarginTop());            // textMarginTop
        writeInt(entry.getTextMarginRight());            // textMarginRight
        writeInt(entry.getTextMarginBottom());            // textMarginBottom

        // More song information
        writePaddedString(entry.getNotes(), 161);
        writeZeroes(94);    // skip
        writePaddedString(entry.getSongNumber(), 11);
        _out.write(0);    // skip
        _out.write(0);    // media embedded
        writeZeroes(57);        // skip
        writeInt(0);                // originalResourceLength
        writeZeroes(12);        // skip

        int mediaContentPointer = 0;
        if (entry.getBackground() instanceof VideoBackground) {
            int contentLength = getEntryContentLength(entry) + getBackgroundContentLength(entry);
            mediaContentPointer = contentPointer + contentLength;
        }
        writeInt(mediaContentPointer);  // mediaContentPointer

        writeZeroes(20);    // skip

        // aspectRatio
        ScheduleEntry.AspectRatio aspectRatio = ScheduleEntry.AspectRatio.STRETCH;
        if (entry.getBackground() instanceof ImageBackground) {
            aspectRatio = ((ImageBackground) entry.getBackground()).getAspectRatio();
        } else if (entry.getBackground() instanceof LiveVideoBackground) {
            aspectRatio = ((LiveVideoBackground) entry.getBackground()).getAspectRatio();
        } else if (entry.getBackground() instanceof VideoBackground) {
            aspectRatio = ((VideoBackground) entry.getBackground()).getAspectRatio();
        }
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

        _out.write(0x01);    // skip
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
            return bytes.length + 4;
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
            _out.write(bytes);
        } else if (content != null) {
            throw new IllegalArgumentException("Unsupported content: " + content);
        }
    }

    private void writeBackgroundForEntry(ScheduleEntry entry) throws IOException {
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

    public void close()
            throws IOException {
        _out.close();
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
