/*
 * Copyright 2013 Gerrit Meinders
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

import java.util.Date;

/**
 * An entry in a {@link Schedule}, which provides content and various meta
 * information.
 *
 * @author Gerrit Meinders
 */
public class ScheduleEntry {
    private String _title;

    private String _mediaResource;

    private String _author;

    private String _copyright;

    private String _administrator;

    private Date _timestamp;

    private Content _content;

    private Type _type;

    private String _notes;

    private String _songNumber;

    private Presentation _presentation;

    private Background _background;

    private BinaryContent _thumbnailImage;

    /**
     * Constructs a new instance.
     */
    public ScheduleEntry() {
    }

    public ScheduleEntry(final String title) {
        setTitle(title);
    }

    public ScheduleEntry(final String title, final Content content) {
        setTitle(title);
        setContent(content);
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(final String title) {
        _title = title;
    }

    public String getMediaResource() {
        return _mediaResource;
    }

    public void setMediaResource(final String mediaResource) {
        _mediaResource = mediaResource;
    }

    public String getAuthor() {
        return _author;
    }

    public void setAuthor(final String author) {
        _author = author;
    }

    public String getCopyright() {
        return _copyright;
    }

    public void setCopyright(final String copyright) {
        _copyright = copyright;
    }

    public String getAdministrator() {
        return _administrator;
    }

    public void setAdministrator(final String administrator) {
        _administrator = administrator;
    }

    public Date getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        _timestamp = timestamp;
    }

    public Content getContent() {
        return _content;
    }

    public void setContent(final Content content) {
        _content = content;
    }

    public Type getType() {
        return _type;
    }

    public void setType(final Type type) {
        _type = type;
    }

    public String getNotes() {
        return _notes;
    }

    public void setNotes(final String notes) {
        _notes = notes;
    }

    public String getSongNumber() {
        return _songNumber;
    }

    public void setSongNumber(final String songNumber) {
        _songNumber = songNumber;
    }

    public void setPresentation(final Presentation presentation) {
        _presentation = presentation;
    }

    public Presentation getPresentation() {
        return _presentation;
    }

    public void setBackground(final Background background) {
        _background = background;
    }

    public Background getBackground() {
        return _background;
    }

    public void setThumbnailImage(BinaryContent thumbnailImage) {
        _thumbnailImage = thumbnailImage;
    }

    public BinaryContent getThumbnailImage() {
        return _thumbnailImage;
    }

    public enum Type {
        UNKNOWN,
        SONG,
        SCRIPTURE,
        PRESENTATION,
        VIDEO,
        LIVE_VIDEO,
        IMAGE,
        AUDIO,
        WEB
    }

    public enum HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT,
        DEFAULT
    }

    public enum VerticalAlignment {
        TOP,
        CENTER,
        BOTTOM,
        DEFAULT
    }

    public enum AspectRatio {
        MAINTAIN, // Keep aspect ratio, touch frame from inside
        STRETCH,
        ZOOM // Keep aspect ratio, touch frame from outside
    }

    public enum BackgroundType {
        COLOR,
        GRADIENT,
        IMAGE_TILED,
        IMAGE_SCALED,
        VIDEO,
        LIVE_VIDEO
    }

    public enum GradientStyle {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL_UP,
        DIAGONAL_DOWN
    }

    /**
     * NOTE: Bi-linear is GIMP terminology (though in GIMP the colors are reversed).
     */
    public enum GradientVariant {
        /**
         * Linear gradient: color 1, color 2.
         */
        LINEAR,

        /**
         * Reversed linear gradient: color 2, color 1.
         */
        LINEAR_REVERSED,

        /**
         * Bi-linear gradient: color 1, color 2, color 1.
         */
        BILINEAR,

        /**
         * Reversed bi-linear gradient: color 2, color 1, color 1.
         */
        BILINEAR_REVERSED
    }
}
